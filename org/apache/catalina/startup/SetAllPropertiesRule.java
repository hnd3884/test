package org.apache.catalina.startup;

import org.apache.tomcat.util.IntrospectionUtils;
import org.xml.sax.Attributes;
import java.util.HashMap;
import org.apache.tomcat.util.digester.Rule;

public class SetAllPropertiesRule extends Rule
{
    protected final HashMap<String, String> excludes;
    
    public SetAllPropertiesRule() {
        this.excludes = new HashMap<String, String>();
    }
    
    public SetAllPropertiesRule(final String[] exclude) {
        this.excludes = new HashMap<String, String>();
        for (final String s : exclude) {
            if (s != null) {
                this.excludes.put(s, s);
            }
        }
    }
    
    public void begin(final String namespace, final String nameX, final Attributes attributes) throws Exception {
        for (int i = 0; i < attributes.getLength(); ++i) {
            String name = attributes.getLocalName(i);
            if ("".equals(name)) {
                name = attributes.getQName(i);
            }
            final String value = attributes.getValue(i);
            if (!this.excludes.containsKey(name) && !this.digester.isFakeAttribute(this.digester.peek(), name) && !IntrospectionUtils.setProperty(this.digester.peek(), name, value) && this.digester.getRulesValidation()) {
                this.digester.getLogger().warn((Object)("[SetAllPropertiesRule]{" + this.digester.getMatch() + "} Setting property '" + name + "' to '" + value + "' did not find a matching property."));
            }
        }
    }
}
