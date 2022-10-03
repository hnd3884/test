package javax.swing.plaf.nimbus;

import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.swing.UIManager;
import javax.swing.Painter;
import java.awt.Graphics;
import javax.swing.plaf.UIResource;
import javax.swing.JComponent;

class TableScrollPaneCorner extends JComponent implements UIResource
{
    @Override
    protected void paintComponent(final Graphics graphics) {
        final Painter painter = (Painter)UIManager.get("TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter");
        if (painter != null) {
            if (graphics instanceof Graphics2D) {
                painter.paint((Graphics2D)graphics, this, this.getWidth() + 1, this.getHeight());
            }
            else {
                final BufferedImage bufferedImage = new BufferedImage(this.getWidth(), this.getHeight(), 2);
                final Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
                painter.paint(graphics2D, this, this.getWidth() + 1, this.getHeight());
                graphics2D.dispose();
                graphics.drawImage(bufferedImage, 0, 0, null);
            }
        }
    }
}
