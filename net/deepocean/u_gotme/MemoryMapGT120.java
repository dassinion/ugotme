// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

public class MemoryMapGT120 extends MemoryMap
{
    private final int FLASH_STARTADDRESS = 0;
    private final int FLASH_PAGES = 512;
    private final int SETTINGS_STARTADDRESS = 0;
    private final int TRACK_STARTADDRESS = 4096;
    private final int TRACK_PAGES = 511;
    private final int TRACK_FIRSTPAGE = 1;
    private final int TRACK_LASTPAGE = 511;
    private final int MAX_TRACKPOINTS = 65408;
    private final int ROUTE_STARTADDRESS = 2097152;
    private final int ROUTE_BLOCKS = 0;
    private final int ROUTE_MAXWAYPOINTS = 0;
    private final int WAYPOINTS_STARTADDRESS = 2097152;
    private final int WAYPOINTS_BLOCKS = 0;
    private final boolean ISTRACKSSUPPORTED = true;
    private final boolean ISWAYPOINTSSUPPORTED = false;
    private final boolean ISROUTESUPPORTED = false;
    private final boolean ISSCHEDULESSUPPORTED = true;
    private final boolean ISNMEASUPPORTED = true;
    private final boolean ISVALIDMEMORYMAP = true;
    
    @Override
    public int getFlashStartAddress() {
        return 0;
    }
    
    @Override
    public int getFlashPages() {
        return 512;
    }
    
    @Override
    public int getSettingsStartAddress() {
        return 0;
    }
    
    @Override
    public int getTracksStartAddress() {
        return 4096;
    }
    
    @Override
    public int getTracksPages() {
        return 511;
    }
    
    @Override
    public int getTracksFirstPage() {
        return 1;
    }
    
    @Override
    public int getTracksLastPage() {
        return 511;
    }
    
    @Override
    public int getTracksMaxRecords() {
        return 65408;
    }
    
    @Override
    public int getRouteStartAddress() {
        return 2097152;
    }
    
    @Override
    public int getRoutePages() {
        return 0;
    }
    
    @Override
    public int getRouteMaxWaypoints() {
        return 0;
    }
    
    @Override
    public int getWaypointsStartAddress() {
        return 2097152;
    }
    
    @Override
    public int getWaypointsPages() {
        return 0;
    }
    
    @Override
    public boolean isTrackSupported() {
        return true;
    }
    
    @Override
    public boolean isWaypointsSupported() {
        return false;
    }
    
    @Override
    public boolean isRouteSupported() {
        return false;
    }
    
    @Override
    public boolean isSchedulesSupported() {
        this.getClass();
        return true;
    }
    
    @Override
    public boolean isNmeaSupoorted() {
        this.getClass();
        return true;
    }
    
    @Override
    public boolean isValidMemoryMap() {
        this.getClass();
        return true;
    }
}
