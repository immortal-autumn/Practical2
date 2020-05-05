package main.Impls;

import main.Interfaces.ICNF;
import main.Interfaces.IWalkSAT;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class WalkSAT implements IWalkSAT {

    private void start_searching(String type, Object property) {
        if (property == null) {
            System.err.println("Unable to load object! Failed to create " + type + " Object");
            System.exit(-404);
        }
        switch (type) {
            case "cnf": {
                ICNF cnf = (ICNF) property;
//                cnf.printAllClauses();
                cnf.run();
                break;
            }
            case "sat": {
                System.err.println("Type: " + type + " is not supported in this file.");
                break;
            }
            default: {
                System.err.println("Unable to find a type specifying the type: " + type);
                break;
            }
        }
    }

    @Override
    public void run(File file, double p) {
        ICNF property = null;
        String type = "";
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                switch (scanner.next()) {
                    case "c": {
                        scanner.nextLine();
                        break;
                    }
                    case "p": {
                        type = scanner.next();
                        if (type.equals("cnf")) {
                            property = new CNF(scanner.nextInt(), scanner.nextInt(), p);
                            int clauseInd = 0;
                            while (scanner.hasNextInt()) {
                                int next = scanner.nextInt();
                                if (next != 0) {
                                    property.putClause(next, clauseInd);
                                    property.putLiteral(clauseInd, next);
                                } else {
                                    clauseInd++;
                                }
                            }
                        } else {
                            System.out.println("Type" + type + "not implemented. System exit!");
                            System.exit(-404);
                        }
                        break;
                    }
                    default: {
                        System.out.println("Not readable argument!");
                        System.exit(1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            start_searching(type, property);
        }
    }
}
