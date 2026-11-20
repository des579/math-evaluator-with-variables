import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        mainLoop();
    }

    private static void mainLoop() {
        Scanner scanner = new Scanner(System.in);
        Interpreter interpreter = new Interpreter();
        while (true) {
            System.out.print("> ");
            String expression = scanner.nextLine().trim();

            if (expression.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            Double result = interpreter.input(expression);
            if (result != null)
                System.out.println(result);
        }

        scanner.close();
    }
}
