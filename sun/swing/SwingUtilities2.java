package sun.swing;

import java.awt.geom.AffineTransform;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.event.TreeModelEvent;
import sun.awt.SunToolkit;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultCaret;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.awt.Container;
import javax.swing.UIManager;
import java.util.Locale;
import sun.java2d.SunGraphicsEnvironment;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import javax.swing.UIDefaults;
import sun.awt.AppContext;
import java.lang.reflect.Modifier;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.event.InputEvent;
import java.awt.AWTEvent;
import sun.security.util.SecurityConstants;
import java.awt.GraphicsEnvironment;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Highlighter;
import java.awt.PrintGraphics;
import java.awt.print.PrinterGraphics;
import sun.font.FontDesignMetrics;
import sun.print.ProxyPrintGraphics;
import java.util.Map;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JList;
import java.awt.Rectangle;
import java.awt.font.TextHitInfo;
import java.awt.Color;
import java.awt.font.TextLayout;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.text.BreakIterator;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.awt.font.TextAttribute;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Component;
import java.awt.FontMetrics;
import javax.swing.JComponent;
import sun.font.FontUtilities;
import java.lang.reflect.Field;
import java.awt.font.FontRenderContext;

public class SwingUtilities2
{
    public static final Object LAF_STATE_KEY;
    public static final Object MENU_SELECTION_MANAGER_LISTENER_KEY;
    private static LSBCacheEntry[] fontCache;
    private static final int CACHE_SIZE = 6;
    private static int nextIndex;
    private static LSBCacheEntry searchKey;
    private static final int MIN_CHAR_INDEX = 87;
    private static final int MAX_CHAR_INDEX = 88;
    public static final FontRenderContext DEFAULT_FRC;
    public static final Object AA_TEXT_PROPERTY_KEY;
    public static final String IMPLIED_CR = "CR";
    private static final StringBuilder SKIP_CLICK_COUNT;
    public static final Object COMPONENT_UI_PROPERTY_KEY;
    public static final StringUIClientPropertyKey BASICMENUITEMUI_MAX_TEXT_OFFSET;
    private static Field inputEvent_CanAccessSystemClipboard_Field;
    private static final String UntrustedClipboardAccess = "UNTRUSTED_CLIPBOARD_ACCESS_KEY";
    private static final int CHAR_BUFFER_SIZE = 100;
    private static final Object charsBufferLock;
    private static char[] charsBuffer;
    
    private static int syncCharsBuffer(final String s) {
        final int length = s.length();
        if (SwingUtilities2.charsBuffer == null || SwingUtilities2.charsBuffer.length < length) {
            SwingUtilities2.charsBuffer = s.toCharArray();
        }
        else {
            s.getChars(0, length, SwingUtilities2.charsBuffer, 0);
        }
        return length;
    }
    
    public static final boolean isComplexLayout(final char[] array, final int n, final int n2) {
        return FontUtilities.isComplexText(array, n, n2);
    }
    
    public static AATextInfo drawTextAntialiased(final JComponent component) {
        if (component != null) {
            return (AATextInfo)component.getClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
        }
        return null;
    }
    
    public static int getLeftSideBearing(final JComponent component, final FontMetrics fontMetrics, final String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        return getLeftSideBearing(component, fontMetrics, s.charAt(0));
    }
    
    public static int getLeftSideBearing(final JComponent component, final FontMetrics fontMetrics, final char c) {
        if (c < 'X' && c >= 'W') {
            final FontRenderContext fontRenderContext = getFontRenderContext(component, fontMetrics);
            final Font font = fontMetrics.getFont();
            synchronized (SwingUtilities2.class) {
                LSBCacheEntry searchKey = null;
                if (SwingUtilities2.searchKey == null) {
                    SwingUtilities2.searchKey = new LSBCacheEntry(fontRenderContext, font);
                }
                else {
                    SwingUtilities2.searchKey.reset(fontRenderContext, font);
                }
                for (final LSBCacheEntry lsbCacheEntry : SwingUtilities2.fontCache) {
                    if (SwingUtilities2.searchKey.equals(lsbCacheEntry)) {
                        searchKey = lsbCacheEntry;
                        break;
                    }
                }
                if (searchKey == null) {
                    searchKey = SwingUtilities2.searchKey;
                    SwingUtilities2.fontCache[SwingUtilities2.nextIndex] = SwingUtilities2.searchKey;
                    SwingUtilities2.searchKey = null;
                    SwingUtilities2.nextIndex = (SwingUtilities2.nextIndex + 1) % 6;
                }
                return searchKey.getLeftSideBearing(c);
            }
        }
        return 0;
    }
    
    public static FontMetrics getFontMetrics(final JComponent component, final Graphics graphics) {
        return getFontMetrics(component, graphics, graphics.getFont());
    }
    
    public static FontMetrics getFontMetrics(final JComponent component, final Graphics graphics, final Font font) {
        if (component != null) {
            return component.getFontMetrics(font);
        }
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }
    
    public static int stringWidth(final JComponent component, final FontMetrics fontMetrics, final String s) {
        if (s == null || s.equals("")) {
            return 0;
        }
        boolean complexLayout = component != null && component.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null;
        if (complexLayout) {
            synchronized (SwingUtilities2.charsBufferLock) {
                complexLayout = isComplexLayout(SwingUtilities2.charsBuffer, 0, syncCharsBuffer(s));
            }
        }
        if (complexLayout) {
            return (int)createTextLayout(component, s, fontMetrics.getFont(), fontMetrics.getFontRenderContext()).getAdvance();
        }
        return fontMetrics.stringWidth(s);
    }
    
