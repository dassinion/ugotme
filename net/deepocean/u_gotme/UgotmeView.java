// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import java.util.List;
import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.AbstractButton;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import java.awt.LayoutManager;
import java.awt.Container;
import javax.swing.GroupLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.AbstractListModel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;
import javax.swing.UnsupportedLookAndFeelException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import java.awt.Component;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import javax.swing.JFileChooser;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;

public class UgotmeView extends JFrame implements ProgressListener
{
    private ButtonGroup buttonGroup1;
    private ButtonGroup buttonGroup2;
    private ButtonGroup buttonGroup3;
    private JRadioButtonMenuItem csvMenuItem;
    private JRadioButtonMenuItem debugLevelDebugRadioButton;
    private JRadioButtonMenuItem debugLevelErrorRadioButton;
    private JRadioButtonMenuItem debugLevelInfoRadioButton;
    private JRadioButtonMenuItem debugLevelOffRadioButton;
    private JMenu debugMenu;
    private JRadioButtonMenuItem gpx1_0MenuItem;
    private JRadioButtonMenuItem gpx1_1MenuItem;
    private JRadioButtonMenuItem hybridMenuItem;
    private JButton jButtonDownloadTrack;
    private JButton jButtonEraseTracks;
    private JButton jButtonGetInfo;
    private JButton jButtonSaveRoute;
    private JButton jButtonSaveTrack;
    private JButton jButtonSaveWaypoints;
    private JButton jButtonShowRoute;
    private JButton jButtonShowTrack;
    private JButton jButtonShowWaypoints;
    private JButton jButtonUploadRoute;
    private JComboBox jComboBoxComport;
    private JLabel jLabelComport;
    private JLabel jLabelOutput;
    private JLabel jLabelProgress;
    private JLabel jLabelTracks;
    private JList jListTracks;
    private JMenu jMenu1;
    private JMenuBar jMenuBar1;
    private JMenu jMenuDevice;
    private JMenu jMenuFile;
    private JMenu jMenuHelp;
    private JMenu jMenuExtend;
    private JMenuItem jMenuItem1;
    private JMenuItem jMenuItem2;
    private JMenuItem jMenuItem5;
    private JMenuItem jMenuItem6;
    private JMenuItem jMenuItem7;
    private JMenuItem jMenuItemAbout;
    private JMenuItem jMenuItemExtend01;
    private JMenuItem jMenuItemExtend02;
    private JMenuItem jMenuItemExtend03;
    private JMenuItem jMenuItemExtend04;
    private JMenuItem jMenuItemDeleteRoute;
    private JMenuItem jMenuItemDownloadRoute;
    private JMenuItem jMenuItemDownloadTracks;
    private JMenuItem jMenuItemEraseTracks;
    private JMenuItem jMenuItemGetDeviceType;
    private JMenuItem jMenuItemGetInfo;
    private JMenu jMenuItemMapType;
    private JMenuItem jMenuItemOptions_SaveSettings;
    private JMenuItem jMenuItemRescanComports;
    private JMenuItem jMenuItemSaveDeviceLog;
    private JMenuItem jMenuItemSaveDeviceSettings;
    private JMenuItem jMenuItemSaveDeviceSettingsBlock;
    private JMenuItem jMenuItemSaveSimSet;
    private JMenuItem jMenuItemSaveTracks;
    private JMenuItem jMenuItemShowRoute;
    private JMenuItem jMenuItemShowSpeed;
    private JMenuItem jMenuItemShowWaypoints;
    private JMenuItem jMenuItemUploadRoute;
    private JMenu jMenuOptions;
    private JMenu jMenuOptions_FileOutputType;
    private JMenu jMenuRoutes;
    private JMenu jMenuTracks;
    private JMenu jMenuWaypoints;
    private JProgressBar jProgressBarDownload;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTextArea jTextAreaOutput;
    private JPanel mainPanel;
    private JMenuItem menuItemVerifyCacheFile;
    private JRadioButtonMenuItem roadMenuItem;
    private JRadioButtonMenuItem satelliteMenuItem;
    private JRadioButtonMenuItem tcxMenuItem;
    private JRadioButtonMenuItem terrainMenuItem;
    private JDialog aboutBox;
    private IgotuWriter writer;
    private FileNameExtensionFilter gpxFileFilter;
    private FileNameExtensionFilter tcxFileFilter;
    private FileNameExtensionFilter csvFileFilter;
    private DefaultListModel trackListModel;
    private CommunicationProcess process;
    private boolean executingCommand;
    private Command command;
    private Settings settings;
    private BitmapPane bitmapPane;
    private int nextAfterWaypointDownload;
    private static final int NEXTAFTERWAYPOINTDOWNLOAD_SAVE = 1;
    private static final int NEXTAFTERWAYPOINTDOWNLOAD_SHOW = 2;
    private int nextAfterRouteDownload;
    private static final int NEXTAFTERROUTEDOWNLOAD_SAVE = 1;
    private static final int NEXTAFTERROUTEDOWNLOAD_SHOW = 2;
    
    public UgotmeView() {
        this.setResizable(false);
        this.initComponents();
        this.initFunctionality();
    }
    
    void initFunctionality() {
        this.executingCommand = false;
        this.process = CommunicationProcess.getInstance();
        this.settings = Settings.getInstance();
        this.trackListModel = new DefaultListModel();
        this.jListTracks.setModel(this.trackListModel);
        this.updatePortList();
        if (DebugLogger.getDebugLevel() == 3) {
            this.debugLevelOffRadioButton.setSelected(true);
        }
        else if (DebugLogger.getDebugLevel() == 0) {
            this.debugLevelDebugRadioButton.setSelected(true);
        }
        else if (DebugLogger.getDebugLevel() == 1) {
            this.debugLevelInfoRadioButton.setSelected(true);
        }
        else {
            this.debugLevelErrorRadioButton.setSelected(true);
        }
        this.gpxFileFilter = new FileNameExtensionFilter("GPX files (*.gpx)", new String[] { "GPX" });
        this.tcxFileFilter = new FileNameExtensionFilter("TCX files (*.tcx)", new String[] { "TCX" });
        this.csvFileFilter = new FileNameExtensionFilter("CSV files (*.txt)", new String[] { "TXT" });
        if (this.settings.getOutputFileType().equals("TCX")) {
            this.tcxMenuItem.setSelected(true);
        }
        else if (this.settings.getOutputFileType().equals("TXT")) {
            this.tcxMenuItem.setSelected(true);
        }
        else {
            final GpxWriter gpxWriter = GpxWriter.getInstance();
            if (this.settings.getGpxVersion().equals("1.0")) {
                gpxWriter.setGpxVersion("1.0");
                this.jMenuOptions_FileOutputType.setSelected(true);
            }
            else {
                this.gpx1_1MenuItem.setSelected(true);
                gpxWriter.setGpxVersion("1.1");
            }
        }
        if (this.settings.getMapType().equals("roadmap")) {
            MapFrame.setMapType("roadmap");
            this.roadMenuItem.setSelected(true);
        }
        else if (this.settings.getMapType().equals("satellite")) {
            MapFrame.setMapType("satellite");
            this.satelliteMenuItem.setSelected(true);
        }
        else if (this.settings.getMapType().equals("terrain")) {
            MapFrame.setMapType("terrain");
            this.terrainMenuItem.setSelected(true);
        }
        else if (this.settings.getMapType().equals("hybrid")) {
            MapFrame.setMapType("hybrid");
            this.hybridMenuItem.setSelected(true);
        }
        if (!this.settings.getDebugging()) {
            this.debugMenu.setVisible(false);
        }
    }
    
