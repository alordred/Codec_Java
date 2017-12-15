package mipesc.video.streaming.exoplayerhlsextension;


import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;

/**
 * Created by milospesic on 23.07.17.
 *
 * RadioHttpDataSourceFactory - wrapper around HttpDataSource.BaseFactory
 * that provides possibility to inject creation of RadioHttpDataSource when a
 * Data source is requested during ExoPlayer creation.
 *
 */
public class RadioHttpDataSourceFactory extends HttpDataSource.BaseFactory {

    private final String userAgent;
    private final TransferListener<? super DataSource> listener;
    private RadioHttpDataSourceListener radioListener;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;
    private final boolean allowCrossProtocolRedirects;

    /**
     * Constructs RadioHttpDataSourceFactory
     * @param userAgent - userAgent string to be used by instances to be created by the factory.
     */
    public RadioHttpDataSourceFactory(String userAgent) {
        this(userAgent, null, null);
    }


    /**
     * Constructs RadioHttpDataSourceFactory
     * @param userAgent - userAgent string to be used by instances to be created by the factory.
     * @param listener - TransferListener to be used by instances to be created by the factory.
     * @param connectTimeoutMillis - connection timeout in ms
     * @param readTimeoutMillis - read timeout in ms
     * @param allowCrossProtocolRedirects
     * @param radioListener - RadioHttpSourceDataListener instance to be used by instances to be
     *                      created by the factory.
     */
    public RadioHttpDataSourceFactory(String userAgent,
                                      TransferListener<? super DataSource> listener, int connectTimeoutMillis,
                                      int readTimeoutMillis, boolean allowCrossProtocolRedirects, RadioHttpDataSourceListener radioListener) {
        this.userAgent = userAgent;
        this.listener = listener;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.allowCrossProtocolRedirects = allowCrossProtocolRedirects;
        this.radioListener = radioListener;
    }

    /**
     * Constructs RadioHttpDataSourceFactory
     * @param userAgent -  userAgent string to be used by instances to be created by the factory.
     * @param listener - TransferListener to be used by instances to be created by the factory.
     * @param radioListener - RadioHttpSourceDataListener instance to be used by instances to be
     *                      created by the factory.
     */
    public RadioHttpDataSourceFactory(String userAgent, TransferListener<? super DataSource> listener, RadioHttpDataSourceListener radioListener) {
        this(userAgent, listener, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, false, radioListener);
    }

    @Override
    protected DefaultHttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties defaultRequestProperties) {
        return new RadioHttpDataSource(userAgent, null, listener, connectTimeoutMillis,
                readTimeoutMillis, allowCrossProtocolRedirects, defaultRequestProperties, radioListener);
    }

}
