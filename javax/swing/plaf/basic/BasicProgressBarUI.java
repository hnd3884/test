package javax.swing.plaf.basic;

import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyListener;
import javax.swing.SwingUtilities;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import sun.swing.SwingUtilities2;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Graphics;
import javax.swing.BoundedRangeModel;
import sun.swing.DefaultLookup;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Insets;
import java.awt.FontMetrics;
import java.beans.PropertyChangeListener;
import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Rectangle;
import javax.swing.event.ChangeListener;
import javax.swing.JProgressBar;
import java.awt.Color;
import javax.swing.plaf.ProgressBarUI;

public class BasicProgressBarUI extends ProgressBarUI
{
    private int cachedPercent;
    private int cellLength;
    private int cellSpacing;
    private Color selectionForeground;
    private Color selectionBackground;
    private Animator animator;
    protected JProgressBar progressBar;
    protected ChangeListener changeListener;
    private Handler handler;
    private int animationIndex;
    private int numFrames;
    private int repaintInterval;
    private int cycleTime;
    private static boolean ADJUSTTIMER;
    protected Rectangle boxRect;
    private Rectangle nextPaintRect;
    private Rectangle componentInnards;
    private Rectangle oldComponentInnards;
    private double delta;
    private int maxPosition;
    
    public BasicProgressBarUI() {
        this.animationIndex = 0;
        this.delta = 0.0;
        this.maxPosition = 0;
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new BasicProgressBarUI();
    }
    
    @Override
    public void installUI(final JComponent component) {
        this.progressBar = (JProgressBar)component;
        this.installDefaults();
        this.installListeners();
        if (this.progressBar.isIndeterminate()) {
            this.initIndeterminateValues();
        }
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        if (this.progressBar.isIndeterminate()) {
            this.cleanUpIndeterminateValues();
        }
        this.uninstallDefaults();
        this.uninstallListeners();
        this.progressBar = null;
    }
    
    protected void installDefaults() {
        LookAndFeel.installProperty(this.progressBar, "opaque", Boolean.TRUE);
        LookAndFeel.installBorder(this.progressBar, "ProgressBar.border");
        LookAndFeel.installColorsAndFont(this.progressBar, "ProgressBar.background", "ProgressBar.foreground", "ProgressBar.font");
        this.cellLength = UIManager.getInt("ProgressBar.cellLength");
        if (this.cellLength == 0) {
            this.cellLength = 1;
        }
        this.cellSpacing = UIManager.getInt("ProgressBar.cellSpacing");
        this.selectionForeground = UIManager.getColor("ProgressBar.selectionForeground");
        this.selectionBackground = UIManager.getColor("ProgressBar.selectionBackground");
    }
    
    protected void uninstallDefaults() {
        LookAndFeel.uninstallBorder(this.progressBar);
    }
    
    protected void installListeners() {
        this.changeListener = this.getHandler();
        this.progressBar.addChangeListener(this.changeListener);
        this.progressBar.addPropertyChangeListener(this.getHandler());
    }
    
    private Handler getHandler() {
        if (this.handler == null) {
            this.handler = new Handler();
        }
        return this.handler;
    }
    
    protected void startAnimationTimer() {
        if (this.animator == null) {
            this.animator = new Animator();
        }
        this.animator.start(this.getRepaintInterval());
    }
    
    protected void stopAnimationTimer() {
        if (this.animator != null) {
            this.animator.stop();
        }
    }
    
    protected void uninstallListeners() {
        this.progressBar.removeChangeListener(this.changeListener);
        this.progressBar.removePropertyChangeListener(this.getHandler());
        this.handler = null;
    }
    
