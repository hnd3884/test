package org.apache.taglibs.standard.tlv.el;

import org.apache.taglibs.standard.tlv.JstlCoreTLV;

public class JstlELCoreTLV extends JstlCoreTLV
{
    protected String validateExpression(final String elem, final String att, final String expr) {
        return ValidationUtil.validateExpression(elem, att, expr);
    }
}
