package com.adventnet.swissqlapi.sql.statement;

import java.util.ArrayList;

public class PrepareDocument
{
    private static ArrayList idocs;
    private static ArrayList xmls;
    private static ArrayList xpaths;
    
    public static void prepareDocument(final String idoc, final String xml, final String xpath) {
        PrepareDocument.idocs.add(0, idoc.toLowerCase());
        PrepareDocument.xmls.add(0, xml);
        PrepareDocument.xpaths.add(0, xpath);
    }
    
    public static void removeDocument(final String idoc, final String xml, final String xpath) {
    }
    
    public static String getXML(String idoc) {
        idoc = idoc.toLowerCase();
        if (PrepareDocument.idocs.size() == 0) {
            return "";
        }
        if (PrepareDocument.idocs.indexOf(idoc) != -1) {
            return PrepareDocument.xmls.get(PrepareDocument.idocs.indexOf(idoc));
        }
        if (PrepareDocument.idocs.indexOf(idoc.substring(1)) != -1) {
            return PrepareDocument.xmls.get(PrepareDocument.idocs.indexOf(idoc.substring(1)));
        }
        return "";
    }
    
    public static void resetPrepareDocument() {
        PrepareDocument.idocs = new ArrayList();
        PrepareDocument.xmls = new ArrayList();
        PrepareDocument.xpaths = new ArrayList();
    }
    
    static {
        PrepareDocument.idocs = new ArrayList();
        PrepareDocument.xmls = new ArrayList();
        PrepareDocument.xpaths = new ArrayList();
    }
}
