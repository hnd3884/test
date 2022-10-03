package sun.applet;

import java.util.HashMap;
import java.net.URLConnection;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Locale;
import java.io.Reader;
import java.awt.event.ActionEvent;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.awt.print.PrinterException;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.awt.Point;
import java.security.AccessController;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.FileDialog;
import java.security.PrivilegedAction;
import java.awt.Insets;
import java.awt.Dimension;
import java.util.Iterator;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.security.Permission;
import java.net.SocketPermission;
import java.applet.Applet;
import sun.misc.Ref;
import java.awt.Image;
import java.applet.AudioClip;
import java.awt.event.ActionListener;
import java.awt.MenuBar;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.Menu;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Map;
import java.io.PrintStream;
import java.awt.Label;
import java.awt.print.Printable;
import java.applet.AppletContext;
import java.awt.Frame;

public class AppletViewer extends Frame implements AppletContext, Printable
{
    private static String defaultSaveFile;
    AppletViewerPanel panel;
    Label label;
    PrintStream statusMsgStream;
    AppletViewerFactory factory;
    private static Map audioClips;
    private static Map imageRefs;
    static Vector appletPanels;
    static Hashtable systemParam;
    static AppletProps props;
    static int c;
    private static int x;
    private static int y;
    private static final int XDELTA = 30;
    private static final int YDELTA = 30;
    static String encoding;
    private static AppletMessageHandler amh;
    
    public AppletViewer(final int n, final int n2, final URL url, final Hashtable hashtable, final PrintStream statusMsgStream, final AppletViewerFactory factory) {
        this.factory = factory;
        this.statusMsgStream = statusMsgStream;
        this.setTitle(AppletViewer.amh.getMessage("tool.title", hashtable.get("code")));
        final MenuBar baseMenuBar = factory.getBaseMenuBar();
        final Menu menu = new Menu(AppletViewer.amh.getMessage("menu.applet"));
        this.addMenuItem(menu, "menuitem.restart");
        this.addMenuItem(menu, "menuitem.reload");
        this.addMenuItem(menu, "menuitem.stop");
        this.addMenuItem(menu, "menuitem.save");
        this.addMenuItem(menu, "menuitem.start");
        this.addMenuItem(menu, "menuitem.clone");
        menu.add(new MenuItem("-"));
        this.addMenuItem(menu, "menuitem.tag");
        this.addMenuItem(menu, "menuitem.info");
        this.addMenuItem(menu, "menuitem.edit").disable();
        this.addMenuItem(menu, "menuitem.encoding");
        menu.add(new MenuItem("-"));
        this.addMenuItem(menu, "menuitem.print");
        menu.add(new MenuItem("-"));
        this.addMenuItem(menu, "menuitem.props");
        menu.add(new MenuItem("-"));
        this.addMenuItem(menu, "menuitem.close");
        if (factory.isStandalone()) {
            this.addMenuItem(menu, "menuitem.quit");
        }
        baseMenuBar.add(menu);
        this.setMenuBar(baseMenuBar);
        this.add("Center", this.panel = new AppletViewerPanel(url, hashtable));
        this.add("South", this.label = new Label(AppletViewer.amh.getMessage("label.hello")));
        this.panel.init();
        AppletViewer.appletPanels.addElement(this.panel);
        this.pack();
        this.move(n, n2);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                AppletViewer.this.appletClose();
            }
            
            @Override
            public void windowIconified(final WindowEvent windowEvent) {
                AppletViewer.this.appletStop();
            }
            
