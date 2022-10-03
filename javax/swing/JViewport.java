package javax.swing;

import javax.accessibility.AccessibleRole;
import java.awt.event.ComponentEvent;
import java.io.Serializable;
import java.awt.event.ComponentAdapter;
import javax.accessibility.AccessibleContext;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ComponentPeer;
import javax.swing.event.ChangeListener;
import java.awt.LayoutManager;
import java.beans.Transient;
import java.awt.image.ImageObserver;
import java.awt.Shape;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.Border;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ViewportUI;
import javax.swing.event.ChangeEvent;
import java.awt.event.ComponentListener;
import java.awt.Image;
import java.awt.Point;
import javax.accessibility.Accessible;

public class JViewport extends JComponent implements Accessible
{
    private static final String uiClassID = "ViewportUI";
    static final Object EnableWindowBlit;
    protected boolean isViewSizeSet;
    protected Point lastPaintPosition;
    @Deprecated
    protected boolean backingStore;
    protected transient Image backingStoreImage;
    protected boolean scrollUnderway;
    private ComponentListener viewListener;
    private transient ChangeEvent changeEvent;
    public static final int BLIT_SCROLL_MODE = 1;
    public static final int BACKINGSTORE_SCROLL_MODE = 2;
    public static final int SIMPLE_SCROLL_MODE = 0;
    private int scrollMode;
    private transient boolean repaintAll;
    private transient boolean waitingForRepaint;
    private transient Timer repaintTimer;
    private transient boolean inBlitPaint;
    private boolean hasHadValidView;
    private boolean viewChanged;
    
    public JViewport() {
        this.isViewSizeSet = false;
        this.lastPaintPosition = null;
        this.backingStore = false;
        this.backingStoreImage = null;
        this.scrollUnderway = false;
        this.viewListener = null;
        this.changeEvent = null;
        this.scrollMode = 1;
        this.setLayout(this.createLayoutManager());
        this.setOpaque(true);
        this.updateUI();
        this.setInheritsPopupMenu(true);
    }
    
    public ViewportUI getUI() {
        return (ViewportUI)this.ui;
    }
    
    public void setUI(final ViewportUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ViewportUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ViewportUI";
    }
    
    @Override
    protected void addImpl(final Component view, final Object o, final int n) {
        this.setView(view);
    }
    
    @Override
    public void remove(final Component component) {
        component.removeComponentListener(this.viewListener);
        super.remove(component);
    }
    
    @Override
    public void scrollRectToVisible(final Rectangle rectangle) {
        final Component view = this.getView();
        if (view == null) {
            return;
        }
        if (!view.isValid()) {
            this.validateView();
        }
        final int positionAdjustment = this.positionAdjustment(this.getWidth(), rectangle.width, rectangle.x);
        final int positionAdjustment2 = this.positionAdjustment(this.getHeight(), rectangle.height, rectangle.y);
        if (positionAdjustment != 0 || positionAdjustment2 != 0) {
            final Point viewPosition = this.getViewPosition();
            final Dimension size = view.getSize();
            final int x = viewPosition.x;
            final int y = viewPosition.y;
            final Dimension extentSize = this.getExtentSize();
            final Point point = viewPosition;
            point.x -= positionAdjustment;
            final Point point2 = viewPosition;
            point2.y -= positionAdjustment2;
            if (view.isValid()) {
                if (this.getParent().getComponentOrientation().isLeftToRight()) {
                    if (viewPosition.x + extentSize.width > size.width) {
                        viewPosition.x = Math.max(0, size.width - extentSize.width);
                    }
                    else if (viewPosition.x < 0) {
                        viewPosition.x = 0;
                    }
                }
                else if (extentSize.width > size.width) {
                    viewPosition.x = size.width - extentSize.width;
                }
                else {
                    viewPosition.x = Math.max(0, Math.min(size.width - extentSize.width, viewPosition.x));
                }
                if (viewPosition.y + extentSize.height > size.height) {
                    viewPosition.y = Math.max(0, size.height - extentSize.height);
                }
                else if (viewPosition.y < 0) {
                    viewPosition.y = 0;
                }
            }
            if (viewPosition.x != x || viewPosition.y != y) {
                this.setViewPosition(viewPosition);
                this.scrollUnderway = false;
            }
        }
    }
    
