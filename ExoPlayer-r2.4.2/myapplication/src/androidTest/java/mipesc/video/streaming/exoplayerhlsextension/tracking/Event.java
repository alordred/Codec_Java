package mipesc.video.streaming.exoplayerhlsextension.tracking;

import java.util.Date;

/**
 * Created by milospesic on 26.07.17.
 */
public class Event {

    /**
     * EventType - enum defining all event types player is advertising.
     */
    public enum EventType {
        /**
         * Playback requested.
         */
        PLAYBACK_INIT,
        /**
         * Playback started. It can happen after it's requested or after re-buffering
         */
        PLAYBACK_STARTED,
        /**
         * Buffering started. Will happen initially and whenever player enters buffering state.
         */
        BUFFERING_STARTED
    }

    private Date timestamp;

    private EventType type;

    /**
     * Constructs event with the present timestamp.
     * @param type - type of the event
     */
    public Event(EventType type) {
        this(type, new Date());
    }

    public Event(EventType type, Date date) {
        this.type = type;
        this.timestamp = date;
    }

    /**
     * Returns event type.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Returns event time.
     */
    public Date getTimestamp() {
        return timestamp;
    }

}
