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
import java.util.Calendar;

/**
 * ADD / EDIT EXPENSE ACTIVITY: Handles creating and updating expenses.
 * Uses Models.Expense and DbHelper for simplicity.
 */
public class AddEditExpenseActivity extends AppCompatActivity {

    private EditText etDate, etAmount, etClaimant, etDesc, etLocation;
    private AutoCompleteTextView spinnerCurrency;
    private Spinner spinnerType, spinnerMethod, spinnerStatus;
    private Button btnSave;

    private DbHelper db;
    private int projectId, expenseId = -1;

    private String[] types = {"Travel", "Equipment", "Materials", "Services"};
    private String[] methods = {"Cash", "Credit Card", "Bank Transfer"};
    private String[] statuses = {"Paid", "Pending"};
    private String[] currencies = {"GBP", "USD", "EUR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        db = new DbHelper(this);
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        expenseId = getIntent().getIntExtra("EXPENSE_ID", -1);

        initViews();
        setupPickers();

        if (expenseId != -1) {
            loadData();
            btnSave.setText("Update Expense");
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void initViews() {
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

        // Setup Spinners
        setupSpinner(spinnerType, types);
        setupSpinner(spinnerMethod, methods);
        setupSpinner(spinnerStatus, statuses);
        
        ArrayAdapter<String> curAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencies);
        spinnerCurrency.setAdapter(curAdapter);

        setSupportActionBar(findViewById(R.id.toolbarExpense));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupSpinner(Spinner s, String[] arr) {
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a);
    }

    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                etDate.setText(d + "/" + (m + 1) + "/" + y);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void loadData() {
        Models.Expense e = db.getExpenseById(expenseId);
        if (e != null) {
            etDate.setText(e.getDate());
            etAmount.setText(String.valueOf(e.getAmount()));
            spinnerCurrency.setText(e.getCurrency(), false);
            etClaimant.setText(e.getClaimant());
            etDesc.setText(e.getDescription());
            etLocation.setText(e.getLocation());
            // (Simplified spinner selection logic for demo)
        }
    }

    private void save() {
        String date = etDate.getText().toString();
        String amt = etAmount.getText().toString();
        if (date.isEmpty() || amt.isEmpty()) {
            Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        Models.Expense e = new Models.Expense(expenseId, projectId, date, Double.parseDouble(amt),
                spinnerCurrency.getText().toString(), spinnerType.getSelectedItem().toString(),
                spinnerMethod.getSelectedItem().toString(), etClaimant.getText().toString(),
                spinnerStatus.getSelectedItem().toString(), etDesc.getText().toString(), etLocation.getText().toString());

        if (expenseId == -1) db.addExpense(e); else db.updateExpense(e);
        finish();
    }
}