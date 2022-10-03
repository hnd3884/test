package com.adventnet.client.view.common;

import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.Phrase;
import com.lowagie.text.FontFactory;
import java.awt.Color;
import com.lowagie.text.Chunk;
import com.adventnet.client.view.web.ViewContext;
import com.lowagie.text.Element;
import com.adventnet.client.util.pdf.PDFUtil;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.adventnet.client.view.web.HttpReqWrapper;
import javax.servlet.http.HttpServletRequest;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import javax.servlet.ServletContext;
import java.util.HashMap;

public class DefaultMultiViewPDFRenderer implements MultiViewPDFRenderer
{
    HashMap origParamMap;
    public boolean renderTitle;
    
    public DefaultMultiViewPDFRenderer() {
        this.origParamMap = new HashMap();
        this.renderTitle = true;
    }
    
    @Override
    public void generatePDF(final String[] viewNames, final ServletContext sc, final PdfWriter writer, final Document doc, final HttpServletRequest request) throws Exception {
        this.initPDF(viewNames, sc, writer, doc, request);
        if (request instanceof HttpReqWrapper) {
            this.stripAllDollarParameterNames((HttpReqWrapper)request);
        }
        for (int i = 0; i < viewNames.length; ++i) {
            if (request instanceof HttpReqWrapper) {
                this.addViewSpecificParams((HttpReqWrapper)request, viewNames[i]);
            }
            this.renderTheViewInPDF(viewNames[i], request, doc, sc, writer);
            if (request instanceof HttpReqWrapper) {
                this.removeViewSpecificParams((HttpReqWrapper)request, viewNames[i]);
            }
        }
        this.endPDF(viewNames, sc, writer, doc, request);
    }
    
    public void initPDF(final String[] viewNames, final ServletContext sc, final PdfWriter writer, final Document doc, final HttpServletRequest request) throws Exception {
    }
    
    public void endPDF(final String[] viewNames, final ServletContext sc, final PdfWriter writer, final Document doc, final HttpServletRequest request) throws Exception {
    }
    
    public void stripAllDollarParameterNames(final HttpReqWrapper wreq) {
        final HashMap paramMap = (HashMap)wreq.getParameterMap();
        final List keysToBeRemoved = new ArrayList();
        for (final Object key : paramMap.keySet()) {
            final int index = key.toString().indexOf("::");
            if (index != -1) {
                this.origParamMap.put(key, paramMap.get(key));
                keysToBeRemoved.add(key);
            }
        }
        for (final Object key : keysToBeRemoved) {
            paramMap.remove(key);
        }
    }
    
    public void addViewSpecificParams(final HttpReqWrapper wreq, final String viewname) {
        for (final Object key : this.origParamMap.keySet()) {
            final int index = key.toString().indexOf("::");
            if (key.toString().indexOf(viewname) != -1 && index != -1) {
                wreq.getParameterMap().put(key.toString().substring(index + 2), this.origParamMap.get(key));
            }
        }
    }
    
    public void removeViewSpecificParams(final HttpReqWrapper wreq, final String viewname) {
        for (final Object key : this.origParamMap.keySet()) {
            final int index = key.toString().indexOf("::");
            if (key.toString().indexOf(viewname) != -1 && index != -1) {
                wreq.getParameterMap().remove(key.toString().substring(index + 2));
            }
        }
    }
    
    public void renderTheViewInPDF(final String viewname, final HttpServletRequest request, final Document doc, final ServletContext sc, final PdfWriter writer) throws Exception {
        if (this.renderTitle) {
            this.renderTitle(viewname, request, doc, sc, writer);
        }
        final ViewContext vc = PDFUtil.getViewCtx(request, viewname);
        PDFUtil.initializeRenderingPhase(vc, request);
        final Object ob = PDFUtil.includeView(sc, vc, doc, doc, writer, PDFUtil.getThemeClass(request), "generalBox");
        if (ob != null && ob instanceof Element) {
            doc.add((Element)ob);
        }
        PDFUtil.endRenderingPhase(vc, request);
        doc.newPage();
    }
    
    public void setRenderTitle(final boolean bool) {
        this.renderTitle = bool;
    }
    
    public void renderTitle(final String viewname, final HttpServletRequest request, final Document doc, final ServletContext sc, final PdfWriter writer) throws Exception {
        final String title = ViewContext.getViewContext(viewname, request).getTitle();
        if (title == null) {
            return;
        }
        final Chunk chunk = new Chunk(title);
        chunk.setFont(FontFactory.getFont("Helvetica", 10.0f, 1, new Color(0, 0, 0)));
        final Phrase phrase = new Phrase(chunk);
        final PdfPCell fromToCell = new PdfPCell(phrase);
        fromToCell.setHorizontalAlignment(2);
        fromToCell.setBorder(0);
        fromToCell.setNoWrap(true);
        final PdfPTable table = new PdfPTable(1);
        table.addCell(fromToCell);
        doc.add((Element)table);
    }
}
