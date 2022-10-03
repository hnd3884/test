package com.sun.org.apache.xpath.internal.functions;

import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.XPathContext;

public class FuncLang extends FunctionOneArg
{
    static final long serialVersionUID = -7868705139354872185L;
    
    @Override
    public XObject execute(final XPathContext xctxt) throws TransformerException {
        final String lang = this.m_arg0.execute(xctxt).str();
        int parent = xctxt.getCurrentNode();
        boolean isLang = false;
        for (DTM dtm = xctxt.getDTM(parent); -1 != parent; parent = dtm.getParent(parent)) {
            if (1 == dtm.getNodeType(parent)) {
                final int langAttr = dtm.getAttributeNode(parent, "http://www.w3.org/XML/1998/namespace", "lang");
                if (-1 != langAttr) {
                    final String langVal = dtm.getNodeValue(langAttr);
                    if (langVal.toLowerCase().startsWith(lang.toLowerCase())) {
                        final int valLen = lang.length();
                        if (langVal.length() == valLen || langVal.charAt(valLen) == '-') {
                            isLang = true;
                        }
                        break;
                    }
                    break;
                }
            }
        }
        return isLang ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
}
