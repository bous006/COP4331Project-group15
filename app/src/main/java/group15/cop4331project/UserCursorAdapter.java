package group15.cop4331project;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import group15.cop4331project.data.UsersContract.UsersEntry;

import static group15.cop4331project.R.id.date;

/**
 * Created by bous006 on 3/4/2017.
 */

public class UserCursorAdapter extends CursorAdapter {

    private String logInEmail;
    private String logInName;

    public UserCursorAdapter(Context context, Cursor c){ super(context, c, 0 /*flags*/);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in user_list.xml
        return LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.user_name);
        TextView typeTextView = (TextView) view.findViewById(R.id.user_access);


        // Find the columns of report attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_NAME);
        int typeColumnIndex = cursor.getColumnIndex(UsersEntry.COLUMN_USER_TYPE);

        // Read the report attributes from the Cursor for the current report
        String userName = cursor.getString(nameColumnIndex);
        String userType = cursor.getString(typeColumnIndex);

        /*For some reason typeColumnIndex is returning -1 instead of 2 when there is no more data
         * so for now I'm hardcoding 2 into here*/
        int userTypeInt = cursor.getInt(2);
        String typeString;

        if (userTypeInt == 0) {
            typeString = "VIEWER";
        } else if (userTypeInt == 1) {
            typeString = "USER";
        } else {
            typeString = "ADMIN";
        }
        // Update the TextViews with the attributes for the current report
        nameTextView.setText(userName);
        typeTextView.setText(typeString);
    }

}
