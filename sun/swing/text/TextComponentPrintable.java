package sun.swing.text;

import javax.swing.text.BadLocationException;
import java.awt.Dimension;
import javax.swing.JViewport;
import java.lang.reflect.InvocationTargetException;
import javax.swing.text.AbstractDocument;
import java.awt.Insets;
import java.awt.ComponentOrientation;
import java.awt.Shape;
import java.awt.Rectangle;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import java.awt.print.PrinterException;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.Graphics;
import javax.swing.border.Border;
import javax.swing.text.EditorKit;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import sun.font.FontDesignMetrics;
import java.awt.FontMetrics;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.Collections;
import javax.swing.SwingUtilities;
import javax.swing.CellRendererPane;
import java.awt.Component;
import sun.swing.text.html.FrameEditorPaneTag;
import java.awt.Container;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.Document;
import java.util.Iterator;
import java.util.ArrayList;
import javax.swing.JEditorPane;
import java.awt.print.Printable;
import java.util.List;
import java.awt.Font;
import java.text.MessageFormat;
import java.awt.font.FontRenderContext;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.text.JTextComponent;

public class TextComponentPrintable implements CountingPrintable
{
    private static final int LIST_SIZE = 1000;
    private boolean isLayouted;
    private final JTextComponent textComponentToPrint;
    private final AtomicReference<FontRenderContext> frc;
    private final JTextComponent printShell;
    private final MessageFormat headerFormat;
    private final MessageFormat footerFormat;
    private static final float HEADER_FONT_SIZE = 18.0f;
    private static final float FOOTER_FONT_SIZE = 12.0f;
    private final Font headerFont;
    private final Font footerFont;
    private final List<IntegerSegment> rowsMetrics;
    private final List<IntegerSegment> pagesMetrics;
    private boolean needReadLock;
    
    public static Printable getPrintable(final JTextComponent textComponent, final MessageFormat messageFormat, final MessageFormat messageFormat2) {
        if (textComponent instanceof JEditorPane && isFrameSetDocument(textComponent.getDocument())) {
            final List<JEditorPane> frames = getFrames((JEditorPane)textComponent);
            final ArrayList list = new ArrayList();
            final Iterator<JEditorPane> iterator = frames.iterator();
            while (iterator.hasNext()) {
                list.add(getPrintable(iterator.next(), messageFormat, messageFormat2));
            }
            return new CompoundPrintable(list);
        }
        return new TextComponentPrintable(textComponent, messageFormat, messageFormat2);
    }
    
    private static boolean isFrameSetDocument(final Document document) {
        boolean b = false;
        if (document instanceof HTMLDocument && ((HTMLDocument)document).getIterator(HTML.Tag.FRAME).isValid()) {
            b = true;
        }
        return b;
    }
    
    private static List<JEditorPane> getFrames(final JEditorPane editorPane) {
        final ArrayList list = new ArrayList();
        getFrames(editorPane, list);
        if (list.size() == 0) {
            createFrames(editorPane);
            getFrames(editorPane, list);
        }
        return list;
    }
    
    private static void getFrames(final Container container, final List<JEditorPane> list) {
        for (final Component component : container.getComponents()) {
            if (component instanceof FrameEditorPaneTag && component instanceof JEditorPane) {
                list.add((JEditorPane)component);
            }
            else if (component instanceof Container) {
                getFrames((Container)component, list);
            }
        }
    }
    
