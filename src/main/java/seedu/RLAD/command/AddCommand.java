package seedu.RLAD.command;

import seedu.RLAD.TransactionManager;
import seedu.RLAD.Ui;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddCommand extends Command {
    public AddCommand(String rawArgs) {
        super(rawArgs);
    }

    //Declare a parseArgument method
    private Map<String, String> parseArguments(String rawArgs) {
        Map<String, String> argsMap = new HashMap<>();

        //Check if the string is empty, if it's empty then the empty map is returned, nothing to parse
        if (rawArgs == null || rawArgs.trim().isEmpty()) {
            return argsMap;
        }

        //Variables to track our position while reading
        StringBuilder currentFlag = null;   //To build the flag name char by char (like "--type")
        StringBuilder currentValue = new StringBuilder();   //Builds the value char by char
        boolean insideQuotes = false;   //A flag to check if we're inside the quotes (To handle description with spaces)

        //Loop through each character of the rawArgs
        for (int i = 0; i < rawArgs.length(); i++) {
            char c = rawArgs.charAt(i); //Stores the first char in c

            //Handle quotes, checks when you reached the description part
            if (c == '"') {
                insideQuotes = !insideQuotes;
                continue;   //Moves on to the next char of description
            }

            //If we hit a space and we're not inside quotes, it's the input of a non-description flag
            if (c == '' && !insideQuotes) {
                //If we have a complete flag and value , store it
                if (currentFlag != null && currentValue.length() > 0) {
                    argsMap.put(currentFlag.toString(), currentValue.toString().trim());    //Add or updates the flag-string pair into the HashMap. Updates if the key is already present
                    currentFlag = null; //Reset flag
                    currentValue.setLength(0);  //Clear value
                }
                continue;
            }

            //Check for the start of the flag (--something)
            if (c == '-' && i + 1 < rawArgs.length() && rawArgs.charAt(i + 1) == '-') {
                //Stores the flag, value pair into the HashMap once it's fully found
                if (currentFlag != null && currentValue.length() > 0) {
                    argsMap.put(currentFlag.toString(), currentValue.toString().trim());
                    currentValue.setLength(0);
                }

                //Extract the flag name (--type etc.)
                int flagStart = i;
                //Finding the flag name
                while (i < rawArgs.length() && rawArgs.charAt(i) != ' ') {
                    i++;
                }
                //Flag found, finds the flag substring in the rawArgs and concatenates to currentFlag
                currentFlag = new StringBuilder(rawArgs.substring(flagStart, i));
                i--; //Adjust for loop increment
                continue;
            }

            /*
            The flag is found, the code can now go to the input string after the flag, so add character/string to currentValue.
            You'll then get a matching <flag, string> pair
             */
            if (currentFlag != null) {
                currentValue.append(c); //Appends the input string into currentValue
            }
        }

        /*
        Save the last flag-string pair
        The last flag-string pair is found but not stored yet in the previous for-loop since it onyl saves when it reaches a space
        So need this code to assign the last pair
         */
        if (currentFlag != null && currentValue.length() > 0) {
            argsMap.put(currentFlag.toString(), currentValue.toString().trim());
        }
        return argsMap;
    }

    private void validateRequiredFields(Map<String, String> parsedArgs) throws RLADException {
        // Only validates the necessary fields except category and description
        String[] requiredFields = {"--type", "--amount", "--date"};

        for (String field : requiredFields) {
            //Checks if the flag field is empty
            if (!parsedArgs.containsKey(field) || parsedArgs.get(field).trim().isEmpty()) {
                //Show error messages
                throw new RLADException("Missing required field: " + field);
            }
        }

        //Optional: Validate that --type is either "debit" or "credit"
        String type = parsedArgs.get("--type");
        if (!type.equals("debit") && !type.equals("credit")) {
            throw new RLADException("Invalid --type. Must be either 'debit' or 'credit'");
        }
    }

    private double convertAmount(String amountStr) throws RLADException {
        try {
            return Double.parseDouble(amountStr);   //Code that may cause an exception
        } catch (NumberFormatException e) {
            throw new RLADException("Invalid amount format. Please enter a valid number (e.g., 15.50)");    //Code to execute if the exception is true
        }
    }

    private LocalDate convertDate(String dateStr) throws RLADException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new RLADException("Invalid date format. Please use yyyy-MM-dd (e.g., 2026-02-18)");
        }
    }

    @Override
    public void execute(TransactionManager transactions, Ui ui) {
        // TODO: Use a tokenizer or regex to extract --type, --amount, --category, --date, and --description.
        Map<String, String> parsedArgs = parseArguments(rawArgs);
        // TODO: Validate that mandatory fields (--type, --amount, --date) are present.
        validateRequiredFields(parsedArgs);
        // TODO: Convert the amount string to double and date string to LocalDate.
        double amount = convertAmount(parsedArgs.get("--amount"));
        LocalDate date = convertDate(parsedArgs.get("--date"));
        // TODO: Create a new Transaction object and add it via transactions.addTransaction().
        // TODO: Provide success feedback to the user via ui.showResult().
        ui.showResult("Validation passed! Required fields are present.");
    }

    @Override
    public boolean hasValidArgs() {
        // TODO: Check if rawArgs contains the required flags to prevent runtime RLADExceptions.
        return true;
    }
}