    private void updatePortList() {
        this.jComboBoxComport.removeAllItems();
        final ArrayList<String> comportNames = Device.getInstance().getConnection().getComportList();
        for (int i = 0; i < comportNames.size(); ++i) {
            this.jComboBoxComport.addItem(comportNames.get(i));
        }
        if (!this.settings.isSimulationMode()) {
            final String comport = this.settings.getComport();
            if (!comport.equals("")) {
                this.jComboBoxComport.setSelectedItem(comport);
                if (!comport.equals(this.jComboBoxComport.getSelectedItem())) {
                    this.jTextAreaOutput.setText("Comport " + comport + " (defined in the properties file) not found.\n" + "Make sure to connect the " + "device before starting the software");
                }
            }
            else {
                this.jTextAreaOutput.setText("No comport defined in the properties file");
            }
        }
    }
    
    private void saveWaypointsToGpx() {
        final WaypointLog log = WaypointLog.getInstance();
        if (log.getNumberOfEntries() > 0) {
            final JFileChooser fc = new JFileChooser();
            String path = this.settings.getGpxPath();
            if (!path.equals("")) {
                fc.setCurrentDirectory(new File(path));
            }
            fc.addChoosableFileFilter(this.gpxFileFilter);
            fc.addChoosableFileFilter(this.csvFileFilter);
            fc.setAcceptAllFileFilterUsed(false);
            if (this.settings.getOutputFileType().equals("CSV")) {
                fc.setFileFilter(this.csvFileFilter);
            }
            else {
                fc.setFileFilter(this.gpxFileFilter);
            }
            final int returnValue = fc.showSaveDialog(this.mainPanel);
            if (returnValue == 0) {
                final FileFilter filter = fc.getFileFilter();
                String extension;
                if (filter.equals(this.csvFileFilter)) {
                    this.writer = CsvWriter.getInstance();
                    extension = ".txt";
                }
                else if (filter.equals(this.gpxFileFilter)) {
                    this.writer = GpxWriter.getInstance();
                    extension = ".gpx";
                }
                else {
                    this.writer = GpxWriter.getInstance();
                    extension = ".gpx";
                    DebugLogger.error("Invalid file extension chosen. Assuming .gpx");
                }
                path = fc.getCurrentDirectory().toString();
                this.settings.setGpxPath(path);
                String fileName = path + "/" + fc.getSelectedFile().getName().toString();
                if (!fileName.toLowerCase().endsWith(extension)) {
                    fileName += extension;
                }
                this.writer.writeWaypointsToFile(fileName);
                this.jTextAreaOutput.setText("File saved to " + fileName + "!\n");
            }
            if (returnValue == 1) {
                this.jTextAreaOutput.setText("File not saved!\n");
            }
        }
        else {
            this.jTextAreaOutput.setText("No waypoints found. Nothing to save\n");
        }
    }
    
    private void showWaypoints() {
        this.jTextAreaOutput.removeAll();
        final WaypointLog waypointLog = WaypointLog.getInstance();
        if (waypointLog.getNumberOfEntries() > 0) {
            final MapFrame mapFrame = new MapFrame();
            final String resultString = mapFrame.showWaypoints();
            this.jTextAreaOutput.setText(resultString);
        } else {
            this.jTextAreaOutput.setText("There are no waypoints to show");
        }
    }
    
    private void saveRouteToGpx() {
        final RouteLog log = RouteLog.getInstance();
        if (log.getNumberOfEntries() > 0) {
            final JFileChooser fc = new JFileChooser();
            String path = this.settings.getGpxPath();
            if (!path.equals("")) {
                fc.setCurrentDirectory(new File(path));
            }
            fc.setFileFilter(this.gpxFileFilter);
            fc.setFileFilter(this.csvFileFilter);
            fc.setAcceptAllFileFilterUsed(false);
            if (this.settings.getOutputFileType().equals("CSV")) {
                fc.setFileFilter(this.csvFileFilter);
            } else {
                fc.setFileFilter(this.gpxFileFilter);
            }
            final int returnValue = fc.showSaveDialog(this.mainPanel);
            if (returnValue == 0) {
                final FileFilter filter = fc.getFileFilter();
                String extension;
                if (filter.equals(this.csvFileFilter)) {
                    this.writer = CsvWriter.getInstance();
                    extension = ".txt";
                }
                else if (filter.equals(this.gpxFileFilter)) {
                    this.writer = GpxWriter.getInstance();
                    extension = ".gpx";
                }
                else {
                    this.writer = GpxWriter.getInstance();
                    extension = ".gpx";
                    DebugLogger.error("Invalid file extension chosen. Assuming .gpx");
                }
                path = fc.getCurrentDirectory().toString();
                this.settings.setGpxPath(path);
                String fileName = path + "/" + fc.getSelectedFile().getName().toString();
                if (!fileName.toLowerCase().endsWith(extension)) {
                    fileName += extension;
                }
                this.writer.writeRouteToFile(fileName);
                this.jTextAreaOutput.setText("File saved to " + fileName + "!\n");
            }
            if (returnValue == 1) {
                this.jTextAreaOutput.setText("File not saved!\n");
            }
        }
        else {
            this.jTextAreaOutput.setText("No route found. Nothing to save\n");
        }
    }
    
    private boolean readRouteFromGpx() {
        boolean fileRead = false;
        final JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Read route");
        String path = this.settings.getGpxPath();
        if (!path.equals("")) {
            fc.setCurrentDirectory(new File(path));
        }
        fc.setFileFilter(this.gpxFileFilter);
        fc.setAcceptAllFileFilterUsed(false);
        final int returnValue = fc.showOpenDialog(this.mainPanel);
        if (returnValue == 0) {
            path = fc.getCurrentDirectory().toString();
            this.settings.setGpxPath(path);
            String fileName = path + "/" + fc.getSelectedFile().getName().toString();
            if (!fileName.toLowerCase().endsWith(".gpx")) {
                fileName += ".gpx";
            }
            final GpxReader reader = GpxReader.getInstance();
            reader.readRouteFromFile(fileName);
            this.jTextAreaOutput.setText("File read from " + fileName + "!\n");
            fileRead = true;
        }
        if (returnValue == 1) {
            this.jTextAreaOutput.setText("No file read!\n");
        }
        return fileRead;
    }
    
