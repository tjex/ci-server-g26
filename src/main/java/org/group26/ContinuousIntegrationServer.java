package org.group26;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.shared.verifier.VerificationException;
import org.eclipse.jetty.server.Server;
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
	public static final String PATH = "/Users/arian/edu/kth/softfund_dd2480/testing/";
	public void handle(String target,
			Request baseRequest,
			HttpServletRequest request,
			HttpServletResponse response)
					throws IOException, ServletException
	{
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		baseRequest.setHandled(true);
		response.addHeader("ngrok-skip-browser-warning", "anyvalue");

		response.getWriter().println("START OF LIFE");
		response.getWriter().println(request.getHeader("User-Agent"));
		System.out.println(request.getHeader("User-Agent"));

		boolean pushEvent = false;
		if(request.getHeader("User-Agent").contains("GitHub-Hookshot")){
			//response.getWriter().println("RECOGNISING THE USER IS FROM GITHUB");
			if(request.getHeader("X-GitHub-Event").equals("push")){
				pushEvent = true;
				//System.out.println("Succesfully got payload from webhook");
				response.getWriter().println("WEBHOOK WENT THROUGH ALL THE if statements");
			}
		}

		System.out.println(request.getHeaderNames().toString());

		// Get payload as JSON
		JSONObject requestJson = HelperFucntion.getJsonFromRequestReader(request.getReader());
		
		if(pushEvent) {
			response.getWriter().println("Succesfully found the webhook and about to clone");
			boolean status;
			try {
				status = cloneRepository(requestJson);
				if (status)
					System.out.println("Successfully cloned repository");
			} catch (Exception e) { e.printStackTrace(); }
		}

		System.out.println(target);


		// here you do all the continuous integration tasks
		// for example
		// 1st clone your repository
		// 2nd compile the code

		response.getWriter().println("end of function");

		response.getWriter().println("CI job done");
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
    	String[] result = new String[]{"SUCCESS",time.toString(),""}; // BUILD STATUS, TIME, LOG

        try {
            Verifier verifier = new Verifier(pathToRepo);
            verifier.addCliArgument( "install" );
            verifier.execute();
            verifier.verify(true);

        } catch (VerificationException e) {
            result[0] = "FAILED";
            result[2] = e.toString();
            //System.out.println(e.getMessage());
        }

    	return result;
    }

}
