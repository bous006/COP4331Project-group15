package group15.cop4331project;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import group15.cop4331project.data.ReportContract;

/**
 * Created by bous006 on 4/10/2017.
 */

public class CreateReportPDF {

    /** Identifier for the report data loader
    private static final int EXISTING_REPORT_LOADER = 0;


    private Uri mCurrentReportUri;

    public void createPDF (Uri currentReportUri){

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the viewer shows all report attributes, define a projection that contains
        // all columns from the report table
        String[] projection = {
                ReportContract.ReportEntry._ID,
                ReportContract.ReportEntry.COLUMN_REPORT_NAME,
                ReportContract.ReportEntry.COLUMN_REPORT_TYPE,
                ReportContract.ReportEntry.COLUMN_REPORT_DATE,
                ReportContract.ReportEntry.COLUMN_REPORT_DESCRIPTION};

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
            int nameColumnIndex = cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_REPORT_NAME);
            int typeColumnIndex = cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_REPORT_TYPE);
            int dateColumnIndex = cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_REPORT_DATE);
            int descriptionColumnIndex = cursor.getColumnIndex(ReportContract.ReportEntry.COLUMN_REPORT_DESCRIPTION);

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
            int month = calendar.get(calendar.MONTH)+1;
            int day = calendar.get(calendar.DAY_OF_MONTH);
            int year = calendar.get(calendar.YEAR);
            showDate(year, month, day);
            //showDate(1970, 0, 0);
            mDescriptionText.setText(description);

            switch (type) {
                case ReportContract.ReportEntry.TYPE_THEFT:
                    mTypeText.setText(R.string.type_theft);
                    break;
                case ReportContract.ReportEntry.TYPE_ASSAULT:
                    mTypeText.setText(R.string.type_assault);
                    break;
                case ReportContract.ReportEntry.TYPE_VANDALISM:
                    mTypeText.setText(R.string.type_vandalism);
                    break;
                default:
                    mTypeText.setText(R.string.type_general);
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

    private void showDate(int year, int month, int day) {
        mDateText.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }*/
}
