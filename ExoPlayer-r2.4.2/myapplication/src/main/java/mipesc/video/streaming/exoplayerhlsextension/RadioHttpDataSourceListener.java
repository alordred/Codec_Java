package mipesc.video.streaming.exoplayerhlsextension;

import android.net.Uri;

import com.google.android.exoplayer2.upstream.DataSpec;

/**
 * Created by milospesic on 25.07.17.
 *
 * interface RadioHttpDataSourceListener
 * @brief
 * Provides a way for listening to RadioHttpDataSource callbacks.
 * <p>
 *     All modules interested in receiving async callbacks from RadioHttpDataSource
 *     should implement this interface. It provides a way for listening on the following
 *     events.
 *     <ul>
 *         <li>Http transfer start</li>
 *         <li>Bytes transfer</li>
 *         <li>Http transfer end</li>
 *     </ul>
 * </p>
 */
public interface RadioHttpDataSourceListener {

    /**
     * The callback will be invoked on an http connection opening.
     * @param dataSpec - contains context for the Http request (uri, unique key, etc..)
     *                 @see com.google.android.exoplayer2.upstream.DataSpec
     */
    void onHttpTransferStart(DataSpec dataSpec);

    /**
     * The callback will be invoked upon reading all http response bytes from the source.
     * @param dataSpec - contains context for the Http request (uri, unique key, etc..)
     *                 @see com.google.android.exoplayer2.upstream.DataSpec
     */
    void onHttpTransferEnd(DataSpec dataSpec);

    /**
     * The callback will be invoked whenever bytes from the source are read. It could be used
     * to get http response independently in async way. It may be useful for building custom
     * http cache for example.
     * @param dataSpec - contains context for the Http request (uri, unique key, etc..)
     *                 @see com.google.android.exoplayer2.upstream.DataSpec
     */
    void onBytesTransferred(DataSpec dataSpec, byte[] transferedBytes);


}
