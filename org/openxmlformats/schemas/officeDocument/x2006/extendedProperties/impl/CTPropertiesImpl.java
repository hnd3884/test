package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.impl;

import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTDigSigBlob;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTVectorLpstr;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTVectorVariant;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPropertiesImpl extends XmlComplexContentImpl implements CTProperties
{
    private static final long serialVersionUID = 1L;
    private static final QName TEMPLATE$0;
    private static final QName MANAGER$2;
    private static final QName COMPANY$4;
    private static final QName PAGES$6;
    private static final QName WORDS$8;
    private static final QName CHARACTERS$10;
    private static final QName PRESENTATIONFORMAT$12;
    private static final QName LINES$14;
    private static final QName PARAGRAPHS$16;
    private static final QName SLIDES$18;
    private static final QName NOTES$20;
    private static final QName TOTALTIME$22;
    private static final QName HIDDENSLIDES$24;
    private static final QName MMCLIPS$26;
    private static final QName SCALECROP$28;
    private static final QName HEADINGPAIRS$30;
    private static final QName TITLESOFPARTS$32;
    private static final QName LINKSUPTODATE$34;
    private static final QName CHARACTERSWITHSPACES$36;
    private static final QName SHAREDDOC$38;
    private static final QName HYPERLINKBASE$40;
    private static final QName HLINKS$42;
    private static final QName HYPERLINKSCHANGED$44;
    private static final QName DIGSIG$46;
    private static final QName APPLICATION$48;
    private static final QName APPVERSION$50;
    private static final QName DOCSECURITY$52;
    
    public CTPropertiesImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public String getTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.TEMPLATE$0, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTPropertiesImpl.TEMPLATE$0, 0);
        }
    }
    
    public boolean isSetTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.TEMPLATE$0) != 0;
        }
    }
    
    public void setTemplate(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.TEMPLATE$0, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.TEMPLATE$0);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetTemplate(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTPropertiesImpl.TEMPLATE$0, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTPropertiesImpl.TEMPLATE$0);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetTemplate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.TEMPLATE$0, 0);
        }
    }
    
    public String getManager() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.MANAGER$2, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetManager() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTPropertiesImpl.MANAGER$2, 0);
        }
    }
    
    public boolean isSetManager() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.MANAGER$2) != 0;
        }
    }
    
    public void setManager(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.MANAGER$2, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.MANAGER$2);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetManager(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTPropertiesImpl.MANAGER$2, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTPropertiesImpl.MANAGER$2);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetManager() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.MANAGER$2, 0);
        }
    }
    
    public String getCompany() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.COMPANY$4, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetCompany() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTPropertiesImpl.COMPANY$4, 0);
        }
    }
    
    public boolean isSetCompany() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.COMPANY$4) != 0;
        }
    }
    
    public void setCompany(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.COMPANY$4, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.COMPANY$4);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetCompany(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTPropertiesImpl.COMPANY$4, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTPropertiesImpl.COMPANY$4);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetCompany() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.COMPANY$4, 0);
        }
    }
    
    public int getPages() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.PAGES$6, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetPages() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.PAGES$6, 0);
        }
    }
    
    public boolean isSetPages() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.PAGES$6) != 0;
        }
    }
    
    public void setPages(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.PAGES$6, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.PAGES$6);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetPages(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.PAGES$6, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.PAGES$6);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetPages() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.PAGES$6, 0);
        }
    }
    
    public int getWords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.WORDS$8, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetWords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.WORDS$8, 0);
        }
    }
    
    public boolean isSetWords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.WORDS$8) != 0;
        }
    }
    
    public void setWords(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.WORDS$8, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.WORDS$8);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetWords(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.WORDS$8, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.WORDS$8);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetWords() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.WORDS$8, 0);
        }
    }
    
    public int getCharacters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERS$10, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetCharacters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERS$10, 0);
        }
    }
    
    public boolean isSetCharacters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.CHARACTERS$10) != 0;
        }
    }
    
    public void setCharacters(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERS$10, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.CHARACTERS$10);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetCharacters(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERS$10, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.CHARACTERS$10);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetCharacters() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.CHARACTERS$10, 0);
        }
    }
    
    public String getPresentationFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.PRESENTATIONFORMAT$12, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetPresentationFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTPropertiesImpl.PRESENTATIONFORMAT$12, 0);
        }
    }
    
    public boolean isSetPresentationFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.PRESENTATIONFORMAT$12) != 0;
        }
    }
    
    public void setPresentationFormat(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.PRESENTATIONFORMAT$12, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.PRESENTATIONFORMAT$12);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetPresentationFormat(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTPropertiesImpl.PRESENTATIONFORMAT$12, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTPropertiesImpl.PRESENTATIONFORMAT$12);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetPresentationFormat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.PRESENTATIONFORMAT$12, 0);
        }
    }
    
    public int getLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.LINES$14, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.LINES$14, 0);
        }
    }
    
    public boolean isSetLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.LINES$14) != 0;
        }
    }
    
    public void setLines(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.LINES$14, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.LINES$14);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetLines(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.LINES$14, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.LINES$14);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.LINES$14, 0);
        }
    }
    
    public int getParagraphs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.PARAGRAPHS$16, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetParagraphs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.PARAGRAPHS$16, 0);
        }
    }
    
    public boolean isSetParagraphs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.PARAGRAPHS$16) != 0;
        }
    }
    
    public void setParagraphs(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.PARAGRAPHS$16, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.PARAGRAPHS$16);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetParagraphs(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.PARAGRAPHS$16, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.PARAGRAPHS$16);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetParagraphs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.PARAGRAPHS$16, 0);
        }
    }
    
    public int getSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.SLIDES$18, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.SLIDES$18, 0);
        }
    }
    
    public boolean isSetSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.SLIDES$18) != 0;
        }
    }
    
    public void setSlides(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.SLIDES$18, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.SLIDES$18);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetSlides(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.SLIDES$18, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.SLIDES$18);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.SLIDES$18, 0);
        }
    }
    
    public int getNotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.NOTES$20, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetNotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.NOTES$20, 0);
        }
    }
    
    public boolean isSetNotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.NOTES$20) != 0;
        }
    }
    
    public void setNotes(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.NOTES$20, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.NOTES$20);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetNotes(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.NOTES$20, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.NOTES$20);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetNotes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.NOTES$20, 0);
        }
    }
    
    public int getTotalTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.TOTALTIME$22, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetTotalTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.TOTALTIME$22, 0);
        }
    }
    
    public boolean isSetTotalTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.TOTALTIME$22) != 0;
        }
    }
    
    public void setTotalTime(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.TOTALTIME$22, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.TOTALTIME$22);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetTotalTime(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.TOTALTIME$22, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.TOTALTIME$22);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetTotalTime() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.TOTALTIME$22, 0);
        }
    }
    
    public int getHiddenSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.HIDDENSLIDES$24, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetHiddenSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.HIDDENSLIDES$24, 0);
        }
    }
    
    public boolean isSetHiddenSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.HIDDENSLIDES$24) != 0;
        }
    }
    
    public void setHiddenSlides(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.HIDDENSLIDES$24, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.HIDDENSLIDES$24);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetHiddenSlides(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.HIDDENSLIDES$24, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.HIDDENSLIDES$24);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetHiddenSlides() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.HIDDENSLIDES$24, 0);
        }
    }
    
    public int getMMClips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.MMCLIPS$26, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetMMClips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.MMCLIPS$26, 0);
        }
    }
    
    public boolean isSetMMClips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.MMCLIPS$26) != 0;
        }
    }
    
    public void setMMClips(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.MMCLIPS$26, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.MMCLIPS$26);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetMMClips(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.MMCLIPS$26, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.MMCLIPS$26);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetMMClips() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.MMCLIPS$26, 0);
        }
    }
    
    public boolean getScaleCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.SCALECROP$28, 0);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetScaleCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.SCALECROP$28, 0);
        }
    }
    
    public boolean isSetScaleCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.SCALECROP$28) != 0;
        }
    }
    
    public void setScaleCrop(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.SCALECROP$28, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.SCALECROP$28);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetScaleCrop(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.SCALECROP$28, 0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_element_user(CTPropertiesImpl.SCALECROP$28);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetScaleCrop() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.SCALECROP$28, 0);
        }
    }
    
    public CTVectorVariant getHeadingPairs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVectorVariant ctVectorVariant = (CTVectorVariant)this.get_store().find_element_user(CTPropertiesImpl.HEADINGPAIRS$30, 0);
            if (ctVectorVariant == null) {
                return null;
            }
            return ctVectorVariant;
        }
    }
    
    public boolean isSetHeadingPairs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.HEADINGPAIRS$30) != 0;
        }
    }
    
    public void setHeadingPairs(final CTVectorVariant ctVectorVariant) {
        this.generatedSetterHelperImpl((XmlObject)ctVectorVariant, CTPropertiesImpl.HEADINGPAIRS$30, 0, (short)1);
    }
    
    public CTVectorVariant addNewHeadingPairs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVectorVariant)this.get_store().add_element_user(CTPropertiesImpl.HEADINGPAIRS$30);
        }
    }
    
    public void unsetHeadingPairs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.HEADINGPAIRS$30, 0);
        }
    }
    
    public CTVectorLpstr getTitlesOfParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVectorLpstr ctVectorLpstr = (CTVectorLpstr)this.get_store().find_element_user(CTPropertiesImpl.TITLESOFPARTS$32, 0);
            if (ctVectorLpstr == null) {
                return null;
            }
            return ctVectorLpstr;
        }
    }
    
    public boolean isSetTitlesOfParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.TITLESOFPARTS$32) != 0;
        }
    }
    
    public void setTitlesOfParts(final CTVectorLpstr ctVectorLpstr) {
        this.generatedSetterHelperImpl((XmlObject)ctVectorLpstr, CTPropertiesImpl.TITLESOFPARTS$32, 0, (short)1);
    }
    
    public CTVectorLpstr addNewTitlesOfParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVectorLpstr)this.get_store().add_element_user(CTPropertiesImpl.TITLESOFPARTS$32);
        }
    }
    
    public void unsetTitlesOfParts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.TITLESOFPARTS$32, 0);
        }
    }
    
    public boolean getLinksUpToDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.LINKSUPTODATE$34, 0);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLinksUpToDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.LINKSUPTODATE$34, 0);
        }
    }
    
    public boolean isSetLinksUpToDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.LINKSUPTODATE$34) != 0;
        }
    }
    
    public void setLinksUpToDate(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.LINKSUPTODATE$34, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.LINKSUPTODATE$34);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLinksUpToDate(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.LINKSUPTODATE$34, 0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_element_user(CTPropertiesImpl.LINKSUPTODATE$34);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLinksUpToDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.LINKSUPTODATE$34, 0);
        }
    }
    
    public int getCharactersWithSpaces() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERSWITHSPACES$36, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetCharactersWithSpaces() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERSWITHSPACES$36, 0);
        }
    }
    
    public boolean isSetCharactersWithSpaces() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.CHARACTERSWITHSPACES$36) != 0;
        }
    }
    
    public void setCharactersWithSpaces(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERSWITHSPACES$36, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.CHARACTERSWITHSPACES$36);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetCharactersWithSpaces(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.CHARACTERSWITHSPACES$36, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.CHARACTERSWITHSPACES$36);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetCharactersWithSpaces() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.CHARACTERSWITHSPACES$36, 0);
        }
    }
    
    public boolean getSharedDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.SHAREDDOC$38, 0);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetSharedDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.SHAREDDOC$38, 0);
        }
    }
    
    public boolean isSetSharedDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.SHAREDDOC$38) != 0;
        }
    }
    
    public void setSharedDoc(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.SHAREDDOC$38, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.SHAREDDOC$38);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetSharedDoc(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.SHAREDDOC$38, 0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_element_user(CTPropertiesImpl.SHAREDDOC$38);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetSharedDoc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.SHAREDDOC$38, 0);
        }
    }
    
    public String getHyperlinkBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKBASE$40, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetHyperlinkBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKBASE$40, 0);
        }
    }
    
    public boolean isSetHyperlinkBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.HYPERLINKBASE$40) != 0;
        }
    }
    
    public void setHyperlinkBase(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKBASE$40, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.HYPERLINKBASE$40);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetHyperlinkBase(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKBASE$40, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTPropertiesImpl.HYPERLINKBASE$40);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetHyperlinkBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.HYPERLINKBASE$40, 0);
        }
    }
    
    public CTVectorVariant getHLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTVectorVariant ctVectorVariant = (CTVectorVariant)this.get_store().find_element_user(CTPropertiesImpl.HLINKS$42, 0);
            if (ctVectorVariant == null) {
                return null;
            }
            return ctVectorVariant;
        }
    }
    
    public boolean isSetHLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.HLINKS$42) != 0;
        }
    }
    
    public void setHLinks(final CTVectorVariant ctVectorVariant) {
        this.generatedSetterHelperImpl((XmlObject)ctVectorVariant, CTPropertiesImpl.HLINKS$42, 0, (short)1);
    }
    
    public CTVectorVariant addNewHLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTVectorVariant)this.get_store().add_element_user(CTPropertiesImpl.HLINKS$42);
        }
    }
    
    public void unsetHLinks() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.HLINKS$42, 0);
        }
    }
    
    public boolean getHyperlinksChanged() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKSCHANGED$44, 0);
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetHyperlinksChanged() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKSCHANGED$44, 0);
        }
    }
    
    public boolean isSetHyperlinksChanged() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.HYPERLINKSCHANGED$44) != 0;
        }
    }
    
    public void setHyperlinksChanged(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKSCHANGED$44, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.HYPERLINKSCHANGED$44);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetHyperlinksChanged(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_element_user(CTPropertiesImpl.HYPERLINKSCHANGED$44, 0);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_element_user(CTPropertiesImpl.HYPERLINKSCHANGED$44);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetHyperlinksChanged() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.HYPERLINKSCHANGED$44, 0);
        }
    }
    
    public CTDigSigBlob getDigSig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDigSigBlob ctDigSigBlob = (CTDigSigBlob)this.get_store().find_element_user(CTPropertiesImpl.DIGSIG$46, 0);
            if (ctDigSigBlob == null) {
                return null;
            }
            return ctDigSigBlob;
        }
    }
    
    public boolean isSetDigSig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.DIGSIG$46) != 0;
        }
    }
    
    public void setDigSig(final CTDigSigBlob ctDigSigBlob) {
        this.generatedSetterHelperImpl((XmlObject)ctDigSigBlob, CTPropertiesImpl.DIGSIG$46, 0, (short)1);
    }
    
    public CTDigSigBlob addNewDigSig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDigSigBlob)this.get_store().add_element_user(CTPropertiesImpl.DIGSIG$46);
        }
    }
    
    public void unsetDigSig() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.DIGSIG$46, 0);
        }
    }
    
    public String getApplication() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.APPLICATION$48, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetApplication() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTPropertiesImpl.APPLICATION$48, 0);
        }
    }
    
    public boolean isSetApplication() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.APPLICATION$48) != 0;
        }
    }
    
    public void setApplication(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.APPLICATION$48, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.APPLICATION$48);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetApplication(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTPropertiesImpl.APPLICATION$48, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTPropertiesImpl.APPLICATION$48);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetApplication() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.APPLICATION$48, 0);
        }
    }
    
    public String getAppVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.APPVERSION$50, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetAppVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTPropertiesImpl.APPVERSION$50, 0);
        }
    }
    
    public boolean isSetAppVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.APPVERSION$50) != 0;
        }
    }
    
    public void setAppVersion(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.APPVERSION$50, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.APPVERSION$50);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetAppVersion(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTPropertiesImpl.APPVERSION$50, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTPropertiesImpl.APPVERSION$50);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetAppVersion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.APPVERSION$50, 0);
        }
    }
    
    public int getDocSecurity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.DOCSECURITY$52, 0);
            if (simpleValue == null) {
                return 0;
            }
            return simpleValue.getIntValue();
        }
    }
    
    public XmlInt xgetDocSecurity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.DOCSECURITY$52, 0);
        }
    }
    
    public boolean isSetDocSecurity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPropertiesImpl.DOCSECURITY$52) != 0;
        }
    }
    
    public void setDocSecurity(final int intValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTPropertiesImpl.DOCSECURITY$52, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTPropertiesImpl.DOCSECURITY$52);
            }
            simpleValue.setIntValue(intValue);
        }
    }
    
    public void xsetDocSecurity(final XmlInt xmlInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlInt xmlInt2 = (XmlInt)this.get_store().find_element_user(CTPropertiesImpl.DOCSECURITY$52, 0);
            if (xmlInt2 == null) {
                xmlInt2 = (XmlInt)this.get_store().add_element_user(CTPropertiesImpl.DOCSECURITY$52);
            }
            xmlInt2.set((XmlObject)xmlInt);
        }
    }
    
    public void unsetDocSecurity() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPropertiesImpl.DOCSECURITY$52, 0);
        }
    }
    
    static {
        TEMPLATE$0 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Template");
        MANAGER$2 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Manager");
        COMPANY$4 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Company");
        PAGES$6 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Pages");
        WORDS$8 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Words");
        CHARACTERS$10 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Characters");
        PRESENTATIONFORMAT$12 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "PresentationFormat");
        LINES$14 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Lines");
        PARAGRAPHS$16 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Paragraphs");
        SLIDES$18 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Slides");
        NOTES$20 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Notes");
        TOTALTIME$22 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "TotalTime");
        HIDDENSLIDES$24 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "HiddenSlides");
        MMCLIPS$26 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "MMClips");
        SCALECROP$28 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "ScaleCrop");
        HEADINGPAIRS$30 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "HeadingPairs");
        TITLESOFPARTS$32 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "TitlesOfParts");
        LINKSUPTODATE$34 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "LinksUpToDate");
        CHARACTERSWITHSPACES$36 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "CharactersWithSpaces");
        SHAREDDOC$38 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "SharedDoc");
        HYPERLINKBASE$40 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "HyperlinkBase");
        HLINKS$42 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "HLinks");
        HYPERLINKSCHANGED$44 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "HyperlinksChanged");
        DIGSIG$46 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "DigSig");
        APPLICATION$48 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "Application");
        APPVERSION$50 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "AppVersion");
        DOCSECURITY$52 = new QName("http://schemas.openxmlformats.org/officeDocument/2006/extended-properties", "DocSecurity");
    }
}
