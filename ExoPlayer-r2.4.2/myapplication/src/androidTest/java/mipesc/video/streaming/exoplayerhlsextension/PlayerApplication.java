package mipesc.video.streaming.exoplayerhlsextension;

import android.app.Application;
import android.util.Log;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by milospesic on 23.07.17.
 */
public class PlayerApplication extends Application {

    @Override
    public void onCreate() {
        Log.d("PlayerApplication", "Creating the app");
        super.onCreate();
    }

}
