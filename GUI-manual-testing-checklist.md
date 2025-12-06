# Manual GUI Testing Checklist

This is a realistic checklist for manually testing the **TicketTracking** JavaFX application before each release.

---

## 1. Startup Behaviour

- App launches without errors
- Main table loads tickets (or shows empty list if `tickets.json` is missing)
- **“All changes saved”** shows in the footer
- Window size is reasonable

---

## 2. Creating Tickets

- Click **New Ticket** → dialog appears
- Required fields enforced:
    - Empty **title** → error dialog
    - Empty **status** → error dialog
    - Empty **priority** → error dialog
- Successful save adds ticket to table
- ID generated correctly
- Priority column shows correct colour
- **“All changes saved”** updates after save

---

## 3. Editing Tickets

- Double-click row → Edit dialog opens
- Status dropdown updates ticket
- Priority dropdown updates
- Description updates
- AssignedTo updates

### Comments
- Add comment → appears immediately
- Timestamp correct
- New entry appended at end

---

## 4. Deleting Tickets

- Select ticket → **Delete** → confirmation dialog
- Ticket disappears from table
- JSON file updates after Save

---

## 5. Search Bar

Type sample text and verify:

- Ticket ID → filters correctly
- Partial title → filters
- Status text → filters
- Lowercase search matches uppercase title (case-insensitive)
- Clear search → full list restored

---

## 6. Filtering Controls

### Status Filter
- `NEW` → only NEW tickets appear
- `IN_PROGRESS` → correct filtering
- `All` → full list restored

### Priority Filter
- `LOW` → only low-priority tickets appear
- `HIGH` → only high-priority tickets appear
- `All` → full list restored

---

## 7. Menu Bar

- **File → Save**: writes JSON
- **File → Load**: reloads tickets
- **Help → About**: dialog appears correctly
- **File → Exit**: closes app

---

## 8. Persistence

After saving:

- Quit and restart the app
- All tickets load as expected
- Comments load correctly
- Edited values remain

---

## 9. Resize Behaviour

- Make window small → layout should not collapse
- Make window large → table expands properly
- Dialog windows maintain layout

---

## 10. Error Handling

Simulate common error scenarios:

- Delete `tickets.json` → App creates a new file on next Save
- Corrupted `tickets.json` → App shows a meaningful error
- Attempt to open Edit/Delete with no ticket selected → safe fail (alert dialog)  

