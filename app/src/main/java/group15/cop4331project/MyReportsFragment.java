package group15.cop4331project;

import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.content.ContentUris;
import android.support.v4.content.CursorLoader;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import group15.cop4331project.data.ReportContract.ReportEntry;
import group15.cop4331project.data.UsersContract;

public class MyReportsFragment extends android.support.v4.app.Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //Identifier for the report data loader
    private static final int REPORT_LOADER = 0;

    //adapter for the ListView
    ReportCursorAdapter mCursorAdapter;

    View rootView;

    public MyReportsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.report_list, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditReportActivity.class);

                if (UserDataHolder.getCurrentUserAccess() == 0) {
                    Toast.makeText(getActivity(), getString(R.string.create_access_denied),
                            Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(intent);
                }
            }
        });

        // Find the listview which will be populated with the report data
        ListView listView = (ListView) rootView.findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of  data in the Cursor.
        mCursorAdapter = new ReportCursorAdapter(getActivity(), null);
        listView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(getContext(), ViewReportActivity.class);

                Uri currentReportUri = ContentUris.withAppendedId(ReportEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentReportUri);

                // Launch the {@link EditorActivity} to display the data for the current report.
                startActivity(intent);
            }
        });



        /*if (UserDataHolder.getCurrentUserAccess() == 0) {
            fab.setVisibility(View.INVISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public void onResume() {
        // Kick off the loader
        getLoaderManager().initLoader(REPORT_LOADER, null, this);
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ReportEntry._ID,
                ReportEntry.COLUMN_REPORT_NAME,
                ReportEntry.COLUMN_REPORT_TYPE,
                ReportEntry.COLUMN_REPORT_DATE,
                ReportEntry.COLUMN_REPORT_DESCRIPTION,
                ReportEntry.COLUMN_REPORT_LOCATION,
                ReportEntry.COLUMN_REPORTER_NAME};

        String selection = ReportEntry.COLUMN_REPORTER_NAME + "=?";
        String currentUser = UserDataHolder.getCurrentUserName();
        String selectionArgs[] = {currentUser};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(getActivity(),   // Parent activity context
                ReportEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                selection,                   // No selection clause
                selectionArgs,                   // No selection arguments
                null);                  // Default sort order
    }
    /***/
    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
