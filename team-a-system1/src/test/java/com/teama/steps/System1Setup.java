package com.teama.steps;

import com.teama.steps.base.PageObjectRegistry;
import io.cucumber.java.After;
import io.cucumber.java.Before;

/**
 * Registers System1's PageObjectProvider before each scenario.
 * This is the ONE thing each team writes to wire up the shared steps.
 *
 * order=0 ensures this runs before any step that calls PageObjectRegistry.get().
 * order=9999 on @After ensures cleanup happens last.
 */
public class System1Setup {

    @Before(order = 0)
    public void registerSystem1Provider() {
        PageObjectRegistry.set(new System1PageObjectProvider());
        System.out.println("[System1Setup] Registered System1PageObjectProvider");
    }

    @After(order = 9999)
    public void clearProvider() {
        PageObjectRegistry.clear();
    }
}
