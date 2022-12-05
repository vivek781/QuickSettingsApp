package com.fbd.quicksettings.vs;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

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
import com.suke.widget.SwitchButton;

public class SettingsActivity extends AppCompatActivity
{
    RelativeLayout rel_ad_layout;
    AdRequest banner_adRequest;

    InterstitialAd ad_mob_interstitial;
    AdRequest interstitial_adRequest;

    BluetoothAdapter mBluetoothAdapter;
    TextView textView, txt_blt, txt_brightnessval;
    ImageView img_back;
    NetworkInfo wifiCheck;
    ConnectivityManager connectionManager;
    Boolean isChecked = false, isWifi = false, isScreen;
    SharedPreferences prefs;
    WifiManager wifi;
    SwitchButton switch_wifi, switch_mobile, switch_bluetooth,switch_gps,switch_airplane,switch_rotation;
    boolean GpsStatus;
    LocationManager locationManager;
    int brightness;
    ContentResolver cResolver;
    Window window;
    AudioManager mAudio;
    NotificationManager mNotificationManager;
    RelativeLayout rl_ringer, rl_sync, rl_dnd, rl_battery, rl_brightness;
    Animation objAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppConstants.overridePendingTransitionEnter(this);
        setContentView(R.layout.activity_settings_1);

