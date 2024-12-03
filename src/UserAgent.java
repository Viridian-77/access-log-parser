import java.util.regex.Matcher;
import java.util.regex.Pattern;

class UserAgent {
    static String firstBracketsRegex = "\\(.*?\\)";
    static String searchBotRegex = "^.*?(?=/)";
    String line;
    String firstBrackets;

    UserAgent(String line) {
        this.line = line;
    }

    private void setFirstBrackets() {
        Pattern pattern = Pattern.compile(firstBracketsRegex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            firstBrackets = matcher.group(0);
        };
    }

    String getSearchBot() {
        setFirstBrackets();
        String searchBot = "";
        if (firstBrackets == null) return searchBot;
        String[] parts = firstBrackets.split(";");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        if (parts.length >=2) {
            String fragment = parts[1];
            Pattern pattern = Pattern.compile(searchBotRegex);
            Matcher matcher = pattern.matcher(fragment);
            if (matcher.find()) {
                searchBot = matcher.group(0);
            };
        }
        return searchBot;
    }
}
