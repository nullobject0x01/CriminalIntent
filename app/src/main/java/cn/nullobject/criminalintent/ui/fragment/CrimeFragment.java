package cn.nullobject.criminalintent.ui.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import cn.nullobject.criminalintent.R;
import cn.nullobject.criminalintent.model.Crime;
import cn.nullobject.criminalintent.model.CrimeLab;
import cn.nullobject.criminalintent.utils.PictureUtils;

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
    public static final int REQUEST_PHOTO = 2;

    private Unbinder mUnbinder;

    @BindView(R.id.crime_photo)
    AppCompatImageView mCrimePhoto;
    @BindView(R.id.crime_camera)
    AppCompatImageButton mCrimeCamera;
    @BindView(R.id.crime_title)
    AppCompatEditText mCrimeTitle;
    @BindView(R.id.crime_date)
    AppCompatButton mCrimeDate;
    @BindView(R.id.crime_solved)
    AppCompatCheckBox mCrimeSolved;
    @BindView(R.id.crime_suspect)
    AppCompatButton mCrimeSuspect;
    @BindView(R.id.crime_report)
    AppCompatButton mCrimeReport;

    private Crime mCrime;

    private ViewTreeObserver mViewTreeObserver;

    private Activity mActivity;

    private File mPhotoFile;
    private final String FILE_PROVIDER_AUTHORITY = "cn.nullobject.criminalintent.fileprovider";

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
            mPhotoFile = CrimeLab.get(mActivity)
                                 .getPhotoFile(mCrime);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @OnTextChanged({R.id.crime_title})
    void onTextChanged(final CharSequence charSequence) {
        mCrime.setTitle(charSequence.toString());
    }

    @OnCheckedChanged({R.id.crime_solved})
    void onCheckChanged(boolean check) {
        mCrime.setSolved(check);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mViewTreeObserver = mCrimePhoto.getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                CrimeFragment.this.updatePhotoView();
                if (mViewTreeObserver.isAlive()) {
                    mViewTreeObserver.removeOnGlobalLayoutListener(this);
                }
            }
        });
        mCrimeTitle.setText(mCrime.getTitle());
        updateDate();
        mCrimeDate.setEnabled(true);

        mCrimeSolved.setChecked(mCrime.isSolved());
        final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (!TextUtils.isEmpty(mCrime.getSuspect())) {
            mCrimeSuspect.setText(mCrime.getSuspect());
        }
        PackageManager manager = mActivity.getPackageManager();
        if (manager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mCrimeSuspect.setEnabled(false);
        }
        final Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = null != mPhotoFile && null != intentPhoto.resolveActivity(manager);
        mCrimeCamera.setEnabled(canTakePhoto);
        //        updatePhotoView();
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
                    mCrimeSuspect.setText(suspect);
                } finally {
                    cursor.close();

                }
            } else if (requestCode == REQUEST_PHOTO) {
                Uri uri = FileProvider.getUriForFile(mActivity, FILE_PROVIDER_AUTHORITY, mPhotoFile);
                mActivity.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                updatePhotoView();
            } else {

            }
        }

    }

    private void updateDate() {
        mCrimeDate.setText(mCrime.getDate()
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


    @OnClick(R.id.crime_camera)
    public void onMCrimeCameraClicked() {
        Uri uri = FileProvider.getUriForFile(mActivity, FILE_PROVIDER_AUTHORITY, mPhotoFile);
        final Intent intentPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        List<ResolveInfo> cameraActivities = mActivity.getPackageManager()
                                                      .queryIntentActivities(intentPhoto,
                                                              PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo info : cameraActivities) {
            mActivity.grantUriPermission(info.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(intentPhoto, REQUEST_PHOTO);
    }

    @OnClick(R.id.crime_date)
    public void onMCrimeDateClicked() {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.setCancelable(true);
            dialog.show(fragmentManager, DIALOG_DATE);
        }
    }

    @OnClick(R.id.crime_suspect)
    public void onMCrimeSuspectClicked() {
        final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    @OnClick(R.id.crime_report)
    public void onMCrimeReportClicked() {
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
    }

    private void updatePhotoView() {
        if (null == mPhotoFile || !mPhotoFile.exists()) {
            mCrimePhoto.setImageDrawable(null);
        } else {
            //            Bitmap bitmap = PictureUtils.getDefaultScaledBitmap(mPhotoFile.getPath(), mActivity);
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), mCrimePhoto.getWidth(),
                    mCrimePhoto.getHeight());
            mCrimePhoto.setImageBitmap(bitmap);
        }
    }
}
