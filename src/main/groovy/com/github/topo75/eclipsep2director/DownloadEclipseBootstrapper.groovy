package com.github.topo75.eclipsep2director

import de.undercouch.gradle.tasks.download.DownloadAction
import org.gradle.api.Project

class DownloadEclipseBootstrapper {
    static final String eclipseZipName = "Eclipse.zip"
//    static final String eclipseZipName = "eclipse-platform-4.7.2-win32-x86_64.zip"
//    static final String tpUrl = "https://ftp.heanet.ie/mirrors/eclipse/eclipse/downloads/drops4/R-4.7.2-201711300510/" + eclipseZipName

    private DownloadEclipseBootstrapper() {
    }

    private enum EBootstrapper {
        LINUX_X86('linux_x86', 'eclipse'),
        LINUX_X86_64('linux_x86_64', 'eclipse'),
        MACOSX('macosx', 'eclipse'),
        WIN_X86('win_x86', 'eclipsec.exe'),
        WIN_X86_64('win_x86_64', 'eclipsec.exe')

        String subDir
        String eclipseExe
        EBootstrapper(String subDir, String eclipseExe) {
            this.subDir = subDir
            this.eclipseExe = eclipseExe
        }

        static EBootstrapper getCurrent() {
            switch (OperatingSystem.OS) {
                case OperatingSystem.Os.WINDOWS:
                    switch (OperatingSystem.ARCH) {
                        case OperatingSystem.Arch.X86:
                            return WIN_X86
                            break
                        case OperatingSystem.Arch.X86_64:
                            return WIN_X86_64
                            break
                    }
                    break
                case OperatingSystem.Os.LINUX:
                    switch (OperatingSystem.ARCH) {
                        case OperatingSystem.Arch.X86:
                            return LINUX_X86
                            break
                        case OperatingSystem.Arch.X86_64:
                            return LINUX_X86_64
                            break
                    }
                    break
                case OperatingSystem.Os.MACOSX:
                    switch (OperatingSystem.ARCH) {
                        case OperatingSystem.Arch.X86:
                            break
                        case OperatingSystem.Arch.X86_64:
                            return MACOSX
                            break
                    }
                    break
            }
            throw new IllegalStateException("No bootstrapper found for: OS=${OperatingSystem.OS}, WS=${OperatingSystem.WS}, ARCH=${OperatingSystem.ARCH}")
        }
    }

    static synchronized File downloadIfMissing(Project project) {
        EBootstrapper current = EBootstrapper.current
        def bootstrapperUrl
        if (project.hasProperty('EclipseP2BootstrapperBaseUrl')) {
            bootstrapperUrl = project.property('EclipseP2BootstrapperBaseUrl')
        } else {
            bootstrapperUrl = "https://github.com/topo75/EclipseP2Director/raw/${VersionInfo.version}/Bootstrapper/bootstrappers"
        }

        String downloadUrl = "${bootstrapperUrl}/${current.subDir}/${eclipseZipName}"
        File userDir = new File(System.getProperty('user.home'))
        File eclipseP2BaseDir = new File(userDir, ".eclipsep2")
        File bootstrapBaseDir = new File(eclipseP2BaseDir, 'bootstrap')
        File bootstrapDir = new File(bootstrapBaseDir, project.rootProject.version.toString())
        File eclipseZipFile = new File(new File(bootstrapDir, current.subDir), eclipseZipName)
        File eclipseDir = new File(new File(bootstrapDir, current.subDir), "Eclipse")
        File eclipseExe = new File(eclipseDir, current.eclipseExe)

        long lastTimestamp = 0
        if (eclipseZipFile.exists()) lastTimestamp = eclipseZipFile.lastModified()

        def download = new DownloadAction(project)

        download.with {
            src downloadUrl
            dest eclipseZipFile
            onlyIfModified eclipseExe.exists()
            overwrite false
            quiet false
        }

        download.execute()

        if (lastTimestamp != eclipseZipFile.lastModified()) {
            project.logger.info("Eclipse ${eclipseZipFile} was downloaded from ${downloadUrl}")
            def ant = new AntBuilder()
            ant.unzip(src: eclipseZipFile, dest: eclipseDir, overwrite:true)
        }

        eclipseExe
    }
}
