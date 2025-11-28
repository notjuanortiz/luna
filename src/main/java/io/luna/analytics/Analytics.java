package io.luna.analytics;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Analytics {
    private final LogStorage storage;
    private final ConcurrentLinkedQueue<LogEntry> writeQueue;
    private static Analytics instance;

    Analytics(LogStorage storage) {
        this.storage = storage;
        this.writeQueue = new ConcurrentLinkedQueue<>();
    }

    public static Analytics instance() {
        if (instance == null) {
            try {
                instance = createFromConfig();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    private static Analytics createFromConfig() throws Exception {
        AnalyticsConfiguration config = new AnalyticsConfiguration();

        LogStorage storage = null;

        // Build from config properties here
        if (config.isFileStorageEnabled()) {
            storage = new FileLogStorage(config.getFilePath());
        } else {
            throw new UnsupportedOperationException("We only support the default solution for now.");
        }

        return new Analytics(storage);
    }

    public void customData(String eventName, Map<String, Object> parameters) {
        LogEntry entry = new LogEntry(eventName, parameters);
        try {
            writeQueue.add(entry);
            System.out.println("New event: " + eventName + " successfully logged to file.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void flush() {
        // Open the log file
        // Dump all entries at once. This stops us from having to open and write to file every log.
        try {
            storage.writeAll(writeQueue);
            System.out.print(writeQueue.size() + " events saved.");
            writeQueue.clear();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
