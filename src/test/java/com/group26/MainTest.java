package com.group26;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.io.FileUtils;
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
     */
    public MainTest(String testName) {
        super(testName);
    }

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
     * Tests if a working commit on main is built successful
     * The specific commit ae7050c32e33393f58117d78810eed725549597c has been tested manually
     */
    public void testBuildSuccess() {
        String path = ContinuousIntegrationServer.PATH + "ci-server-g26/test_build_success/";
        File dir = new File(path);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ContinuousIntegrationServer server = new ContinuousIntegrationServer();
        try {
            HelperFucntion.gitCloneWithOutputDir("git@github.com:tjex/ci-server-g26.git", "main", path);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        System.out.println("cd " + path + " && git checkout ae7050c32e33393f58117d78810eed725549597c");
        try{
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "cd " + path + " && git checkout ae7050c32e33393f58117d78810eed725549597c");
            Process proc = processBuilder.start();
            proc.waitFor();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }


        String[] result = server.build(path);
        if(result[0]!= "SUCCESS"){
            System.out.println(result[2]);
        }

        assertEquals("SUCCESS", result[0]);

        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tests if a build fails
     * By removing the file that contains the function that is used in this test
     * the project should not be able to build
     */
    public void testBuildFail() {
        String path = ContinuousIntegrationServer.PATH + "ci-server-g26/test_build_fail/";
        System.out.println("directory to be deleted " + path);
        File dir = new File(path);
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(path);
        ContinuousIntegrationServer server = new ContinuousIntegrationServer();
        try {
            HelperFucntion.gitCloneWithOutputDir("git@github.com:tjex/ci-server-g26.git", "main", path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // checking out a commit that passes all text
        try{
            Process proc = Runtime.getRuntime().exec("cd " + path + " && git checkout ae7050c32e33393f58117d78810eed725549597c");
            proc.waitFor();
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Deleting the file that contains the function that's called in this function should give build error
        File file = new File(path + "src/main/java/org/group26/HelperFucntion.java");
        System.out.println(path + "src/main/java/org/group26/HelperFucntion.java");
        Boolean is_deleted = file.delete();

        if (!is_deleted) {
            throw new RuntimeException("failed to delete file: " + file.getName());
        }

        String[] result = server.build(path);
        assertEquals("FAILED", result[0]);

        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
