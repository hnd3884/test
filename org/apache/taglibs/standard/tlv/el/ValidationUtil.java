package org.apache.taglibs.standard.tlv.el;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;

public class ValidationUtil
{
    static String validateExpression(final String elem, final String att, final String expr) {
        String response = ExpressionEvaluatorManager.validate(att, expr);
        if (response != null) {
            response = "tag = '" + elem + "' / attribute = '" + att + "': " + response;
        }
        return response;
    }
}
