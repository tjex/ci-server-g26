package com.group26;


import org.group26.HelperFucntion;
import org.junit.Test;
import org.group26.ContinuousIntegrationServer;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HelperFunction_test {
    /*
    Test if a JSONObject is created through the function getJsonFromConnection and contains appropriate repo.
     */
    @Test
    public void getCorrectRepoFromApi()
    {
        ContinuousIntegrationServer ser = new ContinuousIntegrationServer();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL("https://api.github.com/repos/tjex/ci-server-g26").openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        JSONObject json;
        try {
            json = HelperFucntion.getJsonFromConnection(conn);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(json.getString("full_name"), "tjex/ci-server-g26");
    }

}