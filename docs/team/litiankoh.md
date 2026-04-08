# Koh Li Tian - Project Portfolio Page

## Overview

**Project:** Record Losses And Debt (RLAD)

RLAD is a minimalist **Command-Line Interface (CLI) finance tracker** built for power users who prefer the speed and 
efficiency of a terminal. Unlike bloated apps or complex spreadsheets, RLAD allows users to track income/expenses, 
set budget goals, and get instant summaries with simple, intuitive commands. 
My primary role was to architect and implement the **budgeting feature**, which forms the core of RLAD's financial planning capabilities.

## Summary of Contributions

### Code contributed
[RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=litiankoh&breakdown=true)

### Enhancements implemented

Here is a summary of my key enhancements to RLAD:

| Enhancement | Description | Key Technical Highlights |
| :--- | :--- | :--- |
| **AddCommand & Intuitive Argument Parsing** | Built the foundational command for adding transactions with a **position-based** argument parser that eliminates the need for flags. | - Implemented position-based parsing: `type`, `amount`, `date`, `category`, `description` in natural order.<br>- Added quote handling for multi-word descriptions.<br>- Provided clear validation errors for missing or invalid inputs.<br>- **Result:** Users can now type `add debit 15.50 2026-03-05 food` instead of the verbose `add --type debit --amount 15.50 --date 2026-03-05 --category food` |
| **Budget Management System** | Implemented a full suite of commands (`set`, `view`, `edit`, `delete`) for users to create and manage monthly budget goals across 12 predefined categories with **simplified syntax**. | - Designed `MonthlyBudget` and `BudgetManager` classes to handle budget storage and calculations.<br>- Integrated with existing `TransactionManager` to calculate real-time spending against budgets.<br>- Implemented robust input validation for month formats and category codes.<br>- **Simplified syntax:** `budget set 2026-03 1 500` instead of `budget set --month 2026-03 --category 1 --amount 500` |
| **Progressive Budget Notifications** | Created a real-time notification system that alerts users at 80%, 90%, and 100% of their budget limits. | - Used a `Map<String, Set<Integer>>` to track notifications per `(month, category)` and prevent duplicate alerts.<br>- Implemented conditional logic to show **positive/encouraging messages for Savings** and **warnings for all other categories**, enhancing user experience. |

### Contributions to User Experience Design

I led the effort to **redesign the entire CLI syntax** from flag-based to position-based commands, making RLAD significantly more user-friendly:

**Before (flag-based):**
```
add --type debit --amount 15.50 --date 2026-03-05 --category food --description "Chicken rice"
```
**After (position-based):**
```
add debit 15.50 2026-03-05 food "Chicken rice"
```
### Contributions to the User Guide (UG)

I authored and maintained the documentation for the `budget` feature, ensuring it was clear, comprehensive, and user-friendly. My contributions include:

- **Added UG for BudgetManager:** Wrote detailed sections for all budget commands (`set`, `view`, `edit`, `delete`) with examples, input formats, and expected outputs.
- **Added UG for Budget Notification Logic:** Documented the progressive notification system (80%, 90%, 100% thresholds), clearly differentiating between Savings (positive) and other categories (warnings).

I completely rewrote the UG to reflect the new intuitive syntax, including:

- **Simplified command formats** showing position-based parameters instead of flags
- **Quick reference card** for fast lookup of common commands
- **Natural language examples** that mirror how users think about transactions
- **Clear documentation** of the new filter syntax (`type:debit`, `cat:food`, `from:2026-03-01`)

### Contributions to the Developer Guide (DG)
I made substantial contributions to the Developer Guide, documenting the architecture and implementation of key features to help the team understand and maintain the codebase.\
Added all the UMLs shown in the DG as well.

| Section | Contribution                                                                                                                                                        |
|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|3. Design | Authored the Architecture Overview, Component Descriptions, and all ASCII UML class diagrams showing relationships between core components.                         |
| 4.1 Add Transaction | Documented the complete implementation sequence, including parser logic, validation steps, and integration with TransactionManager.                                 |
| 4.7 Budget Management | Wrote comprehensive documentation for the entire budget feature, including sequence diagrams, class interactions, and progress tracking logic.                      |
| 4.8 Storage Management | Documented the CSV export/import and clear data feature, including sequence diagrams, parser changes, and required methods for TransactionManager and BudgetManager. |
| 6. User Stories | Authored the complete user stories table covering v1.0 and v2.0 features.                                                                                           |
| 7. Non-Functional Requirements | Defined all NFRs including performance, reliability, portability, data integrity, maintainability, and usability.                                                   |
| 8. Glossary | Created the glossary of key terms used throughout the DG.|
| 9. Instructions for Manual Testing | Authored the complete manual testing guide covering all 29 test cases across add, list, sort, summarize, modify, delete, budget, export, import, clear, and exit commands. |

### Contributions to Team-Based Tasks

- **Essential Code Foundation:** Created the `AddCommand` with position-based parsing, which served as a template for all other commands, enabling the team to quickly implement consistent, user-friendly commands.
- **Syntax Redesign Leadership:** Initiated and led the team discussion to move from flag-based to position-based syntax, resulting in unanimous agreement and a much more intuitive product.
- **Code Review:** Actively reviewed team members' PRs to ensure they followed the new position-based pattern consistently.

### Reviewing Contributions (Mentoring)

I actively reviewed team members' pull requests to ensure code quality, adherence to standards, and functional correctness.

- [PR #18: ListCommand Implementation](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/18)
- [PR #22: ModifyCommand Implementation](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/22)
- [PR #39: BudgetManager Persistence](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/39)
- [PR #40: BudgetCommand Refactoring](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/40)
- [PR #45: TransactionManager Enhancements](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/45)
- [PR #52: Implement Budget Yearly Summary Command](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/52#pullrequestreview-4051302843)
- [PR #53: Enchanced FilterCommand](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/53)
- [PR #59: Fix Description Parsing Bug](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/59#pullrequestreview-4054023028)

### Contributions beyond the project team

I took a proactive leadership role by managing the project's workflow and ensuring all team members had clear, well-defined tasks.

- **Project Management & Task Delegation:** Proactively created and assigned detailed GitHub issues for the entire team, ensuring a fair distribution of work and that everyone had an opportunity to contribute meaningfully to different parts of the project.

**Issues created:**

- [Issue #20: Implement SetBudgetCommand (Logic)](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/20)
- [Issue #26: Implement Budget Persistence](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/26)
- [Issue #46: Budget Goals - Progressive Notifications](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/46)
- [Issue #47: Yearly Budget Summary with Trend Graph](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/47)
- [Issue #49: Search and Edit Specific Transaction](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/49)
- [Issue #50: Update UG and DG](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/50)
- [Issue #55: Implement Storage Management System (CSV Export/Import & Data Clear)](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/55)