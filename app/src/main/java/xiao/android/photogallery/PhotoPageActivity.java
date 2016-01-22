package xiao.android.photogallery;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        Intent intent = getIntent();
        String photoPageUrl = intent.getData().toString();
        return PhotoPageFragment.newInstance(photoPageUrl);
    }
}
