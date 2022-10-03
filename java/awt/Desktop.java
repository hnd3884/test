package java.awt;

import java.io.FilePermission;
import java.net.URISyntaxException;
import java.net.URL;
import sun.awt.DesktopBrowse;
import java.net.MalformedURLException;
import java.net.URI;
import java.io.IOException;
import java.security.Permission;
import java.io.File;
import sun.awt.SunToolkit;
import sun.awt.AppContext;
import java.awt.peer.DesktopPeer;

public class Desktop
{
    private DesktopPeer peer;
    
    private Desktop() {
        this.peer = Toolkit.getDefaultToolkit().createDesktopPeer(this);
    }
    
    public static synchronized Desktop getDesktop() {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (!isDesktopSupported()) {
            throw new UnsupportedOperationException("Desktop API is not supported on the current platform");
        }
        final AppContext appContext = AppContext.getAppContext();
        Desktop desktop = (Desktop)appContext.get(Desktop.class);
        if (desktop == null) {
            desktop = new Desktop();
            appContext.put(Desktop.class, desktop);
        }
        return desktop;
    }
    
    public static boolean isDesktopSupported() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        return defaultToolkit instanceof SunToolkit && ((SunToolkit)defaultToolkit).isDesktopSupported();
    }
    
    public boolean isSupported(final Action action) {
        return this.peer.isSupported(action);
    }
    
    private static void checkFileValidation(final File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("The file: " + file.getPath() + " doesn't exist.");
        }
    }
    
    private void checkActionSupport(final Action action) {
        if (!this.isSupported(action)) {
            throw new UnsupportedOperationException("The " + action.name() + " action is not supported on the current platform!");
        }
    }
    
    private void checkAWTPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new AWTPermission("showWindowWithoutWarningBanner"));
        }
    }
    
    public void open(File file) throws IOException {
        file = new File(file.getPath());
        this.checkAWTPermission();
        this.checkExec();
        this.checkActionSupport(Action.OPEN);
        checkFileValidation(file);
        this.peer.open(file);
    }
    
    public void edit(File file) throws IOException {
        file = new File(file.getPath());
        this.checkAWTPermission();
        this.checkExec();
        this.checkActionSupport(Action.EDIT);
        file.canWrite();
        checkFileValidation(file);
        this.peer.edit(file);
    }
    
    public void print(File file) throws IOException {
        file = new File(file.getPath());
        this.checkExec();
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPrintJobAccess();
        }
        this.checkActionSupport(Action.PRINT);
        checkFileValidation(file);
        this.peer.print(file);
    }
    
    public void browse(final URI uri) throws IOException {
        SecurityException ex = null;
        try {
            this.checkAWTPermission();
            this.checkExec();
        }
        catch (final SecurityException ex2) {
            ex = ex2;
        }
        this.checkActionSupport(Action.BROWSE);
        if (uri == null) {
            throw new NullPointerException();
        }
        if (ex == null) {
            this.peer.browse(uri);
            return;
        }
        URL url;
        try {
            url = uri.toURL();
        }
        catch (final MalformedURLException ex3) {
            throw new IllegalArgumentException("Unable to convert URI to URL", ex3);
        }
        final DesktopBrowse instance = DesktopBrowse.getInstance();
        if (instance == null) {
            throw ex;
        }
        instance.browse(url);
    }
    
    public void mail() throws IOException {
        this.checkAWTPermission();
        this.checkExec();
        this.checkActionSupport(Action.MAIL);
        try {
            this.peer.mail(new URI("mailto:?"));
        }
        catch (final URISyntaxException ex) {}
    }
    
    public void mail(final URI uri) throws IOException {
        this.checkAWTPermission();
        this.checkExec();
        this.checkActionSupport(Action.MAIL);
        if (uri == null) {
            throw new NullPointerException();
        }
        if (!"mailto".equalsIgnoreCase(uri.getScheme())) {
            throw new IllegalArgumentException("URI scheme is not \"mailto\"");
        }
        this.peer.mail(uri);
    }
    
    private void checkExec() throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new FilePermission("<<ALL FILES>>", "execute"));
        }
    }
    
    public enum Action
    {
        OPEN, 
        EDIT, 
        PRINT, 
        MAIL, 
        BROWSE;
    }
}
