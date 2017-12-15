package mipesc.video.streaming.exoplayerhlsextension.tracking;

/**
 * Created by milospesic on 26.07.17.
 */

/**
 * AnalyticsProvider - interface to be implemented by modules
 * for sending metrics the player is advertising. During runtime the player
 * will be emitting metrics containing stat data (i.e playback latency, buffering latency) and
 * depending on the system for sending/storing/analysing data, concrete implementation of
 * this interface may for example serialize these metrics to json or some other format
 * and send them to a telemetry backend.
 */
public interface AnalyticsProvider {
    /**
     * Callback for handling metric object advertised by the player.
     * @param metric - object containing metric name, value, network type, battery level, screen
     */
    void reportMetric(Metric metric);

    /**
     * Callback for handling error object advertised by the player.
     * @param error - object containing error reason, stack trace, etc..
     */
    void reportError(Error error);
}
