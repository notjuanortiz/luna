package io.luna.analytics;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

class FileLogStorage implements LogStorage {
    final Path filePath;
    private final LogFormatter formatter;

    FileLogStorage(String filePath) {
        this.filePath = Paths.get(filePath);
        this.formatter = new LogFormatter();
    }

    /**
     * @param entry
     * @throws Exception
     */
    @Override
    public void write(LogEntry entry) throws Exception {
        Files.createDirectories(filePath.getParent());
        String newEntry = formatter.format(entry);
        String existingContent = Files.exists(filePath) ? Files.readString(filePath) : "";
        String newContent = existingContent + newEntry;
        Files.writeString(filePath, newContent, StandardCharsets.UTF_8);
    }

    public void writeAll(Collection<LogEntry> entries) throws Exception {
        Files.createDirectories(filePath.getParent());

        // Concat all entries to a single string
        StringBuilder sb = new StringBuilder();
        for (LogEntry entry : entries) {
            sb.append(formatter.format(entry));
        }

        String existingContent = Files.exists(filePath) ? Files.readString(filePath) : "";
        String newContent = existingContent + sb;
        Files.writeString(filePath, newContent, StandardCharsets.UTF_8);
    }

    /**
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

    }
}
