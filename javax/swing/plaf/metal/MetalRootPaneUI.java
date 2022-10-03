package javax.swing.plaf.metal;

import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.Rectangle;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.LayoutManager2;
import java.beans.PropertyChangeEvent;
import javax.swing.JLayeredPane;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;
import java.awt.Component;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import java.awt.Cursor;
import javax.swing.JRootPane;
import java.awt.LayoutManager;
import javax.swing.event.MouseInputListener;
import javax.swing.JComponent;
import java.awt.Window;
import javax.swing.plaf.basic.BasicRootPaneUI;

public class MetalRootPaneUI extends BasicRootPaneUI
{
    private static final String[] borderKeys;
    private static final int CORNER_DRAG_WIDTH = 16;
    private static final int BORDER_DRAG_THICKNESS = 5;
    private Window window;
    private JComponent titlePane;
    private MouseInputListener mouseInputListener;
    private LayoutManager layoutManager;
    private LayoutManager savedOldLayout;
    private JRootPane root;
    private Cursor lastCursor;
    private static final int[] cursorMapping;
    
    public MetalRootPaneUI() {
        this.lastCursor = Cursor.getPredefinedCursor(0);
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new MetalRootPaneUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        this.root = (JRootPane)component;
        if (this.root.getWindowDecorationStyle() != 0) {
            this.installClientDecorations(this.root);
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        super.uninstallUI(component);
        this.uninstallClientDecorations(this.root);
        this.layoutManager = null;
        this.mouseInputListener = null;
        this.root = null;
    }
    
    void installBorder(final JRootPane rootPane) {
        final int windowDecorationStyle = rootPane.getWindowDecorationStyle();
        if (windowDecorationStyle == 0) {
            LookAndFeel.uninstallBorder(rootPane);
        }
        else {
            LookAndFeel.installBorder(rootPane, MetalRootPaneUI.borderKeys[windowDecorationStyle]);
        }
    }
    
    private void uninstallBorder(final JRootPane rootPane) {
        LookAndFeel.uninstallBorder(rootPane);
    }
    
    private void installWindowListeners(final JRootPane rootPane, final Component component) {
        if (component instanceof Window) {
            this.window = (Window)component;
        }
        else {
            this.window = SwingUtilities.getWindowAncestor(component);
        }
        if (this.window != null) {
            if (this.mouseInputListener == null) {
                this.mouseInputListener = this.createWindowMouseInputListener(rootPane);
            }
            this.window.addMouseListener(this.mouseInputListener);
            this.window.addMouseMotionListener(this.mouseInputListener);
        }
    }
    
    private void uninstallWindowListeners(final JRootPane rootPane) {
        if (this.window != null) {
            this.window.removeMouseListener(this.mouseInputListener);
            this.window.removeMouseMotionListener(this.mouseInputListener);
        }
    }
    
    private void installLayout(final JRootPane rootPane) {
        if (this.layoutManager == null) {
            this.layoutManager = this.createLayoutManager();
        }
        this.savedOldLayout = rootPane.getLayout();
        rootPane.setLayout(this.layoutManager);
    }
    
    private void uninstallLayout(final JRootPane rootPane) {
        if (this.savedOldLayout != null) {
            rootPane.setLayout(this.savedOldLayout);
            this.savedOldLayout = null;
        }
    }
    
    private void installClientDecorations(final JRootPane rootPane) {
        this.installBorder(rootPane);
        this.setTitlePane(rootPane, this.createTitlePane(rootPane));
        this.installWindowListeners(rootPane, rootPane.getParent());
        this.installLayout(rootPane);
        if (this.window != null) {
            rootPane.revalidate();
            rootPane.repaint();
        }
    }
    
    private void uninstallClientDecorations(final JRootPane rootPane) {
        this.uninstallBorder(rootPane);
        this.uninstallWindowListeners(rootPane);
        this.setTitlePane(rootPane, null);
        this.uninstallLayout(rootPane);
        if (rootPane.getWindowDecorationStyle() == 0) {
            rootPane.repaint();
            rootPane.revalidate();
        }
        if (this.window != null) {
            this.window.setCursor(Cursor.getPredefinedCursor(0));
        }
        this.window = null;
    }
    
    private JComponent createTitlePane(final JRootPane rootPane) {
        return new MetalTitlePane(rootPane, this);
    }
    
    private MouseInputListener createWindowMouseInputListener(final JRootPane rootPane) {
        return new MouseInputHandler();
    }
    
    private LayoutManager createLayoutManager() {
        return new MetalRootLayout();
    }
    
    private void setTitlePane(final JRootPane rootPane, final JComponent titlePane) {
        final JLayeredPane layeredPane = rootPane.getLayeredPane();
        final JComponent titlePane2 = this.getTitlePane();
        if (titlePane2 != null) {
            titlePane2.setVisible(false);
            layeredPane.remove(titlePane2);
        }
        if (titlePane != null) {
            layeredPane.add(titlePane, JLayeredPane.FRAME_CONTENT_LAYER);
            titlePane.setVisible(true);
        }
        this.titlePane = titlePane;
    }
    
    private JComponent getTitlePane() {
        return this.titlePane;
    }
    
    private JRootPane getRootPane() {
        return this.root;
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        super.propertyChange(propertyChangeEvent);
        final String propertyName = propertyChangeEvent.getPropertyName();
        if (propertyName == null) {
            return;
        }
        if (propertyName.equals("windowDecorationStyle")) {
            final JRootPane rootPane = (JRootPane)propertyChangeEvent.getSource();
            final int windowDecorationStyle = rootPane.getWindowDecorationStyle();
            this.uninstallClientDecorations(rootPane);
            if (windowDecorationStyle != 0) {
                this.installClientDecorations(rootPane);
            }
        }
        else if (propertyName.equals("ancestor")) {
            this.uninstallWindowListeners(this.root);
            if (((JRootPane)propertyChangeEvent.getSource()).getWindowDecorationStyle() != 0) {
                this.installWindowListeners(this.root, this.root.getParent());
            }
        }
    }
    
    static {
        borderKeys = new String[] { null, "RootPane.frameBorder", "RootPane.plainDialogBorder", "RootPane.informationDialogBorder", "RootPane.errorDialogBorder", "RootPane.colorChooserDialogBorder", "RootPane.fileChooserDialogBorder", "RootPane.questionDialogBorder", "RootPane.warningDialogBorder" };
        cursorMapping = new int[] { 6, 6, 8, 7, 7, 6, 0, 0, 0, 7, 10, 0, 0, 0, 11, 4, 0, 0, 0, 5, 4, 4, 9, 5, 5 };
    }
    
    private static class MetalRootLayout implements LayoutManager2
    {
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            int width = 0;
            int height = 0;
            int width2 = 0;
            int height2 = 0;
            int width3 = 0;
            final Insets insets = container.getInsets();
            final JRootPane rootPane = (JRootPane)container;
            Dimension dimension;
            if (rootPane.getContentPane() != null) {
                dimension = rootPane.getContentPane().getPreferredSize();
            }
            else {
                dimension = rootPane.getSize();
            }
            if (dimension != null) {
                width = dimension.width;
                height = dimension.height;
            }
            if (rootPane.getMenuBar() != null) {
                final Dimension preferredSize = rootPane.getMenuBar().getPreferredSize();
                if (preferredSize != null) {
                    width2 = preferredSize.width;
                    height2 = preferredSize.height;
                }
            }
            if (rootPane.getWindowDecorationStyle() != 0 && rootPane.getUI() instanceof MetalRootPaneUI) {
                final JComponent access$200 = ((MetalRootPaneUI)rootPane.getUI()).getTitlePane();
                if (access$200 != null) {
                    final Dimension preferredSize2 = access$200.getPreferredSize();
                    if (preferredSize2 != null) {
                        width3 = preferredSize2.width;
                        final int height3 = preferredSize2.height;
                    }
                }
            }
            return new Dimension(Math.max(Math.max(width, width2), width3) + insets.left + insets.right, height + height2 + width3 + insets.top + insets.bottom);
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            int width = 0;
            int height = 0;
            int width2 = 0;
            int height2 = 0;
            int width3 = 0;
            final Insets insets = container.getInsets();
            final JRootPane rootPane = (JRootPane)container;
            Dimension dimension;
            if (rootPane.getContentPane() != null) {
                dimension = rootPane.getContentPane().getMinimumSize();
            }
            else {
                dimension = rootPane.getSize();
            }
            if (dimension != null) {
                width = dimension.width;
                height = dimension.height;
            }
            if (rootPane.getMenuBar() != null) {
                final Dimension minimumSize = rootPane.getMenuBar().getMinimumSize();
                if (minimumSize != null) {
                    width2 = minimumSize.width;
                    height2 = minimumSize.height;
                }
            }
            if (rootPane.getWindowDecorationStyle() != 0 && rootPane.getUI() instanceof MetalRootPaneUI) {
                final JComponent access$200 = ((MetalRootPaneUI)rootPane.getUI()).getTitlePane();
                if (access$200 != null) {
                    final Dimension minimumSize2 = access$200.getMinimumSize();
                    if (minimumSize2 != null) {
                        width3 = minimumSize2.width;
                        final int height3 = minimumSize2.height;
                    }
                }
            }
            return new Dimension(Math.max(Math.max(width, width2), width3) + insets.left + insets.right, height + height2 + width3 + insets.top + insets.bottom);
        }
        
        @Override
        public Dimension maximumLayoutSize(final Container container) {
            int width = Integer.MAX_VALUE;
            int height = Integer.MAX_VALUE;
            int width2 = Integer.MAX_VALUE;
            int height2 = Integer.MAX_VALUE;
            int width3 = Integer.MAX_VALUE;
            int height3 = Integer.MAX_VALUE;
            final Insets insets = container.getInsets();
            final JRootPane rootPane = (JRootPane)container;
            if (rootPane.getContentPane() != null) {
                final Dimension maximumSize = rootPane.getContentPane().getMaximumSize();
                if (maximumSize != null) {
                    width = maximumSize.width;
                    height = maximumSize.height;
                }
            }
            if (rootPane.getMenuBar() != null) {
                final Dimension maximumSize2 = rootPane.getMenuBar().getMaximumSize();
                if (maximumSize2 != null) {
                    width2 = maximumSize2.width;
                    height2 = maximumSize2.height;
                }
            }
            if (rootPane.getWindowDecorationStyle() != 0 && rootPane.getUI() instanceof MetalRootPaneUI) {
                final JComponent access$200 = ((MetalRootPaneUI)rootPane.getUI()).getTitlePane();
                if (access$200 != null) {
                    final Dimension maximumSize3 = access$200.getMaximumSize();
                    if (maximumSize3 != null) {
                        width3 = maximumSize3.width;
                        height3 = maximumSize3.height;
                    }
                }
            }
            int max = Math.max(Math.max(height, height2), height3);
            if (max != Integer.MAX_VALUE) {
                max = height + height2 + height3 + insets.top + insets.bottom;
            }
            int max2 = Math.max(Math.max(width, width2), width3);
            if (max2 != Integer.MAX_VALUE) {
                max2 += insets.left + insets.right;
            }
            return new Dimension(max2, max);
        }
        
        @Override
        public void layoutContainer(final Container container) {
            final JRootPane rootPane = (JRootPane)container;
            final Rectangle bounds = rootPane.getBounds();
            final Insets insets = rootPane.getInsets();
            int n = 0;
            final int n2 = bounds.width - insets.right - insets.left;
            final int n3 = bounds.height - insets.top - insets.bottom;
            if (rootPane.getLayeredPane() != null) {
                rootPane.getLayeredPane().setBounds(insets.left, insets.top, n2, n3);
            }
            if (rootPane.getGlassPane() != null) {
                rootPane.getGlassPane().setBounds(insets.left, insets.top, n2, n3);
            }
            if (rootPane.getWindowDecorationStyle() != 0 && rootPane.getUI() instanceof MetalRootPaneUI) {
                final JComponent access$200 = ((MetalRootPaneUI)rootPane.getUI()).getTitlePane();
                if (access$200 != null) {
                    final Dimension preferredSize = access$200.getPreferredSize();
                    if (preferredSize != null) {
                        final int height = preferredSize.height;
                        access$200.setBounds(0, 0, n2, height);
                        n += height;
                    }
                }
            }
            if (rootPane.getMenuBar() != null) {
                final Dimension preferredSize2 = rootPane.getMenuBar().getPreferredSize();
                rootPane.getMenuBar().setBounds(0, n, n2, preferredSize2.height);
                n += preferredSize2.height;
            }
            if (rootPane.getContentPane() != null) {
                rootPane.getContentPane().getPreferredSize();
                rootPane.getContentPane().setBounds(0, n, n2, (n3 < n) ? 0 : (n3 - n));
            }
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public void addLayoutComponent(final Component component, final Object o) {
        }
        
        @Override
        public float getLayoutAlignmentX(final Container container) {
            return 0.0f;
        }
        
        @Override
        public float getLayoutAlignmentY(final Container container) {
            return 0.0f;
        }
        
        @Override
        public void invalidateLayout(final Container container) {
        }
    }
    
    private class MouseInputHandler implements MouseInputListener
    {
        private boolean isMovingWindow;
        private int dragCursor;
        private int dragOffsetX;
        private int dragOffsetY;
        private int dragWidth;
        private int dragHeight;
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (MetalRootPaneUI.this.getRootPane().getWindowDecorationStyle() == 0) {
                return;
            }
            final Point point = mouseEvent.getPoint();
            final Window window = (Window)mouseEvent.getSource();
            if (window != null) {
                window.toFront();
            }
            final Point convertPoint = SwingUtilities.convertPoint(window, point, MetalRootPaneUI.this.getTitlePane());
            Frame frame = null;
            Dialog dialog = null;
            if (window instanceof Frame) {
                frame = (Frame)window;
            }
            else if (window instanceof Dialog) {
                dialog = (Dialog)window;
            }
            final int n = (frame != null) ? frame.getExtendedState() : 0;
            if (MetalRootPaneUI.this.getTitlePane() != null && MetalRootPaneUI.this.getTitlePane().contains(convertPoint)) {
                if (((frame != null && (n & 0x6) == 0x0) || dialog != null) && point.y >= 5 && point.x >= 5 && point.x < window.getWidth() - 5) {
                    this.isMovingWindow = true;
                    this.dragOffsetX = point.x;
                    this.dragOffsetY = point.y;
                }
            }
            else if ((frame != null && frame.isResizable() && (n & 0x6) == 0x0) || (dialog != null && dialog.isResizable())) {
                this.dragOffsetX = point.x;
                this.dragOffsetY = point.y;
                this.dragWidth = window.getWidth();
                this.dragHeight = window.getHeight();
                this.dragCursor = this.getCursor(this.calculateCorner(window, point.x, point.y));
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (this.dragCursor != 0 && MetalRootPaneUI.this.window != null && !MetalRootPaneUI.this.window.isValid()) {
                MetalRootPaneUI.this.window.validate();
                MetalRootPaneUI.this.getRootPane().repaint();
            }
            this.isMovingWindow = false;
            this.dragCursor = 0;
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            if (MetalRootPaneUI.this.getRootPane().getWindowDecorationStyle() == 0) {
                return;
            }
            final Window window = (Window)mouseEvent.getSource();
            Frame frame = null;
            Dialog dialog = null;
            if (window instanceof Frame) {
                frame = (Frame)window;
            }
            else if (window instanceof Dialog) {
                dialog = (Dialog)window;
            }
            final int cursor = this.getCursor(this.calculateCorner(window, mouseEvent.getX(), mouseEvent.getY()));
            if (cursor != 0 && ((frame != null && frame.isResizable() && (frame.getExtendedState() & 0x6) == 0x0) || (dialog != null && dialog.isResizable()))) {
                window.setCursor(Cursor.getPredefinedCursor(cursor));
            }
            else {
                window.setCursor(MetalRootPaneUI.this.lastCursor);
            }
        }
        
        private void adjust(final Rectangle rectangle, final Dimension dimension, final int n, final int n2, final int n3, final int n4) {
            rectangle.x += n;
            rectangle.y += n2;
            rectangle.width += n3;
            rectangle.height += n4;
            if (dimension != null) {
                if (rectangle.width < dimension.width) {
                    final int n5 = dimension.width - rectangle.width;
                    if (n != 0) {
                        rectangle.x -= n5;
                    }
                    rectangle.width = dimension.width;
                }
                if (rectangle.height < dimension.height) {
                    final int n6 = dimension.height - rectangle.height;
                    if (n2 != 0) {
                        rectangle.y -= n6;
                    }
                    rectangle.height = dimension.height;
                }
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            final Window window = (Window)mouseEvent.getSource();
            final Point point = mouseEvent.getPoint();
            if (this.isMovingWindow) {
                final Point locationOnScreen = mouseEvent.getLocationOnScreen();
                window.setLocation(locationOnScreen.x - this.dragOffsetX, locationOnScreen.y - this.dragOffsetY);
            }
            else if (this.dragCursor != 0) {
                final Rectangle bounds = window.getBounds();
                final Rectangle rectangle = new Rectangle(bounds);
                final Dimension minimumSize = window.getMinimumSize();
                switch (this.dragCursor) {
                    case 11: {
                        this.adjust(bounds, minimumSize, 0, 0, point.x + (this.dragWidth - this.dragOffsetX) - bounds.width, 0);
                        break;
                    }
                    case 9: {
                        this.adjust(bounds, minimumSize, 0, 0, 0, point.y + (this.dragHeight - this.dragOffsetY) - bounds.height);
                        break;
                    }
                    case 8: {
                        this.adjust(bounds, minimumSize, 0, point.y - this.dragOffsetY, 0, -(point.y - this.dragOffsetY));
                        break;
                    }
                    case 10: {
                        this.adjust(bounds, minimumSize, point.x - this.dragOffsetX, 0, -(point.x - this.dragOffsetX), 0);
                        break;
                    }
                    case 7: {
                        this.adjust(bounds, minimumSize, 0, point.y - this.dragOffsetY, point.x + (this.dragWidth - this.dragOffsetX) - bounds.width, -(point.y - this.dragOffsetY));
                        break;
                    }
                    case 5: {
                        this.adjust(bounds, minimumSize, 0, 0, point.x + (this.dragWidth - this.dragOffsetX) - bounds.width, point.y + (this.dragHeight - this.dragOffsetY) - bounds.height);
                        break;
                    }
                    case 6: {
                        this.adjust(bounds, minimumSize, point.x - this.dragOffsetX, point.y - this.dragOffsetY, -(point.x - this.dragOffsetX), -(point.y - this.dragOffsetY));
                        break;
                    }
                    case 4: {
                        this.adjust(bounds, minimumSize, point.x - this.dragOffsetX, 0, -(point.x - this.dragOffsetX), point.y + (this.dragHeight - this.dragOffsetY) - bounds.height);
                        break;
                    }
                }
                if (!bounds.equals(rectangle)) {
                    window.setBounds(bounds);
                    if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
                        window.validate();
                        MetalRootPaneUI.this.getRootPane().repaint();
                    }
                }
            }
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            MetalRootPaneUI.this.lastCursor = ((Window)mouseEvent.getSource()).getCursor();
            this.mouseMoved(mouseEvent);
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            ((Window)mouseEvent.getSource()).setCursor(MetalRootPaneUI.this.lastCursor);
        }
        
        @Override
        public void mouseClicked(final MouseEvent mouseEvent) {
            final Window window = (Window)mouseEvent.getSource();
            if (!(window instanceof Frame)) {
                return;
            }
            final Frame frame = (Frame)window;
            final Point convertPoint = SwingUtilities.convertPoint(window, mouseEvent.getPoint(), MetalRootPaneUI.this.getTitlePane());
            final int extendedState = frame.getExtendedState();
            if (MetalRootPaneUI.this.getTitlePane() != null && MetalRootPaneUI.this.getTitlePane().contains(convertPoint) && mouseEvent.getClickCount() % 2 == 0 && (mouseEvent.getModifiers() & 0x10) != 0x0 && frame.isResizable()) {
                if ((extendedState & 0x6) != 0x0) {
                    frame.setExtendedState(extendedState & 0xFFFFFFF9);
                }
                else {
                    frame.setExtendedState(extendedState | 0x6);
                }
            }
        }
        
        private int calculateCorner(final Window window, final int n, final int n2) {
            final Insets insets = window.getInsets();
            final int calculatePosition = this.calculatePosition(n - insets.left, window.getWidth() - insets.left - insets.right);
            final int calculatePosition2 = this.calculatePosition(n2 - insets.top, window.getHeight() - insets.top - insets.bottom);
            if (calculatePosition == -1 || calculatePosition2 == -1) {
                return -1;
            }
            return calculatePosition2 * 5 + calculatePosition;
        }
        
        private int getCursor(final int n) {
            if (n == -1) {
                return 0;
            }
            return MetalRootPaneUI.cursorMapping[n];
        }
        
        private int calculatePosition(final int n, final int n2) {
            if (n < 5) {
                return 0;
            }
            if (n < 16) {
                return 1;
            }
            if (n >= n2 - 5) {
                return 4;
            }
            if (n >= n2 - 16) {
                return 3;
            }
            return 2;
        }
    }
}
