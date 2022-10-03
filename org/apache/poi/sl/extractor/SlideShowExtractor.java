package org.apache.poi.sl.extractor;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.sl.usermodel.Comment;
import com.zaxxer.sparsebits.SparseBitSet;
import java.util.BitSet;
import org.apache.poi.util.LocaleUtil;
import java.util.ArrayList;
import org.apache.poi.sl.usermodel.ObjectShape;
import org.apache.poi.sl.usermodel.Notes;
import org.apache.poi.sl.usermodel.TableCell;
import org.apache.poi.sl.usermodel.TableShape;
import org.apache.poi.sl.usermodel.ShapeContainer;
import java.util.LinkedList;
import org.apache.poi.sl.usermodel.PlaceholderDetails;
import org.apache.poi.sl.usermodel.Placeholder;
import java.util.function.Function;
import java.util.List;
import org.apache.poi.sl.usermodel.TextShape;
import org.apache.poi.sl.usermodel.MasterSheet;
import org.apache.poi.sl.usermodel.Sheet;
import java.util.function.Consumer;
import java.util.Iterator;
import org.apache.poi.sl.usermodel.Slide;
import java.io.Closeable;
import java.util.function.Predicate;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.util.POILogger;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.Shape;

public class SlideShowExtractor<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends POITextExtractor
{
    private static final POILogger LOG;
    private static final String SLIDE_NUMBER_PH = "\u2039#\u203a";
    private SlideShow<S, P> slideshow;
    private boolean slidesByDefault;
    private boolean notesByDefault;
    private boolean commentsByDefault;
    private boolean masterByDefault;
    private Predicate<Object> filter;
    
    public SlideShowExtractor(final SlideShow<S, P> slideshow) {
        this.slidesByDefault = true;
        this.filter = (o -> true);
        this.setFilesystem(slideshow);
        this.slideshow = slideshow;
    }
    
    @Override
    public final Object getDocument() {
        return this.slideshow.getPersistDocument();
    }
    
    public void setSlidesByDefault(final boolean slidesByDefault) {
        this.slidesByDefault = slidesByDefault;
    }
    
    public void setNotesByDefault(final boolean notesByDefault) {
        this.notesByDefault = notesByDefault;
    }
    
    public void setCommentsByDefault(final boolean commentsByDefault) {
        this.commentsByDefault = commentsByDefault;
    }
    
    public void setMasterByDefault(final boolean masterByDefault) {
        this.masterByDefault = masterByDefault;
    }
    
    @Override
    public POITextExtractor getMetadataTextExtractor() {
        return this.slideshow.getMetadataTextExtractor();
    }
    
    @Override
    public String getText() {
        final StringBuilder sb = new StringBuilder();
        for (final Slide<S, P> slide : this.slideshow.getSlides()) {
            this.getText(slide, sb::append);
        }
        return sb.toString();
    }
    
    public String getText(final Slide<S, P> slide) {
        final StringBuilder sb = new StringBuilder();
        this.getText(slide, sb::append);
        return sb.toString();
    }
    
    private void getText(final Slide<S, P> slide, final Consumer<String> consumer) {
        if (this.slidesByDefault) {
            this.printShapeText(slide, consumer);
        }
        if (this.masterByDefault) {
            final MasterSheet<S, P> ms = slide.getMasterSheet();
            this.printSlideMaster(ms, consumer);
            final MasterSheet<S, P> sl = slide.getSlideLayout();
            if (sl != ms) {
                this.printSlideMaster(sl, consumer);
            }
        }
        if (this.commentsByDefault) {
            this.printComments(slide, consumer);
        }
        if (this.notesByDefault) {
            this.printNotes(slide, consumer);
        }
    }
    
    private void printSlideMaster(final MasterSheet<S, P> master, final Consumer<String> consumer) {
        if (master == null) {
            return;
        }
        for (final Shape<S, P> shape : master) {
            if (shape instanceof TextShape) {
                final TextShape<S, P> ts = (TextShape)shape;
                final String text = ts.getText();
                if (text == null || text.isEmpty()) {
                    continue;
                }
                if ("*".equals(text)) {
                    continue;
                }
                if (ts.isPlaceholder()) {
                    SlideShowExtractor.LOG.log(3, "Ignoring boiler plate (placeholder) text on slide master:", text);
                }
                else {
                    this.printTextParagraphs(ts.getTextParagraphs(), consumer);
                }
            }
        }
    }
    
