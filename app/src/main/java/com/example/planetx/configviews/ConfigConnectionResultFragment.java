package com.example.planetx.configviews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.planetx.R;
import com.example.planetx.viewmodels.ConfigViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigConnectionResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This class displays the result of the wifi connection, success or failure.
 * In case of success, the green success icon and save button are displayed.
 * In case of failure, the red error icon and retry button are displayed
 */
public class ConfigConnectionResultFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Declare attributes
    private static  final String TAG = ConfigConnectionResultFragment.class.getSimpleName();
    private ImageView connectionResultImage;
    private TextView connectionResultMessage;
    private Button saveOrRetryButton;
    private NavController navController;
    private ConfigViewModel configViewModel;

    public ConfigConnectionResultFragment() {
        super(R.layout.fragment_config_connection_result);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigConnectionResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfigConnectionResultFragment newInstance(String param1, String param2) {
        ConfigConnectionResultFragment fragment = new ConfigConnectionResultFragment();
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize views
        connectionResultImage = view.findViewById(R.id.connection_result_image);
        connectionResultMessage = view.findViewById(R.id.connection_result_message);
        saveOrRetryButton = view.findViewById(R.id.save_or_retry_button);

        //Check the wifi connection state (connected or not connected).
        //The result determines the information (Success - Save or Error - Retry) to be displayed on the page.
        boolean wifiConnectionResult = configViewModel.getConnectionStateMutableLiveData().getValue();

        if(wifiConnectionResult) {
            Log.i(TAG, "onViewCreated: Received Successful wifi connection. called");
            setSuccessResultData();
            setSuccessResultListener();
        }
        else {
            Log.i(TAG, "onViewCreated: Received Error in wifi connection. called");
            setFailureResultData();
            setFailureResultListener();
        }
    }

    private void setSuccessResultData() {
        connectionResultImage.setImageResource(R.drawable.ic_round_check_circle_72);
        connectionResultMessage.setText(R.string.connection_success_msg);
        saveOrRetryButton.setText(R.string.save);
    }

    private void setSuccessResultListener() {
        saveOrRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Save button called");
                //Store the config data on the app preference
                configViewModel.storeConfigData();
                //Navigate to homeFragment
                navController.navigate(R.id.action_configConnectionResultFragment_to_homeFragment);
            }
        });
    }

    private void setFailureResultData() {
        connectionResultImage.setImageResource(R.drawable.ic_round_error_72);
        connectionResultMessage.setText(R.string.connection_failure_msg);
        saveOrRetryButton.setText(R.string.retry);
    }

    private void setFailureResultListener() {
        saveOrRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick(): Retry button called");
                navController.navigate(R.id.action_configConnectionResultFragment_to_configDataDisplayFragment);
            }
        });
    }
}