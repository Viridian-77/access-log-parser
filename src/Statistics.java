import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private int nonBotVisitsCnt = 0;
    private int responseErrorsCnt = 0;
    private final HashSet<String> sitePages = new HashSet<>();
    private final HashSet<String> nonexistentSitePages = new HashSet<>();
    private final HashMap<String, Integer> osCountMap = new HashMap<>();
    private final HashMap<String, Integer> browserCountMap = new HashMap<>();
    private final HashMap<Integer, Integer> visitsPerEachSecondMap = new HashMap<>();
    private final HashMap<String, Integer> uniqueNonBotVisitsMap = new HashMap<>();
    private final HashSet<String> refererDomains = new HashSet<>();

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

    public HashSet<String> getRefererDomains() {
        return refererDomains;
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
        addIfNotBot(logEntry);
        if (logEntry.getResponseCode() / 100 == 4 || logEntry.getResponseCode() / 100 == 5) {
            responseErrorsCnt++;
        }
        String domain = getRefererDomain(logEntry);
        if (!domain.isEmpty()) {
            refererDomains.add(getRefererDomain(logEntry));
        }
    }

    public long getTrafficRate() {
        Duration duration = Duration.between(minTime, maxTime);
        long hours = duration.toHours();
        return totalTraffic / hours;
    }

    public Map<String, Double> getOsStats() {
        return calculateStats(osCountMap);
    }

    public Map<String, Double> getBrowserStats() {
        return calculateStats(browserCountMap);
    }

    private Map<String, Double> calculateStats(Map<String, Integer> countMap) {
        int count = countMap.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
        return countMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        ent -> count == 0 ? 0d : (double) ent.getValue() / count
                ));
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
        return nonBotVisitsCnt / uniqueNonBotVisitsMap.keySet().size();
    }

    public int getPeakVisitsPerSecond() {
        return visitsPerEachSecondMap.values()
                .stream()
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    public int getMaxVisitsByUser() {
        return uniqueNonBotVisitsMap.values()
                .stream()
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    private void addIfNotBot(LogEntry logEntry) {
        if (!logEntry.getAgent().isBot()) {
            nonBotVisitsCnt++;
            int currentSecond = (int) logEntry.getTime().atZone(ZoneId.systemDefault()).toEpochSecond();
            if (!visitsPerEachSecondMap.containsKey(currentSecond)) {
                visitsPerEachSecondMap.put(currentSecond, 1);
            } else {
                visitsPerEachSecondMap.put(currentSecond, visitsPerEachSecondMap.get(currentSecond) + 1);
            }
            String ip = logEntry.getIpAddr();
            if (!uniqueNonBotVisitsMap.containsKey(ip)) {
                uniqueNonBotVisitsMap.put(ip, 1);
            } else {
                uniqueNonBotVisitsMap.put(ip, uniqueNonBotVisitsMap.get(ip) + 1);
            }
        }
    }

    private String getRefererDomain(LogEntry logEntry) {
        String tmp = logEntry.getReferer();
        String domain = "";
        Matcher matchHttp = Pattern.compile("http(://|s://|%3A%2F%2F|s%3A%2F%2F)").matcher(tmp);
        if (matchHttp.find()) {
            tmp = tmp.substring(matchHttp.group(0).length());
        }
        Matcher matchWww = Pattern.compile("www.").matcher(tmp);
        if (matchWww.find()) {
            tmp = tmp.substring(matchWww.group(0).length());
        }
        Matcher matchDomain = Pattern.compile("^.*?\\..*?(?=[/\"%&?:=#])").matcher(tmp);
        if (matchDomain.find()) {
            domain = matchDomain.group(0);
        }
        return domain;
    }
}
