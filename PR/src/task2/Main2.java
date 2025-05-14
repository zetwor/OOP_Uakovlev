package task2;

import java.io.*;
import java.util.Scanner;

/**
 * Серіалізований клас для зберігання параметрів та результатів обчислень
 * частоти серцебиття на основі температури тіла.
 * @author Student
 */
class HeartRateData implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Нормальна температура тіла (у градусах Цельсія) */
    private double normalTemperature;

    /** Нормальна частота серцебиття (ударів за хвилину) */
    private int normalHeartRate;

    /** Поточна температура тіла (у градусах Цельсія) */
    private double currentTemperature;

    /** Поточна частота серцебиття (ударів за хвилину) */
    private int currentHeartRate;

    /** Transient поле - не буде серіалізовано, використовується для демонстрації */
    private transient String additionalInfo;

    /**
     * Конструктор для створення об'єкта з параметрами
     * @param normalTemperature Нормальна температура тіла
     * @param normalHeartRate Нормальна частота серцебиття
     * @param currentTemperature Поточна температура тіла
     */
    public HeartRateData(double normalTemperature, int normalHeartRate, double currentTemperature) {
        this.normalTemperature = normalTemperature;
        this.normalHeartRate = normalHeartRate;
        this.currentTemperature = currentTemperature;
        this.additionalInfo = "Ця інформація не буде збережена при серіалізації";

        // Розрахунок поточної частоти серцебиття
        calculateHeartRate();
    }

    /**
     * Метод для розрахунку поточної частоти серцебиття на основі різниці температур
     */
    private void calculateHeartRate() {
        // При підвищенні температури на 1 градус частота збільшується на 10 ударів за хвилину
        double temperatureDifference = currentTemperature - normalTemperature;
        this.currentHeartRate = normalHeartRate + (int)(temperatureDifference * 10);
    }

    /**
     * Отримати нормальну температуру тіла
     * @return Нормальна температура тіла
     */
    public double getNormalTemperature() {
        return normalTemperature;
    }

    /**
     * Отримати нормальну частоту серцебиття
     * @return Нормальна частота серцебиття
     */
    public int getNormalHeartRate() {
        return normalHeartRate;
    }

    /**
     * Отримати поточну температуру тіла
     * @return Поточна температура тіла
     */
    public double getCurrentTemperature() {
        return currentTemperature;
    }

    /**
     * Отримати поточну частоту серцебиття
     * @return Поточна частота серцебиття
     */
    public int getCurrentHeartRate() {
        return currentHeartRate;
    }

    /**
     * Отримати додаткову інформацію (transient поле)
     * @return Значення додаткової інформації або "Не вказано" якщо null
     */
    public String getAdditionalInfo() {
        return (additionalInfo == null) ? "Не вказано" : additionalInfo;
    }

    /**
     * Встановити додаткову інформацію
     * @param additionalInfo Нова додаткова інформація
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * Метод для представлення об'єкта у вигляді рядка
     * @return Рядок з інформацією про об'єкт
     */
    @Override
    public String toString() {
        return "HeartRateData:\n" +
                "- Нормальна температура: " + normalTemperature + "°C\n" +
                "- Нормальна частота серцебиття: " + normalHeartRate + " уд/хв\n" +
                "- Поточна температура: " + currentTemperature + "°C\n" +
                "- Поточна частота серцебиття: " + currentHeartRate + " уд/хв\n" +
                "- Додаткова інформація: " + getAdditionalInfo();
    }
}

/**
 * Клас для обчислення частоти серцебиття на основі температури тіла
 * з використанням агрегування
 */
class HeartRateCalculator {
    private HeartRateData data;

    /**
     * Конструктор, що приймає об'єкт HeartRateData
     * @param data Дані для розрахунку
     */
    public HeartRateCalculator(HeartRateData data) {
        this.data = data;
    }

    /**
     * Отримати результат обчислення
     * @return Об'єкт з даними та результатом
     */
    public HeartRateData getData() {
        return data;
    }

    /**
     * Метод для серіалізації об'єкта в файл
     * @param filename Ім'я файлу для збереження
     * @throws IOException При помилці вводу/виводу
     */
    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Дані успішно збережено в файл: " + filename);
        }
    }

    /**
     * Метод для десеріалізації об'єкта з файлу
     * @param filename Ім'я файлу для відновлення
     * @return Відновлений об'єкт HeartRateData
     * @throws IOException При помилці вводу/виводу
     * @throws ClassNotFoundException Якщо клас не знайдено
     */
    public static HeartRateData loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            HeartRateData loadedData = (HeartRateData) ois.readObject();
            System.out.println("Дані успішно відновлено з файлу: " + filename);
            return loadedData;
        }
    }
}

/**
 * Клас для демонстрації збереження та відновлення стану об'єкта з використанням серіалізації
 */
class HeartRateDemo {
    private static final String FILENAME = "heartrate.ser";
    private HeartRateCalculator calculator;

