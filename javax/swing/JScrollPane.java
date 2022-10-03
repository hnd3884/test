package javax.swing;

import javax.accessibility.AccessibleRelation;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import javax.accessibility.AccessibleRole;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.UIResource;
import javax.accessibility.AccessibleContext;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.awt.ComponentOrientation;
import java.beans.Transient;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.ScrollPaneUI;
import java.awt.Point;
import java.awt.LayoutManager;
import java.awt.Component;
import javax.swing.border.Border;
import javax.accessibility.Accessible;

public class JScrollPane extends JComponent implements ScrollPaneConstants, Accessible
{
    private Border viewportBorder;
    private static final String uiClassID = "ScrollPaneUI";
    protected int verticalScrollBarPolicy;
    protected int horizontalScrollBarPolicy;
    protected JViewport viewport;
    protected JScrollBar verticalScrollBar;
    protected JScrollBar horizontalScrollBar;
    protected JViewport rowHeader;
    protected JViewport columnHeader;
    protected Component lowerLeft;
    protected Component lowerRight;
    protected Component upperLeft;
    protected Component upperRight;
    private boolean wheelScrollState;
    
    public JScrollPane(final Component viewportView, final int verticalScrollBarPolicy, final int horizontalScrollBarPolicy) {
        this.verticalScrollBarPolicy = 20;
        this.horizontalScrollBarPolicy = 30;
        this.wheelScrollState = true;
        this.setLayout(new ScrollPaneLayout.UIResource());
        this.setVerticalScrollBarPolicy(verticalScrollBarPolicy);
        this.setHorizontalScrollBarPolicy(horizontalScrollBarPolicy);
        this.setViewport(this.createViewport());
        this.setVerticalScrollBar(this.createVerticalScrollBar());
        this.setHorizontalScrollBar(this.createHorizontalScrollBar());
        if (viewportView != null) {
            this.setViewportView(viewportView);
        }
        this.setUIProperty("opaque", true);
        this.updateUI();
        if (!this.getComponentOrientation().isLeftToRight()) {
            this.viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
        }
    }
    
    public JScrollPane(final Component component) {
        this(component, 20, 30);
    }
    
    public JScrollPane(final int n, final int n2) {
        this(null, n, n2);
    }
    
    public JScrollPane() {
        this(null, 20, 30);
    }
    
    public ScrollPaneUI getUI() {
        return (ScrollPaneUI)this.ui;
    }
    
    public void setUI(final ScrollPaneUI ui) {
        super.setUI(ui);
    }
    
    @Override
    public void updateUI() {
        this.setUI((ScrollPaneUI)UIManager.getUI(this));
    }
    
    @Override
    public String getUIClassID() {
        return "ScrollPaneUI";
    }
    
    @Override
    public void setLayout(final LayoutManager layoutManager) {
        if (layoutManager instanceof ScrollPaneLayout) {
            super.setLayout(layoutManager);
            ((ScrollPaneLayout)layoutManager).syncWithScrollPane(this);
        }
        else {
            if (layoutManager != null) {
                throw new ClassCastException("layout of JScrollPane must be a ScrollPaneLayout");
            }
            super.setLayout(layoutManager);
        }
    }
    
    @Override
    public boolean isValidateRoot() {
        return true;
    }
    
    public int getVerticalScrollBarPolicy() {
        return this.verticalScrollBarPolicy;
    }
    
    public void setVerticalScrollBarPolicy(final int verticalScrollBarPolicy) {
        switch (verticalScrollBarPolicy) {
            case 20:
            case 21:
            case 22: {
                this.firePropertyChange("verticalScrollBarPolicy", this.verticalScrollBarPolicy, this.verticalScrollBarPolicy = verticalScrollBarPolicy);
                this.revalidate();
                this.repaint();
                return;
            }
            default: {
                throw new IllegalArgumentException("invalid verticalScrollBarPolicy");
            }
        }
    }
    
    public int getHorizontalScrollBarPolicy() {
        return this.horizontalScrollBarPolicy;
    }
    
