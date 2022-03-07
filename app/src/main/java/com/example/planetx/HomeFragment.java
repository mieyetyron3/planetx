package com.example.planetx;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.planetx.viewmodels.ConfigViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final  String TAG = HomeFragment.class.getSimpleName();
    private ImageView connectionStateImage;
    private ConfigViewModel configViewModel;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Instantiate view model
        configViewModel = new ViewModelProvider(requireActivity()).get(ConfigViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Initialize views
        connectionStateImage = view.findViewById(R.id.connection_state_image);

        //Set listeners for clickable views
        setOnClickListeners();

        //Set connection state image
        if (configViewModel.getConnectionStateMutableLiveData().getValue()) {
            connectionStateImage.setImageResource(R.drawable.ic_baseline_cast_connected_32);
        } else {
            connectionStateImage.setImageResource(R.drawable.ic_baseline_cast_32);
        }

        //livedata observers
        setLiveDataObservers();
    }

    private void setOnClickListeners() {
        connectionStateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!configViewModel.getConnectionStateMutableLiveData().getValue()) {
                    configViewModel.testConnect();
                }
            }
        });
    }

    private void setLiveDataObservers() {
        // Create the observer which observes and reacts to the connection state.
        final Observer<Boolean> connectionStatusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean connectionState) {
                Log.i(TAG, "onChanged: " + "of connectionStatusObserver called." + connectionState.toString() + " was received");
                if(connectionState) {
                    connectionStateImage.setImageResource(R.drawable.ic_baseline_cast_connected_32);
                }
                else {
                    connectionStateImage.setImageResource(R.drawable.ic_baseline_cast_32);
                }

            }
        };
        // Observe the connectionState LiveData, passing in this fragment as the LifecycleOwner and the observer.
        configViewModel.getConnectionStateMutableLiveData().observe(getViewLifecycleOwner(), connectionStatusObserver);
    }
}