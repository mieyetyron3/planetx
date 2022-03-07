package com.example.planetx.utils;

import static android.content.Context.WIFI_SERVICE;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static String currentSsid = "";
    private static boolean connected = false; //A workaround since Android does not provide the ssid in the WifiInfo for API < 31

    /**
     * Use this method to connect to a wifi network using the parameters below.
     * This method will call connectToWifiUsingConnectivityManager or connectToWifiUsingWifiManager (to be implemented)
     * for API levels greater than 28 and less or equal 28 respectively
     *
     * @param ssid Parameter 1.
     * @param password Parameter 2.
     * @param callback Parameter 3.
     * The callback's accept method has a single boolean parameter
     * which indicates the connection outcome (success or failure) of the wifi connection using true or false respectively.
     */
    // TODO: Implementation of wifi connection for API levels below API level 29 (Q)
    public static void connectToWifi(Application application, String ssid, String password, Consumer<Boolean> callback) {
        Log.d(TAG, "connectToWifi(): called");
        if(connected && currentSsid.equals(ssid)) {
            callback.accept(true);
            return;
        }

        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //TODO: cHECK NETWORK CONNECTION STATUS if it is connected to vciModel's network. If connected return callback.accept(true);
                    connectToWifiUsingConnectivityManager(application, ssid, password, callback);
                }
                else {
                    connectToWifiUsingWifiManager(application, ssid, password, callback);
                }
            }
        });
    }

    /**
     * Use this method to connect to a wifi network for API levels greater than 28.
     * It uses the ConnectivityManager API and WifiNetworkSpecifier (Network request API) dedicated to IoT peer connectivity.
     * The wifi network connection is within the scope of only the calling app.
     * This implementation does not provide internet connection. It only provides data exchange capability.
     *
     * @param ssid Parameter 1.
     * @param password Parameter 2.
     * @param callback Parameter 3.
     * The callback's accept method has a single boolean parameter
     * which indicates the connection outcome (success or failure) of the wifi connection using true or false respectively.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void connectToWifiUsingConnectivityManager(Application application, String ssid, String password, Consumer<Boolean> callback) {
        Log.d(TAG, "connectToWifiUsingConnectivityManager(): called");
        final NetworkSpecifier specifier =
                new WifiNetworkSpecifier.Builder()
                        .setSsid(ssid)
                        .setWpa2Passphrase(password)
                        .build();

        final NetworkRequest request =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        //.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .setNetworkSpecifier(specifier)
                        .build();

        final ConnectivityManager connectivityManager = (ConnectivityManager)
                application.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.d(TAG, "connectToWifiUsingConnectivityManager end(): called");
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                // do success processing here..
                Log.i(TAG, "onAvailable: "+ "Connected to wifi");
                connected = true;
                currentSsid = ssid;
                callback.accept(true);
            }

            @Override
            public void onUnavailable() {
                // do failure processing here..
                Log.e(TAG, "onUnavailable: " + "Unable to connect to wifi");
                connected = false;
                currentSsid = "";
                callback.accept(false);
                // Release the request when done.
                connectivityManager.unregisterNetworkCallback(this);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                // Release the request when done.
                connected = false;
                currentSsid = "";
                connectivityManager.unregisterNetworkCallback(this);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
                WifiInfo wifiInfo = (WifiInfo) networkCapabilities.getTransportInfo();
                /*currentSsid = wifiInfo.getSSID();*/
            }
        };

        connectivityManager.requestNetwork(request, networkCallback);
    }

    private static void connectToWifiUsingWifiManager(Application application, String ssid, String password, Consumer<Boolean> callback) {
        Log.d(TAG, "connectToWifiUsingWifiManager(): called");
        // TODO: Implementation of wifi connection using WifiManager
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.preSharedKey = "\"" + password + "\"";

        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);



        WifiManager wifiManager = (WifiManager)application.getApplicationContext().getSystemService(WIFI_SERVICE);

        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        boolean enabled = wifiManager.enableNetwork(netId, true);

        boolean connected = wifiManager.reconnect();

        if(connected && enabled) {
            if(!wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(true);
                Log.i("Wifi", "Wifi enabled");
            }
            Log.i(TAG, "connectToWifiUsingWifiManager: "+ "Connected to wifi");
            callback.accept(true);
            //Possibly do clean up
        }
        else {
            Log.e(TAG, "connectToWifiUsingWifiManager: " + "Unable to connect to wifi");
            callback.accept(false);
        }
    }

}
