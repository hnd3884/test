package org.apache.poi.poifs.crypt.dsig.facets;

import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.Transform;
import java.util.ArrayList;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.Reference;
import java.util.List;
import org.w3c.dom.Document;

public class EnvelopedSignatureFacet extends SignatureFacet
{
    @Override
    public void preSign(final Document document, final List<Reference> references, final List<XMLObject> objects) throws XMLSignatureException {
        final List<Transform> transforms = new ArrayList<Transform>();
        final Transform envelopedTransform = this.newTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
        transforms.add(envelopedTransform);
        final Transform exclusiveTransform = this.newTransform("http://www.w3.org/2001/10/xml-exc-c14n#");
        transforms.add(exclusiveTransform);
        final Reference reference = this.newReference("", transforms, null, null, null);
        references.add(reference);
    }
}
