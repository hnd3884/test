package org.apache.poi.poifs.crypt.dsig.facets;

import org.w3c.dom.Node;
import org.etsi.uri.x01903.v13.UnsignedSignaturePropertiesType;
import org.etsi.uri.x01903.v13.UnsignedPropertiesType;
import org.w3c.dom.NodeList;
import org.apache.xmlbeans.XmlException;
import org.etsi.uri.x01903.v13.QualifyingPropertiesType;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import javax.xml.crypto.MarshalException;
import org.w3c.dom.Document;

public class Office2010SignatureFacet extends SignatureFacet
{
    @Override
    public void postSign(final Document document) throws MarshalException {
        final NodeList nl = document.getElementsByTagNameNS("http://uri.etsi.org/01903/v1.3.2#", "QualifyingProperties");
        if (nl.getLength() != 1) {
            throw new MarshalException("no XAdES-BES extension present");
        }
        QualifyingPropertiesType qualProps;
        try {
            qualProps = QualifyingPropertiesType.Factory.parse(nl.item(0), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        }
        catch (final XmlException e) {
            throw new MarshalException((Throwable)e);
        }
        UnsignedPropertiesType unsignedProps = qualProps.getUnsignedProperties();
        if (unsignedProps == null) {
            unsignedProps = qualProps.addNewUnsignedProperties();
        }
        final UnsignedSignaturePropertiesType unsignedSigProps = unsignedProps.getUnsignedSignatureProperties();
        if (unsignedSigProps == null) {
            unsignedProps.addNewUnsignedSignatureProperties();
        }
        final Node n = document.importNode(qualProps.getDomNode().getFirstChild(), true);
        nl.item(0).getParentNode().replaceChild(n, nl.item(0));
    }
}
