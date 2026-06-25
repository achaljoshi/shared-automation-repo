# Shared Automation Framework

A Maven multi-module automation framework enabling **cross-team test reuse** across government systems.

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

```bash
# System 1 — shared + system-specific tests
mvn test -pl team-a-system1 -Dcucumber.filter.tags="@shared or @sys1"

# System 2 — shared + system-specific tests
mvn test -pl team-b-system2 -Dcucumber.filter.tags="@shared or @sys2"

# Shared scenarios only (any system)
mvn test -pl team-a-system1 -Dcucumber.filter.tags="@shared"
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
