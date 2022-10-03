package javax.swing.colorchooser;

import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Insets;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.JComponent;

final class DiagramComponent extends JComponent implements MouseListener, MouseMotionListener
{
    private final ColorPanel panel;
    private final boolean diagram;
    private final Insets insets;
    private int width;
    private int height;
    private int[] array;
    private BufferedImage image;
    
    DiagramComponent(final ColorPanel panel, final boolean diagram) {
        this.insets = new Insets(0, 0, 0, 0);
        this.panel = panel;
        this.diagram = diagram;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    @Override
    protected void paintComponent(final Graphics graphics) {
        this.getInsets(this.insets);
        this.width = this.getWidth() - this.insets.left - this.insets.right;
        this.height = this.getHeight() - this.insets.top - this.insets.bottom;
        if (this.image == null || this.width != this.image.getWidth() || this.height != this.image.getHeight()) {
            final int n = this.width * this.height;
            if (this.array == null || this.array.length < n) {
                this.array = new int[n];
            }
            this.image = new BufferedImage(this.width, this.height, 1);
        }
        final float n2 = 1.0f / (this.width - 1);
        final float n3 = 1.0f / (this.height - 1);
        int n4 = 0;
        float n5 = 0.0f;
        for (int i = 0; i < this.height; ++i, n5 += n3) {
            if (this.diagram) {
                float n6 = 0.0f;
                for (int j = 0; j < this.width; ++j, n6 += n2, ++n4) {
                    this.array[n4] = this.panel.getColor(n6, n5);
                }
            }
            else {
                final int color = this.panel.getColor(n5);
                for (int k = 0; k < this.width; ++k, ++n4) {
                    this.array[n4] = color;
                }
            }
        }
        this.image.setRGB(0, 0, this.width, this.height, this.array, 0, this.width);
        graphics.drawImage(this.image, this.insets.left, this.insets.top, this.width, this.height, this);
        if (this.isEnabled()) {
            --this.width;
            --this.height;
            graphics.setXORMode(Color.WHITE);
            graphics.setColor(Color.BLACK);
            if (this.diagram) {
                final int value = getValue(this.panel.getValueX(), this.insets.left, this.width);
                final int value2 = getValue(this.panel.getValueY(), this.insets.top, this.height);
                graphics.drawLine(value - 8, value2, value + 8, value2);
                graphics.drawLine(value, value2 - 8, value, value2 + 8);
            }
            else {
                final int value3 = getValue(this.panel.getValueZ(), this.insets.top, this.height);
                graphics.drawLine(this.insets.left, value3, this.insets.left + this.width, value3);
            }
            graphics.setPaintMode();
        }
    }
    
    @Override
    public void mousePressed(final MouseEvent mouseEvent) {
        this.mouseDragged(mouseEvent);
    }
    
    @Override
    public void mouseReleased(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseClicked(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseEntered(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseExited(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseMoved(final MouseEvent mouseEvent) {
    }
    
    @Override
    public void mouseDragged(final MouseEvent mouseEvent) {
        if (this.isEnabled()) {
            final float value = getValue(mouseEvent.getY(), this.insets.top, this.height);
            if (this.diagram) {
                this.panel.setValue(getValue(mouseEvent.getX(), this.insets.left, this.width), value);
            }
            else {
                this.panel.setValue(value);
            }
        }
    }
    
    private static int getValue(final float n, final int n2, final int n3) {
        return n2 + (int)(n * n3);
    }
    
    private static float getValue(int n, final int n2, final int n3) {
        if (n2 < n) {
            n -= n2;
            return (n < n3) ? (n / (float)n3) : 1.0f;
        }
        return 0.0f;
    }
}
