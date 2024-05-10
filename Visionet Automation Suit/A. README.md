<body style="font-family:Segoe UI; font-weight:400; line-height: 2;text-align: justify;color:#353b5f;">

<div style="display: flex; align-items: center; justify-content: space-between; height: 94px;  background-color: #353b5f;">
    <span id="logo" style="color:#fff; font-family:Segoe UI; font-weight: 700; text-transform: uppercase; line-height: 1; letter-spacing: 5px; padding-left: 15px; font-size: 36px;">VISIONET</span>
</div>

</body>




# About 'com.visionetsystems:visionet-automation-suite' Framework

The Visionet Automation Suite Framework is an advanced test automation solution developed using Java, Selenium, and Behavior-Driven Development (BDD) methodologies. It leverages the Page Object Model (POM) design pattern to ensure robustness and scalability in testing web applications. This comprehensive framework supports a variety of testing types, including API testing, Appium for mobile application testing, cross-browser testing, and web application testing, making it adaptable to diverse platforms such as Mac and Windows. By incorporating BDD, it facilitates better collaboration between developers, testers, and business stakeholders, enabling the creation of test scenarios in natural language. The framework provides extensive test coverage and ensures a holistic approach to automation testing.

## Table of Contents

- [License](#license)
- [Overview](#overview)
- [Dependencies](#dependencies)
- [Java Classes Description](#java-classes-description)
- [Pom.xml](pom.xml)
- [Setup and Execution](#setup-and-execution)
  - [Option 1: Run the TomcatServerStarter](#option-1-run-the-tomcatserverstarter)
  - [Option 2: Run the tests using TestNG](#option-2-run-the-tests-using-testng)
  - [Option 3: Run Individual Test Class](#option-3-run-individual-test-class)
- [Reports](#reports)
- [Prerequisite Setup](#prerequisite-setup)
- [Automation Report Emailer](#automation-report-emailer)
- [BrowserUtilities](#browserutilities)
- [Contact](#contact)

## License

This project is licensed under the [MIT License](./License.md).

## Overview

My Framework is a powerful automation framework built using Selenium and Java. It follows the Page Object Model (POM) design pattern to provide a robust and scalable test automation solution. With My Framework, you can easily automate your web application tests and achieve efficient test coverage.

## Dependencies

Specify the project dependencies and their versions in the `pom.xml` file. Here are some of the main dependencies:

- Tomcat Embedded (version: 9.0.59)
- TestNG (version: 7.4.0)
- Selenium (version: 4.4.0)
- Logging (version: 1.2.6, 2.15.0)
- Apache POI (version: 5.1.0)
- Fillo (version: 1.21)
- JavaFaker (version: 1.0.2)
- Java Servlet API (version: 4.0.1)
- SLF4J StaticLoggerBinder (version: 2.0.0)
- Aspose PDF (version: 21.5)
- Aspose Excel (version: 21.6)
- Maven Plugins (version: 3.3.0, 6.5.0.Final)
- javax.mail (version: 1.6.2)
- JFreeChart (version: 1.5.3)
- Yaml (version: 1.29)
- Apache HttpClient (version: 4.5.13)
- Apache HttpCore (version: 4.4.14)
- FreeTTS (version: 1.2.2)
- ExtentReports (version: 2.41.2)
- UADetector (version: 0.9.22, 2014.10)
- jSerialComm (version: 2.6.2)
- JSON (version: 20210307)


## Java Classes Description

- **TestBase.java**: Description of TestBase.java.
- **TomcatServerStarter.java**: Description of TomcatServerStarter.java.
- **InsightsAndToolsPageFactory.java**: Description of InsightsAndToolsPageFactory.java.
- **InsightsAndToolsPage.java**: Description of InsightsAndToolsPage.java.
- **InsightsAndToolsTest.java**: Description of InsightsAndToolsTest.java.
- **SeleniumActionType.java**: Description of SeleniumActionType.java.
- **BrowserAutoLaunch.java**: Description of BrowserAutoLaunch.java.
- **CustomLifecycleListener.java**: Description of CustomLifecycleListener.java.
- **TestNGListener.java**: Description of TestNGListener.java.
- **InsightsAndToolsServlet.java**: Description of InsightsAndToolsServlet.java.
- **SeleniumAction.java**: Description of SeleniumAction.java.
- **GenericMethod.java**: Description of GenericMethod.java.
- **ConfigurationMethods.java**: Description of ConfigurationMethods.java.

Provide a brief description of each important Java class in your framework.

## Pom.xml

The `pom.xml` file contains the project's dependencies. Here are some of the main dependencies and their versions:

- Tomcat Embedded (version: 9.0.59)
- TestNG (version: 7.4.0)
- Selenium (version: 4.4.0)
- Logging (version: 1.2.6, 2.15.0)
- Apache POI (version: 5.1.0)
- Fillo (version: 1.21)
- JavaFaker (version: 1.0.2)
- Java Servlet API (version: 4.0.1)
- SLF4J StaticLoggerBinder (version: 2.0.0)
- Aspose PDF (version: 21.5)
- Aspose Excel (version: 21.6)
- Maven Plugins (version: 3.3.0, 6.5.0.Final)
- javax.mail (version: 1.6.2)
- JFreeChart (version: 1.5.3)
- Yaml (version: 1.29)
- Apache HttpClient (version: 4.5.13)
- Apache HttpCore (version: 4.4.14)
- FreeTTS (version: 1.2.2)
- ExtentReports (version: 2.41.2)
- UADetector (version: 0.9.22, 2014.10)
- jSerialComm (version: 2.6.2)
- JSON (version: 20210307)

Please update the versions as per your project requirements.

## Setup and Execution

To set up and execute the framework, follow these steps:

### Option 1: Run the TomcatServerStarter

1. Clone the repository or download the project from [Google Drive](https://drive.google.com/drive/folders/1JX5jXLEwpBh23gtNvHdj6nF2lZTDqxHh?usp=sharing).
2. Open Eclipse IDE.
3. Import the project as a Maven project:
   - Go to **File > Import**.
   - Select **Existing Maven Projects** and click **Next**.
   - Browse to the project directory and click **Finish**.
4. Set up the required dependencies and ensure that the browser drivers are properly configured.
5. Open the file located at `src/main/java/com/securianfinancial/etqa/base/TomcatServerStarter.java`.
6. Right-click on the file and select **Run As > Java Application**.
7. The Tomcat server will start running on the specified port.

### Option 2: Run the tests using TestNG

1. Clone the repository or download the project from [Google Drive](https://drive.google.com/drive/folders/1JX5jXLEwpBh23gtNvHdj6nF2lZTDqxHh?usp=sharing).
2. Open Eclipse IDE.
3. Import the project as a Maven project:
   - Go to **File > Import**.
   - Select **Existing Maven Projects** and click **Next**.
   - Browse to the project directory and click **Finish**.
4. Set up the required dependencies and ensure that the browser drivers are properly configured.
5. Open the `testng.xml` file located at the root of the project.
6. Right-click on the file and select **Run As > TestNG Suite**.
7. The tests will start executing based on the configurations in the `testng.xml` file.

### Option 3: Run Individual Test Class

1. Clone the repository or download the project from [Google Drive](https://drive.google.com/drive/folders/1JX5jXLEwpBh23gtNvHdj6nF2lZTDqxHh?usp=sharing).
2. Open Eclipse IDE.
3. Import the project as a Maven project:
   - Go to **File > Import**.
   - Select **Existing Maven Projects** and click **Next**.
   - Browse to the project directory and click **Finish**.
4. Set up the required dependencies and ensure that the browser drivers are properly configured.
5. Open the `InsightsAndToolsTest` class located at `src/main/java/com/securianfinancial/etqa/test/InsightsAndToolsTest.java`.
6. Right-click on the file and select **Run As > TestNG Test**.
7. The tests defined in the `InsightsAndToolsTest` class will start executing.

## Reports

The framework generates multiple reports to provide comprehensive test execution insights and results.

- **ExtentReports HTML Report**: The ExtentReports HTML report provides detailed information about the test execution. It can be found at `src\test\resources\reports`.
- **Log Files**: The log files capture the execution logs of the project and can be found at `src\test\resources\logs`.

## Prerequisite Setup

To configure the necessary settings for your application, follow these steps:

1. Open the `app.yml` file located in the project directory.
2. Update the required properties such as `environment`, `base_url`, and other relevant settings.
3. Save the changes to the `app.yml` file.

## Automation Report Emailer

The Automation Report Emailer provides a utility for sending an email with an execution report attachment. To enable this feature, set the `send_mail_report` property in the `app.yml` file to "Yes" and provide the required email settings.

## BrowserUtilities

The BrowserUtilities class provides utility methods for creating WebDriver instances for different web browsers. It supports various browser types, including Chrome, Firefox, Edge, PhantomJS, Opera, Internet Explorer, Chromium, Maxthon, Brave, and Vivaldi.

Ensure that you have the appropriate browser drivers installed and configured to use the desired browser for test execution.

## Contact

<div style="background-color: #f8f8f8; padding: 20px; margin-top: 20px;">
        <h2 style="color:#13162a; font-weight:bold;">Contact Details</h2>
        <table style="width: 100%; border-collapse: collapse; margin-top: 10px; background-color: #fff; box-shadow: 0 4px 8px rgba(0,0,0,0.1);">
            <tr style="background-color: #353b5f; color: #fff;">
                <th style="padding: 10px; text-align: left;">Role</th>
                <th style="padding: 10px; text-align: left;">Name</th>
                <th style="padding: 10px; text-align: left;">Email</th>
                <th style="padding: 10px; text-align: left;">Phone</th>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Initial Framework Designer</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Dakshina Moorthy</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><a href="mailto:dakshina.moorthy@visionet.com" style="color: #353b5f; text-decoration: none;">dakshina.moorthy@visionet.com</a></td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><a href="tel:+919789998021" style="color: #353b5f; text-decoration: none;">+91 978 9998 021</a></td>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Project Manager</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Ashish Kumar Vats</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><a href="mailto:ashishkumar.vats@visionet.com" style="color: #353b5f; text-decoration: none;">ashishkumar.vats@visionet.com</a></td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"><a href="tel:+919999600646" style="color: #353b5f; text-decoration: none;">+91 999 9600 646</a></td>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">SPOC1</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Name</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">SPOC2</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Name</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Testers</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Name</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Automation Lead Developer</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;">Name</td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
                <td style="padding: 10px; border-bottom: 1px solid #ddd;"></td>
            </tr>
        </table>
            </div>

<hr style="margin-top: 20px; margin-bottom: 20px; border: none; border-top: 1px solid #ccc;">

**Disclaimer:**

The information contained in this project is for general informational purposes only. While we strive to keep the information up to date and correct, we make no representations or warranties of any kind, express or implied, about the completeness, accuracy, reliability, suitability, or availability with respect to the project or the information contained herein. Any reliance you place on such information is therefore strictly at your own risk.

In no event will we be liable for any loss or damage arising from the use of this project.

<div style="background-color: #353b5f; color: #fff; padding: 10px; font-family: 'Segoe UI'; font-size: 0.75rem; display: flex; justify-content: space-between; align-items: center;">
    <div>
         © <script>document.write(new Date().getFullYear())</script> <a href="https://www.visionet.com/" style="color: #fff; text-decoration: none;">Visionet Systems</a>. All rights reserved.
    </div>
    <div>
        This project is licensed under the <a href="./License.md" style="color: #fff; text-decoration: none;">MIT License</a>.
    </div>
</div>


