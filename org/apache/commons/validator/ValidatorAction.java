package org.apache.commons.validator;

import org.apache.commons.validator.util.ValidatorUtils;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.commons.logging.LogFactory;
import java.util.List;
import java.lang.reflect.Method;
import org.apache.commons.logging.Log;
import java.io.Serializable;

public class ValidatorAction implements Serializable
{
    private transient Log log;
    private String name;
    private String classname;
    private Class validationClass;
    private String method;
    private Method validationMethod;
    private String methodParams;
    private Class[] parameterClasses;
    private String depends;
    private String msg;
    private String jsFunctionName;
    private String jsFunction;
    private String javascript;
    private Object instance;
    private List dependencyList;
    private List methodParameterList;
    
    public ValidatorAction() {
        this.log = LogFactory.getLog(ValidatorAction.class);
        this.name = null;
        this.classname = null;
        this.validationClass = null;
        this.method = null;
        this.validationMethod = null;
        this.methodParams = "java.lang.Object,org.apache.commons.validator.ValidatorAction,org.apache.commons.validator.Field";
        this.parameterClasses = null;
        this.depends = null;
        this.msg = null;
        this.jsFunctionName = null;
        this.jsFunction = null;
        this.javascript = null;
        this.instance = null;
        this.dependencyList = Collections.synchronizedList(new ArrayList<Object>());
        this.methodParameterList = new ArrayList();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getClassname() {
        return this.classname;
    }
    
    public void setClassname(final String classname) {
        this.classname = classname;
    }
    
    public String getMethod() {
        return this.method;
    }
    
    public void setMethod(final String method) {
        this.method = method;
    }
    
    public String getMethodParams() {
        return this.methodParams;
    }
    
    public void setMethodParams(final String methodParams) {
        this.methodParams = methodParams;
        this.methodParameterList.clear();
        final StringTokenizer st = new StringTokenizer(methodParams, ",");
        while (st.hasMoreTokens()) {
            final String value = st.nextToken().trim();
            if (value != null && value.length() > 0) {
                this.methodParameterList.add(value);
            }
        }
    }
    
    public String getDepends() {
        return this.depends;
    }
    
    public void setDepends(final String depends) {
        this.depends = depends;
        this.dependencyList.clear();
        final StringTokenizer st = new StringTokenizer(depends, ",");
        while (st.hasMoreTokens()) {
            final String depend = st.nextToken().trim();
            if (depend != null && depend.length() > 0) {
                this.dependencyList.add(depend);
            }
        }
    }
    
    public String getMsg() {
        return this.msg;
    }
    
    public void setMsg(final String msg) {
        this.msg = msg;
    }
    
    public String getJsFunctionName() {
        return this.jsFunctionName;
    }
    
    public void setJsFunctionName(final String jsFunctionName) {
        this.jsFunctionName = jsFunctionName;
    }
    
    public void setJsFunction(final String jsFunction) {
        if (this.javascript != null) {
            throw new IllegalStateException("Cannot call setJsFunction() after calling setJavascript()");
        }
        this.jsFunction = jsFunction;
    }
    
    public String getJavascript() {
        return this.javascript;
    }
    
    public void setJavascript(final String javascript) {
        if (this.jsFunction != null) {
            throw new IllegalStateException("Cannot call setJavascript() after calling setJsFunction()");
        }
        this.javascript = javascript;
    }
    
    protected void init() {
        this.loadJavascriptFunction();
    }
    
    protected synchronized void loadJavascriptFunction() {
        if (this.javascriptAlreadyLoaded()) {
            return;
        }
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)"  Loading function begun");
        }
        if (this.jsFunction == null) {
            this.jsFunction = this.generateJsFunction();
        }
        final String javascriptFileName = this.formatJavascriptFileName();
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)("  Loading js function '" + javascriptFileName + "'"));
        }
        this.javascript = this.readJavascriptFile(javascriptFileName);
        if (this.getLog().isTraceEnabled()) {
            this.getLog().trace((Object)"  Loading javascript function completed");
        }
    }
    
    private String readJavascriptFile(final String javascriptFileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        }
        InputStream is = classLoader.getResourceAsStream(javascriptFileName);
        if (is == null) {
            is = this.getClass().getResourceAsStream(javascriptFileName);
        }
        if (is == null) {
            this.getLog().debug((Object)("  Unable to read javascript name " + javascriptFileName));
            return null;
        }
        final StringBuffer buffer = new StringBuffer();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
        }
        catch (final IOException e) {
            this.getLog().error((Object)"Error reading javascript file.", (Throwable)e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e2) {
                this.getLog().error((Object)"Error closing stream to javascript file.", (Throwable)e2);
            }
        }
        final String function = buffer.toString();
        return function.equals("") ? null : function;
    }
    
    private String formatJavascriptFileName() {
        String name = this.jsFunction.substring(1);
        if (!this.jsFunction.startsWith("/")) {
            name = this.jsFunction.replace('.', '/') + ".js";
        }
        return name;
    }
    
    private boolean javascriptAlreadyLoaded() {
        return this.javascript != null;
    }
    
    private String generateJsFunction() {
        final StringBuffer jsName = new StringBuffer("org.apache.commons.validator.javascript");
        jsName.append(".validate");
        jsName.append(this.name.substring(0, 1).toUpperCase());
        jsName.append(this.name.substring(1, this.name.length()));
        return jsName.toString();
    }
    
    public boolean isDependency(final String validatorName) {
        return this.dependencyList.contains(validatorName);
    }
    
    public List getDependencyList() {
        return Collections.unmodifiableList((List<?>)this.dependencyList);
    }
    
    public String toString() {
        final StringBuffer results = new StringBuffer("ValidatorAction: ");
        results.append(this.name);
        results.append("\n");
        return results.toString();
    }
    
    boolean executeValidationMethod(final Field field, final Map params, final ValidatorResults results, final int pos) throws ValidatorException {
        params.put("org.apache.commons.validator.ValidatorAction", this);
        try {
            if (this.validationMethod == null) {
                synchronized (this) {
                    final ClassLoader loader = this.getClassLoader(params);
                    this.loadValidationClass(loader);
                    this.loadParameterClasses(loader);
                    this.loadValidationMethod();
                }
            }
            final Object[] paramValues = this.getParameterValues(params);
            if (field.isIndexed()) {
                this.handleIndexedField(field, pos, paramValues);
            }
            Object result = null;
            try {
                result = this.validationMethod.invoke(this.getValidationClassInstance(), paramValues);
            }
            catch (final IllegalArgumentException e) {
                throw new ValidatorException(e.getMessage());
            }
            catch (final IllegalAccessException e2) {
                throw new ValidatorException(e2.getMessage());
            }
            catch (final InvocationTargetException e3) {
                if (e3.getTargetException() instanceof Exception) {
                    throw (Exception)e3.getTargetException();
                }
                if (e3.getTargetException() instanceof Error) {
                    throw (Error)e3.getTargetException();
                }
            }
            final boolean valid = this.isValid(result);
            if (!valid || (valid && !this.onlyReturnErrors(params))) {
                results.add(field, this.name, valid, result);
            }
            if (!valid) {
                return false;
            }
        }
        catch (final Exception e4) {
            if (e4 instanceof ValidatorException) {
                throw (ValidatorException)e4;
            }
            this.getLog().error((Object)("Unhandled exception thrown during validation: " + e4.getMessage()), (Throwable)e4);
            results.add(field, this.name, false);
            return false;
        }
        return true;
    }
    
    private void loadValidationMethod() throws ValidatorException {
        if (this.validationMethod != null) {
            return;
        }
        try {
            this.validationMethod = this.validationClass.getMethod(this.method, (Class[])this.parameterClasses);
        }
        catch (final NoSuchMethodException e) {
            throw new ValidatorException("No such validation method: " + e.getMessage());
        }
    }
    
    private void loadValidationClass(final ClassLoader loader) throws ValidatorException {
        if (this.validationClass != null) {
            return;
        }
        try {
            this.validationClass = loader.loadClass(this.classname);
        }
        catch (final ClassNotFoundException e) {
            throw new ValidatorException(e.toString());
        }
    }
    
    private void loadParameterClasses(final ClassLoader loader) throws ValidatorException {
        if (this.parameterClasses != null) {
            return;
        }
        final Class[] parameterClasses = new Class[this.methodParameterList.size()];
        for (int i = 0; i < this.methodParameterList.size(); ++i) {
            final String paramClassName = this.methodParameterList.get(i);
            try {
                parameterClasses[i] = loader.loadClass(paramClassName);
            }
            catch (final ClassNotFoundException e) {
                throw new ValidatorException(e.getMessage());
            }
        }
        this.parameterClasses = parameterClasses;
    }
    
    private Object[] getParameterValues(final Map params) {
        final Object[] paramValue = new Object[this.methodParameterList.size()];
        for (int i = 0; i < this.methodParameterList.size(); ++i) {
            final String paramClassName = this.methodParameterList.get(i);
            paramValue[i] = params.get(paramClassName);
        }
        return paramValue;
    }
    
    private Object getValidationClassInstance() throws ValidatorException {
        if (Modifier.isStatic(this.validationMethod.getModifiers())) {
            this.instance = null;
        }
        else if (this.instance == null) {
            try {
                this.instance = this.validationClass.newInstance();
            }
            catch (final InstantiationException e) {
                final String msg = "Couldn't create instance of " + this.classname + ".  " + e.getMessage();
                throw new ValidatorException(msg);
            }
            catch (final IllegalAccessException e2) {
                final String msg2 = "Couldn't create instance of " + this.classname + ".  " + e2.getMessage();
                throw new ValidatorException(msg2);
            }
        }
        return this.instance;
    }
    
    private void handleIndexedField(final Field field, final int pos, final Object[] paramValues) throws ValidatorException {
        final int beanIndex = this.methodParameterList.indexOf("java.lang.Object");
        final int fieldIndex = this.methodParameterList.indexOf("org.apache.commons.validator.Field");
        final Object[] indexedList = field.getIndexedProperty(paramValues[beanIndex]);
        paramValues[beanIndex] = indexedList[pos];
        final Field indexedField = (Field)field.clone();
        indexedField.setKey(ValidatorUtils.replace(indexedField.getKey(), "[]", "[" + pos + "]"));
        paramValues[fieldIndex] = indexedField;
    }
    
    private boolean isValid(final Object result) {
        if (result instanceof Boolean) {
            final Boolean valid = (Boolean)result;
            return valid;
        }
        return result != null;
    }
    
    private ClassLoader getClassLoader(final Map params) {
        final Validator v = params.get("org.apache.commons.validator.Validator");
        return v.getClassLoader();
    }
    
    private boolean onlyReturnErrors(final Map params) {
        final Validator v = params.get("org.apache.commons.validator.Validator");
        return v.getOnlyReturnErrors();
    }
    
    private Log getLog() {
        if (this.log == null) {
            this.log = LogFactory.getLog(ValidatorAction.class);
        }
        return this.log;
    }
}
