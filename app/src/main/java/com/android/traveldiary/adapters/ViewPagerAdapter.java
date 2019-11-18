package com.android.traveldiary.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.android.traveldiary.fragments.EntryFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<EntryFragment> mFragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        for(EntryFragment f: mFragmentList){
            f.notifyAdapterDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(EntryFragment fragment) {
        mFragmentList.add(fragment);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        //when its not destroying it wont create fragments every time you swipe right or left
    }
}
