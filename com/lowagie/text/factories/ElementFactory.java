package com.lowagie.text.factories;

import java.util.Hashtable;
import com.lowagie.text.Annotation;
import java.io.IOException;
import java.net.MalformedURLException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.Image;
import com.lowagie.text.Section;
import com.lowagie.text.ChapterAutoNumber;
import java.awt.Color;
import com.lowagie.text.BadElementException;
import com.lowagie.text.ExceptionConverter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import com.lowagie.text.Table;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Cell;
import com.lowagie.text.Utilities;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Anchor;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.Markup;
import com.lowagie.text.ElementTags;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Chunk;
import java.util.Properties;

public class ElementFactory
{
    public static Chunk getChunk(final Properties attributes) {
        final Chunk chunk = new Chunk();
        chunk.setFont(FontFactory.getFont(attributes));
        String value = attributes.getProperty("itext");
        if (value != null) {
            chunk.append(value);
        }
        value = attributes.getProperty(ElementTags.LOCALGOTO);
        if (value != null) {
            chunk.setLocalGoto(value);
        }
        value = attributes.getProperty(ElementTags.REMOTEGOTO);
        if (value != null) {
            final String page = attributes.getProperty("page");
            if (page != null) {
                chunk.setRemoteGoto(value, Integer.parseInt(page));
            }
            else {
                final String destination = attributes.getProperty("destination");
                if (destination != null) {
                    chunk.setRemoteGoto(value, destination);
                }
            }
        }
        value = attributes.getProperty(ElementTags.LOCALDESTINATION);
        if (value != null) {
            chunk.setLocalDestination(value);
        }
        value = attributes.getProperty(ElementTags.SUBSUPSCRIPT);
        if (value != null) {
            chunk.setTextRise(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("vertical-align");
        if (value != null && value.endsWith("%")) {
            final float p = Float.parseFloat(value.substring(0, value.length() - 1) + "f") / 100.0f;
            chunk.setTextRise(p * chunk.getFont().getSize());
        }
        value = attributes.getProperty(ElementTags.GENERICTAG);
        if (value != null) {
            chunk.setGenericTag(value);
        }
        value = attributes.getProperty("backgroundcolor");
        if (value != null) {
            chunk.setBackground(Markup.decodeColor(value));
        }
        return chunk;
    }
    
    public static Phrase getPhrase(final Properties attributes) {
        final Phrase phrase = new Phrase();
        phrase.setFont(FontFactory.getFont(attributes));
        String value = attributes.getProperty("leading");
        if (value != null) {
            phrase.setLeading(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("line-height");
        if (value != null) {
            phrase.setLeading(Markup.parseLength(value, 12.0f));
        }
        value = attributes.getProperty("itext");
        if (value != null) {
            final Chunk chunk = new Chunk(value);
            if ((value = attributes.getProperty(ElementTags.GENERICTAG)) != null) {
                chunk.setGenericTag(value);
            }
            phrase.add(chunk);
        }
        return phrase;
    }
    
    public static Anchor getAnchor(final Properties attributes) {
        final Anchor anchor = new Anchor(getPhrase(attributes));
        String value = attributes.getProperty("name");
        if (value != null) {
            anchor.setName(value);
        }
        value = ((Hashtable<K, String>)attributes).remove("reference");
        if (value != null) {
            anchor.setReference(value);
        }
        return anchor;
    }
    
    public static Paragraph getParagraph(final Properties attributes) {
        final Paragraph paragraph = new Paragraph(getPhrase(attributes));
        String value = attributes.getProperty("align");
        if (value != null) {
            paragraph.setAlignment(value);
        }
        value = attributes.getProperty("indentationleft");
        if (value != null) {
            paragraph.setIndentationLeft(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("indentationright");
        if (value != null) {
            paragraph.setIndentationRight(Float.parseFloat(value + "f"));
        }
        return paragraph;
    }
    
    public static ListItem getListItem(final Properties attributes) {
        final ListItem item = new ListItem(getParagraph(attributes));
        return item;
    }
    
    public static List getList(final Properties attributes) {
        final List list = new List();
        list.setNumbered(Utilities.checkTrueOrFalse(attributes, "numbered"));
        list.setLettered(Utilities.checkTrueOrFalse(attributes, "lettered"));
        list.setLowercase(Utilities.checkTrueOrFalse(attributes, "lowercase"));
        list.setAutoindent(Utilities.checkTrueOrFalse(attributes, "autoindent"));
        list.setAlignindent(Utilities.checkTrueOrFalse(attributes, "alignindent"));
        String value = attributes.getProperty("first");
        if (value != null) {
            final char character = value.charAt(0);
            if (Character.isLetter(character)) {
                list.setFirst(character);
            }
            else {
                list.setFirst(Integer.parseInt(value));
            }
        }
        value = attributes.getProperty("listsymbol");
        if (value != null) {
            list.setListSymbol(new Chunk(value, FontFactory.getFont(attributes)));
        }
        value = attributes.getProperty("indentationleft");
        if (value != null) {
            list.setIndentationLeft(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("indentationright");
        if (value != null) {
            list.setIndentationRight(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("symbolindent");
        if (value != null) {
            list.setSymbolIndent(Float.parseFloat(value));
        }
        return list;
    }
    
    public static Cell getCell(final Properties attributes) {
        final Cell cell = new Cell();
        cell.setHorizontalAlignment(attributes.getProperty("horizontalalign"));
        cell.setVerticalAlignment(attributes.getProperty("verticalalign"));
        String value = attributes.getProperty("width");
        if (value != null) {
            cell.setWidth(value);
        }
        value = attributes.getProperty("colspan");
        if (value != null) {
            cell.setColspan(Integer.parseInt(value));
        }
        value = attributes.getProperty("rowspan");
        if (value != null) {
            cell.setRowspan(Integer.parseInt(value));
        }
        value = attributes.getProperty("leading");
        if (value != null) {
            cell.setLeading(Float.parseFloat(value + "f"));
        }
        cell.setHeader(Utilities.checkTrueOrFalse(attributes, "header"));
        if (Utilities.checkTrueOrFalse(attributes, "nowrap")) {
            cell.setMaxLines(1);
        }
        setRectangleProperties(cell, attributes);
        return cell;
    }
    
    public static Table getTable(final Properties attributes) {
        try {
            String value = attributes.getProperty("widths");
            Table table;
            if (value != null) {
                final StringTokenizer widthTokens = new StringTokenizer(value, ";");
                final ArrayList values = new ArrayList();
                while (widthTokens.hasMoreTokens()) {
                    values.add(widthTokens.nextToken());
                }
                table = new Table(values.size());
                final float[] widths = new float[table.getColumns()];
                for (int i = 0; i < values.size(); ++i) {
                    value = values.get(i);
                    widths[i] = Float.parseFloat(value + "f");
                }
                table.setWidths(widths);
            }
            else {
                value = attributes.getProperty("columns");
                try {
                    table = new Table(Integer.parseInt(value));
                }
                catch (final Exception e) {
                    table = new Table(1);
                }
            }
            table.setBorder(15);
            table.setBorderWidth(1.0f);
            table.getDefaultCell().setBorder(15);
            value = attributes.getProperty("lastHeaderRow");
            if (value != null) {
                table.setLastHeaderRow(Integer.parseInt(value));
            }
            value = attributes.getProperty("align");
            if (value != null) {
                table.setAlignment(value);
            }
            value = attributes.getProperty("cellspacing");
            if (value != null) {
                table.setSpacing(Float.parseFloat(value + "f"));
            }
            value = attributes.getProperty("cellpadding");
            if (value != null) {
                table.setPadding(Float.parseFloat(value + "f"));
            }
            value = attributes.getProperty("offset");
            if (value != null) {
                table.setOffset(Float.parseFloat(value + "f"));
            }
            value = attributes.getProperty("width");
            if (value != null) {
                if (value.endsWith("%")) {
                    table.setWidth(Float.parseFloat(value.substring(0, value.length() - 1) + "f"));
                }
                else {
                    table.setWidth(Float.parseFloat(value + "f"));
                    table.setLocked(true);
                }
            }
            table.setTableFitsPage(Utilities.checkTrueOrFalse(attributes, "tablefitspage"));
            table.setCellsFitPage(Utilities.checkTrueOrFalse(attributes, "cellsfitpage"));
            table.setConvert2pdfptable(Utilities.checkTrueOrFalse(attributes, "convert2pdfp"));
            setRectangleProperties(table, attributes);
            return table;
        }
        catch (final BadElementException e2) {
            throw new ExceptionConverter(e2);
        }
    }
    
    private static void setRectangleProperties(final Rectangle rect, final Properties attributes) {
        String value = attributes.getProperty("borderwidth");
        if (value != null) {
            rect.setBorderWidth(Float.parseFloat(value + "f"));
        }
        int border = 0;
        if (Utilities.checkTrueOrFalse(attributes, "left")) {
            border |= 0x4;
        }
        if (Utilities.checkTrueOrFalse(attributes, "right")) {
            border |= 0x8;
        }
        if (Utilities.checkTrueOrFalse(attributes, "top")) {
            border |= 0x1;
        }
        if (Utilities.checkTrueOrFalse(attributes, "bottom")) {
            border |= 0x2;
        }
        rect.setBorder(border);
        String r = attributes.getProperty("red");
        String g = attributes.getProperty("green");
        String b = attributes.getProperty("blue");
        if (r != null || g != null || b != null) {
            int red = 0;
            int green = 0;
            int blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            rect.setBorderColor(new Color(red, green, blue));
        }
        else {
            rect.setBorderColor(Markup.decodeColor(attributes.getProperty("bordercolor")));
        }
        r = ((Hashtable<K, String>)attributes).remove("bgred");
        g = ((Hashtable<K, String>)attributes).remove("bggreen");
        b = ((Hashtable<K, String>)attributes).remove("bgblue");
        value = attributes.getProperty("backgroundcolor");
        if (r != null || g != null || b != null) {
            int red = 0;
            int green = 0;
            int blue = 0;
            if (r != null) {
                red = Integer.parseInt(r);
            }
            if (g != null) {
                green = Integer.parseInt(g);
            }
            if (b != null) {
                blue = Integer.parseInt(b);
            }
            rect.setBackgroundColor(new Color(red, green, blue));
        }
        else if (value != null) {
            rect.setBackgroundColor(Markup.decodeColor(value));
        }
        else {
            value = attributes.getProperty("grayfill");
            if (value != null) {
                rect.setGrayFill(Float.parseFloat(value + "f"));
            }
        }
    }
    
    public static ChapterAutoNumber getChapter(final Properties attributes) {
        final ChapterAutoNumber chapter = new ChapterAutoNumber("");
        setSectionParameters(chapter, attributes);
        return chapter;
    }
    
    public static Section getSection(final Section parent, final Properties attributes) {
        final Section section = parent.addSection("");
        setSectionParameters(section, attributes);
        return section;
    }
    
    private static void setSectionParameters(final Section section, final Properties attributes) {
        String value = attributes.getProperty("numberdepth");
        if (value != null) {
            section.setNumberDepth(Integer.parseInt(value));
        }
        value = attributes.getProperty("indent");
        if (value != null) {
            section.setIndentation(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("indentationleft");
        if (value != null) {
            section.setIndentationLeft(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("indentationright");
        if (value != null) {
            section.setIndentationRight(Float.parseFloat(value + "f"));
        }
    }
    
    public static Image getImage(final Properties attributes) throws BadElementException, IOException {
        String value = attributes.getProperty("url");
        if (value == null) {
            throw new MalformedURLException(MessageLocalization.getComposedMessage("the.url.of.the.image.is.missing"));
        }
        final Image image = Image.getInstance(value);
        value = attributes.getProperty("align");
        int align = 0;
        if (value != null) {
            if ("Left".equalsIgnoreCase(value)) {
                align |= 0x0;
            }
            else if ("Right".equalsIgnoreCase(value)) {
                align |= 0x2;
            }
            else if ("Middle".equalsIgnoreCase(value)) {
                align |= 0x1;
            }
        }
        if ("true".equalsIgnoreCase(attributes.getProperty("underlying"))) {
            align |= 0x8;
        }
        if ("true".equalsIgnoreCase(attributes.getProperty("textwrap"))) {
            align |= 0x4;
        }
        image.setAlignment(align);
        value = attributes.getProperty("alt");
        if (value != null) {
            image.setAlt(value);
        }
        final String x = attributes.getProperty("absolutex");
        final String y = attributes.getProperty("absolutey");
        if (x != null && y != null) {
            image.setAbsolutePosition(Float.parseFloat(x + "f"), Float.parseFloat(y + "f"));
        }
        value = attributes.getProperty("plainwidth");
        if (value != null) {
            image.scaleAbsoluteWidth(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("plainheight");
        if (value != null) {
            image.scaleAbsoluteHeight(Float.parseFloat(value + "f"));
        }
        value = attributes.getProperty("rotation");
        if (value != null) {
            image.setRotation(Float.parseFloat(value + "f"));
        }
        return image;
    }
    
    public static Annotation getAnnotation(final Properties attributes) {
        float llx = 0.0f;
        float lly = 0.0f;
        float urx = 0.0f;
        float ury = 0.0f;
        String value = attributes.getProperty("llx");
        if (value != null) {
            llx = Float.parseFloat(value + "f");
        }
        value = attributes.getProperty("lly");
        if (value != null) {
            lly = Float.parseFloat(value + "f");
        }
        value = attributes.getProperty("urx");
        if (value != null) {
            urx = Float.parseFloat(value + "f");
        }
        value = attributes.getProperty("ury");
        if (value != null) {
            ury = Float.parseFloat(value + "f");
        }
        final String title = attributes.getProperty("title");
        final String text = attributes.getProperty("content");
        if (title != null || text != null) {
            return new Annotation(title, text, llx, lly, urx, ury);
        }
        value = attributes.getProperty("url");
        if (value != null) {
            return new Annotation(llx, lly, urx, ury, value);
        }
        value = attributes.getProperty("named");
        if (value != null) {
            return new Annotation(llx, lly, urx, ury, Integer.parseInt(value));
        }
        final String file = attributes.getProperty("file");
        final String destination = attributes.getProperty("destination");
        final String page = ((Hashtable<K, String>)attributes).remove("page");
        if (file != null) {
            if (destination != null) {
                return new Annotation(llx, lly, urx, ury, file, destination);
            }
            if (page != null) {
                return new Annotation(llx, lly, urx, ury, file, Integer.parseInt(page));
            }
        }
        return new Annotation("", "", llx, lly, urx, ury);
    }
}
