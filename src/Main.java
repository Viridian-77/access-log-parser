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
                getBrowserAndOsStats(path);
                getVariousStats(path);
            } catch (IOException | InappropriateLineLengthException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void getVariousStats(String path) throws IOException {
        Statistics statistics = new Statistics();
        try (FileReader fileReader = new FileReader(path);
             BufferedReader reader =
                     new BufferedReader(fileReader);) {
            String line;
            while ((line = reader.readLine()) != null) {
                statistics.addEntry(new LogEntry(line));
            }
        }
        System.out.println("Траффика в час: " + statistics.getTrafficRate() + " байт");
        System.out.println("Доли ОС: " + statistics.getOsStats());
        System.out.println("Доли браузеров: " + statistics.getBrowserStats());
        System.out.println("Среднее количество посещений сайта за час: " + statistics.getAverageVisitsPerHour());
        System.out.println("Среднее количество ошибочных запросов в час: " + statistics.getAverageErrorsPerHour());
        System.out.println("Средняя посещаемость одним пользователем: " + statistics.getAverageVisitsPerUser());
    }

    public static void getBrowserAndOsStats(String path) throws IOException {
        int edgeCount = 0, firefoxCount = 0, chromeCount = 0, operaCount = 0, otherCount = 0;
        int windowsCount = 0, macOSCount = 0, linuxCount = 0;
        try (FileReader fileReader = new FileReader(path);
             BufferedReader reader =
                     new BufferedReader(fileReader);) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry logEntry = new LogEntry(line);
                switch (logEntry.getAgent().getBrowser().toLowerCase()) {
                    case "edge":
                        edgeCount++;
                        break;
                    case "firefox":
                        firefoxCount++;
                        break;
                    case "chrome":
                        chromeCount++;
                        break;
                    case "opera":
                        operaCount++;
                        break;
                    default:
                        otherCount++;
                        break;
                }
                switch (logEntry.getAgent().getOperatingSystem().toLowerCase()) {
                    case "windows":
                        windowsCount++;
                        break;
                    case "macintosh":
                        macOSCount++;
                        break;
                    case "linux":
                        linuxCount++;
                        break;
                }
            }
            System.out.printf("Edge: %d, Firefox: %d, Chrome: %d, Opera: %d, Other: %d\n", edgeCount, firefoxCount, chromeCount, operaCount, otherCount);
            System.out.printf("Windows: %d, macOS: %d, Linux: %d\n", windowsCount, macOSCount, linuxCount);
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
        printSearchBotStats(countLine, countGooglebot, countYandexBot);
    }

    private static String getSearchBot(String line) {
        LogEntry parsedLine = new LogEntry(line);
        return parsedLine.getAgent().getSearchBot();
    }

    private static void printSearchBotStats(int countLine, int countGooglebot, int countYandexBot) {
        double onePercent = (countLine / 100.0);
        double googlePercent = countGooglebot / onePercent;
        double yandexPercent = countYandexBot / onePercent;
        System.out.printf("Запросов от YandexBot: %d, что составляет %f процентов относительно общего числа сделанных запросов.\n", countYandexBot, yandexPercent);
        System.out.printf("Запросов от Googlebot: %d, что составляет %f процентов относительно общего числа сделанных запросов.\n", countGooglebot, googlePercent);
    }
}
