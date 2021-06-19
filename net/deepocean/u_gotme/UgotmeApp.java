// 
// Decompiled by Procyon v0.5.36
// 

package net.deepocean.u_gotme;

import org.jdesktop.application.Application;
import java.awt.Window;
import javax.swing.ImageIcon;
import org.jdesktop.application.ResourceMap;
import java.util.List;
import java.awt.Image;
import java.util.ArrayList;
import org.jdesktop.application.SingleFrameApplication;

public class UgotmeApp extends SingleFrameApplication
{
    protected void startup() {
        final Settings settings = Settings.getInstance();
        DebugLogger.setDebugLevel(settings.getDebugLevel());
        final UgotmeView view = new UgotmeView();
        view.setVisible(true);
        final ResourceMap resourceMap = this.getMainView().getResourceMap();
        System.out.println("Resource Dir " + resourceMap.getResourcesDir());
        final ArrayList<Image> iconList = new ArrayList<Image>();
        ImageIcon icon = resourceMap.getImageIcon("Application.icon16");
        iconList.add(icon.getImage());
        icon = resourceMap.getImageIcon("Application.icon24");
        iconList.add(icon.getImage());
        icon = resourceMap.getImageIcon("Application.icon32");
        iconList.add(icon.getImage());
        icon = resourceMap.getImageIcon("Application.icon42");
        iconList.add(icon.getImage());
        view.setIconImages(iconList);
    }
    
    protected void configureWindow(final Window root) {
        final ResourceMap resourceMap = this.getMainView().getResourceMap();
        System.out.println("Resource Dir " + resourceMap.getResourcesDir());
        final ArrayList<Image> iconList = new ArrayList<Image>();
        ImageIcon icon = resourceMap.getImageIcon("Application.icon16");
        iconList.add(icon.getImage());
        icon = resourceMap.getImageIcon("Application.icon24");
        iconList.add(icon.getImage());
        icon = resourceMap.getImageIcon("Application.icon32");
        iconList.add(icon.getImage());
        icon = resourceMap.getImageIcon("Application.icon42");
        iconList.add(icon.getImage());
        this.getMainFrame().setIconImages(iconList);
    }
    
    public static UgotmeApp getApplication() {
        return (UgotmeApp)Application.getInstance((Class)UgotmeApp.class);
    }
    
    public static void main(final String[] args) {
        launch((Class)UgotmeApp.class, args);
    }
}
