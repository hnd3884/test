package com.sun.org.apache.xalan.internal.xsltc.trax;

import java.util.Properties;

public final class OutputSettings
{
    private String _cdata_section_elements;
    private String _doctype_public;
    private String _encoding;
    private String _indent;
    private String _media_type;
    private String _method;
    private String _omit_xml_declaration;
    private String _standalone;
    private String _version;
    
    public OutputSettings() {
        this._cdata_section_elements = null;
        this._doctype_public = null;
        this._encoding = null;
        this._indent = null;
        this._media_type = null;
        this._method = null;
        this._omit_xml_declaration = null;
        this._standalone = null;
        this._version = null;
    }
    
    public Properties getProperties() {
        final Properties properties = new Properties();
        return properties;
    }
}
