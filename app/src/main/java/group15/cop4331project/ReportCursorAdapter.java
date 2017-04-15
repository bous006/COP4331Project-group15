package group15.cop4331project;

import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import group15.cop4331project.data.ReportContract.ReportEntry;

import static group15.cop4331project.R.id.date;

/**
 * Created by bous006 on 3/4/2017.
 */

public class ReportCursorAdapter extends CursorAdapter {

    private Calendar calendar;

    private TextView dateTextView;

    public ReportCursorAdapter(Context context, Cursor c){ super(context, c, 0 /*flags*/);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView typeTextView = (TextView) view.findViewById(R.id.type);
        dateTextView = (TextView) view.findViewById(date);

        // Find the columns of report attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_NAME);
        int typeColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_TYPE);
        //int dateColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_DATE);
        int descColumnIndex = cursor.getColumnIndex(ReportEntry.COLUMN_REPORT_DESCRIPTION);

        // Read the report attributes from the Cursor for the current report
        String reportName = cursor.getString(nameColumnIndex);
        String reportDesc = cursor.getString(descColumnIndex);
        /**For some reason dateColumnIndex is returning -1
        Long reportDate = cursor.getLong(-1);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reportDate);

        int month = calendar.get(calendar.MONTH)+1;
        int day = calendar.get(calendar.DAY_OF_MONTH);
        int year = calendar.get(calendar.YEAR);
        showDate(year, month, day);*/

        /**For some reason typeColumnIndex is returning -1 instead of 2 when there is no more data
         * so for now I'm hardcoding 2 into here*/
        int reportType = cursor.getInt(2);
        String typeString;

        if (reportType == 1) {
            typeString = "Theft";
        } else if (reportType == 2) {
            typeString = "Assault";
        } else if (reportType == 3) {
            typeString = "Vandalism";
        } else {
            typeString = "General";
        }
        // Update the TextViews with the attributes for the current report
        nameTextView.setText(reportName);
        typeTextView.setText(typeString);
    }

    private void showDate(int year, int month, int day) {
        dateTextView.setText(new StringBuilder().append(month).append("/")
                .append(day).append("/").append(year));
    }
}
