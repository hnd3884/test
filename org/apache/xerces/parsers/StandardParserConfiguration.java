package org.apache.xerces.parsers;

import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.impl.xs.XMLSchemaValidator;

public class StandardParserConfiguration extends DTDConfiguration
{
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String IGNORE_XSI_TYPE = "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl";
    protected static final String ID_IDREF_CHECKING = "http://apache.org/xml/features/validation/id-idref-checking";
    protected static final String UNPARSED_ENTITY_CHECKING = "http://apache.org/xml/features/validation/unparsed-entity-checking";
    protected static final String IDENTITY_CONSTRAINT_CHECKING = "http://apache.org/xml/features/validation/identity-constraint-checking";
    protected static final String TYPE_ALTERNATIVES_CHECKING = "http://apache.org/xml/features/validation/type-alternative-checking";
    protected static final String CTA_FULL_XPATH_CHECKING = "http://apache.org/xml/features/validation/cta-full-xpath-checking";
    protected static final String ASSERT_COMMENT_PI_CHECKING = "http://apache.org/xml/features/validation/assert-comments-and-pi-checking";
    protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
    protected static final String ROOT_TYPE_DEF = "http://apache.org/xml/properties/validation/schema/root-type-definition";
    protected static final String ROOT_ELEMENT_DECL = "http://apache.org/xml/properties/validation/schema/root-element-declaration";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    protected XMLSchemaValidator fSchemaValidator;
    
    public StandardParserConfiguration() {
        this(null, null, null);
    }
    
    public StandardParserConfiguration(final SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }
    
    public StandardParserConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool) {
        this(symbolTable, xmlGrammarPool, null);
    }
    
    public StandardParserConfiguration(final SymbolTable symbolTable, final XMLGrammarPool xmlGrammarPool, final XMLComponentManager xmlComponentManager) {
        super(symbolTable, xmlGrammarPool, xmlComponentManager);
        this.addRecognizedFeatures(new String[] { "http://apache.org/xml/features/validation/schema/normalized-value", "http://apache.org/xml/features/validation/schema/element-default", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl", "http://apache.org/xml/features/validation/id-idref-checking", "http://apache.org/xml/features/validation/identity-constraint-checking", "http://apache.org/xml/features/validation/unparsed-entity-checking", "http://apache.org/xml/features/validation/type-alternative-checking", "http://apache.org/xml/features/validation/cta-full-xpath-checking", "http://apache.org/xml/features/validation/assert-comments-and-pi-checking" });
        this.setFeature("http://apache.org/xml/features/validation/schema/element-default", true);
        this.setFeature("http://apache.org/xml/features/validation/schema/normalized-value", true);
        this.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", true);
        this.setFeature("http://apache.org/xml/features/generate-synthetic-annotations", false);
        this.setFeature("http://apache.org/xml/features/validate-annotations", false);
        this.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", false);
        this.setFeature("http://apache.org/xml/features/namespace-growth", false);
        this.setFeature("http://apache.org/xml/features/internal/tolerate-duplicates", false);
        this.setFeature("http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl", false);
        this.setFeature("http://apache.org/xml/features/validation/id-idref-checking", true);
        this.setFeature("http://apache.org/xml/features/validation/identity-constraint-checking", true);
        this.setFeature("http://apache.org/xml/features/validation/unparsed-entity-checking", true);
        this.setFeature("http://apache.org/xml/features/validation/type-alternative-checking", true);
        this.setFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking", false);
        this.setFeature("http://apache.org/xml/features/validation/assert-comments-and-pi-checking", false);
        this.addRecognizedProperties(new String[] { "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://apache.org/xml/properties/validation/schema/root-type-definition", "http://apache.org/xml/properties/validation/schema/root-element-declaration", "http://apache.org/xml/properties/internal/validation/schema/dv-factory" });
    }
    
    protected void configurePipeline() {
        super.configurePipeline();
        if (this.getFeature("http://apache.org/xml/features/validation/schema")) {
            if (this.fSchemaValidator == null) {
                this.fSchemaValidator = new XMLSchemaValidator();
                this.fProperties.put("http://apache.org/xml/properties/internal/validator/schema", this.fSchemaValidator);
                this.addComponent(this.fSchemaValidator);
                if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
                    this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
                }
            }
            this.fLastComponent = this.fSchemaValidator;
            this.fNamespaceBinder.setDocumentHandler(this.fSchemaValidator);
            this.fSchemaValidator.setDocumentHandler(this.fDocumentHandler);
            this.fSchemaValidator.setDocumentSource(this.fNamespaceBinder);
        }
    }
    
    protected void checkFeature(final String s) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/features/")) {
            final int n = s.length() - "http://apache.org/xml/features/".length();
            if (n == "validation/schema".length() && s.endsWith("validation/schema")) {
                return;
            }
            if (n == "validation/schema-full-checking".length() && s.endsWith("validation/schema-full-checking")) {
                return;
            }
            if (n == "validation/schema/normalized-value".length() && s.endsWith("validation/schema/normalized-value")) {
                return;
            }
            if (n == "validation/schema/element-default".length() && s.endsWith("validation/schema/element-default")) {
                return;
            }
        }
        super.checkFeature(s);
    }
    
    protected void checkProperty(final String s) throws XMLConfigurationException {
        if (s.startsWith("http://apache.org/xml/properties/")) {
            final int n = s.length() - "http://apache.org/xml/properties/".length();
            if (n == "schema/external-schemaLocation".length() && s.endsWith("schema/external-schemaLocation")) {
                return;
            }
            if (n == "schema/external-noNamespaceSchemaLocation".length() && s.endsWith("schema/external-noNamespaceSchemaLocation")) {
                return;
            }
        }
        if (s.startsWith("http://java.sun.com/xml/jaxp/properties/") && s.length() - "http://java.sun.com/xml/jaxp/properties/".length() == "schemaSource".length() && s.endsWith("schemaSource")) {
            return;
        }
        super.checkProperty(s);
    }
}
