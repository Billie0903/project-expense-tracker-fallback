package com.example.projectexpensetrackerv2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

/**
 * EXPENSE LIST ACTIVITY: Shows all expenses for a specific project.
 * Uses an inner Adapter class for simplicity.
 */
public class ExpenseListActivity extends AppCompatActivity {

    private ListView listView;
    private DbHelper db;
    private int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        db = new DbHelper(this);
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);

        listView = findViewById(R.id.lvExpenses);
        
        findViewById(R.id.btnAddExpense).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditExpenseActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            startActivity(intent);
        });

        setSupportActionBar(findViewById(R.id.toolbarExpenses));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void updateList() {
        List<Models.Expense> list = db.getExpensesForProject(projectId);
        listView.setAdapter(new ExpenseAdapter(this, list));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    // --- INNER ADAPTER ---
    private class ExpenseAdapter extends ArrayAdapter<Models.Expense> {
        public ExpenseAdapter(Context context, List<Models.Expense> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Models.Expense e = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_item, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.tvExpDate)).setText(e.getDate());
            ((TextView) convertView.findViewById(R.id.tvExpType)).setText(e.getType());
            ((TextView) convertView.findViewById(R.id.tvExpClaimant)).setText("By: " + e.getClaimant());
            ((TextView) convertView.findViewById(R.id.tvExpAmount)).setText("£" + String.format("%.2f", e.getAmountInGBP()));
            ((TextView) convertView.findViewById(R.id.tvExpStatus)).setText(e.getPaymentStatus());

            // EDIT: Open AddEditExpenseActivity
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddEditExpenseActivity.class);
                intent.putExtra("PROJECT_ID", projectId);
                intent.putExtra("EXPENSE_ID", e.getId());
                getContext().startActivity(intent);
            });

            // DELETE
            convertView.findViewById(R.id.ivExpDelete).setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete?")
                        .setPositiveButton("Yes", (d, w) -> {
                            db.deleteExpense(e.getId());
                            updateList();
                        })
                        .setNegativeButton("No", null).show();
            });

            return convertView;
        }
    }
}