    public void setHorizontalScrollBarPolicy(final int horizontalScrollBarPolicy) {
        switch (horizontalScrollBarPolicy) {
            case 30:
            case 31:
            case 32: {
                this.firePropertyChange("horizontalScrollBarPolicy", this.horizontalScrollBarPolicy, this.horizontalScrollBarPolicy = horizontalScrollBarPolicy);
                this.revalidate();
                this.repaint();
                return;
            }
            default: {
                throw new IllegalArgumentException("invalid horizontalScrollBarPolicy");
            }
        }
    }
    
    public Border getViewportBorder() {
        return this.viewportBorder;
    }
    
    public void setViewportBorder(final Border viewportBorder) {
        this.firePropertyChange("viewportBorder", this.viewportBorder, this.viewportBorder = viewportBorder);
    }
    
    public Rectangle getViewportBorderBounds() {
        final Rectangle rectangle = new Rectangle(this.getSize());
        final Insets insets = this.getInsets();
        rectangle.x = insets.left;
        rectangle.y = insets.top;
        final Rectangle rectangle2 = rectangle;
        rectangle2.width -= insets.left + insets.right;
        final Rectangle rectangle3 = rectangle;
        rectangle3.height -= insets.top + insets.bottom;
        final boolean leftToRight = SwingUtilities.isLeftToRight(this);
        final JViewport columnHeader = this.getColumnHeader();
        if (columnHeader != null && columnHeader.isVisible()) {
            final int height = columnHeader.getHeight();
            final Rectangle rectangle4 = rectangle;
            rectangle4.y += height;
            final Rectangle rectangle5 = rectangle;
            rectangle5.height -= height;
        }
        final JViewport rowHeader = this.getRowHeader();
        if (rowHeader != null && rowHeader.isVisible()) {
            final int width = rowHeader.getWidth();
            if (leftToRight) {
                final Rectangle rectangle6 = rectangle;
                rectangle6.x += width;
            }
            final Rectangle rectangle7 = rectangle;
            rectangle7.width -= width;
        }
        final JScrollBar verticalScrollBar = this.getVerticalScrollBar();
        if (verticalScrollBar != null && verticalScrollBar.isVisible()) {
            final int width2 = verticalScrollBar.getWidth();
            if (!leftToRight) {
                final Rectangle rectangle8 = rectangle;
                rectangle8.x += width2;
            }
            final Rectangle rectangle9 = rectangle;
            rectangle9.width -= width2;
        }
        final JScrollBar horizontalScrollBar = this.getHorizontalScrollBar();
        if (horizontalScrollBar != null && horizontalScrollBar.isVisible()) {
            final Rectangle rectangle10 = rectangle;
            rectangle10.height -= horizontalScrollBar.getHeight();
        }
        return rectangle;
    }
    
    public JScrollBar createHorizontalScrollBar() {
        return new ScrollBar(0);
    }
    
    @Transient
    public JScrollBar getHorizontalScrollBar() {
        return this.horizontalScrollBar;
    }
    
    public void setHorizontalScrollBar(final JScrollBar horizontalScrollBar) {
        final JScrollBar horizontalScrollBar2 = this.getHorizontalScrollBar();
        this.horizontalScrollBar = horizontalScrollBar;
        if (horizontalScrollBar != null) {
            this.add(horizontalScrollBar, "HORIZONTAL_SCROLLBAR");
        }
        else if (horizontalScrollBar2 != null) {
            this.remove(horizontalScrollBar2);
        }
        this.firePropertyChange("horizontalScrollBar", horizontalScrollBar2, horizontalScrollBar);
        this.revalidate();
        this.repaint();
    }
    
    public JScrollBar createVerticalScrollBar() {
        return new ScrollBar(1);
    }
    
    @Transient
    public JScrollBar getVerticalScrollBar() {
        return this.verticalScrollBar;
    }
    
    public void setVerticalScrollBar(final JScrollBar verticalScrollBar) {
        final JScrollBar verticalScrollBar2 = this.getVerticalScrollBar();
        this.add(this.verticalScrollBar = verticalScrollBar, "VERTICAL_SCROLLBAR");
        this.firePropertyChange("verticalScrollBar", verticalScrollBar2, verticalScrollBar);
        this.revalidate();
        this.repaint();
    }
    
