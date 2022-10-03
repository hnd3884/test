package javax.swing.plaf.basic;

import javax.swing.BoundedRangeModel;
import javax.swing.Scrollable;
import java.awt.event.ActionEvent;
import sun.swing.UIAction;
import java.awt.event.MouseWheelEvent;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.ActionMap;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.LookAndFeel;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Action;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.ScrollPaneUI;

public class BasicScrollPaneUI extends ScrollPaneUI implements ScrollPaneConstants
{
    protected JScrollPane scrollpane;
    protected ChangeListener vsbChangeListener;
    protected ChangeListener hsbChangeListener;
    protected ChangeListener viewportChangeListener;
    protected PropertyChangeListener spPropertyChangeListener;
    private MouseWheelListener mouseScrollListener;
    private int oldExtent;
    private PropertyChangeListener vsbPropertyChangeListener;
    private PropertyChangeListener hsbPropertyChangeListener;
    private Handler handler;
    private boolean setValueCalled;
    
    public BasicScrollPaneUI() {
        this.oldExtent = Integer.MIN_VALUE;
        this.setValueCalled = false;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicScrollPaneUI();
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("scrollUp"));
        lazyActionMap.put(new Actions("scrollDown"));
        lazyActionMap.put(new Actions("scrollHome"));
        lazyActionMap.put(new Actions("scrollEnd"));
        lazyActionMap.put(new Actions("unitScrollUp"));
        lazyActionMap.put(new Actions("unitScrollDown"));
        lazyActionMap.put(new Actions("scrollLeft"));
        lazyActionMap.put(new Actions("scrollRight"));
        lazyActionMap.put(new Actions("unitScrollRight"));
        lazyActionMap.put(new Actions("unitScrollLeft"));
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Border viewportBorder = this.scrollpane.getViewportBorder();
        if (viewportBorder != null) {
            final Rectangle viewportBorderBounds = this.scrollpane.getViewportBorderBounds();
            viewportBorder.paintBorder(this.scrollpane, graphics, viewportBorderBounds.x, viewportBorderBounds.y, viewportBorderBounds.width, viewportBorderBounds.height);
        }
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(32767, 32767);
    }
    
    protected void installDefaults(final JScrollPane scrollPane) {
        LookAndFeel.installBorder(scrollPane, "ScrollPane.border");
        LookAndFeel.installColorsAndFont(scrollPane, "ScrollPane.background", "ScrollPane.foreground", "ScrollPane.font");
        final Border viewportBorder = scrollPane.getViewportBorder();
        if (viewportBorder == null || viewportBorder instanceof UIResource) {
            scrollPane.setViewportBorder(UIManager.getBorder("ScrollPane.viewportBorder"));
        }
        LookAndFeel.installProperty(scrollPane, "opaque", Boolean.TRUE);
    }
    
    protected void installListeners(final JScrollPane scrollPane) {
        this.vsbChangeListener = this.createVSBChangeListener();
        this.vsbPropertyChangeListener = this.createVSBPropertyChangeListener();
        this.hsbChangeListener = this.createHSBChangeListener();
        this.hsbPropertyChangeListener = this.createHSBPropertyChangeListener();
        this.viewportChangeListener = this.createViewportChangeListener();
        this.spPropertyChangeListener = this.createPropertyChangeListener();
        final JViewport viewport = this.scrollpane.getViewport();
        final JScrollBar verticalScrollBar = this.scrollpane.getVerticalScrollBar();
        final JScrollBar horizontalScrollBar = this.scrollpane.getHorizontalScrollBar();
        if (viewport != null) {
            viewport.addChangeListener(this.viewportChangeListener);
        }
        if (verticalScrollBar != null) {
            verticalScrollBar.getModel().addChangeListener(this.vsbChangeListener);
            verticalScrollBar.addPropertyChangeListener(this.vsbPropertyChangeListener);
        }
        if (horizontalScrollBar != null) {
            horizontalScrollBar.getModel().addChangeListener(this.hsbChangeListener);
            horizontalScrollBar.addPropertyChangeListener(this.hsbPropertyChangeListener);
        }
        this.scrollpane.addPropertyChangeListener(this.spPropertyChangeListener);
        this.mouseScrollListener = this.createMouseWheelListener();
        this.scrollpane.addMouseWheelListener(this.mouseScrollListener);
    }
    
    protected void installKeyboardActions(final JScrollPane scrollPane) {
        SwingUtilities.replaceUIInputMap(scrollPane, 1, this.getInputMap(1));
        LazyActionMap.installLazyActionMap(scrollPane, BasicScrollPaneUI.class, "ScrollPane.actionMap");
    }
    
    InputMap getInputMap(final int n) {
        if (n != 1) {
            return null;
        }
        final InputMap parent = (InputMap)DefaultLookup.get(this.scrollpane, this, "ScrollPane.ancestorInputMap");
        final InputMap inputMap;
        if (this.scrollpane.getComponentOrientation().isLeftToRight() || (inputMap = (InputMap)DefaultLookup.get(this.scrollpane, this, "ScrollPane.ancestorInputMap.RightToLeft")) == null) {
            return parent;
        }
        inputMap.setParent(parent);
        return inputMap;
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.installDefaults(this.scrollpane = (JScrollPane)component);
        this.installListeners(this.scrollpane);
        this.installKeyboardActions(this.scrollpane);
    }
    
    protected void uninstallDefaults(final JScrollPane scrollPane) {
        LookAndFeel.uninstallBorder(this.scrollpane);
        if (this.scrollpane.getViewportBorder() instanceof UIResource) {
            this.scrollpane.setViewportBorder(null);
        }
    }
    
    protected void uninstallListeners(final JComponent component) {
        final JViewport viewport = this.scrollpane.getViewport();
        final JScrollBar verticalScrollBar = this.scrollpane.getVerticalScrollBar();
        final JScrollBar horizontalScrollBar = this.scrollpane.getHorizontalScrollBar();
        if (viewport != null) {
            viewport.removeChangeListener(this.viewportChangeListener);
        }
        if (verticalScrollBar != null) {
            verticalScrollBar.getModel().removeChangeListener(this.vsbChangeListener);
            verticalScrollBar.removePropertyChangeListener(this.vsbPropertyChangeListener);
        }
        if (horizontalScrollBar != null) {
            horizontalScrollBar.getModel().removeChangeListener(this.hsbChangeListener);
            horizontalScrollBar.removePropertyChangeListener(this.hsbPropertyChangeListener);
        }
        this.scrollpane.removePropertyChangeListener(this.spPropertyChangeListener);
        if (this.mouseScrollListener != null) {
            this.scrollpane.removeMouseWheelListener(this.mouseScrollListener);
        }
        this.vsbChangeListener = null;
        this.hsbChangeListener = null;
        this.viewportChangeListener = null;
        this.spPropertyChangeListener = null;
        this.mouseScrollListener = null;
        this.handler = null;
    }
    
    protected void uninstallKeyboardActions(final JScrollPane scrollPane) {
        SwingUtilities.replaceUIActionMap(scrollPane, null);
        SwingUtilities.replaceUIInputMap(scrollPane, 1, null);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.uninstallDefaults(this.scrollpane);
        this.uninstallListeners(this.scrollpane);
        this.uninstallKeyboardActions(this.scrollpane);
        this.scrollpane = null;
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected void syncScrollPaneWithViewport() {
        final JViewport viewport = this.scrollpane.getViewport();
        final JScrollBar verticalScrollBar = this.scrollpane.getVerticalScrollBar();
        final JScrollBar horizontalScrollBar = this.scrollpane.getHorizontalScrollBar();
        final JViewport rowHeader = this.scrollpane.getRowHeader();
        final JViewport columnHeader = this.scrollpane.getColumnHeader();
        final boolean leftToRight = this.scrollpane.getComponentOrientation().isLeftToRight();
        if (viewport != null) {
            final Dimension extentSize = viewport.getExtentSize();
            final Dimension viewSize = viewport.getViewSize();
            final Point viewPosition = viewport.getViewPosition();
            if (verticalScrollBar != null) {
                final int height = extentSize.height;
                final int height2 = viewSize.height;
                verticalScrollBar.setValues(Math.max(0, Math.min(viewPosition.y, height2 - height)), height, 0, height2);
            }
            if (horizontalScrollBar != null) {
                final int width = extentSize.width;
                final int width2 = viewSize.width;
                int n;
                if (leftToRight) {
                    n = Math.max(0, Math.min(viewPosition.x, width2 - width));
                }
                else {
                    final int value = horizontalScrollBar.getValue();
                    if (this.setValueCalled && width2 - value == viewPosition.x) {
                        n = Math.max(0, Math.min(width2 - width, value));
                        if (width != 0) {
                            this.setValueCalled = false;
                        }
                    }
                    else if (width > width2) {
                        viewPosition.x = width2 - width;
                        viewport.setViewPosition(viewPosition);
                        n = 0;
                    }
                    else {
                        n = Math.max(0, Math.min(width2 - width, width2 - width - viewPosition.x));
                        if (this.oldExtent > width) {
                            n -= this.oldExtent - width;
                        }
                    }
                }
                horizontalScrollBar.setValues(n, this.oldExtent = width, 0, width2);
            }
            if (rowHeader != null) {
                final Point viewPosition2 = rowHeader.getViewPosition();
                viewPosition2.y = viewport.getViewPosition().y;
                viewPosition2.x = 0;
                rowHeader.setViewPosition(viewPosition2);
            }
            if (columnHeader != null) {
                final Point viewPosition3 = columnHeader.getViewPosition();
                if (leftToRight) {
                    viewPosition3.x = viewport.getViewPosition().x;
                }
                else {
                    viewPosition3.x = Math.max(0, viewport.getViewPosition().x);
                }
                viewPosition3.y = 0;
                columnHeader.setViewPosition(viewPosition3);
            }
        }
    }
    
    @Override
    public int getBaseline(final JComponent component, int max, int max2) {
        if (component == null) {
            throw new NullPointerException("Component must be non-null");
        }
        if (max < 0 || max2 < 0) {
            throw new IllegalArgumentException("Width and height must be >= 0");
        }
        final JViewport viewport = this.scrollpane.getViewport();
        final Insets insets = this.scrollpane.getInsets();
        int top = insets.top;
        max2 = max2 - insets.top - insets.bottom;
        max = max - insets.left - insets.right;
        final JViewport columnHeader = this.scrollpane.getColumnHeader();
        if (columnHeader != null && columnHeader.isVisible()) {
            final Component view = columnHeader.getView();
            if (view != null && view.isVisible()) {
                final Dimension preferredSize = view.getPreferredSize();
                final int baseline = view.getBaseline(preferredSize.width, preferredSize.height);
                if (baseline >= 0) {
                    return top + baseline;
                }
            }
            final Dimension preferredSize2 = columnHeader.getPreferredSize();
            max2 -= preferredSize2.height;
            top += preferredSize2.height;
        }
        final Component component2 = (viewport == null) ? null : viewport.getView();
        if (component2 != null && component2.isVisible() && component2.getBaselineResizeBehavior() == Component.BaselineResizeBehavior.CONSTANT_ASCENT) {
            final Border viewportBorder = this.scrollpane.getViewportBorder();
            if (viewportBorder != null) {
                final Insets borderInsets = viewportBorder.getBorderInsets(this.scrollpane);
                top += borderInsets.top;
                max2 = max2 - borderInsets.top - borderInsets.bottom;
                max = max - borderInsets.left - borderInsets.right;
            }
            if (component2.getWidth() > 0 && component2.getHeight() > 0) {
                final Dimension minimumSize = component2.getMinimumSize();
                max = Math.max(minimumSize.width, component2.getWidth());
                max2 = Math.max(minimumSize.height, component2.getHeight());
            }
            if (max > 0 && max2 > 0) {
                final int baseline2 = component2.getBaseline(max, max2);
                if (baseline2 > 0) {
                    return top + baseline2;
                }
            }
        }
        return -1;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return Component.BaselineResizeBehavior.CONSTANT_ASCENT;
    }
    
    protected ChangeListener createViewportChangeListener() {
        return this.getHandler();
    }
    
    private PropertyChangeListener createHSBPropertyChangeListener() {
        return this.getHandler();
    }
    
    protected ChangeListener createHSBChangeListener() {
        return this.getHandler();
    }
    
    private PropertyChangeListener createVSBPropertyChangeListener() {
        return this.getHandler();
    }
    
    protected ChangeListener createVSBChangeListener() {
        return this.getHandler();
    }
    
    protected MouseWheelListener createMouseWheelListener() {
        return this.getHandler();
    }
    
    protected void updateScrollBarDisplayPolicy(final PropertyChangeEvent propertyChangeEvent) {
        this.scrollpane.revalidate();
        this.scrollpane.repaint();
    }
    
    protected void updateViewport(final PropertyChangeEvent propertyChangeEvent) {
        final JViewport viewport = (JViewport)propertyChangeEvent.getOldValue();
        final JViewport viewport2 = (JViewport)propertyChangeEvent.getNewValue();
        if (viewport != null) {
            viewport.removeChangeListener(this.viewportChangeListener);
        }
        if (viewport2 != null) {
            final Point viewPosition = viewport2.getViewPosition();
            if (this.scrollpane.getComponentOrientation().isLeftToRight()) {
                viewPosition.x = Math.max(viewPosition.x, 0);
            }
            else {
                final int width = viewport2.getViewSize().width;
                final int width2 = viewport2.getExtentSize().width;
                if (width2 > width) {
                    viewPosition.x = width - width2;
                }
                else {
                    viewPosition.x = Math.max(0, Math.min(width - width2, viewPosition.x));
                }
            }
            viewPosition.y = Math.max(viewPosition.y, 0);
            viewport2.setViewPosition(viewPosition);
            viewport2.addChangeListener(this.viewportChangeListener);
        }
    }
    
    protected void updateRowHeader(final PropertyChangeEvent propertyChangeEvent) {
        final JViewport viewport = (JViewport)propertyChangeEvent.getNewValue();
        if (viewport != null) {
            final JViewport viewport2 = this.scrollpane.getViewport();
            final Point viewPosition = viewport.getViewPosition();
            viewPosition.y = ((viewport2 != null) ? viewport2.getViewPosition().y : 0);
            viewport.setViewPosition(viewPosition);
        }
    }
    
    protected void updateColumnHeader(final PropertyChangeEvent propertyChangeEvent) {
        final JViewport viewport = (JViewport)propertyChangeEvent.getNewValue();
        if (viewport != null) {
            final JViewport viewport2 = this.scrollpane.getViewport();
            final Point viewPosition = viewport.getViewPosition();
            if (viewport2 == null) {
                viewPosition.x = 0;
            }
            else if (this.scrollpane.getComponentOrientation().isLeftToRight()) {
                viewPosition.x = viewport2.getViewPosition().x;
            }
            else {
                viewPosition.x = Math.max(0, viewport2.getViewPosition().x);
            }
            viewport.setViewPosition(viewPosition);
            this.scrollpane.add(viewport, "COLUMN_HEADER");
        }
    }
    
    private void updateHorizontalScrollBar(final PropertyChangeEvent propertyChangeEvent) {
        this.updateScrollBar(propertyChangeEvent, this.hsbChangeListener, this.hsbPropertyChangeListener);
    }
    
    private void updateVerticalScrollBar(final PropertyChangeEvent propertyChangeEvent) {
        this.updateScrollBar(propertyChangeEvent, this.vsbChangeListener, this.vsbPropertyChangeListener);
    }
    
    private void updateScrollBar(final PropertyChangeEvent propertyChangeEvent, final ChangeListener changeListener, final PropertyChangeListener propertyChangeListener) {
        final JScrollBar scrollBar = (JScrollBar)propertyChangeEvent.getOldValue();
        if (scrollBar != null) {
            if (changeListener != null) {
                scrollBar.getModel().removeChangeListener(changeListener);
            }
            if (propertyChangeListener != null) {
                scrollBar.removePropertyChangeListener(propertyChangeListener);
            }
        }
        final JScrollBar scrollBar2 = (JScrollBar)propertyChangeEvent.getNewValue();
        if (scrollBar2 != null) {
            if (changeListener != null) {
                scrollBar2.getModel().addChangeListener(changeListener);
            }
            if (propertyChangeListener != null) {
                scrollBar2.addPropertyChangeListener(propertyChangeListener);
            }
        }
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    public class ViewportChangeHandler implements ChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            BasicScrollPaneUI.this.getHandler().stateChanged(changeEvent);
        }
    }
    
    public class HSBChangeListener implements ChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            BasicScrollPaneUI.this.getHandler().stateChanged(changeEvent);
        }
    }
    
    public class VSBChangeListener implements ChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            BasicScrollPaneUI.this.getHandler().stateChanged(changeEvent);
        }
    }
    
    protected class MouseWheelHandler implements MouseWheelListener
    {
        @Override
        public void mouseWheelMoved(final MouseWheelEvent mouseWheelEvent) {
            BasicScrollPaneUI.this.getHandler().mouseWheelMoved(mouseWheelEvent);
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicScrollPaneUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String SCROLL_UP = "scrollUp";
        private static final String SCROLL_DOWN = "scrollDown";
        private static final String SCROLL_HOME = "scrollHome";
        private static final String SCROLL_END = "scrollEnd";
        private static final String UNIT_SCROLL_UP = "unitScrollUp";
        private static final String UNIT_SCROLL_DOWN = "unitScrollDown";
        private static final String SCROLL_LEFT = "scrollLeft";
        private static final String SCROLL_RIGHT = "scrollRight";
        private static final String UNIT_SCROLL_LEFT = "unitScrollLeft";
        private static final String UNIT_SCROLL_RIGHT = "unitScrollRight";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JScrollPane scrollPane = (JScrollPane)actionEvent.getSource();
            final boolean leftToRight = scrollPane.getComponentOrientation().isLeftToRight();
            final String name = this.getName();
            if (name == "scrollUp") {
                this.scroll(scrollPane, 1, -1, true);
            }
            else if (name == "scrollDown") {
                this.scroll(scrollPane, 1, 1, true);
            }
            else if (name == "scrollHome") {
                this.scrollHome(scrollPane);
            }
            else if (name == "scrollEnd") {
                this.scrollEnd(scrollPane);
            }
            else if (name == "unitScrollUp") {
                this.scroll(scrollPane, 1, -1, false);
            }
            else if (name == "unitScrollDown") {
                this.scroll(scrollPane, 1, 1, false);
            }
            else if (name == "scrollLeft") {
                this.scroll(scrollPane, 0, leftToRight ? -1 : 1, true);
            }
            else if (name == "scrollRight") {
                this.scroll(scrollPane, 0, leftToRight ? 1 : -1, true);
            }
            else if (name == "unitScrollLeft") {
                this.scroll(scrollPane, 0, leftToRight ? -1 : 1, false);
            }
            else if (name == "unitScrollRight") {
                this.scroll(scrollPane, 0, leftToRight ? 1 : -1, false);
            }
        }
        
        private void scrollEnd(final JScrollPane scrollPane) {
            final JViewport viewport = scrollPane.getViewport();
            final Component view;
            if (viewport != null && (view = viewport.getView()) != null) {
                final Rectangle viewRect = viewport.getViewRect();
                final Rectangle bounds = view.getBounds();
                if (scrollPane.getComponentOrientation().isLeftToRight()) {
                    viewport.setViewPosition(new Point(bounds.width - viewRect.width, bounds.height - viewRect.height));
                }
                else {
                    viewport.setViewPosition(new Point(0, bounds.height - viewRect.height));
                }
            }
        }
        
        private void scrollHome(final JScrollPane scrollPane) {
            final JViewport viewport = scrollPane.getViewport();
            final Component view;
            if (viewport != null && (view = viewport.getView()) != null) {
                if (scrollPane.getComponentOrientation().isLeftToRight()) {
                    viewport.setViewPosition(new Point(0, 0));
                }
                else {
                    viewport.setViewPosition(new Point(view.getBounds().width - viewport.getViewRect().width, 0));
                }
            }
        }
        
        private void scroll(final JScrollPane scrollPane, final int n, final int n2, final boolean b) {
            final JViewport viewport = scrollPane.getViewport();
            final Component view;
            if (viewport != null && (view = viewport.getView()) != null) {
                final Rectangle viewRect = viewport.getViewRect();
                final Dimension size = view.getSize();
                int n3;
                if (view instanceof Scrollable) {
                    if (b) {
                        n3 = ((Scrollable)view).getScrollableBlockIncrement(viewRect, n, n2);
                    }
                    else {
                        n3 = ((Scrollable)view).getScrollableUnitIncrement(viewRect, n, n2);
                    }
                }
                else if (b) {
                    if (n == 1) {
                        n3 = viewRect.height;
                    }
                    else {
                        n3 = viewRect.width;
                    }
                }
                else {
                    n3 = 10;
                }
                if (n == 1) {
                    final Rectangle rectangle = viewRect;
                    rectangle.y += n3 * n2;
                    if (viewRect.y + viewRect.height > size.height) {
                        viewRect.y = Math.max(0, size.height - viewRect.height);
                    }
                    else if (viewRect.y < 0) {
                        viewRect.y = 0;
                    }
                }
                else if (scrollPane.getComponentOrientation().isLeftToRight()) {
                    final Rectangle rectangle2 = viewRect;
                    rectangle2.x += n3 * n2;
                    if (viewRect.x + viewRect.width > size.width) {
                        viewRect.x = Math.max(0, size.width - viewRect.width);
                    }
                    else if (viewRect.x < 0) {
                        viewRect.x = 0;
                    }
                }
                else {
                    final Rectangle rectangle3 = viewRect;
                    rectangle3.x -= n3 * n2;
                    if (viewRect.width > size.width) {
                        viewRect.x = size.width - viewRect.width;
                    }
                    else {
                        viewRect.x = Math.max(0, Math.min(size.width - viewRect.width, viewRect.x));
                    }
                }
                viewport.setViewPosition(viewRect.getLocation());
            }
        }
    }
    
    class Handler implements ChangeListener, PropertyChangeListener, MouseWheelListener
    {
        @Override
        public void mouseWheelMoved(final MouseWheelEvent mouseWheelEvent) {
            if (BasicScrollPaneUI.this.scrollpane.isWheelScrollingEnabled() && mouseWheelEvent.getWheelRotation() != 0) {
                JScrollBar scrollBar = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
                final int n = (mouseWheelEvent.getWheelRotation() < 0) ? -1 : 1;
                int n2 = 1;
                if (scrollBar == null || !scrollBar.isVisible() || mouseWheelEvent.isShiftDown()) {
                    scrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
                    if (scrollBar == null || !scrollBar.isVisible()) {
                        return;
                    }
                    n2 = 0;
                }
                mouseWheelEvent.consume();
                if (mouseWheelEvent.getScrollType() == 0) {
                    final JViewport viewport = BasicScrollPaneUI.this.scrollpane.getViewport();
                    if (viewport == null) {
                        return;
                    }
                    final Component view = viewport.getView();
                    final int abs = Math.abs(mouseWheelEvent.getUnitsToScroll());
                    final boolean b = Math.abs(mouseWheelEvent.getWheelRotation()) == 1;
                    if (Boolean.TRUE == scrollBar.getClientProperty("JScrollBar.fastWheelScrolling") && view instanceof Scrollable) {
                        final Scrollable scrollable = (Scrollable)view;
                        final Rectangle viewRect = viewport.getViewRect();
                        final int x = viewRect.x;
                        final boolean leftToRight = view.getComponentOrientation().isLeftToRight();
                        int n3 = scrollBar.getMinimum();
                        int min = scrollBar.getMaximum() - scrollBar.getModel().getExtent();
                        if (b) {
                            final int scrollableBlockIncrement = scrollable.getScrollableBlockIncrement(viewRect, n2, n);
                            if (n < 0) {
                                n3 = Math.max(n3, scrollBar.getValue() - scrollableBlockIncrement);
                            }
                            else {
                                min = Math.min(min, scrollBar.getValue() + scrollableBlockIncrement);
                            }
                        }
                        for (int i = 0; i < abs; ++i) {
                            final int scrollableUnitIncrement = scrollable.getScrollableUnitIncrement(viewRect, n2, n);
                            if (n2 == 1) {
                                if (n < 0) {
                                    final Rectangle rectangle = viewRect;
                                    rectangle.y -= scrollableUnitIncrement;
                                    if (viewRect.y <= n3) {
                                        viewRect.y = n3;
                                        break;
                                    }
                                }
                                else {
                                    final Rectangle rectangle2 = viewRect;
                                    rectangle2.y += scrollableUnitIncrement;
                                    if (viewRect.y >= min) {
                                        viewRect.y = min;
                                        break;
                                    }
                                }
                            }
                            else if ((leftToRight && n < 0) || (!leftToRight && n > 0)) {
                                final Rectangle rectangle3 = viewRect;
                                rectangle3.x -= scrollableUnitIncrement;
                                if (leftToRight && viewRect.x < n3) {
                                    viewRect.x = n3;
                                    break;
                                }
                            }
                            else if ((leftToRight && n > 0) || (!leftToRight && n < 0)) {
                                final Rectangle rectangle4 = viewRect;
                                rectangle4.x += scrollableUnitIncrement;
                                if (leftToRight && viewRect.x > min) {
                                    viewRect.x = min;
                                    break;
                                }
                            }
                            else {
                                assert false : "Non-sensical ComponentOrientation / scroll direction";
                            }
                        }
                        if (n2 == 1) {
                            scrollBar.setValue(viewRect.y);
                        }
                        else if (leftToRight) {
                            scrollBar.setValue(viewRect.x);
                        }
                        else {
                            int value = scrollBar.getValue() - (viewRect.x - x);
                            if (value < n3) {
                                value = n3;
                            }
                            else if (value > min) {
                                value = min;
                            }
                            scrollBar.setValue(value);
                        }
                    }
                    else {
                        BasicScrollBarUI.scrollByUnits(scrollBar, n, abs, b);
                    }
                }
                else if (mouseWheelEvent.getScrollType() == 1) {
                    BasicScrollBarUI.scrollByBlock(scrollBar, n);
                }
            }
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final JViewport viewport = BasicScrollPaneUI.this.scrollpane.getViewport();
            if (viewport != null) {
                if (changeEvent.getSource() == viewport) {
                    BasicScrollPaneUI.this.syncScrollPaneWithViewport();
                }
                else {
                    final JScrollBar horizontalScrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
                    if (horizontalScrollBar != null && changeEvent.getSource() == horizontalScrollBar.getModel()) {
                        this.hsbStateChanged(viewport, changeEvent);
                    }
                    else {
                        final JScrollBar verticalScrollBar = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
                        if (verticalScrollBar != null && changeEvent.getSource() == verticalScrollBar.getModel()) {
                            this.vsbStateChanged(viewport, changeEvent);
                        }
                    }
                }
            }
        }
        
        private void vsbStateChanged(final JViewport viewport, final ChangeEvent changeEvent) {
            final BoundedRangeModel boundedRangeModel = (BoundedRangeModel)changeEvent.getSource();
            final Point viewPosition = viewport.getViewPosition();
            viewPosition.y = boundedRangeModel.getValue();
            viewport.setViewPosition(viewPosition);
        }
        
        private void hsbStateChanged(final JViewport viewport, final ChangeEvent changeEvent) {
            final BoundedRangeModel boundedRangeModel = (BoundedRangeModel)changeEvent.getSource();
            final Point viewPosition = viewport.getViewPosition();
            final int value = boundedRangeModel.getValue();
            if (BasicScrollPaneUI.this.scrollpane.getComponentOrientation().isLeftToRight()) {
                viewPosition.x = value;
            }
            else {
                final int width = viewport.getViewSize().width;
                final int width2 = viewport.getExtentSize().width;
                final int x = viewPosition.x;
                viewPosition.x = width - width2 - value;
                if (width2 == 0 && value != 0 && x == width) {
                    BasicScrollPaneUI.this.setValueCalled = true;
                }
                else if (width2 != 0 && x < 0 && viewPosition.x == 0) {
                    final Point point = viewPosition;
                    point.x += value;
                }
            }
            viewport.setViewPosition(viewPosition);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getSource() == BasicScrollPaneUI.this.scrollpane) {
                this.scrollPanePropertyChange(propertyChangeEvent);
            }
            else {
                this.sbPropertyChange(propertyChangeEvent);
            }
        }
        
        private void scrollPanePropertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "verticalScrollBarDisplayPolicy") {
                BasicScrollPaneUI.this.updateScrollBarDisplayPolicy(propertyChangeEvent);
            }
            else if (propertyName == "horizontalScrollBarDisplayPolicy") {
                BasicScrollPaneUI.this.updateScrollBarDisplayPolicy(propertyChangeEvent);
            }
            else if (propertyName == "viewport") {
                BasicScrollPaneUI.this.updateViewport(propertyChangeEvent);
            }
            else if (propertyName == "rowHeader") {
                BasicScrollPaneUI.this.updateRowHeader(propertyChangeEvent);
            }
            else if (propertyName == "columnHeader") {
                BasicScrollPaneUI.this.updateColumnHeader(propertyChangeEvent);
            }
            else if (propertyName == "verticalScrollBar") {
                BasicScrollPaneUI.this.updateVerticalScrollBar(propertyChangeEvent);
            }
            else if (propertyName == "horizontalScrollBar") {
                BasicScrollPaneUI.this.updateHorizontalScrollBar(propertyChangeEvent);
            }
            else if (propertyName == "componentOrientation") {
                BasicScrollPaneUI.this.scrollpane.revalidate();
                BasicScrollPaneUI.this.scrollpane.repaint();
            }
        }
        
        private void sbPropertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            final Object source = propertyChangeEvent.getSource();
            if ("model" == propertyName) {
                JScrollBar scrollBar = BasicScrollPaneUI.this.scrollpane.getVerticalScrollBar();
                final BoundedRangeModel boundedRangeModel = (BoundedRangeModel)propertyChangeEvent.getOldValue();
                ChangeListener changeListener = null;
                if (source == scrollBar) {
                    changeListener = BasicScrollPaneUI.this.vsbChangeListener;
                }
                else if (source == BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar()) {
                    scrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
                    changeListener = BasicScrollPaneUI.this.hsbChangeListener;
                }
                if (changeListener != null) {
                    if (boundedRangeModel != null) {
                        boundedRangeModel.removeChangeListener(changeListener);
                    }
                    if (scrollBar.getModel() != null) {
                        scrollBar.getModel().addChangeListener(changeListener);
                    }
                }
            }
            else if ("componentOrientation" == propertyName && source == BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar()) {
                final JScrollBar horizontalScrollBar = BasicScrollPaneUI.this.scrollpane.getHorizontalScrollBar();
                final JViewport viewport = BasicScrollPaneUI.this.scrollpane.getViewport();
                final Point viewPosition = viewport.getViewPosition();
                if (BasicScrollPaneUI.this.scrollpane.getComponentOrientation().isLeftToRight()) {
                    viewPosition.x = horizontalScrollBar.getValue();
                }
                else {
                    viewPosition.x = viewport.getViewSize().width - viewport.getExtentSize().width - horizontalScrollBar.getValue();
                }
                viewport.setViewPosition(viewPosition);
            }
        }
    }
}
