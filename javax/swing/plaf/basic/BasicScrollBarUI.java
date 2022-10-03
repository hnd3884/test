package javax.swing.plaf.basic;

import java.awt.event.FocusEvent;
import sun.swing.UIAction;
import java.beans.PropertyChangeEvent;
import java.awt.event.ActionEvent;
import javax.swing.JViewport;
import java.awt.Point;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.BoundedRangeModel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.event.ChangeEvent;
import java.awt.Container;
import sun.swing.SwingUtilities2;
import java.awt.Insets;
import java.awt.Graphics;
import sun.swing.DefaultLookup;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.Action;
import javax.swing.Timer;
import java.beans.PropertyChangeListener;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JScrollBar;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.SwingConstants;
import java.awt.LayoutManager;
import javax.swing.plaf.ScrollBarUI;

public class BasicScrollBarUI extends ScrollBarUI implements LayoutManager, SwingConstants
{
    private static final int POSITIVE_SCROLL = 1;
    private static final int NEGATIVE_SCROLL = -1;
    private static final int MIN_SCROLL = 2;
    private static final int MAX_SCROLL = 3;
    protected Dimension minimumThumbSize;
    protected Dimension maximumThumbSize;
    protected Color thumbHighlightColor;
    protected Color thumbLightShadowColor;
    protected Color thumbDarkShadowColor;
    protected Color thumbColor;
    protected Color trackColor;
    protected Color trackHighlightColor;
    protected JScrollBar scrollbar;
    protected JButton incrButton;
    protected JButton decrButton;
    protected boolean isDragging;
    protected TrackListener trackListener;
    protected ArrowButtonListener buttonListener;
    protected ModelListener modelListener;
    protected Rectangle thumbRect;
    protected Rectangle trackRect;
    protected int trackHighlight;
    protected static final int NO_HIGHLIGHT = 0;
    protected static final int DECREASE_HIGHLIGHT = 1;
    protected static final int INCREASE_HIGHLIGHT = 2;
    protected ScrollListener scrollListener;
    protected PropertyChangeListener propertyChangeListener;
    protected Timer scrollTimer;
    private static final int scrollSpeedThrottle = 60;
    private boolean supportsAbsolutePositioning;
    protected int scrollBarWidth;
    private Handler handler;
    private boolean thumbActive;
    private boolean useCachedValue;
    private int scrollBarValue;
    protected int incrGap;
    protected int decrGap;
    
