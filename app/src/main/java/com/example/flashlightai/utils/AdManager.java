package com.example.flashlightai.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import java.util.Date;

/**
 * Quản lý tất cả các loại quảng cáo trong ứng dụng
 */
public class AdManager {
    private static final String TAG = "AdManager";
    
    // Thời gian tối thiểu giữa các quảng cáo interstitial
    private static final long MIN_INTERSTITIAL_INTERVAL = 3 * 60 * 1000; // 3 phút
    
    // Các mã quảng cáo thử nghiệm AdMob
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private static final String ADAPTIVE_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741";
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379";
    private static final String NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    private static final String NATIVE_VIDEO_AD_UNIT_ID = "ca-app-pub-3940256099942544/1044960115";
    private static final String APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";
    
    private static AdManager instance;
    private Context context;
    
    // Các biến lưu trữ quảng cáo đã tải
    private InterstitialAd interstitialAd;
    private RewardedAd rewardedAd;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private AppOpenAd appOpenAd;
    private NativeAd nativeAd;
    
    // Biến theo dõi thời gian và trạng thái
    private boolean isAdDisabled = false;
    private long lastInterstitialShown = 0;
    private long appOpenAdLoadTime = 0;
    private boolean isInterstitialTimerRunning = false;
    private long interstitialTimer = 0;
    private Handler interstitialHandler;
    
    // Callback interfaces
    public interface InterstitialCallback {
        void onAdClosed();
        void onAdFailed();
    }
    
    public interface RewardCallback {
        void onRewarded(RewardItem reward);
        void onAdClosed();
        void onAdFailed();
    }
    
    public interface AppOpenAdCallback {
        void onAdClosed();
        void onAdFailed();
    }
    
    private AdManager(Context context) {
        this.context = context.getApplicationContext();
        interstitialHandler = new Handler();
    }
    
    public static synchronized AdManager getInstance(Context context) {
        if (instance == null) {
            instance = new AdManager(context);
        }
        return instance;
    }
    
