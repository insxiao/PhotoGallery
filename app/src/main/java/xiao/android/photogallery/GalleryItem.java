package xiao.android.photogallery;

/**
 * Created by Xiao on 2016/1/18.
 */
public class GalleryItem {
    private String mId;
    private String mCaption;
    private String mUrl;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String toString() {
//        return "GalleryItem{" +
//                "mCaption='" + mCaption + '\'' +
//                '}';
        return mCaption;
    }
}
