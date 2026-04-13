package com.example.projectexpensetrackerv2;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.Calendar;

public class AddEditExpenseActivity extends AppCompatActivity {

    private EditText etDate, etAmount, etClaimant, etDesc, etLocation;
    private AutoCompleteTextView spinnerCurrency;
    private Spinner spinnerType, spinnerMethod, spinnerStatus;
    private Button btnSave;
    private ProjectDatabaseHelper dbHelper;
    private int projectId;
    private int expenseId = -1;

    // Data for Spinners based on Coursework Specs
    private String[] expenseTypes = {"Travel", "Equipment", "Materials", "Services", "Software/Licenses", "Labour costs", "Utilities", "Miscellaneous"};
    private String[] paymentMethods = {"Cash", "Credit Card", "Bank Transfer", "Cheque"};
    private String[] paymentStatuses = {"Paid", "Pending", "Reimbursed"};
    private String[] currencies = {"GBP", "USD", "EUR", "VND"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        dbHelper = new ProjectDatabaseHelper(this);

        // Get the Project ID passed from the previous screen
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        expenseId = getIntent().getIntExtra("EXPENSE_ID", -1);

        if (projectId == -1) {
            Toast.makeText(this, "Error: No project selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupSpinners();
        setupDatePicker();

        if (expenseId != -1) {
            loadExpenseData();
        }

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarExpense);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(expenseId == -1 ? "Add Expense" : "Edit Expense");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etDate = findViewById(R.id.etExpenseDate);
        etAmount = findViewById(R.id.etExpenseAmount);
        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        spinnerType = findViewById(R.id.spinnerExpenseType);
        spinnerMethod = findViewById(R.id.spinnerPaymentMethod);
        etClaimant = findViewById(R.id.etClaimant);
        spinnerStatus = findViewById(R.id.spinnerPaymentStatus);
        etDesc = findViewById(R.id.etExpenseDesc);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSaveExpense);
    }

    private void setupSpinners() {
        // Expense Type Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Payment Method Spinner
        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        methodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMethod.setAdapter(methodAdapter);

        // Payment Status Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentStatuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Currency AutoComplete (Material Exposed Dropdown)
        ArrayAdapter<String> currAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencies);
        spinnerCurrency.setAdapter(currAdapter);
        spinnerCurrency.setText(currencies[0], false); // Default to GBP
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
                String date = d + "/" + (m + 1) + "/" + y;
                etDate.setText(date);
            }, year, month, day);
            dialog.show();
        });
    }

    private void saveExpense() {
        String date = etDate.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String currency = spinnerCurrency.getText().toString();
        String type = spinnerType.getSelectedItem().toString();
        String method = spinnerMethod.getSelectedItem().toString();
        String claimant = etClaimant.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        String desc = etDesc.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Basic Validation
        if (date.isEmpty() || amountStr.isEmpty() || claimant.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // Create Expense Object
        Expense newExpense = new Expense(projectId, date, amount, currency, type, method, claimant, status, desc, location);

        if (expenseId != -1) {
            newExpense.setId(expenseId);
            int rows = dbHelper.updateExpense(newExpense);
            if (rows > 0) {
                Toast.makeText(this, "Expense Updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update expense", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Save to Database
            long id = dbHelper.addExpense(newExpense);
            if (id > 0) {
                Toast.makeText(this, "Expense Added!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadExpenseData() {
        Expense expense = dbHelper.getExpenseById(expenseId);
        if (expense != null) {
            etDate.setText(expense.getDate());
            etAmount.setText(String.valueOf(expense.getAmount()));
            spinnerCurrency.setText(expense.getCurrency(), false);
            etClaimant.setText(expense.getClaimant());
            etDesc.setText(expense.getDescription());
            etLocation.setText(expense.getLocation());

            setSpinnerSelection(spinnerType, expenseTypes, expense.getType());
            setSpinnerSelection(spinnerMethod, paymentMethods, expense.getPaymentMethod());
            setSpinnerSelection(spinnerStatus, paymentStatuses, expense.getPaymentStatus());

            btnSave.setText("Update Expense");
        } else {
            Toast.makeText(this, "Error loading expense for edit", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}