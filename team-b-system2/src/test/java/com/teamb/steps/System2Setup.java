package com.teamb.steps;

import com.teama.steps.base.PageObjectRegistry;
import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Registers System2's PageObjectProvider before each scenario.
 *
 * This is literally the only wiring code team-b writes.
 * SharedLoginSteps and SharedSearchSteps (from team-a test-jar) pick up
 * System2PageObjectProvider via PageObjectRegistry and call System2's pages.
 */
public class System2Setup {

    @Before(order = 0)
    public void registerSystem2Provider() {
        PageObjectRegistry.set(new System2PageObjectProvider());
        System.out.println("[System2Setup] Registered System2PageObjectProvider");
    }

    @After(order = 9999)
    public void clearProvider() {
        PageObjectRegistry.clear();
    }
}
