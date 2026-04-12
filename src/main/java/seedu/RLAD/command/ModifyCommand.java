// ModifyCommand.java - Redesigned
package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModifyCommand extends Command {

    private static final double MAX_AMOUNT = 10_000_000.00;
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    public ModifyCommand(String rawArgs) {
        super(rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException("Usage: modify <hashID> [field=value ...]\n" +
                    "  Fields: type, amount, date, category, description\n" +
                    "  Example: modify a7b2c3 amount=25.00 description='New description'");
        }

        String[] parts = rawArgs.trim().split("\\s+", 2);
        // Allows for uppercase and lowercase hashID in the input
        String id = parts[0].toLowerCase();
        String updatesStr = parts.length > 1 ? parts[1] : "";

        Transaction existing = transactions.findTransaction(id);
        if (existing == null) {
            throw new RLADException("Transaction not found: " + id);
        }

        // Parse field=value pairs (supports quoted values with spaces)
        Map<String, String> updates = new HashMap<>();
        if (!updatesStr.isEmpty()) {
            List<String> pairs = splitRespectingQuotes(updatesStr);
            for (String pair : pairs) {
                String[] kv = pair.split("=", 2);
                if (kv.length == 2) {
                    String field = kv[0].toLowerCase();
                    String value = kv[1];
                    // Strip surrounding quotes from value
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (updates.containsKey(field)) {
                        throw new RLADException("Duplicate field: '" + field
                                + "' was specified more than once.");
                    }
                    updates.put(field, value);
                } else {
                    throw new RLADException("Invalid format. Use field=value (e.g., amount=25.00)");
                }
            }
        }

        // After parsing updates, check if there are any
        if (updates.isEmpty()) {
            throw new RLADException("No fields to update. Usage: modify <hashID> field=value [field=value ...]");
        }

        // Reject unknown field names
        Set<String> validFields = Set.of("type", "amount", "date", "category", "description");
        for (String key : updates.keySet()) {
            if (!validFields.contains(key)) {
                throw new RLADException("Unknown field: '" + key + "'. "
                        + "Valid fields: type, amount, date, category, description");
            }
        }

        // Apply updates
        String type = updates.getOrDefault("type", existing.getType());
        String category = updates.getOrDefault("category", existing.getCategory());
        String description = updates.getOrDefault("description", existing.getDescription());
        double amount = updates.containsKey("amount") ?
                parseAmount(updates.get("amount")) : existing.getAmount();
        LocalDate date = updates.containsKey("date") ?
                parseDate(updates.get("date")) : existing.getDate();

        // Validate type
        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("Type must be 'credit' or 'debit'");
        }

        Transaction updated = new Transaction(type, category, amount, date, description);
        updated.setHashId(id);

        transactions.updateTransaction(id, updated);
        ui.showResult(String.format(
                "✅ Transaction updated!\n   ID: %s\n   New: %s",
                id, formatTransaction(updated)
        ));
    }

    private double parseAmount(String amountStr) throws RLADException {
        try {
            double value = Double.parseDouble(amountStr);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new RLADException("Invalid amount");
            }
            if (value <= 0) {
                throw new RLADException("Amount must be > 0");
            }
            if (value > MAX_AMOUNT) {
                throw new RLADException(String.format("Amount cannot exceed $%,.2f", MAX_AMOUNT));
            }
            double rounded = Math.round(value * 100.0) / 100.0;
            if (rounded <= 0) {
                throw new RLADException("Amount rounds to $0.00. Minimum is $0.01.");
            }
            return rounded;
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount");
        }
    }

    private LocalDate parseDate(String dateStr) throws RLADException {
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
        } catch (Exception e) {
            throw new RLADException("Invalid date. Use YYYY-MM-DD");
        }
    }

    private String formatTransaction(Transaction t) {
        return String.format("%s | $%,.2f | %s | %s | %s",
                t.getType().toUpperCase(), t.getAmount(), t.getDate(),
                (t.getCategory() == null || t.getCategory().isEmpty())
                        ? "(none)" : t.getCategory(),
                (t.getDescription() == null || t.getDescription().isEmpty())
                        ? "(none)" : t.getDescription());
    }

    /**
     * Splits a string by whitespace but keeps quoted segments together.
     * e.g. 'amount=25.00 description="Fancy chicken rice"'
     * becomes ["amount=25.00", "description=\"Fancy chicken rice\""]
     */
    private List<String> splitRespectingQuotes(String input) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ' ' && !inQuotes) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
