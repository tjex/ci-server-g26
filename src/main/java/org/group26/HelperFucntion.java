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
	 * Takes in a JSONObject which is connected to the webhook. This in turn extracts the git clone_url and the branch
	 * at which the webhook triggered. Returns a concatenated string which with both separated by space.
	 *
	 * @param json
	 * @return prepURL
	 */
	public static String getBranchAndGitURL(JSONObject json){
		JSONObject repo = (JSONObject) json.get("repository");
		String cloningUrl = repo.getString("clone_url");
		String branch = json.getString("ref");
		String[] refs = branch.split("/");
		branch = refs[refs.length - 1];
		String prepURL = branch + "/" + cloningUrl;
		return prepURL;
	}

	/**
	 * Takes in a String which comes from getBranchAndGitURL and formats it into a command. This command is then executed
	 * through Runtime.getRuntime() to clone the repo from webhook branch. Stored at path on the raspberry pi.
	 * @param branchGitURL
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void gitClone(String branchGitURL) throws IOException, InterruptedException {
		String[] branchAndgitURL = branchGitURL.split("/");
		String branch = branchAndgitURL[0];
		String cloningURL = branchAndgitURL[1];
		String path = "/home/g26/repo"; //--single-branch
		//Runtime.getRuntime().exec("git clone -b " + branch  + " " + cloningURL + " " + path);
		Runtime.getRuntime().exec("git clone " + cloningURL + " " + path);
	}
}
