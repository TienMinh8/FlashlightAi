package com.example.flashlightai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashlightai.R;
import com.example.flashlightai.model.AppItem;

import java.util.List;

/**
 * Adapter hiển thị danh sách ứng dụng trong RecyclerView
 */
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppViewHolder> {
    
    private Context context;
    private List<AppItem> appList;
    
    public AppAdapter(Context context, List<AppItem> appList) {
        this.context = context;
        this.appList = appList;
    }
    
    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppItem app = appList.get(position);
        
        holder.appIcon.setImageDrawable(app.getIcon());
        holder.appName.setText(app.getAppName());
        holder.appPackage.setText(app.getPackageName());
        holder.checkBox.setChecked(app.isSelected());
        
        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            boolean newState = !app.isSelected();
            app.setSelected(newState);
            holder.checkBox.setChecked(newState);
        });
        
        // Xử lý sự kiện click vào checkbox
        holder.checkBox.setOnClickListener(v -> {
            app.setSelected(holder.checkBox.isChecked());
        });
    }
    
    @Override
    public int getItemCount() {
        return appList.size();
    }
    
    /**
     * ViewHolder cho mỗi item trong danh sách
     */
    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;
        TextView appPackage;
        CheckBox checkBox;
        
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.app_icon);
            appName = itemView.findViewById(R.id.app_name);
            appPackage = itemView.findViewById(R.id.app_package);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }
} 