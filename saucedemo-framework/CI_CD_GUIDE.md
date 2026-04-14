# CI/CD Pipeline Integration Guide
### SauceDemo Selenium Framework — GitHub Actions & Harness CI

> **Who is this guide for?**  
> Anyone who has never set up a CI/CD pipeline before. Every step is explained from scratch. No assumed knowledge beyond knowing how to open a terminal.

---

## What Is CI/CD and Why Does This Project Need It?

**CI/CD** stands for *Continuous Integration / Continuous Delivery*.

In plain English:  
- Every time someone pushes code to the repository, a server automatically **builds the project** and **runs all 39 tests**.  
- If a test breaks, the team knows immediately — before anything reaches production.  
- Reports (HTML, PDF, screenshots) are saved and downloadable from each run.

Without CI/CD you have to remember to run `mvn test` manually. With CI/CD, it happens automatically on every push or pull request.

---

## Prerequisites — Do These Before Anything Else

| # | What you need | How to get it | Free? |
|---|---|---|---|
| 1 | **Git installed** on your machine | [git-scm.com/downloads](https://git-scm.com/downloads) | ✅ |
| 2 | **GitHub account** | [github.com](https://github.com) → Sign up | ✅ |
| 3 | **Java 11 installed** locally (to run tests on your machine) | [adoptium.net](https://adoptium.net) | ✅ |
| 4 | **Maven 3.9+ installed** locally | [maven.apache.org/download](https://maven.apache.org/download.cgi) | ✅ |
| 5 | **Harness account** *(Option B only)* | [app.harness.io](https://app.harness.io) → Sign up free | ✅ |

---

## Step 0 — Prepare the Repository (Required for Both Options)

These steps must be done once before you connect anything to a pipeline.

### 0-A. Create a `.gitignore` file

In the project root (`saucedemo-framework/`), create a file called `.gitignore` with this content:

```
# Maven build output
target/

# Generated test reports (created fresh on every pipeline run)
reports/

# IDE files
.idea/
*.iml
.vscode/

# OS files
.DS_Store
Thumbs.db
```

> **Why?** The `reports/` folder is generated during each test run. It must not be committed — the pipeline will produce its own copy.

### 0-B. Enable Headless Mode for CI

Open `src/test/resources/config.properties` and change this line:

```properties
# Before (works on your local machine with a visible browser)
headless=false

# After (required for CI servers that have no screen)
headless=true
```

> **Why?** CI servers have no monitor. Chrome needs headless mode to run without a display.  
> Alternatively, you can leave the file as `false` and pass the flag at run time (`-Dheadless=true`). The pipeline examples below use the flag approach so your local setup is unaffected.

### 0-C. Push the Project to GitHub

Open a terminal in the `saucedemo-framework/` folder and run these commands one at a time:

```bash
# 1. Initialise Git (skip if already a Git repo)
git init

# 2. Stage all files
git add .

# 3. Create the first commit
git commit -m "Initial framework commit"

# 4. Create a new empty repo on github.com first, then link it here
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git

# 5. Push
git push -u origin main
```

> Replace `YOUR_USERNAME` and `YOUR_REPO_NAME` with your actual GitHub username and the repo name you created.

---

## Option A — GitHub Actions ✅ Recommended for Beginners

GitHub Actions is built into GitHub. There is nothing extra to install or sign up for.

### How it works (in one sentence)
You add a YAML file to your repository that tells GitHub *"when code is pushed, run these commands on a Linux server"*.

### A-1. Create the workflow file

Inside the project, create the folder structure exactly as shown:

```
saucedemo-framework/
└── .github/
    └── workflows/
        └── selenium-tests.yml      ← create this file
```

Paste the following content into `selenium-tests.yml`:

```yaml
# ─────────────────────────────────────────────────────────────────────────────
# Name shown in the GitHub Actions tab
name: SauceDemo Selenium Tests

# ─────────────────────────────────────────────────────────────────────────────
# When to run this pipeline
on:
  push:
    branches: [ "main" ]          # runs on every push to main
  pull_request:
    branches: [ "main" ]          # runs on every pull request targeting main

# ─────────────────────────────────────────────────────────────────────────────
jobs:
  test:
    name: Run Selenium Suite
    runs-on: ubuntu-latest        # GitHub-hosted Linux runner (free)

    steps:

      # 1. Check out the repository code
      - name: Checkout repository
        uses: actions/checkout@v4

      # 2. Install Java 11
      - name: Set up Java 11
        uses: actions/setup-java@v4
        with:
          java-version: '11'
          distribution: 'temurin'

      # 3. Cache Maven dependencies so repeat runs are faster
      - name: Cache Maven packages
        uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2

      # 4. Install Google Chrome (the runner has it pre-installed, this ensures latest)
      - name: Install Google Chrome
        run: |
          wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | sudo apt-key add -
          sudo sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list'
          sudo apt-get update -qq
          sudo apt-get install -y google-chrome-stable

      # 5. Run all 39 tests
      #    -Dheadless=true    → no screen on the server
      #    -Dbrowser=chrome   → use Chrome
      - name: Run Maven tests
        run: mvn test -Dheadless=true -Dbrowser=chrome

      # 6. Upload the HTML report so you can download it after each run
      - name: Upload HTML report
        if: always()              # run even if tests fail
        uses: actions/upload-artifact@v4
        with:
          name: extent-report-${{ github.run_number }}
          path: reports/html/
          retention-days: 30

      # 7. Upload the PDF report
      - name: Upload PDF report
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: pdf-report-${{ github.run_number }}
          path: reports/pdf/
          retention-days: 30

      # 8. Upload failure screenshots
      - name: Upload screenshots
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: screenshots-${{ github.run_number }}
          path: reports/screenshots/
          retention-days: 30

      # 9. Publish TestNG results as a structured test summary
      - name: Publish test results
        if: always()
        uses: dorny/test-reporter@v1
        with:
          name: TestNG Results
          path: target/surefire-reports/*.xml
          reporter: java-junit
```

### A-2. Commit and push the workflow file

```bash
git add .github/
git commit -m "Add GitHub Actions CI pipeline"
git push
```

### A-3. Watch it run

1. Go to your repository on **github.com**
2. Click the **Actions** tab at the top
3. You will see a workflow called **"SauceDemo Selenium Tests"** running
4. Click it → click the job **"Run Selenium Suite"** → watch the live logs

### A-4. Download reports after the run

1. On the finished run page, scroll to the bottom
2. Under **Artifacts** you will see:
   - `extent-report-1` — download and open the HTML file in your browser
   - `pdf-report-1` — the PDF summary
   - `screenshots-1` — any failure screenshots

---

## Option B — Harness CI

Harness CI is an enterprise-grade platform. Use this if your team already uses Harness or needs advanced features (dashboards, governance, secrets management).

### B-1. Create a Free Harness Account

1. Go to [app.harness.io](https://app.harness.io)
2. Click **Sign Up** → complete registration
3. On first login, Harness will create a default **Organisation** and **Project** for you — you can use those

### B-2. Connect Your GitHub Repository

Harness needs permission to read your code.

1. In Harness, go to **Account Settings** → **Connectors** → **+ New Connector**
2. Choose **GitHub**
3. Fill in:
   - **Name:** `github-saucedemo`
   - **URL type:** Repository
   - **Repository URL:** `https://github.com/YOUR_USERNAME/YOUR_REPO_NAME`
4. For authentication, select **HTTP** → **Username and Token**
   - Generate a GitHub **Personal Access Token** at  
     `github.com → Settings → Developer Settings → Personal Access Tokens → Tokens (classic)`  
     with scope: `repo`
   - Paste that token into Harness as a **Secret** (Harness will store it encrypted)
5. Click **Save and Continue** → **Test Connection** → **Finish**

### B-3. Create the Pipeline

1. In Harness, go to your project → **Builds** → **Pipelines** → **+ Create a Pipeline**
2. Give it a name: `SauceDemo Automation`
3. Choose **Inline** (stores YAML in Harness, not your repo)
4. Click **Add Stage** → **Build**
5. Switch to the **YAML** editor and paste:

```yaml
pipeline:
  name: SauceDemo Automation
  identifier: saucedemo_automation
  projectIdentifier: default        # your Harness project ID
  orgIdentifier: default            # your Harness org ID

  stages:
    - stage:
        name: Run Tests
        identifier: run_tests
        type: CI
        spec:
          cloneCodebase: true
          infrastructure:
            type: Cloud
            spec:
              platform:
                os: Linux
                arch: Amd64

          execution:
            steps:

              # Install Chrome and run tests
              - step:
                  type: Run
                  name: Install Chrome and Run Maven Tests
                  identifier: run_maven_tests
                  spec:
                    image: maven:3.9.6-eclipse-temurin-11   # Docker image with Java 11 + Maven
                    command: |
                      # Install Chrome
                      apt-get update -qq
                      apt-get install -y wget gnupg2
                      wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add -
                      echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list
                      apt-get update -qq
                      apt-get install -y google-chrome-stable

                      # Run the test suite in headless mode
                      mvn test -Dheadless=true -Dbrowser=chrome

                    # Tell Harness where the TestNG XML results are
                    reports:
                      type: JUnit
                      spec:
                        paths:
                          - target/surefire-reports/**/*.xml

              # Cache Maven dependencies to speed up future runs
              - step:
                  type: SaveCacheGCS
                  name: Cache Maven Dependencies
                  identifier: cache_maven
                  spec:
                    key: maven-{{ checksum "pom.xml" }}
                    sourcePaths:
                      - /root/.m2

  properties:
    ci:
      codebase:
        connectorRef: github-saucedemo    # name from Step B-2
        repoName: YOUR_REPO_NAME
        build:
          type: branch
          spec:
            branch: main
```

> Replace `YOUR_REPO_NAME` with your actual repository name.

6. Click **Save** → **Run Pipeline**

### B-4. Download Reports from Harness

Harness does not store arbitrary files by default. To save the HTML/PDF reports, add an upload step after the test step. Choose the storage that fits your team:

**If using AWS S3:**
```yaml
              - step:
                  type: S3Upload
                  name: Upload Reports to S3
                  identifier: upload_reports
                  spec:
                    connectorRef: aws_connector      # set up an AWS connector first
                    region: us-east-1
                    bucket: my-test-reports
                    sourcePath: reports/
                    target: saucedemo/<+pipeline.executionId>/
```

**If using Google Cloud Storage:**
```yaml
              - step:
                  type: GCSUpload
                  name: Upload Reports to GCS
                  identifier: upload_reports
                  spec:
                    connectorRef: gcs_connector
                    bucket: my-test-reports
                    sourcePath: reports/
                    target: saucedemo/<+pipeline.executionId>/
```

### B-5. Set Up Automatic Triggers

Instead of clicking "Run Pipeline" manually, set a trigger so it runs on every push:

1. In Harness, open your pipeline → **Triggers** tab → **+ Add Trigger**
2. Choose **GitHub**
3. Set:
   - **Event:** Push
   - **Branch:** `main`
4. Click **Create Trigger**

Now every `git push` to `main` fires the pipeline automatically.

---

## Troubleshooting — Most Common Problems

### ❌ `WebDriverException: unknown error: Chrome failed to start`
**Cause:** Headless mode is off, or Chrome is not installed on the runner.  
**Fix:** Make sure `-Dheadless=true` is in your `mvn test` command AND Chrome is installed (the pipeline steps above handle both).

---

### ❌ `Could not find or load main class` or `BUILD FAILURE` before tests even run
**Cause:** Wrong Java version. This project requires **Java 11**.  
**Fix:**
- GitHub Actions: verify `java-version: '11'` in the `setup-java` step.
- Harness: verify the Docker image is `maven:3.9.6-eclipse-temurin-11`, not a different tag.

---

### ❌ Tests pass locally but `TimeoutException` in CI
**Cause:** The CI runner is slower than your machine. The default explicit wait is **15 seconds** — usually enough, but some cloud runners are constrained.  
**Fix:** Override the wait at run time:
```bash
mvn test -Dheadless=true -Dexplicit.wait=30
```

---

### ❌ `No artifact found` — reports folder is empty after the run
**Cause:** The test step failed before writing reports, OR the upload step ran before the test step finished.  
**Fix:** Both upload steps use `if: always()` (GitHub Actions) or are placed after the test step (Harness). Check the logs for the actual failure first.

---

### ❌ `Permission denied` pushing to GitHub
**Cause:** The remote URL uses HTTPS but no credentials are cached.  
**Fix:**
```bash
# Use the GitHub CLI to authenticate
gh auth login

# OR store credentials temporarily
git config --global credential.helper store
git push   # enter username + personal access token when prompted
```

---

## Next Steps — Once the Pipeline Is Green

| Enhancement | How |
|---|---|
| **Slack / email notifications** | GitHub Actions: add `8398a7/action-slack@v3` step. Harness: add a Notification rule in the pipeline. |
| **Run only smoke tests on PR, full suite on merge** | Add TestNG groups (`@Test(groups = "smoke")`) and pass `-Dgroups=smoke` on PR triggers. |
| **Parallel execution** | Set `thread-count` in `testng.xml` and ensure each test class is independent (they already are). |
| **Scheduled nightly run** | GitHub Actions: add `schedule: - cron: '0 2 * * *'` to the `on:` block. |
| **Badge on README** | Add `![CI](https://github.com/USER/REPO/actions/workflows/selenium-tests.yml/badge.svg)` to `README.md`. |

---

## File Locations Summary

```
saucedemo-framework/
├── .github/
│   └── workflows/
│       └── selenium-tests.yml        ← GitHub Actions pipeline (Option A)
├── src/test/resources/
│   └── config.properties             ← set headless=true for CI
├── reports/                          ← generated at runtime, NOT committed
│   ├── html/                         ← ExtentReports HTML
│   ├── pdf/                          ← PDF summary
│   └── screenshots/                  ← failure screenshots
└── target/
    └── surefire-reports/             ← TestNG XML (read by pipeline for test counts)
        └── *.xml
```
