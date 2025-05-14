package task3;

import java.io.*;
import java.util.*;

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
        this.currentHeartRate = normalHeartRate + (int)(diff * 10);
    }

    public double getNormalTemperature() { return normalTemperature; }
    public int getNormalHeartRate() { return normalHeartRate; }
    public double getCurrentTemperature() { return currentTemperature; }
    public int getCurrentHeartRate() { return currentHeartRate; }
    public String getAdditionalInfo() { return (additionalInfo == null) ? "Не вказано" : additionalInfo; }
    public void setAdditionalInfo(String info) { this.additionalInfo = info; }

    @Override
    public String toString() {
        return "HeartRateData:\n" +
                "- Нормальна температура: " + normalTemperature + "°C\n" +
                "- Нормальна ЧСС: " + normalHeartRate + " уд/хв\n" +
                "- Поточна температура: " + currentTemperature + "°C\n" +
                "- Поточна ЧСС: " + currentHeartRate + " уд/хв\n" +
                "- Додаткова інформація: " + getAdditionalInfo();
    }
}

class HeartRateCalculator {
    private HeartRateData data;

    public HeartRateCalculator(HeartRateData data) {
        this.data = data;
    }

    public HeartRateData getData() {
        return data;
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Дані збережено у файл: " + filename);
        }
    }

    public static HeartRateData loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            HeartRateData data = (HeartRateData) ois.readObject();
            System.out.println("Дані відновлено з файлу: " + filename);
            return data;
        }
    }
}

interface Displayable {
    void display(HeartRateData data);
}

class TextDisplay implements Displayable {
    @Override
    public void display(HeartRateData data) {
        System.out.println(data);
    }
}

interface DisplayFactory {
    Displayable createDisplay();
}

class TextDisplayFactory implements DisplayFactory {
    @Override
    public Displayable createDisplay() {
        return new TextDisplay();
    }
}


public class Main3 {
    private static final String FILENAME = "heartrate.ser";
    private static List<HeartRateData> results = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DisplayFactory factory = new TextDisplayFactory();
        Displayable display = factory.createDisplay();

        try {
            System.out.println("=== Введення даних для розрахунку ===");

            System.out.print("Нормальна температура тіла (°C): ");
            double normTemp = Double.parseDouble(scanner.nextLine());

            System.out.print("Нормальна ЧСС (уд/хв): ");
            int normHR = Integer.parseInt(scanner.nextLine());

            System.out.print("Поточна температура тіла (°C): ");
            double currTemp = Double.parseDouble(scanner.nextLine());

            System.out.print("Додаткова інформація: ");
            String info = scanner.nextLine();

            HeartRateData data = new HeartRateData(normTemp, normHR, currTemp);
            data.setAdditionalInfo(info);

            HeartRateCalculator calculator = new HeartRateCalculator(data);
            results.add(data); // збереження в колекцію

            System.out.println("\n=== Результат обчислення ===");
            display.display(data);

            System.out.print("\nЗберегти в файл? (так/ні): ");
            if (scanner.nextLine().equalsIgnoreCase("так")) {
                calculator.saveToFile(FILENAME);
            }

            System.out.print("\nЗчитати з файлу? (так/ні): ");
            if (scanner.nextLine().equalsIgnoreCase("так")) {
                HeartRateData loaded = HeartRateCalculator.loadFromFile(FILENAME);
                System.out.println("\n=== Дані після десеріалізації ===");
                display.display(loaded);
            }

            System.out.println("\n=== Всі збережені результати в колекції ===");
            for (HeartRateData d : results) {
                display.display(d);
                System.out.println("---");
            }

        } catch (Exception e) {
            System.err.println("Сталася помилка: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nРоботу завершено.");
    }
}
