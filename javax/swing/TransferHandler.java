package javax.swing;

import sun.misc.SharedSecrets;
import sun.swing.SwingUtilities2;
import java.security.AccessControlContext;
import java.awt.AWTEvent;
import sun.awt.AWTAccessor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.JavaSecurityAccess;
import sun.swing.UIAction;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.Cursor;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.TooManyListenersException;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.UIResource;
import java.awt.dnd.DropTarget;
import java.io.IOException;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.text.JTextComponent;
import sun.swing.SwingAccessor;
import sun.awt.SunToolkit;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.Component;
import sun.awt.AppContext;
import java.awt.dnd.DropTargetListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.awt.datatransfer.DataFlavor;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import sun.reflect.misc.MethodUtil;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureListener;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.InputEvent;
import java.awt.Point;
import java.awt.Image;
import java.io.Serializable;

public class TransferHandler implements Serializable
{
    public static final int NONE = 0;
    public static final int COPY = 1;
    public static final int MOVE = 2;
    public static final int COPY_OR_MOVE = 3;
    public static final int LINK = 1073741824;
    private Image dragImage;
    private Point dragImageOffset;
    private String propertyName;
    private static SwingDragGestureRecognizer recognizer;
    static final Action cutAction;
    static final Action copyAction;
    static final Action pasteAction;
    
    public static Action getCutAction() {
        return TransferHandler.cutAction;
    }
    
    public static Action getCopyAction() {
        return TransferHandler.copyAction;
    }
    
    public static Action getPasteAction() {
        return TransferHandler.pasteAction;
    }
    
    public TransferHandler(final String propertyName) {
        this.propertyName = propertyName;
    }
    
    protected TransferHandler() {
        this(null);
    }
    
    public void setDragImage(final Image dragImage) {
        this.dragImage = dragImage;
    }
    
    public Image getDragImage() {
        return this.dragImage;
    }
    
    public void setDragImageOffset(final Point point) {
        this.dragImageOffset = new Point(point);
    }
    
    public Point getDragImageOffset() {
        if (this.dragImageOffset == null) {
            return new Point(0, 0);
        }
        return new Point(this.dragImageOffset);
    }
    
    public void exportAsDrag(final JComponent component, final InputEvent inputEvent, int n) {
        final int sourceActions = this.getSourceActions(component);
        if (!(inputEvent instanceof MouseEvent) || (n != 1 && n != 2 && n != 1073741824) || (sourceActions & n) == 0x0) {
            n = 0;
        }
        if (n != 0 && !GraphicsEnvironment.isHeadless()) {
            if (TransferHandler.recognizer == null) {
                TransferHandler.recognizer = new SwingDragGestureRecognizer(new DragHandler());
            }
            TransferHandler.recognizer.gestured(component, (MouseEvent)inputEvent, sourceActions, n);
        }
        else {
            this.exportDone(component, null, 0);
        }
    }
    
    public void exportToClipboard(final JComponent component, final Clipboard clipboard, final int n) throws IllegalStateException {
        if ((n == 1 || n == 2) && (this.getSourceActions(component) & n) != 0x0) {
            final Transferable transferable = this.createTransferable(component);
            if (transferable != null) {
                try {
                    clipboard.setContents(transferable, null);
                    this.exportDone(component, transferable, n);
                    return;
                }
                catch (final IllegalStateException ex) {
                    this.exportDone(component, transferable, 0);
                    throw ex;
                }
            }
        }
        this.exportDone(component, null, 0);
    }
    
    public boolean importData(final TransferSupport transferSupport) {
        return transferSupport.getComponent() instanceof JComponent && this.importData((JComponent)transferSupport.getComponent(), transferSupport.getTransferable());
    }
    
