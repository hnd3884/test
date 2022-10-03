package org.apache.commons.validator;

import java.util.Collection;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.PropertyUtils;
import java.util.Iterator;
import org.apache.commons.validator.util.ValidatorUtils;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.collections.FastHashMap;
import java.util.List;
import java.io.Serializable;

public class Field implements Cloneable, Serializable
{
    private static final String DEFAULT_ARG = "org.apache.commons.validator.Field.DEFAULT";
    public static final String TOKEN_INDEXED = "[]";
    protected static final String TOKEN_START = "${";
    protected static final String TOKEN_END = "}";
    protected static final String TOKEN_VAR = "var:";
    protected String property;
    protected String indexedProperty;
    protected String indexedListProperty;
    protected String key;
    protected String depends;
    protected int page;
    protected int fieldOrder;
    private List dependencyList;
    protected FastHashMap hVars;
    protected FastHashMap hMsgs;
    protected Map[] args;
    
    public Field() {
        this.property = null;
        this.indexedProperty = null;
        this.indexedListProperty = null;
        this.key = null;
        this.depends = null;
        this.page = 0;
        this.fieldOrder = 0;
        this.dependencyList = Collections.synchronizedList(new ArrayList<Object>());
        this.hVars = new FastHashMap();
        this.hMsgs = new FastHashMap();
        this.args = new Map[0];
    }
    
    public int getPage() {
        return this.page;
    }
    
    public void setPage(final int page) {
        this.page = page;
    }
    
    public int getFieldOrder() {
        return this.fieldOrder;
    }
    
    public void setFieldOrder(final int fieldOrder) {
        this.fieldOrder = fieldOrder;
    }
    
    public String getProperty() {
        return this.property;
    }
    
    public void setProperty(final String property) {
        this.property = property;
    }
    
    public String getIndexedProperty() {
        return this.indexedProperty;
    }
    
    public void setIndexedProperty(final String indexedProperty) {
        this.indexedProperty = indexedProperty;
    }
    
    public String getIndexedListProperty() {
        return this.indexedListProperty;
    }
    
