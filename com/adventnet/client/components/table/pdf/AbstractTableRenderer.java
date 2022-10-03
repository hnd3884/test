package com.adventnet.client.components.table.pdf;

import java.util.Map;
import com.lowagie.text.pdf.PdfPCell;
import com.adventnet.persistence.Row;
import com.adventnet.i18n.I18N;
import com.lowagie.text.Chunk;
import com.lowagie.text.Image;
import com.adventnet.client.util.pdf.PDFUtil;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.adventnet.client.components.table.web.TableViewModel;
import com.lowagie.text.pdf.PdfPTable;
import com.adventnet.client.components.table.web.TableTransformerContext;
import com.adventnet.client.components.table.web.TableIterator;
import java.util.logging.Logger;
import com.adventnet.client.view.pdf.PDFView;

public abstract class AbstractTableRenderer extends PDFView
{
    static final Logger LOGGER;
    protected TableIterator globalTableIterator;
    protected TableTransformerContext transContext;
    
    public AbstractTableRenderer() {
        this.globalTableIterator = null;
        this.transContext = null;
    }
    
    protected PdfPTable generateIfNullTable(PdfPTable pTab, final TableViewModel vm) {
        if (pTab.size() == pTab.getHeaderRows()) {
            pTab = new PdfPTable(1);
            String noRowMsg = null;
            final Phrase ph = new Phrase();
            this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)pTab.getDefaultCell(), "nodataRow");
            final Object no_row_message_id = vm.getTableViewConfigRow().get("EMPTY_MESSAGE_ID");
            if (no_row_message_id != null) {
                try {
                    final Row empty_message_row = vm.getEmptyTableMessageRow();
                    final String titleText = (String)empty_message_row.get("TITLE_TEXT");
                    final String messageText = (String)empty_message_row.get("MESSAGE_TEXT");
                    final String iconUrl = (String)empty_message_row.get("ICON_URL");
                    if (iconUrl != null) {
                        final Image img = Image.getInstance(PDFUtil.getRealPathOfImage(this.sc, iconUrl, this.vc.getRequest()));
                        ph.add((Object)new Chunk(img, 0.0f, 0.0f));
                    }
                    if (titleText != null) {
                        ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(titleText), "title"));
                    }
                    ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(messageText), "empty_message"));
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                noRowMsg = (String)vm.getTableViewConfigRow().get("EMPTY_TABLE_MESSAGE");
                if (noRowMsg == null) {
                    noRowMsg = "No Rows found";
                }
                try {
                    noRowMsg = I18N.getMsg(noRowMsg, new Object[0]);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                }
                ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(noRowMsg), "nodataRow"));
            }
            this.addCellToTable(ph, pTab);
            pTab.setWidthPercentage(100.0f);
            pTab.setSpacingBefore(0.0f);
            pTab.setSpacingAfter(0.0f);
        }
        return pTab;
    }
    
    protected PdfPTable generateIfNullTable(final PdfPTable pTab) {
        return this.generateIfNullTable(pTab, null);
    }
    
    void addDummyCell(final PdfPTable datatable) {
        final PdfPCell cell = new PdfPCell(new Phrase(""));
        cell.setBorderWidth(0.0f);
        datatable.addCell(cell);
    }
    
    protected Phrase getCell(final TableTransformerContext transContext, final String tcClass) throws Exception {
        final Map props = transContext.getRenderedAttributes();
        final Phrase ph = new Phrase();
        final String icon = props.get("ICON");
        final String suffixIcon = props.get("SUFFIX_ICON");
        final String replaceIcon = props.get("REPLACE_ICON");
        if (icon != null) {
            this.vc.getRequest().setAttribute("COLUMNNAME", transContext.getColumnConfigRow().get("COLUMNALIAS"));
            final Image img = Image.getInstance(PDFUtil.getRealPathOfImage(this.sc, icon, this.vc.getRequest()));
            final Element el = this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)img, tcClass);
            if (el instanceof Image) {
                ph.add((Object)new Chunk(img, 0.0f, 0.0f));
            }
            else if (el instanceof Phrase) {
                ph.add((Object)el);
            }
        }
        if (props.get("PREFIX_TEXT") != null) {
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk((String)props.get("PREFIX_TEXT")), tcClass));
        }
        if (props.get("ACTUAL_VALUE") != null) {
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(String.valueOf(props.get("ACTUAL_VALUE"))), tcClass));
        }
        else if (props.get("REPLACE_ICON") != null) {
            final Image img = Image.getInstance(PDFUtil.getRealPathOfImage(this.sc, replaceIcon, this.vc.getRequest()));
            ph.add((Object)new Chunk(img, 0.0f, 0.0f));
        }
        else if (props.get("VALUE") != null) {
            this.printValue(props, ph, tcClass);
        }
        if (props.get("SUFFIX_TEXT") != null) {
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk((String)props.get("SUFFIX_TEXT")), tcClass));
        }
        if (suffixIcon != null) {
            final Image img = Image.getInstance(PDFUtil.getRealPathOfImage(this.sc, suffixIcon, this.vc.getRequest()));
            ph.add((Object)new Chunk(img, 0.0f, 0.0f));
        }
        return ph;
    }
    
    protected String[] validateForHtmlImage(final String val) {
        if (val.indexOf("img") != -1 && val.indexOf("<", 0) != -1 && val.indexOf("<", 0) < val.indexOf("img", 0) && val.indexOf("/>") != -1 && val.indexOf("/>") > val.indexOf("img")) {
            String first = "";
            String last = "";
            String icon = val.substring(val.indexOf("src=") + 5, val.indexOf(val.charAt(val.indexOf("src=") + 4), val.indexOf("src=") + 5));
            icon = icon.substring(icon.indexOf("/", 1), icon.length());
            if (val.indexOf("<") > 0) {
                first = val.substring(0, val.indexOf("<"));
                last = val.substring(val.indexOf("/>") + 2, val.length());
            }
            return new String[] { first, icon, last };
        }
        return new String[] { val };
    }
    
    protected void printValue(final Map props, final Phrase ph, final String tcClass) throws Exception {
        this.printAsText(props, ph, tcClass);
    }
    
    protected void printAsText(final Map props, final Phrase ph, final String tcClass) throws Exception {
        ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(String.valueOf(props.get("VALUE"))), tcClass));
    }
    
    protected void printAfterImageCheck(final Map props, final Phrase ph, final String tcClass) throws Exception {
        final String[] val = this.validateForHtmlImage(props.get("VALUE"));
        if (val.length == 1) {
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(String.valueOf(props.get("VALUE"))), tcClass));
        }
        else {
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(val[0]), tcClass));
            final Image img = Image.getInstance(PDFUtil.getRealPathOfImage(this.sc, val[1], this.vc.getRequest()));
            ph.add((Object)new Chunk(img, 0.0f, 0.0f));
            ph.add((Object)this.theme.updateThemeAttributes(this.sc, this.vc, this.doc, this.pdfWriter, (Element)new Chunk(val[2]), tcClass));
        }
    }
    
    protected void addCellToTable(final Phrase ph, final PdfPTable dataTable) {
        if (PDFUtil.isRightToLeft(this.pdfWriter)) {
            final PdfPCell cell = new PdfPCell(ph);
            cell.setRunDirection(3);
            dataTable.addCell(cell);
        }
        else {
            dataTable.addCell(ph);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractTableRenderer.class.getName());
    }
}
