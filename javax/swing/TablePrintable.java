package javax.swing;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Graphics2D;
import java.awt.print.PrinterException;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Rectangle;
import java.text.MessageFormat;
import javax.swing.table.TableColumnModel;
import javax.swing.table.JTableHeader;
import java.awt.print.Printable;

class TablePrintable implements Printable
{
    private JTable table;
    private JTableHeader header;
    private TableColumnModel colModel;
    private int totalColWidth;
    private JTable.PrintMode printMode;
    private MessageFormat headerFormat;
    private MessageFormat footerFormat;
    private int last;
    private int row;
    private int col;
    private final Rectangle clip;
    private final Rectangle hclip;
    private final Rectangle tempRect;
    private static final int H_F_SPACE = 8;
    private static final float HEADER_FONT_SIZE = 18.0f;
    private static final float FOOTER_FONT_SIZE = 12.0f;
    private Font headerFont;
    private Font footerFont;
    
    public TablePrintable(final JTable table, final JTable.PrintMode printMode, final MessageFormat headerFormat, final MessageFormat footerFormat) {
        this.last = -1;
        this.row = 0;
        this.col = 0;
        this.clip = new Rectangle(0, 0, 0, 0);
        this.hclip = new Rectangle(0, 0, 0, 0);
        this.tempRect = new Rectangle(0, 0, 0, 0);
        this.table = table;
        this.header = table.getTableHeader();
        this.colModel = table.getColumnModel();
        this.totalColWidth = this.colModel.getTotalColumnWidth();
        if (this.header != null) {
            this.hclip.height = this.header.getHeight();
        }
        this.printMode = printMode;
        this.headerFormat = headerFormat;
        this.footerFormat = footerFormat;
        this.headerFont = table.getFont().deriveFont(1, 18.0f);
        this.footerFont = table.getFont().deriveFont(0, 12.0f);
    }
    
    @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int n) throws PrinterException {
        final int width = (int)pageFormat.getImageableWidth();
        final int n2 = (int)pageFormat.getImageableHeight();
        if (width <= 0) {
            throw new PrinterException("Width of printable area is too small.");
        }
        final Object[] array = { n + 1 };
        String format = null;
        if (this.headerFormat != null) {
            format = this.headerFormat.format(array);
        }
        String format2 = null;
        if (this.footerFormat != null) {
            format2 = this.footerFormat.format(array);
        }
        Rectangle2D stringBounds = null;
        Rectangle2D stringBounds2 = null;
        int n3 = 0;
        int n4 = 0;
        int height = n2;
        if (format != null) {
            graphics.setFont(this.headerFont);
            stringBounds = graphics.getFontMetrics().getStringBounds(format, graphics);
            n3 = (int)Math.ceil(stringBounds.getHeight());
            height -= n3 + 8;
        }
        if (format2 != null) {
            graphics.setFont(this.footerFont);
            stringBounds2 = graphics.getFontMetrics().getStringBounds(format2, graphics);
            n4 = (int)Math.ceil(stringBounds2.getHeight());
            height -= n4 + 8;
        }
        if (height <= 0) {
            throw new PrinterException("Height of printable area is too small.");
        }
        double n5 = 1.0;
        if (this.printMode == JTable.PrintMode.FIT_WIDTH && this.totalColWidth > width) {
            assert width > 0;
            assert this.totalColWidth > 1;
            n5 = width / (double)this.totalColWidth;
        }
        assert n5 > 0.0;
        while (this.last < n) {
            if (this.row >= this.table.getRowCount() && this.col == 0) {
                return 1;
            }
            this.findNextClip((int)(width / n5), (int)((height - this.hclip.height) / n5));
            ++this.last;
        }
        final Graphics2D graphics2D = (Graphics2D)graphics.create();
        graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        if (format2 != null) {
            final AffineTransform transform = graphics2D.getTransform();
            graphics2D.translate(0, n2 - n4);
            this.printText(graphics2D, format2, stringBounds2, this.footerFont, width);
            graphics2D.setTransform(transform);
        }
        if (format != null) {
            this.printText(graphics2D, format, stringBounds, this.headerFont, width);
            graphics2D.translate(0, n3 + 8);
        }
        this.tempRect.x = 0;
        this.tempRect.y = 0;
        this.tempRect.width = width;
        this.tempRect.height = height;
        graphics2D.clip(this.tempRect);
        if (n5 != 1.0) {
            graphics2D.scale(n5, n5);
        }
        else {
            graphics2D.translate((width - this.clip.width) / 2, 0);
        }
        final AffineTransform transform2 = graphics2D.getTransform();
        final Shape clip = graphics2D.getClip();
        if (this.header != null) {
            this.hclip.x = this.clip.x;
            this.hclip.width = this.clip.width;
            graphics2D.translate(-this.hclip.x, 0);
            graphics2D.clip(this.hclip);
            this.header.print(graphics2D);
            graphics2D.setTransform(transform2);
            graphics2D.setClip(clip);
            graphics2D.translate(0, this.hclip.height);
        }
        graphics2D.translate(-this.clip.x, -this.clip.y);
        graphics2D.clip(this.clip);
        this.table.print(graphics2D);
        graphics2D.setTransform(transform2);
        graphics2D.setClip(clip);
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawRect(0, 0, this.clip.width, this.hclip.height + this.clip.height);
        graphics2D.dispose();
        return 0;
    }
    
    private void printText(final Graphics2D graphics2D, final String s, final Rectangle2D rectangle2D, final Font font, final int n) {
        int n2;
        if (rectangle2D.getWidth() < n) {
            n2 = (int)((n - rectangle2D.getWidth()) / 2.0);
        }
        else if (this.table.getComponentOrientation().isLeftToRight()) {
            n2 = 0;
        }
        else {
            n2 = -(int)(Math.ceil(rectangle2D.getWidth()) - n);
        }
        final int n3 = (int)Math.ceil(Math.abs(rectangle2D.getY()));
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(font);
        graphics2D.drawString(s, n2, n3);
    }
    
    private void findNextClip(final int n, final int n2) {
        final boolean leftToRight = this.table.getComponentOrientation().isLeftToRight();
        if (this.col == 0) {
            if (leftToRight) {
                this.clip.x = 0;
            }
            else {
                this.clip.x = this.totalColWidth;
            }
            final Rectangle clip = this.clip;
            clip.y += this.clip.height;
            this.clip.width = 0;
            this.clip.height = 0;
            final int rowCount = this.table.getRowCount();
            int n3 = this.table.getRowHeight(this.row);
            do {
                final Rectangle clip2 = this.clip;
                clip2.height += n3;
                if (++this.row >= rowCount) {
                    break;
                }
                n3 = this.table.getRowHeight(this.row);
            } while (this.clip.height + n3 <= n2);
        }
        if (this.printMode == JTable.PrintMode.FIT_WIDTH) {
            this.clip.x = 0;
            this.clip.width = this.totalColWidth;
            return;
        }
        if (leftToRight) {
            final Rectangle clip3 = this.clip;
            clip3.x += this.clip.width;
        }
        this.clip.width = 0;
        final int columnCount = this.table.getColumnCount();
        int n4 = this.colModel.getColumn(this.col).getWidth();
        do {
            final Rectangle clip4 = this.clip;
            clip4.width += n4;
            if (!leftToRight) {
                final Rectangle clip5 = this.clip;
                clip5.x -= n4;
            }
            if (++this.col >= columnCount) {
                this.col = 0;
                break;
            }
            n4 = this.colModel.getColumn(this.col).getWidth();
        } while (this.clip.width + n4 <= n);
    }
}
