package rocket.club.com.rocketpoker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.UserDetails;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "videoCall.db";
    public static final int DB_VERSION = 1;

    public static final String tableName = "appUsers";

    public static final String _id = "id";
    public static final String userId = "UserId";
    public static final String mobile = "Mobile";
    public static final String userName = "Name";

    public static final String CREATE_TABLE = "CREATE TABLE " + tableName + "(" + _id +
            " integer primary key, " + userId + " text, " + mobile + " text, " + userName + " text)";

    public static final String SELECT_ALL = "SELECT * FROM " + tableName;

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean insertContactDetils(ArrayList<UserDetails> userDetails){
        SQLiteDatabase db = this.getWritableDatabase();

        for(UserDetails details : userDetails) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.userId, details.getUserId());
            contentValues.put(this.mobile, details.getMobile());
            contentValues.put(this.userName, details.getUserName());
            db.insert(tableName, null, contentValues);
        }
        return true;
    }

    public ArrayList<ContactClass> getAllContacts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery(SELECT_ALL, null );
        res.moveToFirst();

        ArrayList<ContactClass> contactList = new ArrayList<ContactClass>();

        while(!res.isAfterLast()) {
            String name = res.getString(res.getColumnIndex(userName));
            String userAppId = res.getString(res.getColumnIndex(userId));
            String userMobile = res.getString(res.getColumnIndex(mobile));

            ContactClass contactClass = new ContactClass();
            contactClass.setContactName(name);
            contactClass.setPhoneNumber(userMobile);
            contactClass.setUserId(userAppId);

            contactList.add(contactClass);

            res.moveToNext();
        }
        return contactList;
    }
}
