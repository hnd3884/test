package com.sun.org.apache.xpath.internal.domapi;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

public final class XPathStylesheetDOM3Exception extends TransformerException
{
    public XPathStylesheetDOM3Exception(final String msg, final SourceLocator arg1) {
        super(msg, arg1);
    }
}
