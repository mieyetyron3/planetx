package com.example.planetx.utils;

import androidx.annotation.NonNull;

import com.example.planetx.models.ConfigModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConfigurationValidator {
    public static ConfigModel validateConfig(@NonNull String qrInput) throws Exception {
        //parseJSON
        //check null and empty
        //Verify data type alignment

        ConfigModel configModel = parseJSON(qrInput);

        checkNulls(configModel);

        checkEmptyOrBlanks(configModel);

        checkIpv4s(configModel);

        checkPorts(configModel);

        return configModel;
    }

    // TODO: Create a separate class for handling JSON and move this method there
    public static ConfigModel parseJSON(@NonNull String qrInput) throws Exception {
        Gson gson = new GsonBuilder().create();
        ConfigModel configModel = null;

        configModel = gson.fromJson(qrInput, ConfigModel.class);

        return configModel;
    }

    public static void checkNulls(@NonNull ConfigModel configModel) {

        String userId = configModel.getUserId();
        if(checkNull(userId)) {
            throw new NullPointerException("User Id is null");
        }

        String codeName = configModel.getCodeName();
        if(checkNull(codeName)) {
            throw new NullPointerException("Code Name is null");
        }


        String ssid = configModel.getSsid();
        if(checkNull(ssid)) {
            throw new NullPointerException("Ssid is null");
        }

        String password = configModel.getPassword();
        if(checkNull(password)) {
            throw new NullPointerException("Password is null");
        }

        String ip = configModel.getIp();
        if(checkNull(ip)) {
            throw new NullPointerException("IP is null");
        }
        Integer port = configModel.getPort();
        if(checkNull(port)) {
            throw new NullPointerException("Port is null");
        }
    }

    public static boolean checkNull(@NonNull Object object) {
        return object == null;
    }

    //For the strings
    public static void checkEmptyOrBlanks(@NonNull ConfigModel configModel) {

        String userId = configModel.getUserId();
        if(checkEmptyOrBlank(userId)) {
            throw new IllegalArgumentException("User Id is empty or blank");
        }

        String codeName = configModel.getCodeName();
        if(checkEmptyOrBlank(codeName)) {
            throw new IllegalArgumentException("Code Name is empty or blank");
        }

        String ip = configModel.getIp();
        if(checkEmptyOrBlank(ip)) {
            throw new IllegalArgumentException("IP is empty or blank");
        }

        String ssid = configModel.getSsid();
        if(checkEmptyOrBlank(ssid)) {
            throw new IllegalArgumentException("Ssid is empty or blank");
        }

        String password = configModel.getPassword();
        if(checkEmptyOrBlank(password)) {
            throw new IllegalArgumentException("Password is empty or blank");
        }

    }

    public static boolean checkEmptyOrBlank(@NonNull String string) {
        return string.isEmpty() || isBlank(string);
    }

    public static void checkIpv4s(@NonNull ConfigModel configModel) {
        String ip = configModel.getIp();

        if(!checkIpv4(ip)) {
            throw new IllegalArgumentException("IP is not a valid IP address");
        }
    }

    public static boolean checkIpv4(@NonNull String string) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return string.matches(PATTERN);
    }

    public static void checkPorts(@NonNull ConfigModel configModel) {
        Integer port = configModel.getPort();

        if(!checkPort(port)) {
            throw new IllegalArgumentException("Port is not between 1024 and 65535 exclusive");
        }
    }

    public static boolean checkPort(@NonNull Integer integer) { //between1024And65535
        return integer > 1024 && integer < 65535;
    }

    private static boolean isBlank(@NonNull String string) {
        return string.trim().isEmpty();
    }
}
