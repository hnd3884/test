package org.apache.commons.digester.xmlrules;

import org.apache.commons.digester.SetRootRule;
import org.apache.commons.digester.SetNextRule;
import org.apache.commons.digester.SetTopRule;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SetPropertyRule;
import org.apache.commons.digester.SetPropertiesRule;
import org.apache.commons.digester.ObjectCreateRule;
import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.NodeCreateRule;
import org.apache.commons.digester.ObjectParamRule;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.digester.CallParamRule;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.apache.commons.digester.CallMethodRule;
import org.apache.commons.digester.BeanPropertySetterRule;
import org.apache.commons.digester.AbstractObjectCreationFactory;
import java.util.List;
import org.apache.commons.digester.Rules;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.digester.RuleSet;
import java.io.FileNotFoundException;
import org.xml.sax.Attributes;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.commons.digester.Rule;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class DigesterRuleParser extends RuleSetBase
{
    public static final String DIGESTER_PUBLIC_ID = "-//Jakarta Apache //DTD digester-rules XML V1.0//EN";
    private String digesterDtdUrl;
    protected Digester targetDigester;
    protected String basePath;
    protected PatternStack patternStack;
    private Set includedFiles;
    
    public DigesterRuleParser() {
        this.basePath = "";
        this.includedFiles = new HashSet();
        this.patternStack = new PatternStack();
    }
    
    public DigesterRuleParser(final Digester targetDigester) {
        this.basePath = "";
        this.includedFiles = new HashSet();
        this.targetDigester = targetDigester;
        this.patternStack = new PatternStack();
    }
    
    private DigesterRuleParser(final Digester targetDigester, final PatternStack stack, final Set includedFiles) {
        this.basePath = "";
        this.includedFiles = new HashSet();
        this.targetDigester = targetDigester;
        this.patternStack = stack;
        this.includedFiles = includedFiles;
    }
    
    public void setTarget(final Digester d) {
        this.targetDigester = d;
    }
    
    public void setBasePath(final String path) {
        if (path == null) {
            this.basePath = "";
        }
        else if (path.length() > 0 && !path.endsWith("/")) {
            this.basePath = path + "/";
        }
        else {
            this.basePath = path;
        }
    }
    
    public void setDigesterRulesDTD(final String dtdURL) {
        this.digesterDtdUrl = dtdURL;
    }
    
    protected String getDigesterRulesDTD() {
        return this.digesterDtdUrl;
    }
    
    public void add(final Rule rule) {
        this.targetDigester.addRule(this.basePath + this.patternStack.toString(), rule);
    }
    
    public void addRuleInstances(final Digester digester) {
        final String ruleClassName = Rule.class.getName();
        digester.register("-//Jakarta Apache //DTD digester-rules XML V1.0//EN", this.getDigesterRulesDTD());
        digester.addRule("*/pattern", new PatternRule("value"));
        digester.addRule("*/include", new IncludeRule());
        digester.addFactoryCreate("*/bean-property-setter-rule", new BeanPropertySetterRuleFactory());
        digester.addRule("*/bean-property-setter-rule", new PatternRule("pattern"));
        digester.addSetNext("*/bean-property-setter-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/call-method-rule", new CallMethodRuleFactory());
        digester.addRule("*/call-method-rule", new PatternRule("pattern"));
        digester.addSetNext("*/call-method-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/object-param-rule", new ObjectParamRuleFactory());
        digester.addRule("*/object-param-rule", new PatternRule("pattern"));
        digester.addSetNext("*/object-param-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/call-param-rule", new CallParamRuleFactory());
        digester.addRule("*/call-param-rule", new PatternRule("pattern"));
        digester.addSetNext("*/call-param-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/factory-create-rule", new FactoryCreateRuleFactory());
        digester.addRule("*/factory-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/factory-create-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/object-create-rule", new ObjectCreateRuleFactory());
        digester.addRule("*/object-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/object-create-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/node-create-rule", new NodeCreateRuleFactory());
        digester.addRule("*/node-create-rule", new PatternRule("pattern"));
        digester.addSetNext("*/node-create-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-properties-rule", new SetPropertiesRuleFactory());
        digester.addRule("*/set-properties-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-properties-rule", "add", ruleClassName);
        digester.addRule("*/set-properties-rule/alias", new SetPropertiesAliasRule());
        digester.addFactoryCreate("*/set-property-rule", new SetPropertyRuleFactory());
        digester.addRule("*/set-property-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-property-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-nested-properties-rule", new SetNestedPropertiesRuleFactory());
        digester.addRule("*/set-nested-properties-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-nested-properties-rule", "add", ruleClassName);
        digester.addRule("*/set-nested-properties-rule/alias", new SetNestedPropertiesAliasRule());
        digester.addFactoryCreate("*/set-top-rule", new SetTopRuleFactory());
        digester.addRule("*/set-top-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-top-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-next-rule", new SetNextRuleFactory());
        digester.addRule("*/set-next-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-next-rule", "add", ruleClassName);
        digester.addFactoryCreate("*/set-root-rule", new SetRootRuleFactory());
        digester.addRule("*/set-root-rule", new PatternRule("pattern"));
        digester.addSetNext("*/set-root-rule", "add", ruleClassName);
    }
    
    protected class PatternStack extends ArrayStack
    {
        public String toString() {
            final StringBuffer str = new StringBuffer();
            for (int i = 0; i < this.size(); ++i) {
                final String elem = this.get(i).toString();
                if (elem.length() > 0) {
                    if (str.length() > 0) {
                        str.append('/');
                    }
                    str.append(elem);
                }
            }
            return str.toString();
        }
    }
    
    private class PatternRule extends Rule
    {
        private String attrName;
        private String pattern;
        
        public PatternRule(final String attrName) {
            this.pattern = null;
            this.attrName = attrName;
        }
        
        public void begin(final Attributes attributes) {
            this.pattern = attributes.getValue(this.attrName);
            if (this.pattern != null) {
                DigesterRuleParser.this.patternStack.push((Object)this.pattern);
            }
        }
        
        public void end() {
            if (this.pattern != null) {
                DigesterRuleParser.this.patternStack.pop();
            }
        }
    }
    
    private class IncludeRule extends Rule
    {
        public IncludeRule() {
        }
        
        public void begin(final Attributes attributes) throws Exception {
            final String fileName = attributes.getValue("path");
            if (fileName != null && fileName.length() > 0) {
                this.includeXMLRules(fileName);
            }
            final String className = attributes.getValue("class");
            if (className != null && className.length() > 0) {
                this.includeProgrammaticRules(className);
            }
        }
        
        private void includeXMLRules(String fileName) throws IOException, SAXException, CircularIncludeException {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl == null) {
                cl = DigesterRuleParser.this.getClass().getClassLoader();
            }
            final URL fileURL = cl.getResource(fileName);
            if (fileURL == null) {
                throw new FileNotFoundException("File \"" + fileName + "\" not found.");
            }
            fileName = fileURL.toExternalForm();
            if (!DigesterRuleParser.this.includedFiles.add(fileName)) {
                throw new CircularIncludeException(fileName);
            }
            final DigesterRuleParser includedSet = new DigesterRuleParser(DigesterRuleParser.this.targetDigester, DigesterRuleParser.this.patternStack, DigesterRuleParser.this.includedFiles, null);
            includedSet.setDigesterRulesDTD(DigesterRuleParser.this.getDigesterRulesDTD());
            final Digester digester = new Digester();
            digester.addRuleSet(includedSet);
            digester.push(DigesterRuleParser.this);
            digester.parse(fileName);
            DigesterRuleParser.this.includedFiles.remove(fileName);
        }
        
        private void includeProgrammaticRules(final String className) throws ClassNotFoundException, ClassCastException, InstantiationException, IllegalAccessException {
            final Class cls = Class.forName(className);
            final DigesterRulesSource rulesSource = cls.newInstance();
            final Rules digesterRules = DigesterRuleParser.this.targetDigester.getRules();
            final Rules prefixWrapper = new RulesPrefixAdapter(DigesterRuleParser.this.patternStack.toString(), digesterRules);
            DigesterRuleParser.this.targetDigester.setRules(prefixWrapper);
            try {
                rulesSource.getRules(DigesterRuleParser.this.targetDigester);
            }
            finally {
                DigesterRuleParser.this.targetDigester.setRules(digesterRules);
            }
        }
    }
    
    private class RulesPrefixAdapter implements Rules
    {
        private Rules delegate;
        private String prefix;
        
        public RulesPrefixAdapter(final String patternPrefix, final Rules rules) {
            this.prefix = patternPrefix;
            this.delegate = rules;
        }
        
        public void add(final String pattern, final Rule rule) {
            final StringBuffer buffer = new StringBuffer();
            buffer.append(this.prefix);
            if (!pattern.startsWith("/")) {
                buffer.append('/');
            }
            buffer.append(pattern);
            this.delegate.add(buffer.toString(), rule);
        }
        
        public void clear() {
            this.delegate.clear();
        }
        
        public Digester getDigester() {
            return this.delegate.getDigester();
        }
        
        public String getNamespaceURI() {
            return this.delegate.getNamespaceURI();
        }
        
        public List match(final String pattern) {
            return this.delegate.match(pattern);
        }
        
        public List match(final String namespaceURI, final String pattern) {
            return this.delegate.match(namespaceURI, pattern);
        }
        
        public List rules() {
            return this.delegate.rules();
        }
        
        public void setDigester(final Digester digester) {
            this.delegate.setDigester(digester);
        }
        
        public void setNamespaceURI(final String namespaceURI) {
            this.delegate.setNamespaceURI(namespaceURI);
        }
    }
    
    private class BeanPropertySetterRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) throws Exception {
            Rule beanPropertySetterRule = null;
            final String propertyname = attributes.getValue("propertyname");
            if (propertyname == null) {
                beanPropertySetterRule = new BeanPropertySetterRule();
            }
            else {
                beanPropertySetterRule = new BeanPropertySetterRule(propertyname);
            }
            return beanPropertySetterRule;
        }
    }
    
    protected class CallMethodRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            Rule callMethodRule = null;
            final String methodName = attributes.getValue("methodname");
            int targetOffset = 0;
            final String targetOffsetStr = attributes.getValue("targetoffset");
            if (targetOffsetStr != null) {
                targetOffset = Integer.parseInt(targetOffsetStr);
            }
            if (attributes.getValue("paramcount") == null) {
                callMethodRule = new CallMethodRule(targetOffset, methodName);
            }
            else {
                final int paramCount = Integer.parseInt(attributes.getValue("paramcount"));
                final String paramTypesAttr = attributes.getValue("paramtypes");
                if (paramTypesAttr == null || paramTypesAttr.length() == 0) {
                    callMethodRule = new CallMethodRule(targetOffset, methodName, paramCount);
                }
                else {
                    final String[] paramTypes = this.getParamTypes(paramTypesAttr);
                    callMethodRule = new CallMethodRule(targetOffset, methodName, paramCount, paramTypes);
                }
            }
            return callMethodRule;
        }
        
        private String[] getParamTypes(final String paramTypes) {
            String[] paramTypesArray;
            if (paramTypes != null) {
                final ArrayList paramTypesList = new ArrayList();
                final StringTokenizer tokens = new StringTokenizer(paramTypes, " \t\n\r,");
                while (tokens.hasMoreTokens()) {
                    paramTypesList.add(tokens.nextToken());
                }
                paramTypesArray = paramTypesList.toArray(new String[0]);
            }
            else {
                paramTypesArray = new String[0];
            }
            return paramTypesArray;
        }
    }
    
    protected class CallParamRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final int paramIndex = Integer.parseInt(attributes.getValue("paramnumber"));
            final String attributeName = attributes.getValue("attrname");
            final String fromStack = attributes.getValue("from-stack");
            final String stackIndex = attributes.getValue("stack-index");
            Rule callParamRule = null;
            if (attributeName == null) {
                if (stackIndex != null) {
                    callParamRule = new CallParamRule(paramIndex, Integer.parseInt(stackIndex));
                }
                else if (fromStack != null) {
                    callParamRule = new CallParamRule(paramIndex, Boolean.valueOf(fromStack));
                }
                else {
                    callParamRule = new CallParamRule(paramIndex);
                }
            }
            else {
                if (fromStack != null) {
                    throw new RuntimeException("Attributes from-stack and attrname cannot both be present.");
                }
                callParamRule = new CallParamRule(paramIndex, attributeName);
            }
            return callParamRule;
        }
    }
    
    protected class ObjectParamRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) throws Exception {
            final int paramIndex = Integer.parseInt(attributes.getValue("paramnumber"));
            final String attributeName = attributes.getValue("attrname");
            final String type = attributes.getValue("type");
            final String value = attributes.getValue("value");
            Rule objectParamRule = null;
            if (type == null) {
                throw new RuntimeException("Attribute 'type' is required.");
            }
            Object param = null;
            final Class clazz = Class.forName(type);
            if (value == null) {
                param = clazz.newInstance();
            }
            else {
                param = ConvertUtils.convert(value, clazz);
            }
            if (attributeName == null) {
                objectParamRule = new ObjectParamRule(paramIndex, param);
            }
            else {
                objectParamRule = new ObjectParamRule(paramIndex, attributeName, param);
            }
            return objectParamRule;
        }
    }
    
    protected class NodeCreateRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) throws Exception {
            final String nodeType = attributes.getValue("type");
            if (nodeType == null || "".equals(nodeType)) {
                return new NodeCreateRule();
            }
            if ("element".equals(nodeType)) {
                return new NodeCreateRule(1);
            }
            if ("fragment".equals(nodeType)) {
                return new NodeCreateRule(11);
            }
            throw new RuntimeException("Unrecognized node type: " + nodeType + ".  This attribute is optional or can have a value of element|fragment.");
        }
    }
    
    protected class FactoryCreateRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final String className = attributes.getValue("classname");
            final String attrName = attributes.getValue("attrname");
            final boolean ignoreExceptions = "true".equalsIgnoreCase(attributes.getValue("ignore-exceptions"));
            return (attrName == null || attrName.length() == 0) ? new FactoryCreateRule(className, ignoreExceptions) : new FactoryCreateRule(className, attrName, ignoreExceptions);
        }
    }
    
    protected class ObjectCreateRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final String className = attributes.getValue("classname");
            final String attrName = attributes.getValue("attrname");
            return (attrName == null || attrName.length() == 0) ? new ObjectCreateRule(className) : new ObjectCreateRule(className, attrName);
        }
    }
    
    protected class SetPropertiesRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            return new SetPropertiesRule();
        }
    }
    
    protected class SetPropertyRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final String name = attributes.getValue("name");
            final String value = attributes.getValue("value");
            return new SetPropertyRule(name, value);
        }
    }
    
    protected class SetNestedPropertiesRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final boolean allowUnknownChildElements = "true".equalsIgnoreCase(attributes.getValue("allow-unknown-child-elements"));
            final SetNestedPropertiesRule snpr = new SetNestedPropertiesRule();
            snpr.setAllowUnknownChildElements(allowUnknownChildElements);
            return snpr;
        }
    }
    
    protected class SetTopRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final String methodName = attributes.getValue("methodname");
            final String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ? new SetTopRule(methodName) : new SetTopRule(methodName, paramType);
        }
    }
    
    protected class SetNextRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final String methodName = attributes.getValue("methodname");
            final String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ? new SetNextRule(methodName) : new SetNextRule(methodName, paramType);
        }
    }
    
    protected class SetRootRuleFactory extends AbstractObjectCreationFactory
    {
        public Object createObject(final Attributes attributes) {
            final String methodName = attributes.getValue("methodname");
            final String paramType = attributes.getValue("paramtype");
            return (paramType == null || paramType.length() == 0) ? new SetRootRule(methodName) : new SetRootRule(methodName, paramType);
        }
    }
    
    protected class SetPropertiesAliasRule extends Rule
    {
        public SetPropertiesAliasRule() {
        }
        
        public void begin(final Attributes attributes) {
            final String attrName = attributes.getValue("attr-name");
            final String propName = attributes.getValue("prop-name");
            final SetPropertiesRule rule = (SetPropertiesRule)this.digester.peek();
            rule.addAlias(attrName, propName);
        }
    }
    
    protected class SetNestedPropertiesAliasRule extends Rule
    {
        public SetNestedPropertiesAliasRule() {
        }
        
        public void begin(final Attributes attributes) {
            final String attrName = attributes.getValue("attr-name");
            final String propName = attributes.getValue("prop-name");
            final SetNestedPropertiesRule rule = (SetNestedPropertiesRule)this.digester.peek();
            rule.addAlias(attrName, propName);
        }
    }
}
