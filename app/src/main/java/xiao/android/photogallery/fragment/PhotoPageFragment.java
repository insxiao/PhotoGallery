package xiao.android.photogallery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import xiao.android.photogallery.R;

public class PhotoPageFragment extends Fragment {

    public static final String PHOTO_PAGE_URL = "photoPageUrl";
    private WebView mWebView;

    public static PhotoPageFragment newInstance(String url) {
        PhotoPageFragment fragment = new PhotoPageFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO_PAGE_URL, url);
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mWebView = (WebView)view.findViewById(R.id.photo_page_webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView.loadUrl(getArguments().getString(PHOTO_PAGE_URL));
        return view;
    }
}
