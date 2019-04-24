package com.edsusantoo.bismillah.moviecatalogue.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.edsusantoo.bismillah.moviecatalogue.daftarfilm.DaftarFilmFragment;

public class MainViewPagerAdapater extends FragmentStatePagerAdapter {
    public MainViewPagerAdapater(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DaftarFilmFragment();
            case 1:
                return new DaftarFilmFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Movies";
            case 1:
                return "Tv Shows";
        }
        return null;
    }
}
