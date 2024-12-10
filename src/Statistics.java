import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> sitePages = new HashSet<>();
    private HashMap<String, Integer> osCountMap = new HashMap<>();

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public HashSet<String> getSitePages() {
        return sitePages;
    }

    public void addEntry(LogEntry logEntry) {
        totalTraffic += logEntry.getResponseSize();
        if (minTime.isAfter(logEntry.getTime())) {
            minTime = logEntry.getTime();
        }
        if (maxTime.isBefore(logEntry.getTime())) {
            maxTime = logEntry.getTime();
        }
        if (logEntry.getResponseCode() == 200) {
            sitePages.add(logEntry.getPath());
        }
        String os = logEntry.getAgent().getOperatingSystem();
        if (!osCountMap.containsKey(os)) {
            osCountMap.put(os, 1);
        } else {
            osCountMap.put(os, osCountMap.get(os) + 1);
        }
    }

    public long getTrafficRate() {
        Duration duration = Duration.between(minTime, maxTime);
        long hours = duration.toHours();
        return totalTraffic / hours;
    }

    public HashMap<String, Double> getOsStats() {
        HashMap<String, Double> osStats = new HashMap<>();
        int allOsesCount = 0;
        for (Map.Entry<String, Integer> entry : osCountMap.entrySet()) {
            osStats.put(entry.getKey(), 0d);
            allOsesCount += entry.getValue();
        }
        for (Map.Entry<String, Double> entry : osStats.entrySet()) {
            entry.setValue((double) osCountMap.get(entry.getKey()) / allOsesCount);
        }
        return osStats;
    }
}
