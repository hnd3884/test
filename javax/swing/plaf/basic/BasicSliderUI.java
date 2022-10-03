package javax.swing.plaf.basic;

import sun.swing.UIAction;
import javax.swing.AbstractAction;
import java.awt.event.ComponentAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.BoundedRangeModel;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentEvent;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeEvent;
import java.awt.Polygon;
import javax.swing.Icon;
import java.awt.image.ImageObserver;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Dictionary;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.ActionMap;
import javax.swing.Action;
import sun.swing.DefaultLookup;
import javax.swing.InputMap;
import javax.swing.SwingUtilities;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.UIManager;
import java.awt.IllegalComponentStateException;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.awt.event.FocusListener;
import java.awt.event.ComponentListener;
import javax.swing.event.ChangeListener;
import java.awt.Rectangle;
import java.awt.Insets;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.plaf.SliderUI;

public class BasicSliderUI extends SliderUI
{
    private static final Actions SHARED_ACTION;
    public static final int POSITIVE_SCROLL = 1;
    public static final int NEGATIVE_SCROLL = -1;
    public static final int MIN_SCROLL = -2;
    public static final int MAX_SCROLL = 2;
    protected Timer scrollTimer;
    protected JSlider slider;
    protected Insets focusInsets;
    protected Insets insetCache;
    protected boolean leftToRightCache;
    protected Rectangle focusRect;
    protected Rectangle contentRect;
    protected Rectangle labelRect;
    protected Rectangle tickRect;
    protected Rectangle trackRect;
    protected Rectangle thumbRect;
    protected int trackBuffer;
    private transient boolean isDragging;
    protected TrackListener trackListener;
    protected ChangeListener changeListener;
    protected ComponentListener componentListener;
    protected FocusListener focusListener;
    protected ScrollListener scrollListener;
    protected PropertyChangeListener propertyChangeListener;
    private Handler handler;
    private int lastValue;
    private Color shadowColor;
    private Color highlightColor;
    private Color focusColor;
    private boolean checkedLabelBaselines;
    private boolean sameLabelBaselines;
    private static Rectangle unionRect;
    
    protected Color getShadowColor() {
        return this.shadowColor;
    }
    
    protected Color getHighlightColor() {
        return this.highlightColor;
    }
    
    protected Color getFocusColor() {
        return this.focusColor;
    }
    
    protected boolean isDragging() {
        return this.isDragging;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicSliderUI((JSlider)component);
    }
    
    public BasicSliderUI(final JSlider slider) {
        this.focusInsets = null;
        this.insetCache = null;
        this.leftToRightCache = true;
        this.focusRect = null;
        this.contentRect = null;
        this.labelRect = null;
        this.tickRect = null;
        this.trackRect = null;
        this.thumbRect = null;
        this.trackBuffer = 0;
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.slider = (JSlider)component;
        this.checkedLabelBaselines = false;
        this.slider.setEnabled(this.slider.isEnabled());
        LookAndFeel.installProperty(this.slider, "opaque", Boolean.TRUE);
        this.isDragging = false;
        this.trackListener = this.createTrackListener(this.slider);
        this.changeListener = this.createChangeListener(this.slider);
        this.componentListener = this.createComponentListener(this.slider);
        this.focusListener = this.createFocusListener(this.slider);
        this.scrollListener = this.createScrollListener(this.slider);
        this.propertyChangeListener = this.createPropertyChangeListener(this.slider);
        this.installDefaults(this.slider);
        this.installListeners(this.slider);
        this.installKeyboardActions(this.slider);
        (this.scrollTimer = new Timer(100, this.scrollListener)).setInitialDelay(300);
        this.insetCache = this.slider.getInsets();
        this.leftToRightCache = BasicGraphicsUtils.isLeftToRight(this.slider);
        this.focusRect = new Rectangle();
        this.contentRect = new Rectangle();
        this.labelRect = new Rectangle();
        this.tickRect = new Rectangle();
        this.trackRect = new Rectangle();
        this.thumbRect = new Rectangle();
        this.lastValue = this.slider.getValue();
        this.calculateGeometry();
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        if (component != this.slider) {
            throw new IllegalComponentStateException(this + " was asked to deinstall() " + component + " when it only knows about " + this.slider + ".");
        }
        this.scrollTimer.stop();
        this.scrollTimer = null;
        this.uninstallDefaults(this.slider);
        this.uninstallListeners(this.slider);
        this.uninstallKeyboardActions(this.slider);
        this.insetCache = null;
        this.leftToRightCache = true;
        this.focusRect = null;
        this.contentRect = null;
        this.labelRect = null;
        this.tickRect = null;
        this.trackRect = null;
        this.thumbRect = null;
        this.trackListener = null;
        this.changeListener = null;
        this.componentListener = null;
        this.focusListener = null;
        this.scrollListener = null;
        this.propertyChangeListener = null;
        this.slider = null;
    }
    
    protected void installDefaults(final JSlider slider) {
        LookAndFeel.installBorder(slider, "Slider.border");
        LookAndFeel.installColorsAndFont(slider, "Slider.background", "Slider.foreground", "Slider.font");
        this.highlightColor = UIManager.getColor("Slider.highlight");
        this.shadowColor = UIManager.getColor("Slider.shadow");
        this.focusColor = UIManager.getColor("Slider.focus");
        this.focusInsets = (Insets)UIManager.get("Slider.focusInsets");
        if (this.focusInsets == null) {
            this.focusInsets = new InsetsUIResource(2, 2, 2, 2);
        }
    }
    
    protected void uninstallDefaults(final JSlider slider) {
        LookAndFeel.uninstallBorder(slider);
        this.focusInsets = null;
    }
    
    protected TrackListener createTrackListener(final JSlider slider) {
        return new TrackListener();
    }
    
    protected ChangeListener createChangeListener(final JSlider slider) {
        return this.getHandler();
    }
    
    protected ComponentListener createComponentListener(final JSlider slider) {
        return this.getHandler();
    }
    
    protected FocusListener createFocusListener(final JSlider slider) {
        return this.getHandler();
    }
    
    protected ScrollListener createScrollListener(final JSlider slider) {
        return new ScrollListener();
    }
    
