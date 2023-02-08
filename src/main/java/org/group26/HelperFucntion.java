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
	 * Takes in a String which comes from getBranchAndGitURL and formats it into a command. This command is then executed
	 * through Runtime.getRuntime() to clone the repo from webhook branch. Stored at path on the raspberry pi.
	 * 
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
    /**
     * Takes in a String for the github repo and a String for which branch to clone from. It then formats it into a command.
     * This command is then executed through Runtime.getRuntime() to clone the repo from webhook branch.
     * The repo is cloned to the path determined by String output
     *
     * @param cloningURL
     * @param branch
     * @param output
     * @throws IOException
     * @throws InterruptedException
     */
    public static void gitCloneWithOutputDir(String cloningURL, String branch, String output) throws IOException, InterruptedException {
        System.out.println("Attempt to clone with command: git clone -b " +  branch + " --single-branch " + cloningURL + " " + output);
        //Runtime.getRuntime().exec("git clone -b " + branch  + " " + cloningURL + " " + path);
        //Runtime.getRuntime().exec("git clone " + cloningURL + " " + ContinuousIntegrationServer.PATH);
        Process proc = Runtime.getRuntime().exec("git clone -b " +  branch + " --single-branch " + cloningURL + " " + output );
        proc.waitFor();
    }
}
