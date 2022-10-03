package com.adventnet.db.persistence.metadata.parser;

import java.util.Iterator;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.DataDictionary;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class DDErrorInfo
{
    private static Logger log;
    public ArrayList<String> exceptions;
    public ArrayList<String> warnings;
    public ArrayList<String> searchTerms;
    public HashMap<Integer, String> warningKey;
    public HashMap<Integer, String> exceptionKey;
    public HashMap<Integer, Integer> exceptionType;
    public HashMap<Integer, Integer> warningType;
    public DataDictionary dd;
    private int exceptionIndex;
    private int warningIndex;
    
    public DDErrorInfo() {
        this.exceptions = new ArrayList<String>();
        this.warnings = new ArrayList<String>();
        this.searchTerms = new ArrayList<String>();
        this.warningKey = new HashMap<Integer, String>();
        this.exceptionKey = new HashMap<Integer, String>();
        this.exceptionType = new HashMap<Integer, Integer>();
        this.warningType = new HashMap<Integer, Integer>();
        this.dd = null;
        this.exceptionIndex = 1;
        this.warningIndex = 1;
    }
    
    protected void addException(final String exceptionDesc, final TableDefinition td, int exceptionType, final String searchTerm) {
        final int index = this.exceptionIndex;
        this.exceptions.add(index + ". " + exceptionDesc);
        if (exceptionType == 2 && td.getPrimaryKey() != null && !td.getPrimaryKey().getName().equals("")) {
            this.exceptionKey.put(index, td.getPrimaryKey().getName());
        }
        else {
            this.exceptionKey.put(index, td.getTableName());
            exceptionType = 1;
        }
        this.exceptionType.put(index, exceptionType);
        if (searchTerm != null) {
            this.searchTerms.add(searchTerm);
        }
        ++this.exceptionIndex;
    }
    
    protected void addWarning(final String warningDesc, final TableDefinition td, int warningType, final String searchTerm) {
        final int index = this.warningIndex;
        this.warnings.add(index + ". " + warningDesc);
        if (warningType == 2 && td.getPrimaryKey() != null && !td.getPrimaryKey().getName().equals("")) {
            this.warningKey.put(index, td.getPrimaryKey().getName());
        }
        else {
            this.warningKey.put(index, td.getTableName());
            warningType = 1;
        }
        this.warningType.put(index, warningType);
        if (searchTerm != null) {
            this.searchTerms.add(searchTerm);
        }
        ++this.warningIndex;
    }
    
    public void showErrorInfo() {
        DDErrorInfo.log.info("*************************Exceptions*************************");
        for (final String a : this.exceptions) {
            DDErrorInfo.log.info(a);
        }
        DDErrorInfo.log.info("*************************Warnings*************************");
        for (final String a : this.warnings) {
            DDErrorInfo.log.info(a);
        }
    }
    
    public ArrayList<String> getExceptions() {
        return this.exceptions;
    }
    
    public ArrayList<String> getWarnings() {
        return this.warnings;
    }
    
    public ArrayList<String> getSearchTerms() {
        return this.searchTerms;
    }
    
    public HashMap<Integer, String> getExceptionkeys() {
        return this.exceptionKey;
    }
    
    public HashMap<Integer, String> getWarningkeys() {
        return this.warningKey;
    }
    
    public HashMap<Integer, Integer> getExceptionType() {
        return this.exceptionType;
    }
    
    public HashMap<Integer, Integer> getWarningType() {
        return this.warningType;
    }
    
    public DataDictionary getDataDicationary() {
        return this.dd;
    }
    
    static {
        DDErrorInfo.log = Logger.getLogger(DDErrorInfo.class.getName());
    }
}
