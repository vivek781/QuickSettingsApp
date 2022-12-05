package com.fbd.quicksettings.vs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appizona.yehiahd.fastsave.FastSave;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EUGeneralClass
{
    private static String TAG = "EUGeneralClass: ";

    private static Context mContext;

    public static boolean is_online;

    public EUGeneralClass(Context ctx)
    {
        // TODO Auto-generated constructor stub
        mContext = ctx;
    }

    public static void RateApp(Context ctx)
    {
        // TODO Auto-generated method stub
        try
        {
            mContext = ctx;
            String rateUrl = EUGeneralHelper.rate_url + mContext.getPackageName();

            String dialog_header = "Rate App";
            String dialog_message = "If you enjoy this app, would you mind taking a moment to rate it?" + "\n" + "Thanks for your support!";

            ConformRateDialog(mContext, rateUrl, dialog_header, dialog_message);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static void ShareApp(Context ctx)
    {
        // TODO Auto-generated method stub
        try
        {
            mContext = ctx;
            String app_name = mContext.getResources().getString(R.string.app_name) + " :";
            String shareUrl = EUGeneralHelper.rate_url + mContext.getPackageName();

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, app_name);
            sharingIntent.putExtra(Intent.EXTRA_TEXT, app_name + "\n" + shareUrl);
            mContext.startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*public static boolean isOnline(Context mContext)
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected())
        {
            is_online = true;
            return is_online;
        }
        else
        {
            is_online = false;
            return is_online;
        }
    }*/

    public static Boolean isOnline(Context mContext)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        }
        else
        {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    public static void ShowSuccessToast(Context mContext, String toast_message)
    {
        MDToast mdToast = MDToast.makeText(mContext, toast_message, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS);
        mdToast.show();
    }
    public static void ShowInfoToast(Context mContext, String toast_message)
    {
        MDToast mdToast = MDToast.makeText(mContext, toast_message, MDToast.LENGTH_SHORT, MDToast.TYPE_INFO);
        mdToast.show();
    }

    public static void ShowWarningToast(Context mContext, String toast_message)
    {
        MDToast mdToast = MDToast.makeText(mContext, toast_message, MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING);
        mdToast.show();
    }

    public static void ShowErrorToast(Context mContext, String toast_message)
    {
        MDToast mdToast = MDToast.makeText(mContext, toast_message, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR);
        mdToast.show();
    }

    public static void ConformRateDialog(final Context mContext, final String appUrl, final String header, final String message)
    {
        final Dialog conform_dialog = new Dialog(mContext,R.style.TransparentBackground);
        conform_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        conform_dialog.setContentView(R.layout.dialog_rate);

        Button conform_dialog_btn_yes = (Button) conform_dialog.findViewById(R.id.dialog_conform_btn_yes);
        Button conform_dialog_btn_no = (Button) conform_dialog.findViewById(R.id.dialog_conform_btn_no);

        TextView conform_dialog_txt_header = (TextView)conform_dialog.findViewById(R.id.dialog_conform_txt_header);
        TextView conform_dialog_txt_message = (TextView)conform_dialog.findViewById(R.id.dialog_conform_txt_message);

        String conform_dialog_header = header;
        String conform_dialog_message = message;

        conform_dialog_txt_header.setText(conform_dialog_header);
        conform_dialog_txt_message.setText(conform_dialog_message);

        conform_dialog_btn_yes.setText("Rate now");
        conform_dialog_btn_no.setText("Cancel");

        conform_dialog_btn_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Uri uri = Uri.parse(appUrl);
                    Intent view_intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(view_intent);
                    EUGeneralHelper.is_show_open_ad = false;
                }
                catch (ActivityNotFoundException e)
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

    public static void ExitDialog(final Context mContext, final Activity mActivity)
    {
        final Dialog exit_dialog = new Dialog(mContext,R.style.TransparentBackground);
        exit_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exit_dialog.setContentView(R.layout.dialog_rate);

        Button dialog_btn_yes = exit_dialog.findViewById(R.id.dialog_conform_btn_yes);
        Button dialog_btn_no = exit_dialog.findViewById(R.id.dialog_conform_btn_no);

        TextView dialog_txt_header = exit_dialog.findViewById(R.id.dialog_conform_txt_header);
        TextView dialog_txt_message = exit_dialog.findViewById(R.id.dialog_conform_txt_message);

        String dialog_header = "Exit";
        String dialog_message = "Thank You For Using Our Application." + "\n" +"Are you sure you want to exit from application?";

        dialog_txt_header.setText(dialog_header);
        dialog_txt_message.setText(dialog_message);

        dialog_btn_yes.setText("Exit");
        dialog_btn_no.setText("Cancel");

        dialog_btn_yes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exit_dialog.dismiss();
                ExitApp(mActivity);
            }
        });

        dialog_btn_no.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exit_dialog.dismiss();
            }
        });

        exit_dialog.show();
    }

    public static void DoConsentProcess(final Context mContext, final Activity mActivity)
    {
        /*// Testing Purpose Start //
        String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();

        ConsentInformation consentInformation = ConsentInformation.getInstance(mContext);
        ConsentInformation.getInstance(mContext).addTestDevice(deviceId);
        ConsentInformation.getInstance(mContext).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        // Testing Purpose End //*/

        ConsentInformation consentInformation = ConsentInformation.getInstance(mContext);

        String[] publisherIds = {EUGeneralHelper.ad_mob_pub_id};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener()
        {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus)
            {
                // User's consent status successfully updated.
                boolean is_user_eea = ConsentInformation.getInstance(mContext).isRequestLocationInEeaOrUnknown();
                if(is_user_eea)
                {
                    Log.e(TAG,"User is from EEA!");
                    if(consentStatus == ConsentStatus.PERSONALIZED)
                    {
                        Log.e(TAG,"User approve PERSONALIZED Ads!");
                        ConsentInformation.getInstance(mContext).setConsentStatus(ConsentStatus.PERSONALIZED);

                        FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);
                    }
                    else if(consentStatus == ConsentStatus.NON_PERSONALIZED)
                    {
                        Log.e(TAG,"User approve NON_PERSONALIZED Ads!");
                        ConsentInformation.getInstance(mContext).setConsentStatus(ConsentStatus.NON_PERSONALIZED);

                        FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,true);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);
                    }
                    else if(consentStatus == ConsentStatus.UNKNOWN)
                    {
                        Log.e(TAG,"User has neither granted nor declined consent!");
                        ShowAdMobConsentDialog(mContext,mActivity,false);
                    }
                }
                else
                {
                    Log.e(TAG,"User is not from EEA!");
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription)
            {
                // User's consent status failed to update.
                Log.e(TAG,"Consent Status Failed :" + errorDescription);
                //DismissLoadingDialog();
            }
        });
    }

    public static void DoConsentProcessSetting(final Context mContext, final Activity mActivity)
    {
        String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();

        ConsentInformation consentInformation = ConsentInformation.getInstance(mContext);

        String[] publisherIds = {EUGeneralHelper.ad_mob_pub_id};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener()
        {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus)
            {
                // User's consent status successfully updated.
                boolean is_user_eea = ConsentInformation.getInstance(mContext).isRequestLocationInEeaOrUnknown();
                if(is_user_eea)
                {
                    Log.e(TAG,"User is from EEA!");
                    if(consentStatus == ConsentStatus.PERSONALIZED)
                    {
                        Log.e(TAG,"User approve PERSONALIZED Ads!");
                        ConsentInformation.getInstance(mContext).setConsentStatus(ConsentStatus.PERSONALIZED);

                        FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                        ShowAdMobConsentDialog(mContext,mActivity,true);
                    }
                    else if(consentStatus == ConsentStatus.NON_PERSONALIZED)
                    {
                        Log.e(TAG,"User approve NON_PERSONALIZED Ads!");
                        ConsentInformation.getInstance(mContext).setConsentStatus(ConsentStatus.NON_PERSONALIZED);

                        FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,true);
                        FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                        ShowAdMobConsentDialog(mContext,mActivity,true);
                    }
                    else if(consentStatus == ConsentStatus.UNKNOWN)
                    {
                        Log.e(TAG,"User has neither granted nor declined consent!");
                        ShowAdMobConsentDialog(mContext,mActivity,true);
                    }
                }
                else
                {
                    Log.e(TAG,"User is not from EEA!");
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription)
            {
                // User's consent status failed to update.
                Log.e(TAG,"Consent Status Failed :" + errorDescription);
                //DismissLoadingDialog();
            }
        });
    }

    public static void ShowAdMobConsentDialog(final Context mContext, final Activity mActivity, boolean showCancel)
    {
        final Dialog eu_consent_dialog = new Dialog(mContext,R.style.TransparentBackground);
        eu_consent_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        eu_consent_dialog.setContentView(R.layout.eu_consent_custom);

        eu_consent_dialog.setCancelable(showCancel);

        TextView txt_app_name = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_app_name);
        TextView txt_care = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_care);
        TextView txt_ask_continue = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_ask_continue);
        TextView txt_desc = eu_consent_dialog.findViewById(R.id.eu_dialog_txt_desc);
        TextView txt_learn_more = eu_consent_dialog.findViewById(R.id.eu_dialog_lbl_learn_more);

        RelativeLayout rel_continue_ad = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_continue);
        RelativeLayout rel_irrelevant = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_irrelevant);
        RelativeLayout rel_remove_ads = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_remove_ads);
        RelativeLayout rel_exit = eu_consent_dialog.findViewById(R.id.eu_dialog_rel_exit);

        rel_remove_ads.setVisibility(View.GONE);

        String appName = mContext.getResources().getString(R.string.app_name);
        String desc_data = "You can change your choice anytime for " + appName + " in the app settings.Our partners collect data and use a unique identifier on your device to show you ads.";
        String care_data = "We care about your privacy & data security.We keep this app free by showing ads.";
        String ask_continue_data = "Can we continue to use yor data to tailor ads for you?";
        String learn_more = "Privacy & Policy" + "\n" + "How App & our partners uses your data!";

        txt_app_name.setText(appName);
        txt_care.setText(care_data);
        txt_ask_continue.setText(ask_continue_data);
        txt_desc.setText(desc_data);
        txt_learn_more.setText(learn_more);

        rel_continue_ad.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eu_consent_dialog.cancel();

                String toast_message = "Thank you for continue to see personalize ads!";
                ShowSuccessToast(mContext,toast_message);

                FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,false);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                ConsentInformation.getInstance(mContext).setConsentStatus(ConsentStatus.PERSONALIZED);
            }
        });

        rel_irrelevant.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eu_consent_dialog.cancel();

                String toast_message = "Thank you for continue to see non-personalize ads!";
                ShowSuccessToast(mContext,toast_message);

                FastSave.getInstance().saveBoolean(EUGeneralHelper.REMOVE_ADS_KEY,false);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.SHOW_NON_PERSONALIZE_ADS_KEY,true);
                FastSave.getInstance().saveBoolean(EUGeneralHelper.ADS_CONSENT_SET_KEY,true);

                ConsentInformation.getInstance(mContext).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
            }
        });

        rel_exit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                eu_consent_dialog.cancel();
                ExitApp(mActivity);
            }
        });

        txt_learn_more.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(EUGeneralHelper.privacy_policy_url));
                mContext.startActivity(browserIntent);
            }
        });

        eu_consent_dialog.show();
    }

    public static final String md5(final String s)
    {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
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

    @SuppressLint("NewApi")
    public static void ExitApp(Activity mActivity)
    {
        mActivity.finishAndRemoveTask();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }
}
