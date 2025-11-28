package io.luna.analytics;

import java.util.Date;
import java.util.Map;

class LogEntry {
    final String eventName;
    final Map<String, Object> eventData;
    final Date timestamp;

    LogEntry(String eventName, Map<String, Object> eventData) {
        this.eventName = eventName;
        this.eventData = eventData;
        this.timestamp = new Date();
    }
}
