package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFTextInput;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFDDList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFCheckBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFStatusText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFHelpText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMacroName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFName;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFFData;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTFFDataImpl extends XmlComplexContentImpl implements CTFFData
{
    private static final long serialVersionUID = 1L;
    private static final QName NAME$0;
    private static final QName ENABLED$2;
    private static final QName CALCONEXIT$4;
    private static final QName ENTRYMACRO$6;
    private static final QName EXITMACRO$8;
    private static final QName HELPTEXT$10;
    private static final QName STATUSTEXT$12;
    private static final QName CHECKBOX$14;
    private static final QName DDLIST$16;
    private static final QName TEXTINPUT$18;
    
    public CTFFDataImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTFFName> getNameList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NameList extends AbstractList<CTFFName>
            {
                @Override
                public CTFFName get(final int n) {
                    return CTFFDataImpl.this.getNameArray(n);
                }
                
                @Override
                public CTFFName set(final int n, final CTFFName ctffName) {
                    final CTFFName nameArray = CTFFDataImpl.this.getNameArray(n);
                    CTFFDataImpl.this.setNameArray(n, ctffName);
                    return nameArray;
                }
                
                @Override
                public void add(final int n, final CTFFName ctffName) {
                    CTFFDataImpl.this.insertNewName(n).set((XmlObject)ctffName);
                }
                
                @Override
                public CTFFName remove(final int n) {
                    final CTFFName nameArray = CTFFDataImpl.this.getNameArray(n);
                    CTFFDataImpl.this.removeName(n);
                    return nameArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfNameArray();
                }
            }
            return new NameList();
        }
    }
    
    @Deprecated
    public CTFFName[] getNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.NAME$0, (List)list);
            final CTFFName[] array = new CTFFName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFFName getNameArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFFName ctffName = (CTFFName)this.get_store().find_element_user(CTFFDataImpl.NAME$0, n);
            if (ctffName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctffName;
        }
    }
    
    public int sizeOfNameArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.NAME$0);
        }
    }
    
    public void setNameArray(final CTFFName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.NAME$0);
    }
    
    public void setNameArray(final int n, final CTFFName ctffName) {
        this.generatedSetterHelperImpl((XmlObject)ctffName, CTFFDataImpl.NAME$0, n, (short)2);
    }
    
    public CTFFName insertNewName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFName)this.get_store().insert_element_user(CTFFDataImpl.NAME$0, n);
        }
    }
    
    public CTFFName addNewName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFName)this.get_store().add_element_user(CTFFDataImpl.NAME$0);
        }
    }
    
    public void removeName(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.NAME$0, n);
        }
    }
    
    public List<CTOnOff> getEnabledList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EnabledList extends AbstractList<CTOnOff>
            {
                @Override
                public CTOnOff get(final int n) {
                    return CTFFDataImpl.this.getEnabledArray(n);
                }
                
                @Override
                public CTOnOff set(final int n, final CTOnOff ctOnOff) {
                    final CTOnOff enabledArray = CTFFDataImpl.this.getEnabledArray(n);
                    CTFFDataImpl.this.setEnabledArray(n, ctOnOff);
                    return enabledArray;
                }
                
                @Override
                public void add(final int n, final CTOnOff ctOnOff) {
                    CTFFDataImpl.this.insertNewEnabled(n).set((XmlObject)ctOnOff);
                }
                
                @Override
                public CTOnOff remove(final int n) {
                    final CTOnOff enabledArray = CTFFDataImpl.this.getEnabledArray(n);
                    CTFFDataImpl.this.removeEnabled(n);
                    return enabledArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfEnabledArray();
                }
            }
            return new EnabledList();
        }
    }
    
    @Deprecated
    public CTOnOff[] getEnabledArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.ENABLED$2, (List)list);
            final CTOnOff[] array = new CTOnOff[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOnOff getEnabledArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTFFDataImpl.ENABLED$2, n);
            if (ctOnOff == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOnOff;
        }
    }
    
    public int sizeOfEnabledArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.ENABLED$2);
        }
    }
    
    public void setEnabledArray(final CTOnOff[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.ENABLED$2);
    }
    
    public void setEnabledArray(final int n, final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTFFDataImpl.ENABLED$2, n, (short)2);
    }
    
    public CTOnOff insertNewEnabled(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().insert_element_user(CTFFDataImpl.ENABLED$2, n);
        }
    }
    
    public CTOnOff addNewEnabled() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTFFDataImpl.ENABLED$2);
        }
    }
    
    public void removeEnabled(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.ENABLED$2, n);
        }
    }
    
    public List<CTOnOff> getCalcOnExitList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CalcOnExitList extends AbstractList<CTOnOff>
            {
                @Override
                public CTOnOff get(final int n) {
                    return CTFFDataImpl.this.getCalcOnExitArray(n);
                }
                
                @Override
                public CTOnOff set(final int n, final CTOnOff ctOnOff) {
                    final CTOnOff calcOnExitArray = CTFFDataImpl.this.getCalcOnExitArray(n);
                    CTFFDataImpl.this.setCalcOnExitArray(n, ctOnOff);
                    return calcOnExitArray;
                }
                
                @Override
                public void add(final int n, final CTOnOff ctOnOff) {
                    CTFFDataImpl.this.insertNewCalcOnExit(n).set((XmlObject)ctOnOff);
                }
                
                @Override
                public CTOnOff remove(final int n) {
                    final CTOnOff calcOnExitArray = CTFFDataImpl.this.getCalcOnExitArray(n);
                    CTFFDataImpl.this.removeCalcOnExit(n);
                    return calcOnExitArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfCalcOnExitArray();
                }
            }
            return new CalcOnExitList();
        }
    }
    
    @Deprecated
    public CTOnOff[] getCalcOnExitArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.CALCONEXIT$4, (List)list);
            final CTOnOff[] array = new CTOnOff[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOnOff getCalcOnExitArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTFFDataImpl.CALCONEXIT$4, n);
            if (ctOnOff == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOnOff;
        }
    }
    
    public int sizeOfCalcOnExitArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.CALCONEXIT$4);
        }
    }
    
    public void setCalcOnExitArray(final CTOnOff[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.CALCONEXIT$4);
    }
    
    public void setCalcOnExitArray(final int n, final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTFFDataImpl.CALCONEXIT$4, n, (short)2);
    }
    
    public CTOnOff insertNewCalcOnExit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().insert_element_user(CTFFDataImpl.CALCONEXIT$4, n);
        }
    }
    
    public CTOnOff addNewCalcOnExit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTFFDataImpl.CALCONEXIT$4);
        }
    }
    
    public void removeCalcOnExit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.CALCONEXIT$4, n);
        }
    }
    
    public List<CTMacroName> getEntryMacroList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EntryMacroList extends AbstractList<CTMacroName>
            {
                @Override
                public CTMacroName get(final int n) {
                    return CTFFDataImpl.this.getEntryMacroArray(n);
                }
                
                @Override
                public CTMacroName set(final int n, final CTMacroName ctMacroName) {
                    final CTMacroName entryMacroArray = CTFFDataImpl.this.getEntryMacroArray(n);
                    CTFFDataImpl.this.setEntryMacroArray(n, ctMacroName);
                    return entryMacroArray;
                }
                
                @Override
                public void add(final int n, final CTMacroName ctMacroName) {
                    CTFFDataImpl.this.insertNewEntryMacro(n).set((XmlObject)ctMacroName);
                }
                
                @Override
                public CTMacroName remove(final int n) {
                    final CTMacroName entryMacroArray = CTFFDataImpl.this.getEntryMacroArray(n);
                    CTFFDataImpl.this.removeEntryMacro(n);
                    return entryMacroArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfEntryMacroArray();
                }
            }
            return new EntryMacroList();
        }
    }
    
    @Deprecated
    public CTMacroName[] getEntryMacroArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.ENTRYMACRO$6, (List)list);
            final CTMacroName[] array = new CTMacroName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMacroName getEntryMacroArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMacroName ctMacroName = (CTMacroName)this.get_store().find_element_user(CTFFDataImpl.ENTRYMACRO$6, n);
            if (ctMacroName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMacroName;
        }
    }
    
    public int sizeOfEntryMacroArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.ENTRYMACRO$6);
        }
    }
    
    public void setEntryMacroArray(final CTMacroName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.ENTRYMACRO$6);
    }
    
    public void setEntryMacroArray(final int n, final CTMacroName ctMacroName) {
        this.generatedSetterHelperImpl((XmlObject)ctMacroName, CTFFDataImpl.ENTRYMACRO$6, n, (short)2);
    }
    
    public CTMacroName insertNewEntryMacro(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMacroName)this.get_store().insert_element_user(CTFFDataImpl.ENTRYMACRO$6, n);
        }
    }
    
    public CTMacroName addNewEntryMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMacroName)this.get_store().add_element_user(CTFFDataImpl.ENTRYMACRO$6);
        }
    }
    
    public void removeEntryMacro(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.ENTRYMACRO$6, n);
        }
    }
    
    public List<CTMacroName> getExitMacroList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ExitMacroList extends AbstractList<CTMacroName>
            {
                @Override
                public CTMacroName get(final int n) {
                    return CTFFDataImpl.this.getExitMacroArray(n);
                }
                
                @Override
                public CTMacroName set(final int n, final CTMacroName ctMacroName) {
                    final CTMacroName exitMacroArray = CTFFDataImpl.this.getExitMacroArray(n);
                    CTFFDataImpl.this.setExitMacroArray(n, ctMacroName);
                    return exitMacroArray;
                }
                
                @Override
                public void add(final int n, final CTMacroName ctMacroName) {
                    CTFFDataImpl.this.insertNewExitMacro(n).set((XmlObject)ctMacroName);
                }
                
                @Override
                public CTMacroName remove(final int n) {
                    final CTMacroName exitMacroArray = CTFFDataImpl.this.getExitMacroArray(n);
                    CTFFDataImpl.this.removeExitMacro(n);
                    return exitMacroArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfExitMacroArray();
                }
            }
            return new ExitMacroList();
        }
    }
    
    @Deprecated
    public CTMacroName[] getExitMacroArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.EXITMACRO$8, (List)list);
            final CTMacroName[] array = new CTMacroName[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMacroName getExitMacroArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMacroName ctMacroName = (CTMacroName)this.get_store().find_element_user(CTFFDataImpl.EXITMACRO$8, n);
            if (ctMacroName == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMacroName;
        }
    }
    
    public int sizeOfExitMacroArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.EXITMACRO$8);
        }
    }
    
    public void setExitMacroArray(final CTMacroName[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.EXITMACRO$8);
    }
    
    public void setExitMacroArray(final int n, final CTMacroName ctMacroName) {
        this.generatedSetterHelperImpl((XmlObject)ctMacroName, CTFFDataImpl.EXITMACRO$8, n, (short)2);
    }
    
    public CTMacroName insertNewExitMacro(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMacroName)this.get_store().insert_element_user(CTFFDataImpl.EXITMACRO$8, n);
        }
    }
    
    public CTMacroName addNewExitMacro() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMacroName)this.get_store().add_element_user(CTFFDataImpl.EXITMACRO$8);
        }
    }
    
    public void removeExitMacro(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.EXITMACRO$8, n);
        }
    }
    
    public List<CTFFHelpText> getHelpTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HelpTextList extends AbstractList<CTFFHelpText>
            {
                @Override
                public CTFFHelpText get(final int n) {
                    return CTFFDataImpl.this.getHelpTextArray(n);
                }
                
                @Override
                public CTFFHelpText set(final int n, final CTFFHelpText ctffHelpText) {
                    final CTFFHelpText helpTextArray = CTFFDataImpl.this.getHelpTextArray(n);
                    CTFFDataImpl.this.setHelpTextArray(n, ctffHelpText);
                    return helpTextArray;
                }
                
                @Override
                public void add(final int n, final CTFFHelpText ctffHelpText) {
                    CTFFDataImpl.this.insertNewHelpText(n).set((XmlObject)ctffHelpText);
                }
                
                @Override
                public CTFFHelpText remove(final int n) {
                    final CTFFHelpText helpTextArray = CTFFDataImpl.this.getHelpTextArray(n);
                    CTFFDataImpl.this.removeHelpText(n);
                    return helpTextArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfHelpTextArray();
                }
            }
            return new HelpTextList();
        }
    }
    
    @Deprecated
    public CTFFHelpText[] getHelpTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.HELPTEXT$10, (List)list);
            final CTFFHelpText[] array = new CTFFHelpText[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFFHelpText getHelpTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFFHelpText ctffHelpText = (CTFFHelpText)this.get_store().find_element_user(CTFFDataImpl.HELPTEXT$10, n);
            if (ctffHelpText == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctffHelpText;
        }
    }
    
    public int sizeOfHelpTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.HELPTEXT$10);
        }
    }
    
    public void setHelpTextArray(final CTFFHelpText[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.HELPTEXT$10);
    }
    
    public void setHelpTextArray(final int n, final CTFFHelpText ctffHelpText) {
        this.generatedSetterHelperImpl((XmlObject)ctffHelpText, CTFFDataImpl.HELPTEXT$10, n, (short)2);
    }
    
    public CTFFHelpText insertNewHelpText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFHelpText)this.get_store().insert_element_user(CTFFDataImpl.HELPTEXT$10, n);
        }
    }
    
    public CTFFHelpText addNewHelpText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFHelpText)this.get_store().add_element_user(CTFFDataImpl.HELPTEXT$10);
        }
    }
    
    public void removeHelpText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.HELPTEXT$10, n);
        }
    }
    
    public List<CTFFStatusText> getStatusTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class StatusTextList extends AbstractList<CTFFStatusText>
            {
                @Override
                public CTFFStatusText get(final int n) {
                    return CTFFDataImpl.this.getStatusTextArray(n);
                }
                
                @Override
                public CTFFStatusText set(final int n, final CTFFStatusText ctffStatusText) {
                    final CTFFStatusText statusTextArray = CTFFDataImpl.this.getStatusTextArray(n);
                    CTFFDataImpl.this.setStatusTextArray(n, ctffStatusText);
                    return statusTextArray;
                }
                
                @Override
                public void add(final int n, final CTFFStatusText ctffStatusText) {
                    CTFFDataImpl.this.insertNewStatusText(n).set((XmlObject)ctffStatusText);
                }
                
                @Override
                public CTFFStatusText remove(final int n) {
                    final CTFFStatusText statusTextArray = CTFFDataImpl.this.getStatusTextArray(n);
                    CTFFDataImpl.this.removeStatusText(n);
                    return statusTextArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfStatusTextArray();
                }
            }
            return new StatusTextList();
        }
    }
    
    @Deprecated
    public CTFFStatusText[] getStatusTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.STATUSTEXT$12, (List)list);
            final CTFFStatusText[] array = new CTFFStatusText[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFFStatusText getStatusTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFFStatusText ctffStatusText = (CTFFStatusText)this.get_store().find_element_user(CTFFDataImpl.STATUSTEXT$12, n);
            if (ctffStatusText == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctffStatusText;
        }
    }
    
    public int sizeOfStatusTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.STATUSTEXT$12);
        }
    }
    
    public void setStatusTextArray(final CTFFStatusText[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.STATUSTEXT$12);
    }
    
    public void setStatusTextArray(final int n, final CTFFStatusText ctffStatusText) {
        this.generatedSetterHelperImpl((XmlObject)ctffStatusText, CTFFDataImpl.STATUSTEXT$12, n, (short)2);
    }
    
    public CTFFStatusText insertNewStatusText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFStatusText)this.get_store().insert_element_user(CTFFDataImpl.STATUSTEXT$12, n);
        }
    }
    
    public CTFFStatusText addNewStatusText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFStatusText)this.get_store().add_element_user(CTFFDataImpl.STATUSTEXT$12);
        }
    }
    
    public void removeStatusText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.STATUSTEXT$12, n);
        }
    }
    
    public List<CTFFCheckBox> getCheckBoxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CheckBoxList extends AbstractList<CTFFCheckBox>
            {
                @Override
                public CTFFCheckBox get(final int n) {
                    return CTFFDataImpl.this.getCheckBoxArray(n);
                }
                
                @Override
                public CTFFCheckBox set(final int n, final CTFFCheckBox ctffCheckBox) {
                    final CTFFCheckBox checkBoxArray = CTFFDataImpl.this.getCheckBoxArray(n);
                    CTFFDataImpl.this.setCheckBoxArray(n, ctffCheckBox);
                    return checkBoxArray;
                }
                
                @Override
                public void add(final int n, final CTFFCheckBox ctffCheckBox) {
                    CTFFDataImpl.this.insertNewCheckBox(n).set((XmlObject)ctffCheckBox);
                }
                
                @Override
                public CTFFCheckBox remove(final int n) {
                    final CTFFCheckBox checkBoxArray = CTFFDataImpl.this.getCheckBoxArray(n);
                    CTFFDataImpl.this.removeCheckBox(n);
                    return checkBoxArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfCheckBoxArray();
                }
            }
            return new CheckBoxList();
        }
    }
    
    @Deprecated
    public CTFFCheckBox[] getCheckBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.CHECKBOX$14, (List)list);
            final CTFFCheckBox[] array = new CTFFCheckBox[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFFCheckBox getCheckBoxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFFCheckBox ctffCheckBox = (CTFFCheckBox)this.get_store().find_element_user(CTFFDataImpl.CHECKBOX$14, n);
            if (ctffCheckBox == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctffCheckBox;
        }
    }
    
    public int sizeOfCheckBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.CHECKBOX$14);
        }
    }
    
    public void setCheckBoxArray(final CTFFCheckBox[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.CHECKBOX$14);
    }
    
    public void setCheckBoxArray(final int n, final CTFFCheckBox ctffCheckBox) {
        this.generatedSetterHelperImpl((XmlObject)ctffCheckBox, CTFFDataImpl.CHECKBOX$14, n, (short)2);
    }
    
    public CTFFCheckBox insertNewCheckBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFCheckBox)this.get_store().insert_element_user(CTFFDataImpl.CHECKBOX$14, n);
        }
    }
    
    public CTFFCheckBox addNewCheckBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFCheckBox)this.get_store().add_element_user(CTFFDataImpl.CHECKBOX$14);
        }
    }
    
    public void removeCheckBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.CHECKBOX$14, n);
        }
    }
    
    public List<CTFFDDList> getDdListList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DdListList extends AbstractList<CTFFDDList>
            {
                @Override
                public CTFFDDList get(final int n) {
                    return CTFFDataImpl.this.getDdListArray(n);
                }
                
                @Override
                public CTFFDDList set(final int n, final CTFFDDList list) {
                    final CTFFDDList ddListArray = CTFFDataImpl.this.getDdListArray(n);
                    CTFFDataImpl.this.setDdListArray(n, list);
                    return ddListArray;
                }
                
                @Override
                public void add(final int n, final CTFFDDList list) {
                    CTFFDataImpl.this.insertNewDdList(n).set((XmlObject)list);
                }
                
                @Override
                public CTFFDDList remove(final int n) {
                    final CTFFDDList ddListArray = CTFFDataImpl.this.getDdListArray(n);
                    CTFFDataImpl.this.removeDdList(n);
                    return ddListArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfDdListArray();
                }
            }
            return new DdListList();
        }
    }
    
    @Deprecated
    public CTFFDDList[] getDdListArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.DDLIST$16, (List)list);
            final CTFFDDList[] array = new CTFFDDList[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFFDDList getDdListArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFFDDList list = (CTFFDDList)this.get_store().find_element_user(CTFFDataImpl.DDLIST$16, n);
            if (list == null) {
                throw new IndexOutOfBoundsException();
            }
            return list;
        }
    }
    
    public int sizeOfDdListArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.DDLIST$16);
        }
    }
    
    public void setDdListArray(final CTFFDDList[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.DDLIST$16);
    }
    
    public void setDdListArray(final int n, final CTFFDDList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTFFDataImpl.DDLIST$16, n, (short)2);
    }
    
    public CTFFDDList insertNewDdList(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFDDList)this.get_store().insert_element_user(CTFFDataImpl.DDLIST$16, n);
        }
    }
    
    public CTFFDDList addNewDdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFDDList)this.get_store().add_element_user(CTFFDataImpl.DDLIST$16);
        }
    }
    
    public void removeDdList(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.DDLIST$16, n);
        }
    }
    
    public List<CTFFTextInput> getTextInputList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextInputList extends AbstractList<CTFFTextInput>
            {
                @Override
                public CTFFTextInput get(final int n) {
                    return CTFFDataImpl.this.getTextInputArray(n);
                }
                
                @Override
                public CTFFTextInput set(final int n, final CTFFTextInput ctffTextInput) {
                    final CTFFTextInput textInputArray = CTFFDataImpl.this.getTextInputArray(n);
                    CTFFDataImpl.this.setTextInputArray(n, ctffTextInput);
                    return textInputArray;
                }
                
                @Override
                public void add(final int n, final CTFFTextInput ctffTextInput) {
                    CTFFDataImpl.this.insertNewTextInput(n).set((XmlObject)ctffTextInput);
                }
                
                @Override
                public CTFFTextInput remove(final int n) {
                    final CTFFTextInput textInputArray = CTFFDataImpl.this.getTextInputArray(n);
                    CTFFDataImpl.this.removeTextInput(n);
                    return textInputArray;
                }
                
                @Override
                public int size() {
                    return CTFFDataImpl.this.sizeOfTextInputArray();
                }
            }
            return new TextInputList();
        }
    }
    
    @Deprecated
    public CTFFTextInput[] getTextInputArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTFFDataImpl.TEXTINPUT$18, (List)list);
            final CTFFTextInput[] array = new CTFFTextInput[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTFFTextInput getTextInputArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTFFTextInput ctffTextInput = (CTFFTextInput)this.get_store().find_element_user(CTFFDataImpl.TEXTINPUT$18, n);
            if (ctffTextInput == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctffTextInput;
        }
    }
    
    public int sizeOfTextInputArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTFFDataImpl.TEXTINPUT$18);
        }
    }
    
    public void setTextInputArray(final CTFFTextInput[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTFFDataImpl.TEXTINPUT$18);
    }
    
    public void setTextInputArray(final int n, final CTFFTextInput ctffTextInput) {
        this.generatedSetterHelperImpl((XmlObject)ctffTextInput, CTFFDataImpl.TEXTINPUT$18, n, (short)2);
    }
    
    public CTFFTextInput insertNewTextInput(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFTextInput)this.get_store().insert_element_user(CTFFDataImpl.TEXTINPUT$18, n);
        }
    }
    
    public CTFFTextInput addNewTextInput() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTFFTextInput)this.get_store().add_element_user(CTFFDataImpl.TEXTINPUT$18);
        }
    }
    
    public void removeTextInput(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTFFDataImpl.TEXTINPUT$18, n);
        }
    }
    
    static {
        NAME$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "name");
        ENABLED$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "enabled");
        CALCONEXIT$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "calcOnExit");
        ENTRYMACRO$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "entryMacro");
        EXITMACRO$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "exitMacro");
        HELPTEXT$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "helpText");
        STATUSTEXT$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "statusText");
        CHECKBOX$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "checkBox");
        DDLIST$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "ddList");
        TEXTINPUT$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "textInput");
    }
}
