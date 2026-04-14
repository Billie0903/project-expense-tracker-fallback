package com.example.projectexpensetrackerv2; // Defines the package for this file

// Import necessary Android and Java tools
import android.content.ContentValues; // Used to pack data for database storage
import android.content.Context;      // Provides context about the app's environment
import android.database.Cursor;       // Used to read results from a database query
import android.database.sqlite.SQLiteDatabase; // The tool for interacting with the local database
import android.database.sqlite.SQLiteOpenHelper; // A helper class to manage database creation and versions
import java.util.ArrayList;         // A list that can change size (dynamic array)
import java.util.List;              // The basic interface for all types of lists

/**
 * DATABASE HELPER: This file handles all SQLite operations (saving, loading, updating, deleting).
 * It acts as the bridge between the app's code and the phone's internal storage.
 */
public class DbHelper extends SQLiteOpenHelper {

    // The name of the database file stored on the phone
    private static final String DB_NAME = "DemoTracker.db";
    // The version of the database. Increase this if you add new columns to force an update.
    private static final int DB_VERSION = 3; 

    // --- TABLE & COLUMN NAMES FOR PROJECTS ---
    public static final String TABLE_P = "projects";       // Name of the project table
    public static final String P_ID = "id";               // Column name for Project ID
    public static final String P_CODE = "code";           // Column name for Project Code
    public static final String P_NAME = "name";           // Column name for Project Name
    public static final String P_DESC = "description";    // Column name for Project Description
    public static final String P_START = "start_date";    // Column name for Project Start Date
    public static final String P_END = "end_date";        // Column name for Project End Date
    public static final String P_MNGR = "manager";        // Column name for Project Manager
    public static final String P_STATUS = "status";       // Column name for Project Status
    public static final String P_BUDGET = "budget";       // Column name for Project Budget
    public static final String P_PRIORITY = "priority";   // Column name for Priority (High/Medium/Low)
    public static final String P_URGENT = "is_urgent";     // Column name for Urgency (Toggle)
    public static final String P_CHECK = "is_checked";     // Column name for Checkbox state

    // --- TABLE & COLUMN NAMES FOR EXPENSES ---
    public static final String TABLE_E = "expenses";      // Name of the expense table
    public static final String E_ID = "id";               // Column name for Expense ID
    public static final String E_PID = "project_id";      // Column for linking the expense to a project
    public static final String E_DATE = "date";           // Column for Expense Date
    public static final String E_AMT = "amount";          // Column for Expense Amount
    public static final String E_CURR = "currency";       // Column for Expense Currency (USD, etc)
    public static final String E_TYPE = "type";           // Column for Expense Type (Food, Travel)
    public static final String E_METH = "method";         // Column for Payment Method (Cash, Card)
    public static final String E_CLMT = "claimant";       // Column for who spent the money
    public static final String E_STAT = "status";         // Column for Payment Status
    public static final String E_DESC = "description";    // Column for the Expense Notes
    public static final String E_LOC = "location";        // Column for where the money was spent

    // Constructor: Sets up the database helper with the database name and version
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // This runs the first time the app is installed to create the tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Project Table SQL Command
        db.execSQL("CREATE TABLE " + TABLE_P + " (" +
                P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID automatically goes up 1, 2, 3...
                P_CODE + " TEXT, " + P_NAME + " TEXT, " + P_DESC + " TEXT, " +
                P_START + " TEXT, " + P_END + " TEXT, " + P_MNGR + " TEXT, " +
                P_STATUS + " TEXT, " + P_BUDGET + " REAL, " + // REAL is for decimal numbers
                P_PRIORITY + " TEXT, " + P_URGENT + " INTEGER, " + P_CHECK + " INTEGER)"); // SQLite uses INTEGER (0 or 1) for booleans

