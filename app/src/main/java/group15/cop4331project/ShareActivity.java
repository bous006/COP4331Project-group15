package group15.cop4331project;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import group15.cop4331project.data.ReportContract;
import group15.cop4331project.data.UsersContract;

public class ShareActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the report data loader
    private static final int USER_LOADER = 0;

    /** Identifier for the report data loader */
    private static final int EXISTING_REPORT_LOADER = 0;

    /** Content URI for the existing report (null if it's a new report) */
    private Uri mCurrentReportUri;

    //adapter for the ListView
    UserCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setTitle("Share Report");

        // Find the ListView which will be populated with the user data
        ListView ListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        ListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of user data in the Cursor.
        // There is no user data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new UserCursorAdapter(this, null);
        ListView.setAdapter(mCursorAdapter);

        Intent intent = getIntent();
        mCurrentReportUri = intent.getData();

        // Setup the button's onClickListener


        // Setup the item click listener
        /**ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent user_intent = new Intent(ShareActivity.this, EditUserActivity.class);

                Uri currentUserUri = ContentUris.withAppendedId(UsersContract.UsersEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                user_intent.setData(currentUserUri);

                // Launch the {@link EditorActivity} to display the data for the current report.
                startActivity(user_intent);

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","abc@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Share report..."));
            }
        });*/

        // Setup the item click listener
        ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                int access = UserDataHolder.getCurrentUserAccess();
                int admin = UsersContract.UsersEntry.TYPE_ADMIN;
                if (access == admin) {
                    // Create new intent to go to {@link EditorActivity}
                    Intent user_intent = new Intent(ShareActivity.this, EditUserActivity.class);

                    Uri currentUserUri = ContentUris.withAppendedId(UsersContract.UsersEntry.CONTENT_URI, id);

                    // Set the URI on the data field of the intent
                    user_intent.setData(currentUserUri);

                    // Launch the {@link EditorActivity} to display the data for the current report.
                    startActivity(user_intent);
                    return true;
                } else
                    return true;
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(USER_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_insert_dummy_admin:
                insertUserAdmin();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_insert_dummy_user:
                insertUserGeneral();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_insert_dummy_viewer:
                insertUserViewer();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_users:
                deleteAllUsers();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertUserAdmin() {
        // Create a ContentValues object where column names are the keys,
        // and report attributes are the values.

        ContentValues values = new ContentValues();
        values.put(UsersContract.UsersEntry.COLUMN_NAME, "Bilbo Baggins");
        values.put(UsersContract.UsersEntry.COLUMN_EMAIL, "bilbo@baggins.com");
        values.put(UsersContract.UsersEntry.COLUMN_USER_TYPE, UsersContract.UsersEntry.TYPE_ADMIN);

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link report#CONTENT_URI} to indicate that we want to insert
        // into the reports database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(UsersContract.UsersEntry.CONTENT_URI, values);
    }

    private void insertUserGeneral() {
        // Create a ContentValues object where column names are the keys,
        // and report attributes are the values.

        ContentValues values = new ContentValues();
        values.put(UsersContract.UsersEntry.COLUMN_NAME, "Samwise Gamgee");
        values.put(UsersContract.UsersEntry.COLUMN_EMAIL, "sam@gamgee.com");
        values.put(UsersContract.UsersEntry.COLUMN_USER_TYPE, UsersContract.UsersEntry.TYPE_USER);

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link report#CONTENT_URI} to indicate that we want to insert
        // into the reports database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(UsersContract.UsersEntry.CONTENT_URI, values);
    }

    private void insertUserViewer() {
        // Create a ContentValues object where column names are the keys,
        // and report attributes are the values.

        ContentValues values = new ContentValues();
        values.put(UsersContract.UsersEntry.COLUMN_NAME, "Frodo Baggins");
        values.put(UsersContract.UsersEntry.COLUMN_EMAIL, "frodo@baggins.com");
        values.put(UsersContract.UsersEntry.COLUMN_USER_TYPE, UsersContract.UsersEntry.TYPE_THIRD_PARTY);

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link report#CONTENT_URI} to indicate that we want to insert
        // into the reports database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(UsersContract.UsersEntry.CONTENT_URI, values);
    }

    private void deleteAllUsers() {
        int rowsDeleted = getContentResolver().delete(UsersContract.UsersEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from report database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                UsersContract.UsersEntry._UID,
                UsersContract.UsersEntry.COLUMN_NAME,
                UsersContract.UsersEntry.COLUMN_USER_TYPE,
                UsersContract.UsersEntry.COLUMN_EMAIL };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                UsersContract.UsersEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
