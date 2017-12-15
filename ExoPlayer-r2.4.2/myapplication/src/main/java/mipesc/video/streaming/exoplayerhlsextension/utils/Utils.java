package mipesc.video.streaming.exoplayerhlsextension.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by milospesic on 26.07.17.
 */
public class Utils {

    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            return "OFFLINE";
        } else {
            return networkInfo.getTypeName();
        }
    }

    public static int getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50;
        }

        return (int)(((float)level / (float)scale) * 100.0f);
    }

    public static String getScreenOrientation(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                return "LANDSCAPE";
        return "PORTRAIT";
    }

}
