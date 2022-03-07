package com.example.planetx;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.planetx.models.ConfigModel;
import com.example.planetx.utils.ConfigurationValidator;
import com.example.planetx.viewmodels.ConfigViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartRouterFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * This class serves as transparent router which determines the first page visible to the user of the application.
 * It does this by routing the user to either of 1 or 2 below:
 * 1. route the user to the configuration start page (ConfigWelcomeFragment) in the case when the user has not previously completed the required configuration.
 * 2. route the user to the home page (HomeFragment) in the case when the user has previously completed the required configuration
 */
public class StartRouterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Declare attributes
    private static final String TAG = StartRouterFragment.class.getSimpleName();
    private NavController navController;
    private ConfigViewModel configViewModel;

    //Layout inflated in the constructor
    public StartRouterFragment() {
        super(R.layout.fragment_start_router);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartRouterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartRouterFragment newInstance(String param1, String param2) {
        StartRouterFragment fragment = new StartRouterFragment();
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

        //Get the shared preferences
        Context context = this.getContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.config_data_shared_preference), Context.MODE_PRIVATE);

        //Check if the shared preferences contains CONFIG_JSON shared preference.
        //The result determines the page to be displayed (Configuration or Home page)
        boolean containsVci = sharedPref.contains(getString(R.string.config_json));


        if(containsVci) {
            Log.i(TAG, "onCreate: Configuration previously completed. called");
            String configPreferenceKey = getString(R.string.config_json);
            String configJson = sharedPref.getString(configPreferenceKey, null);
            try {
                ConfigModel configModel = ConfigurationValidator.parseJSON(configJson);
                configViewModel.setConfigModel(configModel);
            }
            catch (Exception exception) {
                //This line should never be reached
            }

            //Navigate to HomeFragment
            navController.navigate(R.id.action_startRouterFragment_to_homeFragment);
        }
        else {
            Log.i(TAG, "onCreate: Configuration not completed. called");
            //Navigate to ConfigWelcomeFragment
            navController.navigate(R.id.action_startRouterFragment_to_configWelcomeFragment);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated: Planet X closing ..... called");
        //End the activity when back is pressed from either ConfigWelcomeFragment or HomeFragment
        this.getActivity().finish();
    }
}