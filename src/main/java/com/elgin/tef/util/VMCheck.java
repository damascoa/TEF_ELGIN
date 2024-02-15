package com.elgin.tef.util;

public class VMCheck {

    public static String checkJvmVersion() {
        String architecture = System.getProperty("os.arch");
        String bitVersion = architecture.contains("64") ? "64" : "32";
        return bitVersion;
    }
}