    private void printTextParagraphs(final List<P> paras, final Consumer<String> consumer) {
        this.printTextParagraphs(paras, consumer, "\n");
    }
    
    private void printTextParagraphs(final List<P> paras, final Consumer<String> consumer, final String trailer) {
        this.printTextParagraphs(paras, consumer, trailer, SlideShowExtractor::replaceTextCap);
    }
    
    private void printTextParagraphs(final List<P> paras, final Consumer<String> consumer, final String trailer, final Function<TextRun, String> converter) {
        for (final P p : paras) {
            for (final TextRun r : p) {
                if (this.filter.test(r)) {
                    consumer.accept(converter.apply(r));
                }
            }
            if (!trailer.isEmpty() && this.filter.test(trailer)) {
                consumer.accept(trailer);
            }
        }
    }
    
    private void printHeaderFooter(final Sheet<S, P> sheet, final Consumer<String> consumer, final Consumer<String> footerCon) {
        final Sheet<S, P> m = (sheet instanceof Slide) ? sheet.getMasterSheet() : sheet;
        this.addSheetPlaceholderDatails(sheet, Placeholder.HEADER, consumer);
        this.addSheetPlaceholderDatails(sheet, Placeholder.FOOTER, footerCon);
        if (!this.masterByDefault) {
            return;
        }
        for (final Shape<S, P> s : m) {
            if (!(s instanceof TextShape)) {
                continue;
            }
            final TextShape<S, P> ts = (TextShape)s;
            final PlaceholderDetails pd = ts.getPlaceholderDetails();
            if (pd == null || !pd.isVisible()) {
                continue;
            }
            if (pd.getPlaceholder() == null) {
                continue;
            }
            switch (pd.getPlaceholder()) {
                case HEADER: {
                    this.printTextParagraphs(ts.getTextParagraphs(), consumer);
                    continue;
                }
                case FOOTER: {
                    this.printTextParagraphs(ts.getTextParagraphs(), footerCon);
                    continue;
                }
                case SLIDE_NUMBER: {
                    this.printTextParagraphs(ts.getTextParagraphs(), footerCon, "\n", SlideShowExtractor::replaceSlideNumber);
                    continue;
                }
            }
        }
    }
    
    private void addSheetPlaceholderDatails(final Sheet<S, P> sheet, final Placeholder placeholder, final Consumer<String> consumer) {
        final PlaceholderDetails headerPD = sheet.getPlaceholderDetails(placeholder);
        final String headerStr = (headerPD != null) ? headerPD.getText() : null;
        if (headerStr != null && this.filter.test(headerPD)) {
            consumer.accept(headerStr);
        }
    }
    
    private void printShapeText(final Sheet<S, P> sheet, final Consumer<String> consumer) {
        final List<String> footer = new LinkedList<String>();
        this.printHeaderFooter(sheet, consumer, footer::add);
        this.printShapeText((ShapeContainer<S, P>)sheet, consumer);
        footer.forEach(consumer);
    }
    
    private void printShapeText(final ShapeContainer<S, P> container, final Consumer<String> consumer) {
        for (final Shape<S, P> shape : container) {
            if (shape instanceof TextShape) {
                this.printTextParagraphs(((TextShape)shape).getTextParagraphs(), consumer);
            }
            else if (shape instanceof TableShape) {
                this.printShapeText((TableShape)shape, consumer);
            }
            else {
                if (!(shape instanceof ShapeContainer)) {
                    continue;
                }
                this.printShapeText((ShapeContainer)shape, consumer);
            }
        }
    }
    
    private void printShapeText(final TableShape<S, P> shape, final Consumer<String> consumer) {
        final int nrows = shape.getNumberOfRows();
        final int ncols = shape.getNumberOfColumns();
        for (int row = 0; row < nrows; ++row) {
            String trailer = "";
            for (int col = 0; col < ncols; ++col) {
                final TableCell<S, P> cell = shape.getCell(row, col);
                if (cell != null) {
                    trailer = ((col < ncols - 1) ? "\t" : "\n");
                    this.printTextParagraphs(cell.getTextParagraphs(), consumer, trailer);
                }
            }
            if (!trailer.equals("\n") && this.filter.test("\n")) {
                consumer.accept("\n");
            }
        }
    }
    
