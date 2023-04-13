package edu.northeastern.numadsp23_team20;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class FriendsFragment extends Fragment {


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private static FriendsRecyclerView.OnButtonClickListener mListener;


    private static ArrayList<FriendsData> all_users;
    private static ArrayList<FriendsData> friends;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        all_users = new ArrayList<>();
        friends = new ArrayList<>();

        //get entries from the database
        all_users.add(new FriendsData("Bob", "follow"));
        all_users.add(new FriendsData("Sally", "follow"));

        friends.add(new FriendsData("Susan", "following"));
        friends.add(new FriendsData("Alexa", "following"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mTabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.view_pager);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        return view;

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] tabTitles = {"All Users", "Friends"};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Tab1Fragment();
                case 1:
                    return new Tab2Fragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    public static class Tab1Fragment extends Fragment {

        private RecyclerView recyclerView;
        private FriendsRecyclerView adapter;


        FriendsRecyclerView.OnButtonClickListener listener = new FriendsRecyclerView.OnButtonClickListener() {
            @Override
            public void onButtonClickChange(int position) {
                //friends.set(position, "New Data"); // Update the data source

                FriendsData datapoint = all_users.get(position);

                if (datapoint.getButtonDetails().equals("Following")) {
                    datapoint.setButtonDetails("Follow");
                } else {
                    datapoint.setButtonDetails("Following");
                }
                adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_fragment_tab1, container, false);

            //get values from database

            recyclerView = view.findViewById(R.id.recycler_view);
            adapter = new FriendsRecyclerView(all_users, listener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return view;
        }
    }

    public static class Tab2Fragment extends Fragment {

        private RecyclerView recyclerView;
        private FriendsRecyclerView adapter;

        FriendsRecyclerView.OnButtonClickListener listener = new FriendsRecyclerView.OnButtonClickListener() {
            @Override
            public void onButtonClickChange(int position) {
                //friends.set(position, "New Data"); // Update the data source
                FriendsData data = friends.get(position);
                if (data.getButtonDetails().equals("Following")) {
                    data.setButtonDetails("Follow");
                } else {
                    data.setButtonDetails("Following");
                }
                adapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }
        };


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_fragment_tab2, container, false);


            recyclerView = view.findViewById(R.id.recycler_view);
            adapter = new FriendsRecyclerView(friends, listener);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return view;
        }
    }

}