    protected JViewport createViewport() {
        return new JViewport();
    }
    
    public JViewport getViewport() {
        return this.viewport;
    }
    
    public void setViewport(final JViewport viewport) {
        final JViewport viewport2 = this.getViewport();
        this.viewport = viewport;
        if (viewport != null) {
            this.add(viewport, "VIEWPORT");
        }
        else if (viewport2 != null) {
            this.remove(viewport2);
        }
        this.firePropertyChange("viewport", viewport2, viewport);
        if (this.accessibleContext != null) {
            ((AccessibleJScrollPane)this.accessibleContext).resetViewPort();
        }
        this.revalidate();
        this.repaint();
    }
    
    public void setViewportView(final Component view) {
        if (this.getViewport() == null) {
            this.setViewport(this.createViewport());
        }
        this.getViewport().setView(view);
    }
    
    @Transient
    public JViewport getRowHeader() {
        return this.rowHeader;
    }
    
    public void setRowHeader(final JViewport rowHeader) {
        final JViewport rowHeader2 = this.getRowHeader();
        this.rowHeader = rowHeader;
        if (rowHeader != null) {
            this.add(rowHeader, "ROW_HEADER");
        }
        else if (rowHeader2 != null) {
            this.remove(rowHeader2);
        }
        this.firePropertyChange("rowHeader", rowHeader2, rowHeader);
        this.revalidate();
        this.repaint();
    }
    
    public void setRowHeaderView(final Component view) {
        if (this.getRowHeader() == null) {
            this.setRowHeader(this.createViewport());
        }
        this.getRowHeader().setView(view);
    }
    
    @Transient
    public JViewport getColumnHeader() {
        return this.columnHeader;
    }
    
    public void setColumnHeader(final JViewport columnHeader) {
        final JViewport columnHeader2 = this.getColumnHeader();
        this.columnHeader = columnHeader;
        if (columnHeader != null) {
            this.add(columnHeader, "COLUMN_HEADER");
        }
        else if (columnHeader2 != null) {
            this.remove(columnHeader2);
        }
        this.firePropertyChange("columnHeader", columnHeader2, columnHeader);
        this.revalidate();
        this.repaint();
    }
    
    public void setColumnHeaderView(final Component view) {
        if (this.getColumnHeader() == null) {
            this.setColumnHeader(this.createViewport());
        }
        this.getColumnHeader().setView(view);
    }
    
