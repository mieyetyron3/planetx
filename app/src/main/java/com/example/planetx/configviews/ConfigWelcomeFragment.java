package com.example.planetx.configviews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.planetx.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigWelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This is the first page to be displayed to begin configuration.
 * It provides information about the requirements for configuration and a Start button
 */
public class ConfigWelcomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Declare attributes
    private static final String TAG = ConfigWelcomeFragment.class.getSimpleName();
    private NavController navController;
    private Button configStartButton;

    //Layout inflated in the constructor
    public ConfigWelcomeFragment() {
        super(R.layout.fragment_config_welcome);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigWelcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigWelcomeFragment newInstance(String param1, String param2) {
        ConfigWelcomeFragment fragment = new ConfigWelcomeFragment();
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

        //Initialize fragment resources
        navController = NavHostFragment.findNavController(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize views
        configStartButton = view.findViewById(R.id.config_start_button);

        //Set listeners for clickable views
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        //Config Start Button
        configStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Start button called");
                navController.navigate(R.id.action_configWelcomeFragment_to_configQrScanFragment);
            }
        });
        //Set other view listeners below
    }
}