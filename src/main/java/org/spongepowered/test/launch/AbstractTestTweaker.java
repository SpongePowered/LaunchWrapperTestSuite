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

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class AbstractTestTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {

    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void injectIntoClassLoader(LaunchClassLoader loader) {
        // JUnit attempts to lookup the @Test annotation so we need to make sure the classes are loaded
        // using the same class loader (the main class loader)
        loader.addClassLoaderExclusion("org.junit.");
        loader.addClassLoaderExclusion("org.hamcrest.");
    }

    @Override
    public String getLaunchTarget() {
        return "org.spongepowered.test.launch.TestMain";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    /**
     * Registers an access transformer to apply before test execution.
     *
     * @param files The file paths to the access transformer configurations
     */
    protected static void registerAccessTransformer(String... files) {
        Launch.blackboard.put("at", files);
        Launch.classLoader.registerTransformer("org.spongepowered.test.launch.transformer.AccessTransformer");
    }

}
