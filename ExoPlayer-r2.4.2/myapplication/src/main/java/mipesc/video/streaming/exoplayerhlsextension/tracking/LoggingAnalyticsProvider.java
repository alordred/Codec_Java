package mipesc.video.streaming.exoplayerhlsextension.tracking;

import android.util.Log;

/**
 * Created by milospesic on 26.07.17.
 *
 * LoggingAnalyticsProvider - implementation of AnalyticsProvider for
 * logging purposes.
 */
public class LoggingAnalyticsProvider implements AnalyticsProvider {

    public static final String TAG = "AnalyticsLogger";

    @Override
    public void reportMetric(Metric metric) {
        Log.d(TAG, "Metric type: " + metric.type + " ,value:" +
                metric.value + " , networks" + metric.networkType + " , battery:" + metric.batteryLevel);
    }

    @Override
    public void reportError(Error error) {
        Log.e(TAG, "Error! Type: " + error.type + " ,message: " + error.errorMessage);
    }
}
