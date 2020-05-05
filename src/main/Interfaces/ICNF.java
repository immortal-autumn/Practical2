package main.Interfaces;

/**
 * Handles the program format: cnf
 */
public interface ICNF {

    void putClause(int clause, int var);

    void putLiteral(int var, int clause);

    void printAllClauses();

    void run();
}
