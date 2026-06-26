# Step Navigation Guide — "Go to Definition" Across Repos

> **Problem:** Clicking a step in a `.feature` file should jump to the right Java class.  
> Steps in team-b features come from **two** different places:
> - `SharedLoginSteps.java` / `SharedSearchSteps.java` — live in **team-a** source (`steps/base/`)
> - `System2LoginSteps.java` / `System2SearchSteps.java` — live in **team-b** source (`steps/`)
>
> Without correct IDE configuration, clicking a shared step either does nothing, jumps to a
> decompiled `.class` file, or shows "step not defined" even though tests pass.

---

## Why this happens

When team-b runs, it gets `SharedLoginSteps` from a compiled JAR — `team-a-system1-tests.jar`.
The IDE has the bytecode (`.class`) but not the source (`.java`), so navigation fails.

The fix has **two parts** that must both be in place:

| Part | What it fixes |
|------|--------------|
| **A — Sources JAR** | Generates `*-test-sources.jar` alongside `*-tests.jar` so IDEs can attach source to the binary |
| **B — IDE source attachment** | Tells IntelliJ / VS Code to use team-a's source (or the sources JAR) when resolving steps from team-b |

---

## Part A — Generate the Sources JAR (one-time Maven change)

`maven-source-plugin` is already configured in `team-a-system1/pom.xml`.  
After any change to shared steps, rebuild to refresh both JARs:

```bash
mvn package -pl team-a-system1 -DskipTests
# Produces in team-a-system1/target/:
#   team-a-system1-1.0.0-SNAPSHOT.jar              (main JAR — feature files)
#   team-a-system1-1.0.0-SNAPSHOT-tests.jar        (binary step classes)
#   team-a-system1-1.0.0-SNAPSHOT-sources.jar      (main source)
#   team-a-system1-1.0.0-SNAPSHOT-test-sources.jar (test source — IDEs attach this)

# Install into local .m2 so team-b can resolve it
mvn install -pl team-a-system1 -DskipTests
```

---

## Part B — IntelliJ IDEA

### Scenario 1: Both modules open in the same IntelliJ project ✅ (recommended)

When you open the repo **root** (`File → Open → parent pom.xml folder`), IntelliJ imports all modules:
`team-a-system1`, `team-b-system2`, `shared-core/*`.

In this case IntelliJ **resolves steps from source automatically** — no JAR attachment needed.

**Verify it works:**
1. Open `team-b-system2/src/test/resources/features/system2_specific.feature`
2. `Ctrl+Click` on `Given the user is on the login page`  
   → Should jump to `SharedLoginSteps.java` line 33 in team-a module  
   → **If it does → you're done. No further steps needed.**

**Verify Cucumber plugin glue is configured:**
- Open `System2TestRunner.java` → confirm `glue` includes `"com.teama.steps.base"`:
  ```java
  glue = {
      "com.teama.steps.base",   // ← SharedLoginSteps, SharedSearchSteps
      "com.teamb.steps",        // ← System2LoginSteps, System2Setup
      "com.sharedframework.cucumber"
  }
  ```
- If not, add it and the plugin will re-index step definitions.

---

### Scenario 2: team-b is a separate IntelliJ project (no team-a source)

Use this when team-b is cloned alone and team-a is only available as a JAR.

#### Step 1 — Attach the test-sources JAR to the tests JAR

1. **File → Project Structure (`Ctrl+Alt+Shift+S`)→ Libraries**
2. Find `Maven: com.sharedframework:team-a-system1:1.0.0-SNAPSHOT:tests`  
   (or search for `team-a` in the library list)
3. Click **+** (Add) → **Java** → browse to:
   ```
   ~/.m2/repository/com/sharedframework/team-a-system1/1.0.0-SNAPSHOT/
   team-a-system1-1.0.0-SNAPSHOT-test-sources.jar
   ```
4. IntelliJ asks which JAR to attach it to — select the `tests.jar` (not the main JAR).
5. Click **OK → Apply**.

#### Step 2 — Verify source attachment

- Go to **Project panel → External Libraries → Maven: team-a-system1:tests**
- Expand it — you should see a `Sources` node alongside `Classes`.

#### Step 3 — Re-index

`File → Invalidate Caches → Invalidate and Restart`

