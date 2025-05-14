package task5;

import java.util.*;

/**
 * Інтерфейс для реалізації шаблону Command.
 */
interface Command {
    void execute();
    void undo();
}

/**
 * Клас результату здоров'я: пульс, температура і опис стану.
 */
class HealthResult {
    private final int heartRate;
    private final double temperature;
    private final String statusInfo;

    public HealthResult(int heartRate, double temperature, String statusInfo) {
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.statusInfo = statusInfo;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    @Override
    public String toString() {
        return "Пульс: " + heartRate + " уд/хв | Температура: " + temperature + " °C | Стан: " + statusInfo;
    }
}

/**
 * Команда для додавання одного результату.
 */
class AddResultCommand implements Command {
    private final Task5 task;
    private final HealthResult result;

    public AddResultCommand(Task5 task, HealthResult result) {
        this.task = task;
        this.result = result;
    }

    @Override
    public void execute() {
        task.addResult(result);
    }

    @Override
    public void undo() {
        task.removeResult(result);
    }
}

/**
 * Макрокоманда, що виконує список команд.
 */
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
    }

    @Override
    public void undo() {
        ListIterator<Command> it = commands.listIterator(commands.size());
        while (it.hasPrevious()) {
            it.previous().undo();
        }
    }
}

/**
 * Singleton-клас для обробки результатів здоров'я.
 */
class Task5 {
    private static Task5 instance;
    private final Deque<Command> commandHistory = new ArrayDeque<>();
    private final List<HealthResult> results = new ArrayList<>();

    private Task5() {}

    public static Task5 getInstance() {
        if (instance == null) {
            instance = new Task5();
        }
        return instance;
    }

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            Command last = commandHistory.pop();
            last.undo();
        } else {
            System.out.println("Немає команд для скасування.");
        }
    }

    public void addResult(HealthResult result) {
        results.add(result);
    }

    public void removeResult(HealthResult result) {
        results.remove(result);
    }

    public List<HealthResult> getResults() {
        return results;
    }
}

/**
 * Основний клас із діалоговим інтерфейсом.
 */
public class Main5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Task5 task = Task5.getInstance();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Додати результат");
            System.out.println("2. Скасувати останню дію");
            System.out.println("3. Показати результати");
            System.out.println("4. Макрокоманда: додати кілька результатів");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // очищення буфера

            switch (choice) {
                case 1 -> {
                    HealthResult result = getResultFromUser(scanner);
                    task.executeCommand(new AddResultCommand(task, result));
                }
                case 2 -> task.undoLastCommand();
                case 3 -> {
                    if (task.getResults().isEmpty()) {
                        System.out.println("Результати відсутні.");
                    } else {
                        task.getResults().forEach(System.out::println);
                    }
                }
                case 4 -> {
                    System.out.print("Скільки результатів додати: ");
                    int n = scanner.nextInt();
                    scanner.nextLine();
                    List<Command> commands = new ArrayList<>();
                    for (int i = 0; i < n; i++) {
                        HealthResult r = getResultFromUser(scanner);
                        commands.add(new AddResultCommand(task, r));
                    }
                    task.executeCommand(new MacroCommand(commands));
                }
                case 0 -> {
                    System.out.println("Завершення роботи.");
                    return;
                }
                default -> System.out.println("Невірний вибір.");
            }
        }
    }

    /**
     * Зчитування даних користувача.
     */
    private static HealthResult getResultFromUser(Scanner scanner) {
        System.out.print("Введіть пульс: ");
        int heartRate = scanner.nextInt();
        System.out.print("Введіть температуру тіла (°C): ");
        double temperature = scanner.nextDouble();
        scanner.nextLine(); // очищення буфера
        System.out.print("Введіть додаткову інформацію про стан: ");
        String info = scanner.nextLine();

        return new HealthResult(heartRate, temperature, info);
    }
}
