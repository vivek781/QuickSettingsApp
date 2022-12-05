package com.fbd.quicksettings.vs;

import android.content.Context;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.appizona.yehiahd.fastsave.FastSave;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class FireBaseInitializeApp extends MultiDexApplication
{
    private static FireBaseInitializeApp fire_base_app;

    static {
        System.loadLibrary("native-lib");
    }
    public native String StringADMobCode();

    public static synchronized FireBaseInitializeApp getInstance()
    {
        return fire_base_app;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        fire_base_app = this;
        MobileAds.initialize(this, new OnInitializationCompleteListener()
        {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus)
            {
                Log.e("Mobile Ads :","Mobile Ads initialize complete!");
            }
        });

        FastSave.init(getApplicationContext());

        String ad_mob_id = StringADMobCode();
        String[] split = ad_mob_id.split("::");
        EUGeneralHelper.ad_mob_pub_id = split[0];
        EUGeneralHelper.ad_mob_app_id = split[1];
        EUGeneralHelper.ad_mob_banner_ad_id = split[2];
        EUGeneralHelper.ad_mob_banner_rectangle_ad_id = split[3];
        EUGeneralHelper.ad_mob_interstitial_ad_id = split[4];
        EUGeneralHelper.ad_mob_native_ad_id = split[5];
        EUGeneralHelper.ad_mob_open_ads_ad_id = split[6];

        boolean check_play_store_user = false;
        try
        {
            check_play_store_user = isInstalledViaGooglePlay(getApplicationContext());
            FastSave.getInstance().saveBoolean(EUGeneralHelper.GOOGLE_PLAY_STORE_USER_KEY, check_play_store_user);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            check_play_store_user = true;
            FastSave.getInstance().saveBoolean(EUGeneralHelper.GOOGLE_PLAY_STORE_USER_KEY, true);
        }
        Log.e("Play Store :", String.valueOf("App Install from PlayStore: " + check_play_store_user));
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // Check Play Install Start //
    public static boolean isInstalledViaGooglePlay(Context ctx)
    {
        return isInstalledVia(ctx, "com.android.vending");
    }

    public static boolean isInstalledVia(Context ctx, String required)
    {
        String installer = getInstallerPackageName(ctx);
        return required.equals(installer);
    }

    private static String getInstallerPackageName(Context ctx)
    {
        try
        {
            String packageName = ctx.getPackageName();
            PackageManager pm = ctx.getPackageManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                InstallSourceInfo info = pm.getInstallSourceInfo(packageName);
                if (info != null)
                {
                    return info.getInstallingPackageName();
                }
            }
            return pm.getInstallerPackageName(packageName);
        }
        catch (PackageManager.NameNotFoundException e)
        {

        }
        return "";
    }
    // Check Play Install End //


    /*public static boolean verifyInstallerId(Context context)
    {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }*/
}
