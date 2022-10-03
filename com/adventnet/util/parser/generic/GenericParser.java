package com.adventnet.util.parser.generic;

import java.util.Vector;
import com.adventnet.util.parser.RuleObject;
import com.adventnet.util.parser.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import com.adventnet.util.parser.ParserInterface;

public class GenericParser implements ParserInterface
{
    private Hashtable rules;
    private ArrayList errorRules;
    
    public GenericParser() {
        this.rules = null;
        this.errorRules = null;
    }
    
    public void init() throws ParseException {
        this.rules = new Hashtable();
        this.errorRules = new ArrayList();
    }
    
    public Object parseMessage(final String s, final String s2) throws ParseException {
        try {
            final RuleObject ruleObject = this.rules.get(s2);
            if (ruleObject == null) {
                throw new ParseException("No rule found for command");
            }
            if (ruleObject.getParserErrorRule() != null) {
                final MessageParser messageParser = (MessageParser)ruleObject.getParserErrorRule();
                messageParser.parseIntoTokens(s);
                final Vector matchList = messageParser.getMatchList();
                if (matchList != null && matchList.size() > 0) {
                    System.err.println(" error in command " + s2);
                    ruleObject.setResponseRule(RuleObject.ERROR);
                    return matchList;
                }
            }
            if (this.errorRules != null && this.errorRules.size() > 0) {
                System.out.println(" into common error rules");
                for (int i = 0; i < this.errorRules.size(); ++i) {
                    final MessageParser messageParser2 = this.errorRules.get(i);
                    if (messageParser2 != null) {
                        messageParser2.parseIntoTokens(s);
                        final Vector matchList2 = messageParser2.getMatchList();
                        if (matchList2 != null && matchList2.size() > 0) {
                            System.err.println(" returning err object for command " + s2);
                            ruleObject.setResponseRule(RuleObject.ERROR);
                            return matchList2;
                        }
                    }
                }
            }
            final MessageParser messageParser3 = (MessageParser)ruleObject.getParserValidRule();
            if (messageParser3 == null) {
                throw new ParseException("Valid Rule not found for command " + s2);
            }
            messageParser3.parseIntoTokens(s);
            final Vector matchList3 = messageParser3.getMatchList();
            ruleObject.setResponseRule(RuleObject.VALID);
            return matchList3;
        }
        catch (final Exception ex) {
            throw new ParseException(ex.getMessage());
        }
    }
    
    public void parseRules(final String[] array, final Object[] array2) throws ParseException {
        for (int i = 0; i < array.length; ++i) {
            RuleObject ruleObject;
            try {
                ruleObject = (RuleObject)array2[i];
                if (ruleObject.getErrorRule() != null) {
                    final MessageParser parserErrorRule = new MessageParser();
                    parserErrorRule.parseRule(ruleObject.getErrorRule());
                    ruleObject.setParserErrorRule(parserErrorRule);
                }
                if (ruleObject.getValidRule() != null) {
                    final MessageParser parserValidRule = new MessageParser();
                    parserValidRule.parseRule(ruleObject.getValidRule());
                    ruleObject.setParserValidRule(parserValidRule);
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
            final MessageParser messageParser = new MessageParser();
            messageParser.parseRule(list.get(i));
            this.errorRules.add(messageParser);
        }
    }
}
