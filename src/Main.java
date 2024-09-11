import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int x = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int y = new Scanner(System.in).nextInt();
        int sum = x + y;
        int diff = x - y;
        int product = x * y;
        double quotient = (double) x / y;
        System.out.println("Сумма: " + sum);
        System.out.println("Разность: " + diff);
        System.out.println("Произведение: " + product);
        System.out.println("Частное: " + quotient);
    }
}