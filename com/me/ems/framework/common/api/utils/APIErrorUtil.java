package com.me.ems.framework.common.api.utils;

import org.xml.sax.Attributes;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import com.me.ems.framework.common.api.v1.model.ErrorInfo;
import org.xml.sax.helpers.DefaultHandler;
import com.me.devicemanagement.framework.utils.XMLUtils;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

public class APIErrorUtil
{
    static Logger logger;
    public static HashMap<String, String> errorCodeMap;
    public static HashMap<String, Integer> httpStatusMap;
    public static HashMap<String, String> referenceURIMap;
    
    public static void parseAndAddErrorsFromXML(final File errorConstantsFile) {
        try {
            final SAXParser parser = XMLUtils.getSAXParser();
            final DataLoader dataLoader = new DataLoader();
            parser.parse(errorConstantsFile, dataLoader);
            if (APIErrorUtil.errorCodeMap == null) {
                APIErrorUtil.errorCodeMap = new HashMap<String, String>();
            }
            if (APIErrorUtil.httpStatusMap == null) {
                APIErrorUtil.httpStatusMap = new HashMap<String, Integer>();
            }
            if (APIErrorUtil.referenceURIMap == null) {
                APIErrorUtil.referenceURIMap = new HashMap<String, String>();
            }
            for (final ErrorInfo error : dataLoader.getErrors()) {
                APIErrorUtil.errorCodeMap.put(error.getErrorCode(), error.getI18nKey());
                APIErrorUtil.httpStatusMap.put(error.getErrorCode(), error.getHttpStatus());
                APIErrorUtil.referenceURIMap.put(error.getErrorCode(), error.getReferenceUri());
            }
        }
        catch (final SAXException | ParserConfigurationException e) {
            APIErrorUtil.logger.log(Level.SEVERE, "Error while parsing XML", e);
        }
        catch (final IOException e2) {
            APIErrorUtil.logger.log(Level.SEVERE, "Error while opening XML File", e2);
        }
        catch (final Exception e) {
            APIErrorUtil.logger.log(Level.SEVERE, "Error in XML File", e);
        }
    }
    
    private static List<String> getApiErrorInfoFiles() {
        final List<String> list = new ArrayList<String>();
        try {
            final JSONObject jsonObject = FrameworkConfigurations.getFrameworkConfigurations();
            final JSONArray arr = jsonObject.names();
            for (int i = 0; i < arr.length(); ++i) {
                if (String.valueOf(arr.get(i)).contains("api_error_info_config_files")) {
                    list.add(String.valueOf(jsonObject.get(String.valueOf(arr.get(i)))));
                }
            }
        }
        catch (final JSONException ex) {
            APIErrorUtil.logger.log(Level.SEVERE, "Exception occured while gettings api_error_info_files.xml");
        }
        return list;
    }
    
    static {
        APIErrorUtil.logger = Logger.getLogger(APIErrorUtil.class.getName());
        APIErrorUtil.errorCodeMap = null;
        APIErrorUtil.httpStatusMap = null;
        APIErrorUtil.referenceURIMap = null;
        final List files = getApiErrorInfoFiles();
        for (final String filename : files) {
            final File errorConstantsXMLFile = new File(System.getProperty("server.home") + File.separator + filename);
            parseAndAddErrorsFromXML(errorConstantsXMLFile);
        }
    }
    
    static class DataLoader extends DefaultHandler
    {
        private List errors;
        private ErrorInfo error;
        private boolean isValue;
        private boolean isI18Key;
        private boolean isHttpStatusCode;
        private boolean isReferenceUri;
        
        DataLoader() {
            this.errors = null;
            this.isValue = false;
            this.isI18Key = false;
            this.isHttpStatusCode = false;
            this.isReferenceUri = false;
        }
        
        public List<ErrorInfo> getErrors() {
            return this.errors;
        }
        
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("value")) {
                this.isValue = true;
                (this.error = new ErrorInfo()).setErrorCode(attributes.getValue("name"));
            }
            else if (qName.equalsIgnoreCase("i18nkey")) {
                this.isI18Key = true;
            }
            else if (qName.equalsIgnoreCase("httpstatus")) {
                this.isHttpStatusCode = true;
            }
            else if (qName.equalsIgnoreCase("referenceUri")) {
                this.isReferenceUri = true;
            }
        }
        
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if (qName.equalsIgnoreCase("value")) {
                this.isValue = false;
                if (this.errors == null) {
                    this.errors = new ArrayList();
                }
                this.errors.add(this.error);
            }
            else if (qName.equalsIgnoreCase("i18nkey")) {
                this.isI18Key = false;
            }
            else if (qName.equalsIgnoreCase("httpstatus")) {
                this.isHttpStatusCode = false;
            }
            else if (qName.equalsIgnoreCase("referenceUri")) {
                this.isReferenceUri = false;
            }
        }
        
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            if (this.isI18Key) {
                this.error.setI18nKey(new String(ch, start, length));
            }
            else if (this.isHttpStatusCode) {
                this.error.setHttpStatus(Integer.valueOf(new String(ch, start, length)));
            }
            else if (this.isReferenceUri) {
                this.error.setReferenceUri(new String(ch, start, length));
            }
        }
    }
}
