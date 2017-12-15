package mipesc.video.streaming.exoplayerhlsextension;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

/**
 * Created by milospesic on 24.07.17.
 *
 * ExoFactory - class responsible for instantiating ExoPlayer and
 * its components.
 *
 */
public class ExoFactory {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private Context context;
    private String userAgent;

    public ExoFactory(Context context, String userAgent) {
        this.context = context;
        this.userAgent = userAgent;
    }

    /**
     * builds SimpleExoPlayer (with default renderers factory and track selector)
     * @return - SimpleExoPlayer instance
     */
    public SimpleExoPlayer buildExoPlayer() {
        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context, null, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        return ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
    }

    /**
     * Instantiates DataSourceFactory.
     * @param useBandwidthMeter - should a source use default bandwidth meter.
     * @return
     */
    public DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    /**
     * Builds MediaSource depending on the uri extension
     * @param factory - data source factory used for constructing media source
     * @param uri - uri for which media source is instantiated, media type will be inferred based
     *            on uri if overrideExtension is empty string.
     * @param overrideExtension
     * @return
     */
    public MediaSource buildMediaSource(DataSource.Factory factory, Uri uri, String overrideExtension) {
        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(factory), null, null);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(factory), null, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, factory, null, null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, factory, new DefaultExtractorsFactory(),
                        null, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(context, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new RadioHttpDataSourceFactory(userAgent, bandwidthMeter, null);
    }

}
