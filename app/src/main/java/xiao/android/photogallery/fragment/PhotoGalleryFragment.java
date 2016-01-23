package xiao.android.photogallery.fragment;


import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xiao.android.photogallery.FlickrFetchr;
import xiao.android.photogallery.GalleryItem;
import xiao.android.photogallery.service.PollService;
import xiao.android.photogallery.R;
import xiao.android.photogallery.activity.PhotoPageActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends VisibleFragment {
    private GridView mGridView;
    private ArrayList<GalleryItem> mItems;
    public static String TAG = "PhotoGalleryFragment";

    private SearchView mSearchView;
    private MenuItem mActionSearch;
    private SharedPreferences mDefaultSharedPreferences;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        updateItems();

        Log.i(TAG, "background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem item = (GalleryItem) mGridView.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), PhotoPageActivity.class);
                intent.setData(Uri.parse(item.getPhotoPageUrl()));
                startActivity(intent);
                Log.d(TAG, "GridView::OnItemClickListener: " + position);
            }
        });
        mGridView.requestFocusFromTouch();
        setupAdapter();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
        mActionSearch = menu.findItem(R.id.menu_item_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mActionSearch);
        if (mSearchView == null) {
            Log.d(TAG, "it is null");
        }
        if (mSearchView != null) {
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            ComponentName componentName = getActivity().getComponentName();
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
            mSearchView.setSearchableInfo(searchableInfo);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String lastQuery = mDefaultSharedPreferences.getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
                    if (lastQuery != null) {
                        mSearchView.setQueryHint(lastQuery);
                    }
                    mDefaultSharedPreferences.edit()
                            .putString(FlickrFetchr.PREF_SEARCH_QUERY, query)
                            .apply();
                    updateItems();
                    MenuItemCompat.collapseActionView(mActionSearch);
                    SearchView searchView = (SearchView) MenuItemCompat.getActionView(mActionSearch);
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
                    getActivity().onSearchRequested();

                return true;
            case R.id.menu_item_clear:
                mDefaultSharedPreferences.edit().remove(FlickrFetchr.PREF_SEARCH_QUERY).apply();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;
        if (mItems != null) {
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }
    }

    public void updateItems() {
        new FetchItemsTask().execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PollService.setServiceAlarm(getActivity(), false);
        Log.d(TAG, "background thread stopped");
    }

    private class GalleryItemAdapter extends ArrayAdapter<GalleryItem> {
        public GalleryItemAdapter(ArrayList<GalleryItem> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
            }
            ImageView imageView = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);

            GalleryItem item = getItem(position);
            String url = item.getUrl();
            Picasso.with(getActivity()).load(url).into(imageView);
            return convertView;
        }
    }

    public class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            String query = mDefaultSharedPreferences
                    .getString(FlickrFetchr.PREF_SEARCH_QUERY, null);
            if (query == null)
                return new FlickrFetchr().fetchItems();
            else
                return new FlickrFetchr().search(query);
        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
}
