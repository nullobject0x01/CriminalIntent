package cn.nullobject.criminalintent.ui.activity;

import androidx.fragment.app.Fragment;
import cn.nullobject.criminalintent.ui.fragment.CrimeFragment;
import cn.nullobject.criminalintent.base.BaseFragmentActivity;

public class CrimeActivity extends BaseFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}
