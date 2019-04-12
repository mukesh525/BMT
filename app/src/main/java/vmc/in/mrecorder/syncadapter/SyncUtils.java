
package vmc.in.mrecorder.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import vmc.in.mrecorder.accounts.GenericAccountService;
import vmc.in.mrecorder.provider.FeedProvider;

public class SyncUtils {
    private static long SYNC_FREQUENCY;  // 1 hour (in seconds)
    private static final String CONTENT_AUTHORITY = FeedProvider.CONTENT_AUTHORITY;
    private static final String PREF_SETUP_COMPLETE = "setup_complete";
    private static Context mContext;
    private static Account account;

    public static void CreateSyncAccount(Context context) {
        mContext = context;
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        int value = Integer.parseInt(sharedPrefs.getString("prefSyncFrequency", "10"));
        Log.e("sync_utils: ", "frequency_value: " + value);
        SYNC_FREQUENCY = value;
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);
        account = GenericAccountService.GetAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            if (!ContentResolver.getMasterSyncAutomatically())
                ContentResolver.setMasterSyncAutomatically(true);

            ContentResolver.addPeriodicSync(
                    account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
            Log.d("account", "newAccount");
            Log.d("account", "duratin " + SYNC_FREQUENCY);
        }

        if (newAccount || !setupComplete) {
            TriggerRefresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).commit();
        }

//        if(ContentResolver.isSyncActive(account, FeedProvider.CONTENT_AUTHORITY)){
//            // sync is enable
//            Log.d("SYNC", "Sync is Enable");
//        }
    }

    public static void TriggerRefresh() {
        Bundle b = new Bundle();
        // Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                GenericAccountService.GetAccount(),      // Sync account
                FeedProvider.CONTENT_AUTHORITY, // Content authority
                b);                                      // Extras
    }

    public static void updateSync() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        int value = Integer.parseInt(sharedPrefs.getString("prefSyncFrequency", "10"));
        SYNC_FREQUENCY = value;
        ContentResolver.addPeriodicSync(
                account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
    }
}
