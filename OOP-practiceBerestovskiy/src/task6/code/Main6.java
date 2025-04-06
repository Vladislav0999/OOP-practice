package task6.code;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

// Інтерфейс для команд
interface Command {
    void execute();
}
// Додати число
class AddNumberCommand implements Command {
    private final int number;

    public AddNumberCommand(int number) {
        this.number = number;
    }
    @Override
    public void execute() {
        NumberRepresentation binary = new BinaryRepresentation(number);
        RepresentationManager.getInstance().addRepresentation(binary);
        CommandHistory.add(this);
    }
}
// Макрокоманда
class MacroCommand implements Command {
    private final List<Command> commands;

    public MacroCommand(List<Command> commands) {
        this.commands = commands;
    }
    @Override
    public void execute() {
        for (Command cmd : commands) {
            cmd.execute();
        }
        CommandHistory.add(this);
    }
    public List<Command> getCommands() {
        return commands;
    }
}
// Історія команд
class CommandHistory {
    private static final Stack<Command> history = new Stack<>();

    public static void add(Command cmd) {
        history.push(cmd);
    }

    public static void execute(Command cmd) {
        cmd.execute();
        add(cmd);
    }
    public static void undoLast() {
        if (!history.isEmpty()) {
            Command last = history.pop();
            if (last instanceof AddNumberCommand) {
                RepresentationManager.getInstance().removeLast();
            } else if (last instanceof MacroCommand macro) {
                for (int i = 0; i < macro.getCommands().size(); i++) {
                    RepresentationManager.getInstance().removeLast();
                }
            }
            System.out.println("Останню команду скасовано.");
        } else {
            System.out.println("Немає команд для скасування.");
        }
    }
}
// Менеджер представлень
class RepresentationManager {
    private static final RepresentationManager instance = new RepresentationManager();
    private final List<NumberRepresentation> representations = new ArrayList<>();
    private RepresentationManager() {}
    public static RepresentationManager getInstance() {
        return instance;
    }
    public void addRepresentation(NumberRepresentation rep) {
        representations.add(rep);
    }
    public List<NumberRepresentation> getRepresentations() {
        return representations;
    }
    public void setRepresentations(List<NumberRepresentation> reps) {
        representations.clear();
        representations.addAll(reps);
    }
    public boolean isEmpty() {
        return representations.isEmpty();
    }
    public void removeLast() {
        if (!representations.isEmpty()) {
            representations.remove(representations.size() - 1);
        }
    }
}
// Інтерфейс для відображення результатів
interface Displayable {
    void display();
    void display(String align, int width);
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
    public void display(String align, int width) {
        String value = getFormattedValue();
        String formatted = "right".equalsIgnoreCase(align)
                ? String.format("%" + width + "s", value)
                : String.format("%-" + width + "s", value);
        System.out.printf("| %-18s | %s |\n", getType(), formatted);
    }
    protected abstract String getFormattedValue();
    protected abstract String getType();
}
// Реалізація: Двійкове представлення
class BinaryRepresentation extends NumberRepresentation {
    private final String binary;
    public BinaryRepresentation(int number) {
        super(number);
        this.binary = Integer.toBinaryString(number);
    }
    public void display() {
        System.out.println("Двійкове представлення: " + binary);
    }
    protected String getFormattedValue() {
        return binary;
    }
    protected String getType() {
        return "Двійкове";
    }
}
// Клас для тестування функціональності
class Tester {
    public static void runTests() {
        System.out.println("\nТестування:");
        Command c1 = new AddNumberCommand(10);
        Command c2 = new AddNumberCommand(255);
        MacroCommand macro = new MacroCommand(List.of(c1, c2));
        CommandHistory.execute(macro);
        RepresentationManager.getInstance().getRepresentations().forEach(NumberRepresentation::display);
        CommandHistory.undoLast(); // undo macro
        System.out.println("Після скасування макрокоманди:");
        RepresentationManager.getInstance().getRepresentations().forEach(NumberRepresentation::display);
    }
    public static void performStatistics() {
        List<NumberRepresentation> numbers = RepresentationManager.getInstance().getRepresentations();
        int min = numbers.stream().mapToInt(NumberRepresentation::getNumber).parallel().min().orElseThrow();
        int max = numbers.stream().mapToInt(NumberRepresentation::getNumber).parallel().max().orElseThrow();
        double avg = numbers.stream().mapToInt(NumberRepresentation::getNumber).parallel().average().orElseThrow();
        System.out.println("Мінімум: " + min);
        System.out.println("Максимум: " + max);
        System.out.println("Середнє: " + avg);
    }
}

