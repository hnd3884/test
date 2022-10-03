package com.me.devicemanagement.framework.server.ddextension;

import org.xml.sax.SAXException;
import java.util.logging.Level;
import org.xml.sax.Attributes;
import java.util.logging.Logger;
import java.util.Stack;
import java.util.HashMap;
import org.xml.sax.helpers.DefaultHandler;

public class DDExtnSaxHandler extends DefaultHandler implements DDExtnConstants
{
    private HashMap<String, HashMap> tables;
    private HashMap<String, HashMap> columns;
    private HashMap<String, String> columnDefns;
    private Stack<String> parentsStack;
    DataDictionaryExtn dataDictionaryExtn;
    private Logger logger;
    private StringBuilder propertyValue;
    
    public HashMap<String, HashMap> getTables() {
        return this.tables;
    }
    
    public DDExtnSaxHandler() {
        this.tables = null;
        this.columns = null;
        this.columnDefns = null;
        this.parentsStack = new Stack<String>();
        this.dataDictionaryExtn = null;
        this.logger = Logger.getLogger(DDExtnSaxHandler.class.getName());
        this.propertyValue = null;
        this.tables = new HashMap<String, HashMap>();
    }
    
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        if (qName.length() > 0) {
            this.parentsStack.push(qName);
            if ("table".equalsIgnoreCase(qName)) {
                (this.dataDictionaryExtn = new DataDictionaryExtn()).setTableName(attributes.getValue("name"));
            }
            else if ("columns".equalsIgnoreCase(qName)) {
                this.dataDictionaryExtn.setColumnPropsDefns(new HashMap());
                this.columns = new HashMap<String, HashMap>();
            }
            else if ("column".equalsIgnoreCase(qName)) {
                this.dataDictionaryExtn.setColumnName(attributes.getValue("name"));
                this.columnDefns = new HashMap<String, String>();
            }
            else if ("data-dictionary-extn".equalsIgnoreCase(qName)) {
                this.logger.log(Level.INFO, "StartElement received is " + qName);
            }
            else {
                this.propertyValue = new StringBuilder();
            }
        }
    }
    
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if ("table".equalsIgnoreCase(qName)) {
            this.tables.put(this.dataDictionaryExtn.getTableName(), this.dataDictionaryExtn.getColumns());
        }
        else if ("columns".equalsIgnoreCase(qName)) {
            this.dataDictionaryExtn.setColumns(this.columns);
        }
        else if ("column".equalsIgnoreCase(qName)) {
            this.columns.put(this.dataDictionaryExtn.getColumnName(), this.columnDefns);
        }
        else if ("data-dictionary-extn".equalsIgnoreCase(qName)) {
            this.logger.log(Level.INFO, "StartElement received is " + qName);
        }
        else if (this.columnDefns != null && this.columns != null && this.propertyValue != null) {
            final HashMap hashMap = this.dataDictionaryExtn.getColumnPropsDefns();
            if (hashMap != null && !this.parentsStack.isEmpty()) {
                hashMap.put(this.parentsStack.peek(), this.propertyValue.toString());
                this.dataDictionaryExtn.setColumnPropsDefns(hashMap);
            }
            this.columnDefns.put(qName, String.valueOf(this.dataDictionaryExtn.getColumnPropsDefns().get(qName)));
        }
        if (!this.parentsStack.isEmpty()) {
            this.parentsStack.pop();
        }
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (this.propertyValue != null) {
            this.propertyValue.append(new String(ch, start, length).trim());
        }
    }
}
