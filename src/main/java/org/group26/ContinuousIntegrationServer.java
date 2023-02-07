package org.group26;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

/**
 *	Skeleton of a ContinuousIntegrationServer which acts as webhook
 *	See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    public JSONObject json;
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);


        //Setup the connection to the Repo url
        //HttpURLConnection conn = (HttpURLConnection) new URL("https://api.github.com/repos/tjex/ci-server-g26").openConnection();
        //HttpURLConnection conn = (HttpURLConnection) new URL("https://b314-90-143-27-220.eu.ngrok.io/").openConnection();
        response.addHeader("ngrok-skip-browser-warning", "anyvalue");
        //conn.setRequestProperty("ngrok-skip-browser-warning", "anyvalue");
        //System.out.println(request.getHeader("user-agent"));
        //conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        response.getWriter().println("START OF LIFE");
        response.getWriter().println(request.getHeader("User-Agent"));
        System.out.println(request.getHeader("User-Agent"));

        boolean pushEvent = false;
        if(request.getHeader("User-Agent").contains("GitHub-Hookshot")){
            response.getWriter().println("RECOGNISING THE USER IS FROM GITHUB");
            if(request.getHeader("X-GitHub-Event").equals("push")){
                pushEvent = true;
                System.out.println("Succesfully got payload from webhook");
                response.getWriter().println("WEBHOOK WENT THROUGH ALL THE if statements");
            }
        }
        System.out.println("testttttttttt");
        System.out.println(request.getHeaderNames());
        //System.out.println(request. );
        if(pushEvent){
            response.getWriter().println("Succesfully found the webhook and about to clone");
            //get payload from webhook
            BufferedReader bufferReader = request.getReader();
            JSONObject json = HelperFucntion.getJsonFromRequestReader(bufferReader);
            //
            String branchGitURL = HelperFucntion.getBranchAndGitURL(json);
            try {
                HelperFucntion.gitClone(branchGitURL);
                System.out.println("Succesfully cloned");
            } catch (InterruptedException e) {
                response.getWriter().println("ERROR coulden't clone");
                throw new RuntimeException(e);
            }
        }
        //System.out.println(jsonString);
        //prints when connecting to localhost
        System.out.println(target);
        //System.out.println(json);
        //response.getWriter().println(json);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code
        /*
        //String branchGitURL = HelperFucntion.getBranchAndGitURL(json);
        try {
            HelperFucntion.gitClone(branchGitURL);
            System.out.println("kebab");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */
        response.getWriter().println("end of function");

        response.getWriter().println("CI job done");
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
     * 	Clones the repository into folder: ciserver-repo
     *
     * 	@param repoURL HTTP URL to the GitHub repository.
     */
    public boolean cloneRepository(String repoURL) {
        String folderName = "ciserver-repo";

        // TODO: Add remaining code

        return false;
    }

    /**
     * 	Sends response back to Github.
     *
     * 	@param message The message
     * 	@param url URL to where (PR, commit, issue, whatever?)
     */
    public void sendResponse(String message, String url) {
        // TODO: Add remaining code
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