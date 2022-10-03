package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;

public interface RestrictionType extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RestrictionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("restrictiontype939ftype");
    
    GroupRef getGroup();
    
    boolean isSetGroup();
    
    void setGroup(final GroupRef p0);
    
    GroupRef addNewGroup();
    
    void unsetGroup();
    
    All getAll();
    
    boolean isSetAll();
    
    void setAll(final All p0);
    
    All addNewAll();
    
    void unsetAll();
    
    ExplicitGroup getChoice();
    
    boolean isSetChoice();
    
    void setChoice(final ExplicitGroup p0);
    
    ExplicitGroup addNewChoice();
    
    void unsetChoice();
    
    ExplicitGroup getSequence();
    
    boolean isSetSequence();
    
    void setSequence(final ExplicitGroup p0);
    
    ExplicitGroup addNewSequence();
    
    void unsetSequence();
    
    LocalSimpleType getSimpleType();
    
    boolean isSetSimpleType();
    
    void setSimpleType(final LocalSimpleType p0);
    
    LocalSimpleType addNewSimpleType();
    
    void unsetSimpleType();
    
    Facet[] getMinExclusiveArray();
    
    Facet getMinExclusiveArray(final int p0);
    
    int sizeOfMinExclusiveArray();
    
    void setMinExclusiveArray(final Facet[] p0);
    
    void setMinExclusiveArray(final int p0, final Facet p1);
    
    Facet insertNewMinExclusive(final int p0);
    
    Facet addNewMinExclusive();
    
    void removeMinExclusive(final int p0);
    
    Facet[] getMinInclusiveArray();
    
    Facet getMinInclusiveArray(final int p0);
    
    int sizeOfMinInclusiveArray();
    
    void setMinInclusiveArray(final Facet[] p0);
    
    void setMinInclusiveArray(final int p0, final Facet p1);
    
    Facet insertNewMinInclusive(final int p0);
    
    Facet addNewMinInclusive();
    
    void removeMinInclusive(final int p0);
    
    Facet[] getMaxExclusiveArray();
    
    Facet getMaxExclusiveArray(final int p0);
    
    int sizeOfMaxExclusiveArray();
    
    void setMaxExclusiveArray(final Facet[] p0);
    
    void setMaxExclusiveArray(final int p0, final Facet p1);
    
    Facet insertNewMaxExclusive(final int p0);
    
    Facet addNewMaxExclusive();
    
    void removeMaxExclusive(final int p0);
    
    Facet[] getMaxInclusiveArray();
    
    Facet getMaxInclusiveArray(final int p0);
    
    int sizeOfMaxInclusiveArray();
    
    void setMaxInclusiveArray(final Facet[] p0);
    
    void setMaxInclusiveArray(final int p0, final Facet p1);
    
    Facet insertNewMaxInclusive(final int p0);
    
    Facet addNewMaxInclusive();
    
    void removeMaxInclusive(final int p0);
    
    TotalDigitsDocument.TotalDigits[] getTotalDigitsArray();
    
    TotalDigitsDocument.TotalDigits getTotalDigitsArray(final int p0);
    
    int sizeOfTotalDigitsArray();
    
    void setTotalDigitsArray(final TotalDigitsDocument.TotalDigits[] p0);
    
    void setTotalDigitsArray(final int p0, final TotalDigitsDocument.TotalDigits p1);
    
    TotalDigitsDocument.TotalDigits insertNewTotalDigits(final int p0);
    
    TotalDigitsDocument.TotalDigits addNewTotalDigits();
    
    void removeTotalDigits(final int p0);
    
    NumFacet[] getFractionDigitsArray();
    
    NumFacet getFractionDigitsArray(final int p0);
    
    int sizeOfFractionDigitsArray();
    
    void setFractionDigitsArray(final NumFacet[] p0);
    
    void setFractionDigitsArray(final int p0, final NumFacet p1);
    
    NumFacet insertNewFractionDigits(final int p0);
    
    NumFacet addNewFractionDigits();
    
    void removeFractionDigits(final int p0);
    
    NumFacet[] getLengthArray();
    
    NumFacet getLengthArray(final int p0);
    
    int sizeOfLengthArray();
    
    void setLengthArray(final NumFacet[] p0);
    
    void setLengthArray(final int p0, final NumFacet p1);
    
    NumFacet insertNewLength(final int p0);
    
    NumFacet addNewLength();
    
    void removeLength(final int p0);
    
    NumFacet[] getMinLengthArray();
    
    NumFacet getMinLengthArray(final int p0);
    
    int sizeOfMinLengthArray();
    
    void setMinLengthArray(final NumFacet[] p0);
    
    void setMinLengthArray(final int p0, final NumFacet p1);
    
    NumFacet insertNewMinLength(final int p0);
    
    NumFacet addNewMinLength();
    
    void removeMinLength(final int p0);
    
    NumFacet[] getMaxLengthArray();
    
    NumFacet getMaxLengthArray(final int p0);
    
    int sizeOfMaxLengthArray();
    
    void setMaxLengthArray(final NumFacet[] p0);
    
    void setMaxLengthArray(final int p0, final NumFacet p1);
    
    NumFacet insertNewMaxLength(final int p0);
    
    NumFacet addNewMaxLength();
    
    void removeMaxLength(final int p0);
    
    NoFixedFacet[] getEnumerationArray();
    
    NoFixedFacet getEnumerationArray(final int p0);
    
    int sizeOfEnumerationArray();
    
    void setEnumerationArray(final NoFixedFacet[] p0);
    
    void setEnumerationArray(final int p0, final NoFixedFacet p1);
    
    NoFixedFacet insertNewEnumeration(final int p0);
    
    NoFixedFacet addNewEnumeration();
    
    void removeEnumeration(final int p0);
    
    WhiteSpaceDocument.WhiteSpace[] getWhiteSpaceArray();
    
    WhiteSpaceDocument.WhiteSpace getWhiteSpaceArray(final int p0);
    
    int sizeOfWhiteSpaceArray();
    
    void setWhiteSpaceArray(final WhiteSpaceDocument.WhiteSpace[] p0);
    
    void setWhiteSpaceArray(final int p0, final WhiteSpaceDocument.WhiteSpace p1);
    
    WhiteSpaceDocument.WhiteSpace insertNewWhiteSpace(final int p0);
    
    WhiteSpaceDocument.WhiteSpace addNewWhiteSpace();
    
    void removeWhiteSpace(final int p0);
    
    PatternDocument.Pattern[] getPatternArray();
    
    PatternDocument.Pattern getPatternArray(final int p0);
    
    int sizeOfPatternArray();
    
    void setPatternArray(final PatternDocument.Pattern[] p0);
    
    void setPatternArray(final int p0, final PatternDocument.Pattern p1);
    
    PatternDocument.Pattern insertNewPattern(final int p0);
    
    PatternDocument.Pattern addNewPattern();
    
    void removePattern(final int p0);
    
    Attribute[] getAttributeArray();
    
    Attribute getAttributeArray(final int p0);
    
    int sizeOfAttributeArray();
    
    void setAttributeArray(final Attribute[] p0);
    
    void setAttributeArray(final int p0, final Attribute p1);
    
    Attribute insertNewAttribute(final int p0);
    
    Attribute addNewAttribute();
    
    void removeAttribute(final int p0);
    
    AttributeGroupRef[] getAttributeGroupArray();
    
    AttributeGroupRef getAttributeGroupArray(final int p0);
    
    int sizeOfAttributeGroupArray();
    
    void setAttributeGroupArray(final AttributeGroupRef[] p0);
    
    void setAttributeGroupArray(final int p0, final AttributeGroupRef p1);
    
    AttributeGroupRef insertNewAttributeGroup(final int p0);
    
    AttributeGroupRef addNewAttributeGroup();
    
    void removeAttributeGroup(final int p0);
    
    Wildcard getAnyAttribute();
    
    boolean isSetAnyAttribute();
    
    void setAnyAttribute(final Wildcard p0);
    
    Wildcard addNewAnyAttribute();
    
    void unsetAnyAttribute();
    
    QName getBase();
    
    XmlQName xgetBase();
    
    void setBase(final QName p0);
    
    void xsetBase(final XmlQName p0);
    
    public static final class Factory
    {
        public static RestrictionType newInstance() {
            return (RestrictionType)XmlBeans.getContextTypeLoader().newInstance(RestrictionType.type, null);
        }
        
        public static RestrictionType newInstance(final XmlOptions options) {
            return (RestrictionType)XmlBeans.getContextTypeLoader().newInstance(RestrictionType.type, options);
        }
        
        public static RestrictionType parse(final String xmlAsString) throws XmlException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, RestrictionType.type, null);
        }
        
        public static RestrictionType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, RestrictionType.type, options);
        }
        
        public static RestrictionType parse(final File file) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(file, RestrictionType.type, null);
        }
        
        public static RestrictionType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(file, RestrictionType.type, options);
        }
        
        public static RestrictionType parse(final URL u) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(u, RestrictionType.type, null);
        }
        
        public static RestrictionType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(u, RestrictionType.type, options);
        }
        
        public static RestrictionType parse(final InputStream is) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(is, RestrictionType.type, null);
        }
        
        public static RestrictionType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(is, RestrictionType.type, options);
        }
        
        public static RestrictionType parse(final Reader r) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(r, RestrictionType.type, null);
        }
        
        public static RestrictionType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(r, RestrictionType.type, options);
        }
        
        public static RestrictionType parse(final XMLStreamReader sr) throws XmlException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(sr, RestrictionType.type, null);
        }
        
        public static RestrictionType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(sr, RestrictionType.type, options);
        }
        
        public static RestrictionType parse(final Node node) throws XmlException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(node, RestrictionType.type, null);
        }
        
        public static RestrictionType parse(final Node node, final XmlOptions options) throws XmlException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(node, RestrictionType.type, options);
        }
        
        @Deprecated
        public static RestrictionType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(xis, RestrictionType.type, null);
        }
        
        @Deprecated
        public static RestrictionType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (RestrictionType)XmlBeans.getContextTypeLoader().parse(xis, RestrictionType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RestrictionType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RestrictionType.type, options);
        }
        
        private Factory() {
        }
    }
}
