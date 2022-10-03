package java.awt;

import sun.awt.AWTAccessor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import sun.awt.HeadlessToolkit;
import java.util.EventListener;
import sun.awt.SunToolkit;
import sun.awt.AppContext;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.peer.TrayIconPeer;

public class TrayIcon
{
    private Image image;
    private String tooltip;
    private PopupMenu popup;
    private boolean autosize;
    private int id;
    private String actionCommand;
    private transient TrayIconPeer peer;
    transient MouseListener mouseListener;
    transient MouseMotionListener mouseMotionListener;
    transient ActionListener actionListener;
    private final AccessControlContext acc;
    
    final AccessControlContext getAccessControlContext() {
        if (this.acc == null) {
            throw new SecurityException("TrayIcon is missing AccessControlContext");
        }
        return this.acc;
    }
    
    private TrayIcon() throws UnsupportedOperationException, HeadlessException, SecurityException {
        this.acc = AccessController.getContext();
        SystemTray.checkSystemTrayAllowed();
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException();
        }
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException();
        }
        SunToolkit.insertTargetMapping(this, AppContext.getAppContext());
    }
    
    public TrayIcon(final Image image) {
        this();
        if (image == null) {
            throw new IllegalArgumentException("creating TrayIcon with null Image");
        }
        this.setImage(image);
    }
    
    public TrayIcon(final Image image, final String toolTip) {
        this(image);
        this.setToolTip(toolTip);
    }
    
    public TrayIcon(final Image image, final String s, final PopupMenu popupMenu) {
        this(image, s);
        this.setPopupMenu(popupMenu);
    }
    
    public void setImage(final Image image) {
        if (image == null) {
            throw new NullPointerException("setting null Image");
        }
        this.image = image;
        final TrayIconPeer peer = this.peer;
        if (peer != null) {
            peer.updateImage();
        }
    }
    
    public Image getImage() {
        return this.image;
    }
    
    public void setPopupMenu(final PopupMenu popup) {
        if (popup == this.popup) {
            return;
        }
        synchronized (TrayIcon.class) {
            if (popup != null) {
                if (popup.isTrayIconPopup) {
                    throw new IllegalArgumentException("the PopupMenu is already set for another TrayIcon");
                }
                popup.isTrayIconPopup = true;
            }
            if (this.popup != null) {
                this.popup.isTrayIconPopup = false;
            }
            this.popup = popup;
        }
    }
    
    public PopupMenu getPopupMenu() {
        return this.popup;
    }
    
    public void setToolTip(final String s) {
        this.tooltip = s;
        final TrayIconPeer peer = this.peer;
        if (peer != null) {
            peer.setToolTip(s);
        }
    }
    
    public String getToolTip() {
        return this.tooltip;
    }
    
    public void setImageAutoSize(final boolean autosize) {
        this.autosize = autosize;
        final TrayIconPeer peer = this.peer;
        if (peer != null) {
            peer.updateImage();
        }
    }
    
    public boolean isImageAutoSize() {
        return this.autosize;
    }
    
    public synchronized void addMouseListener(final MouseListener mouseListener) {
        if (mouseListener == null) {
            return;
        }
        this.mouseListener = AWTEventMulticaster.add(this.mouseListener, mouseListener);
    }
    
    public synchronized void removeMouseListener(final MouseListener mouseListener) {
        if (mouseListener == null) {
            return;
        }
        this.mouseListener = AWTEventMulticaster.remove(this.mouseListener, mouseListener);
    }
    
    public synchronized MouseListener[] getMouseListeners() {
        return AWTEventMulticaster.getListeners(this.mouseListener, MouseListener.class);
    }
    
    public synchronized void addMouseMotionListener(final MouseMotionListener mouseMotionListener) {
        if (mouseMotionListener == null) {
            return;
        }
        this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, mouseMotionListener);
    }
    
    public synchronized void removeMouseMotionListener(final MouseMotionListener mouseMotionListener) {
        if (mouseMotionListener == null) {
            return;
        }
        this.mouseMotionListener = AWTEventMulticaster.remove(this.mouseMotionListener, mouseMotionListener);
    }
    
    public synchronized MouseMotionListener[] getMouseMotionListeners() {
        return AWTEventMulticaster.getListeners(this.mouseMotionListener, MouseMotionListener.class);
    }
    
    public String getActionCommand() {
        return this.actionCommand;
    }
    
    public void setActionCommand(final String actionCommand) {
        this.actionCommand = actionCommand;
    }
    
    public synchronized void addActionListener(final ActionListener actionListener) {
        if (actionListener == null) {
            return;
        }
        this.actionListener = AWTEventMulticaster.add(this.actionListener, actionListener);
    }
    
    public synchronized void removeActionListener(final ActionListener actionListener) {
        if (actionListener == null) {
            return;
        }
        this.actionListener = AWTEventMulticaster.remove(this.actionListener, actionListener);
    }
    
    public synchronized ActionListener[] getActionListeners() {
        return AWTEventMulticaster.getListeners(this.actionListener, ActionListener.class);
    }
    
    public void displayMessage(final String s, final String s2, final MessageType messageType) {
        if (s == null && s2 == null) {
            throw new NullPointerException("displaying the message with both caption and text being null");
        }
        final TrayIconPeer peer = this.peer;
        if (peer != null) {
            peer.displayMessage(s, s2, messageType.name());
        }
    }
    
    public Dimension getSize() {
        return SystemTray.getSystemTray().getTrayIconSize();
    }
    
    void addNotify() throws AWTException {
        synchronized (this) {
            if (this.peer == null) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                if (defaultToolkit instanceof SunToolkit) {
                    this.peer = ((SunToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
                }
                else if (defaultToolkit instanceof HeadlessToolkit) {
                    this.peer = ((HeadlessToolkit)Toolkit.getDefaultToolkit()).createTrayIcon(this);
                }
            }
        }
        this.peer.setToolTip(this.tooltip);
    }
    
    void removeNotify() {
        TrayIconPeer peer = null;
        synchronized (this) {
            peer = this.peer;
            this.peer = null;
        }
        if (peer != null) {
            peer.dispose();
        }
    }
    
    void setID(final int id) {
        this.id = id;
    }
    
    int getID() {
        return this.id;
    }
    
    void dispatchEvent(final AWTEvent currentEventAndMostRecentTime) {
        EventQueue.setCurrentEventAndMostRecentTime(currentEventAndMostRecentTime);
        Toolkit.getDefaultToolkit().notifyAWTEventListeners(currentEventAndMostRecentTime);
        this.processEvent(currentEventAndMostRecentTime);
    }
    
    void processEvent(final AWTEvent awtEvent) {
        if (awtEvent instanceof MouseEvent) {
            switch (awtEvent.getID()) {
                case 500:
                case 501:
                case 502: {
                    this.processMouseEvent((MouseEvent)awtEvent);
                    break;
                }
                case 503: {
                    this.processMouseMotionEvent((MouseEvent)awtEvent);
                    break;
                }
                default: {}
            }
        }
        else if (awtEvent instanceof ActionEvent) {
            this.processActionEvent((ActionEvent)awtEvent);
        }
    }
    
    void processMouseEvent(final MouseEvent mouseEvent) {
        final MouseListener mouseListener = this.mouseListener;
        if (mouseListener != null) {
            switch (mouseEvent.getID()) {
                case 501: {
                    mouseListener.mousePressed(mouseEvent);
                    break;
                }
                case 502: {
                    mouseListener.mouseReleased(mouseEvent);
                    break;
                }
                case 500: {
                    mouseListener.mouseClicked(mouseEvent);
                    break;
                }
                default: {}
            }
        }
    }
    
    void processMouseMotionEvent(final MouseEvent mouseEvent) {
        final MouseMotionListener mouseMotionListener = this.mouseMotionListener;
        if (mouseMotionListener != null && mouseEvent.getID() == 503) {
            mouseMotionListener.mouseMoved(mouseEvent);
        }
    }
    
    void processActionEvent(final ActionEvent actionEvent) {
        final ActionListener actionListener = this.actionListener;
        if (actionListener != null) {
            actionListener.actionPerformed(actionEvent);
        }
    }
    
    private static native void initIDs();
    
    static {
        Toolkit.loadLibraries();
        if (!GraphicsEnvironment.isHeadless()) {
            initIDs();
        }
        AWTAccessor.setTrayIconAccessor(new AWTAccessor.TrayIconAccessor() {
            @Override
            public void addNotify(final TrayIcon trayIcon) throws AWTException {
                trayIcon.addNotify();
            }
            
            @Override
            public void removeNotify(final TrayIcon trayIcon) {
                trayIcon.removeNotify();
            }
        });
    }
    
    public enum MessageType
    {
        ERROR, 
        WARNING, 
        INFO, 
        NONE;
    }
}
