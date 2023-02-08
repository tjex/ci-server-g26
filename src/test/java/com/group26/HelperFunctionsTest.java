package com.group26;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.group26.ContinuousIntegrationServer;
import org.group26.HelperFucntion;
import org.json.JSONObject;
import org.junit.Test;

public class HelperFunctionsTest {

	/**
	 *	Test if a JSONObject is created through the function {@code getJsonFromConnection} and contains appropriate repo.
	 */
	@Test
	public void CheckCorrectJsonReader() throws IOException {
		String dummyTest = "{\"fruit\": \"Apple\",\"size\": \"Large\",\"color\": \"Red\"}";
		File file = new File("temp.txt");
		FileWriter myWriter = new FileWriter(file);
		myWriter.write(dummyTest);
		myWriter.close();
		BufferedReader br = new BufferedReader(new FileReader("temp.txt"));
		JSONObject json = HelperFucntion.getJsonFromRequestReader(br);
		assertEquals(json.getString("fruit"), "Apple");
		assertEquals(json.getString("size"), "Large");
		assertEquals(json.getString("color"), "Red");
		file.delete();
	}

	/**
	 * 	First check if there exist a directory ci-server-g26/ we should assert false.
	 * 	Second check if there exist a directory ci-server-g26/ after using gitClone function, which should assert true.
	 */
	@Test
	public void CheckIfGitCloneCreatesNewFolder() throws IOException, InterruptedException {
		String branch = "main";
		String URL = "https://github.com/tjex/ci-server-g26.git";
		File file = new File(ContinuousIntegrationServer.PATH + "ci-server-g26/");
		assertFalse(file.isDirectory());
		HelperFucntion.gitClone(URL, branch);
		assertTrue(file.isDirectory());
		file.delete();
	}

    /**
     * 	First check if there exist a directory ci-server-g26/ we should assert false.
     * 	Second check if there exist a directory ci-server-g26/ after using gitClone function, which should assert true.
     */
    @Test
    public void CheckIfGitCloneWithOutputDirCreatesNewFolder() throws IOException, InterruptedException {
        String branch = "main";
        String URL = "https://github.com/tjex/ci-server-g26.git";
        File file = new File(ContinuousIntegrationServer.PATH + "ci-server-g26/");
        assertFalse(file.isDirectory());
        HelperFucntion.gitCloneWithOutputDir(URL, branch,ContinuousIntegrationServer.PATH);
        assertTrue(file.isDirectory());
        file.delete();
    }
}