package io.luna.analytics;

import java.text.SimpleDateFormat;
import java.util.Map;

class LogFormatter {
    final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String format(LogEntry entry) {
        StringBuilder sb = new StringBuilder();

        // Date
        sb.append("[").append(dateFormat.format(entry.timestamp)).append("] ");

        // Event name
        sb.append("eventName=").append(escapeValue(entry.eventName));

        // Event data
        for (Map.Entry<String, Object> field : entry.eventData.entrySet()) {

            sb.append(", ").append(field.getKey()).append("=")
                    .append(escapeValue(field.getValue()));
        }
        sb.append("\n");
        return sb.toString();
    }

    private String escapeValue(Object value) {
        if (value == null) {
            return "null";
        }
        String string = value.toString();
        if (string.contains(",") || string.contains("=") || string.contains("\"")) {
            return "\"" + string.replace("\"", "\\\"") + "\"";
        }
        return string;
    }
}
