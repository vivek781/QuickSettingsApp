package com.fbd.quicksettings.vs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;

import de.psdev.licensesdialog.LicensesDialog;

@SuppressWarnings("deprecation")
public class InfoActivity extends Activity implements PurchasesUpdatedListener
{
	public static Activity info_activity = null;
	RelativeLayout rel_native_ad;
	NativeAd ad_mob_native_ad;
	AdRequest native_ad_request;

	InterstitialAd ad_mob_interstitial;
	AdRequest interstitial_adRequest;

	TextView txt_version_name;

	RelativeLayout rel_ad_free;
	RelativeLayout rel_user_consent;
	RelativeLayout rel_share_app;
	RelativeLayout rel_rate_us;
	RelativeLayout rel_privacy;
	RelativeLayout rel_license;

	View view_below_ad_free;
	View view_below_user_consent;

	boolean is_eea_user = false;

	private BillingClient mBillingClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SetView();
	}

	private void SetView()
	{
		// TODO Auto-generated method stub
		try
		{
			setContentView(R.layout.activity_info);
			info_activity = InfoActivity.this;

			EUGeneralHelper.is_show_open_ad = true;

			InAppBillingSetup();

			rel_ad_free = findViewById(R.id.setting_rel_ad_free);
			rel_user_consent = findViewById(R.id.setting_rel_user_consent);
			rel_share_app = findViewById(R.id.setting_rel_share_app);
			rel_rate_us = findViewById(R.id.setting_rel_rate_us);
			rel_privacy = findViewById(R.id.setting_rel_privacy);
			rel_license = findViewById(R.id.setting_rel_license);

			view_below_ad_free = (View) findViewById(R.id.setting_view_1);
			view_below_user_consent = (View) findViewById(R.id.setting_view_3);

			txt_version_name = (TextView)findViewById(R.id.setting_txt_version);

			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String versionName = packageInfo.versionName;

			txt_version_name.setText(versionName);

			is_eea_user = FastSave.getInstance().getBoolean(EUGeneralHelper.EEA_USER_KEY,false);
			if(!is_eea_user)
			{
				rel_user_consent.setVisibility(View.GONE);
				view_below_user_consent.setVisibility(View.GONE);
			}
			else
			{
				rel_user_consent.setVisibility(View.VISIBLE);
				view_below_user_consent.setVisibility(View.VISIBLE);
			}

			rel_ad_free.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					ConformPurchaseDialog();
				}
			});

			rel_user_consent.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					boolean is_online = EUGeneralClass.isOnline(InfoActivity.this);
					if(is_online)
					{
						EUGeneralClass.DoConsentProcessSetting(InfoActivity.this,info_activity);
					}
					else
					{
						String toast_message = "Please enable your internet connection!";
						EUGeneralClass.ShowErrorToast(InfoActivity.this,toast_message);
					}
				}
			});

			rel_share_app.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					EUGeneralClass.ShareApp(InfoActivity.this);
				}
			});

			rel_rate_us.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					EUGeneralClass.RateApp(InfoActivity.this);
				}
			});

			rel_privacy.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					PrivacyPolicyScreen();
				}
			});

			rel_license.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					try
					{
						LicenseAgreement();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});

		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void PrivacyPolicyScreen()
	{
		Intent i = new Intent(InfoActivity.this, PrivacyPolicyActivity.class);
		startActivity(i);
	}

	private void LicenseAgreement()
	{
		// TODO Auto-generated method stub
		new LicensesDialog.Builder(this)
				.setNotices(R.raw.notices)
				.build()
				.show();
	}

	// In-App Billing Start //
	private void InAppBillingSetup()
	{
		mBillingClient = BillingClient.newBuilder(this)
				.enablePendingPurchases()
				.setListener(this)
				.build();
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
		final Dialog conform_dialog = new Dialog(this,R.style.TransparentBackground);
		conform_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		conform_dialog.setContentView(R.layout.dialog_rate);

		Button conform_dialog_btn_yes = (Button) conform_dialog.findViewById(R.id.dialog_conform_btn_yes);
		Button conform_dialog_btn_no = (Button) conform_dialog.findViewById(R.id.dialog_conform_btn_no);

		TextView conform_dialog_txt_header = (TextView)conform_dialog.findViewById(R.id.dialog_conform_txt_header);
		TextView conform_dialog_txt_message = (TextView)conform_dialog.findViewById(R.id.dialog_conform_txt_message);

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
						int responseCode = mBillingClient.launchBillingFlow(InfoActivity.this, flowParams).getResponseCode();
					}
				}

			}
		});
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
						EUGeneralClass.DoConsentProcess(this,InfoActivity.this);
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
		rel_native_ad = findViewById(R.id.ad_layout);
		rel_native_ad.setVisibility(View.GONE);

		rel_ad_free.setVisibility(View.GONE);
		view_below_ad_free.setVisibility(View.GONE);

		rel_user_consent.setVisibility(View.GONE);
		view_below_user_consent.setVisibility(View.GONE);
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
			InterstitialAd.load(this, EUGeneralHelper.ad_mob_interstitial_ad_id,interstitial_adRequest, new InterstitialAdLoadCallback()
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
					Log.e("TAG", "The ad was dismissed.");
					BackScreen();
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
