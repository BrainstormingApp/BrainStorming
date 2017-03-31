package databaseStuff;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import authenticatorStuff.User;

/**
 * Created by pyronaid on 07/03/2017.
 */
@Singleton
public class BrainStormingSQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_ACCOUNT_NAME = "account";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SURNAME = "surname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_BIRTHDAY = "birthday";

    private static final String DATABASE_NAME = "brainstorming.db";
    private static final int DATABASE_VERSION = 1;

    private User user;
    private SQLiteDatabase database;
    private Cursor lastCursor;
    // Database creation sql statement
    private static final String TABLE_ACCOUNT_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ACCOUNT_NAME + " ( "
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_SURNAME + " TEXT NOT NULL, "
            + COLUMN_EMAIL + " TEXT NOT NULL, "
            + COLUMN_PHONE + " TEXT NOT NULL, "
            + COLUMN_BIRTHDAY + " TEXT NOT NULL );";
    private static final String TABLE_ACCOUNT_CLEAN = "DROP TABLE IF EXISTS "
            + TABLE_ACCOUNT_NAME +";";

    public BrainStormingSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i("Brainstorming", TABLE_ACCOUNT_CREATE);
        database.execSQL(TABLE_ACCOUNT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BrainStormingSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        Log.i("Brainstorming", TABLE_ACCOUNT_CLEAN);
        database.execSQL(TABLE_ACCOUNT_CLEAN);
        onCreate(db);
    }

    public void saveUser(User user) {
        this.user = user;
        database = getWritableDatabase();
        String query = "insert into "+ TABLE_ACCOUNT_NAME +" ("
                            + COLUMN_NAME + ","
                            + COLUMN_SURNAME + ","
                            + COLUMN_EMAIL + ","
                            + COLUMN_PHONE + ","
                            + COLUMN_BIRTHDAY + ")"
                            +" values ("
                            + "'" + user.getName()+ "',"
                            + "'" + user.getSurname()+ "',"
                            + "'" + user.getEmail()+ "',"
                            + "'" + user.getPhone()+ "',"
                            + "'" + user.getBirthday()+ "');";
        Log.i("Brainstorming", query);
        database.execSQL(query);
        close();
    }

    public Cursor getUserInfo() {
        database = getReadableDatabase();
        String query = "select "
                + COLUMN_NAME + ","
                + COLUMN_SURNAME + ","
                + COLUMN_EMAIL + ","
                + COLUMN_PHONE + ","
                + COLUMN_BIRTHDAY + ""
                +" from "
                + TABLE_ACCOUNT_NAME +";";
        Log.i("Brainstorming", query);
        if(lastCursor != null){
            lastCursor.close();
        }
        lastCursor = database.rawQuery(query, null);
        //close();
        return lastCursor;
    }

    public void dropAll() {
        if(lastCursor != null){
            lastCursor.close();
        }
        database = getWritableDatabase();
        Log.i("Brainstorming", TABLE_ACCOUNT_CLEAN);
        database.execSQL(TABLE_ACCOUNT_CLEAN);
        Log.i("Brainstorming", TABLE_ACCOUNT_CREATE);
        database.execSQL(TABLE_ACCOUNT_CREATE);
        close();
    }

    public void editField(String tableName, String columnName, String value) {
        if(isFieldExist(tableName,columnName) && user != null){
            database = getReadableDatabase();
            String query = "update "+ tableName+" SET "
                    + columnName + "='" + value + "'"
                    + " WHERE "
                    + COLUMN_EMAIL + "='" + user.getEmail() + "';";
            Log.i("Brainstorming", query);
            database.execSQL(query);
            close();
        } else {
            Log.e("Brainstorming", "Table "+tableName+" cannot be update because it does not have the column "+columnName);
        }
    }


    public boolean isFieldExist(String tableName, String fieldName) {
        boolean isExist = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);

        if (res.moveToFirst()) {
            do {
                int value = res.getColumnIndex("name");
                if(value != -1 && res.getString(value).equals(fieldName)){
                    isExist = true;
                    break;
                }
                // Add book to books

            } while (res.moveToNext());
        }

        close();
        return isExist;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void autoDefineUser() {
        Cursor c = getUserInfo();
        c.moveToFirst();
        User u = new User();
        u.setName(c.getString(c.getColumnIndex(COLUMN_NAME)));
        u.setSurname(c.getString(c.getColumnIndex(COLUMN_SURNAME)));
        u.setBirthday(c.getString(c.getColumnIndex(COLUMN_BIRTHDAY)));
        u.setEmail(c.getString(c.getColumnIndex(COLUMN_EMAIL)));
        u.setPhone(c.getString(c.getColumnIndex(COLUMN_PHONE)));

        this.user = u;
    }
}
