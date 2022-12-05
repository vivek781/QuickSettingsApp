package com.fbd.quicksettings.vs;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.an.deviceinfo.device.model.Battery;
import com.an.deviceinfo.device.model.Device;
import com.appizona.yehiahd.fastsave.FastSave;
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
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsActivity2 extends AppCompatActivity
{
    RelativeLayout rel_ad_layout;
    AdRequest banner_adRequest;

    InterstitialAd ad_mob_interstitial;
    AdRequest interstitial_adRequest;

    RelativeLayout rl_tethering,rl_screentimeout,rl_language,rl_datetime,rl_deviceinfo,rl_background,rl_battery;
    TextView txt_batteryval,txt_timeout,txt_day,txt_date,txt_time,txt_temp,txt_manufacture,txt_model,txt_buildVersion,txt_dpi,txt_tethering_status;
    Battery objbattery;
    int val;
    Calendar instance;
    String currentDateTimeString;
    SimpleDateFormat sdf,df;
    Date d;
    int hour,min,sec;
    Device device;
    DisplayMetrics metrics;
    WifiManager wifiManager;
    int apState;
    ImageView img_back;
    Animation objAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppConstants.overridePendingTransitionEnter(this);
        setContentView(R.layout.activity_settings_2);

        objAnimation = AnimationUtils.loadAnimation(SettingsActivity2.this, R.anim.viewpush);

        img_back = findViewById(R.id.img_back);

        objbattery = new Battery(SettingsActivity2.this);
        rl_tethering = findViewById(R.id.rl_tethering);
        rl_screentimeout = findViewById(R.id.rl_screentimeout);
        rl_language = findViewById(R.id.rl_language);
        rl_background = findViewById(R.id.rl_background);
        rl_deviceinfo = findViewById(R.id.rl_deviceinfo);
        rl_battery = findViewById(R.id.rl_battery);
        txt_batteryval = findViewById(R.id.txt_batteryval);
        txt_timeout = findViewById(R.id.txt_timeout);
        rl_datetime = findViewById(R.id.rl_datetime);
        txt_day = findViewById(R.id.txt_day);
        txt_date = findViewById(R.id.txt_date);
        txt_time = findViewById(R.id.txt_time);
        txt_temp = findViewById(R.id.txt_temp);
        txt_manufacture = findViewById(R.id.txt_manufacture);
        txt_model = findViewById(R.id.txt_model);
        txt_buildVersion = findViewById(R.id.txt_buildVersion);
        txt_dpi = findViewById(R.id.txt_dpi);
        txt_tethering_status = findViewById(R.id.txt_tethering_status);
        device = new Device(SettingsActivity2.this);
        metrics = getResources().getDisplayMetrics();

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            apState = (Integer) wifiManager.getClass().getMethod("getWifiApState").invoke(wifiManager);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if (apState == 13) {
            txt_tethering_status.setText("Wifi hotspot is On");
        }else{
            txt_tethering_status.setText("Wifi hotspot is Off");
        }


        int densityDpi = (int)(metrics.density * 160f);
        txt_dpi.setText("Dpi : " + String.valueOf(densityDpi));

        try {
            val = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        txt_timeout.setText(String.valueOf(val));

        rl_tethering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        rl_screentimeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(i);
            }
        });

        rl_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(i);
            }
        });

        rl_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(i);

            }
        });

        rl_deviceinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                /*Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                startActivity(i);*/

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(SettingsActivity2.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    finish();
                    startActivity(i);
                    return;
                }
            }
        });

        txt_temp.setText(String.valueOf(objbattery.getBatteryTemperature()) + " c");

        rl_battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(i);
            }
        });

        rl_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_DATE_SETTINGS);
                startActivity(i);
            }
        });
        txt_batteryval.setText(String.valueOf(objbattery.getBatteryPercent()) + "%");

        sdf = new SimpleDateFormat("EEEE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            instance = Calendar.getInstance();
            hour = instance.get(Calendar.HOUR);
            min = instance.get(Calendar.MINUTE);
            sec = instance.get(Calendar.SECOND);
        }
        currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
        d = new Date();
        String dayOfTheWeek = sdf.format(d);
        txt_day.setText(dayOfTheWeek);
        txt_date.setText(currentDateTimeString);

        txt_manufacture.setText(device.getManufacturer());
        txt_model.setText(device.getModel());
        txt_buildVersion.setText(device.getReleaseBuildVersion());

        img_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                onBackPressed();
            }
        });
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        txt_timeout.setText(String.valueOf(val));
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
                        EUGeneralClass.DoConsentProcess(this, SettingsActivity2.this);
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