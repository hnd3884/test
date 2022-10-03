package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotSelection;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPane;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetView;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSheetViewImpl extends XmlComplexContentImpl implements CTSheetView
{
    private static final long serialVersionUID = 1L;
    private static final QName PANE$0;
    private static final QName SELECTION$2;
    private static final QName PIVOTSELECTION$4;
    private static final QName EXTLST$6;
    private static final QName WINDOWPROTECTION$8;
    private static final QName SHOWFORMULAS$10;
    private static final QName SHOWGRIDLINES$12;
    private static final QName SHOWROWCOLHEADERS$14;
    private static final QName SHOWZEROS$16;
    private static final QName RIGHTTOLEFT$18;
    private static final QName TABSELECTED$20;
    private static final QName SHOWRULER$22;
    private static final QName SHOWOUTLINESYMBOLS$24;
    private static final QName DEFAULTGRIDCOLOR$26;
    private static final QName SHOWWHITESPACE$28;
    private static final QName VIEW$30;
    private static final QName TOPLEFTCELL$32;
    private static final QName COLORID$34;
    private static final QName ZOOMSCALE$36;
    private static final QName ZOOMSCALENORMAL$38;
    private static final QName ZOOMSCALESHEETLAYOUTVIEW$40;
    private static final QName ZOOMSCALEPAGELAYOUTVIEW$42;
    private static final QName WORKBOOKVIEWID$44;
    
    public CTSheetViewImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTPane getPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPane ctPane = (CTPane)this.get_store().find_element_user(CTSheetViewImpl.PANE$0, 0);
            if (ctPane == null) {
                return null;
            }
            return ctPane;
        }
    }
    
    public boolean isSetPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetViewImpl.PANE$0) != 0;
        }
    }
    
    public void setPane(final CTPane ctPane) {
        this.generatedSetterHelperImpl((XmlObject)ctPane, CTSheetViewImpl.PANE$0, 0, (short)1);
    }
    
    public CTPane addNewPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPane)this.get_store().add_element_user(CTSheetViewImpl.PANE$0);
        }
    }
    
    public void unsetPane() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetViewImpl.PANE$0, 0);
        }
    }
    
    public List<CTSelection> getSelectionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SelectionList extends AbstractList<CTSelection>
            {
                @Override
                public CTSelection get(final int n) {
                    return CTSheetViewImpl.this.getSelectionArray(n);
                }
                
                @Override
                public CTSelection set(final int n, final CTSelection ctSelection) {
                    final CTSelection selectionArray = CTSheetViewImpl.this.getSelectionArray(n);
                    CTSheetViewImpl.this.setSelectionArray(n, ctSelection);
                    return selectionArray;
                }
                
                @Override
                public void add(final int n, final CTSelection ctSelection) {
                    CTSheetViewImpl.this.insertNewSelection(n).set((XmlObject)ctSelection);
                }
                
                @Override
                public CTSelection remove(final int n) {
                    final CTSelection selectionArray = CTSheetViewImpl.this.getSelectionArray(n);
                    CTSheetViewImpl.this.removeSelection(n);
                    return selectionArray;
                }
                
                @Override
                public int size() {
                    return CTSheetViewImpl.this.sizeOfSelectionArray();
                }
            }
            return new SelectionList();
        }
    }
    
    @Deprecated
    public CTSelection[] getSelectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSheetViewImpl.SELECTION$2, (List)list);
            final CTSelection[] array = new CTSelection[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSelection getSelectionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSelection ctSelection = (CTSelection)this.get_store().find_element_user(CTSheetViewImpl.SELECTION$2, n);
            if (ctSelection == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSelection;
        }
    }
    
    public int sizeOfSelectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetViewImpl.SELECTION$2);
        }
    }
    
    public void setSelectionArray(final CTSelection[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSheetViewImpl.SELECTION$2);
    }
    
    public void setSelectionArray(final int n, final CTSelection ctSelection) {
        this.generatedSetterHelperImpl((XmlObject)ctSelection, CTSheetViewImpl.SELECTION$2, n, (short)2);
    }
    
    public CTSelection insertNewSelection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSelection)this.get_store().insert_element_user(CTSheetViewImpl.SELECTION$2, n);
        }
    }
    
    public CTSelection addNewSelection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSelection)this.get_store().add_element_user(CTSheetViewImpl.SELECTION$2);
        }
    }
    
    public void removeSelection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetViewImpl.SELECTION$2, n);
        }
    }
    
    public List<CTPivotSelection> getPivotSelectionList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PivotSelectionList extends AbstractList<CTPivotSelection>
            {
                @Override
                public CTPivotSelection get(final int n) {
                    return CTSheetViewImpl.this.getPivotSelectionArray(n);
                }
                
                @Override
                public CTPivotSelection set(final int n, final CTPivotSelection ctPivotSelection) {
                    final CTPivotSelection pivotSelectionArray = CTSheetViewImpl.this.getPivotSelectionArray(n);
                    CTSheetViewImpl.this.setPivotSelectionArray(n, ctPivotSelection);
                    return pivotSelectionArray;
                }
                
                @Override
                public void add(final int n, final CTPivotSelection ctPivotSelection) {
                    CTSheetViewImpl.this.insertNewPivotSelection(n).set((XmlObject)ctPivotSelection);
                }
                
                @Override
                public CTPivotSelection remove(final int n) {
                    final CTPivotSelection pivotSelectionArray = CTSheetViewImpl.this.getPivotSelectionArray(n);
                    CTSheetViewImpl.this.removePivotSelection(n);
                    return pivotSelectionArray;
                }
                
                @Override
                public int size() {
                    return CTSheetViewImpl.this.sizeOfPivotSelectionArray();
                }
            }
            return new PivotSelectionList();
        }
    }
    
    @Deprecated
    public CTPivotSelection[] getPivotSelectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSheetViewImpl.PIVOTSELECTION$4, (List)list);
            final CTPivotSelection[] array = new CTPivotSelection[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPivotSelection getPivotSelectionArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotSelection ctPivotSelection = (CTPivotSelection)this.get_store().find_element_user(CTSheetViewImpl.PIVOTSELECTION$4, n);
            if (ctPivotSelection == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPivotSelection;
        }
    }
    
    public int sizeOfPivotSelectionArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetViewImpl.PIVOTSELECTION$4);
        }
    }
    
    public void setPivotSelectionArray(final CTPivotSelection[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSheetViewImpl.PIVOTSELECTION$4);
    }
    
    public void setPivotSelectionArray(final int n, final CTPivotSelection ctPivotSelection) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotSelection, CTSheetViewImpl.PIVOTSELECTION$4, n, (short)2);
    }
    
    public CTPivotSelection insertNewPivotSelection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotSelection)this.get_store().insert_element_user(CTSheetViewImpl.PIVOTSELECTION$4, n);
        }
    }
    
    public CTPivotSelection addNewPivotSelection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotSelection)this.get_store().add_element_user(CTSheetViewImpl.PIVOTSELECTION$4);
        }
    }
    
    public void removePivotSelection(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetViewImpl.PIVOTSELECTION$4, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSheetViewImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSheetViewImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSheetViewImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSheetViewImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSheetViewImpl.EXTLST$6, 0);
        }
    }
    
    public boolean getWindowProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.WINDOWPROTECTION$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.WINDOWPROTECTION$8);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetWindowProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.WINDOWPROTECTION$8);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.WINDOWPROTECTION$8);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetWindowProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.WINDOWPROTECTION$8) != null;
        }
    }
    
    public void setWindowProtection(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.WINDOWPROTECTION$8);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.WINDOWPROTECTION$8);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetWindowProtection(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.WINDOWPROTECTION$8);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.WINDOWPROTECTION$8);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetWindowProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.WINDOWPROTECTION$8);
        }
    }
    
    public boolean getShowFormulas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWFORMULAS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.SHOWFORMULAS$10);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowFormulas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWFORMULAS$10);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.SHOWFORMULAS$10);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowFormulas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.SHOWFORMULAS$10) != null;
        }
    }
    
    public void setShowFormulas(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWFORMULAS$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWFORMULAS$10);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowFormulas(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWFORMULAS$10);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWFORMULAS$10);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowFormulas() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.SHOWFORMULAS$10);
        }
    }
    
    public boolean getShowGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWGRIDLINES$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.SHOWGRIDLINES$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWGRIDLINES$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.SHOWGRIDLINES$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.SHOWGRIDLINES$12) != null;
        }
    }
    
    public void setShowGridLines(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWGRIDLINES$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWGRIDLINES$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowGridLines(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWGRIDLINES$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWGRIDLINES$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowGridLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.SHOWGRIDLINES$12);
        }
    }
    
    public boolean getShowRowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowRowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowRowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.SHOWROWCOLHEADERS$14) != null;
        }
    }
    
    public void setShowRowColHeaders(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowRowColHeaders(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowRowColHeaders() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.SHOWROWCOLHEADERS$14);
        }
    }
    
    public boolean getShowZeros() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWZEROS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.SHOWZEROS$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowZeros() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWZEROS$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.SHOWZEROS$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowZeros() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.SHOWZEROS$16) != null;
        }
    }
    
    public void setShowZeros(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWZEROS$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWZEROS$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowZeros(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWZEROS$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWZEROS$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowZeros() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.SHOWZEROS$16);
        }
    }
    
    public boolean getRightToLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.RIGHTTOLEFT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.RIGHTTOLEFT$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetRightToLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.RIGHTTOLEFT$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.RIGHTTOLEFT$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetRightToLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.RIGHTTOLEFT$18) != null;
        }
    }
    
    public void setRightToLeft(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.RIGHTTOLEFT$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.RIGHTTOLEFT$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetRightToLeft(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.RIGHTTOLEFT$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.RIGHTTOLEFT$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetRightToLeft() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.RIGHTTOLEFT$18);
        }
    }
    
    public boolean getTabSelected() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.TABSELECTED$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.TABSELECTED$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetTabSelected() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.TABSELECTED$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.TABSELECTED$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetTabSelected() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.TABSELECTED$20) != null;
        }
    }
    
    public void setTabSelected(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.TABSELECTED$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.TABSELECTED$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetTabSelected(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.TABSELECTED$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.TABSELECTED$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetTabSelected() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.TABSELECTED$20);
        }
    }
    
    public boolean getShowRuler() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWRULER$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.SHOWRULER$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowRuler() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWRULER$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.SHOWRULER$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowRuler() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.SHOWRULER$22) != null;
        }
    }
    
    public void setShowRuler(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWRULER$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWRULER$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowRuler(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWRULER$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWRULER$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowRuler() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.SHOWRULER$22);
        }
    }
    
    public boolean getShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24) != null;
        }
    }
    
    public void setShowOutlineSymbols(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowOutlineSymbols(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowOutlineSymbols() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.SHOWOUTLINESYMBOLS$24);
        }
    }
    
    public boolean getDefaultGridColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetDefaultGridColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetDefaultGridColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.DEFAULTGRIDCOLOR$26) != null;
        }
    }
    
    public void setDefaultGridColor(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetDefaultGridColor(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetDefaultGridColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.DEFAULTGRIDCOLOR$26);
        }
    }
    
    public boolean getShowWhiteSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWWHITESPACE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.SHOWWHITESPACE$28);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetShowWhiteSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWWHITESPACE$28);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSheetViewImpl.SHOWWHITESPACE$28);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetShowWhiteSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.SHOWWHITESPACE$28) != null;
        }
    }
    
    public void setShowWhiteSpace(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWWHITESPACE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWWHITESPACE$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetShowWhiteSpace(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSheetViewImpl.SHOWWHITESPACE$28);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSheetViewImpl.SHOWWHITESPACE$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetShowWhiteSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.SHOWWHITESPACE$28);
        }
    }
    
    public STSheetViewType.Enum getView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.VIEW$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.VIEW$30);
            }
            if (simpleValue == null) {
                return null;
            }
            return (STSheetViewType.Enum)simpleValue.getEnumValue();
        }
    }
    
    public STSheetViewType xgetView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSheetViewType stSheetViewType = (STSheetViewType)this.get_store().find_attribute_user(CTSheetViewImpl.VIEW$30);
            if (stSheetViewType == null) {
                stSheetViewType = (STSheetViewType)this.get_default_attribute_value(CTSheetViewImpl.VIEW$30);
            }
            return stSheetViewType;
        }
    }
    
    public boolean isSetView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.VIEW$30) != null;
        }
    }
    
    public void setView(final STSheetViewType.Enum enumValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.VIEW$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.VIEW$30);
            }
            simpleValue.setEnumValue((StringEnumAbstractBase)enumValue);
        }
    }
    
    public void xsetView(final STSheetViewType stSheetViewType) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STSheetViewType stSheetViewType2 = (STSheetViewType)this.get_store().find_attribute_user(CTSheetViewImpl.VIEW$30);
            if (stSheetViewType2 == null) {
                stSheetViewType2 = (STSheetViewType)this.get_store().add_attribute_user(CTSheetViewImpl.VIEW$30);
            }
            stSheetViewType2.set((XmlObject)stSheetViewType);
        }
    }
    
    public void unsetView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.VIEW$30);
        }
    }
    
    public String getTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.TOPLEFTCELL$32);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public STCellRef xgetTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (STCellRef)this.get_store().find_attribute_user(CTSheetViewImpl.TOPLEFTCELL$32);
        }
    }
    
    public boolean isSetTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.TOPLEFTCELL$32) != null;
        }
    }
    
    public void setTopLeftCell(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.TOPLEFTCELL$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.TOPLEFTCELL$32);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTopLeftCell(final STCellRef stCellRef) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            STCellRef stCellRef2 = (STCellRef)this.get_store().find_attribute_user(CTSheetViewImpl.TOPLEFTCELL$32);
            if (stCellRef2 == null) {
                stCellRef2 = (STCellRef)this.get_store().add_attribute_user(CTSheetViewImpl.TOPLEFTCELL$32);
            }
            stCellRef2.set((XmlObject)stCellRef);
        }
    }
    
    public void unsetTopLeftCell() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.TOPLEFTCELL$32);
        }
    }
    
    public long getColorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.COLORID$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.COLORID$34);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetColorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.COLORID$34);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTSheetViewImpl.COLORID$34);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetColorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.COLORID$34) != null;
        }
    }
    
    public void setColorId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.COLORID$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.COLORID$34);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetColorId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.COLORID$34);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetViewImpl.COLORID$34);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetColorId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.COLORID$34);
        }
    }
    
    public long getZoomScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALE$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALE$36);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetZoomScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALE$36);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALE$36);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetZoomScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALE$36) != null;
        }
    }
    
    public void setZoomScale(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALE$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALE$36);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetZoomScale(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALE$36);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALE$36);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetZoomScale() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.ZOOMSCALE$36);
        }
    }
    
    public long getZoomScaleNormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetZoomScaleNormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetZoomScaleNormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALENORMAL$38) != null;
        }
    }
    
    public void setZoomScaleNormal(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetZoomScaleNormal(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALENORMAL$38);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetZoomScaleNormal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.ZOOMSCALENORMAL$38);
        }
    }
    
    public long getZoomScaleSheetLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetZoomScaleSheetLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetZoomScaleSheetLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40) != null;
        }
    }
    
    public void setZoomScaleSheetLayoutView(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetZoomScaleSheetLayoutView(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetZoomScaleSheetLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.ZOOMSCALESHEETLAYOUTVIEW$40);
        }
    }
    
    public long getZoomScalePageLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            }
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetZoomScalePageLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            if (xmlUnsignedInt == null) {
                xmlUnsignedInt = (XmlUnsignedInt)this.get_default_attribute_value(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            }
            return xmlUnsignedInt;
        }
    }
    
    public boolean isSetZoomScalePageLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42) != null;
        }
    }
    
    public void setZoomScalePageLayoutView(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetZoomScalePageLayoutView(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetZoomScalePageLayoutView() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSheetViewImpl.ZOOMSCALEPAGELAYOUTVIEW$42);
        }
    }
    
    public long getWorkbookViewId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.WORKBOOKVIEWID$44);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetWorkbookViewId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.WORKBOOKVIEWID$44);
        }
    }
    
    public void setWorkbookViewId(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSheetViewImpl.WORKBOOKVIEWID$44);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSheetViewImpl.WORKBOOKVIEWID$44);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetWorkbookViewId(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSheetViewImpl.WORKBOOKVIEWID$44);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSheetViewImpl.WORKBOOKVIEWID$44);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    static {
        PANE$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pane");
        SELECTION$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "selection");
        PIVOTSELECTION$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "pivotSelection");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extLst");
        WINDOWPROTECTION$8 = new QName("", "windowProtection");
        SHOWFORMULAS$10 = new QName("", "showFormulas");
        SHOWGRIDLINES$12 = new QName("", "showGridLines");
        SHOWROWCOLHEADERS$14 = new QName("", "showRowColHeaders");
        SHOWZEROS$16 = new QName("", "showZeros");
        RIGHTTOLEFT$18 = new QName("", "rightToLeft");
        TABSELECTED$20 = new QName("", "tabSelected");
        SHOWRULER$22 = new QName("", "showRuler");
        SHOWOUTLINESYMBOLS$24 = new QName("", "showOutlineSymbols");
        DEFAULTGRIDCOLOR$26 = new QName("", "defaultGridColor");
        SHOWWHITESPACE$28 = new QName("", "showWhiteSpace");
        VIEW$30 = new QName("", "view");
        TOPLEFTCELL$32 = new QName("", "topLeftCell");
        COLORID$34 = new QName("", "colorId");
        ZOOMSCALE$36 = new QName("", "zoomScale");
        ZOOMSCALENORMAL$38 = new QName("", "zoomScaleNormal");
        ZOOMSCALESHEETLAYOUTVIEW$40 = new QName("", "zoomScaleSheetLayoutView");
        ZOOMSCALEPAGELAYOUTVIEW$42 = new QName("", "zoomScalePageLayoutView");
        WORKBOOKVIEWID$44 = new QName("", "workbookViewId");
    }
}
