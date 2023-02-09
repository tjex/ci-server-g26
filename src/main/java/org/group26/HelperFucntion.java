package org.group26;


import java.io.BufferedReader;
import java.io.IOException;

import org.json.JSONObject;

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
	 * Clones the repo from branch and on path.
	 * @param cloningURL
	 * @param branch
	 * @param path
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void gitClone(String cloningURL, String branch, String path) throws IOException, InterruptedException {
		System.out.println("git clone -b " +  branch + " --single-branch " + cloningURL + " " + path);
		Process pro = Runtime.getRuntime().exec("git clone -b " +  branch + " --single-branch " + cloningURL + " " + path);
		pro.waitFor();
	}
}
