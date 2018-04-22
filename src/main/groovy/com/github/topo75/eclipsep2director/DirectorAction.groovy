package com.github.topo75.eclipsep2director

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import com.github.topo75.eclipsep2director.OperatingSystem.Os
import com.github.topo75.eclipsep2director.OperatingSystem.Ws
import com.github.topo75.eclipsep2director.OperatingSystem.Arch

class DirectorAction {
    private final Project project
    private final Logger logger

    File destination

    File bundlePool = null
    String installIU = null
    String profile = null
    String repository = null
    P2 p2 = new P2()
    boolean roaming = false

    class P2 {
        Os os = OperatingSystem.OS
        Ws ws = OperatingSystem.WS
        Arch arch = OperatingSystem.ARCH
    }

    DirectorAction(Project project) {
        this.project = project
        destination = project.buildDir
        this.logger = project.logger
    }

    void p2(Closure cl) {
        project.configure(this.p2, cl)
    }

    void execute() {
        def bootstrapper = DownloadEclipseBootstrapper.downloadIfMissing(project)
        execDirector(bootstrapper)
    }

    def execDirector(File bootstrapper) {
        def pmdCommand = bootstrapper.absolutePath + buildCommandLineParameters()

        def sout = new StringBuffer()
        def serr = new StringBuffer()

        def currentEnv = [:]
        currentEnv.putAll(System.getenv())
        currentEnv.remove("JAVA_HOME")
        currentEnv.remove("PATH")
        def env = System.getenv().collect { k,v -> "$k=$v" }
        env.add("JAVA_HOME=${System.getProperty('java.home')}")
        env.add("PATH=${System.getProperty('java.home')}${System.getProperty('file.separator')}bin${System.getProperty('path.separator')}${System.getenv('PATH')}")

        def process = pmdCommand.execute(env, bootstrapper.parentFile)
        process.waitForProcessOutput(sout, serr)

        logger.debug(sout.toString())
        if (!serr.toString().empty) {
            logger.error("Executing eclipsec: " + pmdCommand)
            logger.error("eclipsec Returned Out: \n" + sout.toString())
            logger.error("eclipsec Returned Err: \n" + serr.toString().trim())
        }
    }

    static final String SPACE = " "

    private String buildCommandLineParameters() {
        def ret = ""
        ret += SPACE
        ret += "-noSplash"
        ret += SPACE
        ret += "-application"
        ret += SPACE
        ret += "org.eclipse.equinox.p2.director"
        ret += SPACE
        ret += "-profileProperties"
        ret += SPACE
        ret += "org.eclipse.update.install.features=true"
        if (destination) {
            ret += SPACE
            ret += "-destination"
            ret += SPACE
            ret += "\"${destination.absolutePath}\""
        }
        if (roaming) {
            ret += SPACE
            ret += "-roaming"
        }
        if (installIU) {
            ret += SPACE
            ret += "-installIU"
            ret += SPACE
            ret += installIU
        }
        if (repository) {
            ret += SPACE
            ret += "-repository"
            ret += SPACE
            ret += repository
        }
        if (profile) {
            ret += SPACE
            ret += "-profile"
            ret += SPACE
            ret += profile
        }
        if (bundlePool) {
            ret += SPACE
            ret += "-bundlePool"
            ret += SPACE
            ret += "\"${bundlePool.absolutePath}\""
        }
        if (p2) {
            if (p2.os) {
                ret += SPACE
                ret += "-p2.os"
                ret += SPACE
                ret += p2.os.p2
            }
            if (p2.ws) {
                ret += SPACE
                ret += "-p2.ws"
                ret += SPACE
                ret += p2.ws.p2
            }
            if (p2.arch) {
                ret += SPACE
                ret += "-p2.arch"
                ret += SPACE
                ret += p2.arch.p2
            }
        }
        ret += SPACE
        ret += "-vmargs"
        ret += getSystemPropertiesFromGradleProperties()
        ret
    }

    private static final String SYSPROP_PREFIX = "systemProp."

    private static String getSystemPropertiesFromGradleProperties() throws IOException {
        def ret = ""
        final File gradlePropertiesFile = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".gradle" + System.getProperty("file.separator") + "gradle.properties")
        final Properties gradleProperties = new Properties()
        gradlePropertiesFile.withReader { reader ->
            gradleProperties.load(reader)
        }
        gradleProperties.stringPropertyNames().forEach{propName ->
            if (propName.startsWith(SYSPROP_PREFIX)) {
                ret += SPACE
                ret += "\"-D"
                ret += propName.substring(SYSPROP_PREFIX.length())
                ret += "="
                ret += gradleProperties.getProperty(propName)
                ret += "\""
            }
        }
        ret
    }

}