    private void showRoute() {
        this.jTextAreaOutput.removeAll();
        final RouteLog routeLog = RouteLog.getInstance();
        if (routeLog.getNumberOfEntries() > 0) {
            final MapFrame mapFrame = new MapFrame();
            final String resultString = mapFrame.showRoute();
            this.jTextAreaOutput.setText(resultString);
        } else {
            this.jTextAreaOutput.setText("There is no route to show");
        }
    }
    
    private void saveTrackToGpx() {
        final int selection = this.jListTracks.getSelectedIndex();
        if (selection >= 0) {
            final JFileChooser fc = new JFileChooser();
            String path = this.settings.getGpxPath();
            if (!path.equals("")) {
                fc.setCurrentDirectory(new File(path));
            }
            fc.addChoosableFileFilter(this.gpxFileFilter);
            fc.addChoosableFileFilter(this.tcxFileFilter);
            fc.addChoosableFileFilter(this.csvFileFilter);
            fc.setAcceptAllFileFilterUsed(false);
            if (this.settings.getOutputFileType().equals("CSV")) {
                fc.setFileFilter(this.csvFileFilter);
            }
            else if (this.settings.getOutputFileType().equals("TCX")) {
                fc.setFileFilter(this.tcxFileFilter);
            }
            else {
                fc.setFileFilter(this.gpxFileFilter);
            }
            final int returnValue = fc.showSaveDialog(this.mainPanel);
            if (returnValue == 0) {
                final FileFilter filter = fc.getFileFilter();
                String extension;
                if (filter.equals(this.csvFileFilter)) {
                    this.writer = CsvWriter.getInstance();
                    extension = ".txt";
                }
                else if (filter.equals(this.tcxFileFilter)) {
                    this.writer = TcxWriter.getInstance();
                    extension = ".tcx";
                }
                else if (filter.equals(this.gpxFileFilter)) {
                    this.writer = GpxWriter.getInstance();
                    extension = ".gpx";
                }
                else {
                    this.writer = GpxWriter.getInstance();
                    extension = ".gpx";
                    DebugLogger.error("Invalid file extension chosen. Assuming .gpx");
                }
                path = fc.getCurrentDirectory().toString();
                this.settings.setGpxPath(path);
                String fileName = path + "/" + fc.getSelectedFile().getName().toString();
                if (!fileName.toLowerCase().endsWith(extension)) {
                    fileName += extension;
                }
                this.writer.writeTrackToFile(fileName, selection, "track" + selection);
                this.jTextAreaOutput.setText("File saved to " + fileName + "!\n");
            }
            if (returnValue == 1) {
                this.jTextAreaOutput.setText("File not saved!\n");
            }
        } else {
            this.jTextAreaOutput.setText("First, select a track...\n");
        }
    }
    
    private void saveHeartRate() {
        final int selection = this.jListTracks.getSelectedIndex();
        if (selection >= 0) {
            final JFileChooser fc = new JFileChooser();
            String path = this.settings.getGpxPath();
            if (!path.equals("")) {
                fc.setCurrentDirectory(new File(path));
            }
            fc.addChoosableFileFilter(this.csvFileFilter);
            fc.setAcceptAllFileFilterUsed(false);
            fc.setFileFilter(this.csvFileFilter);
            final int returnValue = fc.showSaveDialog(this.mainPanel);
            if (returnValue == 0) {
                this.writer = CsvWriter.getInstance();
                final String extension = ".txt";
                path = fc.getCurrentDirectory().toString();
                this.settings.setGpxPath(path);
                String fileName = path + "/" + fc.getSelectedFile().getName().toString();
                if (!fileName.toLowerCase().endsWith(extension)) {
                    fileName += extension;
                }
                this.writer.writeHeartRateToFile(fileName, selection);
                this.jTextAreaOutput.setText("File saved to " + fileName + "!\n");
            }
            if (returnValue == 1) {
                this.jTextAreaOutput.setText("File not saved!\n");
            }
        }
        else {
            this.jTextAreaOutput.setText("First, select a track...\n");
        }
    }
    
    private void saveDeviceLogToTextFile() {
        final DeviceLog deviceLog = DeviceLog.getInstance();
        if (deviceLog.getNumberOfEntries() > 0) {
            final JFileChooser fc = new JFileChooser();
            String path = this.settings.getLogPath();
            if (!path.equals("")) {
                fc.setCurrentDirectory(new File(path));
            }
            final FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files (*.txt)", new String[] { "TXT" });
            fc.setFileFilter(filter);
            final int returnValue = fc.showSaveDialog(this.mainPanel);
            if (returnValue == 0) {
                path = fc.getCurrentDirectory().toString();
                this.settings.setLogPath(path);
                String fileName = path + "/" + fc.getSelectedFile().getName().toString();
                if (!fileName.toLowerCase().endsWith(".txt")) {
                    fileName += ".txt";
                }
                deviceLog.writeToFile(fileName);
                this.jTextAreaOutput.setText("File saved to " + fileName + "!\n");
            }
            if (returnValue == 1) {
                this.jTextAreaOutput.setText("File not saved!\n");
            }
        }
        else {
            this.jTextAreaOutput.setText("No device log entries found. First download tracks\n");
        }
    }
    
    void processDownloadResults() {
        final TrackLog log = TrackLog.getInstance();
        final String outputString = this.process.getResult();
        this.jTextAreaOutput.setText(outputString);
        final DefaultListModel model = (DefaultListModel)this.jListTracks.getModel();
        model.clear();
        final int numberOfTracks = log.getNumberOfTracks();
        for (int i = 0; i < numberOfTracks; ++i) {
            final String description = log.getTrackDescription(i);
            model.addElement(description);
        }
        this.jListTracks.setSelectedIndex(numberOfTracks - 1);
    }
    
    void processResults() {
        final String outputString = this.process.getResult();
        this.jTextAreaOutput.setText(outputString);
    }
    
