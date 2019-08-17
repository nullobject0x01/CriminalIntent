package cn.nullobject.criminalintent.ui.activity;

import androidx.fragment.app.Fragment;
import cn.nullobject.criminalintent.base.BaseFragmentActivity;
import cn.nullobject.criminalintent.ui.fragment.CrimeListFragment;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public class CrimeListActivity extends BaseFragmentActivity {

    
    
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    
}
