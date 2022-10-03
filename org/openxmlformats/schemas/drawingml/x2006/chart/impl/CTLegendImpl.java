package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLayout;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegendEntry;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegendPos;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegend;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLegendImpl extends XmlComplexContentImpl implements CTLegend
{
    private static final long serialVersionUID = 1L;
    private static final QName LEGENDPOS$0;
    private static final QName LEGENDENTRY$2;
    private static final QName LAYOUT$4;
    private static final QName OVERLAY$6;
    private static final QName SPPR$8;
    private static final QName TXPR$10;
    private static final QName EXTLST$12;
    
    public CTLegendImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTLegendPos getLegendPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegendPos ctLegendPos = (CTLegendPos)this.get_store().find_element_user(CTLegendImpl.LEGENDPOS$0, 0);
            if (ctLegendPos == null) {
                return null;
            }
            return ctLegendPos;
        }
    }
    
    public boolean isSetLegendPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLegendImpl.LEGENDPOS$0) != 0;
        }
    }
    
    public void setLegendPos(final CTLegendPos ctLegendPos) {
        this.generatedSetterHelperImpl((XmlObject)ctLegendPos, CTLegendImpl.LEGENDPOS$0, 0, (short)1);
    }
    
    public CTLegendPos addNewLegendPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegendPos)this.get_store().add_element_user(CTLegendImpl.LEGENDPOS$0);
        }
    }
    
    public void unsetLegendPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLegendImpl.LEGENDPOS$0, 0);
        }
    }
    
    public List<CTLegendEntry> getLegendEntryList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LegendEntryList extends AbstractList<CTLegendEntry>
            {
                @Override
                public CTLegendEntry get(final int n) {
                    return CTLegendImpl.this.getLegendEntryArray(n);
                }
                
                @Override
                public CTLegendEntry set(final int n, final CTLegendEntry ctLegendEntry) {
                    final CTLegendEntry legendEntryArray = CTLegendImpl.this.getLegendEntryArray(n);
                    CTLegendImpl.this.setLegendEntryArray(n, ctLegendEntry);
                    return legendEntryArray;
                }
                
                @Override
                public void add(final int n, final CTLegendEntry ctLegendEntry) {
                    CTLegendImpl.this.insertNewLegendEntry(n).set((XmlObject)ctLegendEntry);
                }
                
                @Override
                public CTLegendEntry remove(final int n) {
                    final CTLegendEntry legendEntryArray = CTLegendImpl.this.getLegendEntryArray(n);
                    CTLegendImpl.this.removeLegendEntry(n);
                    return legendEntryArray;
                }
                
                @Override
                public int size() {
                    return CTLegendImpl.this.sizeOfLegendEntryArray();
                }
            }
            return new LegendEntryList();
        }
    }
    
    @Deprecated
    public CTLegendEntry[] getLegendEntryArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTLegendImpl.LEGENDENTRY$2, (List)list);
            final CTLegendEntry[] array = new CTLegendEntry[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLegendEntry getLegendEntryArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegendEntry ctLegendEntry = (CTLegendEntry)this.get_store().find_element_user(CTLegendImpl.LEGENDENTRY$2, n);
            if (ctLegendEntry == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLegendEntry;
        }
    }
    
    public int sizeOfLegendEntryArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLegendImpl.LEGENDENTRY$2);
        }
    }
    
    public void setLegendEntryArray(final CTLegendEntry[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLegendImpl.LEGENDENTRY$2);
    }
    
    public void setLegendEntryArray(final int n, final CTLegendEntry ctLegendEntry) {
        this.generatedSetterHelperImpl((XmlObject)ctLegendEntry, CTLegendImpl.LEGENDENTRY$2, n, (short)2);
    }
    
    public CTLegendEntry insertNewLegendEntry(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegendEntry)this.get_store().insert_element_user(CTLegendImpl.LEGENDENTRY$2, n);
        }
    }
    
    public CTLegendEntry addNewLegendEntry() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegendEntry)this.get_store().add_element_user(CTLegendImpl.LEGENDENTRY$2);
        }
    }
    
    public void removeLegendEntry(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLegendImpl.LEGENDENTRY$2, n);
        }
    }
    
    public CTLayout getLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLayout ctLayout = (CTLayout)this.get_store().find_element_user(CTLegendImpl.LAYOUT$4, 0);
            if (ctLayout == null) {
                return null;
            }
            return ctLayout;
        }
    }
    
    public boolean isSetLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLegendImpl.LAYOUT$4) != 0;
        }
    }
    
    public void setLayout(final CTLayout ctLayout) {
        this.generatedSetterHelperImpl((XmlObject)ctLayout, CTLegendImpl.LAYOUT$4, 0, (short)1);
    }
    
    public CTLayout addNewLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLayout)this.get_store().add_element_user(CTLegendImpl.LAYOUT$4);
        }
    }
    
    public void unsetLayout() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLegendImpl.LAYOUT$4, 0);
        }
    }
    
    public CTBoolean getOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTLegendImpl.OVERLAY$6, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLegendImpl.OVERLAY$6) != 0;
        }
    }
    
    public void setOverlay(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTLegendImpl.OVERLAY$6, 0, (short)1);
    }
    
    public CTBoolean addNewOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTLegendImpl.OVERLAY$6);
        }
    }
    
    public void unsetOverlay() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLegendImpl.OVERLAY$6, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTLegendImpl.SPPR$8, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLegendImpl.SPPR$8) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTLegendImpl.SPPR$8, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTLegendImpl.SPPR$8);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLegendImpl.SPPR$8, 0);
        }
    }
    
    public CTTextBody getTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTLegendImpl.TXPR$10, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLegendImpl.TXPR$10) != 0;
        }
    }
    
    public void setTxPr(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTLegendImpl.TXPR$10, 0, (short)1);
    }
    
    public CTTextBody addNewTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTLegendImpl.TXPR$10);
        }
    }
    
    public void unsetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLegendImpl.TXPR$10, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTLegendImpl.EXTLST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLegendImpl.EXTLST$12) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTLegendImpl.EXTLST$12, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTLegendImpl.EXTLST$12);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLegendImpl.EXTLST$12, 0);
        }
    }
    
    static {
        LEGENDPOS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "legendPos");
        LEGENDENTRY$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "legendEntry");
        LAYOUT$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "layout");
        OVERLAY$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "overlay");
        SPPR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        TXPR$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "txPr");
        EXTLST$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
