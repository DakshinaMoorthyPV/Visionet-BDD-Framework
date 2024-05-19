<a href="https://visionet.com" target="_blank">
    <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSiSibSX3_L-NrWwg_E2K53LfbAW0NcB6byNJFDqgiEzA&s" alt="Visionet" style="width: 200px; height: auto;">
</a>

<div style="width: 100%; height: 200px; overflow: hidden;">
    <img src="https://i.ibb.co/p0DwV2W/waving-indian-flag-design-with-blue-chakra-1017-23041.png" alt="Waving Indian Flag" style="width: 100%; height: auto; object-fit: cover; object-position: center;">
</div>

<div style="text-align: left; background-color:#ccc; font-weight: 400;">
    <h1>Visionet's Selenium Automation Testing Framework</h1>
    <img src="https://badgen.net/badge/Project/Info/006daf?icon=info" alt="Project Info Badge"> &nbsp;
    <img src="https://badgen.net/badge/Open%20Source/Yes/4caf50?icon=github" alt="Open Source Badge"> &nbsp;
    <img src="https://badgen.net/badge/License/MIT/blue" alt="License Badge"> &nbsp;
    <img src="https://badgen.net/badge/Tested%20On/Windows/00BFFF?icon=windows" alt="Tested On Windows Badge"> &nbsp;
    <img src="https://badgen.net/badge/Made%20With/Selenium/43B02A?icon=selenium" alt="Made With Selenium Badge"> &nbsp;
    <img src="https://badgen.net/badge/Made%20With/Java/007396?icon=java" alt="Made With Java Badge"> &nbsp;
    <img src="https://badgen.net/badge/Made%20With/Cucumber/green" alt="Made With Cucumber Badge">
    <img src="https://badgen.net/badge/Made%20With/TestNG/1F4E78?icon=testng" alt="Made With TestNG Badge"> &nbsp;
    <br/>
    <p>Welcome to Visionet's Selenium Automation Testing Framework. This powerful framework integrates Cucumber, BDD, and TestNG to deliver efficient and effective testing for web applications.</p>
 
### ⚠️ **Dear all, before cloning the project, please set up SSH keys for GitHub access.**
  
</div>
<h1>Setting Up SSH Keys for GitHub Access</h1>
<ol>
  <li>
    <h2>Step 1: Check for Existing SSH Keys</h2>
    <p>Before creating a new SSH key, check if you already have one:</p>
    <pre><code>ls -al ~/.ssh</code></pre>
    <p>Look for files named either <code>id_rsa.pub</code>, <code>id_ed25519.pub</code>, or another public key file. If you find an existing key that you wish to use, you can skip to Step 3.</p>
  </li>
  <li>
    <h2>Step 2: Generate a New SSH Key</h2>
    <p>If you do not have an SSH key or wish to create a new one, use the following command. Replace <code>your_email@visionetsystems.com</code> with your email:</p>
    <pre><code>ssh-keygen -t ed25519 -C "your_email@visionetsystems.com"</code></pre>
    <p>When prompted to "Enter a file in which to save the key," press Enter to use the default file location. Enter a secure passphrase when prompted for better security (optional).</p>
    <p>Note: If your system does not support the ed25519 algorithm, use rsa:</p>
    <pre><code>ssh-keygen -t rsa -b 4096 -C "your_email@visionetsystems.com"</code></pre>
  </li>
  <li>
    <h2>Step 3: Start the SSH Agent</h2>
    <p>Start the ssh-agent in the background:</p>
    <pre><code>eval "$(ssh-agent -s)"</code></pre>
  </li>
  <li>
    <h2>Step 4: Add Your SSH Key to the SSH Agent</h2>
    <p>Add your SSH private key to the ssh-agent:</p>
    <pre><code>ssh-add ~/.ssh/id_ed25519</code></pre>
    <p>Replace <code>id_ed25519</code> with your private key file name if you used a different one.</p>
  </li>
  <li>
    <h2>Step 5: Add SSH Key to Your GitHub Account</h2>
    <p>Copy the SSH public key to your clipboard:</p>
    <pre><code>cat ~/.ssh/id_ed25519.pub | clip</code></pre>
    <p>Replace <code>id_ed25519.pub</code> with your public key file name if different.</p>
    <p>Go to <a href="https://github.com">GitHub.com</a>.</p>
    <p>In the upper-right corner, click your profile photo, then click Settings.</p>
    <p>In the user settings sidebar, click SSH and GPG keys.</p>
    <p>Click New SSH key or Add SSH key.</p>
    <p>In the "Title" field, add a descriptive label for the new key.</p>
    <p>Paste your key into the "Key" field.</p>
    <p>Click Add SSH key.</p>
  </li>
  <li>
    <h2>Step 6: Test Your SSH Connection</h2>
    <p>Test your SSH setup by connecting to GitHub:</p>
    <pre><code>ssh -T git@github.com</code></pre>
    <p>If everything is set up correctly, you should see a message like:</p>
    <pre><code>Hi username! You've successfully authenticated, but GitHub does not provide shell access.</code></pre>
  </li>
</ol>
<h2>Conclusion</h2>
<p>You are now ready to clone, pull, and push to repositories on GitHub using SSH!</p>
