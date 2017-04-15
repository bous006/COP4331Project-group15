package group15.cop4331project;

/**
 * Created by bous006 on 4/13/2017.
 */

public class UserDataHolder {
    private static int currentUserUID;
    private static String currentUserName;
    private static String currentUserEmail;
    private static int currentUserAccess;

    public static int getCurrentUserUID() {return currentUserUID;}

    public static String getCurrentUserName() {return currentUserName;}

    public static String getCurrentUserEmail() {return currentUserEmail;}

    public static int getCurrentUserAccess() {return currentUserAccess;}

    public static void setCurrentUserData(int UID, String name, String email, int access) {
        UserDataHolder.currentUserUID = UID;
        UserDataHolder.currentUserName = name;
        UserDataHolder.currentUserEmail = email;
        UserDataHolder.currentUserAccess = access;
    }
}
