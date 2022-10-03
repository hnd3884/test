package com.me.devicemanagement.onpremise.start;

import java.util.Hashtable;
import com.jeans.trayicon.TrayIconPopupSimpleItem;
import java.awt.event.ActionEvent;
import java.io.File;
import com.jeans.trayicon.TrayIconPopupItem;
import com.jeans.trayicon.TrayIconPopup;
import java.util.Iterator;
import java.awt.event.ActionListener;
import com.jeans.trayicon.TrayIconException;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.util.logging.Level;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.JFrame;
import com.jeans.trayicon.WindowsTrayIcon;
import java.util.logging.Logger;

public class ProductTrayIcon extends ProductStarter
{
    private static final Logger LOGGER;
    private static WindowsTrayIcon trayIcon;
    private static final JFrame DUMMY_FRAME;
    Properties taryIconProps;
    boolean processInProgress;
    ArrayList menuItems;
    private static Image enableIcon;
    private static Image disableIcon;
    
    public ProductTrayIcon(final String processInfoFileName) throws Exception {
        super(processInfoFileName);
        this.menuItems = new ArrayList();
        this.taryIconProps = ProductTrayIcon.trayIconInfoParser.getTrayIconInfo();
        ProductTrayIcon.LOGGER.log(Level.INFO, "taryIconProps " + this.taryIconProps);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        ProductTrayIcon.enableIcon = StartupUtil.loadImage(this.taryIconProps.getProperty("EnableIcon"));
        ProductTrayIcon.disableIcon = StartupUtil.loadImage(this.taryIconProps.getProperty("DisableIcon"));
        ProductTrayIcon.DUMMY_FRAME.setIconImage(ProductTrayIcon.enableIcon);
        final String application_name = this.taryIconProps.getProperty("ApplicationName");
        ProductTrayIcon.LOGGER.log(Level.INFO, "application_name " + application_name);
        final String osname = System.getProperty("os.name");
        final String osversion = System.getProperty("os.version");
        if ((osname != null && osname.indexOf("Vista") == -1) || (osversion != null && osversion.indexOf("6") == -1)) {
            if (WindowsTrayIcon.isRunning(application_name)) {
                JOptionPane.showMessageDialog(ProductTrayIcon.DUMMY_FRAME, "Already an instance is running", "Product Name", 0);
                System.exit(0);
            }
            WindowsTrayIcon.initTrayIcon(application_name);
            try {
                ProductTrayIcon.trayIcon = new WindowsTrayIcon(ProductTrayIcon.disableIcon, 16, 16);
            }
            catch (final TrayIconException tie) {
                ProductTrayIcon.LOGGER.log(Level.SEVERE, "Exception while invoking tray icon");
                tie.printStackTrace();
            }
            catch (final InterruptedException ie) {
                ProductTrayIcon.LOGGER.log(Level.SEVERE, "Exception while invoking tray icon");
                ie.printStackTrace();
            }
        }
    }
    
