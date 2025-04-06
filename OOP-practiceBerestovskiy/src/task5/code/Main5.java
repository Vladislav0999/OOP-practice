package task5.code;
import java.io.*;
import java.util.*;

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
class OctalRepresentation extends NumberRepresentation {
    private final String octal;
    public OctalRepresentation(int number) {
        super(number);
        this.octal = Integer.toOctalString(number);
    }
    public void display() {
        System.out.println("Вісімкове представлення: " + octal);
    }
    protected String getFormattedValue() {
        return octal;
    }
    protected String getType() {
        return "Вісімкове";
    }
}
class HexRepresentation extends NumberRepresentation {
    private final String hex;
    public HexRepresentation(int number) {
        super(number);
        this.hex = Integer.toHexString(number).toUpperCase();
    }
    public void display() {
        System.out.println("Шістнадцяткове представлення: " + hex);
    }
    protected String getFormattedValue() {
        return hex;
    }
    protected String getType() {
        return "Шістнадцяткове";
    }
}
// Інтерфейс фабрики
interface NumberFactory {
    NumberRepresentation create(int number);
}
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
// Singleton для менеджера представлень
class RepresentationManager {
    private static RepresentationManager instance;
    private List<NumberRepresentation> representations = new ArrayList<>();
    private RepresentationManager() {}
    public static RepresentationManager getInstance() {
        if (instance == null) {
            instance = new RepresentationManager();
        }
        return instance;
    }
    public List<NumberRepresentation> getRepresentations() {
        return representations;
    }
    public void setRepresentations(List<NumberRepresentation> newList) {
        this.representations = newList;
    }
    public void addRepresentation(NumberRepresentation r) {
        representations.add(r);
    }
    public void clear() {
        representations.clear();
    }
}
// Command Pattern
interface Command {
    void execute();
    void undo();
}
class AddNumberCommand implements Command {
    private final int number;
    private final List<NumberRepresentation> added = new ArrayList<>();
    public AddNumberCommand(int number) {
        this.number = number;
    }
    public void execute() {
        var manager = RepresentationManager.getInstance();
        added.add(new BinaryFactory().create(number));
        added.add(new OctalFactory().create(number));
        added.add(new HexFactory().create(number));
        for (var rep : added) manager.addRepresentation(rep);
        System.out.println("Представлення додано.");
    }
    public void undo() {
        var manager = RepresentationManager.getInstance();
        manager.getRepresentations().removeAll(added);
        System.out.println("Скасовано додавання числа: " + number);
    }
}
class MacroCommand implements Command {
    private final List<Command> commands;
    public MacroCommand(List<Command> commands) {
        this.commands = commands;
    }
    public void execute() {
        for (Command cmd : commands) cmd.execute();
    }
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo();
    }
}
// Історія команд
class CommandHistory {
    private static final Deque<Command> history = new ArrayDeque<>();
    public static void execute(Command command) {
        command.execute();
        history.push(command);
    }
    public static void undoLast() {
        if (!history.isEmpty()) {
            history.pop().undo();
        } else {
            System.out.println("Немає команд для скасування.");
        }
    }
}
// Основний клас
public class Main5 {
    private static final String FILE_NAME = "numbers.ser";
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        var manager = RepresentationManager.getInstance();
        boolean running = true;
        while (running) {
            System.out.println("[1] Ввести число");
            System.out.println("[2] Показати збережені представлення");
            System.out.println("[3] Вивести у вигляді таблиці");
            System.out.println("[4] Зберегти у файл");
            System.out.println("[5] Відновити з файлу");
            System.out.println("[6] Тестування");
            System.out.println("[7] Скасувати останню дію");
            System.out.println("[0] Вийти");
            System.out.print("Ваш вибір: ");
            int option = scanner.nextInt();
            switch (option) {
                case 1 -> {
                    System.out.print("Введіть ціле число: ");
                    int number = scanner.nextInt();
                    CommandHistory.execute(new AddNumberCommand(number));
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
        Command c1 = new AddNumberCommand(10);
        Command c2 = new AddNumberCommand(255);
        MacroCommand macro = new MacroCommand(List.of(c1, c2));
        CommandHistory.execute(macro);
        RepresentationManager.getInstance().getRepresentations().forEach(NumberRepresentation::display);
        CommandHistory.undoLast(); // undo macro
        System.out.println("Після скасування макрокоманди:");
        RepresentationManager.getInstance().getRepresentations().forEach(NumberRepresentation::display);
    }
}
