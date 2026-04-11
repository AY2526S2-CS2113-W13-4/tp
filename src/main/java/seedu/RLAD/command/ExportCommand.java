package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;
import seedu.RLAD.storage.CsvStorageManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Exports all transactions to a CSV file.
 */
public class ExportCommand extends Command {

    /**
     * Creates a new ExportCommand.
     *
     * @param rawArgs the raw argument string
     */
    public ExportCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        String filename = parseFilename(rawArgs);

        Path filePath = Paths.get(filename);
        Path dirPath = filePath.getParent();
        if (dirPath == null) {
            dirPath = Paths.get(".");
        } else if (!Files.isDirectory(dirPath)) {
            throw new RLADException("Directory does not exist: " + dirPath);
        }

        ArrayList<Transaction> txns = transactions.getTransactions();
        if (txns.isEmpty()) {
            ui.showResult("No transactions to export.");
            return;
        }

        String fullPath = filePath.isAbsolute()
                ? filePath.toString()
                : dirPath.resolve(filePath.getFileName()).toString();
        CsvStorageManager.exportToCsv(txns, fullPath);

        ui.showResult("Exported " + txns.size() + " transactions to: " + fullPath);
    }

    private static String parseFilename(String rawArgs) {
        String defaultName = "transactions_"
                + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv";
        if (rawArgs == null || rawArgs.isBlank()) {
            return defaultName;
        }
        return stripQuotes(rawArgs.trim());
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
