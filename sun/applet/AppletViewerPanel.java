package sun.applet;

import java.applet.AppletContext;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.net.URL;

class AppletViewerPanel extends AppletPanel
{
    static boolean debug;
    URL documentURL;
    URL baseURL;
    Hashtable atts;
    private static final long serialVersionUID = 8890989370785545619L;
    
    AppletViewerPanel(final URL url, final Hashtable atts) {
        this.documentURL = url;
        this.atts = atts;
        String s = this.getParameter("codebase");
        if (s != null) {
            if (!s.endsWith("/")) {
                s += "/";
            }
            try {
                this.baseURL = new URL(url, s);
            }
            catch (final MalformedURLException ex) {}
        }
        if (this.baseURL == null) {
            final String file = url.getFile();
            final int lastIndex = file.lastIndexOf(47);
            if (lastIndex >= 0 && lastIndex < file.length() - 1) {
                try {
                    this.baseURL = new URL(url, file.substring(0, lastIndex + 1));
                }
                catch (final MalformedURLException ex2) {}
            }
        }
        if (this.baseURL == null) {
            this.baseURL = url;
        }
    }
    
    @Override
    public String getParameter(final String s) {
        return this.atts.get(s.toLowerCase());
    }
    
    @Override
    public URL getDocumentBase() {
        return this.documentURL;
    }
    
    @Override
    public URL getCodeBase() {
        return this.baseURL;
    }
    
    @Override
    public int getWidth() {
        final String parameter = this.getParameter("width");
        if (parameter != null) {
            return Integer.valueOf(parameter);
        }
        return 0;
    }
    
    @Override
    public int getHeight() {
        final String parameter = this.getParameter("height");
        if (parameter != null) {
            return Integer.valueOf(parameter);
        }
        return 0;
    }
    
    @Override
    public boolean hasInitialFocus() {
        if (this.isJDK11Applet() || this.isJDK12Applet()) {
            return false;
        }
        final String parameter = this.getParameter("initial_focus");
        return parameter == null || !parameter.toLowerCase().equals("false");
    }
    
    public String getCode() {
        return this.getParameter("code");
    }
    
    public String getJarFiles() {
        return this.getParameter("archive");
    }
    
    public String getSerializedObject() {
        return this.getParameter("object");
    }
    
    @Override
    public AppletContext getAppletContext() {
        return (AppletContext)this.getParent();
    }
    
    static void debug(final String s) {
        if (AppletViewerPanel.debug) {
            System.err.println("AppletViewerPanel:::" + s);
        }
    }
    
    static void debug(final String s, final Throwable t) {
        if (AppletViewerPanel.debug) {
            t.printStackTrace();
            debug(s);
        }
    }
    
    static {
        AppletViewerPanel.debug = false;
    }
}
