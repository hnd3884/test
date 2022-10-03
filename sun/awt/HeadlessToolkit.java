package sun.awt;

import java.awt.peer.DesktopPeer;
import java.awt.Desktop;
import java.awt.event.AWTEventListener;
import java.beans.PropertyChangeListener;
import java.awt.FontMetrics;
import java.awt.Font;
import sun.awt.datatransfer.DataTransferer;
import java.awt.peer.FontPeer;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.awt.image.ImageObserver;
import java.awt.EventQueue;
import java.util.Properties;
import java.awt.PrintJob;
import java.awt.PageAttributes;
import java.awt.JobAttributes;
import java.awt.datatransfer.Clipboard;
import java.awt.Insets;
import java.awt.GraphicsConfiguration;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Image;
import java.util.Map;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.peer.SystemTrayPeer;
import java.awt.SystemTray;
import java.awt.peer.TrayIconPeer;
import java.awt.TrayIcon;
import java.awt.AWTException;
import java.awt.peer.RobotPeer;
import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.CheckboxMenuItem;
import java.awt.peer.MenuItemPeer;
import java.awt.MenuItem;
import java.awt.peer.PopupMenuPeer;
import java.awt.PopupMenu;
import java.awt.peer.MenuPeer;
import java.awt.Menu;
import java.awt.peer.MenuBarPeer;
import java.awt.MenuBar;
import java.awt.peer.FileDialogPeer;
import java.awt.FileDialog;
import java.awt.peer.TextAreaPeer;
import java.awt.TextArea;
import java.awt.peer.ScrollPanePeer;
import java.awt.ScrollPane;
import java.awt.peer.ScrollbarPeer;
import java.awt.Scrollbar;
import java.awt.peer.CheckboxPeer;
import java.awt.Checkbox;
import java.awt.peer.ListPeer;
import java.awt.List;
import java.awt.peer.LabelPeer;
import java.awt.Label;
import java.awt.peer.ChoicePeer;
import java.awt.Choice;
import java.awt.peer.TextFieldPeer;
import java.awt.TextField;
import java.awt.peer.ButtonPeer;
import java.awt.Button;
import java.awt.peer.DialogPeer;
import java.awt.Dialog;
import java.awt.peer.FramePeer;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.peer.WindowPeer;
import java.awt.Window;
import java.awt.peer.PanelPeer;
import java.awt.Panel;
import java.awt.Component;
import java.awt.peer.CanvasPeer;
import java.awt.Canvas;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.Toolkit;

public class HeadlessToolkit extends Toolkit implements ComponentFactory, KeyboardFocusManagerPeerProvider
{
    private static final KeyboardFocusManagerPeer kfmPeer;
    private Toolkit tk;
    private ComponentFactory componentFactory;
    
    public HeadlessToolkit(final Toolkit tk) {
        this.tk = tk;
        if (tk instanceof ComponentFactory) {
            this.componentFactory = (ComponentFactory)tk;
        }
    }
    
    public Toolkit getUnderlyingToolkit() {
        return this.tk;
    }
    
    @Override
    public CanvasPeer createCanvas(final Canvas canvas) {
        return (CanvasPeer)this.createComponent(canvas);
    }
    
    @Override
    public PanelPeer createPanel(final Panel panel) {
        return (PanelPeer)this.createComponent(panel);
    }
    
