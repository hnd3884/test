package org.apache.taglibs.standard.tlv.el;

import org.apache.taglibs.standard.tlv.JstlXmlTLV;

public class JstlELXmlTLV extends JstlXmlTLV
{
    protected String validateExpression(final String elem, final String att, final String expr) {
        return ValidationUtil.validateExpression(elem, att, expr);
    }
}
