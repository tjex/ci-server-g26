# ci-server-g26

This repo is the codebase for a continuous integration server.   
The server acts as an automated build confirmation tool that communicates 
with Github on the success / faliure of commited code. The process is as follows:

1. code is pushed to Github from a local repo
2. Github registers the push and sends a json package to the server
3. the server clones the repo, 
4. builds the resultant code, 
5. formats a response destined for Github and
6. sends the response (headers and json) back to Github

The response back to Github contains the status of the build: either 
`error`, `faliure`, `pending` or `success`. This response is displayed 
alongside the commit in Github giving a visual indicator. 

Through this process developers can work together with greater fluidity due 
to a shared, automated and established build and test environment.  
This means greater transparency in the reliability of the proposed code and 
and opportunity to continuously integrate code smoothly into production.

## The CI Server

The server itself is a Raspbery PI running ngrok and a simple Java HTTP server.   
For setting up ngrok refer to their [get started guide](https://ngrok.com/docs/getting-started).   

The server process running on the Raspberry PI (that handles receiving / sending http requests) 
is below: 

```java
// ../src/main/java/org/group26/ContinuousIntegrationServer.java

	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8026);
		server.setHandler(new ContinuousIntegrationServer());
		server.start();
		server.join();
	}

```

## Functions Breakdown

As a first step, the server registers whether a received post request contains the 
'push' directive from Github. After this, the program clones the repository to `/home/g26/repo`:


```java
// ../src/main/java/org/group26/ContinuousIntegrationServer.java

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

```

The cloned repository is then built and tested: 

```java 
// ../src/main/java/org/group26/ContinuousIntegrationServer.java

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
```

## Statement of Contributions

- Tillman Jex
    - raspberry pi server setup
    - webhook setup
    - server response debugging
    - documentation
- Michael Morales Sundstedt
    - Communication handling from webhook to JSONObject.
    - Git cloning the branch of which the webhook was pushed from.
    - Test for both of the above.
