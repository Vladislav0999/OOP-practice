package task2.code;
import java.io.*;
import java.util.Scanner;

// Клас для зберігання параметрів і результатів обчислень.
class CalculationData2 implements Serializable {
    private static final long serialVersionUID = 1L;
    public double input;
    public double result;
    public CalculationData2(double input) {
        this.input = input;
    }
    public void setResult(double result) {
        this.result = result;
    }
    public double getResult() {
        return result;
    }
}
// Клас, що агрегує CalculationData2 для знаходження розв'язку.
class Solvering {
    private CalculationData2 data;

    public Solvering(CalculationData2 data) {
        this.data = data;
    }
    public void compute() {
        data.setResult(Math.sqrt(data.input));
    }
}
/**
 * Клас для тестування обчислень і серіалізації.
 * Використовується для перевірки правильності обчислень; серіалізації та десеріалізації
 */
public class Main2Ex3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введіть число для обчислення квадратного кореня: ");
        double input = scanner.nextDouble();
        CalculationData2 data = new CalculationData2(input);
        Solvering solver = new Solvering(data);
        solver.compute();
        System.out.println("Результат обчислення: " + data.getResult());

        // Тест серіалізації
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("calc.ser"))) {
            oos.writeObject(data);
            System.out.println("Об'єкт серіалізовано.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Тест десеріалізації
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("calc.ser"))) {
            CalculationData2 restored = (CalculationData2) ois.readObject();
            System.out.println("Відновлений результат: " + restored.getResult());
            // Перевірка точності
            if (Math.abs(restored.getResult() - Math.sqrt(input)) < 1e-9) {
                System.out.println("Серіалізація успішна.");
            } else {
                System.err.println("Помилка під час серіалізації.");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        scanner.close();
    }
}