    @Override
    public int getBaseline(final JComponent component, final int n, int n2) {
        super.getBaseline(component, n, n2);
        if (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
            final FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
            final Insets insets = this.progressBar.getInsets();
            final int top = insets.top;
            n2 = n2 - insets.top - insets.bottom;
            return top + (n2 + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2;
        }
        return -1;
    }
    
    @Override
    public Component.BaselineResizeBehavior getBaselineResizeBehavior(final JComponent component) {
        super.getBaselineResizeBehavior(component);
        if (this.progressBar.isStringPainted() && this.progressBar.getOrientation() == 0) {
            return Component.BaselineResizeBehavior.CENTER_OFFSET;
        }
        return Component.BaselineResizeBehavior.OTHER;
    }
    
    protected Dimension getPreferredInnerHorizontal() {
        Dimension dimension = (Dimension)DefaultLookup.get(this.progressBar, this, "ProgressBar.horizontalSize");
        if (dimension == null) {
            dimension = new Dimension(146, 12);
        }
        return dimension;
    }
    
    protected Dimension getPreferredInnerVertical() {
        Dimension dimension = (Dimension)DefaultLookup.get(this.progressBar, this, "ProgressBar.verticalSize");
        if (dimension == null) {
            dimension = new Dimension(12, 146);
        }
        return dimension;
    }
    
    protected Color getSelectionForeground() {
        return this.selectionForeground;
    }
    
    protected Color getSelectionBackground() {
        return this.selectionBackground;
    }
    
    private int getCachedPercent() {
        return this.cachedPercent;
    }
    
    private void setCachedPercent(final int cachedPercent) {
        this.cachedPercent = cachedPercent;
    }
    
    protected int getCellLength() {
        if (this.progressBar.isStringPainted()) {
            return 1;
        }
        return this.cellLength;
    }
    
    protected void setCellLength(final int cellLength) {
        this.cellLength = cellLength;
    }
    
    protected int getCellSpacing() {
        if (this.progressBar.isStringPainted()) {
            return 0;
        }
        return this.cellSpacing;
    }
    
    protected void setCellSpacing(final int cellSpacing) {
        this.cellSpacing = cellSpacing;
    }
    
    protected int getAmountFull(final Insets insets, final int n, final int n2) {
        int n3 = 0;
        final BoundedRangeModel model = this.progressBar.getModel();
        if (model.getMaximum() - model.getMinimum() != 0) {
            if (this.progressBar.getOrientation() == 0) {
                n3 = (int)Math.round(n * this.progressBar.getPercentComplete());
            }
            else {
                n3 = (int)Math.round(n2 * this.progressBar.getPercentComplete());
            }
        }
        return n3;
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        if (this.progressBar.isIndeterminate()) {
            this.paintIndeterminate(graphics, component);
        }
        else {
            this.paintDeterminate(graphics, component);
        }
    }
    
    protected Rectangle getBox(Rectangle genericBox) {
        final int animationIndex = this.getAnimationIndex();
        final int n = this.numFrames / 2;
        if (this.sizeChanged() || this.delta == 0.0 || this.maxPosition == 0.0) {
            this.updateSizes();
        }
        genericBox = this.getGenericBox(genericBox);
        if (genericBox == null) {
            return null;
        }
        if (n <= 0) {
            return null;
        }
        if (this.progressBar.getOrientation() == 0) {
            if (animationIndex < n) {
                genericBox.x = this.componentInnards.x + (int)Math.round(this.delta * animationIndex);
            }
            else {
                genericBox.x = this.maxPosition - (int)Math.round(this.delta * (animationIndex - n));
            }
        }
        else if (animationIndex < n) {
            genericBox.y = this.componentInnards.y + (int)Math.round(this.delta * animationIndex);
        }
        else {
            genericBox.y = this.maxPosition - (int)Math.round(this.delta * (animationIndex - n));
        }
        return genericBox;
    }
    
    private void updateSizes() {
        if (this.progressBar.getOrientation() == 0) {
            this.maxPosition = this.componentInnards.x + this.componentInnards.width - this.getBoxLength(this.componentInnards.width, this.componentInnards.height);
        }
        else {
            this.maxPosition = this.componentInnards.y + this.componentInnards.height - this.getBoxLength(this.componentInnards.height, this.componentInnards.width);
        }
        this.delta = 2.0 * this.maxPosition / this.numFrames;
    }
    
    private Rectangle getGenericBox(Rectangle rectangle) {
        if (rectangle == null) {
            rectangle = new Rectangle();
        }
        if (this.progressBar.getOrientation() == 0) {
            rectangle.width = this.getBoxLength(this.componentInnards.width, this.componentInnards.height);
            if (rectangle.width < 0) {
                rectangle = null;
            }
            else {
                rectangle.height = this.componentInnards.height;
                rectangle.y = this.componentInnards.y;
            }
        }
        else {
            rectangle.height = this.getBoxLength(this.componentInnards.height, this.componentInnards.width);
            if (rectangle.height < 0) {
                rectangle = null;
            }
            else {
                rectangle.width = this.componentInnards.width;
                rectangle.x = this.componentInnards.x;
            }
        }
        return rectangle;
    }
    
    protected int getBoxLength(final int n, final int n2) {
        return (int)Math.round(n / 6.0);
    }
    
    protected void paintIndeterminate(final Graphics graphics, final JComponent component) {
        if (!(graphics instanceof Graphics2D)) {
            return;
        }
        final Insets insets = this.progressBar.getInsets();
        final int n = this.progressBar.getWidth() - (insets.right + insets.left);
        final int n2 = this.progressBar.getHeight() - (insets.top + insets.bottom);
        if (n <= 0 || n2 <= 0) {
            return;
        }
        final Graphics2D graphics2D = (Graphics2D)graphics;
        this.boxRect = this.getBox(this.boxRect);
        if (this.boxRect != null) {
            graphics2D.setColor(this.progressBar.getForeground());
            graphics2D.fillRect(this.boxRect.x, this.boxRect.y, this.boxRect.width, this.boxRect.height);
        }
        if (this.progressBar.isStringPainted()) {
            if (this.progressBar.getOrientation() == 0) {
                this.paintString(graphics2D, insets.left, insets.top, n, n2, this.boxRect.x, this.boxRect.width, insets);
            }
            else {
                this.paintString(graphics2D, insets.left, insets.top, n, n2, this.boxRect.y, this.boxRect.height, insets);
            }
        }
    }
    
    protected void paintDeterminate(final Graphics graphics, final JComponent component) {
        if (!(graphics instanceof Graphics2D)) {
            return;
        }
        final Insets insets = this.progressBar.getInsets();
        final int n = this.progressBar.getWidth() - (insets.right + insets.left);
        final int n2 = this.progressBar.getHeight() - (insets.top + insets.bottom);
        if (n <= 0 || n2 <= 0) {
            return;
        }
        final int cellLength = this.getCellLength();
        final int cellSpacing = this.getCellSpacing();
        final int amountFull = this.getAmountFull(insets, n, n2);
        final Graphics2D graphics2D = (Graphics2D)graphics;
        graphics2D.setColor(this.progressBar.getForeground());
        if (this.progressBar.getOrientation() == 0) {
            if (cellSpacing == 0 && amountFull > 0) {
                graphics2D.setStroke(new BasicStroke((float)n2, 0, 2));
            }
            else {
                graphics2D.setStroke(new BasicStroke((float)n2, 0, 2, 0.0f, new float[] { (float)cellLength, (float)cellSpacing }, 0.0f));
            }
            if (BasicGraphicsUtils.isLeftToRight(component)) {
                graphics2D.drawLine(insets.left, n2 / 2 + insets.top, amountFull + insets.left, n2 / 2 + insets.top);
            }
            else {
                graphics2D.drawLine(n + insets.left, n2 / 2 + insets.top, n + insets.left - amountFull, n2 / 2 + insets.top);
            }
        }
        else {
            if (cellSpacing == 0 && amountFull > 0) {
                graphics2D.setStroke(new BasicStroke((float)n, 0, 2));
            }
            else {
                graphics2D.setStroke(new BasicStroke((float)n, 0, 2, 0.0f, new float[] { (float)cellLength, (float)cellSpacing }, 0.0f));
            }
            graphics2D.drawLine(n / 2 + insets.left, insets.top + n2, n / 2 + insets.left, insets.top + n2 - amountFull);
        }
        if (this.progressBar.isStringPainted()) {
            this.paintString(graphics, insets.left, insets.top, n, n2, amountFull, insets);
        }
    }
    
    protected void paintString(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final Insets insets) {
        if (this.progressBar.getOrientation() == 0) {
            if (BasicGraphicsUtils.isLeftToRight(this.progressBar)) {
                if (this.progressBar.isIndeterminate()) {
                    this.boxRect = this.getBox(this.boxRect);
                    this.paintString(graphics, n, n2, n3, n4, this.boxRect.x, this.boxRect.width, insets);
                }
                else {
                    this.paintString(graphics, n, n2, n3, n4, n, n5, insets);
                }
            }
            else {
                this.paintString(graphics, n, n2, n3, n4, n + n3 - n5, n5, insets);
            }
        }
        else if (this.progressBar.isIndeterminate()) {
            this.boxRect = this.getBox(this.boxRect);
            this.paintString(graphics, n, n2, n3, n4, this.boxRect.y, this.boxRect.height, insets);
        }
        else {
            this.paintString(graphics, n, n2, n3, n4, n2 + n4 - n5, n5, insets);
        }
    }
    
    private void paintString(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final Insets insets) {
        if (!(graphics instanceof Graphics2D)) {
            return;
        }
        final Graphics2D graphics2D = (Graphics2D)graphics;
        final String string = this.progressBar.getString();
        graphics2D.setFont(this.progressBar.getFont());
        final Point stringPlacement = this.getStringPlacement(graphics2D, string, n, n2, n3, n4);
        final Rectangle clipBounds = graphics2D.getClipBounds();
        if (this.progressBar.getOrientation() == 0) {
            graphics2D.setColor(this.getSelectionBackground());
            SwingUtilities2.drawString(this.progressBar, graphics2D, string, stringPlacement.x, stringPlacement.y);
            graphics2D.setColor(this.getSelectionForeground());
            graphics2D.clipRect(n5, n2, n6, n4);
            SwingUtilities2.drawString(this.progressBar, graphics2D, string, stringPlacement.x, stringPlacement.y);
        }
        else {
            graphics2D.setColor(this.getSelectionBackground());
            graphics2D.setFont(this.progressBar.getFont().deriveFont(AffineTransform.getRotateInstance(1.5707963267948966)));
            final Point stringPlacement2 = this.getStringPlacement(graphics2D, string, n, n2, n3, n4);
            SwingUtilities2.drawString(this.progressBar, graphics2D, string, stringPlacement2.x, stringPlacement2.y);
            graphics2D.setColor(this.getSelectionForeground());
            graphics2D.clipRect(n, n5, n3, n6);
            SwingUtilities2.drawString(this.progressBar, graphics2D, string, stringPlacement2.x, stringPlacement2.y);
        }
        graphics2D.setClip(clipBounds);
    }
    
    protected Point getStringPlacement(final Graphics graphics, final String s, final int n, final int n2, final int n3, final int n4) {
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(this.progressBar, graphics, this.progressBar.getFont());
        final int stringWidth = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, s);
        if (this.progressBar.getOrientation() == 0) {
            return new Point(n + Math.round((float)(n3 / 2 - stringWidth / 2)), n2 + (n4 + fontMetrics.getAscent() - fontMetrics.getLeading() - fontMetrics.getDescent()) / 2);
        }
        return new Point(n + (n3 - fontMetrics.getAscent() + fontMetrics.getLeading() + fontMetrics.getDescent()) / 2, n2 + Math.round((float)(n4 / 2 - stringWidth / 2)));
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Insets insets = this.progressBar.getInsets();
        final FontMetrics fontMetrics = this.progressBar.getFontMetrics(this.progressBar.getFont());
        Dimension dimension;
        if (this.progressBar.getOrientation() == 0) {
            dimension = new Dimension(this.getPreferredInnerHorizontal());
            if (this.progressBar.isStringPainted()) {
                final int stringWidth = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, this.progressBar.getString());
                if (stringWidth > dimension.width) {
                    dimension.width = stringWidth;
                }
                final int height = fontMetrics.getHeight() + fontMetrics.getDescent();
                if (height > dimension.height) {
                    dimension.height = height;
                }
            }
        }
        else {
            dimension = new Dimension(this.getPreferredInnerVertical());
            if (this.progressBar.isStringPainted()) {
                final String string = this.progressBar.getString();
                final int width = fontMetrics.getHeight() + fontMetrics.getDescent();
                if (width > dimension.width) {
                    dimension.width = width;
                }
                final int stringWidth2 = SwingUtilities2.stringWidth(this.progressBar, fontMetrics, string);
                if (stringWidth2 > dimension.height) {
                    dimension.height = stringWidth2;
                }
            }
        }
        final Dimension dimension2 = dimension;
        dimension2.width += insets.left + insets.right;
        final Dimension dimension3 = dimension;
        dimension3.height += insets.top + insets.bottom;
        return dimension;
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(this.progressBar);
        if (this.progressBar.getOrientation() == 0) {
            preferredSize.width = 10;
        }
        else {
            preferredSize.height = 10;
        }
        return preferredSize;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(this.progressBar);
        if (this.progressBar.getOrientation() == 0) {
            preferredSize.width = 32767;
        }
        else {
            preferredSize.height = 32767;
        }
        return preferredSize;
    }
    
    protected int getAnimationIndex() {
        return this.animationIndex;
    }
    
    protected final int getFrameCount() {
        return this.numFrames;
    }
    
    protected void setAnimationIndex(final int n) {
        if (this.animationIndex == n) {
            return;
        }
        if (this.sizeChanged()) {
            this.animationIndex = n;
            this.maxPosition = 0;
            this.delta = 0.0;
            this.progressBar.repaint();
            return;
        }
        this.nextPaintRect = this.getBox(this.nextPaintRect);
        this.animationIndex = n;
        if (this.nextPaintRect != null) {
            this.boxRect = this.getBox(this.boxRect);
            if (this.boxRect != null) {
                this.nextPaintRect.add(this.boxRect);
            }
        }
        if (this.nextPaintRect != null) {
            this.progressBar.repaint(this.nextPaintRect);
        }
        else {
            this.progressBar.repaint();
        }
    }
    
    private boolean sizeChanged() {
        if (this.oldComponentInnards == null || this.componentInnards == null) {
            return true;
        }
        this.oldComponentInnards.setRect(this.componentInnards);
        this.componentInnards = SwingUtilities.calculateInnerArea(this.progressBar, this.componentInnards);
        return !this.oldComponentInnards.equals(this.componentInnards);
    }
    
    protected void incrementAnimationIndex() {
        final int animationIndex = this.getAnimationIndex() + 1;
        if (animationIndex < this.numFrames) {
            this.setAnimationIndex(animationIndex);
        }
        else {
            this.setAnimationIndex(0);
        }
    }
    
    private int getRepaintInterval() {
        return this.repaintInterval;
    }
    
    private int initRepaintInterval() {
        return this.repaintInterval = DefaultLookup.getInt(this.progressBar, this, "ProgressBar.repaintInterval", 50);
    }
    
    private int getCycleTime() {
        return this.cycleTime;
    }
    
    private int initCycleTime() {
        return this.cycleTime = DefaultLookup.getInt(this.progressBar, this, "ProgressBar.cycleTime", 3000);
    }
    
    private void initIndeterminateDefaults() {
        this.initRepaintInterval();
        this.initCycleTime();
        if (this.repaintInterval <= 0) {
            this.repaintInterval = 100;
        }
        if (this.repaintInterval > this.cycleTime) {
            this.cycleTime = this.repaintInterval * 20;
        }
        else {
            this.cycleTime = this.repaintInterval * (int)Math.ceil(this.cycleTime / (this.repaintInterval * 2.0)) * 2;
        }
    }
    
    private void initIndeterminateValues() {
        this.initIndeterminateDefaults();
        this.numFrames = this.cycleTime / this.repaintInterval;
        this.initAnimationIndex();
        this.boxRect = new Rectangle();
        this.nextPaintRect = new Rectangle();
        this.componentInnards = new Rectangle();
        this.oldComponentInnards = new Rectangle();
        this.progressBar.addHierarchyListener(this.getHandler());
        if (this.progressBar.isDisplayable()) {
            this.startAnimationTimer();
        }
    }
    
    private void cleanUpIndeterminateValues() {
        if (this.progressBar.isDisplayable()) {
            this.stopAnimationTimer();
        }
        final int n = 0;
        this.repaintInterval = n;
        this.cycleTime = n;
        final int n2 = 0;
        this.animationIndex = n2;
        this.numFrames = n2;
        this.maxPosition = 0;
        this.delta = 0.0;
        final Rectangle rectangle = null;
        this.nextPaintRect = rectangle;
        this.boxRect = rectangle;
        final Rectangle rectangle2 = null;
        this.oldComponentInnards = rectangle2;
        this.componentInnards = rectangle2;
        this.progressBar.removeHierarchyListener(this.getHandler());
    }
    
    private void initAnimationIndex() {
        if (this.progressBar.getOrientation() == 0 && BasicGraphicsUtils.isLeftToRight(this.progressBar)) {
            this.setAnimationIndex(0);
        }
        else {
            this.setAnimationIndex(this.numFrames / 2);
        }
    }
    
    static {
        BasicProgressBarUI.ADJUSTTIMER = true;
    }
    
    private class Animator implements ActionListener
    {
        private Timer timer;
        private long previousDelay;
        private int interval;
        private long lastCall;
        private int MINIMUM_DELAY;
        
        private Animator() {
            this.MINIMUM_DELAY = 5;
        }
        
        private void start(final int delay) {
            this.previousDelay = delay;
            this.lastCall = 0L;
            if (this.timer == null) {
                this.timer = new Timer(delay, this);
            }
            else {
                this.timer.setDelay(delay);
            }
            if (BasicProgressBarUI.ADJUSTTIMER) {
                this.timer.setRepeats(false);
                this.timer.setCoalesce(false);
            }
            this.timer.start();
        }
        
        private void stop() {
            this.timer.stop();
        }
        
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            if (BasicProgressBarUI.ADJUSTTIMER) {
                final long currentTimeMillis = System.currentTimeMillis();
                if (this.lastCall > 0L) {
                    int minimum_DELAY = (int)(this.previousDelay - currentTimeMillis + this.lastCall + BasicProgressBarUI.this.getRepaintInterval());
                    if (minimum_DELAY < this.MINIMUM_DELAY) {
                        minimum_DELAY = this.MINIMUM_DELAY;
                    }
                    this.timer.setInitialDelay(minimum_DELAY);
                    this.previousDelay = minimum_DELAY;
                }
                this.timer.start();
                this.lastCall = currentTimeMillis;
            }
            BasicProgressBarUI.this.incrementAnimationIndex();
        }
    }
    
    public class ChangeHandler implements ChangeListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            BasicProgressBarUI.this.getHandler().stateChanged(changeEvent);
        }
    }
    
    private class Handler implements ChangeListener, PropertyChangeListener, HierarchyListener
    {
        @Override
        public void stateChanged(final ChangeEvent changeEvent) {
            final BoundedRangeModel model = BasicProgressBarUI.this.progressBar.getModel();
            final int n = model.getMaximum() - model.getMinimum();
            final int access$700 = BasicProgressBarUI.this.getCachedPercent();
            int n2;
            if (n > 0) {
                n2 = (int)(100L * model.getValue() / n);
            }
            else {
                n2 = 0;
            }
            if (n2 != access$700) {
                BasicProgressBarUI.this.setCachedPercent(n2);
                BasicProgressBarUI.this.progressBar.repaint();
            }
        }
        
        @Override
        public void propertyChange(final PropertyChangeEvent propertyChangeEvent) {
            if ("indeterminate" == propertyChangeEvent.getPropertyName()) {
                if (BasicProgressBarUI.this.progressBar.isIndeterminate()) {
                    BasicProgressBarUI.this.initIndeterminateValues();
                }
                else {
                    BasicProgressBarUI.this.cleanUpIndeterminateValues();
                }
                BasicProgressBarUI.this.progressBar.repaint();
            }
        }
        
        @Override
        public void hierarchyChanged(final HierarchyEvent hierarchyEvent) {
            if ((hierarchyEvent.getChangeFlags() & 0x2L) != 0x0L && BasicProgressBarUI.this.progressBar.isIndeterminate()) {
                if (BasicProgressBarUI.this.progressBar.isDisplayable()) {
                    BasicProgressBarUI.this.startAnimationTimer();
                }
                else {
                    BasicProgressBarUI.this.stopAnimationTimer();
                }
            }
        }
    }
}
