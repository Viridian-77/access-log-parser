import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int count = 0;
        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();
            if (!fileExists || isDirectory) {
                System.out.println("Файл не существует или указанный путь является путём к папке.");
                continue;
            } else if (fileExists) {
                count++;
                System.out.printf("Путь указан верно. Это файл номер %d%n", count);
            }
            try {
                getUserAgentStats(path);
            } catch (IOException | InappropriateLineLengthException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void getUserAgentStats(String path) throws IOException {
        int countLine = 0;
        int countGooglebot = 0;
        int countYandexBot = 0;
        try (FileReader fileReader = new FileReader(path);
             BufferedReader reader =
                     new BufferedReader(fileReader);) {
            String line;
            while ((line = reader.readLine()) != null) {
                int length = line.length();
                if (length > 1024) {
                    throw new InappropriateLineLengthException("Строка не должна быть длинее 1024 символов");
                }
                countLine++;
                String searchBot = getSearchBot(line);
                switch (searchBot.toLowerCase()) {
                    case "googlebot":
                        countGooglebot++;
                        break;
                    case "yandexbot":
                        countYandexBot++;
                        break;
                }
            }
        }
        System.out.println("Общее количество строк в файле: " + countLine);
        printSearchBotStats(countLine,countGooglebot, countYandexBot);
    }

    private static String getSearchBot(String line) {
        AccessLogLine parsedLine = new AccessLogLine(line);
        parsedLine.initialize();
        UserAgent userAgent = new UserAgent(parsedLine.userAgent);
        return userAgent.getSearchBot();
    }

    private static void printSearchBotStats(int countLine, int countGooglebot, int countYandexBot) {
        double onePercent = (countLine / 100.0);
        double  googlePercent = countGooglebot / onePercent;
        double  yandexPercent = countYandexBot / onePercent;
        System.out.printf("Запросов от YandexBot: %d, что составляет %f процентов относительно общего числа сделанных запросов.\n", countYandexBot, yandexPercent);
        System.out.printf("Запросов от Googlebot: %d, что составляет %f процентов относительно общего числа сделанных запросов.\n", countGooglebot, googlePercent);
    }
}
