package org.apache.commons.digester;

import java.beans.PropertyDescriptor;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.DynaBean;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import java.util.HashMap;
import org.apache.commons.logging.Log;

public class SetNestedPropertiesRule extends Rule
{
    private Log log;
    private boolean trimData;
    private boolean allowUnknownChildElements;
    private HashMap elementNames;
    
    public SetNestedPropertiesRule() {
        this.log = null;
        this.trimData = true;
        this.allowUnknownChildElements = false;
        this.elementNames = new HashMap();
    }
    
    public SetNestedPropertiesRule(final String elementName, final String propertyName) {
        this.log = null;
        this.trimData = true;
        this.allowUnknownChildElements = false;
        (this.elementNames = new HashMap()).put(elementName, propertyName);
    }
    
    public SetNestedPropertiesRule(final String[] elementNames, final String[] propertyNames) {
        this.log = null;
        this.trimData = true;
        this.allowUnknownChildElements = false;
        this.elementNames = new HashMap();
        for (int i = 0, size = elementNames.length; i < size; ++i) {
            String propName = null;
            if (i < propertyNames.length) {
                propName = propertyNames[i];
            }
            this.elementNames.put(elementNames[i], propName);
        }
    }
    
    public void setDigester(final Digester digester) {
        super.setDigester(digester);
        this.log = digester.getLogger();
    }
    
    public void setTrimData(final boolean trimData) {
        this.trimData = trimData;
    }
    
    public boolean getTrimData() {
        return this.trimData;
    }
    
    public void setAllowUnknownChildElements(final boolean allowUnknownChildElements) {
        this.allowUnknownChildElements = allowUnknownChildElements;
    }
    
    public boolean getAllowUnknownChildElements() {
        return this.allowUnknownChildElements;
    }
    
    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
        final Rules oldRules = this.digester.getRules();
        final AnyChildRule anyChildRule = new AnyChildRule();
        anyChildRule.setDigester(this.digester);
        final AnyChildRules newRules = new AnyChildRules(anyChildRule);
        newRules.init(this.digester.getMatch() + "/", oldRules);
        this.digester.setRules(newRules);
    }
    
    public void body(final String bodyText) throws Exception {
        final AnyChildRules newRules = (AnyChildRules)this.digester.getRules();
        this.digester.setRules(newRules.getOldRules());
    }
    
    public void addAlias(final String elementName, final String propertyName) {
        this.elementNames.put(elementName, propertyName);
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("SetNestedPropertiesRule[");
        sb.append("allowUnknownChildElements=");
        sb.append(this.allowUnknownChildElements);
        sb.append(", trimData=");
        sb.append(this.trimData);
        sb.append(", elementNames=");
        sb.append(this.elementNames);
        sb.append("]");
        return sb.toString();
    }
    
    private class AnyChildRules implements Rules
    {
        private String matchPrefix;
        private Rules decoratedRules;
        private ArrayList rules;
        private AnyChildRule rule;
        
        public AnyChildRules(final AnyChildRule rule) {
            this.matchPrefix = null;
            this.decoratedRules = null;
            this.rules = new ArrayList(1);
            this.rule = rule;
            this.rules.add(rule);
        }
        
        public Digester getDigester() {
            return null;
        }
        
        public void setDigester(final Digester digester) {
        }
        
        public String getNamespaceURI() {
            return null;
        }
        
        public void setNamespaceURI(final String namespaceURI) {
        }
        
        public void add(final String pattern, final Rule rule) {
        }
        
        public void clear() {
        }
        
        public List match(final String matchPath) {
            return this.match(null, matchPath);
        }
        
        public List match(final String namespaceURI, final String matchPath) {
            final List match = this.decoratedRules.match(namespaceURI, matchPath);
            if (!matchPath.startsWith(this.matchPrefix) || matchPath.indexOf(47, this.matchPrefix.length()) != -1) {
                return match;
            }
            if (match == null || match.size() == 0) {
                return this.rules;
            }
            final LinkedList newMatch = new LinkedList(match);
            newMatch.addLast(this.rule);
            return newMatch;
        }
        
        public List rules() {
            SetNestedPropertiesRule.this.log.debug((Object)"AnyChildRules.rules invoked.");
            return this.decoratedRules.rules();
        }
        
        public void init(final String prefix, final Rules rules) {
            this.matchPrefix = prefix;
            this.decoratedRules = rules;
        }
        
        public Rules getOldRules() {
            return this.decoratedRules;
        }
    }
    
    private class AnyChildRule extends Rule
    {
        private String currChildNamespaceURI;
        private String currChildElementName;
        
        private AnyChildRule() {
            this.currChildNamespaceURI = null;
            this.currChildElementName = null;
        }
        
        public void begin(final String namespaceURI, final String name, final Attributes attributes) throws Exception {
            this.currChildNamespaceURI = namespaceURI;
            this.currChildElementName = name;
        }
        
        public void body(String value) throws Exception {
            String propName = this.currChildElementName;
            if (SetNestedPropertiesRule.this.elementNames.containsKey(this.currChildElementName)) {
                propName = SetNestedPropertiesRule.this.elementNames.get(this.currChildElementName);
                if (propName == null) {
                    return;
                }
            }
            final boolean debug = SetNestedPropertiesRule.this.log.isDebugEnabled();
            if (debug) {
                SetNestedPropertiesRule.this.log.debug((Object)("[SetNestedPropertiesRule]{" + this.digester.match + "} Setting property '" + propName + "' to '" + value + "'"));
            }
            final Object top = this.digester.peek();
            if (debug) {
                if (top != null) {
                    SetNestedPropertiesRule.this.log.debug((Object)("[SetNestedPropertiesRule]{" + this.digester.match + "} Set " + top.getClass().getName() + " properties"));
                }
                else {
                    SetNestedPropertiesRule.this.log.debug((Object)("[SetPropertiesRule]{" + this.digester.match + "} Set NULL properties"));
                }
            }
            if (SetNestedPropertiesRule.this.trimData) {
                value = value.trim();
            }
            if (!SetNestedPropertiesRule.this.allowUnknownChildElements) {
                if (top instanceof DynaBean) {
                    final DynaProperty desc = ((DynaBean)top).getDynaClass().getDynaProperty(propName);
                    if (desc == null) {
                        throw new NoSuchMethodException("Bean has no property named " + propName);
                    }
                }
                else {
                    final PropertyDescriptor desc2 = PropertyUtils.getPropertyDescriptor(top, propName);
                    if (desc2 == null) {
                        throw new NoSuchMethodException("Bean has no property named " + propName);
                    }
                }
            }
            try {
                BeanUtils.setProperty(top, propName, (Object)value);
            }
            catch (final NullPointerException e) {
                SetNestedPropertiesRule.this.log.error((Object)("NullPointerException: top=" + top + ",propName=" + propName + ",value=" + value + "!"));
                throw e;
            }
        }
        
        public void end(final String namespace, final String name) throws Exception {
            this.currChildElementName = null;
        }
    }
}
