package org.group26;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

/**
 *	Skeleton of a ContinuousIntegrationServer which acts as webhook.
 *
 *	See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
	/**
	 * 	Path on server where the repository will be cloned to.
	 */
	public static final String PATH = "/home/g26/repo/";
	
	/**
	 * 	Path on server to build files.
	 */
    public static final String BUILD_PATH = "/home/g26/build/";

	private enum CommitStatus {
		ERROR,
		FAILURE,
		PENDING,
		SUCCESS
	}
	
	/**
	 * 	{@inheritDoc}
	 */
	public void handle(
			String target, 
			Request baseRequest, 
			HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		
		File file = new File(PATH);
		if(file.isDirectory()){
			FileUtils.deleteDirectory(file);
			//Process pro = Runtime.getRuntime().exec("rm -rf " +  PATH);
		}
		baseRequest.setHandled(true);
		
		// response.getWriter().println("START OF LIFE");
		// response.getWriter().println(request.getHeader("User-Agent"));
		System.out.println(request.getHeader("User-Agent"));

		boolean pushEvent = false;
		if(request.getHeader("User-Agent").contains("GitHub-Hookshot")){
			//response.getWriter().println("RECOGNISING THE USER IS FROM GITHUB");
			if(request.getHeader("X-GitHub-Event").equals("push")){
				pushEvent = true;
				//System.out.println("Succesfully got payload from webhook");
				// response.getWriter().println("WEBHOOK WENT THROUGH ALL THE if statements");
			}
		}

		System.out.println(request.getHeaderNames().toString());

		// Get payload as JSON
		JSONObject requestJson = HelperFucntion.getJsonFromRequestReader(request.getReader());
		boolean buildEval = false;
        BuildStatus buildStatus = new BuildStatus();
		if(pushEvent) {
			// response.getWriter().println("Succesfully found the webhook and about to clone");
			boolean status;
			try {
				status = cloneRepository(requestJson);
				if (status)
					System.out.println("Successfully cloned repository");
					System.out.println("Starting build of cloned repo");
					buildStatus = buildRepo(PATH);
			} catch (Exception e) { e.printStackTrace(); }
		}
		String commitURL = requestJson.getJSONObject("head_commit").getString("url");

		if(buildStatus.success){
            System.out.println("successful build eval - true");
			sendResponse(CommitStatus.SUCCESS, commitURL, buildStatus);
		}
		else{
            System.out.println("successful build eval - false");
			sendResponse(CommitStatus.FAILURE, commitURL, buildStatus);
		}

        saveBuildStatus(buildStatus,commitURL,BUILD_PATH);
        System.out.println("CI job done");
	}

	/**
	 * 	Clones the repository into folder: {@code ContinuousIntegrationServer.PATH}/ci-server-g26/
	 *
	 * 	@param payload JSON payload from GitHub web hook
	 * 
	 * 	@throws IOException If an I/O error occurs 
	 * 	@throws InterruptedException If the process is interrupted
	 * 
	 * 	@return true if repository was successfully cloned.
	 */
	public boolean cloneRepository(JSONObject payload) throws IOException, InterruptedException {
		// Gets the relevant info from json file such as clone url and branch
		JSONObject repo = (JSONObject) payload.get("repository");
		String cloningURL= repo.getString("clone_url");
		System.out.println("cloningURL: " + cloningURL);
		String branch = payload.getString("ref");
		String[] refs = branch.split("/");
		int counter = 0;
		branch = "";
		for (String bra:refs) {
			if(counter > 1){
				branch += bra + "/";
			}
			counter ++;
		}
		branch = branch.substring(0, branch.length() - 1);
		System.out.println(branch);
		HelperFucntion.gitClone(cloningURL, branch, ContinuousIntegrationServer.PATH);
		
		// Returns true if repository was successfully cloned
		File file = new File(ContinuousIntegrationServer.PATH + "ci-server-g26/");
		return file.isDirectory();
	}

	/**
	 * 	Sends response back to GitHub.
	 *
	 * 	@param status Commit status (error, failure, pending, success)
	 * 	@param commitUrl The pushed commit URL
	 * 	@param buildStatus The result of building this commit
	 * 
	 * 	@throws IOException If HTTP client can't send response
	 * 
	 *  @see <a href="https://docs.github.com/en/rest/commits/statuses?apiVersion=2022-11-28">GitHub status docs</a>
	 */
	public void sendResponse(CommitStatus status, String commitUrl, BuildStatus buildStatus) throws IOException {
		
		System.out.println("Sending response to commit url: " + commitUrl);
		
		String token = System.getenv("CI_TOKEN");
		
		// Get commit id from URL
		String[] split = commitUrl.split("/");
		String commitId = split[split.length - 1]; 
		
		CloseableHttpClient client = HttpClientBuilder.create().build();

		HttpPost response = new HttpPost("https://api.github.com/repos/tjex/ci-server-g26/statuses/" + commitId);
		response.setHeader("Authorization", "Bearer " + token);
		response.setHeader("Content-type", "application/json");
		response.setHeader("Accept", "application/vnd.github.v3+json");

		JSONObject body = new JSONObject();
		body.put("owner", "tjex");
		body.put("repo", "ci-server-g26");
		body.put("sha", commitId);
		body.put("state", status.toString().toLowerCase());
		if(buildStatus.success){
			body.put("description","The build succeeded!");
		}
		else{
			body.put("description","The build failed! :(");
		}
		body.put("context","CI-Server-g26");
		StringEntity params = new StringEntity(body.toString());
		response.setEntity(params);

		System.out.println("Response payload:");
		System.out.println(body.toString());
		
		// Send POST to GitHub	
		client.execute(response);
	}

	/**
	 * 	Starts the server on port 8026
	 * 
	 * 	@param args No command line arguments needed.
	 * 	@throws Exception If an error occurs
	 */
	public static void main(String[] args) throws Exception {
        Server server = new Server(8026);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }

	/**
	 * Builds the cloned down repo from git push branch and evaluates if it's a success or not
	 * 
	 * @param path File path to repository
	 * 
	 * @return A {@link org.group26.BuildStatus} object containing information about the build.
	 * 
	 * @throws IOException If an I/O error occurs.
	 * @throws InterruptedException If build command fails.
	 */
	public BuildStatus buildRepo(String path) throws IOException, InterruptedException {
		File file = new File(path);
		System.out.println(file.isDirectory() + " is directory " + file.getName());

		ProcessBuilder probbuilder = new ProcessBuilder(new String[]{"mvn","package"});
		probbuilder.directory(file);
		Process pro = probbuilder.start();
		pro.waitFor();
		File jarFile = new File(path + "target/");
		System.out.println(jarFile.isDirectory() + " is directory " + jarFile.getName());
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		String log = "";
		String line = "";
		boolean buildBoolean = false;
		while ((line = bufferedReader.readLine()) != null){
			System.out.println(line);
			log += line + "\n";

			if(line.contains("BUILD") && line.contains("FAILURE")){

				buildBoolean = false;
			}
			if(line.contains("BUILD") && line.contains("SUCCESS")){
				buildBoolean = true;
			}
		}
        LocalDateTime time = LocalDateTime.now();
        BuildStatus build = new BuildStatus(buildBoolean,time,log);
		return build;
	}

    /**
     * Saves the build status to a file.
     *
     * The file is created using the commit ID extracted from the commit URL and a given path.
     * If the file already exists, it is overwritten.
     * The file contains the commit URL, the build time, and the build log.
     *
     * @param build the build status to be saved
     * @param commitURL the URL of the commit
     * @param path the path where the file should be saved
     */
    public void saveBuildStatus(BuildStatus build, String commitURL ,String path){
        String[] split = commitURL.split("/");
        String commitId = split[split.length - 1];
        File file = new File(path + commitId + ".log");
        //Creating a folder using mkdir() method
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
            fileWriter.write(commitURL + "\n" + build.time + "\n" + build.log);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
