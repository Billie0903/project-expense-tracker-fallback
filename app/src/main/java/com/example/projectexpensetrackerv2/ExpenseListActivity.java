package com.example.projectexpensetrackerv2; // Package name for the app folder

// Import tools for UI, navigation, and data handling
import android.content.Context;           // Information about the app environment
import android.content.Intent;            // Used to move to other screens
import android.os.Bundle;                // Used for basic activity setup
import android.view.LayoutInflater;      // Turns XML layout into real UI components
import android.view.View;                // Base class for UI objects
import android.view.ViewGroup;           // Container for UI objects
import android.widget.ArrayAdapter;     // Bridge between list data and the ListView UI
import android.widget.ListView;         // Displays a scrolling list of items
import android.widget.TextView;         // Displays text on the screen
import android.widget.Toast;            // Small popup messages
import androidx.appcompat.app.AlertDialog; // Popup boxes with buttons
import androidx.appcompat.app.AppCompatActivity; // Base screen class
import java.util.List;                  // Standard way to store a list of objects

/**
 * EXPENSE LIST ACTIVITY: This screen shows all the money spent (expenses) for one specific project.
 * It uses a list to show the date, amount, and person who spent the money.
 */
public class ExpenseListActivity extends AppCompatActivity {

    private ListView listView;   // The UI list that shows expenses
    private DbHelper db;         // Connects to the phone's database
    private int projectId;       // Remembers which Project we are viewing expenses for

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);           // Basic Android setup
        setContentView(R.layout.activity_expense_list); // Tell Android to use activity_expense_list.xml

        db = new DbHelper(this); // Initialize the database connection
        
        // Get the Project ID from the previous screen so we know which project's expenses to load
        projectId = getIntent().getIntExtra("PROJECT_ID", -1);

        listView = findViewById(R.id.lvExpenses); // Find the list UI on the screen
        
        // ADD BUTTON CLICK: When the "+" button is clicked, go to the Add Expense screen
        findViewById(R.id.btnAddExpense).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditExpenseActivity.class);
            intent.putExtra("PROJECT_ID", projectId); // Pass the project ID forward
            startActivity(intent);
        });

        // Set up the toolbar (title bar) at the top
        setSupportActionBar(findViewById(R.id.toolbarExpenses));
        // Add a "Back" button to the toolbar
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Handles what happens when the "Back" button in the toolbar is clicked
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // Just go back to the previous screen
        return true;
    }

    // Pulls the latest expenses from the database and updates the screen
    private void updateList() {
        // Fetch all expenses linked to this project's ID
        List<Models.Expense> list = db.getExpensesForProject(projectId);
        // Create a bridge (adapter) and give it to the ListView
        listView.setAdapter(new ExpenseAdapter(this, list));
    }

    // Runs every time we return to this screen (refreshing the data)
    @Override
    protected void onResume() {
        super.onResume();
        updateList(); // Refresh the list
    }

    /**
     * INNER ADAPTER: This tells the app how each individual row in the list should look.
     */
    private class ExpenseAdapter extends ArrayAdapter<Models.Expense> {
        public ExpenseAdapter(Context context, List<Models.Expense> objects) {
            super(context, 0, objects); // Initialize the adapter
        }

        // Builds the UI for one single expense row in the list
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Models.Expense e = getItem(position); // Get the expense at this list position
            
            // If the row hasn't been made yet, create it from expense_item.xml
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_item, parent, false);
            }

            // Fill the row's text views with the expense data
            ((TextView) convertView.findViewById(R.id.tvExpDate)).setText(e.getDate()); // Set Date
            ((TextView) convertView.findViewById(R.id.tvExpType)).setText(e.getType()); // Set Category
            ((TextView) convertView.findViewById(R.id.tvExpClaimant)).setText("By: " + e.getClaimant()); // Set Claimant
            // Show the amount in GBP (after conversion calculation)
            ((TextView) convertView.findViewById(R.id.tvExpAmount)).setText("£" + String.format("%.2f", e.getAmountInGBP()));
            ((TextView) convertView.findViewById(R.id.tvExpStatus)).setText(e.getPaymentStatus()); // Set Status

            // ROW CLICK: Clicking the expense row opens it for editing
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddEditExpenseActivity.class);
                intent.putExtra("PROJECT_ID", projectId); // Remind the screen which project we are in
                intent.putExtra("EXPENSE_ID", e.getId()); // Tell the screen which expense to load
                getContext().startActivity(intent);
            });

            // DELETE ICON: Clicking the trash can shows a delete confirmation box
            convertView.findViewById(R.id.ivExpDelete).setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete?") // Popup Title
                        .setPositiveButton("Yes", (d, w) -> {
                            db.deleteExpense(e.getId()); // Remove from database
                            updateList(); // Refresh the list on screen
                        })
                        .setNegativeButton("No", null).show(); // Cancel if they say No
            });

            return convertView; // Return the finished row UI
        }
    }
}