        objAnimation = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.viewpush);

        switch_wifi = findViewById(R.id.switch_wifi);
        prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mAudio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        img_back = findViewById(R.id.img_back);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        txt_blt = findViewById(R.id.txt_blt);
        textView = findViewById(R.id.textView);

        rl_ringer = findViewById(R.id.rl_ringer);
        rl_sync = findViewById(R.id.rl_sync);
        rl_dnd = findViewById(R.id.rl_dnd);
        rl_battery = findViewById(R.id.rl_battery);
        rl_brightness = findViewById(R.id.rl_brightness);

        //img_bluetoothConnection = findViewById(R.id.img_bluetoothConnection);

        switch_gps = findViewById(R.id.switch_gps);
        switch_airplane = findViewById(R.id.switch_airplane);
        switch_mobile = findViewById(R.id.switch_mobile);
        switch_rotation = findViewById(R.id.switch_rotation);
        switch_bluetooth = findViewById(R.id.switch_bluetooth);

        txt_brightnessval = findViewById(R.id.txt_brightnessval);

        connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiCheck = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //Brightness value
        cResolver = getContentResolver();
        window = getWindow();
        try
        {
            // To handle the auto
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            //Get the current system brightness
            brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
            txt_brightnessval.setText(String.valueOf(brightness));
        }
        catch (Settings.SettingNotFoundException e)
        {
            //Throw an error case it couldn't be retrieved
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }

        isScreen = prefs.getBoolean("Screen", false);
        if (isScreen == true)
        {
            switch_rotation.setChecked(true);
        }
        else
        {
            switch_rotation.setChecked(false);
        }

        isWifi = prefs.getBoolean("wifi", false);
        if (isWifi == true)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                if (wifiCheck.isConnected())
                {
                    switch_wifi.setChecked(true);
                    textView.setText("ON");
                }
                else
                {
                    switch_wifi.setChecked(false);
                    textView.setText("OFF");
                }
            }
            else
            {
                textView.setText("ON");
                switch_wifi.setChecked(true);
                wifi.setWifiEnabled(true);
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                if (wifiCheck.isConnected())
                {
                    switch_wifi.setChecked(true);
                    textView.setText("ON");
                }
                else
                {
                    switch_wifi.setChecked(false);
                    textView.setText("OFF");
                }
            }
            else
            {
                textView.setText("OFF");
                switch_wifi.setChecked(false);
                wifi.setWifiEnabled(false);
            }
        }

        switch_wifi.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked)
            {
                if (isChecked)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    {
                        isWifi = prefs.edit().putBoolean("wifi", true).commit();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                    else
                    {
                        textView.setText("ON");
                        isWifi = prefs.edit().putBoolean("wifi", true).commit();
                        wifi.setWifiEnabled(true);
                    }
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        isWifi = prefs.edit().putBoolean("wifi", false).commit();
                    }
                    else
                    {
                        textView.setText("OFF");
                        isWifi = prefs.edit().putBoolean("wifi", false).commit();
                        wifi.setWifiEnabled(false);
                    }
                }
            }
        });

        switch_bluetooth.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean Checked)
            {
                if (!isChecked)
                {
                    isChecked = true;
                    if (!mBluetoothAdapter.isEnabled())
                    {
                        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 101);
                        txt_blt.setText("On");
                        return;
                    }
                    isChecked = false;
                    mBluetoothAdapter.disable();
                    txt_blt.setText("Off");
                    return;
                }
                isChecked = false;
                mBluetoothAdapter.disable();
                txt_blt.setText("Off");
            }
        });

        CheckGpsStatus();
        checkAirplaneMode();

        switch_gps.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        switch_rotation.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked)
            {
                if (isChecked)
                {
                    isScreen = prefs.edit().putBoolean("Screen", true).commit();
                    setAutoOrientationEnabled(SettingsActivity.this, isChecked);
                }
                else
                {
                    isScreen = prefs.edit().putBoolean("Screen", false).commit();
                    setAutoOrientationEnabled(SettingsActivity.this, isChecked);
                }
            }
        });

        switch_airplane.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked)
            {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                startActivity(intent);
            }
        });

        switch_mobile.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked)
            {
                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivity(i);
            }
        });

        rl_brightness.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                startActivity(i);
            }
        });

        rl_ringer.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                showCustomDialog();
            }
        });

        rl_sync.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_SYNC_SETTINGS);
                startActivity(i);
            }
        });

        rl_dnd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
            }
        });

        rl_battery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                Intent i1 = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
                startActivity(i1);
            }
        });

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

    private void showCustomDialog()
    {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.ring_settings_layout, viewGroup, false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        SeekBar alarm = dialogView.findViewById(R.id.alarm);
        SeekBar music = dialogView.findViewById(R.id.music);
        SeekBar ring = dialogView.findViewById(R.id.ring);
        Button btn_done = dialogView.findViewById(R.id.btn_done);


        RelativeLayout ring_rel = dialogView.findViewById(R.id.ring_rel);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            ring_rel.setVisibility(View.GONE);
        }
        else
        {
            ring_rel.setVisibility(View.VISIBLE);
        }

        SeekBar system = dialogView.findViewById(R.id.system);
        SeekBar voice = dialogView.findViewById(R.id.voice);
        initControls(alarm, AudioManager.STREAM_ALARM);
        initControls(music, AudioManager.STREAM_MUSIC);
        initControls(ring, AudioManager.STREAM_RING);
        initControls(system, AudioManager.STREAM_SYSTEM);
        initControls(voice, AudioManager.STREAM_VOICE_CALL);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();

        btn_done.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void initControls(SeekBar seek, final int stream)
    {
        seek.setMax(mAudio.getStreamMaxVolume(stream));
        seek.setProgress(mAudio.getStreamVolume(stream));
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser)
            {
                mAudio.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar)
            {
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar)
            {
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mBluetoothAdapter.isEnabled())
        {
            switch_bluetooth.setChecked(true);
        }
        else
        {
            switch_bluetooth.setChecked(false);
        }
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals("android.bluetooth.adapter.action.STATE_CHANGED"))
            {
                int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
                if (intExtra == 10)
                {
                    isChecked = false;
                    switch_bluetooth.setChecked(false);
                }
                else if (intExtra == 12)
                {
                    isChecked = true;
                    switch_bluetooth.setChecked(true);
                }
            }
        }
    };

    //Gps
    public void CheckGpsStatus()
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (GpsStatus == true)
        {
            switch_gps.setChecked(true);
        }
        else
        {
            switch_gps.setChecked(false);
        }
    }

    //Airplane
    private void checkAirplaneMode()
    {
        if (isAirplaneModeOn(getApplicationContext()))
        {
            switch_airplane.setChecked(true);
        }
        else
        {
            switch_airplane.setChecked(false);
        }
    }

    private static boolean isAirplaneModeOn(Context context)
    {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    //Mobile data
    private void checkMobileData()
    {
        if (isMobileDataModeOn(getApplicationContext()))
        {
            switch_mobile.setChecked(true);
        }
        else
        {
            switch_mobile.setChecked(false);
        }
    }

    private static boolean isMobileDataModeOn(Context context)
    {
        return Settings.Secure.getInt(context.getContentResolver(), "mobile_data", 1) == 1;
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled)
    {
        Settings.System.putInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        CheckGpsStatus();
        checkAirplaneMode();
        checkMobileData();
    }

    protected void changeInterruptionFiler(int interruptionFilter)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (mNotificationManager.isNotificationPolicyAccessGranted())
            {
                mNotificationManager.setInterruptionFilter(interruptionFilter);
            }
            else
            {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        try
        {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            registerReceiver(mBroadcastReceiver1, intentFilter);
        }
        catch (Exception unused)
        {

        }

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
                        EUGeneralClass.DoConsentProcess(this, SettingsActivity.this);
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
