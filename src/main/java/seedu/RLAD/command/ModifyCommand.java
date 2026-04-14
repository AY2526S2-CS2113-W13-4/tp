package seedu.RLAD.command;

import seedu.RLAD.Transaction;
import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import seedu.RLAD.exception.RLADException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModifyCommand extends Command {

    public ModifyCommand(String action, String rawArgs) {
        super(action, rawArgs);
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) throws RLADException {
        if (!hasValidArgs()) {
            throw new RLADException(getUsageHelp());
        }

        List<String> tokens = parseWithQuotes(rawArgs.trim());
        if (tokens.isEmpty()) {
            throw new RLADException(getUsageHelp());
        }

        String id = tokens.get(0);
        Transaction existing = transactions.findTransaction(id);
        if (existing == null) {
            throw new RLADException("Transaction not found: " + id);
        }

        Map<String, String> updates = parseFieldValuePairs(tokens.subList(1, tokens.size()));

        // This if statement fixes the failing test 'execute_noFields_throwsException'
        if (updates.isEmpty()) {
            throw new RLADException("No fields to update. "
                    + "Usage: modify <hashID> field=value [field=value ...]");
        }

        // Check if any fields would actually change
        Map<String, String> actualChanges = filterUnchangedFields(existing, updates);

        if (actualChanges.isEmpty()) {
            throw new RLADException("No changes detected. All provided fields already have the same values.\n"
                    + "Transaction ID: " + id + "\n"
                    + "Current: " + formatTransaction(existing));
        }

        Transaction updated = buildUpdatedTransaction(existing, updates);

        transactions.updateTransaction(id, updated);
        ui.showResult(String.format("✅ Transaction updated!\n   ID: %s\n   New: %s",
                id, formatTransaction(updated)));
    }

    /**
     * Filters out updates that would not actually change the transaction.
     * Compares the proposed updates against the existing transaction values.
     *
     * @param existing The current transaction
     * @param updates The proposed updates from the user
     * @return A map containing only updates that would result in a change
     * @throws RLADException If type validation fails
     */
    private Map<String, String> filterUnchangedFields(Transaction existing, Map<String, String> updates)
            throws RLADException {
        Map<String, String> actualChanges = new HashMap<>();

        for (Map.Entry<String, String> entry : updates.entrySet()) {
            String field = entry.getKey();
            String newValue = entry.getValue();

            switch (field) {
            case "type":
                String newType = newValue.toLowerCase();
                if (!newType.equals("credit") && !newType.equals("debit")) {
                    throw new RLADException("Type must be 'credit' or 'debit'.");
                }
                if (!existing.getType().equalsIgnoreCase(newType)) {
                    actualChanges.put(field, newValue);
                }
                break;

            case "amount":
                double newAmount = AmountValidator.parseAndValidate(newValue);
                if (Math.abs(existing.getAmount() - newAmount) > 0.0001) { // Compare with tolerance
                    actualChanges.put(field, newValue);
                }
                break;

            case "date":
                LocalDate newDate;
                try {
                    newDate = LocalDate.parse(newValue.trim());
                } catch (DateTimeParseException e) {
                    throw new RLADException("Invalid date format. Use YYYY-MM-DD.");
                }
                if (!existing.getDate().equals(newDate)) {
                    actualChanges.put(field, newValue);
                }
                break;

            case "category":
                String existingCategory = existing.getCategory() == null ? "" : existing.getCategory();
                String newCategory = newValue == null ? "" : newValue;
                if (!existingCategory.equals(newCategory)) {
                    actualChanges.put(field, newValue);
                }
                break;

            case "description":
                String existingDesc = existing.getDescription() == null ? "" : existing.getDescription();
                String newDesc = newValue == null ? "" : newValue;
                if (!existingDesc.equals(newDesc)) {
                    actualChanges.put(field, newValue);
                }
                break;

            default:
                // Unknown field will be caught in buildUpdatedTransaction
                actualChanges.put(field, newValue);
                break;
            }
        }

        return actualChanges;
    }

    private List<String> parseWithQuotes(String input) {
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
                    tokens.add(removeSurroundingQuotes(current.toString()));
                    current = new StringBuilder();
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(removeSurroundingQuotes(current.toString()));
        }
        return tokens;
    }

    private String removeSurroundingQuotes(String token) {
        if (token == null) {
            return null;
        }
        String cleaned = token.trim();
        while (cleaned.length() >= 2 && cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        return cleaned;
    }

    private Map<String, String> parseFieldValuePairs(List<String> tokens) throws RLADException {
        Map<String, String> updates = new HashMap<>();
        for (String pair : tokens) {
            int eqIndex = pair.indexOf('=');
            if (eqIndex <= 0 || eqIndex == pair.length() - 1) {
                throw new RLADException("Invalid format: '" + pair + "'. Use field=value.");
            }
            updates.put(pair.substring(0, eqIndex).toLowerCase(), pair.substring(eqIndex + 1));
        }
        return updates;
    }

    private Transaction buildUpdatedTransaction(Transaction existing, Map<String, String> updates)
            throws RLADException {
        Set<String> validFields = Set.of("type", "amount", "date", "category", "description");
        for (String key : updates.keySet()) {
            if (!validFields.contains(key)) {
                throw new RLADException("Unknown field: '" + key + "'.");
            }
        }

        String type = updates.getOrDefault("type", existing.getType());
        String category = updates.getOrDefault("category", existing.getCategory());

        if (category != null && category.trim().matches("-?\\d+(\\.\\d+)?")) {
            throw new RLADException("Category cannot be purely numerical.");
        }

        double amount = updates.containsKey("amount")
                ? AmountValidator.parseAndValidate(updates.get("amount")) : existing.getAmount();

        LocalDate date = existing.getDate();
        if (updates.containsKey("date")) {
            try {
                date = LocalDate.parse(updates.get("date").trim());
            } catch (DateTimeParseException e) {
                throw new RLADException("Invalid date format. Use YYYY-MM-DD.");
            }
        }

        if (!type.equals("credit") && !type.equals("debit")) {
            throw new RLADException("Type must be 'credit' or 'debit'.");
        }

        Transaction updated = new Transaction(type, category, amount, date,
                updates.getOrDefault("description", existing.getDescription()));
        updated.setHashId(existing.getHashId());
        return updated;
    }

    private String formatTransaction(Transaction t) {
        String cat = (t.getCategory() == null || t.getCategory().isEmpty()) ? "(none)" : t.getCategory();
        String desc = (t.getDescription() == null || t.getDescription().isEmpty()) ? "(none)" : t.getDescription();
        return String.format("%s | $%,.2f | %s | %s | %s",
                t.getType().toUpperCase(), t.getAmount(), t.getDate(), cat, desc);
    }

    private String getUsageHelp() {
        return "Usage: modify <hashID> field=value [field=value ...]";
    }

    @Override
    public boolean hasValidArgs() {
        return rawArgs != null && !rawArgs.trim().isEmpty();
    }
}