            @Override
            public void windowDeiconified(final WindowEvent windowEvent) {
                AppletViewer.this.appletStart();
            }
        });
        this.panel.addAppletListener(new AppletEventListener(this));
        this.showStatus(AppletViewer.amh.getMessage("status.start"));
        this.initEventQueue();
    }
    
    public MenuItem addMenuItem(final Menu menu, final String s) {
        final MenuItem menuItem = new MenuItem(AppletViewer.amh.getMessage(s));
        menuItem.addActionListener(new UserActionListener());
        return menu.add(menuItem);
    }
    
    private void initEventQueue() {
        final String property = System.getProperty("appletviewer.send.event");
        if (property == null) {
            this.panel.sendEvent(1);
            this.panel.sendEvent(2);
            this.panel.sendEvent(3);
        }
        else {
            final String[] splitSeparator = this.splitSeparator(",", property);
            for (int i = 0; i < splitSeparator.length; ++i) {
                System.out.println("Adding event to queue: " + splitSeparator[i]);
                if (splitSeparator[i].equals("dispose")) {
                    this.panel.sendEvent(0);
                }
                else if (splitSeparator[i].equals("load")) {
                    this.panel.sendEvent(1);
                }
                else if (splitSeparator[i].equals("init")) {
                    this.panel.sendEvent(2);
                }
                else if (splitSeparator[i].equals("start")) {
                    this.panel.sendEvent(3);
                }
                else if (splitSeparator[i].equals("stop")) {
                    this.panel.sendEvent(4);
                }
                else if (splitSeparator[i].equals("destroy")) {
                    this.panel.sendEvent(5);
                }
                else if (splitSeparator[i].equals("quit")) {
                    this.panel.sendEvent(6);
                }
                else if (splitSeparator[i].equals("error")) {
                    this.panel.sendEvent(7);
                }
                else {
                    System.out.println("Unrecognized event name: " + splitSeparator[i]);
                }
            }
            while (!this.panel.emptyEventQueue()) {}
            this.appletSystemExit();
        }
    }
    
    private String[] splitSeparator(final String s, final String s2) {
        final Vector vector = new Vector();
        int n;
        int index;
        for (n = 0; (index = s2.indexOf(s, n)) != -1; n = index + 1) {
            vector.addElement(s2.substring(n, index));
        }
        vector.addElement(s2.substring(n));
        final String[] array = new String[vector.size()];
        vector.copyInto(array);
        return array;
    }
    
    @Override
    public AudioClip getAudioClip(final URL url) {
        checkConnect(url);
        synchronized (AppletViewer.audioClips) {
            AudioClip audioClip = AppletViewer.audioClips.get(url);
            if (audioClip == null) {
                AppletViewer.audioClips.put(url, audioClip = new AppletAudioClip(url));
            }
            return audioClip;
        }
    }
    
    @Override
    public Image getImage(final URL url) {
        return getCachedImage(url);
    }
    
    static Image getCachedImage(final URL url) {
        return (Image)getCachedImageRef(url).get();
    }
    
    static Ref getCachedImageRef(final URL url) {
        synchronized (AppletViewer.imageRefs) {
            AppletImageRef appletImageRef = AppletViewer.imageRefs.get(url);
            if (appletImageRef == null) {
                appletImageRef = new AppletImageRef(url);
                AppletViewer.imageRefs.put(url, appletImageRef);
            }
            return appletImageRef;
        }
    }
    
    static void flushImageCache() {
        AppletViewer.imageRefs.clear();
    }
    
    @Override
    public Applet getApplet(String lowerCase) {
        final AppletSecurity appletSecurity = (AppletSecurity)System.getSecurityManager();
        lowerCase = lowerCase.toLowerCase();
        final SocketPermission socketPermission = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
        final Enumeration elements = AppletViewer.appletPanels.elements();
        while (elements.hasMoreElements()) {
            final AppletPanel appletPanel = (AppletPanel)elements.nextElement();
            String s = appletPanel.getParameter("name");
            if (s != null) {
                s = s.toLowerCase();
            }
            if (lowerCase.equals(s) && appletPanel.getDocumentBase().equals(this.panel.getDocumentBase()) && socketPermission.implies(new SocketPermission(appletPanel.getCodeBase().getHost(), "connect"))) {
                return appletPanel.applet;
            }
        }
        return null;
    }
    
    @Override
    public Enumeration getApplets() {
        final AppletSecurity appletSecurity = (AppletSecurity)System.getSecurityManager();
        final Vector vector = new Vector();
        final SocketPermission socketPermission = new SocketPermission(this.panel.getCodeBase().getHost(), "connect");
        final Enumeration elements = AppletViewer.appletPanels.elements();
        while (elements.hasMoreElements()) {
            final AppletPanel appletPanel = (AppletPanel)elements.nextElement();
            if (appletPanel.getDocumentBase().equals(this.panel.getDocumentBase()) && socketPermission.implies(new SocketPermission(appletPanel.getCodeBase().getHost(), "connect"))) {
                vector.addElement(appletPanel.applet);
            }
        }
        return vector.elements();
    }
    
    @Override
    public void showDocument(final URL url) {
    }
    
    @Override
    public void showDocument(final URL url, final String s) {
    }
    
    @Override
    public void showStatus(final String text) {
        this.label.setText(text);
    }
    
    @Override
    public void setStream(final String s, final InputStream inputStream) throws IOException {
    }
    
    @Override
    public InputStream getStream(final String s) {
        return null;
    }
    
    @Override
    public Iterator getStreamKeys() {
        return null;
    }
    
    public static void printTag(final PrintStream printStream, final Hashtable hashtable) {
        printStream.print("<applet");
        final String s = hashtable.get("codebase");
        if (s != null) {
            printStream.print(" codebase=\"" + s + "\"");
        }
        String s2 = hashtable.get("code");
        if (s2 == null) {
            s2 = "applet.class";
        }
        printStream.print(" code=\"" + s2 + "\"");
        String s3 = hashtable.get("width");
        if (s3 == null) {
            s3 = "150";
        }
        printStream.print(" width=" + s3);
        String s4 = hashtable.get("height");
        if (s4 == null) {
            s4 = "100";
        }
        printStream.print(" height=" + s4);
        final String s5 = hashtable.get("name");
        if (s5 != null) {
            printStream.print(" name=\"" + s5 + "\"");
        }
        printStream.println(">");
        final String[] array = new String[hashtable.size()];
        int n = 0;
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String s6;
            int n2;
            for (s6 = (String)keys.nextElement(), n2 = 0; n2 < n && array[n2].compareTo(s6) < 0; ++n2) {}
            System.arraycopy(array, n2, array, n2 + 1, n - n2);
            array[n2] = s6;
            ++n;
        }
        for (final String s7 : array) {
            if (AppletViewer.systemParam.get(s7) == null) {
                printStream.println("<param name=" + s7 + " value=\"" + hashtable.get(s7) + "\">");
            }
        }
        printStream.println("</applet>");
    }
    
    public void updateAtts() {
        final Dimension size = this.panel.size();
        final Insets insets = this.panel.insets();
        this.panel.atts.put("width", Integer.toString(size.width - (insets.left + insets.right)));
        this.panel.atts.put("height", Integer.toString(size.height - (insets.top + insets.bottom)));
    }
    
    void appletRestart() {
        this.panel.sendEvent(4);
        this.panel.sendEvent(5);
        this.panel.sendEvent(2);
        this.panel.sendEvent(3);
    }
    
    void appletReload() {
        this.panel.sendEvent(4);
        this.panel.sendEvent(5);
        this.panel.sendEvent(0);
        AppletPanel.flushClassLoader(this.panel.getClassLoaderCacheKey());
        try {
            this.panel.joinAppletThread();
            this.panel.release();
        }
        catch (final InterruptedException ex) {
            return;
        }
        this.panel.createAppletThread();
        this.panel.sendEvent(1);
        this.panel.sendEvent(2);
        this.panel.sendEvent(3);
    }
    
    void appletSave() {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction() {
            @Override
            public Object run() {
                AppletViewer.this.panel.sendEvent(4);
                final FileDialog fileDialog = new FileDialog(AppletViewer.this, AppletViewer.amh.getMessage("appletsave.filedialogtitle"), 1);
                fileDialog.setDirectory(System.getProperty("user.dir"));
                fileDialog.setFile(AppletViewer.defaultSaveFile);
                fileDialog.show();
                final String file = fileDialog.getFile();
                if (file == null) {
                    AppletViewer.this.panel.sendEvent(3);
                    return null;
                }
                final File file2 = new File(fileDialog.getDirectory(), file);
                try (final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                     final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                     final ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream)) {
                    AppletViewer.this.showStatus(AppletViewer.amh.getMessage("appletsave.err1", AppletViewer.this.panel.applet.toString(), file2.toString()));
                    objectOutputStream.writeObject(AppletViewer.this.panel.applet);
                }
                catch (final IOException ex) {
                    System.err.println(AppletViewer.amh.getMessage("appletsave.err2", ex));
                }
                finally {
                    AppletViewer.this.panel.sendEvent(3);
                }
                return null;
            }
        });
    }
    
    void appletClone() {
        final Point location = this.location();
        this.updateAtts();
        this.factory.createAppletViewer(location.x + 30, location.y + 30, this.panel.documentURL, (Hashtable)this.panel.atts.clone());
    }
    
    void appletTag() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.updateAtts();
        printTag(new PrintStream(byteArrayOutputStream), this.panel.atts);
        this.showStatus(AppletViewer.amh.getMessage("applettag"));
        final Point location = this.location();
        new TextFrame(location.x + 30, location.y + 30, AppletViewer.amh.getMessage("applettag.textframe"), byteArrayOutputStream.toString());
    }
    
    void appletInfo() {
        String s = this.panel.applet.getAppletInfo();
        if (s == null) {
            s = AppletViewer.amh.getMessage("appletinfo.applet");
        }
        String s2 = s + "\n\n";
        final String[][] parameterInfo = this.panel.applet.getParameterInfo();
        if (parameterInfo != null) {
            for (int i = 0; i < parameterInfo.length; ++i) {
                s2 = s2 + parameterInfo[i][0] + " -- " + parameterInfo[i][1] + " -- " + parameterInfo[i][2] + "\n";
            }
        }
        else {
            s2 += AppletViewer.amh.getMessage("appletinfo.param");
        }
        final Point location = this.location();
        new TextFrame(location.x + 30, location.y + 30, AppletViewer.amh.getMessage("appletinfo.textframe"), s2);
    }
    
    void appletCharacterEncoding() {
        this.showStatus(AppletViewer.amh.getMessage("appletencoding", AppletViewer.encoding));
    }
    
    void appletEdit() {
    }
    
    void appletPrint() {
        final PrinterJob printerJob = PrinterJob.getPrinterJob();
        if (printerJob != null) {
            final HashPrintRequestAttributeSet set = new HashPrintRequestAttributeSet();
            if (printerJob.printDialog(set)) {
                printerJob.setPrintable(this);
                try {
                    printerJob.print(set);
                    this.statusMsgStream.println(AppletViewer.amh.getMessage("appletprint.finish"));
                }
                catch (final PrinterException ex) {
                    this.statusMsgStream.println(AppletViewer.amh.getMessage("appletprint.fail"));
                }
            }
            else {
                this.statusMsgStream.println(AppletViewer.amh.getMessage("appletprint.cancel"));
            }
        }
        else {
            this.statusMsgStream.println(AppletViewer.amh.getMessage("appletprint.fail"));
        }
    }
    
    @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int n) {
        if (n > 0) {
            return 1;
        }
        ((Graphics2D)graphics).translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        this.panel.applet.printAll(graphics);
        return 0;
    }
    
    public static synchronized void networkProperties() {
        if (AppletViewer.props == null) {
            AppletViewer.props = new AppletProps();
        }
        AppletViewer.props.addNotify();
        AppletViewer.props.setVisible(true);
    }
    
    void appletStart() {
        this.panel.sendEvent(3);
    }
    
    void appletStop() {
        this.panel.sendEvent(4);
    }
    
    private void appletShutdown(final AppletPanel appletPanel) {
        appletPanel.sendEvent(4);
        appletPanel.sendEvent(5);
        appletPanel.sendEvent(0);
        appletPanel.sendEvent(6);
    }
    
    void appletClose() {
        new Thread(new Runnable() {
            final /* synthetic */ AppletPanel val$p = AppletViewer.this.panel;
            
            @Override
            public void run() {
                AppletViewer.this.appletShutdown(this.val$p);
                AppletViewer.appletPanels.removeElement(this.val$p);
                AppletViewer.this.dispose();
                if (AppletViewer.countApplets() == 0) {
                    AppletViewer.this.appletSystemExit();
                }
            }
        }).start();
    }
    
    private void appletSystemExit() {
        if (this.factory.isStandalone()) {
            System.exit(0);
        }
    }
    
    protected void appletQuit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Enumeration elements = AppletViewer.appletPanels.elements();
                while (elements.hasMoreElements()) {
                    AppletViewer.this.appletShutdown((AppletPanel)elements.nextElement());
                }
                AppletViewer.this.appletSystemExit();
            }
        }).start();
    }
    
    public void processUserAction(final ActionEvent actionEvent) {
        final String label = ((MenuItem)actionEvent.getSource()).getLabel();
        if (AppletViewer.amh.getMessage("menuitem.restart").equals(label)) {
            this.appletRestart();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.reload").equals(label)) {
            this.appletReload();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.clone").equals(label)) {
            this.appletClone();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.stop").equals(label)) {
            this.appletStop();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.save").equals(label)) {
            this.appletSave();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.start").equals(label)) {
            this.appletStart();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.tag").equals(label)) {
            this.appletTag();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.info").equals(label)) {
            this.appletInfo();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.encoding").equals(label)) {
            this.appletCharacterEncoding();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.edit").equals(label)) {
            this.appletEdit();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.print").equals(label)) {
            this.appletPrint();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.props").equals(label)) {
            networkProperties();
            return;
        }
        if (AppletViewer.amh.getMessage("menuitem.close").equals(label)) {
            this.appletClose();
            return;
        }
        if (this.factory.isStandalone() && AppletViewer.amh.getMessage("menuitem.quit").equals(label)) {
            this.appletQuit();
        }
    }
    
    public static int countApplets() {
        return AppletViewer.appletPanels.size();
    }
    
    public static void skipSpace(final Reader reader) throws IOException {
        while (AppletViewer.c >= 0 && (AppletViewer.c == 32 || AppletViewer.c == 9 || AppletViewer.c == 10 || AppletViewer.c == 13)) {
            AppletViewer.c = reader.read();
        }
    }
    
    public static String scanIdentifier(final Reader reader) throws IOException {
        final StringBuffer sb = new StringBuffer();
        while ((AppletViewer.c >= 97 && AppletViewer.c <= 122) || (AppletViewer.c >= 65 && AppletViewer.c <= 90) || (AppletViewer.c >= 48 && AppletViewer.c <= 57) || AppletViewer.c == 95) {
            sb.append((char)AppletViewer.c);
            AppletViewer.c = reader.read();
        }
        return sb.toString();
    }
    
    public static Hashtable scanTag(final Reader reader) throws IOException {
        final Hashtable hashtable = new Hashtable();
        skipSpace(reader);
        while (AppletViewer.c >= 0 && AppletViewer.c != 62) {
            final String scanIdentifier = scanIdentifier(reader);
            String string = "";
            skipSpace(reader);
            if (AppletViewer.c == 61) {
                int c = -1;
                AppletViewer.c = reader.read();
                skipSpace(reader);
                if (AppletViewer.c == 39 || AppletViewer.c == 34) {
                    c = AppletViewer.c;
                    AppletViewer.c = reader.read();
                }
                final StringBuffer sb = new StringBuffer();
                while (AppletViewer.c > 0 && ((c < 0 && AppletViewer.c != 32 && AppletViewer.c != 9 && AppletViewer.c != 10 && AppletViewer.c != 13 && AppletViewer.c != 62) || (c >= 0 && AppletViewer.c != c))) {
                    sb.append((char)AppletViewer.c);
                    AppletViewer.c = reader.read();
                }
                if (AppletViewer.c == c) {
                    AppletViewer.c = reader.read();
                }
                skipSpace(reader);
                string = sb.toString();
            }
            if (!string.equals("")) {
                hashtable.put(scanIdentifier.toLowerCase(Locale.ENGLISH), string);
            }
            while (AppletViewer.c != 62 && AppletViewer.c >= 0 && (AppletViewer.c < 97 || AppletViewer.c > 122) && (AppletViewer.c < 65 || AppletViewer.c > 90) && (AppletViewer.c < 48 || AppletViewer.c > 57) && AppletViewer.c != 95) {
                AppletViewer.c = reader.read();
            }
        }
        return hashtable;
    }
    
    private static Reader makeReader(final InputStream inputStream) {
        if (AppletViewer.encoding != null) {
            try {
                return new BufferedReader(new InputStreamReader(inputStream, AppletViewer.encoding));
            }
            catch (final IOException ex) {}
        }
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        AppletViewer.encoding = inputStreamReader.getEncoding();
        return new BufferedReader(inputStreamReader);
    }
    
    public static void parse(final URL url, final String encoding) throws IOException {
        AppletViewer.encoding = encoding;
        parse(url, System.out, new StdAppletViewerFactory());
    }
    
    public static void parse(final URL url) throws IOException {
        parse(url, System.out, new StdAppletViewerFactory());
    }
    
    public static void parse(URL url, final PrintStream printStream, final AppletViewerFactory appletViewerFactory) throws IOException {
        int n = 0;
        final String message = AppletViewer.amh.getMessage("parse.warning.requiresname");
        final String message2 = AppletViewer.amh.getMessage("parse.warning.paramoutside");
        final String message3 = AppletViewer.amh.getMessage("parse.warning.applet.requirescode");
        final String message4 = AppletViewer.amh.getMessage("parse.warning.applet.requiresheight");
        final String message5 = AppletViewer.amh.getMessage("parse.warning.applet.requireswidth");
        final String message6 = AppletViewer.amh.getMessage("parse.warning.object.requirescode");
        final String message7 = AppletViewer.amh.getMessage("parse.warning.object.requiresheight");
        final String message8 = AppletViewer.amh.getMessage("parse.warning.object.requireswidth");
        final String message9 = AppletViewer.amh.getMessage("parse.warning.embed.requirescode");
        final String message10 = AppletViewer.amh.getMessage("parse.warning.embed.requiresheight");
        final String message11 = AppletViewer.amh.getMessage("parse.warning.embed.requireswidth");
        final String message12 = AppletViewer.amh.getMessage("parse.warning.appnotLongersupported");
        final URLConnection openConnection = url.openConnection();
        final Reader reader = makeReader(openConnection.getInputStream());
        url = openConnection.getURL();
        int n2 = 1;
        Hashtable hashtable = null;
        while (true) {
            AppletViewer.c = reader.read();
            if (AppletViewer.c == -1) {
                break;
            }
            if (AppletViewer.c != 60) {
                continue;
            }
            AppletViewer.c = reader.read();
            if (AppletViewer.c == 47) {
                AppletViewer.c = reader.read();
                final String scanIdentifier = scanIdentifier(reader);
                if (!scanIdentifier.equalsIgnoreCase("applet") && !scanIdentifier.equalsIgnoreCase("object") && !scanIdentifier.equalsIgnoreCase("embed")) {
                    continue;
                }
                if (n != 0 && hashtable.get("code") == null && hashtable.get("object") == null) {
                    printStream.println(message6);
                    hashtable = null;
                }
                if (hashtable != null) {
                    appletViewerFactory.createAppletViewer(AppletViewer.x, AppletViewer.y, url, hashtable);
                    AppletViewer.x += 30;
                    AppletViewer.y += 30;
                    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    if (AppletViewer.x > screenSize.width - 300 || AppletViewer.y > screenSize.height - 300) {
                        AppletViewer.x = 0;
                        AppletViewer.y = 2 * n2 * 30;
                        ++n2;
                    }
                }
                hashtable = null;
                n = 0;
            }
            else {
                final String scanIdentifier2 = scanIdentifier(reader);
                if (scanIdentifier2.equalsIgnoreCase("param")) {
                    final Hashtable scanTag = scanTag(reader);
                    final String s = scanTag.get("name");
                    if (s == null) {
                        printStream.println(message);
                    }
                    else {
                        final String s2 = scanTag.get("value");
                        if (s2 == null) {
                            printStream.println(message);
                        }
                        else if (hashtable != null) {
                            hashtable.put(s.toLowerCase(), s2);
                        }
                        else {
                            printStream.println(message2);
                        }
                    }
                }
                else if (scanIdentifier2.equalsIgnoreCase("applet")) {
                    hashtable = scanTag(reader);
                    if (hashtable.get("code") == null && hashtable.get("object") == null) {
                        printStream.println(message3);
                        hashtable = null;
                    }
                    else if (hashtable.get("width") == null) {
                        printStream.println(message5);
                        hashtable = null;
                    }
                    else {
                        if (hashtable.get("height") != null) {
                            continue;
                        }
                        printStream.println(message4);
                        hashtable = null;
                    }
                }
                else if (scanIdentifier2.equalsIgnoreCase("object")) {
                    n = 1;
                    hashtable = scanTag(reader);
                    if (hashtable.get("codebase") != null) {
                        hashtable.remove("codebase");
                    }
                    if (hashtable.get("width") == null) {
                        printStream.println(message8);
                        hashtable = null;
                    }
                    else {
                        if (hashtable.get("height") != null) {
                            continue;
                        }
                        printStream.println(message7);
                        hashtable = null;
                    }
                }
                else if (scanIdentifier2.equalsIgnoreCase("embed")) {
                    hashtable = scanTag(reader);
                    if (hashtable.get("code") == null && hashtable.get("object") == null) {
                        printStream.println(message9);
                        hashtable = null;
                    }
                    else if (hashtable.get("width") == null) {
                        printStream.println(message11);
                        hashtable = null;
                    }
                    else {
                        if (hashtable.get("height") != null) {
                            continue;
                        }
                        printStream.println(message10);
                        hashtable = null;
                    }
                }
                else {
                    if (!scanIdentifier2.equalsIgnoreCase("app")) {
                        continue;
                    }
                    printStream.println(message12);
                    final Hashtable scanTag2 = scanTag(reader);
                    final String s3 = scanTag2.get("class");
                    if (s3 != null) {
                        scanTag2.remove("class");
                        scanTag2.put("code", s3 + ".class");
                    }
                    final String s4 = scanTag2.get("src");
                    if (s4 != null) {
                        scanTag2.remove("src");
                        scanTag2.put("codebase", s4);
                    }
                    if (scanTag2.get("width") == null) {
                        scanTag2.put("width", "100");
                    }
                    if (scanTag2.get("height") == null) {
                        scanTag2.put("height", "100");
                    }
                    printTag(printStream, scanTag2);
                    printStream.println();
                }
            }
        }
        reader.close();
    }
    
    @Deprecated
    public static void main(final String[] array) {
        Main.main(array);
    }
    
    private static void checkConnect(final URL url) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                final Permission permission = url.openConnection().getPermission();
                if (permission != null) {
                    securityManager.checkPermission(permission);
                }
                else {
                    securityManager.checkConnect(url.getHost(), url.getPort());
                }
            }
            catch (final IOException ex) {
                securityManager.checkConnect(url.getHost(), url.getPort());
            }
        }
    }
    
    static {
        AppletViewer.defaultSaveFile = "Applet.ser";
        AppletViewer.audioClips = new HashMap();
        AppletViewer.imageRefs = new HashMap();
        AppletViewer.appletPanels = new Vector();
        (AppletViewer.systemParam = new Hashtable()).put("codebase", "codebase");
        AppletViewer.systemParam.put("code", "code");
        AppletViewer.systemParam.put("alt", "alt");
        AppletViewer.systemParam.put("width", "width");
        AppletViewer.systemParam.put("height", "height");
        AppletViewer.systemParam.put("align", "align");
        AppletViewer.systemParam.put("vspace", "vspace");
        AppletViewer.systemParam.put("hspace", "hspace");
        AppletViewer.x = 0;
        AppletViewer.y = 0;
        AppletViewer.encoding = null;
        AppletViewer.amh = new AppletMessageHandler("appletviewer");
    }
    
    private final class UserActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            AppletViewer.this.processUserAction(actionEvent);
        }
    }
}