// Потік для виконання команд
class TaskWorker implements Runnable {
    private final Command command;
    public TaskWorker(Command command) {
        this.command = command;
    }
    @Override
    public void run() {
        command.execute();
    }
}
// Менеджер черги
class TaskQueueManager {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void submitCommand(Command command) {
        executor.submit(new TaskWorker(command));
    }

    public void shutdown() {
        executor.shutdown();
    }
}
// Основний клас
public class Main6 {
    private static final String FILE_NAME = "numbers.ser";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        var manager = RepresentationManager.getInstance();
        TaskQueueManager queueManager = new TaskQueueManager();
        boolean running = true;
        while (running) {
            System.out.println("[1] Ввести число");
            System.out.println("[2] Показати збережені представлення");
            System.out.println("[3] Вивести у вигляді таблиці");
            System.out.println("[4] Зберегти у файл");
            System.out.println("[5] Відновити з файлу");
            System.out.println("[6] Тестування");
            System.out.println("[7] Скасувати останню дію");
            System.out.println("[8] Статистичні обчислення");
            System.out.println("[0] Вийти");
            System.out.print("Ваш вибір: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1 -> {
                    System.out.print("Введіть ціле число: ");
                    int number = scanner.nextInt();
                    Command addCommand = new AddNumberCommand(number);
                    queueManager.submitCommand(addCommand);
                }
                case 2 -> {
                    var list = manager.getRepresentations();
                    if (list.isEmpty()) {
                        System.out.println("Список порожній.");
                    } else {
                        for (var r : list) {
                            System.out.println("Число: " + r.getNumber());
                            r.display();
                        }
                    }
                }
                case 3 -> {
                    System.out.print("Введіть ширину стовпця значення: ");
                    int width = scanner.nextInt();
                    scanner.nextLine(); // пропустити \n
                    System.out.print("Вирівнювання (l/r): ");
                    String align = scanner.nextLine();
                    System.out.println("\n+--------------------+-------------------------+");
                    System.out.printf("| %-18s | %-23s |\n", "Тип представлення", "Значення");
                    System.out.println("+--------------------+-------------------------+");
                    for (var r : manager.getRepresentations()) {
                        r.display(align.equals("r") ? "right" : "left", width);
                    }
                    System.out.println("+--------------------+-------------------------+");
                }
                case 4 -> {
                    try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
                        out.writeObject(manager.getRepresentations());
                        System.out.println("Збережено у файл.");
                    } catch (IOException e) {
                        System.out.println("Помилка збереження: " + e.getMessage());
                    }
                }
                case 5 -> {
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                        var loaded = (List<NumberRepresentation>) in.readObject();
                        manager.setRepresentations(loaded);
                        System.out.println("Список відновлено з файлу.");
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Помилка відновлення: " + e.getMessage());
                    }
                }
                case 6 -> Tester.runTests();
                case 7 -> CommandHistory.undoLast();
                case 8 -> Tester.performStatistics();
                case 0 -> running = false;
                default -> System.out.println("Невірна опція.");
            }
        }

        queueManager.shutdown();
        scanner.close();
        System.out.println("Програма завершена.");
    }
}