    public Component getCorner(String s) {
        final boolean leftToRight = this.getComponentOrientation().isLeftToRight();
        if (s.equals("LOWER_LEADING_CORNER")) {
            s = (leftToRight ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER");
        }
        else if (s.equals("LOWER_TRAILING_CORNER")) {
            s = (leftToRight ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER");
        }
        else if (s.equals("UPPER_LEADING_CORNER")) {
            s = (leftToRight ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER");
        }
        else if (s.equals("UPPER_TRAILING_CORNER")) {
            s = (leftToRight ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER");
        }
        if (s.equals("LOWER_LEFT_CORNER")) {
            return this.lowerLeft;
        }
        if (s.equals("LOWER_RIGHT_CORNER")) {
            return this.lowerRight;
        }
        if (s.equals("UPPER_LEFT_CORNER")) {
            return this.upperLeft;
        }
        if (s.equals("UPPER_RIGHT_CORNER")) {
            return this.upperRight;
        }
        return null;
    }
    
    public void setCorner(String s, final Component component) {
        final boolean leftToRight = this.getComponentOrientation().isLeftToRight();
        if (s.equals("LOWER_LEADING_CORNER")) {
            s = (leftToRight ? "LOWER_LEFT_CORNER" : "LOWER_RIGHT_CORNER");
        }
        else if (s.equals("LOWER_TRAILING_CORNER")) {
            s = (leftToRight ? "LOWER_RIGHT_CORNER" : "LOWER_LEFT_CORNER");
        }
        else if (s.equals("UPPER_LEADING_CORNER")) {
            s = (leftToRight ? "UPPER_LEFT_CORNER" : "UPPER_RIGHT_CORNER");
        }
        else if (s.equals("UPPER_TRAILING_CORNER")) {
            s = (leftToRight ? "UPPER_RIGHT_CORNER" : "UPPER_LEFT_CORNER");
        }
        Component component2;
        if (s.equals("LOWER_LEFT_CORNER")) {
            component2 = this.lowerLeft;
            this.lowerLeft = component;
        }
        else if (s.equals("LOWER_RIGHT_CORNER")) {
            component2 = this.lowerRight;
            this.lowerRight = component;
        }
        else if (s.equals("UPPER_LEFT_CORNER")) {
            component2 = this.upperLeft;
            this.upperLeft = component;
        }
        else {
            if (!s.equals("UPPER_RIGHT_CORNER")) {
                throw new IllegalArgumentException("invalid corner key");
            }
            component2 = this.upperRight;
            this.upperRight = component;
        }
        if (component2 != null) {
            this.remove(component2);
        }
        if (component != null) {
            this.add(component, s);
        }
        this.firePropertyChange(s, component2, component);
        this.revalidate();
        this.repaint();
    }
    
    @Override
    public void setComponentOrientation(final ComponentOrientation componentOrientation) {
        super.setComponentOrientation(componentOrientation);
        if (this.verticalScrollBar != null) {
            this.verticalScrollBar.setComponentOrientation(componentOrientation);
        }
        if (this.horizontalScrollBar != null) {
            this.horizontalScrollBar.setComponentOrientation(componentOrientation);
        }
    }
    
    public boolean isWheelScrollingEnabled() {
        return this.wheelScrollState;
    }
    
    public void setWheelScrollingEnabled(final boolean wheelScrollState) {
        this.firePropertyChange("wheelScrollingEnabled", this.wheelScrollState, this.wheelScrollState = wheelScrollState);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.getUIClassID().equals("ScrollPaneUI")) {
            final byte b = (byte)(JComponent.getWriteObjCounter(this) - 1);
            JComponent.setWriteObjCounter(this, b);
            if (b == 0 && this.ui != null) {
                this.ui.installUI(this);
            }
        }
    }
    
    @Override
    protected String paramString() {
        final String s = (this.viewportBorder != null) ? this.viewportBorder.toString() : "";
        final String s2 = (this.viewport != null) ? this.viewport.toString() : "";
        String s3;
        if (this.verticalScrollBarPolicy == 20) {
            s3 = "VERTICAL_SCROLLBAR_AS_NEEDED";
        }
        else if (this.verticalScrollBarPolicy == 21) {
            s3 = "VERTICAL_SCROLLBAR_NEVER";
        }
        else if (this.verticalScrollBarPolicy == 22) {
            s3 = "VERTICAL_SCROLLBAR_ALWAYS";
        }
        else {
            s3 = "";
        }
        String s4;
        if (this.horizontalScrollBarPolicy == 30) {
            s4 = "HORIZONTAL_SCROLLBAR_AS_NEEDED";
        }
        else if (this.horizontalScrollBarPolicy == 31) {
            s4 = "HORIZONTAL_SCROLLBAR_NEVER";
        }
        else if (this.horizontalScrollBarPolicy == 32) {
            s4 = "HORIZONTAL_SCROLLBAR_ALWAYS";
        }
        else {
            s4 = "";
        }
        return super.paramString() + ",columnHeader=" + ((this.columnHeader != null) ? this.columnHeader.toString() : "") + ",horizontalScrollBar=" + ((this.horizontalScrollBar != null) ? this.horizontalScrollBar.toString() : "") + ",horizontalScrollBarPolicy=" + s4 + ",lowerLeft=" + ((this.lowerLeft != null) ? this.lowerLeft.toString() : "") + ",lowerRight=" + ((this.lowerRight != null) ? this.lowerRight.toString() : "") + ",rowHeader=" + ((this.rowHeader != null) ? this.rowHeader.toString() : "") + ",upperLeft=" + ((this.upperLeft != null) ? this.upperLeft.toString() : "") + ",upperRight=" + ((this.upperRight != null) ? this.upperRight.toString() : "") + ",verticalScrollBar=" + ((this.verticalScrollBar != null) ? this.verticalScrollBar.toString() : "") + ",verticalScrollBarPolicy=" + s3 + ",viewport=" + s2 + ",viewportBorder=" + s;
    }
    
    @Override
    public AccessibleContext getAccessibleContext() {
        if (this.accessibleContext == null) {
            this.accessibleContext = new AccessibleJScrollPane();
        }
        return this.accessibleContext;
    }
    
    protected class ScrollBar extends JScrollBar implements UIResource
    {
        private boolean unitIncrementSet;
        private boolean blockIncrementSet;
        
        public ScrollBar(final int n) {
            super(n);
            this.putClientProperty("JScrollBar.fastWheelScrolling", Boolean.TRUE);
        }
        
        @Override
        public void setUnitIncrement(final int unitIncrement) {
            this.unitIncrementSet = true;
            this.putClientProperty("JScrollBar.fastWheelScrolling", null);
            super.setUnitIncrement(unitIncrement);
        }
        
        @Override
        public int getUnitIncrement(final int n) {
            final JViewport viewport = JScrollPane.this.getViewport();
            if (!this.unitIncrementSet && viewport != null && viewport.getView() instanceof Scrollable) {
                return ((Scrollable)viewport.getView()).getScrollableUnitIncrement(viewport.getViewRect(), this.getOrientation(), n);
            }
            return super.getUnitIncrement(n);
        }
        
        @Override
        public void setBlockIncrement(final int blockIncrement) {
            this.blockIncrementSet = true;
            this.putClientProperty("JScrollBar.fastWheelScrolling", null);
            super.setBlockIncrement(blockIncrement);
        }
        
        @Override
        public int getBlockIncrement(final int n) {
            final JViewport viewport = JScrollPane.this.getViewport();
            if (this.blockIncrementSet || viewport == null) {
                return super.getBlockIncrement(n);
            }
            if (viewport.getView() instanceof Scrollable) {
                return ((Scrollable)viewport.getView()).getScrollableBlockIncrement(viewport.getViewRect(), this.getOrientation(), n);
            }
            if (this.getOrientation() == 1) {
                return viewport.getExtentSize().height;
            }
            return viewport.getExtentSize().width;
        }
    }
    
    protected class AccessibleJScrollPane extends AccessibleJComponent implements ChangeListener, PropertyChangeListener
    {
        protected JViewport viewPort;
        
        public void resetViewPort() {
            if (this.viewPort != null) {
                this.viewPort.removeChangeListener(this);
                this.viewPort.removePropertyChangeListener(this);
            }
            this.viewPort = JScrollPane.this.getViewport();
            if (this.viewPort != null) {
                this.viewPort.addChangeListener(this);
                this.viewPort.addPropertyChangeListener(this);
            }
        }
        
        public AccessibleJScrollPane() {
            this.viewPort = null;
            this.resetViewPort();
            final JScrollBar horizontalScrollBar = JScrollPane.this.getHorizontalScrollBar();
            if (horizontalScrollBar != null) {
                this.setScrollBarRelations(horizontalScrollBar);
            }
            final JScrollBar verticalScrollBar = JScrollPane.this.getVerticalScrollBar();
            if (verticalScrollBar != null) {
                this.setScrollBarRelations(verticalScrollBar);
            }
        }
        
        @Override
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.SCROLL_PANE;
        }
        
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            if (changeEvent == null) {
                throw new NullPointerException();
            }
            this.firePropertyChange("AccessibleVisibleData", false, true);
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if ((propertyName == "horizontalScrollBar" || propertyName == "verticalScrollBar") && propertyChangeEvent.getNewValue() instanceof JScrollBar) {
                this.setScrollBarRelations((JScrollBar)propertyChangeEvent.getNewValue());
            }
        }
        
        void setScrollBarRelations(final JScrollBar scrollBar) {
            final AccessibleRelation accessibleRelation = new AccessibleRelation(AccessibleRelation.CONTROLLED_BY, scrollBar);
            scrollBar.getAccessibleContext().getAccessibleRelationSet().add(new AccessibleRelation(AccessibleRelation.CONTROLLER_FOR, JScrollPane.this));
            this.getAccessibleRelationSet().add(accessibleRelation);
        }
    }
}
