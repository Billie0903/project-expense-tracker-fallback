package com.example.projectexpensetrackerv2; // Package folder name for the app

// Import tools for pickers, UI elements, and Android features
import android.app.DatePickerDialog;    // A popup calendar to pick a date
import android.os.Bundle;                // Basic setup data for an Activity
import android.widget.ArrayAdapter;     // Bridge for dropdown (spinner) data
import android.widget.AutoCompleteTextView; // A text box that gives suggestions
import android.widget.Button;           // A clickable button
import android.widget.EditText;         // A box where the user types text
import android.widget.Spinner;          // A dropdown select menu
import android.widget.Toast;            // Small popup text messages
import androidx.appcompat.app.AppCompatActivity; // Base screen class
import java.util.Calendar;              // Tool for managing dates and times

/**
 * ADD / EDIT EXPENSE ACTIVITY: This screen is used to add a new expense
 * or change an existing one. It has fields for amount, date, category, and more.
 */
public class AddEditExpenseActivity extends AppCompatActivity {

    // Define the UI elements (input boxes, dropdowns, etc.)
    private EditText etDate, etAmount, etClaimant, etDesc, etLocation; // Entry boxes for typing
    private AutoCompleteTextView spinnerCurrency; // Suggestion box for money type (USD, GBP)
    private Spinner spinnerType, spinnerMethod, spinnerStatus; // Selection menus (Drop-downs)
    private Button btnSave; // Clickable button to save the data

    private DbHelper db;                             // Connection to the local database
    private int projectId, expenseId = -1;           // Remembers the IDs ( -1 means "New Expense" )

    // Lists of options for the dropdown menus
    private String[] types = {"Travel", "Equipment", "Materials", "Services"};
    private String[] methods = {"Cash", "Credit Card", "Bank Transfer"};
    private String[] statuses = {"Paid", "Pending"};
    private String[] currencies = {"GBP", "USD", "EUR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                // Basic Android setup
        setContentView(R.layout.activity_add_edit_expense); // Use this layout file for the screen UI

        db = new DbHelper(this); // Link to the local database

        // Get the Project ID and Expense ID from the previous screen
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        expenseId = getIntent().getIntExtra("EXPENSE_ID", -1);

        initViews();    // Set up all the screen UI elements
        setupPickers(); // Prepare the calendar date-picker tool

        // If expenseId is NOT -1, it means we are EDITING an old expense
        if (expenseId != -1) {
            loadData(); // Pull the data from the database and show it on the labels
            btnSave.setText("Update Expense"); // Change button text to "Update"
        }

        // When the "Save" button is clicked, run the save function
        btnSave.setOnClickListener(v -> save());
    }

    // This function finds and links the UI elements on the screen to our code variables
    private void initViews() {
        etDate = findViewById(R.id.etExpenseDate);       // Link date box
        etAmount = findViewById(R.id.etExpenseAmount);   // Link amount box
        spinnerCurrency = findViewById(R.id.spinnerCurrency); // Link currency box
        spinnerType = findViewById(R.id.spinnerExpenseType); // Link category menu
        spinnerMethod = findViewById(R.id.spinnerPaymentMethod); // Link method menu
        etClaimant = findViewById(R.id.etClaimant);      // Link person box
        spinnerStatus = findViewById(R.id.spinnerPaymentStatus); // Link status menu
        etDesc = findViewById(R.id.etExpenseDesc);       // Link notes box
        etLocation = findViewById(R.id.etLocation);      // Link location box
        btnSave = findViewById(R.id.btnSaveExpense);     // Link save button

        // Setup the choice lists for the dropdown menus
        setupSpinner(spinnerType, types);
        setupSpinner(spinnerMethod, methods);
        setupSpinner(spinnerStatus, statuses);
        
        // Setup the currency suggestion box (AutoComplete)
        ArrayAdapter<String> curAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currencies);
        spinnerCurrency.setAdapter(curAdapter);

        // Setup the title bar at the top with a back button
        setSupportActionBar(findViewById(R.id.toolbarExpense));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Handles what happens when the "Back" arrow in the title bar is clicked
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go back to the previous screen
        return true;
    }

    // A helper function to easily set up dropdown (Spinner) menus
    private void setupSpinner(Spinner s, String[] arr) {
        // Create an adapter to bridge our list of words (arr) with the UI menu (s)
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arr);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(a); // Apply the options to the menu
    }

    // Configures the date-picker so a calendar pops up when you tap the Date box
    private void setupPickers() {
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance(); // Get current today's date
            // Show the popup calendar
            new DatePickerDialog(this, (view, y, m, d) -> {
                // When a date is picked, show it in the text box (d/m/y)
                etDate.setText(d + "/" + (m + 1) + "/" + y);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    // Loads an existing expense from the database into the screen fields (for editing)
    private void loadData() {
        Models.Expense e = db.getExpenseById(expenseId); // Fetch the expense from the database
        if (e != null) {
            etDate.setText(e.getDate());                    // Set date in box
            etAmount.setText(String.valueOf(e.getAmount())); // Set amount in box
            spinnerCurrency.setText(e.getCurrency(), false); // Set currency in box
            etClaimant.setText(e.getClaimant());             // Set person name in box
            etDesc.setText(e.getDescription());              // Set notes in box
            etLocation.setText(e.getLocation());             // Set location in box
        }
    }

    // Collects everything entered on screen and saves it to the database
    private void save() {
        String date = etDate.getText().toString(); // Read date text
        String amt = etAmount.getText().toString(); // Read amount text
        
        // VALIDATION: Check if the required fields are empty
        if (date.isEmpty() || amt.isEmpty()) {
            Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show();
            return; // Stop here and don't save
        }

        // Create an Expense container object and fill it with all our screen data
        Models.Expense e = new Models.Expense(expenseId, projectId, date, Double.parseDouble(amt),
                spinnerCurrency.getText().toString(), spinnerType.getSelectedItem().toString(),
                spinnerMethod.getSelectedItem().toString(), etClaimant.getText().toString(),
                spinnerStatus.getSelectedItem().toString(), etDesc.getText().toString(), etLocation.getText().toString());

        // Check if we are creating NEW or updating OLD
        if (expenseId == -1) {
            db.addExpense(e); // Save as new
        } else {
            db.updateExpense(e); // Save as update
        }
        
        finish(); // Close this screen and go back
    }
}