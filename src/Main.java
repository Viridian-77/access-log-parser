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
                getFileLinesInfo(path);
            } catch (IOException | InappropriateLineLengthException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void getFileLinesInfo(String path) throws IOException {
        int countLine = 0;
        int minLineLength = Integer.MAX_VALUE;
        int maxLineLength = 0;
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
                if (length < minLineLength) {
                    minLineLength = length;
                }
                if (length > maxLineLength) {
                    maxLineLength = length;
                }
            }
        }
        System.out.println("Общее количество строк в файле: " + countLine);
        System.out.println("Длина самой длинной строки в файле: " + maxLineLength);
        System.out.println("Длина самой короткой строки в файле: " + minLineLength);
    }
}
