package javax.swing.plaf.basic;

import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.UIManager;
import java.awt.Cursor;
import java.awt.LayoutManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import sun.swing.DefaultLookup;
import javax.swing.border.Border;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.awt.Container;

public class BasicSplitPaneDivider extends Container implements PropertyChangeListener
{
    protected static final int ONE_TOUCH_SIZE = 6;
    protected static final int ONE_TOUCH_OFFSET = 2;
    protected DragController dragger;
    protected BasicSplitPaneUI splitPaneUI;
    protected int dividerSize;
    protected Component hiddenDivider;
    protected JSplitPane splitPane;
    protected MouseHandler mouseHandler;
    protected int orientation;
    protected JButton leftButton;
    protected JButton rightButton;
    private Border border;
    private boolean mouseOver;
    private int oneTouchSize;
    private int oneTouchOffset;
    private boolean centerOneTouchButtons;
    
    public BasicSplitPaneDivider(final BasicSplitPaneUI basicSplitPaneUI) {
        this.dividerSize = 0;
        this.oneTouchSize = DefaultLookup.getInt(basicSplitPaneUI.getSplitPane(), basicSplitPaneUI, "SplitPane.oneTouchButtonSize", 6);
        this.oneTouchOffset = DefaultLookup.getInt(basicSplitPaneUI.getSplitPane(), basicSplitPaneUI, "SplitPane.oneTouchButtonOffset", 2);
        this.centerOneTouchButtons = DefaultLookup.getBoolean(basicSplitPaneUI.getSplitPane(), basicSplitPaneUI, "SplitPane.centerOneTouchButtons", true);
        this.setLayout(new DividerLayout());
        this.setBasicSplitPaneUI(basicSplitPaneUI);
        this.orientation = this.splitPane.getOrientation();
        this.setCursor((this.orientation == 1) ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
        this.setBackground(UIManager.getColor("SplitPane.background"));
    }
    
    private void revalidateSplitPane() {
        this.invalidate();
        if (this.splitPane != null) {
            this.splitPane.revalidate();
        }
    }
    
    public void setBasicSplitPaneUI(final BasicSplitPaneUI splitPaneUI) {
        if (this.splitPane != null) {
            this.splitPane.removePropertyChangeListener(this);
            if (this.mouseHandler != null) {
                this.splitPane.removeMouseListener(this.mouseHandler);
                this.splitPane.removeMouseMotionListener(this.mouseHandler);
                this.removeMouseListener(this.mouseHandler);
                this.removeMouseMotionListener(this.mouseHandler);
                this.mouseHandler = null;
            }
        }
        if ((this.splitPaneUI = splitPaneUI) != null) {
            this.splitPane = splitPaneUI.getSplitPane();
            if (this.splitPane != null) {
                if (this.mouseHandler == null) {
                    this.mouseHandler = new MouseHandler();
                }
                this.splitPane.addMouseListener(this.mouseHandler);
                this.splitPane.addMouseMotionListener(this.mouseHandler);
                this.addMouseListener(this.mouseHandler);
                this.addMouseMotionListener(this.mouseHandler);
                this.splitPane.addPropertyChangeListener(this);
                if (this.splitPane.isOneTouchExpandable()) {
                    this.oneTouchExpandableChanged();
                }
            }
        }
        else {
            this.splitPane = null;
        }
    }
    
    public BasicSplitPaneUI getBasicSplitPaneUI() {
        return this.splitPaneUI;
    }
    
    public void setDividerSize(final int dividerSize) {
        this.dividerSize = dividerSize;
    }
    
    public int getDividerSize() {
        return this.dividerSize;
    }
    
    public void setBorder(final Border border) {
        final Border border2 = this.border;
        this.border = border;
    }
    
    public Border getBorder() {
        return this.border;
    }
    
    @Override
    public Insets getInsets() {
        final Border border = this.getBorder();
        if (border != null) {
            return border.getBorderInsets(this);
        }
        return super.getInsets();
    }
    
    protected void setMouseOver(final boolean mouseOver) {
        this.mouseOver = mouseOver;
    }
    
    public boolean isMouseOver() {
        return this.mouseOver;
    }
    
    @Override
    public Dimension getPreferredSize() {
        if (this.orientation == 1) {
            return new Dimension(this.getDividerSize(), 1);
        }
        return new Dimension(1, this.getDividerSize());
    }
    
    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }
    
