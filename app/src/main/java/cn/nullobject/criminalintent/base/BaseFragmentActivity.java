package cn.nullobject.criminalintent.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import cn.nullobject.criminalintent.R;

/**
 * @author xiongda
 * Created on 2019/8/10.
 * Introduction:
 */
public abstract class BaseFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (null == fragment) {
            fragment = createFragment();
            fragmentManager.beginTransaction()
                           .add(R.id.fragment_container, fragment)
                           .commit();
        }

    }
}
