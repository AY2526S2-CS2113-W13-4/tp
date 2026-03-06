package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.TransactionSorter;
import seedu.RLAD.Ui;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Displays transactions with optional filtering and sorting.
 * Filtering is delegated to FilterCommand.buildPredicate() (Single Source of Truth).
 * Sorting uses TransactionSorter for --sort flag (amount or date, ascending).
 *
 * Example usage:
 *   list                                -> Show all transactions
 *   list --sort amount                  -> Sort by amount ascending
 *   list --type debit --sort amount     -> Show only debits, sorted by amount
 *   list --category food --sort date    -> Show food category, sorted by date
 *   list --amount -gt 50 --sort amount  -> Show amounts > 50, sorted
 */
public class ListCommand extends Command {
    private String sortBy;       //The field to sort by (amount or date), empty if no sorting
    private String filterArgs;   //The filter portion of rawArgs (everything except --sort)

    public ListCommand(String rawArgs) {
        super(rawArgs);
        this.sortBy = parseSortField(rawArgs);
        this.filterArgs = extractFilterArgs(rawArgs);
    }

    /**
     * Extracts the --sort value from raw arguments.
     *
     * @param rawArgs the raw argument string from user input
     * @return the sort field if provided, or empty string if not
     */
    private String parseSortField(String rawArgs) {
        if (rawArgs == null || rawArgs.isEmpty()) {
            return "";
        }
        String[] tokens = rawArgs.split("\\s+");
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].equals("--sort")) {
                return tokens[i + 1].toLowerCase();
            }
        }
        return "";
    }

    /**
     * Removes the --sort flag and its value from raw arguments,
     * leaving only filter-related flags for FilterCommand.buildPredicate().
     * This ensures --sort is handled by ListCommand while filter flags
     * are passed cleanly to the shared filtering logic.
     *
     * @param rawArgs the raw argument string from user input
     * @return the argument string with --sort and its value removed
     */
    private String extractFilterArgs(String rawArgs) {
        if (rawArgs == null || rawArgs.isEmpty()) {
            return "";
        }
        String[] tokens = rawArgs.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("--sort")) {
                //Skip the --sort flag and its value (next token)
                i++;
            } else {
                //Append non-sort tokens to the filter string
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(tokens[i]);
            }
        }
        return sb.toString().trim();
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // Step 1: Get all transactions from the manager
        ArrayList<Transaction> allTransactions = transactions.getTransactions();

        if (allTransactions.isEmpty()) {
            ui.showResult("Your wallet is empty! Use 'add' to record a transaction.");
            return;
        }

        // Step 2: Apply filter using FilterCommand's shared predicate builder
        Predicate<Transaction> predicate = FilterCommand.buildPredicate(filterArgs);
        ArrayList<Transaction> results = new ArrayList<>();
        for (Transaction t : allTransactions) {
            if (predicate.test(t)) {
                results.add(t);
            }
        }

        //Inform user if no transactions matched the filter criteria
        if (results.isEmpty()) {
            ui.showResult("No transactions match your filter criteria.");
            return;
        }

        // Step 3: Sort the filtered results if --sort flag was provided
        if (!sortBy.isEmpty()) {
            results = TransactionSorter.sort(results, sortBy);
        }

        // Step 4: Display the filtered (and optionally sorted) transactions
        for (Transaction transaction : results) {
            ui.showResult(transaction.toString());
        }
    }

    @Override
    public boolean hasValidArgs() {
        //If --sort is provided, validate that the sort field is recognized
        if (!sortBy.isEmpty() && !TransactionSorter.isValidSortField(sortBy)) {
            return false;
        }
        return true;
    }
}
