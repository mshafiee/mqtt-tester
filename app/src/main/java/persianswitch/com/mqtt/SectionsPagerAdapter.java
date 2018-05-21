package persianswitch.com.mqtt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        switch (position) {
            case 0:
                return ChatFragment.newInstance();
            case 1:
                return LogFragment.newInstance(LogFragment.LOG_LEVEL_MESSAGES);
            case 2:
                return LogFragment.newInstance(LogFragment.LOG_LEVEL_CONTROL_MESSAGES);
            case 3:
                return SettingFragment.newInstance();
        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 4;
    }
}