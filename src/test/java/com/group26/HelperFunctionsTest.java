package com.group26;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
//
// import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.group26.ContinuousIntegrationServer;
import org.group26.HelperFucntion;
import org.json.JSONObject;
import org.junit.Test;

public class HelperFunctionsTest {

	/**
	 *	Test if a JSONObject is created through the function {@code getJsonFromConnection} and contains appropriate repo.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void CheckCorrectJsonReader() throws IOException, InterruptedException {
		String dummyTest = "{\"fruit\": \"Apple\",\"size\": \"Large\",\"color\": \"Red\"}";
		Process pro = Runtime.getRuntime().exec("touch /home/g26/temp.txt");
		pro.waitFor();
		File file = new File("/home/g26/temp.txt");
		FileWriter myWriter = new FileWriter(file);
		myWriter.write(dummyTest);
		myWriter.close();
		BufferedReader br = new BufferedReader(new FileReader(file));
		JSONObject json = HelperFucntion.getJsonFromRequestReader(br);
		assertEquals(json.getString("fruit"), "Apple");
		assertEquals(json.getString("size"), "Large");
		assertEquals(json.getString("color"), "Red");
		pro = Runtime.getRuntime().exec("rm /home/g26/temp.txt");
		pro.waitFor();
		file.delete();
	}

	/**
	 * 	First check if there exist a directory ci-server-g26/ we should assert false.
	 * 	Second check if there exist a directory ci-server-g26/ after using gitClone function, which should assert true.
	 * 	@throws IOException
	 * 	@throws InterruptedException
	 */


	@Test
	public void CheckIfGitCloneCreatesNewFolder() throws IOException, InterruptedException {
		String branch = "main";
		String URL = "https://github.com/tjex/ci-server-g26.git";
		File file = new File("/home/g26/Test/");
		if(file.isDirectory()){
			FileUtils.deleteDirectory(file);
		}
		assertFalse(file.isDirectory());
		HelperFucntion.gitClone(URL, branch, "/home/g26/Test/");
		assertTrue(file.isDirectory());
		if(file.isDirectory()){
			FileUtils.deleteDirectory(file);
		}
	}
}

