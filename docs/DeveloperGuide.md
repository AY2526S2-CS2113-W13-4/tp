# Developer Guide

## Acknowledgements

- Architecture inspired by [AddressBook-Level2](https://github.com/se-edu/addressbook-level2)
- Java `Predicate` chaining pattern adapted from Java 8 Streams documentation

## Design & Implementation

### Architecture Overview

RLAD follows the **MVC (Model-View-Controller)** pattern combined with the **Command Design Pattern**.

The main loop in `RLAD.java` drives the application:
1. Read input via `Ui.readCommand()`
2. Parse input via `Parser.parse()`
3. Execute the returned `Command` object
4. Display results via `Ui`

### 4.2 DeleteCommand

**Classes involved:** `DeleteCommand`, `TransactionManager`, `BudgetManager`

**Sequence:**

```
Parser.parse("delete --hashID a7b2c3")
    |
    v
DeleteCommand(rawArgs) created
    |
    v
DeleteCommand.hasValidArgs()
    |  checks: rawArgs contains --hashID and a non-empty value
    v
DeleteCommand.execute(transactions, ui)
    |
    |-- parseHashId()                    -> "a7b2c3"
    |-- transactions.findTransaction("a7b2c3")
    |       -> Transaction (or null -> throw RLADException)
    |-- transactions.deleteTransaction("a7b2c3")
    |       -> transactions.remove(t)
    |       -> transMap.remove("a7b2c3")
    |       -> budgetManager.onTransactionDeleted(t)
    |       -> budgetManager.checkBudgetThresholds(month)
    |
    v
ui.showResult("Transaction deleted successfully! ...")
```

### 4.3 ModifyCommand

**Classes involved:** `ModifyCommand`, `TransactionManager`, `BudgetManager`, `Transaction`

**Sequence:**

```
Parser.parse("modify a7b2c3 --amount 20.00")
    |
    v
ModifyCommand(action, rawArgs) created
    |
    v
ModifyCommand.execute(transactions, ui)
    |
    |-- Parse rawArgs to extract ID and any update fields
    |-- transactions.findTransaction(id)
    |       -> Transaction old (or error if not found)
    |-- Construct updated Transaction with merged fields
    |       (unchanged fields copied from old)
    |-- transactions.updateTransaction(id, updated)
    |       -> transMap.put(id, updated)
    |       -> transactions.set(indexOf(old), updated)
    |       -> budgetManager.onTransactionUpdated(old, updated)
    |       -> budgetManager.checkBudgetThresholds(month)
    |
    v
ui.showResult("Transaction updated successfully!")
```

**Design notes:**
- The updated Transaction keeps the same HashID as the original (`setHashId` is called with the original ID before calling `updateTransaction`).
- Both the ArrayList position and the HashMap entry are updated atomically within `updateTransaction`.

### 4.4 ListCommand

**Classes involved:** `ListCommand`, `FilterCommand`, `TransactionSorter`, `TransactionManager`

**Sequence:**

```
Parser.parse("list --type debit --sort amount desc")
    |
    v
ListCommand(rawArgs) created
    |
    v
ListCommand.execute(transactions, ui)
    |
    |-- FilterCommand.parseFlags(rawArgs)
    |       -> Map{ "type"->"debit", "sort"->"amount desc" }
    |
    |-- Validate --sort value
    |       sortBy = "amount", sortDirection = "desc"
    |
    |-- FilterCommand.buildPredicate(rawArgs)
    |       -> Predicate<Transaction>
    |          (type == "debit")
    |
    |-- transactions.getTransactions().stream()
    |       .filter(predicate)
    |       .collect(toList())
    |       -> List<Transaction> results
    |
    |-- TransactionSorter.sort(results, "amount", "desc")
    |       -> sorted List<Transaction>
    |
    v
Print formatted table via ui.showResult()
```

### 4.6 SummarizeCommand

**Classes involved:** `SummarizeCommand`, `FilterCommand`

`SummarizeCommand` reuses `FilterCommand.buildPredicate()` identically to `ListCommand`, then aggregates filtered results:

```
summarize --date-from 2026-01-01 --date-to 2026-03-31
    |
    v
SummarizeCommand(rawArgs)
    |
    |-- FilterCommand.buildPredicate(rawArgs) -> Predicate
    |-- transactions.getTransactions().stream().filter(p).collect(toList())
    |
    |-- For each transaction:
    |       if credit: totalCredit += amount
    |       if debit:  totalDebit  += amount
    |       categoryTotals.merge(category, amount, BigDecimal::add)
    |
    |-- net = totalCredit - totalDebit
    |
    v
ui.showResult(formatted summary)
```

**Design notes:**
- Uses `BigDecimal` for all aggregation to avoid floating-point display errors (e.g. `0.10 + 0.20 = 0.30` not `0.30000000000000004`).
- Category totals are grouped using `Map.merge()` with `BigDecimal::add`.

## Product scope

### Target user profile

NUS students or young adults managing personal finances from the command line who prefer a lightweight tool over GUI apps or spreadsheets.

### Value proposition

RLAD lets users record, filter, sort, and summarize financial transactions entirely from the terminal — faster than GUI apps for keyboard-driven users.

## User Stories

| Version | As a ...    | I want to ...                          | So that I can ...                                      |
|---------|-------------|----------------------------------------|--------------------------------------------------------|
| v1.0    | new user    | see usage instructions                 | refer to them when I forget how to use the application |
| v1.0    | user        | add a transaction                      | record my income and expenses                          |
| v1.0    | user        | delete a transaction by ID             | remove incorrect entries                               |
| v2.0    | user        | modify a transaction's fields          | correct mistakes without deleting and re-adding        |
| v2.0    | user        | search transactions by keyword         | quickly find a specific entry                          |
| v2.0    | user        | summarize my transactions              | get an overview of my spending and income              |

## Non-Functional Requirements

- Should work on Windows, macOS, and Linux with Java 17 installed.
- All commands should respond within 1 second for up to 1000 transactions.
- Data is stored in-memory; no persistence between sessions (future enhancement).

## Glossary

* **Transaction** — A financial record with type, amount, date, optional category and description.
* **HashID** — A 6-character unique identifier auto-generated for each transaction (e.g. `a7b2c3`).
* **Credit** — An income transaction (money in).
* **Debit** — An expense transaction (money out).
* **Predicate** — A filter condition that returns true/false for a given transaction.

## Instructions for Manual Testing

1. Run `java -jar duke.jar`
2. Add a transaction: `add --type credit --amount 3000.00 --date 2026-01-15 --category salary`
3. Add an expense: `add --type debit --amount 50.00 --date 2026-01-16 --category food --description "Hawker lunch"`
4. List all: `list`
5. Filter by type: `list --type credit`
6. Sort by amount: `list --sort amount`
7. Search by keyword: `search --keyword lunch`
8. Modify a transaction (use HashID from list output): `modify a7b2c3 --amount 25.00 --category dining`
9. Summarize all: `summarize`
10. Summarize filtered: `summarize --type debit`
11. Delete by ID: `delete --hashID a7b2c3`
12. Exit: `exit`
