package cn.nullobject.criminalintent.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import cn.nullobject.criminalintent.R;
import cn.nullobject.criminalintent.adapter.CrimeListAdapter;
import cn.nullobject.criminalintent.model.Crime;
import cn.nullobject.criminalintent.model.CrimeLab;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeListAdapter mListAdapter;

    private Activity mActivity;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_ist, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //        updateUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimeList = crimeLab.getCrimes();
        if (null == mListAdapter) {
            mListAdapter = new CrimeListAdapter(crimeList, mActivity);
            mCrimeRecyclerView.setAdapter(mListAdapter);
        }else {
            mListAdapter.notifyDataSetChanged();
        }
    }
}
