import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    public static final String IP_ADDR_REGEX = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}";
    public static final String TWO_OMITTED_COMPONENTS_REGEX = "^.*?(?=\\[)";
    public static final String TIME_REGEX = "\\[.*?\\]";
    public static final String METHOD_REGEX = "[A-Z]{3,7}(?= )";
    public static final String PATH_REGEX = "/.*?\\\"(?= )";
    public static final String RESPONSE_CODE_REGEX = "\\d{3}(?= )";
    public static final String RESPONSE_SIZE_REGEX = "\\d\\d*?(?= )";
    public static final String REFERER_REGEX = "\\\".*?\\\"(?= )";
    public static final String USER_AGENT_REGEX = "\\\".*?\\\"$";

    private String tmpLine;
    private final String ipAddr;
    private final LocalDateTime time;
    private final HttpMethod method;
    private final String path;
    private final int responseCode;
    private final int responseSize;
    private final String referer;
    private final UserAgent agent;

    LogEntry(String line) {
        tmpLine = line;
        ipAddr = initIp();
        deleteTwoOmittedComponents();
        time = initTime();
        method = initMethod();
        path = initPath();
        responseCode = initResponseCode();
        responseSize = initResponseSize();
        referer = initReferer();
        agent = initUserAgent();
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getAgent() {
        return agent;
    }

    private String initIp() {
        String tmpIdAddr = "";
        Pattern pattern = Pattern.compile(IP_ADDR_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            tmpIdAddr = matcher.group(0);
            tmpLine = tmpLine.substring(tmpIdAddr.length()).trim();
        }
        return tmpIdAddr;
    }

    private void deleteTwoOmittedComponents() {
        String twoOmittedComponents = "";
        Pattern pattern = Pattern.compile(TWO_OMITTED_COMPONENTS_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            twoOmittedComponents = matcher.group(0).trim();
        }
        tmpLine = tmpLine.substring(twoOmittedComponents.length()).trim();
    }

    private LocalDateTime initTime() {
        String timeString = "";
        Pattern pattern = Pattern.compile(TIME_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            timeString = matcher.group(0);
            tmpLine = tmpLine.substring(timeString.length()).trim();
            timeString = timeString.substring(1, timeString.length() - 1);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss ZZZ", new Locale("en", "US"));
        ZoneId rusZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime zonedTime = ZonedDateTime.parse(timeString, formatter);
        ZonedDateTime rusZoned = zonedTime.withZoneSameInstant(rusZone);
        LocalDateTime rusLocal = rusZoned.toLocalDateTime();
        return rusLocal;
    }

    private HttpMethod initMethod() {
        String methodString = "";
        Pattern pattern = Pattern.compile(METHOD_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            methodString = matcher.group(0);
            tmpLine = tmpLine.substring(methodString.length() + 1).trim();
        }
        return HttpMethod.valueOf(methodString);
    }

    private String initPath() {
        String tmpPath = "";
        Pattern pattern = Pattern.compile(PATH_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            tmpPath = matcher.group(0);
            tmpLine = tmpLine.substring(tmpPath.length()).trim();
        }
        return tmpPath.substring(0, tmpPath.length() - 1);
    }

    private int initResponseCode() {
        String responseCodeString = "";
        Pattern pattern = Pattern.compile(RESPONSE_CODE_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            responseCodeString = matcher.group(0);
            tmpLine = tmpLine.substring(responseCodeString.length()).trim();
        }
        return Integer.parseInt(responseCodeString);
    }

    private int initResponseSize() {
        String responseSizeString = "";
        Pattern pattern = Pattern.compile(RESPONSE_SIZE_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            responseSizeString = matcher.group(0);
            tmpLine = tmpLine.substring(responseSizeString.length()).trim();
        }
        return Integer.parseInt(responseSizeString);
    }

    private String initReferer() {
        String tmpReferer = "";
        Pattern pattern = Pattern.compile(REFERER_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            tmpReferer = matcher.group(0);
            tmpLine = tmpLine.substring(tmpReferer.length()).trim();
        }
        tmpReferer = tmpReferer.substring(1, tmpReferer.length() - 1);
        return tmpReferer;
    }

    private UserAgent initUserAgent() {
        String userAgentString = "";
        Pattern pattern = Pattern.compile(USER_AGENT_REGEX);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            userAgentString = matcher.group(0);
            tmpLine = tmpLine.substring(userAgentString.length()).trim();
        }
        userAgentString = userAgentString.substring(1, userAgentString.length() - 1);
        return new UserAgent(userAgentString);
    }
}
