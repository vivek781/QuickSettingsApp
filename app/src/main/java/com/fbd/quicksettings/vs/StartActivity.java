package com.fbd.quicksettings.vs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.appizona.yehiahd.fastsave.FastSave;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import rb.exit.nativelibrary.MyExitView;

public class StartActivity extends AppCompatActivity implements PurchasesUpdatedListener
{
    public static Activity start_activity = null;
    RelativeLayout rel_native_ad;
    NativeAd ad_mob_native_ad;
    AdRequest native_ad_request;

    private BillingClient mBillingClient;

    ImageView img_info;
    ImageView img_ad_free;

    RelativeLayout rl_device_info,rl_setting1,rl_setting2,rl_setting3;

    String action_name = "";
    String SETTINGS_1 = "settings_1";
    String SETTINGS_2 = "settings_2";
    String SETTINGS_3 = "settings_3";

    int MANAGE_ACCESS_ID = 1001;

    Animation objAnimation;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        start_activity = StartActivity.this;

        Context mContext = StartActivity.this;
        MyExitView.init(mContext);

        objAnimation = AnimationUtils.loadAnimation(StartActivity.this, R.anim.viewpush);

        img_info = findViewById(R.id.img_info);
        img_ad_free = findViewById(R.id.img_ad_free);

        InAppBillingSetup();

