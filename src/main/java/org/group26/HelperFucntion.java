package org.group26;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HelperFucntion {
    /*
    @param HTTPURLConnection conn
    @return JSONObject jsonstring
    Reads line by line and concatinates a string with the repo from github, which is a json object. This is then used to
    create a JSONObject.
     */
    public static JSONObject getJsonFromConnection(HttpURLConnection connn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connn.getInputStream()));
        String inputline;
        String jsonString = "";
        while((inputline = in.readLine()) != null){
            jsonString += inputline + "\n";
        }
        JSONObject jo = new JSONObject(jsonString);
        return jo;
    }
}
