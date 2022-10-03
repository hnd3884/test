package org.apache.commons.digester.substitution;

import org.xml.sax.Attributes;
import org.apache.commons.digester.Substitutor;

public class VariableSubstitutor extends Substitutor
{
    private VariableExpander attributesExpander;
    private VariableAttributes variableAttributes;
    private VariableExpander bodyTextExpander;
    
    public VariableSubstitutor(final VariableExpander expander) {
        this(expander, expander);
    }
    
    public VariableSubstitutor(final VariableExpander attributesExpander, final VariableExpander bodyTextExpander) {
        this.attributesExpander = attributesExpander;
        this.bodyTextExpander = bodyTextExpander;
        this.variableAttributes = new VariableAttributes();
    }
    
    public Attributes substitute(final Attributes attributes) {
        Attributes results = attributes;
        if (this.attributesExpander != null) {
            this.variableAttributes.init(attributes, this.attributesExpander);
            results = this.variableAttributes;
        }
        return results;
    }
    
    public String substitute(final String bodyText) {
        String result = bodyText;
        if (this.bodyTextExpander != null) {
            result = this.bodyTextExpander.expand(bodyText);
        }
        return result;
    }
}
