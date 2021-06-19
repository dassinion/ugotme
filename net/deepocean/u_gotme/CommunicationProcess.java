package net.deepocean.u_gotme;

public class CommunicationProcess implements Runnable
{
    private static CommunicationProcess theInstance;
    private boolean running;
    private boolean finished;
    private Command command;
    private ProgressListener listener;
    private String parameter;
    private String outputString;
    private Thread theThread;
    private Device device;
    
    private CommunicationProcess() {
        synchronized (this) {
            this.running = false;
            this.finished = false;
        }
        (this.theThread = new Thread(this)).start();
        this.device = Device.getInstance();
    }
    
    public static CommunicationProcess getInstance() {
        if (CommunicationProcess.theInstance == null) {
            CommunicationProcess.theInstance = new CommunicationProcess();
        }
        return CommunicationProcess.theInstance;
    }
    
    void executeCommand(final Command command, final ProgressListener listener, final String param) {
        synchronized (this) {
            this.listener = listener;
            this.command = command;
            this.parameter = param;
            this.notify();
        }
    }
    
    String getResult() {
        final String resultString;
        synchronized (this) {
            resultString = new String(this.outputString);
        }
        return resultString;
    }
    
    void exit() {
        synchronized (this) {
            this.finished = true;
            this.notify();
        }
    }
    
    private void downloadTracks(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.downloadTracks(listener);
        this.device.close();
    }
    
    private void downloadWaypoints(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.downloadWaypoints(listener);
        this.device.close();
    }
    
    private void getInfo(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.getInfo(listener);
        this.device.close();
    }

    private void scanFlash(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.scanFlash();
        this.device.close();
    }
    
    private void scanFlashForUniqueStrings(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.scanFlashForUniqueStrings();
        this.device.close();
    }
    
    private void eraseTracks(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.eraseTracks(listener);
        this.device.close();
    }
    
    private void getDeviceType(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.getDeviceType(listener);
        this.device.close();
    }
    
    private void uploadRoute(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.uploadRoute(listener);
        this.device.close();
    }
    
    private void downloadRoute(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.downloadRoute(listener);
        this.device.close();
    }
    
    private void eraseRoute(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.eraseRoute(listener);
        this.device.close();
    }
    
    private void saveSimulationSet(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.simulationDump(this.parameter, listener);
        this.device.close();
    }
    
    private void saveDeviceSettings(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.saveDeviceSettings(this.parameter, listener);
        this.device.close();
    }
    
    private void restoreDeviceSettings(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.restoreDeviceSettings(this.parameter, listener);
        this.device.close();
    }
    
    private void saveDeviceSettingsAsText(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.saveDeviceSettingsAsText(this.parameter, listener);
        this.device.close();
    }
    
    private void verifyCacheFile(final String comport, final ProgressListener listener) {
        (this.device = Device.getInstance()).open(comport);
        this.outputString = this.device.verifyCache(listener);
        this.device.close();
    }
    
    public void run() {
        DebugLogger.debug("Command executing thread started");
        boolean finishedLocal;
        synchronized (this) {
            this.running = true;
            finishedLocal = this.finished;
        }
        while (!finishedLocal) {
            final ProgressListener listenerLocal;
            final Command commandLocal;
            synchronized (this) {
                try {
                    DebugLogger.debug("Command executing thread says 'Zzzzzzz'");
                    this.wait();
                    DebugLogger.debug("Command executing thread says 'Wazzup?!'");
                }
                catch (InterruptedException ex) {}
                finishedLocal = this.finished;
                listenerLocal = this.listener;
                commandLocal = new Command(this.command);
            }
            if (!finishedLocal) {
                switch (commandLocal.command) {
                    case COMMAND_GETINFO: {
                        this.getInfo(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_SCANFLASH: {
                        this.scanFlash(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_SCANFLASHFORUNIQUESTRINGS: {
                        this.scanFlashForUniqueStrings(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_DOWNLOADTRACKS: {
                        this.downloadTracks(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_ERASETRACKS: {
                        this.eraseTracks(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_UPLOADROUTE: {
                        this.uploadRoute(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_DOWNLOADROUTE: {
                        this.downloadRoute(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_ERASEROUTE: {
                        this.eraseRoute(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_DOWNLOADWAYPOINTS: {
                        this.downloadWaypoints(commandLocal.comport, listenerLocal);
                        break;
                    }
                    case COMMAND_GETDEVICETYPE: {
                        this.getDeviceType(commandLocal.comport, this.listener);
                        break;
                    }
                    case COMMAND_SAVESIMULATIONSET: {
                        this.saveSimulationSet(commandLocal.comport, this.listener);
                        break;
                    }
                    case COMMAND_SAVEDEVICESETTINGS: {
                        this.saveDeviceSettings(commandLocal.comport, this.listener);
                        break;
                    }
                    case COMMAND_RESTOREDEVICESETTINGS: {
                        this.restoreDeviceSettings(commandLocal.comport, this.listener);
                        break;
                    }
                    case COMMAND_SAVEDEVICESETTINGSASTEXT: {
                        this.saveDeviceSettingsAsText(commandLocal.comport, this.listener);
                        break;
                    }
                    case COMMAND_VERIFYCACHEFILE: {
                        this.verifyCacheFile(commandLocal.comport, this.listener);
                        break;
                    }
                }
                listenerLocal.reportProgress(100);
            }
        }
        DebugLogger.debug("Command executing thread says 'Basta! I quit'");
    }
    
    static {
        CommunicationProcess.theInstance = null;
    }
}
