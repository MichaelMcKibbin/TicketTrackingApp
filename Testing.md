# TESTING

This document describes the testing strategy for the **TicketTracking** application, including:

- How to run automated unit tests
- How to perform structured **manual GUI testing** of the JavaFX interface

---

## 1. Test Types

### 1.1 Automated Tests (Unit Tests)

Automated tests focus on **non-UI logic**, including:

- `TicketService` – ticket persistence, validation, and ID generation
- `Ticket` – comment handling and default values
- `UserService` – default users, roles, and current user handling
- (Optional) Filtering/utility logic if extracted into helper classes

These tests are run with **JUnit 5** using Maven.

### 1.2 Manual Tests (GUI Testing)

Manual GUI tests verify that the user interface:

- Renders correctly
- Responds correctly to user actions
- Correctly wires UI controls to underlying logic

This includes form validation, table updates, dialogs, filters, and interactions with `tickets.json`.

---

## 2. Test Environment

### 2.1 Software

- **JDK**: 17 or 21 (recommended)
- **JavaFX**: 21 (via Maven dependencies)
- **Build Tool**: Maven
- **IDE**: IntelliJ IDEA (recommended but not required)

### 2.2 Data Files

- `tickets.json` – main application data file (in working directory)
- `sample_tickets.json` – example data file for seeding tests or demos

For some tests, a temporary directory and a temporary `tickets.json` are used (via JUnit’s `@TempDir`) to avoid interfering with real user data.

---

## 3. Running Automated Tests

### 3.1 Using Maven

From the project root:

```bash
mvn test
```

This will:

- Compile main and test sources
- Run all JUnit tests under `src/test/java`
- Print a summary of passed/failed tests

### 3.2 Using IntelliJ IDEA

1. Right-click the `test` directory or an individual test class (e.g. `TicketServiceTest`).
2. Choose **Run 'Tests in ...'**.
3. View results in the **Run** or **Test Results** window.

---

## 4. Unit Test Coverage

### 4.1 TicketServiceTest

**Class under test:** `TicketService`

**Main behaviours tested:**

- Loading tickets from an empty/non-existing file results in an empty list.
- Saving a ticket:
    - Generates a new ID if none is set.
    - Sets `createdAt` (and optionally `updatedAt`).
- ID generation:
    - IDs are numeric.
    - New tickets get incrementally higher IDs.
- Updating a ticket:
    - Preserves the original ID.
    - Updates fields.
    - Sets `updatedAt`.
- Deleting a ticket:
    - Removes it from the internal list.
- Validation:
    - Saving a ticket with no title throws `IllegalArgumentException`.
    - Saving a ticket with no status throws `IllegalArgumentException`.
    - Saving a ticket with no priority throws `IllegalArgumentException`.

**Notes:**
Tests typically use `@TempDir` to set a temporary `user.dir` so that `tickets.json` is created in a safe, isolated location during testing.

---

## 4.2 TicketTest

**Class under test:** `Ticket`

**Main behaviours tested:**

- **Default constructor:**
    - Sets sensible defaults for title, description, priority, comments list, etc.
- **Parameterized constructor:**
    - Sets title, description, priority, status, and timestamps.
- **Comments:**
    - `getComments()` returns a non-null list even if the internal list is null.
    - `addComment()`:
        - Adds the comment to the ticket’s comment list.
        - Sets the comment’s `ticketId` to match the ticket’s ID.

---

## 4.3 UserServiceTest

**Class under test:** `UserService`

**Main behaviours tested:**

- Default users:
    - Correct number of default users.
    - Includes `admin`, `support1`, `support2`, `customer1`.
- Current user:
    - Defaults to `support1` with `SUPPORT_STAFF` role.
- Usernames:
    - `getUsernames()` returns all usernames for display/selection.
- Lookup:
    - `findUserByUsername()` returns the correct `User` for existing usernames.
    - Returns `null` for unknown usernames.
- Setting current user:
    - `setCurrentUser()` successfully changes the current user and role.

---

## 5. Manual GUI Testing

Manual GUI testing is used to verify the JavaFX interface and user interactions, including layout, dialogs, and visual feedback.

**Tip:** Before starting manual tests, ensure you are using a stable Java/JavaFX combo (e.g. JDK 21 + JavaFX 21) and that the app builds and runs without warnings or errors.

