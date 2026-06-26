# Developer Setup Guide — Shared Automation Framework

> **Stack:** Java 11 · Maven 3.9 (via wrapper) · Cucumber 7.15 · Selenium 4.18 · JUnit 4 · RestAssured 5.4  
> **Repos:** `team-a-system1` (owner) · `team-b-system2` (consumer) · `shared-core` (base libraries)  
> **Constraint:** Government machine — no internet, no Docker, no admin rights required after initial setup.

---

## ⚡ Quick Start (TL;DR)

```
1.  git clone https://github.com/achaljoshi/shared-automation-repo.git
2.  cd shared-automation-repo
3.  setup.bat          (Windows)   ← run this BEFORE opening any IDE
    ./setup.sh         (Mac/Linux)
4.  Open IDE — everything is auto-configured from committed files
```

> **Why setup first?** The IDE import resolves Maven dependencies from your local `.m2` cache.
> If you open the IDE before running setup, those JARs don't exist yet and every import shows red.
> The script installs them. Then the IDE opens clean.

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [First-Time Setup Script](#2-first-time-setup-script)
3. [IntelliJ IDEA](#3-intellij-idea)
   - 3.1 [What Auto-Configures on Import](#31-what-auto-configures-on-import)
   - 3.2 [Open the Project](#32-open-the-project)
   - 3.3 [Plugins (offline machines)](#33-plugins-offline-machines)
   - 3.4 [Maven Settings (offline machines)](#34-maven-settings-offline-machines)
   - 3.5 [Run Configs — Pre-Built](#35-run-configs--pre-built)
   - 3.6 [Debugging Tests](#36-debugging-tests)
   - 3.7 [IntelliJ Troubleshooting](#37-intellij-troubleshooting)
4. [VS Code](#4-vs-code)
   - 4.1 [What Auto-Configures on Open](#41-what-auto-configures-on-open)
   - 4.2 [Open the Project](#42-open-the-project)
   - 4.3 [Extensions (offline machines)](#43-extensions-offline-machines)
   - 4.4 [Update Java Path for Your Machine](#44-update-java-path-for-your-machine)
   - 4.5 [Run & Debug Tests](#45-run--debug-tests)
   - 4.6 [VS Code Troubleshooting](#46-vs-code-troubleshooting)
5. [Step Navigation — "Go to Definition"](#5-step-navigation--go-to-definition)
6. [Running Tests from Terminal](#6-running-tests-from-terminal)
7. [Project Structure Reference](#7-project-structure-reference)
8. [Environment Variables & System Properties](#8-environment-variables--system-properties)
9. [Common Errors & Fixes](#9-common-errors--fixes)

---

## 1. Prerequisites

Only two things must be manually installed. Everything else is handled by the setup script or committed IDE config.

### Java 11 JDK

```bash
java -version
# Must show: openjdk 11.x.x  or  java 11.x.x
```

If not installed, get OpenJDK 11 from the shared network drive or your IT team.  
**Do not use Java 8 or Java 17** — `maven.compiler.source=11` in `pom.xml` requires exactly Java 11.

### Maven — not required to install separately

The repo ships with a **Maven wrapper** (`mvnw` / `mvnw.cmd`). It downloads and manages Maven 3.9.6 automatically on first run. You never need to install Maven globally.

```bash
./mvnw -version          # macOS/Linux
mvnw.cmd -version        # Windows
```

> **Offline machines:** Place `apache-maven-3.9.6-bin.zip` in `.mvn/wrapper/` and update
> `.mvn/wrapper/maven-wrapper.properties` to use a `file:///` URL instead of the download URL.

### Local `.m2` seed (no-internet machines)

If Maven cannot reach any registry at all, copy the pre-populated `.m2/repository` folder from the team shared drive before running the setup script:

- Windows: `C:\Users\<you>\.m2\repository`
- macOS/Linux: `~/.m2/repository`

Then add the offline flag to `~/.m2/settings.xml`:

```xml
<settings>
  <offline>true</offline>
  <mirrors>
    <mirror>
      <id>internal</id>
      <mirrorOf>*</mirrorOf>
      <url>http://your-nexus:8081/repository/maven-public/</url>
    </mirror>
  </mirrors>
</settings>
```

### ChromeDriver

Bundled in the repo at `shared-core/selenium-base/drivers/`. No download needed.

---

## 2. First-Time Setup Script

Run **once** after cloning, **before opening any IDE**.

```bash
# macOS / Linux
chmod +x setup.sh && ./setup.sh

# Windows
setup.bat
```

### What the script does

| Step | Command | Why |
|------|---------|-----|
| 1 | Checks `java -version` | Fails fast if Java 11 is missing |
| 2 | `./mvnw install -pl shared-core/...` | Installs cucumber-base, selenium-base, api-base, testdata-base into `.m2` |
| 3 | `./mvnw install -pl team-a-system1` | Builds and installs `tests.jar` + `test-sources.jar` — team-b depends on these |
| 4 | `./mvnw compile -pl team-b-system2` | Verifies cross-team dependency resolves correctly |
| 5 | Prints "Setup complete — NOW open your IDE" | Explicit signal that it's safe to open the IDE |

### Expected output (last few lines)

```
[OK] shared-core installed
[OK] team-a-system1 installed
    • team-a-system1-1.0.0-SNAPSHOT.jar
    • team-a-system1-1.0.0-SNAPSHOT-tests.jar
    • team-a-system1-1.0.0-SNAPSHOT-test-sources.jar
[OK] team-b-system2 compiles successfully

══════════════════════════════════════════
  Setup complete! Dependencies installed.
══════════════════════════════════════════
  NEXT — open your IDE NOW
```

If the script fails, check [§9 Common Errors](#9-common-errors--fixes) before opening the IDE.

---

## 3. IntelliJ IDEA

### 3.1 What Auto-Configures on Import

The `.idea/` folder is committed to the repo. IntelliJ reads it on first open and applies everything below **with no manual steps**.

| File | What it sets | Without it |
|------|-------------|-----------|
| `.idea/misc.xml` | Project SDK = Java 11 | You'd see "Project SDK not set" |
| `.idea/compiler.xml` | Bytecode target = 11 for all modules | Modules may compile at wrong level |
| `.idea/encodings.xml` | UTF-8 for all source + feature files | Windows machines default to CP1252, garbling feature file text |
| `.idea/vcs.xml` | Git root mapped | No Git integration on first open |
| `.idea/codeStyles/` | 4-space Java, 2-space Gherkin | Team inconsistency in diffs |
| `.idea/cucumber.xml` | Glue = `com.teama.steps.base + com.teama.steps + com.teamb.steps` | Shared steps show red/undefined in feature files |
| `.idea/runConfigurations/` | System1, System2, Bootstrap configs in toolbar | Every developer creates their own run config manually |

### 3.2 Open the Project

```
File → Open → select the repo root folder (where pom.xml lives)
```

1. IntelliJ shows a popup — click **"Open as Maven Project"** (or **"Trust Project"** first if prompted).
2. Maven sync starts automatically — watch the progress bar at the bottom.
3. When sync completes, all modules appear in the Project panel:

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

That's it. Run configs are in the toolbar, step navigation works, SDK is set.

> If modules are missing: right-click any `pom.xml` → **Add as Maven Project**.

### 3.3 Plugins (offline machines)

IntelliJ needs two plugins for Cucumber step navigation. These are **not auto-installed** — check once after first open.

**File → Settings → Plugins → Installed** — confirm these are present:

| Plugin | Purpose |
|--------|---------|
| **Cucumber for Java** | Gherkin highlighting, `Ctrl+Click` step navigation, run configs |
| **Gherkin** | `.feature` file language support (usually auto-installed with Cucumber plugin) |

If missing and the Marketplace doesn't load (no internet):
1. Get the plugin `.zip` files from the shared drive.
2. Plugins panel → ⚙️ icon → **Install Plugin from Disk** → select the `.zip`.
3. Restart IntelliJ.

Optional but useful:

| Plugin | Purpose |
|--------|---------|
| **Maven Helper** | Right-click `pom.xml` → Analyze Dependencies, run goals |
| **EnvFile** | Load `.env` files into run configurations |

### 3.4 Maven Settings (offline machines)

The Maven wrapper handles the Maven version automatically. You only need to check this once for offline mode.

**File → Settings → Build, Execution, Deployment → Build Tools → Maven**

| Setting | Value |
|---------|-------|
| **Maven home path** | `Use Maven wrapper` (select from dropdown) |
| **User settings file** | `C:\Users\<you>\.m2\settings.xml` |
| **Work offline** | ✅ Check this on government machines |
| **JDK for importer** | Java 11 |

Click **Apply → OK** → click ↻ Refresh in the Maven panel.

### 3.5 Run Configs — Pre-Built

Run configurations are committed in `.idea/runConfigurations/` and appear in the toolbar automatically.

| Config name | What it runs | Module |
|-------------|-------------|--------|
| **System1 — All Tests (@sys1 or @shared)** | Full team-a test suite | team-a-system1 |
| **System2 — All Tests (@sys2 or @shared)** | Full team-b test suite | team-b-system2 |
| **System2 — Shared Tests Only (@shared)** | Only shared scenarios on team-b | team-b-system2 |
| **Bootstrap — First Time Build** | Re-runs the shared-core + team-a install (useful after git pull) | root |

**Running a config:** Select from the dropdown → click ▶ or press `Shift+F10`.

**Changing tags without editing code:**  
Run → Edit Configurations → select config → VM options → add `-Dcucumber.filter.tags=@login`

**Running a single scenario:**  
Open any `.feature` file → click ▶ in the gutter next to a `Scenario:` line → IntelliJ creates a temporary run config for just that scenario.

> Shared feature files loaded from `classpath:features/shared` must be run via the runner class, not directly from the feature file editor.

### 3.6 Debugging Tests

1. Open any step definition file → click in the gutter to set a breakpoint (red dot).
2. Select a run config → click the 🐛 debug button (or `Shift+F9`).
3. IntelliJ pauses at your breakpoint.

**Keyboard shortcuts:**

| Key | Action |
|-----|--------|
| `F8` | Step over |
| `F7` | Step into |
| `Shift+F8` | Step out |
| `F9` | Resume |
| `Alt+F8` | Evaluate expression |

**Inspect which page object is active:**  
In the Debug Variables panel → evaluate `PageObjectRegistry.get()` → shows `System1PageObjectProvider` or `System2PageObjectProvider` depending on which runner triggered the test.

**Debug a specific scenario:**  
Open the `.feature` file → click 🐛 in the gutter next to the `Scenario:` line.

### 3.7 IntelliJ Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| Red errors everywhere on first open | Setup script wasn't run before opening IDE | Close IDE, run `setup.bat`/`setup.sh`, reopen |
| `Cannot resolve symbol 'Cucumber'` | Maven sync failed | Click ↻ in Maven panel, or **File → Invalidate Caches → Restart** |
| Step shows red/undefined in feature file | `.idea/cucumber.xml` not loaded yet | Restart IntelliJ; confirm Cucumber plugin is installed |
| `team-a-system1-tests.jar` not found | setup script didn't complete | Run `./mvnw install -pl team-a-system1 -DskipTests` then ↻ Maven |
| `Error: release version 11 not supported` | JDK is not Java 11 | File → Project Structure → SDK → set to Java 11 |
| Tests pass in Maven but fail in IntelliJ | Working directory mismatch | Edit run config → set **Working directory = `$MODULE_WORKING_DIR$`** |
| `classpath:features/shared` not found | team-a main JAR not on classpath | `./mvnw install -pl team-a-system1 -DskipTests` then ↻ Maven |
| Maven panel is empty | Tool window not open | **View → Tool Windows → Maven** |
| Run configs not in toolbar | `.idea/runConfigurations/` not loaded | File → Invalidate Caches → Restart |

---

## 4. VS Code

### 4.1 What Auto-Configures on Open

The `.vscode/` folder is committed to the repo. VS Code reads it the moment you open the folder.

| File | What it sets | Without it |
|------|-------------|-----------|
| `.vscode/extensions.json` | Shows "Install recommended extensions?" popup | You'd manually install each extension |
| `.vscode/settings.json` | `java.import.maven.enabled: true` (auto-imports all Maven modules), Cucumber step paths for both team-a and team-b, source attachment for shared steps JAR | Modules not imported, step navigation broken |
| `.vscode/launch.json` | 4 debug configs with correct `sourcePaths` | You'd create debug configs manually |
| `.vscode/tasks.json` | All test tasks in command palette, Bootstrap as default build task | No `Ctrl+Shift+B` shortcut |
| `.editorconfig` | Consistent tab/encoding across all editors | Diff noise from formatting differences |

### 4.2 Open the Project

```bash
code .
```

On first open VS Code does three things automatically:

1. **Extension popup** — "This workspace has extension recommendations. Do you want to install them?" → click **Install**. This installs:
   - `vscjava.vscode-java-pack` (Java Language Server, Debugger, Test Runner, Maven)
   - `alexkrechik.cucumberautocomplete` (Cucumber step navigation)
   - `EditorConfig.EditorConfig`
   - `redhat.vscode-xml`

2. **Maven import** — `java.import.maven.enabled: true` in `settings.json` triggers automatic multi-module import. All modules (`team-a-system1`, `team-b-system2`, `shared-core/*`) appear in the Java Projects panel.

3. **Java indexing** — Language Server builds a symbol index. A progress bar appears bottom-left. Step navigation and Test Explorer are available after this completes (~1 min).

> **Trust prompt:** If asked "Do you trust the authors?" → click **Yes, I trust the authors**.

### 4.3 Extensions (offline machines)

If the extension Marketplace doesn't load (no internet), install from `.vsix` files on the shared drive:

Extensions panel → `...` (More Actions) → **Install from VSIX** → select each file:

| Extension ID | File to install |
|---|---|
| `vscjava.vscode-java-pack` | `vscjava.vscode-java-pack-*.vsix` |
| `alexkrechik.cucumberautocomplete` | `alexkrechik.cucumberautocomplete-*.vsix` |
| `EditorConfig.EditorConfig` | `EditorConfig.EditorConfig-*.vsix` |

Reload VS Code after installing.

### 4.4 Update Java Path for Your Machine

`.vscode/settings.json` has a Java runtime path that may need to match your machine's JDK location.

Open `.vscode/settings.json` and update `java.configuration.runtimes`:

```json
"java.configuration.runtimes": [
  {
    "name": "JavaSE-11",
    "path": "C:\\Program Files\\Java\\jdk-11",    // Windows
    // "path": "/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home",  // macOS
    // "path": "/usr/lib/jvm/java-11-openjdk-amd64",                          // Linux
    "default": true
  }
]
```

Find your Java path:
```bash
# Windows
where java
# macOS
/usr/libexec/java_home -v 11
# Linux
update-alternatives --list java
```

### 4.5 Run & Debug Tests

#### From the Test Explorer (easiest)

1. Open the **Testing** panel — beaker icon in the left sidebar.
2. `System1TestRunner` and `System2TestRunner` appear after Java indexing.
3. Click ▶ to run, 🐛 to debug.

#### From the command palette tasks

`Ctrl+Shift+P` → **Tasks: Run Task** → choose:

| Task | What it runs |
|------|-------------|
| `Test — System1 All (@sys1 or @shared)` | Full team-a suite |
| `Test — System2 All (@sys2 or @shared)` | Full team-b suite |
| `Test — System2 Shared Only (@shared)` | Shared scenarios on team-b |
| `Test — System1 Login Tag Only` | @login tag only |
| `Test — All Modules` | Both teams, fail-at-end |
| `Rebuild team-a test-jar` | After editing shared steps — regenerates JAR |
| `Bootstrap — First Time Build` | Re-run the initial build (after git pull) |

`Ctrl+Shift+B` runs the **Bootstrap** task directly (useful after pulling changes that update shared-core).

#### Debug with launch configs

Press `F5` → choose a configuration from `.vscode/launch.json`:

| Config | Module | Tags |
|--------|--------|------|
| System1 — All Tests | team-a-system1 | @sys1 or @shared |
| System2 — All Tests | team-b-system2 | @sys2 or @shared |
| System2 — Shared Only | team-b-system2 | @shared |
| System1 — Login Tag Only | team-a-system1 | @login |

Set a breakpoint in any `.java` file → press F5 → VS Code pauses at the line. Works in shared step files (`SharedLoginSteps.java`) because `sourcePaths` in `launch.json` includes both team-a and team-b source roots.

### 4.6 VS Code Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| Red errors everywhere on first open | Setup script wasn't run first | Close VS Code, run `setup.bat`/`setup.sh`, reopen |
| Extension popup didn't appear | Extensions already installed, or dismissed | `Ctrl+Shift+P` → **Extensions: Show Recommended Extensions** |
| Java Language Server not starting | Extension not installed | Confirm `vscjava.vscode-java-pack` is installed and enabled |
| Maven modules not imported | `java.import.maven.enabled` not applied | `Ctrl+Shift+P` → **Java: Import Java Projects** |
| No step navigation in feature files | Cucumber extension not installed, or path mismatch | Confirm `alexkrechik.cucumberautocomplete` installed; `Ctrl+Shift+P` → **Reload Window** |
| Shared steps still not found after reload | Glob path wrong for your workspace root | See [§5 Step Navigation](#5-step-navigation--go-to-definition) for path fix |
| Test Explorer shows no tests | Java indexing still running | Wait for progress bar to finish; `Ctrl+Shift+P` → **Java: Force Java Compilation** |
| Debugger shows decompiled bytecode instead of source | Sources JAR not attached | Run `./mvnw package -pl team-a-system1 -DskipTests` then **Reload Window** |
| `ClassNotFoundException` in test run | Module not compiled | `Ctrl+Shift+P` → **Java: Force Java Compilation** |

---

## 5. Step Navigation — "Go to Definition"

`Ctrl+Click` on any step in any `.feature` file should jump to the correct Java source file.

| Step type | Expected destination |
|-----------|---------------------|
| `Given the user is on the login page` (shared) | `team-a-system1/.../SharedLoginSteps.java:33` |
| `When the user enters valid credentials` (shared) | `team-a-system1/.../SharedLoginSteps.java:40` |
| `When user navigates to portfolio dashboard` (team-b) | `team-b-system2/.../System2LoginSteps.java` |
| `When user navigates to the document repository` (team-a) | `team-a-system1/.../System1LoginSteps.java` |

**How it works:**

- **IntelliJ:** `.idea/cucumber.xml` sets global glue = `com.teama.steps.base + com.teama.steps + com.teamb.steps`. The Cucumber plugin scans all these packages across all open modules and builds a step index. Since team-a is a module in the same project, it resolves from source directly.

- **VS Code:** `.vscode/settings.json` sets `cucumberautocomplete.steps` to glob both `team-a-system1/src/test/java/com/teama/steps/base/*.java` and `team-b-system2/src/test/java/com/teamb/steps/*.java`. The extension indexes all `@Given`/`@When`/`@Then` annotations from both paths.

**If step navigation doesn't work** — see `STEP_NAVIGATION.md` for the full troubleshooting guide including the JAR-only scenario (team-b cloned without team-a source).

---

## 6. Running Tests from Terminal

Use `./mvnw` (not `mvn`) to ensure the correct Maven version is used.

```bash
# Team A — all tests
./mvnw test -pl team-a-system1

# Team B — all tests
./mvnw test -pl team-b-system2

# Filter by tag
./mvnw test -pl team-a-system1 -Dcucumber.filter.tags="@login"
./mvnw test -pl team-b-system2 -Dcucumber.filter.tags="@sys2"
./mvnw test -pl team-b-system2 -Dcucumber.filter.tags="@shared"
./mvnw test -pl team-b-system2 -Dcucumber.filter.tags="@sys2 or @shared"

# Pass credentials
./mvnw test -pl team-a-system1 -Dtest.username=admin -Dtest.password=s3cr3t

# Run all modules (don't stop on first failure)
./mvnw test --fail-at-end

# Rebuild shared steps JAR after editing SharedLoginSteps / SharedSearchSteps
./mvnw package -pl team-a-system1 -DskipTests
./mvnw install -pl team-a-system1 -DskipTests
./mvnw test -pl team-b-system2   # picks up the new shared steps
```

**Windows:** replace `./mvnw` with `mvnw.cmd`.

---

## 7. Project Structure Reference

```
shared-automation-framework/               ← root (parent pom.xml)
│
├── .idea/                                 ← IntelliJ auto-config (committed)
│   ├── misc.xml                           ← JDK 11
│   ├── compiler.xml                       ← bytecode target 11
│   ├── encodings.xml                      ← UTF-8
│   ├── cucumber.xml                       ← global glue paths for step navigation
│   └── runConfigurations/                 ← pre-built run configs
│
├── .vscode/                               ← VS Code auto-config (committed)
│   ├── settings.json                      ← Maven auto-import, Cucumber step paths
│   ├── extensions.json                    ← recommended extensions popup
│   ├── launch.json                        ← debug configs with sourcePaths
│   └── tasks.json                         ← Maven test tasks
│
├── .mvn/wrapper/                          ← Maven wrapper (no Maven install needed)
│
├── setup.sh  /  setup.bat                 ← run ONCE after clone, before IDE
│
├── shared-core/
│   ├── cucumber-base/                     ← ScenarioContext, hooks, base utilities
│   ├── selenium-base/                     ← WebDriver factory, base page
│   ├── api-base/                          ← RestAssured setup
│   └── testdata-base/                     ← PropertyLoader, test data helpers
│
├── team-a-system1/                        ← Team A's project (publishes shared steps)
│   ├── src/main/resources/features/shared/  ← ★ shared .feature files (in main JAR)
│   └── src/test/java/com/teama/
│       ├── runner/System1TestRunner.java
│       ├── steps/base/                    ← ★ exported in test-jar + test-sources.jar
│       │   ├── SharedLoginSteps.java      ←   Ctrl+Click from any team-b feature → here
│       │   ├── SharedSearchSteps.java
│       │   ├── PageObjectProvider.java    ←   interface all teams implement
│       │   └── PageObjectRegistry.java   ←   ThreadLocal — how step wiring works
│       └── steps/                         ← local to team-a (not exported)
│           ├── System1Setup.java
│           ├── System1LoginSteps.java
│           └── System1PageObjectProvider.java
│
└── team-b-system2/                        ← Team B's project (imports shared steps)
    └── src/test/java/com/teamb/
        ├── runner/System2TestRunner.java
        └── steps/                         ← local to team-b (not exported)
            ├── System2Setup.java
            ├── System2LoginSteps.java
            └── System2PageObjectProvider.java
```

---

## 8. Environment Variables & System Properties

All properties use `System.getProperty()` with safe defaults — no property file needed for basic runs.

| Property | Default | Description |
|----------|---------|-------------|
| `test.username` | `testuser` | Login username passed to shared login steps |
| `test.password` | `testpass` | Login password |
| `cucumber.filter.tags` | runner default | Tag expression — overrides the `tags` in `@CucumberOptions` |
| `webdriver.chrome.driver` | auto-detected | Path to ChromeDriver binary |
| `browser.headless` | `false` | `true` = run Chrome without a window |
| `base.url` | system-specific | Target app URL per environment |

**IntelliJ run config:**
```
Edit Configurations → VM options:
-Dtest.username=admin -Dtest.password=s3cr3t
```

**VS Code launch.json:**
```json
"vmArgs": "-Dtest.username=admin -Dtest.password=s3cr3t"
```

**Terminal:**
```bash
./mvnw test -pl team-a-system1 -Dtest.username=admin -Dtest.password=s3cr3t
```

---

## 9. Common Errors & Fixes

### Red errors everywhere in the IDE on first open

**Cause:** IDE was opened before `setup.bat` / `setup.sh` completed.  
**Fix:** Close the IDE. Run the setup script. Wait for "Setup complete". Reopen the IDE.

---

### `Cannot resolve symbol 'Cucumber'` / `Cannot resolve symbol 'Given'`

**Cause:** Maven sync failed — test dependencies not on classpath.  
**Fix:**
- IntelliJ: click ↻ in the Maven panel, or **File → Invalidate Caches → Restart**
- VS Code: `Ctrl+Shift+P` → **Java: Import Java Projects**

---

### Shared steps show as undefined (red underline) in `.feature` file

**Cause:** Cucumber plugin glue not configured for `com.teama.steps.base`.  
**IntelliJ fix:** `.idea/cucumber.xml` should have loaded automatically. Restart IntelliJ. Confirm Cucumber for Java plugin is installed.  
**VS Code fix:** Confirm `cucumberautocomplete.steps` in `settings.json` includes `team-a-system1/src/test/java/com/teama/steps/base/*.java`. `Ctrl+Shift+P` → **Reload Window**.

---

### `team-a-system1-tests.jar` not found when compiling team-b

**Cause:** Setup script didn't run, or ran but failed at step 3.  
**Fix:**
```bash
./mvnw install -pl team-a-system1 -DskipTests
```
Confirm the JAR exists:
```
~/.m2/repository/com/sharedframework/team-a-system1/1.0.0-SNAPSHOT/
  team-a-system1-1.0.0-SNAPSHOT-tests.jar         ← required by team-b
  team-a-system1-1.0.0-SNAPSHOT-test-sources.jar  ← required for IDE step navigation
```

---

### `InvalidMethodException: You're not allowed to extend classes that define Step Definitions`

**Cause:** Cucumber 7 forbids inheriting from any class with `@Given`/`@When`/`@Then`.  
**Fix:** Use the `PageObjectRegistry` pattern instead. Never extend `SharedLoginSteps`. Create a `SystemXSetup.java` with `@Before(order=0)` that calls `PageObjectRegistry.set(new YourProvider())`.

---

### `No PageObjectProvider registered for this thread`

**Cause:** `@Before(order=0)` hook is missing or its package is not in the runner's `glue`.  
**Fix:**
1. Confirm `SystemXSetup.java` exists with `@Before(order=0)`.
2. Confirm the package is in the runner's glue:
```java
glue = {
    "com.teama.steps.base",
    "com.teamb.steps",        // ← System2Setup must be in this package
    "com.sharedframework.cucumber"
}
```

---

### `NullPointerException` — username/password is `null`

**Cause:** `PropertyLoader.get(filename, key)` was used. Its first argument is a *filename*, not a key.  
**Fix:** Use `System.getProperty("test.username", "testuser")` and pass values via `-D` args.

---

### `classpath:features/shared` — no features found at runtime

**Cause:** Shared feature files in `src/main/resources/` aren't being packaged into the main JAR.  
**Fix:** Confirm `team-a-system1/pom.xml` has:
```xml
<build>
  <resources>
    <resource><directory>src/main/resources</directory></resource>
  </resources>
</build>
```
Then: `./mvnw install -pl team-a-system1 -DskipTests`

---

### Debugger opens decompiled bytecode instead of source file

**Cause:** Sources JAR not attached to the tests JAR.  
**Fix:** `./mvnw package -pl team-a-system1 -DskipTests` generates `test-sources.jar`. VS Code attaches it via the `java.project.referencedLibraries.sources` mapping in `settings.json`. Reload VS Code after rebuilding.  
For IntelliJ: **File → Project Structure → Libraries** → find `team-a-system1:tests` → add `test-sources.jar` as Sources.

---

*See also: `STEP_NAVIGATION.md` for the full step-navigation troubleshooting guide.*  
*Update this document when new modules, glue packages, or system properties are added.*
