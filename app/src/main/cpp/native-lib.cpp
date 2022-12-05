#include <jni.h>
#include <string>
#include <vector>


std::string ad_mob_pub_id="pub-8677665109043809::";
std::string ad_mob_app_id="ca-app-pub-8677665109043809~1138777225::";
std::string ad_mob_banner_ad_id="ca-app-pub-8677665109043809/4293415990::";
std::string ad_mob_banner_rectangle_ad_id="ca-app-pub-8677665109043809/8041089316::";
std::string ad_mob_interstitial_ad_id="ca-app-pub-8677665109043809/6845565329::";
std::string ad_mob_native_ad_id="ca-app-pub-8677665109043809/9697306983::";
std::string ad_mob_open_ads_ad_id="ca-app-pub-8677665109043809/3634940445::";


/*
std::string ad_mob_pub_id = "pub-3940256099942544::";
std::string ad_mob_app_id = "ca-app-pub-3940256099942544~3347511713::";
std::string ad_mob_banner_ad_id = "ca-app-pub-3940256099942544/6300978111::";
std::string ad_mob_banner_rectangle_ad_id="ca-app-pub-3940256099942544/6300978111::";
std::string ad_mob_interstitial_ad_id = "ca-app-pub-3940256099942544/1033173712::";
std::string ad_mob_native_ad_id = "ca-app-pub-3940256099942544/1044960115::";
std::string ad_mob_open_ads_ad_id="ca-app-pub-3940256099942544/3419835294::";
*/

extern "C" JNIEXPORT jstring
JNICALL
Java_com_fbd_quicksettings_vs_FireBaseInitializeApp_StringADMobCode(JNIEnv *env, jobject)
{
    std::string final_s =
            ad_mob_pub_id + ad_mob_app_id +
            ad_mob_banner_ad_id + ad_mob_banner_rectangle_ad_id +
            ad_mob_interstitial_ad_id +
            ad_mob_native_ad_id + ad_mob_open_ads_ad_id;
    return env->NewStringUTF(final_s.c_str());
}
