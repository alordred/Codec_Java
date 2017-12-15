package mipesc.video.streaming.exoplayerhlsextension.tracking;

import android.content.Context;

import java.util.ArrayList;

import mipesc.video.streaming.exoplayerhlsextension.utils.Utils;

/**
 * Created by milospesic on 26.07.17.
 */

/**
 * EventCollector - class responsible for player event collection and their aggregation in metrics.
 * Player will invoke its API for signalling events. @see Event.
 */
public class EventCollector {

    private ArrayList<Event> events;
    private boolean timeToPlaySignalled;
    private Event buffering;
    private Context context;
    private AnalyticsProvider provider;

    private static final String TIME_TO_PLAY = new String("time-to-play");
    private static final String TIME_TO_BUFFER = new String("time-to-buffer");

    /**
     * Constructs EventCollector
     * @param context - android context need to enrich metrics with platform data
     * @param provider - implementation of AnalyticsProvider interface for sending metrics to
     *                 a backend (logging, telemetry...)
     */
    public EventCollector(Context context, AnalyticsProvider provider) {
        events = new ArrayList<>();
        this.context = context;
        this.provider = provider;
    }

    /**
     * signal - method player should use for reporting an event.
     * @param event
     */
    public void signal(Event event) {
        events.add(event);
        if (event.getType() == Event.EventType.PLAYBACK_STARTED) {
            if (!timeToPlaySignalled) {
                Metric timeToPlayMetric = buildTimeToPlayMetric(event);
                if (timeToPlayMetric == null) {
                    return;
                }
                timeToPlaySignalled = true;
                provider.reportMetric(timeToPlayMetric);
            } else if (buffering != null) {
                Metric timeToBufferMetric = buildTimeToBufferMertic(event);
                provider.reportMetric(timeToBufferMetric);
                buffering = null;
            }
        } else if (event.getType() == Event.EventType.BUFFERING_STARTED) {
            if(buffering == null)
                buffering = event;
        }
    }

    private Metric buildTimeToPlayMetric(Event playbackStarted) {
        Metric ttp = null;
        for(Event e: events) {
            if(e.getType().equals(Event.EventType.PLAYBACK_INIT)) {
                ttp = new Metric();
                ttp.type = TIME_TO_PLAY;
                ttp.value = playbackStarted.getTimestamp().getTime() - e.getTimestamp().getTime();
                enrichMetricObject(ttp);
            }
        }
        return ttp;
    }

    private Metric buildTimeToBufferMertic(Event event) {
        Metric ttb = null;
        ttb = new Metric();
        ttb.type = TIME_TO_BUFFER;
        ttb.value = event.getTimestamp().getTime() - buffering.getTimestamp().getTime();
        enrichMetricObject(ttb);
        return ttb;
    }

    private void enrichMetricObject(Metric metric) {
        metric.networkType = Utils.getNetworkType(context);
        metric.batteryLevel = Utils.getBatteryLevel(context);
        metric.screenOrientation = Utils.getScreenOrientation(context);
    }
}
