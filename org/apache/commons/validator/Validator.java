package org.apache.commons.validator;

import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

public class Validator implements Serializable
{
    public static final String BEAN_PARAM = "java.lang.Object";
    public static final String VALIDATOR_ACTION_PARAM = "org.apache.commons.validator.ValidatorAction";
    public static final String VALIDATOR_RESULTS_PARAM = "org.apache.commons.validator.ValidatorResults";
    public static final String FORM_PARAM = "org.apache.commons.validator.Form";
    public static final String FIELD_PARAM = "org.apache.commons.validator.Field";
    public static final String VALIDATOR_PARAM = "org.apache.commons.validator.Validator";
    public static final String LOCALE_PARAM = "java.util.Locale";
    protected ValidatorResources resources;
    protected String formName;
    protected String fieldName;
    protected Map parameters;
    protected int page;
    protected ClassLoader classLoader;
    protected boolean useContextClassLoader;
    protected boolean onlyReturnErrors;
    
    public Validator(final ValidatorResources resources) {
        this(resources, null);
    }
    
    public Validator(final ValidatorResources resources, final String formName) {
        this.resources = null;
        this.formName = null;
        this.fieldName = null;
        this.parameters = new HashMap();
        this.page = 0;
        this.classLoader = null;
        this.useContextClassLoader = false;
        this.onlyReturnErrors = false;
        if (resources == null) {
            throw new IllegalArgumentException("Resources cannot be null.");
        }
        this.resources = resources;
        this.formName = formName;
    }
    
    public Validator(final ValidatorResources resources, final String formName, final String fieldName) {
        this.resources = null;
        this.formName = null;
        this.fieldName = null;
        this.parameters = new HashMap();
        this.page = 0;
        this.classLoader = null;
        this.useContextClassLoader = false;
        this.onlyReturnErrors = false;
        if (resources == null) {
            throw new IllegalArgumentException("Resources cannot be null.");
        }
        this.resources = resources;
        this.formName = formName;
        this.fieldName = fieldName;
    }
    
    public void setParameter(final String parameterClassName, final Object parameterValue) {
        this.parameters.put(parameterClassName, parameterValue);
    }
    
    public Object getParameterValue(final String parameterClassName) {
        return this.parameters.get(parameterClassName);
    }
    
    public String getFormName() {
        return this.formName;
    }
    
    public void setFormName(final String formName) {
        this.formName = formName;
    }
    
    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }
    
    public int getPage() {
        return this.page;
    }
    
    public void setPage(final int page) {
        this.page = page;
    }
    
    public void clear() {
        this.formName = null;
        this.fieldName = null;
        this.parameters = new HashMap();
        this.page = 0;
    }
    
    public boolean getUseContextClassLoader() {
        return this.useContextClassLoader;
    }
    
    public void setUseContextClassLoader(final boolean use) {
        this.useContextClassLoader = use;
    }
    
    public ClassLoader getClassLoader() {
        if (this.classLoader != null) {
            return this.classLoader;
        }
        if (this.useContextClassLoader) {
            final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
            if (contextLoader != null) {
                return contextLoader;
            }
        }
        return this.getClass().getClassLoader();
    }
    
    public void setClassLoader(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public ValidatorResults validate() throws ValidatorException {
        Locale locale = (Locale)this.getParameterValue("java.util.Locale");
        if (locale == null) {
            locale = Locale.getDefault();
        }
        this.setParameter("org.apache.commons.validator.Validator", this);
        final Form form = this.resources.getForm(locale, this.formName);
        if (form != null) {
            this.setParameter("org.apache.commons.validator.Form", form);
            return form.validate(this.parameters, this.resources.getValidatorActions(), this.page, this.fieldName);
        }
        return new ValidatorResults();
    }
    
    public boolean getOnlyReturnErrors() {
        return this.onlyReturnErrors;
    }
    
    public void setOnlyReturnErrors(final boolean onlyReturnErrors) {
        this.onlyReturnErrors = onlyReturnErrors;
    }
}
