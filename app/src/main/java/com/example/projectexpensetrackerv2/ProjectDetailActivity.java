package com.example.projectexpensetrackerv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class ProjectDetailActivity extends AppCompatActivity {

    private ProjectDatabaseHelper dbHelper;
    private int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_details);

        dbHelper = new ProjectDatabaseHelper(this);
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);

        setupToolbar();
        displayProjectDetails();

        Button btnManageExpenses = findViewById(R.id.btnManageExpenses);
        btnManageExpenses.setOnClickListener(v -> {
            Intent intent = new Intent(ProjectDetailActivity.this, ExpenseListActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Handle Back Button
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayProjectDetails() {
        // We'll need to add getProjectById to the dbHelper later
        // For now, let's assume we have it
        Project project = dbHelper.getProjectById(projectId);

        if (project != null) {
            ((TextView) findViewById(R.id.detTitle)).setText(project.getName());
            ((TextView) findViewById(R.id.detCode)).setText(project.getProjectCode());
            ((TextView) findViewById(R.id.detDesc)).setText(project.getDescription());
            ((TextView) findViewById(R.id.detStart)).setText(project.getStartDate());
            ((TextView) findViewById(R.id.detEnd)).setText(project.getEndDate());
            ((TextView) findViewById(R.id.detManager)).setText(project.getManager());
            ((TextView) findViewById(R.id.detStatus)).setText(project.getStatus());
            ((TextView) findViewById(R.id.detBudget)).setText("£" + String.format("%.2f", project.getBudget()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, EditProjectActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete this project and ALL its expenses?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteProject(projectId);
                    android.widget.Toast.makeText(this, "Project deleted", android.widget.Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}