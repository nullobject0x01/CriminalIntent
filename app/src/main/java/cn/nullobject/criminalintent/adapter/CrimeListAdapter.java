package cn.nullobject.criminalintent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import cn.nullobject.criminalintent.R;
import cn.nullobject.criminalintent.model.Crime;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class CrimeListAdapter extends RecyclerView.Adapter<CrimeListAdapter.CrimeHolder> {

   
    private List<Crime> mCrimes;

    private final Context mContext;

    public CrimeListAdapter(@NonNull final List<Crime> crimes, @NonNull final Context context) {
        mCrimes = crimes;
        mContext = context;
    }

    class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatTextView mTitle;
        private AppCompatTextView mDate;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            mTitle = itemView.findViewById(R.id.crime_title);
            mDate = itemView.findViewById(R.id.crime_date);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitle.setText(mCrime.getTitle());
            mDate.setText(mCrime.getDate()
                                .toString());
        }

        @Override
        public void onClick(final View view) {
            Toast.makeText(mContext, mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                 .show();
        }
    }

    @NonNull
    @Override
    public CrimeHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        return new CrimeHolder(LayoutInflater.from(mContext), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final CrimeHolder holder, final int position) {
        holder.bind(mCrimes.get(position));
    }

    @Override
    public int getItemCount() {
        return mCrimes.size();
    }
    
}
