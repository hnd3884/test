package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTimePeriod;
import org.apache.xmlbeans.XmlString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STConditionalFormattingOperator;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCfType;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIconSet;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBar;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColorScale;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFormula;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import java.util.AbstractList;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCfRule;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTCfRuleImpl extends XmlComplexContentImpl implements CTCfRule
{
    private static final long serialVersionUID = 1L;
    private static final QName FORMULA$0;
    private static final QName COLORSCALE$2;
    private static final QName DATABAR$4;
    private static final QName ICONSET$6;
    private static final QName EXTLST$8;
    private static final QName TYPE$10;
    private static final QName DXFID$12;
    private static final QName PRIORITY$14;
    private static final QName STOPIFTRUE$16;
    private static final QName ABOVEAVERAGE$18;
    private static final QName PERCENT$20;
    private static final QName BOTTOM$22;
    private static final QName OPERATOR$24;
    private static final QName TEXT$26;
    private static final QName TIMEPERIOD$28;
    private static final QName RANK$30;
    private static final QName STDDEV$32;
    private static final QName EQUALAVERAGE$34;
    
    public CTCfRuleImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<String> getFormulaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FormulaList extends AbstractList<String>
            {
                @Override
                public String get(final int n) {
                    return CTCfRuleImpl.this.getFormulaArray(n);
                }
                
                @Override
                public String set(final int n, final String s) {
                    final String formulaArray = CTCfRuleImpl.this.getFormulaArray(n);
                    CTCfRuleImpl.this.setFormulaArray(n, s);
                    return formulaArray;
                }
                
                @Override
                public void add(final int n, final String s) {
                    CTCfRuleImpl.this.insertFormula(n, s);
                }
                
                @Override
                public String remove(final int n) {
                    final String formulaArray = CTCfRuleImpl.this.getFormulaArray(n);
                    CTCfRuleImpl.this.removeFormula(n);
                    return formulaArray;
                }
                
                @Override
                public int size() {
                    return CTCfRuleImpl.this.sizeOfFormulaArray();
                }
            }
            return new FormulaList();
        }
    }
    
    @Deprecated
    public String[] getFormulaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCfRuleImpl.FORMULA$0, (List)list);
            final String[] array = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                array[i] = ((SimpleValue)list.get(i)).getStringValue();
            }
            return array;
        }
    }
    
    public String getFormulaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTCfRuleImpl.FORMULA$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            return simpleValue.getStringValue();
        }
    }
    
    public List<STFormula> xgetFormulaList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FormulaList extends AbstractList<STFormula>
            {
                @Override
                public STFormula get(final int n) {
                    return CTCfRuleImpl.this.xgetFormulaArray(n);
                }
                
                @Override
                public STFormula set(final int n, final STFormula stFormula) {
                    final STFormula xgetFormulaArray = CTCfRuleImpl.this.xgetFormulaArray(n);
                    CTCfRuleImpl.this.xsetFormulaArray(n, stFormula);
                    return xgetFormulaArray;
                }
                
                @Override
                public void add(final int n, final STFormula stFormula) {
                    CTCfRuleImpl.this.insertNewFormula(n).set((XmlObject)stFormula);
                }
                
                @Override
                public STFormula remove(final int n) {
                    final STFormula xgetFormulaArray = CTCfRuleImpl.this.xgetFormulaArray(n);
                    CTCfRuleImpl.this.removeFormula(n);
                    return xgetFormulaArray;
                }
                
                @Override
                public int size() {
                    return CTCfRuleImpl.this.sizeOfFormulaArray();
                }
            }
            return new FormulaList();
        }
    }
    
    @Deprecated
    public STFormula[] xgetFormulaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTCfRuleImpl.FORMULA$0, (List)list);
            final STFormula[] array = new STFormula[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public STFormula xgetFormulaArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STFormula stFormula = (STFormula)this.get_store().find_element_user(CTCfRuleImpl.FORMULA$0, n);
            if (stFormula == null) {
                throw new IndexOutOfBoundsException();
            }
            return stFormula;
        }
    }
    
    public int sizeOfFormulaArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCfRuleImpl.FORMULA$0);
        }
    }
    
    public void setFormulaArray(final String[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper(array, CTCfRuleImpl.FORMULA$0);
        }
    }
    
    public void setFormulaArray(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTCfRuleImpl.FORMULA$0, n);
            if (simpleValue == null) {
                throw new IndexOutOfBoundsException();
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetFormulaArray(final STFormula[] array) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, CTCfRuleImpl.FORMULA$0);
        }
    }
    
    public void xsetFormulaArray(final int n, final STFormula stFormula) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final STFormula stFormula2 = (STFormula)this.get_store().find_element_user(CTCfRuleImpl.FORMULA$0, n);
            if (stFormula2 == null) {
                throw new IndexOutOfBoundsException();
            }
            stFormula2.set((XmlObject)stFormula);
        }
    }
    
    public void insertFormula(final int n, final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().insert_element_user(CTCfRuleImpl.FORMULA$0, n)).setStringValue(stringValue);
        }
    }
    
    public void addFormula(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ((SimpleValue)this.get_store().add_element_user(CTCfRuleImpl.FORMULA$0)).setStringValue(stringValue);
        }
    }
    
    public STFormula insertNewFormula(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFormula)this.get_store().insert_element_user(CTCfRuleImpl.FORMULA$0, n);
        }
    }
    
    public STFormula addNewFormula() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STFormula)this.get_store().add_element_user(CTCfRuleImpl.FORMULA$0);
        }
    }
    
    public void removeFormula(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCfRuleImpl.FORMULA$0, n);
        }
    }
    
    public CTColorScale getColorScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorScale ctColorScale = (CTColorScale)this.get_store().find_element_user(CTCfRuleImpl.COLORSCALE$2, 0);
            if (ctColorScale == null) {
                return null;
            }
            return ctColorScale;
        }
    }
    
    public boolean isSetColorScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCfRuleImpl.COLORSCALE$2) != 0;
        }
    }
    
    public void setColorScale(final CTColorScale ctColorScale) {
        this.generatedSetterHelperImpl((XmlObject)ctColorScale, CTCfRuleImpl.COLORSCALE$2, 0, (short)1);
    }
    
    public CTColorScale addNewColorScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorScale)this.get_store().add_element_user(CTCfRuleImpl.COLORSCALE$2);
        }
    }
    
    public void unsetColorScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCfRuleImpl.COLORSCALE$2, 0);
        }
    }
    
    public CTDataBar getDataBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataBar ctDataBar = (CTDataBar)this.get_store().find_element_user(CTCfRuleImpl.DATABAR$4, 0);
            if (ctDataBar == null) {
                return null;
            }
            return ctDataBar;
        }
    }
    
    public boolean isSetDataBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCfRuleImpl.DATABAR$4) != 0;
        }
    }
    
    public void setDataBar(final CTDataBar ctDataBar) {
        this.generatedSetterHelperImpl((XmlObject)ctDataBar, CTCfRuleImpl.DATABAR$4, 0, (short)1);
    }
    
    public CTDataBar addNewDataBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataBar)this.get_store().add_element_user(CTCfRuleImpl.DATABAR$4);
        }
    }
    
    public void unsetDataBar() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCfRuleImpl.DATABAR$4, 0);
        }
    }
    
    public CTIconSet getIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIconSet set = (CTIconSet)this.get_store().find_element_user(CTCfRuleImpl.ICONSET$6, 0);
            if (set == null) {
                return null;
            }
            return set;
        }
    }
    
    public boolean isSetIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCfRuleImpl.ICONSET$6) != 0;
        }
    }
    
    public void setIconSet(final CTIconSet set) {
        this.generatedSetterHelperImpl((XmlObject)set, CTCfRuleImpl.ICONSET$6, 0, (short)1);
    }
    
    public CTIconSet addNewIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIconSet)this.get_store().add_element_user(CTCfRuleImpl.ICONSET$6);
        }
    }
    
    public void unsetIconSet() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCfRuleImpl.ICONSET$6, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTCfRuleImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTCfRuleImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTCfRuleImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTCfRuleImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTCfRuleImpl.EXTLST$8, 0);
        }
    }
    
    public STCfType.Enum getType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.TYPE$10);
            if (simpleValue == null) {
                return null;
            }
            return (STCfType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STCfType xgetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCfType)this.get_store().find_attribute_user(CTCfRuleImpl.TYPE$10);
        }
    }
    
    public boolean isSetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.TYPE$10) != null;
        }
    }
    
    public void setType(final STCfType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.TYPE$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.TYPE$10);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetType(final STCfType stCfType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCfType stCfType2 = (STCfType)this.get_store().find_attribute_user(CTCfRuleImpl.TYPE$10);
            if (stCfType2 == null) {
                stCfType2 = (STCfType)this.get_store().add_attribute_user(CTCfRuleImpl.TYPE$10);
            }
            stCfType2.set((XmlObject)stCfType);
        }
    }
    
    public void unsetType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.TYPE$10);
        }
    }
    
    public long getDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.DXFID$12);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public STDxfId xgetDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STDxfId)this.get_store().find_attribute_user(CTCfRuleImpl.DXFID$12);
        }
    }
    
    public boolean isSetDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.DXFID$12) != null;
        }
    }
    
    public void setDxfId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.DXFID$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.DXFID$12);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetDxfId(final STDxfId stDxfId) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STDxfId stDxfId2 = (STDxfId)this.get_store().find_attribute_user(CTCfRuleImpl.DXFID$12);
            if (stDxfId2 == null) {
                stDxfId2 = (STDxfId)this.get_store().add_attribute_user(CTCfRuleImpl.DXFID$12);
            }
            stDxfId2.set((XmlObject)stDxfId);
        }
    }
    
    public void unsetDxfId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.DXFID$12);
        }
    }
    
    public int getPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.PRIORITY$14);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetPriority() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(CTCfRuleImpl.PRIORITY$14);
        }
    }
    
    public void setPriority(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.PRIORITY$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.PRIORITY$14);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetPriority(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTCfRuleImpl.PRIORITY$14);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTCfRuleImpl.PRIORITY$14);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public boolean getStopIfTrue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.STOPIFTRUE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCfRuleImpl.STOPIFTRUE$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetStopIfTrue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.STOPIFTRUE$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCfRuleImpl.STOPIFTRUE$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetStopIfTrue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.STOPIFTRUE$16) != null;
        }
    }
    
    public void setStopIfTrue(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.STOPIFTRUE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.STOPIFTRUE$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetStopIfTrue(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.STOPIFTRUE$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCfRuleImpl.STOPIFTRUE$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetStopIfTrue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.STOPIFTRUE$16);
        }
    }
    
    public boolean getAboveAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.ABOVEAVERAGE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCfRuleImpl.ABOVEAVERAGE$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetAboveAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.ABOVEAVERAGE$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCfRuleImpl.ABOVEAVERAGE$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetAboveAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.ABOVEAVERAGE$18) != null;
        }
    }
    
    public void setAboveAverage(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.ABOVEAVERAGE$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.ABOVEAVERAGE$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetAboveAverage(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.ABOVEAVERAGE$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCfRuleImpl.ABOVEAVERAGE$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetAboveAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.ABOVEAVERAGE$18);
        }
    }
    
    public boolean getPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.PERCENT$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCfRuleImpl.PERCENT$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.PERCENT$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCfRuleImpl.PERCENT$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.PERCENT$20) != null;
        }
    }
    
    public void setPercent(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.PERCENT$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.PERCENT$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetPercent(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.PERCENT$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCfRuleImpl.PERCENT$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.PERCENT$20);
        }
    }
    
    public boolean getBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.BOTTOM$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCfRuleImpl.BOTTOM$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.BOTTOM$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCfRuleImpl.BOTTOM$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.BOTTOM$22) != null;
        }
    }
    
    public void setBottom(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.BOTTOM$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.BOTTOM$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetBottom(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.BOTTOM$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCfRuleImpl.BOTTOM$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetBottom() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.BOTTOM$22);
        }
    }
    
    public STConditionalFormattingOperator.Enum getOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.OPERATOR$24);
            if (simpleValue == null) {
                return null;
            }
            return (STConditionalFormattingOperator.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STConditionalFormattingOperator xgetOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STConditionalFormattingOperator)this.get_store().find_attribute_user(CTCfRuleImpl.OPERATOR$24);
        }
    }
    
    public boolean isSetOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.OPERATOR$24) != null;
        }
    }
    
    public void setOperator(final STConditionalFormattingOperator.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.OPERATOR$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.OPERATOR$24);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetOperator(final STConditionalFormattingOperator stConditionalFormattingOperator) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STConditionalFormattingOperator stConditionalFormattingOperator2 = (STConditionalFormattingOperator)this.get_store().find_attribute_user(CTCfRuleImpl.OPERATOR$24);
            if (stConditionalFormattingOperator2 == null) {
                stConditionalFormattingOperator2 = (STConditionalFormattingOperator)this.get_store().add_attribute_user(CTCfRuleImpl.OPERATOR$24);
            }
            stConditionalFormattingOperator2.set((XmlObject)stConditionalFormattingOperator);
        }
    }
    
    public void unsetOperator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.OPERATOR$24);
        }
    }
    
    public String getText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.TEXT$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_attribute_user(CTCfRuleImpl.TEXT$26);
        }
    }
    
    public boolean isSetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.TEXT$26) != null;
        }
    }
    
    public void setText(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.TEXT$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.TEXT$26);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetText(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(CTCfRuleImpl.TEXT$26);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_attribute_user(CTCfRuleImpl.TEXT$26);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.TEXT$26);
        }
    }
    
    public STTimePeriod.Enum getTimePeriod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.TIMEPERIOD$28);
            if (simpleValue == null) {
                return null;
            }
            return (STTimePeriod.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STTimePeriod xgetTimePeriod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STTimePeriod)this.get_store().find_attribute_user(CTCfRuleImpl.TIMEPERIOD$28);
        }
    }
    
    public boolean isSetTimePeriod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.TIMEPERIOD$28) != null;
        }
    }
    
    public void setTimePeriod(final STTimePeriod.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.TIMEPERIOD$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.TIMEPERIOD$28);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetTimePeriod(final STTimePeriod stTimePeriod) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STTimePeriod stTimePeriod2 = (STTimePeriod)this.get_store().find_attribute_user(CTCfRuleImpl.TIMEPERIOD$28);
            if (stTimePeriod2 == null) {
                stTimePeriod2 = (STTimePeriod)this.get_store().add_attribute_user(CTCfRuleImpl.TIMEPERIOD$28);
            }
            stTimePeriod2.set((XmlObject)stTimePeriod);
        }
    }
    
    public void unsetTimePeriod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.TIMEPERIOD$28);
        }
    }
    
    public long getRank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.RANK$30);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetRank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTCfRuleImpl.RANK$30);
        }
    }
    
    public boolean isSetRank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.RANK$30) != null;
        }
    }
    
    public void setRank(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.RANK$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.RANK$30);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetRank(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTCfRuleImpl.RANK$30);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTCfRuleImpl.RANK$30);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetRank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.RANK$30);
        }
    }
    
    public int getStdDev() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.STDDEV$32);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetStdDev() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_attribute_user(CTCfRuleImpl.STDDEV$32);
        }
    }
    
    public boolean isSetStdDev() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.STDDEV$32) != null;
        }
    }
    
    public void setStdDev(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.STDDEV$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.STDDEV$32);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetStdDev(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_attribute_user(CTCfRuleImpl.STDDEV$32);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_attribute_user(CTCfRuleImpl.STDDEV$32);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetStdDev() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.STDDEV$32);
        }
    }
    
    public boolean getEqualAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.EQUALAVERAGE$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTCfRuleImpl.EQUALAVERAGE$34);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetEqualAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.EQUALAVERAGE$34);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTCfRuleImpl.EQUALAVERAGE$34);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetEqualAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTCfRuleImpl.EQUALAVERAGE$34) != null;
        }
    }
    
    public void setEqualAverage(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTCfRuleImpl.EQUALAVERAGE$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTCfRuleImpl.EQUALAVERAGE$34);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetEqualAverage(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTCfRuleImpl.EQUALAVERAGE$34);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTCfRuleImpl.EQUALAVERAGE$34);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetEqualAverage() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTCfRuleImpl.EQUALAVERAGE$34);
        }
    }
    
    static {
        FORMULA$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "formula");
        COLORSCALE$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "colorScale");
        DATABAR$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "dataBar");
        ICONSET$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "iconSet");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        TYPE$10 = new QName("", "type");
        DXFID$12 = new QName("", "dxfId");
        PRIORITY$14 = new QName("", "priority");
        STOPIFTRUE$16 = new QName("", "stopIfTrue");
        ABOVEAVERAGE$18 = new QName("", "aboveAverage");
        PERCENT$20 = new QName("", "percent");
        BOTTOM$22 = new QName("", "bottom");
        OPERATOR$24 = new QName("", "operator");
        TEXT$26 = new QName("", "text");
        TIMEPERIOD$28 = new QName("", "timePeriod");
        RANK$30 = new QName("", "rank");
        STDDEV$32 = new QName("", "stdDev");
        EQUALAVERAGE$34 = new QName("", "equalAverage");
    }
}
