package task2.code;
import java.io.*;
import java.util.Scanner;

/**
 * Клас демонструє збереження/відновлення стану об'єкта з використанням серіалізації.
 * Містить transient поле, яке не буде збережене.
 */
class SerializationDemo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String name;
    public transient String temporaryNote;
    public SerializationDemo(String name, String note) {
        this.name = name;
        this.temporaryNote = note;
    }
}

// Головний клас для демонстрації серіалізації
public class Main2Ex2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть ім'я: ");
        String name = scanner.nextLine();
        System.out.print("Введіть нотатку (тимчасову): ");
        String note = scanner.nextLine();
        SerializationDemo obj = new SerializationDemo(name, note);
        // Серіалізація
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("demo.ser"))) {
            oos.writeObject(obj);
            System.out.println("Об'єкт збережено.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Десеріалізація
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("demo.ser"))) {
            SerializationDemo restored = (SerializationDemo) ois.readObject();
            System.out.println("Об'єкт відновлено.");
            System.out.println("Ім'я: " + restored.name);
            System.out.println("Нотатка (transient): " + restored.temporaryNote); // буде null
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}
