package com.tundrey.lightspeed.util;

public class CompatUtil {
    public static boolean existsClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}