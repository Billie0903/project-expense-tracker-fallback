package com.example.projectexpensetrackerv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ProjectDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ProjectTracker.db";
    private static final int DATABASE_VERSION = 2;

    // Table and Column Names
    public static final String TABLE_PROJECTS = "projects";
    public static final String COL_ID = "id";
    public static final String COL_CODE = "project_code";
    public static final String COL_NAME = "name";
    public static final String COL_DESC = "description";
    public static final String COL_START = "start_date";
    public static final String COL_END = "end_date";
    public static final String COL_MANAGER = "manager";
    public static final String COL_STATUS = "status";
    public static final String COL_BUDGET = "budget";

    public ProjectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        // Enable foreign key support for CASCADE DELETE
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Project Table
        String createProjectTable = "CREATE TABLE " + TABLE_PROJECTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CODE + " TEXT, " +
                COL_NAME + " TEXT, " +
                COL_DESC + " TEXT, " +
                COL_START + " TEXT, " +
                COL_END + " TEXT, " +
                COL_MANAGER + " TEXT, " +
                COL_STATUS + " TEXT, " +
                COL_BUDGET + " REAL)";

        db.execSQL(createProjectTable);

        //Expense table creation here
        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "project_id INTEGER NOT NULL," +
                "expense_date TEXT NOT NULL," +
                "amount REAL NOT NULL," +
                "currency TEXT NOT NULL," +
                "expense_type TEXT NOT NULL," +
                "payment_method TEXT NOT NULL," +
                "claimant TEXT NOT NULL," +
                "payment_status TEXT NOT NULL," +
                "description TEXT," +
                "location TEXT," +
                "FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS expenses (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "project_id INTEGER NOT NULL," +
                    "expense_date TEXT NOT NULL," +
                    "amount REAL NOT NULL," +
                    "currency TEXT NOT NULL," +
                    "expense_type TEXT NOT NULL," +
                    "payment_method TEXT NOT NULL," +
                    "claimant TEXT NOT NULL," +
                    "payment_status TEXT NOT NULL," +
                    "description TEXT," +
                    "location TEXT," +
                    "FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE)");
        }
    }

    // --- FEATURE B: PROJECT CRUD ---

    public long addProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CODE, project.getProjectCode());
        cv.put(COL_NAME, project.getName());
        cv.put(COL_DESC, project.getDescription());
        cv.put(COL_START, project.getStartDate());
        cv.put(COL_END, project.getEndDate());
        cv.put(COL_MANAGER, project.getManager());
        cv.put(COL_STATUS, project.getStatus());
        cv.put(COL_BUDGET, project.getBudget());
        return db.insert(TABLE_PROJECTS, null, cv);
    }

    public List<Project> getAllProjects() {
        List<Project> projectList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROJECTS, null);

        if (cursor.moveToFirst()) {
            do {
                Project project = new Project(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getDouble(8));
                projectList.add(project);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return projectList;
    }

    public Project getProjectById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PROJECTS, null,
                COL_ID + "=?", new String[] { String.valueOf(id) },
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Project project = new Project(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getDouble(8));
            cursor.close();
            return project;
        }
        if (cursor != null)
            cursor.close();
        return null;
    }

    public void deleteProject(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJECTS, COL_ID + "=?", new String[] { String.valueOf(id) });
    }

    public int updateProject(Project updatedProject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_CODE, updatedProject.getProjectCode());
        cv.put(COL_NAME, updatedProject.getName());
        cv.put(COL_DESC, updatedProject.getDescription());
        cv.put(COL_START, updatedProject.getStartDate());
        cv.put(COL_END, updatedProject.getEndDate());
        cv.put(COL_MANAGER, updatedProject.getManager());
        cv.put(COL_STATUS, updatedProject.getStatus());
        cv.put(COL_BUDGET, updatedProject.getBudget());

        return db.update(TABLE_PROJECTS, cv, COL_ID + "=?",
                new String[]{String.valueOf(updatedProject.getId())});
    }

    // --- FEATURE C: EXPENSE CRUD ---

    public long addExpense(Expense e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("project_id", e.getProjectId());
        cv.put("expense_date", e.getDate());
        cv.put("amount", e.getAmount());
        cv.put("currency", e.getCurrency());
        cv.put("expense_type", e.getType());
        cv.put("payment_method", e.getPaymentMethod());
        cv.put("claimant", e.getClaimant());
        cv.put("payment_status", e.getPaymentStatus());
        cv.put("description", e.getDescription());
        cv.put("location", e.getLocation());

        long id = db.insert("expenses", null, cv);
        db.close();
        return id;
    }

    public ArrayList<Expense> getExpensesForProject(int projectId) {
        ArrayList<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("expenses", null, "project_id=?",
                new String[]{String.valueOf(projectId)}, null, null, null);

        if (c.moveToFirst()) {
            do {
                Expense expense = new Expense(
                        c.getInt(0), c.getInt(1), c.getString(2), c.getDouble(3),
                        c.getString(4), c.getString(5), c.getString(6),
                        c.getString(7), c.getString(8), c.getString(9), c.getString(10)
                );
                list.add(expense);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public Expense getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query("expenses", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);

        if (c != null && c.moveToFirst()) {
            Expense expense = new Expense(
                    c.getInt(0), c.getInt(1), c.getString(2), c.getDouble(3),
                    c.getString(4), c.getString(5), c.getString(6),
                    c.getString(7), c.getString(8), c.getString(9), c.getString(10)
            );
            c.close();
            return expense;
        }
        if (c != null) c.close();
        return null;
    }

    public int updateExpense(Expense e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("project_id", e.getProjectId());
        cv.put("expense_date", e.getDate());
        cv.put("amount", e.getAmount());
        cv.put("currency", e.getCurrency());
        cv.put("expense_type", e.getType());
        cv.put("payment_method", e.getPaymentMethod());
        cv.put("claimant", e.getClaimant());
        cv.put("payment_status", e.getPaymentStatus());
        cv.put("description", e.getDescription());
        cv.put("location", e.getLocation());
        return db.update("expenses", cv, "id=?", new String[]{String.valueOf(e.getId())});
    }

    public void deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("expenses", "id=?", new String[]{String.valueOf(id)});
    }

    // --- FEATURE D: SEARCH LOGIC ---

    /**
     * Searches for projects where the name matches the query.
     * Uses SQL LIKE with % wildcards for partial matches.
     */
    public List<Project> searchProjects(String query) {
        List<Project> filteredList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Match query anywhere in the name
        String selection = COL_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%"};

        Cursor cursor = db.query(TABLE_PROJECTS, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                filteredList.add(new Project(
                        cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5),
                        cursor.getString(6), cursor.getString(7), cursor.getDouble(8)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return filteredList;
    }
}