    public void reportProgress(final int progress) {
        this.jProgressBarDownload.setValue(progress);
        if (progress == 100 && this.executingCommand) {
            switch (this.command.command) {
                case COMMAND_GETDEVICETYPE:
                case COMMAND_GETINFO:
                case COMMAND_SCANFLASH:
                case COMMAND_SAVESIMULATIONSET:
                case COMMAND_SAVEDEVICESETTINGS:
                case COMMAND_SAVEDEVICESETTINGSASTEXT:
                case COMMAND_VERIFYCACHEFILE:
                case COMMAND_ERASEROUTE: {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UgotmeView.this.processResults();
                        }
                    });
                    break;
                }
                case COMMAND_DOWNLOADTRACKS: {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UgotmeView.this.processDownloadResults();
                        }
                    });
                    break;
                }
                case COMMAND_DOWNLOADWAYPOINTS: {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UgotmeView.this.processResults();
                            if (UgotmeView.this.nextAfterWaypointDownload == 1) {
                                UgotmeView.this.saveWaypointsToGpx();
                            }
                            else if (UgotmeView.this.nextAfterWaypointDownload == 2) {
                                UgotmeView.this.showWaypoints();
                            }
                        }
                    });
                    break;
                }
                case COMMAND_UPLOADROUTE: {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UgotmeView.this.processResults();
                        }
                    });
                    break;
                }
                case COMMAND_DOWNLOADROUTE: {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UgotmeView.this.processResults();
                            if (UgotmeView.this.nextAfterRouteDownload == 1) {
                                UgotmeView.this.saveRouteToGpx();
                            }
                            else {
                                UgotmeView.this.showRoute();
                            }
                        }
                    });
                    break;
                }
                case COMMAND_ERASETRACKS: {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UgotmeView.this.processResults();
                        }
                    });
                    break;
                }
            }
            this.executingCommand = false;
        }
    }
    
    private void executeCommand(final Command.CommandType commandId, final String messageWhileExecuting, final String param) {
        this.jTextAreaOutput.setText(messageWhileExecuting);
        if (!this.executingCommand) {
            final Object selectedItem = this.jComboBoxComport.getSelectedItem();
            if (selectedItem != null) {
                this.executingCommand = true;
                final String comport = selectedItem.toString();
                this.command = new Command(commandId, comport);
                this.process.executeCommand(this.command, this, param);
            }
            else {
                this.jTextAreaOutput.setText("No valid comport. First select device, than start software\n");
            }
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    public static void main(final String[] args) {
        try {
            for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(UgotmeView.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InstantiationException ex2) {
            Logger.getLogger(UgotmeView.class.getName()).log(Level.SEVERE, null, ex2);
        }
        catch (IllegalAccessException ex3) {
            Logger.getLogger(UgotmeView.class.getName()).log(Level.SEVERE, null, ex3);
        }
        catch (UnsupportedLookAndFeelException ex4) {
            Logger.getLogger(UgotmeView.class.getName()).log(Level.SEVERE, null, ex4);
        }
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UgotmeView().setVisible(true);
            }
        });
    }
    
    private void initComponents() {
        this.buttonGroup1 = new ButtonGroup();
        this.buttonGroup2 = new ButtonGroup();
        this.buttonGroup3 = new ButtonGroup();
        this.mainPanel = new JPanel();
        this.jButtonDownloadTrack = new JButton();
        this.jProgressBarDownload = new JProgressBar();
        this.jButtonGetInfo = new JButton();
        this.jLabelTracks = new JLabel();
        this.jLabelProgress = new JLabel();
        this.jButtonEraseTracks = new JButton();
        this.jLabelOutput = new JLabel();
        this.jButtonSaveTrack = new JButton();
        this.jButtonUploadRoute = new JButton();
        this.jScrollPane2 = new JScrollPane();
        this.jListTracks = new JList();
        this.jScrollPane1 = new JScrollPane();
        this.jTextAreaOutput = new JTextArea();
        this.jLabelComport = new JLabel();
        this.jComboBoxComport = new JComboBox();
        this.jButtonShowWaypoints = new JButton();
        this.jButtonShowRoute = new JButton();
        this.jButtonShowTrack = new JButton();
        this.jButtonSaveRoute = new JButton();
        this.jButtonSaveWaypoints = new JButton();
        this.jMenuBar1 = new JMenuBar();
        this.jMenuFile = new JMenu();
        this.jMenuItem2 = new JMenuItem();
        this.jMenuDevice = new JMenu();
        this.jMenuItemGetDeviceType = new JMenuItem();
        this.jMenuItemGetInfo = new JMenuItem();
        this.jMenuItemSaveDeviceLog = new JMenuItem();
        this.jMenuItemRescanComports = new JMenuItem();
        this.jMenuTracks = new JMenu();
        this.jMenuItemDownloadTracks = new JMenuItem();
        this.jMenuItemEraseTracks = new JMenuItem();
        this.jMenuItemSaveTracks = new JMenuItem();
        this.jMenuItem5 = new JMenuItem();
        this.jMenuItem6 = new JMenuItem();
        this.jMenuItem7 = new JMenuItem();
        this.jMenuItemShowSpeed = new JMenuItem();
        this.jMenuWaypoints = new JMenu();
        this.jMenuItem1 = new JMenuItem();
        this.jMenuItemShowWaypoints = new JMenuItem();
        this.jMenuRoutes = new JMenu();
        this.jMenuItemDownloadRoute = new JMenuItem();
        this.jMenuItemUploadRoute = new JMenuItem();
        this.jMenuItemShowRoute = new JMenuItem();
        this.jMenuItemDeleteRoute = new JMenuItem();
        this.jMenuOptions = new JMenu();
        this.jMenuOptions_FileOutputType = new JMenu();
        this.gpx1_0MenuItem = new JRadioButtonMenuItem();
        this.gpx1_1MenuItem = new JRadioButtonMenuItem();
        this.tcxMenuItem = new JRadioButtonMenuItem();
        this.csvMenuItem = new JRadioButtonMenuItem();
        this.jMenuItemMapType = new JMenu();
        this.roadMenuItem = new JRadioButtonMenuItem();
        this.satelliteMenuItem = new JRadioButtonMenuItem();
        this.terrainMenuItem = new JRadioButtonMenuItem();
        this.hybridMenuItem = new JRadioButtonMenuItem();
        this.jMenuItemOptions_SaveSettings = new JMenuItem();
        this.debugMenu = new JMenu();
        this.jMenu1 = new JMenu();
        this.debugLevelOffRadioButton = new JRadioButtonMenuItem();
        this.debugLevelErrorRadioButton = new JRadioButtonMenuItem();
        this.debugLevelInfoRadioButton = new JRadioButtonMenuItem();
        this.debugLevelDebugRadioButton = new JRadioButtonMenuItem();
        this.jMenuItemSaveSimSet = new JMenuItem();
        this.jMenuItemSaveDeviceSettingsBlock = new JMenuItem();
        this.jMenuItemSaveDeviceSettings = new JMenuItem();
        this.menuItemVerifyCacheFile = new JMenuItem();
        this.jMenuHelp = new JMenu();
        this.jMenuExtend = new JMenu();
        this.jMenuItemAbout = new JMenuItem();
        this.jMenuItemExtend01 = new JMenuItem();
        this.jMenuItemExtend02 = new JMenuItem();
        this.jMenuItemExtend03 = new JMenuItem();
        this.jMenuItemExtend04 = new JMenuItem();
        this.jMenuItemAbout = new JMenuItem();
        this.setDefaultCloseOperation(3);
        this.jButtonDownloadTrack.setText("Download");
        this.jButtonDownloadTrack.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionDownloadTracks(evt);
            }
        });
        this.jButtonGetInfo.setText("Get Info");
        this.jButtonGetInfo.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGetInfo(evt);
            }
        });
        this.jLabelTracks.setText("Tracks");
        this.jLabelProgress.setText("Progress");
        this.jButtonEraseTracks.setText("Erase Tracks");
        this.jButtonEraseTracks.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionEraseTracks(evt);
            }
        });
        this.jLabelOutput.setText("Info");
        this.jButtonSaveTrack.setText("Save Track");
        this.jButtonSaveTrack.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveTrack(evt);
            }
        });
        this.jButtonUploadRoute.setText("Upload Route");
        this.jButtonUploadRoute.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionUploadRoute(evt);
            }
        });
        this.jListTracks.setFont(new Font("Monospaced", 0, 11));
        this.jListTracks.setModel(new AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            
            public int getSize() {
                return this.strings.length;
            }
            
            public Object getElementAt(final int i) {
                return this.strings[i];
            }
        });
        this.jScrollPane2.setViewportView(this.jListTracks);
        this.jTextAreaOutput.setColumns(20);
        this.jTextAreaOutput.setRows(5);
        this.jScrollPane1.setViewportView(this.jTextAreaOutput);
        this.jLabelComport.setText("Comport");
        this.jComboBoxComport.setModel(new DefaultComboBoxModel<String>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        this.jButtonShowWaypoints.setText("Show Waypoints");
        this.jButtonShowWaypoints.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowWaypoints(evt);
            }
        });
        this.jButtonShowRoute.setText("Show Route");
        this.jButtonShowRoute.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowRoute(evt);
            }
        });
        this.jButtonShowTrack.setText("Show Track");
        this.jButtonShowTrack.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowTrack(evt);
            }
        });
        this.jButtonSaveRoute.setText("Save Route");
        this.jButtonSaveRoute.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveRoute(evt);
            }
        });
        this.jButtonSaveWaypoints.setText("Save Waypoints");
        this.jButtonSaveWaypoints.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveWaypointLog(evt);
            }
        });
        final GroupLayout mainPanelLayout = new GroupLayout(this.mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);

        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup().addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane1)
                    .addGroup(mainPanelLayout.createSequentialGroup().addComponent(this.jLabelProgress).addGap(18, 18, 18).addComponent(this.jProgressBarDownload, -1, -1, 32767))
                        .addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup().addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(mainPanelLayout.createSequentialGroup().addComponent(this.jLabelComport).addGap(19, 19, 19).addComponent(this.jComboBoxComport, -2, 153, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.jButtonDownloadTrack, -2, 125, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButtonSaveTrack, -2, 109, -2)).addGroup(mainPanelLayout.createSequentialGroup().addGap(0, 0, 32767).addComponent(this.jButtonEraseTracks, -2, 109, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButtonShowTrack))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(mainPanelLayout.createSequentialGroup().addComponent(this.jButtonSaveWaypoints).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButtonSaveRoute).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButtonGetInfo, -2, 109, -2)).addGroup(GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup().addComponent(this.jButtonShowWaypoints).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButtonShowRoute).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButtonUploadRoute, -2, 109, -2)))).addComponent(this.jScrollPane2).addGroup(mainPanelLayout.createSequentialGroup().addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabelOutput).addComponent(this.jLabelTracks)).addGap(0, 0, 32767))).addContainerGap()));

        mainPanelLayout.linkSize(0, this.jButtonDownloadTrack, this.jButtonEraseTracks, this.jButtonGetInfo, this.jButtonSaveRoute, this.jButtonSaveTrack, this.jButtonSaveWaypoints, this.jButtonShowRoute, this.jButtonShowTrack, this.jButtonShowWaypoints, this.jButtonUploadRoute);

        mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(mainPanelLayout.createSequentialGroup().addContainerGap().addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jComboBoxComport, -2, -1, -2).addComponent(this.jButtonDownloadTrack).addComponent(this.jButtonSaveTrack).addComponent(this.jButtonGetInfo).addComponent(this.jLabelComport).addComponent(this.jButtonSaveRoute).addComponent(this.jButtonSaveWaypoints)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jButtonShowWaypoints).addComponent(this.jButtonShowRoute).addComponent(this.jButtonUploadRoute).addComponent(this.jButtonShowTrack).addComponent(this.jButtonEraseTracks)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jLabelProgress).addComponent(this.jProgressBarDownload, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabelTracks).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane2, -2, 256, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabelOutput).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane1, -2, -1, -2).addContainerGap(-1, 32767)));


        this.jMenuFile.setText("File");
        this.jMenuItem2.setAccelerator(KeyStroke.getKeyStroke(81, 2));
        
                        this.jMenuItem2.setText("Quit");
        this.jMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionQuit(evt);
            }
        });
        this.jMenuFile.add(this.jMenuItem2);
        this.jMenuBar1.add(this.jMenuFile);
        this.jMenuDevice.setText("Device");
        this.jMenuItemGetDeviceType.setText("Get Device Type");
        this.jMenuItemGetDeviceType.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGetDeviceType(evt);
            }
        });
        this.jMenuDevice.add(this.jMenuItemGetDeviceType);
        this.jMenuItemGetInfo.setText("Get Info");
        this.jMenuItemGetInfo.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGetInfo(evt);
            }
        });
        this.jMenuDevice.add(this.jMenuItemGetInfo);
        this.jMenuItemSaveDeviceLog.setText("Save Device Log");
        this.jMenuItemSaveDeviceLog.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveDeviceLog(evt);
            }
        });
        this.jMenuDevice.add(this.jMenuItemSaveDeviceLog);
        this.jMenuItemRescanComports.setText("Rescan Comports");
        this.jMenuItemRescanComports.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionRescanComports(evt);
            }
        });
        this.jMenuDevice.add(this.jMenuItemRescanComports);
        this.jMenuBar1.add(this.jMenuDevice);
        this.jMenuTracks.setText("Tracks");
        this.jMenuItemDownloadTracks.setText("Download Tracks");
        this.jMenuItemDownloadTracks.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionDownloadTracks(evt);
            }
        });
        this.jMenuTracks.add(this.jMenuItemDownloadTracks);
        this.jMenuItemEraseTracks.setText("Erase Tracks");
        this.jMenuItemEraseTracks.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionEraseTracks(evt);
            }
        });
        this.jMenuTracks.add(this.jMenuItemEraseTracks);
        this.jMenuItemSaveTracks.setText("Save Selected Track");
        this.jMenuItemSaveTracks.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveTrack(evt);
            }
        });
        this.jMenuTracks.add(this.jMenuItemSaveTracks);
        this.jMenuItem5.setText("Show Selected Track");
        this.jMenuItem5.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowTrack(evt);
            }
        });
        this.jMenuTracks.add(this.jMenuItem5);
        this.jMenuItem6.setText("Save Heartrate");
        this.jMenuItem6.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveHeartrate(evt);
            }
        });
        this.jMenuTracks.add(this.jMenuItem6);
        this.jMenuItem7.setText("Show Heartrate");
        this.jMenuItem7.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionPlotHeartrate(evt);
            }
        });
        this.jMenuTracks.add(this.jMenuItem7);
        this.jMenuItemShowSpeed.setText("Show Speed");
        this.jMenuItemShowSpeed.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowSpeed(evt);
            }
        });
        this.jMenuTracks.add(this.jMenuItemShowSpeed);
        this.jMenuBar1.add(this.jMenuTracks);
        this.jMenuWaypoints.setText("Waypoints");
        this.jMenuItem1.setText("Save Waypoints");
        this.jMenuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveWaypointLog(evt);
            }
        });
        this.jMenuWaypoints.add(this.jMenuItem1);
        this.jMenuItemShowWaypoints.setText("Show Waypoints");
        this.jMenuItemShowWaypoints.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowWaypoints(evt);
            }
        });
        this.jMenuWaypoints.add(this.jMenuItemShowWaypoints);
        this.jMenuBar1.add(this.jMenuWaypoints);

