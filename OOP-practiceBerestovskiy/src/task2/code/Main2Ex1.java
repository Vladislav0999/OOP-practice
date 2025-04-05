package task2.code;
import java.io.Serializable;

// Клас для зберігання параметрів і результатів обчислень
class CalculationData implements Serializable {
    private static final long serialVersionUID = 1L;
    public double input;
    public double result;
    public CalculationData(double input) {
        this.input = input;
    }
    public void setResult(double result) {
        this.result = result;
    }
    public double getResult() {
        return result;
    }
}
// Клас, що агрегує CalculationData для знаходження розв'язку
class Solver {
    private CalculationData data;

    public Solver(CalculationData data) {
        this.data = data;
    }
    public void compute() {
        data.setResult(Math.sqrt(data.input));
    }
}
//Головний клас для виконання пункту 1
public class Main2Ex1 {
    public static void main(String[] args) {
        CalculationData data = new CalculationData(36);
        Solver solver = new Solver(data);
        solver.compute();
        System.out.println("Вхідне значення: " + data.input);
        System.out.println("Результат: " + data.getResult());
    }
}