    public BasicScrollBarUI() {
        this.useCachedValue = false;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("positiveUnitIncrement"));
        lazyActionMap.put(new Actions("positiveBlockIncrement"));
        lazyActionMap.put(new Actions("negativeUnitIncrement"));
        lazyActionMap.put(new Actions("negativeBlockIncrement"));
        lazyActionMap.put(new Actions("minScroll"));
        lazyActionMap.put(new Actions("maxScroll"));
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicScrollBarUI();
    }
    
    protected void configureScrollBarColors() {
        LookAndFeel.installColors(this.scrollbar, "ScrollBar.background", "ScrollBar.foreground");
        this.thumbHighlightColor = UIManager.getColor("ScrollBar.thumbHighlight");
        this.thumbLightShadowColor = UIManager.getColor("ScrollBar.thumbShadow");
        this.thumbDarkShadowColor = UIManager.getColor("ScrollBar.thumbDarkShadow");
        this.thumbColor = UIManager.getColor("ScrollBar.thumb");
        this.trackColor = UIManager.getColor("ScrollBar.track");
        this.trackHighlightColor = UIManager.getColor("ScrollBar.trackHighlight");
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.scrollbar = (JScrollBar)component;
        this.thumbRect = new Rectangle(0, 0, 0, 0);
        this.trackRect = new Rectangle(0, 0, 0, 0);
        this.installDefaults();
        this.installComponents();
        this.installListeners();
        this.installKeyboardActions();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        this.scrollbar = (JScrollBar)component;
        this.uninstallListeners();
        this.uninstallDefaults();
        this.uninstallComponents();
        this.uninstallKeyboardActions();
        this.thumbRect = null;
        this.scrollbar = null;
        this.incrButton = null;
        this.decrButton = null;
    }
    
    protected void installDefaults() {
        this.scrollBarWidth = UIManager.getInt("ScrollBar.width");
        if (this.scrollBarWidth <= 0) {
            this.scrollBarWidth = 16;
        }
        this.minimumThumbSize = (Dimension)UIManager.get("ScrollBar.minimumThumbSize");
        this.maximumThumbSize = (Dimension)UIManager.get("ScrollBar.maximumThumbSize");
        final Boolean b = (Boolean)UIManager.get("ScrollBar.allowsAbsolutePositioning");
        this.supportsAbsolutePositioning = (b != null && b);
        this.trackHighlight = 0;
        if (this.scrollbar.getLayout() == null || this.scrollbar.getLayout() instanceof UIResource) {
            this.scrollbar.setLayout(this);
        }
        this.configureScrollBarColors();
        LookAndFeel.installBorder(this.scrollbar, "ScrollBar.border");
        LookAndFeel.installProperty(this.scrollbar, "opaque", Boolean.TRUE);
        this.scrollBarValue = this.scrollbar.getValue();
        this.incrGap = UIManager.getInt("ScrollBar.incrementButtonGap");
        this.decrGap = UIManager.getInt("ScrollBar.decrementButtonGap");
        final String s = (String)this.scrollbar.getClientProperty("JComponent.sizeVariant");
        if (s != null) {
            if ("large".equals(s)) {
                this.scrollBarWidth *= (int)1.15;
                this.incrGap *= (int)1.15;
                this.decrGap *= (int)1.15;
            }
            else if ("small".equals(s)) {
                this.scrollBarWidth *= (int)0.857;
                this.incrGap *= (int)0.857;
                this.decrGap *= (int)0.714;
            }
            else if ("mini".equals(s)) {
                this.scrollBarWidth *= (int)0.714;
                this.incrGap *= (int)0.714;
                this.decrGap *= (int)0.714;
            }
        }
    }
    
    protected void installComponents() {
        switch (this.scrollbar.getOrientation()) {
            case 1: {
                this.incrButton = this.createIncreaseButton(5);
                this.decrButton = this.createDecreaseButton(1);
                break;
            }
            case 0: {
                if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
                    this.incrButton = this.createIncreaseButton(3);
                    this.decrButton = this.createDecreaseButton(7);
                    break;
                }
                this.incrButton = this.createIncreaseButton(7);
                this.decrButton = this.createDecreaseButton(3);
                break;
            }
        }
        this.scrollbar.add(this.incrButton);
        this.scrollbar.add(this.decrButton);
        this.scrollbar.setEnabled(this.scrollbar.isEnabled());
    }
    
    protected void uninstallComponents() {
        this.scrollbar.remove(this.incrButton);
        this.scrollbar.remove(this.decrButton);
    }
    
    protected void installListeners() {
        this.trackListener = this.createTrackListener();
        this.buttonListener = this.createArrowButtonListener();
        this.modelListener = this.createModelListener();
        this.propertyChangeListener = this.createPropertyChangeListener();
        this.scrollbar.addMouseListener(this.trackListener);
        this.scrollbar.addMouseMotionListener(this.trackListener);
        this.scrollbar.getModel().addChangeListener(this.modelListener);
        this.scrollbar.addPropertyChangeListener(this.propertyChangeListener);
        this.scrollbar.addFocusListener(this.getHandler());
        if (this.incrButton != null) {
            this.incrButton.addMouseListener(this.buttonListener);
        }
        if (this.decrButton != null) {
            this.decrButton.addMouseListener(this.buttonListener);
        }
        this.scrollListener = this.createScrollListener();
        (this.scrollTimer = new Timer(60, this.scrollListener)).setInitialDelay(300);
    }
    
    protected void installKeyboardActions() {
        LazyActionMap.installLazyActionMap(this.scrollbar, BasicScrollBarUI.class, "ScrollBar.actionMap");
        SwingUtilities.replaceUIInputMap(this.scrollbar, 0, this.getInputMap(0));
        SwingUtilities.replaceUIInputMap(this.scrollbar, 1, this.getInputMap(1));
    }
    
    protected void uninstallKeyboardActions() {
        SwingUtilities.replaceUIInputMap(this.scrollbar, 0, null);
        SwingUtilities.replaceUIActionMap(this.scrollbar, null);
    }
    
    private InputMap getInputMap(final int n) {
        if (n == 0) {
            final InputMap parent = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap");
            final InputMap inputMap;
            if (this.scrollbar.getComponentOrientation().isLeftToRight() || (inputMap = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.focusInputMap.RightToLeft")) == null) {
                return parent;
            }
            inputMap.setParent(parent);
            return inputMap;
        }
        else {
            if (n != 1) {
                return null;
            }
            final InputMap parent2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap");
            final InputMap inputMap2;
            if (this.scrollbar.getComponentOrientation().isLeftToRight() || (inputMap2 = (InputMap)DefaultLookup.get(this.scrollbar, this, "ScrollBar.ancestorInputMap.RightToLeft")) == null) {
                return parent2;
            }
            inputMap2.setParent(parent2);
            return inputMap2;
        }
    }
    
    protected void uninstallListeners() {
        this.scrollTimer.stop();
        this.scrollTimer = null;
        if (this.decrButton != null) {
            this.decrButton.removeMouseListener(this.buttonListener);
        }
        if (this.incrButton != null) {
            this.incrButton.removeMouseListener(this.buttonListener);
        }
        this.scrollbar.getModel().removeChangeListener(this.modelListener);
        this.scrollbar.removeMouseListener(this.trackListener);
        this.scrollbar.removeMouseMotionListener(this.trackListener);
        this.scrollbar.removePropertyChangeListener(this.propertyChangeListener);
        this.scrollbar.removeFocusListener(this.getHandler());
        this.handler = null;
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.scrollbar);
        if (this.scrollbar.getLayout() == this) {
            this.scrollbar.setLayout(null);
        }
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected TrackListener createTrackListener() {
        return new TrackListener();
    }
    
    protected ArrowButtonListener createArrowButtonListener() {
        return new ArrowButtonListener();
    }
    
    protected ModelListener createModelListener() {
        return new ModelListener();
    }
    
    protected ScrollListener createScrollListener() {
        return new ScrollListener();
    }
    
    protected PropertyChangeListener createPropertyChangeListener() {
        return this.getHandler();
    }
    
    private void updateThumbState(final int n, final int n2) {
        this.setThumbRollover(this.getThumbBounds().contains(n, n2));
    }
    
    protected void setThumbRollover(final boolean thumbActive) {
        if (this.thumbActive != thumbActive) {
            this.thumbActive = thumbActive;
            this.scrollbar.repaint(this.getThumbBounds());
        }
    }
    
    public boolean isThumbRollover() {
        return this.thumbActive;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        this.paintTrack(graphics, component, this.getTrackBounds());
        final Rectangle thumbBounds = this.getThumbBounds();
        if (thumbBounds.intersects(graphics.getClipBounds())) {
            this.paintThumb(graphics, component, thumbBounds);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return (this.scrollbar.getOrientation() == 1) ? new Dimension(this.scrollBarWidth, 48) : new Dimension(48, this.scrollBarWidth);
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    protected JButton createDecreaseButton(final int n) {
        return new BasicArrowButton(n, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
    }
    
    protected JButton createIncreaseButton(final int n) {
        return new BasicArrowButton(n, UIManager.getColor("ScrollBar.thumb"), UIManager.getColor("ScrollBar.thumbShadow"), UIManager.getColor("ScrollBar.thumbDarkShadow"), UIManager.getColor("ScrollBar.thumbHighlight"));
    }
    
    protected void paintDecreaseHighlight(final Graphics graphics) {
        final Insets insets = this.scrollbar.getInsets();
        final Rectangle thumbBounds = this.getThumbBounds();
        graphics.setColor(this.trackHighlightColor);
        if (this.scrollbar.getOrientation() == 1) {
            final int left = insets.left;
            final int y = this.trackRect.y;
            graphics.fillRect(left, y, this.scrollbar.getWidth() - (insets.left + insets.right), thumbBounds.y - y);
        }
        else {
            int x;
            int n;
            if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
                x = this.trackRect.x;
                n = thumbBounds.x - x;
            }
            else {
                x = thumbBounds.x + thumbBounds.width;
                n = this.trackRect.x + this.trackRect.width - x;
            }
            graphics.fillRect(x, insets.top, n, this.scrollbar.getHeight() - (insets.top + insets.bottom));
        }
    }
    
    protected void paintIncreaseHighlight(final Graphics graphics) {
        final Insets insets = this.scrollbar.getInsets();
        final Rectangle thumbBounds = this.getThumbBounds();
        graphics.setColor(this.trackHighlightColor);
        if (this.scrollbar.getOrientation() == 1) {
            final int left = insets.left;
            final int n = thumbBounds.y + thumbBounds.height;
            graphics.fillRect(left, n, this.scrollbar.getWidth() - (insets.left + insets.right), this.trackRect.y + this.trackRect.height - n);
        }
        else {
            int x;
            int n2;
            if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
                x = thumbBounds.x + thumbBounds.width;
                n2 = this.trackRect.x + this.trackRect.width - x;
            }
            else {
                x = this.trackRect.x;
                n2 = thumbBounds.x - x;
            }
            graphics.fillRect(x, insets.top, n2, this.scrollbar.getHeight() - (insets.top + insets.bottom));
        }
    }
    
    protected void paintTrack(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        graphics.setColor(this.trackColor);
        graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        if (this.trackHighlight == 1) {
            this.paintDecreaseHighlight(graphics);
        }
        else if (this.trackHighlight == 2) {
            this.paintIncreaseHighlight(graphics);
        }
    }
    
    protected void paintThumb(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        if (rectangle.isEmpty() || !this.scrollbar.isEnabled()) {
            return;
        }
        final int width = rectangle.width;
        final int height = rectangle.height;
        graphics.translate(rectangle.x, rectangle.y);
        graphics.setColor(this.thumbDarkShadowColor);
        SwingUtilities2.drawRect(graphics, 0, 0, width - 1, height - 1);
        graphics.setColor(this.thumbColor);
        graphics.fillRect(0, 0, width - 1, height - 1);
        graphics.setColor(this.thumbHighlightColor);
        SwingUtilities2.drawVLine(graphics, 1, 1, height - 2);
        SwingUtilities2.drawHLine(graphics, 2, width - 3, 1);
        graphics.setColor(this.thumbLightShadowColor);
        SwingUtilities2.drawHLine(graphics, 2, width - 2, height - 2);
        SwingUtilities2.drawVLine(graphics, width - 2, 1, height - 3);
        graphics.translate(-rectangle.x, -rectangle.y);
    }
    
    protected Dimension getMinimumThumbSize() {
        return this.minimumThumbSize;
    }
    
    protected Dimension getMaximumThumbSize() {
        return this.maximumThumbSize;
    }
    
    @Override
    public void addLayoutComponent(final String s, final Component component) {
    }
    
    @Override
    public void removeLayoutComponent(final Component component) {
    }
    
    @Override
    public Dimension preferredLayoutSize(final Container container) {
        return this.getPreferredSize((JComponent)container);
    }
    
    @Override
    public Dimension minimumLayoutSize(final Container container) {
        return this.getMinimumSize((JComponent)container);
    }
    
    private int getValue(final JScrollBar scrollBar) {
        return this.useCachedValue ? this.scrollBarValue : scrollBar.getValue();
    }
    
    protected void layoutVScrollbar(final JScrollBar scrollBar) {
        final Dimension size = scrollBar.getSize();
        final Insets insets = scrollBar.getInsets();
        final int n = size.width - (insets.left + insets.right);
        final int left = insets.left;
        final boolean boolean1 = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
        int n2 = boolean1 ? n : this.decrButton.getPreferredSize().height;
        final int top = insets.top;
        int n3 = boolean1 ? n : this.incrButton.getPreferredSize().height;
        int n4 = size.height - (insets.bottom + n3);
        final int n5 = insets.top + insets.bottom;
        final int n6 = n2 + n3;
        final float n7 = (float)(size.height - (n5 + n6) - (this.decrGap + this.incrGap));
        final float n8 = (float)scrollBar.getMinimum();
        final float n9 = (float)scrollBar.getVisibleAmount();
        final float n10 = scrollBar.getMaximum() - n8;
        final float n11 = (float)this.getValue(scrollBar);
        final int min = Math.min(Math.max((n10 <= 0.0f) ? this.getMaximumThumbSize().height : ((int)(n7 * (n9 / n10))), this.getMinimumThumbSize().height), this.getMaximumThumbSize().height);
        int n12 = n4 - this.incrGap - min;
        if (n11 < scrollBar.getMaximum() - scrollBar.getVisibleAmount()) {
            n12 = (int)(0.5f + (n7 - min) * ((n11 - n8) / (n10 - n9))) + (top + n2 + this.decrGap);
        }
        final int n13 = size.height - n5;
        if (n13 < n6) {
            n2 = (n3 = n13 / 2);
            n4 = size.height - (insets.bottom + n3);
        }
        this.decrButton.setBounds(left, top, n, n2);
        this.incrButton.setBounds(left, n4, n, n3);
        final int n14 = top + n2 + this.decrGap;
        final int n15 = n4 - this.incrGap - n14;
        this.trackRect.setBounds(left, n14, n, n15);
        if (min >= (int)n7) {
            if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
                this.setThumbBounds(left, n14, n, n15);
            }
            else {
                this.setThumbBounds(0, 0, 0, 0);
            }
        }
        else {
            if (n12 + min > n4 - this.incrGap) {
                n12 = n4 - this.incrGap - min;
            }
            if (n12 < top + n2 + this.decrGap) {
                n12 = top + n2 + this.decrGap + 1;
            }
            this.setThumbBounds(left, n12, n, min);
        }
    }
    
    protected void layoutHScrollbar(final JScrollBar scrollBar) {
        final Dimension size = scrollBar.getSize();
        final Insets insets = scrollBar.getInsets();
        final int n = size.height - (insets.top + insets.bottom);
        final int top = insets.top;
        final boolean leftToRight = scrollBar.getComponentOrientation().isLeftToRight();
        final boolean boolean1 = DefaultLookup.getBoolean(this.scrollbar, this, "ScrollBar.squareButtons", false);
        int n2 = boolean1 ? n : this.decrButton.getPreferredSize().width;
        int n3 = boolean1 ? n : this.incrButton.getPreferredSize().width;
        if (!leftToRight) {
            final int n4 = n2;
            n2 = n3;
            n3 = n4;
        }
        final int left = insets.left;
        int n5 = size.width - (insets.right + n3);
        final int n6 = leftToRight ? this.decrGap : this.incrGap;
        final int n7 = leftToRight ? this.incrGap : this.decrGap;
        final int n8 = insets.left + insets.right;
        final int n9 = n2 + n3;
        final float n10 = (float)(size.width - (n8 + n9) - (n6 + n7));
        final float n11 = (float)scrollBar.getMinimum();
        final float n12 = (float)scrollBar.getMaximum();
        final float n13 = (float)scrollBar.getVisibleAmount();
        final float n14 = n12 - n11;
        final float n15 = (float)this.getValue(scrollBar);
        final int min = Math.min(Math.max((n14 <= 0.0f) ? this.getMaximumThumbSize().width : ((int)(n10 * (n13 / n14))), this.getMinimumThumbSize().width), this.getMaximumThumbSize().width);
        int n16 = leftToRight ? (n5 - n7 - min) : (left + n2 + n6);
        if (n15 < n12 - scrollBar.getVisibleAmount()) {
            final float n17 = n10 - min;
            int n18;
            if (leftToRight) {
                n18 = (int)(0.5f + n17 * ((n15 - n11) / (n14 - n13)));
            }
            else {
                n18 = (int)(0.5f + n17 * ((n12 - n13 - n15) / (n14 - n13)));
            }
            n16 = n18 + (left + n2 + n6);
        }
        final int n19 = size.width - n8;
        if (n19 < n9) {
            n2 = (n3 = n19 / 2);
            n5 = size.width - (insets.right + n3 + n7);
        }
        (leftToRight ? this.decrButton : this.incrButton).setBounds(left, top, n2, n);
        (leftToRight ? this.incrButton : this.decrButton).setBounds(n5, top, n3, n);
        final int n20 = left + n2 + n6;
        final int n21 = n5 - n7 - n20;
        this.trackRect.setBounds(n20, top, n21, n);
        if (min >= (int)n10) {
            if (UIManager.getBoolean("ScrollBar.alwaysShowThumb")) {
                this.setThumbBounds(n20, top, n21, n);
            }
            else {
                this.setThumbBounds(0, 0, 0, 0);
            }
        }
        else {
            if (n16 + min > n5 - n7) {
                n16 = n5 - n7 - min;
            }
            if (n16 < left + n2 + n6) {
                n16 = left + n2 + n6 + 1;
            }
            this.setThumbBounds(n16, top, min, n);
        }
    }
    
    @Override
    public void layoutContainer(final Container container) {
        if (this.isDragging) {
            return;
        }
        final JScrollBar scrollBar = (JScrollBar)container;
        switch (scrollBar.getOrientation()) {
            case 1: {
                this.layoutVScrollbar(scrollBar);
                break;
            }
            case 0: {
                this.layoutHScrollbar(scrollBar);
                break;
            }
        }
    }
    
    protected void setThumbBounds(final int n, final int n2, final int n3, final int n4) {
        if (this.thumbRect.x == n && this.thumbRect.y == n2 && this.thumbRect.width == n3 && this.thumbRect.height == n4) {
            return;
        }
        final int min = Math.min(n, this.thumbRect.x);
        final int min2 = Math.min(n2, this.thumbRect.y);
        final int max = Math.max(n + n3, this.thumbRect.x + this.thumbRect.width);
        final int max2 = Math.max(n2 + n4, this.thumbRect.y + this.thumbRect.height);
        this.thumbRect.setBounds(n, n2, n3, n4);
        this.scrollbar.repaint(min, min2, max - min, max2 - min2);
        this.setThumbRollover(false);
    }
    
    protected Rectangle getThumbBounds() {
        return this.thumbRect;
    }
    
    protected Rectangle getTrackBounds() {
        return this.trackRect;
    }
    
    static void scrollByBlock(final JScrollBar scrollBar, final int n) {
        final int value = scrollBar.getValue();
        final int n2 = scrollBar.getBlockIncrement(n) * ((n > 0) ? 1 : -1);
        int value2 = value + n2;
        if (n2 > 0 && value2 < value) {
            value2 = scrollBar.getMaximum();
        }
        else if (n2 < 0 && value2 > value) {
            value2 = scrollBar.getMinimum();
        }
        scrollBar.setValue(value2);
    }
    
    protected void scrollByBlock(final int n) {
        scrollByBlock(this.scrollbar, n);
        this.trackHighlight = ((n > 0) ? 2 : 1);
        final Rectangle trackBounds = this.getTrackBounds();
        this.scrollbar.repaint(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
    
    static void scrollByUnits(final JScrollBar scrollBar, final int n, final int n2, final boolean b) {
        int n3 = -1;
        if (b) {
            if (n < 0) {
                n3 = scrollBar.getValue() - scrollBar.getBlockIncrement(n);
            }
            else {
                n3 = scrollBar.getValue() + scrollBar.getBlockIncrement(n);
            }
        }
        for (int i = 0; i < n2; ++i) {
            int unitIncrement;
            if (n > 0) {
                unitIncrement = scrollBar.getUnitIncrement(n);
            }
            else {
                unitIncrement = -scrollBar.getUnitIncrement(n);
            }
            final int value = scrollBar.getValue();
            int value2 = value + unitIncrement;
            if (unitIncrement > 0 && value2 < value) {
                value2 = scrollBar.getMaximum();
            }
            else if (unitIncrement < 0 && value2 > value) {
                value2 = scrollBar.getMinimum();
            }
            if (value == value2) {
                break;
            }
            if (b && i > 0) {
                assert n3 != -1;
                if (n < 0 && value2 < n3) {
                    break;
                }
                if (n > 0 && value2 > n3) {
                    break;
                }
            }
            scrollBar.setValue(value2);
        }
    }
    
    protected void scrollByUnit(final int n) {
        scrollByUnits(this.scrollbar, n, 1, false);
    }
    
    public boolean getSupportsAbsolutePositioning() {
        return this.supportsAbsolutePositioning;
    }
    
    private boolean isMouseLeftOfThumb() {
        return this.trackListener.currentMouseX < this.getThumbBounds().x;
    }
    
    private boolean isMouseRightOfThumb() {
        final Rectangle thumbBounds = this.getThumbBounds();
        return this.trackListener.currentMouseX > thumbBounds.x + thumbBounds.width;
    }
    
    private boolean isMouseBeforeThumb() {
        return this.scrollbar.getComponentOrientation().isLeftToRight() ? this.isMouseLeftOfThumb() : this.isMouseRightOfThumb();
    }
    
    private boolean isMouseAfterThumb() {
        return this.scrollbar.getComponentOrientation().isLeftToRight() ? this.isMouseRightOfThumb() : this.isMouseLeftOfThumb();
    }
    
    private void updateButtonDirections() {
        final int orientation = this.scrollbar.getOrientation();
        if (this.scrollbar.getComponentOrientation().isLeftToRight()) {
            if (this.incrButton instanceof BasicArrowButton) {
                ((BasicArrowButton)this.incrButton).setDirection((orientation == 0) ? 3 : 5);
            }
            if (this.decrButton instanceof BasicArrowButton) {
                ((BasicArrowButton)this.decrButton).setDirection((orientation == 0) ? 7 : 1);
            }
        }
        else {
            if (this.incrButton instanceof BasicArrowButton) {
                ((BasicArrowButton)this.incrButton).setDirection((orientation == 0) ? 7 : 5);
            }
            if (this.decrButton instanceof BasicArrowButton) {
                ((BasicArrowButton)this.decrButton).setDirection((orientation == 0) ? 3 : 1);
            }
        }
    }
    
    protected class ModelListener implements ChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            if (!BasicScrollBarUI.this.useCachedValue) {
                BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue();
            }
            BasicScrollBarUI.this.layoutContainer(BasicScrollBarUI.this.scrollbar);
            BasicScrollBarUI.this.useCachedValue = false;
        }
    }
    
    protected class TrackListener extends MouseAdapter implements MouseMotionListener
    {
        protected transient int offset;
        protected transient int currentMouseX;
        protected transient int currentMouseY;
        private transient int direction;
        
        protected TrackListener() {
            this.direction = 1;
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (BasicScrollBarUI.this.isDragging) {
                BasicScrollBarUI.this.updateThumbState(mouseEvent.getX(), mouseEvent.getY());
            }
            if (SwingUtilities.isRightMouseButton(mouseEvent) || (!BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(mouseEvent))) {
                return;
            }
            if (!BasicScrollBarUI.this.scrollbar.isEnabled()) {
                return;
            }
            final Rectangle trackBounds = BasicScrollBarUI.this.getTrackBounds();
            BasicScrollBarUI.this.scrollbar.repaint(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            BasicScrollBarUI.this.trackHighlight = 0;
            BasicScrollBarUI.this.isDragging = false;
            this.offset = 0;
            BasicScrollBarUI.this.scrollTimer.stop();
            BasicScrollBarUI.this.useCachedValue = true;
            BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
        }
        
        @Override
        public void mousePressed(final MouseEvent valueFrom) {
            if (SwingUtilities.isRightMouseButton(valueFrom) || (!BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(valueFrom))) {
                return;
            }
            if (!BasicScrollBarUI.this.scrollbar.isEnabled()) {
                return;
            }
            if (!BasicScrollBarUI.this.scrollbar.hasFocus() && BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled()) {
                BasicScrollBarUI.this.scrollbar.requestFocus();
            }
            BasicScrollBarUI.this.useCachedValue = true;
            BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(true);
            this.currentMouseX = valueFrom.getX();
            this.currentMouseY = valueFrom.getY();
            if (BasicScrollBarUI.this.getThumbBounds().contains(this.currentMouseX, this.currentMouseY)) {
                switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
                    case 1: {
                        this.offset = this.currentMouseY - BasicScrollBarUI.this.getThumbBounds().y;
                        break;
                    }
                    case 0: {
                        this.offset = this.currentMouseX - BasicScrollBarUI.this.getThumbBounds().x;
                        break;
                    }
                }
                BasicScrollBarUI.this.isDragging = true;
                return;
            }
            if (BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(valueFrom)) {
                switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
                    case 1: {
                        this.offset = BasicScrollBarUI.this.getThumbBounds().height / 2;
                        break;
                    }
                    case 0: {
                        this.offset = BasicScrollBarUI.this.getThumbBounds().width / 2;
                        break;
                    }
                }
                BasicScrollBarUI.this.isDragging = true;
                this.setValueFrom(valueFrom);
                return;
            }
            BasicScrollBarUI.this.isDragging = false;
            final Dimension size = BasicScrollBarUI.this.scrollbar.getSize();
            this.direction = 1;
            switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
                case 1: {
                    if (BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
                        this.direction = ((this.currentMouseY < size.height / 2) ? -1 : 1);
                        break;
                    }
                    this.direction = ((this.currentMouseY < BasicScrollBarUI.this.getThumbBounds().y) ? -1 : 1);
                    break;
                }
                case 0: {
                    if (BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
                        this.direction = ((this.currentMouseX < size.width / 2) ? -1 : 1);
                    }
                    else {
                        this.direction = ((this.currentMouseX < BasicScrollBarUI.this.getThumbBounds().x) ? -1 : 1);
                    }
                    if (!BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
                        this.direction = -this.direction;
                        break;
                    }
                    break;
                }
            }
            BasicScrollBarUI.this.scrollByBlock(this.direction);
            BasicScrollBarUI.this.scrollTimer.stop();
            BasicScrollBarUI.this.scrollListener.setDirection(this.direction);
            BasicScrollBarUI.this.scrollListener.setScrollByBlock(true);
            this.startScrollTimerIfNecessary();
        }
        
        @Override
        public void mouseDragged(final MouseEvent valueFrom) {
            if (SwingUtilities.isRightMouseButton(valueFrom) || (!BasicScrollBarUI.this.getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton(valueFrom))) {
                return;
            }
            if (!BasicScrollBarUI.this.scrollbar.isEnabled() || BasicScrollBarUI.this.getThumbBounds().isEmpty()) {
                return;
            }
            if (BasicScrollBarUI.this.isDragging) {
                this.setValueFrom(valueFrom);
            }
            else {
                this.currentMouseX = valueFrom.getX();
                this.currentMouseY = valueFrom.getY();
                BasicScrollBarUI.this.updateThumbState(this.currentMouseX, this.currentMouseY);
                this.startScrollTimerIfNecessary();
            }
        }
        
        private void setValueFrom(final MouseEvent mouseEvent) {
            final boolean thumbRollover = BasicScrollBarUI.this.isThumbRollover();
            final BoundedRangeModel model = BasicScrollBarUI.this.scrollbar.getModel();
            final Rectangle thumbBounds = BasicScrollBarUI.this.getThumbBounds();
            int n;
            int n2;
            int n3;
            if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1) {
                n = BasicScrollBarUI.this.trackRect.y;
                n2 = BasicScrollBarUI.this.trackRect.y + BasicScrollBarUI.this.trackRect.height - thumbBounds.height;
                n3 = Math.min(n2, Math.max(n, mouseEvent.getY() - this.offset));
                BasicScrollBarUI.this.setThumbBounds(thumbBounds.x, n3, thumbBounds.width, thumbBounds.height);
                final float n4 = (float)BasicScrollBarUI.this.getTrackBounds().height;
            }
            else {
                n = BasicScrollBarUI.this.trackRect.x;
                n2 = BasicScrollBarUI.this.trackRect.x + BasicScrollBarUI.this.trackRect.width - thumbBounds.width;
                n3 = Math.min(n2, Math.max(n, mouseEvent.getX() - this.offset));
                BasicScrollBarUI.this.setThumbBounds(n3, thumbBounds.y, thumbBounds.width, thumbBounds.height);
                final float n5 = (float)BasicScrollBarUI.this.getTrackBounds().width;
            }
            if (n3 == n2) {
                if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1 || BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
                    BasicScrollBarUI.this.scrollbar.setValue(model.getMaximum() - model.getExtent());
                }
                else {
                    BasicScrollBarUI.this.scrollbar.setValue(model.getMinimum());
                }
            }
            else {
                final float n6 = model.getMaximum() - model.getExtent() - (float)model.getMinimum();
                final float n7 = (float)(n3 - n);
                final float n8 = (float)(n2 - n);
                int n9;
                if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1 || BasicScrollBarUI.this.scrollbar.getComponentOrientation().isLeftToRight()) {
                    n9 = (int)(0.5 + n7 / n8 * n6);
                }
                else {
                    n9 = (int)(0.5 + (n2 - n3) / n8 * n6);
                }
                BasicScrollBarUI.this.useCachedValue = true;
                BasicScrollBarUI.this.scrollBarValue = n9 + model.getMinimum();
                BasicScrollBarUI.this.scrollbar.setValue(this.adjustValueIfNecessary(BasicScrollBarUI.this.scrollBarValue));
            }
            BasicScrollBarUI.this.setThumbRollover(thumbRollover);
        }
        
        private int adjustValueIfNecessary(int n) {
            if (BasicScrollBarUI.this.scrollbar.getParent() instanceof JScrollPane) {
                final JScrollPane scrollPane = (JScrollPane)BasicScrollBarUI.this.scrollbar.getParent();
                final JViewport viewport = scrollPane.getViewport();
                final Component view = viewport.getView();
                if (view instanceof JList) {
                    final JList list = (JList)view;
                    if (DefaultLookup.getBoolean(list, list.getUI(), "List.lockToPositionOnScroll", false)) {
                        int n2 = n;
                        final int layoutOrientation = list.getLayoutOrientation();
                        final int orientation = BasicScrollBarUI.this.scrollbar.getOrientation();
                        if (orientation == 1 && layoutOrientation == 0) {
                            final int locationToIndex = list.locationToIndex(new Point(0, n));
                            final Rectangle cellBounds = list.getCellBounds(locationToIndex, locationToIndex);
                            if (cellBounds != null) {
                                n2 = cellBounds.y;
                            }
                        }
                        if (orientation == 0 && (layoutOrientation == 1 || layoutOrientation == 2)) {
                            if (scrollPane.getComponentOrientation().isLeftToRight()) {
                                final int locationToIndex2 = list.locationToIndex(new Point(n, 0));
                                final Rectangle cellBounds2 = list.getCellBounds(locationToIndex2, locationToIndex2);
                                if (cellBounds2 != null) {
                                    n2 = cellBounds2.x;
                                }
                            }
                            else {
                                final Point point = new Point(n, 0);
                                final int width = viewport.getExtentSize().width;
                                final Point point2 = point;
                                point2.x += width - 1;
                                final int locationToIndex3 = list.locationToIndex(point);
                                final Rectangle cellBounds3 = list.getCellBounds(locationToIndex3, locationToIndex3);
                                if (cellBounds3 != null) {
                                    n2 = cellBounds3.x + cellBounds3.width - width;
                                }
                            }
                        }
                        n = n2;
                    }
                }
            }
            return n;
        }
        
        private void startScrollTimerIfNecessary() {
            if (BasicScrollBarUI.this.scrollTimer.isRunning()) {
                return;
            }
            final Rectangle thumbBounds = BasicScrollBarUI.this.getThumbBounds();
            switch (BasicScrollBarUI.this.scrollbar.getOrientation()) {
                case 1: {
                    if (this.direction > 0) {
                        if (thumbBounds.y + thumbBounds.height < BasicScrollBarUI.this.trackListener.currentMouseY) {
                            BasicScrollBarUI.this.scrollTimer.start();
                            break;
                        }
                        break;
                    }
                    else {
                        if (thumbBounds.y > BasicScrollBarUI.this.trackListener.currentMouseY) {
                            BasicScrollBarUI.this.scrollTimer.start();
                            break;
                        }
                        break;
                    }
                    break;
                }
                case 0: {
                    if ((this.direction > 0 && BasicScrollBarUI.this.isMouseAfterThumb()) || (this.direction < 0 && BasicScrollBarUI.this.isMouseBeforeThumb())) {
                        BasicScrollBarUI.this.scrollTimer.start();
                        break;
                    }
                    break;
                }
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
            if (!BasicScrollBarUI.this.isDragging) {
                BasicScrollBarUI.this.updateThumbState(mouseEvent.getX(), mouseEvent.getY());
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            if (!BasicScrollBarUI.this.isDragging) {
                BasicScrollBarUI.this.setThumbRollover(false);
            }
        }
    }
    
    protected class ArrowButtonListener extends MouseAdapter
    {
        boolean handledEvent;
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (!BasicScrollBarUI.this.scrollbar.isEnabled()) {
                return;
            }
            if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
                return;
            }
            final int direction = (mouseEvent.getSource() == BasicScrollBarUI.this.incrButton) ? 1 : -1;
            BasicScrollBarUI.this.scrollByUnit(direction);
            BasicScrollBarUI.this.scrollTimer.stop();
            BasicScrollBarUI.this.scrollListener.setDirection(direction);
            BasicScrollBarUI.this.scrollListener.setScrollByBlock(false);
            BasicScrollBarUI.this.scrollTimer.start();
            this.handledEvent = true;
            if (!BasicScrollBarUI.this.scrollbar.hasFocus() && BasicScrollBarUI.this.scrollbar.isRequestFocusEnabled()) {
                BasicScrollBarUI.this.scrollbar.requestFocus();
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            BasicScrollBarUI.this.scrollTimer.stop();
            this.handledEvent = false;
            BasicScrollBarUI.this.scrollbar.setValueIsAdjusting(false);
        }
    }
    
    protected class ScrollListener implements ActionListener
    {
        int direction;
        boolean useBlockIncrement;
        
        public ScrollListener() {
            this.direction = 1;
            this.direction = 1;
            this.useBlockIncrement = false;
        }
        
        public ScrollListener(final int direction, final boolean useBlockIncrement) {
            this.direction = 1;
            this.direction = direction;
            this.useBlockIncrement = useBlockIncrement;
        }
        
        public void setDirection(final int direction) {
            this.direction = direction;
        }
        
        public void setScrollByBlock(final boolean useBlockIncrement) {
            this.useBlockIncrement = useBlockIncrement;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (this.useBlockIncrement) {
                BasicScrollBarUI.this.scrollByBlock(this.direction);
                if (BasicScrollBarUI.this.scrollbar.getOrientation() == 1) {
                    if (this.direction > 0) {
                        if (BasicScrollBarUI.this.getThumbBounds().y + BasicScrollBarUI.this.getThumbBounds().height >= BasicScrollBarUI.this.trackListener.currentMouseY) {
                            ((Timer)actionEvent.getSource()).stop();
                        }
                    }
                    else if (BasicScrollBarUI.this.getThumbBounds().y <= BasicScrollBarUI.this.trackListener.currentMouseY) {
                        ((Timer)actionEvent.getSource()).stop();
                    }
                }
                else if ((this.direction > 0 && !BasicScrollBarUI.this.isMouseAfterThumb()) || (this.direction < 0 && !BasicScrollBarUI.this.isMouseBeforeThumb())) {
                    ((Timer)actionEvent.getSource()).stop();
                }
            }
            else {
                BasicScrollBarUI.this.scrollByUnit(this.direction);
            }
            if (this.direction > 0 && BasicScrollBarUI.this.scrollbar.getValue() + BasicScrollBarUI.this.scrollbar.getVisibleAmount() >= BasicScrollBarUI.this.scrollbar.getMaximum()) {
                ((Timer)actionEvent.getSource()).stop();
            }
            else if (this.direction < 0 && BasicScrollBarUI.this.scrollbar.getValue() <= BasicScrollBarUI.this.scrollbar.getMinimum()) {
                ((Timer)actionEvent.getSource()).stop();
            }
        }
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicScrollBarUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    private static class Actions extends UIAction
    {
        private static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
        private static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
        private static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
        private static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
        private static final String MIN_SCROLL = "minScroll";
        private static final String MAX_SCROLL = "maxScroll";
        
        Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JScrollBar scrollBar = (JScrollBar)actionEvent.getSource();
            final String name = this.getName();
            if (name == "positiveUnitIncrement") {
                this.scroll(scrollBar, 1, false);
            }
            else if (name == "positiveBlockIncrement") {
                this.scroll(scrollBar, 1, true);
            }
            else if (name == "negativeUnitIncrement") {
                this.scroll(scrollBar, -1, false);
            }
            else if (name == "negativeBlockIncrement") {
                this.scroll(scrollBar, -1, true);
            }
            else if (name == "minScroll") {
                this.scroll(scrollBar, 2, true);
            }
            else if (name == "maxScroll") {
                this.scroll(scrollBar, 3, true);
            }
        }
        
        private void scroll(final JScrollBar scrollBar, final int n, final boolean b) {
            if (n == -1 || n == 1) {
                int n2;
                if (b) {
                    if (n == -1) {
                        n2 = -1 * scrollBar.getBlockIncrement(-1);
                    }
                    else {
                        n2 = scrollBar.getBlockIncrement(1);
                    }
                }
                else if (n == -1) {
                    n2 = -1 * scrollBar.getUnitIncrement(-1);
                }
                else {
                    n2 = scrollBar.getUnitIncrement(1);
                }
                scrollBar.setValue(scrollBar.getValue() + n2);
            }
            else if (n == 2) {
                scrollBar.setValue(scrollBar.getMinimum());
            }
            else if (n == 3) {
                scrollBar.setValue(scrollBar.getMaximum());
            }
        }
    }
    
    private class Handler implements FocusListener, PropertyChangeListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicScrollBarUI.this.scrollbar.repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicScrollBarUI.this.scrollbar.repaint();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if ("model" == propertyName) {
                final BoundedRangeModel boundedRangeModel = (BoundedRangeModel)propertyChangeEvent.getOldValue();
                final BoundedRangeModel boundedRangeModel2 = (BoundedRangeModel)propertyChangeEvent.getNewValue();
                boundedRangeModel.removeChangeListener(BasicScrollBarUI.this.modelListener);
                boundedRangeModel2.addChangeListener(BasicScrollBarUI.this.modelListener);
                BasicScrollBarUI.this.scrollBarValue = BasicScrollBarUI.this.scrollbar.getValue();
                BasicScrollBarUI.this.scrollbar.repaint();
                BasicScrollBarUI.this.scrollbar.revalidate();
            }
            else if ("orientation" == propertyName) {
                BasicScrollBarUI.this.updateButtonDirections();
            }
            else if ("componentOrientation" == propertyName) {
                BasicScrollBarUI.this.updateButtonDirections();
                SwingUtilities.replaceUIInputMap(BasicScrollBarUI.this.scrollbar, 0, BasicScrollBarUI.this.getInputMap(0));
            }
        }
    }
}