// Routes menu
        this.jMenuRoutes.setText("Routes");
        this.jMenuItemDownloadRoute.setText("Save Route");
        this.jMenuItemDownloadRoute.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveRoute(evt);
            }
        });
        this.jMenuRoutes.add(this.jMenuItemDownloadRoute);
        this.jMenuItemUploadRoute.setText("Upload Route");
        this.jMenuItemUploadRoute.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionUploadRoute(evt);
            }
        });
        this.jMenuRoutes.add(this.jMenuItemUploadRoute);
        this.jMenuItemShowRoute.setText("Show Route");
        this.jMenuItemShowRoute.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowRoute(evt);
            }
        });
        this.jMenuRoutes.add(this.jMenuItemShowRoute);
        this.jMenuItemDeleteRoute.setText("Erase Route");
        this.jMenuItemDeleteRoute.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionDeleteRoute(evt);
            }
        });
        this.jMenuRoutes.add(this.jMenuItemDeleteRoute);
        this.jMenuBar1.add(this.jMenuRoutes);

// Options menu
        this.jMenuOptions.setText("Options");
        this.jMenuOptions_FileOutputType.setText("File Output Format");
        this.jMenuOptions_FileOutputType.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGpxVersion(evt);
            }
        });
        this.buttonGroup2.add(this.gpx1_0MenuItem);
        this.gpx1_0MenuItem.setSelected(true);
        this.gpx1_0MenuItem.setText("GPX 1.0");
        this.gpx1_0MenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGpxVersion(evt);
            }
        });
        this.jMenuOptions_FileOutputType.add(this.gpx1_0MenuItem);
        this.buttonGroup2.add(this.gpx1_1MenuItem);
        this.gpx1_1MenuItem.setText("GPX 1.1");
        this.gpx1_1MenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGpxVersion(evt);
            }
        });
        this.jMenuOptions_FileOutputType.add(this.gpx1_1MenuItem);
        this.buttonGroup2.add(this.tcxMenuItem);
        this.tcxMenuItem.setText("TCX");
        this.tcxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGpxVersion(evt);
            }
        });
        this.jMenuOptions_FileOutputType.add(this.tcxMenuItem);
        this.buttonGroup2.add(this.csvMenuItem);
        this.csvMenuItem.setText("CSV");
        this.csvMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionGpxVersion(evt);
            }
        });
        this.jMenuOptions_FileOutputType.add(this.csvMenuItem);
        this.jMenuOptions.add(this.jMenuOptions_FileOutputType);
        this.jMenuItemMapType.setText("Map Type");
        this.buttonGroup3.add(this.roadMenuItem);
        this.roadMenuItem.setSelected(true);
        this.roadMenuItem.setText("Road");
        this.roadMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionMapButton(evt);
            }
        });
        this.jMenuItemMapType.add(this.roadMenuItem);
        this.buttonGroup3.add(this.satelliteMenuItem);
        this.satelliteMenuItem.setText("Satellite");
        this.satelliteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionMapButton(evt);
            }
        });
        this.jMenuItemMapType.add(this.satelliteMenuItem);
        this.buttonGroup3.add(this.terrainMenuItem);
        this.terrainMenuItem.setText("Terrain");
        this.terrainMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionMapButton(evt);
            }
        });
        this.jMenuItemMapType.add(this.terrainMenuItem);
        this.buttonGroup3.add(this.hybridMenuItem);
        this.hybridMenuItem.setText("Hybrid");
        this.hybridMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionMapButton(evt);
            }
        });
        this.jMenuItemMapType.add(this.hybridMenuItem);
        this.jMenuOptions.add(this.jMenuItemMapType);
        this.jMenuItemOptions_SaveSettings.setText("Save Settings");
        this.jMenuItemOptions_SaveSettings.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveSettings(evt);
            }
        });
        this.jMenuOptions.add(this.jMenuItemOptions_SaveSettings);
        this.jMenuBar1.add(this.jMenuOptions);
        this.debugMenu.setText("Debug");
        this.jMenu1.setText("Debug");
        this.buttonGroup1.add(this.debugLevelOffRadioButton);
        this.debugLevelOffRadioButton.setSelected(true);
        this.debugLevelOffRadioButton.setText("Off");
        this.debugLevelOffRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionDebugRadioButton(evt);
            }
        });
        this.jMenu1.add(this.debugLevelOffRadioButton);
        this.buttonGroup1.add(this.debugLevelErrorRadioButton);
        this.debugLevelErrorRadioButton.setText("Errors");
        this.debugLevelErrorRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionDebugRadioButton(evt);
            }
        });
        this.jMenu1.add(this.debugLevelErrorRadioButton);
        this.buttonGroup1.add(this.debugLevelInfoRadioButton);
        this.debugLevelInfoRadioButton.setText("Info");
        this.debugLevelInfoRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionDebugRadioButton(evt);
            }
        });
        this.jMenu1.add(this.debugLevelInfoRadioButton);
        this.buttonGroup1.add(this.debugLevelDebugRadioButton);
        this.debugLevelDebugRadioButton.setText("Debug");
        this.debugLevelDebugRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionDebugRadioButton(evt);
            }
        });
        this.jMenu1.add(this.debugLevelDebugRadioButton);
        this.debugMenu.add(this.jMenu1);
        this.jMenuItemSaveSimSet.setText("Save Simulation Set");
        this.jMenuItemSaveSimSet.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveSimulationSet(evt);
            }
        });
        this.debugMenu.add(this.jMenuItemSaveSimSet);
        this.jMenuItemSaveDeviceSettingsBlock.setText("Save Device Settings Block");
        this.jMenuItemSaveDeviceSettingsBlock.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveDeviceSettingsBlock(evt);
            }
        });
        this.debugMenu.add(this.jMenuItemSaveDeviceSettingsBlock);
        this.jMenuItemSaveDeviceSettings.setText("Save Device Settings");
        this.jMenuItemSaveDeviceSettings.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionSaveDeviceSettings(evt);
            }
        });
        this.debugMenu.add(this.jMenuItemSaveDeviceSettings);
        this.menuItemVerifyCacheFile.setText("Verify Cache File");
        this.menuItemVerifyCacheFile.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionVerifyCacheFile(evt);
            }
        });
        this.debugMenu.add(this.menuItemVerifyCacheFile);
        this.jMenuBar1.add(this.debugMenu);

