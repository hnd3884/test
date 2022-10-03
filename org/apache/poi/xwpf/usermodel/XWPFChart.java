package org.apache.poi.xwpf.usermodel;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTInline;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;

public class XWPFChart extends XDDFChart
{
    public static final int DEFAULT_WIDTH = 500000;
    public static final int DEFAULT_HEIGHT = 500000;
    private Long checksum;
    private CTInline ctInline;
    
    protected XWPFChart() {
    }
    
    protected XWPFChart(final PackagePart part) throws IOException, XmlException {
        super(part);
    }
    
    @Override
    protected POIXMLRelation getChartRelation() {
        return XWPFRelation.CHART;
    }
    
    @Override
    protected POIXMLRelation getChartWorkbookRelation() {
        return XWPFRelation.WORKBOOK;
    }
    
    @Override
    protected POIXMLFactory getChartFactory() {
        return XWPFFactory.getInstance();
    }
    
    public Long getChecksum() {
        if (this.checksum == null) {
            byte[] data;
            try (final InputStream is = this.getPackagePart().getInputStream()) {
                data = IOUtils.toByteArray(is);
            }
            catch (final IOException e) {
                throw new POIXMLException(e);
            }
            this.checksum = IOUtils.calculateChecksum(data);
        }
        return this.checksum;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj != null && !(obj instanceof XWPFChart) && false);
    }
    
    @Override
    public int hashCode() {
        return this.getChecksum().hashCode();
    }
    
    protected void attach(final String chartRelId, final XWPFRun run) throws InvalidFormatException, IOException {
        (this.ctInline = run.addChart(chartRelId)).addNewExtent();
        this.setChartBoundingBox(500000L, 500000L);
    }
    
    public void setChartHeight(final long height) {
        this.ctInline.getExtent().setCy(height);
    }
    
    public void setChartWidth(final long width) {
        this.ctInline.getExtent().setCx(width);
    }
    
    public long getChartHeight() {
        return this.ctInline.getExtent().getCy();
    }
    
    public long getChartWidth() {
        return this.ctInline.getExtent().getCx();
    }
    
    public void setChartBoundingBox(final long width, final long height) {
        this.setChartWidth(width);
        this.setChartHeight(height);
    }
    
    public void setChartTopMargin(final long margin) {
        this.ctInline.setDistT(margin);
    }
    
    public long getChartTopMargin(final long margin) {
        return this.ctInline.getDistT();
    }
    
    public void setChartBottomMargin(final long margin) {
        this.ctInline.setDistB(margin);
    }
    
    public long getChartBottomMargin(final long margin) {
        return this.ctInline.getDistB();
    }
    
    public void setChartLeftMargin(final long margin) {
        this.ctInline.setDistL(margin);
    }
    
    public long getChartLeftMargin(final long margin) {
        return this.ctInline.getDistL();
    }
    
    public void setChartRightMargin(final long margin) {
        this.ctInline.setDistR(margin);
    }
    
    public long getChartRightMargin(final long margin) {
        return this.ctInline.getDistR();
    }
    
    public void setChartMargin(final long top, final long right, final long bottom, final long left) {
        this.setChartBottomMargin(bottom);
        this.setChartRightMargin(right);
        this.setChartLeftMargin(left);
        this.setChartRightMargin(right);
    }
}
