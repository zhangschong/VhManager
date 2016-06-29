package com.leon.tools.systemutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.leon.tools.utils.JavaUtils;

import java.util.Random;

/**
 * Android 电话相关信息
 *
 * @author zhanghong
 * @time 2015-6-8
 */
public class PhoneInfo {
    private Context mContext = null;

    public String systemVersion;
    public String model;
    public String identify;
    public String phone;
    public String androidUniqueId;
    private final int NETWORK_WIFI = 10;
    private final int NETWORK_UNKONW = 20;
    private final int NETWORK_2G = 21;
    private final int NETWORK_3G = 22;
    private final int NETWORK_4G = 23;
    public int networkTypeId = NETWORK_UNKONW;

    public PhoneInfo(Context context) {
        mContext = context;
    }

    private String returnStr(String str) {
        return null == str ? "" : str;
    }

    /**
     * android设备唯一标识 MD5(Device id + MAC + Android id)
     *
     * @return
     */
    public String getAndroidUniqueId() {
        if (null == androidUniqueId) {
            androidUniqueId = getIMEI() + getMac() + getAndroidID();
            androidUniqueId = JavaUtils.getStringMD5(androidUniqueId);
        }
        return returnStr(androidUniqueId);
    }

    /**
     * 手机唯一标识
     *
     * @return
     */
    public String getIMEI() {
        if (null == identify) {
            TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            identify = telephonyManager.getDeviceId();
        }
        return returnStr(identify);
    }

    /**
     * @return 网络类型
     */
    public int getNetworkTypeId() {
        if (NETWORK_UNKONW == networkTypeId) {
            ConnectivityManager connectMgr = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectMgr.getActiveNetworkInfo();
            if (null != info) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    networkTypeId = NETWORK_WIFI;
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                            networkTypeId = NETWORK_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            networkTypeId = NETWORK_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            networkTypeId = NETWORK_4G;
                            break;
                        default:
                            networkTypeId = NETWORK_UNKONW;
                            break;
                    }
                }

            }
        }
        return networkTypeId;
    }

    /**
     * @return 电话号码
     */
    public String getPhoneNum() {
        if (null == phone) {
            TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != mTelephonyMgr)
                phone = mTelephonyMgr.getLine1Number();
        }
        return returnStr(phone);
    }

    /**
     * 网络当前状态
     *
     * @return
     */
    public String getNetworkType() {
        ConnectivityManager connectMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        String networkType = null;
        if (null != info) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "wifi";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA) {
                    networkType = "CDMA";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE) {
                    networkType = "EDGE";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0) {
                    networkType = "EVDO0";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A) {
                    networkType = "EVDOA";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS) {
                    networkType = "GPRS";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA) {
                    networkType = "HSDPA";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPA) {
                    networkType = "HSPA";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA) {
                    networkType = "HSUPA";
                } else if (info.getSubtype() == TelephonyManager.NETWORK_TYPE_UMTS) {
                    networkType = "UMTS";
                } else {
                    networkType = "unknown";
                }
            } else {
                networkType = "unknown";
            }
        }
        return returnStr(networkType);
    }

    /**
     * 当前ip地址
     *
     * @return
     */
    public String getlocalip() {
//        String ip = null;
//        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        if (ipAddress != 0) {
//            ip = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
//        }
//        return returnStr(ip);
        return null;
    }

    /**
     * mac地址
     *
     * @return
     */
    public String getMac() {
//        WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//        return manager.getConnectionInfo().getMacAddress();
        return null;
    }

    /**
     * 应用版本
     *
     * @return
     */
    public String getApkVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 应用版本  VersionCode
     *
     * @return
     */
    public int getApkVersionCode() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * @return 系统版本
     */
    public String getOSVersion() {

        if (null == systemVersion) {
            systemVersion = android.os.Build.VERSION.RELEASE;
        }
        return returnStr(systemVersion);

    }

    public String getPackageName() {
        return mContext.getPackageName();
    }

    /**
     * 手机型号
     *
     * @return
     */
    public String getPhoneModel() {
        if (null == model) {
            model = android.os.Build.MODEL;
        }
        return returnStr(model);
    }

    private boolean isNull(String str) {
        return null == str || "".equals(str);
    }

    public String getAndroidID() {
        return android.provider.Settings.Secure.getString(mContext.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public String getSmiID() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }

    public String getAppClientTag() {
        String appClientTag = getMac();
        if (isNull(appClientTag)) {//
            appClientTag = getSmiID();
            if (isNull(appClientTag)) {//
                appClientTag = getIMEI();
                if (isNull(appClientTag)) {//
                    appClientTag = getAndroidID();
                    if (isNull(appClientTag)) {//
                        Random random = new Random(System.currentTimeMillis());
                        appClientTag = "RANDOM_" + Long.toString(random.nextLong());
                    } else {
                        appClientTag = "AID_" + appClientTag;
                    }
                } else {
                    appClientTag = "IMEI_" + appClientTag;
                }
            } else {
                appClientTag = "SMI_" + appClientTag;
            }
        } else {
            appClientTag = "MAC_" + appClientTag;
        }
        return appClientTag;
    }
}
