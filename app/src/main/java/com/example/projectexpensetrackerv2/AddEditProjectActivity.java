package com.example.projectexpensetrackerv2; // Package folder for the app

// Import tools for UI elements, navigation, and Android features
import android.content.Intent;            // Used to open other screens
import android.os.Bundle;                // Basic setup data for an Activity
import android.view.View;                // Base class for all UI objects
import android.widget.ArrayAdapter;     // Bridge for dropdown (spinner) data
import android.widget.Button;           // A clickable button
import android.widget.CheckBox;         // A ticket box (Yes/No)
import android.widget.EditText;         // A box where the user types text
import android.widget.RadioButton;      // A circular button for selecting one option
import android.widget.RadioGroup;       // A group that holds multiple RadioButtons
import android.widget.Spinner;          // A dropdown list menu
import android.widget.Toast;            // Small popup text messages
import androidx.appcompat.app.AppCompatActivity; // Base screen class
import com.google.android.material.switchmaterial.SwitchMaterial; // A slide-toggle switch

/**
 * ADD / EDIT ACTIVITY: This screen is used for both adding a brand new project 
 * and editing an existing one. It contains various input fields for the user.
 */
public class AddEditProjectActivity extends AppCompatActivity {

    // Define all the UI elements (input boxes, buttons, etc.)
    private EditText etCode, etName, etBudget; // Text entry boxes
    private Spinner spinnerStatus;            // Dropdown menu
    private RadioGroup rgPriority;           // Priority selection group
    private SwitchMaterial switchUrgent;      // Sliding toggle switch
    private CheckBox cbVerified;             // Checkmark box
    private Button btnSave, btnManageExpenses; // Clickable buttons

    private DbHelper db;             // Connection to the phone's database
    private int projectId = -1;      // Remembers which project we are editing (-1 means "New Project")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);            // Basic Android setup
        setContentView(R.layout.activity_add_edit_project); // Use the layout file for this screen

        db = new DbHelper(this); // Link to the local database
        initViews();            // Link the code variables to the actual screen items

        // Get the ID of the project we are editing (if any) from the previous screen
        projectId = getIntent().getIntExtra("ID", -1);

        // If projectId is NOT -1, it means we are EDITING an existing project
        if (projectId != -1) {
            loadData(); // Pull the project data from the database and show it on screen
            btnManageExpenses.setVisibility(View.VISIBLE); // Show the "Manage Expenses" button
            btnSave.setText("Update Project");             // Change button text to "Update"
        }

        // When "Save" is clicked, run the saveProject function
        btnSave.setOnClickListener(v -> saveProject());
        
        // When "Manage Expenses" is clicked, open the Expense List screen
        btnManageExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExpenseListActivity.class);
            intent.putExtra("PROJECT_ID", projectId); // Pass the project ID forward
            startActivity(intent);
        });
    }

    // This function finds all the UI elements on the screen and prepares them
    private void initViews() {
        etCode = findViewById(R.id.etCode);           // Link project code box
        etName = findViewById(R.id.etName);           // Link project name box
        etBudget = findViewById(R.id.etBudget);       // Link budget box
        spinnerStatus = findViewById(R.id.spinnerStatus); // Link status dropdown
        rgPriority = findViewById(R.id.rgPriority);   // Link priority radio group
        switchUrgent = findViewById(R.id.switchUrgent); // Link urgency toggle
        cbVerified = findViewById(R.id.cbVerified);   // Link verification checkbox
        btnSave = findViewById(R.id.btnSave);         // Link save button
        btnManageExpenses = findViewById(R.id.btnManageExpenses); // Link manage expenses button

        // Setup the dropdown (Spinner) options
        String[] options = {"Active", "Completed", "On Hold"};
        // Create an adapter to bridge the options list with the Spinner UI
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter); // Apply options to the spinner

        // Setup the title bar at the top with a back button
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Handles what happens when the "Back" arrow in the title bar is clicked
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Go back to the previous screen
        return true;
    }

    // Loads project data from the database into the screen fields (for editing)
    private void loadData() {
        Models.Project p = db.getProjectById(projectId); // Fetch project details by ID
        if (p != null) {
            etCode.setText(p.getProjectCode());       // Put code in the box
            etName.setText(p.getName());               // Put name in the box
            etBudget.setText(String.valueOf(p.getBudget())); // Put budget in the box
            
            // Loop through the dropdown options to find which one matches the project status
            for (int i = 0; i < 3; i++) {
                if (spinnerStatus.getItemAtPosition(i).toString().equalsIgnoreCase(p.getStatus())) {
                    spinnerStatus.setSelection(i); // Select the matching status in the dropdown
                }
            }

            // Set the correct Radio Button based on the project's priority
            if ("Low".equalsIgnoreCase(p.getPriority())) rgPriority.check(R.id.rbLow);
            else if ("Med".equalsIgnoreCase(p.getPriority())) rgPriority.check(R.id.rbMed);
            else if ("High".equalsIgnoreCase(p.getPriority())) rgPriority.check(R.id.rbHigh);

            // Set the toggle switch and checkbox based on the project data
            switchUrgent.setChecked(p.isUrgent());
            cbVerified.setChecked(p.isChecked());
        }
    }

    // Collects all data from the screen and saves it to the database
    private void saveProject() {
        // --- STEP 1: Get text from the entry boxes ---
        String code = etCode.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        // VALIDATION: Check if any required boxes are empty
        if (code.isEmpty() || name.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return; // Stop here and don't save
        }

        // --- STEP 2: Figure out which priority radio button was selected ---
        String priority = "Low"; // Default to Low
        int checkedId = rgPriority.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMed) priority = "Med";
        else if (checkedId == R.id.rbHigh) priority = "High";

        // --- STEP 3: Create a Project object container and fill it with our screen data ---
        Models.Project p = new Models.Project(projectId, code, name, "", "", "", "",
                spinnerStatus.getSelectedItem().toString(),
                Double.parseDouble(budgetStr),
                priority, switchUrgent.isChecked(), cbVerified.isChecked());

        // --- STEP 4: Save to the database ---
        if (projectId == -1) {
            db.addProject(p); // If it's a new project, use "Add"
            Toast.makeText(this, "Project Added!", Toast.LENGTH_SHORT).show();
        } else {
            db.updateProject(p); // If it's an existing project, use "Update"
            Toast.makeText(this, "Project Updated!", Toast.LENGTH_SHORT).show();
        }
        finish(); // Close this screen and go back
    }
}
