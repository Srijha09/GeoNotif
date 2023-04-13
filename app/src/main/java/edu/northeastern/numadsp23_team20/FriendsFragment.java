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
    private static FriendsRecyclerView adapter_friends;

    private static FriendsRecyclerView adapter_all_users;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        all_users = new ArrayList<>();
        friends = new ArrayList<>();

        //get entries from the database
        all_users.add(new FriendsData("Bob", "following"));
        all_users.add(new FriendsData("Sally", "follow"));
        all_users.add(new FriendsData("Alexa", "following"));

        friends.add(new FriendsData("Bob", "following"));
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
        //private  FriendsRecyclerView adapter_all_users;


        FriendsRecyclerView.OnButtonClickListener listener = new FriendsRecyclerView.OnButtonClickListener() {
            @Override
            public void onButtonClickChange(int position) {
                //friends.set(position, "New Data"); // Update the data source

                FriendsData datapoint = all_users.get(position);

                if (datapoint.getButtonDetails().equals("Following")) {
                    datapoint.setButtonDetails("Follow");
                    friends.remove(datapoint);

                    //remove from database (user's friends) too.
                } else {
                    datapoint.setButtonDetails("Following");
                    friends.add(datapoint);
                    //add to database (user's friends)  too.
                }
                adapter_friends.notifyDataSetChanged(); // Notify the adapter that the data has changed
                adapter_all_users.notifyDataSetChanged(); // Notify the adapter that the data has changed

            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_fragment_tab1, container, false);

            //get values from database

            recyclerView = view.findViewById(R.id.recycler_view);
            adapter_all_users = new FriendsRecyclerView(all_users, listener);
            recyclerView.setAdapter(adapter_all_users);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return view;
        }
    }

    public static class Tab2Fragment extends Fragment {

        private RecyclerView recyclerView;
        //private  FriendsRecyclerView adapter_friends;

        FriendsRecyclerView.OnButtonClickListener listener = new FriendsRecyclerView.OnButtonClickListener() {
            @Override
            public void onButtonClickChange(int position) {
                //friends.set(position, "New Data"); // Update the data source
                FriendsData data = friends.get(position);

                for (int i = 0; i < all_users.size(); i++) {
                    if (data.getUserName().equals(all_users.get(i).getUserName())) {
                        all_users.get(i).setButtonDetails("follow");
                        adapter_all_users.notifyDataSetChanged(); // Notify the adapter that the data has changed
                    }
                }
                friends.remove(data);
                //remove from database (user friends) too.

                adapter_friends.notifyDataSetChanged(); // Notify the adapter that the data has changed
            }
        };


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.activity_fragment_tab2, container, false);


            recyclerView = view.findViewById(R.id.recycler_view);
            adapter_friends = new FriendsRecyclerView(friends, listener);
            recyclerView.setAdapter(adapter_friends);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            return view;
        }
    }

}