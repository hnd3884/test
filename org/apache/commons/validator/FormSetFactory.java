package org.apache.commons.validator;

import org.xml.sax.Attributes;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.digester.AbstractObjectCreationFactory;

public class FormSetFactory extends AbstractObjectCreationFactory
{
    private transient Log log;
    
    public FormSetFactory() {
        this.log = LogFactory.getLog(FormSetFactory.class);
    }
    
    public Object createObject(final Attributes attributes) throws Exception {
        final ValidatorResources resources = (ValidatorResources)this.digester.peek(0);
        final String language = attributes.getValue("language");
        final String country = attributes.getValue("country");
        final String variant = attributes.getValue("variant");
        return this.createFormSet(resources, language, country, variant);
    }
    
    private FormSet createFormSet(final ValidatorResources resources, final String language, final String country, final String variant) throws Exception {
        FormSet formSet = resources.getFormSet(language, country, variant);
        if (formSet != null) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)("FormSet[" + formSet.displayKey() + "] found - merging."));
            }
            return formSet;
        }
        formSet = new FormSet();
        formSet.setLanguage(language);
        formSet.setCountry(country);
        formSet.setVariant(variant);
        resources.addFormSet(formSet);
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("FormSet[" + formSet.displayKey() + "] created."));
        }
        return formSet;
    }
    
    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(FormSetFactory.class);
        }
        return this.log;
    }
}
