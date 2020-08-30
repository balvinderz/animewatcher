package com.stuffbox.webscraper.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import com.stuffbox.webscraper.constants.Constants;
import com.stuffbox.webscraper.fragments.AnimeFragment;
import com.stuffbox.webscraper.fragments.DownloadedFragment;
import com.stuffbox.webscraper.fragments.RecentFragment;

public class    ViewPagerAdapter extends FragmentPagerAdapter {
   public  ViewPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
    Fragment fragment=null;
    if(position==0)
    {
        fragment= AnimeFragment.newInstance(Constants.url+"page-recent-release.html?page=1&type=2");


    }
    else if(position==1)
        fragment=AnimeFragment.newInstance(Constants.url);
    else if(position ==3)
        fragment = new DownloadedFragment();

    else
    {
        fragment=new RecentFragment();
    }
    return  fragment;
    }
    @Override
    public int getCount() {
        return 4;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 1)
        {
            title = "SUB";
        }
        else if (position == 0)
        {
            title = "DUB";
        }
        else if(position==2)
        {
            title= "RECENT";
        }
        else if(position ==3)
            title="Downloads";



        return title;
    }
}
