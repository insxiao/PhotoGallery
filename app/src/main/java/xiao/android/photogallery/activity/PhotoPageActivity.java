package xiao.android.photogallery.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import xiao.android.photogallery.fragment.PhotoPageFragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        Intent intent = getIntent();
        String photoPageUrl = intent.getData().toString();
        return PhotoPageFragment.newInstance(photoPageUrl);
    }
}
