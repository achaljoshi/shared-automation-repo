package com.teama.steps.base;

/**
 * ThreadLocal registry that decouples SharedLoginSteps / SharedSearchSteps
 * from any specific team's PageObjectProvider.
 *
 * How it works:
 *   1. Each team creates a setup class with a @Before(order=0) hook that calls
 *      PageObjectRegistry.set(new TheirProvider()).
 *   2. SharedLoginSteps and SharedSearchSteps call PageObjectRegistry.get()
 *      instead of taking PageObjectProvider as a constructor parameter.
 *   3. @After(order=9999) calls PageObjectRegistry.clear() to clean up.
 *
 * This avoids all PicoContainer interface-injection issues and Cucumber's
 * "no extending step-def classes" rule simultaneously.
 */
public class PageObjectRegistry {

    private static final ThreadLocal<PageObjectProvider> CURRENT = new ThreadLocal<>();

    /** Called in each team's @Before(order=0) hook. */
    public static void set(PageObjectProvider provider) {
        CURRENT.set(provider);
    }

    /** Called by SharedLoginSteps, SharedSearchSteps to get the active provider. */
    public static PageObjectProvider get() {
        PageObjectProvider provider = CURRENT.get();
        if (provider == null) {
            throw new IllegalStateException(
                "No PageObjectProvider registered for this thread.\n" +
                "Add a @Before(order=0) hook in your team's setup class:\n" +
                "  PageObjectRegistry.set(new YourSystemPageObjectProvider());"
            );
        }
        return provider;
    }

    /** Called in each team's @After(order=9999) hook. */
    public static void clear() {
        CURRENT.remove();
    }
}
