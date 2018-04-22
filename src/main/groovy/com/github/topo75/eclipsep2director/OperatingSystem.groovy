package com.github.topo75.eclipsep2director

import static org.apache.tools.ant.taskdefs.condition.Os.*

class OperatingSystem {
    enum Os {
        LINUX("linux"), MACOSX("macosx"), WINDOWS("win32")

        String p2
        Os(String p2) {
            this.p2 = p2
        }

        String getP2() {
            p2
        }

        private static Os current() {
            if (isFamily(FAMILY_MAC)) {
                MACOSX
            } else if (isFamily(FAMILY_WINDOWS)) {
                WINDOWS
            } else if (isFamily(FAMILY_UNIX)) {
                LINUX
            } else {
                throw new IllegalStateException("Unsupported OS: {OS_NAME}")
            }
        }
    }

    enum Arch {
        X86("x86"), X86_64("x86_64")

        String p2
        Arch(String p2) {
            this.p2 = p2
        }

        String getP2() {
            p2
        }

        private static Arch current() {
            if ('amd64'.equalsIgnoreCase(OS_ARCH)) {
                X86_64
            } else if ('x86'.equalsIgnoreCase(OS_ARCH)) {
                X86
            } else {
                throw new IllegalStateException("Unsupported architecture: {OS_ARCH}")
            }
        }
    }

    enum Ws {
        GTK("gtk"), COCOA("cocoa"), WINDOWS("win32")

        String p2
        Ws(String p2) {
            this.p2 = p2
        }

        String getP2() {
            p2
        }

        private static Ws current() {
            if (isFamily(FAMILY_MAC)) {
                COCOA
            } else if (isFamily(FAMILY_WINDOWS)) {
                WINDOWS
            } else if (isFamily(FAMILY_UNIX)) {
                GTK
            } else {
                throw new IllegalStateException("Unsupported WS: {OS_NAME}")
            }
        }
    }

    static final Os OS = Os.current()
    static final Arch ARCH = Arch.current()
    static final Ws WS = Ws.current()

}
