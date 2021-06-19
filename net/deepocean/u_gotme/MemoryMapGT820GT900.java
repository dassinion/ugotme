package net.deepocean.u_gotme;

public class MemoryMapGT820GT900 extends MemoryMap
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
    private final boolean ISVALIDMEMORYMAP = false;
    
    @Override
    public int getFlashStartAddress() {
        return FLASH_STARTADDRESS;
    }
    
    @Override
    public int getFlashPages() {
        return FLASH_PAGES;
    }
    
    @Override
    public int getSettingsStartAddress() {
        return SETTINGS_STARTADDRESS;
    }
    
    @Override
    public int getTracksStartAddress() {
        return TRACK_STARTADDRESS;
    }
    
    @Override
    public int getTracksPages() {
        return TRACK_PAGES;
    }
    
    @Override
    public int getTracksFirstPage() {
        return TRACK_FIRSTPAGE;
    }
    
    @Override
    public int getTracksLastPage() {
        return TRACK_LASTPAGE;
    }
    
    @Override
    public int getTracksMaxRecords() {
        return MAX_TRACKPOINTS;
    }
    
    @Override
    public int getRouteStartAddress() {
        return ROUTE_STARTADDRESS;
    }
    
    @Override
    public int getRoutePages() {
        return ROUTE_BLOCKS;
    }
    
    @Override
    public int getRouteMaxWaypoints() {
        return ROUTE_MAXWAYPOINTS;
    }
    
    @Override
    public int getWaypointsStartAddress() {
        return WAYPOINTS_STARTADDRESS;
    }
    
    @Override
    public int getWaypointsPages() {
        return WAYPOINTS_BLOCKS;
    }
    
    @Override
    public boolean isTrackSupported() {
        return ISTRACKSSUPPORTED;
    }
    
    @Override
    public boolean isWaypointsSupported() {
        return ISWAYPOINTSSUPPORTED;
    }
    
    @Override
    public boolean isRouteSupported() {
        return ISROUTESUPPORTED;
    }
    
    @Override
    public boolean isValidMemoryMap() {
        this.getClass();
        return ISVALIDMEMORYMAP;
    }
}
