package java.beans;

import java.net.URL;
import java.applet.AppletContext;
import java.applet.Applet;
import java.applet.AppletStub;

class BeansAppletStub implements AppletStub
{
    transient boolean active;
    transient Applet target;
    transient AppletContext context;
    transient URL codeBase;
    transient URL docBase;
    
    BeansAppletStub(final Applet target, final AppletContext context, final URL codeBase, final URL docBase) {
        this.target = target;
        this.context = context;
        this.codeBase = codeBase;
        this.docBase = docBase;
    }
    
    @Override
    public boolean isActive() {
        return this.active;
    }
    
    @Override
    public URL getDocumentBase() {
        return this.docBase;
    }
    
    @Override
    public URL getCodeBase() {
        return this.codeBase;
    }
    
    @Override
    public String getParameter(final String s) {
        return null;
    }
    
    @Override
    public AppletContext getAppletContext() {
        return this.context;
    }
    
    @Override
    public void appletResize(final int n, final int n2) {
    }
}
