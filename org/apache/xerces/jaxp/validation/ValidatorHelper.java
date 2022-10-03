package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;

interface ValidatorHelper
{
    void validate(final Source p0, final Result p1) throws SAXException, IOException;
}
