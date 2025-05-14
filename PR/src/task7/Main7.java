package task7;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Клас Result містить дані про пульс, температуру та стан.
 */
class Result {
    private final int heartRate;
    private final double temperature;
    private final String condition;

    public Result(int heartRate, double temperature, String condition) {
        this.heartRate = heartRate;
        this.temperature = temperature;
        this.condition = condition;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "Пульс: " + heartRate + ", Температура: " + temperature + ", Стан: " + condition;
    }
}

/**
 * Інтерфейс команди.
 */
interface Command {
    void execute();
    void undo();
}

/**
 * Команда додавання результату.
 */
class AddResultCommand implements Command {
    private final Task7 task;
    private final Result result;

    public AddResultCommand(Task7 task, Result result) {
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
 * Макрокоманда, яка виконує список команд.
 */
class MacroCommand implements Command {
    private final java.util.List<Command> commands;

    public MacroCommand(java.util.List<Command> commands) {
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

class Task7 {
    private static Task7 instance;
    private final java.util.List<Result> results = new ArrayList<>();
    private final Deque<Command> commandHistory = new ArrayDeque<>();

    private Task7() {}

    public static Task7 getInstance() {
        if (instance == null) {
            instance = new Task7();
        }
        return instance;
    }

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            commandHistory.pop().undo();
        }
    }

    public void addResult(Result result) {
        results.add(result);
    }

    public void removeResult(Result result) {
        results.remove(result);
    }

    public java.util.List<Result> getResults() {
        return results;
    }
}

/**
 * Основний клас Main7 — реалізує графічний інтерфейс користувача.
 */
public class Main7 extends JFrame {
    private final JTextField heartRateField = new JTextField(5);
    private final JTextField temperatureField = new JTextField(5);
    private final JTextField conditionField = new JTextField(10);
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Пульс", "Температура", "Стан"}, 0);
    private final JTable resultTable = new JTable(tableModel);
    private final Task7 task = Task7.getInstance();

    public Main7() {
        super("Реєстрація показників");

        // Поля введення
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Пульс:"));
        inputPanel.add(heartRateField);
        inputPanel.add(new JLabel("Температура:"));
        inputPanel.add(temperatureField);
        inputPanel.add(new JLabel("Стан:"));
        inputPanel.add(conditionField);

        // Кнопки
        JButton addButton = new JButton("Додати");
        JButton macroButton = new JButton("Додати 3 записи");
        JButton undoButton = new JButton("Undo");

        inputPanel.add(addButton);
        inputPanel.add(macroButton);
        inputPanel.add(undoButton);

        // Обробка кнопок
        addButton.addActionListener(e -> {
            Result result = createResultFromInput();
            if (result != null) {
                task.executeCommand(new AddResultCommand(task, result));
                updateTable();
            }
        });

        macroButton.addActionListener(e -> {
            java.util.List<Command> cmds = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                Result r = new Result(60 + new Random().nextInt(40), 36.0 + new Random().nextDouble(), "Стан " + (i + 1));
                cmds.add(new AddResultCommand(task, r));
            }
            task.executeCommand(new MacroCommand(cmds));
            updateTable();
        });

        undoButton.addActionListener(e -> {
            task.undoLastCommand();
            updateTable();
        });

        // Основна панель
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Створює об'єкт Result із полів введення.
     */
    private Result createResultFromInput() {
        try {
            int heartRate = Integer.parseInt(heartRateField.getText());
            double temperature = Double.parseDouble(temperatureField.getText());
            String condition = conditionField.getText();
            return new Result(heartRate, temperature, condition);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Невірний формат введення!", "Помилка", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Оновлює таблицю з результатами.
     */
    private void updateTable() {
        tableModel.setRowCount(0);
        for (Result r : task.getResults()) {
            tableModel.addRow(new Object[]{r.getHeartRate(), r.getTemperature(), r.getCondition()});
        }
    }

    /**
     * Запуск програми.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main7::new);
    }
}
