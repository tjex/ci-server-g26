package org.group26;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

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
		
		if(pushEvent) {
			// response.getWriter().println("Succesfully found the webhook and about to clone");
			boolean status;
			try {
				status = cloneRepository(requestJson);
				if (status)
					System.out.println("Successfully cloned repository");
			} catch (Exception e) { e.printStackTrace(); }
		}

		System.out.println(target);

		String commitURL = requestJson.getJSONObject("head_commit").getString("url");
		sendResponse(response, CommitStatus.SUCCESS, commitURL);

		// here you do all the continuous integration tasks
		// for example
		// 1st clone your repository
		// 2nd compile the code

		// response.getWriter().println("end of function");

		// response.getWriter().println("CI job done");
	}
	
	/**
	 * 	Attempts to build the application.
	 *
	 * 	@param pathToRepo Project which is going to be built.
	 */
	public String[] build(String pathToRepo) {
		String[] result = new String[3];

		// TODO: Add remaining code

		result[0] = "FAILED";
		result[1] = "DATE";
		result[2] = "LOG";
		return result;
	}

	/**
	 * 	Attempts to runs all tests
	 *
	 * 	@return Array of test statuses?
	 */
	public String[] test() {
		String[] result = new String[1337]; // dummy number

		// TODO: Add remaining code

		return result;
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
		branch = refs[refs.length - 1];
		
		HelperFucntion.gitClone(cloningURL, branch);
		
		// Returns true if repository was successfully cloned
		File file = new File(ContinuousIntegrationServer.PATH + "ci-server-g26/");
		return file.isDirectory();
	}

	/**
	 * 	Sends response back to GitHub.
	 *
	 *	@param response
	 * 	@param status Commit status (error, failure, pending, success)
	 * 	@param commitUrl The pushed commit URL
	 * 	@throws IOException 
	 * 	@throws ClientProtocolException
	 * 
	 *  @see https://docs.github.com/en/rest/commits/statuses?apiVersion=2022-11-28
	 */
	public void sendResponse(HttpServletResponse response, CommitStatus status, String commitUrl) throws ClientProtocolException, IOException {
		
		System.out.println("Sending response to commit url: " + commitUrl);
		
		response.setHeader("Content-type", "application/json");
		response.setHeader("Accept", "application/vnd.github.v3+json");
		response.setStatus(HttpServletResponse.SC_OK);
		response.addHeader("ngrok-skip-browser-warning", "anyvalue");
		
		// Get commit id from URL
		String[] split = commitUrl.split("/");
		String commitId = split[split.length - 1];
		
		JSONObject body = new JSONObject();
		body.put("owner", "tjex");
		body.put("repo", "ci-server-g26");
		body.put("sha", commitId);
		body.put("state", status.toString().toLowerCase());
		
		response.getWriter().print(body.toString());
		System.out.println("Response payload:");
		System.out.println(body.toString());
	}

	// used to start the CI server in command line
	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8026);
		server.setHandler(new ContinuousIntegrationServer());
		server.start();
		server.join();
	}
}