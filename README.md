# LaunchWrapperTestSuite
LaunchWrapperTestSuite makes it possible to run JUnit tests that require transformed classes. It provides a custom test runner that will load the test class in a separate `LaunchClassLoader` that applies previously configured transformers.

# Setup
LaunchWrapperTestSuite can be used with any build system. The following examples will use [Gradle].

1. Add test dependency on LaunchWrapperTestSuite:

    ```gradle
    dependencies {
        testCompile 'org.spongepowered:launchwrappertestsuite:1.0'
    }
    ```

2. Implement a test tweak class that configures the transformers. LaunchWrapperTestSuite comes with `AbstractTestTweaker` which provides some utility methods and sets up the class loader to work with JUnit.

    ```java
    import net.minecraft.launchwrapper.LaunchClassLoader;
    import org.spongepowered.test.launch.AbstractTestTweaker;
    
    public class MyTestTweaker extends AbstractTestTweaker {
    
        @Override
        public void injectIntoClassLoader(LaunchClassLoader loader) {
            // Important so we can configure some settings for JUnit
            super.injectIntoClassLoader(loader);
            
            // Configure your transformers here (a few examples below)
            
            // Access transformer
            registerAccessTransformer("test_at.cfg", "test2_at.cfg");
            
            // Mixin environment
            MixinBootstrap.init();
            Mixins.addConfiguration("mixins.test.json");
            // Set Mixin side, otherwise you get a warning when running the tests
            MixinEnvironment.getDefaultEnvironment().setSide(SERVER);
            
            // Custom transformer
            loader.registerTransformer("com.example.test.launch.transformer.MyCustomTransformer");
        }
    
    }
    ```

3. Set a system property `org.spongepowered.test.launch.tweaker` with the full qualified class name of your tweaker:

    ```gradle
    test {
        systemProperty 'org.spongepowered.test.launch.tweaker', 'com.example.test.launch.MyTestTweaker'
        
        // Run tests it temp directory so the generated logs don't get written to the project
        workingDir = {test.temporaryDir}
    }
    ```

# Usage
To make a test class use the custom test runner, all you need to do is add a annotation to it:

```java
@RunWith(LaunchWrapperTestRunner.class)
public void MyTest {

    @Test
    public void testSomething() {
        // ...
    }

}
```

When you run your tests using `gradle test` it should load the test class in the custom classloader and apply the configured transformers.

## Parameterized test
If you are using a [parameterized test](https://github.com/junit-team/junit4/wiki/Parameterized-tests) you need to change the test runner to `LaunchWrapperParameterized`:

```java
@RunWith(LaunchWrapperParameterized.class)
public void MyParameterizedTest {

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        // ...
    }

}
```

[Gradle]: https://gradle.org/
