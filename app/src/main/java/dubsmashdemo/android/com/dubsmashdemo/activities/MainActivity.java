package dubsmashdemo.android.com.dubsmashdemo.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import dubsmashdemo.android.com.dubsmashdemo.R;
import dubsmashdemo.android.com.dubsmashdemo.fragment.VideoListFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // LeakCanary.install(getApplication());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpVideoGalleryFragment();
    }

    private void setUpVideoGalleryFragment() {
        VideoListFragment fragment = new VideoListFragment();
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
    }


}
