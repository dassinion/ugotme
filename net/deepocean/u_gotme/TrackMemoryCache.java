// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;

public class TrackMemoryCache
{
    private static final int NO_BLOCK_WRITTEN = -1;
    int startBlock;
    int endBlock;
    int blockSize;
    int serial;
    String device;
    byte[][] byteBlock;
    int lastBlockWritten;
    boolean isCacheWrappedAround;
    boolean isUpdated;
    Settings settings;
    
    public TrackMemoryCache(final int startBlock, final int endBlock, final int blockSize, final String device, final int serial) {
        DebugLogger.info("Create cache for device: " + device + " serial: " + serial);
        this.settings = Settings.getInstance();
        this.startBlock = startBlock;
        this.endBlock = endBlock;
        this.blockSize = blockSize;
        this.device = device;
        this.serial = serial;
        this.byteBlock = new byte[endBlock - startBlock + 1][blockSize];
        this.resetCache();
        this.readCache();
        this.isUpdated = false;
    }
    
    public final void resetCache() {
        this.lastBlockWritten = -1;
        this.isCacheWrappedAround = false;
    }
    
    public boolean checkCache(final String device, final int serial) {
        final boolean isOk = this.device.equals(device) && this.serial == serial;
        return isOk;
    }
    
    public void validateCache(final byte[] oldestData, final int blockNumber) {
        DebugLogger.info("Validating cache based on block 0x" + Integer.toHexString(blockNumber));
        int i;
        boolean isEqual;
        for (i = 1, isEqual = true; i < 6 && isEqual; ++i) {
            if (this.byteBlock[blockNumber - this.startBlock][i] != oldestData[i]) {
                isEqual = false;
            }
        }
        if (!isEqual) {
            DebugLogger.info("Cache invalidated");
            this.resetCache();
        }
        else {
            DebugLogger.info("Cache validated");
        }
    }
    
    private String getFileName() {
        final String path = this.settings.getLogPath();
        final String fileName = path + "cache_" + this.device + "_" + this.serial + ".bin";
        return fileName;
    }
    
    private void readCache() {
        final String fileName = this.getFileName();
        final File file = new File(fileName);
        if (file.exists()) {
            DebugLogger.info("Reading cache binary file " + fileName);
            DebugLogger.debug("File size: " + file.length());
            ObjectInputStream input = null;
            try {
                input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
                this.startBlock = input.readInt();
                this.endBlock = input.readInt();
                this.blockSize = input.readInt();
                this.lastBlockWritten = input.readInt();
                final int dummy = input.readInt();
                if (dummy > 0) {
                    this.isCacheWrappedAround = true;
                }
                else {
                    this.isCacheWrappedAround = false;
                }
                for (int i = 0; i < this.endBlock - this.startBlock + 1; ++i) {
                    int totalBytesRead;
                    int bytesRead;
                    for (totalBytesRead = 0; totalBytesRead < this.blockSize; totalBytesRead += bytesRead) {
                        final int bytesRemaining = this.blockSize - totalBytesRead;
                        bytesRead = input.read(this.byteBlock[i], totalBytesRead, bytesRemaining);
                        if (bytesRead > 0) {}
                    }
                    if (totalBytesRead != this.blockSize) {
                        DebugLogger.error("Error reading cache: block " + i + " has size 0x" + Integer.toHexString(totalBytesRead));
                    }
                    DebugLogger.debug("Num bytes read: " + totalBytesRead);
                }
                input.close();
            }
            catch (FileNotFoundException ex2) {
                DebugLogger.error("Cache file not found.");
                this.resetCache();
            }
            catch (IOException ex) {
                DebugLogger.error("IO Exception reading cache file: " + ex.toString());
                this.resetCache();
            }
        }
        else {
            this.resetCache();
            DebugLogger.debug("No cache file found");
        }
    }
    
    private void writeCache() {
        if (this.isUpdated) {
            final String fileName = this.getFileName();
            DebugLogger.info("Writing binary cache file " + fileName);
            try {
                final ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
                output.writeInt(this.startBlock);
                output.writeInt(this.endBlock);
                output.writeInt(this.blockSize);
                output.writeInt(this.lastBlockWritten);
                if (this.isCacheWrappedAround) {
                    output.writeInt(1);
                }
                else {
                    output.writeInt(0);
                }
                for (int i = 0; i < this.endBlock - this.startBlock + 1; ++i) {
                    output.write(this.byteBlock[i]);
                }
                output.close();
            }
            catch (FileNotFoundException ex2) {
                DebugLogger.error("Cannot write cache file " + fileName + ". Perhaps 'logPath' directory does not exist");
            }
            catch (IOException ex) {
                DebugLogger.error("IO exception while writing cache file: " + ex.toString());
            }
            this.isUpdated = false;
        }
    }
    
    public boolean getBlock(final byte[] target, final int blockNumber, final int mostRecentTrackMemBlock) {
        boolean bytesRead = false;
        if (mostRecentTrackMemBlock < this.lastBlockWritten) {
            if (blockNumber > mostRecentTrackMemBlock && blockNumber < this.lastBlockWritten) {
                bytesRead = true;
            }
        }
        else if (mostRecentTrackMemBlock > this.lastBlockWritten) {
            if (blockNumber < this.lastBlockWritten) {
                bytesRead = true;
            }
            if (blockNumber > mostRecentTrackMemBlock) {
                bytesRead = true;
            }
        }
        else if (blockNumber != this.lastBlockWritten) {
            bytesRead = true;
        }
        if (bytesRead) {
            System.arraycopy(this.byteBlock[blockNumber - this.startBlock], 0, target, 0, this.blockSize);
            DebugLogger.info("Read block 0x" + Integer.toHexString(blockNumber) + " from cache");
        }
        return bytesRead;
    }
    
    public void writeBlock(final int blockNumber, final byte[] blockData) {
        DebugLogger.info("Write block 0x" + Integer.toHexString(blockNumber) + " to cache");
        System.arraycopy(blockData, 0, this.byteBlock[blockNumber - this.startBlock], 0, this.blockSize);
        this.isUpdated = true;
        if (blockNumber == this.startBlock && this.lastBlockWritten == this.endBlock) {
            this.isCacheWrappedAround = true;
            this.lastBlockWritten = blockNumber;
        }
        else {
            this.lastBlockWritten = blockNumber;
        }
    }
    
    public void makeCachePersistent() {
        this.writeCache();
    }
    
    public boolean checkBlockContent(final byte[] data, final int block) {
        boolean isOk = true;
        for (int i = 0; i < this.blockSize && !isOk; ++i) {
            if (this.byteBlock[block][i] != data[i]) {
                isOk = false;
            }
        }
        return isOk;
    }
    
    public boolean checkLastBlockWritten(final int block) {
        final boolean isOk = this.lastBlockWritten == block;
        return isOk;
    }
}
