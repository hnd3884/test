package org.openxmlformats.schemas.drawingml.x2006.main.impl;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextField;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextLineBreak;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTextParagraphImpl extends XmlComplexContentImpl implements CTTextParagraph
{
    private static final long serialVersionUID = 1L;
    private static final QName PPR$0;
    private static final QName R$2;
    private static final QName BR$4;
    private static final QName FLD$6;
    private static final QName ENDPARARPR$8;
    
    public CTTextParagraphImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTextParagraphProperties getPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextParagraphProperties ctTextParagraphProperties = (CTTextParagraphProperties)this.get_store().find_element_user(CTTextParagraphImpl.PPR$0, 0);
            if (ctTextParagraphProperties == null) {
                return null;
            }
            return ctTextParagraphProperties;
        }
    }
    
    public boolean isSetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphImpl.PPR$0) != 0;
        }
    }
    
    public void setPPr(final CTTextParagraphProperties ctTextParagraphProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextParagraphProperties, CTTextParagraphImpl.PPR$0, 0, (short)1);
    }
    
    public CTTextParagraphProperties addNewPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextParagraphProperties)this.get_store().add_element_user(CTTextParagraphImpl.PPR$0);
        }
    }
    
    public void unsetPPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphImpl.PPR$0, 0);
        }
    }
    
    public List<CTRegularTextRun> getRList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RList extends AbstractList<CTRegularTextRun>
            {
                @Override
                public CTRegularTextRun get(final int n) {
                    return CTTextParagraphImpl.this.getRArray(n);
                }
                
                @Override
                public CTRegularTextRun set(final int n, final CTRegularTextRun ctRegularTextRun) {
                    final CTRegularTextRun rArray = CTTextParagraphImpl.this.getRArray(n);
                    CTTextParagraphImpl.this.setRArray(n, ctRegularTextRun);
                    return rArray;
                }
                
                @Override
                public void add(final int n, final CTRegularTextRun ctRegularTextRun) {
                    CTTextParagraphImpl.this.insertNewR(n).set((XmlObject)ctRegularTextRun);
                }
                
                @Override
                public CTRegularTextRun remove(final int n) {
                    final CTRegularTextRun rArray = CTTextParagraphImpl.this.getRArray(n);
                    CTTextParagraphImpl.this.removeR(n);
                    return rArray;
                }
                
                @Override
                public int size() {
                    return CTTextParagraphImpl.this.sizeOfRArray();
                }
            }
            return new RList();
        }
    }
    
    @Deprecated
    public CTRegularTextRun[] getRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTextParagraphImpl.R$2, (List)list);
            final CTRegularTextRun[] array = new CTRegularTextRun[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRegularTextRun getRArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRegularTextRun ctRegularTextRun = (CTRegularTextRun)this.get_store().find_element_user(CTTextParagraphImpl.R$2, n);
            if (ctRegularTextRun == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRegularTextRun;
        }
    }
    
    public int sizeOfRArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphImpl.R$2);
        }
    }
    
    public void setRArray(final CTRegularTextRun[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTextParagraphImpl.R$2);
    }
    
    public void setRArray(final int n, final CTRegularTextRun ctRegularTextRun) {
        this.generatedSetterHelperImpl((XmlObject)ctRegularTextRun, CTTextParagraphImpl.R$2, n, (short)2);
    }
    
    public CTRegularTextRun insertNewR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRegularTextRun)this.get_store().insert_element_user(CTTextParagraphImpl.R$2, n);
        }
    }
    
    public CTRegularTextRun addNewR() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRegularTextRun)this.get_store().add_element_user(CTTextParagraphImpl.R$2);
        }
    }
    
    public void removeR(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphImpl.R$2, n);
        }
    }
    
    public List<CTTextLineBreak> getBrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BrList extends AbstractList<CTTextLineBreak>
            {
                @Override
                public CTTextLineBreak get(final int n) {
                    return CTTextParagraphImpl.this.getBrArray(n);
                }
                
                @Override
                public CTTextLineBreak set(final int n, final CTTextLineBreak ctTextLineBreak) {
                    final CTTextLineBreak brArray = CTTextParagraphImpl.this.getBrArray(n);
                    CTTextParagraphImpl.this.setBrArray(n, ctTextLineBreak);
                    return brArray;
                }
                
                @Override
                public void add(final int n, final CTTextLineBreak ctTextLineBreak) {
                    CTTextParagraphImpl.this.insertNewBr(n).set((XmlObject)ctTextLineBreak);
                }
                
                @Override
                public CTTextLineBreak remove(final int n) {
                    final CTTextLineBreak brArray = CTTextParagraphImpl.this.getBrArray(n);
                    CTTextParagraphImpl.this.removeBr(n);
                    return brArray;
                }
                
                @Override
                public int size() {
                    return CTTextParagraphImpl.this.sizeOfBrArray();
                }
            }
            return new BrList();
        }
    }
    
    @Deprecated
    public CTTextLineBreak[] getBrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTextParagraphImpl.BR$4, (List)list);
            final CTTextLineBreak[] array = new CTTextLineBreak[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextLineBreak getBrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextLineBreak ctTextLineBreak = (CTTextLineBreak)this.get_store().find_element_user(CTTextParagraphImpl.BR$4, n);
            if (ctTextLineBreak == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextLineBreak;
        }
    }
    
    public int sizeOfBrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphImpl.BR$4);
        }
    }
    
    public void setBrArray(final CTTextLineBreak[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTextParagraphImpl.BR$4);
    }
    
    public void setBrArray(final int n, final CTTextLineBreak ctTextLineBreak) {
        this.generatedSetterHelperImpl((XmlObject)ctTextLineBreak, CTTextParagraphImpl.BR$4, n, (short)2);
    }
    
    public CTTextLineBreak insertNewBr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextLineBreak)this.get_store().insert_element_user(CTTextParagraphImpl.BR$4, n);
        }
    }
    
    public CTTextLineBreak addNewBr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextLineBreak)this.get_store().add_element_user(CTTextParagraphImpl.BR$4);
        }
    }
    
    public void removeBr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphImpl.BR$4, n);
        }
    }
    
    public List<CTTextField> getFldList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FldList extends AbstractList<CTTextField>
            {
                @Override
                public CTTextField get(final int n) {
                    return CTTextParagraphImpl.this.getFldArray(n);
                }
                
                @Override
                public CTTextField set(final int n, final CTTextField ctTextField) {
                    final CTTextField fldArray = CTTextParagraphImpl.this.getFldArray(n);
                    CTTextParagraphImpl.this.setFldArray(n, ctTextField);
                    return fldArray;
                }
                
                @Override
                public void add(final int n, final CTTextField ctTextField) {
                    CTTextParagraphImpl.this.insertNewFld(n).set((XmlObject)ctTextField);
                }
                
                @Override
                public CTTextField remove(final int n) {
                    final CTTextField fldArray = CTTextParagraphImpl.this.getFldArray(n);
                    CTTextParagraphImpl.this.removeFld(n);
                    return fldArray;
                }
                
                @Override
                public int size() {
                    return CTTextParagraphImpl.this.sizeOfFldArray();
                }
            }
            return new FldList();
        }
    }
    
    @Deprecated
    public CTTextField[] getFldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTextParagraphImpl.FLD$6, (List)list);
            final CTTextField[] array = new CTTextField[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTextField getFldArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextField ctTextField = (CTTextField)this.get_store().find_element_user(CTTextParagraphImpl.FLD$6, n);
            if (ctTextField == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTextField;
        }
    }
    
    public int sizeOfFldArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphImpl.FLD$6);
        }
    }
    
    public void setFldArray(final CTTextField[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTextParagraphImpl.FLD$6);
    }
    
    public void setFldArray(final int n, final CTTextField ctTextField) {
        this.generatedSetterHelperImpl((XmlObject)ctTextField, CTTextParagraphImpl.FLD$6, n, (short)2);
    }
    
    public CTTextField insertNewFld(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextField)this.get_store().insert_element_user(CTTextParagraphImpl.FLD$6, n);
        }
    }
    
    public CTTextField addNewFld() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextField)this.get_store().add_element_user(CTTextParagraphImpl.FLD$6);
        }
    }
    
    public void removeFld(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphImpl.FLD$6, n);
        }
    }
    
    public CTTextCharacterProperties getEndParaRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextCharacterProperties ctTextCharacterProperties = (CTTextCharacterProperties)this.get_store().find_element_user(CTTextParagraphImpl.ENDPARARPR$8, 0);
            if (ctTextCharacterProperties == null) {
                return null;
            }
            return ctTextCharacterProperties;
        }
    }
    
    public boolean isSetEndParaRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTextParagraphImpl.ENDPARARPR$8) != 0;
        }
    }
    
    public void setEndParaRPr(final CTTextCharacterProperties ctTextCharacterProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctTextCharacterProperties, CTTextParagraphImpl.ENDPARARPR$8, 0, (short)1);
    }
    
    public CTTextCharacterProperties addNewEndParaRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextCharacterProperties)this.get_store().add_element_user(CTTextParagraphImpl.ENDPARARPR$8);
        }
    }
    
    public void unsetEndParaRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTextParagraphImpl.ENDPARARPR$8, 0);
        }
    }
    
    static {
        PPR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "pPr");
        R$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "r");
        BR$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "br");
        FLD$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "fld");
        ENDPARARPR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "endParaRPr");
    }
}
