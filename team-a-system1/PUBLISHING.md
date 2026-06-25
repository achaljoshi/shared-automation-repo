# Publishing team-a-system1 as a Reusable Test Library

## Local install (for demo / local development)

```bash
# From repo root — installs all modules including team-a-system1-*-tests.jar
mvn install -DskipTests

# Or from team-a-system1 directory only
cd team-a-system1
mvn install -DskipTests
```

## Publish to GitLab Package Registry

Uncomment the `<distributionManagement>` block in `team-a-system1/pom.xml`, then:

```bash
mvn deploy -DskipTests
```

Set `CI_PROJECT_ID` in your environment or CI/CD variables to match the GitLab project that hosts the package registry.

## What gets published

| Artifact | Contents |
|---|---|
| `team-a-system1-1.0.0-SNAPSHOT.jar` | Main production classes |
| `team-a-system1-1.0.0-SNAPSHOT-tests.jar` | Test classes: `BaseLoginSteps`, `BaseSearchSteps`, `System1LoginPage`, `System1SearchPage`, plus all `features/shared/*.feature` files |

## How team-b imports it

Add to `team-b-system2/pom.xml`:

```xml
<dependency>
    <groupId>com.sharedframework</groupId>
    <artifactId>team-a-system1</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <classifier>tests</classifier>
    <scope>test</scope>
</dependency>
```

The `classifier=tests` targets the `-tests.jar`. The `scope=test` keeps it off the production compile path.

## What team-b gets for free

- `BaseLoginSteps` — all login `@Given`/`@When`/`@Then` logic (navigate, enter credentials, click login, assert logged in, welcome message, logout, assert login failure)
- `BaseSearchSteps` — all search `@When`/`@Then` logic (enter term, click search, assert results, assert result content, assert no results, clear search)
- `classpath:features/shared/login_common.feature` — 3 runnable login scenarios, consumable via `classpath:features/shared` in runner config
- `classpath:features/shared/search_common.feature` — 2 runnable search scenarios

## What team-b writes themselves (only 3 things per domain)

1. **Their page objects** — `System2LoginPage` and `System2SearchPage` implementing `LoginPageContract` / `SearchPageContract` with their system's locators
2. **Their step classes** — `System2LoginSteps extends BaseLoginSteps` and `System2SearchSteps extends BaseSearchSteps`, providing only `getLoginPage()` / `getSearchPage()` overrides
3. **Their feature files** — system2-specific scenarios in `src/test/resources/features/`

## Runner configuration for team-b

```java
@CucumberOptions(
    features = {
        "src/test/resources/features",   // System2-specific features
        "classpath:features/shared"      // Shared features from team-a JAR
    },
    glue = {
        "com.teamb.steps",               // System2 step implementations
        "com.sharedframework.cucumber"   // Hooks from shared-core
    },
    tags = "@sys2 or @shared"
)
```

## Adding a new team (e.g. team-c-system3)

1. Add the same `team-a-system1` test-jar dependency to `team-c-system3/pom.xml`
2. Create `System3LoginPage implements LoginPageContract` with system3 locators
3. Create `System3LoginSteps extends BaseLoginSteps`, return `System3LoginPage` from `getLoginPage()`
4. Point runner at `classpath:features/shared` — all shared scenarios run immediately with zero copy-paste
