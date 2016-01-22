package xiao.android.photogallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.net.ConnectivityManagerCompat;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PollService extends IntentService {

    public static final String TAG = "PollService";

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm.getActiveNetworkInfo()) {
            return;
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String query = prefs.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
        String lastResultId = prefs.getString(FlickrFetchr.PREF_LAST_RESULT_ID, null);

        ArrayList<GalleryItem> items;

        if (query != null) {
            items = new FlickrFetchr().search(query);
        } else {
            items = new FlickrFetchr().fetchItems();
        }

        if (items.size() == 0) {
            return;
        }

        String resultID = items.get(0).getId();

        if (!resultID.equals(lastResultId))  {
            Log.d(TAG, "Got a new result: " + resultID);
        } else {
            Log.d(TAG, "Got a old result: " + resultID);
        }
        prefs.edit().putString(FlickrFetchr.PREF_LAST_RESULT_ID, lastResultId);

        Log.i(TAG, "Received an intent: " + intent);
    }
}
