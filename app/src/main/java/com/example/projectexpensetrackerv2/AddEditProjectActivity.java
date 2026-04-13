package com.example.projectexpensetrackerv2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * ADD / EDIT ACTIVITY: Handles both adding new projects and editing existing ones.
 * 
 * DEMO KEY FEATURES:
 * 1. CRUD (Create / Read / Update / Delete): This screen handles 'C', 'R', and 'U'.
 * 2. Multiple Field Types (Requirement 9):
 *    - Textbox: see etCode, etName, etBudget
 *    - Dropdown (Spinner): see spinnerStatus
 *    - Radio Button: see rgPriority
 *    - Toggle (Switch): see switchUrgent
 *    - Checkbox: see cbVerified
 */
public class AddEditProjectActivity extends AppCompatActivity {

    private EditText etCode, etName, etBudget;
    private Spinner spinnerStatus;
    private RadioGroup rgPriority;
    private SwitchMaterial switchUrgent;
    private CheckBox cbVerified;
    private Button btnSave, btnManageExpenses;

    private DbHelper db;
    private int projectId = -1; // -1 means we are ADDING a new project

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_project);

        db = new DbHelper(this);
        initViews();

        // Get ID from intent (if we are editing)
        projectId = getIntent().getIntExtra("ID", -1);

        if (projectId != -1) {
            loadData();
            btnManageExpenses.setVisibility(View.VISIBLE);
            btnSave.setText("Update Project");
        }

        btnSave.setOnClickListener(v -> saveProject());
        btnManageExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExpenseListActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            startActivity(intent);
        });
    }

    private void initViews() {
        etCode = findViewById(R.id.etCode);
        etName = findViewById(R.id.etName);
        etBudget = findViewById(R.id.etBudget);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        rgPriority = findViewById(R.id.rgPriority);
        switchUrgent = findViewById(R.id.switchUrgent);
        cbVerified = findViewById(R.id.cbVerified);
        btnSave = findViewById(R.id.btnSave);
        btnManageExpenses = findViewById(R.id.btnManageExpenses);

        // Setup Spinner
        String[] options = {"Active", "Completed", "On Hold"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadData() {
        Models.Project p = db.getProjectById(projectId);
        if (p != null) {
            etCode.setText(p.getProjectCode());
            etName.setText(p.getName());
            etBudget.setText(String.valueOf(p.getBudget()));
            
            // Set Spinner
            for (int i = 0; i < 3; i++) {
                if (spinnerStatus.getItemAtPosition(i).toString().equalsIgnoreCase(p.getStatus())) {
                    spinnerStatus.setSelection(i);
                }
            }

            // Set Radio Buttons
            if ("Low".equalsIgnoreCase(p.getPriority())) rgPriority.check(R.id.rbLow);
            else if ("Med".equalsIgnoreCase(p.getPriority())) rgPriority.check(R.id.rbMed);
            else if ("High".equalsIgnoreCase(p.getPriority())) rgPriority.check(R.id.rbHigh);

            // Set Toggle and Checkbox
            switchUrgent.setChecked(p.isUrgent());
            cbVerified.setChecked(p.isChecked());
        }
    }

    private void saveProject() {
        // --- STEP 1: DATA COLLECTION ---
        String code = etCode.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        // VALIDATION: Basic check to ensure required fields aren't empty
        if (code.isEmpty() || name.isEmpty() || budgetStr.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- STEP 2: RADIO BUTTON LOGIC ---
        String priority = "Low";
        int checkedId = rgPriority.getCheckedRadioButtonId();
        if (checkedId == R.id.rbMed) priority = "Med";
        else if (checkedId == R.id.rbHigh) priority = "High";

        // --- STEP 3: CREATE MODEL OBJECT ---
        // Boolean values from Switch and Checkbox are passed directly
        Models.Project p = new Models.Project(projectId, code, name, "", "", "", "",
                spinnerStatus.getSelectedItem().toString(),
                Double.parseDouble(budgetStr),
                priority, switchUrgent.isChecked(), cbVerified.isChecked());

        // --- STEP 4: DATABASE PERSISTENCE ---
        if (projectId == -1) {
            db.addProject(p); // CREATE
            Toast.makeText(this, "Project Added!", Toast.LENGTH_SHORT).show();
        } else {
            db.updateProject(p); // UPDATE
            Toast.makeText(this, "Project Updated!", Toast.LENGTH_SHORT).show();
        }
        finish(); // Close activity
    }
}
