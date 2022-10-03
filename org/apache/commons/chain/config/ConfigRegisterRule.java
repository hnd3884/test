package org.apache.commons.chain.config;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Catalog;
import org.apache.commons.chain.Command;
import org.xml.sax.Attributes;
import org.apache.commons.digester.Rule;

class ConfigRegisterRule extends Rule
{
    private String nameAttribute;
    
    public ConfigRegisterRule(final String nameAttribute) {
        this.nameAttribute = null;
        this.nameAttribute = nameAttribute;
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        final Object top = this.digester.peek(0);
        if (top == null || !(top instanceof Command)) {
            return;
        }
        final Command command = (Command)top;
        final Object next = this.digester.peek(1);
        if (next == null) {
            return;
        }
        if (next instanceof Catalog) {
            final String nameValue = attributes.getValue(this.nameAttribute);
            if (nameValue != null) {
                ((Catalog)next).addCommand(nameValue, command);
            }
        }
        else if (next instanceof Chain) {
            ((Chain)next).addCommand(command);
        }
    }
}