    public void setIndexedListProperty(final String indexedListProperty) {
        this.indexedListProperty = indexedListProperty;
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
    
    public void addMsg(final Msg msg) {
        this.hMsgs.put((Object)msg.getName(), (Object)msg);
    }
    
    public String getMsg(final String key) {
        final Msg msg = this.getMessage(key);
        return (msg == null) ? null : msg.getKey();
    }
    
    public Msg getMessage(final String key) {
        return (Msg)this.hMsgs.get((Object)key);
    }
    
    public Map getMessages() {
        return Collections.unmodifiableMap((Map<?, ?>)this.hMsgs);
    }
    
    public void addArg(final Arg arg) {
        if (arg == null || arg.getKey() == null || arg.getKey().length() == 0) {
            return;
        }
        this.determineArgPosition(arg);
        this.ensureArgsCapacity(arg);
        Map argMap = this.args[arg.getPosition()];
        if (argMap == null) {
            argMap = new HashMap();
            this.args[arg.getPosition()] = argMap;
        }
        if (arg.getName() == null) {
            argMap.put("org.apache.commons.validator.Field.DEFAULT", arg);
        }
        else {
            argMap.put(arg.getName(), arg);
        }
    }
    
    private void determineArgPosition(final Arg arg) {
        final int position = arg.getPosition();
        if (position >= 0) {
            return;
        }
        if (this.args == null || this.args.length == 0) {
            arg.setPosition(0);
            return;
        }
        final String key = (arg.getName() == null) ? "org.apache.commons.validator.Field.DEFAULT" : arg.getName();
        int lastPosition = -1;
        int lastDefault = -1;
        for (int i = 0; i < this.args.length; ++i) {
            if (this.args[i] != null && this.args[i].containsKey(key)) {
                lastPosition = i;
            }
            if (this.args[i] != null && this.args[i].containsKey("org.apache.commons.validator.Field.DEFAULT")) {
                lastDefault = i;
            }
        }
        if (lastPosition < 0) {
            lastPosition = lastDefault;
        }
        arg.setPosition(++lastPosition);
    }
    
    private void ensureArgsCapacity(final Arg arg) {
        if (arg.getPosition() >= this.args.length) {
            final Map[] newArgs = new Map[arg.getPosition() + 1];
            System.arraycopy(this.args, 0, newArgs, 0, this.args.length);
            this.args = newArgs;
        }
    }
    
    public Arg getArg(final int position) {
        return this.getArg("org.apache.commons.validator.Field.DEFAULT", position);
    }
    
    public Arg getArg(final String key, final int position) {
        if (position >= this.args.length || this.args[position] == null) {
            return null;
        }
        final Arg arg = this.args[position].get(key);
        if (arg == null && key.equals("org.apache.commons.validator.Field.DEFAULT")) {
            return null;
        }
        return (arg == null) ? this.getArg(position) : arg;
    }
    
    public Arg[] getArgs(final String key) {
        final Arg[] args = new Arg[this.args.length];
        for (int i = 0; i < this.args.length; ++i) {
            args[i] = this.getArg(key, i);
        }
        return args;
    }
    
    public void addVar(final Var v) {
        this.hVars.put((Object)v.getName(), (Object)v);
    }
    
    public void addVar(final String name, final String value, final String jsType) {
        this.addVar(new Var(name, value, jsType));
    }
    
    public Var getVar(final String mainKey) {
        return (Var)this.hVars.get((Object)mainKey);
    }
    
    public String getVarValue(final String mainKey) {
        String value = null;
        final Object o = this.hVars.get((Object)mainKey);
        if (o != null && o instanceof Var) {
            final Var v = (Var)o;
            value = v.getValue();
        }
        return value;
    }
    
    public Map getVars() {
        return Collections.unmodifiableMap((Map<?, ?>)this.hVars);
    }
    
    public String getKey() {
        if (this.key == null) {
            this.generateKey();
        }
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public boolean isIndexed() {
        return this.indexedListProperty != null && this.indexedListProperty.length() > 0;
    }
    
    public void generateKey() {
        if (this.isIndexed()) {
            this.key = this.indexedListProperty + "[]" + "." + this.property;
        }
        else {
            this.key = this.property;
        }
    }
    
    void process(final Map globalConstants, final Map constants) {
        this.hMsgs.setFast(false);
        this.hVars.setFast(true);
        this.generateKey();
        final Iterator i = constants.keySet().iterator();
        while (i.hasNext()) {
            final String key = i.next();
            final String key2 = "${" + key + "}";
            final String replaceValue = constants.get(key);
            this.property = ValidatorUtils.replace(this.property, key2, replaceValue);
            this.processVars(key2, replaceValue);
            this.processMessageComponents(key2, replaceValue);
        }
        final Iterator j = globalConstants.keySet().iterator();
        while (j.hasNext()) {
            final String key3 = j.next();
            final String key4 = "${" + key3 + "}";
            final String replaceValue2 = globalConstants.get(key3);
            this.property = ValidatorUtils.replace(this.property, key4, replaceValue2);
            this.processVars(key4, replaceValue2);
            this.processMessageComponents(key4, replaceValue2);
        }
        final Iterator k = this.hVars.keySet().iterator();
        while (k.hasNext()) {
            final String key5 = k.next();
            final String key6 = "${var:" + key5 + "}";
            final Var var = this.getVar(key5);
            final String replaceValue3 = var.getValue();
            this.processMessageComponents(key6, replaceValue3);
        }
        this.hMsgs.setFast(true);
    }
    
    private void processVars(final String key, final String replaceValue) {
        final Iterator i = this.hVars.keySet().iterator();
        while (i.hasNext()) {
            final String varKey = i.next();
            final Var var = this.getVar(varKey);
            var.setValue(ValidatorUtils.replace(var.getValue(), key, replaceValue));
        }
    }
    
    private void processMessageComponents(final String key, final String replaceValue) {
        final String varKey = "${var:";
        if (key != null && !key.startsWith(varKey)) {
            final Iterator i = this.hMsgs.values().iterator();
            while (i.hasNext()) {
                final Msg msg = i.next();
                msg.setKey(ValidatorUtils.replace(msg.getKey(), key, replaceValue));
            }
        }
        this.processArg(key, replaceValue);
    }
    
    private void processArg(final String key, final String replaceValue) {
        for (int i = 0; i < this.args.length; ++i) {
            final Map argMap = this.args[i];
            if (argMap != null) {
                final Iterator iter = argMap.values().iterator();
                while (iter.hasNext()) {
                    final Arg arg = iter.next();
                    if (arg != null) {
                        arg.setKey(ValidatorUtils.replace(arg.getKey(), key, replaceValue));
                    }
                }
            }
        }
    }
    
    public boolean isDependency(final String validatorName) {
        return this.dependencyList.contains(validatorName);
    }
    
    public List getDependencyList() {
        return Collections.unmodifiableList((List<?>)this.dependencyList);
    }
    
    public Object clone() {
        Field field = null;
        try {
            field = (Field)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.toString());
        }
        field.args = new Map[this.args.length];
        for (int i = 0; i < this.args.length; ++i) {
            if (this.args[i] != null) {
                final Map argMap = new HashMap(this.args[i]);
                final Iterator iter = argMap.keySet().iterator();
                while (iter.hasNext()) {
                    final String validatorName = iter.next();
                    final Arg arg = argMap.get(validatorName);
                    argMap.put(validatorName, arg.clone());
                }
                field.args[i] = argMap;
            }
        }
        field.hVars = ValidatorUtils.copyFastHashMap(this.hVars);
        field.hMsgs = ValidatorUtils.copyFastHashMap(this.hMsgs);
        return field;
    }
    
    public String toString() {
        final StringBuffer results = new StringBuffer();
        results.append("\t\tkey = " + this.key + "\n");
        results.append("\t\tproperty = " + this.property + "\n");
        results.append("\t\tindexedProperty = " + this.indexedProperty + "\n");
        results.append("\t\tindexedListProperty = " + this.indexedListProperty + "\n");
        results.append("\t\tdepends = " + this.depends + "\n");
        results.append("\t\tpage = " + this.page + "\n");
        results.append("\t\tfieldOrder = " + this.fieldOrder + "\n");
        if (this.hVars != null) {
            results.append("\t\tVars:\n");
            final Iterator i = this.hVars.keySet().iterator();
            while (i.hasNext()) {
                final Object key = i.next();
                results.append("\t\t\t");
                results.append(key);
                results.append("=");
                results.append(this.hVars.get(key));
                results.append("\n");
            }
        }
        return results.toString();
    }
    
    Object[] getIndexedProperty(final Object bean) throws ValidatorException {
        Object indexedProperty = null;
        try {
            indexedProperty = PropertyUtils.getProperty(bean, this.getIndexedListProperty());
        }
        catch (final IllegalAccessException e) {
            throw new ValidatorException(e.getMessage());
        }
        catch (final InvocationTargetException e2) {
            throw new ValidatorException(e2.getMessage());
        }
        catch (final NoSuchMethodException e3) {
            throw new ValidatorException(e3.getMessage());
        }
        if (indexedProperty instanceof Collection) {
            return ((Collection)indexedProperty).toArray();
        }
        if (indexedProperty.getClass().isArray()) {
            return (Object[])indexedProperty;
        }
        throw new ValidatorException(this.getKey() + " is not indexed");
    }
    
    private int getIndexedPropertySize(final Object bean) throws ValidatorException {
        Object indexedProperty = null;
        try {
            indexedProperty = PropertyUtils.getProperty(bean, this.getIndexedListProperty());
        }
        catch (final IllegalAccessException e) {
            throw new ValidatorException(e.getMessage());
        }
        catch (final InvocationTargetException e2) {
            throw new ValidatorException(e2.getMessage());
        }
        catch (final NoSuchMethodException e3) {
            throw new ValidatorException(e3.getMessage());
        }
        if (indexedProperty == null) {
            return 0;
        }
        if (indexedProperty instanceof Collection) {
            return ((Collection)indexedProperty).size();
        }
        if (indexedProperty.getClass().isArray()) {
            return ((Object[])indexedProperty).length;
        }
        throw new ValidatorException(this.getKey() + " is not indexed");
    }
    
    private boolean validateForRule(final ValidatorAction va, final ValidatorResults results, final Map actions, final Map params, final int pos) throws ValidatorException {
        final ValidatorResult result = results.getValidatorResult(this.getKey());
        if (result != null && result.containsAction(va.getName())) {
            return result.isValid(va.getName());
        }
        return this.runDependentValidators(va, results, actions, params, pos) && va.executeValidationMethod(this, params, results, pos);
    }
    
    private boolean runDependentValidators(final ValidatorAction va, final ValidatorResults results, final Map actions, final Map params, final int pos) throws ValidatorException {
        final List dependentValidators = va.getDependencyList();
        if (dependentValidators.isEmpty()) {
            return true;
        }
        final Iterator iter = dependentValidators.iterator();
        while (iter.hasNext()) {
            final String depend = iter.next();
            final ValidatorAction action = actions.get(depend);
            if (action == null) {
                this.handleMissingAction(depend);
            }
            if (!this.validateForRule(action, results, actions, params, pos)) {
                return false;
            }
        }
        return true;
    }
    
    public ValidatorResults validate(final Map params, final Map actions) throws ValidatorException {
        if (this.getDepends() == null) {
            return new ValidatorResults();
        }
        final ValidatorResults allResults = new ValidatorResults();
        final Object bean = params.get("java.lang.Object");
        for (int numberOfFieldsToValidate = this.isIndexed() ? this.getIndexedPropertySize(bean) : 1, fieldNumber = 0; fieldNumber < numberOfFieldsToValidate; ++fieldNumber) {
            final Iterator dependencies = this.dependencyList.iterator();
            final ValidatorResults results = new ValidatorResults();
            while (dependencies.hasNext()) {
                final String depend = dependencies.next();
                final ValidatorAction action = actions.get(depend);
                if (action == null) {
                    this.handleMissingAction(depend);
                }
                final boolean good = this.validateForRule(action, results, actions, params, fieldNumber);
                if (!good) {
                    allResults.merge(results);
                    return allResults;
                }
            }
            allResults.merge(results);
        }
        return allResults;
    }
    
    private void handleMissingAction(final String name) throws ValidatorException {
        throw new ValidatorException("No ValidatorAction named " + name + " found for field " + this.getProperty());
    }
    
    protected Map getMsgMap() {
        return (Map)this.hMsgs;
    }
    
    protected Map getVarMap() {
        return (Map)this.hVars;
    }
}
