package java.applet;

import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleRole;
import java.util.Locale;
import sun.applet.AppletAudioClip;
import java.net.MalformedURLException;
import java.awt.Image;
import java.awt.Dimension;
import java.net.URL;
import java.security.Permission;
import java.awt.AWTPermission;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.awt.HeadlessException;
import java.awt.GraphicsEnvironment;
import javax.accessibility.AccessibleContext;
import java.awt.Panel;

public class Applet extends Panel
{
    private transient AppletStub stub;
    private static final long serialVersionUID = -5836846270535785031L;
    AccessibleContext accessibleContext;
    
    public Applet() throws HeadlessException {
        this.accessibleContext = null;
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        objectInputStream.defaultReadObject();
    }
    
    public final void setStub(final AppletStub stub) {
        if (this.stub != null) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(new AWTPermission("setAppletStub"));
            }
        }
        this.stub = stub;
    }
    
    public boolean isActive() {
        return this.stub != null && this.stub.isActive();
    }
    
    public URL getDocumentBase() {
        return this.stub.getDocumentBase();
    }
    
    public URL getCodeBase() {
        return this.stub.getCodeBase();
    }
    
    public String getParameter(final String s) {
        return this.stub.getParameter(s);
    }
    
    public AppletContext getAppletContext() {
        return this.stub.getAppletContext();
    }
    
    @Override
    public void resize(final int n, final int n2) {
        final Dimension size = this.size();
        if (size.width != n || size.height != n2) {
            super.resize(n, n2);
            if (this.stub != null) {
                this.stub.appletResize(n, n2);
            }
        }
    }
    
    @Override
    public void resize(final Dimension dimension) {
        this.resize(dimension.width, dimension.height);
    }
    
    @Override
    public boolean isValidateRoot() {
        return true;
    }
    
    public void showStatus(final String s) {
        this.getAppletContext().showStatus(s);
    }
    
    public Image getImage(final URL url) {
        return this.getAppletContext().getImage(url);
    }
    
    public Image getImage(final URL url, final String s) {
        try {
            return this.getImage(new URL(url, s));
        }
        catch (final MalformedURLException ex) {
            return null;
        }
    }
    
    public static final AudioClip newAudioClip(final URL url) {
        return new AppletAudioClip(url);
    }
    
    public AudioClip getAudioClip(final URL url) {
        return this.getAppletContext().getAudioClip(url);
    }
    
    public AudioClip getAudioClip(final URL url, final String s) {
        try {
            return this.getAudioClip(new URL(url, s));
        }
        catch (final MalformedURLException ex) {
            return null;
        }
    }
    
    public String getAppletInfo() {
        return null;
    }
    
    @Override
    public Locale getLocale() {
        final Locale locale = super.getLocale();
        if (locale == null) {
            return Locale.getDefault();
        }
        return locale;
    }
    
    public String[][] getParameterInfo() {
        return null;
    }
    
    public void play(final URL url) {
        final AudioClip audioClip = this.getAudioClip(url);
        if (audioClip != null) {
            audioClip.play();
        }
    }
    
    public void play(final URL url, final String s) {
        final AudioClip audioClip = this.getAudioClip(url, s);
        if (audioClip != null) {
            audioClip.play();
        }
    }
    
    public void init() {
    }
    
    public void start() {
    }
    
    public void stop() {
    }
    
    public void destroy() {
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleApplet();
        }
        return this.accessibleContext;
    }
    
    protected class AccessibleApplet extends AccessibleAWTPanel
    {
        private static final long serialVersionUID = 8127374778187708896L;
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.FRAME;
        }
        
        @Override
        public AccessibleStateSet getAccessibleStateSet() {
            final AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
            accessibleStateSet.add(AccessibleState.ACTIVE);
            return accessibleStateSet;
        }
    }
}
