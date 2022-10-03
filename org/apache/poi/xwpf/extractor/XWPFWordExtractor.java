package org.apache.poi.xwpf.extractor;

import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFSDTCell;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.apache.poi.xwpf.model.XWPFParagraphDecorator;
import org.apache.poi.xwpf.model.XWPFCommentsDecorator;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.util.Iterator;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.ooxml.POIXMLDocument;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;

public class XWPFWordExtractor extends POIXMLTextExtractor
{
    public static final XWPFRelation[] SUPPORTED_TYPES;
    private XWPFDocument document;
    private boolean fetchHyperlinks;
    private boolean concatenatePhoneticRuns;
    
    public XWPFWordExtractor(final OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        this(new XWPFDocument(container));
    }
    
    public XWPFWordExtractor(final XWPFDocument document) {
        super(document);
        this.concatenatePhoneticRuns = true;
        this.document = document;
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XWPFWordExtractor <filename.docx>");
            System.exit(1);
        }
        final POIXMLTextExtractor extractor = new XWPFWordExtractor(POIXMLDocument.openPackage(args[0]));
        System.out.println(extractor.getText());
        extractor.close();
    }
    
    public void setFetchHyperlinks(final boolean fetch) {
        this.fetchHyperlinks = fetch;
    }
    
    public void setConcatenatePhoneticRuns(final boolean concatenatePhoneticRuns) {
        this.concatenatePhoneticRuns = concatenatePhoneticRuns;
    }
    
    public String getText() {
        final StringBuilder text = new StringBuilder(64);
        final XWPFHeaderFooterPolicy hfPolicy = this.document.getHeaderFooterPolicy();
        this.extractHeaders(text, hfPolicy);
        for (final IBodyElement e : this.document.getBodyElements()) {
            this.appendBodyElementText(text, e);
            text.append('\n');
        }
        this.extractFooters(text, hfPolicy);
        return text.toString();
    }
    
    public void appendBodyElementText(final StringBuilder text, final IBodyElement e) {
        if (e instanceof XWPFParagraph) {
            this.appendParagraphText(text, (XWPFParagraph)e);
        }
        else if (e instanceof XWPFTable) {
            this.appendTableText(text, (XWPFTable)e);
        }
        else if (e instanceof XWPFSDT) {
            text.append(((XWPFSDT)e).getContent().getText());
        }
    }
    
    public void appendParagraphText(final StringBuilder text, final XWPFParagraph paragraph) {
        CTSectPr ctSectPr = null;
        if (paragraph.getCTP().getPPr() != null) {
            ctSectPr = paragraph.getCTP().getPPr().getSectPr();
        }
        XWPFHeaderFooterPolicy headerFooterPolicy = null;
        if (ctSectPr != null) {
            headerFooterPolicy = new XWPFHeaderFooterPolicy(this.document, ctSectPr);
            this.extractHeaders(text, headerFooterPolicy);
        }
        for (final IRunElement run : paragraph.getRuns()) {
            if (!this.concatenatePhoneticRuns && run instanceof XWPFRun) {
                text.append(((XWPFRun)run).text());
            }
            else {
                text.append(run);
            }
            if (run instanceof XWPFHyperlinkRun && this.fetchHyperlinks) {
                final XWPFHyperlink link = ((XWPFHyperlinkRun)run).getHyperlink(this.document);
                if (link == null) {
                    continue;
                }
                text.append(" <").append(link.getURL()).append(">");
            }
        }
        final XWPFCommentsDecorator decorator = new XWPFCommentsDecorator(paragraph, null);
        final String commentText = decorator.getCommentText();
        if (commentText.length() > 0) {
            text.append(commentText).append('\n');
        }
        final String footnameText = paragraph.getFootnoteText();
        if (footnameText != null && footnameText.length() > 0) {
            text.append(footnameText).append('\n');
        }
        if (ctSectPr != null) {
            this.extractFooters(text, headerFooterPolicy);
        }
    }
    
    private void appendTableText(final StringBuilder text, final XWPFTable table) {
        for (final XWPFTableRow row : table.getRows()) {
            final List<ICell> cells = row.getTableICells();
            for (int i = 0; i < cells.size(); ++i) {
                final ICell cell = cells.get(i);
                if (cell instanceof XWPFTableCell) {
                    text.append(((XWPFTableCell)cell).getTextRecursively());
                }
                else if (cell instanceof XWPFSDTCell) {
                    text.append(((XWPFSDTCell)cell).getContent().getText());
                }
                if (i < cells.size() - 1) {
                    text.append("\t");
                }
            }
            text.append('\n');
        }
    }
    
    private void extractFooters(final StringBuilder text, final XWPFHeaderFooterPolicy hfPolicy) {
        if (hfPolicy == null) {
            return;
        }
        if (hfPolicy.getFirstPageFooter() != null) {
            text.append(hfPolicy.getFirstPageFooter().getText());
        }
        if (hfPolicy.getEvenPageFooter() != null) {
            text.append(hfPolicy.getEvenPageFooter().getText());
        }
        if (hfPolicy.getDefaultFooter() != null) {
            text.append(hfPolicy.getDefaultFooter().getText());
        }
    }
    
    private void extractHeaders(final StringBuilder text, final XWPFHeaderFooterPolicy hfPolicy) {
        if (hfPolicy == null) {
            return;
        }
        if (hfPolicy.getFirstPageHeader() != null) {
            text.append(hfPolicy.getFirstPageHeader().getText());
        }
        if (hfPolicy.getEvenPageHeader() != null) {
            text.append(hfPolicy.getEvenPageHeader().getText());
        }
        if (hfPolicy.getDefaultHeader() != null) {
            text.append(hfPolicy.getDefaultHeader().getText());
        }
    }
    
    static {
        SUPPORTED_TYPES = new XWPFRelation[] { XWPFRelation.DOCUMENT, XWPFRelation.TEMPLATE, XWPFRelation.MACRO_DOCUMENT, XWPFRelation.MACRO_TEMPLATE_DOCUMENT };
    }
}
