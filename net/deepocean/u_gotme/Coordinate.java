// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

public class Coordinate
{
    protected int latitude;
    protected int longitude;
    protected int elevation;
    
    public Coordinate(final int longitude, final int latitude, final int elevation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
    }
    
    public Coordinate() {
        this.latitude = 0;
        this.longitude = 0;
        this.elevation = 0;
    }
    
    public double getLatitude() {
        return this.latitude / 1.0E7;
    }
    
    public double getLongitude() {
        return this.longitude / 1.0E7;
    }
    
    public double getElevation() {
        return this.elevation / 100.0;
    }
}
