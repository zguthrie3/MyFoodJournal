package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zach on 3/30/2018.
 */

public class RestaurantFragment extends Fragment implements View.OnClickListener {
    //parameters
    private RestaurantFragment.OnRestaurantInteractionListener mListener;
    private View view;

    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    public RestaurantFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileFragment.
     */
    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String[] keyStrings = {"Menu Item", "Description", "Rating"};
        view = inflater.inflate(R.layout.restaurant_summary_fragment, container, false);
        TextView title = (TextView) view.findViewById(R.id.name_header);
        TextView address = (TextView) view.findViewById(R.id.address_header);

        final DefaultActivity activity = (DefaultActivity) getActivity();
        final Place restaurantName = activity.getRestaurantName();
        title.setText(restaurantName.getName());
        address.setText(restaurantName.getAddress());


        data = new ArrayList<>();
        ListView listview = (ListView) view.findViewById(R.id.listviewID);

        DatabaseReference restaurantRef = FirebaseDatabase.getInstance().getReference().child("restaurants").child(restaurantName.getId());

        adapter = new SimpleAdapter(getContext(), data,
                R.layout.restaurantreview_row, keyStrings,
                new int[] {R.id.text1, R.id.text2, R.id.text3});
        listview.setAdapter(adapter);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        restaurantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data.clear();
                for (DataSnapshot entry : dataSnapshot.getChildren()) {
                    Map reviewInfo = (Map) entry.getValue();
                    Map<String, String> datum = new HashMap<>();
                    datum.put("Restaurant Name", (String) reviewInfo.get("restaurant_name"));
                    datum.put("Menu Item", (String) reviewInfo.get("menuitem"));
                    datum.put("Description", (String) reviewInfo.get("description"));
                    datum.put("Rating", reviewInfo.get("rating") + "/5");
                    datum.put("Date Submitted", (String) reviewInfo.get("date_submitted"));
                    datum.put("User ID", (String) reviewInfo.get("userId"));
                    datum.put("Review ID", (String) reviewInfo.get("reviewId"));
                    datum.put("Restaurant ID", (String) reviewInfo.get("restaurant_id"));
                    datum.put("Address", (String) reviewInfo.get("address"));
                    data.add(datum);
                }
                Collections.sort(data, time_comparator);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        AdapterView.OnItemClickListener listListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Map<String, String> info = (Map<String, String>) adapterView.getItemAtPosition(position);
                Fragment fragment;
                if (info.get("User ID").equals(user.getUid())) {
                    fragment = DetailedMyReviewFragment.newInstance(info);
                } else {
                    fragment = DetailedResReviewFragment.newInstance(info);
                }
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).addToBackStack("Restaurant Reviews").commit();
            }
        };

        listview.setOnItemClickListener(listListener);
        Button sortByButton = (Button) view.findViewById(R.id.sortby_button);
        sortByButton.setOnClickListener(this);
        sortByButton.setText("Sort By: \nMost Recent");
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RestaurantFragment.OnRestaurantInteractionListener) {
            mListener = (RestaurantFragment.OnRestaurantInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRestaurantInteractionListener");
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
            case (R.id.search_bar):
                if (mListener != null) {
                    mListener.onSearchBarClicked();
                }
                break;
            case (R.id.sortby_button):
                if (mListener != null) {
                    onSortByButtonClicked();
                }
                break;
        }
    }

    public void onSortByButtonClicked() {
        Log.d("RESTAURANT", "Sort By button clicked on Restaurant page");
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
    public interface OnRestaurantInteractionListener {
        // TODO: Update argument type and name
        void onSearchBarClicked();
    }
}