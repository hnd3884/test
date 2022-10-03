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
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface RestrictionDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RestrictionDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("restriction0049doctype");
    
    Restriction getRestriction();
    
    void setRestriction(final Restriction p0);
    
    Restriction addNewRestriction();
    
    public interface Restriction extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Restriction.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("restrictionad11elemtype");
        
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
        
        QName getBase();
        
        XmlQName xgetBase();
        
        boolean isSetBase();
        
        void setBase(final QName p0);
        
        void xsetBase(final XmlQName p0);
        
        void unsetBase();
        
        public static final class Factory
        {
            public static Restriction newInstance() {
                return (Restriction)XmlBeans.getContextTypeLoader().newInstance(Restriction.type, null);
            }
            
            public static Restriction newInstance(final XmlOptions options) {
                return (Restriction)XmlBeans.getContextTypeLoader().newInstance(Restriction.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static RestrictionDocument newInstance() {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().newInstance(RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument newInstance(final XmlOptions options) {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().newInstance(RestrictionDocument.type, options);
        }
        
        public static RestrictionDocument parse(final String xmlAsString) throws XmlException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, RestrictionDocument.type, options);
        }
        
        public static RestrictionDocument parse(final File file) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(file, RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(file, RestrictionDocument.type, options);
        }
        
        public static RestrictionDocument parse(final URL u) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(u, RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(u, RestrictionDocument.type, options);
        }
        
        public static RestrictionDocument parse(final InputStream is) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(is, RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(is, RestrictionDocument.type, options);
        }
        
        public static RestrictionDocument parse(final Reader r) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(r, RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(r, RestrictionDocument.type, options);
        }
        
        public static RestrictionDocument parse(final XMLStreamReader sr) throws XmlException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(sr, RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(sr, RestrictionDocument.type, options);
        }
        
        public static RestrictionDocument parse(final Node node) throws XmlException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(node, RestrictionDocument.type, null);
        }
        
        public static RestrictionDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(node, RestrictionDocument.type, options);
        }
        
        @Deprecated
        public static RestrictionDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(xis, RestrictionDocument.type, null);
        }
        
        @Deprecated
        public static RestrictionDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (RestrictionDocument)XmlBeans.getContextTypeLoader().parse(xis, RestrictionDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RestrictionDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RestrictionDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
