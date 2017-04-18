package group15.cop4331project;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import group15.cop4331project.data.ReportContract;
import group15.cop4331project.data.ReportContract.ReportEntry;

import static android.R.attr.id;

/**
 * Created by bous006 on 3/5/2017.
 */

public class ViewReportActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the report data loader */
    private static final int EXISTING_REPORT_LOADER = 0;

    /** Content URI for the existing report (null if it's a new report) */
    private Uri mCurrentReportUri;

    private TextView mNameText;

    private TextView mDescriptionText;

    private TextView mDateText;

    private TextView mTypeText;

    private static boolean verify;

    private static String reportName;

    private static String reportDesc;

    private static String reportDate;

    private static String reportType;

    private static Uri cachedReportUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new report or editing an existing one.
        Intent intent = getIntent();
        mCurrentReportUri = intent.getData();
        cachedReportUri = mCurrentReportUri;

        // Find all relevant views that we will need to read user input from
        mNameText = (TextView) findViewById(R.id.view_report_name);
        mDescriptionText = (TextView) findViewById(R.id.view_report_description);
        mDateText = (TextView) findViewById(R.id.view_report_date);
        mTypeText = (TextView) findViewById(R.id.view_report_type);

        if (!verify){
            // Initialize a loader to read the report data from the database
            // and display the current values
            getLoaderManager().initLoader(EXISTING_REPORT_LOADER, null, this);
            verify = true;
        } else {
            mNameText.setText(reportName);
            mNameText.setPaintFlags(mNameText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mDescriptionText.setText(reportDesc);
            mDateText.setText(reportDate);
            mTypeText.setText(reportType);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_view, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    private void editReport() {
         // Create new intent to go to {@link EditorActivity}
         Intent intent = new Intent(this, EditReportActivity.class);

         //Uri currentReportUri = ContentUris.withAppendedId(ReportContract.ReportEntry.CONTENT_URI, id);

         // Set the URI on the data field of the intent
         intent.setData(mCurrentReportUri);

         // Launch the {@link EditorActivity} to display the data for the current report.
         startActivity(intent);
    }

    private void shareReport() {
        // Create new intent to go to {@link ShareActivity}
        Intent intent = new Intent(this, ShareActivity.class);

        //Uri mCurrentReportUri = ContentUris.withAppendedId(ReportContract.ReportEntry.CONTENT_URI, id);

        // Set the URI on the data field of the intent
        intent.setData(mCurrentReportUri);

        // Launch the {@link EditorActivity} to display the data for the current report.
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // User clicked on a menu option in the app bar overflow menu
         switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_edit:
                // Edit Report
                if (UserDataHolder.getCurrentUserAccess() == 0) {
                    Toast.makeText(this, getString(R.string.editor_access_denied),
                            Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    editReport();
                    // Exit activity
                    finish();
                    return true;
                }
            // Respond to a click on the "Share" menu option
            case R.id.action_share:
                // Edit Report
                if (UserDataHolder.getCurrentUserAccess() == 0) {
                    Toast.makeText(this, getString(R.string.share_access_denied),
                            Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    shareReport();
                    // Exit activity
                    return true;
                }
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                if (UserDataHolder.getCurrentUserAccess() == 0) {
                    Toast.makeText(this, getString(R.string.delete_access_denied),
                            Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    // Pop up confirmation dialog for deletion
                    showDeleteConfirmationDialog();
                    return true;
                }
         }
         return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the viewer shows all report attributes, define a projection that contains
        // all columns from the report table
        String[] projection = {
                ReportEntry._ID,
                ReportEntry.COLUMN_REPORT_NAME,
                ReportEntry.COLUMN_REPORT_TYPE,
                ReportEntry.COLUMN_REPORT_DATE,
                ReportEntry.COLUMN_REPORT_DESCRIPTION};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentReportUri,      // Query the content URI for the current report
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
                // Find the columns of report attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_NAME);
            int typeColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_TYPE);
            int dateColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_DATE);
            int descriptionColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_DESCRIPTION);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            int type = cursor.getInt(typeColumnIndex);
            long date = cursor.getLong(dateColumnIndex);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);


            // Update the views on the screen with the values from the database
            mNameText.setText(name);
            mNameText.setPaintFlags(mNameText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            reportName = name;
            int month = calendar.get(calendar.MONTH)+1;
            int day = calendar.get(calendar.DAY_OF_MONTH);
            int year = calendar.get(calendar.YEAR);
            showDate(year, month, day);
            //showDate(1970, 0, 0);
            mDescriptionText.setText(description);
            reportDesc = description;

            switch (type) {
                case ReportEntry.TYPE_THEFT:
                    mTypeText.setText(R.string.type_theft);
                    reportType = "Theft";
                    break;
                case ReportEntry.TYPE_ASSAULT:
                    mTypeText.setText(R.string.type_assault);
                    reportType = "Assault";
                    break;
                case ReportEntry.TYPE_VANDALISM:
                    mTypeText.setText(R.string.type_vandalism);
                    reportType = "Vandalism";
                    break;
                default:
                    mTypeText.setText(R.string.type_general);
                    reportType = "General";
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameText.setText("");
        mDescriptionText.setText("");
        showDate(1970, 0, 0);
        mTypeText.setText(R.string.type_general);
    }


    /**
     * Prompt the user to confirm that they want to delete this report.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the report.
                deleteReport();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the report.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the report in the database.
     */
    private void deleteReport() {
        // Only perform the delete if this is an existing report.
        if (cachedReportUri != null) {
            // Call the ContentResolver to delete the report at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentReportUri
            // content URI already identifies the report that we want.
            int rowsDeleted = getContentResolver().delete(cachedReportUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_report_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_report_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    private void showDate(int year, int month, int day) {
        reportDate = new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year).toString();
        mDateText.setText(reportDate);
    }
}
