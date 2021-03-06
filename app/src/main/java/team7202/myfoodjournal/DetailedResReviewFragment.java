package team7202.myfoodjournal;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Zach on 3/31/2018.
 */

public class DetailedResReviewFragment extends Fragment implements View.OnClickListener {

    private static Map<String, String> reviewInfo;
    private View view;
    private Place restaurantName;
    private OnResReviewInteractionListener mListener;
    public static DetailedResReviewFragment newInstance(Map<String, String> information) {
        DetailedResReviewFragment fragment = new DetailedResReviewFragment();
        reviewInfo = information;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detailed_res_review, container, false);
        final TextView reviewedBy = (TextView) view.findViewById(R.id.username);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(reviewInfo.get("User ID"));
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = (String) dataSnapshot.child("username").getValue();
                reviewedBy.setText(username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        reviewedBy.setOnClickListener(this);
        TextView name = (TextView) view.findViewById(R.id.restuarant_name);
        name.setText(reviewInfo.get("Restaurant Name"));
        TextView menuItem = (TextView) view.findViewById(R.id.menu_item_name);
        menuItem.setText(reviewInfo.get("Menu Item"));
        TextView rating = (TextView) view.findViewById(R.id.rating_value);
        rating.setText(reviewInfo.get("Rating"));
        TextView description = (TextView) view.findViewById(R.id.description_value);
        description.setText(reviewInfo.get("Description"));
        description.setMovementMethod(new ScrollingMovementMethod());

        final DefaultActivity activity = (DefaultActivity) getActivity();
        restaurantName = activity.getRestaurantName();

        Button addToWishlistButton = (Button) view.findViewById(R.id.add_wishlist_button);
        addToWishlistButton.setOnClickListener(this);
        Button cancelButton = (Button) view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResReviewInteractionListener) {
            mListener = (OnResReviewInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnResReviewInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Calling the method through mListener will run the code in the default activity
            // which should swap the fragment to go to the right fragment
            case (R.id.add_wishlist_button):
                if (mListener != null) {
                    mListener.onAddWishlistButtonClicked(reviewInfo);
                }
                break;
            case (R.id.cancel_button):
                if (mListener != null) {
                    mListener.onCancelButtonClicked();
                }
                break;
            case (R.id.username):
                if (mListener != null) {
                    mListener.onUsernameLinkClicked(reviewInfo);
                }
                break;
        }
    }

    public interface OnResReviewInteractionListener {
        // TODO: Update argument type and name
        void onAddWishlistButtonClicked(Map<String, String> reviewInfo);
        void onCancelButtonClicked();
        void onUsernameLinkClicked(Map<String, String> reviewInfo);
    }
}
