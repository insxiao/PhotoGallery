package xiao.android.photogallery;

import android.net.Uri;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Xiao on 2016/1/11.
 */
public class FlickrFetchr {

    public static final String TAG = "FlickrFetchr";
    private static final String ENDPOINT = "https://api.flickr.com/services/rest";
    private static final String API_KEY = "5d5bdfb5895439b050a247825bb2fc5f";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String PARAM_EXTRAS = "extras";
    private static final String PARAM_TEXT = "text";

    private static final String EXTRA_SMALL_URL = "url_s";
    public static final String XML_PHOTO = "photo";
    public static final String PREF_SEARCH_QUERY = "searchQuery";

    private String getRecentURL() {
        return Uri.parse(ENDPOINT).buildUpon().appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter("method", METHOD_GET_RECENT).build().toString();
    }

    private String getSearchURL(String text) {
        return Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("method", METHOD_SEARCH)
                .appendQueryParameter(PARAM_EXTRAS, EXTRA_SMALL_URL)
                .appendQueryParameter(PARAM_TEXT, text).build().toString();
    }

    public ArrayList<GalleryItem> search(String query) {
        return downloadGalleryItems(getSearchURL(query));
    }
    public ArrayList<GalleryItem> fetchItems() {
        return downloadGalleryItems(getRecentURL());
    }
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        if (url == null) {
            Log.d(TAG, "url is null");
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = new BufferedInputStream(connection.getInputStream());

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public ArrayList<GalleryItem> downloadGalleryItems(String url) {
        ArrayList<GalleryItem> items = new ArrayList<>();
        try {

            String xmlString = getUrl(url);

            Log.d(TAG, xmlString);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            parseItems(items, parser);
        } catch (IOException e) {
            Log.d(TAG, "Unable to fetch items", e);
        } catch (XmlPullParserException e) {
            Log.d(TAG, "error with XmlPullParser: ", e);
        }
        return items;
    }

    public void parseItems(ArrayList<GalleryItem> items, XmlPullParser parser)
            throws XmlPullParserException, IOException {

        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String title = parser.getAttributeValue(null, "title");
                String smallUrl = parser.getAttributeValue(null, EXTRA_SMALL_URL);
                String owner = parser.getAttributeValue(null, "owner");
                GalleryItem item = new GalleryItem(id, owner, title, smallUrl);
                items.add(item);
            }
            eventType = parser.next();
        }
    }
}
