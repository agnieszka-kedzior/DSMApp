package com.example.dsmapp.Tasks;

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

public class FragmentTasks extends Fragment {

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
        View view = inflater.inflate(R.layout.tasks, container,false);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pages);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.task_tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(start);

        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    public static FragmentTasks newInstance(String mToken, int position){
        FragmentTasks fragmentTasks = new FragmentTasks();
        Bundle bundle = new Bundle();
        bundle.putString("access_token", mToken);
        fragmentTasks.setArguments(bundle);
        start = position;
        return fragmentTasks;
    }


    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = FragTasksAssigned.newInstance(mToken,0);
                    break;
                case 1:
                    fragment = FragTasksCreated.newInstance(mToken,1);
                    break;
                case 2:
                    fragment = FragTasksHistory.newInstance(mToken,2);
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
                    return "Assigned";
                case 1:
                    return "Created";
                case 2:
                    return "History";
            }
            return null;
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, position, object);
        }
    }
}
