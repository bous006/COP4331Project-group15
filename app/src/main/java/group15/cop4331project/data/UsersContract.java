package group15.cop4331project.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Created by bous006 on 3/4/2017.
 */

public class UsersContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private UsersContract() {}

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
    public static final String PATH_USERS = "users";

    /**
     * Inner Class that defines constant values for Users Database table
     * each entry in the table represents a single users
     */
    public static final class UsersEntry implements BaseColumns {

        /** Name of database table for users */
        public final static String TABLE_NAME = "users";

        /**
         * Unique ID number for the user (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _UID = BaseColumns._ID;

        /**
         * Name of the user.
         *
         * Type: TEXT
         */
        public final static String COLUMN_NAME ="name";

        /**
         * email of the user.
         *
         * Type: TEXT
         */
        public final static String COLUMN_EMAIL = "email";

        /**
         * Type of the user.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_USER_TYPE = "type";

        /**
         * Possible values for the class of user.
         */
        public static final int TYPE_THIRD_PARTY = 0;
        public static final int TYPE_USER = 1;
        public static final int TYPE_ADMIN = 2;

        /**
         * final content uri
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of users.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single user.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        /**
         * uri specifically for verifying users

        public static final Uri VERIFY_USER_URI = Uri.withAppendedPath(CONTENT_ITEM_TYPE, PATH_USERS);*/

        /**
         * Returns whether or not the given type is valid
         */
        public static boolean isValidType(int type) {
            if (type == TYPE_THIRD_PARTY || type == TYPE_USER || type == TYPE_ADMIN) {
                return true;
            }
            return false;
        }
    }
}
