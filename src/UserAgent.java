import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAgent {
    public static final String FIRST_BRACKETS_REGEX = "\\(.*?\\)";
    public static final String SEARCH_BOT_REGEX = "^.*?(?=/)";
    public static final String FIREFOX_REGEX = "Firefox(?=/)";
    public static final String OPERA_REGEX = "(Chrome(?=/).*?Safari(?=/).*?OPR(?=/)|Opera(?=/))";
    public static final String EDGE_REGEX = "Chrome(?=/).*?Safari(?=/).*?(Edg|Edge)(?=/)";
    public static final String CHROME_REGEX = "\\(KHTML, like Gecko\\).*?Chrome(?=/).*?Safari(?=/)";
    public static final String OPERATING_SYSTEM_REGEX = "(Windows|Linux|Macintosh)";

    private final String userAgentString;
    private final String browser;
    private final String operatingSystem;
    private String firstBrackets;

    UserAgent(String userAgentString) {
        this.userAgentString = userAgentString;
        browser = initBrowser();
        operatingSystem = initOperatingSystem();
    }

    public String getUserAgentString() {
        return userAgentString;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    private String initBrowser() {
        String tmpBrowser = "";
        Pattern firefoxP = Pattern.compile(FIREFOX_REGEX);
        Pattern operaP = Pattern.compile(OPERA_REGEX);
        Pattern edgeP = Pattern.compile(EDGE_REGEX);
        Pattern chromeP = Pattern.compile(CHROME_REGEX);
        Matcher firefoxM = firefoxP.matcher(userAgentString);
        Matcher operaM = operaP.matcher(userAgentString);
        Matcher edgeM = edgeP.matcher(userAgentString);
        Matcher chromeM = chromeP.matcher(userAgentString);
        if (firefoxM.find()) {
            tmpBrowser = "Firefox";
        } else if (operaM.find()) {
            tmpBrowser = "Opera";
        } else if (edgeM.find()) {
            tmpBrowser = "Edge";
        } else if (chromeM.find()) {
            tmpBrowser = "Chrome";
        } else {
            tmpBrowser = "Other";
        }
        return tmpBrowser;
    }

    private String initOperatingSystem() {
        String tmpOperatingSystem;
        Pattern pattern = Pattern.compile(OPERATING_SYSTEM_REGEX);
        Matcher matcher = pattern.matcher(userAgentString);
        if (matcher.find()) {
            tmpOperatingSystem = matcher.group(0);
        } else {
            tmpOperatingSystem = "Other";
        }
        return tmpOperatingSystem;
    }

    private void setFirstBrackets() {
        Pattern pattern = Pattern.compile(FIRST_BRACKETS_REGEX);
        Matcher matcher = pattern.matcher(userAgentString);
        if (matcher.find()) {
            firstBrackets = matcher.group(0);
        }
    }

    String getSearchBot() {
        setFirstBrackets();
        String searchBot = "";
        if (firstBrackets == null) return searchBot;
        String[] parts = firstBrackets.split(";");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        if (parts.length >= 2) {
            String fragment = parts[1];
            Pattern pattern = Pattern.compile(SEARCH_BOT_REGEX);
            Matcher matcher = pattern.matcher(fragment);
            if (matcher.find()) {
                searchBot = matcher.group(0);
            }
        }
        return searchBot;
    }

    @Override
    public String toString() {
        return userAgentString;
    }
}