**After restart:**
- `Ctrl+Click` on `Given the user is on the login page` in any team-b feature file  
  → Jumps to `SharedLoginSteps.java` source (not decompiled bytecode).

---

### IntelliJ — Cucumber plugin scope

The Cucumber for Java plugin scans step definitions from the **glue path** defined in the runner.  
When it shows `Step 'xxx' is not defined` in a feature file even though tests pass, the glue
path in the **IDE run configuration** is missing `com.teama.steps.base`.

Fix via **Run → Edit Configurations → (select your config) → Before launch**:

Or check the Cucumber Inspection settings:  
**File → Settings → Languages & Frameworks → Cucumber → Glue**  
Add: `com.teama.steps.base`

This makes the IDE underline-annotate steps correctly:
- Green underline = step found
- Yellow/red underline = step not found in configured glue

---

### IntelliJ — Quick verify checklist

| Check | Expected |
|-------|----------|
| `Ctrl+Click` on shared step | Jumps to `SharedLoginSteps.java` in team-a |
| `Ctrl+Click` on sys2-specific step | Jumps to `System2LoginSteps.java` in team-b |
| Hover over a shared step | Tooltip shows `SharedLoginSteps > theUserIsOnTheLoginPage()` |
| Step shown in red/undefined | Glue missing `com.teama.steps.base` — fix run config |
| Jumps to `.class` decompile view | Sources JAR not attached — follow Scenario 2 steps above |

---

## Part B — VS Code

`.vscode/settings.json` is already configured in this repo. Here is what each setting does and why.

### What's configured in `.vscode/settings.json`

```jsonc
"cucumberautocomplete.steps": [
  // Shared steps — team-a source. Extension scans these for @Given/@When/@Then.
  // This is why Ctrl+Click on "the user is on the login page" opens SharedLoginSteps.java.
  "team-a-system1/src/test/java/com/teama/steps/base/*.java",

  // Team-A system-specific steps
  "team-a-system1/src/test/java/com/teama/steps/*.java",

  // Team-B steps — System2LoginSteps, System2SearchSteps
  "team-b-system2/src/test/java/com/teamb/steps/*.java",

  // shared-core hooks
  "shared-core/cucumber-base/src/test/java/**/*.java",
  "shared-core/cucumber-base/src/main/java/**/*.java"
]
```

The extension reads **all** listed `.java` files, extracts every `@Given`/`@When`/`@Then` annotation,
and builds an index. When you `Ctrl+Click` a step, it looks up that step text in the index and
opens the file it came from.

```jsonc
"java.project.referencedLibraries": {
  "sources": {
    // When the Language Server encounters team-a-system1-tests.jar,
    // attach this sources jar so "Go to Definition" shows the .java file.
    "team-a-system1/target/team-a-system1-*-tests.jar":
    "team-a-system1/target/team-a-system1-*-test-sources.jar"
  }
}
```

This covers **two** navigation paths:
- Cucumber extension `Ctrl+Click` → resolved via `cucumberautocomplete.steps` (source files)
- Java Language Server `F12` / `Ctrl+Click` on class → resolved via source attachment

### If step navigation still doesn't work in VS Code

**Step 1 — Reload the Cucumber extension index**

`Ctrl+Shift+P` → type `Cucumber: Recount steps` (or reload the window `Ctrl+Shift+P → Developer: Reload Window`)

**Step 2 — Confirm the extension found your steps**

Open the Output panel (`Ctrl+Shift+U`) → select **Cucumber Autocomplete** from the dropdown.  
You should see lines like:
```
Loaded step from: team-a-system1/src/test/java/com/teama/steps/base/SharedLoginSteps.java
  @Given("the user is on the login page")
  @When("the user enters valid credentials")
  ...
Loaded step from: team-b-system2/src/test/java/com/teamb/steps/System2LoginSteps.java
  @When("user navigates to portfolio dashboard")
  ...
```
If a file is missing, the glob in `cucumberautocomplete.steps` is wrong.

**Step 3 — Verify glob paths are relative to workspace root**

The globs in `cucumberautocomplete.steps` are relative to the folder you opened in VS Code.
If you opened `team-b-system2/` directly (not the repo root), you need:

```json
"cucumberautocomplete.steps": [
  // Path from team-b root back to team-a source
  "../team-a-system1/src/test/java/com/teama/steps/base/*.java",
  "src/test/java/com/teamb/steps/*.java"
]
```

**Step 4 — Force Java Language Server to reindex**

`Ctrl+Shift+P` → `Java: Clean Java Language Server Workspace` → **Restart and delete**

**Step 5 — Rebuild sources JAR and reload**

```bash
mvn package -pl team-a-system1 -DskipTests
# Then in VS Code: Ctrl+Shift+P → Developer: Reload Window
```

---

### VS Code — Quick verify checklist

| Check | Expected |
|-------|----------|
| `Ctrl+Click` on shared step in team-b feature | Opens `SharedLoginSteps.java` (team-a source) |
| `Ctrl+Click` on sys2-specific step | Opens `System2LoginSteps.java` (team-b source) |
| Autocomplete in `.feature` file | Shows steps from both team-a and team-b |
| Cucumber Output log | Shows steps loaded from both source paths |
| `F12` on `SharedLoginSteps` class reference | Opens source file, not decompiled bytecode |

---

## Scenario 3: Separate repo clones — team-a source not available locally

If a developer has **only** `team-b-system2` cloned and team-a is only available as a JAR from
the GitLab Package Registry, step navigation to shared steps requires one of:

### Option A — Clone team-a alongside team-b and use multi-root workspace

```bash
# Clone both repos side by side
git clone <team-a-repo> team-a-system1
git clone <team-b-repo> team-b-system2

# Open both in VS Code as a multi-root workspace
code --add team-a-system1 --add team-b-system2
```

Then `cucumberautocomplete.steps` paths work as configured. This is the recommended approach.

### Option B — Use the test-sources JAR (no team-a clone needed)

**VS Code:**
1. Ensure `team-a-system1-test-sources.jar` is in `~/.m2/repository/...`  
   (it's there after `mvn install -pl team-a-system1`)
2. The `java.project.referencedLibraries.sources` mapping in `.vscode/settings.json`  
   attaches it automatically — `F12` on any shared class will open the source.
3. For Cucumber step navigation specifically, add the extracted source path:
   ```bash
   # Extract the test-sources jar to a local folder
   mkdir -p /tmp/team-a-sources
   cd /tmp/team-a-sources
   jar -xf ~/.m2/repository/com/sharedframework/team-a-system1/1.0.0-SNAPSHOT/team-a-system1-1.0.0-SNAPSHOT-test-sources.jar
   ```
   Then in `.vscode/settings.json`:
   ```json
   "cucumberautocomplete.steps": [
     "/tmp/team-a-sources/com/teama/steps/base/*.java",
     "src/test/java/com/teamb/steps/*.java"
   ]
   ```

**IntelliJ:**  
Follow Scenario 2 steps above (attach sources JAR via Project Structure → Libraries).

---

## Step ownership quick reference

| Step text (Gherkin) | Java class | Module | Package |
|---------------------|-----------|--------|---------|
| `the user is on the login page` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the user enters valid credentials` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the user clicks the login button` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the user should be logged in successfully` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the user should see a welcome message` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the user logs out` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the user should be returned to the login page` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the login should fail with an error message` | `SharedLoginSteps` | team-a | `com.teama.steps.base` |
| `the user searches for {string}` | `SharedSearchSteps` | team-a | `com.teama.steps.base` |
| `search results should be displayed` | `SharedSearchSteps` | team-a | `com.teama.steps.base` |
| `the results should contain {string}` | `SharedSearchSteps` | team-a | `com.teama.steps.base` |
| `no results message should be displayed` | `SharedSearchSteps` | team-a | `com.teama.steps.base` |
| `the user clears the search` | `SharedSearchSteps` | team-a | `com.teama.steps.base` |
| `user navigates to the document repository` | `System1LoginSteps` | team-a | `com.teama.steps` |
| `a confirmation number should be generated` | `System1LoginSteps` | team-a | `com.teama.steps` |
| `user navigates to portfolio dashboard` | `System2LoginSteps` | team-b | `com.teamb.steps` |
| `user selects report type {string}` | `System2LoginSteps` | team-b | `com.teamb.steps` |
| `report should be generated successfully` | `System2LoginSteps` | team-b | `com.teamb.steps` |