    private static void createFrames(final JEditorPane editorPane) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final CellRendererPane cellRendererPane = new CellRendererPane();
                cellRendererPane.add(editorPane);
                cellRendererPane.setSize(500, 500);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            }
            catch (final Exception ex) {
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException)ex;
                }
                throw new RuntimeException(ex);
            }
        }
    }
    
    private TextComponentPrintable(final JTextComponent textComponentToPrint, final MessageFormat headerFormat, final MessageFormat footerFormat) {
        this.isLayouted = false;
        this.frc = new AtomicReference<FontRenderContext>(null);
        this.needReadLock = false;
        this.textComponentToPrint = textComponentToPrint;
        this.headerFormat = headerFormat;
        this.footerFormat = footerFormat;
        this.headerFont = textComponentToPrint.getFont().deriveFont(1, 18.0f);
        this.footerFont = textComponentToPrint.getFont().deriveFont(0, 12.0f);
        this.pagesMetrics = Collections.synchronizedList(new ArrayList<IntegerSegment>());
        this.rowsMetrics = new ArrayList<IntegerSegment>(1000);
        this.printShell = this.createPrintShell(textComponentToPrint);
    }
    
    private JTextComponent createPrintShell(final JTextComponent textComponent) {
        if (SwingUtilities.isEventDispatchThread()) {
            return this.createPrintShellOnEDT(textComponent);
        }
        final FutureTask futureTask = new FutureTask((Callable<V>)new Callable<JTextComponent>() {
            @Override
            public JTextComponent call() throws Exception {
                return TextComponentPrintable.this.createPrintShellOnEDT(textComponent);
            }
        });
        SwingUtilities.invokeLater(futureTask);
        try {
            return (JTextComponent)futureTask.get();
        }
        catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        catch (final ExecutionException ex2) {
            final Throwable cause = ex2.getCause();
            if (cause instanceof Error) {
                throw (Error)cause;
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            throw new AssertionError((Object)cause);
        }
    }
    
    private JTextComponent createPrintShellOnEDT(final JTextComponent textComponent) {
        assert SwingUtilities.isEventDispatchThread();
        JTextComponent textComponent2 = null;
        if (textComponent instanceof JPasswordField) {
            textComponent2 = new JPasswordField() {
                {
                    this.setEchoChar(((JPasswordField)textComponent).getEchoChar());
                    this.setHorizontalAlignment(((JTextField)textComponent).getHorizontalAlignment());
                }
                
                @Override
                public FontMetrics getFontMetrics(final Font font) {
                    return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(font) : FontDesignMetrics.getMetrics(font, TextComponentPrintable.this.frc.get());
                }
            };
        }
        else if (textComponent instanceof JTextField) {
            textComponent2 = new JTextField() {
                {
                    this.setHorizontalAlignment(((JTextField)textComponent).getHorizontalAlignment());
                }
                
                @Override
                public FontMetrics getFontMetrics(final Font font) {
                    return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(font) : FontDesignMetrics.getMetrics(font, TextComponentPrintable.this.frc.get());
                }
            };
        }
        else if (textComponent instanceof JTextArea) {
            textComponent2 = new JTextArea() {
                {
                    final JTextArea textArea = (JTextArea)textComponent;
                    this.setLineWrap(textArea.getLineWrap());
                    this.setWrapStyleWord(textArea.getWrapStyleWord());
                    this.setTabSize(textArea.getTabSize());
                }
                
                @Override
                public FontMetrics getFontMetrics(final Font font) {
                    return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(font) : FontDesignMetrics.getMetrics(font, TextComponentPrintable.this.frc.get());
                }
            };
        }
        else if (textComponent instanceof JTextPane) {
            textComponent2 = new JTextPane() {
                @Override
                public FontMetrics getFontMetrics(final Font font) {
                    return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(font) : FontDesignMetrics.getMetrics(font, TextComponentPrintable.this.frc.get());
                }
                
                @Override
                public EditorKit getEditorKit() {
                    if (this.getDocument() == textComponent.getDocument()) {
                        return ((JTextPane)textComponent).getEditorKit();
                    }
                    return super.getEditorKit();
                }
            };
        }
        else if (textComponent instanceof JEditorPane) {
            textComponent2 = new JEditorPane() {
                @Override
                public FontMetrics getFontMetrics(final Font font) {
                    return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(font) : FontDesignMetrics.getMetrics(font, TextComponentPrintable.this.frc.get());
                }
                
                @Override
                public EditorKit getEditorKit() {
                    if (this.getDocument() == textComponent.getDocument()) {
                        return ((JEditorPane)textComponent).getEditorKit();
                    }
                    return super.getEditorKit();
                }
            };
        }
        textComponent2.setBorder(null);
        textComponent2.setOpaque(textComponent.isOpaque());
        textComponent2.setEditable(textComponent.isEditable());
        textComponent2.setEnabled(textComponent.isEnabled());
        textComponent2.setFont(textComponent.getFont());
        textComponent2.setBackground(textComponent.getBackground());
        textComponent2.setForeground(textComponent.getForeground());
        textComponent2.setComponentOrientation(textComponent.getComponentOrientation());
        if (textComponent2 instanceof JEditorPane) {
            textComponent2.putClientProperty("JEditorPane.honorDisplayProperties", textComponent.getClientProperty("JEditorPane.honorDisplayProperties"));
            textComponent2.putClientProperty("JEditorPane.w3cLengthUnits", textComponent.getClientProperty("JEditorPane.w3cLengthUnits"));
            textComponent2.putClientProperty("charset", textComponent.getClientProperty("charset"));
        }
        textComponent2.setDocument(textComponent.getDocument());
        return textComponent2;
    }
    
    @Override
    public int getNumberOfPages() {
        return this.pagesMetrics.size();
    }
    
    @Override
    public int print(final Graphics graphics, final PageFormat pageFormat, final int n) throws PrinterException {
        if (!this.isLayouted) {
            if (graphics instanceof Graphics2D) {
                this.frc.set(((Graphics2D)graphics).getFontRenderContext());
            }
            this.layout((int)Math.floor(pageFormat.getImageableWidth()));
            this.calculateRowsMetrics();
        }
        int n2;
        if (!SwingUtilities.isEventDispatchThread()) {
            final FutureTask futureTask = new FutureTask((Callable<V>)new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return TextComponentPrintable.this.printOnEDT(graphics, pageFormat, n);
                }
            });
            SwingUtilities.invokeLater(futureTask);
            try {
                n2 = futureTask.get();
            }
            catch (final InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            catch (final ExecutionException ex2) {
                final Throwable cause = ex2.getCause();
                if (cause instanceof PrinterException) {
                    throw (PrinterException)cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new RuntimeException(cause);
            }
        }
        else {
            n2 = this.printOnEDT(graphics, pageFormat, n);
        }
        return n2;
    }
    
    private int printOnEDT(final Graphics graphics, final PageFormat pageFormat, final int n) throws PrinterException {
        assert SwingUtilities.isEventDispatchThread();
        Border emptyBorder = BorderFactory.createEmptyBorder();
        if (this.headerFormat != null || this.footerFormat != null) {
            final Object[] array = { n + 1 };
            if (this.headerFormat != null) {
                emptyBorder = new TitledBorder(emptyBorder, this.headerFormat.format(array), 2, 1, this.headerFont, this.printShell.getForeground());
            }
            if (this.footerFormat != null) {
                emptyBorder = new TitledBorder(emptyBorder, this.footerFormat.format(array), 2, 6, this.footerFont, this.printShell.getForeground());
            }
        }
        final Insets borderInsets = emptyBorder.getBorderInsets(this.printShell);
        this.updatePagesMetrics(n, (int)Math.floor(pageFormat.getImageableHeight()) - borderInsets.top - borderInsets.bottom);
        if (this.pagesMetrics.size() <= n) {
            return 1;
        }
        final Graphics2D graphics2D = (Graphics2D)graphics.create();
        graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        emptyBorder.paintBorder(this.printShell, graphics2D, 0, 0, (int)Math.floor(pageFormat.getImageableWidth()), (int)Math.floor(pageFormat.getImageableHeight()));
        graphics2D.translate(0, borderInsets.top);
        graphics2D.clip(new Rectangle(0, 0, (int)pageFormat.getWidth(), this.pagesMetrics.get(n).end - this.pagesMetrics.get(n).start + 1));
        int n2 = 0;
        if (ComponentOrientation.RIGHT_TO_LEFT == this.printShell.getComponentOrientation()) {
            n2 = (int)pageFormat.getImageableWidth() - this.printShell.getWidth();
        }
        graphics2D.translate(n2, -this.pagesMetrics.get(n).start);
        this.printShell.print(graphics2D);
        graphics2D.dispose();
        return 0;
    }
    
    private void releaseReadLock() {
        assert !SwingUtilities.isEventDispatchThread();
        final Document document = this.textComponentToPrint.getDocument();
        if (document instanceof AbstractDocument) {
            try {
                ((AbstractDocument)document).readUnlock();
                this.needReadLock = true;
            }
            catch (final Error error) {}
        }
    }
    
    private void acquireReadLock() {
        assert !SwingUtilities.isEventDispatchThread();
        if (this.needReadLock) {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
            catch (final InterruptedException ex) {}
            catch (final InvocationTargetException ex2) {}
            ((AbstractDocument)this.textComponentToPrint.getDocument()).readLock();
            this.needReadLock = false;
        }
    }
    
    private void layout(final int n) {
        if (!SwingUtilities.isEventDispatchThread()) {
            final FutureTask futureTask = new FutureTask((Callable<V>)new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    TextComponentPrintable.this.layoutOnEDT(n);
                    return null;
                }
            });
            this.releaseReadLock();
            SwingUtilities.invokeLater(futureTask);
            try {
                futureTask.get();
            }
            catch (final InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            catch (final ExecutionException ex2) {
                final Throwable cause = ex2.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                throw new RuntimeException(cause);
            }
            finally {
                this.acquireReadLock();
            }
        }
        else {
            this.layoutOnEDT(n);
        }
        this.isLayouted = true;
    }
    
    private void layoutOnEDT(final int n) {
        assert SwingUtilities.isEventDispatchThread();
        final CellRendererPane cellRendererPane = new CellRendererPane();
        final JViewport viewport = new JViewport();
        viewport.setBorder(null);
        Dimension dimension = new Dimension(n, 2147482647);
        if (this.printShell instanceof JTextField) {
            dimension = new Dimension(dimension.width, this.printShell.getPreferredSize().height);
        }
        this.printShell.setSize(dimension);
        viewport.setComponentOrientation(this.printShell.getComponentOrientation());
        viewport.setSize(dimension);
        viewport.add(this.printShell);
        cellRendererPane.add(viewport);
    }
    
    private void updatePagesMetrics(final int n, final int n2) {
        while (n >= this.pagesMetrics.size() && !this.rowsMetrics.isEmpty()) {
            final int n3 = this.pagesMetrics.size() - 1;
            int n4;
            int n5;
            for (n4 = ((n3 >= 0) ? (this.pagesMetrics.get(n3).end + 1) : 0), n5 = 0; n5 < this.rowsMetrics.size() && this.rowsMetrics.get(n5).end - n4 + 1 <= n2; ++n5) {}
            if (n5 == 0) {
                this.pagesMetrics.add(new IntegerSegment(n4, n4 + n2 - 1));
            }
            else {
                --n5;
                this.pagesMetrics.add(new IntegerSegment(n4, this.rowsMetrics.get(n5).end));
                for (int i = 0; i <= n5; ++i) {
                    this.rowsMetrics.remove(0);
                }
            }
        }
    }
    
    private void calculateRowsMetrics() {
        final int length = this.printShell.getDocument().getLength();
        final ArrayList list = new ArrayList(1000);
        int i = 0;
        int n = -1;
        int n2 = -1;
        while (i < length) {
            try {
                final Rectangle modelToView = this.printShell.modelToView(i);
                if (modelToView != null) {
                    final int n3 = (int)modelToView.getY();
                    final int n4 = (int)modelToView.getHeight();
                    if (n4 != 0 && (n3 != n || n4 != n2)) {
                        n = n3;
                        n2 = n4;
                        list.add(new IntegerSegment(n3, n3 + n4 - 1));
                    }
                }
            }
            catch (final BadLocationException ex) {
                assert false;
            }
            ++i;
        }
        Collections.sort((List<Comparable>)list);
        int start = Integer.MIN_VALUE;
        int n5 = Integer.MIN_VALUE;
        for (final IntegerSegment integerSegment : list) {
            if (n5 < integerSegment.start) {
                if (n5 != Integer.MIN_VALUE) {
                    this.rowsMetrics.add(new IntegerSegment(start, n5));
                }
                start = integerSegment.start;
                n5 = integerSegment.end;
            }
            else {
                n5 = integerSegment.end;
            }
        }
        if (n5 != Integer.MIN_VALUE) {
            this.rowsMetrics.add(new IntegerSegment(start, n5));
        }
    }
    
    private static class IntegerSegment implements Comparable<IntegerSegment>
    {
        final int start;
        final int end;
        
        IntegerSegment(final int start, final int end) {
            this.start = start;
            this.end = end;
        }
        
        @Override
        public int compareTo(final IntegerSegment integerSegment) {
            final int n = this.start - integerSegment.start;
            return (n != 0) ? n : (this.end - integerSegment.end);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof IntegerSegment && this.compareTo((IntegerSegment)o) == 0;
        }
        
        @Override
        public int hashCode() {
            return 37 * (37 * 17 + this.start) + this.end;
        }
        
        @Override
        public String toString() {
            return "IntegerSegment [" + this.start + ", " + this.end + "]";
        }
    }
}