    protected PropertyChangeListener createPropertyChangeListener(final JSlider slider) {
        return this.getHandler();
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected void installListeners(final JSlider slider) {
        slider.addMouseListener(this.trackListener);
        slider.addMouseMotionListener(this.trackListener);
        slider.addFocusListener(this.focusListener);
        slider.addComponentListener(this.componentListener);
        slider.addPropertyChangeListener(this.propertyChangeListener);
        slider.getModel().addChangeListener(this.changeListener);
    }
    
    protected void uninstallListeners(final JSlider slider) {
        slider.removeMouseListener(this.trackListener);
        slider.removeMouseMotionListener(this.trackListener);
        slider.removeFocusListener(this.focusListener);
        slider.removeComponentListener(this.componentListener);
        slider.removePropertyChangeListener(this.propertyChangeListener);
        slider.getModel().removeChangeListener(this.changeListener);
        this.handler = null;
    }
    
    protected void installKeyboardActions(final JSlider slider) {
        SwingUtilities.replaceUIInputMap(slider, 0, this.getInputMap(0, slider));
        LazyActionMap.installLazyActionMap(slider, BasicSliderUI.class, "Slider.actionMap");
    }
    
    InputMap getInputMap(final int n, final JSlider slider) {
        if (n != 0) {
            return null;
        }
        final InputMap parent = (InputMap)DefaultLookup.get(slider, this, "Slider.focusInputMap");
        final InputMap inputMap;
        if (slider.getComponentOrientation().isLeftToRight() || (inputMap = (InputMap)DefaultLookup.get(slider, this, "Slider.focusInputMap.RightToLeft")) == null) {
            return parent;
        }
        inputMap.setParent(parent);
        return inputMap;
    }
    
    static void loadActionMap(final LazyActionMap lazyActionMap) {
        lazyActionMap.put(new Actions("positiveUnitIncrement"));
        lazyActionMap.put(new Actions("positiveBlockIncrement"));
        lazyActionMap.put(new Actions("negativeUnitIncrement"));
        lazyActionMap.put(new Actions("negativeBlockIncrement"));
        lazyActionMap.put(new Actions("minScroll"));
        lazyActionMap.put(new Actions("maxScroll"));
    }
    
    protected void uninstallKeyboardActions(final JSlider slider) {
        SwingUtilities.replaceUIActionMap(slider, null);
        SwingUtilities.replaceUIInputMap(slider, 0, null);
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, final int n2) {
        super.getBaseline(component, n, n2);
        if (this.slider.getPaintLabels() && this.labelsHaveSameBaselines()) {
            final FontMetrics fontMetrics = this.slider.getFontMetrics(this.slider.getFont());
            final Insets insets = this.slider.getInsets();
            final Dimension thumbSize = this.getThumbSize();
            if (this.slider.getOrientation() == 0) {
                final int tickLength = this.getTickLength();
                final int n3 = n2 - insets.top - insets.bottom - this.focusInsets.top - this.focusInsets.bottom;
                int height;
                final int n4 = height = thumbSize.height;
                if (this.slider.getPaintTicks()) {
                    height += tickLength;
                }
                final int n5 = insets.top + this.focusInsets.top + (n3 - (height + this.getHeightOfTallestLabel()) - 1) / 2 + n4;
                int n6 = tickLength;
                if (!this.slider.getPaintTicks()) {
                    n6 = 0;
                }
                return n5 + n6 + fontMetrics.getAscent();
            }
            final Integer n7 = this.slider.getInverted() ? this.getLowestValue() : this.getHighestValue();
            if (n7 != null) {
                final int max = Math.max(fontMetrics.getHeight() / 2, thumbSize.height / 2);
                return this.yPositionForValue(n7, this.focusInsets.top + insets.top + max, n2 - this.focusInsets.top - this.focusInsets.bottom - insets.top - insets.bottom - max - max) - fontMetrics.getHeight() / 2 + fontMetrics.getAscent();
            }
        }
        return 0;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        return Component.BaselineResizeBehavior.OTHER;
    }
    
    protected boolean labelsHaveSameBaselines() {
        if (!this.checkedLabelBaselines) {
            this.checkedLabelBaselines = true;
            final Dictionary labelTable = this.slider.getLabelTable();
            if (labelTable != null) {
                this.sameLabelBaselines = true;
                final Enumeration elements = labelTable.elements();
                int n = -1;
                while (elements.hasMoreElements()) {
                    final JComponent component = (JComponent)elements.nextElement();
                    final Dimension preferredSize = component.getPreferredSize();
                    final int baseline = component.getBaseline(preferredSize.width, preferredSize.height);
                    if (baseline < 0) {
                        this.sameLabelBaselines = false;
                        break;
                    }
                    if (n == -1) {
                        n = baseline;
                    }
                    else {
                        if (n != baseline) {
                            this.sameLabelBaselines = false;
                            break;
                        }
                        continue;
                    }
                }
            }
            else {
                this.sameLabelBaselines = false;
            }
        }
        return this.sameLabelBaselines;
    }
    
    public Dimension getPreferredHorizontalSize() {
        Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.horizontalSize");
        if (dimension == null) {
            dimension = new Dimension(200, 21);
        }
        return dimension;
    }
    
    public Dimension getPreferredVerticalSize() {
        Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.verticalSize");
        if (dimension == null) {
            dimension = new Dimension(21, 200);
        }
        return dimension;
    }
    
    public Dimension getMinimumHorizontalSize() {
        Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.minimumHorizontalSize");
        if (dimension == null) {
            dimension = new Dimension(36, 21);
        }
        return dimension;
    }
    
    public Dimension getMinimumVerticalSize() {
        Dimension dimension = (Dimension)DefaultLookup.get(this.slider, this, "Slider.minimumVerticalSize");
        if (dimension == null) {
            dimension = new Dimension(21, 36);
        }
        return dimension;
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        this.recalculateIfInsetsChanged();
        Dimension dimension;
        if (this.slider.getOrientation() == 1) {
            dimension = new Dimension(this.getPreferredVerticalSize());
            dimension.width = this.insetCache.left + this.insetCache.right;
            final Dimension dimension2 = dimension;
            dimension2.width += this.focusInsets.left + this.focusInsets.right;
            final Dimension dimension3 = dimension;
            dimension3.width += this.trackRect.width + this.tickRect.width + this.labelRect.width;
        }
        else {
            dimension = new Dimension(this.getPreferredHorizontalSize());
            dimension.height = this.insetCache.top + this.insetCache.bottom;
            final Dimension dimension4 = dimension;
            dimension4.height += this.focusInsets.top + this.focusInsets.bottom;
            final Dimension dimension5 = dimension;
            dimension5.height += this.trackRect.height + this.tickRect.height + this.labelRect.height;
        }
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        this.recalculateIfInsetsChanged();
        Dimension dimension;
        if (this.slider.getOrientation() == 1) {
            dimension = new Dimension(this.getMinimumVerticalSize());
            dimension.width = this.insetCache.left + this.insetCache.right;
            final Dimension dimension2 = dimension;
            dimension2.width += this.focusInsets.left + this.focusInsets.right;
            final Dimension dimension3 = dimension;
            dimension3.width += this.trackRect.width + this.tickRect.width + this.labelRect.width;
        }
        else {
            dimension = new Dimension(this.getMinimumHorizontalSize());
            dimension.height = this.insetCache.top + this.insetCache.bottom;
            final Dimension dimension4 = dimension;
            dimension4.height += this.focusInsets.top + this.focusInsets.bottom;
            final Dimension dimension5 = dimension;
            dimension5.height += this.trackRect.height + this.tickRect.height + this.labelRect.height;
        }
        return dimension;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        if (this.slider.getOrientation() == 1) {
            preferredSize.height = 32767;
        }
        else {
            preferredSize.width = 32767;
        }
        return preferredSize;
    }
    
    protected void calculateGeometry() {
        this.calculateFocusRect();
        this.calculateContentRect();
        this.calculateThumbSize();
        this.calculateTrackBuffer();
        this.calculateTrackRect();
        this.calculateTickRect();
        this.calculateLabelRect();
        this.calculateThumbLocation();
    }
    
    protected void calculateFocusRect() {
        this.focusRect.x = this.insetCache.left;
        this.focusRect.y = this.insetCache.top;
        this.focusRect.width = this.slider.getWidth() - (this.insetCache.left + this.insetCache.right);
        this.focusRect.height = this.slider.getHeight() - (this.insetCache.top + this.insetCache.bottom);
    }
    
    protected void calculateThumbSize() {
        final Dimension thumbSize = this.getThumbSize();
        this.thumbRect.setSize(thumbSize.width, thumbSize.height);
    }
    
    protected void calculateContentRect() {
        this.contentRect.x = this.focusRect.x + this.focusInsets.left;
        this.contentRect.y = this.focusRect.y + this.focusInsets.top;
        this.contentRect.width = this.focusRect.width - (this.focusInsets.left + this.focusInsets.right);
        this.contentRect.height = this.focusRect.height - (this.focusInsets.top + this.focusInsets.bottom);
    }
    
    private int getTickSpacing() {
        final int majorTickSpacing = this.slider.getMajorTickSpacing();
        final int minorTickSpacing = this.slider.getMinorTickSpacing();
        int n;
        if (minorTickSpacing > 0) {
            n = minorTickSpacing;
        }
        else if (majorTickSpacing > 0) {
            n = majorTickSpacing;
        }
        else {
            n = 0;
        }
        return n;
    }
    
    protected void calculateThumbLocation() {
        if (this.slider.getSnapToTicks()) {
            int value;
            final int n = value = this.slider.getValue();
            final int tickSpacing = this.getTickSpacing();
            if (tickSpacing != 0) {
                if ((n - this.slider.getMinimum()) % tickSpacing != 0) {
                    final float n2 = (n - this.slider.getMinimum()) / (float)tickSpacing;
                    int round = Math.round(n2);
                    if (n2 - (int)n2 == 0.5 && n < this.lastValue) {
                        --round;
                    }
                    value = this.slider.getMinimum() + round * tickSpacing;
                }
                if (value != n) {
                    this.slider.setValue(value);
                }
            }
        }
        if (this.slider.getOrientation() == 0) {
            this.thumbRect.x = this.xPositionForValue(this.slider.getValue()) - this.thumbRect.width / 2;
            this.thumbRect.y = this.trackRect.y;
        }
        else {
            final int yPositionForValue = this.yPositionForValue(this.slider.getValue());
            this.thumbRect.x = this.trackRect.x;
            this.thumbRect.y = yPositionForValue - this.thumbRect.height / 2;
        }
    }
    
    protected void calculateTrackBuffer() {
        if (this.slider.getPaintLabels() && this.slider.getLabelTable() != null) {
            final Component highestValueLabel = this.getHighestValueLabel();
            final Component lowestValueLabel = this.getLowestValueLabel();
            if (this.slider.getOrientation() == 0) {
                this.trackBuffer = Math.max(highestValueLabel.getBounds().width, lowestValueLabel.getBounds().width) / 2;
                this.trackBuffer = Math.max(this.trackBuffer, this.thumbRect.width / 2);
            }
            else {
                this.trackBuffer = Math.max(highestValueLabel.getBounds().height, lowestValueLabel.getBounds().height) / 2;
                this.trackBuffer = Math.max(this.trackBuffer, this.thumbRect.height / 2);
            }
        }
        else if (this.slider.getOrientation() == 0) {
            this.trackBuffer = this.thumbRect.width / 2;
        }
        else {
            this.trackBuffer = this.thumbRect.height / 2;
        }
    }
    
    protected void calculateTrackRect() {
        if (this.slider.getOrientation() == 0) {
            int height = this.thumbRect.height;
            if (this.slider.getPaintTicks()) {
                height += this.getTickLength();
            }
            if (this.slider.getPaintLabels()) {
                height += this.getHeightOfTallestLabel();
            }
            this.trackRect.x = this.contentRect.x + this.trackBuffer;
            this.trackRect.y = this.contentRect.y + (this.contentRect.height - height - 1) / 2;
            this.trackRect.width = this.contentRect.width - this.trackBuffer * 2;
            this.trackRect.height = this.thumbRect.height;
        }
        else {
            int width = this.thumbRect.width;
            if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
                if (this.slider.getPaintTicks()) {
                    width += this.getTickLength();
                }
                if (this.slider.getPaintLabels()) {
                    width += this.getWidthOfWidestLabel();
                }
            }
            else {
                if (this.slider.getPaintTicks()) {
                    width -= this.getTickLength();
                }
                if (this.slider.getPaintLabels()) {
                    width -= this.getWidthOfWidestLabel();
                }
            }
            this.trackRect.x = this.contentRect.x + (this.contentRect.width - width - 1) / 2;
            this.trackRect.y = this.contentRect.y + this.trackBuffer;
            this.trackRect.width = this.thumbRect.width;
            this.trackRect.height = this.contentRect.height - this.trackBuffer * 2;
        }
    }
    
