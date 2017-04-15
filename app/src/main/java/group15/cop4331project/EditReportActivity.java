package group15.cop4331project;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import group15.cop4331project.data.ReportContract.ReportEntry;

/**
 * Created by bous006 on 3/4/2017.
 */

public class EditReportActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = EditReportActivity.class.getSimpleName();

    //TODO: if the user edits the report, but doesn't change the date then the date goes to the default... fix this

    private DatePicker datePicker;
    private TextView mDateView;
    private Calendar calendar;
    private int year, month, day;

    /** Identifier for the report data loader */
    private static final int EXISTING_REPORT_LOADER = 0;

    /** Content URI for the existing report (null if it's a new report) */
    private Uri mCurrentReportUri;

    /** EditText field to enter the report's name */
    private EditText mNameEditText;

    /** EditText field to enter the report's description */
    private EditText mDescriptionEditText;

    /**Use a spinner for report type*/
    private Spinner mTypeSpinner;
    private int mType = ReportEntry.TYPE_GENERAL;

    /**Report Date*/
    private long mDate;

    /** Boolean flag that keeps track of whether the report has been edited (true) or not (false) */
    private boolean mReportHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mReportHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mReportHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report);

        /**Set up the date picker*/
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new report or editing an existing one.
        Intent intent = getIntent();
        mCurrentReportUri = intent.getData();

        // If the intent DOES NOT contain a report content URI, then we know that we are
        // creating a new report.
        if (mCurrentReportUri == null) {
            // This is a new report, so change the app bar to say "Add a report"
            setTitle(getString(R.string.editor_activity_title_new_report));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a report that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing report, so change app bar to say "Edit report"
            setTitle(getString(R.string.editor_activity_title_edit_report));

            // Initialize a loader to read the report data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_REPORT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_report_name);
        mDescriptionEditText = (EditText) findViewById(R.id.edit_report_description);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        mDateView = (TextView) findViewById(R.id.date_view);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mDescriptionEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mDateView.setOnTouchListener(mTouchListener);


        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the report.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(typeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_theft))) {
                        mType = ReportEntry.TYPE_THEFT;
                    } else if (selection.equals(getString(R.string.type_assault))) {
                        mType = ReportEntry.TYPE_ASSAULT;
                    } else if (selection.equals(getString(R.string.type_vandalism))) {
                        mType = ReportEntry.TYPE_VANDALISM;
                    } else {
                        mType = ReportEntry.TYPE_GENERAL;
                    }
                }
            }
            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = ReportEntry.TYPE_GENERAL;
            }
        });
    }

    /**
     * Get user input from editor and save report into database.
     */
    private void saveReport() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String descriptionString = mDescriptionEditText.getText().toString().trim();
        //Long dateLong = mDateView.get

        // Check if this is supposed to be a new report
        // and check if all the fields in the editor are blank
        if (mCurrentReportUri == null &&
                TextUtils.isEmpty(nameString) && mType == ReportEntry.TYPE_GENERAL
                && TextUtils.isEmpty(descriptionString)){
            // Since no fields were modified, we can return early without creating a new report.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and report attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(ReportEntry.COLUMN_REPORT_NAME, nameString);
        values.put(ReportEntry.COLUMN_REPORT_DESCRIPTION, descriptionString);
        values.put(ReportEntry.COLUMN_REPORT_TYPE, mType);
        values.put(ReportEntry.COLUMN_REPORT_DATE, mDate);

        // Determine if this is a new or existing report by checking if mCurrentReportUri is null or not
        if (mCurrentReportUri == null) {
            // This is a NEW report, so insert a new report into the provider,
            // returning the content URI for the new report.
            Uri newUri = getContentResolver().insert(ReportEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_report_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_report_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING report, so update the report with content URI: mCurrentReportUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentReportUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentReportUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_report_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_report_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new report, hide the "Delete" menu item.
        if (mCurrentReportUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save report to database
                saveReport();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the report hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mReportHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditReportActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditReportActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the report hasn't changed, continue with handling back button press
        if (!mReportHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all report attributes, define a projection that contains
        // all columns from the report table
        String[] projection = {
                ReportEntry._ID,
                ReportEntry.COLUMN_REPORT_NAME,
                ReportEntry.COLUMN_REPORT_TYPE,
                ReportEntry.COLUMN_REPORT_DATE,
                ReportEntry.COLUMN_REPORT_DESCRIPTION};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentReportUri,         // Query the content URI for the current report
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
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);


            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            int month = calendar.get(calendar.MONTH)+1;
            int day = calendar.get(calendar.DAY_OF_MONTH);
            int year = calendar.get(calendar.YEAR);
            showDate(year, month, day);
            //showDate(1970, 0, 0);
            mDescriptionEditText.setText(description);

            switch (type) {
                case ReportEntry.TYPE_THEFT:
                    mTypeSpinner.setSelection(1);
                    break;
                case ReportEntry.TYPE_ASSAULT:
                    mTypeSpinner.setSelection(2);
                    break;
                case ReportEntry.TYPE_VANDALISM:
                    mTypeSpinner.setSelection(3);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mDescriptionEditText.setText("");
        showDate(1970, 0, 0);
        mTypeSpinner.setSelection(0);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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
     * Prompt the user to confirm that they want to delete this report.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
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
        if (mCurrentReportUri != null) {
            // Call the ContentResolver to delete the report at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentReportUri
            // content URI already identifies the report that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentReportUri, null, null);

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

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(arg1, arg2, arg3);
                    mDate = calendar.getTimeInMillis();

                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        mDateView.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }
}
