<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BDD with Cucumber and Selenium</title>
    <style>
        .keyword { color: #7F0055; font-weight: bold; } /* purple */
        .string { color: #2A00FF; } /* blue */
        .annotation { color: #646464; } /* dark gray */
        .method { color: #7F0055; font-weight: bold; } /* purple */
        .comment { color: #3F7F5F; } /* green */
        .variable { color: #000000; } /* black */
        .background { background-color: #EEEEEE; padding: 10px; font-family: monospace; white-space: pre; }
    </style>
</head>
<body style="font-family:Segoe UI; font-weight:400; line-height:2; text-align:justify; color:#353b5f;">
   <div style="display: flex; align-items: center; justify-content: space-between; height: 47px; background-color: #353b5f;padding: 10px;">
      <ul style="list-style: none; display: flex; align-items: center; background-color: transparent;">
         <li style="margin-right: 10px;">
            <a href="https://www.facebook.com/VisionetSystemsInc" style="color: white; text-decoration: none; display: flex; align-items: center;">
               <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M14,6h3a1,1,0,0,0,1-1V3a1,1,0,0,0-1-1H14A5,5,0,0,0,9,7v3H7a1,1,0,0,0-1,1v2a1,1,0,0,0,1,1H9v7a1,1,0,0,0,1,1h2a1,1,0,0,0,1-1V14h2.22a1,1,0,0,0,1-.76l.5-2a1,1,0,0,0-1-1.24H13V7A1,1,0,0,1,14,6Z" style="fill: rgb(255, 255, 255);"></path>
               </svg>
            </a>
         </li>
         <li style="margin-right: 10px;">
            <a href="https://www.instagram.com/visionetsystems/" style="color: white; text-decoration: none; display: flex; align-items: center;">
               <svg fill="#fff" width="14" height="14" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" data-name="Layer 1">
                  <path d="M17.34,5.46h0a1.2,1.2,0,1,0,1.2,1.2A1.2,1.2,0,0,0,17.34,5.46Zm4.6,2.42a7.59,7.59,0,0,0-.46-2.43,4.94,4.94,0,0,0-1.16-1.77,4.7,4.7,0,0,0-1.77-1.15,7.3,7.3,0,0,0-2.43-.47C15.06,2,14.72,2,12,2s-3.06,0-4.12.06a7.3,7.3,0,0,0-2.43.47A4.78,4.78,0,0,0,3.68,3.68,4.7,4.7,0,0,0,2.53,5.45a7.3,7.3,0,0,0-.47,2.43C2,8.94,2,9.28,2,12s0,3.06.06,4.12a7.3,7.3,0,0,0,.47,2.43,4.7,4.7,0,0,0,1.15,1.77,4.78,4.78,0,0,0,1.77,1.15,7.3,7.3,0,0,0,2.43.47C8.94,22,9.28,22,12,22s3.06,0,4.12-.06a7.3,7.3,0,0,0,2.43-.47,4.7,4.7,0,0,0,1.77-1.15,4.85,4.85,0,0,0,1.16-1.77,7.59,7.59,0,0,0,.46-2.43c0-1.06.06-1.4.06-4.12S22,8.94,21.94,7.88ZM20.14,16a5.61,5.61,0,0,1-.34,1.86,3.06,3.06,0,0,1-.75,1.15,3.19,3.19,0,0,1-1.15.75,5.61,5.61,0,0,1-1.86.34c-1,.05-1.37.06-4,.06s-3,0-4-.06A5.73,5.73,0,0,1,6.1,19.8,3.27,3.27,0,0,1,5,19.05a3,3,0,0,1-.74-1.15A5.54,5.54,0,0,1,3.86,16c0-1-.06-1.37-.06-4s0-3,.06-4A5.54,5.54,0,0,1,4.21,6.1,3,3,0,0,1,5,5,3.14,3.14,0,0,1,6.1,4.2,5.73,5.73,0,0,1,8,3.86c1,0,1.37-.06,4-.06s3,0,4,.06a5.61,5.61,0,0,1,1.86.34A3.06,3.06,0,0,1,19.05,5,3.06,3.06,0,0,1,19.8,6.1,5.61,5.61,0,0,1,20.14,8c.05,1,.06,1.37.06,4S20.19,15,20.14,16ZM12,6.87A5.13,5.13,0,1,0,17.14,12,5.12,5.12,0,0,0,12,6.87Zm0,8.46A3.33,3.33,0,1,1,15.33,12,3.33,3.33,0,0,1,12,15.33Z"/>
               </svg>
            </a>
         </li>
         <li style="margin-right: 10px;">
            <a href="https://twitter.com/visionet" style="color: white; text-decoration: none; display: flex; align-items: center;">
               <svg fill="#fff" width="14" height="14" viewBox="0 0 24 24" aria-hidden="true" class="r-4qtqp9 r-yyyyoo r-dnmrzs r-bnwqim r-1plcrui r-lrvibr r-18jsvk2 r-rxcuwo r-1777fci r-m327ed r-494qqr">
                  <g>
                     <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z"></path>
                  </g>
               </svg>
            </a>
         </li>
         <li style="margin-right: 10px;">
            <a href="https://www.youtube.com/@Visionetsystems-official" style="color: white; text-decoration: none; display: flex; align-items: center;">
               <svg fill="#fff" width="14" height="14" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" data-name="Layer 1">
                  <path d="M23,9.71a8.5,8.5,0,0,0-.91-4.13,2.92,2.92,0,0,0-1.72-1A78.36,78.36,0,0,0,12,4.27a78.45,78.45,0,0,0-8.34.3,2.87,2.87,0,0,0-1.46.74c-.9.83-1,2.25-1.1,3.45a48.29,48.29,0,0,0,0,6.48,9.55,9.55,0,0,0,.3,2,3.14,3.14,0,0,0,.71,1.36,2.86,2.86,0,0,0,1.49.78,45.18,45.18,0,0,0,6.5.33c3.5.05,6.57,0,10.2-.28a2.88,2.88,0,0,0,1.53-.78,2.49,2.49,0,0,0,.61-1,10.58,10.58,0,0,0,.52-3.4C23,13.69,23,10.31,23,9.71ZM9.74,14.85V8.66l5.92,3.11C14,12.69,11.81,13.73,9.74,14.85Z"/>
               </svg>
            </a>
         </li>
         <li>
            <a href="https://www.linkedin.com/company/visionet-systems-inc-" style="color: white; text-decoration: none; display: flex; align-items: center;">
               <!-- License: PD. Made by stephenhutchings: https://github.com/stephenhutchings/typicons.font -->
               <svg fill="#fff" version="1.1" id="Capa_1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"
                  width="14" height="14" viewBox="0 0 552.77 552.77" style="enable-background:new 0 0 552.77 552.77;"
                  xml:space="preserve">
                  <g>
                     <g>
                        <path d="M17.95,528.854h71.861c9.914,0,17.95-8.037,17.95-17.951V196.8c0-9.915-8.036-17.95-17.95-17.95H17.95
                           C8.035,178.85,0,186.885,0,196.8v314.103C0,520.816,8.035,528.854,17.95,528.854z"/>
                        <path d="M17.95,123.629h71.861c9.914,0,17.95-8.036,17.95-17.95V41.866c0-9.914-8.036-17.95-17.95-17.95H17.95
                           C8.035,23.916,0,31.952,0,41.866v63.813C0,115.593,8.035,123.629,17.95,123.629z"/>
                        <path d="M525.732,215.282c-10.098-13.292-24.988-24.223-44.676-32.791c-19.688-8.562-41.42-12.846-65.197-12.846
                           c-48.268,0-89.168,18.421-122.699,55.27c-6.672,7.332-11.523,5.729-11.523-4.186V196.8c0-9.915-8.037-17.95-17.951-17.95h-64.192
                           c-9.915,0-17.95,8.035-17.95,17.95v314.103c0,9.914,8.036,17.951,17.95,17.951h71.861c9.915,0,17.95-8.037,17.95-17.951V401.666
                           c0-45.508,2.748-76.701,8.244-93.574c5.494-16.873,15.66-30.422,30.488-40.649c14.83-10.227,31.574-15.343,50.24-15.343
                           c14.572,0,27.037,3.58,37.393,10.741c10.355,7.16,17.834,17.19,22.436,30.104c4.604,12.912,6.904,41.354,6.904,85.33v132.627
                           c0,9.914,8.035,17.951,17.949,17.951h71.861c9.914,0,17.949-8.037,17.949-17.951V333.02c0-31.445-1.982-55.607-5.941-72.48
                           S535.836,228.581,525.732,215.282z"/>
                     </g>
                  </g>
               </svg>
            </a>
         </li>
      </ul>
      <ul style="list-style: none; display: flex; padding: 10px; margin: 0;">
         <li style="margin-right: 10px;"><a href="https://www.visionet.com/about-us" style="color: white; text-decoration: none;">About us</a></li>
         <li style="margin-right: 10px;"><a href="https://www.visionet.com/careers" style="color: white; text-decoration: none;">Careers</a></li>
         <li style="margin-right: 10px;"><a href="https://www.visionet.com/news" style="color: white; text-decoration: none;">News</a></li>
         <li><a href="https://www.visionet.com/case-study" style="color: white; text-decoration: none;">Case Studies</a></li>
      </ul>
   </div>
 <div style="display: flex; align-items: center; justify-content: space-between; height: 94px; background-color: #fff; box-shadow: 0 0 16px rgba(0,0,0,.15);">
      <span id="logo" style="color:#353b5f; font-family:'SultanNahiaW20'; font-weight: 400; text-transform: uppercase; line-height: 1; letter-spacing: 5px; padding-left: 42px; font-size: 24px; width 134px; height 18px;">VISIONET</span>
   </div>
      <div style="text-align: left; margin: 1px; background-color:#fff; padding:30px;box-shadow: 4px 4px 36px rgba(0,0,0,.15);font-size: 1rem;font-weight: 400;">
      <body>
    <h1>Write Tests Guide: BDD with Cucumber and Selenium</h1>
    <img src="https://img.shields.io/badge/Writing-Tests-006daf?logo=data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4KPCEtLSBMaWNlbnNlOiBNSVQuIE1hZGUgYnkgdnNjb2RlLWljb25zOiBodHRwczovL2dpdGh1Yi5jb20vdnNjb2RlLWljb25zL3ZzY29kZS1pY29ucyAtLT4KPHN2ZyB3aWR0aD0iODAwcHgiIGhlaWdodD0iODAwcHgiIHZpZXdCb3g9IjAgMCAzMiAzMiIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48dGl0bGU+ZmlsZV90eXBlX2N1Y3VtYmVyPC90aXRsZT48cGF0aCBkPSJNMTYuMTI5LDJhMTIuMzQ4LDEyLjM0OCwwLDAsMC0yLjM1LDI0LjQ2NVYzMGM3LjM3MS0xLjExNCwxMy45LTYuOTgyLDE0LjM4NC0xNC42ODRhMTIuOCwxMi44LDAsMCwwLTUuOS0xMS42NjdjLS4yMjMtLjEzMi0uNDQ5LS4yNjItLjY4Mi0uMzc3cy0uNDgxLS4yMzEtLjcyOS0uMzNjLS4wNzktLjAzMy0uMTU2LS4wNjMtLjIzNS0uMDk0LS4yMTYtLjA4LS40MzUtLjE3LS42NTgtLjIzNkExMi4xODgsMTIuMTg4LDAsMCwwLDE2LjEyOSwyWiIgc3R5bGU9ImZpbGw6IzAwYTgxOCIvPjxwYXRoIGQ9Ik0xOC42OCw2LjU2M2ExLjM0NSwxLjM0NSwwLDAsMC0xLjE3OC40NzIsNS40OTMsNS40OTMsMCwwLDAtLjUxOC45LDIuOSwyLjksMCwwLDAsLjM3NywzLjAyM0EzLjMxNywzLjMxNywwLDAsMCwxOS43NjMsOSwyLjM4OCwyLjM4OCwwLDAsMCwyMCw4LDEuNDExLDEuNDExLDAsMCwwLDE4LjY4LDYuNTYzWm0tNS40ODguMDcxQTEuNDQxLDEuNDQxLDAsMCwwLDExLjg1LDgsMi4zODgsMi4zODgsMCwwLDAsMTIuMDg1LDlhMy40MjcsMy40MjcsMCwwLDAsMi40NzMsMS45NiwzLjE0MSwzLjE0MSwwLDAsMC0uMjEyLTMuODUsMS4zMjIsMS4zMjIsMCwwLDAtMS4xNTQtLjQ3MlptLTMuNywzLjYzN2ExLjMsMS4zLDAsMCwwLS43MywyLjMzOCw1LjY2Myw1LjY2MywwLDAsMCwuODk1LjU0MywzLjM4NiwzLjM4NiwwLDAsMCwzLjE3OS0uMzA3LDMuNDkyLDMuNDkyLDAsMCwwLTIuMDQ5LTIuMzM4LDIuNjksMi42OSwwLDAsMC0xLjA2LS4yMzYsMS4zNjksMS4zNjksMCwwLDAtLjIzNiwwWm0xMS42MTEsNC41ODJhMy40NCwzLjQ0LDAsMCwwLTEuOTU1LjU2N0EzLjQ5MiwzLjQ5MiwwLDAsMCwyMS4yLDE3Ljc1OGEyLjY5LDIuNjksMCwwLDAsMS4wNi4yMzYsMS4zMjksMS4zMjksMCwwLDAsLjk2Ni0yLjM2Miw1LjQ3LDUuNDcsMCwwLDAtLjg5NS0uNTIsMy4yNDcsMy4yNDcsMCwwLDAtMS4yMjUtLjI2Wm0tMTAuMjkyLjA3MWEzLjI0NywzLjI0NywwLDAsMC0xLjIyNS4yNiwyLjU3NSwyLjU3NSwwLDAsMC0uODk1LjU0M0ExLjM0LDEuMzQsMCwwLDAsOS43MywxOC4wNjVhMi40MjYsMi40MjYsMCwwLDAsMS4wNi0uMjM2LDMuMTg1LDMuMTg1LDAsMCwwLDEuOTU1LTIuMzM4LDMuMzY2LDMuMzY2LDAsMCwwLTEuOTMxLS41NjdabTMuODE1LDIuMzE0YTMuMzE3LDMuMzE3LDAsMCwwLTIuNCwxLjk2LDIuMjg2LDIuMjg2LDAsMCwwLS4yMzYuOTY4LDEuNCwxLjQsMCwwLDAsMi40MjYuOTkyLDUuNDkyLDUuNDkyLDAsMCwwLC41MTgtLjksMy4xMDksMy4xMDksMCwwLDAtLjMwNi0zLjAyM1ptMi44LjA3MWEzLjE0MSwzLjE0MSwwLDAsMCwuMjEyLDMuODUsMS40NywxLjQ3LDAsMCwwLDIuNS0uOSwyLjM4OCwyLjM4OCwwLDAsMC0uMjM2LS45OTIsMy40MjcsMy40MjcsMCwwLDAtMi40NzMtMS45NloiIHN0eWxlPSJmaWxsOiNmZmYiLz48L3N2Zz4="alt="SQL Badge">
    <p>This guide provides an overview of writing effective Behavior-Driven Development (BDD) tests using Cucumber and Selenium. Follow these instructions to structure feature files, write step definitions, and implement page objects efficiently.</p>

   <h2>Introduction to BDD, Cucumber, and Selenium</h2>
    <p>Behavior-Driven Development (BDD) is a software development approach that enhances communication among project stakeholders. With BDD, tests are written in a human-readable format that describes the behavior of the application under test. Cucumber
        is a popular tool for running automated tests written in a BDD style.</p>
    <p>Selenium is an automation tool for web applications. It can interact with web browsers at the OS level, supporting actions like clicking and typing.</p>

   <h2>Prerequisites</h2>
    <ul>
        <li>Basic knowledge of Java or any other programming language supported by Selenium</li>
        <li>Installation of Java, Cucumber, Selenium WebDriver, and any IDE (like IntelliJ IDEA or Eclipse)</li>
    </ul>

   <h2>Project Structure</h2>
    <p>Create the following directories in your Maven project to organize your tests:</p>
    <ul>
        <li><strong>src/test/java</strong> - For Java classes</li>
        <li><strong>src/test/resources</strong> - For resources like feature files</li>
    </ul>
    <ul>
        <li style="color: green;"><strong>Feature Files</strong>: Place your feature files in <span style="font-family: monospace;">src/test/resources/features</span></li>
        <li style="color: blue;"><strong>Step Definitions</strong>: Place your step definition files in <span style="font-family: monospace;">src/test/java/stepdefinitions</span></li>
        <li style="color: purple;"><strong>Page Objects</strong>: Place your page object classes in <span style="font-family: monospace;">src/test/java/pageobjects</span></li>
    </ul>
    <h2>Project Structure</h2>
    <pre style="font-family: monospace; white-space: pre;">
│
├── <span style="color: orange;">src/</span><br>
│   ├── <span style="color: orange;">main/</span><br>
│   │   └── <span style="color: orange;">java/</span><br>
│   │       └── <span style="color: orange;">com/</span><br>
│   │           └── <span style="color: orange;">visionetsystems/</span><br>
│   │               ├── <span style="color: green;">utils/</span>                 <span style="color: gray;"># Utility classes like database connectors, API clients</span><br>
│   │               └── <span style="color: green;">pages/</span>                 <span style="color: gray;"># Page classes for each page in the web application</span><br>
│   │<br>
│   └── <span style="color: orange;">test/</span><br>
│       ├── <span style="color: orange;">java/</span><br>
│       │   └── <span style="color: orange;">com/</span><br>
│       │       └── <span style="color: orange;">visionetsystems/</span><br>
│       │           ├── <span style="color: blue;">stepDefinitions/</span>       <span style="color: gray;"># Cucumber step definitions</span><br>
│       │           ├── <span style="color: blue;">runners/</span>               <span style="color: gray;"># Cucumber test runners</span><br>
│       │           └── <span style="color: blue;">config/</span>                <span style="color: gray;"># Configuration management (e.g., BaseTest)</span><br>
│       │<br>
│       └── <span style="color: orange;">resources/</span><br>
│           ├── <span style="color: blue;">features/</span>                     <span style="color: gray;"># Cucumber feature files</span><br>
│           └── <span style="color: blue;">config/</span>                       <span style="color: gray;"># Configuration files (e.g., properties or xml files)</span><br>
│<br>
├── <span style="color: purple;">.gitignore</span>                 <span style="color: gray;"># Specifies intentionally untracked files to ignore</span><br>
├── <span style="color: purple;">pom.xml</span>                   <span style="color: gray;"># Maven configuration file with dependencies, plugins</span><br>
└── <span style="color: purple;">README.md</span>                 <span style="color: gray;"># Project description and instructions</span></pre>

   <p>This guide provides a basic framework for getting started with BDD using Cucumber and Selenium. Expand upon these foundations according to your project's requirements.</p>
    <h2>Writing Feature Files</h2>
    <p>Feature files are the cornerstone of BDD. They describe the expected behavior of the application. For better clarity and engagement in scenarios, use direct pronouns like "I should be". This style encourages clear ownership and directness in feature
        definitions.</p>
    <h2>Implementing Page Objects</h2>
    <p>The Page Object Model is a design pattern that creates an object repository for storing web elements. It helps in making tests easier to maintain and read.</p>
    <ul>
        <li><strong>Example Feature: </strong> <code> Login.feature</code>
             <pre style="background-color: #EEEEEE; padding: 10px;"><br>
<span class="keyword">Feature:</span> Login Functionality<br>
<span class="keyword">Scenario:</span> Successful login with valid credentials<br>
    <span class="keyword">Given</span> I am on the login page<br>
    <span class="keyword">When</span> I enter valid credentials<br>
    <span class="keyword">Then</span> I should be redirected to the dashboard<br>
    </pre>
</li>
        <li><strong>Example Step Definitions Page:</strong> <code> LoginDefinations.Java</code>
    <pre class="background">
<span class="keyword">public class</span> LoginSteps {<br>
    <span class="keyword">private</span> WebDriver driver = <span class="keyword">new</span> ChromeDriver();<br>
    <span class="keyword">private</span> LoginPage loginPage = <span class="keyword">new</span> LoginPage(driver);<br>
   <span class="annotation">@Given</span>(<span class="string">"I am on the login page"</span>)<br>
    <span class="keyword">public void</span> i_am_on_the_login_page() {<br>
        driver.<span class="method">get</span>(<span class="string">"http://example.com/login"</span>);<br>
    }<br>

   <span class="annotation">@When</span>(<span class="string">"I enter valid credentials"</span>)<br>
    <span class="keyword">public void</span> i_enter_valid_credentials() {<br>
     	loginPage.<span class="method">login</span>(<span class="string">"testuser"</span>, <span class="string">"testpass"</span>);
<br>
    }<br>
    
   <span class="annotation">@Then</span>(<span class="string">"I should be redirected to the dashboard"</span>)<br>
    <span class="keyword">public void</span> i_should_be_redirected_to_the_dashboard() {<br>
        assertTrue(driver.<span class="method">getTitle</span>().<span class="method">contains</span>(<span class="string">"Dashboard"</span>);
    }<br>
}<br></pre>
</li>
   <li><strong>Example Page Object:</strong> <code>LoginPage.java</code></li>
        <pre style="background-color: #EEEEEE; padding: 10px;"><br>
<span class="keyword">public class</span> LoginPage {<br>
    WebDriver driver;<br>
    By username = By.<span class="method">id</span>(<span class="string">"username"</span>);<br>
    By password = By.<span class="method">id</span>(<span class="string">"password"</span>);<br>
    By loginButton = By.<span class="method">id</span>(<span class="string">"login"</span>);<br>

   <span class="keyword">public</span> LoginPage(WebDriver driver) {<br>
        <span class="variable">this</span>.driver = driver;<br>
    }<br>

   <span class="keyword">public void</span> login(String user, String pass) {<br>
        driver.<span class="method">findElement</span>(username).<span class="method">sendKeys</span>(user);<br>
        driver.<span class="method">findElement</span>(password).<span class="method">sendKeys</span>(pass);<br>
        driver.<span class="method">findElement</span>(loginButton).<span class="method">click</span>();<br>
    }<br>
}<br></pre>
        </li>
    </ul>
    <p>This guide provides a basic framework for getting started with BDD using Cucumber and Selenium. Expand upon these foundations according to your project's requirements.</p>
     </div>&nbsp;
   <script>document.write(atob('IDxkaXYgc3R5bGU9ImJhY2tncm91bmQtY29sb3I6ICMzNTNiNWY7IGNvbG9yOiAjZmZmOyBwYWRkaW5nOiAxMHB4OyBmb250LWZhbWlseTogJ1BvcHBpbnMnLCBzYW5zLXNlcmlmOyBmb250LXNpemU6IDAuNzVyZW07IGRpc3BsYXk6IGZsZXg7IGp1c3RpZnktY29udGVudDogc3BhY2UtYmV0d2VlbjsgYWxpZ24taXRlbXM6IGNlbnRlcjsgaGVpZ2h0OiA2Mi43MTI1cHg7Ij4gICAgICAgICA8ZGl2IHN0eWxlPSJwYWRkaW5nLWxlZnQ6IDM1cHg7Ij4gICAgICAgICAgICAgICAgICZjb3B5OyAgICAgICAgICA8c2NyaXB0PmRvY3VtZW50LndyaXRlKG5ldyBEYXRlKCkuZ2V0RnVsbFllYXIoKSk8L3NjcmlwdD4gICAgICAgICBWaXNpb25ldCBTeXN0ZW1zIEFsbCByaWdodHMgcmVzZXJ2ZWQuIDxzcGFuIHN0eWxlPSJjb2xvcjojNmQ2ZTcwOyI+fDwvc3Bhbj4gPGJyPiBDcmVhdGVkIC8gRHJhZnQgYnkgRGFrc2hpbmEgTW9vcnRoeTxzcGFuPiAodmVyIDEuMC4wKSAmI3gyNEM3Ozwvc3Bhbj4gICAgICAgICA8L2Rpdj4gICAgICAgICA8dWwgc3R5bGU9Imxpc3Qtc3R5bGU6IG5vbmU7IGRpc3BsYXk6IGZsZXg7IGdhcDogMTVweDsgcGFkZGluZzogMDsgbWFyZ2luOiAwOyI+ICAgICAgICAgICAgICAgICA8bGkgc3R5bGU9ImRpc3BsYXk6IGlubGluZTsgbWFyZ2luLXJpZ2h0OiAxNXB4OyI+ICAgICAgICAgICAgIDxhIGhyZWY9Imh0dHBzOi8vd3d3LnZpc2lvbmV0LmNvbS90ZXJtcy1vZi11c2UiIHN0eWxlPSJjb2xvcjogI2ZmZjsgdGV4dC1kZWNvcmF0aW9uOiB1bmRlcmxpbmU7Ij5UZXJtcyBvZiBVc2U8L2E+ICAgICAgICAgPC9saT4gICAgICAgICAgICAgICAgIDxsaSBzdHlsZT0iZGlzcGxheTogaW5saW5lOyBtYXJnaW4tcmlnaHQ6IDE1cHg7Ij4gICAgICAgICAgICAgPGEgaHJlZj0iaHR0cHM6Ly93d3cudmlzaW9uZXQuY29tL3ByaXZhY3ktcG9saWN5IiBzdHlsZT0iY29sb3I6ICNmZmY7IHRleHQtZGVjb3JhdGlvbjogdW5kZXJsaW5lOyI+UHJpdmFjeSBQb2xpY3k8L2E+ICAgICAgICAgPC9saT4gICAgICAgICAgICAgICAgIDxsaSBzdHlsZT0iZGlzcGxheTogaW5saW5lOyBtYXJnaW4tcmlnaHQ6IDE1cHg7Ij4gICAgICAgICAgICAgPGEgdGFyZ2V0PSJfYmxhbmsiIGhyZWY9Imh0dHBzOi8vaW5mby52aXNpb25ldC5jb20vaHViZnMvQ2FyYm9uLVJlZHVjdGlvbi1QbGFuLnBkZiIgc3R5bGU9ImNvbG9yOiAjZmZmOyB0ZXh0LWRlY29yYXRpb246IHVuZGVybGluZTsiPkNhcmJvbiBSZWR1Y3Rpb24gUGxhbjwvYT4gICAgICAgICA8L2xpPiAgICAgICAgICAgICAgICAgPGxpIHN0eWxlPSJkaXNwbGF5OiBpbmxpbmU7IG1hcmdpbi1yaWdodDogMTVweDsiPiAgICAgICAgICAgICA8YSB0YXJnZXQ9Il9ibGFuayIgaHJlZj0iaHR0cHM6Ly9pbmZvLnZpc2lvbmV0LmNvbS9odWJmcy9ERUktUG9saWN5LnBkZiIgc3R5bGU9ImNvbG9yOiAjZmZmOyB0ZXh0LWRlY29yYXRpb246IHVuZGVybGluZTsiPkRFSSBQb2xpY3kgYW5kIFByb2NlZHVyZTwvYT4gICAgICAgICA8L2xpPiAgICAgICAgIDwvdWw+ICA8L2Rpdj4='));</script>
</body>
</html>