        if(Build.VERSION.SDK_INT>=24)
        {
            try
            {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        rl_device_info = findViewById(R.id.rl_deviceinfo);
        rl_setting1 = findViewById(R.id.rl_setting1);
        rl_setting2 = findViewById(R.id.rl_setting2);
        rl_setting3 = findViewById(R.id.rl_setting3);

        /*boolean settingsCanWrite = Settings.System.canWrite(this);
        if(!settingsCanWrite)
        {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(StartActivity.this,"You have system write settings permission now.",Toast.LENGTH_SHORT).show();
        }*/

        rl_device_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);
                startActivity(new Intent(StartActivity.this,SystemInfoActivity.class));
            }
        });

        rl_setting1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);

                action_name = SETTINGS_1;
                boolean settingsCanWrite = Settings.System.canWrite(StartActivity.this);
                if(settingsCanWrite)
                {
                    SettingsScreen1();
                }
                else
                {
                    ManagerWriteDialog();
                }
            }
        });

        rl_setting2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);

                action_name = SETTINGS_2;
                boolean settingsCanWrite = Settings.System.canWrite(StartActivity.this);
                if(settingsCanWrite)
                {
                    SettingsScreen2();
                }
                else
                {
                    ManagerWriteDialog();
                }
            }
        });

        rl_setting3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                v.startAnimation(objAnimation);

                action_name = SETTINGS_3;
                boolean settingsCanWrite = Settings.System.canWrite(StartActivity.this);
                if(settingsCanWrite)
                {
                    SettingsScreen3();
                }
                else
                {
                    ManagerWriteDialog();
                }
            }
        });

        img_info.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                view.startAnimation(objAnimation);
                Intent intent = new Intent(StartActivity.this, InfoActivity.class);
                startActivity(intent);
            }
        });

        img_ad_free.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                view.startAnimation(objAnimation);
                ConformPurchaseDialog();
            }
        });
    }

    private void ManagerWriteDialog()
    {
        final Dialog conform_dialog = new Dialog(this,R.style.TransparentBackground);
        conform_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        conform_dialog.setContentView(R.layout.dialog_rate);

        TextView dialog_txt_header = conform_dialog.findViewById(R.id.dialog_conform_txt_header);
        TextView dialog_txt_message = conform_dialog.findViewById(R.id.dialog_conform_txt_message);

        Button dialog_btn_yes = conform_dialog.findViewById(R.id.dialog_conform_btn_yes);
        Button dialog_btn_no = conform_dialog.findViewById(R.id.dialog_conform_btn_no);

        String header_text = "Manage Write Settings";
        String message_text = "This will redirect you to Manage Settings screen. Allow Easy Quick Settings app for better use." + "\n" + "Are you sure you want to continue?";

        dialog_txt_header.setText(header_text);
        dialog_txt_message.setText(message_text);

        dialog_btn_yes.setText("Continue");
        dialog_btn_no.setText("Cancel");

        dialog_btn_yes.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {
                conform_dialog.dismiss();

                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                startActivityForResult(intent, MANAGE_ACCESS_ID);
            }
        });

        dialog_btn_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                conform_dialog.dismiss();
            }
        });

        conform_dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MANAGE_ACCESS_ID)
        {
            if (action_name.equalsIgnoreCase(SETTINGS_1))
            {
                boolean settingsCanWrite = Settings.System.canWrite(StartActivity.this);
                if(settingsCanWrite)
                {
                    SettingsScreen1();
                }
            }
            else if (action_name.equalsIgnoreCase(SETTINGS_2))
            {
                boolean settingsCanWrite = Settings.System.canWrite(StartActivity.this);
                if(settingsCanWrite)
                {
                    SettingsScreen2();
                }
            }
            else if (action_name.equalsIgnoreCase(SETTINGS_3))
            {
                boolean settingsCanWrite = Settings.System.canWrite(StartActivity.this);
                if(settingsCanWrite)
                {
                    SettingsScreen3();
                }
            }
        }
    }

    private void SettingsScreen1()
    {
        startActivity(new Intent(StartActivity.this,SettingsActivity.class));
    }

    private void SettingsScreen2()
    {
        startActivity(new Intent(StartActivity.this, SettingsActivity2.class));
    }

    private void SettingsScreen3()
    {
        startActivity(new Intent(StartActivity.this, SettingsActivity3.class));
    }

    // In-App Billing Start //
    private void InAppBillingSetup()
    {
        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener()
        {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult)
            {

            }

            @Override
            public void onBillingServiceDisconnected()
            {

            }
        });

        queryPurchases();
    }

    private void ConformPurchaseDialog()
    {
        final Dialog conform_dialog = new Dialog(this, R.style.TransparentBackground);
        conform_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        conform_dialog.setContentView(R.layout.dialog_rate);

        Button conform_dialog_btn_yes = (Button) conform_dialog.findViewById(R.id.dialog_conform_btn_yes);
        Button conform_dialog_btn_no = (Button) conform_dialog.findViewById(R.id.dialog_conform_btn_no);

        TextView conform_dialog_txt_header = (TextView) conform_dialog.findViewById(R.id.dialog_conform_txt_header);
        TextView conform_dialog_txt_message = (TextView) conform_dialog.findViewById(R.id.dialog_conform_txt_message);

        String conform_dialog_header = "Confirm Your In-App Purchase";
        String conform_dialog_message = "With purchasing this item all ads from application will be removed.";

        conform_dialog_txt_header.setText(conform_dialog_header);
        conform_dialog_txt_message.setText(conform_dialog_message);

        conform_dialog_btn_yes.setText("Purchase");
        conform_dialog_btn_no.setText("Cancel");

        conform_dialog_btn_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    InAppPurchase();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                conform_dialog.dismiss();
            }
        });

        conform_dialog_btn_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                conform_dialog.dismiss();
            }
        });

        conform_dialog.show();
    }

    private void InAppPurchase()
    {
        List<String> skuList = new ArrayList<>();
        skuList.add(EUGeneralHelper.REMOVE_ADS_PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener()
        {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList)
            {
                // Process the result.
                for (SkuDetails skuDetails : skuDetailsList)
                {
                    String sku = skuDetails.getSku();

                    if (EUGeneralHelper.REMOVE_ADS_PRODUCT_ID.equals(sku))
                    {
                        //premiumUpgradePrice = price;
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();
                        int responseCode = mBillingClient.launchBillingFlow(StartActivity.this, flowParams).getResponseCode();
                    }
                }

            }
        });
    }

    private void queryPurchases()
    {
        //Method not being used for now, but can be used if purchases ever need to be queried in the future
        mBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener()
        {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list)
            {
                List<Purchase> purchasesList = list;

                if (purchasesList == null)
                {
                    return;
                }

                if (!purchasesList.isEmpty())
                {
                    for (Purchase purchase : purchasesList)
                    {
                        ArrayList<String> itemSKU = purchase.getSkus();
                        if (itemSKU.contains(EUGeneralHelper.REMOVE_ADS_PRODUCT_ID))
                        {
                            FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,true);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases)
    {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null)
        {
            for (Purchase purchase : purchases)
            {
                handlePurchase(purchase);
            }
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED)
        {
            // Handle an error caused by a user cancelling the purchase flow.
            // Log.d(TAG, "User Canceled" + billingResult.getResponseCode());
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)
        {
            FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,true);
            HideViews();
        }
        else
        {
            //Log.d(TAG, "Other code" + BillingClient.BillingResponseCode);
            // Handle any other error codes.
        }
    }

    private void handlePurchase(Purchase purchase)
    {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
        {
            // Grant entitlement to the user.
            AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener()
            {
                @Override
                public void onAcknowledgePurchaseResponse(BillingResult billingResult)
                {
                    Log.e("result",""+billingResult.getResponseCode()+"::"+billingResult.getDebugMessage());

                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK )
                    {
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,true);
                        HideViews();
                    }
                }
            };

            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged())
            {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
            else
            {
                ArrayList<String> itemSKU = purchase.getSkus();
                if (itemSKU.contains(EUGeneralHelper.REMOVE_ADS_PRODUCT_ID))
                {
                    FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,true);
                    HideViews();
                }
            }
        }
    }
    // In-App Billing End //

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
                        EUGeneralClass.DoConsentProcess(this, StartActivity.this);
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
            LoadAdMobNativeAd();
        }
        else
        {
            // Hide Ads
            HideViews();
        }
    }

    private void HideViews()
    {
        rel_native_ad = findViewById(R.id.ad_layout);
        rel_native_ad.setVisibility(View.GONE);

        img_ad_free = findViewById(R.id.img_ad_free);
        img_ad_free.setVisibility(View.GONE);
    }

    private void LoadAdMobNativeAd()
    {
        AdLoader.Builder builder = new AdLoader.Builder(this, EUGeneralHelper.ad_mob_native_ad_id);
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener()
        {
            @Override
            public void onNativeAdLoaded(NativeAd nativeAd)
            {
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.native_ad_layout);
                NativeAdView adView = (NativeAdView) getLayoutInflater().inflate(R.layout.layout_native_ad, null);
                PopulateAdMobNativeAdView(nativeAd, adView);
                frameLayout.removeAllViews();
                frameLayout.addView(adView);
            }
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener()
        {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError)
            {
                super.onAdFailedToLoad(loadAdError);
                Log.e("Unified Native:", "Failed to load native ad!");
            }
        }).build();

        Bundle non_personalize_bundle = new Bundle();
        non_personalize_bundle.putString("npa", "1");

        boolean is_show_non_personalize = FastSave.getInstance().getBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
        if(is_show_non_personalize)
        {
            native_ad_request = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, non_personalize_bundle).build();
        }
        else
        {
            native_ad_request = new AdRequest.Builder().build();
        }

        adLoader.loadAd(native_ad_request);

    }

    private void PopulateAdMobNativeAdView(NativeAd nativeAd, NativeAdView adView)
    {
        ad_mob_native_ad = nativeAd;

        View icon_view = adView.findViewById(R.id.ad_app_icon);
        View headline_view = adView.findViewById(R.id.ad_headline);
        View body_view = adView.findViewById(R.id.ad_body);
        View rating_view = adView.findViewById(R.id.ad_stars);
        View price_view = adView.findViewById(R.id.ad_price);
        View store_view = adView.findViewById(R.id.ad_store);
        View advertiser_view = adView.findViewById(R.id.ad_advertiser);
        View call_to_action_view = adView.findViewById(R.id.ad_call_to_action);

        adView.setIconView(icon_view);
        adView.setHeadlineView(headline_view);
        adView.setBodyView(body_view);
        adView.setStarRatingView(rating_view);
        adView.setPriceView(price_view);
        adView.setStoreView(store_view);
        adView.setAdvertiserView(advertiser_view);
        adView.setCallToActionView(call_to_action_view);

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        ((TextView) headline_view).setText(nativeAd.getHeadline());
        ((TextView) body_view).setText(nativeAd.getBody());
        ((Button) call_to_action_view).setText(nativeAd.getCallToAction());

        // check before trying to display them.
        if (nativeAd.getIcon() == null)
        {
            icon_view.setVisibility(View.GONE);
        }
        else
        {
            ((ImageView) icon_view).setImageDrawable(nativeAd.getIcon().getDrawable());
            icon_view.setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null)
        {
            price_view.setVisibility(View.INVISIBLE);
        }
        else
        {
            price_view.setVisibility(View.VISIBLE);
            ((TextView) price_view).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null)
        {
            store_view.setVisibility(View.INVISIBLE);
        }
        else
        {
            store_view.setVisibility(View.VISIBLE);
            ((TextView) store_view).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null)
        {
            rating_view.setVisibility(View.INVISIBLE);
        }
        else
        {
            ((RatingBar) rating_view).setRating(nativeAd.getStarRating().floatValue());
            rating_view.setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null)
        {
            advertiser_view.setVisibility(View.INVISIBLE);
        }
        else
        {
            ((TextView) advertiser_view).setText(nativeAd.getAdvertiser());
            advertiser_view.setVisibility(View.VISIBLE);
        }

        //mediaView.setVisibility(View.VISIBLE);
        //mainImageView.setVisibility(View.VISIBLE);
        body_view.setVisibility(View.GONE);
        rating_view.setVisibility(View.VISIBLE);
        advertiser_view.setVisibility(View.VISIBLE);
        store_view.setVisibility(View.GONE);
        price_view.setVisibility(View.GONE);

        adView.setNativeAd(nativeAd);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(ad_mob_native_ad != null)
        {
            Log.e("Destroy :","Native Ad destroyed...");
            ad_mob_native_ad.destroy();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(ad_mob_native_ad != null)
        {
            Log.e("Pause :","Native Ad paused...");
            ad_mob_native_ad.destroy();
        }
    }

    @Override
    public void onBackPressed()
    {
        boolean is_hide_ads = FastSave.getInstance().getBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
        if(!is_hide_ads)
        {
            boolean is_online = EUGeneralClass.isOnline(StartActivity.this);
            if(is_online)
            {
                boolean is_g_user = FastSave.getInstance().getBoolean(EUGeneralHelper.GOOGLE_PLAY_STORE_USER_KEY, false);
                if (is_g_user)
                {
                    // Call Exit Screen
                    boolean is_eea_user = FastSave.getInstance().getBoolean(EUGeneralHelper.EEA_USER_KEY,false);
                    boolean is_consent_set = FastSave.getInstance().getBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,false);
                    boolean is_show_non_personalize = FastSave.getInstance().getBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
                    MyExitView.OpenExitScreen(is_hide_ads,is_eea_user,is_consent_set,is_show_non_personalize, EUGeneralHelper.ad_mob_native_ad_id);
                }
                else
                {
                    EUGeneralClass.ExitDialog(StartActivity.this,start_activity);
                }
            }
            else
            {
                EUGeneralClass.ExitDialog(StartActivity.this,start_activity);
            }
        }
        else
        {
            EUGeneralClass.ExitDialog(StartActivity.this,start_activity);
        }
    }
}