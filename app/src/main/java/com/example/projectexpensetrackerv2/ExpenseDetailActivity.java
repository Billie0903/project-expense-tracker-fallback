package com.example.projectexpensetrackerv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class ExpenseDetailActivity extends AppCompatActivity {

    private ProjectDatabaseHelper dbHelper;
    private int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_details);

        dbHelper = new ProjectDatabaseHelper(this);
        expenseId = getIntent().getIntExtra("EXPENSE_ID", -1);

        if (expenseId == -1) {
            Toast.makeText(this, "Error: Invalid Expense", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload details whenever the activity is shown (in case of edits)
        displayExpenseDetails();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarExpDetail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayExpenseDetails() {
        Expense expense = dbHelper.getExpenseById(expenseId);

        if (expense != null) {
            ((TextView) findViewById(R.id.detExpType)).setText(expense.getType());
            ((TextView) findViewById(R.id.detExpDate)).setText(expense.getDate());
            
            if (expense.getCurrency().equals("GBP")) {
                ((TextView) findViewById(R.id.detExpAmount)).setText("£" + String.format("%.2f", expense.getAmountInGBP()));
            } else {
                ((TextView) findViewById(R.id.detExpAmount)).setText("£" + String.format("%.2f", expense.getAmountInGBP()) + " (Original: " + expense.getCurrency() + " " + String.format("%.2f", expense.getAmount()) + ")");
            }

            ((TextView) findViewById(R.id.detExpClaimant)).setText(expense.getClaimant());
            ((TextView) findViewById(R.id.detExpMethod)).setText(expense.getPaymentMethod());
            ((TextView) findViewById(R.id.detExpStatus)).setText(expense.getPaymentStatus());
            
            // Description and Location might be empty
            String desc = expense.getDescription();
            ((TextView) findViewById(R.id.detExpDesc)).setText((desc == null || desc.isEmpty()) ? "N/A" : desc);
            
            String loc = expense.getLocation();
            ((TextView) findViewById(R.id.detExpLocation)).setText((loc == null || loc.isEmpty()) ? "N/A" : loc);
        } else {
            Toast.makeText(this, "Error loading expense details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        Expense expense = dbHelper.getExpenseById(expenseId);
        if (expense == null) return super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, AddEditExpenseActivity.class);
            intent.putExtra("PROJECT_ID", expense.getProjectId());
            intent.putExtra("EXPENSE_ID", expense.getId());
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteExpense(expenseId);
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
