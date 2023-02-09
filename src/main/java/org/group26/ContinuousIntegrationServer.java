package org.group26;

import java.io.*;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.verifier.VerificationException;
import org.eclipse.jetty.server.Server;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

import org.apache.maven.shared.verifier.util.ResourceExtractor;
import org.apache.maven.shared.verifier.Verifier;


/**
 *	Skeleton of a ContinuousIntegrationServer which acts as webhook
 *	See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
	public static final String PATH = "/home/g26/repo/";
	
	private enum CommitStatus {
		ERROR,
		FAILURE,
		PENDING,
		SUCCESS
	}
	
	public void handle(String target,
			Request baseRequest,
			HttpServletRequest request,
			HttpServletResponse response)
					throws IOException, ServletException
	{
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
		if(pushEvent) {
			// response.getWriter().println("Succesfully found the webhook and about to clone");
			boolean status;
			try {
				status = cloneRepository(requestJson);
				if (status)
					System.out.println("Successfully cloned repository");
					System.out.println("Starting build of cloned repo");
					buildEval = buildRepo();
			} catch (Exception e) { e.printStackTrace(); }
		}
		String commitURL = requestJson.getJSONObject("head_commit").getString("url");

		if(buildEval){
            System.out.println("successful build eval - true");
			sendResponse(CommitStatus.SUCCESS, commitURL);
		}
		else{
            System.out.println("successful build eval - false");
			sendResponse(CommitStatus.FAILURE, commitURL);
		}


		//System.out.println(target);

		//String commitURL = requestJson.getJSONObject("head_commit").getString("url");
		//sendResponse(CommitStatus.SUCCESS, commitURL);


		// here you do all the continuous integration tasks
		// for example
		// 1st clone your repository
		// 2nd compile the code

		// response.getWriter().println("end of function");

		 System.out.println("CI job done");
	}

	/**
	 * 	Clones the repository into folder: {@code ContinuousIntegrationServer.PATH}/ci-server-g26/
	 *
	 * 	@param payload JSON payload from GitHub web hook
	 * 	@throws IOException If an I/O error occurs 
	 * 	@throws InterruptedException 
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
			if(counter < 2){
				continue;
			}
			else{
				branch += bra;
			}
			counter++;
		}
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
	 * 	@throws IOException 
	 * 	@throws ClientProtocolException
	 * 
	 *  @see https://docs.github.com/en/rest/commits/statuses?apiVersion=2022-11-28
	 */
	public void sendResponse(CommitStatus status, String commitUrl) throws IOException {
		
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
		StringEntity params = new StringEntity(body.toString());
		response.setEntity(params);

		System.out.println("Response payload:");
		System.out.println(body.toString());
		
		// Send POST to GitHub	
		client.execute(response);
	}

	// used to start the CI server in command line
	public static void main(String[] args) throws Exception
	{
        Server server = new Server(8026);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
    
    /**
     * 	Attempts to build the application.
     * 
     * 	@param pathToRepo Project which is going to be built.
     */
    public String[] build(String pathToRepo) {
        LocalDateTime time = LocalDateTime.now();
        System.out.println(time.toString());
    	String[] result = new String[]{"NONE",time.toString(),""}; // BUILD STATUS, TIME, LOG

        try {
            Verifier verifier = new Verifier(pathToRepo);
            verifier.addCliArgument( "install" );
            verifier.execute();
            verifier.verify(true);
            result[0] = "SUCCESS";

        } catch (VerificationException e) {
            result[0] = "FAILED";
            result[2] = e.toString();
            //System.out.println(e.getMessage());
        }

    	return result;
    }

	public boolean buildRepo() throws IOException, InterruptedException {
		File file = new File(PATH);

		System.out.println(file.isDirectory() + " is directory " + file.getName());

		ProcessBuilder probbuilder = new ProcessBuilder(new String[]{"mvn","package"});
		probbuilder.directory(file);
		Process pro = probbuilder.start();
		pro.waitFor();
		File jarFile = new File(PATH + "target/");
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
		return buildBoolean;
	}
}
