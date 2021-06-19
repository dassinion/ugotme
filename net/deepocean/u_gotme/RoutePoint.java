// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.image.BufferedImage;

public class RoutePoint extends Coordinate
{
    public static final int ROUTEPOINT_RECORDSIZE = 288;
    public static final int BITMAP_SIZE = 256;
    public static final int BITMAP_WIDTH = 128;
    public static final int BITMAP_HEIGHT = 16;
    public static final int BITMAP_SEPARATORX = 29;
    public static final int BITMAP_SYMBOLX = 31;
    public static final int BITMAP_NAMEX = 47;
    public static final int BITS_PER_BYTE = 8;
    private byte[] bitmap;
    private String name;
    private String symbol;
    private static byte[] record;
    private static BufferedImage image;
    private static SymbolRepository symbolRepository;
    
    private void initStatics() {
        if (RoutePoint.record == null) {
            RoutePoint.record = new byte[288];
        }
        if (RoutePoint.image == null) {
            RoutePoint.image = new BufferedImage(128, 16, 12);
        }
        if (RoutePoint.symbolRepository == null) {
            RoutePoint.symbolRepository = SymbolRepository.getInstance();
        }
    }
    
    public RoutePoint(final byte[] data, final int routePointNumber) {
        this.initStatics();
        this.latitude = (data[4] & 0xFF) + ((data[5] & 0xFF) << 8) + ((data[6] & 0xFF) << 16) + (data[7] << 24);
        this.longitude = (data[8] & 0xFF) + ((data[9] & 0xFF) << 8) + ((data[10] & 0xFF) << 16) + (data[11] << 24);
        this.elevation = (data[12] & 0xFF) + ((data[13] & 0xFF) << 8) + ((data[14] & 0xFF) << 16) + (data[15] << 24);
        this.bitmap = new byte[256];
        for (int i = 0; i < 256; ++i) {
            this.bitmap[i] = data[32 + i];
        }
        if (data[16] == 0) {
            this.name = String.format("%03d", routePointNumber);
        }
        else {
            final byte[] nameBytes = new byte[15];
            for (int i = 0; i < 15; ++i) {
                if (data[16 + i] > 0) {
                    nameBytes[i] = data[16 + i];
                }
                else {
                    nameBytes[i] = 32;
                }
            }
            this.name = new String(nameBytes).trim();
        }
        final int symbolId = data[31] & 0xFF;
        this.symbol = RoutePoint.symbolRepository.getSymbolNameById(symbolId);
    }
    
    public RoutePoint(final int longitude, final int latitude, final int elevation, final String name, final String symbol) {
        super(longitude, latitude, elevation);
        this.initStatics();
        this.name = name;
        this.symbol = symbol;
        this.createBitMap();
    }
    
    private void createBitMap() {
        final Graphics2D g = RoutePoint.image.createGraphics();
        g.clearRect(0, 0, 128, 16);
        g.drawLine(0, 0, 127, 0);
        g.drawLine(0, 0, 0, 15);
        g.drawLine(29, 0, 29, 15);
        g.drawLine(127, 0, 127, 15);
        final Settings settings = Settings.getInstance();
        final String fontsizeString = settings.getRoutepointFontsize();
        int offset;
        int fontsize;
        int style;
        if (fontsizeString.equals("large")) {
            offset = 1;
            fontsize = 18;
            style = 1;
        }
        else if (fontsizeString.equals("small")) {
            offset = 3;
            fontsize = 10;
            style = 0;
        }
        else {
            offset = 2;
            fontsize = 14;
            style = 0;
        }
        final Font f = new Font("Lucida Sans", style, fontsize);
        g.setFont(f);
        g.drawString(this.name, 47, 16 - offset);
        final int[] symbolBitmap = RoutePoint.symbolRepository.getSymbolBitmap(this.symbol);
        if (symbolBitmap != null) {
            for (int y = 0; y < 15; ++y) {
                for (int x = 0; x < 15; ++x) {
                    if ((symbolBitmap[y] & 1 << x) != 0x0) {
                        g.drawLine(x + 31, y + 1, x + 31, y + 1);
                    }
                }
            }
        }
        this.bitmap = new byte[256];
        for (int y = 0; y < 16; ++y) {
            for (int x = 0; x < 16; ++x) {
                for (int dx = 0; dx < 8; ++dx) {
                    if ((RoutePoint.image.getRGB(x * 8 + dx, y) & 0xFFFFFF) == 0xFFFFFF) {
                        final byte[] bitmap = this.bitmap;
                        final int n = y * 16 + x;
                        bitmap[n] |= (byte)(1 << dx);
                    }
                }
            }
        }
    }
    
    public byte[] getBitmap() {
        return this.bitmap;
    }
    
    @Override
    public String toString() {
        final String outputString = new String("Lon: " + this.longitude + " Lat: " + this.latitude + " Elevation: " + this.elevation + " Name: " + this.name + " Symbol: " + this.symbol);
        return outputString;
    }
    
    public byte[] getRecordAsByteArray() {
        final int lon = (int)(this.longitude * 1.0E7);
        final int lat = (int)(this.latitude * 1.0E7);
        final int ele = (int)(this.elevation * 1.0E7);
        RoutePoint.record[0] = -72;
        RoutePoint.record[1] = 72;
        RoutePoint.record[2] = 32;
        RoutePoint.record[3] = 2;
        RoutePoint.record[4] = (byte)(this.latitude & 0xFF);
        RoutePoint.record[5] = (byte)(this.latitude >> 8 & 0xFF);
        RoutePoint.record[6] = (byte)(this.latitude >> 16 & 0xFF);
        RoutePoint.record[7] = (byte)(this.latitude >> 24 & 0xFF);
        RoutePoint.record[8] = (byte)(this.longitude & 0xFF);
        RoutePoint.record[9] = (byte)(this.longitude >> 8 & 0xFF);
        RoutePoint.record[10] = (byte)(this.longitude >> 16 & 0xFF);
        RoutePoint.record[11] = (byte)(this.longitude >> 24 & 0xFF);
        RoutePoint.record[12] = (byte)(this.elevation & 0xFF);
        RoutePoint.record[13] = (byte)(this.elevation >> 8 & 0xFF);
        RoutePoint.record[14] = (byte)(this.elevation >> 16 & 0xFF);
        RoutePoint.record[15] = (byte)(this.elevation >> 24 & 0xFF);
        final byte[] nameBytes = this.name.getBytes();
        int nameLength = nameBytes.length;
        if (nameLength > 15) {
            nameLength = 15;
        }
        int i;
        for (i = 0; i < nameLength; ++i) {
            RoutePoint.record[16 + i] = nameBytes[i];
        }
        while (i < 15) {
            RoutePoint.record[16 + i] = 32;
            ++i;
        }
        RoutePoint.record[31] = (byte)RoutePoint.symbolRepository.getSymbolIdByName(this.symbol);
        for (i = 0; i < 256; ++i) {
            RoutePoint.record[32 + i] = this.bitmap[i];
        }
        return RoutePoint.record;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getSymbolName() {
        return this.symbol;
    }
    
    static {
        RoutePoint.record = null;
        RoutePoint.image = null;
        RoutePoint.symbolRepository = null;
    }
}