    protected int getTickLength() {
        return 8;
    }
    
    protected void calculateTickRect() {
        if (this.slider.getOrientation() == 0) {
            this.tickRect.x = this.trackRect.x;
            this.tickRect.y = this.trackRect.y + this.trackRect.height;
            this.tickRect.width = this.trackRect.width;
            this.tickRect.height = (this.slider.getPaintTicks() ? this.getTickLength() : 0);
        }
        else {
            this.tickRect.width = (this.slider.getPaintTicks() ? this.getTickLength() : 0);
            if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
                this.tickRect.x = this.trackRect.x + this.trackRect.width;
            }
            else {
                this.tickRect.x = this.trackRect.x - this.tickRect.width;
            }
            this.tickRect.y = this.trackRect.y;
            this.tickRect.height = this.trackRect.height;
        }
    }
    
    protected void calculateLabelRect() {
        if (this.slider.getPaintLabels()) {
            if (this.slider.getOrientation() == 0) {
                this.labelRect.x = this.tickRect.x - this.trackBuffer;
                this.labelRect.y = this.tickRect.y + this.tickRect.height;
                this.labelRect.width = this.tickRect.width + this.trackBuffer * 2;
                this.labelRect.height = this.getHeightOfTallestLabel();
            }
            else {
                if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
                    this.labelRect.x = this.tickRect.x + this.tickRect.width;
                    this.labelRect.width = this.getWidthOfWidestLabel();
                }
                else {
                    this.labelRect.width = this.getWidthOfWidestLabel();
                    this.labelRect.x = this.tickRect.x - this.labelRect.width;
                }
                this.labelRect.y = this.tickRect.y - this.trackBuffer;
                this.labelRect.height = this.tickRect.height + this.trackBuffer * 2;
            }
        }
        else if (this.slider.getOrientation() == 0) {
            this.labelRect.x = this.tickRect.x;
            this.labelRect.y = this.tickRect.y + this.tickRect.height;
            this.labelRect.width = this.tickRect.width;
            this.labelRect.height = 0;
        }
        else {
            if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
                this.labelRect.x = this.tickRect.x + this.tickRect.width;
            }
            else {
                this.labelRect.x = this.tickRect.x;
            }
            this.labelRect.y = this.tickRect.y;
            this.labelRect.width = 0;
            this.labelRect.height = this.tickRect.height;
        }
    }
    
    protected Dimension getThumbSize() {
        final Dimension dimension = new Dimension();
        if (this.slider.getOrientation() == 1) {
            dimension.width = 20;
            dimension.height = 11;
        }
        else {
            dimension.width = 11;
            dimension.height = 20;
        }
        return dimension;
    }
    
    protected int getWidthOfWidestLabel() {
        final Dictionary labelTable = this.slider.getLabelTable();
        int max = 0;
        if (labelTable != null) {
            final Enumeration keys = labelTable.keys();
            while (keys.hasMoreElements()) {
                max = Math.max(((JComponent)labelTable.get(keys.nextElement())).getPreferredSize().width, max);
            }
        }
        return max;
    }
    
    protected int getHeightOfTallestLabel() {
        final Dictionary labelTable = this.slider.getLabelTable();
        int max = 0;
        if (labelTable != null) {
            final Enumeration keys = labelTable.keys();
            while (keys.hasMoreElements()) {
                max = Math.max(((JComponent)labelTable.get(keys.nextElement())).getPreferredSize().height, max);
            }
        }
        return max;
    }
    
    protected int getWidthOfHighValueLabel() {
        final Component highestValueLabel = this.getHighestValueLabel();
        int width = 0;
        if (highestValueLabel != null) {
            width = highestValueLabel.getPreferredSize().width;
        }
        return width;
    }
    
    protected int getWidthOfLowValueLabel() {
        final Component lowestValueLabel = this.getLowestValueLabel();
        int width = 0;
        if (lowestValueLabel != null) {
            width = lowestValueLabel.getPreferredSize().width;
        }
        return width;
    }
    
    protected int getHeightOfHighValueLabel() {
        final Component highestValueLabel = this.getHighestValueLabel();
        int height = 0;
        if (highestValueLabel != null) {
            height = highestValueLabel.getPreferredSize().height;
        }
        return height;
    }
    
    protected int getHeightOfLowValueLabel() {
        final Component lowestValueLabel = this.getLowestValueLabel();
        int height = 0;
        if (lowestValueLabel != null) {
            height = lowestValueLabel.getPreferredSize().height;
        }
        return height;
    }
    
    protected boolean drawInverted() {
        if (this.slider.getOrientation() != 0) {
            return this.slider.getInverted();
        }
        if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
            return this.slider.getInverted();
        }
        return !this.slider.getInverted();
    }
    
    protected Integer getHighestValue() {
        final Dictionary labelTable = this.slider.getLabelTable();
        if (labelTable == null) {
            return null;
        }
        final Enumeration keys = labelTable.keys();
        Integer n = null;
        while (keys.hasMoreElements()) {
            final Integer n2 = (Integer)keys.nextElement();
            if (n == null || n2 > n) {
                n = n2;
            }
        }
        return n;
    }
    
    protected Integer getLowestValue() {
        final Dictionary labelTable = this.slider.getLabelTable();
        if (labelTable == null) {
            return null;
        }
        final Enumeration keys = labelTable.keys();
        Integer n = null;
        while (keys.hasMoreElements()) {
            final Integer n2 = (Integer)keys.nextElement();
            if (n == null || n2 < n) {
                n = n2;
            }
        }
        return n;
    }
    
    protected Component getLowestValueLabel() {
        final Integer lowestValue = this.getLowestValue();
        if (lowestValue != null) {
            return (Component)this.slider.getLabelTable().get(lowestValue);
        }
        return null;
    }
    
    protected Component getHighestValueLabel() {
        final Integer highestValue = this.getHighestValue();
        if (highestValue != null) {
            return (Component)this.slider.getLabelTable().get(highestValue);
        }
        return null;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        this.recalculateIfInsetsChanged();
        this.recalculateIfOrientationChanged();
        final Rectangle clipBounds = graphics.getClipBounds();
        if (!clipBounds.intersects(this.trackRect) && this.slider.getPaintTrack()) {
            this.calculateGeometry();
        }
        if (this.slider.getPaintTrack() && clipBounds.intersects(this.trackRect)) {
            this.paintTrack(graphics);
        }
        if (this.slider.getPaintTicks() && clipBounds.intersects(this.tickRect)) {
            this.paintTicks(graphics);
        }
        if (this.slider.getPaintLabels() && clipBounds.intersects(this.labelRect)) {
            this.paintLabels(graphics);
        }
        if (this.slider.hasFocus() && clipBounds.intersects(this.focusRect)) {
            this.paintFocus(graphics);
        }
        if (clipBounds.intersects(this.thumbRect)) {
            this.paintThumb(graphics);
        }
    }
    
    protected void recalculateIfInsetsChanged() {
        final Insets insets = this.slider.getInsets();
        if (!insets.equals(this.insetCache)) {
            this.insetCache = insets;
            this.calculateGeometry();
        }
    }
    
    protected void recalculateIfOrientationChanged() {
        final boolean leftToRight = BasicGraphicsUtils.isLeftToRight(this.slider);
        if (leftToRight != this.leftToRightCache) {
            this.leftToRightCache = leftToRight;
            this.calculateGeometry();
        }
    }
    
    public void paintFocus(final Graphics graphics) {
        graphics.setColor(this.getFocusColor());
        BasicGraphicsUtils.drawDashedRect(graphics, this.focusRect.x, this.focusRect.y, this.focusRect.width, this.focusRect.height);
    }
    
    public void paintTrack(final Graphics graphics) {
        final Rectangle trackRect = this.trackRect;
        if (this.slider.getOrientation() == 0) {
            final int n = trackRect.height / 2 - 2;
            final int width = trackRect.width;
            graphics.translate(trackRect.x, trackRect.y + n);
            graphics.setColor(this.getShadowColor());
            graphics.drawLine(0, 0, width - 1, 0);
            graphics.drawLine(0, 1, 0, 2);
            graphics.setColor(this.getHighlightColor());
            graphics.drawLine(0, 3, width, 3);
            graphics.drawLine(width, 0, width, 3);
            graphics.setColor(Color.black);
            graphics.drawLine(1, 1, width - 2, 1);
            graphics.translate(-trackRect.x, -(trackRect.y + n));
        }
        else {
            final int n2 = trackRect.width / 2 - 2;
            final int height = trackRect.height;
            graphics.translate(trackRect.x + n2, trackRect.y);
            graphics.setColor(this.getShadowColor());
            graphics.drawLine(0, 0, 0, height - 1);
            graphics.drawLine(1, 0, 2, 0);
            graphics.setColor(this.getHighlightColor());
            graphics.drawLine(3, 0, 3, height);
            graphics.drawLine(0, height, 3, height);
            graphics.setColor(Color.black);
            graphics.drawLine(1, 1, 1, height - 2);
            graphics.translate(-(trackRect.x + n2), -trackRect.y);
        }
    }
    
    public void paintTicks(final Graphics graphics) {
        final Rectangle tickRect = this.tickRect;
        graphics.setColor(DefaultLookup.getColor(this.slider, this, "Slider.tickColor", Color.black));
        if (this.slider.getOrientation() == 0) {
            graphics.translate(0, tickRect.y);
            if (this.slider.getMinorTickSpacing() > 0) {
                for (int i = this.slider.getMinimum(); i <= this.slider.getMaximum(); i += this.slider.getMinorTickSpacing()) {
                    this.paintMinorTickForHorizSlider(graphics, tickRect, this.xPositionForValue(i));
                    if (Integer.MAX_VALUE - this.slider.getMinorTickSpacing() < i) {
                        break;
                    }
                }
            }
            if (this.slider.getMajorTickSpacing() > 0) {
                for (int j = this.slider.getMinimum(); j <= this.slider.getMaximum(); j += this.slider.getMajorTickSpacing()) {
                    this.paintMajorTickForHorizSlider(graphics, tickRect, this.xPositionForValue(j));
                    if (Integer.MAX_VALUE - this.slider.getMajorTickSpacing() < j) {
                        break;
                    }
                }
            }
            graphics.translate(0, -tickRect.y);
        }
        else {
            graphics.translate(tickRect.x, 0);
            if (this.slider.getMinorTickSpacing() > 0) {
                int n = 0;
                if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
                    n = tickRect.width - tickRect.width / 2;
                    graphics.translate(n, 0);
                }
                for (int k = this.slider.getMinimum(); k <= this.slider.getMaximum(); k += this.slider.getMinorTickSpacing()) {
                    this.paintMinorTickForVertSlider(graphics, tickRect, this.yPositionForValue(k));
                    if (Integer.MAX_VALUE - this.slider.getMinorTickSpacing() < k) {
                        break;
                    }
                }
                if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
                    graphics.translate(-n, 0);
                }
            }
            if (this.slider.getMajorTickSpacing() > 0) {
                if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
                    graphics.translate(2, 0);
                }
                for (int l = this.slider.getMinimum(); l <= this.slider.getMaximum(); l += this.slider.getMajorTickSpacing()) {
                    this.paintMajorTickForVertSlider(graphics, tickRect, this.yPositionForValue(l));
                    if (Integer.MAX_VALUE - this.slider.getMajorTickSpacing() < l) {
                        break;
                    }
                }
                if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
                    graphics.translate(-2, 0);
                }
            }
            graphics.translate(-tickRect.x, 0);
        }
    }
    
    protected void paintMinorTickForHorizSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.drawLine(n, 0, n, rectangle.height / 2 - 1);
    }
    
    protected void paintMajorTickForHorizSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.drawLine(n, 0, n, rectangle.height - 2);
    }
    
    protected void paintMinorTickForVertSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.drawLine(0, n, rectangle.width / 2 - 1, n);
    }
    
    protected void paintMajorTickForVertSlider(final Graphics graphics, final Rectangle rectangle, final int n) {
        graphics.drawLine(0, n, rectangle.width - 2, n);
    }
    
    public void paintLabels(final Graphics graphics) {
        final Rectangle labelRect = this.labelRect;
        final Dictionary labelTable = this.slider.getLabelTable();
        if (labelTable != null) {
            final Enumeration keys = labelTable.keys();
            final int minimum = this.slider.getMinimum();
            final int maximum = this.slider.getMaximum();
            final boolean enabled = this.slider.isEnabled();
            while (keys.hasMoreElements()) {
                final Integer n = (Integer)keys.nextElement();
                final int intValue = n;
                if (intValue >= minimum && intValue <= maximum) {
                    final JComponent component = labelTable.get(n);
                    component.setEnabled(enabled);
                    if (component instanceof JLabel) {
                        final Icon icon = component.isEnabled() ? ((JLabel)component).getIcon() : ((JLabel)component).getDisabledIcon();
                        if (icon instanceof ImageIcon) {
                            Toolkit.getDefaultToolkit().checkImage(((ImageIcon)icon).getImage(), -1, -1, this.slider);
                        }
                    }
                    if (this.slider.getOrientation() == 0) {
                        graphics.translate(0, labelRect.y);
                        this.paintHorizontalLabel(graphics, intValue, component);
                        graphics.translate(0, -labelRect.y);
                    }
                    else {
                        int n2 = 0;
                        if (!BasicGraphicsUtils.isLeftToRight(this.slider)) {
                            n2 = labelRect.width - component.getPreferredSize().width;
                        }
                        graphics.translate(labelRect.x + n2, 0);
                        this.paintVerticalLabel(graphics, intValue, component);
                        graphics.translate(-labelRect.x - n2, 0);
                    }
                }
            }
        }
    }
    
    protected void paintHorizontalLabel(final Graphics graphics, final int n, final Component component) {
        final int n2 = this.xPositionForValue(n) - component.getPreferredSize().width / 2;
        graphics.translate(n2, 0);
        component.paint(graphics);
        graphics.translate(-n2, 0);
    }
    
    protected void paintVerticalLabel(final Graphics graphics, final int n, final Component component) {
        final int n2 = this.yPositionForValue(n) - component.getPreferredSize().height / 2;
        graphics.translate(0, n2);
        component.paint(graphics);
        graphics.translate(0, -n2);
    }
    
    public void paintThumb(final Graphics graphics) {
        final Rectangle thumbRect = this.thumbRect;
        final int width = thumbRect.width;
        final int height = thumbRect.height;
        graphics.translate(thumbRect.x, thumbRect.y);
        if (this.slider.isEnabled()) {
            graphics.setColor(this.slider.getBackground());
        }
        else {
            graphics.setColor(this.slider.getBackground().darker());
        }
        final Boolean b = (Boolean)this.slider.getClientProperty("Slider.paintThumbArrowShape");
        if ((!this.slider.getPaintTicks() && b == null) || b == Boolean.FALSE) {
            graphics.fillRect(0, 0, width, height);
            graphics.setColor(Color.black);
            graphics.drawLine(0, height - 1, width - 1, height - 1);
            graphics.drawLine(width - 1, 0, width - 1, height - 1);
            graphics.setColor(this.highlightColor);
            graphics.drawLine(0, 0, 0, height - 2);
            graphics.drawLine(1, 0, width - 2, 0);
            graphics.setColor(this.shadowColor);
            graphics.drawLine(1, height - 2, width - 2, height - 2);
            graphics.drawLine(width - 2, 1, width - 2, height - 3);
        }
        else if (this.slider.getOrientation() == 0) {
            final int n = width / 2;
            graphics.fillRect(1, 1, width - 3, height - 1 - n);
            final Polygon polygon = new Polygon();
            polygon.addPoint(1, height - n);
            polygon.addPoint(n - 1, height - 1);
            polygon.addPoint(width - 2, height - 1 - n);
            graphics.fillPolygon(polygon);
            graphics.setColor(this.highlightColor);
            graphics.drawLine(0, 0, width - 2, 0);
            graphics.drawLine(0, 1, 0, height - 1 - n);
            graphics.drawLine(0, height - n, n - 1, height - 1);
            graphics.setColor(Color.black);
            graphics.drawLine(width - 1, 0, width - 1, height - 2 - n);
            graphics.drawLine(width - 1, height - 1 - n, width - 1 - n, height - 1);
            graphics.setColor(this.shadowColor);
            graphics.drawLine(width - 2, 1, width - 2, height - 2 - n);
            graphics.drawLine(width - 2, height - 1 - n, width - 1 - n, height - 2);
        }
        else {
            final int n2 = height / 2;
            if (BasicGraphicsUtils.isLeftToRight(this.slider)) {
                graphics.fillRect(1, 1, width - 1 - n2, height - 3);
                final Polygon polygon2 = new Polygon();
                polygon2.addPoint(width - n2 - 1, 0);
                polygon2.addPoint(width - 1, n2);
                polygon2.addPoint(width - 1 - n2, height - 2);
                graphics.fillPolygon(polygon2);
                graphics.setColor(this.highlightColor);
                graphics.drawLine(0, 0, 0, height - 2);
                graphics.drawLine(1, 0, width - 1 - n2, 0);
                graphics.drawLine(width - n2 - 1, 0, width - 1, n2);
                graphics.setColor(Color.black);
                graphics.drawLine(0, height - 1, width - 2 - n2, height - 1);
                graphics.drawLine(width - 1 - n2, height - 1, width - 1, height - 1 - n2);
                graphics.setColor(this.shadowColor);
                graphics.drawLine(1, height - 2, width - 2 - n2, height - 2);
                graphics.drawLine(width - 1 - n2, height - 2, width - 2, height - n2 - 1);
            }
            else {
                graphics.fillRect(5, 1, width - 1 - n2, height - 3);
                final Polygon polygon3 = new Polygon();
                polygon3.addPoint(n2, 0);
                polygon3.addPoint(0, n2);
                polygon3.addPoint(n2, height - 2);
                graphics.fillPolygon(polygon3);
                graphics.setColor(this.highlightColor);
                graphics.drawLine(n2 - 1, 0, width - 2, 0);
                graphics.drawLine(0, n2, n2, 0);
                graphics.setColor(Color.black);
                graphics.drawLine(0, height - 1 - n2, n2, height - 1);
                graphics.drawLine(n2, height - 1, width - 1, height - 1);
                graphics.setColor(this.shadowColor);
                graphics.drawLine(n2, height - 2, width - 2, height - 2);
                graphics.drawLine(width - 1, 1, width - 1, height - 2);
            }
        }
        graphics.translate(-thumbRect.x, -thumbRect.y);
    }
    
    public void setThumbLocation(final int n, final int n2) {
        BasicSliderUI.unionRect.setBounds(this.thumbRect);
        this.thumbRect.setLocation(n, n2);
        SwingUtilities.computeUnion(this.thumbRect.x, this.thumbRect.y, this.thumbRect.width, this.thumbRect.height, BasicSliderUI.unionRect);
        this.slider.repaint(BasicSliderUI.unionRect.x, BasicSliderUI.unionRect.y, BasicSliderUI.unionRect.width, BasicSliderUI.unionRect.height);
    }
    
    public void scrollByBlock(final int n) {
        synchronized (this.slider) {
            int n2 = (this.slider.getMaximum() - this.slider.getMinimum()) / 10;
            if (n2 == 0) {
                n2 = 1;
            }
            if (this.slider.getSnapToTicks()) {
                final int tickSpacing = this.getTickSpacing();
                if (n2 < tickSpacing) {
                    n2 = tickSpacing;
                }
            }
            this.slider.setValue(this.slider.getValue() + n2 * ((n > 0) ? 1 : -1));
        }
    }
    
    public void scrollByUnit(final int n) {
        synchronized (this.slider) {
            int n2 = (n > 0) ? 1 : -1;
            if (this.slider.getSnapToTicks()) {
                n2 *= this.getTickSpacing();
            }
            this.slider.setValue(this.slider.getValue() + n2);
        }
    }
    
    protected void scrollDueToClickInTrack(final int n) {
        this.scrollByBlock(n);
    }
    
    protected int xPositionForValue(final int n) {
        final int minimum = this.slider.getMinimum();
        final double n2 = this.trackRect.width / (this.slider.getMaximum() - (double)minimum);
        final int x = this.trackRect.x;
        final int n3 = this.trackRect.x + (this.trackRect.width - 1);
        int n4;
        if (!this.drawInverted()) {
            n4 = (int)(x + Math.round(n2 * (n - (double)minimum)));
        }
        else {
            n4 = (int)(n3 - Math.round(n2 * (n - (double)minimum)));
        }
        return Math.min(n3, Math.max(x, n4));
    }
    
    protected int yPositionForValue(final int n) {
        return this.yPositionForValue(n, this.trackRect.y, this.trackRect.height);
    }
    
    protected int yPositionForValue(final int n, final int n2, final int n3) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        final double n4 = n3 / (maximum - (double)minimum);
        final int n5 = n2 + (n3 - 1);
        int n6;
        if (!this.drawInverted()) {
            n6 = (int)(n2 + Math.round(n4 * (maximum - (double)n)));
        }
        else {
            n6 = (int)(n2 + Math.round(n4 * (n - (double)minimum)));
        }
        return Math.min(n5, Math.max(n2, n6));
    }
    
    public int valueForYPosition(final int n) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        final int height = this.trackRect.height;
        final int y = this.trackRect.y;
        final int n2 = this.trackRect.y + (this.trackRect.height - 1);
        int n3;
        if (n <= y) {
            n3 = (this.drawInverted() ? minimum : maximum);
        }
        else if (n >= n2) {
            n3 = (this.drawInverted() ? maximum : minimum);
        }
        else {
            final int n4 = (int)Math.round((n - y) * ((maximum - (double)minimum) / height));
            n3 = (this.drawInverted() ? (minimum + n4) : (maximum - n4));
        }
        return n3;
    }
    
    public int valueForXPosition(final int n) {
        final int minimum = this.slider.getMinimum();
        final int maximum = this.slider.getMaximum();
        final int width = this.trackRect.width;
        final int x = this.trackRect.x;
        final int n2 = this.trackRect.x + (this.trackRect.width - 1);
        int n3;
        if (n <= x) {
            n3 = (this.drawInverted() ? maximum : minimum);
        }
        else if (n >= n2) {
            n3 = (this.drawInverted() ? minimum : maximum);
        }
        else {
            final int n4 = (int)Math.round((n - x) * ((maximum - (double)minimum) / width));
            n3 = (this.drawInverted() ? (maximum - n4) : (minimum + n4));
        }
        return n3;
    }
    
    static {
        SHARED_ACTION = new Actions();
        BasicSliderUI.unionRect = new Rectangle();
    }
    
    public class PropertyChangeHandler implements PropertyChangeListener
    {
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            BasicSliderUI.this.getHandler().propertyChange(propertyChangeEvent);
        }
    }
    
    private class Handler implements ChangeListener, ComponentListener, FocusListener, PropertyChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            if (!BasicSliderUI.this.isDragging) {
                BasicSliderUI.this.calculateThumbLocation();
                BasicSliderUI.this.slider.repaint();
            }
            BasicSliderUI.this.lastValue = BasicSliderUI.this.slider.getValue();
        }
        
        @Override
        public void componentHidden(final ComponentEvent componentEvent) {
        }
        
        @Override
        public void componentMoved(final ComponentEvent componentEvent) {
        }
        
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            BasicSliderUI.this.calculateGeometry();
            BasicSliderUI.this.slider.repaint();
        }
        
        @Override
        public void componentShown(final ComponentEvent componentEvent) {
        }
        
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicSliderUI.this.slider.repaint();
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicSliderUI.this.slider.repaint();
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            final String propertyName = propertyChangeEvent.getPropertyName();
            if (propertyName == "orientation" || propertyName == "inverted" || propertyName == "labelTable" || propertyName == "majorTickSpacing" || propertyName == "minorTickSpacing" || propertyName == "paintTicks" || propertyName == "paintTrack" || propertyName == "font" || propertyName == "paintLabels" || propertyName == "Slider.paintThumbArrowShape") {
                BasicSliderUI.this.checkedLabelBaselines = false;
                BasicSliderUI.this.calculateGeometry();
                BasicSliderUI.this.slider.repaint();
            }
            else if (propertyName == "componentOrientation") {
                BasicSliderUI.this.calculateGeometry();
                BasicSliderUI.this.slider.repaint();
                SwingUtilities.replaceUIInputMap(BasicSliderUI.this.slider, 0, BasicSliderUI.this.getInputMap(0, BasicSliderUI.this.slider));
            }
            else if (propertyName == "model") {
                ((BoundedRangeModel)propertyChangeEvent.getOldValue()).removeChangeListener(BasicSliderUI.this.changeListener);
                ((BoundedRangeModel)propertyChangeEvent.getNewValue()).addChangeListener(BasicSliderUI.this.changeListener);
                BasicSliderUI.this.calculateThumbLocation();
                BasicSliderUI.this.slider.repaint();
            }
        }
    }
    
    public class ChangeHandler implements ChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            BasicSliderUI.this.getHandler().stateChanged(changeEvent);
        }
    }
    
    public class TrackListener extends MouseInputAdapter
    {
        protected transient int offset;
        protected transient int currentMouseX;
        protected transient int currentMouseY;
        
        @Override
        public void mouseReleased(final MouseEvent mouseEvent) {
            if (!BasicSliderUI.this.slider.isEnabled()) {
                return;
            }
            this.offset = 0;
            BasicSliderUI.this.scrollTimer.stop();
            BasicSliderUI.this.isDragging = false;
            BasicSliderUI.this.slider.setValueIsAdjusting(false);
            BasicSliderUI.this.slider.repaint();
        }
        
        @Override
        public void mousePressed(final MouseEvent mouseEvent) {
            if (!BasicSliderUI.this.slider.isEnabled()) {
                return;
            }
            BasicSliderUI.this.calculateGeometry();
            this.currentMouseX = mouseEvent.getX();
            this.currentMouseY = mouseEvent.getY();
            if (BasicSliderUI.this.slider.isRequestFocusEnabled()) {
                BasicSliderUI.this.slider.requestFocus();
            }
            if (BasicSliderUI.this.thumbRect.contains(this.currentMouseX, this.currentMouseY)) {
                if (UIManager.getBoolean("Slider.onlyLeftMouseButtonDrag") && !SwingUtilities.isLeftMouseButton(mouseEvent)) {
                    return;
                }
                switch (BasicSliderUI.this.slider.getOrientation()) {
                    case 1: {
                        this.offset = this.currentMouseY - BasicSliderUI.this.thumbRect.y;
                        break;
                    }
                    case 0: {
                        this.offset = this.currentMouseX - BasicSliderUI.this.thumbRect.x;
                        break;
                    }
                }
                BasicSliderUI.this.isDragging = true;
            }
            else {
                if (!SwingUtilities.isLeftMouseButton(mouseEvent)) {
                    return;
                }
                BasicSliderUI.this.isDragging = false;
                BasicSliderUI.this.slider.setValueIsAdjusting(true);
                final Dimension size = BasicSliderUI.this.slider.getSize();
                int direction = 1;
                switch (BasicSliderUI.this.slider.getOrientation()) {
                    case 1: {
                        if (BasicSliderUI.this.thumbRect.isEmpty()) {
                            final int n = size.height / 2;
                            if (!BasicSliderUI.this.drawInverted()) {
                                direction = ((this.currentMouseY < n) ? 1 : -1);
                            }
                            else {
                                direction = ((this.currentMouseY < n) ? -1 : 1);
                            }
                            break;
                        }
                        final int y = BasicSliderUI.this.thumbRect.y;
                        if (!BasicSliderUI.this.drawInverted()) {
                            direction = ((this.currentMouseY < y) ? 1 : -1);
                        }
                        else {
                            direction = ((this.currentMouseY < y) ? -1 : 1);
                        }
                        break;
                    }
                    case 0: {
                        if (BasicSliderUI.this.thumbRect.isEmpty()) {
                            final int n2 = size.width / 2;
                            if (!BasicSliderUI.this.drawInverted()) {
                                direction = ((this.currentMouseX < n2) ? -1 : 1);
                            }
                            else {
                                direction = ((this.currentMouseX < n2) ? 1 : -1);
                            }
                            break;
                        }
                        final int x = BasicSliderUI.this.thumbRect.x;
                        if (!BasicSliderUI.this.drawInverted()) {
                            direction = ((this.currentMouseX < x) ? -1 : 1);
                            break;
                        }
                        direction = ((this.currentMouseX < x) ? 1 : -1);
                        break;
                    }
                }
                if (this.shouldScroll(direction)) {
                    BasicSliderUI.this.scrollDueToClickInTrack(direction);
                }
                if (this.shouldScroll(direction)) {
                    BasicSliderUI.this.scrollTimer.stop();
                    BasicSliderUI.this.scrollListener.setDirection(direction);
                    BasicSliderUI.this.scrollTimer.start();
                }
            }
        }
        
        public boolean shouldScroll(final int n) {
            final Rectangle thumbRect = BasicSliderUI.this.thumbRect;
            if (BasicSliderUI.this.slider.getOrientation() == 1) {
                Label_0056: {
                    if (BasicSliderUI.this.drawInverted()) {
                        if (n >= 0) {
                            break Label_0056;
                        }
                    }
                    else if (n <= 0) {
                        break Label_0056;
                    }
                    if (thumbRect.y <= this.currentMouseY) {
                        return false;
                    }
                    return (n <= 0 || BasicSliderUI.this.slider.getValue() + BasicSliderUI.this.slider.getExtent() < BasicSliderUI.this.slider.getMaximum()) && (n >= 0 || BasicSliderUI.this.slider.getValue() > BasicSliderUI.this.slider.getMinimum());
                }
                if (thumbRect.y + thumbRect.height >= this.currentMouseY) {
                    return false;
                }
            }
            else {
                Label_0113: {
                    if (BasicSliderUI.this.drawInverted()) {
                        if (n >= 0) {
                            break Label_0113;
                        }
                    }
                    else if (n <= 0) {
                        break Label_0113;
                    }
                    if (thumbRect.x + thumbRect.width >= this.currentMouseX) {
                        return false;
                    }
                    return (n <= 0 || BasicSliderUI.this.slider.getValue() + BasicSliderUI.this.slider.getExtent() < BasicSliderUI.this.slider.getMaximum()) && (n >= 0 || BasicSliderUI.this.slider.getValue() > BasicSliderUI.this.slider.getMinimum());
                }
                if (thumbRect.x <= this.currentMouseX) {
                    return false;
                }
            }
            return (n <= 0 || BasicSliderUI.this.slider.getValue() + BasicSliderUI.this.slider.getExtent() < BasicSliderUI.this.slider.getMaximum()) && (n >= 0 || BasicSliderUI.this.slider.getValue() > BasicSliderUI.this.slider.getMinimum());
        }
        
        @Override
        public void mouseDragged(final MouseEvent mouseEvent) {
            if (!BasicSliderUI.this.slider.isEnabled()) {
                return;
            }
            this.currentMouseX = mouseEvent.getX();
            this.currentMouseY = mouseEvent.getY();
            if (!BasicSliderUI.this.isDragging) {
                return;
            }
            BasicSliderUI.this.slider.setValueIsAdjusting(true);
            switch (BasicSliderUI.this.slider.getOrientation()) {
                case 1: {
                    final int n = BasicSliderUI.this.thumbRect.height / 2;
                    final int n2 = mouseEvent.getY() - this.offset;
                    int y = BasicSliderUI.this.trackRect.y;
                    int n3 = BasicSliderUI.this.trackRect.y + (BasicSliderUI.this.trackRect.height - 1);
                    final int yPositionForValue = BasicSliderUI.this.yPositionForValue(BasicSliderUI.this.slider.getMaximum() - BasicSliderUI.this.slider.getExtent());
                    if (BasicSliderUI.this.drawInverted()) {
                        n3 = yPositionForValue;
                    }
                    else {
                        y = yPositionForValue;
                    }
                    final int min = Math.min(Math.max(n2, y - n), n3 - n);
                    BasicSliderUI.this.setThumbLocation(BasicSliderUI.this.thumbRect.x, min);
                    BasicSliderUI.this.slider.setValue(BasicSliderUI.this.valueForYPosition(min + n));
                    break;
                }
                case 0: {
                    final int n4 = BasicSliderUI.this.thumbRect.width / 2;
                    final int n5 = mouseEvent.getX() - this.offset;
                    int x = BasicSliderUI.this.trackRect.x;
                    int n6 = BasicSliderUI.this.trackRect.x + (BasicSliderUI.this.trackRect.width - 1);
                    final int xPositionForValue = BasicSliderUI.this.xPositionForValue(BasicSliderUI.this.slider.getMaximum() - BasicSliderUI.this.slider.getExtent());
                    if (BasicSliderUI.this.drawInverted()) {
                        x = xPositionForValue;
                    }
                    else {
                        n6 = xPositionForValue;
                    }
                    final int min2 = Math.min(Math.max(n5, x - n4), n6 - n4);
                    BasicSliderUI.this.setThumbLocation(min2, BasicSliderUI.this.thumbRect.y);
                    BasicSliderUI.this.slider.setValue(BasicSliderUI.this.valueForXPosition(min2 + n4));
                    break;
                }
            }
        }
        
        @Override
        public void mouseMoved(final MouseEvent mouseEvent) {
        }
    }
    
    public class ScrollListener implements ActionListener
    {
        int direction;
        boolean useBlockIncrement;
        
        public ScrollListener() {
            this.direction = 1;
            this.direction = 1;
            this.useBlockIncrement = true;
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
                BasicSliderUI.this.scrollByBlock(this.direction);
            }
            else {
                BasicSliderUI.this.scrollByUnit(this.direction);
            }
            if (!BasicSliderUI.this.trackListener.shouldScroll(this.direction)) {
                ((Timer)actionEvent.getSource()).stop();
            }
        }
    }
    
    public class ComponentHandler extends ComponentAdapter
    {
        @Override
        public void componentResized(final ComponentEvent componentEvent) {
            BasicSliderUI.this.getHandler().componentResized(componentEvent);
        }
    }
    
    public class FocusHandler implements FocusListener
    {
        @Override
        public void focusGained(final FocusEvent focusEvent) {
            BasicSliderUI.this.getHandler().focusGained(focusEvent);
        }
        
        @Override
        public void focusLost(final FocusEvent focusEvent) {
            BasicSliderUI.this.getHandler().focusLost(focusEvent);
        }
    }
    
    public class ActionScroller extends AbstractAction
    {
        int dir;
        boolean block;
        JSlider slider;
        
        public ActionScroller(final JSlider slider, final int dir, final boolean block) {
            this.dir = dir;
            this.block = block;
            this.slider = slider;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            BasicSliderUI.SHARED_ACTION.scroll(this.slider, BasicSliderUI.this, this.dir, this.block);
        }
        
        @Override
        public boolean isEnabled() {
            boolean enabled = true;
            if (this.slider != null) {
                enabled = this.slider.isEnabled();
            }
            return enabled;
        }
    }
    
    static class SharedActionScroller extends AbstractAction
    {
        int dir;
        boolean block;
        
        public SharedActionScroller(final int dir, final boolean block) {
            this.dir = dir;
            this.block = block;
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JSlider slider = (JSlider)actionEvent.getSource();
            final BasicSliderUI basicSliderUI = (BasicSliderUI)BasicLookAndFeel.getUIOfType(slider.getUI(), BasicSliderUI.class);
            if (basicSliderUI == null) {
                return;
            }
            BasicSliderUI.SHARED_ACTION.scroll(slider, basicSliderUI, this.dir, this.block);
        }
    }
    
    private static class Actions extends UIAction
    {
        public static final String POSITIVE_UNIT_INCREMENT = "positiveUnitIncrement";
        public static final String POSITIVE_BLOCK_INCREMENT = "positiveBlockIncrement";
        public static final String NEGATIVE_UNIT_INCREMENT = "negativeUnitIncrement";
        public static final String NEGATIVE_BLOCK_INCREMENT = "negativeBlockIncrement";
        public static final String MIN_SCROLL_INCREMENT = "minScroll";
        public static final String MAX_SCROLL_INCREMENT = "maxScroll";
        
        Actions() {
            super(null);
        }
        
        public Actions(final String s) {
            super(s);
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            final JSlider slider = (JSlider)actionEvent.getSource();
            final BasicSliderUI basicSliderUI = (BasicSliderUI)BasicLookAndFeel.getUIOfType(slider.getUI(), BasicSliderUI.class);
            final String name = this.getName();
            if (basicSliderUI == null) {
                return;
            }
            if ("positiveUnitIncrement" == name) {
                this.scroll(slider, basicSliderUI, 1, false);
            }
            else if ("negativeUnitIncrement" == name) {
                this.scroll(slider, basicSliderUI, -1, false);
            }
            else if ("positiveBlockIncrement" == name) {
                this.scroll(slider, basicSliderUI, 1, true);
            }
            else if ("negativeBlockIncrement" == name) {
                this.scroll(slider, basicSliderUI, -1, true);
            }
            else if ("minScroll" == name) {
                this.scroll(slider, basicSliderUI, -2, false);
            }
            else if ("maxScroll" == name) {
                this.scroll(slider, basicSliderUI, 2, false);
            }
        }
        
        private void scroll(final JSlider slider, final BasicSliderUI basicSliderUI, int n, final boolean b) {
            final boolean inverted = slider.getInverted();
            if (n == -1 || n == 1) {
                if (inverted) {
                    n = ((n == 1) ? -1 : 1);
                }
                if (b) {
                    basicSliderUI.scrollByBlock(n);
                }
                else {
                    basicSliderUI.scrollByUnit(n);
                }
            }
            else {
                if (inverted) {
                    n = ((n == -2) ? 2 : -2);
                }
                slider.setValue((n == -2) ? slider.getMinimum() : slider.getMaximum());
            }
        }
    }
}
