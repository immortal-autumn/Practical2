package main.Impls;

import main.Interfaces.ICNF;

import java.util.*;

public class CNF implements ICNF {
    private int num_of_var;
    private int num_of_clause;
    // The clause read from the file. When considering the value as truth assignment:
    // Once element in clauses is set to 0, then this element is set to true.
    // If the element is set to -1, then this element is false.
    private Map<Integer, Map<Integer, Boolean>> clauses;

    // Clause contains the structure like:
    // var : [clause1(1), clause3(3), clause5(5)]
    // It will be initialised via: keyset: [-3, -2, -1, 1, 2, 3]
    // To scan the file, the num of clause will add 1 if it scanned 0.
    private Map<Integer, List<Integer>> clausesContainingLiterals;
    private List<Integer> falseClauses;
    private int[] numSatisfiedPerClause;

    private double p; // The initial input argument probability p.

    /**
     * Initializer of CNF, including the structure and additional structure.
     *
     * @param num_of_var    is number of variable.
     * @param num_of_clause is number of clauses.
     */
    public CNF(int num_of_var, int num_of_clause, double p) {
        // Global var inherited from user input.
        this.num_of_clause = num_of_clause;
        this.num_of_var = num_of_var;
        this.p = p;

        // Initialising static variable.
        clausesContainingLiterals = new HashMap<>();
        clauses = new HashMap<>();

        // Initialising variables.
        falseClauses = new ArrayList<>();
        numSatisfiedPerClause = new int[num_of_clause];

        // Initialising structures
        init();
    }

    /**
     * Prevent the null pointer exception. Add new values to clauses.
     */
    private void init() {
        for (int i = 1; i <= num_of_var; i++) {
            clausesContainingLiterals.put(i, new ArrayList<>());
            clausesContainingLiterals.put(-i, new ArrayList<>());
        }
        for (int i = 0; i < num_of_clause; i++) {
            clauses.put(i, new HashMap<>());
        }
    }

    /**
     * Generate a random truth assignment.
     *
     * @param random is the random class called by caller.
     */
    private void generateRandomAssignment(Random random) {
        for (int i = 1; i <= num_of_var; i++) {
            boolean randAssign = random.nextBoolean();
            for (int j : clausesContainingLiterals.get(i)) {
                if (randAssign) {
                    numSatisfiedPerClause[j] += 1;
                }
                clauses.get(j).put(i, randAssign);
            }
            for (int j : clausesContainingLiterals.get(-i)) {
                if (!randAssign) {
                    numSatisfiedPerClause[j] += 1;
                }
                clauses.get(j).put(-i, !randAssign);
            }
        }
        checkFalseClause();
//        System.out.println(falseClauses + "\n\n" + clauses);
    }

    /**
     * Totally flush the false clause and add num satisfied lits per clause == 0 to false clause.
     */
    private void checkFalseClause() {
        for (int i = 0; i < num_of_clause; i++) {
            if (numSatisfiedPerClause[i] == 0) {
                falseClauses.add(i);
            }
        }
    }

    @Override
    public void putClause(int clause, int var) {
        clausesContainingLiterals.get(clause).add(var);
    }

    @Override
    public void putLiteral(int var, int clause) {
        clauses.get(var).put(clause, false);
    }

    @Override
    public void printAllClauses() {
        System.out.println("False clauses: " + falseClauses);
        System.out.println("Num satisfied: " + Arrays.toString(numSatisfiedPerClause));

        System.out.println("Clauses :" + clauses);
        System.out.println("Clauses containing literal: " + clausesContainingLiterals);

        System.out.println("Clause size: " + clauses.size() + " - " + num_of_clause);
        System.out.println("Literal size:" + clausesContainingLiterals.size() + " - " + num_of_var);

        System.out.println("\nReconstruction of clauses:");
        //   Construct the clause from clauses containing literals.
        for (int i = 0; i < num_of_clause; i++) {
            System.out.print("Clause " + i + ": [");
            for (int k : clausesContainingLiterals.keySet()) {
                if (clausesContainingLiterals.get(k).contains(i)) {
                    System.out.print(" " + k);
                }
            }
            System.out.println(" ]"); // Flush the line and return a new line.
        }
    }

    @Override
    public void run() {
        Random random = new Random(System.currentTimeMillis());
        generateRandomAssignment(random); // T is now randomly   assigned.
        int turns = 0;
        while (!falseClauses.isEmpty()) {
            turns++;
            int clauseInd = falseClauses.get(random.nextInt(falseClauses.size()));
            double r = random.nextDouble();
            int var;
            if (r > p) {
                var = pickVar(clauseInd);
            } else {
                Map<Integer, Boolean> clause = clauses.get(clauseInd);
                List<Integer> list = new ArrayList<>(clause.keySet());
                var = list.get(random.nextInt(list.size()));
            }
            flip(var);
        }
        print(turns);
    }

    private void print(int turns) {
        System.out.println("- SATISFIABLE -");
        System.out.println("Total looping: " + turns + " \nresult: ");
        int index = 0;
        for (Map<Integer, Boolean> o : clauses.values()) {
            System.out.print(index + ": [");
            for (Boolean j : o.values()) {
                System.out.print(j + " ");
            }
            index++;
            System.out.println("]");
        }
    }

    private void update_clause(boolean now, int clause) {
        if (now) {
            numSatisfiedPerClause[clause] -= 1;
        } else {
            numSatisfiedPerClause[clause] += 1;
        }
        if (numSatisfiedPerClause[clause] == 0) {
            falseClauses.add(clause);
        }
        if (numSatisfiedPerClause[clause] == 1) {
            falseClauses.remove((Integer) clause);
        }
//        System.out.println(falseClauses);
    }

    private void flip(int var) {
//        long current = System.nanoTime();
        for (int k : clausesContainingLiterals.get(var)) {
            boolean now = clauses.get(k).get(var);
            clauses.get(k).put(var, !now);
            update_clause(now, k);
        }
        for (int k : clausesContainingLiterals.get(-var)) {
            boolean now = clauses.get(k).get(-var);
            clauses.get(k).put(-var, !now);
            update_clause(now, k);
        }
//        System.out.println(System.nanoTime() - current);
    }

    /**
     * Calculate the change by calculating make(x) - break(x)
     *
     * @param clauseInd is the clause that we have chosen.
     * @return the maximal satisfiable.
     */
    private int pickVar(int clauseInd) {
        int max_change = Integer.MIN_VALUE;
        int max_ind = 0;
        for (int i : clauses.get(clauseInd).keySet()) {
            int current = makeVar(i) - breakVar(i);
            if (max_change <= current) {
                max_change = current;
                max_ind = i;
            }
        }
        return max_ind;
    }

    /**
     * Count the satisfiable status when flipping x.
     *
     * @param var is the variable that we want to flip.
     * @return number if clauses in clause contains literal(x) have value number satisfied lits per clause = 0;
     */
    private int makeVar(int var) {
        int num = 0;
        List<Integer> list = clausesContainingLiterals.get(var);
        for (int i : list) {
            if (numSatisfiedPerClause[i] == 0) {
                num += 1;
            }
        }
        return num;
    }

    /**
     * Count the satisfiable status when flipping x.
     *
     * @param var is the variable that we want to flip.
     * @return number if clauses in clause contains literal(¬x) have value number satisfied lits per clause = 1;
     */
    private int breakVar(int var) {
        int num = 0;
        List<Integer> list = clausesContainingLiterals.get(-var);
        for (int i : list) {
            if (numSatisfiedPerClause[i] == 1) {
                num += 1;
            }
        }
        return num;
    }
}
