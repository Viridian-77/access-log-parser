import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AccessLogLine {
    static String ipRegex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}";
    static String twoOmittedComponentsRegex = "^.*?(?=\\[)";
    static String dateTimeRegex = "\\[.*?\\]";
    static String requestRegex = "\\\"[A-Z]{3}.*?\\\"(?= )";
    static String responseCodeRegex = "\\d{3}(?= )";
    static String byteSizeRegex = "\\d\\d*?(?= )";
    static String refererRegex = "\\\".*?\\\"(?= )";
    static String userAgentRegex = "\\\".*?\\\"$";

    String tmpLine = "";
    String ip = "";
    String twoOmittedComponents = "";
    String dateTime = "";
    String request = "";
    String responseCode = "";
    String byteSize = "";
    String referer = "";
    String userAgent = "";

    AccessLogLine(String tmpLine) {
        this.tmpLine = tmpLine;
    }

    void initialize() {
        setIp();
        setTwoOmittedComponentsRegex();
        setDateTime();
        setRequest();
        setResponseCode();
        setByteSize();
        setReferer();
        setUserAgent();
    }

    private void setIp() {
        Pattern pattern = Pattern.compile(ipRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            ip = matcher.group(0);
        };
        tmpLine = tmpLine.substring(ip.length()).trim();
    }

    private void setTwoOmittedComponentsRegex() {
        Pattern pattern = Pattern.compile(twoOmittedComponentsRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            twoOmittedComponents = matcher.group(0).trim();
        };
        tmpLine = tmpLine.substring(twoOmittedComponents.length()).trim();
    }

    private void setDateTime() {
        Pattern pattern = Pattern.compile(dateTimeRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            dateTime = matcher.group(0);
        };
        tmpLine = tmpLine.substring(dateTime.length()).trim();
    }

    private void setRequest() {
        Pattern pattern = Pattern.compile(requestRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            request = matcher.group(0);
        };
        tmpLine = tmpLine.substring(request.length()).trim();
    }

    private void setResponseCode() {
        Pattern pattern = Pattern.compile(responseCodeRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            responseCode = matcher.group(0);
        };
        tmpLine = tmpLine.substring(responseCode.length()).trim();
    }

    private void setByteSize() {
        Pattern pattern = Pattern.compile(byteSizeRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            byteSize = matcher.group(0);
        };
        tmpLine = tmpLine.substring(byteSize.length()).trim();
    }

    private void setReferer() {
        Pattern pattern = Pattern.compile(refererRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            referer = matcher.group(0);
        };
        tmpLine = tmpLine.substring(referer.length()).trim();
    }

    private void setUserAgent() {
        Pattern pattern = Pattern.compile(userAgentRegex);
        Matcher matcher = pattern.matcher(tmpLine);
        if (matcher.find()) {
            userAgent = matcher.group(0);
        };
        tmpLine = tmpLine.substring(userAgent.length()).trim();
    }
}
