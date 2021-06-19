// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

public class Symbol
{
    public static final int SYMBOL_BYTES = 15;
    private int id;
    private String name;
    private int[] bitmap;
    
    public Symbol(final int id, final String name, final int[] bitmap) {
        if (id < 0 || id > 255) {
            DebugLogger.error("Symbol id out of range");
        }
        this.id = id;
        this.name = name;
        this.bitmap = bitmap;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int[] getBitmap() {
        return this.bitmap;
    }
}
