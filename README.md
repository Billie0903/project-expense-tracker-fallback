# 🚀 Project Expense Tracker V2

A clean, robust Android application built for managing projects and their associated expenses with local persistence and cloud backup capabilities.

## 🛠 Tech Stack
- **Language**: Java 8
- **UI Framework**: Android Material Design (Material Components)
- **Local Database**: SQLite (via `DbHelper`)
- **Cloud Database**: Google Firebase Firestore (via `FirebaseHelper`)
- **Architecture**: Simple Activity-based model with a centralized data handler (`Models.java`).

---

## 📱 Application Modules & Flow

### 1. Project List (`ProjectListActivity`)
- **Function**: The main landing page.
- **Key Features**:
    - **Live Search**: Use the search bar at the top to filter projects by name in real-time.
    - **Floating Action Button (FAB)**: Tap the `+` button to create a new project.
    - **Project Cards**: Displays Name, Project Code, Budget (GBP), and Status.
    - **Cloud Backup**: Tap the "Cloud" icon/menu in the toolbar to sync all your local data to Firebase.

### 2. Add/Edit Project (`AddEditProjectActivity`)
- **Function**: Detailed form for project management.
- **Demo Highlights (Multiple Input Types)**:
    - **Text Fields**: Name, Code, and Budget.
    - **Dropdown (Spinner)**: Manage status (Active, On Hold, Completed).
    - **Radio Buttons**: Set Priority (Low, Med, High).
    - **Toggle (Switch)**: Mark as "Urgent".
    - **Checkbox**: Toggle "Verified" status.
- **Context Actions**: From here, you can also jump directly to the **Manage Expenses** screen for the selected project.

### 3. Expense Management (`ExpenseListActivity`)
- **Function**: View all expenses specifically linked to the active project.
- **Key Features**:
    - **Overview**: Displays date, type, claimant, and amount in GBP.
    - **Delete**: Swipe or tap the delete icon to remove records.
    - **Navigation**: Full toolbar support with a working Back button to return to project details.

### 4. Add/Edit Expense (`AddEditExpenseActivity`)
- **Function**: Quick entry for expense records.
- **Includes**: Date picker, Currency selector, and Claimant details.

---

## 💾 Data Handling logic

### Local (SQLite)
- Handled by `DbHelper.java`.
- Implements full CRUD (Create, Read, Update, Delete) for both Projects and Expenses.
- Projects and Expenses are linked via a `PROJECT_ID` foreign key.

### Cloud (Firebase)
- Handled by `FirebaseHelper.java`.
- Aggregates all local data into a batch upload.
- **Structure**: Saves to a `projects` root collection with an `expenses` sub-collection for each document.

---

## 💡 Demo Cheat Sheet
1.  **Search**: Type a few letters and watch the list filter.
2.  **Add Project**: Walk through the different input types (Radio, Toggle, Checkbox) – this shows UI complexity.
3.  **Manage Expenses**: Go into a project, add a "Travel" expense, then delete it.
4.  **The "Wow" Factor**: Trigger the **Cloud Backup** from the menu. Mention that it uses `WriteBatch` for efficiency and atomicity.
5.  **Navigation**: Showcase the smooth back-button transitions between lists and forms.

Good luck with the demo! 🍀
