package com.zoho.security.appfirewall;

import org.xml.sax.SAXException;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import java.util.List;

public class RuleConverterFactory
{
    static List<AppFirewallRule> convert(final Object rulesObject) throws JSONException, SAXException {
        if (rulesObject instanceof Document) {
            final AppXMLRuleConverter converter = new AppXMLRuleConverter();
            return converter.convert(rulesObject);
        }
        if (rulesObject instanceof JSONObject) {
            final AppSenseRuleConverter converter2 = new AppSenseRuleConverter();
            return converter2.convert(rulesObject);
        }
        if (rulesObject instanceof JSONArray) {
            final AppSenseLocalFileConverter converter3 = new AppSenseLocalFileConverter();
            return converter3.convert(rulesObject);
        }
        return null;
    }
}
