package com.example.projectexpensetrackerv2;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    public ExpenseAdapter(Context context, List<Expense> expenses) {
        super(context, 0, expenses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Expense expense = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_item, parent, false);
        }

        TextView tvDate = convertView.findViewById(R.id.tvExpDate);
        TextView tvType = convertView.findViewById(R.id.tvExpType);
        TextView tvClaimant = convertView.findViewById(R.id.tvExpClaimant);
        TextView tvAmount = convertView.findViewById(R.id.tvExpAmount);
        TextView tvStatus = convertView.findViewById(R.id.tvExpStatus);

        ImageView ivEdit = convertView.findViewById(R.id.ivExpEdit);
        ImageView ivDelete = convertView.findViewById(R.id.ivExpDelete);

        if (expense != null) {
            tvDate.setText(expense.getDate());
            tvType.setText(expense.getType());
            tvClaimant.setText("By: " + expense.getClaimant());
            
            if (expense.getCurrency().equals("GBP")) {
                tvAmount.setText("£" + String.format("%.2f", expense.getAmountInGBP()));
            } else {
                tvAmount.setText("£" + String.format("%.2f", expense.getAmountInGBP()) + "\n(" + expense.getCurrency() + " " + String.format("%.2f", expense.getAmount()) + ")");
                tvAmount.setTextSize(14f); // Slightly smaller to fit both
            }
            
            tvStatus.setText(expense.getPaymentStatus());

            // 1. VIEW DETAILS
            convertView.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(getContext(), ExpenseDetailActivity.class);
                intent.putExtra("EXPENSE_ID", expense.getId());
                getContext().startActivity(intent);
            });

            // 2. EDIT
            if (ivEdit != null) {
                ivEdit.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(getContext(), AddEditExpenseActivity.class);
                    // Pass Project ID and Expense ID to indicate edit mode
                    intent.putExtra("PROJECT_ID", expense.getProjectId());
                    intent.putExtra("EXPENSE_ID", expense.getId());
                    getContext().startActivity(intent);
                });
            }

            // 3. DELETE
            if (ivDelete != null) {
                ivDelete.setOnClickListener(v -> {
                    if (getContext() instanceof ExpenseListActivity) {
                        ((ExpenseListActivity) getContext()).showDeleteDialog(expense);
                    }
                });
            }
        }

        return convertView;
    }
}
