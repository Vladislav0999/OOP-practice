package task2.code;
import java.io.*;
import java.util.Scanner;

// Клас, який зберігає представлення числа
class NumberRepresentation implements Serializable {
    private static final long serialVersionUID = 1L;
    int number;
    String binary;
    String octal;
    String hexadecimal;
    public NumberRepresentation(int number) {
        this.number = number;
        this.binary = Integer.toBinaryString(number);
        this.octal = Integer.toOctalString(number);
        this.hexadecimal = Integer.toHexString(number).toUpperCase();
    }
    public void display() {
        System.out.println("Число: " + number);
        System.out.println("Двійкове представлення: " + binary);
        System.out.println("Вісімкове представлення: " + octal);
        System.out.println("Шістнадцяткове представлення: " + hexadecimal);
    }
}
public class Main2Ex4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        NumberRepresentation obj = null;
        System.out.print("Введіть ціле число: ");
        int number = scanner.nextInt();
        // Створення об'єкта
        obj = new NumberRepresentation(number);
        System.out.println("\n[1] Показати представлення");
        System.out.println("[2] Зберегти у файл (серіалізація)");
        System.out.println("[3] Відновити з файлу (десеріалізація)");
        System.out.print("Оберіть опцію: ");
        int option = scanner.nextInt();
        switch (option) {
            case 1:
                obj.display();
                break;
            case 2:
                try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("number.ser"))) {
                    out.writeObject(obj);
                    System.out.println("Об'єкт збережено у файл number.ser");
                } catch (IOException e) {
                    System.out.println("Помилка при збереженні: " + e.getMessage());
                }
                break;
            case 3:
                try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("number.ser"))) {
                    obj = (NumberRepresentation) in.readObject();
                    System.out.println("Об'єкт успішно відновлено з файлу:");
                    obj.display();
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Помилка: " + e.getMessage());
                }
                break;
            default:
                System.out.println("Невірна опція");
        }
        scanner.close();
    }
}
