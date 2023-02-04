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
        HttpURLConnection conn = (HttpURLConnection) new URL("https://api.github.com/repos/tjex/ci-server-g26").openConnection();
        conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");

        JSONObject json = getAPItoJsonOb(conn);
        //prints when connecting to localhost
        System.out.println(target);
        response.getWriter().println(json);

        //response.getWriter().println(line);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

        response.getWriter().println("CI job done");
    }
    public JSONObject getAPItoJsonOb(HttpURLConnection connn) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(connn.getInputStream()));
        String inputline;
        String jsonString = "";

        while((inputline = in.readLine()) != null){
            jsonString += inputline;
        }

        JSONObject jo = new JSONObject(jsonString);
        return new JSONObject(jsonString);
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