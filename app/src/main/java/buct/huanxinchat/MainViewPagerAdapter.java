package buct.huanxinchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tian on 2018/7/8.
 */

public class MainViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>(3);
    }

    public void setFragments(List<Fragment> fragments) {
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
