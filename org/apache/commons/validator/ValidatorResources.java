package org.apache.commons.validator;

import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import org.xml.sax.Attributes;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.xml.sax.InputSource;
import java.net.URL;
import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.LogFactory;
import java.util.Locale;
import org.apache.commons.collections.FastHashMap;
import org.apache.commons.logging.Log;
import java.io.Serializable;

public class ValidatorResources implements Serializable
{
    private static final String VALIDATOR_RULES = "digester-rules.xml";
    private static final String[] REGISTRATIONS;
    private transient Log log;
    protected FastHashMap hFormSets;
    protected FastHashMap hConstants;
    protected FastHashMap hActions;
    protected static Locale defaultLocale;
    protected FormSet defaultFormSet;
    private static final String ARGS_PATTERN = "form-validation/formset/form/field/arg";
    
    public ValidatorResources() {
        this.log = LogFactory.getLog(ValidatorResources.class);
        this.hFormSets = new FastHashMap();
        this.hConstants = new FastHashMap();
        this.hActions = new FastHashMap();
    }
    
    public ValidatorResources(final InputStream in) throws IOException, SAXException {
        this(new InputStream[] { in });
    }
    
    public ValidatorResources(final InputStream[] streams) throws IOException, SAXException {
        this.log = LogFactory.getLog(ValidatorResources.class);
        this.hFormSets = new FastHashMap();
        this.hConstants = new FastHashMap();
        this.hActions = new FastHashMap();
        final Digester digester = this.initDigester();
        for (int i = 0; i < streams.length; ++i) {
            digester.push((Object)this);
            digester.parse(streams[i]);
        }
        this.process();
    }
    
    public ValidatorResources(final String uri) throws IOException, SAXException {
        this(new String[] { uri });
    }
    
    public ValidatorResources(final String[] uris) throws IOException, SAXException {
        this.log = LogFactory.getLog(ValidatorResources.class);
        this.hFormSets = new FastHashMap();
        this.hConstants = new FastHashMap();
        this.hActions = new FastHashMap();
        final Digester digester = this.initDigester();
        for (int i = 0; i < uris.length; ++i) {
            digester.push((Object)this);
            digester.parse(uris[i]);
        }
        this.process();
    }
    
    public ValidatorResources(final URL url) throws IOException, SAXException {
        this(new URL[] { url });
    }
    
