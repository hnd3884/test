package org.apache.commons.validator;

import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.commons.logging.Log;
import java.io.Serializable;

public class FormSet implements Serializable
{
    private transient Log log;
    private boolean processed;
    private String language;
    private String country;
    private String variant;
    private Map forms;
    private Map constants;
    protected static final int GLOBAL_FORMSET = 1;
    protected static final int LANGUAGE_FORMSET = 2;
    protected static final int COUNTRY_FORMSET = 3;
    protected static final int VARIANT_FORMSET = 4;
    private boolean merged;
    
    public FormSet() {
        this.log = LogFactory.getLog(FormSet.class);
        this.processed = false;
        this.language = null;
        this.country = null;
        this.variant = null;
        this.forms = new HashMap();
        this.constants = new HashMap();
    }
    
    protected boolean isMerged() {
        return this.merged;
    }
    
    protected int getType() {
        if (this.getVariant() != null) {
            if (this.getLanguage() == null || this.getCountry() == null) {
                throw new NullPointerException("When variant is specified, country and language must be specified.");
            }
            return 4;
        }
        else if (this.getCountry() != null) {
            if (this.getLanguage() == null) {
                throw new NullPointerException("When country is specified, language must be specified.");
            }
            return 3;
        }
        else {
            if (this.getLanguage() != null) {
                return 2;
            }
            return 1;
        }
    }
    
    protected void merge(final FormSet depends) {
        if (depends != null) {
            final Map pForms = this.getForms();
            final Map dForms = depends.getForms();
            final Iterator it = dForms.keySet().iterator();
            while (it.hasNext()) {
                final Object key = it.next();
                final Form pForm = pForms.get(key);
                if (pForm != null) {
                    pForm.merge(dForms.get(key));
                }
                else {
                    this.addForm(dForms.get(key));
                }
            }
        }
        this.merged = true;
    }
    
    public boolean isProcessed() {
        return this.processed;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public void setLanguage(final String language) {
        this.language = language;
    }
    
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(final String country) {
        this.country = country;
    }
    
    public String getVariant() {
        return this.variant;
    }
    
    public void setVariant(final String variant) {
        this.variant = variant;
    }
    
    public void addConstant(final String name, final String value) {
        if (this.constants.containsKey(name)) {
            this.getLog().error((Object)("Constant '" + name + "' already exists in FormSet[" + this.displayKey() + "] - ignoring."));
        }
        else {
            this.constants.put(name, value);
        }
    }
    
    public void addForm(final Form f) {
        final String formName = f.getName();
        if (this.forms.containsKey(formName)) {
            this.getLog().error((Object)("Form '" + formName + "' already exists in FormSet[" + this.displayKey() + "] - ignoring."));
        }
        else {
            this.forms.put(f.getName(), f);
        }
    }
    
    public Form getForm(final String formName) {
        return this.forms.get(formName);
    }
    
    public Map getForms() {
        return Collections.unmodifiableMap((Map<?, ?>)this.forms);
    }
    
    synchronized void process(final Map globalConstants) {
        final Iterator i = this.forms.values().iterator();
        while (i.hasNext()) {
            final Form f = i.next();
            f.process(globalConstants, this.constants, this.forms);
        }
        this.processed = true;
    }
    
    public String displayKey() {
        final StringBuffer results = new StringBuffer();
        if (this.language != null && this.language.length() > 0) {
            results.append("language=");
            results.append(this.language);
        }
        if (this.country != null && this.country.length() > 0) {
            if (results.length() > 0) {
                results.append(", ");
            }
            results.append("country=");
            results.append(this.country);
        }
        if (this.variant != null && this.variant.length() > 0) {
            if (results.length() > 0) {
                results.append(", ");
            }
            results.append("variant=");
            results.append(this.variant);
        }
        if (results.length() == 0) {
            results.append("default");
        }
        return results.toString();
    }
    
    public String toString() {
        final StringBuffer results = new StringBuffer();
        results.append("FormSet: language=");
        results.append(this.language);
        results.append("  country=");
        results.append(this.country);
        results.append("  variant=");
        results.append(this.variant);
        results.append("\n");
        final Iterator i = this.getForms().values().iterator();
        while (i.hasNext()) {
            results.append("   ");
            results.append(i.next());
            results.append("\n");
        }
        return results.toString();
    }
    
    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(FormSet.class);
        }
        return this.log;
    }
}
