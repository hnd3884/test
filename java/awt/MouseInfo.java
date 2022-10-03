package java.awt;

import sun.security.util.SecurityConstants;

public class MouseInfo
{
    private MouseInfo() {
    }
    
    public static PointerInfo getPointerInfo() throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(SecurityConstants.AWT.WATCH_MOUSE_PERMISSION);
        }
        final Point point = new Point(0, 0);
        final int fillPointWithCoords = Toolkit.getDefaultToolkit().getMouseInfoPeer().fillPointWithCoords(point);
        final GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        PointerInfo pointerInfo = null;
        if (areScreenDevicesIndependent(screenDevices)) {
            pointerInfo = new PointerInfo(screenDevices[fillPointWithCoords], point);
        }
        else {
            for (int i = 0; i < screenDevices.length; ++i) {
                if (screenDevices[i].getDefaultConfiguration().getBounds().contains(point)) {
                    pointerInfo = new PointerInfo(screenDevices[i], point);
                }
            }
        }
        return pointerInfo;
    }
    
    private static boolean areScreenDevicesIndependent(final GraphicsDevice[] array) {
        for (int i = 0; i < array.length; ++i) {
            final Rectangle bounds = array[i].getDefaultConfiguration().getBounds();
            if (bounds.x != 0 || bounds.y != 0) {
                return false;
            }
        }
        return true;
    }
    
    public static int getNumberOfButtons() throws HeadlessException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        final Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("awt.mouse.numButtons");
        if (desktopProperty instanceof Integer) {
            return (int)desktopProperty;
        }
        assert false : "awt.mouse.numButtons is not an integer property";
        return 0;
    }
}
