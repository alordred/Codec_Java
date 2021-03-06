package mipesc.video.streaming.exoplayerhlsextension;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.exoplayer2.AL.ALCmd;
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

    private int resumeWindow;
    private long resumePosition;

    //测试功能
    public static boolean forward = false;
    //测试功能

    private EventCollector eventCollector;
    private AnalyticsProvider analyticsProvider;
    private boolean mPlayVideoWhenForegrounded;
    private Context m_context;
    //合并Unity用
//    MyExoplayer(Activity m_activity){
//        m_context = m_activity.getApplicationContext();
//        exoFactory = new ExoFactory(m_context, USER_AGENT);
//        analyticsProvider = new LoggingAnalyticsProvider();
//        eventCollector = new EventCollector(m_context, analyticsProvider);
//    }

    //测试用



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ALCmd.CURRENT_MOVE_STATE = ALCmd.MOVE_STATE_FORWARD;
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

        //测试用获取按钮ALCmd.MOVE_STATE_BACK;
        Button button1 = (Button) findViewById(R.id.reverse);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ALCmd.CURRENT_MOVE_STATE = ALCmd.MOVE_STATE_BACK;
//                mipesc.video.streaming.exoplayerhlsextension.ALCmd.CURRENT_MOVE_STATE = mipesc.video.streaming.exoplayerhlsextension.ALCmd.MOVE_STATE_BACK;
//                sendEmptyMessage(MSG_END_OF_SOURCE);
//                handler.sendEmptyMessage(MOVE_STATE_BACK);
            }
        });

        //测试用获取按钮ALCmd.MOVE_STATE_FORWARD;
        Button button2 = (Button) findViewById(R.id.forward);
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ALCmd.CURRENT_MOVE_STATE = ALCmd.MOVE_STATE_FORWARD;
//                mipesc.video.streaming.exoplayerhlsextension.ALCmd.CURRENT_MOVE_STATE = mipesc.video.streaming.exoplayerhlsextension.ALCmd.MOVE_STATE_BACK;
//                sendEmptyMessage(MSG_END_OF_SOURCE);
//                handler.sendEmptyMessage(MOVE_STATE_BACK);
            }
        });

        //测试用获取按钮ALCmd.MOVE_STATE_LOOK_UP;
        Button button3 = (Button) findViewById(R.id.LOOK_UP);
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ALCmd.CURRENT_MOVE_STATE = ALCmd.MOVE_STATE_LOOK_UP;
//                mipesc.video.streaming.exoplayerhlsextension.ALCmd.CURRENT_MOVE_STATE = mipesc.video.streaming.exoplayerhlsextension.ALCmd.MOVE_STATE_BACK;
//                sendEmptyMessage(MSG_END_OF_SOURCE);
//                handler.sendEmptyMessage(MOVE_STATE_BACK);
            }
        });

        //测试用获取按钮ALCmd.MOVE_STATE_LOOK_DOWN;
        Button button4 = (Button) findViewById(R.id.LOOK_DOWN);
        button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ALCmd.CURRENT_MOVE_STATE = ALCmd.MOVE_STATE_LOOK_DOWN;
//                mipesc.video.streaming.exoplayerhlsextension.ALCmd.CURRENT_MOVE_STATE = mipesc.video.streaming.exoplayerhlsextension.ALCmd.MOVE_STATE_BACK;
//                sendEmptyMessage(MSG_END_OF_SOURCE);
//                handler.sendEmptyMessage(MOVE_STATE_BACK);
            }
        });
    }

    //初始化函数
    private void initializePlayer() {
        if (player == null) {
            //做一些必要的操作Exoplayer
            eventCollector.signal(new Event(Event.EventType.PLAYBACK_INIT));
            player = exoFactory.buildExoPlayer();
            player.addListener(this);
            simpleExoPlayerView.setPlayer(player);
            player.setPlayWhenReady(true);
            boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;
            if (haveResumePosition) {
                player.seekTo(resumeWindow, resumePosition);
            }

            //这里写m3u8文件对应的地址，这个地址能直接对应到
//            Uri trackUri = Uri.parse("http://10.213.122.118:8080/hls/NFS_N/index.m3u8");//新买的路由器
//            Uri trackUri = Uri.parse("http://10.213.122.139:8080/hls/NFS_1G/index.m3u8");//新买的路由器
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/furion/index.m3u8");//ubantu
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/vr60H/index.m3u8");//清华校内ip
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/vr40UD/index.m3u8");//清华校内ip
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/vr1m/index.m3u8");//清华校内ip
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/vr5m/index.m3u8");//清华校内ip
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/vr_20DU/index.m3u8");//清华校内ip60000k
            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/NFS_1G/index.m3u8");
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/vr60H/index.m3u8");//清华校内ip60000k
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/60BigChunk/index.m3u8");//清华校内ip60000k
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/vr20L/index.m3u8");//清华校内ip
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/vrPre40M/index.m3u8");//A-全高清，有Prefetch
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/Prefetch.m3u8");//B-动态自适应码率，有Prefetch
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/NoPrefetch.m3u8");//C-动态自适应码率，没有Prefetch
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/vr40M/index.m3u8");//D-全高清，没有Prefetch

//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/B40M/index.m3u8");//A-全高清，有Prefetch
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/B_Prefetch.m3u8");//B-动态自适应码率，有Prefetch
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/N_Prefetch.m3u8");//C-动态自适应码率，没有Prefetch
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/N40M/index.m3u8");//D-全高清，没有Prefetch

//            Uri trackUri = Uri.parse("http://192.168.31.218:8080/hls/Prefetch.m3u8");//Home
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/vr5m/index.m3u8");//清华校内ip
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/vr30m/index.m3u8");//清华校内ipssssssssss
//            Uri trackUri = Uri.parse("http://203.91.121.132:8080/hls/vr40DU/index.m3u8");//清华校内ip
//            Uri trackUri = Uri.parse("http://10.213.122.139:8080/hls/NFS.m3u8");//mac
//            Uri trackUri = Uri.parse("http://192.168.199.218:8080/hls/furion/index.m3u8");//ubantu
//            Uri trackUri = Uri.parse("http://10.213.122.102:8080/hls/NFS_1G/index.m3u8");//ubantu

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


    //以前的功能
//    public void Stop()
//    {
//        // Store off if we were playing so we know if we should start when we're foregrounded again.
//        mPlayVideoWhenForegrounded = player.getPlayWhenReady();
//        // Store off the last position our player was in before we paused it.
//        resumePosition = player.getCurrentPosition();
//        // Pause the player
//        player.setPlayWhenReady(false);
//    }
//    public void Start() {
//        if (player == null) {
//            // init player
//            System.out.println("player == nul Start");
//        }
//        // Seek to the last position of the player.
//        player.seekTo(resumePosition);
//        // Put the player into the last state we were in.
//        player.setPlayWhenReady(mPlayVideoWhenForegrounded);
//    }
}
