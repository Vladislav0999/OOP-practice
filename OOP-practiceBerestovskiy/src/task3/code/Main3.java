package task3.code;
import java.io.*;
import java.util.*;

// Інтерфейс для відображення результатів
interface Displayable {
    void display();
}
// Абстрактний клас представлення числа
abstract class NumberRepresentation implements Serializable, Displayable {
    protected int number;

    public NumberRepresentation(int number) {
        this.number = number;
    }
}
// Конкретні реалізації представлення
class BinaryRepresentation extends NumberRepresentation {
    private String binary;
    public BinaryRepresentation(int number) {
        super(number);
        this.binary = Integer.toBinaryString(number);
    }
    public void display() {
        System.out.println("Двійкове представлення: " + binary);
    }
}
class OctalRepresentation extends NumberRepresentation {
    private String octal;
    public OctalRepresentation(int number) {
        super(number);
        this.octal = Integer.toOctalString(number);
    }
    public void display() {
        System.out.println("Вісімкове представлення: " + octal);
    }
}
class HexRepresentation extends NumberRepresentation {
    private String hex;
    public HexRepresentation(int number) {
        super(number);
        this.hex = Integer.toHexString(number).toUpperCase();
    }
    public void display() {
        System.out.println("Шістнадцяткове представлення: " + hex);
    }
}
// Інтерфейс фабрики
interface NumberFactory {
    NumberRepresentation create(int number);
}
// Конкретні фабрики
class BinaryFactory implements NumberFactory {
    public NumberRepresentation create(int number) {
        return new BinaryRepresentation(number);
    }
}
class OctalFactory implements NumberFactory {
    public NumberRepresentation create(int number) {
        return new OctalRepresentation(number);
    }
}
class HexFactory implements NumberFactory {
    public NumberRepresentation create(int number) {
        return new HexRepresentation(number);
    }
}
// Основний клас
public class Main3 {
    private static final String FILE_NAME = "numbers.ser";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<NumberRepresentation> representations = new ArrayList<>();
        boolean running = true;
        while (running) {
            System.out.println("[1] Ввести число");
            System.out.println("[2] Показати всі збережені представлення");
            System.out.println("[3] Зберегти у файл");
            System.out.println("[4] Відновити з файлу");
            System.out.println("[0] Вийти");
            System.out.print("Оберіть опцію: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1:
                    System.out.print("Введіть ціле число: ");
                    int number = scanner.nextInt();
                    // Створюємо всі три представлення
                    NumberRepresentation binary = new BinaryFactory().create(number);
                    NumberRepresentation octal = new OctalFactory().create(number);
                    NumberRepresentation hex = new HexFactory().create(number);
                    System.out.println("\nЧисло: " + number);
                    binary.display();
                    octal.display();
                    hex.display();
                    representations.add(binary);
                    representations.add(octal);
                    representations.add(hex);
                    break;
                case 2:
                    if (representations.isEmpty()) {
                        System.out.println("Список порожній.");
                    } else {
                        System.out.println("\nЗбережені представлення:");
                        for (NumberRepresentation r : representations) {
                            System.out.println("Число: " + r.number);
                            r.display();
                            System.out.println("------------");
                        }
                    }
                    break;
                case 3:
                    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                        out.writeObject(representations);
                        System.out.println("Список збережено у файл " + FILE_NAME);
                    } catch (IOException e) {
                        System.out.println("Помилка збереження: " + e.getMessage());
                    }
                    break;
                case 4:
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                        representations = (List<NumberRepresentation>) in.readObject();
                        System.out.println("Список успішно відновлено з файлу.");
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Помилка відновлення: " + e.getMessage());
                    }
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Невірна опція");
            }
        }
        scanner.close();
        System.out.println("Програма завершена.");
    }
}
