package group15.cop4331project.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import group15.cop4331project.data.UsersContract.UsersEntry;

/**
 * Database helper. Manages Database creation and version management
 */

public class UsersDBHelper extends SQLiteOpenHelper{

    /** Name of the database file */
    private static final String DATABASE_NAME = "users.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * constructor for ReportDbHelper
     */
    public UsersDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the reports table
        String SQL_CREATE_Reports_TABLE =  "CREATE TABLE " + UsersEntry.TABLE_NAME + " ("
                + UsersEntry._UID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + UsersEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + UsersEntry.COLUMN_EMAIL + " TEXT NOT NULL, "
                + UsersEntry.COLUMN_USER_TYPE + " INTEGER NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_Reports_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
