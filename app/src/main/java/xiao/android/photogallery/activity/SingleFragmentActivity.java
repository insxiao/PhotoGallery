package xiao.android.photogallery.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import xiao.android.photogallery.R;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    public abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = createFragment();
        if (manager.findFragmentById(R.id.fragment_container) == null) {
            manager.beginTransaction().add(R.id.fragment_container, fragment).commit();
        } else {
            manager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

}
