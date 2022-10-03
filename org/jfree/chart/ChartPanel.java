package org.jfree.chart;

import javax.swing.JMenu;
import java.util.EventListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.jfree.ui.ExtensionFileFilter;
import javax.swing.JFileChooser;
import org.jfree.chart.editor.ChartEditor;
import java.awt.Component;
import javax.swing.JOptionPane;
import org.jfree.chart.editor.ChartEditorManager;
import org.jfree.chart.plot.PlotRenderingInfo;
import java.awt.Shape;
import java.awt.Color;
import java.io.IOException;
import java.awt.event.ActionEvent;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartChangeEvent;
import java.awt.image.ImageObserver;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;
import java.awt.Graphics;
import org.jfree.chart.entity.ChartEntity;
import java.awt.Insets;
import org.jfree.chart.entity.EntityCollection;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import org.jfree.chart.plot.Plot;
import javax.swing.ToolTipManager;
import java.awt.Dimension;
import org.jfree.chart.plot.Zoomable;
import java.util.ResourceBundle;
import javax.swing.JMenuItem;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Point;
import org.jfree.chart.plot.PlotOrientation;
import java.awt.geom.Point2D;
import javax.swing.JPopupMenu;
import java.awt.Image;
import javax.swing.event.EventListenerList;
import java.io.Serializable;
import java.awt.print.Printable;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.event.ChartChangeListener;
import javax.swing.JPanel;

public class ChartPanel extends JPanel implements ChartChangeListener, ChartProgressListener, ActionListener, MouseListener, MouseMotionListener, Printable, Serializable
{
    private static final long serialVersionUID = 6046366297214274674L;
    public static final boolean DEFAULT_BUFFER_USED = false;
    public static final int DEFAULT_WIDTH = 680;
    public static final int DEFAULT_HEIGHT = 420;
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 800;
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 600;
    public static final int DEFAULT_ZOOM_TRIGGER_DISTANCE = 10;
    public static final String PROPERTIES_COMMAND = "PROPERTIES";
    public static final String SAVE_COMMAND = "SAVE";
    public static final String PRINT_COMMAND = "PRINT";
    public static final String ZOOM_IN_BOTH_COMMAND = "ZOOM_IN_BOTH";
    public static final String ZOOM_IN_DOMAIN_COMMAND = "ZOOM_IN_DOMAIN";
    public static final String ZOOM_IN_RANGE_COMMAND = "ZOOM_IN_RANGE";
    public static final String ZOOM_OUT_BOTH_COMMAND = "ZOOM_OUT_BOTH";
    public static final String ZOOM_OUT_DOMAIN_COMMAND = "ZOOM_DOMAIN_BOTH";
    public static final String ZOOM_OUT_RANGE_COMMAND = "ZOOM_RANGE_BOTH";
    public static final String ZOOM_RESET_BOTH_COMMAND = "ZOOM_RESET_BOTH";
    public static final String ZOOM_RESET_DOMAIN_COMMAND = "ZOOM_RESET_DOMAIN";
    public static final String ZOOM_RESET_RANGE_COMMAND = "ZOOM_RESET_RANGE";
    private JFreeChart chart;
    private EventListenerList chartMouseListeners;
    private boolean useBuffer;
    private boolean refreshBuffer;
    private Image chartBuffer;
    private int chartBufferHeight;
    private int chartBufferWidth;
    private int minimumDrawWidth;
    private int minimumDrawHeight;
    private int maximumDrawWidth;
    private int maximumDrawHeight;
    private JPopupMenu popup;
    private ChartRenderingInfo info;
    private Point2D anchor;
    private double scaleX;
    private double scaleY;
    private PlotOrientation orientation;
    private boolean domainZoomable;
    private boolean rangeZoomable;
    private Point zoomPoint;
    private transient Rectangle2D zoomRectangle;
    private boolean fillZoomRectangle;
    private int zoomTriggerDistance;
    private boolean horizontalAxisTrace;
    private boolean verticalAxisTrace;
    private transient Line2D verticalTraceLine;
    private transient Line2D horizontalTraceLine;
    private JMenuItem zoomInBothMenuItem;
    private JMenuItem zoomInDomainMenuItem;
    private JMenuItem zoomInRangeMenuItem;
    private JMenuItem zoomOutBothMenuItem;
    private JMenuItem zoomOutDomainMenuItem;
    private JMenuItem zoomOutRangeMenuItem;
    private JMenuItem zoomResetBothMenuItem;
    private JMenuItem zoomResetDomainMenuItem;
    private JMenuItem zoomResetRangeMenuItem;
    private boolean enforceFileExtensions;
    private boolean ownToolTipDelaysActive;
    private int originalToolTipInitialDelay;
    private int originalToolTipReshowDelay;
    private int originalToolTipDismissDelay;
    private int ownToolTipInitialDelay;
    private int ownToolTipReshowDelay;
    private int ownToolTipDismissDelay;
    private double zoomInFactor;
    private double zoomOutFactor;
    protected static ResourceBundle localizationResources;
    
    public ChartPanel(final JFreeChart chart) {
        this(chart, 680, 420, 300, 200, 800, 600, false, true, true, true, true, true);
    }
    
