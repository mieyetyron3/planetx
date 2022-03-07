package com.example.planetx.configviews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.planetx.R;
import com.example.planetx.models.ConfigModel;
import com.example.planetx.viewmodels.ConfigViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigDataDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This class displays the data received from the qr scan.
 * It also provides buttons Previous and Connect to return to scan page and connect to wifi respectively.
 */
public class ConfigDataDisplayFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Declare attributes
    private static final String TAG = ConfigDataDisplayFragment.class.getSimpleName();
    private Button previousButton;
    private Button connectButton;
    private NavController navController;
    private ConfigViewModel configViewModel;
    private ConfigModel configModel;
    private TextView userIdValue;
    private TextView codeNameValue;
    private ProgressBar isLoadingProgressBar;
    private boolean enableNavigation;

    public ConfigDataDisplayFragment() {
        super(R.layout.fragment_config_data_display);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigDataDisplayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigDataDisplayFragment newInstance(String param1, String param2) {
        ConfigDataDisplayFragment fragment = new ConfigDataDisplayFragment();
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

        //Instantiate view model
        configViewModel = new ViewModelProvider(requireActivity()).get(ConfigViewModel.class);

        //initialize Config model with date from qr scan
        configModel = configViewModel.getConfigModel();

        //The next line resets the corresponding variable for every view creation
        enableNavigation = false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize views
        previousButton = view.findViewById(R.id.previous_button);
        connectButton = view.findViewById(R.id.connect_button);
        userIdValue = view.findViewById(R.id.user_id_value);
        codeNameValue = view.findViewById(R.id.code_name_value);
        isLoadingProgressBar = view.findViewById(R.id.is_loading_progress_bar);

        //Set listeners for clickable views
        setOnClickListeners();

        //Set the values for the Config data
        setConfigData();

        //livedata observers
        setLiveDataObservers();
    }

    private void setOnClickListeners() {
        //Previous Button
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Previous button called");
                navController.navigate(R.id.action_configDataDisplayFragment_to_configQrScanFragment);
            }
        });

        //Connect Button
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Connect button called");
                enableNavigation = true;
                configViewModel.testConnect();
            }
        });
    }

    private void setConfigData() {
        Log.i(TAG, "setConfigData(): called");

        String userId = configModel.getUserId();
        userIdValue.setText(userId);

        String codeName = configModel.getCodeName();
        codeNameValue.setText(codeName);
    }

    private void setLiveDataObservers() {
        // Create the observer which determines the progress indicator's visibility.
        final Observer<Boolean> isLoadingObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean isLoading) {
                Log.i(TAG, "onChanged: of isLoadingObserver called. " + isLoading.toString() + " was received");
                if(isLoading) {
                    //Disable the UI elements here
                    isLoadingProgressBar.setVisibility(View.VISIBLE);
                    previousButton.setEnabled(false);
                    connectButton.setEnabled(false);
                }
                else {
                    //Enable the UI elements here
                    isLoadingProgressBar.setVisibility(View.INVISIBLE);
                    previousButton.setEnabled(true);
                    connectButton.setEnabled(true);
                }
            }
        };
        // Observe the isLoading LiveData, passing in this fragment as the LifecycleOwner and the observer.
        configViewModel.getIsLoadingMutableLiveData().observe(getViewLifecycleOwner(), isLoadingObserver);

        // Create the observer which observes and reacts to the connection state.
        final Observer<Boolean> connectionStatusObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean connectionState) {
                Log.i(TAG, "onChanged: " + "of connectionStatusObserver called." + connectionState.toString() + " was received");
                if(enableNavigation) {
                    navController.navigate(R.id.action_configDataDisplayFragment_to_configConnectionResultFragment);
                }

            }
        };
        // Observe the connectionState LiveData, passing in this fragment as the LifecycleOwner and the observer.
        configViewModel.getConnectionStateMutableLiveData().observe(getViewLifecycleOwner(), connectionStatusObserver);
    }


}