package com.teama.steps.base;

import com.sharedframework.cucumber.LoginPageContract;
import com.sharedframework.cucumber.SearchPageContract;

/**
 * Interface each team implements to supply their own page objects.
 *
 * HOW IT WORKS:
 * - SharedLoginSteps and SharedSearchSteps (in this same package) have @Given/@When/@Then.
 * - They take PageObjectProvider in their constructor.
 * - Cucumber's PicoContainer sees that and looks for a class implementing this interface
 *   in the glue path — it finds the team's own impl (e.g. System2PageObjectProvider)
 *   and injects it automatically.
 * - No inheritance needed. No Cucumber rules broken.
 *
 * To onboard a new team: implement this interface + point runner glue at com.teama.steps.base
 */
public interface PageObjectProvider {

    /** Return this system's login page implementation. */
    LoginPageContract getLoginPage();

    /** Return this system's search page implementation. */
    SearchPageContract getSearchPage();

    /** Human-readable system name shown in logs. */
    String getSystemName();
}
