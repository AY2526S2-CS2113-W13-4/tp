package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.storage.CsvStorageManager;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Imports transactions from a CSV file, either replacing or merging with existing data.
 */
public class ImportCommand extends Command {

    /**
     * Creates a new ImportCommand.
     *
     * @param rawArgs the raw argument string
     */
    public ImportCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (rawArgs == null || rawArgs.isBlank()) {
            throw new RLADException("Filename is required for import. "
                    + "Usage: import <filename> [merge]");
        }

        String[] tokens = rawArgs.trim().split("\\s+");
        String filePath = stripQuotes(tokens[0]);

        boolean mergeMode = false;
        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("merge")) {
                mergeMode = true;
            } else {
                throw new RLADException("Unknown argument: " + tokens[i] + ". "
                        + "Usage: import <filename> [merge]");
            }
        }

        if (!Files.exists(Paths.get(filePath))) {
            throw new RLADException("File not found: " + filePath);
        }

        if (!mergeMode && transactions.getTransactionCount() > 0) {
            boolean confirmed = ui.askConfirmation(
                    "WARNING: Replace mode will delete all "
                            + transactions.getTransactionCount() + " existing transactions.\n"
                            + "Use 'merge' to add to existing data instead.");
            if (!confirmed) {
                ui.showResult("Import cancelled.");
                return;
            }
        }

        CsvStorageManager.CsvImportResult result = CsvStorageManager.importFromCsv(filePath);

        for (String error : result.getErrors()) {
            ui.showError(error);
        }

        if (result.getSuccessCount() == 0) {
            throw new RLADException("No valid transactions found in file.");
        }

        if (!mergeMode) {
            transactions.clearAllTransactions();
        }

        for (Transaction t : result.getTransactions()) {
            transactions.addTransaction(t);
        }

        ui.showResult("Import complete: " + result.getSuccessCount() + " succeeded, "
                + result.getFailCount() + " failed.");
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    @Override
    public boolean hasValidArgs() {
        return true;
    }
}