    @Override
    public WindowPeer createWindow(final Window window) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public FramePeer createFrame(final Frame frame) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public DialogPeer createDialog(final Dialog dialog) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public ButtonPeer createButton(final Button button) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public TextFieldPeer createTextField(final TextField textField) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public ChoicePeer createChoice(final Choice choice) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public LabelPeer createLabel(final Label label) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public ListPeer createList(final List list) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public CheckboxPeer createCheckbox(final Checkbox checkbox) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public ScrollbarPeer createScrollbar(final Scrollbar scrollbar) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public ScrollPanePeer createScrollPane(final ScrollPane scrollPane) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public TextAreaPeer createTextArea(final TextArea textArea) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public FileDialogPeer createFileDialog(final FileDialog fileDialog) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public MenuBarPeer createMenuBar(final MenuBar menuBar) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public MenuPeer createMenu(final Menu menu) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public PopupMenuPeer createPopupMenu(final PopupMenu popupMenu) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public MenuItemPeer createMenuItem(final MenuItem menuItem) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public CheckboxMenuItemPeer createCheckboxMenuItem(final CheckboxMenuItem checkboxMenuItem) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent dragGestureEvent) throws InvalidDnDOperationException {
        throw new InvalidDnDOperationException("Headless environment");
    }
    
    @Override
    public RobotPeer createRobot(final Robot robot, final GraphicsDevice graphicsDevice) throws AWTException, HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() {
        return HeadlessToolkit.kfmPeer;
    }
    
    public TrayIconPeer createTrayIcon(final TrayIcon trayIcon) throws HeadlessException {
        throw new HeadlessException();
    }
    
    public SystemTrayPeer createSystemTray(final SystemTray systemTray) throws HeadlessException {
        throw new HeadlessException();
    }
    
    public boolean isTraySupported() {
        return false;
    }
    
    public GlobalCursorManager getGlobalCursorManager() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    protected void loadSystemColors(final int[] array) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public ColorModel getColorModel() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public int getScreenResolution() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Map mapInputMethodHighlight(final InputMethodHighlight inputMethodHighlight) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public int getMenuShortcutKeyMask() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public boolean getLockingKeyState(final int n) throws UnsupportedOperationException {
        throw new HeadlessException();
    }
    
    @Override
    public void setLockingKeyState(final int n, final boolean b) throws UnsupportedOperationException {
        throw new HeadlessException();
    }
    
    @Override
    public Cursor createCustomCursor(final Image image, final Point point, final String s) throws IndexOutOfBoundsException, HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Dimension getBestCursorSize(final int n, final int n2) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public int getMaximumCursorColors() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public <T extends DragGestureRecognizer> T createDragGestureRecognizer(final Class<T> clazz, final DragSource dragSource, final Component component, final int n, final DragGestureListener dragGestureListener) {
        return null;
    }
    
    public int getScreenHeight() throws HeadlessException {
        throw new HeadlessException();
    }
    
    public int getScreenWidth() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Dimension getScreenSize() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Insets getScreenInsets(final GraphicsConfiguration graphicsConfiguration) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public void setDynamicLayout(final boolean b) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    protected boolean isDynamicLayoutSet() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public boolean isDynamicLayoutActive() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public Clipboard getSystemClipboard() throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public PrintJob getPrintJob(final Frame frame, final String s, final JobAttributes jobAttributes, final PageAttributes pageAttributes) {
        if (frame != null) {
            throw new HeadlessException();
        }
        throw new NullPointerException("frame must not be null");
    }
    
    @Override
    public PrintJob getPrintJob(final Frame frame, final String s, final Properties properties) {
        if (frame != null) {
            throw new HeadlessException();
        }
        throw new NullPointerException("frame must not be null");
    }
    
    @Override
    public void sync() {
    }
    
    @Override
    public void beep() {
        System.out.write(7);
    }
    
    public EventQueue getSystemEventQueueImpl() {
        return SunToolkit.getSystemEventQueueImplPP();
    }
    
    @Override
    public int checkImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return this.tk.checkImage(image, n, n2, imageObserver);
    }
    
    @Override
    public boolean prepareImage(final Image image, final int n, final int n2, final ImageObserver imageObserver) {
        return this.tk.prepareImage(image, n, n2, imageObserver);
    }
    
    @Override
    public Image getImage(final String s) {
        return this.tk.getImage(s);
    }
    
    @Override
    public Image getImage(final URL url) {
        return this.tk.getImage(url);
    }
    
    @Override
    public Image createImage(final String s) {
        return this.tk.createImage(s);
    }
    
    @Override
    public Image createImage(final URL url) {
        return this.tk.createImage(url);
    }
    
    @Override
    public Image createImage(final byte[] array, final int n, final int n2) {
        return this.tk.createImage(array, n, n2);
    }
    
    @Override
    public Image createImage(final ImageProducer imageProducer) {
        return this.tk.createImage(imageProducer);
    }
    
    @Override
    public Image createImage(final byte[] array) {
        return this.tk.createImage(array);
    }
    
    @Override
    public FontPeer getFontPeer(final String s, final int n) {
        if (this.componentFactory != null) {
            return this.componentFactory.getFontPeer(s, n);
        }
        return null;
    }
    
    @Override
    public DataTransferer getDataTransferer() {
        return null;
    }
    
    @Override
    public FontMetrics getFontMetrics(final Font font) {
        return this.tk.getFontMetrics(font);
    }
    
    @Override
    public String[] getFontList() {
        return this.tk.getFontList();
    }
    
    @Override
    public void addPropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.tk.addPropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    public void removePropertyChangeListener(final String s, final PropertyChangeListener propertyChangeListener) {
        this.tk.removePropertyChangeListener(s, propertyChangeListener);
    }
    
    @Override
    public boolean isModalityTypeSupported(final Dialog.ModalityType modalityType) {
        return false;
    }
    
    @Override
    public boolean isModalExclusionTypeSupported(final Dialog.ModalExclusionType modalExclusionType) {
        return false;
    }
    
    @Override
    public boolean isAlwaysOnTopSupported() {
        return false;
    }
    
    @Override
    public void addAWTEventListener(final AWTEventListener awtEventListener, final long n) {
        this.tk.addAWTEventListener(awtEventListener, n);
    }
    
    @Override
    public void removeAWTEventListener(final AWTEventListener awtEventListener) {
        this.tk.removeAWTEventListener(awtEventListener);
    }
    
    @Override
    public AWTEventListener[] getAWTEventListeners() {
        return this.tk.getAWTEventListeners();
    }
    
    @Override
    public AWTEventListener[] getAWTEventListeners(final long n) {
        return this.tk.getAWTEventListeners(n);
    }
    
    public boolean isDesktopSupported() {
        return false;
    }
    
    public DesktopPeer createDesktopPeer(final Desktop desktop) throws HeadlessException {
        throw new HeadlessException();
    }
    
    @Override
    public boolean areExtraMouseButtonsEnabled() throws HeadlessException {
        throw new HeadlessException();
    }
    
    static {
        kfmPeer = new KeyboardFocusManagerPeer() {
            @Override
            public void setCurrentFocusedWindow(final Window window) {
            }
            
            @Override
            public Window getCurrentFocusedWindow() {
                return null;
            }
            
            @Override
            public void setCurrentFocusOwner(final Component component) {
            }
            
            @Override
            public Component getCurrentFocusOwner() {
                return null;
            }
            
            @Override
            public void clearGlobalFocusOwner(final Window window) {
            }
        };
    }
}
