package group15.cop4331project;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import group15.cop4331project.data.ReportContract.ReportEntry;
import group15.cop4331project.data.UsersContract.UsersEntry;


/**
 * This activity hold the "My Reports" and "Recent Reports" fragments
 * Using fragments allows us to swipe between these pages making the UI more user friendly
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleApiClient mGoogleApiClient;
    private String logInEmail;
    private String logInName;
    private int logInType;
    private int logInUID;

    //The context of the app
    private Context mContext;

    /** URI matcher code for the content URI for the reports table */
    private static final int REPORTS = 100;

    /** URI matcher code for the content URI for a single report in the reports table */
    private static final int REPORT_ID = 101;

    /** URI matcher code for the content URI for the users table */
    private static final int USERS = 200;

    /** URI matcher code for the content URI for a single user in the users table */
    private static final int USER_UID = 201;

    /** Identifier for the report data loader */
    private static final int EXISTING_USER_LOADER = 0;

    /**Set to true onCreate and to false on onStop. this way we do not try and re-verify the user every time they go back the main activity*/
    private static boolean verify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        //Create an Adapter that knows which fragment should be on which page
        PageAdapter adapter = new PageAdapter(this, getSupportFragmentManager());

        //Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        //Find the tab layout that shows the tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        tabLayout.setupWithViewPager(viewPager);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        if (!verify) {
            logInName = getIntent().getStringExtra("name");
            logInEmail = getIntent().getStringExtra("email");

            UserDataHolder.setCurrentUserName(logInName);
            setTitle(logInName);

            verifyUser();
            verify = true;
        } else {
            setTitle(UserDataHolder.getCurrentUserName());
        }
    }
    /**
     * This is really just for us so that we can clear the db easily
     */
    private void deleteAllReports() {
        int rowsDeleted = getContentResolver().delete(ReportEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from report database");
    }

    private void deleteAllUsers() {
        int rowsDeleted = getContentResolver().delete(UsersEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from users database");
    }

    /**
     * Sign Out
     */
    private void signOut() {
        verify = false;
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),LogInActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertReport();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllReports();
                return true;
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
            // Respond to a click on the "Sign Out" menu option
            case R.id.action_sign_out:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertReport() {
        // Create a ContentValues object where column names are the keys,
        // and report attributes are the values.
        Calendar calendar = Calendar.getInstance();
        Long date = calendar.getTimeInMillis();
        
        ContentValues values = new ContentValues();
        values.put(ReportEntry.COLUMN_REPORT_NAME, "Kid Pooped on Table");
        values.put(ReportEntry.COLUMN_REPORT_TYPE, ReportEntry.TYPE_ASSAULT);
        values.put(ReportEntry.COLUMN_REPORT_DATE, date);
        values.put(ReportEntry.COLUMN_REPORT_DESCRIPTION, "There was this kid and he pooped on a table.");
        values.put(ReportEntry.COLUMN_REPORTER_NAME, "Gandalf");
        values.put(ReportEntry.COLUMN_REPORT_LOCATION, "UCF");

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link report#CONTENT_URI} to indicate that we want to insert
        // into the reports database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(ReportEntry.CONTENT_URI, values);
    }

    private void insertUserAdmin() {
        // Create a ContentValues object where column names are the keys,
        // and report attributes are the values.

        ContentValues values = new ContentValues();
        values.put(UsersEntry.COLUMN_NAME, "Bilbo Baggins");
        values.put(UsersEntry.COLUMN_EMAIL, "bilbo@baggins.com");
        values.put(UsersEntry.COLUMN_USER_TYPE, UsersEntry.TYPE_ADMIN);

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link report#CONTENT_URI} to indicate that we want to insert
        // into the reports database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(UsersEntry.CONTENT_URI, values);
    }

    private void insertUserGeneral() {
        // Create a ContentValues object where column names are the keys,
        // and report attributes are the values.

        ContentValues values = new ContentValues();
        values.put(UsersEntry.COLUMN_NAME, "Samwise Gamgee");
        values.put(UsersEntry.COLUMN_EMAIL, "sam@gamgee.com");
        values.put(UsersEntry.COLUMN_USER_TYPE, UsersEntry.TYPE_USER);

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link report#CONTENT_URI} to indicate that we want to insert
        // into the reports database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(UsersEntry.CONTENT_URI, values);
    }

    private void insertUserViewer() {
        // Create a ContentValues object where column names are the keys,
        // and report attributes are the values.

        ContentValues values = new ContentValues();
        values.put(UsersEntry.COLUMN_NAME, "Other Hobbit");
        values.put(UsersEntry.COLUMN_EMAIL, "frodo@baggins.com");
        values.put(UsersEntry.COLUMN_USER_TYPE, UsersEntry.TYPE_THIRD_PARTY);

        // Insert a new row into the provider using the ContentResolver.
        // Use the {@link report#CONTENT_URI} to indicate that we want to insert
        // into the reports database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(UsersEntry.CONTENT_URI, values);
    }

    @Override
    protected void onStart() {

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

        if (!verify) {
            logInName = getIntent().getStringExtra("name");
            logInEmail = getIntent().getStringExtra("email");

            setTitle(logInName);

            verifyUser();
            verify = true;
        } else {
            setTitle(UserDataHolder.getCurrentUserName());
        }*/

        super.onStart();
    }

    public void verifyUser() {

        // Initialize a loader to read the user data from the database
        getLoaderManager().initLoader(EXISTING_USER_LOADER, null, this);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Create uri to search for the user with email of logInEmail
        Uri userUri = Uri.withAppendedPath(UsersEntry.CONTENT_URI, logInEmail);
        String testString = userUri.toString();

        Log.v("MainActivity", testString);

        // Create the projection for the query. We only really need the user's email, but we can get UID just in case
        String[] projection = {
                UsersEntry._UID,
                UsersEntry.COLUMN_EMAIL,
                UsersEntry.COLUMN_USER_TYPE,
                UsersEntry.COLUMN_NAME};

        String selection = UsersEntry.COLUMN_EMAIL + "=?";
        String selectionArgs[] = {logInEmail};

        // This loader will execute the ContentProvider's query method on a background thread
        return new android.content.CursorLoader(this,   // Parent activity context
                UsersEntry.CONTENT_URI,      // Query the content URI for the current user
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
                selectionArgs,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // if we are adding a new user
        if (cursor == null || cursor.getCount() < 1) {

            ContentValues values = new ContentValues();
            values.put(UsersEntry.COLUMN_NAME, logInName);
            values.put(UsersEntry.COLUMN_EMAIL, logInEmail);

            if (logInName.equals("Stephen Murphy") || logInName.equals("Josh Ackerman") || logInName.equals("Yanni Guenov") || logInName.equals("Alex Arwin")) {
                values.put(UsersEntry.COLUMN_USER_TYPE, UsersEntry.TYPE_ADMIN);
            } else {
                values.put(UsersEntry.COLUMN_USER_TYPE, UsersEntry.TYPE_THIRD_PARTY);
            }

            logInType = UsersEntry.TYPE_THIRD_PARTY;

            // Insert a new row into the provider using the ContentResolver.
            // Use the {@link report#CONTENT_URI} to indicate that we want to insert
            // into the reports database table.
            // Receive the new content URI that will allow us to access Toto's data in the future.
            Uri newUri = getContentResolver().insert(UsersEntry.CONTENT_URI, values);

            UserDataHolder.setCurrentUserData(0, logInName, logInEmail, logInType);

            return;
        } else {

            if (cursor.moveToFirst()) {
                // Find the columns of user attributes that we're interested in
                int nameColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_NAME);
                int emailColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_EMAIL);
                int typeColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_TYPE);
                int UIDColumnIndex = cursor.getColumnIndex(UsersEntry._UID);

                // Extract out the value from the Cursor for the given column index
                int type = cursor.getInt(typeColumnIndex);
                logInUID = cursor.getInt(UIDColumnIndex);
                logInName = cursor.getString(nameColumnIndex);
                logInEmail = cursor.getString(emailColumnIndex);

                switch (type) {
                    case UsersEntry.TYPE_ADMIN:
                        logInType = UsersEntry.TYPE_ADMIN;
                        break;
                    case UsersEntry.TYPE_USER:
                        logInType = UsersEntry.TYPE_USER;
                        break;
                    default:
                        logInType = UsersEntry.TYPE_THIRD_PARTY;
                        break;
                }
            }

            UserDataHolder.setCurrentUserData(logInUID, logInName, logInEmail, logInType);
            return;
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
    }

}