    public void startAndRun() {
        try {
            ProductTrayIcon.LOGGER.log(Level.INFO, "startAndRun");
            final TrayIconListner til = new TrayIconListner();
            if (ProductTrayIcon.trayIcon != null) {
                ProductTrayIcon.trayIcon.addActionListener((ActionListener)til);
                ProductTrayIcon.trayIcon.setPopup(this.makePopup(til));
                ProductTrayIcon.trayIcon.setToolTipText(this.taryIconProps.getProperty("ToolTipText"));
                final WindowsTrayIcon trayIcon = ProductTrayIcon.trayIcon;
                WindowsTrayIcon.keepAlive();
                ProductTrayIcon.trayIcon.setVisible(true);
            }
            this.setProcessInProgress(true);
            this.executeProcess(this.taryIconProps.getProperty("MainProcess"));
            this.setProcessInProgress(false);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setProcessInProgress(final boolean progress) {
        ProductTrayIcon.LOGGER.log(Level.INFO, "setProcessInProgress " + progress);
        try {
            this.processInProgress = progress;
            if (ProductTrayIcon.trayIcon != null) {
                if (progress) {
                    ProductTrayIcon.trayIcon.setImage(ProductTrayIcon.disableIcon, 16, 16);
                }
                else {
                    ProductTrayIcon.trayIcon.setImage(ProductTrayIcon.enableIcon, 16, 16);
                }
                for (final TrayIconPopupProcessItem trayIconPopupItem : this.menuItems) {
                    trayIconPopupItem.setEnabled(!progress);
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public TrayIconPopup makePopup(final ActionListener til) {
        final TrayIconPopup popup = new TrayIconPopup();
        try {
            final ArrayList menus = ((Hashtable<K, ArrayList>)this.taryIconProps).get("Menu");
            final String defaultMenuItemId = this.taryIconProps.getProperty("DefaultMenuItem");
            for (final Properties trayIconMenuItemMap : menus) {
                final String id = trayIconMenuItemMap.getProperty("Id");
                final String menuName = trayIconMenuItemMap.getProperty("DisplayName");
                final String rp = trayIconMenuItemMap.getProperty("RunProcess");
                final TrayIconPopupProcessItem menuItem = new TrayIconPopupProcessItem(menuName);
                if (defaultMenuItemId.equals(id)) {
                    menuItem.setDefault(true);
                }
                menuItem.addActionListener(til);
                ProductTrayIcon.LOGGER.log(Level.INFO, "menuName " + menuName + " RunProcess " + rp);
                menuItem.setRunProcess(rp);
                popup.addMenuItem((TrayIconPopupItem)menuItem);
                this.menuItems.add(menuItem);
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return popup;
    }
    
    public static void cleanTray() {
        if (ProductTrayIcon.trayIcon != null) {
            ProductTrayIcon.trayIcon.freeIcon();
            final WindowsTrayIcon trayIcon = ProductTrayIcon.trayIcon;
            WindowsTrayIcon.cleanUp();
        }
    }
    
    public static void main(final String[] args) {
        try {
            if (args.length < 1) {
                ProductTrayIcon.LOGGER.log(Level.INFO, "Useage : java ProductTrayIcon <relative path of conf file from 'product.home' system property>");
                System.exit(0);
            }
            final String productHome = System.getProperty("product.home");
            if (productHome == null) {
                ProductTrayIcon.LOGGER.log(Level.INFO, "Set 'product.home' system property.");
                System.exit(0);
            }
            String processInfoFileName = productHome + File.separator + args[0];
            ProductTrayIcon.LOGGER.log(Level.INFO, "processInfoFileName " + processInfoFileName);
            if (!new File(processInfoFileName).exists()) {
                ProductTrayIcon.LOGGER.log(Level.INFO, "Wrong ProcessInfo conf File. Using default ProcessInfo File");
                processInfoFileName = productHome + File.separator + "conf/TrayIconInfo.xml";
            }
            ProductTrayIcon.LOGGER.log(Level.INFO, "processInfoFileName file exists");
            final ProductTrayIcon opcTrayIcon = new ProductTrayIcon(processInfoFileName);
            ProductTrayIcon.LOGGER.log(Level.INFO, "Going to startAndRun");
            opcTrayIcon.startAndRun();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ProductTrayIcon.class.getName());
        ProductTrayIcon.trayIcon = null;
        DUMMY_FRAME = new JFrame();
    }
    
    class TrayIconListner implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent ae) {
            if (ProductTrayIcon.this.processInProgress) {
                return;
            }
            final Object obj = ae.getSource();
            if (obj instanceof TrayIconPopupProcessItem) {
                final TrayIconPopupProcessItem menuItem = (TrayIconPopupProcessItem)obj;
                final String menuItemName = menuItem.getName();
                final String runProcess = menuItem.getRunProcess();
                ProductTrayIcon.LOGGER.log(Level.INFO, "MenuItemName " + menuItemName + " runProcess " + runProcess);
                if (runProcess != null) {
                    ProductTrayIcon.this.executeProcess(runProcess);
                }
            }
        }
    }
    
    class TrayIconPopupProcessItem extends TrayIconPopupSimpleItem
    {
        private Class invokeClass;
        private String runProcess;
        
        TrayIconPopupProcessItem(final String s) {
            super(s);
        }
        
        public String getRunProcess() {
            return this.runProcess;
        }
        
        public Class getInvokeClass() {
            return this.invokeClass;
        }
        
        public void setRunProcess(final String runProcess) {
            this.runProcess = runProcess;
        }
        
        public void setInvokeClass(final Class invokeClass) {
            this.invokeClass = invokeClass;
        }
    }
}
