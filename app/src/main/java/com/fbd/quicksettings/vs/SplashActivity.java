package com.fbd.quicksettings.vs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;

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
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity implements PurchasesUpdatedListener
{
    String TAG = "SplashActivity :";

    int waiting_second = 3;

    InterstitialAd ad_mob_interstitial;
    AdRequest interstitial_adRequest;
    boolean Ad_Show = false;

    boolean in_app_check = false;
    private BillingClient mBillingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            getWindow().setDecorFitsSystemWindows(false);
        }
        else
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SetView();
    }

    private void SetView()
    {
        mBillingClient = BillingClient.newBuilder(SplashActivity.this).enablePendingPurchases().setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener()
        {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult)
            {
                try
                {
                    mBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP, new PurchasesResponseListener()
                    {
                        @Override
                        public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> list)
                        {
                            List<Purchase> arrayPurchaseList = list;
                            if (arrayPurchaseList.toString().contains(EUGeneralHelper.REMOVE_ADS_PRODUCT_ID))
                            {
                                in_app_check = true;
                            }
                            else
                            {
                                in_app_check = false;
                            }
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBillingServiceDisconnected()
            {

            }
        });

        queryPurchases();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            // Using handler with postDelayed called runnable run method
            @Override
            public void run()
            {
                if (in_app_check)
                {
                    FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,true);
                    ContinueWithoutAdsProcess();
                }
                else
                {
                    boolean is_hide_ads = FastSave.getInstance().getBoolean(EUGeneralHelper.REMOVE_ADS_KEY, false);
                    if (!is_hide_ads)
                    {
                        boolean is_online = EUGeneralClass.isOnline(SplashActivity.this);
                        if (is_online)
                        {
                            boolean is_consent_set = FastSave.getInstance().getBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY, false);
                            if (is_consent_set)
                            {
                                ContinueAdsProcess();
                            }
                            else
                            {
                                DoConsentProcess();
                            }
                        }
                        else
                        {
                            ContinueWithoutAdsProcess();
                        }
                    }
                    else
                    {
                        ContinueWithoutAdsProcess();
                    }
                }
            }
        }, 5 * 1000);
    }

    private void DoConsentProcess()
    {
            /*// Testing Purpose Start //
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String deviceId = md5(android_id).toUpperCase();

            ConsentInformation consentInformation = ConsentInformation.getInstance(this);
            ConsentInformation.getInstance(this).addTestDevice(deviceId);
            ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
            // Testing Purpose End //*/

        ConsentInformation consentInformation = ConsentInformation.getInstance(this);

        String[] publisherIds = {EUGeneralHelper.ad_mob_pub_id};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener()
        {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus)
            {
                // User's consent status successfully updated.
                boolean is_user_eea = ConsentInformation.getInstance(SplashActivity.this).isRequestLocationInEeaOrUnknown();
                if(is_user_eea)
                {
                    Log.e(TAG,"User is from EEA!");
                    FastSave.getInstance().saveBoolean(EUGeneralHelper.EEA_USER_KEY,true);

                    if(consentStatus == ConsentStatus.PERSONALIZED)
                    {
                        Log.e("Consent Status :","User approve PERSONALIZED Ads!");
                        ConsentInformation.getInstance(SplashActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);

                        FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                        ContinueAdsProcess();
                    }
                    else if(consentStatus == ConsentStatus.NON_PERSONALIZED)
                    {
                        Log.e(TAG,"User approve NON_PERSONALIZED Ads!");
                        ConsentInformation.getInstance(SplashActivity.this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);

                        FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,true);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                        ContinueAdsProcess();
                    }
                    else if(consentStatus == ConsentStatus.UNKNOWN)
                    {
                        Log.e(TAG,"User has neither granted nor declined consent!");
                        ShowAdMobConsentDialog(false);
                    }
                }
                else
                {
                    Log.e(TAG,"User is not from EEA!");
                    FastSave.getInstance().saveBoolean(EUGeneralHelper.EEA_USER_KEY,false);
                    ContinueAdsProcess();
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription)
            {
                // User's consent status failed to update.
                Log.e("Consent Status :","Status Failed :" + errorDescription);
                boolean is_hide_ads = FastSave.getInstance().getBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                if(!is_hide_ads)
                {
                    ContinueAdsProcess();
                }
                else
                {
                    ContinueWithoutAdsProcess();
                }
            }
        });
    }

    public static final String md5(final String s)
    {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public void ShowAdMobConsentDialog(boolean showCancel)
    {
        final Dialog eu_consent_dialog = new Dialog(SplashActivity.this,R.style.TransparentBackground);
        eu_consent_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        eu_consent_dialog.setContentView(R.layout.eu_consent_custom);

        eu_consent_dialog.setCancelable(showCancel);

        TextView txt_app_name = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_app_name);
        TextView txt_care = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_care);
        TextView txt_ask_continue = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_ask_continue);
        TextView txt_desc = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_desc);
        TextView txt_learn_more = eu_consent_dialog.findViewById(R.id.eu_dialog_lbl_learn_more);

        RelativeLayout rel_personalize = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_continue);
        RelativeLayout rel_non_personalize = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_irrelevant);
        RelativeLayout rel_remove_ads = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_remove_ads);
        RelativeLayout rel_exit = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_exit);

        String appName = getResources().getString(R.string.app_name);
        String desc_data = "You need to click 'yes, continue' to use this app else you can pay for ad free version.Without your consent you will not be able to use this app.";
        String care_data = "We care about your privacy & data security.We keep this app free by showing ads.";
        String ask_continue_data = "This app uses your data to serve you personalized ads.";
        String learn_more = "Privacy & Policy" + "\n" + "How App & our partners uses your data!";

        txt_app_name.setText(appName);
        txt_care.setText(care_data);
        txt_ask_continue.setText(ask_continue_data);
        txt_desc.setText(desc_data);
        txt_learn_more.setText(learn_more);

        rel_personalize.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eu_consent_dialog.cancel();

                String toast_message = "Thank you for continue to see personalize ads!";
                EUGeneralClass.ShowSuccessToast(SplashActivity.this,toast_message);

                ConsentInformation.getInstance(SplashActivity.this).setConsentStatus(ConsentStatus.PERSONALIZED);

                FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                ContinueAdsProcess();
            }
        });

        rel_non_personalize.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eu_consent_dialog.cancel();

                String toast_message = "Thank you for continue to see non-personalize ads!";
                EUGeneralClass.ShowSuccessToast(SplashActivity.this,toast_message);

                ConsentInformation.getInstance(SplashActivity.this).setConsentStatus(ConsentStatus.NON_PERSONALIZED);

                FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,true);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                ContinueAdsProcess();
            }
        });

        rel_remove_ads.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eu_consent_dialog.cancel();
                InAppPurchase();
            }
        });

        rel_exit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eu_consent_dialog.cancel();
                ExitApp();
            }
        });

        txt_learn_more.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(EUGeneralHelper.privacy_policy_url));
                startActivity(browserIntent);
            }
        });

        eu_consent_dialog.show();
    }

    // In-App Billing Start //
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
                    String price = skuDetails.getPrice();
                    if (EUGeneralHelper.REMOVE_ADS_PRODUCT_ID.equals(sku))
                    {
                        //premiumUpgradePrice = price;
                        BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(skuDetails)
                                .build();
                        int responseCode = mBillingClient.launchBillingFlow(SplashActivity.this, flowParams).getResponseCode();
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
            Log.d(TAG, "User Canceled" + billingResult.getResponseCode());
        }
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED)
        {
            FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY, true);
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
                        ContinueWithoutAdsProcess();
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
                    ContinueWithoutAdsProcess();
                }
            }
        }
    }
    // In-App Billing End //

    private void ContinueAdsProcess()
    {
        boolean is_g_user = FastSave.getInstance().getBoolean(EUGeneralHelper.GOOGLE_PLAY_STORE_USER_KEY, false);
        if (is_g_user)
        {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
            {
                // Using handler with postDelayed called runnable run method
                @Override
                public void run()
                {
                    if (Ad_Show == true)
                    {
                        HomeScreen();
                    }
                }
            }, 15 * 1000); // wait for 5 seconds

            LoadAdMobInterstitialAd();
        }
        else
        {
            ContinueWithoutAdsProcess();
        }
    }

    private void ContinueWithoutAdsProcess()
    {
        EUGeneralHelper.is_ad_closed = true;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
        {
            // Using handler with postDelayed called runnable run method
            @Override
            public void run()
            {
                HomeScreen();
            }
        }, waiting_second * 1000); // wait for 3 seconds
    }

    private void LoadAdMobInterstitialAd()
    {
        // TODO Auto-generated method stub
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
        Ad_Show = true;

        //Interstitial Ad Start //
        InterstitialAd.load(this,EUGeneralHelper.ad_mob_interstitial_ad_id,interstitial_adRequest, new InterstitialAdLoadCallback()
        {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd)
            {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                ad_mob_interstitial = interstitialAd;
                if (Ad_Show == true)
                {
                    if(ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED))
                    {
                        HomeScreenWithoutFinish();
                        Ad_Show = false;
                        DisplayInterstitialAd();
                        overridePendingTransition(0,0);
                    }
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError)
            {
                // Handle the error
                ad_mob_interstitial = null;
                EUGeneralHelper.is_ad_closed = true;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable()
                {
                    // Using handler with postDelayed called runnable run method
                    @Override
                    public void run()
                    {
                        if(Ad_Show == true)
                        {
                            HomeScreen();
                        }
                    }
                }, 3 * 1000); // wait for 5 seconds
            }
        });
        //Interstitial Ad End //
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
                    Log.e("TAG", "The ad was dismissed.");
                    EUGeneralHelper.is_ad_closed = true;
                    finish();
                    EUGeneralHelper.is_show_open_ad = true;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError)
                {
                    // Called when fullscreen content failed to show.
                    Log.e("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent()
                {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    ad_mob_interstitial = null;
                    Log.e("TAG", "The ad was shown.");
                }
            });
        }
        ad_mob_interstitial.show(SplashActivity.this);
        EUGeneralHelper.is_show_open_ad = false;
    }

    private void HomeScreenWithoutFinish()
    {
        // TODO Auto-generated method stub
        Ad_Show = false;
        Intent i = new Intent(SplashActivity.this, StartActivity.class);
        startActivity(i);
    }

    private void HomeScreen()
    {
        // TODO Auto-generated method stub
        Ad_Show = false;
        Intent i = new Intent(SplashActivity.this, StartActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        ExitApp();
    }

    private void ExitApp()
    {
        moveTaskToBack(true);
        finish();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }
}
