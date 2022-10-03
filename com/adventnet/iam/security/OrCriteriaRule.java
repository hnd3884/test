package com.adventnet.iam.security;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import org.w3c.dom.Element;
import java.util.Map;

public class OrCriteriaRule
{
    private int minOccurrences;
    private int maxOccurrences;
    private Map<String, ParameterRule> paramRuleMap;
    
    public OrCriteriaRule(final Element criteriaElem, final String childTagName) {
        this.minOccurrences = 1;
        this.maxOccurrences = 1;
        this.paramRuleMap = new LinkedHashMap<String, ParameterRule>();
        final String minOccur = criteriaElem.getAttribute("min-occurrences");
        if (SecurityUtil.isValid(minOccur)) {
            this.minOccurrences = Integer.parseInt(minOccur);
        }
        final String maxOccur = criteriaElem.getAttribute("max-occurrences");
        if (SecurityUtil.isValid(maxOccur)) {
            this.maxOccurrences = Integer.parseInt(maxOccur);
        }
        if (this.maxOccurrences != -1 && (this.minOccurrences < 0 || this.maxOccurrences < 1 || this.minOccurrences > this.maxOccurrences)) {
            throw new RuntimeException("Invalid configuration : Min/Max occurrences value configured in <or-criteria> rule is incorrect");
        }
        final List<Element> paramElemList = RuleSetParser.getChildNodesByTagName(criteriaElem, childTagName);
        for (final Element paramEle : paramElemList) {
            final String paramName = paramEle.getAttribute("name");
            if (this.paramRuleMap.containsKey(paramName)) {
                throw new RuntimeException("Invalid configuration : Parameter rule '" + paramName + "' already defined under <or-criteria/> tag");
            }
            final ParameterRule paramRule = new ParameterRule(paramEle);
            paramRule.skipDefaultMaxOccurrenceCheckForOrCriteriaParam = true;
            this.paramRuleMap.put(paramName, paramRule);
        }
    }
    
    public Collection<ParameterRule> getParameterRules() {
        return this.paramRuleMap.values();
    }
    
    public int getMinOccurrences() {
        return this.minOccurrences;
    }
    
    public int getMaxOccurrences() {
        return this.maxOccurrences;
    }
    
    @Override
    public String toString() {
        return "Or-Criteria Rule :: minOccurrences : \"" + this.minOccurrences + "\" maxOccurrences : \"" + this.maxOccurrences + "\"";
    }
}
