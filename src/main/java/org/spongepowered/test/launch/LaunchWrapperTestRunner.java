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

package org.spongepowered.test.launch;

import com.google.common.base.Strings;
import net.minecraft.launchwrapper.Launch;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class LaunchWrapperTestRunner extends BlockJUnit4ClassRunner {

    public static final String TWEAKER_PROPERTY = "org.spongepowered.test.launch.tweaker";

    private static boolean initialized;

    public LaunchWrapperTestRunner(Class<?> klass) throws InitializationError {
        super(loadTestClass(klass));
    }

    public static Class<?> loadTestClass(Class<?> originalClass) throws InitializationError {
        if (!initialized) {
            initialized = true;

            String tweakClass = System.getProperty(TWEAKER_PROPERTY);
            if (Strings.isNullOrEmpty(tweakClass)) {
                throw new RuntimeException("Missing system property " + TWEAKER_PROPERTY);
            }

            Launch.main(new String[]{"--tweakClass", tweakClass});
        }

        try {
            return Class.forName(originalClass.getName(), true, Launch.classLoader);
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
    }

}
