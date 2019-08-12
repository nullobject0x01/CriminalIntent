package cn.nullobject.criminalintent.ui.activity;

import android.content.Context;
import android.content.Intent;

import java.util.UUID;

import androidx.fragment.app.Fragment;
import cn.nullobject.criminalintent.ui.fragment.CrimeFragment;
import cn.nullobject.criminalintent.base.BaseFragmentActivity;

public class CrimeActivity extends BaseFragmentActivity {

    private static final String EXTRA_CRIME_ID = "cn.nullobject.criminalintent.crime_id";

    public static void newActivity(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        packageContext.startActivity(intent);
    }

    @Override
    protected Fragment createFragment() {
        return CrimeFragment.newInstance((UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID));
    }
}
