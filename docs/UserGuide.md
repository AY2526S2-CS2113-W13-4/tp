# RLAD User Guide

## Table of Contents
- [Introduction](#introduction)
- [Quick Start](#quick-start)
- [Commands](#commands)
  - [Adding a transaction: `add`](#adding-a-transaction-add)
  - [Listing transactions: `list`](#listing-transactions-list)
  - [Deleting a transaction: `delete`](#deleting-a-transaction-delete)
  - [Modifying a transaction: `modify`](#modifying-a-transaction-modify)
  - [Viewing a summary: `summarize`](#viewing-a-summary-summarize)
  - [Getting help: `help`](#getting-help-help)
  - [Exiting the app: `exit`](#exiting-the-app-exit)
- [UI Examples](#ui-examples)
- [FAQ](#faq)
- [Known Issues](#known-issues)
- [Command Summary](#command-summary)

## Introduction

Record Losses And Debt (RLAD) is a minimalist CLI finance tracker for users who want to manage their
spending without the overhead of spreadsheets or bloated apps. Track your income and expenses, sort and
filter transactions, and get quick summaries of where your money is going -- all from your terminal.

## Quick Start

1. Ensure that you have **Java 17** or above installed.
2. Download the latest `RLAD.jar` from the [Releases](https://github.com/AY2526S2-CS2113-T06-4/tp/releases) page.
3. Copy the file to the folder you want to use as the home folder for RLAD.
4. Open a command terminal, `cd` into the folder, and run:
   ```
   java -jar RLAD.jar
   ```
5. You should see the RLAD welcome screen:
   ```
   ╔════════════════════════════════════════════════╗
   ║       ██████╗  ██╗       █████╗  ██████╗       ║
   ║       ██╔══██╗ ██║      ██╔══██╗ ██╔══██╗      ║
   ║       ██████╔╝ ██║      ███████║ ██║  ██║      ║
   ║       ██╔══██╗ ██║      ██╔══██║ ██║  ██║      ║
   ║       ██║  ██║ ███████╗ ██║  ██║ ██████╔╝      ║
   ║       ╚═╝  ╚═╝ ╚══════╝ ╚═╝  ╚═╝ ╚═════╝       ║
   ║              Record Losses And Debt            ║
   ╚════════════════════════════════════════════════╝

   Hello and welcome to RLAD!
   Handle your financial life from one spot without the spreadsheet headaches
   ```
6. Type a command at the `>` prompt and press Enter. Refer to [Commands](#commands) below for details on each command.

## Commands

> **Notes about the command format:**
> - Words in `UPPER_CASE` are parameters to be supplied by the user.
>   e.g. in `add --type TYPE`, `TYPE` is a parameter: `add --type credit`.
> - Items in square brackets are optional.
>   e.g. `list [--sort FIELD]` can be used as `list` or `list --sort amount`.
> - Flags can be provided in any order.
>   e.g. `--type credit --amount 15.00` and `--amount 15.00 --type credit` are both valid.
> - Extra whitespace between flags is ignored.

### Adding a transaction: `add`

Adds a new credit (income) or debit (expense) entry.

**Format:**
```
add --type TYPE --amount AMOUNT --date DATE [--category CATEGORY] [--description DESCRIPTION]
```

| Flag | Required | Description |
|------|----------|-------------|
| `--type` | Yes | `credit` (income) or `debit` (expense) |
| `--amount` | Yes | Dollar amount (e.g. `15.00`) |
| `--date` | Yes | Date in `YYYY.MM.DD` format |
| `--category` | No | Category label (e.g. `food`, `transport`) |
| `--description` | No | Short description of the transaction |

> **Tip:** Always include a category and description -- it makes filtering and summarizing much more useful later.

**Example:**
```
> add --type credit --category food --amount 15.00 --date 2026.02.18 --description Hawker stall lunch set
```

> **Caution:** `add` is currently under development. The command is recognised but execution logic is not yet
> implemented.

### Listing transactions: `list`

Displays your recorded transactions. Supports sorting by amount or date.

**Format:**
```
list [--sort FIELD]
```

| Flag | Required | Description |
|------|----------|-------------|
| `--sort` | No | Sort by `amount` or `date` (ascending order) |

- `FIELD` must be either `amount` or `date`. Any other value will be rejected.
- If `--sort` is not provided, transactions are displayed in the order they were added.
- Sorting does not modify the stored data -- it only affects the display order.

**Examples:**

List all transactions:
```
> list
```

List transactions sorted by amount (lowest first):
```
> list --sort amount
```

List transactions sorted by date (earliest first):
```
> list --sort date
```

**Sample output:**
```
[a7b2] DEBIT | 2026-02-10 | $5.50 | transport | Bus fare
[f1c3] DEBIT | 2026-02-15 | $25.00 | food | Lunch
[e9d4] CREDIT | 2026-01-01 | $3000.00 | salary | Monthly salary
```

> **Tip:** Filtering by `--type`, `--category`, `--from`, and `--to` is planned for a future release.

### Deleting a transaction: `delete`

Removes a transaction from the records permanently using its hash ID.

**Format:**
```
delete --hashID HASH_ID
```

- `HASH_ID` is the 4-character identifier shown in square brackets when you run `list` (e.g. `a7b2`).

> **Caution:** This action is irreversible. Once deleted, the transaction cannot be recovered.

> **Caution:** `delete` is currently under development. The command is recognised but execution logic is not
> yet implemented.

**Example:**
```
> delete --hashID a7b2
```

### Modifying a transaction: `modify`

Updates specific fields of an existing entry via its hash ID. Only the fields you specify will be changed;
all other fields remain unchanged.

**Format:**
```
modify --hashID HASH_ID [--type TYPE] [--amount AMOUNT] [--date DATE] [--category CATEGORY] [--description DESCRIPTION]
```

- `--hashID` is required. All other flags are optional but at least one must be provided.

**Example:**
```
> modify --hashID a7b2 --amount 20.00 --description Fancy hawker lunch
```

> **Caution:** `modify` is currently under development. The command is recognised but execution logic is not
> yet implemented.

### Viewing a summary: `summarize`

Provides a statistical overview of your finances.

**Format:**
```
summarize [--by GROUPING]
```

| Flag | Required | Description |
|------|----------|-------------|
| `--by` | No | Group by `category`, `month`, or `type` |

**Example:**
```
> summarize --by category
```

> **Caution:** `summarize` is currently under development. The command is recognised but execution logic is
> not yet implemented.

### Getting help: `help`

Displays available commands and their usage. Use `help` on its own to see all commands, or specify a command
name to see its detailed manual.

**Format:**
```
help [COMMAND_NAME]
```

**Examples:**

View all available commands:
```
> help
```

View the manual for a specific command:
```
> help add
> help list
```

### Exiting the app: `exit`

Exits the application.

```
> exit
Thank you for abusing me!
 See you next time...
```

> **Tip:** Your data is not saved between sessions in this version. Persistent storage is planned for a
> future release.

## UI Examples

Here is what a typical session looks like:

```
▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
Hello and welcome to RLAD!
Handle your financial life from one spot without the spreadsheet headaches
Available actions:
  add       : Record a new transaction
  modify    : Edit an existing entry
  delete    : Remove an entry
  list      : View your transaction history with filters
  summarize : Get a high-level breakdown of your spending

Format:
	$action --option_0 $argument_0 ... --option_k $argument_k
Type 'help' for the full list or '$action help' for specific argument details.
▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
> list --sort amount
Your wallet is empty! Use 'add' to record a transaction.
▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
> exit
Thank you for abusing me!
 See you next time...
```

## FAQ

**Q**: How do I find the hash ID of a transaction?

**A**: Use the `list` command. Each transaction is displayed with its 4-character hash ID in square brackets
at the start, e.g. `[a7b2]`.

**Q**: Can I sort transactions in descending order?

**A**: Not yet. Currently, `--sort amount` and `--sort date` sort in ascending order only. Descending sort
may be added in a future release.

**Q**: What happens if I enter an invalid command?

**A**: RLAD will show an error message and display the list of available commands to guide you.

**Q**: Is my data saved when I close the app?

**A**: Not in the current version. Data persistence is planned for a future release.

## Known Issues

1. **No persistent storage** -- All transaction data is lost when the app exits. A file-based save/load
   system is planned.
2. **Hash ID collisions** -- The 4-character hash IDs have a small chance of collision. Collision detection
   and regeneration is not yet implemented.
3. **Commands under development** -- `add`, `delete`, `modify`, and `summarize` are recognised by the parser
   but their execution logic is not yet implemented. They will print placeholder messages.

## Command Summary

| Command | Format | Status |
|---------|--------|--------|
| **add** | `add --type TYPE --amount AMOUNT --date DATE [--category CAT] [--description DESC]` | Planned |
| **list** | `list [--sort amount\|date]` | Working |
| **delete** | `delete --hashID ID` | Planned |
| **modify** | `modify --hashID ID [--type TYPE] [--amount AMT] [--date DATE] ...` | Planned |
| **summarize** | `summarize [--by category\|month\|type]` | Planned |
| **help** | `help [COMMAND]` | Working |
| **exit** | `exit` | Working |
