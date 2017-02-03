package rocket.club.com.rocketpoker.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import rocket.club.com.rocketpoker.classes.ContactClass;
import rocket.club.com.rocketpoker.classes.ContactHelper;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public class FetchContact {

    Context context = null;
    static AppGlobals appGlobals = null;

    private static final String TAG = "FetchContacts";

    public static ContactHelper fetchContacts(Context context, AppGlobals appGlob) {
        appGlobals = appGlob;

        ArrayList<ContactClass> contactList = new ArrayList<ContactClass>();
        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.RawContacts.ACCOUNT_TYPE},
                ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'google' ", null, null);

        int contactCount = cursor.getCount();
        ContactHelper contactHelper = new ContactHelper();

        if(contactCount == 0) {
            appGlobals.logClass.setLogMsg(TAG, "No Contacts found", LogClass.DEBUG_MSG);
        } else {
            contactHelper.setCursor(cursor);
            contactHelper.setCurPos(-1);
            contactHelper.setTotal(contactCount);

            contactHandler(context, contactHelper);
        }
        return contactHelper;
    }

    public static ContactHelper contactHandler(Context context, ContactHelper contactHelper) {

        ArrayList<ContactClass> contactList = new ArrayList<ContactClass>();

        Cursor cursor = contactHelper.getCursor();

        if(contactHelper.getCurPos() == -1) {
            contactHelper.setIsFirst(true);
        } else {
            contactHelper.setIsFirst(false);
        }

        cursor.moveToPosition(contactHelper.getCurPos());

        while(cursor.moveToNext()) {

            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String phoneNumber = number;//getCanonicalPhoneNumber(context, number);

            if(phoneNumber != null) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                if(phoneNumber.startsWith("0")) {
                    phoneNumber = phoneNumber.substring(1);
                }

                phoneNumber = phoneNumber.replaceAll(Pattern.quote("+"), "");
                phoneNumber = phoneNumber.replaceAll(Pattern.quote(" "), "");

                ContactClass contactClass = new ContactClass();
                contactClass.setContactName(name);
                contactClass.setPhoneNumber(phoneNumber);

                contactList.add(contactClass);

                if(contactList.size() == appGlobals.chunkSize)
                    break;
            }
        }
        contactHelper.setCurPos(cursor.getPosition());
        contactHelper.setContactList(contactList);

        if(cursor.isAfterLast()){
            contactHelper.setAtLast(true);
        }

        return contactHelper;
    }

    public static String getCanonicalPhoneNumber(Context context, String number) {
        return getCanonicalPhoneNumber(context, number,
                PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public static String getCanonicalPhoneNumber(Context context, String number, AppGlobals appGlob) {
        appGlobals = appGlob;
        return getCanonicalPhoneNumber(context, number,
                PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    private static String getCanonicalPhoneNumber(Context context,
                                           String phonenumber, PhoneNumberUtil.PhoneNumberFormat phoneNumberFormat) {
        String internationalFormat = null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phNumberProto = getFormatedPhoneNumber(context, phonenumber);
        if (phNumberProto != null) {
            boolean isValid = phoneUtil.isValidNumber(phNumberProto);
            if (isValid) {
                internationalFormat = phoneUtil.format(phNumberProto,
                        phoneNumberFormat);

            } else {
                return null;
            }
        }
        if (internationalFormat != null && internationalFormat.contains("+"))
            internationalFormat = internationalFormat.substring(1,
                    internationalFormat.length());

        return internationalFormat;
    }

    private static Phonenumber.PhoneNumber getFormatedPhoneNumber(Context context,
                                                          String phonenumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phNumberProto = null;
        try {
            String countryIso = getCountryIso(context);
            if(TextUtils.isEmpty(countryIso)) {
                countryIso = Locale.getDefault().getCountry();
            }
            phNumberProto = phoneUtil.parse(phonenumber, countryIso);
        } catch (NumberParseException e) {
//            appGlobals.logClass.setLogMsg(TAG, "NumberParseException " + e.toString(), LogClass.ERROR_MSG);
            return null;
        } catch (Exception e) {
            appGlobals.logClass.setLogMsg(TAG, "Exception " + e.toString(), LogClass.ERROR_MSG);
        }
        return phNumberProto;
    }

    private static String getCountryIso(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimCountryIso();
    }
}
