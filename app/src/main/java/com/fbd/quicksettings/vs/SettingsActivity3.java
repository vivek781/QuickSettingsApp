package com.fbd.quicksettings.vs;

import android.content.Intent;
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

import androidx.annotation.NonNull;
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

public class SettingsActivity3 extends AppCompatActivity
{
    RelativeLayout rel_ad_layout;
    AdRequest banner_adRequest;

    InterstitialAd ad_mob_interstitial;
    AdRequest interstitial_adRequest;

    RelativeLayout rl_home,rl_nfc,rl_datausage,rl_security,rl_privacy,rl_vpn,rl_internalStorage,rl_accessibility;
    ImageView img_back;
    Animation objAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AppConstants.overridePendingTransitionEnter(this);
        setContentView(R.layout.activity_settings_3);

        objAnimation = AnimationUtils.loadAnimation(SettingsActivity3.this, R.anim.viewpush);

        rl_accessibility = findViewById(R.id.rl_accessibility);
        rl_internalStorage = findViewById(R.id.rl_internalStorage);
        rl_privacy = findViewById(R.id.rl_privacy);
        rl_vpn = findViewById(R.id.rl_vpn);
        rl_datausage = findViewById(R.id.rl_datausage);
        rl_nfc  = findViewById(R.id.rl_nfc);
        rl_home  = findViewById(R.id.rl_home);
        rl_security = findViewById(R.id.rl_security);
        img_back = findViewById(R.id.img_back);

        rl_accessibility.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(i);
            }
        });

        rl_internalStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                startActivity(i);
            }
        });

        rl_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_PRIVACY_SETTINGS);
                startActivity(i);
            }
        });

        rl_vpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_VPN_SETTINGS);
                startActivity(i);
            }
        });

        rl_datausage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                try {
                    Intent i = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                    startActivity(i);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        rl_nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_SETTINGS);
                startActivity(i);
            }
        });

        rl_security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                startActivity(i);
            }
        });

        rl_home.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                Intent i = new Intent(Settings.ACTION_HOME_SETTINGS);
                startActivity(i);
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
                        EUGeneralClass.DoConsentProcess(this, SettingsActivity3.this);
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