    public boolean importData(final JComponent component, final Transferable transferable) {
        final PropertyDescriptor propertyDescriptor = this.getPropertyDescriptor(component);
        if (propertyDescriptor != null) {
            final Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod == null) {
                return false;
            }
            final Class<?>[] parameterTypes = writeMethod.getParameterTypes();
            if (parameterTypes.length != 1) {
                return false;
            }
            final DataFlavor propertyDataFlavor = this.getPropertyDataFlavor(parameterTypes[0], transferable.getTransferDataFlavors());
            if (propertyDataFlavor != null) {
                try {
                    MethodUtil.invoke(writeMethod, component, new Object[] { transferable.getTransferData(propertyDataFlavor) });
                    return true;
                }
                catch (final Exception ex) {
                    System.err.println("Invocation failed");
                }
            }
        }
        return false;
    }
    
    public boolean canImport(final TransferSupport transferSupport) {
        return transferSupport.getComponent() instanceof JComponent && this.canImport((JComponent)transferSupport.getComponent(), transferSupport.getDataFlavors());
    }
    
    public boolean canImport(final JComponent component, final DataFlavor[] array) {
        final PropertyDescriptor propertyDescriptor = this.getPropertyDescriptor(component);
        if (propertyDescriptor != null) {
            final Method writeMethod = propertyDescriptor.getWriteMethod();
            if (writeMethod == null) {
                return false;
            }
            final Class<?>[] parameterTypes = writeMethod.getParameterTypes();
            if (parameterTypes.length != 1) {
                return false;
            }
            if (this.getPropertyDataFlavor(parameterTypes[0], array) != null) {
                return true;
            }
        }
        return false;
    }
    
    public int getSourceActions(final JComponent component) {
        if (this.getPropertyDescriptor(component) != null) {
            return 1;
        }
        return 0;
    }
    
    public Icon getVisualRepresentation(final Transferable transferable) {
        return null;
    }
    
    protected Transferable createTransferable(final JComponent component) {
        final PropertyDescriptor propertyDescriptor = this.getPropertyDescriptor(component);
        if (propertyDescriptor != null) {
            return new PropertyTransferable(propertyDescriptor, component);
        }
        return null;
    }
    
    protected void exportDone(final JComponent component, final Transferable transferable, final int n) {
    }
    
    private PropertyDescriptor getPropertyDescriptor(final JComponent component) {
        if (this.propertyName == null) {
            return null;
        }
        final Class<? extends JComponent> class1 = component.getClass();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(class1);
        }
        catch (final IntrospectionException ex) {
            return null;
        }
        final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; ++i) {
            if (this.propertyName.equals(propertyDescriptors[i].getName())) {
                final Method readMethod = propertyDescriptors[i].getReadMethod();
                if (readMethod != null) {
                    final Class<?>[] parameterTypes = readMethod.getParameterTypes();
                    if (parameterTypes == null || parameterTypes.length == 0) {
                        return propertyDescriptors[i];
                    }
                }
            }
        }
        return null;
    }
    
    private DataFlavor getPropertyDataFlavor(final Class<?> clazz, final DataFlavor[] array) {
        for (int i = 0; i < array.length; ++i) {
            final DataFlavor dataFlavor = array[i];
            if ("application".equals(dataFlavor.getPrimaryType()) && "x-java-jvm-local-objectref".equals(dataFlavor.getSubType()) && clazz.isAssignableFrom(dataFlavor.getRepresentationClass())) {
                return dataFlavor;
            }
        }
        return null;
    }
    
    private static DropTargetListener getDropTargetListener() {
        synchronized (DropHandler.class) {
            DropHandler dropHandler = (DropHandler)AppContext.getAppContext().get(DropHandler.class);
            if (dropHandler == null) {
                dropHandler = new DropHandler();
                AppContext.getAppContext().put(DropHandler.class, dropHandler);
            }
            return dropHandler;
        }
    }
    
    static {
        TransferHandler.recognizer = null;
        cutAction = new TransferAction("cut");
        copyAction = new TransferAction("copy");
        pasteAction = new TransferAction("paste");
    }
    
    public static class DropLocation
    {
        private final Point dropPoint;
        
        protected DropLocation(final Point point) {
            if (point == null) {
                throw new IllegalArgumentException("Point cannot be null");
            }
            this.dropPoint = new Point(point);
        }
        
        public final Point getDropPoint() {
            return new Point(this.dropPoint);
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[dropPoint=" + this.dropPoint + "]";
        }
    }
    
    public static final class TransferSupport
    {
        private boolean isDrop;
        private Component component;
        private boolean showDropLocationIsSet;
        private boolean showDropLocation;
        private int dropAction;
        private Object source;
        private DropLocation dropLocation;
        
        private TransferSupport(final Component component, final DropTargetEvent dropTargetEvent) {
            this.dropAction = -1;
            this.isDrop = true;
            this.setDNDVariables(component, dropTargetEvent);
        }
        
        public TransferSupport(final Component component, final Transferable source) {
            this.dropAction = -1;
            if (component == null) {
                throw new NullPointerException("component is null");
            }
            if (source == null) {
                throw new NullPointerException("transferable is null");
            }
            this.isDrop = false;
            this.component = component;
            this.source = source;
        }
        
        private void setDNDVariables(final Component component, final DropTargetEvent source) {
            assert this.isDrop;
            this.component = component;
            this.source = source;
            this.dropLocation = null;
            this.dropAction = -1;
            this.showDropLocationIsSet = false;
            if (this.source == null) {
                return;
            }
            assert this.source instanceof DropTargetDragEvent || this.source instanceof DropTargetDropEvent;
            final Point point = (this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getLocation() : ((DropTargetDropEvent)this.source).getLocation();
            if (SunToolkit.isInstanceOf(component, "javax.swing.text.JTextComponent")) {
                this.dropLocation = SwingAccessor.getJTextComponentAccessor().dropLocationForPoint((JTextComponent)component, point);
            }
            else if (component instanceof JComponent) {
                this.dropLocation = ((JComponent)component).dropLocationForPoint(point);
            }
        }
        
        public boolean isDrop() {
            return this.isDrop;
        }
        
        public Component getComponent() {
            return this.component;
        }
        
        private void assureIsDrop() {
            if (!this.isDrop) {
                throw new IllegalStateException("Not a drop");
            }
        }
        
        public DropLocation getDropLocation() {
            this.assureIsDrop();
            if (this.dropLocation == null) {
                this.dropLocation = new DropLocation((this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getLocation() : ((DropTargetDropEvent)this.source).getLocation());
            }
            return this.dropLocation;
        }
        
        public void setShowDropLocation(final boolean showDropLocation) {
            this.assureIsDrop();
            this.showDropLocation = showDropLocation;
            this.showDropLocationIsSet = true;
        }
        
        public void setDropAction(final int dropAction) {
            this.assureIsDrop();
            final int n = dropAction & this.getSourceDropActions();
            if (n != 1 && n != 2 && n != 1073741824) {
                throw new IllegalArgumentException("unsupported drop action: " + dropAction);
            }
            this.dropAction = dropAction;
        }
        
        public int getDropAction() {
            return (this.dropAction == -1) ? this.getUserDropAction() : this.dropAction;
        }
        
        public int getUserDropAction() {
            this.assureIsDrop();
            return (this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getDropAction() : ((DropTargetDropEvent)this.source).getDropAction();
        }
        
        public int getSourceDropActions() {
            this.assureIsDrop();
            return (this.source instanceof DropTargetDragEvent) ? ((DropTargetDragEvent)this.source).getSourceActions() : ((DropTargetDropEvent)this.source).getSourceActions();
        }
        
        public DataFlavor[] getDataFlavors() {
            if (!this.isDrop) {
                return ((Transferable)this.source).getTransferDataFlavors();
            }
            if (this.source instanceof DropTargetDragEvent) {
                return ((DropTargetDragEvent)this.source).getCurrentDataFlavors();
            }
            return ((DropTargetDropEvent)this.source).getCurrentDataFlavors();
        }
        
        public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
            if (!this.isDrop) {
                return ((Transferable)this.source).isDataFlavorSupported(dataFlavor);
            }
            if (this.source instanceof DropTargetDragEvent) {
                return ((DropTargetDragEvent)this.source).isDataFlavorSupported(dataFlavor);
            }
            return ((DropTargetDropEvent)this.source).isDataFlavorSupported(dataFlavor);
        }
        
        public Transferable getTransferable() {
            if (!this.isDrop) {
                return (Transferable)this.source;
            }
            if (this.source instanceof DropTargetDragEvent) {
                return ((DropTargetDragEvent)this.source).getTransferable();
            }
            return ((DropTargetDropEvent)this.source).getTransferable();
        }
    }
    
    static class PropertyTransferable implements Transferable
    {
        JComponent component;
        PropertyDescriptor property;
        
        PropertyTransferable(final PropertyDescriptor property, final JComponent component) {
            this.property = property;
            this.component = component;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] array = { null };
            final String string = "application/x-java-jvm-local-objectref;class=" + this.property.getPropertyType().getName();
            try {
                array[0] = new DataFlavor(string);
            }
            catch (final ClassNotFoundException ex) {
                array = new DataFlavor[0];
            }
            return array;
        }
        
        @Override
        public boolean isDataFlavorSupported(final DataFlavor dataFlavor) {
            final Class<?> propertyType = this.property.getPropertyType();
            return "application".equals(dataFlavor.getPrimaryType()) && "x-java-jvm-local-objectref".equals(dataFlavor.getSubType()) && dataFlavor.getRepresentationClass().isAssignableFrom(propertyType);
        }
        
        @Override
        public Object getTransferData(final DataFlavor dataFlavor) throws UnsupportedFlavorException, IOException {
            if (!this.isDataFlavorSupported(dataFlavor)) {
                throw new UnsupportedFlavorException(dataFlavor);
            }
            final Method readMethod = this.property.getReadMethod();
            Object invoke;
            try {
                invoke = MethodUtil.invoke(readMethod, this.component, null);
            }
            catch (final Exception ex) {
                throw new IOException("Property read failed: " + this.property.getName());
            }
            return invoke;
        }
    }
    
    static class SwingDropTarget extends DropTarget implements UIResource
    {
        private EventListenerList listenerList;
        
        SwingDropTarget(final Component component) {
            super(component, 1073741827, null);
            try {
                super.addDropTargetListener(getDropTargetListener());
            }
            catch (final TooManyListenersException ex) {}
        }
        
        @Override
        public void addDropTargetListener(final DropTargetListener dropTargetListener) throws TooManyListenersException {
            if (this.listenerList == null) {
                this.listenerList = new EventListenerList();
            }
            this.listenerList.add(DropTargetListener.class, dropTargetListener);
        }
        
        @Override
        public void removeDropTargetListener(final DropTargetListener dropTargetListener) {
            if (this.listenerList != null) {
                this.listenerList.remove(DropTargetListener.class, dropTargetListener);
            }
        }
        
        @Override
        public void dragEnter(final DropTargetDragEvent dropTargetDragEvent) {
            super.dragEnter(dropTargetDragEvent);
            if (this.listenerList != null) {
                final Object[] listenerList = this.listenerList.getListenerList();
                for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                    if (listenerList[i] == DropTargetListener.class) {
                        ((DropTargetListener)listenerList[i + 1]).dragEnter(dropTargetDragEvent);
                    }
                }
            }
        }
        
        @Override
        public void dragOver(final DropTargetDragEvent dropTargetDragEvent) {
            super.dragOver(dropTargetDragEvent);
            if (this.listenerList != null) {
                final Object[] listenerList = this.listenerList.getListenerList();
                for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                    if (listenerList[i] == DropTargetListener.class) {
                        ((DropTargetListener)listenerList[i + 1]).dragOver(dropTargetDragEvent);
                    }
                }
            }
        }
        
        @Override
        public void dragExit(final DropTargetEvent dropTargetEvent) {
            super.dragExit(dropTargetEvent);
            if (this.listenerList != null) {
                final Object[] listenerList = this.listenerList.getListenerList();
                for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                    if (listenerList[i] == DropTargetListener.class) {
                        ((DropTargetListener)listenerList[i + 1]).dragExit(dropTargetEvent);
                    }
                }
            }
            if (!this.isActive()) {
                final DropTargetListener access$200 = getDropTargetListener();
                if (access$200 != null && access$200 instanceof DropHandler) {
                    ((DropHandler)access$200).cleanup(false);
                }
            }
        }
        
        @Override
        public void drop(final DropTargetDropEvent dropTargetDropEvent) {
            super.drop(dropTargetDropEvent);
            if (this.listenerList != null) {
                final Object[] listenerList = this.listenerList.getListenerList();
                for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                    if (listenerList[i] == DropTargetListener.class) {
                        ((DropTargetListener)listenerList[i + 1]).drop(dropTargetDropEvent);
                    }
                }
            }
        }
        
        @Override
        public void dropActionChanged(final DropTargetDragEvent dropTargetDragEvent) {
            super.dropActionChanged(dropTargetDragEvent);
            if (this.listenerList != null) {
                final Object[] listenerList = this.listenerList.getListenerList();
                for (int i = listenerList.length - 2; i >= 0; i -= 2) {
                    if (listenerList[i] == DropTargetListener.class) {
                        ((DropTargetListener)listenerList[i + 1]).dropActionChanged(dropTargetDragEvent);
                    }
                }
            }
        }
    }
    
    private static class DropHandler implements DropTargetListener, Serializable, ActionListener
    {
        private Timer timer;
        private Point lastPosition;
        private Rectangle outer;
        private Rectangle inner;
        private int hysteresis;
        private Component component;
        private Object state;
        private TransferSupport support;
        private static final int AUTOSCROLL_INSET = 10;
        
        private DropHandler() {
            this.outer = new Rectangle();
            this.inner = new Rectangle();
            this.hysteresis = 10;
            this.support = new TransferSupport((Component)null, (DropTargetEvent)null);
        }
        
        private void updateAutoscrollRegion(final JComponent component) {
            final Rectangle visibleRect = component.getVisibleRect();
            this.outer.setBounds(visibleRect.x, visibleRect.y, visibleRect.width, visibleRect.height);
            final Insets insets = new Insets(0, 0, 0, 0);
            if (component instanceof Scrollable) {
                final int n = 20;
                if (visibleRect.width >= n) {
                    final Insets insets2 = insets;
                    final Insets insets3 = insets;
                    final int n2 = 10;
                    insets3.right = n2;
                    insets2.left = n2;
                }
                if (visibleRect.height >= n) {
                    final Insets insets4 = insets;
                    final Insets insets5 = insets;
                    final int n3 = 10;
                    insets5.bottom = n3;
                    insets4.top = n3;
                }
            }
            this.inner.setBounds(visibleRect.x + insets.left, visibleRect.y + insets.top, visibleRect.width - (insets.left + insets.right), visibleRect.height - (insets.top + insets.bottom));
        }
        
        private void autoscroll(final JComponent component, final Point point) {
            if (component instanceof Scrollable) {
                final Scrollable scrollable = (Scrollable)component;
                if (point.y < this.inner.y) {
                    final int scrollableUnitIncrement = scrollable.getScrollableUnitIncrement(this.outer, 1, -1);
                    component.scrollRectToVisible(new Rectangle(this.inner.x, this.outer.y - scrollableUnitIncrement, this.inner.width, scrollableUnitIncrement));
                }
                else if (point.y > this.inner.y + this.inner.height) {
                    component.scrollRectToVisible(new Rectangle(this.inner.x, this.outer.y + this.outer.height, this.inner.width, scrollable.getScrollableUnitIncrement(this.outer, 1, 1)));
                }
                if (point.x < this.inner.x) {
                    final int scrollableUnitIncrement2 = scrollable.getScrollableUnitIncrement(this.outer, 0, -1);
                    component.scrollRectToVisible(new Rectangle(this.outer.x - scrollableUnitIncrement2, this.inner.y, scrollableUnitIncrement2, this.inner.height));
                }
                else if (point.x > this.inner.x + this.inner.width) {
                    component.scrollRectToVisible(new Rectangle(this.outer.x + this.outer.width, this.inner.y, scrollable.getScrollableUnitIncrement(this.outer, 0, 1), this.inner.height));
                }
            }
        }
        
        private void initPropertiesIfNecessary() {
            if (this.timer == null) {
                final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                final Integer n = (Integer)defaultToolkit.getDesktopProperty("DnD.Autoscroll.interval");
                this.timer = new Timer((n == null) ? 100 : n, this);
                final Integer n2 = (Integer)defaultToolkit.getDesktopProperty("DnD.Autoscroll.initialDelay");
                this.timer.setInitialDelay((n2 == null) ? 100 : ((int)n2));
                final Integer n3 = (Integer)defaultToolkit.getDesktopProperty("DnD.Autoscroll.cursorHysteresis");
                if (n3 != null) {
                    this.hysteresis = n3;
                }
            }
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            this.updateAutoscrollRegion((JComponent)this.component);
            if (this.outer.contains(this.lastPosition) && !this.inner.contains(this.lastPosition)) {
                this.autoscroll((JComponent)this.component, this.lastPosition);
            }
        }
        
        private void setComponentDropLocation(final TransferSupport transferSupport, final boolean b) {
            final DropLocation dropLocation = (transferSupport == null) ? null : transferSupport.getDropLocation();
            if (SunToolkit.isInstanceOf(this.component, "javax.swing.text.JTextComponent")) {
                this.state = SwingAccessor.getJTextComponentAccessor().setDropLocation((JTextComponent)this.component, dropLocation, this.state, b);
            }
            else if (this.component instanceof JComponent) {
                this.state = ((JComponent)this.component).setDropLocation(dropLocation, this.state, b);
            }
        }
        
        private void handleDrag(final DropTargetDragEvent dropTargetDragEvent) {
            final TransferHandler transferHandler = ((HasGetTransferHandler)this.component).getTransferHandler();
            if (transferHandler == null) {
                dropTargetDragEvent.rejectDrag();
                this.setComponentDropLocation(null, false);
                return;
            }
            this.support.setDNDVariables(this.component, dropTargetDragEvent);
            final boolean canImport = transferHandler.canImport(this.support);
            if (canImport) {
                dropTargetDragEvent.acceptDrag(this.support.getDropAction());
            }
            else {
                dropTargetDragEvent.rejectDrag();
            }
            this.setComponentDropLocation((this.support.showDropLocationIsSet ? this.support.showDropLocation : canImport) ? this.support : null, false);
        }
        
        @Override
        public void dragEnter(final DropTargetDragEvent dropTargetDragEvent) {
            this.state = null;
            this.component = dropTargetDragEvent.getDropTargetContext().getComponent();
            this.handleDrag(dropTargetDragEvent);
            if (this.component instanceof JComponent) {
                this.lastPosition = dropTargetDragEvent.getLocation();
                this.updateAutoscrollRegion((JComponent)this.component);
                this.initPropertiesIfNecessary();
            }
        }
        
        @Override
        public void dragOver(final DropTargetDragEvent dropTargetDragEvent) {
            this.handleDrag(dropTargetDragEvent);
            if (!(this.component instanceof JComponent)) {
                return;
            }
            final Point location = dropTargetDragEvent.getLocation();
            if (Math.abs(location.x - this.lastPosition.x) > this.hysteresis || Math.abs(location.y - this.lastPosition.y) > this.hysteresis) {
                if (this.timer.isRunning()) {
                    this.timer.stop();
                }
            }
            else if (!this.timer.isRunning()) {
                this.timer.start();
            }
            this.lastPosition = location;
        }
        
        @Override
        public void dragExit(final DropTargetEvent dropTargetEvent) {
            this.cleanup(false);
        }
        
        @Override
        public void drop(final DropTargetDropEvent dropTargetDropEvent) {
            final TransferHandler transferHandler = ((HasGetTransferHandler)this.component).getTransferHandler();
            if (transferHandler == null) {
                dropTargetDropEvent.rejectDrop();
                this.cleanup(false);
                return;
            }
            this.support.setDNDVariables(this.component, dropTargetDropEvent);
            final boolean canImport = transferHandler.canImport(this.support);
            if (canImport) {
                dropTargetDropEvent.acceptDrop(this.support.getDropAction());
                this.setComponentDropLocation((this.support.showDropLocationIsSet ? this.support.showDropLocation : canImport) ? this.support : null, false);
                boolean importData;
                try {
                    importData = transferHandler.importData(this.support);
                }
                catch (final RuntimeException ex) {
                    importData = false;
                }
                dropTargetDropEvent.dropComplete(importData);
                this.cleanup(importData);
            }
            else {
                dropTargetDropEvent.rejectDrop();
                this.cleanup(false);
            }
        }
        
        @Override
        public void dropActionChanged(final DropTargetDragEvent dropTargetDragEvent) {
            if (this.component == null) {
                return;
            }
            this.handleDrag(dropTargetDragEvent);
        }
        
        private void cleanup(final boolean b) {
            this.setComponentDropLocation(null, b);
            if (this.component instanceof JComponent) {
                ((JComponent)this.component).dndDone();
            }
            if (this.timer != null) {
                this.timer.stop();
            }
            this.state = null;
            this.component = null;
            this.lastPosition = null;
        }
    }
    
    private static class DragHandler implements DragGestureListener, DragSourceListener
    {
        private boolean scrolls;
        
        @Override
        public void dragGestureRecognized(final DragGestureEvent dragGestureEvent) {
            final JComponent component = (JComponent)dragGestureEvent.getComponent();
            final TransferHandler transferHandler = component.getTransferHandler();
            final Transferable transferable = transferHandler.createTransferable(component);
            if (transferable != null) {
                this.scrolls = component.getAutoscrolls();
                component.setAutoscrolls(false);
                try {
                    final Image dragImage = transferHandler.getDragImage();
                    if (dragImage == null) {
                        dragGestureEvent.startDrag(null, transferable, this);
                    }
                    else {
                        dragGestureEvent.startDrag(null, dragImage, transferHandler.getDragImageOffset(), transferable, this);
                    }
                    return;
                }
                catch (final RuntimeException ex) {
                    component.setAutoscrolls(this.scrolls);
                }
            }
            transferHandler.exportDone(component, transferable, 0);
        }
        
        @Override
        public void dragEnter(final DragSourceDragEvent dragSourceDragEvent) {
        }
        
        @Override
        public void dragOver(final DragSourceDragEvent dragSourceDragEvent) {
        }
        
        @Override
        public void dragExit(final DragSourceEvent dragSourceEvent) {
        }
        
        @Override
        public void dragDropEnd(final DragSourceDropEvent dragSourceDropEvent) {
            final DragSourceContext dragSourceContext = dragSourceDropEvent.getDragSourceContext();
            final JComponent component = (JComponent)dragSourceContext.getComponent();
            if (dragSourceDropEvent.getDropSuccess()) {
                component.getTransferHandler().exportDone(component, dragSourceContext.getTransferable(), dragSourceDropEvent.getDropAction());
            }
            else {
                component.getTransferHandler().exportDone(component, dragSourceContext.getTransferable(), 0);
            }
            component.setAutoscrolls(this.scrolls);
        }
        
        @Override
        public void dropActionChanged(final DragSourceDragEvent dragSourceDragEvent) {
        }
    }
    
    private static class SwingDragGestureRecognizer extends DragGestureRecognizer
    {
        SwingDragGestureRecognizer(final DragGestureListener dragGestureListener) {
            super(DragSource.getDefaultDragSource(), null, 0, dragGestureListener);
        }
        
        void gestured(final JComponent component, final MouseEvent mouseEvent, final int sourceActions, final int n) {
            this.setComponent(component);
            this.setSourceActions(sourceActions);
            this.appendEvent(mouseEvent);
            this.fireDragGestureRecognized(n, mouseEvent.getPoint());
        }
        
        @Override
        protected void registerListeners() {
        }
        
        @Override
        protected void unregisterListeners() {
        }
    }
    
    static class TransferAction extends UIAction implements UIResource
    {
        private static final JavaSecurityAccess javaSecurityAccess;
        private static Object SandboxClipboardKey;
        
        TransferAction(final String s) {
            super(s);
        }
        
        @Override
        public boolean isEnabled(final Object o) {
            return !(o instanceof JComponent) || ((JComponent)o).getTransferHandler() != null;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Object source = actionEvent.getSource();
            final PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    TransferAction.this.actionPerformedImpl(actionEvent);
                    return null;
                }
            };
            final AccessControlContext context = AccessController.getContext();
            final AccessControlContext accessControlContext = AWTAccessor.getComponentAccessor().getAccessControlContext((Component)source);
            final AccessControlContext accessControlContext2 = AWTAccessor.getAWTEventAccessor().getAccessControlContext(actionEvent);
            if (accessControlContext == null) {
                TransferAction.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)privilegedAction, context, accessControlContext2);
            }
            else {
                TransferAction.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        TransferAction.javaSecurityAccess.doIntersectionPrivilege((PrivilegedAction<Object>)privilegedAction, accessControlContext2);
                        return null;
                    }
                }, context, accessControlContext);
            }
        }
        
        private void actionPerformedImpl(final ActionEvent actionEvent) {
            final Object source = actionEvent.getSource();
            if (source instanceof JComponent) {
                final JComponent component = (JComponent)source;
                final TransferHandler transferHandler = component.getTransferHandler();
                final Clipboard clipboard = this.getClipboard(component);
                final String s = (String)this.getValue("Name");
                Transferable contents = null;
                try {
                    if (clipboard != null && transferHandler != null && s != null) {
                        if ("cut".equals(s)) {
                            transferHandler.exportToClipboard(component, clipboard, 2);
                        }
                        else if ("copy".equals(s)) {
                            transferHandler.exportToClipboard(component, clipboard, 1);
                        }
                        else if ("paste".equals(s)) {
                            contents = clipboard.getContents(null);
                        }
                    }
                }
                catch (final IllegalStateException ex) {
                    UIManager.getLookAndFeel().provideErrorFeedback(component);
                    return;
                }
                if (contents != null) {
                    transferHandler.importData(new TransferSupport(component, contents));
                }
            }
        }
        
        private Clipboard getClipboard(final JComponent component) {
            if (SwingUtilities2.canAccessSystemClipboard()) {
                return component.getToolkit().getSystemClipboard();
            }
            Clipboard clipboard = (Clipboard)AppContext.getAppContext().get(TransferAction.SandboxClipboardKey);
            if (clipboard == null) {
                clipboard = new Clipboard("Sandboxed Component Clipboard");
                AppContext.getAppContext().put(TransferAction.SandboxClipboardKey, clipboard);
            }
            return clipboard;
        }
        
        static {
            javaSecurityAccess = SharedSecrets.getJavaSecurityAccess();
            TransferAction.SandboxClipboardKey = new Object();
        }
    }
    
    interface HasGetTransferHandler
    {
        TransferHandler getTransferHandler();
    }
}
