// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.Iterator;
import java.io.IOException;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

public class SymbolRepository
{
    private static final String SYMBOLSFILE = "symbols.txt";
    private static final String DELIMETER = ";";
    private static final String COMMENTDELIMETER = "#";
    private static SymbolRepository theInstance;
    private ArrayList<Symbol> symbols;
    
    private SymbolRepository() {
        this.symbols = new ArrayList<Symbol>();
        this.readSymbols();
        if (this.symbols.size() == 0) {
            DebugLogger.error("No symbols found!");
        }
    }
    
    private void readSymbols() {
        this.symbols.clear();
        try {
            final FileInputStream fstream = new FileInputStream("symbols.txt");
            final DataInputStream in = new DataInputStream(fstream);
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#")) {
                    final String[] splitLine = line.split("#");
                    final StringTokenizer tokenizer = new StringTokenizer(splitLine[0], ";");
                    if (tokenizer.countTokens() != 17) {
                        continue;
                    }
                    String token = tokenizer.nextToken().trim();
                    final int id = Integer.parseInt(token);
                    final String name = tokenizer.nextToken().trim();
                    final int[] bitmap = new int[15];
                    for (int i = 0; i < 15; ++i) {
                        token = tokenizer.nextToken().trim();
                        bitmap[i] = Integer.parseInt(token, 16);
                    }
                    final Symbol symbol = new Symbol(id, name, bitmap);
                    this.symbols.add(symbol);
                }
            }
            in.close();
        }
        catch (IOException e) {
            DebugLogger.error("Error reading symbols file symbols.txt");
        }
    }
    
    public static SymbolRepository getInstance() {
        if (SymbolRepository.theInstance == null) {
            SymbolRepository.theInstance = new SymbolRepository();
        }
        return SymbolRepository.theInstance;
    }
    
    public int[] getSymbolBitmap(final String symbolName) {
        int[] symbolBitmap = null;
        Iterator<Symbol> i;
        boolean found;
        Symbol symbol;
        for (i = this.symbols.iterator(), found = false; i.hasNext() && !found; found = true, symbolBitmap = symbol.getBitmap()) {
            symbol = i.next();
            if (symbol.getName().toLowerCase().equals(symbolName.toLowerCase())) {}
        }
        if (!found && this.symbols.size() > 0) {
            symbol = this.symbols.get(0);
            symbolBitmap = symbol.getBitmap();
        }
        return symbolBitmap;
    }
    
    public String getSymbolNameById(final int id) {
        String symbolName = "";
        final Iterator<Symbol> i = this.symbols.iterator();
        Symbol symbol;
        for (boolean found = false; i.hasNext() && !found; found = true, symbolName = symbol.getName()) {
            symbol = i.next();
            if (symbol.getId() == id) {}
        }
        return symbolName;
    }
    
    public int getSymbolIdByName(final String name) {
        int id = 0;
        final Iterator<Symbol> i = this.symbols.iterator();
        Symbol symbol;
        for (boolean found = false; i.hasNext() && !found; found = true, id = symbol.getId()) {
            symbol = i.next();
            if (symbol.getName().toLowerCase().equals(name.toLowerCase().trim())) {}
        }
        return id;
    }
    
    static {
        SymbolRepository.theInstance = null;
    }
}
