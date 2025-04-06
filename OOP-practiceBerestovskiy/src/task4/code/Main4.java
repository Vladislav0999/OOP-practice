package task4.code;
import java.io.*;
import java.util.*;

// Інтерфейс для відображення результатів
interface Displayable {
    void display();
    void display(String align, int width); // Перевантажений метод для таблиці
}
// Абстрактний клас представлення числа
abstract class NumberRepresentation implements Serializable, Displayable {
    protected int number;
    public NumberRepresentation(int number) {
        this.number = number;
    }
    public int getNumber() {
        return number;
    }
    // Перевантажений метод: відображення у вигляді таблиці
    public void display(String align, int width) {
        String value = getFormattedValue();
        String formatted;
        if ("right".equalsIgnoreCase(align)) {
            formatted = String.format("%" + width + "s", value);
        } else {
            formatted = String.format("%-" + width + "s", value);
        }
        System.out.printf("| %-18s | %s |\n", getType(), formatted);
    }
    protected abstract String getFormattedValue(); // Поліморфізм: реалізується в кожному підкласі
    protected abstract String getType();           // Назва типу
}
// Двійкове представлення числа
class BinaryRepresentation extends NumberRepresentation {
    private String binary;
    public BinaryRepresentation(int number) {
        super(number);
        this.binary = Integer.toBinaryString(number);
    }
    @Override
    public void display() {
        System.out.println("Двійкове представлення: " + binary);
    }
    @Override
    protected String getFormattedValue() {
        return binary;
    }
    @Override
    protected String getType() {
        return "Двійкове";
    }
}
// Вісімкове представлення числа
class OctalRepresentation extends NumberRepresentation {
    private String octal;
    public OctalRepresentation(int number) {
        super(number);
        this.octal = Integer.toOctalString(number);
    }
    @Override
    public void display() {
        System.out.println("Вісімкове представлення: " + octal);
    }
    @Override
    protected String getFormattedValue() {
        return octal;
    }
    @Override
    protected String getType() {
        return "Вісімкове";
    }
}
// Шістнадцяткове представлення числа
class HexRepresentation extends NumberRepresentation {
    private String hex;
    public HexRepresentation(int number) {
        super(number);
        this.hex = Integer.toHexString(number).toUpperCase();
    }
    @Override
    public void display() {
        System.out.println("Шістнадцяткове представлення: " + hex);
    }
    @Override
    protected String getFormattedValue() {
        return hex;
    }
    @Override
    protected String getType() {
        return "Шістнадцяткове";
    }
}
// Інтерфейс фабрики
interface NumberFactory {
    NumberRepresentation create(int number);
}
// Фабрика для двійкового представлення
class BinaryFactory implements NumberFactory {
    public NumberRepresentation create(int number) {
        return new BinaryRepresentation(number);
    }
}
// Фабрика для вісімкового представлення
class OctalFactory implements NumberFactory {
    public NumberRepresentation create(int number) {
        return new OctalRepresentation(number);
    }
}
// Фабрика для шістнадцяткового представлення
class HexFactory implements NumberFactory {
    public NumberRepresentation create(int number) {
        return new HexRepresentation(number);
    }
}
// Основний клас
public class Main4 {
    private static final String FILE_NAME = "numbers.ser";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<NumberRepresentation> representations = new ArrayList<>();
        boolean running = true;
        while (running) {
            System.out.println("[1] Ввести число");
            System.out.println("[2] Показати збережені представлення");
            System.out.println("[3] Вивести у вигляді таблиці");
            System.out.println("[4] Зберегти у файл");
            System.out.println("[5] Відновити з файлу");
            System.out.println("[6] Тестування");
            System.out.println("[0] Вийти");
            System.out.print("Ваш вибір: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1 -> {
                    System.out.print("Введіть ціле число: ");
                    int number = scanner.nextInt();
                    // Створення трьох представлень
                    representations.add(new BinaryFactory().create(number));
                    representations.add(new OctalFactory().create(number));
                    representations.add(new HexFactory().create(number));
                    System.out.println("Представлення додано.");
                }
                case 2 -> {
                    if (representations.isEmpty()) {
                        System.out.println("Список порожній.");
                    } else {
                        for (NumberRepresentation r : representations) {
                            System.out.println("Число: " + r.getNumber());
                            r.display(); // Поліморфізм
                            System.out.println("------------");
                        }
                    }
                }
                case 3 -> {
                    System.out.print("Введіть ширину стовпця значення: ");
                    int width = scanner.nextInt();
                    scanner.nextLine(); // зчитати символ нового рядка
                    System.out.print("Вирівнювання (l/r): ");
                    String align = scanner.nextLine();

                    // Заголовок таблиці
                    System.out.println("\n+--------------------+-------------------------+");
                    System.out.printf("| %-18s | %-23s |\n", "Тип представлення", "Значення");
                    System.out.println("+--------------------+-------------------------+");
                    for (NumberRepresentation r : representations) {
                        r.display(align, width); // перевантажений метод
                    }
                    System.out.println("+--------------------+-------------------------+");
                }
                case 4 -> {
                    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                        out.writeObject(representations);
                        System.out.println("Збережено у файл.");
                    } catch (IOException e) {
                        System.out.println("Помилка збереження: " + e.getMessage());
                    }
                }
                case 5 -> {
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                        representations = (List<NumberRepresentation>) in.readObject();
                        System.out.println("Список відновлено з файлу.");
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Помилка відновлення: " + e.getMessage());
                    }
                }
                case 6 -> Tester.runTests(); // запуск тестування
                case 0 -> running = false;
                default -> System.out.println("Невірна опція.");
            }
        }
        scanner.close();
        System.out.println("Програма завершена.");
    }
}
// Клас для тестування функціональності
class Tester {
    public static void runTests() {
        System.out.println("\nТестування:");
        NumberRepresentation bin = new BinaryFactory().create(42);
        NumberRepresentation oct = new OctalFactory().create(42);
        NumberRepresentation hex = new HexFactory().create(42);
        bin.display();
        oct.display();
        hex.display();
        System.out.println("\nТест таблиці:");
        bin.display("right", 10);
        oct.display("left", 10);
        hex.display("right", 10);
    }
}

