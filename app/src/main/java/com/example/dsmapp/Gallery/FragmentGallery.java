package com.example.dsmapp.Gallery;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.dsmapp.R;


public class FragmentGallery extends Fragment {

    private String mToken;
    private static int start;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mToken = getArguments().getString("access_token");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery, container,false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.img_viewpager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.img_tabs);

        GalleryViewPagerAdapter adapter = new GalleryViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(start);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    public static FragmentGallery newInstance(String mToken, int position) {
        FragmentGallery fragmentGallery = new FragmentGallery();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        fragmentGallery.setArguments(bundle);
        start = position;
        return fragmentGallery;
    }

    public class GalleryViewPagerAdapter extends FragmentStatePagerAdapter {

        public GalleryViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = FragGalleryPatients.newInstance(mToken);
                    break;
                case 1:
                    fragment = FragGalleryRec.newInstance(mToken);
                    break;
                case 2:
                    fragment = FragGalleryAccess.newInstance(mToken);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Patients";
                case 1:
                    return "Received";
                case 2:
                    return "Access";
            }
            return null;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
        }
    }
}