    public ChartPanel(final JFreeChart chart, final boolean useBuffer) {
        this(chart, 680, 420, 300, 200, 800, 600, useBuffer, true, true, true, true, true);
    }
    
    public ChartPanel(final JFreeChart chart, final boolean properties, final boolean save, final boolean print, final boolean zoom, final boolean tooltips) {
        this(chart, 680, 420, 300, 200, 800, 600, false, properties, save, print, zoom, tooltips);
    }
    
    public ChartPanel(final JFreeChart chart, final int width, final int height, final int minimumDrawWidth, final int minimumDrawHeight, final int maximumDrawWidth, final int maximumDrawHeight, final boolean useBuffer, final boolean properties, final boolean save, final boolean print, final boolean zoom, final boolean tooltips) {
        this.orientation = PlotOrientation.VERTICAL;
        this.domainZoomable = false;
        this.rangeZoomable = false;
        this.zoomPoint = null;
        this.zoomRectangle = null;
        this.fillZoomRectangle = false;
        this.horizontalAxisTrace = false;
        this.verticalAxisTrace = false;
        this.zoomInFactor = 0.5;
        this.zoomOutFactor = 2.0;
        this.chart = chart;
        this.chartMouseListeners = new EventListenerList();
        if (chart != null) {
            chart.addChangeListener(this);
            final Plot plot = chart.getPlot();
            this.domainZoomable = false;
            this.rangeZoomable = false;
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable)plot;
                this.domainZoomable = z.isDomainZoomable();
                this.rangeZoomable = z.isRangeZoomable();
                this.orientation = z.getOrientation();
            }
        }
        this.info = new ChartRenderingInfo();
        this.setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.minimumDrawWidth = minimumDrawWidth;
        this.minimumDrawHeight = minimumDrawHeight;
        this.maximumDrawWidth = maximumDrawWidth;
        this.maximumDrawHeight = maximumDrawHeight;
        this.zoomTriggerDistance = 10;
        this.popup = null;
        if (properties || save || print || zoom) {
            this.popup = this.createPopupMenu(properties, save, print, zoom);
        }
        this.enableEvents(16L);
        this.enableEvents(32L);
        this.setDisplayToolTips(tooltips);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.enforceFileExtensions = true;
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        this.ownToolTipInitialDelay = ttm.getInitialDelay();
        this.ownToolTipDismissDelay = ttm.getDismissDelay();
        this.ownToolTipReshowDelay = ttm.getReshowDelay();
    }
    
    public JFreeChart getChart() {
        return this.chart;
    }
    
    public void setChart(final JFreeChart chart) {
        if (this.chart != null) {
            this.chart.removeChangeListener(this);
            this.chart.removeProgressListener(this);
        }
        if ((this.chart = chart) != null) {
            this.chart.addChangeListener(this);
            this.chart.addProgressListener(this);
            final Plot plot = chart.getPlot();
            this.domainZoomable = false;
            this.rangeZoomable = false;
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable)plot;
                this.domainZoomable = z.isDomainZoomable();
                this.rangeZoomable = z.isRangeZoomable();
                this.orientation = z.getOrientation();
            }
        }
        else {
            this.domainZoomable = false;
            this.rangeZoomable = false;
        }
        if (this.useBuffer) {
            this.refreshBuffer = true;
        }
        this.repaint();
    }
    
    public int getMinimumDrawWidth() {
        return this.minimumDrawWidth;
    }
    
    public void setMinimumDrawWidth(final int width) {
        this.minimumDrawWidth = width;
    }
    
    public int getMaximumDrawWidth() {
        return this.maximumDrawWidth;
    }
    
    public void setMaximumDrawWidth(final int width) {
        this.maximumDrawWidth = width;
    }
    
    public int getMinimumDrawHeight() {
        return this.minimumDrawHeight;
    }
    
    public void setMinimumDrawHeight(final int height) {
        this.minimumDrawHeight = height;
    }
    
    public int getMaximumDrawHeight() {
        return this.maximumDrawHeight;
    }
    
    public void setMaximumDrawHeight(final int height) {
        this.maximumDrawHeight = height;
    }
    
    public double getScaleX() {
        return this.scaleX;
    }
    
    public double getScaleY() {
        return this.scaleY;
    }
    
    public Point2D getAnchor() {
        return this.anchor;
    }
    
    protected void setAnchor(final Point2D anchor) {
        this.anchor = anchor;
    }
    
    public JPopupMenu getPopupMenu() {
        return this.popup;
    }
    
    public void setPopupMenu(final JPopupMenu popup) {
        this.popup = popup;
    }
    
    public ChartRenderingInfo getChartRenderingInfo() {
        return this.info;
    }
    
    public void setMouseZoomable(final boolean flag) {
        this.setMouseZoomable(flag, true);
    }
    
    public void setMouseZoomable(final boolean flag, final boolean fillRectangle) {
        this.setDomainZoomable(flag);
        this.setRangeZoomable(flag);
        this.setFillZoomRectangle(fillRectangle);
    }
    
    public boolean isDomainZoomable() {
        return this.domainZoomable;
    }
    
    public void setDomainZoomable(final boolean flag) {
        if (flag) {
            final Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable)plot;
                this.domainZoomable = (flag && z.isDomainZoomable());
            }
        }
        else {
            this.domainZoomable = false;
        }
    }
    
    public boolean isRangeZoomable() {
        return this.rangeZoomable;
    }
    
    public void setRangeZoomable(final boolean flag) {
        if (flag) {
            final Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable)plot;
                this.rangeZoomable = (flag && z.isRangeZoomable());
            }
        }
        else {
            this.rangeZoomable = false;
        }
    }
    
    public boolean getFillZoomRectangle() {
        return this.fillZoomRectangle;
    }
    
    public void setFillZoomRectangle(final boolean flag) {
        this.fillZoomRectangle = flag;
    }
    
    public int getZoomTriggerDistance() {
        return this.zoomTriggerDistance;
    }
    
    public void setZoomTriggerDistance(final int distance) {
        this.zoomTriggerDistance = distance;
    }
    
    public boolean getHorizontalAxisTrace() {
        return this.horizontalAxisTrace;
    }
    
    public void setHorizontalAxisTrace(final boolean flag) {
        this.horizontalAxisTrace = flag;
    }
    
    protected Line2D getHorizontalTraceLine() {
        return this.horizontalTraceLine;
    }
    
    protected void setHorizontalTraceLine(final Line2D line) {
        this.horizontalTraceLine = line;
    }
    
    public boolean getVerticalAxisTrace() {
        return this.verticalAxisTrace;
    }
    
    public void setVerticalAxisTrace(final boolean flag) {
        this.verticalAxisTrace = flag;
    }
    
    protected Line2D getVerticalTraceLine() {
        return this.verticalTraceLine;
    }
    
    protected void setVerticalTraceLine(final Line2D line) {
        this.verticalTraceLine = line;
    }
    
    public boolean isEnforceFileExtensions() {
        return this.enforceFileExtensions;
    }
    
    public void setEnforceFileExtensions(final boolean enforce) {
        this.enforceFileExtensions = enforce;
    }
    
    public void setDisplayToolTips(final boolean flag) {
        if (flag) {
            ToolTipManager.sharedInstance().registerComponent(this);
        }
        else {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }
    
    public String getToolTipText(final MouseEvent e) {
        String result = null;
        if (this.info != null) {
            final EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                final Insets insets = this.getInsets();
                final ChartEntity entity = entities.getEntity((int)((e.getX() - insets.left) / this.scaleX), (int)((e.getY() - insets.top) / this.scaleY));
                if (entity != null) {
                    result = entity.getToolTipText();
                }
            }
        }
        return result;
    }
    
    public Point translateJava2DToScreen(final Point2D java2DPoint) {
        final Insets insets = this.getInsets();
        final int x = (int)(java2DPoint.getX() * this.scaleX + insets.left);
        final int y = (int)(java2DPoint.getY() * this.scaleY + insets.top);
        return new Point(x, y);
    }
    
    public Point2D translateScreenToJava2D(final Point screenPoint) {
        final Insets insets = this.getInsets();
        final double x = (screenPoint.getX() - insets.left) / this.scaleX;
        final double y = (screenPoint.getY() - insets.top) / this.scaleY;
        return new Point2D.Double(x, y);
    }
    
    public Rectangle2D scale(final Rectangle2D rect) {
        final Insets insets = this.getInsets();
        final double x = rect.getX() * this.getScaleX() + insets.left;
        final double y = rect.getY() * this.getScaleY() + insets.top;
        final double w = rect.getWidth() * this.getScaleX();
        final double h = rect.getHeight() * this.getScaleY();
        return new Rectangle2D.Double(x, y, w, h);
    }
    
    public ChartEntity getEntityForPoint(final int viewX, final int viewY) {
        ChartEntity result = null;
        if (this.info != null) {
            final Insets insets = this.getInsets();
            final double x = (viewX - insets.left) / this.scaleX;
            final double y = (viewY - insets.top) / this.scaleY;
            final EntityCollection entities = this.info.getEntityCollection();
            result = ((entities != null) ? entities.getEntity(x, y) : null);
        }
        return result;
    }
    
    public boolean getRefreshBuffer() {
        return this.refreshBuffer;
    }
    
    public void setRefreshBuffer(final boolean flag) {
        this.refreshBuffer = flag;
    }
    
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        if (this.chart == null) {
            return;
        }
        final Graphics2D g2 = (Graphics2D)g.create();
        final Dimension size = this.getSize();
        final Insets insets = this.getInsets();
        final Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top, size.getWidth() - insets.left - insets.right, size.getHeight() - insets.top - insets.bottom);
        boolean scale = false;
        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        if (drawWidth < this.minimumDrawWidth) {
            this.scaleX = drawWidth / this.minimumDrawWidth;
            drawWidth = this.minimumDrawWidth;
            scale = true;
        }
        else if (drawWidth > this.maximumDrawWidth) {
            this.scaleX = drawWidth / this.maximumDrawWidth;
            drawWidth = this.maximumDrawWidth;
            scale = true;
        }
        if (drawHeight < this.minimumDrawHeight) {
            this.scaleY = drawHeight / this.minimumDrawHeight;
            drawHeight = this.minimumDrawHeight;
            scale = true;
        }
        else if (drawHeight > this.maximumDrawHeight) {
            this.scaleY = drawHeight / this.maximumDrawHeight;
            drawHeight = this.maximumDrawHeight;
            scale = true;
        }
        final Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth, drawHeight);
        if (this.useBuffer) {
            if (this.chartBuffer == null || this.chartBufferWidth != available.getWidth() || this.chartBufferHeight != available.getHeight()) {
                this.chartBufferWidth = (int)available.getWidth();
                this.chartBufferHeight = (int)available.getHeight();
                this.chartBuffer = this.createImage(this.chartBufferWidth, this.chartBufferHeight);
                this.refreshBuffer = true;
            }
            if (this.refreshBuffer) {
                final Rectangle2D bufferArea = new Rectangle2D.Double(0.0, 0.0, this.chartBufferWidth, this.chartBufferHeight);
                final Graphics2D bufferG2 = (Graphics2D)this.chartBuffer.getGraphics();
                if (scale) {
                    final AffineTransform saved = bufferG2.getTransform();
                    final AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                    bufferG2.transform(st);
                    this.chart.draw(bufferG2, chartArea, this.anchor, this.info);
                    bufferG2.setTransform(saved);
                }
                else {
                    this.chart.draw(bufferG2, bufferArea, this.anchor, this.info);
                }
                this.refreshBuffer = false;
            }
            g2.drawImage(this.chartBuffer, insets.left, insets.right, this);
        }
        else {
            final AffineTransform saved2 = g2.getTransform();
            g2.translate(insets.left, insets.top);
            if (scale) {
                final AffineTransform st2 = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                g2.transform(st2);
            }
            this.chart.draw(g2, chartArea, this.anchor, this.info);
            g2.setTransform(saved2);
        }
        this.anchor = null;
        this.verticalTraceLine = null;
        this.horizontalTraceLine = null;
    }
    
    public void chartChanged(final ChartChangeEvent event) {
        this.refreshBuffer = true;
        final Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            final Zoomable z = (Zoomable)plot;
            this.orientation = z.getOrientation();
        }
        this.repaint();
    }
    
    public void chartProgress(final ChartProgressEvent event) {
    }
    
    public void actionPerformed(final ActionEvent event) {
        final String command = event.getActionCommand();
        if (command.equals("PROPERTIES")) {
            this.attemptEditChartProperties();
        }
        else if (command.equals("SAVE")) {
            try {
                this.doSaveAs();
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
        }
        else if (command.equals("PRINT")) {
            this.createChartPrintJob();
        }
        else if (command.equals("ZOOM_IN_BOTH")) {
            this.zoomInBoth(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals("ZOOM_IN_DOMAIN")) {
            this.zoomInDomain(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals("ZOOM_IN_RANGE")) {
            this.zoomInRange(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals("ZOOM_OUT_BOTH")) {
            this.zoomOutBoth(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals("ZOOM_DOMAIN_BOTH")) {
            this.zoomOutDomain(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals("ZOOM_RANGE_BOTH")) {
            this.zoomOutRange(this.zoomPoint.getX(), this.zoomPoint.getY());
        }
        else if (command.equals("ZOOM_RESET_BOTH")) {
            this.restoreAutoBounds();
        }
        else if (command.equals("ZOOM_RESET_DOMAIN")) {
            this.restoreAutoDomainBounds();
        }
        else if (command.equals("ZOOM_RESET_RANGE")) {
            this.restoreAutoRangeBounds();
        }
    }
    
    public void mouseEntered(final MouseEvent e) {
        if (!this.ownToolTipDelaysActive) {
            final ToolTipManager ttm = ToolTipManager.sharedInstance();
            this.originalToolTipInitialDelay = ttm.getInitialDelay();
            ttm.setInitialDelay(this.ownToolTipInitialDelay);
            this.originalToolTipReshowDelay = ttm.getReshowDelay();
            ttm.setReshowDelay(this.ownToolTipReshowDelay);
            this.originalToolTipDismissDelay = ttm.getDismissDelay();
            ttm.setDismissDelay(this.ownToolTipDismissDelay);
            this.ownToolTipDelaysActive = true;
        }
    }
    
    public void mouseExited(final MouseEvent e) {
        if (this.ownToolTipDelaysActive) {
            final ToolTipManager ttm = ToolTipManager.sharedInstance();
            ttm.setInitialDelay(this.originalToolTipInitialDelay);
            ttm.setReshowDelay(this.originalToolTipReshowDelay);
            ttm.setDismissDelay(this.originalToolTipDismissDelay);
            this.ownToolTipDelaysActive = false;
        }
    }
    
    public void mousePressed(final MouseEvent e) {
        if (this.zoomRectangle == null) {
            final Rectangle2D screenDataArea = this.getScreenDataArea(e.getX(), e.getY());
            if (screenDataArea != null) {
                this.zoomPoint = this.getPointInRectangle(e.getX(), e.getY(), screenDataArea);
            }
            else {
                this.zoomPoint = null;
            }
            if (e.isPopupTrigger() && this.popup != null) {
                this.displayPopupMenu(e.getX(), e.getY());
            }
        }
    }
    
    private Point getPointInRectangle(int x, int y, final Rectangle2D area) {
        x = (int)Math.max(Math.ceil(area.getMinX()), Math.min(x, Math.floor(area.getMaxX())));
        y = (int)Math.max(Math.ceil(area.getMinY()), Math.min(y, Math.floor(area.getMaxY())));
        return new Point(x, y);
    }
    
    public void mouseDragged(final MouseEvent e) {
        if (this.popup != null && this.popup.isShowing()) {
            return;
        }
        if (this.zoomPoint == null) {
            return;
        }
        final Graphics2D g2 = (Graphics2D)this.getGraphics();
        g2.setXORMode(Color.gray);
        if (this.zoomRectangle != null) {
            if (this.fillZoomRectangle) {
                g2.fill(this.zoomRectangle);
            }
            else {
                g2.draw(this.zoomRectangle);
            }
        }
        boolean hZoom = false;
        boolean vZoom = false;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            hZoom = this.rangeZoomable;
            vZoom = this.domainZoomable;
        }
        else {
            hZoom = this.domainZoomable;
            vZoom = this.rangeZoomable;
        }
        final Rectangle2D scaledDataArea = this.getScreenDataArea((int)this.zoomPoint.getX(), (int)this.zoomPoint.getY());
        if (hZoom && vZoom) {
            final double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            final double ymax = Math.min(e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), this.zoomPoint.getY(), xmax - this.zoomPoint.getX(), ymax - this.zoomPoint.getY());
        }
        else if (hZoom) {
            final double xmax = Math.min(e.getX(), scaledDataArea.getMaxX());
            this.zoomRectangle = new Rectangle2D.Double(this.zoomPoint.getX(), scaledDataArea.getMinY(), xmax - this.zoomPoint.getX(), scaledDataArea.getHeight());
        }
        else if (vZoom) {
            final double ymax2 = Math.min(e.getY(), scaledDataArea.getMaxY());
            this.zoomRectangle = new Rectangle2D.Double(scaledDataArea.getMinX(), this.zoomPoint.getY(), scaledDataArea.getWidth(), ymax2 - this.zoomPoint.getY());
        }
        if (this.zoomRectangle != null) {
            if (this.fillZoomRectangle) {
                g2.fill(this.zoomRectangle);
            }
            else {
                g2.draw(this.zoomRectangle);
            }
        }
        g2.dispose();
    }
    
    public void mouseReleased(final MouseEvent e) {
        if (this.zoomRectangle != null) {
            boolean hZoom = false;
            boolean vZoom = false;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                hZoom = this.rangeZoomable;
                vZoom = this.domainZoomable;
            }
            else {
                hZoom = this.domainZoomable;
                vZoom = this.rangeZoomable;
            }
            final boolean zoomTrigger1 = hZoom && Math.abs(e.getX() - this.zoomPoint.getX()) >= this.zoomTriggerDistance;
            final boolean zoomTrigger2 = vZoom && Math.abs(e.getY() - this.zoomPoint.getY()) >= this.zoomTriggerDistance;
            if (zoomTrigger1 || zoomTrigger2) {
                if ((hZoom && e.getX() < this.zoomPoint.getX()) || (vZoom && e.getY() < this.zoomPoint.getY())) {
                    this.restoreAutoBounds();
                }
                else {
                    final Rectangle2D screenDataArea = this.getScreenDataArea((int)this.zoomPoint.getX(), (int)this.zoomPoint.getY());
                    double x;
                    double y;
                    double w;
                    double h;
                    if (!vZoom) {
                        x = this.zoomPoint.getX();
                        y = screenDataArea.getMinY();
                        w = Math.min(this.zoomRectangle.getWidth(), screenDataArea.getMaxX() - this.zoomPoint.getX());
                        h = screenDataArea.getHeight();
                    }
                    else if (!hZoom) {
                        x = screenDataArea.getMinX();
                        y = this.zoomPoint.getY();
                        w = screenDataArea.getWidth();
                        h = Math.min(this.zoomRectangle.getHeight(), screenDataArea.getMaxY() - this.zoomPoint.getY());
                    }
                    else {
                        x = this.zoomPoint.getX();
                        y = this.zoomPoint.getY();
                        w = Math.min(this.zoomRectangle.getWidth(), screenDataArea.getMaxX() - this.zoomPoint.getX());
                        h = Math.min(this.zoomRectangle.getHeight(), screenDataArea.getMaxY() - this.zoomPoint.getY());
                    }
                    final Rectangle2D zoomArea = new Rectangle2D.Double(x, y, w, h);
                    this.zoom(zoomArea);
                }
                this.zoomPoint = null;
                this.zoomRectangle = null;
            }
            else {
                final Graphics2D g2 = (Graphics2D)this.getGraphics();
                g2.setXORMode(Color.gray);
                if (this.fillZoomRectangle) {
                    g2.fill(this.zoomRectangle);
                }
                else {
                    g2.draw(this.zoomRectangle);
                }
                g2.dispose();
                this.zoomPoint = null;
                this.zoomRectangle = null;
            }
        }
        else if (e.isPopupTrigger() && this.popup != null) {
            this.displayPopupMenu(e.getX(), e.getY());
        }
    }
    
    public void mouseClicked(final MouseEvent event) {
        final Insets insets = this.getInsets();
        final int x = (int)((event.getX() - insets.left) / this.scaleX);
        final int y = (int)((event.getY() - insets.top) / this.scaleY);
        this.anchor = new Point2D.Double(x, y);
        this.chart.setNotify(true);
        final Object[] listeners = this.chartMouseListeners.getListeners((Class<Object>)ChartMouseListener.class);
        if (listeners.length == 0) {
            return;
        }
        ChartEntity entity = null;
        if (this.info != null) {
            final EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                entity = entities.getEntity(x, y);
            }
        }
        final ChartMouseEvent chartEvent = new ChartMouseEvent(this.getChart(), event, entity);
        for (int i = listeners.length - 1; i >= 0; --i) {
            ((ChartMouseListener)listeners[i]).chartMouseClicked(chartEvent);
        }
    }
    
    public void mouseMoved(final MouseEvent e) {
        if (this.horizontalAxisTrace) {
            this.drawHorizontalAxisTrace(e.getX());
        }
        if (this.verticalAxisTrace) {
            this.drawVerticalAxisTrace(e.getY());
        }
        final Object[] listeners = this.chartMouseListeners.getListeners((Class<Object>)ChartMouseListener.class);
        if (listeners.length == 0) {
            return;
        }
        final Insets insets = this.getInsets();
        final int x = (int)((e.getX() - insets.left) / this.scaleX);
        final int y = (int)((e.getY() - insets.top) / this.scaleY);
        ChartEntity entity = null;
        if (this.info != null) {
            final EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                entity = entities.getEntity(x, y);
            }
        }
        final ChartMouseEvent event = new ChartMouseEvent(this.getChart(), e, entity);
        for (int i = listeners.length - 1; i >= 0; --i) {
            ((ChartMouseListener)listeners[i]).chartMouseMoved(event);
        }
    }
    
    public void zoomInBoth(final double x, final double y) {
        this.zoomInDomain(x, y);
        this.zoomInRange(x, y);
    }
    
    public void zoomInDomain(final double x, final double y) {
        final Plot p = this.chart.getPlot();
        if (p instanceof Zoomable) {
            final Zoomable plot = (Zoomable)p;
            plot.zoomDomainAxes(this.zoomInFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)));
        }
    }
    
    public void zoomInRange(final double x, final double y) {
        final Plot p = this.chart.getPlot();
        if (p instanceof Zoomable) {
            final Zoomable z = (Zoomable)p;
            z.zoomRangeAxes(this.zoomInFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)));
        }
    }
    
    public void zoomOutBoth(final double x, final double y) {
        this.zoomOutDomain(x, y);
        this.zoomOutRange(x, y);
    }
    
    public void zoomOutDomain(final double x, final double y) {
        final Plot p = this.chart.getPlot();
        if (p instanceof Zoomable) {
            final Zoomable z = (Zoomable)p;
            z.zoomDomainAxes(this.zoomOutFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)));
        }
    }
    
    public void zoomOutRange(final double x, final double y) {
        final Plot p = this.chart.getPlot();
        if (p instanceof Zoomable) {
            final Zoomable z = (Zoomable)p;
            z.zoomRangeAxes(this.zoomOutFactor, this.info.getPlotInfo(), this.translateScreenToJava2D(new Point((int)x, (int)y)));
        }
    }
    
    public void zoom(final Rectangle2D selection) {
        final Point2D selectOrigin = this.translateScreenToJava2D(new Point((int)Math.ceil(selection.getX()), (int)Math.ceil(selection.getY())));
        final PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        final Rectangle2D scaledDataArea = this.getScreenDataArea((int)selection.getCenterX(), (int)selection.getCenterY());
        if (selection.getHeight() > 0.0 && selection.getWidth() > 0.0) {
            final double hLower = (selection.getMinX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            final double hUpper = (selection.getMaxX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            final double vLower = (scaledDataArea.getMaxY() - selection.getMaxY()) / scaledDataArea.getHeight();
            final double vUpper = (scaledDataArea.getMaxY() - selection.getMinY()) / scaledDataArea.getHeight();
            final Plot p = this.chart.getPlot();
            if (p instanceof Zoomable) {
                final Zoomable z = (Zoomable)p;
                if (z.getOrientation() == PlotOrientation.HORIZONTAL) {
                    z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
                }
                else {
                    z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
                }
            }
        }
    }
    
    public void restoreAutoBounds() {
        this.restoreAutoDomainBounds();
        this.restoreAutoRangeBounds();
    }
    
    public void restoreAutoDomainBounds() {
        final Plot p = this.chart.getPlot();
        if (p instanceof Zoomable) {
            final Zoomable z = (Zoomable)p;
            z.zoomDomainAxes(0.0, this.info.getPlotInfo(), this.zoomPoint);
        }
    }
    
    public void restoreAutoRangeBounds() {
        final Plot p = this.chart.getPlot();
        if (p instanceof Zoomable) {
            final Zoomable z = (Zoomable)p;
            z.zoomRangeAxes(0.0, this.info.getPlotInfo(), this.zoomPoint);
        }
    }
    
    public Rectangle2D getScreenDataArea() {
        final Rectangle2D dataArea = this.info.getPlotInfo().getDataArea();
        final Insets insets = this.getInsets();
        final double x = dataArea.getX() * this.scaleX + insets.left;
        final double y = dataArea.getY() * this.scaleY + insets.top;
        final double w = dataArea.getWidth() * this.scaleX;
        final double h = dataArea.getHeight() * this.scaleY;
        return new Rectangle2D.Double(x, y, w, h);
    }
    
    public Rectangle2D getScreenDataArea(final int x, final int y) {
        final PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        Rectangle2D result;
        if (plotInfo.getSubplotCount() == 0) {
            result = this.getScreenDataArea();
        }
        else {
            final Point2D selectOrigin = this.translateScreenToJava2D(new Point(x, y));
            final int subplotIndex = plotInfo.getSubplotIndex(selectOrigin);
            if (subplotIndex == -1) {
                return null;
            }
            result = this.scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea());
        }
        return result;
    }
    
    public int getInitialDelay() {
        return this.ownToolTipInitialDelay;
    }
    
    public int getReshowDelay() {
        return this.ownToolTipReshowDelay;
    }
    
    public int getDismissDelay() {
        return this.ownToolTipDismissDelay;
    }
    
    public void setInitialDelay(final int delay) {
        this.ownToolTipInitialDelay = delay;
    }
    
    public void setReshowDelay(final int delay) {
        this.ownToolTipReshowDelay = delay;
    }
    
    public void setDismissDelay(final int delay) {
        this.ownToolTipDismissDelay = delay;
    }
    
    public double getZoomInFactor() {
        return this.zoomInFactor;
    }
    
    public void setZoomInFactor(final double factor) {
        this.zoomInFactor = factor;
    }
    
    public double getZoomOutFactor() {
        return this.zoomOutFactor;
    }
    
    public void setZoomOutFactor(final double factor) {
        this.zoomOutFactor = factor;
    }
    
    private void drawHorizontalAxisTrace(final int x) {
        final Graphics2D g2 = (Graphics2D)this.getGraphics();
        final Rectangle2D dataArea = this.getScreenDataArea();
        g2.setXORMode(Color.orange);
        if ((int)dataArea.getMinX() < x && x < (int)dataArea.getMaxX()) {
            if (this.verticalTraceLine != null) {
                g2.draw(this.verticalTraceLine);
                this.verticalTraceLine.setLine(x, (int)dataArea.getMinY(), x, (int)dataArea.getMaxY());
            }
            else {
                this.verticalTraceLine = new Line2D.Float((float)x, (float)(int)dataArea.getMinY(), (float)x, (float)(int)dataArea.getMaxY());
            }
            g2.draw(this.verticalTraceLine);
        }
    }
    
    private void drawVerticalAxisTrace(final int y) {
        final Graphics2D g2 = (Graphics2D)this.getGraphics();
        final Rectangle2D dataArea = this.getScreenDataArea();
        g2.setXORMode(Color.orange);
        if ((int)dataArea.getMinY() < y && y < (int)dataArea.getMaxY()) {
            if (this.horizontalTraceLine != null) {
                g2.draw(this.horizontalTraceLine);
                this.horizontalTraceLine.setLine((int)dataArea.getMinX(), y, (int)dataArea.getMaxX(), y);
            }
            else {
                this.horizontalTraceLine = new Line2D.Float((float)(int)dataArea.getMinX(), (float)y, (float)(int)dataArea.getMaxX(), (float)y);
            }
            g2.draw(this.horizontalTraceLine);
        }
    }
    
    private void attemptEditChartProperties() {
        final ChartEditor editor = ChartEditorManager.getChartEditor(this.chart);
        final int result = JOptionPane.showConfirmDialog(this, editor, ChartPanel.localizationResources.getString("Chart_Properties"), 2, -1);
        if (result == 0) {
            editor.updateChart(this.chart);
        }
    }
    
    public void doSaveAs() throws IOException {
        final JFileChooser fileChooser = new JFileChooser();
        final ExtensionFileFilter filter = new ExtensionFileFilter(ChartPanel.localizationResources.getString("PNG_Image_Files"), ".png");
        fileChooser.addChoosableFileFilter((FileFilter)filter);
        final int option = fileChooser.showSaveDialog(this);
        if (option == 0) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (this.isEnforceFileExtensions() && !filename.endsWith(".png")) {
                filename += ".png";
            }
            ChartUtilities.saveChartAsPNG(new File(filename), this.chart, this.getWidth(), this.getHeight());
        }
    }
    
    public void createChartPrintJob() {
        final PrinterJob job = PrinterJob.getPrinterJob();
        final PageFormat pf = job.defaultPage();
        final PageFormat pf2 = job.pageDialog(pf);
        if (pf2 != pf) {
            job.setPrintable(this, pf2);
            if (job.printDialog()) {
                try {
                    job.print();
                }
                catch (final PrinterException e) {
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }
    
    public int print(final Graphics g, final PageFormat pf, final int pageIndex) {
        if (pageIndex != 0) {
            return 1;
        }
        final Graphics2D g2 = (Graphics2D)g;
        final double x = pf.getImageableX();
        final double y = pf.getImageableY();
        final double w = pf.getImageableWidth();
        final double h = pf.getImageableHeight();
        this.chart.draw(g2, new Rectangle2D.Double(x, y, w, h), this.anchor, null);
        return 0;
    }
    
    public void addChartMouseListener(final ChartMouseListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Null 'listener' argument.");
        }
        this.chartMouseListeners.add(ChartMouseListener.class, listener);
    }
    
    public void removeChartMouseListener(final ChartMouseListener listener) {
        this.chartMouseListeners.remove(ChartMouseListener.class, listener);
    }
    
    public EventListener[] getListeners(final Class listenerType) {
        if (listenerType == ChartMouseListener.class) {
            return this.chartMouseListeners.getListeners((Class<EventListener>)listenerType);
        }
        return super.getListeners((Class<EventListener>)listenerType);
    }
    
    protected JPopupMenu createPopupMenu(final boolean properties, final boolean save, final boolean print, final boolean zoom) {
        final JPopupMenu result = new JPopupMenu("Chart:");
        boolean separator = false;
        if (properties) {
            final JMenuItem propertiesItem = new JMenuItem(ChartPanel.localizationResources.getString("Properties..."));
            propertiesItem.setActionCommand("PROPERTIES");
            propertiesItem.addActionListener(this);
            result.add(propertiesItem);
            separator = true;
        }
        if (save) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            final JMenuItem saveItem = new JMenuItem(ChartPanel.localizationResources.getString("Save_as..."));
            saveItem.setActionCommand("SAVE");
            saveItem.addActionListener(this);
            result.add(saveItem);
            separator = true;
        }
        if (print) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            final JMenuItem printItem = new JMenuItem(ChartPanel.localizationResources.getString("Print..."));
            printItem.setActionCommand("PRINT");
            printItem.addActionListener(this);
            result.add(printItem);
            separator = true;
        }
        if (zoom) {
            if (separator) {
                result.addSeparator();
                separator = false;
            }
            final JMenu zoomInMenu = new JMenu(ChartPanel.localizationResources.getString("Zoom_In"));
            (this.zoomInBothMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("All_Axes"))).setActionCommand("ZOOM_IN_BOTH");
            this.zoomInBothMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInBothMenuItem);
            zoomInMenu.addSeparator();
            (this.zoomInDomainMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("Domain_Axis"))).setActionCommand("ZOOM_IN_DOMAIN");
            this.zoomInDomainMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInDomainMenuItem);
            (this.zoomInRangeMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("Range_Axis"))).setActionCommand("ZOOM_IN_RANGE");
            this.zoomInRangeMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInRangeMenuItem);
            result.add(zoomInMenu);
            final JMenu zoomOutMenu = new JMenu(ChartPanel.localizationResources.getString("Zoom_Out"));
            (this.zoomOutBothMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("All_Axes"))).setActionCommand("ZOOM_OUT_BOTH");
            this.zoomOutBothMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutBothMenuItem);
            zoomOutMenu.addSeparator();
            (this.zoomOutDomainMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("Domain_Axis"))).setActionCommand("ZOOM_DOMAIN_BOTH");
            this.zoomOutDomainMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutDomainMenuItem);
            (this.zoomOutRangeMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("Range_Axis"))).setActionCommand("ZOOM_RANGE_BOTH");
            this.zoomOutRangeMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutRangeMenuItem);
            result.add(zoomOutMenu);
            final JMenu autoRangeMenu = new JMenu(ChartPanel.localizationResources.getString("Auto_Range"));
            (this.zoomResetBothMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("All_Axes"))).setActionCommand("ZOOM_RESET_BOTH");
            this.zoomResetBothMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetBothMenuItem);
            autoRangeMenu.addSeparator();
            (this.zoomResetDomainMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("Domain_Axis"))).setActionCommand("ZOOM_RESET_DOMAIN");
            this.zoomResetDomainMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetDomainMenuItem);
            (this.zoomResetRangeMenuItem = new JMenuItem(ChartPanel.localizationResources.getString("Range_Axis"))).setActionCommand("ZOOM_RESET_RANGE");
            this.zoomResetRangeMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetRangeMenuItem);
            result.addSeparator();
            result.add(autoRangeMenu);
        }
        return result;
    }
    
    protected void displayPopupMenu(final int x, final int y) {
        if (this.popup != null) {
            final Plot plot = this.chart.getPlot();
            boolean isDomainZoomable = false;
            boolean isRangeZoomable = false;
            if (plot instanceof Zoomable) {
                final Zoomable z = (Zoomable)plot;
                isDomainZoomable = z.isDomainZoomable();
                isRangeZoomable = z.isRangeZoomable();
            }
            if (this.zoomInDomainMenuItem != null) {
                this.zoomInDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomOutDomainMenuItem != null) {
                this.zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomResetDomainMenuItem != null) {
                this.zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
            }
            if (this.zoomInRangeMenuItem != null) {
                this.zoomInRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomOutRangeMenuItem != null) {
                this.zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomResetRangeMenuItem != null) {
                this.zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
            }
            if (this.zoomInBothMenuItem != null) {
                this.zoomInBothMenuItem.setEnabled(isDomainZoomable & isRangeZoomable);
            }
            if (this.zoomOutBothMenuItem != null) {
                this.zoomOutBothMenuItem.setEnabled(isDomainZoomable & isRangeZoomable);
            }
            if (this.zoomResetBothMenuItem != null) {
                this.zoomResetBothMenuItem.setEnabled(isDomainZoomable & isRangeZoomable);
            }
            this.popup.show(this, x, y);
        }
    }
    
    static {
        ChartPanel.localizationResources = ResourceBundle.getBundle("org.jfree.chart.LocalizationBundle");
    }
}
