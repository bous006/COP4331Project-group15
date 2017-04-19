package group15.cop4331project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import group15.cop4331project.data.ReportContract.ReportEntry;

/**
 * Database helper. Manages Database creation and version management
 */

public class ReportDbHelper extends SQLiteOpenHelper{

    /** Name of the database file */
    private static final String DATABASE_NAME = "reports.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * constructor for ReportDbHelper
     */
    public ReportDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the reports table
        String SQL_CREATE_Reports_TABLE =  "CREATE TABLE " + ReportEntry.TABLE_NAME + " ("
                + ReportEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReportEntry.COLUMN_REPORT_NAME + " TEXT NOT NULL, "
                + ReportEntry.COLUMN_REPORT_TYPE + " INTEGER NOT NULL, "
                + ReportEntry.COLUMN_REPORT_DATE + " LONG NOT NULL, "
                + ReportEntry.COLUMN_REPORT_LOCATION + " TEXT NOT NULL, "
                + ReportEntry.COLUMN_REPORT_DESCRIPTION + " TEXT NOT NULL, "
                + ReportEntry.COLUMN_REPORTER_NAME + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_Reports_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
