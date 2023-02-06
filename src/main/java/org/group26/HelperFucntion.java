package org.group26;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class HelperFucntion {
    
	/**
	 *	Reads the connection response and returns it as an JSON object.
	 * 
	 * 	@param conn
	 * 	@return JSONObject with response from connection.
	 * 	@throws IOException
	 */
    public static JSONObject getJsonFromConnection(HttpURLConnection conn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputline;
        String jsonString = "";
        while((inputline = in.readLine()) != null){
            jsonString += inputline + "\n";
        }
        JSONObject jo = new JSONObject(jsonString);
        return jo;
    }
}
