package org.doble.adr;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

public class CommandInitTest {
    private static FileSystem fileSystem;
    final private String rootPath = "/project/adr";
    
    Environment env;
 


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Set up the mock file system
		try {
			fileSystem = Jimfs.newFileSystem(Configuration.unix());
			
			Path rootPath = fileSystem.getPath("/project");
			Files.createDirectory(rootPath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPath)
				.build();
				
	}

	@After
	public void tearDown() throws Exception {

		}

	@Test
	public void testInit() {
		ADR adr = new ADR();

		String[] args = {"init"};

		try {
			adr.run(args, env);
		} catch (ADRException e) {
			// TODO Auto-generated catch block
			fail("ADR Exception raised");
		}


		// Check to see if the .adr directory has been created. 
		String pathName = rootPath + "/.adr";
		try {
			Path p = fileSystem.getPath(pathName);
			boolean exists = Files.exists(p);
			assertTrue(exists);
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			fail("InvalidPathException raised on "+ pathName);
		}
		
		//Now see if the  standard docs directory has been created
		pathName = rootPath + "/doc/adr";
		try {
			Path p = fileSystem.getPath(pathName);
			boolean exists = Files.exists(p);
			assertTrue(exists);
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			fail("InvalidPathException raised on "+ pathName);
		}
		
		// Check if the ADR has been created
		pathName = rootPath + "/doc/adr";
		try {
			Path p = fileSystem.getPath(pathName);
			boolean exists = Files.exists(p);
			assertTrue(exists);
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			fail("InvalidPathException raised on "+ pathName);
		}
		
		// Check if the ADR has been created
		pathName = rootPath + "/doc/adr/0001-record-architecture-decisions.md";
		try {
			Path p = fileSystem.getPath(pathName);
			boolean exists = Files.exists(p);
			assertTrue(exists);
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			fail("InvalidPathException raised on "+ pathName);
		}
		
		// Do a sample check on the content
		pathName = rootPath + "/doc/adr/0001-record-architecture-decisions.md";
		try {
			Path p = fileSystem.getPath(pathName);
			List<String> contents = Files.readAllLines(p);
			
			// Sample the contents
			int matches = 0; 
			for (String line: contents) {
				if (line.contains("Record architecture decisions")) matches++;
				if (line.contains("## Decision")) matches++;
				if (line.contains("Nygard")) matches++;
			}
			
		
			assertTrue(matches == 4);
		} catch (InvalidPathException e) {
			// TODO Auto-generated catch block
			fail("InvalidPathException raised on "+ pathName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage() + " reading " +  pathName);
		}
	}

}
