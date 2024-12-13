import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int nonBotVisitsCnt = 0;
    private int responseErrorsCnt = 0;
    private HashSet<String> sitePages = new HashSet<>();
    private HashSet<String> nonexistentSitePages = new HashSet<>();
    private HashMap<String, Integer> osCountMap = new HashMap<>();
    private HashMap<String, Integer> browserCountMap = new HashMap<>();
    private HashSet<String> uniqueNonBotVisitorIps = new HashSet<>();

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public HashSet<String> getSitePages() {
        return sitePages;
    }

    public HashSet<String> getNonexistentSitePages() {
        return nonexistentSitePages;
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
        } else if (logEntry.getResponseCode() == 404) {
            nonexistentSitePages.add(logEntry.getPath());
        }
        String os = logEntry.getAgent().getOperatingSystem();
        if (!osCountMap.containsKey(os)) {
            osCountMap.put(os, 1);
        } else {
            osCountMap.put(os, osCountMap.get(os) + 1);
        }
        String browser = logEntry.getAgent().getBrowser();
        if (!browserCountMap.containsKey(browser)) {
            browserCountMap.put(browser, 1);
        } else {
            browserCountMap.put(browser, browserCountMap.get(browser) + 1);
        }
        if (!logEntry.getAgent().isBot()) {
            nonBotVisitsCnt++;
            uniqueNonBotVisitorIps.add(logEntry.getIpAddr());
        }
        if (logEntry.getResponseCode() / 100 == 4 || logEntry.getResponseCode() / 100 == 5) {
            responseErrorsCnt++;
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

    public HashMap<String, Double> getBrowserStats() {
        HashMap<String, Double> browserStats = new HashMap<>();
        int allBrowserCount = 0;
        for (Map.Entry<String, Integer> entry : browserCountMap.entrySet()) {
            browserStats.put(entry.getKey(), 0d);
            allBrowserCount += entry.getValue();
        }
        for (Map.Entry<String, Double> entry : browserStats.entrySet()) {
            entry.setValue((double) browserCountMap.get(entry.getKey()) / allBrowserCount);
        }
        return browserStats;
    }

    public int getAverageVisitsPerHour() {
        Duration duration = Duration.between(minTime, maxTime);
        return (int) (nonBotVisitsCnt / duration.toHours());
    }

    public int getAverageErrorsPerHour() {
        Duration duration = Duration.between(minTime, maxTime);
        return (int) (responseErrorsCnt / duration.toHours());
    }

    public int getAverageVisitsPerUser() {
        return nonBotVisitsCnt / uniqueNonBotVisitorIps.size();
    }
}
