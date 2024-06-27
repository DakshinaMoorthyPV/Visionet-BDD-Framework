package com.visionetsystems.framework.runner;

import org.testng.annotations.Listeners;

import com.visionetsystems.framework.listeners.TestListener;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@Listeners({ TestListener.class })
@CucumberOptions(features = "src/test/resources/features/api/send_request.feature", glue = "com.visionetsystems.framework.stepdefinitions", plugin = {
		"pretty", "html:target/cucumber-reports.html", "json:target/cucumber.json" }, monochrome = true)
public class RunnerTest extends AbstractTestNGCucumberTests {

}
