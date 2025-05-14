package task4;

import java.io.*;
import java.util.*;

/**
 * Інтерфейс фабрикованих об'єктів для виводу результатів.
 */
interface OutputStrategy {
    void print(HeartRateData data);
}

/**
 * Текстовий вивід у звичайному форматі.
 */
class TextOutput implements OutputStrategy {
    @Override
    public void print(HeartRateData data) {
        System.out.println(data);
    }
}

/**
 * Вивід у вигляді таблиці з налаштовуваною шириною.
 */
class TableOutput implements OutputStrategy {
    private int columnWidth;

    /**
     * Конструктор з параметром ширини колонки.
     * @param columnWidth ширина стовпців таблиці
     */
    public TableOutput(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    /**
     * Перевантажений метод: без параметра - за замовчуванням ширина 20.
     */
    public TableOutput() {
        this(20);
    }

    @Override
    public void print(HeartRateData data) {
        System.out.println("+" + "-".repeat(columnWidth) + "+" + "-".repeat(columnWidth) + "+");
        System.out.printf("| %-" + (columnWidth - 2) + "s | %-" + (columnWidth - 2) + "s |\n",
                "Параметр", "Значення");
        System.out.println("+" + "-".repeat(columnWidth) + "+" + "-".repeat(columnWidth) + "+");

        printRow("Нормальна температура", data.getNormalTemperature() + "°C");
        printRow("Нормальна ЧСС", data.getNormalHeartRate() + " уд/хв");
        printRow("Поточна температура", data.getCurrentTemperature() + "°C");
        printRow("Поточна ЧСС", data.getCurrentHeartRate() + " уд/хв");
        printRow("Дод. інформація", data.getAdditionalInfo());

        System.out.println("+" + "-".repeat(columnWidth) + "+" + "-".repeat(columnWidth) + "+");
    }

    private void printRow(String param, String value) {
        System.out.printf("| %-" + (columnWidth - 2) + "s | %-" + (columnWidth - 2) + "s |\n", param, value);
    }
}

/**
 * Абстрактна фабрика для створення стратегій виводу.
 */
interface OutputFactory {
    OutputStrategy createOutput();
}

/**
 * Фабрика для створення TextOutput.
 */
class TextOutputFactory implements OutputFactory {
    @Override
    public OutputStrategy createOutput() {
        return new TextOutput();
    }
}

/**
 * Фабрика для створення TableOutput з шириною від користувача.
 */
class TableOutputFactory implements OutputFactory {
    private int columnWidth;

    public TableOutputFactory(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    @Override
    public OutputStrategy createOutput() {
        return new TableOutput(columnWidth);
    }
}

/**
 * Основні дані серцебиття, серіалізований клас.
 */
class HeartRateData implements Serializable {
    private static final long serialVersionUID = 1L;

    private double normalTemperature;
    private int normalHeartRate;
    private double currentTemperature;
    private int currentHeartRate;
    private transient String additionalInfo;

    public HeartRateData(double normalTemperature, int normalHeartRate, double currentTemperature) {
        this.normalTemperature = normalTemperature;
        this.normalHeartRate = normalHeartRate;
        this.currentTemperature = currentTemperature;
        calculateHeartRate();
    }

    private void calculateHeartRate() {
        double diff = currentTemperature - normalTemperature;
        this.currentHeartRate = normalHeartRate + (int) (diff * 10);
    }

    public double getNormalTemperature() {
        return normalTemperature;
    }

    public int getNormalHeartRate() {
        return normalHeartRate;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public int getCurrentHeartRate() {
        return currentHeartRate;
    }

    public String getAdditionalInfo() {
        return (additionalInfo == null) ? "Не вказано" : additionalInfo;
    }

    public void setAdditionalInfo(String info) {
        this.additionalInfo = info;
    }

    @Override
    public String toString() {
        return "HeartRateData: " +
                "\n- Нормальна температура: " + normalTemperature +
                "\n- Нормальна частота серцебиття: " + normalHeartRate +
                "\n- Поточна температура: " + currentTemperature +
                "\n- Поточна частота серцебиття: " + currentHeartRate +
                "\n- Додаткова інформація: " + getAdditionalInfo();
    }
}

/**
 * Калькулятор для серцебиття.
 */
class HeartRateCalculator {
    private HeartRateData data;

    public HeartRateCalculator(HeartRateData data) {
        this.data = data;
    }

    public HeartRateData getData() {
        return data;
    }

    public void save(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        }
    }

    public static HeartRateData load(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (HeartRateData) ois.readObject();
        }
    }
}

/**
 * Клас для тестування обчислень і серіалізації.
 */
class HeartRateTest {
    public void runTests() throws IOException, ClassNotFoundException {
        System.out.println("=== Тести ===");

        HeartRateData data = new HeartRateData(36.6, 70, 38.1);
        assert data.getCurrentHeartRate() == 85 : "Розрахунок ЧСС неправильний";

        File temp = new File("temp.ser");
        HeartRateCalculator calc = new HeartRateCalculator(data);
        calc.save(temp.getAbsolutePath());

        HeartRateData loaded = HeartRateCalculator.load(temp.getAbsolutePath());
        assert loaded.getCurrentHeartRate() == 85 : "Помилка при десеріалізації";

        temp.delete();
        System.out.println("Тести пройдено успішно.");
    }
}

/**
 * Головний клас Task4.
 */
public class Main4 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== Введення даних ===");
            System.out.print("Нормальна температура (°C): ");
            double normalTemp = Double.parseDouble(scanner.nextLine());

            System.out.print("Нормальна ЧСС (уд/хв): ");
            int normalRate = Integer.parseInt(scanner.nextLine());

            System.out.print("Поточна температура (°C): ");
            double currentTemp = Double.parseDouble(scanner.nextLine());

            System.out.print("Додаткова інформація: ");
            String info = scanner.nextLine();

            HeartRateData data = new HeartRateData(normalTemp, normalRate, currentTemp);
            data.setAdditionalInfo(info);

            System.out.println("\nВиберіть формат виводу:\n1 - Звичайний текст\n2 - Таблиця");
            int choice = Integer.parseInt(scanner.nextLine());

            OutputFactory factory;

            if (choice == 2) {
                System.out.print("Введіть ширину стовпця таблиці: ");
                int width = Integer.parseInt(scanner.nextLine());
                factory = new TableOutputFactory(width);
            } else {
                factory = new TextOutputFactory();
            }

            OutputStrategy output = factory.createOutput();
            output.print(data);  // Поліморфізм, пізнє зв'язування

            System.out.print("\nЗберегти у файл? (так/ні): ");
            if (scanner.nextLine().equalsIgnoreCase("так")) {
                new HeartRateCalculator(data).save("output.ser");
            }

            System.out.print("Зчитати з файлу? (так/ні): ");
            if (scanner.nextLine().equalsIgnoreCase("так")) {
                HeartRateData loaded = HeartRateCalculator.load("output.ser");
                output.print(loaded);
            }

            System.out.print("\nЗапустити тести? (так/ні): ");
            if (scanner.nextLine().equalsIgnoreCase("так")) {
                new HeartRateTest().runTests();
            }

        } catch (Exception e) {
            System.out.println("Помилка: " + e.getMessage());
        }
    }
}
