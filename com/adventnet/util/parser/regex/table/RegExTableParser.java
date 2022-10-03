package com.adventnet.util.parser.regex.table;

import com.adventnet.util.parser.regex.ParserResponseObject;
import com.adventnet.util.parser.RuleObject;
import com.adventnet.util.parser.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import com.adventnet.util.parser.ParserInterface;

public class RegExTableParser implements ParserInterface
{
    private Hashtable rules;
    private RegExTableXMLParser regexParser;
    private ArrayList errorRules;
    
    public RegExTableParser() {
        this.rules = null;
        this.regexParser = null;
        this.errorRules = null;
        this.rules = new Hashtable();
        this.regexParser = new RegExTableXMLParser();
        this.errorRules = new ArrayList();
    }
    
    public void init() throws ParseException {
    }
    
    public Object parseMessage(final String s, final String s2) throws ParseException {
        try {
            final RuleObject ruleObject = this.rules.get(s2);
            if (ruleObject == null) {
                throw new ParseException("No rule found for command " + s2);
            }
            if (ruleObject.getParserErrorRule() == null && ruleObject.getParserValidRule() == null) {
                throw new ParseException("No error and valid rule defined for the command " + s2);
            }
            if (ruleObject.getParserErrorRule() != null) {
                System.out.println(" into error rule");
                final ParserResponseObject message = this.regexParser.parseMessage((TableObject)ruleObject.getParserErrorRule(), s);
                if (message != null && message.size() > 0) {
                    System.err.println(" returning err object for command " + s2);
                    ruleObject.setResponseRule(RuleObject.ERROR);
                    return message;
                }
            }
            if (this.errorRules != null && this.errorRules.size() > 0) {
                System.out.println(" into common error rules");
                for (int i = 0; i < this.errorRules.size(); ++i) {
                    final TableObject tableObject = this.errorRules.get(i);
                    if (tableObject != null) {
                        final ParserResponseObject message2 = this.regexParser.parseMessage(tableObject, s);
                        if (message2 != null && message2.size() > 0) {
                            System.err.println(" returning err object for command " + s2);
                            ruleObject.setResponseRule(RuleObject.ERROR);
                            return message2;
                        }
                    }
                }
            }
            final TableObject tableObject2 = (TableObject)ruleObject.getParserValidRule();
            if (tableObject2 == null) {
                throw new ParseException("Valid Rule not found for command " + s2);
            }
            System.out.println(" RegExTableParser: invoking parse mess for valid rule");
            final ParserResponseObject message3 = this.regexParser.parseMessage(tableObject2, s);
            ruleObject.setResponseRule(RuleObject.VALID);
            return message3;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage());
        }
    }
    
    public void parseRules(final String[] array, final Object[] array2) throws ParseException {
        if (array == null) {
            throw new ParseException("no commands present");
        }
        if (array2 == null) {
            throw new ParseException("no rules present for the commands");
        }
        for (int i = 0; i < array.length; ++i) {
            RuleObject ruleObject;
            try {
                if (i >= array2.length) {
                    throw new ParseException("No rule defined for the command " + array[i]);
                }
                if (!(array2[i] instanceof RuleObject)) {
                    throw new ParseException("Invalid rule for command " + array[i]);
                }
                ruleObject = (RuleObject)array2[i];
                if (ruleObject.getErrorRule() == null && ruleObject.getValidRule() == null) {
                    throw new ParseException("No rule defined for the command " + array[i]);
                }
                if (ruleObject.getErrorRule() != null) {
                    ruleObject.setParserErrorRule(this.regexParser.parseRule(ruleObject.getErrorRule()));
                }
                if (ruleObject.getValidRule() != null) {
                    ruleObject.setParserValidRule(this.regexParser.parseRule(ruleObject.getValidRule()));
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
        for (int i = 0; i < list.size(); ++i) {
            this.errorRules.add(this.regexParser.parseRule((String)list.get(i)));
        }
    }
}
