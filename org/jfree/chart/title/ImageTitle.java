package org.jfree.chart.title;

import org.jfree.ui.Size2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import org.jfree.chart.event.TitleChangeEvent;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import java.awt.image.ImageObserver;
import java.awt.Image;

public class ImageTitle extends Title
{
    private Image image;
    
    public ImageTitle(final Image image) {
        this(image, image.getHeight(null), image.getWidth(null), Title.DEFAULT_POSITION, Title.DEFAULT_HORIZONTAL_ALIGNMENT, Title.DEFAULT_VERTICAL_ALIGNMENT, Title.DEFAULT_PADDING);
    }
    
    public ImageTitle(final Image image, final RectangleEdge position, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment) {
        this(image, image.getHeight(null), image.getWidth(null), position, horizontalAlignment, verticalAlignment, Title.DEFAULT_PADDING);
    }
    
    public ImageTitle(final Image image, final int height, final int width, final RectangleEdge position, final HorizontalAlignment horizontalAlignment, final VerticalAlignment verticalAlignment, final RectangleInsets padding) {
        super(position, horizontalAlignment, verticalAlignment, padding);
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        this.setHeight(height);
        this.setWidth(width);
    }
    
    public Image getImage() {
        return this.image;
    }
    
    public void setImage(final Image image) {
        if (image == null) {
            throw new NullPointerException("Null 'image' argument.");
        }
        this.image = image;
        this.notifyListeners(new TitleChangeEvent(this));
    }
    
    public void draw(final Graphics2D g2, final Rectangle2D titleArea) {
        final RectangleEdge position = this.getPosition();
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            this.drawHorizontal(g2, titleArea);
        }
        else {
            if (position != RectangleEdge.LEFT && position != RectangleEdge.RIGHT) {
                throw new RuntimeException("Invalid title position.");
            }
            this.drawVertical(g2, titleArea);
        }
    }
    
    protected Size2D drawHorizontal(final Graphics2D g2, final Rectangle2D chartArea) {
        double startY = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;
        final double w = this.getWidth();
        final double h = this.getHeight();
        final RectangleInsets padding = this.getPadding();
        topSpace = padding.calculateTopOutset(h);
        bottomSpace = padding.calculateBottomOutset(h);
        leftSpace = padding.calculateLeftOutset(w);
        rightSpace = padding.calculateRightOutset(w);
        if (this.getPosition() == RectangleEdge.TOP) {
            startY = chartArea.getY() + topSpace;
        }
        else {
            startY = chartArea.getY() + chartArea.getHeight() - bottomSpace - h;
        }
        final HorizontalAlignment horizontalAlignment = this.getHorizontalAlignment();
        double startX = 0.0;
        if (horizontalAlignment == HorizontalAlignment.CENTER) {
            startX = chartArea.getX() + leftSpace + chartArea.getWidth() / 2.0 - w / 2.0;
        }
        else if (horizontalAlignment == HorizontalAlignment.LEFT) {
            startX = chartArea.getX() + leftSpace;
        }
        else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
            startX = chartArea.getX() + chartArea.getWidth() - rightSpace - w;
        }
        g2.drawImage(this.image, (int)startX, (int)startY, (int)w, (int)h, null);
        return new Size2D(chartArea.getWidth() + leftSpace + rightSpace, h + topSpace + bottomSpace);
    }
    
    protected Size2D drawVertical(final Graphics2D g2, final Rectangle2D chartArea) {
        double startX = 0.0;
        double topSpace = 0.0;
        double bottomSpace = 0.0;
        double leftSpace = 0.0;
        double rightSpace = 0.0;
        final double w = this.getWidth();
        final double h = this.getHeight();
        final RectangleInsets padding = this.getPadding();
        if (padding != null) {
            topSpace = padding.calculateTopOutset(h);
            bottomSpace = padding.calculateBottomOutset(h);
            leftSpace = padding.calculateLeftOutset(w);
            rightSpace = padding.calculateRightOutset(w);
        }
        if (this.getPosition() == RectangleEdge.LEFT) {
            startX = chartArea.getX() + leftSpace;
        }
        else {
            startX = chartArea.getMaxX() - rightSpace - w;
        }
        final VerticalAlignment alignment = this.getVerticalAlignment();
        double startY = 0.0;
        if (alignment == VerticalAlignment.CENTER) {
            startY = chartArea.getMinY() + topSpace + chartArea.getHeight() / 2.0 - h / 2.0;
        }
        else if (alignment == VerticalAlignment.TOP) {
            startY = chartArea.getMinY() + topSpace;
        }
        else if (alignment == VerticalAlignment.BOTTOM) {
            startY = chartArea.getMaxY() - bottomSpace - h;
        }
        g2.drawImage(this.image, (int)startX, (int)startY, (int)w, (int)h, null);
        return new Size2D(chartArea.getWidth() + leftSpace + rightSpace, h + topSpace + bottomSpace);
    }
    
    public Object draw(final Graphics2D g2, final Rectangle2D area, final Object params) {
        this.draw(g2, area);
        return null;
    }
}
