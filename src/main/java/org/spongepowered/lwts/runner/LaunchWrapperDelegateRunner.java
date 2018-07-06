/*
 * This file is part of LaunchWrapperTestSuite, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.lwts.runner;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import com.google.common.base.Strings;

import net.minecraft.launchwrapper.Launch;

/**
 * A runner that delegates to another runner, and runs in Launchwrapper context.
 *
 * When using this directly with {@link RunWith}, also specify a {@link DelegatedRunWith}
 * annotation to indicate the runner to delegate to.
 */
public class LaunchWrapperDelegateRunner extends DelegateRunner {

    public static final String TWEAKER_PROPERTY = "lwts.tweaker";

    private static boolean initialized;

    /**
     * Invoked by JUnit to initialize this runner.
     *
     * @param klass The test class
     * @param builder The RunnerBuilder
     * @throws InitializationError If an error occurs during initialization
     */
    public LaunchWrapperDelegateRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError {
        super(loadTestClass(klass), builder);
    }

    /**
     * Initialize this runner with another runner to delegate to.
     *
     * @param runnerClass The runner to delegate to
     * @param testClass The test class
     * @param builder The RunnerBuilder
     * @throws InitializationError If an error occurs during initialization
     */
    protected LaunchWrapperDelegateRunner(Class<? extends Runner> runnerClass, Class<?> testClass, RunnerBuilder builder) throws InitializationError {
        super(runnerClass, loadTestClass(testClass), builder);
    }

    /**
     * Loads a test class within the Launchwrapper context.
     *
     * <p>The context will be initialized the first time this method is
     * invoked.</p>
     *
     * @param originalClass The original test class to load using Launchwrapper
     * @return The loaded class
     * @throws InitializationError If an errors occurs when loading the class
     */
    public static Class<?> loadTestClass(Class<?> originalClass) throws InitializationError {
        if (!initialized) {
            initialized = true;

            String tweakClass = System.getProperty(TWEAKER_PROPERTY);
            if (Strings.isNullOrEmpty(tweakClass)) {
                throw new RuntimeException("Missing system property " + TWEAKER_PROPERTY);
            }

            // Normally, LaunchWrapper sets the thread's context class loader
            // to the LaunchClassLoader. However, that causes issue as soon as
            // tests are run in the normal class loader in the same thread.
            // Simply resetting it seems to fix various issues with Mockito.
            Thread thread = Thread.currentThread();
            ClassLoader contextClassLoader = thread.getContextClassLoader();

            Launch.main(new String[]{"--tweakClass", tweakClass});

            thread.setContextClassLoader(contextClassLoader);
        }

        try {
            return Class.forName(originalClass.getName(), true, Launch.classLoader);
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
    }

}
