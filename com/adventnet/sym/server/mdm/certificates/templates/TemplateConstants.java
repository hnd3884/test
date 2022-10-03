package com.adventnet.sym.server.mdm.certificates.templates;

public class TemplateConstants
{
    public static final class General
    {
        public static final String TEMPLATE_TYPE = "TEMPLATE_TYPE";
        public static final String TEMPLATE_ID = "TEMPLATE_ID";
        public static final String TEMPLATE_NAME = "TEMPLATE_NAME";
        public static final String CERTIFICATE_OID = "CERTIFICATE_OID";
        public static final String CRITERIA = "CRITERIA";
        public static final String DEFAULT_TEMPLATE_NAME = "DIGICERT";
    }
    
    public static final class TemplateType
    {
        public static final int DIGICERT = 1;
    }
    
    public static final class Api
    {
        public static final class Url
        {
            public static String templates;
            
            static {
                Url.templates = "templates";
            }
        }
        
        public static final class Key
        {
            public static String template_id;
            public static String template_name;
            public static String template_type;
            
            static {
                Key.template_id = "template_id";
                Key.template_name = "template_name";
                Key.template_type = "template_type";
            }
            
            public static final class Digicert
            {
                public static String certificate_oid;
                
                static {
                    Digicert.certificate_oid = "certificate_oid";
                }
            }
        }
    }
}
