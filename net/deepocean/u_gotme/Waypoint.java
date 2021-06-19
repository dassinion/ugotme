// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import hirondelle.date4j.DateTime;

public class Waypoint extends Coordinate
{
    public static long EPOCH_1999_12_31_00_00_00;
    public static int WAYPOINT_RECORDSIZE;
    private DateTime timeStamp;
    private int id;
    
    public Waypoint(final long time, final int longitude, final int latitude, final int elevation, final int id) {
        super(longitude, latitude, elevation);
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        final String dateTimeString = formatter.format(new Date((time + Waypoint.EPOCH_1999_12_31_00_00_00) * 1000L));
        this.timeStamp = new DateTime(dateTimeString);
        this.id = id;
    }
    
    public int getId() {
        return this.id + 100;
    }
    
    public DateTime getDateTime() {
        return this.timeStamp;
    }
    
    @Override
    public String toString() {
        return new String("Waypoint " + String.format("%3d", this.getId()) + " " + this.timeStamp.format("DD-MM-YYYY hh:mm:ss") + " " + "Lon " + String.format("%5.3f", this.getLongitude()) + " " + "Lat " + String.format("%5.3f", this.getLatitude()) + " " + "Ele " + String.format("%5.3f", this.getElevation()));
    }
    
    static {
        Waypoint.EPOCH_1999_12_31_00_00_00 = 946598400L;
        Waypoint.WAYPOINT_RECORDSIZE = 24;
    }
}
