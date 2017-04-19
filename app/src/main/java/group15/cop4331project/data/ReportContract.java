package group15.cop4331project.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bous006 on 3/4/2017.
 */

public class ReportContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ReportContract() {}

    /**
     * content authority for the content provider uri
     */
    public static final String CONTENT_AUTHORITY = "group15.cop4331project";

    /**
     * base content uri. aka, content uri without path
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * path for content uri
     */
    public static final String PATH_REPORTS = "reports";

    /**
     * Inner Class that defines constant values for Report Database table
     * each entry in the table represents a single report
     */
    public static final class ReportEntry implements BaseColumns {

        /** Name of database table for report */
        public final static String TABLE_NAME = "reports";

        /**
         * Unique ID number for the report (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the report.
         *
         * Type: TEXT
         */
        public final static String COLUMN_REPORT_NAME ="name";

        /**
         * Type of the report.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_REPORT_TYPE = "type";

        /**
         * Date of the report.
         *
         * Type: LONG
         */
        public final static String COLUMN_REPORT_DATE = "date";

        /**
         * Description of the report.
         *
         * Type: TEXT
         */
        public final static String COLUMN_REPORT_DESCRIPTION = "description";

        /**
         * Location of the report.
         *
         * Type: TEXT
         */
        public final static String COLUMN_REPORT_LOCATION = "location";

        /**
         * Description of the report.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_REPORTER_NAME = "reporter_name";

        /**
         * Possible values for the types of reports.
         */
        public static final int TYPE_GENERAL = 0;
        public static final int TYPE_THEFT = 1;
        public static final int TYPE_ASSAULT = 2;
        public static final int TYPE_VANDALISM = 3;

        /**
         * final content uri
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_REPORTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of report.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPORTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single report.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPORTS;

        /**
         * Returns whether or not the given type is valid
         */
        public static boolean isValidType(int type) {
            if (type == TYPE_GENERAL || type == TYPE_THEFT || type == TYPE_ASSAULT || type == TYPE_VANDALISM) {
                return true;
            }
            return false;
        }
    }
}
