package at.wifi.swdev.audiorecorderapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import at.wifi.swdev.audiorecorderapp.Adapters.ViewPagerAdapter;
import at.wifi.swdev.audiorecorderapp.Fragments.DrumpadFragment;
import at.wifi.swdev.audiorecorderapp.Fragments.RecorderFragment;
import at.wifi.swdev.audiorecorderapp.Fragments.RecordingsFragment;
import at.wifi.swdev.audiorecorderapp.dialogs.PresetsDialog;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new RecorderFragment(), "Recorder");
        viewPagerAdapter.addFragment(new RecordingsFragment(), "Recordings");
        viewPagerAdapter.addFragment(new DrumpadFragment(), "Drumpad");
        viewPager.setAdapter(viewPagerAdapter);

    }

}
