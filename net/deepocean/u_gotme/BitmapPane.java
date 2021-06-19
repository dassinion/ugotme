// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class BitmapPane extends JPanel
{
    private byte[] bitmap;
    
    public BitmapPane() {
        this.bitmap = null;
    }
    
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final RouteLog log = RouteLog.getInstance();
        if (log.getNumberOfEntries() > 0) {
            final ArrayList<RoutePoint> points = log.getWaypoints();
            final RoutePoint point = points.get(1);
            this.bitmap = point.getBitmap();
        }
        if (this.bitmap != null) {
            for (int y = 0; y < 16; ++y) {
                int x = 0;
                while (x < 128) {
                    final int bitmapByte = this.bitmap[y * 16 + x / 8] & 0xFF;
                    for (int dx = 0; dx < 8; ++dx) {
                        final int mask = 1 << dx;
                        if ((bitmapByte & mask) == 0x0) {
                            g.setColor(Color.WHITE);
                        }
                        else {
                            g.setColor(Color.BLACK);
                        }
                        g.drawLine(x, y, x, y);
                        ++x;
                    }
                }
            }
        }
        else {
            g.setColor(Color.RED);
            for (int x = 0; x < 128; ++x) {
                for (int y = 0; y < 128; ++y) {
                    g.drawLine(x, y, x, y);
                }
            }
        }
    }
    
    public void setBitmap(final byte[] bitmap) {
        this.bitmap = bitmap;
    }
}
