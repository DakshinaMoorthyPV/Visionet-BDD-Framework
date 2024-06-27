<!DOCTYPE html>
<html lang="en">

<head>
 <meta charset="UTF-8">
 <meta name="viewport" content="width=device-width, initial-scale=1.0">
 <title>Advanced Topics - Visionet's Selenium Automation Testing Framework</title>
 <!-- Include Prism.js CSS for syntax highlighting -->
 <link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.24.1/themes/prism-okaidia.min.css" rel="stylesheet" />
 <link rel="stylesheet" href="styles.css">
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
 <header>
 <h1>Advanced Topics in Visionet's Selenium Automation Testing Framework</h1>
 <img src="https://img.shields.io/badge/Advanced%20Topics-CI%2FCD%20%26%20Parallel%20Execution-d33833?logo=jenkins" alt="Advanced Topics Badge">
 </header>

 <main>
 <section>
 <h2>Introduction</h2>
 <p>
 Welcome to the advanced topics guide for Visionet's Selenium Automation Testing Framework. This guide covers cutting-edge features that elevate your testing capabilities and ensure your automation framework is robust and scalable.
 </p>
 </section>

 <section>
 <h2>CI/CD Integration</h2>
 <h3>Overview</h3>
 <p>
 Continuous Integration and Continuous Deployment (CI/CD) pipelines automate the process of testing and deploying your application. Integrating your Selenium tests with CI/CD pipelines ensures that tests are run automatically whenever changes are made
 to your codebase, providing immediate feedback on the health of your application.
 </p>
 <h3>Jenkins Pipeline Configuration</h3>
 <p>
 Below is a simplified example of a Jenkins pipeline configuration. This configuration includes checking out code, running tests with Maven, publishing test results, and sending notifications.
 </p>
  <pre><code class="language-yaml">pipeline { 
    agent any 

    stages {
        stage('Checkout') {
            steps {
                checkout scm // Checks out code from the source control management (SCM) system
            }
        }


        stage('Build and Test') {
            steps {
                sh 'mvn clean test' // Runs Maven to clean and test the project
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml' // Publishes JUnit test results
                    archiveArtifacts artifacts: '**/target/surefire-reports/*', fingerprint: true // Archives test artifacts
                    publishHTML(target: [
                        reportDir: 'target/surefire-reports',
                        reportFiles: 'index.html',
                        reportName: "HTML Test Report",
                        keepAll: true,
                        allowMissing: false
                    ]) // Publishes HTML reports
                }
            }
        }
    }

    post {
        always {
            mail to: 'recipient@example.com',
                subject: "Jenkins Build: ${currentBuild.fullDisplayName}",
                body: """Build: ${currentBuild.fullDisplayName}
                Status: ${currentBuild.result}
                Console Log: ${env.BUILD_URL}console"""
        }
    }
}
</code></pre>

 <h3>Explanation</h3>
 <ul>
 <li><strong>agent any:</strong> Specifies that the pipeline can run on any available agent.</li>
 <li><strong>stages:</strong> Defines the stages of the pipeline, such as Checkout, Build, and Test.</li>
 <li><strong>checkout scm:</strong> Checks out code from the source control management system configured for the project.</li>
 <li><strong>sh 'mvn clean test':</strong> Runs Maven commands to clean and test the project.</li>
 <li><strong>post:</strong> Contains actions to perform after the build stage, such as publishing test results and sending notifications.</li>
 <li><strong>junit '**/target/surefire-reports/*.xml':</strong> Publishes JUnit test results from the specified location.</li>
 <li><strong>archiveArtifacts:</strong> Archives the test artifacts for future reference.</li>
 <li><strong>publishHTML:</strong> Publishes HTML test reports.</li>
 <li><strong>mail:</strong> Sends email notifications with the build details.</li>
 </ul>
 <h3>URLs and Configuration</h3>
 <p>
 Ensure you have the following configurations in place:
 </p>
 <ul>
 <li><strong>Maven Installation:</strong> Install Maven and configure the MAVEN_HOME environment variable in Jenkins.</li>
 <li><strong>Source Control URL:</strong> Configure the repository URL in the SCM configuration section of your Jenkins job.</li>
 <li><strong>Email Notifications:</strong> Configure the SMTP server in Jenkins to enable email notifications.</li>
 <li><strong>HTML Publisher Plugin:</strong> Install the HTML Publisher Plugin in Jenkins for publishing HTML reports.</li>
 </ul>
 </section>

 <section>
 <h3>Steps to Set Up Jenkins Locally and on a Server</h3>
 <h4>Local Setup</h4>
 <ol>
 <li><strong>Download Jenkins:</strong> Visit the Jenkins website and download the appropriate installer for your operating system.</li>
 <li><strong>Install Jenkins:</strong> Run the installer and follow the instructions to install Jenkins on your local machine.</li>
 <li><strong>Start Jenkins:</strong> Once installed, start Jenkins by running the appropriate command for your OS (e.g., <code>java -jar jenkins.war</code>).</li>
 <li><strong>Access Jenkins:</strong> Open a web browser and navigate to <code>http://localhost:8080</code> to access the Jenkins dashboard.</li>
 <li><strong>Install Plugins:</strong> Install necessary plugins such as Git, Maven, and HTML Publisher through the Jenkins plugin manager.</li>
 <li><strong>Configure Jobs:</strong> Create and configure jobs/pipelines to build and test your projects.</li>
 </ol>

 <h4>Server Setup</h4>
 <ol>
 <li><strong>Provision a Server:</strong> Provision a server on a cloud provider (e.g., AWS, Azure, GCP) or use an on-premises server.</li>
 <li><strong>Install Java:</strong> Ensure Java is installed on the server as Jenkins requires Java to run.</li>
 <li><strong>Download Jenkins:</strong> Download the Jenkins package suitable for your server's OS (e.g., DEB package for Ubuntu, RPM for CentOS).</li>
 <li><strong>Install Jenkins:</strong> Install Jenkins using the package manager (e.g., <code>sudo apt install jenkins</code> on Ubuntu).</li>
 <li><strong>Start Jenkins:</strong> Start the Jenkins service using the appropriate command (e.g., <code>sudo systemctl start jenkins</code>).</li>
 <li><strong>Access Jenkins:</strong> Open a web browser and navigate to <code>http://your-server-ip:8080</code> to access the Jenkins dashboard.</li>
 <li><strong>Install Plugins:</strong> Install necessary plugins such as Git, Maven, and HTML Publisher through the Jenkins plugin manager.</li>
 <li><strong>Configure Jobs:</strong> Create and configure jobs/pipelines to build and test your projects.</li>
 </ol>
 </section>
 <section>
