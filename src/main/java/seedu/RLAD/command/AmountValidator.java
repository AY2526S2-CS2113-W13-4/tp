package seedu.RLAD.command;

import seedu.RLAD.exception.RLADException;

/**
 * Shared validation for amount strings used by AddCommand and ModifyCommand.
 * Rejects scientific notation, excess decimal places, and non-positive values.
 */
public class AmountValidator {

    private static final double MAX_AMOUNT = 10_000_000.00;

    /**
     * Validates the format of an amount string before parsing.
     * Rejects scientific notation (e.g., 1e5) and more than 2 decimal places.
     *
     * @param amountStr the raw amount string from user input
     * @throws RLADException if the format is invalid
     */
    public static void validateFormat(String amountStr) throws RLADException {
        if (amountStr.matches(".*[eE].*")) {
            throw new RLADException("Invalid amount: '" + amountStr
                    + "'. Scientific notation is not allowed. Use standard format (e.g., 15.50)");
        }

        if (amountStr.contains(".")) {
            String[] decimalParts = amountStr.split("\\.");
            if (decimalParts.length == 2 && decimalParts[1].length() > 2) {
                throw new RLADException("Invalid amount: '" + amountStr
                        + "'. Maximum 2 decimal places allowed (e.g., 15.50)");
            }
        }
    }

    /**
     * Parses and validates an amount string fully.
     * Checks format, positivity, max limit, and rounds to 2 decimal places.
     *
     * @param amountStr the raw amount string from user input
     * @return the validated and rounded amount
     * @throws RLADException if the amount is invalid
     */
    public static double parseAndValidate(String amountStr) throws RLADException {
        validateFormat(amountStr);

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount: '" + amountStr
                    + "'. Please enter a number (e.g., 15.50)");
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new RLADException("Invalid amount: '" + amountStr
                    + "'. Please enter a valid number.");
        }

        if (amount <= 0) {
            throw new RLADException("Amount must be greater than 0. Got: " + amount);
        }

        if (amount > MAX_AMOUNT) {
            throw new RLADException(String.format(
                    "Amount cannot exceed $%,.2f. Got: $%,.2f", MAX_AMOUNT, amount));
        }

        double rounded = Math.round(amount * 100.0) / 100.0;
        if (rounded <= 0) {
            throw new RLADException("Amount rounds to $0.00. Minimum is $0.01.");
        }
        return rounded;
    }
}
