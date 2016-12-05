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
import org.junit.runners.Parameterized;

/**
 * {@link Parameterized} test runner for JUnit. To run a parameterized test
 * in the Launchwrapper context, declare this class as test runner using
 * {@link RunWith}.
 */
public class LaunchWrapperParameterized extends Parameterized {

    /**
     * Invoked by JUnit to initialize the {@link LaunchWrapperParameterized}
     * test runner.
     *
     * @param klass The test class
     * @throws Throwable If an error occurs during initialization
     */
    public LaunchWrapperParameterized(Class<?> klass) throws Throwable {
        super(LaunchWrapperTestRunner.loadTestClass(klass));
    }

}
