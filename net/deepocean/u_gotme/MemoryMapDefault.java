// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

public class MemoryMapDefault extends MemoryMap
{
    private final int FLASH_STARTADDRESS = 0;
    private final int FLASH_PAGES = 2048;
    private final int SETTINGS_STARTADDRESS = 0;
    private final int TRACK_STARTADDRESS = 4096;
    private final int TRACK_PAGES = 1791;
    private final int TRACK_FIRSTPAGE = 1;
    private final int TRACK_LASTPAGE = 1791;
    private final int MAX_TRACKPOINTS = 229248;
    private final int ROUTE_STARTADDRESS = 7368704;
    private final int ROUTE_BLOCKS = 8;
    private final int ROUTE_MAXWAYPOINTS = 100;
    private final int WAYPOINTS_STARTADDRESS = 7340032;
    private final int WAYPOINTS_BLOCKS = 7;
    private final boolean ISTRACKSSUPPORTED = true;
    private final boolean ISWAYPOINTSSUPPORTED = true;
    private final boolean ISROUTESUPPORTED = true;
    private final boolean ISSCHEDULESSUPPORTED = false;
    private final boolean ISNMEASUPPORTED = false;
    private final boolean ISVALIDMEMORYMAP = true;
    
    @Override
    public int getFlashStartAddress() {
        return 0;
    }
    
    @Override
    public int getFlashPages() {
        return 2048;
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
        return 1791;
    }
    
    @Override
    public int getTracksFirstPage() {
        return 1;
    }
    
    @Override
    public int getTracksLastPage() {
        return 1791;
    }
    
    @Override
    public int getTracksMaxRecords() {
        return 229248;
    }
    
    @Override
    public int getRouteStartAddress() {
        return 7368704;
    }
    
    @Override
    public int getRoutePages() {
        return 8;
    }
    
    @Override
    public int getRouteMaxWaypoints() {
        return 100;
    }
    
    @Override
    public int getWaypointsStartAddress() {
        return 7340032;
    }
    
    @Override
    public int getWaypointsPages() {
        return 7;
    }
    
    @Override
    public boolean isTrackSupported() {
        return true;
    }
    
    @Override
    public boolean isWaypointsSupported() {
        return true;
    }
    
    @Override
    public boolean isRouteSupported() {
        return true;
    }
    
    @Override
    public boolean isSchedulesSupported() {
        this.getClass();
        return false;
    }
    
    @Override
    public boolean isNmeaSupoorted() {
        this.getClass();
        return false;
    }
    
    @Override
    public boolean isValidMemoryMap() {
        this.getClass();
        return true;
    }
}
