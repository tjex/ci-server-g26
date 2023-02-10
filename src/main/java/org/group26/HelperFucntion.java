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
     * Clones a Git repository to a specified location.
     *
     * The repository is cloned with the specified branch and the specified path. The method uses the `git clone` command to clone the repository.
     *
     * @param cloningURL the URL of the Git repository to clone
     * @param branch the branch to clone from the repository
     * @param path the path where the repository should be cloned to
     *
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static void gitClone(String cloningURL, String branch, String path) throws IOException, InterruptedException {
		System.out.println("git clone -b " +  branch + " --single-branch " + cloningURL + " " + path);
		Process pro = Runtime.getRuntime().exec("git clone -b " +  branch + " --single-branch " + cloningURL + " " + path);
		pro.waitFor();
	}
}
