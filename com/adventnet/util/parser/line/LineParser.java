package com.adventnet.util.parser.line;

import java.util.Vector;
import com.adventnet.util.parser.RuleObject;
import com.adventnet.util.parser.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import com.adventnet.util.parser.ParserInterface;

public class LineParser implements ParserInterface
{
    private XMLLineRuleParser xmlParser;
    private LineRuleResponseParser respParser;
    private Hashtable rules;
    private ArrayList errorRules;
    
    public LineParser() {
        this.xmlParser = null;
        this.respParser = null;
        this.rules = null;
        this.errorRules = null;
    }
    
    public void init() throws ParseException {
        this.rules = new Hashtable();
        try {
            this.xmlParser = new XMLLineRuleParser();
            this.respParser = new LineRuleResponseParser();
            this.errorRules = new ArrayList();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage());
        }
    }
    
    public Object parseMessage(final String s, final String s2) throws ParseException {
        try {
            final RuleObject ruleObject = this.rules.get(s2);
            if (ruleObject == null) {
                throw new ParseException("No rule found for command " + s2);
            }
            if (ruleObject.getParserErrorRule() != null) {
                final ParsedResult parsedResult = (ParsedResult)this.respParser.parseMessage((Vector)ruleObject.getParserErrorRule(), s);
                if (parsedResult != null && parsedResult.getResult() != null && parsedResult.getResult().size() > 0) {
                    System.err.println(" returning err object for command " + s2);
                    ruleObject.setResponseRule(RuleObject.ERROR);
                    return parsedResult;
                }
            }
            if (this.errorRules != null && this.errorRules.size() > 0) {
                System.out.println(" into common error rules");
                for (int i = 0; i < this.errorRules.size(); ++i) {
                    final Vector vector = this.errorRules.get(i);
                    if (vector != null) {
                        final ParsedResult parsedResult2 = (ParsedResult)this.respParser.parseMessage(vector, s);
                        if (parsedResult2 != null && parsedResult2.getResult() != null && parsedResult2.getResult().size() > 0) {
                            System.err.println(" returning err object for command " + s2);
                            ruleObject.setResponseRule(RuleObject.ERROR);
                            return parsedResult2;
                        }
                    }
                }
            }
            final Vector vector2 = (Vector)ruleObject.getParserValidRule();
            if (vector2 == null) {
                throw new ParseException("Valid Rule not found for command " + s2);
            }
            final ParsedResult parsedResult3 = (ParsedResult)this.respParser.parseMessage(vector2, s);
            ruleObject.setResponseRule(RuleObject.VALID);
            return parsedResult3;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage());
        }
    }
    
    public void parseRules(final String[] array, final Object[] array2) throws ParseException {
        for (int i = 0; i < array.length; ++i) {
            RuleObject ruleObject;
            try {
                ruleObject = (RuleObject)array2[i];
                if (ruleObject.getErrorRule() != null) {
                    ruleObject.setParserErrorRule(this.xmlParser.parseRule(ruleObject.getErrorRule()));
                }
                if (ruleObject.getValidRule() != null) {
                    ruleObject.setParserValidRule(this.xmlParser.parseRule(ruleObject.getValidRule()));
                }
            }
            catch (final Exception ex) {
                ex.printStackTrace();
                throw new ParseException(ex.getMessage());
            }
            this.rules.put(array[i], ruleObject);
        }
    }
    
    public void parseErrorRules(final ArrayList list) throws ParseException {
        if (list == null) {
            throw new ParseException("No error rules defined");
        }
        try {
            for (int i = 0; i < list.size(); ++i) {
                this.errorRules.add(this.xmlParser.parseRule((String)list.get(i)));
            }
        }
        catch (final Exception ex) {
            throw new ParseException(ex.getMessage());
        }
    }
}
