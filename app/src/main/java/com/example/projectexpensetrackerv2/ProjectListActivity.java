package com.example.projectexpensetrackerv2; // Package name for the app

// Import all the tools needed for Android UI, navigation, and data
import android.content.Context;           // App environment info
import android.content.Intent;            // Used to "Move" between screens
import android.os.Bundle;                // Basic setup data for an Activity
import android.view.LayoutInflater;      // Tool to turn XML layout files into real UI items
import android.view.View;                // The base of all UI elements
import android.view.ViewGroup;           // A container for other UI elements
import android.widget.ArrayAdapter;     // A bridge between a list of data and a ListView
import android.widget.ImageView;        // Displays pictures/icons
import android.widget.ListView;         // Displays a rolling list of items
import android.widget.TextView;         // Displays text
import android.widget.Toast;            // Shows small popup messages at the bottom
import androidx.appcompat.app.AlertDialog; // Shows popup boxes with buttons
import androidx.appcompat.app.AppCompatActivity; // The base class for modern Android screens
import androidx.appcompat.widget.SearchView; // The search bar tool
import java.util.List;                  // Standard Java list

/**
 * PROJECT LIST ACTIVITY: This is the main screen that shows all your projects.
 * It has a search bar at the top and a button to add new projects.
 * It also handles backing up everything to the cloud.
 */
public class ProjectListActivity extends AppCompatActivity {

    private ListView listView;      // The UI element that holds the list of projects
    private DbHelper db;            // Our connection to the phone's database
    private FirebaseHelper firebase; // Our connection to the online cloud database
    private ProjectAdapter adapter; // The bridge that fills the ListView with project data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);      // Basic Android setup
        setContentView(R.layout.project_list);   // Tell Android to use the project_list.xml layout

        db = new DbHelper(this);                // Link to the local database
        firebase = new FirebaseHelper(this, db); // Link to the cloud backup tool
        listView = findViewById(R.id.listViewProjects); // Find the list UI on the screen
        
        // If the list is empty, show a text message (tvEmpty) to the user
        listView.setEmptyView(findViewById(R.id.tvEmpty));

        setupSearch(); // Run the function to set up the search bar

        // ADD BUTTON CLICK: When the "+" button is clicked, go to the Add Screen
        findViewById(R.id.btnAddProject).setOnClickListener(v -> {
            startActivity(new Intent(this, AddEditProjectActivity.class));
        });

        // Set the toolbar at the top of the screen
        setSupportActionBar(findViewById(R.id.toolbarList));
    }

    // This function sets up the search bar logic
    private void setupSearch() {
        SearchView searchView = findViewById(R.id.searchViewProjects); // Find search UI
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) { 
                updateList(q); // When user presses "Search", filter the list
                return true; 
            }
            @Override
            public boolean onQueryTextChange(String q) { 
                updateList(q); // As the user types, filter the list instantly
                return true; 
            }
        });
    }

    // This function refreshes the list with new data from the database
    private void updateList(String query) {
        // If search box is empty, get all projects. Otherwise, search for ones matching the query.
        List<Models.Project> list = (query == null || query.isEmpty()) 
                ? db.getAllProjects() : db.searchProjects(query);
                
        // Create a new adapter with our list of projects
        adapter = new ProjectAdapter(this, list);
        // Link the adapter to our ListView to show them on screen
        listView.setAdapter(adapter);
    }

    // This runs every time we come back to this screen (e.g. after adding a project)
    @Override
    protected void onResume() {
        super.onResume();
        updateList(null); // Refresh the list to show any new changes
    }

    // Creates the menu in the top corner of the screen
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Use the list_menu.xml file to build the menu
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    // Handles clicks on menu items (like the Backup button)
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_backup) { // If Backup is clicked...
            Toast.makeText(this, "Backing up...", Toast.LENGTH_SHORT).show();
            // Start the cloud backup process
            firebase.uploadAllData(new FirebaseHelper.BackupCallback() {
                @Override public void onSuccess() { 
                    Toast.makeText(ProjectListActivity.this, "Backup Success!", Toast.LENGTH_SHORT).show(); 
                }
                @Override public void onFailure(String err) { 
                    Toast.makeText(ProjectListActivity.this, "Error: " + err, Toast.LENGTH_LONG).show(); 
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * INNER ADAPTER CLASS: This "paints" each project into a row on the screen.
     */
    private class ProjectAdapter extends ArrayAdapter<Models.Project> {
        public ProjectAdapter(Context context, List<Models.Project> objects) {
            super(context, 0, objects); // Initialize the adapter
        }

        // This function builds the UI for one single project row
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Models.Project p = getItem(position); // Get the project at this spot in the list
            
            // If the row hasn't been created yet, make a new one from project_item.xml
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.project_item, parent, false);
            }

            // Fill in the row's text with the project's data
            ((TextView) convertView.findViewById(R.id.tvProjectName)).setText(p.getName());
            ((TextView) convertView.findViewById(R.id.tvProjectCode)).setText("Code: " + p.getProjectCode());
            ((TextView) convertView.findViewById(R.id.tvStatus)).setText("Status: " + p.getStatus());
            ((TextView) convertView.findViewById(R.id.tvBudget)).setText("£" + String.format("%.2f", p.getBudget()));

            // ROW CLICK: Clicking anywhere on the project row opens the Edit screen
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddEditProjectActivity.class);
                intent.putExtra("ID", p.getId()); // Pass the project ID so the next screen knows which one to load
                getContext().startActivity(intent);
            });

            // EDIT ICON: Clicking the pencil icon also opens the Edit screen
            convertView.findViewById(R.id.ivEdit).setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddEditProjectActivity.class);
                intent.putExtra("ID", p.getId());
                getContext().startActivity(intent);
            });

            // DELETE ICON: Clicking the trash can shows a "Are you sure?" popup
            convertView.findViewById(R.id.ivDelete).setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete?") // Popup Title
                        .setMessage("Delete " + p.getName() + "?") // Popup Message
                        .setPositiveButton("Yes", (d, w) -> {
                            db.deleteProject(p.getId()); // Delete from database
                            updateList(null); // Refresh the screen
                        })
                        .setNegativeButton("No", null).show(); // Close popup if No is clicked
            });

            return convertView; // Return the finished row UI to be displayed
        }
    }
}