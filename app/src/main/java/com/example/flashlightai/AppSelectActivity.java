package com.example.flashlightai;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.adapter.AppAdapter;
import com.example.flashlightai.base.BaseActivity;
import com.example.flashlightai.model.AppItem;
import com.example.flashlightai.service.NotificationMonitorService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Activity để người dùng chọn ứng dụng nhận thông báo đèn flash
 */
public class AppSelectActivity extends BaseActivity {
    private static final String TAG = "AppSelectActivity";
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    
    private RecyclerView recyclerView;
    private AppAdapter adapter;
    private List<AppItem> appList = new ArrayList<>();
    private Button btnSave;
    private Switch switchEnableNotifications;
    
    private NotificationMonitorService monitorService;
    private boolean serviceBound = false;
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            NotificationMonitorService.LocalBinder binder = (NotificationMonitorService.LocalBinder) service;
            monitorService = binder.getService();
            serviceBound = true;
            
            // Đã kết nối đến service, cập nhật UI
            updateUI();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);
        
        // Thiết lập ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Chọn ứng dụng nhận thông báo");
        }
        
        // Ánh xạ view
        recyclerView = findViewById(R.id.recycler_apps);
        btnSave = findViewById(R.id.btn_save);
        switchEnableNotifications = findViewById(R.id.switch_enable_notifications);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppAdapter(this, appList);
        recyclerView.setAdapter(adapter);
        
        // Kiểm tra quyền và hiển thị thông báo nếu cần
        if (!isNotificationServiceEnabled()) {
            findViewById(R.id.notification_permission_container).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_notification_permission).setOnClickListener(v -> {
                openNotificationListenerSettings();
            });
        } else {
            findViewById(R.id.notification_permission_container).setVisibility(View.GONE);
        }
        
        // Tải danh sách ứng dụng
        loadAppList();
        
        // Kết nối đến service
        Intent intent = new Intent(this, NotificationMonitorService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        
        // Thiết lập sự kiện
        switchEnableNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (serviceBound) {
                monitorService.setAppNotificationsEnabled(isChecked);
            }
        });
        
        btnSave.setOnClickListener(v -> {
            if (serviceBound) {
                // Lấy danh sách các ứng dụng đã chọn
                Set<String> selectedApps = new HashSet<>();
                for (AppItem app : appList) {
                    if (app.isSelected()) {
                        selectedApps.add(app.getPackageName());
                    }
                }
                
                // Lưu vào service
                monitorService.setSelectedApps(selectedApps);
                
                Toast.makeText(this, "Đã lưu " + selectedApps.size() + " ứng dụng", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Không thể kết nối đến dịch vụ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Cập nhật UI từ trạng thái service
     */
    private void updateUI() {
        if (serviceBound) {
            switchEnableNotifications.setChecked(monitorService.isAppNotificationsEnabled());
            
            // Lấy danh sách ứng dụng đã chọn
            Set<String> selectedApps = monitorService.getSelectedApps();
            
            // Cập nhật trạng thái selected của các ứng dụng
            for (AppItem app : appList) {
                app.setSelected(selectedApps.contains(app.getPackageName()));
            }
            
            adapter.notifyDataSetChanged();
        }
    }
    
    /**
     * Tải danh sách ứng dụng
     */
    private void loadAppList() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        
        for (ApplicationInfo app : apps) {
            // Chỉ lấy ứng dụng người dùng, không lấy ứng dụng hệ thống
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = app.loadLabel(pm).toString();
                String packageName = app.packageName;
                appList.add(new AppItem(appName, packageName, app.loadIcon(pm), false));
            }
        }
        
        // Sắp xếp theo tên
        Collections.sort(appList, new Comparator<AppItem>() {
            @Override
            public int compare(AppItem app1, AppItem app2) {
                return app1.getAppName().compareToIgnoreCase(app2.getAppName());
            }
        });
        
        adapter.notifyDataSetChanged();
    }
    
    /**
     * Kiểm tra xem dịch vụ lắng nghe thông báo đã được kích hoạt chưa
     */
    private boolean isNotificationServiceEnabled() {
        String packageName = getPackageName();
        String flat = Settings.Secure.getString(getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        
        if (flat != null) {
            String[] names = flat.split(":");
            for (String name : names) {
                ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null && packageName.equals(cn.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Mở màn hình cài đặt dịch vụ lắng nghe thông báo
     */
    private void openNotificationListenerSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Không thể mở cài đặt thông báo", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Không thể mở cài đặt thông báo: " + e.getMessage());
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Ngắt kết nối service
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 