    private void printComments(final Slide<S, P> slide, final Consumer<String> consumer) {
        slide.getComments().stream().filter(this.filter).map(c -> c.getAuthor() + " - " + c.getText()).forEach((Consumer<? super Object>)consumer);
    }
    
    private void printNotes(final Slide<S, P> slide, final Consumer<String> consumer) {
        final Notes<S, P> notes = slide.getNotes();
        if (notes == null) {
            return;
        }
        final List<String> footer = new LinkedList<String>();
        this.printHeaderFooter(notes, consumer, footer::add);
        this.printShapeText(notes, consumer);
        footer.forEach(consumer);
    }
    
    public List<? extends ObjectShape<S, P>> getOLEShapes() {
        final List<ObjectShape<S, P>> oleShapes = new ArrayList<ObjectShape<S, P>>();
        for (final Slide<S, P> slide : this.slideshow.getSlides()) {
            this.addOLEShapes(oleShapes, slide);
        }
        return oleShapes;
    }
    
    private void addOLEShapes(final List<ObjectShape<S, P>> oleShapes, final ShapeContainer<S, P> container) {
        for (final Shape<S, P> shape : container) {
            if (shape instanceof ShapeContainer) {
                this.addOLEShapes(oleShapes, (ShapeContainer)shape);
            }
            else {
                if (!(shape instanceof ObjectShape)) {
                    continue;
                }
                oleShapes.add((ObjectShape)shape);
            }
        }
    }
    
    private static String replaceSlideNumber(final TextRun tr) {
        final String raw = tr.getRawText();
        if (!raw.contains("\u2039#\u203a")) {
            return raw;
        }
        final TextParagraph tp = tr.getParagraph();
        final TextShape ps = (tp != null) ? tp.getParentShape() : null;
        final Sheet sh = (ps != null) ? ps.getSheet() : null;
        final String slideNr = (sh instanceof Slide) ? Integer.toString(((Slide)sh).getSlideNumber() + 1) : "";
        return raw.replace("\u2039#\u203a", slideNr);
    }
    
    private static String replaceTextCap(final TextRun tr) {
        final TextParagraph tp = tr.getParagraph();
        final TextShape sh = (tp != null) ? tp.getParentShape() : null;
        final Placeholder ph = (sh != null) ? sh.getPlaceholder() : null;
        final char sep = (ph == Placeholder.TITLE || ph == Placeholder.CENTERED_TITLE || ph == Placeholder.SUBTITLE) ? '\n' : ' ';
        String txt = tr.getRawText();
        txt = txt.replace('\r', '\n');
        txt = txt.replace('\u000b', sep);
        switch (tr.getTextCap()) {
            case ALL: {
                txt = txt.toUpperCase(LocaleUtil.getUserLocale());
            }
            case SMALL: {
                txt = txt.toLowerCase(LocaleUtil.getUserLocale());
                break;
            }
        }
        return txt;
    }
    
    @Deprecated
    public BitSet getCodepoints(final String typeface, final Boolean italic, final Boolean bold) {
        final BitSet glyphs = new BitSet();
        final Predicate<Object> filterOld = this.filter;
        try {
            this.filter = (o -> filterFonts(o, typeface, italic, bold));
            this.slideshow.getSlides().forEach(slide -> this.getText(slide, s -> s.codePoints().forEach(glyphs::set)));
        }
        finally {
            this.filter = filterOld;
        }
        return glyphs;
    }
    
    public SparseBitSet getCodepointsInSparseBitSet(final String typeface, final Boolean italic, final Boolean bold) {
        final SparseBitSet glyphs = new SparseBitSet();
        final Predicate<Object> filterOld = this.filter;
        try {
            this.filter = (o -> filterFonts(o, typeface, italic, bold));
            this.slideshow.getSlides().forEach(slide -> this.getText(slide, s -> s.codePoints().forEach(glyphs::set)));
        }
        finally {
            this.filter = filterOld;
        }
        return glyphs;
    }
    
    private static boolean filterFonts(final Object o, final String typeface, final Boolean italic, final Boolean bold) {
        if (!(o instanceof TextRun)) {
            return false;
        }
        final TextRun tr = (TextRun)o;
        return typeface.equalsIgnoreCase(tr.getFontFamily()) && (italic == null || tr.isItalic() == italic) && (bold == null || tr.isBold() == bold);
    }
    
    static {
        LOG = POILogFactory.getLogger(SlideShowExtractor.class);
    }
}
