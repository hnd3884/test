package com.microsoft.schemas.compatibility.impl;

import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import java.util.ArrayList;
import java.util.AbstractList;
import com.microsoft.schemas.compatibility.AlternateContentDocument.AlternateContent;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import com.microsoft.schemas.compatibility.AlternateContentDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class AlternateContentDocumentImpl extends XmlComplexContentImpl implements AlternateContentDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName ALTERNATECONTENT$0;
    
    public AlternateContentDocumentImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public AlternateContent getAlternateContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final AlternateContent alternateContent = (AlternateContent)this.get_store().find_element_user(AlternateContentDocumentImpl.ALTERNATECONTENT$0, 0);
            if (alternateContent == null) {
                return null;
            }
            return alternateContent;
        }
    }
    
    public void setAlternateContent(final AlternateContent alternateContent) {
        this.generatedSetterHelperImpl((XmlObject)alternateContent, AlternateContentDocumentImpl.ALTERNATECONTENT$0, 0, (short)1);
    }
    
    public AlternateContent addNewAlternateContent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AlternateContent)this.get_store().add_element_user(AlternateContentDocumentImpl.ALTERNATECONTENT$0);
        }
    }
    
    static {
        ALTERNATECONTENT$0 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "AlternateContent");
    }
    
    public static class AlternateContentImpl extends XmlComplexContentImpl implements AlternateContent
    {
        private static final long serialVersionUID = 1L;
        private static final QName CHOICE$0;
        private static final QName FALLBACK$2;
        private static final QName IGNORABLE$4;
        private static final QName MUSTUNDERSTAND$6;
        private static final QName PROCESSCONTENT$8;
        
        public AlternateContentImpl(final SchemaType schemaType) {
            super(schemaType);
        }
        
        public List<Choice> getChoiceList() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final class ChoiceList extends AbstractList<Choice>
                {
                    @Override
                    public Choice get(final int n) {
                        return AlternateContentImpl.this.getChoiceArray(n);
                    }
                    
                    @Override
                    public Choice set(final int n, final Choice choice) {
                        final Choice choiceArray = AlternateContentImpl.this.getChoiceArray(n);
                        AlternateContentImpl.this.setChoiceArray(n, choice);
                        return choiceArray;
                    }
                    
                    @Override
                    public void add(final int n, final Choice choice) {
                        AlternateContentImpl.this.insertNewChoice(n).set((XmlObject)choice);
                    }
                    
                    @Override
                    public Choice remove(final int n) {
                        final Choice choiceArray = AlternateContentImpl.this.getChoiceArray(n);
                        AlternateContentImpl.this.removeChoice(n);
                        return choiceArray;
                    }
                    
                    @Override
                    public int size() {
                        return AlternateContentImpl.this.sizeOfChoiceArray();
                    }
                }
                return new ChoiceList();
            }
        }
        
        @Deprecated
        public Choice[] getChoiceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final ArrayList list = new ArrayList();
                this.get_store().find_all_element_users(AlternateContentImpl.CHOICE$0, (List)list);
                final Choice[] array = new Choice[list.size()];
                list.toArray(array);
                return array;
            }
        }
        
        public Choice getChoiceArray(final int n) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final Choice choice = (Choice)this.get_store().find_element_user(AlternateContentImpl.CHOICE$0, n);
                if (choice == null) {
                    throw new IndexOutOfBoundsException();
                }
                return choice;
            }
        }
        
        public int sizeOfChoiceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(AlternateContentImpl.CHOICE$0);
            }
        }
        
        public void setChoiceArray(final Choice[] array) {
            this.check_orphaned();
            this.arraySetterHelper((XmlObject[])array, AlternateContentImpl.CHOICE$0);
        }
        
        public void setChoiceArray(final int n, final Choice choice) {
            this.generatedSetterHelperImpl((XmlObject)choice, AlternateContentImpl.CHOICE$0, n, (short)2);
        }
        
        public Choice insertNewChoice(final int n) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return (Choice)this.get_store().insert_element_user(AlternateContentImpl.CHOICE$0, n);
            }
        }
        
        public Choice addNewChoice() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return (Choice)this.get_store().add_element_user(AlternateContentImpl.CHOICE$0);
            }
        }
        
        public void removeChoice(final int n) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(AlternateContentImpl.CHOICE$0, n);
            }
        }
        
        public Fallback getFallback() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final Fallback fallback = (Fallback)this.get_store().find_element_user(AlternateContentImpl.FALLBACK$2, 0);
                if (fallback == null) {
                    return null;
                }
                return fallback;
            }
        }
        
        public boolean isSetFallback() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(AlternateContentImpl.FALLBACK$2) != 0;
            }
        }
        
        public void setFallback(final Fallback fallback) {
            this.generatedSetterHelperImpl((XmlObject)fallback, AlternateContentImpl.FALLBACK$2, 0, (short)1);
        }
        
        public Fallback addNewFallback() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return (Fallback)this.get_store().add_element_user(AlternateContentImpl.FALLBACK$2);
            }
        }
        
        public void unsetFallback() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(AlternateContentImpl.FALLBACK$2, 0);
            }
        }
        
        public String getIgnorable() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(AlternateContentImpl.IGNORABLE$4);
                if (simpleValue == null) {
                    return null;
                }
                return simpleValue.getStringValue();
            }
        }
        
        public XmlString xgetIgnorable() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return (XmlString)this.get_store().find_attribute_user(AlternateContentImpl.IGNORABLE$4);
            }
        }
        
        public boolean isSetIgnorable() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(AlternateContentImpl.IGNORABLE$4) != null;
            }
        }
        
        public void setIgnorable(final String stringValue) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(AlternateContentImpl.IGNORABLE$4);
                if (simpleValue == null) {
                    simpleValue = (SimpleValue)this.get_store().add_attribute_user(AlternateContentImpl.IGNORABLE$4);
                }
                simpleValue.setStringValue(stringValue);
            }
        }
        
        public void xsetIgnorable(final XmlString xmlString) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(AlternateContentImpl.IGNORABLE$4);
                if (xmlString2 == null) {
                    xmlString2 = (XmlString)this.get_store().add_attribute_user(AlternateContentImpl.IGNORABLE$4);
                }
                xmlString2.set((XmlObject)xmlString);
            }
        }
        
        public void unsetIgnorable() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(AlternateContentImpl.IGNORABLE$4);
            }
        }
        
        public String getMustUnderstand() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(AlternateContentImpl.MUSTUNDERSTAND$6);
                if (simpleValue == null) {
                    return null;
                }
                return simpleValue.getStringValue();
            }
        }
        
        public XmlString xgetMustUnderstand() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return (XmlString)this.get_store().find_attribute_user(AlternateContentImpl.MUSTUNDERSTAND$6);
            }
        }
        
        public boolean isSetMustUnderstand() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(AlternateContentImpl.MUSTUNDERSTAND$6) != null;
            }
        }
        
        public void setMustUnderstand(final String stringValue) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(AlternateContentImpl.MUSTUNDERSTAND$6);
                if (simpleValue == null) {
                    simpleValue = (SimpleValue)this.get_store().add_attribute_user(AlternateContentImpl.MUSTUNDERSTAND$6);
                }
                simpleValue.setStringValue(stringValue);
            }
        }
        
        public void xsetMustUnderstand(final XmlString xmlString) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(AlternateContentImpl.MUSTUNDERSTAND$6);
                if (xmlString2 == null) {
                    xmlString2 = (XmlString)this.get_store().add_attribute_user(AlternateContentImpl.MUSTUNDERSTAND$6);
                }
                xmlString2.set((XmlObject)xmlString);
            }
        }
        
        public void unsetMustUnderstand() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(AlternateContentImpl.MUSTUNDERSTAND$6);
            }
        }
        
        public String getProcessContent() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(AlternateContentImpl.PROCESSCONTENT$8);
                if (simpleValue == null) {
                    return null;
                }
                return simpleValue.getStringValue();
            }
        }
        
        public XmlString xgetProcessContent() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return (XmlString)this.get_store().find_attribute_user(AlternateContentImpl.PROCESSCONTENT$8);
            }
        }
        
        public boolean isSetProcessContent() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(AlternateContentImpl.PROCESSCONTENT$8) != null;
            }
        }
        
        public void setProcessContent(final String stringValue) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(AlternateContentImpl.PROCESSCONTENT$8);
                if (simpleValue == null) {
                    simpleValue = (SimpleValue)this.get_store().add_attribute_user(AlternateContentImpl.PROCESSCONTENT$8);
                }
                simpleValue.setStringValue(stringValue);
            }
        }
        
        public void xsetProcessContent(final XmlString xmlString) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(AlternateContentImpl.PROCESSCONTENT$8);
                if (xmlString2 == null) {
                    xmlString2 = (XmlString)this.get_store().add_attribute_user(AlternateContentImpl.PROCESSCONTENT$8);
                }
                xmlString2.set((XmlObject)xmlString);
            }
        }
        
        public void unsetProcessContent() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(AlternateContentImpl.PROCESSCONTENT$8);
            }
        }
        
        static {
            CHOICE$0 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Choice");
            FALLBACK$2 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Fallback");
            IGNORABLE$4 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Ignorable");
            MUSTUNDERSTAND$6 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "MustUnderstand");
            PROCESSCONTENT$8 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "ProcessContent");
        }
        
        public static class FallbackImpl extends XmlComplexContentImpl implements Fallback
        {
            private static final long serialVersionUID = 1L;
            private static final QName IGNORABLE$0;
            private static final QName MUSTUNDERSTAND$2;
            private static final QName PROCESSCONTENT$4;
            
            public FallbackImpl(final SchemaType schemaType) {
                super(schemaType);
            }
            
            public String getIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(FallbackImpl.IGNORABLE$0);
                    if (simpleValue == null) {
                        return null;
                    }
                    return simpleValue.getStringValue();
                }
            }
            
            public XmlString xgetIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return (XmlString)this.get_store().find_attribute_user(FallbackImpl.IGNORABLE$0);
                }
            }
            
            public boolean isSetIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(FallbackImpl.IGNORABLE$0) != null;
                }
            }
            
            public void setIgnorable(final String stringValue) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(FallbackImpl.IGNORABLE$0);
                    if (simpleValue == null) {
                        simpleValue = (SimpleValue)this.get_store().add_attribute_user(FallbackImpl.IGNORABLE$0);
                    }
                    simpleValue.setStringValue(stringValue);
                }
            }
            
            public void xsetIgnorable(final XmlString xmlString) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(FallbackImpl.IGNORABLE$0);
                    if (xmlString2 == null) {
                        xmlString2 = (XmlString)this.get_store().add_attribute_user(FallbackImpl.IGNORABLE$0);
                    }
                    xmlString2.set((XmlObject)xmlString);
                }
            }
            
            public void unsetIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(FallbackImpl.IGNORABLE$0);
                }
            }
            
            public String getMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(FallbackImpl.MUSTUNDERSTAND$2);
                    if (simpleValue == null) {
                        return null;
                    }
                    return simpleValue.getStringValue();
                }
            }
            
            public XmlString xgetMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return (XmlString)this.get_store().find_attribute_user(FallbackImpl.MUSTUNDERSTAND$2);
                }
            }
            
            public boolean isSetMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(FallbackImpl.MUSTUNDERSTAND$2) != null;
                }
            }
            
            public void setMustUnderstand(final String stringValue) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(FallbackImpl.MUSTUNDERSTAND$2);
                    if (simpleValue == null) {
                        simpleValue = (SimpleValue)this.get_store().add_attribute_user(FallbackImpl.MUSTUNDERSTAND$2);
                    }
                    simpleValue.setStringValue(stringValue);
                }
            }
            
            public void xsetMustUnderstand(final XmlString xmlString) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(FallbackImpl.MUSTUNDERSTAND$2);
                    if (xmlString2 == null) {
                        xmlString2 = (XmlString)this.get_store().add_attribute_user(FallbackImpl.MUSTUNDERSTAND$2);
                    }
                    xmlString2.set((XmlObject)xmlString);
                }
            }
            
            public void unsetMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(FallbackImpl.MUSTUNDERSTAND$2);
                }
            }
            
            public String getProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(FallbackImpl.PROCESSCONTENT$4);
                    if (simpleValue == null) {
                        return null;
                    }
                    return simpleValue.getStringValue();
                }
            }
            
            public XmlString xgetProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return (XmlString)this.get_store().find_attribute_user(FallbackImpl.PROCESSCONTENT$4);
                }
            }
            
            public boolean isSetProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(FallbackImpl.PROCESSCONTENT$4) != null;
                }
            }
            
            public void setProcessContent(final String stringValue) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(FallbackImpl.PROCESSCONTENT$4);
                    if (simpleValue == null) {
                        simpleValue = (SimpleValue)this.get_store().add_attribute_user(FallbackImpl.PROCESSCONTENT$4);
                    }
                    simpleValue.setStringValue(stringValue);
                }
            }
            
            public void xsetProcessContent(final XmlString xmlString) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(FallbackImpl.PROCESSCONTENT$4);
                    if (xmlString2 == null) {
                        xmlString2 = (XmlString)this.get_store().add_attribute_user(FallbackImpl.PROCESSCONTENT$4);
                    }
                    xmlString2.set((XmlObject)xmlString);
                }
            }
            
            public void unsetProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(FallbackImpl.PROCESSCONTENT$4);
                }
            }
            
            static {
                IGNORABLE$0 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Ignorable");
                MUSTUNDERSTAND$2 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "MustUnderstand");
                PROCESSCONTENT$4 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "ProcessContent");
            }
        }
        
        public static class ChoiceImpl extends XmlComplexContentImpl implements Choice
        {
            private static final long serialVersionUID = 1L;
            private static final QName REQUIRES$0;
            private static final QName IGNORABLE$2;
            private static final QName MUSTUNDERSTAND$4;
            private static final QName PROCESSCONTENT$6;
            
            public ChoiceImpl(final SchemaType schemaType) {
                super(schemaType);
            }
            
            public String getRequires() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.REQUIRES$0);
                    if (simpleValue == null) {
                        return null;
                    }
                    return simpleValue.getStringValue();
                }
            }
            
            public XmlString xgetRequires() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return (XmlString)this.get_store().find_attribute_user(ChoiceImpl.REQUIRES$0);
                }
            }
            
            public void setRequires(final String stringValue) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.REQUIRES$0);
                    if (simpleValue == null) {
                        simpleValue = (SimpleValue)this.get_store().add_attribute_user(ChoiceImpl.REQUIRES$0);
                    }
                    simpleValue.setStringValue(stringValue);
                }
            }
            
            public void xsetRequires(final XmlString xmlString) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ChoiceImpl.REQUIRES$0);
                    if (xmlString2 == null) {
                        xmlString2 = (XmlString)this.get_store().add_attribute_user(ChoiceImpl.REQUIRES$0);
                    }
                    xmlString2.set((XmlObject)xmlString);
                }
            }
            
            public String getIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.IGNORABLE$2);
                    if (simpleValue == null) {
                        return null;
                    }
                    return simpleValue.getStringValue();
                }
            }
            
            public XmlString xgetIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return (XmlString)this.get_store().find_attribute_user(ChoiceImpl.IGNORABLE$2);
                }
            }
            
            public boolean isSetIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(ChoiceImpl.IGNORABLE$2) != null;
                }
            }
            
            public void setIgnorable(final String stringValue) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.IGNORABLE$2);
                    if (simpleValue == null) {
                        simpleValue = (SimpleValue)this.get_store().add_attribute_user(ChoiceImpl.IGNORABLE$2);
                    }
                    simpleValue.setStringValue(stringValue);
                }
            }
            
            public void xsetIgnorable(final XmlString xmlString) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ChoiceImpl.IGNORABLE$2);
                    if (xmlString2 == null) {
                        xmlString2 = (XmlString)this.get_store().add_attribute_user(ChoiceImpl.IGNORABLE$2);
                    }
                    xmlString2.set((XmlObject)xmlString);
                }
            }
            
            public void unsetIgnorable() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(ChoiceImpl.IGNORABLE$2);
                }
            }
            
            public String getMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.MUSTUNDERSTAND$4);
                    if (simpleValue == null) {
                        return null;
                    }
                    return simpleValue.getStringValue();
                }
            }
            
            public XmlString xgetMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return (XmlString)this.get_store().find_attribute_user(ChoiceImpl.MUSTUNDERSTAND$4);
                }
            }
            
            public boolean isSetMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(ChoiceImpl.MUSTUNDERSTAND$4) != null;
                }
            }
            
            public void setMustUnderstand(final String stringValue) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.MUSTUNDERSTAND$4);
                    if (simpleValue == null) {
                        simpleValue = (SimpleValue)this.get_store().add_attribute_user(ChoiceImpl.MUSTUNDERSTAND$4);
                    }
                    simpleValue.setStringValue(stringValue);
                }
            }
            
            public void xsetMustUnderstand(final XmlString xmlString) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ChoiceImpl.MUSTUNDERSTAND$4);
                    if (xmlString2 == null) {
                        xmlString2 = (XmlString)this.get_store().add_attribute_user(ChoiceImpl.MUSTUNDERSTAND$4);
                    }
                    xmlString2.set((XmlObject)xmlString);
                }
            }
            
            public void unsetMustUnderstand() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(ChoiceImpl.MUSTUNDERSTAND$4);
                }
            }
            
            public String getProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.PROCESSCONTENT$6);
                    if (simpleValue == null) {
                        return null;
                    }
                    return simpleValue.getStringValue();
                }
            }
            
            public XmlString xgetProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return (XmlString)this.get_store().find_attribute_user(ChoiceImpl.PROCESSCONTENT$6);
                }
            }
            
            public boolean isSetProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    return this.get_store().find_attribute_user(ChoiceImpl.PROCESSCONTENT$6) != null;
                }
            }
            
            public void setProcessContent(final String stringValue) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(ChoiceImpl.PROCESSCONTENT$6);
                    if (simpleValue == null) {
                        simpleValue = (SimpleValue)this.get_store().add_attribute_user(ChoiceImpl.PROCESSCONTENT$6);
                    }
                    simpleValue.setStringValue(stringValue);
                }
            }
            
            public void xsetProcessContent(final XmlString xmlString) {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    XmlString xmlString2 = (XmlString)this.get_store().find_attribute_user(ChoiceImpl.PROCESSCONTENT$6);
                    if (xmlString2 == null) {
                        xmlString2 = (XmlString)this.get_store().add_attribute_user(ChoiceImpl.PROCESSCONTENT$6);
                    }
                    xmlString2.set((XmlObject)xmlString);
                }
            }
            
            public void unsetProcessContent() {
                synchronized (this.monitor()) {
                    this.check_orphaned();
                    this.get_store().remove_attribute(ChoiceImpl.PROCESSCONTENT$6);
                }
            }
            
            static {
                REQUIRES$0 = new QName("", "Requires");
                IGNORABLE$2 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "Ignorable");
                MUSTUNDERSTAND$4 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "MustUnderstand");
                PROCESSCONTENT$6 = new QName("http://schemas.openxmlformats.org/markup-compatibility/2006", "ProcessContent");
            }
        }
    }
}
