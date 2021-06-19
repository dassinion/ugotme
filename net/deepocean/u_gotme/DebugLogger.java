// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import hirondelle.date4j.DateTime;
import java.util.TimeZone;

public class DebugLogger
{
    public static final int DEBUGLEVEL_DEBUG = 0;
    public static final int DEBUGLEVEL_INFO = 1;
    public static final int DEBUGLEVEL_ERROR = 2;
    public static final int DEBUGLEVEL_OFF = 3;
    private static int debugLevel;
    
    private DebugLogger() {
    }
    
    public static void setDebugLevel(final int newDebugLevel) {
        if (newDebugLevel >= 0 && newDebugLevel <= 3) {
            DebugLogger.debugLevel = newDebugLevel;
        }
    }
    
    public static int getDebugLevel() {
        return DebugLogger.debugLevel;
    }
    
    public static void debug(final String info) {
        if (DebugLogger.debugLevel <= 0) {
            final DateTime time = DateTime.now(TimeZone.getDefault());
            System.out.println("d " + time.format("YYYY-MM-DD hh:mm:ss  ") + info);
        }
    }
    
    public static void info(final String info) {
        if (DebugLogger.debugLevel <= 1) {
            final DateTime time = DateTime.now(TimeZone.getDefault());
            System.out.println("i " + time.format("YYYY-MM-DD hh:mm:ss  ") + info);
        }
    }
    
    public static void error(final String info) {
        if (DebugLogger.debugLevel <= 2) {
            final DateTime time = DateTime.now(TimeZone.getDefault());
            System.err.println("e " + time.format("YYYY-MM-DD hh:mm:ss  ERROR: ") + info);
        }
    }
    
    public static String debugLevelToString(final int debugLevel) {
        String returnString = "unknown";
        if (debugLevel == 3) {
            returnString = "off";
        }
        else if (debugLevel == 0) {
            returnString = "debug";
        }
        else if (debugLevel == 1) {
            returnString = "info";
        }
        else if (debugLevel == 2) {
            returnString = "error";
        }
        return returnString;
    }
    
    public static String debugLevelToString() {
        String returnString = "unknown";
        if (DebugLogger.debugLevel == 3) {
            returnString = "off";
        }
        else if (DebugLogger.debugLevel == 0) {
            returnString = "debug";
        }
        else if (DebugLogger.debugLevel == 1) {
            returnString = "info";
        }
        else if (DebugLogger.debugLevel == 2) {
            returnString = "error";
        }
        return returnString;
    }
    
    static {
        DebugLogger.debugLevel = 2;
    }
}