// Help menu
        this.jMenuHelp.setText("Help");
        this.jMenuItemAbout.setText("About");
        this.jMenuItemAbout.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowAboutBox(evt);
            }
        });
        this.jMenuHelp.add(this.jMenuItemAbout);
        this.jMenuBar1.add(this.jMenuHelp);

// My own new menu
        this.jMenuExtend.setText("Extend");
        this.jMenuItemExtend01.setText("Scan flash");
        this.jMenuItemExtend01.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                //UgotmeView.this.actionShowAboutBox(evt);
                UgotmeView.this.actionScanFlash(evt);
            }
        });
        this.jMenuExtend.add(this.jMenuItemExtend01);

        this.jMenuItemExtend02.setText("Scan flash for unoque string");
        this.jMenuItemExtend02.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionScanFlashForUniqueStrings(evt);
            }
        });
        this.jMenuExtend.add(this.jMenuItemExtend02);
/*
        this.jMenuItemExtend03.setText("Command 03");
        this.jMenuItemExtend03.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowAboutBox(evt);
            }
        });
        this.jMenuExtend.add(this.jMenuItemExtend03);

        this.jMenuItemExtend04.setText("Command 04");
        this.jMenuItemExtend04.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent evt) {
                UgotmeView.this.actionShowAboutBox(evt);
            }
        });
        this.jMenuExtend.add(this.jMenuItemExtend04);
*/

        this.jMenuBar1.add(this.jMenuExtend);


        this.setJMenuBar(this.jMenuBar1);
        final GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.mainPanel, GroupLayout.Alignment.TRAILING, -1, -1, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.mainPanel, -2, -1, -2));
        this.pack();
    }
    
    private void actionDownloadTracks(final ActionEvent evt) {
        if (!this.executingCommand) {
            this.trackListModel.clear();
        }
        this.executeCommand(Command.CommandType.COMMAND_DOWNLOADTRACKS, "Downloading tracks...", null);
    }
    
    private void actionSaveTrack(final ActionEvent evt) {
        if (!this.executingCommand) {
            this.jTextAreaOutput.removeAll();
            this.saveTrackToGpx();
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionEraseTracks(final ActionEvent evt) {
        this.jTextAreaOutput.removeAll();
        if (!this.executingCommand) {
            final int confirm = JOptionPane.showConfirmDialog(this.mainPanel, "Are you sure to erase the tracklog on the device?", "Confirm erase", 2);
            if (confirm == 0) {
                this.executeCommand(Command.CommandType.COMMAND_ERASETRACKS, "Erasing... Do not unconnect the device!", null);
            }
            else {
                this.jTextAreaOutput.setText("Erasing canceled");
            }
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionUploadRoute(final ActionEvent evt) {
        if (!this.executingCommand) {
            final boolean fileRead = this.readRouteFromGpx();
            if (fileRead) {
                final RouteLog routeLog = RouteLog.getInstance();
                if (routeLog.getNumberOfEntries() > 0) {
                    this.executeCommand(Command.CommandType.COMMAND_UPLOADROUTE, "Uploading route... Don't disconnect device!", null);
                }
                else {
                    this.jTextAreaOutput.setText("No waypoints in route\n");
                }
            }
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionGetInfo(final ActionEvent evt) {
        this.executeCommand(Command.CommandType.COMMAND_GETINFO, "Downloading info...", null);
    }
    
    private void actionScanFlash(final ActionEvent evt) {
        this.executeCommand(Command.CommandType.COMMAND_SCANFLASH, "Scanning flash...", null);
    }
    
    private void actionScanFlashForUniqueStrings(final ActionEvent evt) {
        this.executeCommand(Command.CommandType.COMMAND_SCANFLASHFORUNIQUESTRINGS, "Scanning flash for unique strings...", null);
    }
    
    private void actionDebugRadioButton(final ActionEvent evt) {
        if (this.debugLevelOffRadioButton.isSelected()) {
            DebugLogger.setDebugLevel(3);
            this.jTextAreaOutput.setText("Debug level: off\n");
        }
        else if (this.debugLevelInfoRadioButton.isSelected()) {
            DebugLogger.setDebugLevel(1);
            this.jTextAreaOutput.setText("Debug level: info\n");
        }
        else if (this.debugLevelDebugRadioButton.isSelected()) {
            DebugLogger.setDebugLevel(0);
            this.jTextAreaOutput.setText("Debug level: debug\n");
        }
        else if (this.debugLevelErrorRadioButton.isSelected()) {
            DebugLogger.setDebugLevel(2);
            this.jTextAreaOutput.setText("Debug level: error\n");
        }
        else {
            DebugLogger.setDebugLevel(2);
            DebugLogger.debug("Debug radio button not selected");
        }
    }
    
    private void actionSaveWaypointLog(final ActionEvent evt) {
        this.nextAfterWaypointDownload = 1;
        this.executeCommand(Command.CommandType.COMMAND_DOWNLOADWAYPOINTS, "Downloading waypoints...", null);
    }
    
    private void actionGetDeviceType(final ActionEvent evt) {
        this.executeCommand(Command.CommandType.COMMAND_GETDEVICETYPE, "Retrieving device type...", null);
    }
    
    private void actionSaveRoute(final ActionEvent evt) {
        this.nextAfterRouteDownload = 1;
        this.executeCommand(Command.CommandType.COMMAND_DOWNLOADROUTE, "Downloading route...", null);
    }
    
    private void actionSaveDeviceLog(final ActionEvent evt) {
        if (!this.executingCommand) {
            this.jTextAreaOutput.removeAll();
            this.saveDeviceLogToTextFile();
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionSaveSettings(final ActionEvent evt) {
        final Object selectedItem = this.jComboBoxComport.getSelectedItem();
        if (selectedItem != null) {
            this.settings.setComport(selectedItem.toString());
        }
        else {
            DebugLogger.info("Uhm.. no comport selected when saving settings");
        }
        if (this.debugLevelOffRadioButton.isSelected()) {
            this.settings.setDebugLevel(3);
        }
        else if (this.debugLevelInfoRadioButton.isSelected()) {
            this.settings.setDebugLevel(1);
        }
        else if (this.debugLevelDebugRadioButton.isSelected()) {
            this.settings.setDebugLevel(0);
        }
        else if (this.debugLevelErrorRadioButton.isSelected()) {
            this.settings.setDebugLevel(2);
        }
        else {
            this.settings.setDebugLevel(2);
            DebugLogger.debug("Debug radio button not selected when saving settings");
        }
        if (this.gpx1_0MenuItem.isSelected()) {
            this.settings.setOutputFileType("GPX");
            this.settings.setGpxVersion("1.0");
        }
        else if (this.tcxMenuItem.isSelected()) {
            this.settings.setOutputFileType("TCX");
            this.settings.setGpxVersion("1.1");
        }
        else if (this.csvMenuItem.isSelected()) {
            this.settings.setOutputFileType("CSV");
            this.settings.setGpxVersion("1.1");
        }
        else {
            this.settings.setOutputFileType("GPX");
            this.settings.setGpxVersion("1.1");
        }
        if (this.roadMenuItem.isSelected()) {
            this.settings.setMapType("roadmap");
        }
        else if (this.satelliteMenuItem.isSelected()) {
            this.settings.setMapType("satellite");
        }
        else if (this.terrainMenuItem.isSelected()) {
            this.settings.setMapType("terrain");
        }
        else if (this.hybridMenuItem.isSelected()) {
            this.settings.setMapType("hybrid");
        }
        else {
            this.settings.setMapType("roadmap");
            DebugLogger.debug("Map type not selected when saving settings");
        }
        this.settings.writeSettings();
        this.jTextAreaOutput.setText("Settings written to " + this.settings.getPropertyFileName());
    }
    
    private void actionGpxVersion(final ActionEvent evt) {
        if (this.gpx1_0MenuItem.isSelected()) {
            this.settings.setOutputFileType("GPX");
            this.settings.setGpxVersion("1.0");
            final GpxWriter gpxWriter = GpxWriter.getInstance();
            gpxWriter.setGpxVersion("1.0");
            this.jTextAreaOutput.setText("Output file type set to GPX version set to 1.0\n");
        }
        else if (this.gpx1_1MenuItem.isSelected()) {
            this.settings.setOutputFileType("GPX");
            this.settings.setGpxVersion("1.1");
            final GpxWriter gpxWriter = GpxWriter.getInstance();
            gpxWriter.setGpxVersion("1.1");
            this.jTextAreaOutput.setText("Output file type set to GPX version set to 1.1\n");
        }
        else if (this.tcxMenuItem.isSelected()) {
            this.settings.setOutputFileType("TCX");
            this.jTextAreaOutput.setText("Output file type set to TCX\n");
        }
        else if (this.csvMenuItem.isSelected()) {
            this.settings.setOutputFileType("CSV");
            this.jTextAreaOutput.setText("Output file type set to CSV\n");
        }
    }
    
    private void actionShowTrack(final ActionEvent evt) {
        final int selection = this.jListTracks.getSelectedIndex();
        if (selection >= 0) {
            final MapFrame mapFrame = new MapFrame();
            final String resultString = mapFrame.showTrack(selection);
            this.jTextAreaOutput.removeAll();
            this.jTextAreaOutput.setText(resultString);
        }
        else {
            this.jTextAreaOutput.setText("First, download tracks and select one...\n");
        }
    }
    
    private void actionShowWaypoints(final ActionEvent evt) {
        this.nextAfterWaypointDownload = 2;
        this.executeCommand(Command.CommandType.COMMAND_DOWNLOADWAYPOINTS, "Downloading waypoints...", null);
    }
    
    private void actionRescanComports(final ActionEvent evt) {
        this.updatePortList();
    }
    
    private void actionShowRoute(final ActionEvent evt) {
        this.nextAfterRouteDownload = 2;
        this.executeCommand(Command.CommandType.COMMAND_DOWNLOADROUTE, "Downloading route...", null);
    }
    
    private void actionMapButton(final ActionEvent evt) {
        if (this.roadMenuItem.isSelected()) {
            MapFrame.setMapType("roadmap");
            this.jTextAreaOutput.setText("Map type set to 'road'\n");
        }
        else if (this.satelliteMenuItem.isSelected()) {
            MapFrame.setMapType("satellite");
            this.jTextAreaOutput.setText("Map type set to 'satellite'\n");
        }
        else if (this.terrainMenuItem.isSelected()) {
            MapFrame.setMapType("terrain");
            this.jTextAreaOutput.setText("Map type set to 'terrain'\n");
        }
        else if (this.hybridMenuItem.isSelected()) {
            MapFrame.setMapType("hybrid");
            this.jTextAreaOutput.setText("Map type set to 'hybrid'\n");
        }
    }
    
    private void actionShowAboutBox(final ActionEvent evt) {
        if (this.aboutBox == null) {
            (this.aboutBox = new UgotmeAboutBox(this, true)).setLocationRelativeTo(this);
        }
        UgotmeApp.getApplication().show(this.aboutBox);
    }
    
    private void actionSaveSimulationSet(final ActionEvent evt) {
        if (!this.executingCommand) {
            this.jTextAreaOutput.removeAll();
            this.executeCommand(Command.CommandType.COMMAND_SAVESIMULATIONSET, "Save simulation set...", this.settings.getLogPath());
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionSaveDeviceSettingsBlock(final ActionEvent evt) {
        if (!this.executingCommand) {
            this.jTextAreaOutput.removeAll();
            this.executeCommand(Command.CommandType.COMMAND_SAVEDEVICESETTINGS, "Save device settings...", this.settings.getLogPath());
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionSaveDeviceSettings(final ActionEvent evt) {
        if (!this.executingCommand) {
            this.jTextAreaOutput.removeAll();
            this.executeCommand(Command.CommandType.COMMAND_SAVEDEVICESETTINGSASTEXT, "Save device settings as text file...", this.settings.getLogPath());
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionSaveHeartrate(final ActionEvent evt) {
        if (!this.executingCommand) {
            this.jTextAreaOutput.removeAll();
            this.saveHeartRate();
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionPlotHeartrate(final ActionEvent evt) {
        this.jTextAreaOutput.removeAll();
        final int selection = this.jListTracks.getSelectedIndex();
        if (selection >= 0) {
            final TrackLog trackLog = TrackLog.getInstance();
            final List heartRateLog = trackLog.getTrackHeartRateLog(selection);
            if (heartRateLog != null || heartRateLog.size() == 0) {
                final GraphFrame frame = new GraphFrame();
                final String resultString = frame.showTrackHeartRateLog("Track " + selection, heartRateLog);
                this.jTextAreaOutput.setText(resultString);
            }
        }
        else {
            this.jTextAreaOutput.setText("First, select a track...\n");
        }
    }
    
    private void actionDeleteRoute(final ActionEvent evt) {
        this.jTextAreaOutput.removeAll();
        if (!this.executingCommand) {
            final int confirm = JOptionPane.showConfirmDialog(this.mainPanel, "Are you sure to erase route from device?", "Confirm erase", 2);
            if (confirm == 0) {
                this.executeCommand(Command.CommandType.COMMAND_ERASEROUTE, "Erasing route... Don't disconnect device!", null);
            }
            else {
                this.jTextAreaOutput.setText("Erasing canceled");
            }
        }
        else {
            this.jTextAreaOutput.setText("A command is being executed already, please wait\n");
        }
    }
    
    private void actionQuit(final ActionEvent evt) {
        System.exit(0);
    }
    
    private void actionVerifyCacheFile(final ActionEvent evt) {
        this.executeCommand(Command.CommandType.COMMAND_VERIFYCACHEFILE, "Verifying cache file...", null);
    }
    
    private void actionShowSpeed(final ActionEvent evt) {
        this.jTextAreaOutput.removeAll();
        final int selection = this.jListTracks.getSelectedIndex();
        if (selection >= 0) {
            final TrackLog trackLog = TrackLog.getInstance();
            final GraphFrame frame = new GraphFrame();
            final String resultString = frame.showTrackSpeed("Track " + selection, trackLog.getTrack(selection));
            this.jTextAreaOutput.setText(resultString);
        }
        else {
            this.jTextAreaOutput.setText("First, select a track...\n");
        }
    }
}
