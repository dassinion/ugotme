// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.util.TimeZone;
import hirondelle.date4j.DateTime;

public class ToolBox
{
    public static DateTime bytesToDateTime(final byte byte0, final byte byte1, final byte byte2, final byte byte3, final byte byte4) {
        final DateTime now = DateTime.now(TimeZone.getTimeZone("GMT"));
        final int yearNow = now.getYear();
        final int yearMod16 = byte0 >> 4 & 0xF;
        int year;
        if (yearNow % 16 >= yearMod16) {
            year = yearNow - yearNow % 16 + yearMod16;
        }
        else {
            year = yearNow - 16 - yearNow % 16 + yearMod16;
        }
        final int month = byte0 & 0xF;
        final int day = byte1 >> 3 & 0x1F;
        final int hour = (byte1 & 0x7) * 4 + (byte2 >> 6 & 0x3);
        final int minute = byte2 & 0x3F;
        final int second = ((byte3 & 0xFF) << 8 | (byte4 & 0xFF)) / 1000;
        final int nanosecond = ((byte3 & 0xFF) << 8 | (byte4 & 0xFF)) % 1000 * 1000000;
        DateTime dateTime = new DateTime(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second), Integer.valueOf(0));
        if (nanosecond > 500000000) {
            dateTime = dateTime.plus(Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1), DateTime.DayOverflow.Abort);
        }
        return dateTime;
    }
    
    public static DateTime convertUtcToLocal(final DateTime utcDateTime) {
        final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
        final TimeZone localTimeZone = TimeZone.getDefault();
        final DateTime localTime = utcDateTime.changeTimeZone(gmtTimeZone, localTimeZone);
        return localTime;
    }
    
    public static int bytesToIntLe(final byte[] array, final int offset, final int length) {
        int theInt = 0;
        if (length == 2) {
            theInt = ByteBuffer.wrap(array, offset, length).order(ByteOrder.LITTLE_ENDIAN).getShort();
        }
        else if (length == 4) {
            theInt = ByteBuffer.wrap(array, offset, length).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
        return theInt;
    }
    
    public static void copyBytes(final byte[] source, final byte[] dest, final int offset, final int length) {
        for (int i = 0; i < length; ++i) {
            dest[i] = source[i + offset];
        }
    }
}