    /**
     * Запуск демонстрації в діалоговому режимі
     */
    public void runDemo() {
        Scanner scanner = new Scanner(System.in);

        try {
            // Отримання вхідних даних від користувача
            System.out.println("=== Розрахунок частоти серцебиття ===");

            System.out.print("Введіть нормальну температуру тіла (°C): ");
            double normalTemperature = Double.parseDouble(scanner.nextLine());

            System.out.print("Введіть нормальну частоту серцебиття (уд/хв): ");
            int normalHeartRate = Integer.parseInt(scanner.nextLine());

            System.out.print("Введіть поточну температуру тіла (°C): ");
            double currentTemperature = Double.parseDouble(scanner.nextLine());

            System.out.print("Введіть додаткову інформацію (не буде серіалізовано): ");
            String additionalInfo = scanner.nextLine();

            // Створення об'єктів
            HeartRateData data = new HeartRateData(normalTemperature, normalHeartRate, currentTemperature);
            data.setAdditionalInfo(additionalInfo);

            calculator = new HeartRateCalculator(data);

            // Виведення результатів
            System.out.println("\n=== Результати обчислень ===");
            System.out.println(calculator.getData());

            // Серіалізація
            System.out.print("\nЗберегти дані у файл? (так/ні): ");
            String saveChoice = scanner.nextLine().toLowerCase();

            if (saveChoice.equals("так")) {
                calculator.saveToFile(FILENAME);
            }

            // Десеріалізація
            System.out.print("\nВідновити дані з файлу? (так/ні): ");
            String loadChoice = scanner.nextLine().toLowerCase();

            if (loadChoice.equals("так")) {
                HeartRateData loadedData = HeartRateCalculator.loadFromFile(FILENAME);

                System.out.println("\n=== Відновлені дані ===");
                System.out.println(loadedData);
                System.out.println("\nЗверніть увагу, що transient поле не було серіалізовано.");

                // Встановлення нової додаткової інформації
                System.out.print("\nВведіть нову додаткову інформацію: ");
                String newInfo = scanner.nextLine();
                loadedData.setAdditionalInfo(newInfo);

                System.out.println("\n=== Оновлені дані ===");
                System.out.println(loadedData);
            }

        } catch (IOException | ClassNotFoundException | NumberFormatException e) {
            System.out.println("Помилка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

/**
 * Клас для тестування коректності обчислень та серіалізації/десеріалізації
 */
class HeartRateTest {
    /**
     * Запуск тестів
     */
    public void runTests() {
        System.out.println("\n=== Запуск тестів ===");

        try {
            testCalculation();
            testSerialization();

            System.out.println("Усі тести пройдено успішно!");
        } catch (AssertionError | IOException | ClassNotFoundException e) {
            System.out.println("Тест не пройдено: " + e.getMessage());
        }
    }

    /**
     * Тест коректності обчислень
     * @throws AssertionError Якщо тест не пройдено
     */
    private void testCalculation() {
        System.out.println("Тест обчислень...");

        // Тестові дані
        double normalTemperature = 36.6;
        int normalHeartRate = 70;
        double currentTemperature = 38.1;

        // Очікувана частота серцебиття: 70 + (38.1 - 36.6) * 10 = 70 + 15 = 85
        int expectedHeartRate = 85;

        HeartRateData data = new HeartRateData(normalTemperature, normalHeartRate, currentTemperature);

        // Перевірка
        if (data.getCurrentHeartRate() != expectedHeartRate) {
            throw new AssertionError(
                    "Очікувана частота: " + expectedHeartRate +
                            ", отримана: " + data.getCurrentHeartRate()
            );
        }

        System.out.println("Тест обчислень пройдено!");
    }

    /**
     * Тест серіалізації та десеріалізації
     * @throws IOException При помилці вводу/виводу
     * @throws ClassNotFoundException Якщо клас не знайдено
     * @throws AssertionError Якщо тест не пройдено
     */
    private void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("Тест серіалізації...");

        String testFilename = "test_heartrate.ser";

        // Створення та серіалізація
        HeartRateData originalData = new HeartRateData(36.6, 70, 38.1);
        originalData.setAdditionalInfo("Тестова інформація");

        HeartRateCalculator calculator = new HeartRateCalculator(originalData);
        calculator.saveToFile(testFilename);

        // Десеріалізація та перевірка
        HeartRateData loadedData = HeartRateCalculator.loadFromFile(testFilename);

        // Перевірка основних полів
        if (originalData.getNormalTemperature() != loadedData.getNormalTemperature() ||
                originalData.getNormalHeartRate() != loadedData.getNormalHeartRate() ||
                originalData.getCurrentTemperature() != loadedData.getCurrentTemperature() ||
                originalData.getCurrentHeartRate() != loadedData.getCurrentHeartRate()) {

            throw new AssertionError("Дані до і після серіалізації не співпадають");
        }

        // Перевірка transient поля
        if (!loadedData.getAdditionalInfo().equals("Не вказано")) {
            throw new AssertionError(
                    "Очікувано, що transient поле буде скинуто, але воно має значення: " +
                            loadedData.getAdditionalInfo()
            );
        }

        // Видалення тестового файлу
        new File(testFilename).delete();

        System.out.println("Тест серіалізації пройдено!");
    }
}

/**
 * Головний клас для демонстрації роботи програми
 */
public class Main2 {
    /**
     * Головний метод програми
     * @param args Аргументи командного рядка
     */
    public static void main(String[] args) {
        System.out.println("=== Програма розрахунку частоти серцебиття ===");

        // Запуск демонстрації
        HeartRateDemo demo = new HeartRateDemo();
        demo.runDemo();

        // Запуск тестів
        System.out.print("\nЗапустити тести? (так/ні): ");
        Scanner scanner = new Scanner(System.in);
        String runTests = scanner.nextLine().toLowerCase();

        if (runTests.equals("так")) {
            HeartRateTest test = new HeartRateTest();
            test.runTests();
        }

        System.out.println("\nПрограму завершено.");
    }
}