---

## 5.1 Checklist Overview

The following subsections provide a structured manual test checklist:

- Startup behaviour
- Creating tickets
- Editing tickets
- Deleting tickets
- Search functionality
- Filtering by status/priority
- Menu actions (File, Help, etc.)
- Persistence across restarts
- Resize behaviour
- Error handling

You can execute these tests after each major change or before a release.

---

## 5.2 Test Cases

### 5.2.1 Startup Behaviour

**Steps:**

1. Launch the application (run `TicketApplication`).
2. Observe the main window.

**Expected Results:**

- Application starts without exceptions.
- Ticket table is visible.
- If `tickets.json` exists, tickets are loaded and displayed.
- If `tickets.json` does not exist, the table is empty.
- Status bar shows something like **“All changes saved.”**

---

### 5.2.2 Creating Tickets

**Steps (validation):**

1. Click **New Ticket**.
2. Leave all fields empty and click **Save**.

**Expected:**

- An error dialog appears indicating missing required fields.
- Ticket is **not** added to the table.

**Steps (successful creation):**

1. Open **New Ticket** again.
2. Fill in:
    - Title: `Login issue`
    - Description: `User cannot log in.`
    - Status: `NEW`
    - Priority: `HIGH`
    - Assigned To: `support1`
3. Click **Save**.

**Expected:**

- Dialog closes.
- New ticket appears in the table.
- A numeric ID is assigned.
- Priority column uses the correct colour for HIGH priority.
- Status bar updates.

---

### 5.2.3 Editing Tickets

**Steps:**

1. Double-click an existing ticket row.
2. In the Edit dialog:
    - Change Status → `IN_PROGRESS`
    - Change Priority → `HIGH`
    - Update the description
3. Click **Save**.

**Expected:**

- Dialog closes.
- Table reflects the updated values.
- `updatedAt` timestamp updates.

**Steps (Comments):**

1. Reopen the ticket.
2. Add a comment (e.g., `Investigating issue`).
3. Click **Add Comment**.

**Expected:**

- Comment appears in the list.
- Correct author and timestamp.

---

### 5.2.4 Deleting Tickets

**Steps:**

1. Select a ticket.
2. Click **Delete**.
3. Confirm when prompted.

**Expected:**

- Ticket is removed.
- Saved JSON file reflects removal.

---

### 5.2.5 Search Functionality

**Steps:**

1. Type part of a title (e.g., `login`) into the search field.

**Expected:**

- Only matching tickets remain visible.
- Search is case-insensitive.

**Steps:**

2. Clear search field.

**Expected:**

- Full list restored.

---

### 5.2.6 Filtering (Status & Priority)

**Status filter:**

- Select `NEW` → only NEW tickets displayed.
- Select `IN_PROGRESS` → only in-progress tickets.
- Select `All` → full list.

**Priority filter:**

- Select `HIGH` → high priority tickets only.
- Select `LOW` → low priority only.
- Select `All` → full list.

---

### 5.2.7 Menu Actions

- **File → Save**
    - Writes `tickets.json`
    - Status bar confirms

- **File → Load**
    - Reloads tickets from file

- **File → Exit**
    - Closes the app cleanly

- **Help → About**
    - About dialog appears

---

### 5.2.8 Persistence Across Restarts

**Steps:**

1. Create or modify tickets.
2. Save.
3. Restart the app.

**Expected:**

- All changes preserved.

---

### 5.2.9 Resize Behaviour

**Steps:**

1. Shrink main window.
2. Enlarge main window.

**Expected:**

- Table resizes correctly.
- No overlapping or clipping.

---

### 5.2.10 Error Handling

- Missing file → app starts with empty list.
- Corrupted JSON → meaningful error shown.
- No ticket selected for Edit/Delete → alert shown, no crash.

---

## 6. Regression Testing

Before a release:

1. Run all tests:

```bash
mvn test
```

2. Perform GUI:
    - Startup
    - Create/Edit/Delete
    - Search/Filter
    - Save/Load
    - Resize behaviour

3. Fix any issues found.

---

## 7. Future Testing Improvements

- Add more granular validation tests.
- Extract filtering logic to test separately.
- Optional: Add TestFX (works best on JDK 17).
- Add GitHub Actions CI to run `mvn test` automatically.

