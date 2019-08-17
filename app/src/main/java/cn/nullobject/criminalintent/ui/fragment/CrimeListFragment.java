package cn.nullobject.criminalintent.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.nullobject.criminalintent.R;
import cn.nullobject.criminalintent.adapter.CrimeListAdapter;
import cn.nullobject.criminalintent.model.Crime;
import cn.nullobject.criminalintent.model.CrimeLab;
import cn.nullobject.criminalintent.ui.activity.CrimePagerActivity;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeListAdapter mListAdapter;

    private AppCompatActivity mActivity;
    boolean mSubTitleVisible = false;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private List<Crime> mCrimes = new ArrayList<>();


    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 通知FragmentManager需要创建菜单
        setHasOptionsMenu(true);
        if (null != savedInstanceState) {
            mSubTitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubTitleVisible);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_ist, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mCrimeRecyclerView.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
        updateSubtitle();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(mActivity);
        mCrimes = crimeLab.getCrimes();
        if (null == mListAdapter) {
            mListAdapter = new CrimeListAdapter(mCrimes, mActivity);
            mCrimeRecyclerView.setAdapter(mListAdapter);
        } else {
            mListAdapter.setCrimes(mCrimes);
            mListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
        MenuItem item = menu.findItem(R.id.show_subtitle);
        if (null != item) {
            if (mSubTitleVisible) {
                item.setTitle(R.string.hide_subtitle);
            } else {
                item.setTitle(R.string.show_subtitle);
            }
        }
        menu.findItem(R.id.remove_crime)
            .setVisible(mCrimes.size() > 0);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.new_crime) {
            Crime crime = new Crime();
            CrimeLab.get(mActivity)
                    .addCrime(crime);
            CrimePagerActivity.newActivity(mActivity, crime.getId());
            return true;
        } else if (item.getItemId() == R.id.show_subtitle) {
            mSubTitleVisible = !mSubTitleVisible;
            mActivity.invalidateOptionsMenu();
            updateSubtitle();
            return true;
        } else if (item.getItemId() == R.id.remove_crime) {
            if (mCrimes.size() > 0) {
                CrimeLab.get(mActivity)
                        .removeCrime(mCrimes.remove(0));
                mListAdapter.notifyItemRemoved(0);
                mActivity.invalidateOptionsMenu();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(mActivity);
        int crimeCount = crimeLab.getCrimes()
                                 .size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        if (!mSubTitleVisible) {
            subtitle = null;
        }
        if (null != mActivity.getSupportActionBar()) {
            mActivity.getSupportActionBar()
                     .setSubtitle(subtitle);
        }
    }
}