    public static String clipStringIfNecessary(final JComponent component, final FontMetrics fontMetrics, final String s, final int n) {
        if (s == null || s.equals("")) {
            return "";
        }
        if (stringWidth(component, fontMetrics, s) > n) {
            return clipString(component, fontMetrics, s, n);
        }
        return s;
    }
    
    public static String clipString(final JComponent component, final FontMetrics fontMetrics, String s, int n) {
        final String s2 = "...";
        n -= stringWidth(component, fontMetrics, s2);
        if (n <= 0) {
            return s2;
        }
        final boolean complexLayout;
        synchronized (SwingUtilities2.charsBufferLock) {
            final int syncCharsBuffer = syncCharsBuffer(s);
            complexLayout = isComplexLayout(SwingUtilities2.charsBuffer, 0, syncCharsBuffer);
            if (!complexLayout) {
                int n2 = 0;
                for (int i = 0; i < syncCharsBuffer; ++i) {
                    n2 += fontMetrics.charWidth(SwingUtilities2.charsBuffer[i]);
                    if (n2 > n) {
                        s = s.substring(0, i);
                        break;
                    }
                }
            }
        }
        if (complexLayout) {
            final AttributedString attributedString = new AttributedString(s);
            if (component != null) {
                attributedString.addAttribute(TextAttribute.NUMERIC_SHAPING, component.getClientProperty(TextAttribute.NUMERIC_SHAPING));
            }
            s = s.substring(0, new LineBreakMeasurer(attributedString.getIterator(), BreakIterator.getCharacterInstance(), getFontRenderContext(component, fontMetrics)).nextOffset((float)n));
        }
        return s + s2;
    }
    
