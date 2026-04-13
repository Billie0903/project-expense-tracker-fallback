package com.example.projectexpensetrackerv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ExpenseListActivity extends AppCompatActivity {

    private ListView lvExpenses;
    private TextView tvProjectTitle, tvBudgetInfo;
    private ProjectDatabaseHelper dbHelper;
    private int projectId;
    private Project currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        dbHelper = new ProjectDatabaseHelper(this);

        // Retrieve the Project ID passed from the Project List or Detail screen
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);

        if (projectId == -1) {
            Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadProjectInfo();

        findViewById(R.id.btnAddExpense).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditExpenseActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            startActivity(intent);
        });
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarExpenses);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        lvExpenses = findViewById(R.id.lvExpenses);
        lvExpenses.setEmptyView(findViewById(R.id.tvEmptyExp));
    }

    private void loadProjectInfo() {
        currentProject = dbHelper.getProjectById(projectId);
        if (currentProject != null) {
            getSupportActionBar().setTitle("Expenses: " + currentProject.getName());
        }
    }

    private void refreshExpenseList() {
        List<Expense> expenses = dbHelper.getExpensesForProject(projectId);

        // Calculate Total Spent in GBP
        double totalSpent = 0;
        for (Expense e : expenses) {
            totalSpent += e.getAmountInGBP();
        }

        // Setup Adapter
        ExpenseAdapter adapter = new ExpenseAdapter(this, expenses);
        lvExpenses.setAdapter(adapter);

        // Optional: Show a toast if over budget
        if (currentProject != null && totalSpent > currentProject.getBudget()) {
            Toast.makeText(this, "Warning: Over Budget!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshExpenseList(); // Refresh data every time we return to this screen
    }

    public void showDeleteDialog(Expense expense) {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense covering " + expense.getAmount() + " " + expense.getCurrency() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteExpense(expense.getId());
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                    refreshExpenseList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}