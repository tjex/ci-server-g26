package org.group26;


import org.eclipse.jetty.server.Request;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Arrays;
public class HelperFucntion {

	/**
	 *	Reads the connection response and returns it as an JSON object.
	 * 
	 * 	@param reader
	 * 	@return JSONObject with response from connection.
	 * 	@throws IOException
	 */
	public static JSONObject getJsonFromRequestReader(BufferedReader reader) throws IOException {
		String inputline;
		String jsonString = "";
		while((inputline = reader.readLine()) != null){
			jsonString += inputline + "\n";
		}
		System.out.println(jsonString);
		return new JSONObject(jsonString);
	}

	/**
	 * Takes in a String which comes from getBranchAndGitURL and formats it into a command. This command is then executed
	 * through Runtime.getRuntime() to clone the repo from webhook branch. Stored at path on the raspberry pi.
	 * @param cloningURL
	 * @param branch
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void gitClone(String cloningURL, String branch) throws IOException, InterruptedException {
		System.out.println("Attempt to clone with command: git clone " + cloningURL + " " + ContinuousIntegrationServer.PATH);
		//Runtime.getRuntime().exec("git clone -b " + branch  + " " + cloningURL + " " + path);
		//Runtime.getRuntime().exec("git clone " + cloningURL + " " + ContinuousIntegrationServer.PATH);
		Runtime.getRuntime().exec("git clone -b " +  branch + " --single-branch " + cloningURL + " " + ContinuousIntegrationServer.PATH);
	}
}