<h4>Set Up Webhook</h4>
<ol>
    <li><strong>Navigate to Repository Settings:</strong> In your Git repository (GitHub, GitLab, Bitbucket, etc.), go to the settings page.</li>
    <li><strong>Add Webhook:</strong> Add a new webhook with the Jenkins URL <code>http://your-server-ip:8080/github-webhook/</code>. Choose the events that will trigger the webhook (e.g., push events).</li>
    <li><strong>For Local Systems:</strong> If working on a local system, follow these additional steps to expose your local Jenkins server to the internet:
        <ol>
            <li><strong>Download ngrok:</strong> Go to <a href="https://ngrok.com/download">ngrok's download page</a> and download ngrok for your operating system.</li>
            <li><strong>Install ngrok:</strong> Unzip the downloaded file and place it in a directory included in your system's PATH. For example, you can place it in <code>/usr/local/bin</code> on Unix-based systems or <code>C:\ngrok</code> on Windows and add this directory to your PATH.</li>
            <li><strong>Run ngrok:</strong> Open a terminal and run the following command to start ngrok and expose your local Jenkins server:<pre><code>ngrok http 8080</code></pre>
            </li>
            <li><strong>Copy ngrok URL:</strong> ngrok will generate a public URL that you can use to set up the webhook. It will look something like <code>http://12345.ngrok.io</code>.</li>
            <li><strong>Add Webhook with ngrok URL:</strong> In your Git repository settings, add the webhook with the ngrok URL followed by <code>/github-webhook/</code> (e.g., <code>http://12345.ngrok.io/github-webhook/</code>).</li>
        </ol>
    </li>
    <li><strong>Configure Job to Poll SCM:</strong> In Jenkins, configure the job to poll SCM or use the GitHub hook trigger for GITScm polling.</li>
</ol>

<h4>Set Up Build Triggers and Steps</h4>
<ol>
    <li><strong>Build Triggers:</strong> In the job configuration, set the build trigger to <strong>GitHub hook trigger for GITScm polling</strong> or <strong>Poll SCM</strong> with a suitable schedule.</li>
    <li><strong>Build Steps:</strong> Add build steps as needed, such as executing shell scripts, building with Maven, or running tests.</li>
</ol>

<h4>Configure Job/Pipeline in Jenkins</h4>
<ol>
    <li><strong>Create a New Job:</strong>
        <ol>
            <li>Navigate to Jenkins dashboard.</li>
            <li>Click on <strong>New Item</strong>.</li>
            <li>Enter a name for your job and select <strong>Freestyle project</strong> or <strong>Pipeline</strong>.</li>
            <li>Click <strong>OK</strong>.</li>
        </ol>
    </li>
    <li><strong>Configure Source Code Management (SCM):</strong>
        <ol>
            <li>In the job configuration page, go to the <strong>Source Code Management</strong> section.</li>
            <li>Select <strong>Git</strong>.</li>
            <li>Enter the repository URL and, if necessary, provide credentials.</li>
        </ol>
    </li>
    <li><strong>Set Up Build Triggers:</strong>
        <ol>
            <li>In the job configuration page, go to the <strong>Build Triggers</strong> section.</li>
            <li>Select <strong>GitHub hook trigger for GITScm polling</strong> to trigger builds based on GitHub webhooks.</li>
            <li>Alternatively, select <strong>Poll SCM</strong> and specify a schedule if you prefer polling.</li>
        </ol>
    </li>
    <li><strong>Configure Build Steps:</strong>
        <ol>
            <li>In the job configuration page, go to the <strong>Build</strong> section.</li>
            <li>Add build steps as needed. For example:
                <ul>
                    <li><strong>Execute Shell:</strong> Add shell commands to build your project.</li>
                    <li><strong>Invoke Top-Level Maven Targets:</strong> Use this step if you're using Maven.</li>
                    <li><strong>Run Tests:</strong> Add steps to run your tests.</li>
                </ul>
            </li>
        </ol>
    </li>
    <li><strong>Post-Build Actions:</strong>
        <ol>
            <li>In the job configuration page, go to the <strong>Post-build Actions</strong> section.</li>
            <li>Add any post-build actions as needed. For example:
                <ul>
                    <li><strong>Publish JUnit test result report</strong> to display test results.</li>
                    <li><strong>Send build notifications</strong> to notify team members.</li>
                </ul>
            </li>
        </ol>
    </li>
    <li><strong>Save and Build:</strong>
        <ol>
            <li>Click <strong>Save</strong> to save your job configuration.</li>
            <li>Trigger a build manually to test the setup.</li>
            <li>Ensure the webhook is working by pushing a change to your Git repository and checking if Jenkins triggers a build automatically.</li>
        </ol>
    </li>
</ol>
</section>
 <section>
    <h2>TestNG Configuration for Parallel Execution and Grouping</h2>
    <h3>Configuration File (test-automation-config.xml)</h3>
<pre style="background: #1E1E1E; padding: 10px; border: 1px solid #ddd; border-radius: 4px;">
<code style="color: #D4D4D4;">
<span style="color: #686e6a;">&lt;!-- TestNG Configuration for Parallel Execution and Test Grouping --&gt;</span><br>
<span style="color: #686e6a;">&lt;</span><span style="color: #167fab;">entry</span> <span style="color: #96b629;">key</span>=&quot;<span style="color: #1b975d;">Parallel</span>&quot;<span style="color: #686e6a;">&gt;</span><span style="color: #FFFFFF;">No</span><span style="color: #686e6a;">&lt;/</span><span style="color: #167fab;">entry</span><span style="color: #686e6a;">&gt;</span> <span style="color: #686e6a;">&lt;!-- Options: 'Yes', 'No' --&gt;</span><br>
<span style="color: #686e6a;">&lt;</span><span style="color: #167fab;">entry</span> <span style="color: #96b629;">key</span>=&quot;<span style="color: #1b975d;">Thread-count</span>&quot;<span style="color: #686e6a;">&gt;</span><span style="color: #FFFFFF;">5</span><span style="color: #686e6a;">&lt;/</span><span style="color: #167fab;">entry</span><span style="color: #686e6a;">&gt;</span><br>
<span style="color: #686e6a;">&lt;</span><span style="color: #167fab;">entry</span> <span style="color: #96b629;">key</span>=&quot;<span style="color: #1b975d;">Grouping</span>&quot;<span style="color: #686e6a;">&gt;</span><span style="color: #FFFFFF;">No</span><span style="color: #686e6a;">&lt;/</span><span style="color: #167fab;">entry</span><span style="color: #686e6a;">&gt;</span> <span style="color: #686e6a;">&lt;!-- Options: 'Yes', 'No' --&gt;</span><br>
<span style="color: #686e6a;">&lt;</span><span style="color: #167fab;">entry</span> <span style="color: #96b629;">key</span>=&quot;<span style="color: #1b975d;">Grouping-on-include</span>&quot;<span style="color: #686e6a;">&gt;</span><span style="color: #FFFFFF;">sanity</span><span style="color: #686e6a;">&lt;/</span><span style="color: #167fab;">entry</span><span style="color: #686e6a;">&gt;</span><br>
<span style="color: #686e6a;">&lt;</span><span style="color: #167fab;">entry</span> <span style="color: #96b629;">key</span>=&quot;<span style="color: #1b975d;">Grouping-on-exclude</span>&quot;<span style="color: #686e6a;">&gt;</span><span style="color: #FFFFFF;">none</span><span style="color: #686e6a;">&lt;/</span><span style="color: #167fab;">entry</span><span style="color: #686e6a;">&gt;</span><br>
</code>
</pre>
    <h3>Explanation</h3>
    <ul>
        <li><strong>Parallel:</strong> Set to 'Yes' to enable parallel execution. Set to 'No' to disable parallel execution.</li>
        <li><strong>Thread-count:</strong> Specifies the number of threads to use for parallel execution.</li>
        <li><strong>Grouping:</strong> Set to 'Yes' to enable test grouping. Set to 'No' to disable test grouping.</li>
        <li><strong>Grouping-on-include:</strong> Specifies the test groups to include in the execution.</li>
        <li><strong>Grouping-on-exclude:</strong> Specifies the test groups to exclude from the execution.</li>
    </ul>
</section>

 <section>
 <h2>Custom TestNG Listeners</h2>
 <h3>Overview</h3>
 <p>
 TestNG listeners provide a way to listen to events during test execution and perform actions based on those events. Custom listeners can be used to enhance reporting, handle test retries, and integrate with other tools.
 </p>
 <h3>Creating Custom Listeners</h3>
 <ol>
 <li><strong>Implement Listener Interface:</strong> Create a new Java class that implements one or more of the TestNG listener interfaces (e.g., <code>ITestListener</code>, <code>IInvokedMethodListener</code>).</li>
 <li><strong>Override Methods:</strong> Override the necessary methods to perform actions based on test events. For example, you might override <code>onTestFailure</code> to take a screenshot when a test fails.</li>
 <li><strong>Register Listener:</strong> Register your custom listener in your TestNG XML configuration file or using the <code>@Listeners</code> annotation in your test classes.</li>
 <li><strong>Execute Tests:</strong> Run your tests with the custom listener enabled. Verify that the listener actions are performed as expected during test execution.</li>
 </ol>
 </section>

 <section>
 <h2>Using Advanced Java 17 Concepts in Selenium</h2>
 <h3>Overview</h3>
 <p>Java 17 introduces several advanced features that can enhance your Selenium test automation framework. Here are a few key features and how to use them in your Selenium code:</p>
 <h3>Records</h3>
 <p>Records provide a compact syntax for declaring classes that are transparent holders for shallowly immutable data. Here's an example:</p>
 
<pre><code class="language-java">

	public class UserTest {
    public static void main(String[] args) {
        User user = new User("testuser", "testuser@example.com");
        System.out.println(user.username());
        System.out.println(user.email());
  }
    }
 </code></pre>

<h3>Pattern Matching for instanceof</h3>
<p>Java 17 simplifies the use of <code>instanceof</code> with pattern matching:</p>
<pre><code class="language-java">
		
		Object obj = "Test String";
		if (obj instanceof String str) {
			System.out.println(str.toUpperCase());
}
</code></pre>
<h3>Sealed Classes</h3>
<p>Sealed classes restrict which other classes or interfaces may extend or implement them. Here's an example:</p>
<pre><code class="language-java">

	public abstract sealed class Shape permits Circle, Square {
	}
	public final class Circle extends Shape {
	// Circle implementation
	}

	public final class Square extends Shape {
	// Square implementation
}
</code></pre>
</section>

 <section>
 <h2>Using Relative Locators in Selenium 4.0</h2>
 <h3>Overview</h3>
 <p>Selenium 4 introduces relative locators, which allow you to locate elements relative to other elements. This can make your tests more readable and maintainable. Here are some examples:</p>
 <h3>Using Above Locator</h3>
 <pre><code class="language-java">

	WebElement password = driver.findElement(By.id("password"));
	WebElement username = driver.findElement(RelativeLocator.with(By.tagName("input")).above
	(password));
//Implement More steps here
</code></pre>

<h3>Using Below Locator</h3>
 <pre><code class="language-java">
	
	WebElement username = driver.findElement(By.id("username"));
	WebElement password = driver.findElement(RelativeLocator.with(By.tagName("input")).below(username));
//Implement More steps here
</code></pre>
 <h3>Using Near Locator</h3>
 <pre><code class="language-java">

	WebElement submitButton = driver.findElement(By.id("submit"));
	WebElement checkbox = driver.findElement(RelativeLocator.with(By.tagName("input")).near(submitButton));
//Implement More steps here
</code></pre>
 <h3>Using ToLeftOf Locator</h3>
 <pre><code class="language-java">
	
	WebElement submitButton = driver.findElement(By.id("submit"));
	WebElement cancelButton = driver.findElement(RelativeLocator.with(By.tagName("input")).toLeftOf(submitButton));
//Implement More steps here
</code></pre>
 <h3>Using ToRightOf Locator</h3>
 <pre><code class="language-java">

	WebElement cancelButton = driver.findElement(By.id("cancel"));
	WebElement submitButton = driver.findElement(RelativeLocator.with(By.tagName("input")).toRightOf(cancelButton));
//Implement More steps here
</code></pre>
</section>

<h2>Conclusion</h2>
 <p>
 By leveraging CI/CD integration, Selenium Grid for parallel execution, custom TestNG listeners, advanced Java 17 concepts, and relative locators in Selenium 4.0, you can significantly enhance your test automation framework. These advanced topics help to ensure your testing process is efficient, scalable, and capable of meeting the demands of modern software development.
 </p>
</main>

<!-- Include Prism.js for syntax highlighting -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.24.1/prism.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.24.1/components/prism-java.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.24.1/components/prism-yaml.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.24.1/components/prism-xml-doc.min.js"></script>

</div><br>
  <script>document.write(atob('IDxkaXYgc3R5bGU9ImJhY2tncm91bmQtY29sb3I6ICMzNTNiNWY7IGNvbG9yOiAjZmZmOyBwYWRkaW5nOiAxMHB4OyBmb250LWZhbWlseTogJ1BvcHBpbnMnLCBzYW5zLXNlcmlmOyBmb250LXNpemU6IDAuNzVyZW07IGRpc3BsYXk6IGZsZXg7IGp1c3RpZnktY29udGVudDogc3BhY2UtYmV0d2VlbjsgYWxpZ24taXRlbXM6IGNlbnRlcjsgaGVpZ2h0OiA2Mi43MTI1cHg7Ij4gICAgICAgICA8ZGl2IHN0eWxlPSJwYWRkaW5nLWxlZnQ6IDM1cHg7Ij4gICAgICAgICAgICAgICAgICZjb3B5OyAgICAgICAgICA8c2NyaXB0PmRvY3VtZW50LndyaXRlKG5ldyBEYXRlKCkuZ2V0RnVsbFllYXIoKSk8L3NjcmlwdD4gICAgICAgICBWaXNpb25ldCBTeXN0ZW1zIEFsbCByaWdodHMgcmVzZXJ2ZWQuIDxzcGFuIHN0eWxlPSJjb2xvcjojNmQ2ZTcwOyI+fDwvc3Bhbj4gPGJyPiBDcmVhdGVkIC8gRHJhZnQgYnkgRGFrc2hpbmEgTW9vcnRoeTxzcGFuPiAodmVyIDEuMC4wKSAmI3gyNEM3Ozwvc3Bhbj4gICAgICAgICA8L2Rpdj4gICAgICAgICA8dWwgc3R5bGU9Imxpc3Qtc3R5bGU6IG5vbmU7IGRpc3BsYXk6IGZsZXg7IGdhcDogMTVweDsgcGFkZGluZzogMDsgbWFyZ2luOiAwOyI+ICAgICAgICAgICAgICAgICA8bGkgc3R5bGU9ImRpc3BsYXk6IGlubGluZTsgbWFyZ2luLXJpZ2h0OiAxNXB4OyI+ICAgICAgICAgICAgIDxhIGhyZWY9Imh0dHBzOi8vd3d3LnZpc2lvbmV0LmNvbS90ZXJtcy1vZi11c2UiIHN0eWxlPSJjb2xvcjogI2ZmZjsgdGV4dC1kZWNvcmF0aW9uOiB1bmRlcmxpbmU7Ij5UZXJtcyBvZiBVc2U8L2E+ICAgICAgICAgPC9saT4gICAgICAgICAgICAgICAgIDxsaSBzdHlsZT0iZGlzcGxheTogaW5saW5lOyBtYXJnaW4tcmlnaHQ6IDE1cHg7Ij4gICAgICAgICAgICAgPGEgaHJlZj0iaHR0cHM6Ly93d3cudmlzaW9uZXQuY29tL3ByaXZhY3ktcG9saWN5IiBzdHlsZT0iY29sb3I6ICNmZmY7IHRleHQtZGVjb3JhdGlvbjogdW5kZXJsaW5lOyI+UHJpdmFjeSBQb2xpY3k8L2E+ICAgICAgICAgPC9saT4gICAgICAgICAgICAgICAgIDxsaSBzdHlsZT0iZGlzcGxheTogaW5saW5lOyBtYXJnaW4tcmlnaHQ6IDE1cHg7Ij4gICAgICAgICAgICAgPGEgdGFyZ2V0PSJfYmxhbmsiIGhyZWY9Imh0dHBzOi8vaW5mby52aXNpb25ldC5jb20vaHViZnMvQ2FyYm9uLVJlZHVjdGlvbi1QbGFuLnBkZiIgc3R5bGU9ImNvbG9yOiAjZmZmOyB0ZXh0LWRlY29yYXRpb246IHVuZGVybGluZTsiPkNhcmJvbiBSZWR1Y3Rpb24gUGxhbjwvYT4gICAgICAgICA8L2xpPiAgICAgICAgICAgICAgICAgPGxpIHN0eWxlPSJkaXNwbGF5OiBpbmxpbmU7IG1hcmdpbi1yaWdodDogMTVweDsiPiAgICAgICAgICAgICA8YSB0YXJnZXQ9Il9ibGFuayIgaHJlZj0iaHR0cHM6Ly9pbmZvLnZpc2lvbmV0LmNvbS9odWJmcy9ERUktUG9saWN5LnBkZiIgc3R5bGU9ImNvbG9yOiAjZmZmOyB0ZXh0LWRlY29yYXRpb246IHVuZGVybGluZTsiPkRFSSBQb2xpY3kgYW5kIFByb2NlZHVyZTwvYT4gICAgICAgICA8L2xpPiAgICAgICAgIDwvdWw+ICA8L2Rpdj4='));</script>
</body>
</html>