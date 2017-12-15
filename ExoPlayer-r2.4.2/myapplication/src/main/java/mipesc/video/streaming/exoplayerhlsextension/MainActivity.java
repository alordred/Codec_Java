package mipesc.video.streaming.exoplayerhlsextension;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;

import java.io.PrintWriter;
import java.io.StringWriter;

import mipesc.video.streaming.exoplayerhlsextension.tracking.AnalyticsProvider;
import mipesc.video.streaming.exoplayerhlsextension.tracking.Error;
import mipesc.video.streaming.exoplayerhlsextension.tracking.Event;
import mipesc.video.streaming.exoplayerhlsextension.tracking.EventCollector;
import mipesc.video.streaming.exoplayerhlsextension.tracking.LoggingAnalyticsProvider;
import mipesc.video.streaming.exoplayerhlsextension.utils.Utils;

public class MainActivity extends AppCompatActivity implements OnClickListener, PlaybackControlView.VisibilityListener, ExoPlayer.EventListener {

    private static final String USER_AGENT = "ExoPlayerHlsExtension";

    private SimpleExoPlayer player;
    private ExoFactory exoFactory;

    private SimpleExoPlayerView simpleExoPlayerView;
    private LinearLayout debugRootView;

    private EventCollector eventCollector;
    private AnalyticsProvider analyticsProvider;

    private int resumeWindow;
    private long resumePosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "Creating main activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clearResumePosition();
        exoFactory = new ExoFactory(this, USER_AGENT);
        analyticsProvider = new LoggingAnalyticsProvider();
        eventCollector = new EventCollector(this, analyticsProvider);
        View rootView = findViewById(R.id.root);
        rootView.setOnClickListener(this);
        debugRootView = (LinearLayout) findViewById(R.id.controls_root);
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.setControllerVisibilityListener(this);
        simpleExoPlayerView.requestFocus();
    }

    private void initializePlayer() {
        if (player == null) {
            eventCollector.signal(new Event(Event.EventType.PLAYBACK_INIT));
            player = exoFactory.buildExoPlayer();
            player.addListener(this);
            simpleExoPlayerView.setPlayer(player);
            player.setPlayWhenReady(true);
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                player.seekTo(resumeWindow, resumePosition);
            }
            Uri trackUri = Uri.parse("http://10.11.12.100:8080/hls/NFS_N/index.m3u8");
//            Uri trackUri = Uri.parse("http://10.11.12.100:8080/hls/vk_OneWay/index.m3u8");
            int type = Util.inferContentType(trackUri);
            MediaSource mediaSource = exoFactory.buildMediaSource(exoFactory.buildDataSourceFactory(true), trackUri, "");
            player.prepare(mediaSource, !haveResumePosition, false);
        }
    }


    private void releasePlayer() {
        if (player != null) {
            updateResumePosition();
            player.release();
            player = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        simpleExoPlayerView.showController();
        return super.dispatchKeyEvent(event) || simpleExoPlayerView.dispatchMediaKeyEvent(event);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onVisibilityChange(int visibility) {
        debugRootView.setVisibility(visibility);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && player.getPlayWhenReady()) {
            eventCollector.signal(new Event(Event.EventType.PLAYBACK_STARTED));
        }
        if (playbackState == ExoPlayer.STATE_BUFFERING) {
            eventCollector.signal(new Event(Event.EventType.BUFFERING_STARTED));
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Error err = new Error();
        err.type = error.type;
        err.errorMessage = error.getMessage();
        StringWriter sw = new StringWriter();
        error.printStackTrace(new PrintWriter(sw));
        err.debugInfo = sw.toString();
        err.networkType = Utils.getNetworkType(this);
        err.bateryLevel = Utils.getBatteryLevel(this);
        err.screenOrientaion = Utils.getScreenOrientation(this);
        analyticsProvider.reportError(err);
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    private void updateResumePosition() {
        resumeWindow = player.getCurrentWindowIndex();
        resumePosition = player.isCurrentWindowSeekable() ? Math.max(0, player.getCurrentPosition())
                : C.TIME_UNSET;
    }

    private void clearResumePosition() {
        resumeWindow = C.INDEX_UNSET;
        resumePosition = C.TIME_UNSET;
    }
}
