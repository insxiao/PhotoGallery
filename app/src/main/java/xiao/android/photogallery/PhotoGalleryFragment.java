package xiao.android.photogallery;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoGalleryFragment extends Fragment {
    private GridView mGridView;
    private ArrayList<GalleryItem> mItems;

    public static String TAG = "PhotoGalleryFragment";
    private ThumbnailDownloader<ImageView> mThumbnailThread;

    private LruCache<String, Bitmap> mLruCache;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    public PhotoGalleryFragment() {
        // Required empty public constructor

    }

    private void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;
        if (mItems != null) {
            mGridView.setAdapter(new GalleryItemAdapter(mItems));
        } else {
            mGridView.setAdapter(null);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_search:
                getActivity().onSearchRequested();
                return true;
            case R.id.menu_item_clear:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mLruCache = new LruCache<>(1024);
        mThumbnailThread = new ThumbnailDownloader<>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail, String url) {
                if (isVisible()) {
                    mLruCache.put(url, thumbnail);
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });

        mThumbnailThread.start();
        mThumbnailThread.getLooper();

        updateItems();

        Log.i(TAG, "background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mGridView = (GridView) view.findViewById(R.id.gridView);
        setupAdapter();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();
        Log.d(TAG, "background thread stopped");
    }

    public void updateItems() {
        new FetchItemsTask().execute();
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
            ImageView image = (ImageView) convertView.findViewById(R.id.gallery_item_imageView);
//                image.setImageResource(R.drawable.nicon);
            image.setImageResource(R.drawable.nicon);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView image = (ImageView) v;
                    GalleryItem item = (GalleryItem) v.getTag();
                    Log.d(TAG, item.getUrl());

                }
            });
            GalleryItem item = getItem(position);
            image.setTag(item);
            String url = item.getUrl();
            if (mLruCache.get(url) == null) {
                mThumbnailThread.queueThumbnail(image, url);
            } else {
                image.setImageBitmap(mLruCache.get(url));
            }
            return convertView;
        }
    }

    public class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<GalleryItem>> {


        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {
            String query = PreferenceManager.getDefaultSharedPreferences(getActivity())
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
