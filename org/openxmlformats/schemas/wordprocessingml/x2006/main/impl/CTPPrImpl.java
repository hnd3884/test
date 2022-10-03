package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPrChange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;

public class CTPPrImpl extends CTPPrBaseImpl implements CTPPr
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    private static final QName SECTPR$2;
    private static final QName PPRCHANGE$4;
    
    public CTPPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    @Override
    public CTParaRPr getRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTParaRPr ctParaRPr = (CTParaRPr)this.get_store().find_element_user(CTPPrImpl.RPR$0, 0);
            if (ctParaRPr == null) {
                return null;
            }
            return ctParaRPr;
        }
    }
    
    @Override
    public boolean isSetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrImpl.RPR$0) != 0;
        }
    }
    
    @Override
    public void setRPr(final CTParaRPr ctParaRPr) {
        this.generatedSetterHelperImpl((XmlObject)ctParaRPr, CTPPrImpl.RPR$0, 0, (short)1);
    }
    
    @Override
    public CTParaRPr addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTParaRPr)this.get_store().add_element_user(CTPPrImpl.RPR$0);
        }
    }
    
    @Override
    public void unsetRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrImpl.RPR$0, 0);
        }
    }
    
    @Override
    public CTSectPr getSectPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSectPr ctSectPr = (CTSectPr)this.get_store().find_element_user(CTPPrImpl.SECTPR$2, 0);
            if (ctSectPr == null) {
                return null;
            }
            return ctSectPr;
        }
    }
    
    @Override
    public boolean isSetSectPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrImpl.SECTPR$2) != 0;
        }
    }
    
    @Override
    public void setSectPr(final CTSectPr ctSectPr) {
        this.generatedSetterHelperImpl((XmlObject)ctSectPr, CTPPrImpl.SECTPR$2, 0, (short)1);
    }
    
    @Override
    public CTSectPr addNewSectPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSectPr)this.get_store().add_element_user(CTPPrImpl.SECTPR$2);
        }
    }
    
    @Override
    public void unsetSectPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrImpl.SECTPR$2, 0);
        }
    }
    
    @Override
    public CTPPrChange getPPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPPrChange ctpPrChange = (CTPPrChange)this.get_store().find_element_user(CTPPrImpl.PPRCHANGE$4, 0);
            if (ctpPrChange == null) {
                return null;
            }
            return ctpPrChange;
        }
    }
    
    @Override
    public boolean isSetPPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPPrImpl.PPRCHANGE$4) != 0;
        }
    }
    
    @Override
    public void setPPrChange(final CTPPrChange ctpPrChange) {
        this.generatedSetterHelperImpl((XmlObject)ctpPrChange, CTPPrImpl.PPRCHANGE$4, 0, (short)1);
    }
    
    @Override
    public CTPPrChange addNewPPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPPrChange)this.get_store().add_element_user(CTPPrImpl.PPRCHANGE$4);
        }
    }
    
    @Override
    public void unsetPPrChange() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPPrImpl.PPRCHANGE$4, 0);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr");
        SECTPR$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "sectPr");
        PPRCHANGE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "pPrChange");
    }
}
