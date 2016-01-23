package xiao.android.photogallery;

public class GalleryItem {
    private String mId;
    private String mCaption;
    private String mUrl;
    private String mSecret;
    private String mServer;

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    private String mOwner;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCaption() {
        return mCaption;
    }

    public GalleryItem(String id, String owner, String caption, String url) {
        mId = id;
        mCaption = caption;
        mUrl = url;
        mOwner = owner;
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

    public String getPhotoPageUrl() {
        return "http://www.flickr.com/photos/"+ mOwner + "/" + mId;
    }

    @Override
    public String toString() {
//        return "GalleryItem{" +
//                "mCaption='" + mCaption + '\'' +
//                '}';
        return mCaption;
    }
}
