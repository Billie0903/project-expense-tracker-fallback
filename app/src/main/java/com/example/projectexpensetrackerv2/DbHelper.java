package com.example.projectexpensetrackerv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * DATABASE HELPER: This file handles all SQLite operations.
 * To change the topic (e.g. from Projects to Books), just change these constants!
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "DemoTracker.db";
    private static final int DB_VERSION = 3; // Incremented for new fields

    // --- TABLE & COLUMN NAMES (Change these for the DEMO TOPIC) ---
    public static final String TABLE_P = "projects";
    public static final String P_ID = "id";
    public static final String P_CODE = "code";
    public static final String P_NAME = "name";
    public static final String P_DESC = "description";
    public static final String P_START = "start_date";
    public static final String P_END = "end_date";
    public static final String P_MNGR = "manager";
    public static final String P_STATUS = "status";
    public static final String P_BUDGET = "budget";
    public static final String P_PRIORITY = "priority"; // Radio
    public static final String P_URGENT = "is_urgent";   // Toggle
    public static final String P_CHECK = "is_checked";   // Checkbox

    public static final String TABLE_E = "expenses";
    public static final String E_ID = "id";
    public static final String E_PID = "project_id";
    public static final String E_DATE = "date";
    public static final String E_AMT = "amount";
    public static final String E_CURR = "currency";
    public static final String E_TYPE = "type";
    public static final String E_METH = "method";
    public static final String E_CLMT = "claimant";
    public static final String E_STAT = "status";
    public static final String E_DESC = "description";
    public static final String E_LOC = "location";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Project Table
        db.execSQL("CREATE TABLE " + TABLE_P + " (" +
                P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                P_CODE + " TEXT, " + P_NAME + " TEXT, " + P_DESC + " TEXT, " +
                P_START + " TEXT, " + P_END + " TEXT, " + P_MNGR + " TEXT, " +
                P_STATUS + " TEXT, " + P_BUDGET + " REAL, " +
                P_PRIORITY + " TEXT, " + P_URGENT + " INTEGER, " + P_CHECK + " INTEGER)");

        // Create Expense Table
        db.execSQL("CREATE TABLE " + TABLE_E + " (" +
                E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                E_PID + " INTEGER, " + E_DATE + " TEXT, " + E_AMT + " REAL, " +
                E_CURR + " TEXT, " + E_TYPE + " TEXT, " + E_METH + " TEXT, " +
                E_CLMT + " TEXT, " + E_STAT + " TEXT, " + E_DESC + " TEXT, " +
                E_LOC + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_P);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_E);
        onCreate(db);
    }

    // --- PROJECT CRUD ---

    public long addProject(Models.Project p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
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
        return db.insert(TABLE_P, null, cv);
    }

    public List<Models.Project> getAllProjects() {
        List<Models.Project> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_P, null);
        if (c.moveToFirst()) {
            do {
                list.add(new Models.Project(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getDouble(8),
                        c.getString(9), c.getInt(10) == 1, c.getInt(11) == 1));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public Models.Project getProjectById(int id) {
        Cursor c = getReadableDatabase().query(TABLE_P, null, P_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Models.Project p = new Models.Project(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                    c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getDouble(8),
                    c.getString(9), c.getInt(10) == 1, c.getInt(11) == 1);
            c.close();
            return p;
        }
        return null;
    }

    public int updateProject(Models.Project p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
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
        return db.update(TABLE_P, cv, P_ID + "=?", new String[]{String.valueOf(p.getId())});
    }

    public void deleteProject(int id) {
        getWritableDatabase().delete(TABLE_P, P_ID + "=?", new String[]{String.valueOf(id)});
        getWritableDatabase().delete(TABLE_E, E_PID + "=?", new String[]{String.valueOf(id)});
    }

    // --- EXPENSE CRUD ---

    public long addExpense(Models.Expense e) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(E_PID, e.getProjectId());
        cv.put(E_DATE, e.getDate());
        cv.put(E_AMT, e.getAmount());
        cv.put(E_CURR, e.getCurrency());
        cv.put(E_TYPE, e.getType());
        cv.put(E_METH, e.getPaymentMethod());
        cv.put(E_CLMT, e.getClaimant());
        cv.put(E_STAT, e.getPaymentStatus());
        cv.put(E_DESC, e.getDescription());
        cv.put(E_LOC, e.getLocation());
        return db.insert(TABLE_E, null, cv);
    }

    public List<Models.Expense> getExpensesForProject(int pid) {
        List<Models.Expense> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE_E, null, E_PID + "=?", new String[]{String.valueOf(pid)}, null, null, null);
        if (c.moveToFirst()) {
            do {
                list.add(new Models.Expense(c.getInt(0), c.getInt(1), c.getString(2), c.getDouble(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8),
                        c.getString(9), c.getString(10)));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

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

    public void deleteExpense(int id) {
        getWritableDatabase().delete(TABLE_E, E_ID + "=?", new String[]{String.valueOf(id)});
    }

    // --- SEARCH ---
    public List<Models.Project> searchProjects(String query) {
        List<Models.Project> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_P + " WHERE " + P_NAME + " LIKE ? OR " + P_CODE + " LIKE ?";
        Cursor c = getReadableDatabase().rawQuery(sql, new String[]{"%" + query + "%", "%" + query + "%"});
        if (c.moveToFirst()) {
            do {
                list.add(new Models.Project(c.getInt(0), c.getString(1), c.getString(2), c.getString(3),
                        c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getDouble(8),
                        c.getString(9), c.getInt(10) == 1, c.getInt(11) == 1));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
}
