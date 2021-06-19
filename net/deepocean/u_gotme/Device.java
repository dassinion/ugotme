// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class Device
{
    static Device theInstance;
    private final Connection connection;
    private final byte[] writeData;
    private final byte[] responseHeader;
    private final byte[] responseData;
    private int trackRecordCount;
    private int logInterval;
    private ModelType modelType;
    private DeviceType deviceType;
    private int serialNumber;
    private int usbVersion;
    private int softwareVersionMajor;
    private int softwareVersionMinor;
    private boolean isError;
    private MemoryMap memMap;
    private boolean isCaching;
    private TrackMemoryCache cache;
    
    private Device() {
        final Settings settings = Settings.getInstance();
        if (settings.isSimulationMode()) {
            final String path = settings.getSimulationPath();
            this.connection = new ConnectionSimulation(path);
        }
        else if (settings.getComportLib().equals("jssc")) {
            DebugLogger.info("Using JSSC comport lib");
            this.connection = new ConnectionSerialJssc();
        }
        else if (settings.getComportLib().equals("purejava")) {
            DebugLogger.info("Using Purejava comport lib");
            this.connection = new ConnectionSerialPurejava();
        }
        else {
            DebugLogger.info("Using RXTX comport lib");
            this.connection = new ConnectionSerialRxtx();
        }
        this.responseHeader = this.connection.getResponseHeader();
        this.responseData = this.connection.getResponseData();
        this.writeData = new byte[4096];
        this.modelType = ModelType.MODELTYPE_UNKNOWN;
        this.deviceType = DeviceType.DEVICETYPE_UNKNOWN;
        this.serialNumber = 0;
        this.usbVersion = 0;
        this.logInterval = -1;
        this.memMap = new MemoryMap();
        this.isCaching = settings.getTrackCaching();
        this.cache = null;
    }
    
    public Connection getConnection() {
        return this.connection;
    }
    
    public static Device getInstance() {
        if (Device.theInstance == null) {
            Device.theInstance = new Device();
        }
        return Device.theInstance;
    }
    
    public void open(final String comport) {
        this.connection.open(comport);
    }
    
    public void close() {
        this.connection.close();
    }
    
    String commandGetIdentification() {
        DebugLogger.info("* Getting device identfication");
        final int dataLength = this.connection.commandGetIdentification();
        String outputString;
        if (dataLength == 10) {
            this.serialNumber = (this.responseData[0] & 0xFF) + ((this.responseData[1] & 0xFF) << 8) + ((this.responseData[2] & 0xFF) << 16) + ((this.responseData[3] & 0xFF) << 24);
            this.usbVersion = (this.responseData[8] & 0xFF) + ((this.responseData[9] & 0xFF) << 8);
            this.softwareVersionMajor = (this.responseData[4] & 0xFF);
            this.softwareVersionMinor = (this.responseData[5] & 0xFF);
            outputString = "Serial: " + this.serialNumber + " Version: " + this.softwareVersionMajor + "." + String.format("%02d", this.softwareVersionMinor) + " Model: " + (((this.responseData[6] & 0xFF) << 8) + (this.responseData[7] & 0xFF)) + " USB lib version: " + this.usbVersion + "\n";
        }
        else {
            outputString = "Response not as expected\n";
        }
        return outputString;
    }
    
    String commandGetModel() {
        this.isError = false;
        DebugLogger.info("* Getting device model info");
        final int dataLength = this.connection.commandGetModel();
        String outputString;
        if (dataLength == 3) {
            final int model;
            final int deviceId = model = this.responseData[2];
            final int unknown = (this.responseData[0] & 0xFF) + ((this.responseData[1] & 0xFF) << 8);
            outputString = "Device type: " + deviceId + " - ";
            switch (deviceId) {
                case 21: {
                    this.modelType = ModelType.MODELTYPE_GT120;
                    outputString += "GT-120";
                    break;
                }
                case 23: {
                    this.modelType = ModelType.MODELTYPE_GT800PLUS;
                    outputString += "GT-800, GT-820 or GT-900 (Pro)";
                    break;
                }
                default: {
                    this.modelType = ModelType.MODELTYPE_UNKNOWN;
                    outputString += "unknown";
                    break;
                }
            }
            outputString += "\n";
        }
        else {
            this.isError = true;
            outputString = "Response not as expected\n";
        }
        return outputString;
    }
    
    String commandGetModel2() {
        String outputString;
        this.isError = false;
        DebugLogger.info("* Getting device model info 2");
        final int dataLength = this.connection.commandGetModel2();
        outputString = "Data Length: " + dataLength + "\n";
        outputString += byteArrayToHex(this.responseData, dataLength);
        DebugLogger.info(outputString);
        return outputString;
    }

    String commandNmeaSwitch(final byte mode) {
        this.isError = false;
        DebugLogger.info("* Setting NMEA switch mode to " + mode);
        String outputString;
        if (mode == 0 || mode == 1 || mode == 3) {
            final int dataLength = this.connection.commandNmeaSwitch(mode);
            if (dataLength == 0) {
                outputString = "Switched to mode " + mode;
            }
            else {
                outputString = "Response not as expected\n";
                this.isError = true;
            }
        }
        else {
            outputString = "Invalid nmea mode: " + mode;
        }
        return outputString;
    }
    
    public String commandGetCount() {
        this.isError = false;
        final int maxTrackPoints = this.memMap.getTracksMaxRecords();
        DebugLogger.info("* Getting number of logged datapoints from device");
        final int dataLength = this.connection.commmandCount();
        String outputString;
        if (dataLength == 3) {
            final int count = ((this.responseData[0] & 0xFF) << 16) + ((this.responseData[1] & 0xFF) << 8) + (this.responseData[2] & 0xFF);
            outputString = "Number of datapoints: " + count + " out of " + maxTrackPoints + " (" + 100 * (maxTrackPoints - count) / maxTrackPoints + "% free";
            if (this.logInterval > 0) {
                outputString = outputString + ", " + (maxTrackPoints - count) * this.logInterval / 3600 + " hours of logging left @ ";
                if (this.logInterval >= 60) {
                    outputString = outputString + this.logInterval / 60 + " min log interval";
                }
                else {
                    outputString = outputString + this.logInterval + " sec log interval";
                }
            }
            outputString += ")\n";
            this.trackRecordCount = count;
        }
        else {
            outputString = "Response not as expected\n";
            this.trackRecordCount = 0;
            this.isError = true;
        }
        return outputString;
    }
    
    private void commandReadFlash(final int readPosition, final int size) {
        DebugLogger.info("* Read flash at 0x" + Integer.toHexString(readPosition) + ", 0x" + Integer.toHexString(size) + " bytes");
        final int dataLength = this.connection.commandReadFlash(readPosition, size);
        if (dataLength == size) {
            final int bytesRead = this.responseHeader[1] * 256 + this.responseHeader[2];
            this.isError = false;
        }
        else {
            this.isError = true;
            DebugLogger.error("Flash read error.");
        }
    }
    
    private void commandWriteFlash(final int writePosition, final int size) {
        DebugLogger.info("* Writing " + Integer.toHexString(size) + " bytes to flash at " + Integer.toHexString(writePosition));
        this.isError = false;
        this.isError = this.connection.commandWriteFlash(writePosition, size, this.writeData);
    }
    
    private void commandEraseFlash(final int erasePosition) {
        DebugLogger.info("* Erasing address: 0x" + Integer.toHexString(erasePosition));
        this.isError = false;
        if (!(this.isError = this.connection.commandEraseFlash(erasePosition))) {
            final String outputString = "Block erased at 0x" + Integer.toHexString(erasePosition);
            DebugLogger.info(outputString);
        }
        else {
            final String outputString = "Error erasing block at 0x" + Integer.toHexString(erasePosition);
            DebugLogger.error(outputString);
        }
    }
    
    boolean isErasedBlock(final int block) {
        DebugLogger.info("Checking erased state of block : 0x" + Integer.toHexString(block));
        boolean isErased = true;
        final int startAddress = this.memMap.getFlashStartAddress();
        final int firstBlock = this.memMap.getTracksFirstPage();
        final int lastBlock = this.memMap.getTracksLastPage();
        if (block >= firstBlock && block <= lastBlock) {
            this.commandReadFlash(startAddress + block * 4096, 16);
            final int firstWord = (this.responseData[0] & 0xFF) << 24 | (this.responseData[1] & 0xFF) << 16 | (this.responseData[2] & 0xFF) << 8 | (this.responseData[3] & 0xFF);
            final int secondWord = (this.responseData[4] & 0xFF) << 24 | (this.responseData[5] & 0xFF) << 16 | (this.responseData[6] & 0xFF) << 8 | (this.responseData[7] & 0xFF);
            isErased = false;
            if (firstWord == -1 && secondWord == -1) {
                isErased = true;
            }
            if ((firstWord == -1506996447 && secondWord == -1342501102) || (firstWord == -1918028639 && secondWord == -1775431968) || (firstWord == -1132750925 && secondWord == -87278574) || (firstWord == -1120179757 && secondWord == -544529440)) {
                isErased = true;
            }
            if ((firstWord == 354338662 && secondWord == 1561947338) || (firstWord == 503303852 && secondWord == 1561947386) || (firstWord == 503303868 && secondWord == 1561947370) || (firstWord == 1126085701 && secondWord == -436454944) || (firstWord == 1369353735 && secondWord == 1561947338) || (firstWord == -956159471 && secondWord == -947519466) || (firstWord == -956159471 && secondWord == -947519458) || (firstWord == 781886754 && secondWord == -947519482) || (firstWord == 781886754 && secondWord == -947519458) || (firstWord == 781886754 && secondWord == -947519466) || (firstWord == 781886754 && secondWord == 135388913) || (firstWord == 1451445904 && secondWord == -444969632) || (firstWord == 1451445912 && secondWord == -444969632) || (firstWord == 1451445888 && secondWord == -444969632) || (firstWord == 1409528385 && secondWord == -444969632) || (firstWord == -157562813 && secondWord == -436454944)) {
                isErased = true;
            }
        }
        else {
            DebugLogger.error("Trying to check erased state of illegal block: " + block);
        }
        return isErased;
    }
    
    private boolean verifyBlock(final int size) {
        boolean ok = true;
        for (int i = 0; i < size && ok; ++i) {
            if (this.writeData[i] != this.responseData[i]) {
                ok = false;
            }
        }
        if (ok) {
            DebugLogger.info("Verification OK");
        }
        else {
            DebugLogger.error("Verification failed");
        }
        return ok;
    }
    
    private void writeBlock(final int address, final int size) {
        int writeRetryCount;
        boolean verificationOk;
        for (writeRetryCount = 0, verificationOk = false; !verificationOk && writeRetryCount < 3; ++writeRetryCount) {
            this.commandWriteFlash(address, size);
            this.commandReadFlash(address, size);
            verificationOk = this.verifyBlock(4096);
            if (!verificationOk) {
                DebugLogger.error("Verification failed while writeBlock " + address);
            }
        }
        if (!verificationOk) {
            this.isError = true;
        }
    }
    
    private void getSettingsFromDevice() {
        this.commandReadFlash(this.memMap.getSettingsStartAddress(), 4096);
        if (!this.isError) {
            if (this.deviceType == DeviceType.DEVICETYPE_GT800 || this.deviceType == DeviceType.DEVICETYPE_GT800PRO || this.deviceType == DeviceType.DEVICETYPE_GT820PRO || this.deviceType == DeviceType.DEVICETYPE_GT900PRO) {
                this.logInterval = (this.responseData[4] & 0xFF) + ((this.responseData[5] & 0xFF) << 8);
            }
            else {
                this.logInterval = -1;
            }
        }
    }
    
    public String getDeviceType(final ProgressListener listener) {
        this.commandNmeaSwitch((byte)3);
        if (listener != null) {
            listener.reportProgress(25);
        }
        this.commandGetModel();
        if (listener != null) {
            listener.reportProgress(50);
        }
        if (!this.isError) {
            this.commandGetIdentification();
        }
        if (listener != null) {
            listener.reportProgress(75);
        }
        String outputString;
        if (!this.isError) {
            if (this.modelType == ModelType.MODELTYPE_GT800PLUS) {
                switch (this.softwareVersionMajor) {
                    case 6: {
                        this.deviceType = DeviceType.DEVICETYPE_GT800;
                        this.memMap = new MemoryMapDefault();
                        break;
                    }
                    case 7: {
                        this.deviceType = DeviceType.DEVICETYPE_GT800PRO;
                        this.memMap = new MemoryMapDefault();
                        break;
                    }
                    case 10: {
                        this.deviceType = DeviceType.DEVICETYPE_GT900;
                        this.memMap = new MemoryMapDefault();
                        break;
                    }
                    case 9: {
                        this.deviceType = DeviceType.DEVICETYPE_GT900PRO;
                        this.memMap = new MemoryMapDefault();
                        break;
                    }
                    case 12: {
                        this.deviceType = DeviceType.DEVICETYPE_GT820;
                        this.memMap = new MemoryMapDefault();
                        break;
                    }
                    case 11: {
                        this.deviceType = DeviceType.DEVICETYPE_GT820PRO;
                        this.memMap = new MemoryMapDefault();
                        break;
                    }
                }
            }
            else if (this.modelType == ModelType.MODELTYPE_GT120) {
                this.deviceType = DeviceType.DEVICETYPE_GT120;
                this.memMap = new MemoryMapGT120();
            }
            outputString = "Device type established: " + getDeviceTypeDescription(this.deviceType) + "\n";
        }
        else {
            outputString = "Error establishing device type";
        }
        if (listener != null) {
            listener.reportProgress(99);
        }
        return outputString;
    }
    
    String downloadTracks(final ProgressListener listener) {
        DebugLogger.info("* Downloading tracks");
        this.isError = false;
        this.getDeviceType(null);
        final int recordsPerBlock = 128;
        final int firstPage = this.memMap.getTracksFirstPage();
        final int lastPage = this.memMap.getTracksLastPage();
        if (this.isCaching) {
            boolean doInitCache = false;
            if (this.cache == null) {
                doInitCache = true;
            }
            else if (!this.cache.checkCache(getDeviceTypeDescription(this.deviceType), this.serialNumber)) {
                doInitCache = true;
            }
            if (doInitCache) {
                this.cache = new TrackMemoryCache(firstPage, lastPage, 4096, getDeviceTypeDescription(this.deviceType), this.serialNumber);
            }
        }
        String outputString;
        if (!this.isErasedBlock(firstPage) && !this.isError) {
            final TrackLog trackLog = TrackLog.getInstance();
            final DeviceLog deviceLog = DeviceLog.getInstance();
            trackLog.initialiseTracklog();
            deviceLog.clear();
            trackLog.setDeviceType(this.deviceType);
            this.commandGetCount();
            int totalBlockCount = 0;
            int numberOfBlocks;
            if (this.trackRecordCount % recordsPerBlock == 0) {
                numberOfBlocks = this.trackRecordCount / recordsPerBlock;
            }
            else {
                numberOfBlocks = this.trackRecordCount / recordsPerBlock + 1;
            }
            final int mostRecentBlock = firstPage + numberOfBlocks - 1;
            final int firstEmptyBlock = firstPage + numberOfBlocks;
            int blocksToRead;
            if (firstEmptyBlock <= lastPage && !this.isErasedBlock(firstEmptyBlock) && !this.isError) {
                blocksToRead = this.memMap.getTracksPages();
                if (this.isCaching) {
                    int oldestPage;
                    if (firstEmptyBlock > lastPage) {
                        oldestPage = firstPage;
                        DebugLogger.error("Error validating cache. This should not happen");
                    }
                    else {
                        oldestPage = firstEmptyBlock;
                    }
                    this.commandReadFlash(4096 * oldestPage, 16);
                    this.cache.validateCache(this.responseData, oldestPage);
                }
                int block = firstEmptyBlock;
                DebugLogger.info("Track log wrapped around. Reading flash page 0x" + Integer.toHexString(block) + " to 0x" + Integer.toHexString(lastPage));
                while (block <= lastPage && !this.isError) {
                    if (this.isCaching) {
                        if (!this.cache.getBlock(this.responseData, block, mostRecentBlock)) {
                            this.commandReadFlash(4096 * block, 4096);
                            if (!this.isError) {
                                this.cache.writeBlock(block, this.responseData);
                            }
                        }
                    }
                    else {
                        this.commandReadFlash(4096 * block, 4096);
                    }
                    if (!this.isError) {
                        trackLog.appendData(this.responseData, 4096);
                        deviceLog.appendData(this.responseData, 4096);
                        listener.reportProgress(99 * totalBlockCount / blocksToRead);
                        ++block;
                        ++totalBlockCount;
                    }
                }
            }
            else {
                blocksToRead = numberOfBlocks;
                if (this.isCaching) {
                    final int oldestPage = firstPage;
                    this.commandReadFlash(4096 * oldestPage, 16);
                    this.cache.validateCache(this.responseData, oldestPage);
                }
            }
            int block = firstPage;
            int recordsLeft = this.trackRecordCount;
            DebugLogger.info("Reading flash page 0x" + Integer.toHexString(block) + " to 0x" + Integer.toHexString(firstEmptyBlock - 1));
            while (recordsLeft > 0 && !this.isError) {
                if (this.isCaching) {
                    if (!this.cache.getBlock(this.responseData, block, mostRecentBlock)) {
                        this.commandReadFlash(4096 * block, 4096);
                        if (!this.isError) {
                            this.cache.writeBlock(block, this.responseData);
                        }
                    }
                }
                else {
                    this.commandReadFlash(4096 * block, 4096);
                }
                if (!this.isError) {
                    if (recordsLeft > recordsPerBlock) {
                        trackLog.appendData(this.responseData, 4096);
                        deviceLog.appendData(this.responseData, 4096);
                        recordsLeft -= recordsPerBlock;
                    }
                    else {
                        trackLog.appendData(this.responseData, recordsLeft * 32);
                        deviceLog.appendData(this.responseData, recordsLeft * 32);
                        recordsLeft = 0;
                    }
                    listener.reportProgress(99 * totalBlockCount / blocksToRead);
                    ++block;
                    ++totalBlockCount;
                }
            }
            if (block != firstEmptyBlock) {
                DebugLogger.error("Unexpected number of blocks read while reading track log");
            }
            if (!this.isError) {
                trackLog.finishTracklog();
                outputString = "Records from device: " + this.trackRecordCount + " Valid records: " + trackLog.getNumberOfRecords() + " Log records: " + deviceLog.getNumberOfEntries() + "\n";
            }
            else {
                outputString = "Error while downloading tracks. Please try again\n";
            }
        }
        else {
            if (this.isCaching) {
                this.cache.resetCache();
            }
            outputString = "The device contains no track log data";
        }
        if (this.isCaching) {
            this.cache.makeCachePersistent();
        }
        return outputString;
    }
    
    String downloadWaypoints(final ProgressListener listener) {
        DebugLogger.info("* Downloading waypoints");
        this.getDeviceType(null);
        final WaypointLog waypointLog = WaypointLog.getInstance();
        waypointLog.clear();
        String outputString;
        if (this.memMap.isWaypointsSupported()) {
            final byte[] record = new byte[Waypoint.WAYPOINT_RECORDSIZE];
            if (!this.isError) {
                waypointLog.setDeviceType(this.deviceType);
            }
            int block = 0;
            this.isError = false;
            boolean hasMoreData = true;
            int remainingData = 0;
            boolean exit = false;
            int next = 0;
            final int waypointsAddress = this.memMap.getWaypointsStartAddress();
            final int waypointsPages = this.memMap.getWaypointsPages();
            while (hasMoreData && !this.isError && !exit) {
                for (int i = 0; i < Waypoint.WAYPOINT_RECORDSIZE && !this.isError; ++i) {
                    if (remainingData <= 0) {
                        if (block < waypointsPages) {
                            this.commandReadFlash(waypointsAddress + 4096 * block, 4096);
                            if (!this.isError) {
                                remainingData = 4096;
                                next = 0;
                                ++block;
                                listener.reportProgress(99 * block / waypointsPages);
                            }
                        }
                        else {
                            exit = true;
                        }
                    }
                    if (!this.isError && !exit) {
                        record[i] = this.responseData[next];
                        ++next;
                        --remainingData;
                    }
                }
                if (!this.isError && !exit) {
                    hasMoreData = waypointLog.appendData(record, Waypoint.WAYPOINT_RECORDSIZE);
                }
            }
            if (!this.isError) {
                outputString = "Waypoints read from device: " + waypointLog.getNumberOfEntries() + "\n";
            }
            else {
                outputString = "Error while downloading waypoints. Please try again\n";
            }
            waypointLog.dumpLog();
        }
        else {
            outputString = "Waypoint log not supported\n";
        }
        return outputString;
    }
    
    String eraseTracks(final ProgressListener listener) {
        DebugLogger.info("* Erasing tracks");
        this.getDeviceType(null);
        String outputString;
        if (this.memMap.isValidMemoryMap()) {
            final int tracksFirstPage = this.memMap.getTracksFirstPage();
            int block;
            final int tracksLastPage = block = this.memMap.getTracksLastPage();
            boolean purge = false;
            this.isError = false;
            int count = 0;
            while (block >= tracksFirstPage && !this.isError) {
                if (!purge && !this.isErasedBlock(block)) {
                    purge = true;
                }
                if (purge && !this.isError) {
                    this.commandEraseFlash(block * 4096);
                    ++count;
                }
                listener.reportProgress(99 * (tracksLastPage - block) / tracksLastPage);
                --block;
            }
            if (!this.isError) {
                outputString = "Erased " + count + " out of " + (tracksLastPage - tracksFirstPage) + " flash blocks";
            }
            else {
                outputString = "Error while erasing...";
            }
        }
        else {
            outputString = "Erasing not supported for non GT-800 or non-Pro devices...";
        }
        return outputString;
    }
    
    String uploadRoute(final ProgressListener listener) {
        DebugLogger.info("* Uploading route");
        this.getDeviceType(null);
        String outputString;
        if (this.memMap.isRouteSupported()) {
            listener.reportProgress(0);
            this.isError = false;
            final RouteLog routeLog = RouteLog.getInstance();
            int numberOfWaypoints = routeLog.getNumberOfEntries();
            final int routeAddress = this.memMap.getRouteStartAddress();
            final int routePages = this.memMap.getRoutePages();
            final int maxWaypoints = this.memMap.getRouteMaxWaypoints();
            if (numberOfWaypoints > maxWaypoints) {
                DebugLogger.error("Route contains more waypoints (" + numberOfWaypoints + ") than can be stored on device (" + maxWaypoints + ")");
                numberOfWaypoints = maxWaypoints;
            }
            final ArrayList<RoutePoint> waypoints = routeLog.getWaypoints();
            if (numberOfWaypoints > 0) {
                int i = 0;
                int bytesInBlock = 0;
                int block = 0;
                while (i < numberOfWaypoints && !this.isError) {
                    final RoutePoint waypoint = waypoints.get(i);
                    final byte[] record = routeLog.getWaypointAsByteArray(i);
                    for (int j = 0; j < 288 && !this.isError; ++j) {
                        this.writeData[bytesInBlock] = record[j];
                        if (++bytesInBlock == 4096) {
                            this.writeBlock(routeAddress + block * 4096, 4096);
                            ++block;
                            bytesInBlock = 0;
                            listener.reportProgress(99 * block / routePages);
                        }
                    }
                    ++i;
                }
                if (bytesInBlock > 0 && !this.isError) {
                    while (bytesInBlock < 4096) {
                        this.writeData[bytesInBlock] = 0;
                        ++bytesInBlock;
                    }
                    this.writeBlock(routeAddress + block * 4096, 4096);
                    ++block;
                    listener.reportProgress(99 * block / routePages);
                }
                for (bytesInBlock = 0; bytesInBlock < 4096; ++bytesInBlock) {
                    this.writeData[bytesInBlock] = 0;
                }
                while (block < routePages && !this.isError) {
                    this.writeBlock(routeAddress + block * 4096, 4096);
                    ++block;
                    listener.reportProgress(99 * block / routePages);
                }
                if (!this.isError) {
                    outputString = "Upload of " + numberOfWaypoints + " waypoints succeeded";
                }
                else {
                    outputString = "Error writing data to device";
                }
            }
            else {
                outputString = "No waypoints to upload";
            }
        }
        else {
            outputString = "Device does not support uploadable routes";
        }
        return outputString;
    }
    
    String eraseRoute(final ProgressListener listener) {
        DebugLogger.info("* Erase route");
        listener.reportProgress(0);
        this.isError = false;
        this.getDeviceType(null);
        String outputString;
        if (this.memMap.isRouteSupported()) {
            final int routeAddress = this.memMap.getRouteStartAddress();
            final int routePages = this.memMap.getRoutePages();
            final int maxWaypoints = this.memMap.getRouteMaxWaypoints();
            for (int bytesInBlock = 0; bytesInBlock < 4096; ++bytesInBlock) {
                this.writeData[bytesInBlock] = 0;
            }
            for (int block = 0; block < routePages && !this.isError; ++block) {
                this.writeBlock(routeAddress + block * 4096, 4096);
                listener.reportProgress(99 * block / routePages);
            }
            if (!this.isError) {
                outputString = "Uploadable route area cleared";
            }
            else {
                outputString = "Error writing data to device";
            }
        }
        else {
            outputString = "Device does not support uploadable routes. Nothing to erase";
        }
        return outputString;
    }
    
    String downloadRoute(final ProgressListener listener) {
        DebugLogger.info("* Downloading route");
        this.getDeviceType(null);
        String outputString;
        if (this.memMap.isRouteSupported()) {
            final int routeAddress = this.memMap.getRouteStartAddress();
            final int routePages = this.memMap.getRoutePages();
            final byte[] record = new byte[288];
            final RouteLog routeLog = RouteLog.getInstance();
            routeLog.clear();
            this.getDeviceType(null);
            if (!this.isError) {
                routeLog.setDeviceType(this.deviceType);
            }
            int block = 0;
            this.isError = false;
            boolean hasMoreData = true;
            int remainingData = 0;
            boolean exit = false;
            int next = 0;
            while (hasMoreData && !this.isError && !exit) {
                for (int i = 0; i < 288 && !this.isError; ++i) {
                    if (remainingData <= 0) {
                        if (block < routePages) {
                            this.commandReadFlash(routeAddress + 4096 * block, 4096);
                            if (!this.isError) {
                                remainingData = 4096;
                                next = 0;
                                ++block;
                                listener.reportProgress(99 * block / routePages);
                            }
                        }
                        else {
                            exit = true;
                        }
                    }
                    if (!this.isError && !exit) {
                        record[i] = this.responseData[next];
                        ++next;
                        --remainingData;
                    }
                }
                if (!this.isError && !exit) {
                    hasMoreData = routeLog.appendData(record, 288);
                }
            }
            if (!this.isError) {
                outputString = "Route waypoints read from device: " + routeLog.getNumberOfEntries() + "\n";
            }
            else {
                outputString = "Error while downloading tracks. Please try again\n";
            }
            routeLog.dumpLog();
        }
        else {
            outputString = "Device does not support uploadable routes. Nothing to download";
        }
        return outputString;
    }
    
    public static String getDeviceTypeDescription(final DeviceType deviceType) {
        String outputString = null;
        switch (deviceType) {
            case DEVICETYPE_GT100: {
                outputString = "GT-100";
                break;
            }
            case DEVICETYPE_GT120: {
                outputString = "GT-120";
                break;
            }
            case DEVICETYPE_GT200: {
                outputString = "GT-200";
                break;
            }
            case DEVICETYPE_GT800: {
                outputString = "GT-800";
                break;
            }
            case DEVICETYPE_GT800PRO: {
                outputString = "GT-800PRO";
                break;
            }
            case DEVICETYPE_GT820: {
                outputString = "GT-820";
                break;
            }
            case DEVICETYPE_GT820PRO: {
                outputString = "GT-820PRO";
                break;
            }
            case DEVICETYPE_GT900: {
                outputString = "GT-900";
                break;
            }
            case DEVICETYPE_GT900PRO: {
                outputString = "GT-900PRO";
                break;
            }
            default: {
                outputString = "Unknown";
                break;
            }
        }
        return outputString;
    }
    
    public String getInfo(final ProgressListener listener) {
        this.getDeviceType(null);
        this.getSettingsFromDevice();
        listener.reportProgress(25);
        String outputString = this.commandGetIdentification();
        listener.reportProgress(50);
        String resultString = this.commandGetModel();
        outputString += resultString;
        listener.reportProgress(75);
        resultString = this.commandGetCount();
        outputString += resultString;
        return outputString;
    }

    public String getInfo2(final ProgressListener listener) {
        this.getDeviceType(null);
        this.getSettingsFromDevice();

        //scanFlash();

        scanFlashForUniqueStrings();

        return this.commandGetModel2();
    }
    
    public String scanFlash() {
        this.isError = false;
        this.getDeviceType(null);
        String fileName = "flashscan_" + getDeviceTypeDescription(this.deviceType);
        fileName = fileName + "_" + this.serialNumber + ".txt";
        DebugLogger.info("Writing to file " + fileName);
        try {
            final FileWriter fileWriter = new FileWriter(fileName);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            final int flashStartAddress = this.memMap.getFlashStartAddress();
            for (int flashPages = this.memMap.getFlashPages(), i = 0; i < flashPages; ++i) {
                this.commandReadFlash(flashStartAddress + i * 4096, 16);
                bufferedWriter.write(String.format("%06x: ", flashStartAddress + i * 4096));
                for (int j = 0; j < 8; ++j) {
                    bufferedWriter.write(String.format("%02x", this.responseData[j]));
                }
                bufferedWriter.write("\n");
            }
            bufferedWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing file " + fileName);
        }
        return "Flash scan completed";
    }
    
    public String scanFlashForUniqueStrings() {
        this.getDeviceType(null);
        final int flashStartAddress = this.memMap.getTracksStartAddress();
        final int flashPages = this.memMap.getTracksPages();
        final ArrayList<String> strings = new ArrayList<String>();
        for (int i = 0; i < flashPages; ++i) {
            this.commandReadFlash(flashStartAddress + i * 4096, 16);
            String flashString = "";
            for (int j = 0; j < 8; ++j) {
                flashString += String.format("%02x", this.responseData[j]);
            }
            Iterator<String> iterator;
            boolean found;
            String testString;
            for (iterator = strings.iterator(), found = false; iterator.hasNext() && !found; found = true) {
                testString = iterator.next();
                if (flashString.equals(testString)) {}
            }
            if (!found) {
                strings.add(flashString);
            }
        }
        this.isError = false;
        this.getDeviceType(null);
        String fileName = "flashscan_uniquestrings_" + getDeviceTypeDescription(this.deviceType);
        fileName = fileName + "_" + this.serialNumber + ".txt";
        DebugLogger.info("Writing to file " + fileName);
        try {
            final FileWriter fileWriter = new FileWriter(fileName);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (final String testString : strings) {
                bufferedWriter.write(testString + "\n");
            }
            bufferedWriter.close();
            fileWriter.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing file " + fileName);
        }
        return "Flash scan completed";
    }
    
    private void writeResponse(final String fileName) {
        try {
            final FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(this.responseHeader, 0, this.connection.getResponseHeaderLength());
            outputStream.write(this.responseData, 0, this.connection.getResponseDataLength());
            outputStream.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing response file " + fileName);
        }
    }
    
    public String simulationDump(final String path, final ProgressListener listener) {
        this.isError = false;
        DebugLogger.info("Writing dump for simulation");
        this.getDeviceType(null);
        final String fileName = path + "memory_dump.dat";
        try {
            final FileOutputStream outputStream = new FileOutputStream(fileName);
            final int flashStartAddress = this.memMap.getFlashStartAddress();
            for (int flashPages = this.memMap.getFlashPages(), i = 0; i < flashPages && !this.isError; ++i) {
                this.commandReadFlash(flashStartAddress + i * 4096, 4096);
                outputStream.write(this.responseData);
                if (listener != null) {
                    listener.reportProgress(95 * i / flashPages);
                }
            }
            outputStream.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing file " + fileName);
        }
        this.commandNmeaSwitch((byte)3);
        this.writeResponse(path + "acknowledge_response.dat");
        this.commandGetModel();
        this.writeResponse(path + "modelcmd_response.dat");
        this.commandGetIdentification();
        this.writeResponse(path + "identificationcmd_response.dat");
        this.commandGetCount();
        this.writeResponse(path + "countcmd_response.dat");
        this.commandNmeaSwitch((byte)0);
        if (listener != null) {
            listener.reportProgress(99);
        }
        return "Simulation data dumped to " + path;
    }
    
    public String saveDeviceSettings(final String path, final ProgressListener listener) {
        this.isError = false;
        DebugLogger.info("Writing settings");
        this.getDeviceType(null);
        final String fileName = path + "settings.dat";
        try {
            final FileOutputStream outputStream = new FileOutputStream(fileName);
            final int settingsStartAddress = this.memMap.getSettingsStartAddress();
            this.commandReadFlash(settingsStartAddress, 4096);
            outputStream.write(this.responseData);
            outputStream.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error writing file " + fileName);
        }
        if (listener != null) {
            listener.reportProgress(99);
        }
        return "Device settings written to " + fileName;
    }
    
    public String restoreDeviceSettings(final String path, final ProgressListener listener) {
        this.isError = false;
        DebugLogger.info("Restore device settings to the device");
        final String fileName = path + "settings.dat";
        return "Not implemented yet";
    }
    
    public String saveDeviceSettingsAsText(final String path, final ProgressListener listener) {
        this.isError = false;
        this.getDeviceType(null);
        String fileName = path + "settings_" + getDeviceTypeDescription(this.deviceType);
        fileName = fileName + "_" + this.serialNumber + ".txt";
        DebugLogger.info("Writing settings to text file " + fileName);
        final int settingsStartAddress = this.memMap.getSettingsStartAddress();
        this.commandReadFlash(settingsStartAddress, 4096);
        if (!this.isError) {
            try {
                final FileWriter fileWriter = new FileWriter(fileName);
                final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write("SETTINGS " + getDeviceTypeDescription(this.deviceType) + " SERIAL: " + this.serialNumber + "\n\n");
                if (this.deviceType == DeviceType.DEVICETYPE_GT800 || this.deviceType == DeviceType.DEVICETYPE_GT800PRO || this.deviceType == DeviceType.DEVICETYPE_GT820 || this.deviceType == DeviceType.DEVICETYPE_GT820PRO || this.deviceType == DeviceType.DEVICETYPE_GT900 || this.deviceType == DeviceType.DEVICETYPE_GT900PRO) {
                    bufferedWriter.write("Log interval           : " + ToolBox.bytesToIntLe(this.responseData, 4, 2) + " s\n");
                    if ((this.responseData[18] & 0x10) > 0) {
                        bufferedWriter.write("Power saving           : On\n");
                    }
                    else {
                        bufferedWriter.write("Power saving           : Off\n");
                    }
                    if ((this.responseData[19] & 0x80) > 0) {
                        bufferedWriter.write("Clock 12/24h           : 12h\n");
                    }
                    else {
                        bufferedWriter.write("Clock 12/24h           : 24h\n");
                    }
                    if ((this.responseData[19] & 0x10) > 0) {
                        bufferedWriter.write("Screen rotation        : Landscape\n");
                    }
                    else {
                        bufferedWriter.write("Screen rotation        : Portrait\n");
                    }
                    bufferedWriter.write("Time offset to UTC     : " + ToolBox.bytesToIntLe(this.responseData, 112, 2) + " min\n");
                    final int theInt = ToolBox.bytesToIntLe(this.responseData, 114, 2);
                    if (theInt > 0) {
                        bufferedWriter.write("Autopause speed        : " + theInt / 10 + " km/h\n");
                    }
                    else {
                        bufferedWriter.write("Autopause speed        : Off\n");
                    }
                    if (this.responseData[116] == 0) {
                        bufferedWriter.write("Pedometer sens.        : High\n");
                    }
                    else if (this.responseData[116] == 1) {
                        bufferedWriter.write("Pedometer sens.        : Normal\n");
                    }
                    else if (this.responseData[116] == 2) {
                        bufferedWriter.write("Pedometer sens.        : Low\n");
                    }
                    else {
                        bufferedWriter.write("Pedometer sens.        : Unknown\n");
                    }
                    if (this.responseData[117] == 0) {
                        bufferedWriter.write("Units                  : Metric\n");
                    }
                    else if (this.responseData[117] == 1) {
                        bufferedWriter.write("Units                  : Imperial\n");
                    }
                    else {
                        bufferedWriter.write("Units                  : Unknown\n");
                    }
                    if (this.responseData[118] == 0) {
                        bufferedWriter.write("Language               : English\n");
                    }
                    else if (this.responseData[118] == 1) {
                        bufferedWriter.write("Language               : Chinese, japanese, whatever\n");
                    }
                    else if (this.responseData[118] == 2) {
                        bufferedWriter.write("Language               : Chinese, japanese, whatever\n");
                    }
                    else if (this.responseData[118] == 3) {
                        bufferedWriter.write("Language               : Chinese, japanese, whatever\n");
                    }
                    else if (this.responseData[118] == 4) {
                        bufferedWriter.write("Language               : German\n");
                    }
                    else if (this.responseData[118] == 5) {
                        bufferedWriter.write("Language               : French\n");
                    }
                    else if (this.responseData[118] == 6) {
                        bufferedWriter.write("Language               : Dutch\n");
                    }
                    else if (this.responseData[118] == 7) {
                        bufferedWriter.write("Language               : Italian\n");
                    }
                    else if (this.responseData[118] == 8) {
                        bufferedWriter.write("Language               : Spanish\n");
                    }
                    else if (this.responseData[118] == 9) {
                        bufferedWriter.write("Language               : Russian\n");
                    }
                    bufferedWriter.write("Altitude calibration   : " + ToolBox.bytesToIntLe(this.responseData, 128, 4) + " cm\n");
                }
                if (this.deviceType == DeviceType.DEVICETYPE_GT820 || this.deviceType == DeviceType.DEVICETYPE_GT820PRO || this.deviceType == DeviceType.DEVICETYPE_GT900 || this.deviceType == DeviceType.DEVICETYPE_GT900PRO) {
                    bufferedWriter.write("Profile length         : " + ToolBox.bytesToIntLe(this.responseData, 176, 2) / 10 + " cm\n");
                    bufferedWriter.write("Profile weight         : " + ToolBox.bytesToIntLe(this.responseData, 178, 2) / 100 + " kg\n");
                    bufferedWriter.write("Profile age            : " + this.responseData[180] + " years\n");
                    if (this.responseData[181] == 0) {
                        bufferedWriter.write("Gender                 : female\n");
                    }
                    else if (this.responseData[181] == 1) {
                        bufferedWriter.write("Gender                 : male\n");
                    }
                    else {
                        bufferedWriter.write("Gender                 : unknown\n");
                    }
                    if ((this.responseData[19] & 0x40) > 0) {
                        bufferedWriter.write("Sound                  : Off\n");
                    }
                    else {
                        bufferedWriter.write("Sound                  : On\n");
                    }
                    if (this.responseData[204] == 0) {
                        bufferedWriter.write("Alert heartrate high   : Off\n");
                    }
                    else if (this.responseData[204] == 1) {
                        bufferedWriter.write("Alert heartrate high   : >106 (zone 1)\n");
                    }
                    else if (this.responseData[204] == 2) {
                        bufferedWriter.write("Alert heartrate high   : >123 (zone 2)\n");
                    }
                    else if (this.responseData[204] == 3) {
                        bufferedWriter.write("Alert heartrate high   : >141 (zone 3)\n");
                    }
                    else if (this.responseData[204] == 4) {
                        bufferedWriter.write("Alert heartrate high   : >156 (zone 4)\n");
                    }
                    else if (this.responseData[204] == 5) {
                        bufferedWriter.write("Alert heartrate high   : >177 (zone 5)\n");
                    }
                    else {
                        bufferedWriter.write("Alert heartrate high   : unknonwn\n");
                    }
                    if (this.responseData[205] == 0) {
                        bufferedWriter.write("Alert heartrate low    : Off\n");
                    }
                    else if (this.responseData[205] == 1) {
                        bufferedWriter.write("Alert heartrate low    : <88 (zone 1)\n");
                    }
                    else if (this.responseData[205] == 2) {
                        bufferedWriter.write("Alert heartrate low    : <106 (zone 2)\n");
                    }
                    else if (this.responseData[205] == 3) {
                        bufferedWriter.write("Alert heartrate low    : <123 (zone 3)\n");
                    }
                    else if (this.responseData[205] == 4) {
                        bufferedWriter.write("Alert heartrate low    : <141 (zone 4)\n");
                    }
                    else if (this.responseData[205] == 5) {
                        bufferedWriter.write("Alert heartrate low    : <159 (zone 5)\n");
                    }
                    else {
                        bufferedWriter.write("Alert heartrate low    : unknonwn\n");
                    }
                    int theInt = ToolBox.bytesToIntLe(this.responseData, 198, 2);
                    if (theInt == 0) {
                        bufferedWriter.write("Alert duration         : Off\n");
                    }
                    else {
                        bufferedWriter.write("Alert duration         : " + theInt + "  min\n");
                    }
                    theInt = ToolBox.bytesToIntLe(this.responseData, 212, 4);
                    if (theInt == 0) {
                        bufferedWriter.write("Alert distance         : Off\n");
                    }
                    else {
                        bufferedWriter.write("Alert distance         : " + theInt + "  m\n");
                    }
                }
                if (this.deviceType == DeviceType.DEVICETYPE_GT120) {
                    bufferedWriter.write("No supported yet");
                }
                bufferedWriter.close();
                fileWriter.close();
            }
            catch (IOException e) {
                DebugLogger.error("Error writing file " + fileName);
            }
        }
        return "Settings written to " + fileName;
    }
    
    String verifyCache(final ProgressListener listener) {
        DebugLogger.info("* Verifying the cache file contents");
        this.isError = false;
        boolean cacheIsOk = true;
        boolean exit = false;
        String outputString = "";
        this.getDeviceType(null);
        final int recordsPerBlock = 128;
        final int firstPage = this.memMap.getTracksFirstPage();
        final int lastPage = this.memMap.getTracksLastPage();
        if (this.isError) {
            exit = true;
        }
        if (!this.isCaching) {
            outputString = "Caching is disabled. Enable caching first\n";
            exit = true;
        }
        else if (this.cache == null) {
            outputString = "Please, perform a track download first\n";
            exit = true;
        }
        else if (!this.cache.checkCache(getDeviceTypeDescription(this.deviceType), this.serialNumber)) {
            exit = true;
            outputString = "Please, perform a track download first\n";
        }
        if (!exit) {
            if (!this.isErasedBlock(firstPage)) {
                this.commandGetCount();
                int totalBlockCount = 0;
                int numberOfBlocks;
                if (this.trackRecordCount % recordsPerBlock == 0) {
                    numberOfBlocks = this.trackRecordCount / recordsPerBlock;
                }
                else {
                    numberOfBlocks = this.trackRecordCount / recordsPerBlock + 1;
                }
                final int firstEmptyBlock = firstPage + numberOfBlocks;
                DebugLogger.info("Checking last block written number: 0x" + Integer.toHexString(firstEmptyBlock - 1));
                if (!this.cache.checkLastBlockWritten(firstEmptyBlock - 1)) {
                    cacheIsOk = false;
                    DebugLogger.info("Cache lastBlockWritten parameter value is not ok");
                }
                else {
                    DebugLogger.info("Cache lastBlockWritten parameter value is ok");
                }
                int blocksToRead;
                if (firstEmptyBlock <= lastPage && !this.isErasedBlock(firstEmptyBlock) && !this.isError) {
                    blocksToRead = this.memMap.getTracksPages();
                    int block = firstEmptyBlock;
                    DebugLogger.info("Track log wrapped around. Checking flash page 0x" + Integer.toHexString(block) + " to 0x" + Integer.toHexString(lastPage));
                    while (block <= lastPage && !this.isError) {
                        this.commandReadFlash(4096 * block, 4096);
                        if (!this.cache.checkBlockContent(this.responseData, block)) {
                            DebugLogger.info("Cache page 0x" + Integer.toHexString(block) + " is not ok!");
                            cacheIsOk = false;
                        }
                        ++block;
                        ++totalBlockCount;
                        listener.reportProgress(99 * totalBlockCount / blocksToRead);
                    }
                }
                else {
                    blocksToRead = numberOfBlocks;
                }
                int block = firstPage;
                final int recordsLeft = this.trackRecordCount;
                DebugLogger.info("Checking flash page 0x" + Integer.toHexString(block) + " to 0x" + Integer.toHexString(firstEmptyBlock - 1));
                while (block < firstEmptyBlock && !this.isError) {
                    this.commandReadFlash(4096 * block, 4096);
                    if (!this.cache.checkBlockContent(this.responseData, block)) {
                        DebugLogger.info("Cache page 0x" + Integer.toHexString(block) + " is not ok!");
                        cacheIsOk = false;
                    }
                    ++block;
                    ++totalBlockCount;
                    listener.reportProgress(99 * totalBlockCount / blocksToRead);
                }
                if (!this.isError && !exit) {
                    if (cacheIsOk) {
                        outputString = "Cache file is ok!";
                    }
                    else {
                        outputString = "Cache file is not ok!";
                    }
                }
            }
            else {
                outputString = "The device contains no track log data";
            }
        }
        return outputString;
    }
    
    static {
        Device.theInstance = null;
    }

    public static String byteArrayToHex(byte[] a, int datalength) {
       StringBuilder sb = new StringBuilder();
       int position = 0;
       for(byte b: a)
          if (position >= datalength) {
              continue;
          } else {
              sb.append("0x" + String.format("%02x", b) + ", ");
              position++;
          }
       return sb.toString();
    }
    
    public enum ModelType
    {
        MODELTYPE_GT100, 
        MODELTYPE_GT120, 
        MODELTYPE_GT200, 
        MODELTYPE_GT800PLUS, 
        MODELTYPE_UNKNOWN;
    }
    
    public enum DeviceType
    {
        DEVICETYPE_GT100, 
        DEVICETYPE_GT120, 
        DEVICETYPE_GT200, 
        DEVICETYPE_GT800, 
        DEVICETYPE_GT800PRO, 
        DEVICETYPE_GT820, 
        DEVICETYPE_GT820PRO, 
        DEVICETYPE_GT900, 
        DEVICETYPE_GT900PRO, 
        DEVICETYPE_GT800_OR_GT800PRO, 
        DEVICETYPE_UNKNOWN;
    }
}
