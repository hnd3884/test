package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTRelId;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageSetup;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageMargins;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTHeaderFooter;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPrintSettings;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPrintSettingsImpl extends XmlComplexContentImpl implements CTPrintSettings
{
    private static final long serialVersionUID = 1L;
    private static final QName HEADERFOOTER$0;
    private static final QName PAGEMARGINS$2;
    private static final QName PAGESETUP$4;
    private static final QName LEGACYDRAWINGHF$6;
    
    public CTPrintSettingsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTHeaderFooter getHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeaderFooter ctHeaderFooter = (CTHeaderFooter)this.get_store().find_element_user(CTPrintSettingsImpl.HEADERFOOTER$0, 0);
            if (ctHeaderFooter == null) {
                return null;
            }
            return ctHeaderFooter;
        }
    }
    
    public boolean isSetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPrintSettingsImpl.HEADERFOOTER$0) != 0;
        }
    }
    
    public void setHeaderFooter(final CTHeaderFooter ctHeaderFooter) {
        this.generatedSetterHelperImpl((XmlObject)ctHeaderFooter, CTPrintSettingsImpl.HEADERFOOTER$0, 0, (short)1);
    }
    
    public CTHeaderFooter addNewHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeaderFooter)this.get_store().add_element_user(CTPrintSettingsImpl.HEADERFOOTER$0);
        }
    }
    
    public void unsetHeaderFooter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPrintSettingsImpl.HEADERFOOTER$0, 0);
        }
    }
    
    public CTPageMargins getPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageMargins ctPageMargins = (CTPageMargins)this.get_store().find_element_user(CTPrintSettingsImpl.PAGEMARGINS$2, 0);
            if (ctPageMargins == null) {
                return null;
            }
            return ctPageMargins;
        }
    }
    
    public boolean isSetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPrintSettingsImpl.PAGEMARGINS$2) != 0;
        }
    }
    
    public void setPageMargins(final CTPageMargins ctPageMargins) {
        this.generatedSetterHelperImpl((XmlObject)ctPageMargins, CTPrintSettingsImpl.PAGEMARGINS$2, 0, (short)1);
    }
    
    public CTPageMargins addNewPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageMargins)this.get_store().add_element_user(CTPrintSettingsImpl.PAGEMARGINS$2);
        }
    }
    
    public void unsetPageMargins() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPrintSettingsImpl.PAGEMARGINS$2, 0);
        }
    }
    
    public CTPageSetup getPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPageSetup ctPageSetup = (CTPageSetup)this.get_store().find_element_user(CTPrintSettingsImpl.PAGESETUP$4, 0);
            if (ctPageSetup == null) {
                return null;
            }
            return ctPageSetup;
        }
    }
    
    public boolean isSetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPrintSettingsImpl.PAGESETUP$4) != 0;
        }
    }
    
    public void setPageSetup(final CTPageSetup ctPageSetup) {
        this.generatedSetterHelperImpl((XmlObject)ctPageSetup, CTPrintSettingsImpl.PAGESETUP$4, 0, (short)1);
    }
    
    public CTPageSetup addNewPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPageSetup)this.get_store().add_element_user(CTPrintSettingsImpl.PAGESETUP$4);
        }
    }
    
    public void unsetPageSetup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPrintSettingsImpl.PAGESETUP$4, 0);
        }
    }
    
    public CTRelId getLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRelId ctRelId = (CTRelId)this.get_store().find_element_user(CTPrintSettingsImpl.LEGACYDRAWINGHF$6, 0);
            if (ctRelId == null) {
                return null;
            }
            return ctRelId;
        }
    }
    
    public boolean isSetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPrintSettingsImpl.LEGACYDRAWINGHF$6) != 0;
        }
    }
    
    public void setLegacyDrawingHF(final CTRelId ctRelId) {
        this.generatedSetterHelperImpl((XmlObject)ctRelId, CTPrintSettingsImpl.LEGACYDRAWINGHF$6, 0, (short)1);
    }
    
    public CTRelId addNewLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelId)this.get_store().add_element_user(CTPrintSettingsImpl.LEGACYDRAWINGHF$6);
        }
    }
    
    public void unsetLegacyDrawingHF() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPrintSettingsImpl.LEGACYDRAWINGHF$6, 0);
        }
    }
    
    static {
        HEADERFOOTER$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "headerFooter");
        PAGEMARGINS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pageMargins");
        PAGESETUP$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pageSetup");
        LEGACYDRAWINGHF$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "legacyDrawingHF");
    }
}