    private void validateView() {
        final Container validateRoot = SwingUtilities.getValidateRoot(this, false);
        if (validateRoot == null) {
            return;
        }
        validateRoot.validate();
        final RepaintManager currentManager = RepaintManager.currentManager(this);
        if (currentManager != null) {
            currentManager.removeInvalidComponent((JComponent)validateRoot);
        }
    }
    
    private int positionAdjustment(final int n, final int n2, final int n3) {
        if (n3 >= 0 && n2 + n3 <= n) {
            return 0;
        }
        if (n3 <= 0 && n2 + n3 >= n) {
            return 0;
        }
        if (n3 > 0 && n2 <= n) {
            return -n3 + n - n2;
        }
        if (n3 >= 0 && n2 >= n) {
            return -n3;
        }
        if (n3 <= 0 && n2 <= n) {
            return -n3;
        }
        if (n3 < 0 && n2 >= n) {
            return -n3 + n - n2;
        }
        return 0;
    }
    
    @Override
    public final void setBorder(final Border border) {
        if (border != null) {
            throw new IllegalArgumentException("JViewport.setBorder() not supported");
        }
    }
    
    @Override
    public final Insets getInsets() {
        return new Insets(0, 0, 0, 0);
    }
    
    @Override
    public final Insets getInsets(final Insets insets) {
        final int n = 0;
        insets.bottom = n;
        insets.right = n;
        insets.top = n;
        insets.left = n;
        return insets;
    }
    
    private Graphics getBackingStoreGraphics(final Graphics graphics) {
        final Graphics graphics2 = this.backingStoreImage.getGraphics();
        graphics2.setColor(graphics.getColor());
        graphics2.setFont(graphics.getFont());
        graphics2.setClip(graphics.getClipBounds());
        return graphics2;
    }
    
    private void paintViaBackingStore(final Graphics graphics) {
        final Graphics backingStoreGraphics = this.getBackingStoreGraphics(graphics);
        try {
            super.paint(backingStoreGraphics);
            graphics.drawImage(this.backingStoreImage, 0, 0, this);
        }
        finally {
            backingStoreGraphics.dispose();
        }
    }
    
