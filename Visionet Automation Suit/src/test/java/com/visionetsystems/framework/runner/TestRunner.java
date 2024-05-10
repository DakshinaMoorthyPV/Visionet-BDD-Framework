package com.visionetsystems.framework.runner;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlGroups;
import org.testng.xml.XmlRun;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.visionetsystems.framework.utils.AppConfigReader;
import com.visionetsystems.framework.utils.UIConstantsUtil;

public class TestRunner {

	public static void main(String[] args) throws Exception {
		try {
			AppConfigReader.xmlDataReader();
			TestNG testng = new TestNG();

			// Create a TestNG Suite
			XmlSuite suite = new XmlSuite();
			suite.setName("Automation" + UIConstantsUtil.APP_CONFIG_MAP.get("TestEnvironment") + " Suite");

			// Configure parallel execution if applicable
			if ("Yes".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("Parallel"))
					&& "Web".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("TestType"))) {
				suite.setParallel(XmlSuite.ParallelMode.CLASSES);
				suite.setThreadCount(Integer.parseInt(UIConstantsUtil.APP_CONFIG_MAP.get("ThreadCount")));
			}

			// Add a custom listener
			suite.addListener("com.visionetsystems.framework.listeners.TestListener");

			// Create a TestNG Test
			XmlTest test = new XmlTest(suite);
			test.setName("Automation" + UIConstantsUtil.APP_CONFIG_MAP.get("TestEnvironment") + " Test");

			// Define groups for selective test execution
			if ("Yes".equalsIgnoreCase(UIConstantsUtil.APP_CONFIG_MAP.get("Grouping"))) {
				XmlGroups groups = new XmlGroups();
				XmlRun xmlRun = new XmlRun();
				xmlRun.onInclude(UIConstantsUtil.APP_CONFIG_MAP.get("GroupingOnInclude"));
				xmlRun.onExclude(UIConstantsUtil.APP_CONFIG_MAP.get("GroupingOnExclude"));
				groups.setRun(xmlRun);
				test.setGroups(groups);
			}

			// Add test classes
			List<XmlClass> classes = Arrays.asList(new XmlClass("com.visionetsystems.framework.runner.RunnerTest"));
			test.setXmlClasses(classes);

			// Set the suite in the TestNG object
			testng.setXmlSuites(Arrays.asList(suite));

			// File handling for TestNG XML
			File file = new File("testng.xml");
			if (file.exists()) {
				file.delete();
				System.out.println("Existing file deleted successfully.");
			}

			// Write the TestNG suite configuration to an XML file
			try (FileWriter writer = new FileWriter(file)) {
				writer.write(suite.toXml());
				System.out.println("Written to XML file successfully.");
			}

			// Execute the TestNG suite
			testng.run();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
