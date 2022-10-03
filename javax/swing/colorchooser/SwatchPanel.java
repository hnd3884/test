package javax.swing.colorchooser;

import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JPanel;

class SwatchPanel extends JPanel
{
    protected Color[] colors;
    protected Dimension swatchSize;
    protected Dimension numSwatches;
    protected Dimension gap;
    private int selRow;
    private int selCol;
    
    public SwatchPanel() {
        this.initValues();
        this.initColors();
        this.setToolTipText("");
        this.setOpaque(true);
        this.setBackground(Color.white);
        this.setFocusable(true);
        this.setInheritsPopupMenu(true);
        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(final FocusEvent focusEvent) {
                SwatchPanel.this.repaint();
            }
            
            @Override
            public void focusLost(final FocusEvent focusEvent) {
                SwatchPanel.this.repaint();
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent keyEvent) {
                switch (keyEvent.getKeyCode()) {
                    case 38: {
                        if (SwatchPanel.this.selRow > 0) {
                            SwatchPanel.this.selRow--;
                            SwatchPanel.this.repaint();
                            break;
                        }
                        break;
                    }
                    case 40: {
                        if (SwatchPanel.this.selRow < SwatchPanel.this.numSwatches.height - 1) {
                            SwatchPanel.this.selRow++;
                            SwatchPanel.this.repaint();
                            break;
                        }
                        break;
                    }
                    case 37: {
                        if (SwatchPanel.this.selCol > 0 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            SwatchPanel.this.selCol--;
                            SwatchPanel.this.repaint();
                            break;
                        }
                        if (SwatchPanel.this.selCol < SwatchPanel.this.numSwatches.width - 1 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            SwatchPanel.this.selCol++;
                            SwatchPanel.this.repaint();
                            break;
                        }
                        break;
                    }
                    case 39: {
                        if (SwatchPanel.this.selCol < SwatchPanel.this.numSwatches.width - 1 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            SwatchPanel.this.selCol++;
                            SwatchPanel.this.repaint();
                            break;
                        }
                        if (SwatchPanel.this.selCol > 0 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            SwatchPanel.this.selCol--;
                            SwatchPanel.this.repaint();
                            break;
                        }
                        break;
                    }
                    case 36: {
                        SwatchPanel.this.selCol = 0;
                        SwatchPanel.this.selRow = 0;
                        SwatchPanel.this.repaint();
                        break;
                    }
                    case 35: {
                        SwatchPanel.this.selCol = SwatchPanel.this.numSwatches.width - 1;
                        SwatchPanel.this.selRow = SwatchPanel.this.numSwatches.height - 1;
                        SwatchPanel.this.repaint();
                        break;
                    }
                }
            }
        });
    }
    
    public Color getSelectedColor() {
        return this.getColorForCell(this.selCol, this.selRow);
    }
    
    protected void initValues() {
    }
    
    public void paintComponent(final Graphics graphics) {
        graphics.setColor(this.getBackground());
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        for (int i = 0; i < this.numSwatches.height; ++i) {
            final int n = i * (this.swatchSize.height + this.gap.height);
            for (int j = 0; j < this.numSwatches.width; ++j) {
                final Color colorForCell = this.getColorForCell(j, i);
                graphics.setColor(colorForCell);
                int n2;
                if (!this.getComponentOrientation().isLeftToRight()) {
                    n2 = (this.numSwatches.width - j - 1) * (this.swatchSize.width + this.gap.width);
                }
                else {
                    n2 = j * (this.swatchSize.width + this.gap.width);
                }
                graphics.fillRect(n2, n, this.swatchSize.width, this.swatchSize.height);
                graphics.setColor(Color.black);
                graphics.drawLine(n2 + this.swatchSize.width - 1, n, n2 + this.swatchSize.width - 1, n + this.swatchSize.height - 1);
                graphics.drawLine(n2, n + this.swatchSize.height - 1, n2 + this.swatchSize.width - 1, n + this.swatchSize.height - 1);
                if (this.selRow == i && this.selCol == j && this.isFocusOwner()) {
                    graphics.setColor(new Color((colorForCell.getRed() < 125) ? 255 : 0, (colorForCell.getGreen() < 125) ? 255 : 0, (colorForCell.getBlue() < 125) ? 255 : 0));
                    graphics.drawLine(n2, n, n2 + this.swatchSize.width - 1, n);
                    graphics.drawLine(n2, n, n2, n + this.swatchSize.height - 1);
                    graphics.drawLine(n2 + this.swatchSize.width - 1, n, n2 + this.swatchSize.width - 1, n + this.swatchSize.height - 1);
                    graphics.drawLine(n2, n + this.swatchSize.height - 1, n2 + this.swatchSize.width - 1, n + this.swatchSize.height - 1);
                    graphics.drawLine(n2, n, n2 + this.swatchSize.width - 1, n + this.swatchSize.height - 1);
                    graphics.drawLine(n2, n + this.swatchSize.height - 1, n2 + this.swatchSize.width - 1, n);
                }
            }
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.numSwatches.width * (this.swatchSize.width + this.gap.width) - 1, this.numSwatches.height * (this.swatchSize.height + this.gap.height) - 1);
    }
    
    protected void initColors() {
    }
    
    @Override
    public String getToolTipText(final MouseEvent mouseEvent) {
        final Color colorForLocation = this.getColorForLocation(mouseEvent.getX(), mouseEvent.getY());
        return colorForLocation.getRed() + ", " + colorForLocation.getGreen() + ", " + colorForLocation.getBlue();
    }
    
    public void setSelectedColorFromLocation(final int n, final int n2) {
        if (!this.getComponentOrientation().isLeftToRight()) {
            this.selCol = this.numSwatches.width - n / (this.swatchSize.width + this.gap.width) - 1;
        }
        else {
            this.selCol = n / (this.swatchSize.width + this.gap.width);
        }
        this.selRow = n2 / (this.swatchSize.height + this.gap.height);
        this.repaint();
    }
    
    public Color getColorForLocation(final int n, final int n2) {
        int n3;
        if (!this.getComponentOrientation().isLeftToRight()) {
            n3 = this.numSwatches.width - n / (this.swatchSize.width + this.gap.width) - 1;
        }
        else {
            n3 = n / (this.swatchSize.width + this.gap.width);
        }
        return this.getColorForCell(n3, n2 / (this.swatchSize.height + this.gap.height));
    }
    
    private Color getColorForCell(final int n, final int n2) {
        return this.colors[n2 * this.numSwatches.width + n];
    }
}
