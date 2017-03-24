package databaseStuff;

import android.content.Context;
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
    // Database creation sql statement
    private static final String TABLE_ACCOUNT_CREATE = "create table if not exist "
            + TABLE_ACCOUNT_NAME + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_SURNAME + " text not null, "
            + COLUMN_EMAIL + " text not null, "
            + COLUMN_PHONE + " text not null, "
            + COLUMN_BIRTHDAY + " text not null );";

    public BrainStormingSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_ACCOUNT_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(BrainStormingSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNT_NAME);
        onCreate(db);
    }

    public void saveUser(User user) {
        this.user = user;
        database = getWritableDatabase();
        database.execSQL("insert into "+ TABLE_ACCOUNT_NAME +" ("
                + COLUMN_NAME + ","
                + COLUMN_SURNAME + ","
                + COLUMN_EMAIL + ","
                + COLUMN_PHONE + ","
                + COLUMN_BIRTHDAY + ","
                +") values ("
                + user.getName()+ ","
                + user.getSurname()+ ","
                + user.getEmail()+ ","
                + user.getPhone()+ ","
                + user.getBirthday()+ ");");
        close();
    }
}
