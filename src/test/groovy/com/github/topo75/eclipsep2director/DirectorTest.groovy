package com.github.topo75.eclipsep2director

import groovy.json.StringEscapeUtils
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import static org.junit.Assume.*

class DirectorTest {
    private static final boolean RUN_TEST = false
    @Rule public final TemporaryFolder testProjectDir = new TemporaryFolder()
    @Rule public final TemporaryFolder testOutputDir = new TemporaryFolder()
    private File buildFile

    @Before
    void setup() throws IOException {
        buildFile = testProjectDir.newFile("build.gradle")
    }

    @Test
    void test() throws IOException {
        assumeTrue(RUN_TEST)
        String buildFileContent = "plugins {\n" +
                "    id 'com.github.topo75.EclipseP2Director'\n" +
                "}\n" +
                "\n" +
                "task testDirector {\n" +
                "    director {\n" +
                "        destination= new File('"+ StringEscapeUtils.escapeJava(testOutputDir.getRoot().getAbsolutePath())+"')\n" +
                "        installIU= \"org.eclipse.sdk.ide\"\n" +
                "        profile= \"SDKProfile\"\n" +
                "        repository= \"http://download.eclipse.org/releases/oxygen/\"\n" +
                "    }\n" +
                "    doLast {\n" +
                "    }\n" +
                "}\n"
        writeFile(buildFile, buildFileContent)

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("testDirector")
                .withPluginClasspath()
                .withDebug(true)
                .build()

        System.out.println("Output: " + result.getOutput())
        assertTrue(result.getOutput().contains(testOutputDir.getRoot().getAbsolutePath()))
        assertEquals(TaskOutcome.SUCCESS, result.task(":testDirector").getOutcome())
    }

    private static final String SYSPROP_PREFIX = "systemProp."

    @BeforeClass
    static void setSystemPropertiesFromGradleProperties() throws IOException {
        final File gradlePropertiesFile = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".gradle" + System.getProperty("file.separator") + "gradle.properties")
        final Properties gradleProperties = new Properties()
        gradlePropertiesFile.withReader { reader ->
            gradleProperties.load(reader)
        }
        gradleProperties.stringPropertyNames().forEach{ propName ->
            if (propName.startsWith(SYSPROP_PREFIX)) {
                System.setProperty(propName.substring(SYSPROP_PREFIX.length()), gradleProperties.getProperty(propName))
            }
        }
    }


    private static void writeFile(File destination, String content) throws IOException {
        destination.withWriter {
            it.write(content)
        }
    }
}
