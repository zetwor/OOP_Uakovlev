package task6;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main6 {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> numbers = new Random().ints(100, 0, 100).boxed().collect(Collectors.toList());

        TaskQueue taskQueue = new TaskQueue();
        taskQueue.submit(new MinTask(numbers));
        taskQueue.submit(new MaxTask(numbers));
        taskQueue.submit(new AverageTask(numbers));
        taskQueue.submit(new FilterTask(numbers, n -> n > 50));
        taskQueue.submit(new StatisticsTask(numbers));
        taskQueue.startWorkers(3);
        Thread.sleep(3000);
        taskQueue.stopWorkers();
    }
}

/**
 * Інтерфейс команди
 */
interface TaskCommand {
    void execute();
}

/**
 * Пошук мінімального значення
 */
class MinTask implements TaskCommand {
    private final List<Integer> data;

    public MinTask(List<Integer> data) {
        this.data = data;
    }

    @Override
    public void execute() {
        int min = data.parallelStream().min(Integer::compare).orElseThrow();
        System.out.println("Мінімальне значення: " + min);
    }
}

/**
 * Пошук максимального значення
 */
class MaxTask implements TaskCommand {
    private final List<Integer> data;

    public MaxTask(List<Integer> data) {
        this.data = data;
    }

    @Override
    public void execute() {
        int max = data.parallelStream().max(Integer::compare).orElseThrow();
        System.out.println("Максимальне значення: " + max);
    }
}

/**
 * Обчислення середнього значення
 */
class AverageTask implements TaskCommand {
    private final List<Integer> data;

    public AverageTask(List<Integer> data) {
        this.data = data;
    }

    @Override
    public void execute() {
        double avg = data.parallelStream().mapToInt(i -> i).average().orElse(0);
        System.out.println("Середнє значення: " + avg);
    }
}

/**
 * Фільтрація даних за критерієм
 */
class FilterTask implements TaskCommand {
    private final List<Integer> data;
    private final java.util.function.Predicate<Integer> predicate;

    public FilterTask(List<Integer> data, java.util.function.Predicate<Integer> predicate) {
        this.data = data;
        this.predicate = predicate;
    }

    @Override
    public void execute() {
        List<Integer> filtered = data.parallelStream().filter(predicate).collect(Collectors.toList());
        System.out.println("Відібрані значення (>50): " + filtered);
    }
}

/**
 * Статистична обробка: кількість, дисперсія
 */
class StatisticsTask implements TaskCommand {
    private final List<Integer> data;

    public StatisticsTask(List<Integer> data) {
        this.data = data;
    }

    @Override
    public void execute() {
        IntSummaryStatistics stats = data.parallelStream().mapToInt(Integer::intValue).summaryStatistics();
        System.out.println("Кількість: " + stats.getCount());
        System.out.println("Сума: " + stats.getSum());
        System.out.println("Середнє: " + stats.getAverage());
        System.out.println("Мін: " + stats.getMin());
        System.out.println("Макс: " + stats.getMax());
    }
}

/**
 * Клас черги завдань з Worker Thread
 */
class TaskQueue {
    private final BlockingQueue<TaskCommand> queue = new LinkedBlockingQueue<>();
    private final List<Thread> workers = new ArrayList<>();
    private volatile boolean running = true;

    /**
     * Додає нове завдання в чергу
     */
    public void submit(TaskCommand command) {
        queue.offer(command);
    }

    /**
     * Запускає вказану кількість робітників
     */
    public void startWorkers(int count) {
        for (int i = 0; i < count; i++) {
            Thread worker = new Thread(() -> {
                while (running || !queue.isEmpty()) {
                    try {
                        TaskCommand command = queue.poll(1, TimeUnit.SECONDS);
                        if (command != null) {
                            command.execute();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            worker.start();
            workers.add(worker);
        }
    }

    /**
     * Зупиняє всіх робітників після завершення обробки
     */
    public void stopWorkers() {
        running = false;
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
