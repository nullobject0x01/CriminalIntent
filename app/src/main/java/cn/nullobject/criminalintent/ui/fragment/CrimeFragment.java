package cn.nullobject.criminalintent.ui.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ShareCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import cn.nullobject.criminalintent.R;
import cn.nullobject.criminalintent.model.Crime;
import cn.nullobject.criminalintent.model.CrimeLab;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    public static final int REQUEST_CONTACT = 1;

    private Crime mCrime;
    private AppCompatEditText mTitleField;
    private AppCompatButton mDateButton;
    private AppCompatCheckBox mSolvedCheckBox;
    private AppCompatButton mReportButton;
    private AppCompatButton mSuspectButton;


    private Activity mActivity;

    private CrimeFragment() {

    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
            mCrime = CrimeLab.get(mActivity)
                             .getCrime(crimeId);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(final Editable editable) {

            }
        });
        mDateButton = view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setEnabled(true);
        mDateButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.setCancelable(true);
                dialog.show(fragmentManager, DIALOG_DATE);
            }
        });
        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener((compoundButton, b) -> mCrime.setSolved(b));

        mReportButton = view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(v -> {
            //            Intent intent = new Intent(Intent.ACTION_SEND);
            //            intent.setType("text/plain");
            //            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
            //            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
            //            intent = Intent.createChooser(intent, getString(R.string.send_report));

            Intent i = ShareCompat.IntentBuilder.from(mActivity)
                                                .setChooserTitle(R.string.crime_report_subject)
                                                .setType("text/plain")
                                                .setText(getCrimeReport())
                                                .createChooserIntent();

            startActivity(i);
        });
        mSuspectButton = view.findViewById(R.id.crime_suspect);
        final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton.setOnClickListener(v -> startActivityForResult(intent, REQUEST_CONTACT));
        if (!TextUtils.isEmpty(mCrime.getSuspect())) {
            mSuspectButton.setText(mCrime.getSuspect());
        }
        PackageManager manager = mActivity.getPackageManager();
        if (manager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            if (requestCode == REQUEST_DATE) {
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
            } else if (requestCode == REQUEST_CONTACT && null != data && null != data.getData()) {
                Uri uri = data.getData();
                // Specify which fields you want your query to return values for
                String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME};
                // Prtform your query - the uri is like a "where" clause here
                ContentResolver resolver = mActivity.getContentResolver();
                Cursor cursor = resolver.query(uri, queryFields, null, null, null);
                try {
                    if (cursor.getCount() == 0) {
                        return;
                    }
                    // Pull out the first column of the first row of data - that is your suspect's name
                    cursor.moveToFirst();
                    String suspect = cursor.getString(0);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                } finally {
                    cursor.close();

                }
            }
        }

    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate()
                                  .toString());
    }

    private String getCrimeReport() {
        String solvedString = getString(mCrime.isSolved() ? R.string.crime_report_solved : R.string.crime_report_unsolved);

        String dateFormat = "EEE, MM dd";
        String dateStr = DateFormat.format(dateFormat, mCrime.getDate())
                                   .toString();

        String suspect = mCrime.getSuspect();
        if (null == suspect) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report, mCrime.getTitle(), dateStr, solvedString, suspect);
    }


}
