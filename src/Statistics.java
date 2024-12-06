import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getResponseSize();
        if (minTime.isAfter(logEntry.getTime())) {
            minTime = logEntry.getTime();
        }
        if (maxTime.isBefore(logEntry.getTime())) {
            maxTime = logEntry.getTime();
        }
    }

    public long getTrafficRate() {
        Duration duration = Duration.between(minTime, maxTime);
        long hours = duration.toHours();
        return totalTraffic / hours;
    }
}