    /**
     * Khởi tạo SDK AdMob
     */
    public void initialize() {
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                // Tải trước các quảng cáo
                preloadInterstitialAd();
                preloadRewardedAd();
                preloadAppOpenAd();
            }
        });
    }
    
    /**
     * Tải trước quảng cáo interstitial
     */
    public void preloadInterstitialAd() {
        if (isAdDisabled) return;
        
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context, INTERSTITIAL_AD_UNIT_ID, adRequest, 
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd ad) {
                        interstitialAd = ad;
                    }
                    
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitialAd = null;
                    }
                });
    }
    
    /**
     * Tải trước quảng cáo rewarded
     */
    public void preloadRewardedAd() {
        if (isAdDisabled) return;
        
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, REWARDED_AD_UNIT_ID, adRequest,
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                    }
                    
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        rewardedAd = null;
                    }
                });
    }
    
    /**
     * Tải trước quảng cáo App Open
     */
    public void preloadAppOpenAd() {
        if (isAdDisabled) return;
        
        AdRequest adRequest = new AdRequest.Builder().build();
        AppOpenAd.load(context, APP_OPEN_AD_UNIT_ID, adRequest, 
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        appOpenAdLoadTime = new Date().getTime();
                    }
                    
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        appOpenAd = null;
                    }
                });
    }
    
    /**
     * Hiển thị quảng cáo banner thông thường trong container
     */
    public void showBannerAd(ViewGroup container) {
        if (isAdDisabled || container == null) return;
        
        container.removeAllViews(); // Xóa banner cũ nếu có
        
        AdView adView = new AdView(context);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);
        adView.setAdSize(AdSize.BANNER);
        
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                container.removeAllViews();
            }
        });
        
        container.addView(adView);
    }
    
    /**
     * Hiển thị quảng cáo banner lớn (MEDIUM_RECTANGLE) trong container
     */
    public void showLargeBannerAd(ViewGroup container) {
        if (isAdDisabled || container == null) return;
        
        container.removeAllViews(); // Xóa banner cũ nếu có
        
        AdView adView = new AdView(context);
        adView.setAdUnitId(BANNER_AD_UNIT_ID);
        adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                container.removeAllViews();
            }
        });
        
        container.addView(adView);
    }
    
    /**
     * Hiển thị quảng cáo adaptive banner trong container
     */
    public void showAdaptiveBannerAd(ViewGroup container, Activity activity) {
        if (isAdDisabled || container == null || activity == null) return;
        
        container.removeAllViews(); // Xóa banner cũ nếu có
        
        AdView adView = new AdView(context);
        adView.setAdUnitId(ADAPTIVE_BANNER_AD_UNIT_ID);
        
        // Tính toán kích thước adaptive banner dựa trên màn hình
        AdSize adSize = getAdaptiveBannerAdSize(activity);
        adView.setAdSize(adSize);
        
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                container.removeAllViews();
            }
        });
        
        container.addView(adView);
    }
    
    private AdSize getAdaptiveBannerAdSize(Activity activity) {
        // Determine the screen width to use for the ad width.
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
        
        // Use the screen width as the ad width.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, screenWidth);
    }
    
    /**
     * Hiển thị quảng cáo interstitial
     */
    public void showInterstitialAd(Activity activity, InterstitialCallback callback) {
        if (isAdDisabled || activity == null) {
            if (callback != null) callback.onAdFailed();
            return;
        }
        
        // Kiểm tra thời gian tối thiểu giữa các lần hiển thị
        long currentTime = new Date().getTime();
        if (currentTime - lastInterstitialShown < MIN_INTERSTITIAL_INTERVAL) {
            if (callback != null) callback.onAdFailed();
            return;
        }
        
        if (interstitialAd == null) {
            preloadInterstitialAd();
            if (callback != null) callback.onAdFailed();
            return;
        }
        
        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Tải quảng cáo mới cho lần sau
                preloadInterstitialAd();
                if (callback != null) callback.onAdClosed();
            }
            
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                // Tải quảng cáo mới cho lần sau
                preloadInterstitialAd();
                if (callback != null) callback.onAdFailed();
            }
            
            @Override
            public void onAdShowedFullScreenContent() {
                interstitialAd = null;
                lastInterstitialShown = new Date().getTime();
            }
        });
        
        // Hiển thị quảng cáo
        interstitialAd.show(activity);
    }
    
    /**
     * Hiển thị quảng cáo rewarded
     */
    public void showRewardedAd(Activity activity, RewardCallback callback) {
        if (isAdDisabled || activity == null) {
            if (callback != null) callback.onAdFailed();
            return;
        }
        
        if (rewardedAd == null) {
            preloadRewardedAd();
            if (callback != null) callback.onAdFailed();
            return;
        }
        
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Tải quảng cáo mới cho lần sau
                preloadRewardedAd();
                if (callback != null) callback.onAdClosed();
            }
            
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                // Tải quảng cáo mới cho lần sau
                preloadRewardedAd();
                if (callback != null) callback.onAdFailed();
            }
            
            @Override
            public void onAdShowedFullScreenContent() {
                rewardedAd = null;
            }
        });
        
        // Hiển thị quảng cáo
        rewardedAd.show(activity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                if (callback != null) callback.onRewarded(rewardItem);
            }
        });
    }
    
    /**
     * Hiển thị quảng cáo App Open
     */
    public void showAppOpenAd(Activity activity, AppOpenAdCallback callback) {
        if (isAdDisabled || activity == null) {
            if (callback != null) callback.onAdFailed();
            return;
        }
        
        // Kiểm tra xem quảng cáo đã tải chưa hoặc đã hết hạn chưa
        if (!isAppOpenAdAvailable()) {
            preloadAppOpenAd();
            if (callback != null) callback.onAdFailed();
            return;
        }
        
        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Tải quảng cáo mới cho lần sau
                appOpenAd = null;
                preloadAppOpenAd();
                if (callback != null) callback.onAdClosed();
            }
            
            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                appOpenAd = null;
                preloadAppOpenAd();
                if (callback != null) callback.onAdFailed();
            }
            
            @Override
            public void onAdShowedFullScreenContent() {
                // Quảng cáo đã hiển thị
            }
        });
        
        // Hiển thị quảng cáo
        appOpenAd.show(activity);
    }
    
    /**
     * Kiểm tra xem App Open Ad có sẵn và chưa hết hạn không
     */
    private boolean isAppOpenAdAvailable() {
        if (appOpenAd == null) return false;
        
        // Kiểm tra xem quảng cáo có hết hạn chưa (hạn 4 giờ)
        long currentTime = new Date().getTime();
        long adExpireTime = 4 * 60 * 60 * 1000; // 4 giờ
        
        return (currentTime - appOpenAdLoadTime) < adExpireTime;
    }
    
    /**
     * Bắt đầu theo dõi thời gian cho quảng cáo interstitial sau 3 phút
     */
    public void startInterstitialTimer() {
        if (isInterstitialTimerRunning) return;
        
        isInterstitialTimerRunning = true;
        interstitialTimer = 0;
        
        // Tạo một Runnable chạy mỗi giây để tăng bộ đếm thời gian
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                interstitialTimer += 1000; // Tăng 1 giây
                if (interstitialHandler != null) {
                    interstitialHandler.postDelayed(this, 1000);
                }
            }
        };
        
        // Bắt đầu bộ đếm thời gian
        interstitialHandler.postDelayed(timerRunnable, 1000);
    }
    
    /**
     * Dừng bộ đếm thời gian
     */
    public void stopInterstitialTimer() {
        isInterstitialTimerRunning = false;
        interstitialHandler.removeCallbacksAndMessages(null);
    }
    
    /**
     * Kiểm tra xem đã đủ thời gian để hiển thị interstitial chưa
     */
    public boolean isInterstitialTimerReady() {
        return interstitialTimer >= 3 * 60 * 1000; // 3 phút
    }
    
    /**
     * Đặt lại bộ đếm thời gian interstitial
     */
    public void resetInterstitialTimer() {
        interstitialTimer = 0;
    }
    
    /**
     * Bật/tắt quảng cáo
     */
    public void disableAds(boolean disable) {
        this.isAdDisabled = disable;
    }
} 