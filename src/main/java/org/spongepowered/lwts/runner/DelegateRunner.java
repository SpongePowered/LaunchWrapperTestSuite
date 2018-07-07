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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import com.google.common.collect.Lists;

/**
 * A runner that delegates to another runner.  Note that this class does not do anything
 * to run in a Launchwrapper context; use {@link LaunchWrapperDelegateRunner} for that.
 *
 * When using this directly with {@link RunWith}, also specify a {@link DelegatedRunWith}
 * annotation to indicate the runner to delegate to.
 */
public class DelegateRunner extends Runner {

    /**
     * Specifies the runner to delegate to.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    public @interface DelegatedRunWith {
        /**
         * @return the runner to use
         */
        public Class<? extends Runner> value();
    }

    /**
     * Invoked by JUnit to initialize this runner.
     *
     * @param testClass The test class
     * @param builder The RunnerBuilder
     * @throws InitializationError If an error occurs during initialization
     */
    public DelegateRunner(Class<?> testClass, RunnerBuilder builder) throws InitializationError {
        this(getDelegateClass(testClass), testClass, builder);
    }

    /**
     * Initialize this runner with another runner to delegate to.
     *
     * @param runnerClass The runner to delegate to
     * @param testClass The test class
     * @param builder The RunnerBuilder
     * @throws InitializationError If an error occurs during initialization
     */
    protected DelegateRunner(Class<? extends Runner> runnerClass, Class<?> testClass, RunnerBuilder builder) throws InitializationError {
        this.runner = constructRunner(runnerClass, testClass, builder);
    }

    /**
     * Finds the delegate runner class using the DelegatedRunWith annotation.
     */
    private static Class<? extends Runner> getDelegateClass(Class<?> testClass) throws InitializationError {
        DelegatedRunWith annotation = testClass.getAnnotation(DelegatedRunWith.class);
        if (annotation == null) {
            throw new InitializationError("class '" + testClass + "' must have a DelegateRunWith annotation");
        }
        return annotation.value();
    }

    /**
     * Constructs the runner instance, trying both a constructor that takes only
     * a class, and one that takes a class and a RunnerBuilder. (See
     * https://git.io/fNeAw)
     */
    @SuppressWarnings("serial")
    private static Runner constructRunner(Class<? extends Runner> runnerClass, Class<?> testClass, RunnerBuilder builder) throws InitializationError {
        try {
            return runnerClass.getConstructor(Class.class).newInstance(testClass);
        } catch (Exception e) {
            try {
                return runnerClass.getConstructor(Class.class, RunnerBuilder.class).newInstance(testClass, builder);
            } catch (Exception e2) {
                throw new InitializationError(Lists.newArrayList(
                        new Throwable("Failed to construct runner '" + runnerClass
                                + "' for test '" + testClass + "'", null, false, false) {
                            @Override public String toString() { return getMessage(); }
                        },
                        e, e2));
            }
        }
    }

    private final Runner runner;

    @Override
    public Description getDescription() {
        return runner.getDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        runner.run(notifier);
    }

    @Override
    public int testCount() {
        return runner.testCount();
    }
}
