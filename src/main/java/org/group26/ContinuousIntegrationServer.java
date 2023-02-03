package org.group26;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *	Skeleton of a ContinuousIntegrationServer which acts as webhook  
 *	See the Jetty documentation for API documentation of those classes.
 */
public class ContinuousIntegrationServer extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) 
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

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
