package mipesc.video.streaming.exoplayerhlsextension.tracking;

/**
 * Created by milospesic on 26.07.17.
 *
 * Metric - simple class containing info for a playback metric.
 * It may contain useful stats for performance analysis.
 * Class ready for serialization and sending to a telemetry backend.
 */

public class Metric {
    public String type;
    public long value;
    public String networkType;
    public int batteryLevel;
    public String screenOrientation;

}