    public ValidatorResources(final URL[] urls) throws IOException, SAXException {
        this.log = LogFactory.getLog(ValidatorResources.class);
        this.hFormSets = new FastHashMap();
        this.hConstants = new FastHashMap();
        this.hActions = new FastHashMap();
        final Digester digester = this.initDigester();
        for (int i = 0; i < urls.length; ++i) {
            digester.push((Object)this);
            InputStream stream = null;
            try {
                stream = urls[i].openStream();
                final InputSource source = new InputSource(urls[i].toExternalForm());
                source.setByteStream(stream);
                digester.parse(source);
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (final IOException ex) {}
                }
            }
        }
        this.process();
    }
    
    private Digester initDigester() {
        URL rulesUrl = this.getClass().getResource("digester-rules.xml");
        if (rulesUrl == null) {
            rulesUrl = ValidatorResources.class.getResource("digester-rules.xml");
        }
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Loading rules from '" + rulesUrl + "'"));
        }
        final Digester digester = DigesterLoader.createDigester(rulesUrl);
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setUseContextClassLoader(true);
        this.addOldArgRules(digester);
        for (int i = 0; i < ValidatorResources.REGISTRATIONS.length; i += 2) {
            final URL url = this.getClass().getResource(ValidatorResources.REGISTRATIONS[i + 1]);
            if (url != null) {
                digester.register(ValidatorResources.REGISTRATIONS[i], url.toString());
            }
        }
        return digester;
    }
    
    private void addOldArgRules(final Digester digester) {
        final Rule rule = new Rule() {
            public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
                final Arg arg = new Arg();
                arg.setKey(attributes.getValue("key"));
                arg.setName(attributes.getValue("name"));
                if ("false".equalsIgnoreCase(attributes.getValue("resource"))) {
                    arg.setResource(false);
                }
                try {
                    arg.setPosition(Integer.parseInt(name.substring(3)));
                }
                catch (final Exception ex) {
                    ValidatorResources.this.getLog().error((Object)("Error parsing Arg position: " + name + " " + arg + " " + ex));
                }
                ((Field)this.getDigester().peek(0)).addArg(arg);
            }
        };
        digester.addRule("form-validation/formset/form/field/arg0", rule);
        digester.addRule("form-validation/formset/form/field/arg1", rule);
        digester.addRule("form-validation/formset/form/field/arg2", rule);
        digester.addRule("form-validation/formset/form/field/arg3", rule);
    }
    
    public void addFormSet(final FormSet fs) {
        final String key = this.buildKey(fs);
        if (key.length() == 0) {
            if (this.getLog().isWarnEnabled() && this.defaultFormSet != null) {
                this.getLog().warn((Object)"Overriding default FormSet definition.");
            }
            this.defaultFormSet = fs;
        }
        else {
            final FormSet formset = (FormSet)this.hFormSets.get((Object)key);
            if (formset == null) {
                if (this.getLog().isDebugEnabled()) {
                    this.getLog().debug((Object)("Adding FormSet '" + fs.toString() + "'."));
                }
            }
            else if (this.getLog().isWarnEnabled()) {
                this.getLog().warn((Object)("Overriding FormSet definition. Duplicate for locale: " + key));
            }
            this.hFormSets.put((Object)key, (Object)fs);
        }
    }
    
    public void addConstant(final String name, final String value) {
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Adding Global Constant: " + name + "," + value));
        }
        this.hConstants.put((Object)name, (Object)value);
    }
    
    public void addValidatorAction(final ValidatorAction va) {
        va.init();
        this.hActions.put((Object)va.getName(), (Object)va);
        if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Add ValidatorAction: " + va.getName() + "," + va.getClassname()));
        }
    }
    
    public ValidatorAction getValidatorAction(final String key) {
        return (ValidatorAction)this.hActions.get((Object)key);
    }
    
    public Map getValidatorActions() {
        return Collections.unmodifiableMap((Map<?, ?>)this.hActions);
    }
    
    protected String buildKey(final FormSet fs) {
        return this.buildLocale(fs.getLanguage(), fs.getCountry(), fs.getVariant());
    }
    
    private String buildLocale(final String lang, final String country, final String variant) {
        String key = (lang != null && lang.length() > 0) ? lang : "";
        key += ((country != null && country.length() > 0) ? ("_" + country) : "");
        key += ((variant != null && variant.length() > 0) ? ("_" + variant) : "");
        return key;
    }
    
    public Form getForm(final Locale locale, final String formKey) {
        return this.getForm(locale.getLanguage(), locale.getCountry(), locale.getVariant(), formKey);
    }
    
    public Form getForm(final String language, final String country, final String variant, final String formKey) {
        Form form = null;
        String key = this.buildLocale(language, country, variant);
        if (key.length() > 0) {
            final FormSet formSet = (FormSet)this.hFormSets.get((Object)key);
            if (formSet != null) {
                form = formSet.getForm(formKey);
            }
        }
        final String localeKey = key;
        if (form == null) {
            key = this.buildLocale(language, country, null);
            if (key.length() > 0) {
                final FormSet formSet2 = (FormSet)this.hFormSets.get((Object)key);
                if (formSet2 != null) {
                    form = formSet2.getForm(formKey);
                }
            }
        }
        if (form == null) {
            key = this.buildLocale(language, null, null);
            if (key.length() > 0) {
                final FormSet formSet2 = (FormSet)this.hFormSets.get((Object)key);
                if (formSet2 != null) {
                    form = formSet2.getForm(formKey);
                }
            }
        }
        if (form == null) {
            form = this.defaultFormSet.getForm(formKey);
            key = "default";
        }
        if (form == null) {
            if (this.getLog().isWarnEnabled()) {
                this.getLog().warn((Object)("Form '" + formKey + "' not found for locale '" + localeKey + "'"));
            }
        }
        else if (this.getLog().isDebugEnabled()) {
            this.getLog().debug((Object)("Form '" + formKey + "' found in formset '" + key + "' for locale '" + localeKey + "'"));
        }
        return form;
    }
    
    public void process() {
        this.hFormSets.setFast(true);
        this.hConstants.setFast(true);
        this.hActions.setFast(true);
        this.processForms();
    }
    
    private void processForms() {
        if (this.defaultFormSet == null) {
            this.defaultFormSet = new FormSet();
        }
        this.defaultFormSet.process((Map)this.hConstants);
        final Iterator i = this.hFormSets.keySet().iterator();
        while (i.hasNext()) {
            final String key = i.next();
            final FormSet fs = (FormSet)this.hFormSets.get((Object)key);
            fs.merge(this.getParent(fs));
        }
        final Iterator j = this.hFormSets.values().iterator();
        while (j.hasNext()) {
            final FormSet fs = j.next();
            if (!fs.isProcessed()) {
                fs.process((Map)this.hConstants);
            }
        }
    }
    
    private FormSet getParent(final FormSet fs) {
        FormSet parent = null;
        if (fs.getType() == 2) {
            parent = this.defaultFormSet;
        }
        else if (fs.getType() == 3) {
            parent = (FormSet)this.hFormSets.get((Object)this.buildLocale(fs.getLanguage(), null, null));
            if (parent == null) {
                parent = this.defaultFormSet;
            }
        }
        else if (fs.getType() == 4) {
            parent = (FormSet)this.hFormSets.get((Object)this.buildLocale(fs.getLanguage(), fs.getCountry(), null));
            if (parent == null) {
                parent = (FormSet)this.hFormSets.get((Object)this.buildLocale(fs.getLanguage(), null, null));
                if (parent == null) {
                    parent = this.defaultFormSet;
                }
            }
        }
        return parent;
    }
    
    FormSet getFormSet(final String language, final String country, final String variant) {
        final String key = this.buildLocale(language, country, variant);
        if (key.length() == 0) {
            return this.defaultFormSet;
        }
        return (FormSet)this.hFormSets.get((Object)key);
    }
    
    protected Map getFormSets() {
        return (Map)this.hFormSets;
    }
    
    protected Map getConstants() {
        return (Map)this.hConstants;
    }
    
    protected Map getActions() {
        return (Map)this.hActions;
    }
    
    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(ValidatorResources.class);
        }
        return this.log;
    }
    
    static {
        REGISTRATIONS = new String[] { "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0//EN", "/org/apache/commons/validator/resources/validator_1_0.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.0.1//EN", "/org/apache/commons/validator/resources/validator_1_0_1.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1//EN", "/org/apache/commons/validator/resources/validator_1_1.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.1.3//EN", "/org/apache/commons/validator/resources/validator_1_1_3.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.2.0//EN", "/org/apache/commons/validator/resources/validator_1_2_0.dtd", "-//Apache Software Foundation//DTD Commons Validator Rules Configuration 1.3.0//EN", "/org/apache/commons/validator/resources/validator_1_3_0.dtd" };
        ValidatorResources.defaultLocale = Locale.getDefault();
    }
}
