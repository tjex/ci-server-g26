package com.group26;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.io.FileUtils;
import org.group26.BuildStatus;
import org.group26.ContinuousIntegrationServer;
import org.group26.HelperFucntion;

import java.io.File;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class MainTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case

    // public MainTest(String testName) {
    //     super(testName);
    // }


    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(MainTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    /**
     * Tests the functionality of the {@link ContinuousIntegrationServer#saveBuildStatus(BuildStatus, String, String)} method.
     * Creates a {@link ContinuousIntegrationServer} object and a {@link BuildStatus} object.
     * The test checks if a file with the log of the build does not exist before the {@link ContinuousIntegrationServer#saveBuildStatus(BuildStatus, String, String)} method is called,
     * and if it exists after the method is called. Finally, the file is deleted.
     */
    public void testSaveBuildState(){
        ContinuousIntegrationServer server = new ContinuousIntegrationServer();
        BuildStatus buildStatus = new BuildStatus();
        buildStatus.log = "hej";
        String commitURL = "https://github.com/tjex/ci-server-g26/commit/hej";
        File file = new File("/home/g26/test_builds/hej.log");
        assertEquals(false,file.isFile());
        server.saveBuildStatus(buildStatus,commitURL,"/home/g26/test_builds/");
        assert(file.length()>0);
        assertEquals(true,file.isFile());
        file.delete();
    }

    /**
     * Tests if a working commit on main is first built successfully
     * Thereafter a file java class is deleted which should give build errors
     * The last step is checking out a commit that has failing tests which should also give build errors
     *
     * These tests are done in the same function as cloning takes long time on the raspberry pi
     * The specific commit a6c479e2db14d78bf5270701c57697b544c74bba has been tested manually
    **/
    public void testBuildSuccessAndFail() throws IOException, InterruptedException {
        String path = "/home/g26/testing/";
        File dir = new File(path);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ContinuousIntegrationServer server = new ContinuousIntegrationServer();
        try {
            HelperFucntion.gitClone("https://github.com/tjex/ci-server-g26.git", "main", path);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try{
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "cd " + path + " && git checkout a6c479e2db14d78bf5270701c57697b544c74bba");
            Process proc = processBuilder.start();
            proc.waitFor();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        BuildStatus buildStatus = server.buildRepo(path);
        assertEquals(true, buildStatus.success);

        File file = new File(path + "src/main/java/org/group26/HelperFucntion.java");
        System.out.println(path + "src/main/java/org/group26/HelperFucntion.java");
        Boolean is_deleted = file.delete();

        if (!is_deleted) {
            throw new RuntimeException("failed to delete file: " + file.getName());
        }

        buildStatus = server.buildRepo(path);
        assertEquals(false, buildStatus.success);

        // 28ba41a9f0cb4fcf50eebc8d9ded11683c24dc4e is a commit that has failing tests
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "cd " + path + " && git checkout 28ba41a9f0cb4fcf50eebc8d9ded11683c24dc4e");
            Process proc = processBuilder.start();
            proc.waitFor();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // the build should fail since the test fails
        buildStatus = server.buildRepo(path);
        assertEquals(false, buildStatus.success);

    }
}
