package mipesc.video.streaming.exoplayerhlsextension.tracking;

/**
 * Created by milospesic on 26.07.17.
 *
 * Error - simple class containing player error details.
 * Class ready for serialization and sending to a telemetry backend.
 */
public class Error {
    public int type;
    public String errorMessage;
    public String debugInfo;
    public String networkType;
    public int bateryLevel;
    public String screenOrientaion;
}