        // Create Expense Table SQL Command
        db.execSQL("CREATE TABLE " + TABLE_E + " (" +
                E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                E_PID + " INTEGER, " + E_DATE + " TEXT, " + E_AMT + " REAL, " +
                E_CURR + " TEXT, " + E_TYPE + " TEXT, " + E_METH + " TEXT, " +
                E_CLMT + " TEXT, " + E_STAT + " TEXT, " + E_DESC + " TEXT, " +
                E_LOC + " TEXT)");
    }

    // This runs if the DB_VERSION is increased (usually to add new columns)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_P); // Delete old Project table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_E); // Delete old Expense table
        onCreate(db); // Create fresh tables with the new structure
    }

    // --- PROJECT CRUD (Create, Read, Update, Delete) ---

    // Adds a new project to the database
    public long addProject(Models.Project p) {
        SQLiteDatabase db = getWritableDatabase(); // Opens database for writing
        ContentValues cv = new ContentValues();   // Helps pack data together
        cv.put(P_CODE, p.getProjectCode());       // Add code to package
        cv.put(P_NAME, p.getName());               // Add name
        cv.put(P_DESC, p.getDescription());       // Add description
        cv.put(P_START, p.getStartDate());         // Add start date
        cv.put(P_END, p.getEndDate());             // Add end date
        cv.put(P_MNGR, p.getManager());           // Add manager
        cv.put(P_STATUS, p.getStatus());           // Add status
        cv.put(P_BUDGET, p.getBudget());           // Add budget
        cv.put(P_PRIORITY, p.getPriority());       // Add priority
        cv.put(P_URGENT, p.isUrgent() ? 1 : 0);   // Store true as 1, false as 0
        cv.put(P_CHECK, p.isChecked() ? 1 : 0);    // Store true as 1, false as 0
        return db.insert(TABLE_P, null, cv);       // Send package to database and save
    }

    // Gets a list of all projects stored in the database
    public List<Models.Project> getAllProjects() {
        List<Models.Project> list = new ArrayList<>(); // Empty list to hold projects
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_P, null); // Run "Read All" command
        if (c.moveToFirst()) { // Start at the first result
            do {
                // Read each column and create a Project object from it
                list.add(new Models.Project(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getDouble(8),
                        c.getString(9), c.getInt(10) == 1, c.getInt(11) == 1));
            } while (c.moveToNext()); // Move to the next project until finished
        }
        c.close(); // Close the reader tool
        return list; // Return the full list to the app
    }

    // Finds a specific project using its unique ID number
    public Models.Project getProjectById(int id) {
        // Query the table for a project where the ID matches
        Cursor c = getReadableDatabase().query(TABLE_P, null, P_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) { // If something was found...
            // Create a Project object from the database data
            Models.Project p = new Models.Project(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getDouble(8),
                    c.getString(9), c.getInt(10) == 1, c.getInt(11) == 1);
            c.close(); // Close reader
            return p;   // Return project
        }
        return null; // Return nothing if project was not found
    }

    // Updates an existing project's data
    public int updateProject(Models.Project p) {
        SQLiteDatabase db = getWritableDatabase(); // Open for writing
        ContentValues cv = new ContentValues();   // Pack the updated data
        cv.put(P_CODE, p.getProjectCode());
        cv.put(P_NAME, p.getName());
        cv.put(P_DESC, p.getDescription());
        cv.put(P_START, p.getStartDate());
        cv.put(P_END, p.getEndDate());
        cv.put(P_MNGR, p.getManager());
        cv.put(P_STATUS, p.getStatus());
        cv.put(P_BUDGET, p.getBudget());
        cv.put(P_PRIORITY, p.getPriority());
        cv.put(P_URGENT, p.isUrgent() ? 1 : 0);
        cv.put(P_CHECK, p.isChecked() ? 1 : 0);
        // Save the update for the project that matches the specific ID
        return db.update(TABLE_P, cv, P_ID + "=?", new String[]{String.valueOf(p.getId())});
    }

    // Deletes a project and all its associated expenses
    public void deleteProject(int id) {
        // Delete the project from the project table
        getWritableDatabase().delete(TABLE_P, P_ID + "=?", new String[]{String.valueOf(id)});
        // Delete any expenses that belonged to that project (cleanup)
        getWritableDatabase().delete(TABLE_E, E_PID + "=?", new String[]{String.valueOf(id)});
    }

    // --- EXPENSE CRUD ---

    // Adds a new expense for a project
    public long addExpense(Models.Expense e) {
        SQLiteDatabase db = getWritableDatabase(); // Opening for writing
        ContentValues cv = new ContentValues();   // Creating data package
        cv.put(E_PID, e.getProjectId());          // Link to the parent project
        cv.put(E_DATE, e.getDate());
        cv.put(E_AMT, e.getAmount());
        cv.put(E_CURR, e.getCurrency());
        cv.put(E_TYPE, e.getType());
        cv.put(E_METH, e.getPaymentMethod());
        cv.put(E_CLMT, e.getClaimant());
        cv.put(E_STAT, e.getPaymentStatus());
        cv.put(E_DESC, e.getDescription());
        cv.put(E_LOC, e.getLocation());
        return db.insert(TABLE_E, null, cv);       // Save to database
    }

    // Gets all expenses that belong to a specific project
    public List<Models.Expense> getExpensesForProject(int pid) {
        List<Models.Expense> list = new ArrayList<>();
        // Query the expense table for items with a matching project ID (pid)
        Cursor c = getReadableDatabase().query(TABLE_E, null, E_PID + "=?", new String[]{String.valueOf(pid)}, null, null, null);
        if (c.moveToFirst()) {
            do {
                // Create Expense objects from the database rows
                list.add(new Models.Expense(c.getInt(0), c.getInt(1), c.getString(2), c.getDouble(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                        c.getString(9), c.getString(10)));
            } while (c.moveToNext());
        }
        c.close();
        return list; // Return the list of expenses for that specific project
    }

    // Gets a single expense using its ID
    public Models.Expense getExpenseById(int id) {
        Cursor c = getReadableDatabase().query(TABLE_E, null, E_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Models.Expense e = new Models.Expense(c.getInt(0), c.getInt(1), c.getString(2), c.getDouble(3),
                    c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                    c.getString(9), c.getString(10));
            c.close();
            return e;
        }
        return null;
    }

    // Updates an existing expense in the database
    public int updateExpense(Models.Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(E_DATE, e.getDate());
        cv.put(E_AMT, e.getAmount());
        cv.put(E_CURR, e.getCurrency());
        cv.put(E_TYPE, e.getType());
        cv.put(E_METH, e.getPaymentMethod());
        cv.put(E_CLMT, e.getClaimant());
        cv.put(E_STAT, e.getPaymentStatus());
        cv.put(E_DESC, e.getDescription());
        cv.put(E_LOC, e.getLocation());
        return db.update(TABLE_E, cv, E_ID + "=?", new String[]{String.valueOf(e.getId())});
    }

    // Deletes an expense from the database
    public void deleteExpense(int id) {
        getWritableDatabase().delete(TABLE_E, E_ID + "=?", new String[]{String.valueOf(id)});
    }

    // --- SEARCH FUNCTIONALITY ---

    // Searches for projects where the name or code matches what the user typed
    public List<Models.Project> searchProjects(String query) {
        List<Models.Project> list = new ArrayList<>();
        // SQL command to find matches using "LIKE" (meaning "contains")
        String sql = "SELECT * FROM " + TABLE_P + " WHERE " + P_NAME + " LIKE ? OR " + P_CODE + " LIKE ?";
        // The "%" symbols mean "anything can be before or after the search term"
        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{"%" + query + "%", "%" + query + "%"});
        if (c.moveToFirst()) {
            do {
                list.add(new Models.Project(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getDouble(8),
                        c.getString(9), c.getInt(10) == 1, c.getInt(11) == 1));
            } while (c.moveToNext());
        }
        c.close();
        return list; // Return the search results
    }
}
