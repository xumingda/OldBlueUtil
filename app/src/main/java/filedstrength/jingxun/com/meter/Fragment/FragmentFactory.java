package filedstrength.jingxun.com.meter.Fragment;

import android.app.Fragment;


public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index) {
        Fragment fragment = null;
        switch (index) {
            case 1:
                fragment = new MainFragment();
                break;
            case 2:
                fragment = new ConfigFragment();
                break;
            case 3:
                fragment = new SystemFragment();
                break;
        }
        return fragment;
    }
}
