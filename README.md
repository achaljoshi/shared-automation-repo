# Shared Automation Framework

A Maven multi-module automation framework enabling **cross-team test reuse** across government systems.

---

## Getting Started — Step-by-Step (follow this order)

> **Important:** Run the setup script BEFORE opening the project in any IDE.
> The IDE import depends on Maven dependencies that the script installs.
> Opening the IDE first causes red "cannot resolve symbol" errors everywhere.

### Step 1 — Clone the repo

```bash
git clone https://github.com/achaljoshi/shared-automation-repo.git
cd shared-automation-repo
```

### Step 2 — Run the setup script

**Windows — PowerShell (recommended):**
```powershell
.\setup.ps1
```

**Windows — Command Prompt (cmd.exe):**
```
setup.bat
```

**Windows — PowerShell fallback (if .ps1 is blocked by policy):**
```powershell
.\setup.bat
```

> **Execution policy:** Government machines often block `.ps1` scripts with
> *"cannot be loaded because running scripts is disabled on this system."*
>
> `setup.ps1` fixes this automatically — it sets `RemoteSigned` policy for your
> user profile (no admin rights needed) then runs the build.
>
> If even `setup.ps1` is blocked, run this **once** in PowerShell first:
> ```powershell
> Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser -Force
> ```
> Then re-run `.\setup.ps1`. Alternatively use `.\setup.bat` — `.bat` files
> are never affected by PowerShell execution policy.

**macOS / Linux:**
```bash
chmod +x setup.sh && ./setup.sh
```

The script takes ~2 minutes and does the following in order:
1. Verifies Java 11 is installed
2. Builds `shared-core` modules (cucumber-base, selenium-base, api-base, testdata-base)
3. Builds `team-a-system1` — installs its `tests.jar` and `test-sources.jar` into your local `.m2`
4. Compiles `team-b-system2` to confirm the cross-team dependency resolves
5. Prints "Setup complete" when done

**The script must complete successfully before you open the IDE.**

### Step 3 — Open in your IDE

Both IDEs auto-configure themselves from files committed in this repo.
No manual plugin setup, no Project Structure changes, no run config creation.

#### IntelliJ IDEA

```
File → Open → select this folder → Open as Maven Project
```

Auto-configured on import:
- Java 11 SDK (`.idea/misc.xml`)
- Bytecode target 11 for all modules (`.idea/compiler.xml`)
- UTF-8 encoding (`.idea/encodings.xml`)
- Cucumber glue paths for step navigation (`.idea/cucumber.xml`)
- Run configs in toolbar: **System1 — All Tests**, **System2 — All Tests**, **Bootstrap** (`.idea/runConfigurations/`)

Verify step navigation works: open any `.feature` file → `Ctrl+Click` a shared step
(e.g. `Given the user is on the login page`) → should jump to `SharedLoginSteps.java` in team-a.

#### VS Code

```bash
code .
```

When VS Code opens:
1. A popup appears: **"Do you want to install the recommended extensions?"** → click **Install**
   (installs Java Pack + Cucumber + EditorConfig automatically)
2. Wait for the Java Language Server to finish indexing (progress bar bottom-left)
3. All Maven modules are imported automatically — no manual steps

Run configs available in the Run panel (`launch.json`). Tasks available via `Ctrl+Shift+B`.

---

## Architecture

```
shared-automation-repo/
├── shared-core/                  ← Published to GitLab Package Registry
│   ├── selenium-base/            ← DriverFactory, BasePageObject, WaitUtils
│   ├── api-base/                 ← RestAssuredClient, ResponseValidator
│   ├── cucumber-base/            ← SystemContext interface, abstract steps
│   └── testdata-base/            ← DataProvider (Excel/JSON), PropertyLoader
├── shared-features/              ← Common .feature files tagged @shared
│   └── src/test/resources/features/
│       ├── login/login_common.feature
│       ├── search/search_common.feature
│       └── upload/file_upload_common.feature
├── team-a-system1/               ← Government Portal team
└── team-b-system2/               ← Financial Dashboard team
```

## How Test Reuse Works

The `SystemContext` interface (in `cucumber-base`) is the key mechanism:

```java
// In shared-core — abstract, system-agnostic
public abstract class LoginSteps {
    @Given("the user is on the login page")
    public void navigateToLogin() {
        getSystemContext().navigateToHome();
        getSystemContext().getLoginPage().waitForPageLoad();
    }
    protected abstract SystemContext getSystemContext();
}

// In team-b-system2 — just wires their own pages
public class System2LoginSteps extends LoginSteps {
    @Override
    protected SystemContext getSystemContext() {
        return new System2Context();  // System2 locators, different UI
    }
}
```

The **same `.feature` file** runs on both systems. No duplication.

## Running Tests

**macOS / Linux / cmd.exe:**
```bash
mvn test -pl team-a-system1 -Dcucumber.filter.tags="@shared or @sys1"
mvn test -pl team-b-system2 -Dcucumber.filter.tags="@shared or @sys2"
mvn test -pl team-a-system1 -Dcucumber.filter.tags="@shared"
```

**Windows PowerShell** — use single quotes around the `-D` value (`@` is a splat operator in double-quoted strings):
```powershell
mvn test -pl team-a-system1 '-Dcucumber.filter.tags=@shared or @sys1'
mvn test -pl team-b-system2 '-Dcucumber.filter.tags=@shared or @sys2'
mvn test -pl team-a-system1 '-Dcucumber.filter.tags=@shared'
```

## Tech Stack

| Layer | Technology |
|---|---|
| BDD Framework | Cucumber 7 + JUnit 4 |
| UI Automation | Selenium 4 (headless-capable) |
| API Testing | REST Assured 5 |
| Windows/COM | JACOB (DLL bundled) |
| Build | Apache Maven 3 (multi-module) |
| CI | GitLab CI — Shell executor (no Docker) |
| Reporting | ExtentReports 5 → GitLab Pages |
| Data | Apache POI + Jackson |
| Language | Java 11 |

## Government Environment Notes

- **No internet required** — GitLab Package Registry on internal network
- **No Docker** — Shell executor with pre-installed JDK + Maven
- **No admin rights** — ChromeDriver bundled under `drivers/`
- **JACOB DLL** — pre-deployed once by IT to `%JAVA_HOME%\bin`

## Onboarding a New Team

1. Add `shared-core` dependency to your `pom.xml`
2. Add `shared-features` as a git submodule
3. Implement `SystemContext` with your page objects
4. Add `include: ci-templates.yml` to your `.gitlab-ci.yml`

Done. Your team's tests + all `@shared` scenarios run immediately.

---

📊 **Executive presentation:** `shared-automation-framework-presentation.pptx`
