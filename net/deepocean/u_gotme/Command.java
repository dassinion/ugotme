// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

public class Command
{
    public CommandType command;
    public String comport;
    
    public Command(final CommandType command, final String comport) {
        this.command = command;
        this.comport = comport;
    }
    
    public Command(final Command command) {
        this.command = command.command;
        this.comport = command.comport;
    }
    
    public enum CommandType
    {
        COMMAND_DOWNLOADTRACKS, 
        COMMAND_ERASETRACKS, 
        COMMAND_GETINFO, 
        COMMAND_SCANFLASH, 
        COMMAND_SCANFLASHFORUNIQUESTRINGS,
        COMMAND_UPLOADROUTE, 
        COMMAND_DOWNLOADWAYPOINTS, 
        COMMAND_GETDEVICETYPE, 
        COMMAND_DOWNLOADROUTE, 
        COMMAND_SAVESIMULATIONSET, 
        COMMAND_SAVEDEVICESETTINGS, 
        COMMAND_RESTOREDEVICESETTINGS, 
        COMMAND_SAVEDEVICESETTINGSASTEXT, 
        COMMAND_ERASEROUTE, 
        COMMAND_VERIFYCACHEFILE;
    }
}
