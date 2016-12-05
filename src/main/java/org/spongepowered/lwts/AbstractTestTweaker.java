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
package org.spongepowered.lwts;

import com.google.common.base.Throwables;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.lwts.transformer.AccessTransformer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.OverridingMethodsMustInvokeSuper;

/**
 * Base tweaker class that configures defaults for use in a testing
 * environment.
 *
 * <p>To use LWTS, extend this class in your own tweaker and initialize
 * your transformers in the {@link #injectIntoClassLoader(LaunchClassLoader)}
 * method.</p>
 */
public abstract class AbstractTestTweaker implements ITweaker {

    private AccessTransformer transformer;

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {

    }

    /**
     * Configures the transformers that should be applied to all classes loaded
     * by the {@link LaunchClassLoader}.
     *
     * <p><b>Important:</b> If you override this method you must call the super
     * method in this class. Otherwise your tests will fail to load.</p>
     *
     * @param loader The launch classloader
     */
    @Override
    @OverridingMethodsMustInvokeSuper
    public void injectIntoClassLoader(LaunchClassLoader loader) {
        // JUnit attempts to lookup the @Test annotation so we need to make sure the classes are loaded
        // using the same class loader (the main class loader)
        loader.addClassLoaderExclusion("org.junit.");
        loader.addClassLoaderExclusion("org.hamcrest.");
    }

    /**
     * Returns the main class that is invoked after initialization of
     * Launchwrapper. If you need to do additional initialization for
     * your tests you can override this method with a custom main class.
     *
     * @return The full qualified name of the main class
     */
    @Override
    public String getLaunchTarget() {
        return "org.spongepowered.test.launch.TestMain";
    }

    /**
     * Returns the arguments that are passed to the launch target configured
     * with {@link #getLaunchTarget()}. The default implementation returns
     * an empty array.
     *
     * @return The launch arguments
     */
    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

    /**
     * Registers an access transformer to apply before test execution.
     *
     * @param file The path to the access transformer configurations
     */
    protected final void registerAccessTransformer(String file) {
        if (this.transformer == null) {
            Launch.classLoader.registerTransformer("org.spongepowered.test.launch.transformer.AccessTransformer");
            for (IClassTransformer transformer : Launch.classLoader.getTransformers()) {
                if (transformer instanceof AccessTransformer) {
                    this.transformer = (AccessTransformer) transformer;
                }
            }
        }

        try {
            this.transformer.register(file);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
