package io.luna.analytics;

import java.util.Collection;

interface LogStorage {
    void write(LogEntry entry) throws Exception;

    void writeAll(Collection<LogEntry> entries) throws Exception;

    void close() throws Exception;
}
