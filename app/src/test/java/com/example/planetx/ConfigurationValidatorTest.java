package com.example.planetx;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.example.planetx.models.ConfigModel;
import com.example.planetx.utils.ConfigurationValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationValidatorTest {
    private ConfigModel configModel;
    private Gson gson;

    /**
     * Set up the environment for testing
     */
    @Before
    public void setUp() {
        gson = new GsonBuilder().create();

    }

    /**
     * Test config model for null input
     */
    @Test
    public void configModelWithNull() {
        String qrInput = "{\n" +
                "\tuserId: null,\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: 'myWifi',\n" +
                "\tpassword: '12345',\n" +
                "\tip: '123.234.12.146',\n" +
                "\tport: 1036\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        assertThrows(NullPointerException.class, () -> ConfigurationValidator.checkNulls(configModel));
    }

    /**
     * Test config model for empty values
     */
    @Test
    public void configModelWithEmpty() {
        String qrInput = "{\n" +
                "\tuserId: 'A19876',\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: 'myWifi',\n" +
                "\tpassword: '',\n" +
                "\tip: '123.234.12.146',\n" +
                "\tport: 1036\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        assertThrows(IllegalArgumentException.class, () -> ConfigurationValidator.checkEmptyOrBlanks(configModel));
    }

    /**
     * Test config model for blank values
     */
    @Test
    public void configModelWithBlank() {
        String qrInput = "{\n" +
                "\tuserId: 'A19876',\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: '  ',\n" +
                "\tpassword: '12345',\n" +
                "\tip: '123.234.12.146',\n" +
                "\tport: 1036\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        assertThrows(IllegalArgumentException.class, () -> ConfigurationValidator.checkEmptyOrBlanks(configModel));
    }

    /**
     * Test config model for ipv4 address with field greater than 255
     */
    @Test
    public void configModelWithIpGreaterThan255() {
        String qrInput = "{\n" +
                "\tuserId: 'A19876',\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: 'myWifi',\n" +
                "\tpassword: '12345',\n" +
                "\tip: '123.234.12.256',\n" +
                "\tport: 1036\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        assertThrows(IllegalArgumentException.class, () -> ConfigurationValidator.checkIpv4s(configModel));
    }

    /**
     * Test config model for ipv4 address with number of field less than 4
     */
    @Test
    public void configModelWithIpFieldsLessThan4() {
        String qrInput = "{\n" +
                "\tuserId: 'A19876',\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: 'myWifi',\n" +
                "\tpassword: '12345',\n" +
                "\tip: '123.234.12',\n" +
                "\tport: 1036\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        assertThrows(IllegalArgumentException.class, () -> ConfigurationValidator.checkIpv4s(configModel));
    }

    /**
     * Test config model for ipv4 address with number of fields greater than 4
     */
    @Test
    public void configModelWithIpFieldsGreaterThan4() {
        String qrInput = "{\n" +
                "\tuserId: 'A19876',\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: 'myWifi',\n" +
                "\tpassword: '12345',\n" +
                "\tip: '123.234.12.200.8',\n" +
                "\tport: 1036\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        assertThrows(IllegalArgumentException.class, () -> ConfigurationValidator.checkIpv4s(configModel));
    }

    /**
     * Test config model for port number greater than or equal to 65535
     */
    @Test
    public void configModelWithPortNumberGreaterThanOrEqual65535() {
        String qrInput = "{\n" +
                "\tuserId: 'A19876',\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: 'myWifi',\n" +
                "\tpassword: '12345',\n" +
                "\tip: '123.234.12.200',\n" +
                "\tport: 65535\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        assertThrows(IllegalArgumentException.class, () -> ConfigurationValidator.checkPorts(configModel));
    }

    /**
     * Test config model for port number less than or equal to 1024
     */
    @Test
    public void configModelWithPortNumberLessThanOrEqual1024() {
        String qrInput = "{\n" +
                "\tuserId: 'A19876',\n" +
                "\tcodeName: 'The General',\n" +
                "\tssid: 'myWifi',\n" +
                "\tpassword: '12345',\n" +
                "\tip: '123.234.12.200.8',\n" +
                "\tport: 1023\n" +
                "}";
        configModel = gson.fromJson(qrInput, ConfigModel.class);

        /*assertThrows(IllegalArgumentException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                ConfigurationValidator.checkPorts(configModel);
            }
        });*/
        assertThrows(IllegalArgumentException.class, () -> ConfigurationValidator.checkPorts(configModel));
    }

    /**
     * Test for null with null input
     */
    @Test
    public void nullValue() {
        boolean result = ConfigurationValidator.checkNull(null);
        assertTrue(result);
    }

    /**
     * Test for non null input
     */
    @Test
    public void nonNullValue() {
        boolean result = ConfigurationValidator.checkNull(123);
        assertFalse(result);
    }

    /**
     * Test for empty string
     */
    @Test
    public void emptyInput() {
        boolean result = ConfigurationValidator.checkEmptyOrBlank("");
        assertTrue(result);
    }

    /**
     * Test for blank string
     */
    @Test
    public void blankInput() {
        boolean result = ConfigurationValidator.checkEmptyOrBlank(" ");
        assertTrue(result);
    }

    /**
     * Test for non empty string nor blank string
     */
    @Test
    public void nonEmptyNorBlankInput() {
        boolean result = ConfigurationValidator.checkEmptyOrBlank("Hello Android");
        assertFalse(result);
    }

    /**
     * Test for ipv4 with letters
     */
    @Test
    public void ipv4WithLetter() {
        boolean result = ConfigurationValidator.checkIpv4("100.12d.111.2");
        assertFalse(result);
    }

    /**
     * Test for ipv4 with value greater than 255
     */
    @Test
    public void ipv4GreaterThan255() {
        boolean result = ConfigurationValidator.checkIpv4("100.256.111.2");
        assertFalse(result);
    }

    /**
     * Test for ipv4 with more than 4 fields
     */
    @Test
    public void ipv4MoreThan4Fields() {
        boolean result = ConfigurationValidator.checkIpv4("100.89.111.2.124");
        assertFalse(result);
    }

    /**
     * Test for correctly formed ipv4 address
     */
    @Test
    public void ipv4Correct() {
        boolean result = ConfigurationValidator.checkIpv4("100.89.111.2");
        assertTrue(result);
    }

    /**
     * Test for port with value greater than or equal to 65535
     */
    @Test
    public void portGreaterThan65535() {
        boolean result = ConfigurationValidator.checkPort(65536);
        assertFalse(result);
    }

    /**
     * Test for port with value less than or equal to 1024
     */
    @Test
    public void portLessThan1024() {
        boolean result = ConfigurationValidator.checkPort(1024);
        assertFalse(result);
    }

    /**
     * Test for port with value between (1024 and 65535)
     */
    @Test
    public void portBetween1024And65535() {
        boolean result = ConfigurationValidator.checkPort(32689);
        assertTrue(result);
    }

}
