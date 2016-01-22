package xiao.android.photogallery;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static final String TAG = "PhotoGalleryActivity";
    private SharedPreferences mDefaultSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            PhotoGalleryFragment fragment = (PhotoGalleryFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_container);
            Log.d(TAG, "onNewIntent with string : " + query);
            SharedPreferences.Editor edit = mDefaultSharedPreferences.edit();
            edit.putString(FlickrFetchr.PREF_SEARCH_QUERY, query);
            fragment.updateItems();
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
