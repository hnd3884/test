package sun.applet;

public class AppletEventMulticaster implements AppletListener
{
    private final AppletListener a;
    private final AppletListener b;
    
    public AppletEventMulticaster(final AppletListener a, final AppletListener b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public void appletStateChanged(final AppletEvent appletEvent) {
        this.a.appletStateChanged(appletEvent);
        this.b.appletStateChanged(appletEvent);
    }
    
    public static AppletListener add(final AppletListener appletListener, final AppletListener appletListener2) {
        return addInternal(appletListener, appletListener2);
    }
    
    public static AppletListener remove(final AppletListener appletListener, final AppletListener appletListener2) {
        return removeInternal(appletListener, appletListener2);
    }
    
    private static AppletListener addInternal(final AppletListener appletListener, final AppletListener appletListener2) {
        if (appletListener == null) {
            return appletListener2;
        }
        if (appletListener2 == null) {
            return appletListener;
        }
        return new AppletEventMulticaster(appletListener, appletListener2);
    }
    
    protected AppletListener remove(final AppletListener appletListener) {
        if (appletListener == this.a) {
            return this.b;
        }
        if (appletListener == this.b) {
            return this.a;
        }
        final AppletListener removeInternal = removeInternal(this.a, appletListener);
        final AppletListener removeInternal2 = removeInternal(this.b, appletListener);
        if (removeInternal == this.a && removeInternal2 == this.b) {
            return this;
        }
        return addInternal(removeInternal, removeInternal2);
    }
    
    private static AppletListener removeInternal(final AppletListener appletListener, final AppletListener appletListener2) {
        if (appletListener == appletListener2 || appletListener == null) {
            return null;
        }
        if (appletListener instanceof AppletEventMulticaster) {
            return ((AppletEventMulticaster)appletListener).remove(appletListener2);
        }
        return appletListener;
    }
}