    @Override
    public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getSource() == this.splitPane) {
            if (propertyChangeEvent.getPropertyName() == "orientation") {
                this.orientation = this.splitPane.getOrientation();
                this.setCursor((this.orientation == 1) ? Cursor.getPredefinedCursor(11) : Cursor.getPredefinedCursor(9));
                this.revalidateSplitPane();
            }
            else if (propertyChangeEvent.getPropertyName() == "oneTouchExpandable") {
                this.oneTouchExpandableChanged();
            }
        }
    }
    
    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        final Border border = this.getBorder();
        if (border != null) {
            final Dimension size = this.getSize();
            border.paintBorder(this, graphics, 0, 0, size.width, size.height);
        }
    }
    
    protected void oneTouchExpandableChanged() {
        if (!DefaultLookup.getBoolean(this.splitPane, this.splitPaneUI, "SplitPane.supportsOneTouchButtons", true)) {
            return;
        }
        if (this.splitPane.isOneTouchExpandable() && this.leftButton == null && this.rightButton == null) {
            this.leftButton = this.createLeftOneTouchButton();
            if (this.leftButton != null) {
                this.leftButton.addActionListener(new OneTouchActionHandler(true));
            }
            this.rightButton = this.createRightOneTouchButton();
            if (this.rightButton != null) {
                this.rightButton.addActionListener(new OneTouchActionHandler(false));
            }
            if (this.leftButton != null && this.rightButton != null) {
                this.add(this.leftButton);
                this.add(this.rightButton);
            }
        }
        this.revalidateSplitPane();
    }
    
    protected JButton createLeftOneTouchButton() {
        final JButton button = new JButton() {
            @Override
            public void setBorder(final Border border) {
            }
            
            @Override
            public void paint(final Graphics graphics) {
                if (BasicSplitPaneDivider.this.splitPane != null) {
                    final int[] array = new int[3];
                    final int[] array2 = new int[3];
                    graphics.setColor(this.getBackground());
                    graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
                    graphics.setColor(Color.black);
                    if (BasicSplitPaneDivider.this.orientation == 0) {
                        final int min = Math.min(this.getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
                        array[0] = min;
                        array[1] = 0;
                        array[2] = min << 1;
                        array2[0] = 0;
                        array2[1] = (array2[2] = min);
                        graphics.drawPolygon(array, array2, 3);
                    }
                    else {
                        final int min2 = Math.min(this.getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
                        array[0] = (array[2] = min2);
                        array2[array[1] = 0] = 0;
                        array2[2] = (array2[1] = min2) << 1;
                    }
                    graphics.fillPolygon(array, array2, 3);
                }
            }
            
            @Override
            public boolean isFocusTraversable() {
                return false;
            }
        };
        button.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
        button.setCursor(Cursor.getPredefinedCursor(0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setRequestFocusEnabled(false);
        return button;
    }
    
    protected JButton createRightOneTouchButton() {
        final JButton button = new JButton() {
            @Override
            public void setBorder(final Border border) {
            }
            
            @Override
            public void paint(final Graphics graphics) {
                if (BasicSplitPaneDivider.this.splitPane != null) {
                    final int[] array = new int[3];
                    final int[] array2 = new int[3];
                    graphics.setColor(this.getBackground());
                    graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
                    if (BasicSplitPaneDivider.this.orientation == 0) {
                        final int min = Math.min(this.getHeight(), BasicSplitPaneDivider.this.oneTouchSize);
                        array[1] = (array[0] = min) << 1;
                        array2[array[2] = 0] = min;
                        array2[1] = (array2[2] = 0);
                    }
                    else {
                        final int min2 = Math.min(this.getWidth(), BasicSplitPaneDivider.this.oneTouchSize);
                        array[0] = (array[2] = 0);
                        array[1] = min2;
                        array2[0] = 0;
                        array2[2] = (array2[1] = min2) << 1;
                    }
                    graphics.setColor(Color.black);
                    graphics.fillPolygon(array, array2, 3);
                }
            }
            
            @Override
            public boolean isFocusTraversable() {
                return false;
            }
        };
        button.setMinimumSize(new Dimension(this.oneTouchSize, this.oneTouchSize));
        button.setCursor(Cursor.getPredefinedCursor(0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setRequestFocusEnabled(false);
        return button;
    }
    
    protected void prepareForDragging() {
        this.splitPaneUI.startDragging();
    }
    
    protected void dragDividerTo(final int n) {
        this.splitPaneUI.dragDividerTo(n);
    }
    
    protected void finishDraggingTo(final int n) {
        this.splitPaneUI.finishDraggingTo(n);
    }
    
    protected class MouseHandler extends MouseAdapter implements MouseMotionListener
    {
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if ((mouseEvent.getSource() == BasicSplitPaneDivider.this || mouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane) && BasicSplitPaneDivider.this.dragger == null && BasicSplitPaneDivider.this.splitPane.isEnabled()) {
                final Component nonContinuousLayoutDivider = BasicSplitPaneDivider.this.splitPaneUI.getNonContinuousLayoutDivider();
                if (BasicSplitPaneDivider.this.hiddenDivider != nonContinuousLayoutDivider) {
                    if (BasicSplitPaneDivider.this.hiddenDivider != null) {
                        BasicSplitPaneDivider.this.hiddenDivider.removeMouseListener(this);
                        BasicSplitPaneDivider.this.hiddenDivider.removeMouseMotionListener(this);
                    }
                    BasicSplitPaneDivider.this.hiddenDivider = nonContinuousLayoutDivider;
                    if (BasicSplitPaneDivider.this.hiddenDivider != null) {
                        BasicSplitPaneDivider.this.hiddenDivider.addMouseMotionListener(this);
                        BasicSplitPaneDivider.this.hiddenDivider.addMouseListener(this);
                    }
                }
                if (BasicSplitPaneDivider.this.splitPane.getLeftComponent() != null && BasicSplitPaneDivider.this.splitPane.getRightComponent() != null) {
                    if (BasicSplitPaneDivider.this.orientation == 1) {
                        BasicSplitPaneDivider.this.dragger = new DragController(mouseEvent);
                    }
                    else {
                        BasicSplitPaneDivider.this.dragger = new VerticalDragController(mouseEvent);
                    }
                    if (!BasicSplitPaneDivider.this.dragger.isValid()) {
                        BasicSplitPaneDivider.this.dragger = null;
                    }
                    else {
                        BasicSplitPaneDivider.this.prepareForDragging();
                        BasicSplitPaneDivider.this.dragger.continueDrag(mouseEvent);
                    }
                }
                mouseEvent.consume();
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (BasicSplitPaneDivider.this.dragger != null) {
                if (mouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane) {
                    BasicSplitPaneDivider.this.dragger.completeDrag(mouseEvent.getX(), mouseEvent.getY());
                }
                else if (mouseEvent.getSource() == BasicSplitPaneDivider.this) {
                    final Point location = BasicSplitPaneDivider.this.getLocation();
                    BasicSplitPaneDivider.this.dragger.completeDrag(mouseEvent.getX() + location.x, mouseEvent.getY() + location.y);
                }
                else if (mouseEvent.getSource() == BasicSplitPaneDivider.this.hiddenDivider) {
                    final Point location2 = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
                    BasicSplitPaneDivider.this.dragger.completeDrag(mouseEvent.getX() + location2.x, mouseEvent.getY() + location2.y);
                }
                BasicSplitPaneDivider.this.dragger = null;
                mouseEvent.consume();
            }
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (BasicSplitPaneDivider.this.dragger != null) {
                if (mouseEvent.getSource() == BasicSplitPaneDivider.this.splitPane) {
                    BasicSplitPaneDivider.this.dragger.continueDrag(mouseEvent.getX(), mouseEvent.getY());
                }
                else if (mouseEvent.getSource() == BasicSplitPaneDivider.this) {
                    final Point location = BasicSplitPaneDivider.this.getLocation();
                    BasicSplitPaneDivider.this.dragger.continueDrag(mouseEvent.getX() + location.x, mouseEvent.getY() + location.y);
                }
                else if (mouseEvent.getSource() == BasicSplitPaneDivider.this.hiddenDivider) {
                    final Point location2 = BasicSplitPaneDivider.this.hiddenDivider.getLocation();
                    BasicSplitPaneDivider.this.dragger.continueDrag(mouseEvent.getX() + location2.x, mouseEvent.getY() + location2.y);
                }
                mouseEvent.consume();
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
        
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() == BasicSplitPaneDivider.this) {
                BasicSplitPaneDivider.this.setMouseOver(true);
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (mouseEvent.getSource() == BasicSplitPaneDivider.this) {
                BasicSplitPaneDivider.this.setMouseOver(false);
            }
        }
    }
    
    protected class DragController
    {
        int initialX;
        int maxX;
        int minX;
        int offset;
        
        protected DragController(final MouseEvent mouseEvent) {
            final JSplitPane splitPane = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
            final Component leftComponent = splitPane.getLeftComponent();
            final Component rightComponent = splitPane.getRightComponent();
            this.initialX = BasicSplitPaneDivider.this.getLocation().x;
            if (mouseEvent.getSource() == BasicSplitPaneDivider.this) {
                this.offset = mouseEvent.getX();
            }
            else {
                this.offset = mouseEvent.getX() - this.initialX;
            }
            if (leftComponent == null || rightComponent == null || this.offset < -1 || this.offset >= BasicSplitPaneDivider.this.getSize().width) {
                this.maxX = -1;
            }
            else {
                final Insets insets = splitPane.getInsets();
                if (leftComponent.isVisible()) {
                    this.minX = leftComponent.getMinimumSize().width;
                    if (insets != null) {
                        this.minX += insets.left;
                    }
                }
                else {
                    this.minX = 0;
                }
                if (rightComponent.isVisible()) {
                    this.maxX = Math.max(0, splitPane.getSize().width - (BasicSplitPaneDivider.this.getSize().width + ((insets != null) ? insets.right : 0)) - rightComponent.getMinimumSize().width);
                }
                else {
                    this.maxX = Math.max(0, splitPane.getSize().width - (BasicSplitPaneDivider.this.getSize().width + ((insets != null) ? insets.right : 0)));
                }
                if (this.maxX < this.minX) {
                    final int n = 0;
                    this.maxX = n;
                    this.minX = n;
                }
            }
        }
        
        protected boolean isValid() {
            return this.maxX > 0;
        }
        
        protected int positionForMouseEvent(final MouseEvent mouseEvent) {
            return Math.min(this.maxX, Math.max(this.minX, ((mouseEvent.getSource() == BasicSplitPaneDivider.this) ? (mouseEvent.getX() + BasicSplitPaneDivider.this.getLocation().x) : mouseEvent.getX()) - this.offset));
        }
        
        protected int getNeededLocation(final int n, final int n2) {
            return Math.min(this.maxX, Math.max(this.minX, n - this.offset));
        }
        
        protected void continueDrag(final int n, final int n2) {
            BasicSplitPaneDivider.this.dragDividerTo(this.getNeededLocation(n, n2));
        }
        
        protected void continueDrag(final MouseEvent mouseEvent) {
            BasicSplitPaneDivider.this.dragDividerTo(this.positionForMouseEvent(mouseEvent));
        }
        
        protected void completeDrag(final int n, final int n2) {
            BasicSplitPaneDivider.this.finishDraggingTo(this.getNeededLocation(n, n2));
        }
        
        protected void completeDrag(final MouseEvent mouseEvent) {
            BasicSplitPaneDivider.this.finishDraggingTo(this.positionForMouseEvent(mouseEvent));
        }
    }
    
    protected class VerticalDragController extends DragController
    {
        protected VerticalDragController(final MouseEvent mouseEvent) {
            super(mouseEvent);
            final JSplitPane splitPane = BasicSplitPaneDivider.this.splitPaneUI.getSplitPane();
            final Component leftComponent = splitPane.getLeftComponent();
            final Component rightComponent = splitPane.getRightComponent();
            this.initialX = BasicSplitPaneDivider.this.getLocation().y;
            if (mouseEvent.getSource() == BasicSplitPaneDivider.this) {
                this.offset = mouseEvent.getY();
            }
            else {
                this.offset = mouseEvent.getY() - this.initialX;
            }
            if (leftComponent == null || rightComponent == null || this.offset < -1 || this.offset > BasicSplitPaneDivider.this.getSize().height) {
                this.maxX = -1;
            }
            else {
                final Insets insets = splitPane.getInsets();
                if (leftComponent.isVisible()) {
                    this.minX = leftComponent.getMinimumSize().height;
                    if (insets != null) {
                        this.minX += insets.top;
                    }
                }
                else {
                    this.minX = 0;
                }
                if (rightComponent.isVisible()) {
                    this.maxX = Math.max(0, splitPane.getSize().height - (BasicSplitPaneDivider.this.getSize().height + ((insets != null) ? insets.bottom : 0)) - rightComponent.getMinimumSize().height);
                }
                else {
                    this.maxX = Math.max(0, splitPane.getSize().height - (BasicSplitPaneDivider.this.getSize().height + ((insets != null) ? insets.bottom : 0)));
                }
                if (this.maxX < this.minX) {
                    final int n = 0;
                    this.maxX = n;
                    this.minX = n;
                }
            }
        }
        
        @Override
        protected int getNeededLocation(final int n, final int n2) {
            return Math.min(this.maxX, Math.max(this.minX, n2 - this.offset));
        }
        
        @Override
        protected int positionForMouseEvent(final MouseEvent mouseEvent) {
            return Math.min(this.maxX, Math.max(this.minX, ((mouseEvent.getSource() == BasicSplitPaneDivider.this) ? (mouseEvent.getY() + BasicSplitPaneDivider.this.getLocation().y) : mouseEvent.getY()) - this.offset));
        }
    }
    
    protected class DividerLayout implements LayoutManager
    {
        @Override
        public void layoutContainer(final Container container) {
            if (BasicSplitPaneDivider.this.leftButton != null && BasicSplitPaneDivider.this.rightButton != null && container == BasicSplitPaneDivider.this) {
                if (BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable()) {
                    final Insets insets = BasicSplitPaneDivider.this.getInsets();
                    if (BasicSplitPaneDivider.this.orientation == 0) {
                        int n = (insets != null) ? insets.left : 0;
                        int n2 = BasicSplitPaneDivider.this.getHeight();
                        if (insets != null) {
                            n2 = Math.max(n2 - (insets.top + insets.bottom), 0);
                        }
                        final int min = Math.min(n2, BasicSplitPaneDivider.this.oneTouchSize);
                        int n3 = (container.getSize().height - min) / 2;
                        if (!BasicSplitPaneDivider.this.centerOneTouchButtons) {
                            n3 = ((insets != null) ? insets.top : 0);
                            n = 0;
                        }
                        BasicSplitPaneDivider.this.leftButton.setBounds(n + BasicSplitPaneDivider.this.oneTouchOffset, n3, min * 2, min);
                        BasicSplitPaneDivider.this.rightButton.setBounds(n + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, n3, min * 2, min);
                    }
                    else {
                        int n4 = (insets != null) ? insets.top : 0;
                        int n5 = BasicSplitPaneDivider.this.getWidth();
                        if (insets != null) {
                            n5 = Math.max(n5 - (insets.left + insets.right), 0);
                        }
                        final int min2 = Math.min(n5, BasicSplitPaneDivider.this.oneTouchSize);
                        int n6 = (container.getSize().width - min2) / 2;
                        if (!BasicSplitPaneDivider.this.centerOneTouchButtons) {
                            n6 = ((insets != null) ? insets.left : 0);
                            n4 = 0;
                        }
                        BasicSplitPaneDivider.this.leftButton.setBounds(n6, n4 + BasicSplitPaneDivider.this.oneTouchOffset, min2, min2 * 2);
                        BasicSplitPaneDivider.this.rightButton.setBounds(n6, n4 + BasicSplitPaneDivider.this.oneTouchOffset + BasicSplitPaneDivider.this.oneTouchSize * 2, min2, min2 * 2);
                    }
                }
                else {
                    BasicSplitPaneDivider.this.leftButton.setBounds(-5, -5, 1, 1);
                    BasicSplitPaneDivider.this.rightButton.setBounds(-5, -5, 1, 1);
                }
            }
        }
        
        @Override
        public Dimension minimumLayoutSize(final Container container) {
            if (container != BasicSplitPaneDivider.this || BasicSplitPaneDivider.this.splitPane == null) {
                return new Dimension(0, 0);
            }
            Dimension minimumSize = null;
            if (BasicSplitPaneDivider.this.splitPane.isOneTouchExpandable() && BasicSplitPaneDivider.this.leftButton != null) {
                minimumSize = BasicSplitPaneDivider.this.leftButton.getMinimumSize();
            }
            final Insets insets = BasicSplitPaneDivider.this.getInsets();
            int n;
            int max = n = BasicSplitPaneDivider.this.getDividerSize();
            if (BasicSplitPaneDivider.this.orientation == 0) {
                if (minimumSize != null) {
                    int height = minimumSize.height;
                    if (insets != null) {
                        height += insets.top + insets.bottom;
                    }
                    n = Math.max(n, height);
                }
                max = 1;
            }
            else {
                if (minimumSize != null) {
                    int width = minimumSize.width;
                    if (insets != null) {
                        width += insets.left + insets.right;
                    }
                    max = Math.max(max, width);
                }
                n = 1;
            }
            return new Dimension(max, n);
        }
        
        @Override
        public Dimension preferredLayoutSize(final Container container) {
            return this.minimumLayoutSize(container);
        }
        
        @Override
        public void removeLayoutComponent(final Component component) {
        }
        
        @Override
        public void addLayoutComponent(final String s, final Component component) {
        }
    }
    
    private class OneTouchActionHandler implements ActionListener
    {
        private boolean toMinimum;
        
        OneTouchActionHandler(final boolean toMinimum) {
            this.toMinimum = toMinimum;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final Insets insets = BasicSplitPaneDivider.this.splitPane.getInsets();
            final int lastDividerLocation = BasicSplitPaneDivider.this.splitPane.getLastDividerLocation();
            final int dividerLocation = BasicSplitPaneDivider.this.splitPaneUI.getDividerLocation(BasicSplitPaneDivider.this.splitPane);
            int dividerLocation2;
            if (this.toMinimum) {
                if (BasicSplitPaneDivider.this.orientation == 0) {
                    if (dividerLocation >= BasicSplitPaneDivider.this.splitPane.getHeight() - insets.bottom - BasicSplitPaneDivider.this.getHeight()) {
                        dividerLocation2 = Math.min(lastDividerLocation, BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation());
                        BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
                    }
                    else {
                        dividerLocation2 = insets.top;
                        BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
                    }
                }
                else if (dividerLocation >= BasicSplitPaneDivider.this.splitPane.getWidth() - insets.right - BasicSplitPaneDivider.this.getWidth()) {
                    dividerLocation2 = Math.min(lastDividerLocation, BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation());
                    BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
                }
                else {
                    dividerLocation2 = insets.left;
                    BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
                }
            }
            else if (BasicSplitPaneDivider.this.orientation == 0) {
                if (dividerLocation == insets.top) {
                    dividerLocation2 = Math.min(lastDividerLocation, BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation());
                    BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
                }
                else {
                    dividerLocation2 = BasicSplitPaneDivider.this.splitPane.getHeight() - BasicSplitPaneDivider.this.getHeight() - insets.top;
                    BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
                }
            }
            else if (dividerLocation == insets.left) {
                dividerLocation2 = Math.min(lastDividerLocation, BasicSplitPaneDivider.this.splitPane.getMaximumDividerLocation());
                BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(false);
            }
            else {
                dividerLocation2 = BasicSplitPaneDivider.this.splitPane.getWidth() - BasicSplitPaneDivider.this.getWidth() - insets.left;
                BasicSplitPaneDivider.this.splitPaneUI.setKeepHidden(true);
            }
            if (dividerLocation != dividerLocation2) {
                BasicSplitPaneDivider.this.splitPane.setDividerLocation(dividerLocation2);
                BasicSplitPaneDivider.this.splitPane.setLastDividerLocation(dividerLocation);
            }
        }
    }
}
