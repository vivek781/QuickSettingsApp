package com.fbd.quicksettings.vs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.an.deviceinfo.device.model.Battery;
import com.an.deviceinfo.device.model.Device;
import com.an.deviceinfo.device.model.Memory;
import com.appizona.yehiahd.fastsave.FastSave;
import com.fbd.quicksettings.vs.network.Connectivity;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SystemInfoActivity extends AppCompatActivity
{
    RelativeLayout rel_ad_layout;
    AdRequest banner_adRequest;

    InterstitialAd ad_mob_interstitial;
    AdRequest interstitial_adRequest;

    TextView temp,battery,B_health,voltage,charging,battery_present;
    TextView txt_ram,txt_memorySpace,txt_AvailableSpace,txt_externalSpace,txt_totalexternalSpace;
    TextView txt_manufacture,txt_model,txt_vCodeName,txt_BVersion,txt_product,txt_device,txt_board,txt_buildBrand,txt_osversion,txt_lang,txt_sdk,txt_sHeight,txt_sWidth;
    TextView connection_type,txt_wifiname,txt_networkname,txt_typeName,connection_status,ipv4,ipv6,MAC_ETH,MAC_WLAN,nw_type,roaming,netwok_class;
    Battery objbattery;
    Memory memory;
    Device device;
    TelephonyManager tm;
    WifiInfo wInfo;
    WifiManager manager;
    ImageView img_back,img_battery;
    Animation objAnimation;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppConstants.overridePendingTransitionEnter(this);
        setContentView(R.layout.activity_system_info);

        objAnimation = AnimationUtils.loadAnimation(SystemInfoActivity.this, R.anim.viewpush);

        objbattery = new Battery(SystemInfoActivity.this);
        memory = new Memory(SystemInfoActivity.this);
        device = new Device(SystemInfoActivity.this);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        img_back = findViewById(R.id.img_back);
        temp = findViewById(R.id.temp);
        battery = findViewById(R.id.battery);
        B_health = findViewById(R.id.B_health);
        voltage = findViewById(R.id.voltage);
        charging = findViewById(R.id.charging);
        battery_present = findViewById(R.id.battery_present);

        txt_ram = findViewById(R.id.txt_ram);
        txt_memorySpace = findViewById(R.id.txt_memorySpace);
        txt_AvailableSpace = findViewById(R.id.txt_AvailableSpace);
        txt_externalSpace = findViewById(R.id.txt_externalSpace);
        txt_totalexternalSpace = findViewById(R.id.txt_totalexternalSpace);

        txt_manufacture = findViewById(R.id.txt_manufacture);
        txt_model = findViewById(R.id.txt_model);
        txt_vCodeName = findViewById(R.id.txt_vCodeName);
        txt_BVersion = findViewById(R.id.txt_BVersion);
        txt_product = findViewById(R.id.txt_product);
        txt_device  = findViewById(R.id.txt_device);
        txt_board  = findViewById(R.id.txt_board);
        txt_buildBrand  = findViewById(R.id.txt_board);
        txt_osversion  = findViewById(R.id.txt_osversion);
        txt_lang  = findViewById(R.id.txt_lang);
        txt_sdk  = findViewById(R.id.txt_sdk);
        txt_sHeight  = findViewById(R.id.txt_sHeight);
        txt_sWidth  = findViewById(R.id.txt_sWidth);

        connection_type = findViewById(R.id.connection_type);
        txt_wifiname = findViewById(R.id.txt_wifiname);
        txt_networkname = findViewById(R.id.txt_networkname);
        txt_typeName = findViewById(R.id.txt_typeName);
        connection_status = findViewById(R.id.connection_status);
        ipv4 = findViewById(R.id.ipv4);
        connection_status = findViewById(R.id.connection_status);
        ipv4 = findViewById(R.id.ipv4);
        ipv6 = findViewById(R.id.ipv6);
        MAC_ETH = findViewById(R.id.MAC_ETH);
        MAC_WLAN = findViewById(R.id.MAC_WLAN);
        nw_type  = findViewById(R.id.nw_type);
        img_battery = findViewById(R.id.img_battery);
        netwok_class = findViewById(R.id.netwok_class);

        temp.setText(String.valueOf(objbattery.getBatteryTemperature()));
        battery.setText(String.valueOf(objbattery.getBatteryPercent()) + " %");

        if(String.valueOf(objbattery.getBatteryPercent()).equals("0")){
            img_battery.setBackgroundResource(R.drawable.img_b1);
        }else if(objbattery.getBatteryPercent() <= 25){
            img_battery.setBackgroundResource(R.drawable.img_battery2);
        }else if(objbattery.getBatteryPercent() <= 50){
            img_battery.setBackgroundResource(R.drawable.img_battery3);
        }else if(objbattery.getBatteryPercent() <= 75){
            img_battery.setBackgroundResource(R.drawable.img_battery4);
        }else if(objbattery.getBatteryPercent() <= 92){
            img_battery.setBackgroundResource(R.drawable.img_battery5);
        }else if(objbattery.getBatteryPercent() >= 93 || objbattery.getBatteryPercent() <= 100){
            img_battery.setBackgroundResource(R.drawable.img_battery6);
        }

        B_health.setText(String.valueOf(objbattery.getBatteryHealth()));
        voltage.setText(String.valueOf(objbattery.getBatteryVoltage()));
        charging.setText(String.valueOf(objbattery.getChargingSource()));
        battery_present.setText(String.valueOf(objbattery.isBatteryPresent()));

        txt_ram.setText(String.valueOf(convertToGb(memory.getTotalRAM())) + " GB");
        txt_memorySpace.setText(String.valueOf(convertToGb(memory.getTotalInternalMemorySize())) + " GB");
        txt_AvailableSpace.setText(String.valueOf(convertToGb(memory.getAvailableInternalMemorySize())) + " GB");
        txt_externalSpace.setText(String.valueOf(convertToGb(memory.getTotalExternalMemorySize())) + " GB");
        txt_totalexternalSpace.setText(String.valueOf(convertToGb(memory.getAvailableExternalMemorySize())) + " GB");

        txt_manufacture.setText(device.getManufacturer());
        txt_model.setText(device.getModel());
        txt_vCodeName.setText(device.getBuildVersionCodeName());
        txt_BVersion.setText(device.getReleaseBuildVersion());
        txt_product.setText(device.getProduct());
        txt_device.setText(device.getDevice());
        txt_board.setText(device.getBoard());
        txt_buildBrand.setText(device.getBuildBrand());
        txt_osversion.setText(device.getOsVersion());
        txt_lang.setText(device.getLanguage());
        txt_sdk.setText(String.valueOf(device.getSdkVersion()));
        txt_sHeight.setText(String.valueOf(device.getScreenHeight()));
        txt_sWidth.setText(String.valueOf(device.getScreenWidth()));


        connection_status.setText(Connectivity.isConnected(SystemInfoActivity.this) ? "Connected"
                : "NotConnected");
        ipv4.setText(Connectivity.getIPAddress(true));
        ipv6.setText(Connectivity.getIPAddress(false));
        MAC_ETH.setText(Connectivity.getMACAddress("eth0"));
        MAC_WLAN.setText(Connectivity.getMACAddress("wlan0"));
        nw_type.setText(Connectivity.isConnectedFast(SystemInfoActivity.this) ? "Fast" : "Slow");
        connection_type.setText(Connectivity.isConnectedMobile(SystemInfoActivity.this) ? "Mobile N/W"
                : (Connectivity.isConnectedWifi(SystemInfoActivity.this) ? "Wifi"
                : "None"));

        if (Connectivity.isConnectedWifi(SystemInfoActivity.this))
        {
            wInfo = manager.getConnectionInfo();
            String ssid = wInfo.getSSID();
            if (ssid != null)
            {
                txt_wifiname.setText(ssid);
            }
            else
            {
                txt_wifiname.setText("N/A");
            }
        }
        else
        {
            txt_typeName.setText(Connectivity.getNetworkClass(SystemInfoActivity.this));
            SubscriptionManager subscriptionManager = (SubscriptionManager) getApplicationContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

            int nDataSubscriptionId = getDefaultDataSubscriptionId(subscriptionManager);

            if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                SubscriptionInfo si = subscriptionManager.getActiveSubscriptionInfo(nDataSubscriptionId);

                if (si != null)
                {
                    txt_networkname.setText(""+si.getCarrierName().toString());
                    Log.v("NetworkName",si.getCarrierName().toString());
                }
            }
        }

        if (null != Connectivity.getNetworkInfo(SystemInfoActivity.this))
            ((TextView) findViewById(R.id.roaming))
                    .setText(Connectivity.getNetworkInfo(SystemInfoActivity.this)
                            .isRoaming() ? "YES" : "NO");

        netwok_class.setText(Connectivity.getNetworkClass(SystemInfoActivity.this));

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                onBackPressed();
            }
        });

    }

    int getDefaultDataSubscriptionId(final SubscriptionManager subscriptionManager)
    {
        if (android.os.Build.VERSION.SDK_INT >= 24)
        {
            int nDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();

            if (nDataSubscriptionId != SubscriptionManager.INVALID_SUBSCRIPTION_ID)
            {
                return (nDataSubscriptionId);
            }
        }

        try
        {
            Class<?> subscriptionClass = Class.forName(subscriptionManager.getClass().getName());
            try
            {
                Method getDefaultDataSubscriptionId = subscriptionClass.getMethod("getDefaultDataSubId");

                try
                {
                    return ((int) getDefaultDataSubscriptionId.invoke(subscriptionManager));
                }
                catch (IllegalAccessException e1)
                {
                    e1.printStackTrace();
                }
                catch (InvocationTargetException e1)
                {
                    e1.printStackTrace();
                }
            }
            catch (NoSuchMethodException e1)
            {
                e1.printStackTrace();
            }
        }
        catch (ClassNotFoundException e1)
        {
            e1.printStackTrace();
        }

        return (SubscriptionManager.INVALID_SUBSCRIPTION_ID);
    }

    private float convertToGb(long valInBytes)
    {
        return Float.valueOf(String.format("%.2f", (float) valInBytes / (1024 * 1024 * 1024)));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        AdMobConsent();
    }

    private void AdMobConsent()
    {
        boolean is_hide_ads = FastSave.getInstance().getBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
        if(!is_hide_ads)
        {
            boolean is_online = EUGeneralClass.isOnline(this);
            if(is_online)
            {
                boolean is_eea_user = FastSave.getInstance().getBoolean(EUGeneralHelper.EEA_USER_KEY,false);
                if(is_eea_user)
                {
                    boolean is_consent_set = FastSave.getInstance().getBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,false);
                    if (is_consent_set)
                    {
                        AdsProcess();
                    }
                    else
                    {
                        EUGeneralClass.DoConsentProcess(this, SystemInfoActivity.this);
                    }
                }
                else
                {
                    AdsProcess();
                }
            }
            else
            {
                // Hide Ads
                HideViews();
            }
        }
        else
        {
            // Hide Ads
            HideViews();
        }
    }

    private void AdsProcess()
    {
        boolean is_g_user = FastSave.getInstance().getBoolean(EUGeneralHelper.GOOGLE_PLAY_STORE_USER_KEY, false);
        if (is_g_user)
        {
            LoadAdMobBannerAd();
            LoadAdMobInterstitialAd();
        }
        else
        {
            // Hide Ads
            HideViews();
        }
    }

    private void HideViews()
    {
        rel_ad_layout = (RelativeLayout)findViewById(R.id.ad_layout);
        rel_ad_layout.setVisibility(View.GONE);
    }

    private void LoadAdMobBannerAd()
    {
        // TODO Auto-generated method stub
        Bundle non_personalize_bundle = new Bundle();
        non_personalize_bundle.putString("npa", "1");

        boolean is_show_non_personalize = FastSave.getInstance().getBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY, false);
        if (is_show_non_personalize)
        {
            banner_adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, non_personalize_bundle).build();
        }
        else
        {
            banner_adRequest = new AdRequest.Builder().build();
        }

        rel_ad_layout = (RelativeLayout) findViewById(R.id.ad_layout);
        rel_ad_layout.setVisibility(View.VISIBLE);

        AdView adView = new AdView(this);
        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);
        adView.setAdUnitId(EUGeneralHelper.ad_mob_banner_ad_id);
        adView.loadAd(banner_adRequest);

        //Banner Ad Start //
        rel_ad_layout.addView(adView);
    }

    private AdSize getAdSize()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            // Do something for lollipop and above versions
            int windowWidth = getResources().getConfiguration().screenWidthDp;

            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, windowWidth);
        }
        else
        {
            // Step 2 - Determine the screen width (less decorations) to use for the ad width.
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);

            float widthPixels = outMetrics.widthPixels;
            float density = outMetrics.density;

            int adWidth = (int) (widthPixels / density);

            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
        }
    }

    private void LoadAdMobInterstitialAd()
    {
        // TODO Auto-generated method stub
        try
        {
            Bundle non_personalize_bundle = new Bundle();
            non_personalize_bundle.putString("npa", "1");

            boolean is_show_non_personalize = FastSave.getInstance().getBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
            if(is_show_non_personalize)
            {
                interstitial_adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, non_personalize_bundle).build();
            }
            else
            {
                interstitial_adRequest = new AdRequest.Builder().build();
            }

            //Interstitial Ad Start //
            InterstitialAd.load(this,EUGeneralHelper.ad_mob_interstitial_ad_id,interstitial_adRequest, new InterstitialAdLoadCallback()
            {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd)
                {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    ad_mob_interstitial = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError)
                {
                    // Handle the error
                    ad_mob_interstitial = null;
                }
            });
            //Interstitial Ad End //
        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        // TODO Auto-generated method stub
        super.onBackPressed();
        boolean is_g_user = FastSave.getInstance().getBoolean(EUGeneralHelper.GOOGLE_PLAY_STORE_USER_KEY, false);
        if (is_g_user)
        {
            boolean is_hide_ads = FastSave.getInstance().getBoolean(EUGeneralHelper.REMOVE_ADS_KEY, false);
            if (!is_hide_ads)
            {
                ShowAdMobInterstitialAd();
            }
            else
            {
                BackScreen();
            }
        }
        else
        {
            // Hide Ads
            BackScreen();
        }
    }

    private void ShowAdMobInterstitialAd()
    {
        // TODO Auto-generated method stub
        if (ad_mob_interstitial != null)
        {
            if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
            {
                DisplayInterstitialAd();
            }
            else
            {
                BackScreen();
            }
        }
        else
        {
            BackScreen();
        }
    }

    private void DisplayInterstitialAd()
    {
        if(ad_mob_interstitial != null)
        {
            ad_mob_interstitial.setFullScreenContentCallback(new FullScreenContentCallback()
            {
                @Override
                public void onAdDismissedFullScreenContent()
                {
                    // Called when fullscreen content is dismissed.
                    BackScreen();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError)
                {
                    // Called when fullscreen content failed to show.
                }

                @Override
                public void onAdShowedFullScreenContent()
                {
                    ad_mob_interstitial = null;
                }
            });
        }
        ad_mob_interstitial.show(this);
        EUGeneralHelper.is_show_open_ad = false;
    }

    private void BackScreen()
    {
        // TODO Auto-generated method stub
        EUGeneralHelper.is_show_open_ad = true;
        finish();
        AppConstants.overridePendingTransitionExit(this);
    }
}