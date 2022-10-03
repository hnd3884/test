package jdk.jfr.internal.jfc;

import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.Attributes;
import java.util.LinkedHashMap;
import java.util.Map;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

final class JFCParserHandler extends DefaultHandler
{
    private static final String ELEMENT_CONFIGURATION = "configuration";
    private static final String ELEMENT_EVENT_TYPE = "event";
    private static final String ELEMENT_SETTING = "setting";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_LABEL = "label";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    private static final String ATTRIBUTE_PROVIDER = "provider";
    private static final String ATTRIBUTE_VERSION = "version";
    final Map<String, String> settings;
    private String currentEventPath;
    private String currentSettingsName;
    private StringBuilder currentCharacters;
    String label;
    String provider;
    String description;
    
    JFCParserHandler() {
        this.settings = new LinkedHashMap<String, String>();
    }
    
    @Override
    public void startElement(final String s, final String s2, final String s3, final Attributes attributes) throws SAXException {
        final String lowerCase = s3.toLowerCase();
        switch (lowerCase) {
            case "configuration": {
                final String value = attributes.getValue("version");
                if (value == null || !value.startsWith("2.")) {
                    throw new SAXException("This version of Flight Recorder can only read JFC file format version 2.x");
                }
                this.label = attributes.getValue("label");
                this.description = this.getOptional(attributes, "description", "");
                this.provider = this.getOptional(attributes, "provider", "");
                break;
            }
            case "event": {
                this.currentEventPath = attributes.getValue("name");
                break;
            }
            case "setting": {
                this.currentSettingsName = attributes.getValue("name");
                break;
            }
        }
        this.currentCharacters = null;
    }
    
    private String getOptional(final Attributes attributes, final String s, final String s2) {
        final String value = attributes.getValue(s);
        return (value == null) ? s2 : value;
    }
    
    @Override
    public void characters(final char[] array, final int n, final int n2) throws SAXException {
        if (this.currentCharacters == null) {
            this.currentCharacters = new StringBuilder(n2);
        }
        this.currentCharacters.append(array, n, n2);
    }
    
    @Override
    public void endElement(final String s, final String s2, final String s3) {
        final String lowerCase = s3.toLowerCase();
        switch (lowerCase) {
            case "event": {
                this.currentEventPath = null;
                break;
            }
            case "setting": {
                this.settings.put(this.currentEventPath + "#" + this.currentSettingsName, "" + ((this.currentCharacters == null) ? "" : this.currentCharacters.toString()));
                this.currentSettingsName = null;
                break;
            }
        }
    }
    
    public Map<String, String> getSettings() {
        return this.settings;
    }
}
