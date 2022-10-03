package com.adventnet.util.parser;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.net.URL;
import java.util.Hashtable;

public class ParserAPI
{
    Hashtable parsers;
    
    public ParserAPI() throws ParseException {
        this.parsers = null;
        String file = null;
        try {
            final URL resource = this.getClass().getResource("/ParserRules.xml");
            if (resource != null) {
                file = resource.getFile();
            }
        }
        catch (final Exception ex) {
            throw new ParseException("Cannot load Parser Rules file: " + ex.getMessage());
        }
        if (file != null) {
            this.loadParserConfig(file);
        }
    }
    
    public ParserAPI(final String s) throws ParseException {
        this.parsers = null;
        if (s == null) {
            throw new ParseException("Rules file name is null");
        }
        this.loadParserConfig(s);
    }
    
    public Object parse(final String s, final String s2, final String s3) throws ParseException {
        if (this.parsers == null) {
            throw new ParseException("No Parsers Found");
        }
        if (s == null) {
            throw new ParseException("Input Message is null");
        }
        if (s2 == null) {
            throw new ParseException("Parser type is null");
        }
        if (s3 == null) {
            throw new ParseException("Command Name is null");
        }
        final Parser parser = this.parsers.get(s2);
        if (parser == null) {
            throw new ParseException("Parser not found for parserType " + s2);
        }
        return parser.getParserInterface().parseMessage(s, s3);
    }
    
    void initParsers() throws ParseException {
        if (this.parsers == null) {
            throw new ParseException("No Parsers Found");
        }
        final Enumeration elements = this.parsers.elements();
        while (elements.hasMoreElements()) {
            final Parser parser = (Parser)elements.nextElement();
            ParserInterface parserInterface;
            try {
                parserInterface = (ParserInterface)Class.forName(parser.getClassName()).newInstance();
                parserInterface.init();
                parser.setParserInterface(parserInterface);
            }
            catch (final Exception ex) {
                throw new ParseException("Cannot create class: " + ex.getMessage());
            }
            final ArrayList ruleList = parser.getRuleList();
            final String[] array = new String[ruleList.size()];
            final RuleObject[] array2 = new RuleObject[ruleList.size()];
            for (int i = 0; i < ruleList.size(); ++i) {
                array[i] = ((RuleObject)ruleList.get(i)).getCommand();
                array2[i] = (RuleObject)ruleList.get(i);
            }
            parserInterface.parseRules(array, array2);
            parserInterface.parseErrorRules(parser.getErrorRules());
        }
    }
    
    public void loadParserConfig(final String s) throws ParseException {
        if (s == null) {
            throw new ParseException("Rules file name is null");
        }
        try {
            this.loadParserConfig(new FileInputStream(new File(s)));
        }
        catch (final FileNotFoundException ex) {
            System.out.println("File not found" + ex.getMessage());
        }
    }
    
    public void loadParserConfig(final InputStream inputStream) throws ParseException {
        this.parsers = new RulesXmlParser(inputStream).getParserList();
        this.initParsers();
    }
    
    public int getResponseStatus(final String s, final String s2) {
        return ((RuleObject)this.parsers.get(s).getRule(s2)).getResponseRule();
    }
}
