package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontScheme;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTVerticalAlignFontProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTUnderlineProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontSize;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBooleanProperty;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTIntProperty;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFontName;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFont;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFontImpl extends XmlComplexContentImpl implements CTFont
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName CHARSET$2;
    private static final QName FAMILY$4;
    private static final QName B$6;
    private static final QName I$8;
    private static final QName STRIKE$10;
    private static final QName OUTLINE$12;
    private static final QName SHADOW$14;
    private static final QName CONDENSE$16;
    private static final QName EXTEND$18;
    private static final QName COLOR$20;
    private static final QName SZ$22;
    private static final QName U$24;
    private static final QName VERTALIGN$26;
    private static final QName SCHEME$28;
    
    public CTFontImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFontName> getNameList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NameList extends AbstractList<CTFontName>
            {
                @Override
                public CTFontName get(final int n) {
                    return CTFontImpl.this.getNameArray(n);
                }
                
                @Override
                public CTFontName set(final int n, final CTFontName ctFontName) {
                    final CTFontName nameArray = CTFontImpl.this.getNameArray(n);
                    CTFontImpl.this.setNameArray(n, ctFontName);
                    return nameArray;
                }
                
                @Override
                public void add(final int n, final CTFontName ctFontName) {
                    CTFontImpl.this.insertNewName(n).set((XmlObject)ctFontName);
                }
                
                @Override
                public CTFontName remove(final int n) {
                    final CTFontName nameArray = CTFontImpl.this.getNameArray(n);
                    CTFontImpl.this.removeName(n);
                    return nameArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfNameArray();
                }
            }
            return new NameList();
        }
    }
    
    @Deprecated
    public CTFontName[] getNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.NAME$0, (List)list);
            final CTFontName[] array = new CTFontName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFontName getNameArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontName ctFontName = (CTFontName)this.get_store().find_element_user(CTFontImpl.NAME$0, n);
            if (ctFontName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFontName;
        }
    }
    
    public int sizeOfNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.NAME$0);
        }
    }
    
    public void setNameArray(final CTFontName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.NAME$0);
    }
    
    public void setNameArray(final int n, final CTFontName ctFontName) {
        this.generatedSetterHelperImpl((XmlObject)ctFontName, CTFontImpl.NAME$0, n, (short)2);
    }
    
    public CTFontName insertNewName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontName)this.get_store().insert_element_user(CTFontImpl.NAME$0, n);
        }
    }
    
    public CTFontName addNewName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontName)this.get_store().add_element_user(CTFontImpl.NAME$0);
        }
    }
    
    public void removeName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.NAME$0, n);
        }
    }
    
    public List<CTIntProperty> getCharsetList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CharsetList extends AbstractList<CTIntProperty>
            {
                @Override
                public CTIntProperty get(final int n) {
                    return CTFontImpl.this.getCharsetArray(n);
                }
                
                @Override
                public CTIntProperty set(final int n, final CTIntProperty ctIntProperty) {
                    final CTIntProperty charsetArray = CTFontImpl.this.getCharsetArray(n);
                    CTFontImpl.this.setCharsetArray(n, ctIntProperty);
                    return charsetArray;
                }
                
                @Override
                public void add(final int n, final CTIntProperty ctIntProperty) {
                    CTFontImpl.this.insertNewCharset(n).set((XmlObject)ctIntProperty);
                }
                
                @Override
                public CTIntProperty remove(final int n) {
                    final CTIntProperty charsetArray = CTFontImpl.this.getCharsetArray(n);
                    CTFontImpl.this.removeCharset(n);
                    return charsetArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfCharsetArray();
                }
            }
            return new CharsetList();
        }
    }
    
    @Deprecated
    public CTIntProperty[] getCharsetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.CHARSET$2, (List)list);
            final CTIntProperty[] array = new CTIntProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTIntProperty getCharsetArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIntProperty ctIntProperty = (CTIntProperty)this.get_store().find_element_user(CTFontImpl.CHARSET$2, n);
            if (ctIntProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctIntProperty;
        }
    }
    
    public int sizeOfCharsetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.CHARSET$2);
        }
    }
    
    public void setCharsetArray(final CTIntProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.CHARSET$2);
    }
    
    public void setCharsetArray(final int n, final CTIntProperty ctIntProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctIntProperty, CTFontImpl.CHARSET$2, n, (short)2);
    }
    
    public CTIntProperty insertNewCharset(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().insert_element_user(CTFontImpl.CHARSET$2, n);
        }
    }
    
    public CTIntProperty addNewCharset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().add_element_user(CTFontImpl.CHARSET$2);
        }
    }
    
    public void removeCharset(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.CHARSET$2, n);
        }
    }
    
    public List<CTIntProperty> getFamilyList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FamilyList extends AbstractList<CTIntProperty>
            {
                @Override
                public CTIntProperty get(final int n) {
                    return CTFontImpl.this.getFamilyArray(n);
                }
                
                @Override
                public CTIntProperty set(final int n, final CTIntProperty ctIntProperty) {
                    final CTIntProperty familyArray = CTFontImpl.this.getFamilyArray(n);
                    CTFontImpl.this.setFamilyArray(n, ctIntProperty);
                    return familyArray;
                }
                
                @Override
                public void add(final int n, final CTIntProperty ctIntProperty) {
                    CTFontImpl.this.insertNewFamily(n).set((XmlObject)ctIntProperty);
                }
                
                @Override
                public CTIntProperty remove(final int n) {
                    final CTIntProperty familyArray = CTFontImpl.this.getFamilyArray(n);
                    CTFontImpl.this.removeFamily(n);
                    return familyArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfFamilyArray();
                }
            }
            return new FamilyList();
        }
    }
    
    @Deprecated
    public CTIntProperty[] getFamilyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.FAMILY$4, (List)list);
            final CTIntProperty[] array = new CTIntProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTIntProperty getFamilyArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIntProperty ctIntProperty = (CTIntProperty)this.get_store().find_element_user(CTFontImpl.FAMILY$4, n);
            if (ctIntProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctIntProperty;
        }
    }
    
    public int sizeOfFamilyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.FAMILY$4);
        }
    }
    
    public void setFamilyArray(final CTIntProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.FAMILY$4);
    }
    
    public void setFamilyArray(final int n, final CTIntProperty ctIntProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctIntProperty, CTFontImpl.FAMILY$4, n, (short)2);
    }
    
    public CTIntProperty insertNewFamily(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().insert_element_user(CTFontImpl.FAMILY$4, n);
        }
    }
    
    public CTIntProperty addNewFamily() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().add_element_user(CTFontImpl.FAMILY$4);
        }
    }
    
    public void removeFamily(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.FAMILY$4, n);
        }
    }
    
    public List<CTBooleanProperty> getBList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTFontImpl.this.getBArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty bArray = CTFontImpl.this.getBArray(n);
                    CTFontImpl.this.setBArray(n, ctBooleanProperty);
                    return bArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTFontImpl.this.insertNewB(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty bArray = CTFontImpl.this.getBArray(n);
                    CTFontImpl.this.removeB(n);
                    return bArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfBArray();
                }
            }
            return new BList();
        }
    }
    
    @Deprecated
    public CTBooleanProperty[] getBArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.B$6, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getBArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTFontImpl.B$6, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfBArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.B$6);
        }
    }
    
    public void setBArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.B$6);
    }
    
    public void setBArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTFontImpl.B$6, n, (short)2);
    }
    
    public CTBooleanProperty insertNewB(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTFontImpl.B$6, n);
        }
    }
    
    public CTBooleanProperty addNewB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTFontImpl.B$6);
        }
    }
    
    public void removeB(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.B$6, n);
        }
    }
    
    public List<CTBooleanProperty> getIList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class IList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTFontImpl.this.getIArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty iArray = CTFontImpl.this.getIArray(n);
                    CTFontImpl.this.setIArray(n, ctBooleanProperty);
                    return iArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTFontImpl.this.insertNewI(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty iArray = CTFontImpl.this.getIArray(n);
                    CTFontImpl.this.removeI(n);
                    return iArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfIArray();
                }
            }
            return new IList();
        }
    }
    
    @Deprecated
    public CTBooleanProperty[] getIArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.I$8, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getIArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTFontImpl.I$8, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfIArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.I$8);
        }
    }
    
    public void setIArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.I$8);
    }
    
    public void setIArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTFontImpl.I$8, n, (short)2);
    }
    
    public CTBooleanProperty insertNewI(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTFontImpl.I$8, n);
        }
    }
    
    public CTBooleanProperty addNewI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTFontImpl.I$8);
        }
    }
    
    public void removeI(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.I$8, n);
        }
    }
    
    public List<CTBooleanProperty> getStrikeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StrikeList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTFontImpl.this.getStrikeArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty strikeArray = CTFontImpl.this.getStrikeArray(n);
                    CTFontImpl.this.setStrikeArray(n, ctBooleanProperty);
                    return strikeArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTFontImpl.this.insertNewStrike(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty strikeArray = CTFontImpl.this.getStrikeArray(n);
                    CTFontImpl.this.removeStrike(n);
                    return strikeArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfStrikeArray();
                }
            }
            return new StrikeList();
        }
    }
    
    @Deprecated
    public CTBooleanProperty[] getStrikeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.STRIKE$10, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getStrikeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTFontImpl.STRIKE$10, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfStrikeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.STRIKE$10);
        }
    }
    
    public void setStrikeArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.STRIKE$10);
    }
    
    public void setStrikeArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTFontImpl.STRIKE$10, n, (short)2);
    }
    
    public CTBooleanProperty insertNewStrike(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTFontImpl.STRIKE$10, n);
        }
    }
    
    public CTBooleanProperty addNewStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTFontImpl.STRIKE$10);
        }
    }
    
    public void removeStrike(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.STRIKE$10, n);
        }
    }
    
    public List<CTBooleanProperty> getOutlineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OutlineList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTFontImpl.this.getOutlineArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty outlineArray = CTFontImpl.this.getOutlineArray(n);
                    CTFontImpl.this.setOutlineArray(n, ctBooleanProperty);
                    return outlineArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTFontImpl.this.insertNewOutline(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty outlineArray = CTFontImpl.this.getOutlineArray(n);
                    CTFontImpl.this.removeOutline(n);
                    return outlineArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfOutlineArray();
                }
            }
            return new OutlineList();
        }
    }
    
    @Deprecated
    public CTBooleanProperty[] getOutlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.OUTLINE$12, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getOutlineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTFontImpl.OUTLINE$12, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfOutlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.OUTLINE$12);
        }
    }
    
    public void setOutlineArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.OUTLINE$12);
    }
    
    public void setOutlineArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTFontImpl.OUTLINE$12, n, (short)2);
    }
    
    public CTBooleanProperty insertNewOutline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTFontImpl.OUTLINE$12, n);
        }
    }
    
    public CTBooleanProperty addNewOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTFontImpl.OUTLINE$12);
        }
    }
    
    public void removeOutline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.OUTLINE$12, n);
        }
    }
    
    public List<CTBooleanProperty> getShadowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShadowList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTFontImpl.this.getShadowArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty shadowArray = CTFontImpl.this.getShadowArray(n);
                    CTFontImpl.this.setShadowArray(n, ctBooleanProperty);
                    return shadowArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTFontImpl.this.insertNewShadow(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty shadowArray = CTFontImpl.this.getShadowArray(n);
                    CTFontImpl.this.removeShadow(n);
                    return shadowArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfShadowArray();
                }
            }
            return new ShadowList();
        }
    }
    
    @Deprecated
    public CTBooleanProperty[] getShadowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.SHADOW$14, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getShadowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTFontImpl.SHADOW$14, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfShadowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.SHADOW$14);
        }
    }
    
    public void setShadowArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.SHADOW$14);
    }
    
    public void setShadowArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTFontImpl.SHADOW$14, n, (short)2);
    }
    
    public CTBooleanProperty insertNewShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTFontImpl.SHADOW$14, n);
        }
    }
    
    public CTBooleanProperty addNewShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTFontImpl.SHADOW$14);
        }
    }
    
    public void removeShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.SHADOW$14, n);
        }
    }
    
    public List<CTBooleanProperty> getCondenseList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CondenseList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTFontImpl.this.getCondenseArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty condenseArray = CTFontImpl.this.getCondenseArray(n);
                    CTFontImpl.this.setCondenseArray(n, ctBooleanProperty);
                    return condenseArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTFontImpl.this.insertNewCondense(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty condenseArray = CTFontImpl.this.getCondenseArray(n);
                    CTFontImpl.this.removeCondense(n);
                    return condenseArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfCondenseArray();
                }
            }
            return new CondenseList();
        }
    }
    
    @Deprecated
    public CTBooleanProperty[] getCondenseArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.CONDENSE$16, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getCondenseArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTFontImpl.CONDENSE$16, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfCondenseArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.CONDENSE$16);
        }
    }
    
    public void setCondenseArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.CONDENSE$16);
    }
    
    public void setCondenseArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTFontImpl.CONDENSE$16, n, (short)2);
    }
    
    public CTBooleanProperty insertNewCondense(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTFontImpl.CONDENSE$16, n);
        }
    }
    
    public CTBooleanProperty addNewCondense() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTFontImpl.CONDENSE$16);
        }
    }
    
    public void removeCondense(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.CONDENSE$16, n);
        }
    }
    
    public List<CTBooleanProperty> getExtendList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExtendList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTFontImpl.this.getExtendArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty extendArray = CTFontImpl.this.getExtendArray(n);
                    CTFontImpl.this.setExtendArray(n, ctBooleanProperty);
                    return extendArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTFontImpl.this.insertNewExtend(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty extendArray = CTFontImpl.this.getExtendArray(n);
                    CTFontImpl.this.removeExtend(n);
                    return extendArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfExtendArray();
                }
            }
            return new ExtendList();
        }
    }
    
    @Deprecated
    public CTBooleanProperty[] getExtendArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.EXTEND$18, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getExtendArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTFontImpl.EXTEND$18, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfExtendArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.EXTEND$18);
        }
    }
    
    public void setExtendArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.EXTEND$18);
    }
    
    public void setExtendArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTFontImpl.EXTEND$18, n, (short)2);
    }
    
    public CTBooleanProperty insertNewExtend(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTFontImpl.EXTEND$18, n);
        }
    }
    
    public CTBooleanProperty addNewExtend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTFontImpl.EXTEND$18);
        }
    }
    
    public void removeExtend(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.EXTEND$18, n);
        }
    }
    
    public List<CTColor> getColorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColorList extends AbstractList<CTColor>
            {
                @Override
                public CTColor get(final int n) {
                    return CTFontImpl.this.getColorArray(n);
                }
                
                @Override
                public CTColor set(final int n, final CTColor ctColor) {
                    final CTColor colorArray = CTFontImpl.this.getColorArray(n);
                    CTFontImpl.this.setColorArray(n, ctColor);
                    return colorArray;
                }
                
                @Override
                public void add(final int n, final CTColor ctColor) {
                    CTFontImpl.this.insertNewColor(n).set((XmlObject)ctColor);
                }
                
                @Override
                public CTColor remove(final int n) {
                    final CTColor colorArray = CTFontImpl.this.getColorArray(n);
                    CTFontImpl.this.removeColor(n);
                    return colorArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfColorArray();
                }
            }
            return new ColorList();
        }
    }
    
    @Deprecated
    public CTColor[] getColorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.COLOR$20, (List)list);
            final CTColor[] array = new CTColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTColor getColorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTFontImpl.COLOR$20, n);
            if (ctColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctColor;
        }
    }
    
    public int sizeOfColorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.COLOR$20);
        }
    }
    
    public void setColorArray(final CTColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.COLOR$20);
    }
    
    public void setColorArray(final int n, final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTFontImpl.COLOR$20, n, (short)2);
    }
    
    public CTColor insertNewColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().insert_element_user(CTFontImpl.COLOR$20, n);
        }
    }
    
    public CTColor addNewColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTFontImpl.COLOR$20);
        }
    }
    
    public void removeColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.COLOR$20, n);
        }
    }
    
    public List<CTFontSize> getSzList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SzList extends AbstractList<CTFontSize>
            {
                @Override
                public CTFontSize get(final int n) {
                    return CTFontImpl.this.getSzArray(n);
                }
                
                @Override
                public CTFontSize set(final int n, final CTFontSize ctFontSize) {
                    final CTFontSize szArray = CTFontImpl.this.getSzArray(n);
                    CTFontImpl.this.setSzArray(n, ctFontSize);
                    return szArray;
                }
                
                @Override
                public void add(final int n, final CTFontSize ctFontSize) {
                    CTFontImpl.this.insertNewSz(n).set((XmlObject)ctFontSize);
                }
                
                @Override
                public CTFontSize remove(final int n) {
                    final CTFontSize szArray = CTFontImpl.this.getSzArray(n);
                    CTFontImpl.this.removeSz(n);
                    return szArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfSzArray();
                }
            }
            return new SzList();
        }
    }
    
    @Deprecated
    public CTFontSize[] getSzArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.SZ$22, (List)list);
            final CTFontSize[] array = new CTFontSize[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFontSize getSzArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontSize ctFontSize = (CTFontSize)this.get_store().find_element_user(CTFontImpl.SZ$22, n);
            if (ctFontSize == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFontSize;
        }
    }
    
    public int sizeOfSzArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.SZ$22);
        }
    }
    
    public void setSzArray(final CTFontSize[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.SZ$22);
    }
    
    public void setSzArray(final int n, final CTFontSize ctFontSize) {
        this.generatedSetterHelperImpl((XmlObject)ctFontSize, CTFontImpl.SZ$22, n, (short)2);
    }
    
    public CTFontSize insertNewSz(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontSize)this.get_store().insert_element_user(CTFontImpl.SZ$22, n);
        }
    }
    
    public CTFontSize addNewSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontSize)this.get_store().add_element_user(CTFontImpl.SZ$22);
        }
    }
    
    public void removeSz(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.SZ$22, n);
        }
    }
    
    public List<CTUnderlineProperty> getUList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class UList extends AbstractList<CTUnderlineProperty>
            {
                @Override
                public CTUnderlineProperty get(final int n) {
                    return CTFontImpl.this.getUArray(n);
                }
                
                @Override
                public CTUnderlineProperty set(final int n, final CTUnderlineProperty ctUnderlineProperty) {
                    final CTUnderlineProperty uArray = CTFontImpl.this.getUArray(n);
                    CTFontImpl.this.setUArray(n, ctUnderlineProperty);
                    return uArray;
                }
                
                @Override
                public void add(final int n, final CTUnderlineProperty ctUnderlineProperty) {
                    CTFontImpl.this.insertNewU(n).set((XmlObject)ctUnderlineProperty);
                }
                
                @Override
                public CTUnderlineProperty remove(final int n) {
                    final CTUnderlineProperty uArray = CTFontImpl.this.getUArray(n);
                    CTFontImpl.this.removeU(n);
                    return uArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfUArray();
                }
            }
            return new UList();
        }
    }
    
    @Deprecated
    public CTUnderlineProperty[] getUArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.U$24, (List)list);
            final CTUnderlineProperty[] array = new CTUnderlineProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnderlineProperty getUArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnderlineProperty ctUnderlineProperty = (CTUnderlineProperty)this.get_store().find_element_user(CTFontImpl.U$24, n);
            if (ctUnderlineProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnderlineProperty;
        }
    }
    
    public int sizeOfUArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.U$24);
        }
    }
    
    public void setUArray(final CTUnderlineProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.U$24);
    }
    
    public void setUArray(final int n, final CTUnderlineProperty ctUnderlineProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctUnderlineProperty, CTFontImpl.U$24, n, (short)2);
    }
    
    public CTUnderlineProperty insertNewU(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnderlineProperty)this.get_store().insert_element_user(CTFontImpl.U$24, n);
        }
    }
    
    public CTUnderlineProperty addNewU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnderlineProperty)this.get_store().add_element_user(CTFontImpl.U$24);
        }
    }
    
    public void removeU(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.U$24, n);
        }
    }
    
    public List<CTVerticalAlignFontProperty> getVertAlignList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VertAlignList extends AbstractList<CTVerticalAlignFontProperty>
            {
                @Override
                public CTVerticalAlignFontProperty get(final int n) {
                    return CTFontImpl.this.getVertAlignArray(n);
                }
                
                @Override
                public CTVerticalAlignFontProperty set(final int n, final CTVerticalAlignFontProperty ctVerticalAlignFontProperty) {
                    final CTVerticalAlignFontProperty vertAlignArray = CTFontImpl.this.getVertAlignArray(n);
                    CTFontImpl.this.setVertAlignArray(n, ctVerticalAlignFontProperty);
                    return vertAlignArray;
                }
                
                @Override
                public void add(final int n, final CTVerticalAlignFontProperty ctVerticalAlignFontProperty) {
                    CTFontImpl.this.insertNewVertAlign(n).set((XmlObject)ctVerticalAlignFontProperty);
                }
                
                @Override
                public CTVerticalAlignFontProperty remove(final int n) {
                    final CTVerticalAlignFontProperty vertAlignArray = CTFontImpl.this.getVertAlignArray(n);
                    CTFontImpl.this.removeVertAlign(n);
                    return vertAlignArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfVertAlignArray();
                }
            }
            return new VertAlignList();
        }
    }
    
    @Deprecated
    public CTVerticalAlignFontProperty[] getVertAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.VERTALIGN$26, (List)list);
            final CTVerticalAlignFontProperty[] array = new CTVerticalAlignFontProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTVerticalAlignFontProperty getVertAlignArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVerticalAlignFontProperty ctVerticalAlignFontProperty = (CTVerticalAlignFontProperty)this.get_store().find_element_user(CTFontImpl.VERTALIGN$26, n);
            if (ctVerticalAlignFontProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctVerticalAlignFontProperty;
        }
    }
    
    public int sizeOfVertAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.VERTALIGN$26);
        }
    }
    
    public void setVertAlignArray(final CTVerticalAlignFontProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.VERTALIGN$26);
    }
    
    public void setVertAlignArray(final int n, final CTVerticalAlignFontProperty ctVerticalAlignFontProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctVerticalAlignFontProperty, CTFontImpl.VERTALIGN$26, n, (short)2);
    }
    
    public CTVerticalAlignFontProperty insertNewVertAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalAlignFontProperty)this.get_store().insert_element_user(CTFontImpl.VERTALIGN$26, n);
        }
    }
    
    public CTVerticalAlignFontProperty addNewVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalAlignFontProperty)this.get_store().add_element_user(CTFontImpl.VERTALIGN$26);
        }
    }
    
    public void removeVertAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.VERTALIGN$26, n);
        }
    }
    
    public List<CTFontScheme> getSchemeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SchemeList extends AbstractList<CTFontScheme>
            {
                @Override
                public CTFontScheme get(final int n) {
                    return CTFontImpl.this.getSchemeArray(n);
                }
                
                @Override
                public CTFontScheme set(final int n, final CTFontScheme ctFontScheme) {
                    final CTFontScheme schemeArray = CTFontImpl.this.getSchemeArray(n);
                    CTFontImpl.this.setSchemeArray(n, ctFontScheme);
                    return schemeArray;
                }
                
                @Override
                public void add(final int n, final CTFontScheme ctFontScheme) {
                    CTFontImpl.this.insertNewScheme(n).set((XmlObject)ctFontScheme);
                }
                
                @Override
                public CTFontScheme remove(final int n) {
                    final CTFontScheme schemeArray = CTFontImpl.this.getSchemeArray(n);
                    CTFontImpl.this.removeScheme(n);
                    return schemeArray;
                }
                
                @Override
                public int size() {
                    return CTFontImpl.this.sizeOfSchemeArray();
                }
            }
            return new SchemeList();
        }
    }
    
    @Deprecated
    public CTFontScheme[] getSchemeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFontImpl.SCHEME$28, (List)list);
            final CTFontScheme[] array = new CTFontScheme[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFontScheme getSchemeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontScheme ctFontScheme = (CTFontScheme)this.get_store().find_element_user(CTFontImpl.SCHEME$28, n);
            if (ctFontScheme == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFontScheme;
        }
    }
    
    public int sizeOfSchemeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFontImpl.SCHEME$28);
        }
    }
    
    public void setSchemeArray(final CTFontScheme[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFontImpl.SCHEME$28);
    }
    
    public void setSchemeArray(final int n, final CTFontScheme ctFontScheme) {
        this.generatedSetterHelperImpl((XmlObject)ctFontScheme, CTFontImpl.SCHEME$28, n, (short)2);
    }
    
    public CTFontScheme insertNewScheme(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontScheme)this.get_store().insert_element_user(CTFontImpl.SCHEME$28, n);
        }
    }
    
    public CTFontScheme addNewScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontScheme)this.get_store().add_element_user(CTFontImpl.SCHEME$28);
        }
    }
    
    public void removeScheme(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFontImpl.SCHEME$28, n);
        }
    }
    
    static {
        NAME$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "name");
        CHARSET$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "charset");
        FAMILY$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "family");
        B$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "b");
        I$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "i");
        STRIKE$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "strike");
        OUTLINE$12 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "outline");
        SHADOW$14 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "shadow");
        CONDENSE$16 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "condense");
        EXTEND$18 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "extend");
        COLOR$20 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "color");
        SZ$22 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "sz");
        U$24 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "u");
        VERTALIGN$26 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "vertAlign");
        SCHEME$28 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "scheme");
    }
}
