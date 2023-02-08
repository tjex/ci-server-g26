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
     * Tests if the latest commit on main is built successful
     * All commits on main should be able to build as well as pass all tests
     */
    public void testBuildSuccess() {
        String path = ContinuousIntegrationServer.PATH + "test_build_success/";
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
        String[] result = server.build(path);
        assertEquals("SUCCESS", result[0]);
    }

    /**
     * Tests if a build fails
     * By removing the file that contains the function that is used in this test
     * the project should not be able to build
     */
    public void testBuildFail() {
        String path = ContinuousIntegrationServer.PATH + "test_build_fail";
        System.out.println("file to be deletd " + path);
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

        // Deleting the file that contains the function that's called in this function should give build error
        File file = new File(path + "/src/main/java/org/group26/ContinuousIntegrationServer.java");
        System.out.println(path + "/src/main/java/org/group26/ContinuousIntegrationServer.java");
        Boolean is_deleted = file.delete();

        if (!is_deleted) {
            throw new RuntimeException("failed to deleted file: " + file.getName());
        }

        String[] result = server.build(path);
        assertEquals("FAILED", result[0]);
    }
}
