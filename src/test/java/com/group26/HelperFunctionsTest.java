package com.group26;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;

import junit.framework.AssertionFailedError;
import org.group26.ContinuousIntegrationServer;
import org.group26.HelperFucntion;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class HelperFunctionsTest {

	/*
	 *	Test if a JSONObject is created through the function {@code getJsonFromConnection} and contains appropriate repo.
	*/
	@Test
	public void CheckCorrectJsonReader() throws IOException {
		String dummyTest = "{\"fruit\": \"Apple\",\"size\": \"Large\",\"color\": \"Red\"}";
		File file = new File("temp.txt");
		FileWriter myWriter = new FileWriter(file);
		myWriter.write(dummyTest);
		myWriter.close();
		HelperFucntion helpJsonCreate = new HelperFucntion();
		BufferedReader br = new BufferedReader(new FileReader("temp.txt"));
		JSONObject json = helpJsonCreate.getJsonFromRequestReader(br);
		assertEquals(json.getString("fruit"), "Apple1");
		assertEquals(json.getString("size"), "Large");
		assertEquals(json.getString("color"), "Red");
		file.delete();
	}

	/**
	 * First check if there exist a directory ci-server-g26/ we should assert false
	 * Second check if there exist a directory ci-server-g26/ after using gitClone function, which should assert true.
	 */
	@Test
	public void CheckIfGitCloneCreatesNewFolder() throws IOException, InterruptedException {
		HelperFucntion helpGitClone = new HelperFucntion();
		String branch = "main";
		String URL = "https://github.com/tjex/ci-server-g26.git";
		File file = new File(ContinuousIntegrationServer.PATH + "ci-server-g26/");
		assertFalse(file.isDirectory());
		helpGitClone.gitClone(URL,branch);
		assertTrue(file.isDirectory());
		file.delete();
	}
}
