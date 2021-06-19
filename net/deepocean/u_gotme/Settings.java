// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.TimeZone;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import hirondelle.date4j.DateTime;

public class Settings
{
    private static Settings theInstance;
    private String propertyFileName;
    private boolean debugging;
    private int debugLevel;
    private String defaultComport;
    private String defaultGpxPath;
    private String defaultLogPath;
    private String outputFileType;
    private String gpxVersion;
    private String mapType;
    private String operationMode;
    private String simulationPath;
    private int segmentSeparationSeconds;
    private String profileGender;
    private double profileWeight;
    private double profileLength;
    private DateTime profileDateOfBirth;
    private double metabolicEquivalent;
    private boolean trackCaching;
    private String comportLib;
    private String fontsize;
    
    private Settings() {
        this.propertyFileName = "u-gotme.properties";
        this.readSettings();
    }
    
    private void readSettings() {
        this.defaultComport = null;
        this.gpxVersion = null;
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(this.propertyFileName));
            String setting = properties.getProperty("debugLevel");
            this.setDebugLevel(setting);
            DebugLogger.setDebugLevel(this.debugLevel);
            setting = properties.getProperty("comport");
            this.setComport(setting);
            setting = properties.getProperty("outputFileType");
            this.setOutputFileType(setting);
            setting = properties.getProperty("gpxVersion");
            this.setGpxVersion(setting);
            setting = properties.getProperty("gpxPath");
            this.setGpxPath(setting);
            setting = properties.getProperty("logPath");
            this.setLogPath(setting);
            setting = properties.getProperty("mapType");
            this.setMapType(setting);
            setting = properties.getProperty("segmentSeparation");
            this.setSegmentSeparationLimit(setting);
            setting = properties.getProperty("operationMode");
            this.setOperationMode(setting);
            setting = properties.getProperty("simulationPath");
            this.setSimulationPath(setting);
            setting = properties.getProperty("debugging");
            this.setDebugging(setting);
            setting = properties.getProperty("profileGender");
            this.setProfileGender(setting);
            setting = properties.getProperty("profileWeight");
            this.setProfileWeight(setting);
            setting = properties.getProperty("profileLength");
            this.setProfileLength(setting);
            setting = properties.getProperty("profileDateOfBirth");
            this.setProfileDateOfBirth(setting);
            setting = properties.getProperty("metabolicEquivalent");
            this.setMetabolicEquivalent(setting);
            setting = properties.getProperty("trackCaching");
            this.setTrackCaching(setting);
            setting = properties.getProperty("comportLib");
            this.setComportLib(setting);
            setting = properties.getProperty("routepointFontsize");
            this.setRoutepointFontsize(setting);
            DebugLogger.info("Settings read");
            this.dumpSettings();
        }
        catch (IOException e) {
            DebugLogger.error("Error reading settings from " + this.propertyFileName);
            this.debugLevel = 2;
            this.defaultComport = "";
            this.defaultGpxPath = "";
            this.defaultLogPath = "";
            this.outputFileType = "GPX";
            this.gpxVersion = "1.1";
            this.mapType = "roadmap";
            this.segmentSeparationSeconds = 0;
            this.operationMode = "normal";
            this.simulationPath = "";
            this.profileWeight = 75.0;
            this.profileGender = "male";
            this.profileLength = 175.0;
            this.profileDateOfBirth = new DateTime("2000-01-01");
            this.metabolicEquivalent = 2.0;
            this.trackCaching = false;
            this.comportLib = "rxtx";
            this.fontsize = "medium";
        }
    }
    
    public void writeSettings() {
        final Properties properties = new Properties();
        try {
            properties.setProperty("debugging", Boolean.toString(this.debugging));
            properties.setProperty("debugLevel", DebugLogger.debugLevelToString(this.debugLevel));
            properties.setProperty("trackCaching", Boolean.toString(this.trackCaching));
            properties.setProperty("gpxPath", this.defaultGpxPath);
            properties.setProperty("logPath", this.defaultLogPath);
            properties.setProperty("comport", this.defaultComport);
            properties.setProperty("outputFileType", this.outputFileType);
            properties.setProperty("gpxVersion", this.gpxVersion);
            properties.setProperty("mapType", this.mapType);
            properties.setProperty("segmentSeparation", Integer.toString(this.segmentSeparationSeconds));
            properties.setProperty("operationMode", this.operationMode);
            properties.setProperty("simulationPath", this.simulationPath);
            properties.setProperty("profileWeight", Double.toString(this.profileWeight));
            properties.setProperty("profileLength", Double.toString(this.profileLength));
            properties.setProperty("profileGender", this.profileGender);
            properties.setProperty("profileDateOfBirth", this.profileDateOfBirth.format("YYYY-MM-DD"));
            properties.setProperty("metabolicEquivalent", Double.toString(this.metabolicEquivalent));
            properties.setProperty("comportLib", this.comportLib);
            properties.setProperty("routepointFontsize", this.fontsize);
            properties.store(new FileOutputStream(this.propertyFileName), "");
            DebugLogger.info("Settings written");
            this.dumpSettings();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing properties file " + this.propertyFileName);
        }
    }
    
    private void dumpSettings() {
        DebugLogger.info("SETTINGS FROM PROPERTY FILE");
        DebugLogger.info("Setting default comport  (comport)            : " + this.defaultComport);
        DebugLogger.info("Setting default GPX path (gpxPath)            : " + this.defaultGpxPath);
        DebugLogger.info("Setting default log path (logPath)            : " + this.defaultLogPath);
        DebugLogger.info("Output file preference   (outputFileType)     : " + this.outputFileType);
        DebugLogger.info("Setting GPX Version      (gpxVersion)         : " + this.gpxVersion);
        DebugLogger.info("Debugging                (debugging)          : " + this.debugging);
        DebugLogger.info("Track caching to file    (trackCaching)       : " + this.trackCaching);
        DebugLogger.info("Setting debug level      (debugLevel)         : " + DebugLogger.debugLevelToString(this.debugLevel));
        DebugLogger.info("Setting map type         (mapType)            : " + this.mapType);
        DebugLogger.info("Segment separation (sec) (segmentSeparation)  : " + this.segmentSeparationSeconds);
        DebugLogger.info("Mode of operation        (operationMode)      : " + this.operationMode);
        DebugLogger.info("Simulation path          (simulationPath)     : " + this.simulationPath);
        DebugLogger.info("Profile date-of-birth    (profileDateOfBirth) : " + this.profileDateOfBirth.format("YYYY-MM-DD") + " (" + this.getProfileAge() + ")");
        DebugLogger.info("Profile gender           (profileGender)      : " + this.profileGender);
        DebugLogger.info("Profile weight           (profileWeight)      : " + this.profileWeight);
        DebugLogger.info("Profile length           (profileLength)      : " + this.profileLength);
        DebugLogger.info("Metabolic Equivalent     (metabolicEquivalent): " + this.metabolicEquivalent);
        DebugLogger.info("Comport library          (comportLib)         : " + this.comportLib);
        DebugLogger.info("Route point fontsize     (routepointFontsize) : " + this.fontsize);
    }
    
    public String getPropertyFileName() {
        return this.propertyFileName;
    }
    
    public static Settings getInstance() {
        if (Settings.theInstance == null) {
            Settings.theInstance = new Settings();
        }
        return Settings.theInstance;
    }
    
    public int getDebugLevel() {
        return this.debugLevel;
    }
    
    public void setDebugLevel(final int debugLevel) {
        this.debugLevel = debugLevel;
    }
    
    public void setDebugLevel(final String debugLevel) {
        this.debugLevel = 2;
        if (debugLevel != null) {
            if (debugLevel.toLowerCase().equals("off")) {
                this.debugLevel = 3;
            }
            else if (debugLevel.toLowerCase().equals("debug")) {
                this.debugLevel = 0;
            }
            else if (debugLevel.toLowerCase().equals("info")) {
                this.debugLevel = 1;
            }
            else if (debugLevel.toLowerCase().equals("error")) {
                this.debugLevel = 2;
            }
        }
    }
    
    public String getGpxPath() {
        return this.defaultGpxPath;
    }
    
    public void setGpxPath(final String newPath) {
        if (newPath != null) {
            this.defaultGpxPath = newPath;
        }
        else {
            this.defaultGpxPath = "";
        }
    }
    
    public String getLogPath() {
        return this.defaultLogPath;
    }
    
    public void setLogPath(final String newPath) {
        if (newPath != null) {
            this.defaultLogPath = newPath;
        }
        else {
            this.defaultLogPath = "";
        }
        if (!this.defaultLogPath.equals("") && !this.defaultLogPath.endsWith("/") && !this.defaultLogPath.endsWith("\\")) {
            this.defaultLogPath += "/";
        }
    }
    
    public void setOutputFileType(final String fileType) {
        if (fileType != null) {
            if (fileType.toUpperCase().equals("TCX")) {
                this.outputFileType = "TCX";
            }
            else if (fileType.toUpperCase().equals("CSV")) {
                this.outputFileType = "CSV";
            }
            else {
                this.outputFileType = "GPX";
            }
        }
        else {
            this.outputFileType = "GPX";
        }
    }
    
    public String getOutputFileType() {
        return this.outputFileType;
    }
    
    public String getGpxVersion() {
        return this.gpxVersion;
    }
    
    public void setGpxVersion(final String newVersion) {
        this.gpxVersion = "1.1";
        if (newVersion != null && newVersion.equals("1.0")) {
            this.gpxVersion = "1.0";
        }
    }
    
    public String getComport() {
        return this.defaultComport;
    }
    
    public void setComport(final String newComport) {
        this.defaultComport = "";
        if (newComport != null) {
            this.defaultComport = newComport;
        }
    }
    
    public String getMapType() {
        return this.mapType;
    }
    
    public void setMapType(final String newMapType) {
        this.mapType = "roadmap";
        if (newMapType != null) {
            this.mapType = newMapType;
        }
    }
    
    public int getSegmentSeparationLimit() {
        return this.segmentSeparationSeconds;
    }
    
    public void setSegmentSeparationLimit(final int newLimit) {
        this.segmentSeparationSeconds = newLimit;
    }
    
    public void setSegmentSeparationLimit(final String newLimit) {
        if (newLimit != null) {
            this.segmentSeparationSeconds = Integer.parseInt(newLimit);
        }
    }
    
    private void setSimulationPath(final String newSimulation) {
        this.simulationPath = "";
        if (newSimulation != null) {
            this.simulationPath = newSimulation;
        }
    }
    
    public String getSimulationPath() {
        return this.simulationPath;
    }
    
    public void setDebugging(final String newState) {
        this.debugging = false;
        if (newState != null && newState.equals("true")) {
            this.debugging = true;
        }
    }
    
    public boolean getDebugging() {
        return this.debugging;
    }
    
    public void setTrackCaching(final String newState) {
        this.trackCaching = false;
        if (newState != null && newState.equals("true")) {
            this.trackCaching = true;
        }
    }
    
    public boolean getTrackCaching() {
        return this.trackCaching;
    }
    
    public String getOperationMode() {
        return this.operationMode;
    }
    
    public void setOperationMode(final String newOperationMode) {
        this.operationMode = "normal";
        if (newOperationMode != null) {
            this.operationMode = newOperationMode;
        }
    }
    
    public boolean isSimulationMode() {
        return this.operationMode.equals("simulation");
    }
    
    public void setProfileGender(final String gender) {
        if (gender != null) {
            if (gender.toLowerCase().equals("female")) {
                this.profileGender = "female";
            }
            else {
                this.profileGender = "male";
            }
        }
        else {
            this.profileGender = "male";
        }
    }
    
    public String getProfileGender() {
        return this.profileGender;
    }
    
    public void setProfileDateOfBirth(final String dob) {
        if (dob != null) {
            this.profileDateOfBirth = new DateTime(dob);
        }
        else {
            this.profileDateOfBirth = new DateTime("2000-01-01");
        }
    }
    
    public DateTime getProfileDateOfBirth() {
        return this.profileDateOfBirth;
    }
    
    public int getProfileAge() {
        final DateTime now = DateTime.now(TimeZone.getDefault());
        int age = now.getYear() - this.profileDateOfBirth.getYear();
        if (this.profileDateOfBirth.getMonth() > now.getMonth() || (this.profileDateOfBirth.getMonth() == now.getMonth() && this.profileDateOfBirth.getDay() > now.getDay())) {
            --age;
        }
        return age;
    }
    
    public void setProfileWeight(final String weight) {
        if (weight != null) {
            this.profileWeight = Double.valueOf(weight);
        }
        else {
            this.profileWeight = 75.0;
        }
    }
    
    public void setProfileWeight(final double newWeight) {
        this.profileWeight = newWeight;
    }
    
    public double getProfileWeight() {
        return this.profileWeight;
    }
    
    public void setProfileLength(final String length) {
        if (length != null) {
            this.profileLength = Double.valueOf(length);
        }
        else {
            this.profileLength = 175.0;
        }
    }
    
    public void setProfileLength(final double newLength) {
        this.profileLength = newLength;
    }
    
    public double getProfileLength() {
        return this.profileLength;
    }
    
    public void setMetabolicEquivalent(final String equivalent) {
        if (equivalent != null) {
            this.metabolicEquivalent = Double.valueOf(equivalent);
        }
        else {
            this.metabolicEquivalent = 2.0;
        }
    }
    
    public double getMetabolicEquivalent() {
        return this.metabolicEquivalent;
    }
    
    public void setComportLib(final String lib) {
        if (lib != null) {
            if (lib.toLowerCase().equals("purejava")) {
                this.comportLib = "purejava";
            }
            else if (lib.toLowerCase().equals("jssc")) {
                this.comportLib = "jssc";
            }
            else {
                this.comportLib = "rxtx";
            }
        }
        else {
            this.comportLib = "rxtx";
        }
    }
    
    public String getComportLib() {
        return this.comportLib;
    }
    
    public void setRoutepointFontsize(final String fontsize) {
        if (fontsize != null) {
            if (fontsize.toLowerCase().equals("small")) {
                this.fontsize = "small";
            }
            else if (fontsize.toLowerCase().equals("large")) {
                this.fontsize = "large";
            }
            else {
                this.fontsize = "medium";
            }
        }
        else {
            this.fontsize = "medium";
        }
    }
    
    public String getRoutepointFontsize() {
        return this.fontsize;
    }
    
    static {
        Settings.theInstance = null;
    }
}
