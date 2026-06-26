# Developer Setup Guide — Shared Automation Framework

> **Stack:** Java 11 · Maven 3.8+ · Cucumber 7.15 · Selenium 4.18 · JUnit 4 · RestAssured 5.4  
> **Repos:** `team-a-system1` (owner) · `team-b-system2` (consumer) · `shared-core` (base libraries)  
> **Constraint:** Government machine — no internet, no Docker, no admin rights required after initial setup.

---

## Table of Contents

1. [Prerequisites (both IDEs)](#1-prerequisites-both-ides)
2. [IntelliJ IDEA Setup](#2-intellij-idea-setup)
   - 2.1 [Install Plugins](#21-install-plugins)
   - 2.2 [Import the Project](#22-import-the-project)
   - 2.3 [Configure JDK](#23-configure-jdk)
   - 2.4 [Configure Maven](#24-configure-maven)
   - 2.5 [Cucumber Run Configuration](#25-cucumber-run-configuration)
   - 2.6 [Run Tests via IntelliJ](#26-run-tests-via-intellij)
   - 2.7 [Debugging Tests](#27-debugging-tests)
   - 2.8 [IntelliJ Troubleshooting](#28-intellij-troubleshooting)
3. [VS Code Setup](#3-vs-code-setup)
   - 3.1 [Install Extensions](#31-install-extensions)
   - 3.2 [Open the Project](#32-open-the-project)
   - 3.3 [Configure Java Runtime](#33-configure-java-runtime)
   - 3.4 [Configure Maven in VS Code](#34-configure-maven-in-vs-code)
   - 3.5 [settings.json Configuration](#35-settingsjson-configuration)
   - 3.6 [Cucumber Extension Configuration](#36-cucumber-extension-configuration)
   - 3.7 [Run & Debug Tests](#37-run--debug-tests)
   - 3.8 [VS Code Troubleshooting](#38-vs-code-troubleshooting)
4. [First-Time Build (both IDEs)](#4-first-time-build-both-ides)
5. [Running Tests from Terminal](#5-running-tests-from-terminal)
6. [Project Structure Reference](#6-project-structure-reference)
7. [Environment Variables & System Properties](#7-environment-variables--system-properties)
8. [Common Errors & Fixes](#8-common-errors--fixes)

---

## 1. Prerequisites (both IDEs)

Complete these steps before opening either IDE.

### 1.1 Java 11 JDK

```bash
java -version
# Must show: openjdk 11.x.x or java 11.x.x
```

If not installed, get OpenJDK 11 from the shared network drive or your IT team.  
**Do not use Java 8 or Java 17** — the `maven.compiler.source=11` in `pom.xml` requires exactly Java 11.

### 1.2 Maven 3.8+

```bash
mvn -version
# Must show: Apache Maven 3.8.x or higher
```

If not installed, download `apache-maven-3.8.x-bin.zip` from the shared drive, extract it, and add `bin/` to your `PATH`:

**Windows:**
```
System Properties → Environment Variables → Path → Add: C:\tools\apache-maven-3.8.x\bin
```

**macOS/Linux (`~/.zshrc` or `~/.bashrc`):**
```bash
export M2_HOME=/opt/apache-maven-3.8.x
export PATH=$M2_HOME/bin:$PATH
```

### 1.3 Local Maven Repository Setup (No Internet)

Since machines have no internet, the local `.m2` cache must be seeded from the team's shared drive.

1. Copy the pre-populated `repository/` folder from the shared drive into:
   - Windows: `C:\Users\<you>\.m2\repository`
   - macOS/Linux: `~/.m2/repository`

2. Add the offline flag to `~/.m2/settings.xml` so Maven never tries the internet:

```xml
<settings>
  <offline>true</offline>

  <!-- If your team uses a local Nexus/GitLab Package Registry mirror, add here -->
  <mirrors>
    <mirror>
      <id>internal-mirror</id>
      <mirrorOf>*</mirrorOf>
      <url>http://your-internal-nexus:8081/repository/maven-public/</url>
    </mirror>
  </mirrors>
</settings>
```

### 1.4 ChromeDriver (for Selenium tests)

ChromeDriver is bundled in the repo at `shared-core/selenium-base/drivers/`.  
No separate download needed. The framework reads it via `WebDriverManager` fallback to the local path.

---

## 2. IntelliJ IDEA Setup

### 2.1 Install Plugins

Go to **File → Settings → Plugins** (Windows/Linux) or **IntelliJ IDEA → Preferences → Plugins** (macOS).

Install each plugin below. If the Marketplace tab doesn't load (no internet), use **Install Plugin from Disk** and load the `.jar` from the shared drive.

| Plugin | Purpose | Install from Marketplace ID |
|--------|---------|----------------------------|
| **Cucumber for Java** | Gherkin syntax highlighting, step navigation, run configs | `gherkin` |
| **Gherkin** | `.feature` file support (auto-installed with Cucumber plugin) | `gherkin` |
| **Maven Helper** | Visualise dependency tree, run Maven goals from right-click | `MavenRunHelper` |
| **JUnit** | JUnit 4 test runner support (usually bundled) | built-in |
| **EnvFile** | Load `.env` files as run configuration environment | `com.intellij.plugins.envfile` |

After installing, **restart IntelliJ**.

> **Offline install:** From the shared drive, copy plugin `.zip` files and use  
> Plugins → ⚙️ → Install Plugin from Disk.

### 2.2 Import the Project

1. **File → Open** → select the root folder of the repo (where the parent `pom.xml` lives).
2. IntelliJ detects the Maven multi-module project and shows a popup — click **"Open as Maven Project"** (or **Trust Project** if prompted).
3. Wait for the Maven sync to complete — watch the progress bar at the bottom.  
   If it fails, see [§2.8 Troubleshooting](#28-intellij-troubleshooting).

**Module structure you should see in the Project panel:**
```
shared-automation-framework          ← root
├── shared-core
│   ├── cucumber-base
│   ├── selenium-base
│   ├── api-base
│   └── testdata-base
├── team-a-system1
└── team-b-system2
```

If modules are missing, right-click `pom.xml` → **Add as Maven Project**.

### 2.3 Configure JDK

1. **File → Project Structure → Project**
2. Set **SDK** to Java 11 (if not shown, click **Add SDK → JDK** and point to your JDK 11 home).
3. Set **Language level** to `11 - Local variable syntax for lambda parameters`.
4. Go to **File → Project Structure → Modules**, select each module, confirm **Language level = 11**.
5. Click **Apply → OK**.

Verify the compiler target matches:
- **File → Settings → Build, Execution, Deployment → Compiler → Java Compiler**
- Set **Project bytecode version** to `11`.

### 2.4 Configure Maven

1. **File → Settings → Build, Execution, Deployment → Build Tools → Maven**

| Setting | Value |
|---------|-------|
| **Maven home path** | Path to your Maven install (e.g., `C:\tools\apache-maven-3.8.x`) |
| **User settings file** | `C:\Users\<you>\.m2\settings.xml` |
| **Local repository** | `C:\Users\<you>\.m2\repository` |
| **Work offline** | ✅ Checked (government machine — no internet) |
| **Use plugin registry** | ✅ Checked |

2. Under **Importing**, set:
   - **JDK for importer**: Java 11
   - **VM options for importer**: `-Xmx1024m`

3. Click **Apply → OK**, then click the **Refresh** (↻) button in the Maven panel.

### 2.5 Cucumber Run Configuration

IntelliJ needs a run configuration to execute Cucumber tests with the correct tags and glue.

#### Option A — Run via JUnit Runner Class (recommended)

This is the simplest method. The runner class already has all settings baked in.

1. Open `team-a-system1/src/test/java/com/teama/runner/System1TestRunner.java`
2. Click the **green play button** ▶ next to the class declaration.
3. IntelliJ auto-creates a JUnit run config for it.

To customise tags without editing code, edit the run config:
- **Run → Edit Configurations → JUnit → System1TestRunner**
- Under **VM options**, add: `-Dcucumber.filter.tags=@sys1`

#### Option B — Dedicated Cucumber Run Configuration

1. **Run → Edit Configurations → + → Cucumber Java**

| Field | Team A Value | Team B Value |
|-------|-------------|-------------|
| **Name** | `System1 - All Tests` | `System2 - All Tests` |
| **Main class** | `io.cucumber.core.cli.Main` | same |
| **Glue** | `com.teama.steps.base com.teama.steps com.sharedframework.cucumber` | `com.teama.steps.base com.teamb.steps com.sharedframework.cucumber` |
| **Feature or folder path** | `src/test/resources/features` | `src/test/resources/features` |
| **Working directory** | `$MODULE_WORKING_DIR$` | `$MODULE_WORKING_DIR$` |
| **Use classpath of module** | `team-a-system1` | `team-b-system2` |
| **Tags** | `@sys1 or @shared` | `@sys2 or @shared` |

2. Under **VM options** add:
```
-Dtest.username=testuser -Dtest.password=testpass
```

3. Click **Apply → OK**.

#### Option C — Run a Single Feature File

1. Open any `.feature` file.
2. Click the ▶ button next to a specific `Scenario:` or `Feature:` line.
3. IntelliJ creates a temporary Cucumber run config for just that scenario.

> For shared feature files loaded from `classpath:features/shared`, you must run them through the runner class — IntelliJ cannot directly run classpath-referenced features.

### 2.6 Run Tests via IntelliJ

**Full test suite for Team A:**
```
Right-click team-a-system1/pom.xml → Run Maven → test
```

**Full test suite for Team B:**
```
Right-click team-b-system2/pom.xml → Run Maven → test
```

**Run specific tags via Maven panel:**
1. Open the Maven panel (right side bar).
2. Expand `team-a-system1 → Lifecycle → test`.
3. Right-click `test` → **Run Maven Build** → add to **Command line**:
   ```
   -Dcucumber.filter.tags="@login"
   ```

**Run via Runner class (fastest for development):**
1. Open `System1TestRunner.java` or `System2TestRunner.java`.
2. Press **Shift+F10** or click ▶ in the gutter.

### 2.7 Debugging Tests

1. Set a breakpoint in any step definition class (e.g., `SharedLoginSteps.java` line 35).
2. Open the runner class → click the **bug icon** 🐛 next to the class.
3. IntelliJ starts Cucumber in debug mode — it pauses at your breakpoint.
4. Use **F8** (step over), **F7** (step into), **F9** (resume).

**Debug a specific scenario:**
- Open the `.feature` file → click 🐛 next to the `Scenario:` line.

**Inspect `PageObjectRegistry` state:**
- In the Debug panel → Variables → evaluate `PageObjectRegistry.get()` to see which provider is active.

### 2.8 IntelliJ Troubleshooting

| Problem | Fix |
|---------|-----|
| `Cannot resolve symbol 'Cucumber'` | Maven sync failed — click ↻ Reload in Maven panel, or **File → Invalidate Caches → Restart** |
| Feature file shows `Step 'xxx' is not defined` | Plugin not installed, or glue path wrong. Check Cucumber plugin is active and run config glue matches runner |
| `team-a-system1-tests.jar` not found | Run `mvn install` on `team-a-system1` first so the test-jar is in `.m2` |
| `Module 'team-b-system2' not found in project` | Right-click `team-b-system2/pom.xml` → Add as Maven Project |
| `Error: release version 11 not supported` | JDK set to Java 8. Change Project SDK to Java 11 in Project Structure |
| Tests pass in Maven but not in IntelliJ | Working directory mismatch. Set **Working directory = `$MODULE_WORKING_DIR$`** in run config |
| `classpath:features/shared not found` | `team-a-system1` main JAR not on classpath. Run `mvn install -pl team-a-system1` first |
| Maven panel is empty | **View → Tool Windows → Maven** to open it |

---

## 3. VS Code Setup

### 3.1 Install Extensions

Open the **Extensions** panel (`Ctrl+Shift+X` / `Cmd+Shift+X`) and install:

| Extension | Publisher | ID | Purpose |
|-----------|-----------|----|---------|
| **Extension Pack for Java** | Microsoft | `vscjava.vscode-java-pack` | Java language support, debugger, test runner |
| **Language Support for Java** | Red Hat | `redhat.java` | Core Java LSP (included in pack above) |
| **Debugger for Java** | Microsoft | `vscjava.vscode-java-debug` | Debug support (included in pack) |
| **Test Runner for Java** | Microsoft | `vscjava.vscode-java-test` | Run/debug JUnit tests (included in pack) |
| **Maven for Java** | Microsoft | `vscjava.vscode-maven` | Maven project support (included in pack) |
| **Cucumber (Gherkin) Full Support** | Alexander Krechik | `alexkrechik.cucumberautocomplete` | Feature file highlighting, step navigation |
| **GitLens** | GitKraken | `eamodio.gitlens` | Optional — Git history/blame |

**Offline install (no internet):**
1. Download the `.vsix` file for each extension from the shared drive.
2. In VS Code: Extensions panel → `...` (More Actions) → **Install from VSIX** → select the file.

### 3.2 Open the Project

1. **File → Open Folder** → select the root repo folder (where parent `pom.xml` lives).
2. VS Code detects Java and Maven — the Java Projects panel populates automatically.
3. If prompted `"Do you trust the authors of the files in this folder?"` → click **Yes, I trust the authors**.
4. Wait for the Java Language Server to initialise (watch the progress indicator bottom-left).

**Multi-root workspace (optional — if teams have separate repo clones):**

Create `automation.code-workspace` in a parent folder:
```json
{
  "folders": [
    { "name": "team-a-system1", "path": "./team-a-system1" },
    { "name": "team-b-system2", "path": "./team-b-system2" },
    { "name": "shared-core",    "path": "./shared-core" }
  ]
}
```
Open it with **File → Open Workspace from File**.

### 3.3 Configure Java Runtime

1. Press `Ctrl+Shift+P` → type `Java: Configure Java Runtime`.
2. Confirm **Java 11** is selected for the project.
3. If Java 11 is not listed, click **+ Add JDK** and browse to your JDK 11 installation.

Or add directly to `settings.json`:
```json
"java.configuration.runtimes": [
  {
    "name": "JavaSE-11",
    "path": "C:\\Program Files\\Java\\jdk-11",
    "default": true
  }
]
```

Verify the compiler level:
- `Ctrl+Shift+P` → `Java: Open Project Settings` → confirm source/target is `11`.

### 3.4 Configure Maven in VS Code

1. `Ctrl+Shift+P` → `Maven: Edit Settings` → opens your `~/.m2/settings.xml`.
2. Confirm `<offline>true</offline>` is set (see §1.3 above).

Add Maven executable path to `settings.json` if `mvn` is not on your PATH:
```json
"maven.executable.path": "C:\\tools\\apache-maven-3.8.x\\bin\\mvn.cmd"
```

**Trigger Maven sync:**
- Open the **Maven** panel (left sidebar → Explorer → Maven).
- Click ↻ on the root project to reload all modules.
- You should see all modules listed: `shared-automation-framework`, `team-a-system1`, `team-b-system2`, etc.

### 3.5 settings.json Configuration

Create or update `.vscode/settings.json` at the **root of the repo**:

```json
{
  // ── Java ──────────────────────────────────────────────────────────────
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-11",
      "path": "C:\\Program Files\\Java\\jdk-11",
      "default": true
    }
  ],
  "java.compile.nullAnalysis.mode": "automatic",
  "java.sources.organizeImports.staticStarThreshold": 5,

  // ── Maven ─────────────────────────────────────────────────────────────
  "maven.executable.path": "C:\\tools\\apache-maven-3.8.x\\bin\\mvn.cmd",
  "maven.terminal.useJavaHome": true,
  "java.configuration.maven.userSettings": "C:\\Users\\<you>\\.m2\\settings.xml",

  // ── Test Runner ───────────────────────────────────────────────────────
  "java.test.config": {
    "workingDirectory": "${workspaceFolder}",
    "vmArgs": [
      "-Dtest.username=testuser",
      "-Dtest.password=testpass",
      "-Dcucumber.filter.tags=@sys1 or @shared"
    ]
  },

  // ── Cucumber Extension ────────────────────────────────────────────────
  "cucumberautocomplete.steps": [
    "team-a-system1/src/test/java/**/*.java",
    "team-b-system2/src/test/java/**/*.java",
    "shared-core/**/src/test/java/**/*.java"
  ],
  "cucumberautocomplete.syncfeatures": "team-a-system1/src/main/resources/features/**/*.feature",
  "cucumberautocomplete.strictGherkinCompletion": false,
  "cucumberautocomplete.onTypeDelay": 300,

  // ── File associations ─────────────────────────────────────────────────
  "files.associations": {
    "*.feature": "cucumber"
  },

  // ── Editor ────────────────────────────────────────────────────────────
  "editor.tabSize": 4,
  "editor.insertSpaces": true,
  "files.encoding": "utf8",
  "editor.formatOnSave": false
}
```

> **macOS/Linux paths:** Replace `C:\\...` with `/opt/...` or `/usr/local/...` as appropriate.

### 3.6 Cucumber Extension Configuration

The **Cucumber (Gherkin) Full Support** extension needs to know where step definitions live so it can navigate from `.feature` steps to Java code.

Create `.vscode/cucumberautocomplete.json` (optional, alternative to settings.json):
```json
{
  "steps": [
    "team-a-system1/src/test/java/**/*.java",
    "team-b-system2/src/test/java/**/*.java"
  ],
  "pages": [],
  "syncfeatures": "**/*.feature",
  "strictGherkinCompletion": false
}
```

**Navigating from feature to step:**
- `Ctrl+Click` on a step in a `.feature` file → jumps to the matching `@Given`/`@When`/`@Then` method.
- For steps in `team-a-system1-tests.jar`, the extension resolves them from `team-a-system1/src/test/java/com/teama/steps/base/`.

**Note on shared steps:** Steps defined in `SharedLoginSteps.java` appear in both team-a and team-b feature files. The extension resolves them from team-a's source since it's in the same workspace.

### 3.7 Run & Debug Tests

#### Run all tests for a module

In the Maven panel (left sidebar):
```
Maven → team-a-system1 → Lifecycle → test → ▶ Run
```

Or from the terminal:
```bash
mvn test -pl team-a-system1
mvn test -pl team-b-system2
```

#### Run via Test Explorer

1. Open the **Testing** panel (beaker icon in the left sidebar, or `Ctrl+Shift+P` → `Testing: Focus on Test Explorer View`).
2. VS Code scans for JUnit classes — you should see `System1TestRunner` and `System2TestRunner`.
3. Click ▶ next to any runner to execute it.
4. Click 🐛 next to any runner to debug it.

#### Run a Single Feature Scenario

1. Open a `.feature` file.
2. A **Run Test | Debug Test** code lens appears above each `Scenario:` — click to run just that scenario.
   > Requires the JUnit runner class to be correctly configured as the entry point.

#### Create a Launch Configuration for Debugging

Create `.vscode/launch.json`:
```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Debug System1 Tests",
      "request": "launch",
      "mainClass": "org.junit.runner.JUnitCore",
      "args": "com.teama.runner.System1TestRunner",
      "projectName": "team-a-system1",
      "vmArgs": "-Dtest.username=testuser -Dtest.password=testpass",
      "cwd": "${workspaceFolder}/team-a-system1"
    },
    {
      "type": "java",
      "name": "Debug System2 Tests",
      "request": "launch",
      "mainClass": "org.junit.runner.JUnitCore",
      "args": "com.teamb.runner.System2TestRunner",
      "projectName": "team-b-system2",
      "vmArgs": "-Dtest.username=testuser -Dtest.password=testpass",
      "cwd": "${workspaceFolder}/team-b-system2"
    },
    {
      "type": "java",
      "name": "Debug System1 — login tag only",
      "request": "launch",
      "mainClass": "org.junit.runner.JUnitCore",
      "args": "com.teama.runner.System1TestRunner",
      "projectName": "team-a-system1",
      "vmArgs": "-Dcucumber.filter.tags=@login -Dtest.username=testuser",
      "cwd": "${workspaceFolder}/team-a-system1"
    }
  ]
}
```

Press **F5** to start debugging with the selected configuration.

**Setting breakpoints:**
- Open `SharedLoginSteps.java` → click in the gutter left of any line → red dot appears.
- Run in debug mode → VS Code pauses at the breakpoint.
- Hover over variables to inspect them, or open the **Debug Console** and type `PageObjectRegistry.get()`.

### 3.8 VS Code Troubleshooting

| Problem | Fix |
|---------|-----|
| Java Language Server not starting | `Ctrl+Shift+P` → `Java: Clean Java Language Server Workspace` → restart |
| No step navigation in `.feature` files | Check `cucumberautocomplete.steps` path in `settings.json` is correct |
| Test Explorer shows no tests | Wait for indexing to finish; or `Ctrl+Shift+P` → `Java: Force Java Compilation` |
| `team-a-system1-tests.jar` not resolved | Run `mvn install -pl team-a-system1` in terminal first |
| Maven panel is empty | `Ctrl+Shift+P` → `Maven: Update Maven Project` |
| `Cannot find symbol SharedLoginSteps` in team-b | Test-jar not installed. Run: `mvn install -pl team-a-system1 -DskipTests` |
| Feature file gherkin errors on shared steps | Steps live in JAR, not source. Extension may not resolve them — use `team-a-system1` source as reference |
| Encoding issues with `.feature` files | Ensure `"files.encoding": "utf8"` in settings.json |
| `ClassNotFoundException: System1TestRunner` | Module not built. `Ctrl+Shift+P` → `Java: Force Java Compilation` |
| Debugger not hitting breakpoints | Source attachment missing. Right-click JAR in Java Projects panel → Attach Source |

---

## 4. First-Time Build (both IDEs)

Run these commands **once** on a fresh clone before opening the IDE. This seeds the local `.m2` cache and installs the shared JAR.

```bash
# Step 1 — build and install shared-core modules
mvn install -pl shared-core/cucumber-base -am -DskipTests
mvn install -pl shared-core/selenium-base -am -DskipTests
mvn install -pl shared-core/api-base      -am -DskipTests
mvn install -pl shared-core/testdata-base -am -DskipTests

# Step 2 — build team-a and install its test-jar into local .m2
# (team-b depends on this test-jar)
mvn install -pl team-a-system1 -DskipTests

# Step 3 — build team-b (now team-a test-jar is available)
mvn compile -pl team-b-system2

# Verify all tests compile and run
mvn test -pl team-a-system1
mvn test -pl team-b-system2
```

**Expected output:**
```
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 5. Running Tests from Terminal

### Run specific tags
```bash
# Only shared login tests
mvn test -pl team-a-system1 -Dcucumber.filter.tags="@login"

# Only system2-specific tests
mvn test -pl team-b-system2 -Dcucumber.filter.tags="@sys2"

# Shared tests on team-b
mvn test -pl team-b-system2 -Dcucumber.filter.tags="@shared"

# Multiple tags
mvn test -pl team-b-system2 -Dcucumber.filter.tags="@sys2 or @shared"
```

### Pass credentials
```bash
mvn test -pl team-a-system1 \
  -Dtest.username=myuser \
  -Dtest.password=mypass
```

### Run all modules at once
```bash
mvn test --fail-at-end
```

### Build team-a test-jar (after making changes to shared steps)
```bash
mvn package -pl team-a-system1 -DskipTests
mvn install -pl team-a-system1 -DskipTests
# Then re-run team-b to pick up the new shared steps
mvn test -pl team-b-system2
```

---

## 6. Project Structure Reference

```
shared-automation-framework/               ← root (parent pom.xml)
│
├── shared-core/
│   ├── cucumber-base/                     ← ScenarioContext, hooks, base utilities
│   ├── selenium-base/                     ← WebDriver factory, base page
│   ├── api-base/                          ← RestAssured setup, API base
│   └── testdata-base/                     ← PropertyLoader, test data helpers
│
├── team-a-system1/                        ← Team A's automation project
│   ├── src/main/resources/
│   │   └── features/shared/               ← ★ Shared feature files (go into main JAR)
│   │       ├── login_common.feature
│   │       └── search_common.feature
│   └── src/test/java/com/teama/
│       ├── runner/
│       │   └── System1TestRunner.java     ← JUnit entry point for Team A
│       ├── steps/base/                    ← ★ Exported in test-jar (importable by others)
│       │   ├── SharedLoginSteps.java
│       │   ├── SharedSearchSteps.java
│       │   ├── PageObjectProvider.java    ← Interface
│       │   └── PageObjectRegistry.java   ← ThreadLocal registry
│       ├── steps/
│       │   ├── System1Setup.java          ← @Before/@After hooks for System1
│       │   ├── System1LoginSteps.java     ← Gov-portal-specific steps
│       │   └── System1PageObjectProvider.java
│       └── pages/
│           ├── System1LoginPage.java
│           └── System1SearchPage.java
│
└── team-b-system2/                        ← Team B's automation project
    └── src/test/java/com/teamb/
        ├── runner/
        │   └── System2TestRunner.java     ← JUnit entry point for Team B
        ├── steps/
        │   ├── System2Setup.java          ← @Before/@After hooks for System2
        │   ├── System2LoginSteps.java     ← Financial-dashboard-specific steps
        │   └── System2PageObjectProvider.java
        └── pages/
            ├── System2LoginPage.java
            └── System2SearchPage.java
```

**Key rule:** Steps in `com.teama.steps.base` are exported; steps in `com.teama.steps` and `com.teamb.steps` are local only.

---

## 7. Environment Variables & System Properties

All properties use `System.getProperty()` with safe defaults — no property file needed for basic runs.

| Property | Default | Description |
|----------|---------|-------------|
| `test.username` | `testuser` | Login username for test scenarios |
| `test.password` | `testpass` | Login password for test scenarios |
| `cucumber.filter.tags` | (runner default) | Cucumber tag expression to filter which scenarios run |
| `webdriver.chrome.driver` | auto-detected | Path to ChromeDriver binary (set if auto-detection fails) |
| `browser.headless` | `false` | Set `true` to run Chrome without a visible window |
| `base.url` | system-specific | Target application URL (set per environment) |

**Set in IntelliJ run config:**
```
VM options: -Dtest.username=admin -Dtest.password=s3cr3t
```

**Set in VS Code `launch.json`:**
```json
"vmArgs": "-Dtest.username=admin -Dtest.password=s3cr3t"
```

**Set in Maven command:**
```bash
mvn test -Dtest.username=admin -Dtest.password=s3cr3t
```

---

## 8. Common Errors & Fixes

### `InvalidMethodException: You're not allowed to extend classes that define Step Definitions`

**Cause:** Cucumber 7 forbids class inheritance for step definition classes.  
**Fix:** The framework uses `PageObjectRegistry` (ThreadLocal) instead of inheritance. Never extend `SharedLoginSteps` or `SharedSearchSteps`. Create a separate `System2Setup.java` with `@Before` hook instead.

---

### `Unsatisfied dependency: interface PageObjectProvider`

**Cause:** PicoContainer cannot resolve interface → concrete class injection.  
**Fix:** This is already solved by the `PageObjectRegistry` pattern. Ensure your team's setup class calls `PageObjectRegistry.set(new YourPageObjectProvider())` in a `@Before(order=0)` method.

---

### `No PageObjectProvider registered for this thread`

**Cause:** `@Before(order=0)` hook is missing or not in the glue path.  
**Fix:**
1. Confirm your `SystemXSetup.java` has `@Before(order=0)`.
2. Confirm the package it's in is listed in your runner's `glue` array.
3. Example: `System2Setup` is in `com.teamb.steps` → runner must include `"com.teamb.steps"` in glue.

---

### `team-a-system1-1.0.0-SNAPSHOT-tests.jar` missing from `.m2`

**Cause:** team-a hasn't been built and installed yet.  
**Fix:**
```bash
mvn install -pl team-a-system1 -DskipTests
```
This creates both the main JAR and the `tests.jar` in `~/.m2/repository/com/sharedframework/team-a-system1/`.

---

### `classpath:features/shared` — No features found

**Cause:** Shared feature files are in `src/main/resources/` (not `src/test/resources/`), so they go into the main JAR only if the resources plugin is configured.  
**Fix:** Confirm `team-a-system1/pom.xml` has this build resource block:
```xml
<build>
  <resources>
    <resource><directory>src/main/resources</directory></resource>
  </resources>
</build>
```
Then rebuild: `mvn install -pl team-a-system1 -DskipTests`.

---

### `NullPointerException` on username/password in shared steps

**Cause:** `PropertyLoader.get(filename, key)` was used instead of `System.getProperty()`. The first argument to `PropertyLoader.get()` is a filename, not a key.  
**Fix:** Use `System.getProperty("test.username", "testuser")` and pass values via `-D` VM arguments.

---

### Step definition not found for shared step (feature file shows as undefined)

**Cause:** `com.teama.steps.base` is missing from team-b's runner glue configuration.  
**Fix:** In `System2TestRunner.java`:
```java
glue = {
    "com.teama.steps.base",    // ← this line must be present
    "com.teamb.steps",
    "com.sharedframework.cucumber"
}
```

---

*Generated for internal team use. Update this document when new modules, step packages, or system properties are added.*