    public static void drawString(final JComponent component, final Graphics graphics, final String s, final int n, final int n2) {
        if (s == null || s.length() <= 0) {
            return;
        }
        if (isPrinting(graphics)) {
            final Graphics2D graphics2D = getGraphics2D(graphics);
            if (graphics2D != null) {
                final String trimTrailingSpaces = trimTrailingSpaces(s);
                if (!trimTrailingSpaces.isEmpty()) {
                    final float n3 = (float)graphics2D.getFont().getStringBounds(trimTrailingSpaces, getFontRenderContext(component)).getWidth();
                    TextLayout textLayout = createTextLayout(component, s, graphics2D.getFont(), graphics2D.getFontRenderContext());
                    if (stringWidth(component, graphics2D.getFontMetrics(), trimTrailingSpaces) > n3) {
                        textLayout = textLayout.getJustifiedLayout(n3);
                    }
                    final Color color = graphics2D.getColor();
                    if (color instanceof PrintColorUIResource) {
                        graphics2D.setColor(((PrintColorUIResource)color).getPrintColor());
                    }
                    textLayout.draw(graphics2D, (float)n, (float)n2);
                    graphics2D.setColor(color);
                }
                return;
            }
        }
        if (graphics instanceof Graphics2D) {
            final AATextInfo drawTextAntialiased = drawTextAntialiased(component);
            final Graphics2D graphics2D2 = (Graphics2D)graphics;
            boolean complexLayout = component != null && component.getClientProperty(TextAttribute.NUMERIC_SHAPING) != null;
            if (complexLayout) {
                synchronized (SwingUtilities2.charsBufferLock) {
                    complexLayout = isComplexLayout(SwingUtilities2.charsBuffer, 0, syncCharsBuffer(s));
                }
            }
            if (drawTextAntialiased != null) {
                Object renderingHint = null;
                Object renderingHint2 = graphics2D2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
                if (drawTextAntialiased.aaHint != renderingHint2) {
                    graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, drawTextAntialiased.aaHint);
                }
                else {
                    renderingHint2 = null;
                }
                if (drawTextAntialiased.lcdContrastHint != null) {
                    renderingHint = graphics2D2.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
                    if (drawTextAntialiased.lcdContrastHint.equals(renderingHint)) {
                        renderingHint = null;
                    }
                    else {
                        graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, drawTextAntialiased.lcdContrastHint);
                    }
                }
                if (complexLayout) {
                    createTextLayout(component, s, graphics2D2.getFont(), graphics2D2.getFontRenderContext()).draw(graphics2D2, (float)n, (float)n2);
                }
                else {
                    graphics.drawString(s, n, n2);
                }
                if (renderingHint2 != null) {
                    graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, renderingHint2);
                }
                if (renderingHint != null) {
                    graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, renderingHint);
                }
                return;
            }
            if (complexLayout) {
                createTextLayout(component, s, graphics2D2.getFont(), graphics2D2.getFontRenderContext()).draw(graphics2D2, (float)n, (float)n2);
                return;
            }
        }
        graphics.drawString(s, n, n2);
    }
    
    public static void drawStringUnderlineCharAt(final JComponent component, final Graphics graphics, final String s, final int n, final int n2, final int n3) {
        if (s == null || s.length() <= 0) {
            return;
        }
        drawString(component, graphics, s, n2, n3);
        final int length = s.length();
        if (n >= 0 && n < length) {
            final int n4 = 1;
            int n5 = 0;
            int n6 = 0;
            boolean b2;
            final boolean b = b2 = isPrinting(graphics);
            if (!b2) {
                synchronized (SwingUtilities2.charsBufferLock) {
                    syncCharsBuffer(s);
                    b2 = isComplexLayout(SwingUtilities2.charsBuffer, 0, length);
                }
            }
            if (!b2) {
                final FontMetrics fontMetrics = graphics.getFontMetrics();
                n5 = n2 + stringWidth(component, fontMetrics, s.substring(0, n));
                n6 = fontMetrics.charWidth(s.charAt(n));
            }
            else {
                final Graphics2D graphics2D = getGraphics2D(graphics);
                if (graphics2D != null) {
                    TextLayout textLayout = createTextLayout(component, s, graphics2D.getFont(), graphics2D.getFontRenderContext());
                    if (b) {
                        final float n7 = (float)graphics2D.getFont().getStringBounds(s, getFontRenderContext(component)).getWidth();
                        if (stringWidth(component, graphics2D.getFontMetrics(), s) > n7) {
                            textLayout = textLayout.getJustifiedLayout(n7);
                        }
                    }
                    final Rectangle bounds = textLayout.getVisualHighlightShape(TextHitInfo.leading(n), TextHitInfo.trailing(n)).getBounds();
                    n5 = n2 + bounds.x;
                    n6 = bounds.width;
                }
            }
            graphics.fillRect(n5, n3 + 1, n6, n4);
        }
    }
    
    public static int loc2IndexFileList(final JList list, final Point point) {
        int locationToIndex = list.locationToIndex(point);
        if (locationToIndex != -1) {
            final Object clientProperty = list.getClientProperty("List.isFileList");
            if (clientProperty instanceof Boolean && (boolean)clientProperty && !pointIsInActualBounds(list, locationToIndex, point)) {
                locationToIndex = -1;
            }
        }
        return locationToIndex;
    }
    
    private static boolean pointIsInActualBounds(final JList list, final int n, final Point point) {
        final Component listCellRendererComponent = list.getCellRenderer().getListCellRendererComponent(list, list.getModel().getElementAt(n), n, false, false);
        final Dimension preferredSize = listCellRendererComponent.getPreferredSize();
        final Rectangle cellBounds = list.getCellBounds(n, n);
        if (!listCellRendererComponent.getComponentOrientation().isLeftToRight()) {
            final Rectangle rectangle = cellBounds;
            rectangle.x += cellBounds.width - preferredSize.width;
        }
        cellBounds.width = preferredSize.width;
        return cellBounds.contains(point);
    }
    
    public static boolean pointOutsidePrefSize(final JTable table, final int n, final int n2, final Point point) {
        if (table.convertColumnIndexToModel(n2) != 0 || n == -1) {
            return true;
        }
        final Dimension preferredSize = table.getCellRenderer(n, n2).getTableCellRendererComponent(table, table.getValueAt(n, n2), false, false, n, n2).getPreferredSize();
        final Rectangle cellRect = table.getCellRect(n, n2, false);
        cellRect.width = preferredSize.width;
        cellRect.height = preferredSize.height;
        assert point.x >= cellRect.x && point.y >= cellRect.y;
        return point.x > cellRect.x + cellRect.width || point.y > cellRect.y + cellRect.height;
    }
    
    public static void setLeadAnchorWithoutSelection(final ListSelectionModel listSelectionModel, final int n, int anchorSelectionIndex) {
        if (anchorSelectionIndex == -1) {
            anchorSelectionIndex = n;
        }
        if (n == -1) {
            listSelectionModel.setAnchorSelectionIndex(-1);
            listSelectionModel.setLeadSelectionIndex(-1);
        }
        else {
            if (listSelectionModel.isSelectedIndex(n)) {
                listSelectionModel.addSelectionInterval(n, n);
            }
            else {
                listSelectionModel.removeSelectionInterval(n, n);
            }
            listSelectionModel.setAnchorSelectionIndex(anchorSelectionIndex);
        }
    }
    
    public static boolean shouldIgnore(final MouseEvent mouseEvent, final JComponent component) {
        return component == null || !component.isEnabled() || !SwingUtilities.isLeftMouseButton(mouseEvent) || mouseEvent.isConsumed();
    }
    
    public static void adjustFocus(final JComponent component) {
        if (!component.hasFocus() && component.isRequestFocusEnabled()) {
            component.requestFocus();
        }
    }
    
    public static int drawChars(final JComponent component, final Graphics graphics, final char[] array, final int n, final int n2, final int n3, final int n4) {
        if (n2 <= 0) {
            return n3;
        }
        final int n5 = n3 + getFontMetrics(component, graphics).charsWidth(array, n, n2);
        if (isPrinting(graphics)) {
            final Graphics2D graphics2D = getGraphics2D(graphics);
            if (graphics2D != null) {
                final FontRenderContext fontRenderContext = graphics2D.getFontRenderContext();
                final FontRenderContext fontRenderContext2 = getFontRenderContext(component);
                if (fontRenderContext2 != null && !isFontRenderContextPrintCompatible(fontRenderContext, fontRenderContext2)) {
                    final String s = new String(array, n, n2);
                    TextLayout justifiedLayout = new TextLayout(s, graphics2D.getFont(), fontRenderContext);
                    final String trimTrailingSpaces = trimTrailingSpaces(s);
                    if (!trimTrailingSpaces.isEmpty()) {
                        final float n6 = (float)graphics2D.getFont().getStringBounds(trimTrailingSpaces, fontRenderContext2).getWidth();
                        if (stringWidth(component, graphics2D.getFontMetrics(), trimTrailingSpaces) > n6) {
                            justifiedLayout = justifiedLayout.getJustifiedLayout(n6);
                        }
                        final Color color = graphics2D.getColor();
                        if (color instanceof PrintColorUIResource) {
                            graphics2D.setColor(((PrintColorUIResource)color).getPrintColor());
                        }
                        justifiedLayout.draw(graphics2D, (float)n3, (float)n4);
                        graphics2D.setColor(color);
                    }
                    return n5;
                }
            }
        }
        final AATextInfo drawTextAntialiased = drawTextAntialiased(component);
        if (drawTextAntialiased != null && graphics instanceof Graphics2D) {
            final Graphics2D graphics2D2 = (Graphics2D)graphics;
            Object renderingHint = null;
            Object renderingHint2 = graphics2D2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            if (drawTextAntialiased.aaHint != null && drawTextAntialiased.aaHint != renderingHint2) {
                graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, drawTextAntialiased.aaHint);
            }
            else {
                renderingHint2 = null;
            }
            if (drawTextAntialiased.lcdContrastHint != null) {
                renderingHint = graphics2D2.getRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST);
                if (drawTextAntialiased.lcdContrastHint.equals(renderingHint)) {
                    renderingHint = null;
                }
                else {
                    graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, drawTextAntialiased.lcdContrastHint);
                }
            }
            graphics.drawChars(array, n, n2, n3, n4);
            if (renderingHint2 != null) {
                graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, renderingHint2);
            }
            if (renderingHint != null) {
                graphics2D2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, renderingHint);
            }
        }
        else {
            graphics.drawChars(array, n, n2, n3, n4);
        }
        return n5;
    }
    
    public static float drawString(final JComponent component, final Graphics graphics, final AttributedCharacterIterator attributedCharacterIterator, final int n, final int n2) {
        final boolean printing = isPrinting(graphics);
        final Color color = graphics.getColor();
        if (printing && color instanceof PrintColorUIResource) {
            graphics.setColor(((PrintColorUIResource)color).getPrintColor());
        }
        final Graphics2D graphics2D = getGraphics2D(graphics);
        float advance;
        if (graphics2D == null) {
            graphics.drawString(attributedCharacterIterator, n, n2);
            advance = (float)n;
        }
        else {
            FontRenderContext fontRenderContext;
            if (printing) {
                fontRenderContext = getFontRenderContext(component);
                if (fontRenderContext.isAntiAliased() || fontRenderContext.usesFractionalMetrics()) {
                    fontRenderContext = new FontRenderContext(fontRenderContext.getTransform(), false, false);
                }
            }
            else if ((fontRenderContext = getFRCProperty(component)) == null) {
                fontRenderContext = graphics2D.getFontRenderContext();
            }
            TextLayout justifiedLayout;
            if (printing) {
                final FontRenderContext fontRenderContext2 = graphics2D.getFontRenderContext();
                if (!isFontRenderContextPrintCompatible(fontRenderContext, fontRenderContext2)) {
                    justifiedLayout = new TextLayout(attributedCharacterIterator, fontRenderContext2);
                    final AttributedCharacterIterator trimmedTrailingSpacesIterator = getTrimmedTrailingSpacesIterator(attributedCharacterIterator);
                    if (trimmedTrailingSpacesIterator != null) {
                        justifiedLayout = justifiedLayout.getJustifiedLayout(new TextLayout(trimmedTrailingSpacesIterator, fontRenderContext).getAdvance());
                    }
                }
                else {
                    justifiedLayout = new TextLayout(attributedCharacterIterator, fontRenderContext);
                }
            }
            else {
                justifiedLayout = new TextLayout(attributedCharacterIterator, fontRenderContext);
            }
            justifiedLayout.draw(graphics2D, (float)n, (float)n2);
            advance = justifiedLayout.getAdvance();
        }
        if (printing) {
            graphics.setColor(color);
        }
        return advance;
    }
    
    public static void drawVLine(final Graphics graphics, final int n, int n2, int n3) {
        if (n3 < n2) {
            final int n4 = n3;
            n3 = n2;
            n2 = n4;
        }
        graphics.fillRect(n, n2, 1, n3 - n2 + 1);
    }
    
    public static void drawHLine(final Graphics graphics, int n, int n2, final int n3) {
        if (n2 < n) {
            final int n4 = n2;
            n2 = n;
            n = n4;
        }
        graphics.fillRect(n, n3, n2 - n + 1, 1);
    }
    
    public static void drawRect(final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
        if (n3 < 0 || n4 < 0) {
            return;
        }
        if (n4 == 0 || n3 == 0) {
            graphics.fillRect(n, n2, n3 + 1, n4 + 1);
        }
        else {
            graphics.fillRect(n, n2, n3, 1);
            graphics.fillRect(n + n3, n2, 1, n4);
            graphics.fillRect(n + 1, n2 + n4, n3, 1);
            graphics.fillRect(n, n2 + 1, 1, n4);
        }
    }
    
    private static TextLayout createTextLayout(final JComponent component, final String s, final Font font, final FontRenderContext fontRenderContext) {
        final Object o = (component == null) ? null : component.getClientProperty(TextAttribute.NUMERIC_SHAPING);
        if (o == null) {
            return new TextLayout(s, font, fontRenderContext);
        }
        final HashMap hashMap = new HashMap();
        hashMap.put(TextAttribute.FONT, font);
        hashMap.put(TextAttribute.NUMERIC_SHAPING, o);
        return new TextLayout(s, hashMap, fontRenderContext);
    }
    
    private static boolean isFontRenderContextPrintCompatible(final FontRenderContext fontRenderContext, final FontRenderContext fontRenderContext2) {
        if (fontRenderContext == fontRenderContext2) {
            return true;
        }
        if (fontRenderContext == null || fontRenderContext2 == null) {
            return false;
        }
        if (fontRenderContext.getFractionalMetricsHint() != fontRenderContext2.getFractionalMetricsHint()) {
            return false;
        }
        if (!fontRenderContext.isTransformed() && !fontRenderContext2.isTransformed()) {
            return true;
        }
        final double[] array = new double[4];
        final double[] array2 = new double[4];
        fontRenderContext.getTransform().getMatrix(array);
        fontRenderContext2.getTransform().getMatrix(array2);
        return array[0] == array2[0] && array[1] == array2[1] && array[2] == array2[2] && array[3] == array2[3];
    }
    
    public static Graphics2D getGraphics2D(final Graphics graphics) {
        if (graphics instanceof Graphics2D) {
            return (Graphics2D)graphics;
        }
        if (graphics instanceof ProxyPrintGraphics) {
            return (Graphics2D)((ProxyPrintGraphics)graphics).getGraphics();
        }
        return null;
    }
    
    public static FontRenderContext getFontRenderContext(final Component component) {
        assert component != null;
        if (component == null) {
            return SwingUtilities2.DEFAULT_FRC;
        }
        return component.getFontMetrics(component.getFont()).getFontRenderContext();
    }
    
    private static FontRenderContext getFontRenderContext(final Component component, final FontMetrics fontMetrics) {
        assert component != null;
        return (fontMetrics != null) ? fontMetrics.getFontRenderContext() : getFontRenderContext(component);
    }
    
    public static FontMetrics getFontMetrics(final JComponent component, final Font font) {
        FontRenderContext fontRenderContext = getFRCProperty(component);
        if (fontRenderContext == null) {
            fontRenderContext = SwingUtilities2.DEFAULT_FRC;
        }
        return FontDesignMetrics.getMetrics(font, fontRenderContext);
    }
    
    private static FontRenderContext getFRCProperty(final JComponent component) {
        if (component != null) {
            final AATextInfo aaTextInfo = (AATextInfo)component.getClientProperty(SwingUtilities2.AA_TEXT_PROPERTY_KEY);
            if (aaTextInfo != null) {
                return aaTextInfo.frc;
            }
        }
        return null;
    }
    
    static boolean isPrinting(final Graphics graphics) {
        return graphics instanceof PrinterGraphics || graphics instanceof PrintGraphics;
    }
    
    private static String trimTrailingSpaces(final String s) {
        int n;
        for (n = s.length() - 1; n >= 0 && Character.isWhitespace(s.charAt(n)); --n) {}
        return s.substring(0, n + 1);
    }
    
    private static AttributedCharacterIterator getTrimmedTrailingSpacesIterator(final AttributedCharacterIterator attributedCharacterIterator) {
        final int index = attributedCharacterIterator.getIndex();
        char c;
        for (c = attributedCharacterIterator.last(); c != '\uffff' && Character.isWhitespace(c); c = attributedCharacterIterator.previous()) {}
        if (c == '\uffff') {
            return null;
        }
        final int index2 = attributedCharacterIterator.getIndex();
        if (index2 == attributedCharacterIterator.getEndIndex() - 1) {
            attributedCharacterIterator.setIndex(index);
            return attributedCharacterIterator;
        }
        return new AttributedString(attributedCharacterIterator, attributedCharacterIterator.getBeginIndex(), index2 + 1).getIterator();
    }
    
    public static boolean useSelectedTextColor(final Highlighter.Highlight highlight, final JTextComponent textComponent) {
        final Highlighter.HighlightPainter painter = highlight.getPainter();
        final String name = ((DefaultHighlighter.DefaultHighlightPainter)painter).getClass().getName();
        if (name.indexOf("javax.swing.text.DefaultHighlighter") != 0 && name.indexOf("com.sun.java.swing.plaf.windows.WindowsTextUI") != 0) {
            return false;
        }
        try {
            final DefaultHighlighter.DefaultHighlightPainter defaultHighlightPainter = (DefaultHighlighter.DefaultHighlightPainter)painter;
            if (defaultHighlightPainter.getColor() != null && !defaultHighlightPainter.getColor().equals(textComponent.getSelectionColor())) {
                return false;
            }
        }
        catch (final ClassCastException ex) {
            return false;
        }
        return true;
    }
    
    public static boolean canAccessSystemClipboard() {
        boolean canCurrentEventAccessSystemClipboard = false;
        if (!GraphicsEnvironment.isHeadless()) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager == null) {
                canCurrentEventAccessSystemClipboard = true;
            }
            else {
                try {
                    securityManager.checkPermission(SecurityConstants.AWT.ACCESS_CLIPBOARD_PERMISSION);
                    canCurrentEventAccessSystemClipboard = true;
                }
                catch (final SecurityException ex) {}
                if (canCurrentEventAccessSystemClipboard && !isTrustedContext()) {
                    canCurrentEventAccessSystemClipboard = canCurrentEventAccessSystemClipboard(true);
                }
            }
        }
        return canCurrentEventAccessSystemClipboard;
    }
    
    public static boolean canCurrentEventAccessSystemClipboard() {
        return isTrustedContext() || canCurrentEventAccessSystemClipboard(false);
    }
    
    public static boolean canEventAccessSystemClipboard(final AWTEvent awtEvent) {
        return isTrustedContext() || canEventAccessSystemClipboard(awtEvent, false);
    }
    
    private static synchronized boolean inputEvent_canAccessSystemClipboard(final InputEvent inputEvent) {
        if (SwingUtilities2.inputEvent_CanAccessSystemClipboard_Field == null) {
            SwingUtilities2.inputEvent_CanAccessSystemClipboard_Field = AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedAction<Field>() {
                @Override
                public Field run() {
                    try {
                        final Field declaredField = InputEvent.class.getDeclaredField("canAccessSystemClipboard");
                        declaredField.setAccessible(true);
                        return declaredField;
                    }
                    catch (final SecurityException ex) {}
                    catch (final NoSuchFieldException ex2) {}
                    return null;
                }
            });
        }
        if (SwingUtilities2.inputEvent_CanAccessSystemClipboard_Field == null) {
            return false;
        }
        boolean boolean1 = false;
        try {
            boolean1 = SwingUtilities2.inputEvent_CanAccessSystemClipboard_Field.getBoolean(inputEvent);
        }
        catch (final IllegalAccessException ex) {}
        return boolean1;
    }
    
    private static boolean isAccessClipboardGesture(final InputEvent inputEvent) {
        boolean b = false;
        if (inputEvent instanceof KeyEvent) {
            final KeyEvent keyEvent = (KeyEvent)inputEvent;
            final int keyCode = keyEvent.getKeyCode();
            final int modifiers = keyEvent.getModifiers();
            switch (keyCode) {
                case 67:
                case 86:
                case 88: {
                    b = (modifiers == 2);
                    break;
                }
                case 155: {
                    b = (modifiers == 2 || modifiers == 1);
                    break;
                }
                case 65485:
                case 65487:
                case 65489: {
                    b = true;
                    break;
                }
                case 127: {
                    b = (modifiers == 1);
                    break;
                }
            }
        }
        return b;
    }
    
    private static boolean canEventAccessSystemClipboard(final AWTEvent awtEvent, final boolean b) {
        return !EventQueue.isDispatchThread() || (awtEvent instanceof InputEvent && (!b || isAccessClipboardGesture((InputEvent)awtEvent)) && inputEvent_canAccessSystemClipboard((InputEvent)awtEvent));
    }
    
    public static void checkAccess(final int n) {
        if (System.getSecurityManager() != null && !Modifier.isPublic(n)) {
            throw new SecurityException("Resource is not accessible");
        }
    }
    
    private static boolean canCurrentEventAccessSystemClipboard(final boolean b) {
        return canEventAccessSystemClipboard(EventQueue.getCurrentEvent(), b);
    }
    
    private static boolean isTrustedContext() {
        return System.getSecurityManager() == null || AppContext.getAppContext().get("UNTRUSTED_CLIPBOARD_ACCESS_KEY") == null;
    }
    
    public static String displayPropertiesToCSS(final Font font, final Color color) {
        final StringBuffer sb = new StringBuffer("body {");
        if (font != null) {
            sb.append(" font-family: ");
            sb.append(font.getFamily());
            sb.append(" ; ");
            sb.append(" font-size: ");
            sb.append(font.getSize());
            sb.append("pt ;");
            if (font.isBold()) {
                sb.append(" font-weight: 700 ; ");
            }
            if (font.isItalic()) {
                sb.append(" font-style: italic ; ");
            }
        }
        if (color != null) {
            sb.append(" color: #");
            if (color.getRed() < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(color.getRed()));
            if (color.getGreen() < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(color.getGreen()));
            if (color.getBlue() < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(color.getBlue()));
            sb.append(" ; ");
        }
        sb.append(" }");
        return sb.toString();
    }
    
    public static Object makeIcon(final Class<?> clazz, final Class<?> clazz2, final String s) {
        return new UIDefaults.LazyValue() {
            @Override
            public Object createValue(final UIDefaults uiDefaults) {
                final byte[] array = AccessController.doPrivileged((PrivilegedAction<byte[]>)new PrivilegedAction<byte[]>() {
                    @Override
                    public byte[] run() {
                        try {
                            InputStream resourceAsStream = null;
                            for (Class clazz = clazz; clazz != null; clazz = clazz.getSuperclass()) {
                                resourceAsStream = clazz.getResourceAsStream(s);
                                if (resourceAsStream != null) {
                                    break;
                                }
                                if (clazz == clazz2) {
                                    break;
                                }
                            }
                            if (resourceAsStream == null) {
                                return null;
                            }
                            final BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream);
                            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                            final byte[] array = new byte[1024];
                            int read;
                            while ((read = bufferedInputStream.read(array)) > 0) {
                                byteArrayOutputStream.write(array, 0, read);
                            }
                            bufferedInputStream.close();
                            byteArrayOutputStream.flush();
                            return byteArrayOutputStream.toByteArray();
                        }
                        catch (final IOException ex) {
                            System.err.println(ex.toString());
                            return null;
                        }
                    }
                });
                if (array == null) {
                    return null;
                }
                if (array.length == 0) {
                    System.err.println("warning: " + s + " is zero-length");
                    return null;
                }
                return new ImageIconUIResource(array);
            }
        };
    }
    
    public static boolean isLocalDisplay() {
        final GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return !(localGraphicsEnvironment instanceof SunGraphicsEnvironment) || ((SunGraphicsEnvironment)localGraphicsEnvironment).isDisplayLocal();
    }
    
    public static int getUIDefaultsInt(final Object o) {
        return getUIDefaultsInt(o, 0);
    }
    
    public static int getUIDefaultsInt(final Object o, final Locale locale) {
        return getUIDefaultsInt(o, locale, 0);
    }
    
    public static int getUIDefaultsInt(final Object o, final int n) {
        return getUIDefaultsInt(o, null, n);
    }
    
    public static int getUIDefaultsInt(final Object o, final Locale locale, final int n) {
        final Object value = UIManager.get(o, locale);
        if (value instanceof Integer) {
            return (int)value;
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            }
            catch (final NumberFormatException ex) {}
        }
        return n;
    }
    
    public static Component compositeRequestFocus(final Component component) {
        if (component instanceof Container) {
            final Container container = (Container)component;
            if (container.isFocusCycleRoot()) {
                final Component defaultComponent = container.getFocusTraversalPolicy().getDefaultComponent(container);
                if (defaultComponent != null) {
                    defaultComponent.requestFocus();
                    return defaultComponent;
                }
            }
            final Container focusCycleRootAncestor = container.getFocusCycleRootAncestor();
            if (focusCycleRootAncestor != null) {
                final Component componentAfter = focusCycleRootAncestor.getFocusTraversalPolicy().getComponentAfter(focusCycleRootAncestor, container);
                if (componentAfter != null && SwingUtilities.isDescendingFrom(componentAfter, container)) {
                    componentAfter.requestFocus();
                    return componentAfter;
                }
            }
        }
        if (component.isFocusable()) {
            component.requestFocus();
            return component;
        }
        return null;
    }
    
    public static boolean tabbedPaneChangeFocusTo(final Component component) {
        if (component != null) {
            if (component.isFocusTraversable()) {
                compositeRequestFocus(component);
                return true;
            }
            if (component instanceof JComponent && ((JComponent)component).requestDefaultFocus()) {
                return true;
            }
        }
        return false;
    }
    
    public static <V> Future<V> submit(final Callable<V> callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        final FutureTask futureTask = new FutureTask((Callable<V>)callable);
        execute(futureTask);
        return futureTask;
    }
    
    public static <V> Future<V> submit(final Runnable runnable, final V v) {
        if (runnable == null) {
            throw new NullPointerException();
        }
        final FutureTask futureTask = new FutureTask(runnable, (V)v);
        execute(futureTask);
        return futureTask;
    }
    
    private static void execute(final Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
    
    public static void setSkipClickCount(final Component component, final int n) {
        if (component instanceof JTextComponent && ((JTextComponent)component).getCaret() instanceof DefaultCaret) {
            ((JTextComponent)component).putClientProperty(SwingUtilities2.SKIP_CLICK_COUNT, n);
        }
    }
    
    public static int getAdjustedClickCount(final JTextComponent textComponent, final MouseEvent mouseEvent) {
        final int clickCount = mouseEvent.getClickCount();
        if (clickCount == 1) {
            textComponent.putClientProperty(SwingUtilities2.SKIP_CLICK_COUNT, null);
        }
        else {
            final Integer n = (Integer)textComponent.getClientProperty(SwingUtilities2.SKIP_CLICK_COUNT);
            if (n != null) {
                return clickCount - n;
            }
        }
        return clickCount;
    }
    
    private static Section liesIn(final Rectangle rectangle, final Point point, final boolean b, final boolean b2, final boolean b3) {
        int n;
        int n2;
        int n3;
        boolean b4;
        if (b) {
            n = rectangle.x;
            n2 = point.x;
            n3 = rectangle.width;
            b4 = b2;
        }
        else {
            n = rectangle.y;
            n2 = point.y;
            n3 = rectangle.height;
            b4 = true;
        }
        if (b3) {
            final int n4 = (n3 >= 30) ? 10 : (n3 / 3);
            if (n2 < n + n4) {
                return b4 ? Section.LEADING : Section.TRAILING;
            }
            if (n2 >= n + n3 - n4) {
                return b4 ? Section.TRAILING : Section.LEADING;
            }
            return Section.MIDDLE;
        }
        else {
            final int n5 = n + n3 / 2;
            if (b4) {
                return (n2 >= n5) ? Section.TRAILING : Section.LEADING;
            }
            return (n2 < n5) ? Section.TRAILING : Section.LEADING;
        }
    }
    
    public static Section liesInHorizontal(final Rectangle rectangle, final Point point, final boolean b, final boolean b2) {
        return liesIn(rectangle, point, true, b, b2);
    }
    
    public static Section liesInVertical(final Rectangle rectangle, final Point point, final boolean b) {
        return liesIn(rectangle, point, false, false, b);
    }
    
    public static int convertColumnIndexToModel(final TableColumnModel tableColumnModel, final int n) {
        if (n < 0) {
            return n;
        }
        return tableColumnModel.getColumn(n).getModelIndex();
    }
    
    public static int convertColumnIndexToView(final TableColumnModel tableColumnModel, final int n) {
        if (n < 0) {
            return n;
        }
        for (int i = 0; i < tableColumnModel.getColumnCount(); ++i) {
            if (tableColumnModel.getColumn(i).getModelIndex() == n) {
                return i;
            }
        }
        return -1;
    }
    
    public static int getSystemMnemonicKeyMask() {
        final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
        if (defaultToolkit instanceof SunToolkit) {
            return ((SunToolkit)defaultToolkit).getFocusAcceleratorKeyMask();
        }
        return 8;
    }
    
    public static TreePath getTreePath(final TreeModelEvent treeModelEvent, final TreeModel treeModel) {
        TreePath treePath = treeModelEvent.getTreePath();
        if (treePath == null && treeModel != null) {
            final Object root = treeModel.getRoot();
            if (root != null) {
                treePath = new TreePath(root);
            }
        }
        return treePath;
    }
    
    static {
        LAF_STATE_KEY = new StringBuffer("LookAndFeel State");
        MENU_SELECTION_MANAGER_LISTENER_KEY = new StringBuffer("MenuSelectionManager listener key");
        DEFAULT_FRC = new FontRenderContext(null, false, false);
        AA_TEXT_PROPERTY_KEY = new StringBuffer("AATextInfoPropertyKey");
        SKIP_CLICK_COUNT = new StringBuilder("skipClickCount");
        COMPONENT_UI_PROPERTY_KEY = new StringBuffer("ComponentUIPropertyKey");
        BASICMENUITEMUI_MAX_TEXT_OFFSET = new StringUIClientPropertyKey("maxTextOffset");
        SwingUtilities2.inputEvent_CanAccessSystemClipboard_Field = null;
        charsBufferLock = new Object();
        SwingUtilities2.charsBuffer = new char[100];
        SwingUtilities2.fontCache = new LSBCacheEntry[6];
    }
    
    public static class AATextInfo
    {
        Object aaHint;
        Integer lcdContrastHint;
        FontRenderContext frc;
        
        private static AATextInfo getAATextInfoFromMap(final Map map) {
            final Object value = map.get(RenderingHints.KEY_TEXT_ANTIALIASING);
            final Integer value2 = map.get(RenderingHints.KEY_TEXT_LCD_CONTRAST);
            if (value == null || value == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF || value == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
                return null;
            }
            return new AATextInfo(value, value2);
        }
        
        public static AATextInfo getAATextInfo(final boolean aaFontSettingsCondition) {
            SunToolkit.setAAFontSettingsCondition(aaFontSettingsCondition);
            final Object desktopProperty = Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
            if (desktopProperty instanceof Map) {
                return getAATextInfoFromMap((Map)desktopProperty);
            }
            return null;
        }
        
        public AATextInfo(final Object aaHint, final Integer lcdContrastHint) {
            if (aaHint == null) {
                throw new InternalError("null not allowed here");
            }
            if (aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF || aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
                throw new InternalError("AA must be on");
            }
            this.aaHint = aaHint;
            this.lcdContrastHint = lcdContrastHint;
            this.frc = new FontRenderContext(null, aaHint, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
        }
    }
    
    private static class LSBCacheEntry
    {
        private static final byte UNSET = Byte.MAX_VALUE;
        private static final char[] oneChar;
        private byte[] lsbCache;
        private Font font;
        private FontRenderContext frc;
        
        public LSBCacheEntry(final FontRenderContext fontRenderContext, final Font font) {
            this.lsbCache = new byte[1];
            this.reset(fontRenderContext, font);
        }
        
        public void reset(final FontRenderContext frc, final Font font) {
            this.font = font;
            this.frc = frc;
            for (int i = this.lsbCache.length - 1; i >= 0; --i) {
                this.lsbCache[i] = 127;
            }
        }
        
        public int getLeftSideBearing(final char c) {
            final int n = c - 'W';
            assert n >= 0 && n < 1;
            byte b = this.lsbCache[n];
            if (b == 127) {
                LSBCacheEntry.oneChar[0] = c;
                b = (byte)this.font.createGlyphVector(this.frc, LSBCacheEntry.oneChar).getGlyphPixelBounds(0, this.frc, 0.0f, 0.0f).x;
                if (b < 0) {
                    final Object antiAliasingHint = this.frc.getAntiAliasingHint();
                    if (antiAliasingHint == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB || antiAliasingHint == RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) {
                        ++b;
                    }
                }
                this.lsbCache[n] = b;
            }
            return b;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof LSBCacheEntry)) {
                return false;
            }
            final LSBCacheEntry lsbCacheEntry = (LSBCacheEntry)o;
            return this.font.equals(lsbCacheEntry.font) && this.frc.equals(lsbCacheEntry.frc);
        }
        
        @Override
        public int hashCode() {
            int n = 17;
            if (this.font != null) {
                n = 37 * n + this.font.hashCode();
            }
            if (this.frc != null) {
                n = 37 * n + this.frc.hashCode();
            }
            return n;
        }
        
        static {
            oneChar = new char[1];
        }
    }
    
    public enum Section
    {
        LEADING, 
        MIDDLE, 
        TRAILING;
    }
    
    public interface RepaintListener
    {
        void repaintPerformed(final JComponent p0, final int p1, final int p2, final int p3, final int p4);
    }
}
