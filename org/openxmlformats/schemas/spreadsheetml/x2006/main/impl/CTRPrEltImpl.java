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
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRPrElt;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRPrEltImpl extends XmlComplexContentImpl implements CTRPrElt
{
    private static final long serialVersionUID = 1L;
    private static final QName RFONT$0;
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
    
    public CTRPrEltImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFontName> getRFontList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RFontList extends AbstractList<CTFontName>
            {
                @Override
                public CTFontName get(final int n) {
                    return CTRPrEltImpl.this.getRFontArray(n);
                }
                
                @Override
                public CTFontName set(final int n, final CTFontName ctFontName) {
                    final CTFontName rFontArray = CTRPrEltImpl.this.getRFontArray(n);
                    CTRPrEltImpl.this.setRFontArray(n, ctFontName);
                    return rFontArray;
                }
                
                @Override
                public void add(final int n, final CTFontName ctFontName) {
                    CTRPrEltImpl.this.insertNewRFont(n).set((XmlObject)ctFontName);
                }
                
                @Override
                public CTFontName remove(final int n) {
                    final CTFontName rFontArray = CTRPrEltImpl.this.getRFontArray(n);
                    CTRPrEltImpl.this.removeRFont(n);
                    return rFontArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfRFontArray();
                }
            }
            return new RFontList();
        }
    }
    
    @Deprecated
    public CTFontName[] getRFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRPrEltImpl.RFONT$0, (List)list);
            final CTFontName[] array = new CTFontName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFontName getRFontArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontName ctFontName = (CTFontName)this.get_store().find_element_user(CTRPrEltImpl.RFONT$0, n);
            if (ctFontName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFontName;
        }
    }
    
    public int sizeOfRFontArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.RFONT$0);
        }
    }
    
    public void setRFontArray(final CTFontName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.RFONT$0);
    }
    
    public void setRFontArray(final int n, final CTFontName ctFontName) {
        this.generatedSetterHelperImpl((XmlObject)ctFontName, CTRPrEltImpl.RFONT$0, n, (short)2);
    }
    
    public CTFontName insertNewRFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontName)this.get_store().insert_element_user(CTRPrEltImpl.RFONT$0, n);
        }
    }
    
    public CTFontName addNewRFont() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontName)this.get_store().add_element_user(CTRPrEltImpl.RFONT$0);
        }
    }
    
    public void removeRFont(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.RFONT$0, n);
        }
    }
    
    public List<CTIntProperty> getCharsetList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CharsetList extends AbstractList<CTIntProperty>
            {
                @Override
                public CTIntProperty get(final int n) {
                    return CTRPrEltImpl.this.getCharsetArray(n);
                }
                
                @Override
                public CTIntProperty set(final int n, final CTIntProperty ctIntProperty) {
                    final CTIntProperty charsetArray = CTRPrEltImpl.this.getCharsetArray(n);
                    CTRPrEltImpl.this.setCharsetArray(n, ctIntProperty);
                    return charsetArray;
                }
                
                @Override
                public void add(final int n, final CTIntProperty ctIntProperty) {
                    CTRPrEltImpl.this.insertNewCharset(n).set((XmlObject)ctIntProperty);
                }
                
                @Override
                public CTIntProperty remove(final int n) {
                    final CTIntProperty charsetArray = CTRPrEltImpl.this.getCharsetArray(n);
                    CTRPrEltImpl.this.removeCharset(n);
                    return charsetArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfCharsetArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.CHARSET$2, (List)list);
            final CTIntProperty[] array = new CTIntProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTIntProperty getCharsetArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIntProperty ctIntProperty = (CTIntProperty)this.get_store().find_element_user(CTRPrEltImpl.CHARSET$2, n);
            if (ctIntProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctIntProperty;
        }
    }
    
    public int sizeOfCharsetArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.CHARSET$2);
        }
    }
    
    public void setCharsetArray(final CTIntProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.CHARSET$2);
    }
    
    public void setCharsetArray(final int n, final CTIntProperty ctIntProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctIntProperty, CTRPrEltImpl.CHARSET$2, n, (short)2);
    }
    
    public CTIntProperty insertNewCharset(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().insert_element_user(CTRPrEltImpl.CHARSET$2, n);
        }
    }
    
    public CTIntProperty addNewCharset() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().add_element_user(CTRPrEltImpl.CHARSET$2);
        }
    }
    
    public void removeCharset(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.CHARSET$2, n);
        }
    }
    
    public List<CTIntProperty> getFamilyList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class FamilyList extends AbstractList<CTIntProperty>
            {
                @Override
                public CTIntProperty get(final int n) {
                    return CTRPrEltImpl.this.getFamilyArray(n);
                }
                
                @Override
                public CTIntProperty set(final int n, final CTIntProperty ctIntProperty) {
                    final CTIntProperty familyArray = CTRPrEltImpl.this.getFamilyArray(n);
                    CTRPrEltImpl.this.setFamilyArray(n, ctIntProperty);
                    return familyArray;
                }
                
                @Override
                public void add(final int n, final CTIntProperty ctIntProperty) {
                    CTRPrEltImpl.this.insertNewFamily(n).set((XmlObject)ctIntProperty);
                }
                
                @Override
                public CTIntProperty remove(final int n) {
                    final CTIntProperty familyArray = CTRPrEltImpl.this.getFamilyArray(n);
                    CTRPrEltImpl.this.removeFamily(n);
                    return familyArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfFamilyArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.FAMILY$4, (List)list);
            final CTIntProperty[] array = new CTIntProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTIntProperty getFamilyArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTIntProperty ctIntProperty = (CTIntProperty)this.get_store().find_element_user(CTRPrEltImpl.FAMILY$4, n);
            if (ctIntProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctIntProperty;
        }
    }
    
    public int sizeOfFamilyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.FAMILY$4);
        }
    }
    
    public void setFamilyArray(final CTIntProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.FAMILY$4);
    }
    
    public void setFamilyArray(final int n, final CTIntProperty ctIntProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctIntProperty, CTRPrEltImpl.FAMILY$4, n, (short)2);
    }
    
    public CTIntProperty insertNewFamily(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().insert_element_user(CTRPrEltImpl.FAMILY$4, n);
        }
    }
    
    public CTIntProperty addNewFamily() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTIntProperty)this.get_store().add_element_user(CTRPrEltImpl.FAMILY$4);
        }
    }
    
    public void removeFamily(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.FAMILY$4, n);
        }
    }
    
    public List<CTBooleanProperty> getBList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTRPrEltImpl.this.getBArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty bArray = CTRPrEltImpl.this.getBArray(n);
                    CTRPrEltImpl.this.setBArray(n, ctBooleanProperty);
                    return bArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTRPrEltImpl.this.insertNewB(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty bArray = CTRPrEltImpl.this.getBArray(n);
                    CTRPrEltImpl.this.removeB(n);
                    return bArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfBArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.B$6, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getBArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTRPrEltImpl.B$6, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfBArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.B$6);
        }
    }
    
    public void setBArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.B$6);
    }
    
    public void setBArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTRPrEltImpl.B$6, n, (short)2);
    }
    
    public CTBooleanProperty insertNewB(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTRPrEltImpl.B$6, n);
        }
    }
    
    public CTBooleanProperty addNewB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTRPrEltImpl.B$6);
        }
    }
    
    public void removeB(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.B$6, n);
        }
    }
    
    public List<CTBooleanProperty> getIList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class IList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTRPrEltImpl.this.getIArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty iArray = CTRPrEltImpl.this.getIArray(n);
                    CTRPrEltImpl.this.setIArray(n, ctBooleanProperty);
                    return iArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTRPrEltImpl.this.insertNewI(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty iArray = CTRPrEltImpl.this.getIArray(n);
                    CTRPrEltImpl.this.removeI(n);
                    return iArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfIArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.I$8, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getIArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTRPrEltImpl.I$8, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfIArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.I$8);
        }
    }
    
    public void setIArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.I$8);
    }
    
    public void setIArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTRPrEltImpl.I$8, n, (short)2);
    }
    
    public CTBooleanProperty insertNewI(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTRPrEltImpl.I$8, n);
        }
    }
    
    public CTBooleanProperty addNewI() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTRPrEltImpl.I$8);
        }
    }
    
    public void removeI(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.I$8, n);
        }
    }
    
    public List<CTBooleanProperty> getStrikeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StrikeList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTRPrEltImpl.this.getStrikeArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty strikeArray = CTRPrEltImpl.this.getStrikeArray(n);
                    CTRPrEltImpl.this.setStrikeArray(n, ctBooleanProperty);
                    return strikeArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTRPrEltImpl.this.insertNewStrike(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty strikeArray = CTRPrEltImpl.this.getStrikeArray(n);
                    CTRPrEltImpl.this.removeStrike(n);
                    return strikeArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfStrikeArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.STRIKE$10, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getStrikeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTRPrEltImpl.STRIKE$10, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfStrikeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.STRIKE$10);
        }
    }
    
    public void setStrikeArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.STRIKE$10);
    }
    
    public void setStrikeArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTRPrEltImpl.STRIKE$10, n, (short)2);
    }
    
    public CTBooleanProperty insertNewStrike(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTRPrEltImpl.STRIKE$10, n);
        }
    }
    
    public CTBooleanProperty addNewStrike() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTRPrEltImpl.STRIKE$10);
        }
    }
    
    public void removeStrike(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.STRIKE$10, n);
        }
    }
    
    public List<CTBooleanProperty> getOutlineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class OutlineList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTRPrEltImpl.this.getOutlineArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty outlineArray = CTRPrEltImpl.this.getOutlineArray(n);
                    CTRPrEltImpl.this.setOutlineArray(n, ctBooleanProperty);
                    return outlineArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTRPrEltImpl.this.insertNewOutline(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty outlineArray = CTRPrEltImpl.this.getOutlineArray(n);
                    CTRPrEltImpl.this.removeOutline(n);
                    return outlineArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfOutlineArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.OUTLINE$12, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getOutlineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTRPrEltImpl.OUTLINE$12, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfOutlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.OUTLINE$12);
        }
    }
    
    public void setOutlineArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.OUTLINE$12);
    }
    
    public void setOutlineArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTRPrEltImpl.OUTLINE$12, n, (short)2);
    }
    
    public CTBooleanProperty insertNewOutline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTRPrEltImpl.OUTLINE$12, n);
        }
    }
    
    public CTBooleanProperty addNewOutline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTRPrEltImpl.OUTLINE$12);
        }
    }
    
    public void removeOutline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.OUTLINE$12, n);
        }
    }
    
    public List<CTBooleanProperty> getShadowList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShadowList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTRPrEltImpl.this.getShadowArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty shadowArray = CTRPrEltImpl.this.getShadowArray(n);
                    CTRPrEltImpl.this.setShadowArray(n, ctBooleanProperty);
                    return shadowArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTRPrEltImpl.this.insertNewShadow(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty shadowArray = CTRPrEltImpl.this.getShadowArray(n);
                    CTRPrEltImpl.this.removeShadow(n);
                    return shadowArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfShadowArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.SHADOW$14, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getShadowArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTRPrEltImpl.SHADOW$14, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfShadowArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.SHADOW$14);
        }
    }
    
    public void setShadowArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.SHADOW$14);
    }
    
    public void setShadowArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTRPrEltImpl.SHADOW$14, n, (short)2);
    }
    
    public CTBooleanProperty insertNewShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTRPrEltImpl.SHADOW$14, n);
        }
    }
    
    public CTBooleanProperty addNewShadow() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTRPrEltImpl.SHADOW$14);
        }
    }
    
    public void removeShadow(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.SHADOW$14, n);
        }
    }
    
    public List<CTBooleanProperty> getCondenseList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CondenseList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTRPrEltImpl.this.getCondenseArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty condenseArray = CTRPrEltImpl.this.getCondenseArray(n);
                    CTRPrEltImpl.this.setCondenseArray(n, ctBooleanProperty);
                    return condenseArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTRPrEltImpl.this.insertNewCondense(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty condenseArray = CTRPrEltImpl.this.getCondenseArray(n);
                    CTRPrEltImpl.this.removeCondense(n);
                    return condenseArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfCondenseArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.CONDENSE$16, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getCondenseArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTRPrEltImpl.CONDENSE$16, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfCondenseArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.CONDENSE$16);
        }
    }
    
    public void setCondenseArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.CONDENSE$16);
    }
    
    public void setCondenseArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTRPrEltImpl.CONDENSE$16, n, (short)2);
    }
    
    public CTBooleanProperty insertNewCondense(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTRPrEltImpl.CONDENSE$16, n);
        }
    }
    
    public CTBooleanProperty addNewCondense() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTRPrEltImpl.CONDENSE$16);
        }
    }
    
    public void removeCondense(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.CONDENSE$16, n);
        }
    }
    
    public List<CTBooleanProperty> getExtendList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExtendList extends AbstractList<CTBooleanProperty>
            {
                @Override
                public CTBooleanProperty get(final int n) {
                    return CTRPrEltImpl.this.getExtendArray(n);
                }
                
                @Override
                public CTBooleanProperty set(final int n, final CTBooleanProperty ctBooleanProperty) {
                    final CTBooleanProperty extendArray = CTRPrEltImpl.this.getExtendArray(n);
                    CTRPrEltImpl.this.setExtendArray(n, ctBooleanProperty);
                    return extendArray;
                }
                
                @Override
                public void add(final int n, final CTBooleanProperty ctBooleanProperty) {
                    CTRPrEltImpl.this.insertNewExtend(n).set((XmlObject)ctBooleanProperty);
                }
                
                @Override
                public CTBooleanProperty remove(final int n) {
                    final CTBooleanProperty extendArray = CTRPrEltImpl.this.getExtendArray(n);
                    CTRPrEltImpl.this.removeExtend(n);
                    return extendArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfExtendArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.EXTEND$18, (List)list);
            final CTBooleanProperty[] array = new CTBooleanProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBooleanProperty getExtendArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBooleanProperty ctBooleanProperty = (CTBooleanProperty)this.get_store().find_element_user(CTRPrEltImpl.EXTEND$18, n);
            if (ctBooleanProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBooleanProperty;
        }
    }
    
    public int sizeOfExtendArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.EXTEND$18);
        }
    }
    
    public void setExtendArray(final CTBooleanProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.EXTEND$18);
    }
    
    public void setExtendArray(final int n, final CTBooleanProperty ctBooleanProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctBooleanProperty, CTRPrEltImpl.EXTEND$18, n, (short)2);
    }
    
    public CTBooleanProperty insertNewExtend(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().insert_element_user(CTRPrEltImpl.EXTEND$18, n);
        }
    }
    
    public CTBooleanProperty addNewExtend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBooleanProperty)this.get_store().add_element_user(CTRPrEltImpl.EXTEND$18);
        }
    }
    
    public void removeExtend(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.EXTEND$18, n);
        }
    }
    
    public List<CTColor> getColorList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ColorList extends AbstractList<CTColor>
            {
                @Override
                public CTColor get(final int n) {
                    return CTRPrEltImpl.this.getColorArray(n);
                }
                
                @Override
                public CTColor set(final int n, final CTColor ctColor) {
                    final CTColor colorArray = CTRPrEltImpl.this.getColorArray(n);
                    CTRPrEltImpl.this.setColorArray(n, ctColor);
                    return colorArray;
                }
                
                @Override
                public void add(final int n, final CTColor ctColor) {
                    CTRPrEltImpl.this.insertNewColor(n).set((XmlObject)ctColor);
                }
                
                @Override
                public CTColor remove(final int n) {
                    final CTColor colorArray = CTRPrEltImpl.this.getColorArray(n);
                    CTRPrEltImpl.this.removeColor(n);
                    return colorArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfColorArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.COLOR$20, (List)list);
            final CTColor[] array = new CTColor[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTColor getColorArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColor ctColor = (CTColor)this.get_store().find_element_user(CTRPrEltImpl.COLOR$20, n);
            if (ctColor == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctColor;
        }
    }
    
    public int sizeOfColorArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.COLOR$20);
        }
    }
    
    public void setColorArray(final CTColor[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.COLOR$20);
    }
    
    public void setColorArray(final int n, final CTColor ctColor) {
        this.generatedSetterHelperImpl((XmlObject)ctColor, CTRPrEltImpl.COLOR$20, n, (short)2);
    }
    
    public CTColor insertNewColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().insert_element_user(CTRPrEltImpl.COLOR$20, n);
        }
    }
    
    public CTColor addNewColor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColor)this.get_store().add_element_user(CTRPrEltImpl.COLOR$20);
        }
    }
    
    public void removeColor(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.COLOR$20, n);
        }
    }
    
    public List<CTFontSize> getSzList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SzList extends AbstractList<CTFontSize>
            {
                @Override
                public CTFontSize get(final int n) {
                    return CTRPrEltImpl.this.getSzArray(n);
                }
                
                @Override
                public CTFontSize set(final int n, final CTFontSize ctFontSize) {
                    final CTFontSize szArray = CTRPrEltImpl.this.getSzArray(n);
                    CTRPrEltImpl.this.setSzArray(n, ctFontSize);
                    return szArray;
                }
                
                @Override
                public void add(final int n, final CTFontSize ctFontSize) {
                    CTRPrEltImpl.this.insertNewSz(n).set((XmlObject)ctFontSize);
                }
                
                @Override
                public CTFontSize remove(final int n) {
                    final CTFontSize szArray = CTRPrEltImpl.this.getSzArray(n);
                    CTRPrEltImpl.this.removeSz(n);
                    return szArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfSzArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.SZ$22, (List)list);
            final CTFontSize[] array = new CTFontSize[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFontSize getSzArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontSize ctFontSize = (CTFontSize)this.get_store().find_element_user(CTRPrEltImpl.SZ$22, n);
            if (ctFontSize == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFontSize;
        }
    }
    
    public int sizeOfSzArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.SZ$22);
        }
    }
    
    public void setSzArray(final CTFontSize[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.SZ$22);
    }
    
    public void setSzArray(final int n, final CTFontSize ctFontSize) {
        this.generatedSetterHelperImpl((XmlObject)ctFontSize, CTRPrEltImpl.SZ$22, n, (short)2);
    }
    
    public CTFontSize insertNewSz(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontSize)this.get_store().insert_element_user(CTRPrEltImpl.SZ$22, n);
        }
    }
    
    public CTFontSize addNewSz() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontSize)this.get_store().add_element_user(CTRPrEltImpl.SZ$22);
        }
    }
    
    public void removeSz(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.SZ$22, n);
        }
    }
    
    public List<CTUnderlineProperty> getUList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class UList extends AbstractList<CTUnderlineProperty>
            {
                @Override
                public CTUnderlineProperty get(final int n) {
                    return CTRPrEltImpl.this.getUArray(n);
                }
                
                @Override
                public CTUnderlineProperty set(final int n, final CTUnderlineProperty ctUnderlineProperty) {
                    final CTUnderlineProperty uArray = CTRPrEltImpl.this.getUArray(n);
                    CTRPrEltImpl.this.setUArray(n, ctUnderlineProperty);
                    return uArray;
                }
                
                @Override
                public void add(final int n, final CTUnderlineProperty ctUnderlineProperty) {
                    CTRPrEltImpl.this.insertNewU(n).set((XmlObject)ctUnderlineProperty);
                }
                
                @Override
                public CTUnderlineProperty remove(final int n) {
                    final CTUnderlineProperty uArray = CTRPrEltImpl.this.getUArray(n);
                    CTRPrEltImpl.this.removeU(n);
                    return uArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfUArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.U$24, (List)list);
            final CTUnderlineProperty[] array = new CTUnderlineProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnderlineProperty getUArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnderlineProperty ctUnderlineProperty = (CTUnderlineProperty)this.get_store().find_element_user(CTRPrEltImpl.U$24, n);
            if (ctUnderlineProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnderlineProperty;
        }
    }
    
    public int sizeOfUArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.U$24);
        }
    }
    
    public void setUArray(final CTUnderlineProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.U$24);
    }
    
    public void setUArray(final int n, final CTUnderlineProperty ctUnderlineProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctUnderlineProperty, CTRPrEltImpl.U$24, n, (short)2);
    }
    
    public CTUnderlineProperty insertNewU(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnderlineProperty)this.get_store().insert_element_user(CTRPrEltImpl.U$24, n);
        }
    }
    
    public CTUnderlineProperty addNewU() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnderlineProperty)this.get_store().add_element_user(CTRPrEltImpl.U$24);
        }
    }
    
    public void removeU(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.U$24, n);
        }
    }
    
    public List<CTVerticalAlignFontProperty> getVertAlignList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class VertAlignList extends AbstractList<CTVerticalAlignFontProperty>
            {
                @Override
                public CTVerticalAlignFontProperty get(final int n) {
                    return CTRPrEltImpl.this.getVertAlignArray(n);
                }
                
                @Override
                public CTVerticalAlignFontProperty set(final int n, final CTVerticalAlignFontProperty ctVerticalAlignFontProperty) {
                    final CTVerticalAlignFontProperty vertAlignArray = CTRPrEltImpl.this.getVertAlignArray(n);
                    CTRPrEltImpl.this.setVertAlignArray(n, ctVerticalAlignFontProperty);
                    return vertAlignArray;
                }
                
                @Override
                public void add(final int n, final CTVerticalAlignFontProperty ctVerticalAlignFontProperty) {
                    CTRPrEltImpl.this.insertNewVertAlign(n).set((XmlObject)ctVerticalAlignFontProperty);
                }
                
                @Override
                public CTVerticalAlignFontProperty remove(final int n) {
                    final CTVerticalAlignFontProperty vertAlignArray = CTRPrEltImpl.this.getVertAlignArray(n);
                    CTRPrEltImpl.this.removeVertAlign(n);
                    return vertAlignArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfVertAlignArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.VERTALIGN$26, (List)list);
            final CTVerticalAlignFontProperty[] array = new CTVerticalAlignFontProperty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTVerticalAlignFontProperty getVertAlignArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVerticalAlignFontProperty ctVerticalAlignFontProperty = (CTVerticalAlignFontProperty)this.get_store().find_element_user(CTRPrEltImpl.VERTALIGN$26, n);
            if (ctVerticalAlignFontProperty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctVerticalAlignFontProperty;
        }
    }
    
    public int sizeOfVertAlignArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.VERTALIGN$26);
        }
    }
    
    public void setVertAlignArray(final CTVerticalAlignFontProperty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.VERTALIGN$26);
    }
    
    public void setVertAlignArray(final int n, final CTVerticalAlignFontProperty ctVerticalAlignFontProperty) {
        this.generatedSetterHelperImpl((XmlObject)ctVerticalAlignFontProperty, CTRPrEltImpl.VERTALIGN$26, n, (short)2);
    }
    
    public CTVerticalAlignFontProperty insertNewVertAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalAlignFontProperty)this.get_store().insert_element_user(CTRPrEltImpl.VERTALIGN$26, n);
        }
    }
    
    public CTVerticalAlignFontProperty addNewVertAlign() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVerticalAlignFontProperty)this.get_store().add_element_user(CTRPrEltImpl.VERTALIGN$26);
        }
    }
    
    public void removeVertAlign(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.VERTALIGN$26, n);
        }
    }
    
    public List<CTFontScheme> getSchemeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SchemeList extends AbstractList<CTFontScheme>
            {
                @Override
                public CTFontScheme get(final int n) {
                    return CTRPrEltImpl.this.getSchemeArray(n);
                }
                
                @Override
                public CTFontScheme set(final int n, final CTFontScheme ctFontScheme) {
                    final CTFontScheme schemeArray = CTRPrEltImpl.this.getSchemeArray(n);
                    CTRPrEltImpl.this.setSchemeArray(n, ctFontScheme);
                    return schemeArray;
                }
                
                @Override
                public void add(final int n, final CTFontScheme ctFontScheme) {
                    CTRPrEltImpl.this.insertNewScheme(n).set((XmlObject)ctFontScheme);
                }
                
                @Override
                public CTFontScheme remove(final int n) {
                    final CTFontScheme schemeArray = CTRPrEltImpl.this.getSchemeArray(n);
                    CTRPrEltImpl.this.removeScheme(n);
                    return schemeArray;
                }
                
                @Override
                public int size() {
                    return CTRPrEltImpl.this.sizeOfSchemeArray();
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
            this.get_store().find_all_element_users(CTRPrEltImpl.SCHEME$28, (List)list);
            final CTFontScheme[] array = new CTFontScheme[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFontScheme getSchemeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFontScheme ctFontScheme = (CTFontScheme)this.get_store().find_element_user(CTRPrEltImpl.SCHEME$28, n);
            if (ctFontScheme == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctFontScheme;
        }
    }
    
    public int sizeOfSchemeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRPrEltImpl.SCHEME$28);
        }
    }
    
    public void setSchemeArray(final CTFontScheme[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRPrEltImpl.SCHEME$28);
    }
    
    public void setSchemeArray(final int n, final CTFontScheme ctFontScheme) {
        this.generatedSetterHelperImpl((XmlObject)ctFontScheme, CTRPrEltImpl.SCHEME$28, n, (short)2);
    }
    
    public CTFontScheme insertNewScheme(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontScheme)this.get_store().insert_element_user(CTRPrEltImpl.SCHEME$28, n);
        }
    }
    
    public CTFontScheme addNewScheme() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFontScheme)this.get_store().add_element_user(CTRPrEltImpl.SCHEME$28);
        }
    }
    
    public void removeScheme(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRPrEltImpl.SCHEME$28, n);
        }
    }
    
    static {
        RFONT$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "rFont");
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
