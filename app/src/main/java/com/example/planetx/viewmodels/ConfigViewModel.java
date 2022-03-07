package com.example.planetx.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.planetx.R;
import com.example.planetx.StartRouterFragment;
import com.example.planetx.models.ConfigModel;
import com.example.planetx.utils.ConfigurationValidator;
import com.example.planetx.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.function.Consumer;

/**
 * This class serves as transparent router which determines the first page visible to the user of the application.
 * It does this by routing the user to either of 1 or 2 below:
 * 1. route the user to the configuration start page (ConfigWelcomeFragment) in the case when the user has not previously completed the required configuration.
 * 2. route the user to the home page (HomeFragment) in the case when the user has previously completed the required configuration
 */
public class ConfigViewModel extends AndroidViewModel {

    //Declare attributes
    private static final String TAG = ConfigViewModel.class.getSimpleName();
    private ConfigModel configModel; //Contains the user info and wifi info to connect to the network
    private MutableLiveData<Boolean> isLoadingMutableLiveData; //In the future replace with enum for different loading states
    private MutableLiveData<Boolean> connectionStateMutableLiveData; //Stores the wifi connection state

    public ConfigViewModel(@NonNull Application application) {
        super(application);
        isLoadingMutableLiveData = new MutableLiveData<>(false);
        connectionStateMutableLiveData = new MutableLiveData<>(false);
    }

    //Deserialize and validate the vci JSON and call setVciModel(..) on the result
    public void setConfigData(String configJson) throws Exception {
        Log.d(TAG, "setConfigData(): called");
        ConfigModel configModel = ConfigurationValidator.validateConfig(configJson);
        setConfigModel(configModel);
    }

    //Result of the scan of the qr code
    public void setConfigModel(ConfigModel configModel) {
        Log.d(TAG, "setConfigModel(): called");
        this.configModel = configModel;
    }

    public ConfigModel getConfigModel() {
        Log.d(TAG, "getConfigModel(): called");
        return configModel;
    }

    public LiveData<Boolean> getIsLoadingMutableLiveData() {
        return isLoadingMutableLiveData;
    }

    public LiveData<Boolean> getConnectionStateMutableLiveData() {
        return connectionStateMutableLiveData;
    }

    //Use callback as argument or use the CompletableFuture<Boolean> if it works as an async method.
    //Note: This method does not disconnect from Leo after successful connection. Disconnection is done in the WizardConnectionResultFragment
    public void testConnect() {
        Log.d(TAG, "testConnect(): called");
        //Disable buttons and indicate connection in progress
        isLoadingMutableLiveData.setValue(true);
        //Checks connection to wifi network
        String ssid = configModel.getSsid();
        String password = configModel.getPassword();
        NetworkUtils.connectToWifi(getApplication(), ssid, password, aBoolean -> {
            connectionStateMutableLiveData.postValue(aBoolean);
            isLoadingMutableLiveData.postValue(false);
            if(aBoolean) {
                //Connection Successful
                Log.d(TAG, "testConnect(): called => Connection to Wifi successful");
            }
            else {
                //Connection failure
                Log.d(TAG, "testConnect(): called => Connection to Wifi failed");
            }
        });

    }

    //After successful testing, store the cofig data on the app preference
    public void storeConfigData() {
        //Serialize the vci data
        ConfigModel configModel = getConfigModel();
        String configJson = new Gson().toJson(configModel);

        //Get the shared preferences
        Context context = getApplication().getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getApplication().getString(R.string.config_data_shared_preference), Context.MODE_PRIVATE);

        //Write to the CONFIG_JSON shared preference. TODO: See EncryptedSharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getApplication().getString(R.string.config_json), configJson);
        editor.apply();
    }
}
