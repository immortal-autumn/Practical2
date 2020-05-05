package main;

import main.Impls.WalkSAT;
import main.Interfaces.IWalkSAT;

import java.io.File;

public class Main {
    public static long now;

    public static void main(String[] args) {
        now = System.currentTimeMillis();
        // write your code here
        if (args.length == 3) {
            validation(args);
            catchExit(); // Catch the exit interrupt and time-out.
            String filepath = args[0];
            String p = args[1];
            Thread thread = new Thread(() -> {
                run(filepath, p);
            });
            thread.start();
            long duration = (long) (Double.parseDouble(args[2]) * 1000);
            try {
                thread.join(duration);
            } catch (InterruptedException e) {
                System.exit(0);
            }
            if (System.currentTimeMillis() >= now + duration) {
                System.out.println("- UNKNOWN -");
                System.err.println("Timed out!");
                System.exit(-123);
            }
        } else {
            System.err.println("CLASS: java Main [filepath] [init probability] [time-out(second)]");
            System.err.println("JAR: java -jar Walk.jar [filepath] [init probability] [time-out(second)]");
        }
    }

    /**
     * Run the walkSAT method.
     *
     * @param filepath is the path of the input file.
     * @param p        is the probability p from user input.
     */
    private static void run(String filepath, String p) {
        File file = new File(filepath);
        if (file.exists()) {
            IWalkSAT walkSAT = new WalkSAT();
            walkSAT.run(file, Double.parseDouble(p));
        } else {
            System.err.println("FILE " + filepath + " does not exists");
        }
    }

    /**
     * String[1] is a time_out double, String[2] is a possibility double, String[0] has no specification.
     *
     * @param args is the command line argument from command-line
     */
    private static void validation(String[] args) {
        try {
            Double.parseDouble(args[1]);
            Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Not a valid input format: p=" + args[1] + " time-out=" + args[2]);
            System.exit(-2);
        }
    }

    private static void catchExit() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Process ends in " + (System.currentTimeMillis() - now) + " ms.");
            System.out.println(Thread.currentThread().getName() + " is called: Thanks for using!");
        }));
    }
}
