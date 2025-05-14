package task1;

public class Main {
        public static void main(String[] args) {
            System.out.println("Програма для виводу аргументів командного рядка");
            System.out.println("Кількість аргументів: " + args.length);

            if (args.length == 0) {
                System.out.println("Аргументи відсутні");
            } else {
                System.out.println("Список аргументів:");
                for (int i = 0; i < args.length; i++) {
                    System.out.println((i + 1) + ": " + args[i]);
                }
            }
        }
    }