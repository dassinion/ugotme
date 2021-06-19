// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.ArrayList;
import hirondelle.date4j.DateTime;

public class HeartRatePoint
{
    DateTime dateTime;
    int heartRate;
    
    private HeartRatePoint(final DateTime dateTime, final int heartRate) {
        this.dateTime = dateTime;
        this.heartRate = heartRate;
    }
    
    public static ArrayList<HeartRatePoint> parseHeartRateRecord(final byte[] record) {
        final ArrayList<HeartRatePoint> points = new ArrayList<HeartRatePoint>();
        final int flags = record[0] & 0xFF;
        final DateTime dateTime = ToolBox.bytesToDateTime(record[1], record[2], record[3], record[4], record[5]);
        if (flags == 245) {
            for (int i = 0; i < 24; ++i) {
                final DateTime pointDateTime = dateTime.minus(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(23 - i), DateTime.DayOverflow.Abort);
                final int pointRate = record[6 + i] & 0xFF;
                final HeartRatePoint point = new HeartRatePoint(pointDateTime, pointRate);
                points.add(point);
            }
        }
        return points;
    }
    
    public DateTime getDateTime() {
        return this.dateTime;
    }
    
    public int getHeartRateValue() {
        int value;
        if (this.heartRate == 0 || this.heartRate == 255) {
            value = -1;
        }
        else {
            value = this.heartRate;
        }
        return value;
    }
}
