package javax.swing.colorchooser;

import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.FontMetrics;
import javax.accessibility.Accessible;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.SwingUtilities;
import javax.swing.JColorChooser;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;

class DefaultPreviewPanel extends JPanel
{
    private int squareSize;
    private int squareGap;
    private int innerGap;
    private int textGap;
    private Font font;
    private String sampleText;
    private int swatchWidth;
    private Color oldColor;
    
    DefaultPreviewPanel() {
        this.squareSize = 25;
        this.squareGap = 5;
        this.innerGap = 5;
        this.textGap = 5;
        this.font = new Font("Dialog", 0, 12);
        this.swatchWidth = 50;
        this.oldColor = null;
    }
    
    private JColorChooser getColorChooser() {
        return (JColorChooser)SwingUtilities.getAncestorOfClass(JColorChooser.class, this);
    }
    
    @Override
    public Dimension getPreferredSize() {
        Accessible colorChooser = this.getColorChooser();
        if (colorChooser == null) {
            colorChooser = this;
        }
        final FontMetrics fontMetrics = ((JComponent)colorChooser).getFontMetrics(this.getFont());
        fontMetrics.getAscent();
        return new Dimension(this.squareSize * 3 + this.squareGap * 2 + this.swatchWidth + SwingUtilities2.stringWidth((JComponent)colorChooser, fontMetrics, this.getSampleText()) + this.textGap * 3, fontMetrics.getHeight() * 3 + this.textGap * 3);
    }
    
    public void paintComponent(final Graphics graphics) {
        if (this.oldColor == null) {
            this.oldColor = this.getForeground();
        }
        graphics.setColor(this.getBackground());
        graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (this.getComponentOrientation().isLeftToRight()) {
            final int paintSquares = this.paintSquares(graphics, 0);
            this.paintSwatch(graphics, paintSquares + this.paintText(graphics, paintSquares));
        }
        else {
            final int paintSwatch = this.paintSwatch(graphics, 0);
            this.paintSquares(graphics, paintSwatch + this.paintText(graphics, paintSwatch));
        }
    }
    
    private int paintSwatch(final Graphics graphics, final int n) {
        graphics.setColor(this.oldColor);
        graphics.fillRect(n, 0, this.swatchWidth, this.squareSize + this.squareGap / 2);
        graphics.setColor(this.getForeground());
        graphics.fillRect(n, this.squareSize + this.squareGap / 2, this.swatchWidth, this.squareSize + this.squareGap / 2);
        return n + this.swatchWidth;
    }
    
    private int paintText(final Graphics graphics, final int n) {
        graphics.setFont(this.getFont());
        Accessible colorChooser = this.getColorChooser();
        if (colorChooser == null) {
            colorChooser = this;
        }
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics((JComponent)colorChooser, graphics);
        final int ascent = fontMetrics.getAscent();
        final int height = fontMetrics.getHeight();
        final int stringWidth = SwingUtilities2.stringWidth((JComponent)colorChooser, fontMetrics, this.getSampleText());
        final int n2 = n + this.textGap;
        final Color foreground = this.getForeground();
        graphics.setColor(foreground);
        SwingUtilities2.drawString((JComponent)colorChooser, graphics, this.getSampleText(), n2 + this.textGap / 2, ascent + 2);
        graphics.fillRect(n2, height + this.textGap, stringWidth + this.textGap, height + 2);
        graphics.setColor(Color.black);
        SwingUtilities2.drawString((JComponent)colorChooser, graphics, this.getSampleText(), n2 + this.textGap / 2, height + ascent + this.textGap + 2);
        graphics.setColor(Color.white);
        graphics.fillRect(n2, (height + this.textGap) * 2, stringWidth + this.textGap, height + 2);
        graphics.setColor(foreground);
        SwingUtilities2.drawString((JComponent)colorChooser, graphics, this.getSampleText(), n2 + this.textGap / 2, (height + this.textGap) * 2 + ascent + 2);
        return stringWidth + this.textGap * 3;
    }
    
    private int paintSquares(final Graphics graphics, final int n) {
        final Color foreground = this.getForeground();
        graphics.setColor(Color.white);
        graphics.fillRect(n, 0, this.squareSize, this.squareSize);
        graphics.setColor(foreground);
        graphics.fillRect(n + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
        graphics.setColor(Color.white);
        graphics.fillRect(n + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
        graphics.setColor(foreground);
        graphics.fillRect(n, this.squareSize + this.squareGap, this.squareSize, this.squareSize);
        graphics.translate(this.squareSize + this.squareGap, 0);
        graphics.setColor(Color.black);
        graphics.fillRect(n, 0, this.squareSize, this.squareSize);
        graphics.setColor(foreground);
        graphics.fillRect(n + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
        graphics.setColor(Color.white);
        graphics.fillRect(n + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
        graphics.translate(-(this.squareSize + this.squareGap), 0);
        graphics.translate(this.squareSize + this.squareGap, this.squareSize + this.squareGap);
        graphics.setColor(Color.white);
        graphics.fillRect(n, 0, this.squareSize, this.squareSize);
        graphics.setColor(foreground);
        graphics.fillRect(n + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
        graphics.translate(-(this.squareSize + this.squareGap), -(this.squareSize + this.squareGap));
        graphics.translate((this.squareSize + this.squareGap) * 2, 0);
        graphics.setColor(Color.white);
        graphics.fillRect(n, 0, this.squareSize, this.squareSize);
        graphics.setColor(foreground);
        graphics.fillRect(n + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
        graphics.setColor(Color.black);
        graphics.fillRect(n + this.innerGap * 2, this.innerGap * 2, this.squareSize - this.innerGap * 4, this.squareSize - this.innerGap * 4);
        graphics.translate(-((this.squareSize + this.squareGap) * 2), 0);
        graphics.translate((this.squareSize + this.squareGap) * 2, this.squareSize + this.squareGap);
        graphics.setColor(Color.black);
        graphics.fillRect(n, 0, this.squareSize, this.squareSize);
        graphics.setColor(foreground);
        graphics.fillRect(n + this.innerGap, this.innerGap, this.squareSize - this.innerGap * 2, this.squareSize - this.innerGap * 2);
        graphics.translate(-((this.squareSize + this.squareGap) * 2), -(this.squareSize + this.squareGap));
        return this.squareSize * 3 + this.squareGap * 2;
    }
    
    private String getSampleText() {
        if (this.sampleText == null) {
            this.sampleText = UIManager.getString("ColorChooser.sampleText", this.getLocale());
        }
        return this.sampleText;
    }
}
