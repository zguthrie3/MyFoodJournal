package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zguthrie3 on 2/13/2018.
 */

public class MyReviewsFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_MENU_OPTION = "menu_option";

    //parameters
    private String menuOptionParam;

    private MyReviewsFragment.OnMyReviewsInteractionListener mListener;
    private View view;
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    public MyReviewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param menuOptionParam the menu option being initialized.
     * @return A new instance of fragment ProfileFragment.
     */
    public static MyReviewsFragment newInstance(String menuOptionParam) {
        MyReviewsFragment fragment = new MyReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MENU_OPTION, menuOptionParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            menuOptionParam = getArguments().getString(ARG_MENU_OPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_myreviews, container, false);
        Button filtersButton = (Button) view.findViewById(R.id.filters_button);
        filtersButton.setOnClickListener(this);
        Button sortByButton = (Button) view.findViewById(R.id.sortby_button);
        sortByButton.setOnClickListener(this);
        sortByButton.setText("Sort By: \nMost Recent");
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        ListView listview = (ListView) view.findViewById(R.id.listviewID);
        DefaultActivity activity = (DefaultActivity) getActivity();
        HashMap<String, ReviewData> allreviews = activity.getAllReviews();
        data = new ArrayList<Map<String, String>>();
        for (String key: allreviews.keySet()) {
            ReviewData reviewdatum = allreviews.get(key);
            Map<String, String> datum = new HashMap<String, String>(4);
            datum.put("Restaurant Name", reviewdatum.restaurant_name);
            datum.put("Menu Item", reviewdatum.menuitem);
            datum.put("Description", reviewdatum.description);
            datum.put("Rating", reviewdatum.rating + "/5");
            datum.put("Date Submitted", reviewdatum.date_submitted);
            data.add(datum);
        }
         adapter = new SimpleAdapter(getContext(), data,
                R.layout.myreview_row,
                new String[] {"Restaurant Name", "Menu Item", "Description", "Rating"},
                new int[] {R.id.text1,
                        R.id.text2, R.id.text3, R.id.text4});
        listview.setAdapter(adapter);
        AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                adapterView.getItemAtPosition(position);
            }
        };
        listview.setOnItemClickListener(listListener);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MyReviewsFragment.OnMyReviewsInteractionListener) {
            mListener = (MyReviewsFragment.OnMyReviewsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMyReviewsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //handles button clicks in the fragment
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.filters_button):
                if (mListener != null) {
                    mListener.onFilterButtonClicked();
                }
                break;
            case (R.id.sortby_button):
                if (mListener != null) {
                    onSortByButtonClicked();
                }
                break;
            case (R.id.fab):
                if (mListener != null) {
                    mListener.onFloatingButtonClicked();
                }
                break;
        }
    }

    private static Comparator<Map<String, String>> rating_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return b.get("Rating").compareTo(a.get("Rating"));
        }
    };

    private static Comparator<Map<String, String>> restaurant_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return a.get("Restaurant Name").compareTo(b.get("Restaurant Name"));
        }
    };

    private static Comparator<Map<String, String>> food_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return a.get("Menu Item").compareTo(b.get("Menu Item"));
        }
    };

    private static Comparator<Map<String, String>> time_comparator = new Comparator<Map<String, String>>(){
        @Override
        public int compare(Map<String, String> a, Map<String, String> b){
            return b.get("Date Submitted").compareTo(a.get("Date Submitted"));
        }
    };



    public void onSortByButtonClicked() {
        Log.d("WISHLIST", "Sort By button clicked on Wishlist page");
        final View anchor = view.findViewById(R.id.sortby_button);
        PopupMenu popup = new PopupMenu(getContext(), anchor);
        getActivity().getMenuInflater().inflate(R.menu.sortby_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                
                switch (menuItem.getItemId()) {
                    case R.id.sortby_mostrecent:
                        Collections.sort(data, time_comparator);
                        break;
                    case R.id.sortby_rating:
                        Collections.sort(data, rating_comparator);
                        break;
                    case R.id.sortby_restaurant:
                        Collections.sort(data, restaurant_comparator);
                        break;
                    case R.id.sortby_food:
                        Collections.sort(data, food_comparator);
                        break;
                }
                adapter.notifyDataSetChanged();
                Button sortByButton = (Button) anchor;
                sortByButton.setText("Sort By: \n" + menuItem.getTitle());
                return true;
            }
        });

        popup.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMyReviewsInteractionListener {
        // TODO: Update argument type and name
        void onFilterButtonClicked();
        void onFloatingButtonClicked();
    }
}