package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTotalsRowFunction;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STXstring;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTXmlColumnPr;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableFormula;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumn;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTableColumnImpl extends XmlComplexContentImpl implements CTTableColumn
{
    private static final long serialVersionUID = 1L;
    private static final QName CALCULATEDCOLUMNFORMULA$0;
    private static final QName TOTALSROWFORMULA$2;
    private static final QName XMLCOLUMNPR$4;
    private static final QName EXTLST$6;
    private static final QName ID$8;
    private static final QName UNIQUENAME$10;
    private static final QName NAME$12;
    private static final QName TOTALSROWFUNCTION$14;
    private static final QName TOTALSROWLABEL$16;
    private static final QName QUERYTABLEFIELDID$18;
    private static final QName HEADERROWDXFID$20;
    private static final QName DATADXFID$22;
    private static final QName TOTALSROWDXFID$24;
    private static final QName HEADERROWCELLSTYLE$26;
    private static final QName DATACELLSTYLE$28;
    private static final QName TOTALSROWCELLSTYLE$30;
    
    public CTTableColumnImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTableFormula getCalculatedColumnFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableFormula ctTableFormula = (CTTableFormula)this.get_store().find_element_user(CTTableColumnImpl.CALCULATEDCOLUMNFORMULA$0, 0);
            if (ctTableFormula == null) {
                return null;
            }
            return ctTableFormula;
        }
    }
    
    public boolean isSetCalculatedColumnFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableColumnImpl.CALCULATEDCOLUMNFORMULA$0) != 0;
        }
    }
    
    public void setCalculatedColumnFormula(final CTTableFormula ctTableFormula) {
        this.generatedSetterHelperImpl((XmlObject)ctTableFormula, CTTableColumnImpl.CALCULATEDCOLUMNFORMULA$0, 0, (short)1);
    }
    
    public CTTableFormula addNewCalculatedColumnFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableFormula)this.get_store().add_element_user(CTTableColumnImpl.CALCULATEDCOLUMNFORMULA$0);
        }
    }
    
    public void unsetCalculatedColumnFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableColumnImpl.CALCULATEDCOLUMNFORMULA$0, 0);
        }
    }
    
    public CTTableFormula getTotalsRowFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTableFormula ctTableFormula = (CTTableFormula)this.get_store().find_element_user(CTTableColumnImpl.TOTALSROWFORMULA$2, 0);
            if (ctTableFormula == null) {
                return null;
            }
            return ctTableFormula;
        }
    }
    
    public boolean isSetTotalsRowFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableColumnImpl.TOTALSROWFORMULA$2) != 0;
        }
    }
    
    public void setTotalsRowFormula(final CTTableFormula ctTableFormula) {
        this.generatedSetterHelperImpl((XmlObject)ctTableFormula, CTTableColumnImpl.TOTALSROWFORMULA$2, 0, (short)1);
    }
    
    public CTTableFormula addNewTotalsRowFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTableFormula)this.get_store().add_element_user(CTTableColumnImpl.TOTALSROWFORMULA$2);
        }
    }
    
    public void unsetTotalsRowFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableColumnImpl.TOTALSROWFORMULA$2, 0);
        }
    }
    
    public CTXmlColumnPr getXmlColumnPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTXmlColumnPr ctXmlColumnPr = (CTXmlColumnPr)this.get_store().find_element_user(CTTableColumnImpl.XMLCOLUMNPR$4, 0);
            if (ctXmlColumnPr == null) {
                return null;
            }
            return ctXmlColumnPr;
        }
    }
    
    public boolean isSetXmlColumnPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableColumnImpl.XMLCOLUMNPR$4) != 0;
        }
    }
    
    public void setXmlColumnPr(final CTXmlColumnPr ctXmlColumnPr) {
        this.generatedSetterHelperImpl((XmlObject)ctXmlColumnPr, CTTableColumnImpl.XMLCOLUMNPR$4, 0, (short)1);
    }
    
    public CTXmlColumnPr addNewXmlColumnPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTXmlColumnPr)this.get_store().add_element_user(CTTableColumnImpl.XMLCOLUMNPR$4);
        }
    }
    
    public void unsetXmlColumnPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableColumnImpl.XMLCOLUMNPR$4, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTTableColumnImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTableColumnImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTTableColumnImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTTableColumnImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTableColumnImpl.EXTLST$6, 0);
        }
    }
    
    public long getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.ID$8);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableColumnImpl.ID$8);
        }
    }
    
    public void setId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.ID$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.ID$8);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableColumnImpl.ID$8);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableColumnImpl.ID$8);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public String getUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.UNIQUENAME$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.UNIQUENAME$10);
        }
    }
    
    public boolean isSetUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.UNIQUENAME$10) != null;
        }
    }
    
    public void setUniqueName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.UNIQUENAME$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.UNIQUENAME$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetUniqueName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.UNIQUENAME$10);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableColumnImpl.UNIQUENAME$10);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetUniqueName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.UNIQUENAME$10);
        }
    }
    
    public String getName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.NAME$12);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.NAME$12);
        }
    }
    
    public void setName(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.NAME$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.NAME$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetName(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.NAME$12);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableColumnImpl.NAME$12);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public STTotalsRowFunction.Enum getTotalsRowFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STTotalsRowFunction.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTotalsRowFunction xgetTotalsRowFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTotalsRowFunction stTotalsRowFunction = (STTotalsRowFunction)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            if (stTotalsRowFunction == null) {
                stTotalsRowFunction = (STTotalsRowFunction)this.get_default_attribute_value(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            }
            return stTotalsRowFunction;
        }
    }
    
    public boolean isSetTotalsRowFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWFUNCTION$14) != null;
        }
    }
    
    public void setTotalsRowFunction(final STTotalsRowFunction.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTotalsRowFunction(final STTotalsRowFunction stTotalsRowFunction) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTotalsRowFunction stTotalsRowFunction2 = (STTotalsRowFunction)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            if (stTotalsRowFunction2 == null) {
                stTotalsRowFunction2 = (STTotalsRowFunction)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWFUNCTION$14);
            }
            stTotalsRowFunction2.set((XmlObject)stTotalsRowFunction);
        }
    }
    
    public void unsetTotalsRowFunction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.TOTALSROWFUNCTION$14);
        }
    }
    
    public String getTotalsRowLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWLABEL$16);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetTotalsRowLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWLABEL$16);
        }
    }
    
    public boolean isSetTotalsRowLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWLABEL$16) != null;
        }
    }
    
    public void setTotalsRowLabel(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWLABEL$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWLABEL$16);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTotalsRowLabel(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWLABEL$16);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWLABEL$16);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetTotalsRowLabel() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.TOTALSROWLABEL$16);
        }
    }
    
    public long getQueryTableFieldId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.QUERYTABLEFIELDID$18);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetQueryTableFieldId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableColumnImpl.QUERYTABLEFIELDID$18);
        }
    }
    
    public boolean isSetQueryTableFieldId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.QUERYTABLEFIELDID$18) != null;
        }
    }
    
    public void setQueryTableFieldId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.QUERYTABLEFIELDID$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.QUERYTABLEFIELDID$18);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetQueryTableFieldId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTTableColumnImpl.QUERYTABLEFIELDID$18);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTTableColumnImpl.QUERYTABLEFIELDID$18);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetQueryTableFieldId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.QUERYTABLEFIELDID$18);
        }
    }
    
    public long getHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWDXFID$20);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWDXFID$20);
        }
    }
    
    public boolean isSetHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWDXFID$20) != null;
        }
    }
    
    public void setHeaderRowDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWDXFID$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.HEADERROWDXFID$20);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetHeaderRowDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWDXFID$20);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableColumnImpl.HEADERROWDXFID$20);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetHeaderRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.HEADERROWDXFID$20);
        }
    }
    
    public long getDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.DATADXFID$22);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableColumnImpl.DATADXFID$22);
        }
    }
    
    public boolean isSetDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.DATADXFID$22) != null;
        }
    }
    
    public void setDataDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.DATADXFID$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.DATADXFID$22);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDataDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableColumnImpl.DATADXFID$22);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableColumnImpl.DATADXFID$22);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetDataDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.DATADXFID$22);
        }
    }
    
    public long getTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWDXFID$24);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWDXFID$24);
        }
    }
    
    public boolean isSetTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWDXFID$24) != null;
        }
    }
    
    public void setTotalsRowDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWDXFID$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWDXFID$24);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetTotalsRowDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWDXFID$24);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWDXFID$24);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetTotalsRowDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.TOTALSROWDXFID$24);
        }
    }
    
    public String getHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWCELLSTYLE$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWCELLSTYLE$26);
        }
    }
    
    public boolean isSetHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWCELLSTYLE$26) != null;
        }
    }
    
    public void setHeaderRowCellStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWCELLSTYLE$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.HEADERROWCELLSTYLE$26);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHeaderRowCellStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.HEADERROWCELLSTYLE$26);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableColumnImpl.HEADERROWCELLSTYLE$26);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetHeaderRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.HEADERROWCELLSTYLE$26);
        }
    }
    
    public String getDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.DATACELLSTYLE$28);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.DATACELLSTYLE$28);
        }
    }
    
    public boolean isSetDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.DATACELLSTYLE$28) != null;
        }
    }
    
    public void setDataCellStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.DATACELLSTYLE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.DATACELLSTYLE$28);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetDataCellStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.DATACELLSTYLE$28);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableColumnImpl.DATACELLSTYLE$28);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetDataCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.DATACELLSTYLE$28);
        }
    }
    
    public String getTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWCELLSTYLE$30);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STXstring xgetTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWCELLSTYLE$30);
        }
    }
    
    public boolean isSetTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWCELLSTYLE$30) != null;
        }
    }
    
    public void setTotalsRowCellStyle(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWCELLSTYLE$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWCELLSTYLE$30);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTotalsRowCellStyle(final STXstring stXstring) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STXstring stXstring2 = (STXstring)this.get_store().find_attribute_user(CTTableColumnImpl.TOTALSROWCELLSTYLE$30);
            if (stXstring2 == null) {
                stXstring2 = (STXstring)this.get_store().add_attribute_user(CTTableColumnImpl.TOTALSROWCELLSTYLE$30);
            }
            stXstring2.set((XmlObject)stXstring);
        }
    }
    
    public void unsetTotalsRowCellStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTTableColumnImpl.TOTALSROWCELLSTYLE$30);
        }
    }
    
    static {
        CALCULATEDCOLUMNFORMULA$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "calculatedColumnFormula");
        TOTALSROWFORMULA$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "totalsRowFormula");
        XMLCOLUMNPR$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "xmlColumnPr");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        ID$8 = new QName("", "id");
        UNIQUENAME$10 = new QName("", "uniqueName");
        NAME$12 = new QName("", "name");
        TOTALSROWFUNCTION$14 = new QName("", "totalsRowFunction");
        TOTALSROWLABEL$16 = new QName("", "totalsRowLabel");
        QUERYTABLEFIELDID$18 = new QName("", "queryTableFieldId");
        HEADERROWDXFID$20 = new QName("", "headerRowDxfId");
        DATADXFID$22 = new QName("", "dataDxfId");
        TOTALSROWDXFID$24 = new QName("", "totalsRowDxfId");
        HEADERROWCELLSTYLE$26 = new QName("", "headerRowCellStyle");
        DATACELLSTYLE$28 = new QName("", "dataCellStyle");
        TOTALSROWCELLSTYLE$30 = new QName("", "totalsRowCellStyle");
    }
}
