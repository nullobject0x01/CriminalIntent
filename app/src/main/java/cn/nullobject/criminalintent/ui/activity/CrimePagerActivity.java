package cn.nullobject.criminalintent.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.nullobject.criminalintent.R;
import cn.nullobject.criminalintent.model.Crime;
import cn.nullobject.criminalintent.model.CrimeLab;
import cn.nullobject.criminalintent.ui.fragment.CrimeFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.UUID;

import static cn.nullobject.criminalintent.utils.Constants.EXTRA_CRIME_ID;

public class CrimePagerActivity extends AppCompatActivity {


    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    public static void newActivity(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        packageContext.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        mViewPager = findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this)
                          .getCrimes();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(final int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        UUID uuid = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        mViewPager.setCurrentItem(mCrimes.indexOf(CrimeLab.get(this)
                                                          .getCrime(uuid)));
    }
}
