package mipesc.video.streaming.exoplayerhlsextension;

import com.google.android.exoplayer2.AL.ALCmd;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Predicate;

import java.util.Arrays;

/**
 * Created by milospesic on 23.07.17.
 */

/**
 * RadioHttpDataSource
 * @brief Extends DefaultHttpSource and provides callbacks related to an http request.
 * The source will notify its listener when an Http transfer starts, ends and will also
 * notify about bytes being transferred. The source could be used as a standalone component
 * for http requests and could be integrated into ExoPlayer instance as DataSource.
 */
public class RadioHttpDataSource extends DefaultHttpDataSource {
    private static final String TAG = "RadioHttpDataSource";

    private RadioHttpDataSourceListener listener;
    private DataSpec dataSpec;

    RadioHttpDataSource(String userAgent, Predicate<String> contentTypePredicate,
                        TransferListener<? super DefaultHttpDataSource> listener, int connectTimeoutMillis,
                        int readTimeoutMillis, boolean allowCrossProtocolRedirects,
                        RequestProperties defaultRequestProperties, RadioHttpDataSourceListener radioListener) {
        super(userAgent, contentTypePredicate, listener, connectTimeoutMillis, readTimeoutMillis, allowCrossProtocolRedirects, defaultRequestProperties);
        this.listener = radioListener;
    }

    public RadioHttpDataSource(String userAgent, Predicate<String> contentTypePredicate, RadioHttpDataSourceListener radioListener) {
        super(userAgent, contentTypePredicate);
        this.listener = radioListener;
    }

    /**
     * Opens the source to read the specified data. On opening invokes callback to
     * notify RadioHttpDataSourceListener instance about upcoming http request.
     * @param dataSpec - DataSpec instance containing uri and request parameters.
     * @return The number of bytes that can be read from the opened source. For unbounded requests
     *     (i.e. requests where {@link DataSpec#length} equals {@link C#LENGTH_UNSET}) this value
     *     is the resolved length of the request, or {@link C#LENGTH_UNSET} if the length is still
     *     unresolved. For all other requests, the value returned will be equal to the request's
     *     {@link DataSpec#length}.
     * @throws HttpDataSourceException
     */
    @Override
    public long open(DataSpec dataSpec) throws HttpDataSourceException {
//        Log.d(TAG, dataSpec.uri.toString());
        ALCmd.RTTtime = System.nanoTime();
        this.dataSpec = dataSpec;
        if (listener != null) {
            listener.onHttpTransferStart(dataSpec);
        }
        return super.open(dataSpec);
    }

    /**
     * Reads up to {@code length} bytes of data and stores them into {@code buffer}, starting at
     * index {@code offset}.
     * <p>
     * On each read - when number of read bytes is > 0 - RadioHttpDataSourceListener will be notified
     * through onBytesTransferred callback. The callback could be handy for async notification for
     * the source. When the end of the input is encountered, the listener will be notified through
     * onHttpTransferEnd callback
     * </p>
     * @see com.google.android.exoplayer2.upstream.DataSource read()
     */
    @Override
    public int read(byte[] buffer, int offset, int readLength) throws HttpDataSourceException {
        int bytesRead =  super.read(buffer, offset, readLength);
        if (listener != null) {
            if (bytesRead == C.RESULT_END_OF_INPUT) {
                listener.onHttpTransferEnd(dataSpec);
            } else {
                if (bytesRead > 0) {
                    listener.onBytesTransferred(dataSpec, Arrays.copyOfRange(buffer, offset, bytesRead));
                }
            }
        }
        return bytesRead;
    }
}