    private void paintViaBackingStore(final Graphics graphics, final Rectangle clip) {
        final Graphics backingStoreGraphics = this.getBackingStoreGraphics(graphics);
        try {
            super.paint(backingStoreGraphics);
            graphics.setClip(clip);
            graphics.drawImage(this.backingStoreImage, 0, 0, this);
        }
        finally {
            backingStoreGraphics.dispose();
        }
    }
    
    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }
    
    @Override
    protected boolean isPaintingOrigin() {
        return this.scrollMode == 2;
    }
    
    private Point getViewLocation() {
        final Component view = this.getView();
        if (view != null) {
            return view.getLocation();
        }
        return new Point(0, 0);
    }
    
    @Override
    public void paint(final Graphics graphics) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        if (this.inBlitPaint) {
            super.paint(graphics);
            return;
        }
        if (this.repaintAll) {
            this.repaintAll = false;
            final Rectangle clipBounds = graphics.getClipBounds();
            if (clipBounds.width < this.getWidth() || clipBounds.height < this.getHeight()) {
                this.waitingForRepaint = true;
                if (this.repaintTimer == null) {
                    this.repaintTimer = this.createRepaintTimer();
                }
                this.repaintTimer.stop();
                this.repaintTimer.start();
            }
            else {
                if (this.repaintTimer != null) {
                    this.repaintTimer.stop();
                }
                this.waitingForRepaint = false;
            }
        }
        else if (this.waitingForRepaint) {
            final Rectangle clipBounds2 = graphics.getClipBounds();
            if (clipBounds2.width >= this.getWidth() && clipBounds2.height >= this.getHeight()) {
                this.waitingForRepaint = false;
                this.repaintTimer.stop();
            }
        }
        if (!this.backingStore || this.isBlitting() || this.getView() == null) {
            super.paint(graphics);
            this.lastPaintPosition = this.getViewLocation();
            return;
        }
        final Rectangle bounds = this.getView().getBounds();
        if (!this.isOpaque()) {
            graphics.clipRect(0, 0, bounds.width, bounds.height);
        }
        if (this.backingStoreImage == null) {
            this.backingStoreImage = this.createImage(width, height);
            final Rectangle clipBounds3 = graphics.getClipBounds();
            if (clipBounds3.width != width || clipBounds3.height != height) {
                if (!this.isOpaque()) {
                    graphics.setClip(0, 0, Math.min(bounds.width, width), Math.min(bounds.height, height));
                }
                else {
                    graphics.setClip(0, 0, width, height);
                }
                this.paintViaBackingStore(graphics, clipBounds3);
            }
            else {
                this.paintViaBackingStore(graphics);
            }
        }
        else if (!this.scrollUnderway || this.lastPaintPosition.equals(this.getViewLocation())) {
            this.paintViaBackingStore(graphics);
        }
        else {
            final Point point = new Point();
            final Point point2 = new Point();
            final Dimension dimension = new Dimension();
            final Rectangle rectangle = new Rectangle();
            final Point viewLocation = this.getViewLocation();
            if (!this.computeBlit(viewLocation.x - this.lastPaintPosition.x, viewLocation.y - this.lastPaintPosition.y, point, point2, dimension, rectangle)) {
                this.paintViaBackingStore(graphics);
            }
            else {
                final int n = point2.x - point.x;
                final int n2 = point2.y - point.y;
                final Rectangle clipBounds4 = graphics.getClipBounds();
                graphics.setClip(0, 0, width, height);
                final Graphics backingStoreGraphics = this.getBackingStoreGraphics(graphics);
                try {
                    backingStoreGraphics.copyArea(point.x, point.y, dimension.width, dimension.height, n, n2);
                    graphics.setClip(clipBounds4.x, clipBounds4.y, clipBounds4.width, clipBounds4.height);
                    backingStoreGraphics.setClip(bounds.intersection(rectangle));
                    super.paint(backingStoreGraphics);
                    graphics.drawImage(this.backingStoreImage, 0, 0, this);
                }
                finally {
                    backingStoreGraphics.dispose();
                }
            }
        }
        this.lastPaintPosition = this.getViewLocation();
        this.scrollUnderway = false;
    }
    
    @Override
    public void reshape(final int n, final int n2, final int n3, final int n4) {
        final boolean b = this.getWidth() != n3 || this.getHeight() != n4;
        if (b) {
            this.backingStoreImage = null;
        }
        super.reshape(n, n2, n3, n4);
        if (b || this.viewChanged) {
            this.viewChanged = false;
            this.fireStateChanged();
        }
    }
    
    public void setScrollMode(final int scrollMode) {
        this.scrollMode = scrollMode;
        this.backingStore = (scrollMode == 2);
    }
    
    public int getScrollMode() {
        return this.scrollMode;
    }
    
    @Deprecated
    public boolean isBackingStoreEnabled() {
        return this.scrollMode == 2;
    }
    
    @Deprecated
    public void setBackingStoreEnabled(final boolean b) {
        if (b) {
            this.setScrollMode(2);
        }
        else {
            this.setScrollMode(1);
        }
    }
    
    private boolean isBlitting() {
        final Component view = this.getView();
        return this.scrollMode == 1 && view instanceof JComponent && view.isOpaque();
    }
    
    public Component getView() {
        return (this.getComponentCount() > 0) ? this.getComponent(0) : null;
    }
    
    public void setView(final Component component) {
        for (int i = this.getComponentCount() - 1; i >= 0; --i) {
            this.remove(this.getComponent(i));
        }
        this.isViewSizeSet = false;
        if (component != null) {
            super.addImpl(component, null, -1);
            component.addComponentListener(this.viewListener = this.createViewListener());
        }
        if (this.hasHadValidView) {
            this.fireStateChanged();
        }
        else if (component != null) {
            this.hasHadValidView = true;
        }
        this.viewChanged = true;
        this.revalidate();
        this.repaint();
    }
    
    public Dimension getViewSize() {
        final Component view = this.getView();
        if (view == null) {
            return new Dimension(0, 0);
        }
        if (this.isViewSizeSet) {
            return view.getSize();
        }
        return view.getPreferredSize();
    }
    
    public void setViewSize(final Dimension size) {
        final Component view = this.getView();
        if (view != null && !size.equals(view.getSize())) {
            this.scrollUnderway = false;
            view.setSize(size);
            this.isViewSizeSet = true;
            this.fireStateChanged();
        }
    }
    
    public Point getViewPosition() {
        final Component view = this.getView();
        if (view != null) {
            final Point location = view.getLocation();
            location.x = -location.x;
            location.y = -location.y;
            return location;
        }
        return new Point(0, 0);
    }
    
    public void setViewPosition(final Point point) {
        final Component view = this.getView();
        if (view == null) {
            return;
        }
        final int x = point.x;
        final int y = point.y;
        int n;
        int n2;
        if (view instanceof JComponent) {
            final JComponent component = (JComponent)view;
            n = component.getX();
            n2 = component.getY();
        }
        else {
            final Rectangle bounds = view.getBounds();
            n = bounds.x;
            n2 = bounds.y;
        }
        final int n3 = -x;
        final int n4 = -y;
        if (n != n3 || n2 != n4) {
            if (!this.waitingForRepaint && this.isBlitting() && this.canUseWindowBlitter()) {
                final RepaintManager currentManager = RepaintManager.currentManager(this);
                final JComponent component2 = (JComponent)view;
                final Rectangle dirtyRegion = currentManager.getDirtyRegion(component2);
                if (dirtyRegion == null || !dirtyRegion.contains(component2.getVisibleRect())) {
                    currentManager.beginPaint();
                    try {
                        final Graphics safelyGetGraphics = JComponent.safelyGetGraphics(this);
                        this.flushViewDirtyRegion(safelyGetGraphics, dirtyRegion);
                        view.setLocation(n3, n4);
                        final Rectangle clip = new Rectangle(0, 0, this.getWidth(), Math.min(this.getHeight(), component2.getHeight()));
                        safelyGetGraphics.setClip(clip);
                        this.repaintAll = (this.windowBlitPaint(safelyGetGraphics) && this.needsRepaintAfterBlit());
                        safelyGetGraphics.dispose();
                        currentManager.notifyRepaintPerformed(this, clip.x, clip.y, clip.width, clip.height);
                        currentManager.markCompletelyClean((JComponent)this.getParent());
                        currentManager.markCompletelyClean(this);
                        currentManager.markCompletelyClean(component2);
                    }
                    finally {
                        currentManager.endPaint();
                    }
                }
                else {
                    view.setLocation(n3, n4);
                    this.repaintAll = false;
                }
            }
            else {
                this.scrollUnderway = true;
                view.setLocation(n3, n4);
                this.repaintAll = false;
            }
            this.revalidate();
            this.fireStateChanged();
        }
    }
    
    public Rectangle getViewRect() {
        return new Rectangle(this.getViewPosition(), this.getExtentSize());
    }
    
    protected boolean computeBlit(final int x, final int y, final Point point, final Point point2, final Dimension dimension, final Rectangle rectangle) {
        final int abs = Math.abs(x);
        final int abs2 = Math.abs(y);
        final Dimension extentSize = this.getExtentSize();
        if (x == 0 && y != 0 && abs2 < extentSize.height) {
            if (y < 0) {
                point.y = -y;
                point2.y = 0;
                rectangle.y = extentSize.height + y;
            }
            else {
                point.y = 0;
                point2.y = y;
                rectangle.y = 0;
            }
            final int x2 = 0;
            point2.x = x2;
            point.x = x2;
            rectangle.x = x2;
            dimension.width = extentSize.width;
            dimension.height = extentSize.height - abs2;
            rectangle.width = extentSize.width;
            rectangle.height = abs2;
            return true;
        }
        if (y == 0 && x != 0 && abs < extentSize.width) {
            if (x < 0) {
                point.x = -x;
                point2.x = 0;
                rectangle.x = extentSize.width + x;
            }
            else {
                point.x = 0;
                point2.x = x;
                rectangle.x = 0;
            }
            final int y2 = 0;
            point2.y = y2;
            point.y = y2;
            rectangle.y = y2;
            dimension.width = extentSize.width - abs;
            dimension.height = extentSize.height;
            rectangle.width = abs;
            rectangle.height = extentSize.height;
            return true;
        }
        return false;
    }
    
    @Transient
    public Dimension getExtentSize() {
        return this.getSize();
    }
    
    public Dimension toViewCoordinates(final Dimension dimension) {
        return new Dimension(dimension);
    }
    
    public Point toViewCoordinates(final Point point) {
        return new Point(point);
    }
    
    public void setExtentSize(final Dimension size) {
        if (!size.equals(this.getExtentSize())) {
            this.setSize(size);
            this.fireStateChanged();
        }
    }
    
    protected ViewListener createViewListener() {
        return new ViewListener();
    }
    
    protected LayoutManager createLayoutManager() {
        return ViewportLayout.SHARED_INSTANCE;
    }
    
    public void addChangeListener(final ChangeListener changeListener) {
        this.listenerList.add(ChangeListener.class, changeListener);
    }
    
    public void removeChangeListener(final ChangeListener changeListener) {
        this.listenerList.remove(ChangeListener.class, changeListener);
    }
    
    public ChangeListener[] getChangeListeners() {
        return this.listenerList.getListeners(ChangeListener.class);
    }
    
    protected void fireStateChanged() {
        final Object[] listenerList = this.listenerList.getListenerList();
        for (int i = listenerList.length - 2; i >= 0; i -= 2) {
            if (listenerList[i] == ChangeListener.class) {
                if (this.changeEvent == null) {
                    this.changeEvent = new ChangeEvent(this);
                }
                ((ChangeListener)listenerList[i + 1]).stateChanged(this.changeEvent);
            }
        }
    }
    
    @Override
    public void repaint(final long n, final int n2, final int n3, final int n4, final int n5) {
        final Container parent = this.getParent();
        if (parent != null) {
            parent.repaint(n, n2 + this.getX(), n3 + this.getY(), n4, n5);
        }
        else {
            super.repaint(n, n2, n3, n4, n5);
        }
    }
    
    @Override
    protected String paramString() {
        return super.paramString() + ",isViewSizeSet=" + (this.isViewSizeSet ? "true" : "false") + ",lastPaintPosition=" + ((this.lastPaintPosition != null) ? this.lastPaintPosition.toString() : "") + ",scrollUnderway=" + (this.scrollUnderway ? "true" : "false");
    }
    
    @Override
    protected void firePropertyChange(final String s, final Object o, final Object o2) {
        super.firePropertyChange(s, o, o2);
        if (s.equals(JViewport.EnableWindowBlit)) {
            if (o2 != null) {
                this.setScrollMode(1);
            }
            else {
                this.setScrollMode(0);
            }
        }
    }
    
    private boolean needsRepaintAfterBlit() {
        Container container;
        for (container = this.getParent(); container != null && container.isLightweight(); container = container.getParent()) {}
        if (container != null) {
            final ComponentPeer peer = container.getPeer();
            if (peer != null && peer.canDetermineObscurity() && !peer.isObscured()) {
                return false;
            }
        }
        return true;
    }
    
    private Timer createRepaintTimer() {
        final Timer timer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent actionEvent) {
                if (JViewport.this.waitingForRepaint) {
                    JViewport.this.repaint();
                }
            }
        });
        timer.setRepeats(false);
        return timer;
    }
    
    private void flushViewDirtyRegion(final Graphics graphics, final Rectangle rectangle) {
        final JComponent component = (JComponent)this.getView();
        if (rectangle != null && rectangle.width > 0 && rectangle.height > 0) {
            rectangle.x += component.getX();
            rectangle.y += component.getY();
            if (graphics.getClipBounds() == null) {
                graphics.setClip(0, 0, this.getWidth(), this.getHeight());
            }
            graphics.clipRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            final Rectangle clipBounds = graphics.getClipBounds();
            if (clipBounds.width > 0 && clipBounds.height > 0) {
                this.paintView(graphics);
            }
        }
    }
    
    private boolean windowBlitPaint(final Graphics graphics) {
        final int width = this.getWidth();
        final int height = this.getHeight();
        if (width == 0 || height == 0) {
            return false;
        }
        RepaintManager.currentManager(this);
        final JComponent component = (JComponent)this.getView();
        boolean b;
        if (this.lastPaintPosition == null || this.lastPaintPosition.equals(this.getViewLocation())) {
            this.paintView(graphics);
            b = false;
        }
        else {
            final Point point = new Point();
            final Point point2 = new Point();
            final Dimension dimension = new Dimension();
            final Rectangle rectangle = new Rectangle();
            final Point viewLocation = this.getViewLocation();
            if (!this.computeBlit(viewLocation.x - this.lastPaintPosition.x, viewLocation.y - this.lastPaintPosition.y, point, point2, dimension, rectangle)) {
                this.paintView(graphics);
                b = false;
            }
            else {
                final Rectangle intersection;
                final Rectangle rectangle2 = intersection = component.getBounds().intersection(rectangle);
                intersection.x -= component.getX();
                final Rectangle rectangle3 = rectangle2;
                rectangle3.y -= component.getY();
                this.blitDoubleBuffered(component, graphics, rectangle2.x, rectangle2.y, rectangle2.width, rectangle2.height, point.x, point.y, point2.x, point2.y, dimension.width, dimension.height);
                b = true;
            }
        }
        this.lastPaintPosition = this.getViewLocation();
        return b;
    }
    
    private void blitDoubleBuffered(final JComponent component, final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10) {
        final RepaintManager currentManager = RepaintManager.currentManager(this);
        final int n11 = n7 - n5;
        final int n12 = n8 - n6;
        Composite composite = null;
        if (graphics instanceof Graphics2D) {
            final Graphics2D graphics2D = (Graphics2D)graphics;
            composite = graphics2D.getComposite();
            graphics2D.setComposite(AlphaComposite.Src);
        }
        currentManager.copyArea(this, graphics, n5, n6, n9, n10, n11, n12, false);
        if (composite != null) {
            ((Graphics2D)graphics).setComposite(composite);
        }
        final int x = component.getX();
        final int y = component.getY();
        graphics.translate(x, y);
        graphics.setClip(n, n2, n3, n4);
        component.paintForceDoubleBuffered(graphics);
        graphics.translate(-x, -y);
    }
    
    private void paintView(final Graphics graphics) {
        final Rectangle clipBounds = graphics.getClipBounds();
        final JComponent component = (JComponent)this.getView();
        if (component.getWidth() >= this.getWidth()) {
            final int x = component.getX();
            final int y = component.getY();
            graphics.translate(x, y);
            graphics.setClip(clipBounds.x - x, clipBounds.y - y, clipBounds.width, clipBounds.height);
            component.paintForceDoubleBuffered(graphics);
            graphics.translate(-x, -y);
            graphics.setClip(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
        }
        else {
            try {
                this.inBlitPaint = true;
                this.paintForceDoubleBuffered(graphics);
            }
            finally {
                this.inBlitPaint = false;
            }
        }
    }
    
    private boolean canUseWindowBlitter() {
        if (!this.isShowing() || (!(this.getParent() instanceof JComponent) && !(this.getView() instanceof JComponent))) {
            return false;
        }
        if (this.isPainting()) {
            return false;
        }
        final Rectangle dirtyRegion = RepaintManager.currentManager(this).getDirtyRegion((JComponent)this.getParent());
        if (dirtyRegion != null && dirtyRegion.width > 0 && dirtyRegion.height > 0) {
            return false;
        }
        final Rectangle bounds = new Rectangle(0, 0, this.getWidth(), this.getHeight());
        final Rectangle rectangle = new Rectangle();
        Rectangle bounds2 = null;
        Component component = null;
        Container parent;
        for (parent = this; parent != null && JComponent.isLightweightComponent(parent); parent = parent.getParent()) {
            final int x = parent.getX();
            final int y = parent.getY();
            final int width = parent.getWidth();
            final int height = parent.getHeight();
            rectangle.setBounds(bounds);
            SwingUtilities.computeIntersection(0, 0, width, height, bounds);
            if (!bounds.equals(rectangle)) {
                return false;
            }
            if (component != null && parent instanceof JComponent && !((JComponent)parent).isOptimizedDrawingEnabled()) {
                final Component[] components = parent.getComponents();
                int i = 0;
                for (int j = components.length - 1; j >= 0; --j) {
                    if (components[j] == component) {
                        i = j - 1;
                        break;
                    }
                }
                while (i >= 0) {
                    bounds2 = components[i].getBounds(bounds2);
                    if (bounds2.intersects(bounds)) {
                        return false;
                    }
                    --i;
                }
            }
            final Rectangle rectangle2 = bounds;
            rectangle2.x += x;
            final Rectangle rectangle3 = bounds;
            rectangle3.y += y;
            component = parent;
        }
        return parent != null;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJViewport();
        }
        return this.accessibleContext;
    }
    
    static {
        EnableWindowBlit = "EnableWindowBlit";
    }
    
    protected class ViewListener extends ComponentAdapter implements Serializable
    {
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            JViewport.this.fireStateChanged();
            JViewport.this.revalidate();
        }
    }
    
    protected class AccessibleJViewport extends AccessibleJComponent
    {
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.VIEWPORT;
        }
    }
}
