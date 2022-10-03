package com.unboundid.ldap.sdk.persist;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum PersistMessages
{
    ERR_DEFAULT_ENCODER_CANNOT_ADD("An error occurred while attempting to add an element to a list or set:  {0}"), 
    ERR_DEFAULT_ENCODER_CANNOT_DESERIALIZE("An error occurred while attempting to deserialize an object for use in attribute {0}:  {1}"), 
    ERR_DEFAULT_ENCODER_CANNOT_FIND_ADD_METHOD("Unable to find the method to use to add an item to a list or set."), 
    ERR_DEFAULT_ENCODER_CANNOT_SERIALIZE("An error occurred while attempting to serialize an object for use in attribute {0}:  {1}"), 
    ERR_DEFAULT_ENCODER_UNSUPPORTED_TYPE("The default object encoder does not support objects of type {0}."), 
    ERR_DEFAULT_ENCODER_VALUE_INVALID_BOOLEAN("Value ''{0}'' cannot be parsed as a Boolean value."), 
    ERR_DEFAULT_ENCODER_VALUE_INVALID_DATE("Value ''{0}'' cannot be parsed as a Date value:  {1}"), 
    ERR_DEFAULT_ENCODER_VALUE_INVALID_ENUM("Value ''{0}'' is not a valid value for the associated enum:  {1}"), 
    ERR_DEFAULT_ENCODER_VALUE_INVALID_URI("Value ''{0}'' cannot be parsed as a URI value:  {1}"), 
    ERR_DEFAULT_ENCODER_VALUE_INVALID_URL("Value ''{0}'' cannot be parsed as a URL value:  {1}"), 
    ERR_DEFAULT_ENCODER_VALUE_INVALID_UUID("Value ''{0}'' cannot be parsed as a UUID value:  {1}"), 
    ERR_ENCODER_CANNOT_SET_NULL_FIELD_VALUE("An error occurred while attempting to set a null or default value to field {0} in an instance of class {1}:  {2}"), 
    ERR_ENCODER_CANNOT_SET_NULL_METHOD_VALUE("An error occurred while attempting to invoke method {0} to set a null or default value to an instance of class {1}:  {2}"), 
    ERR_FIELD_INFO_CANNOT_ENCODE("An error occurred while attempting to encode the value of field {0} in an object of type {1} to an attribute:  {2}"), 
    ERR_FIELD_INFO_CANNOT_GET_ENCODER("An error occurred while attempting to create an instance of class {0} for encoding field {1} in class {2}:  {3}"), 
    ERR_FIELD_INFO_CLASS_NOT_ANNOTATED("Class {0} does not contain the @LDAPObject annotation, but an attempt was made to treat it like it did have that annotation."), 
    ERR_FIELD_INFO_ENCODER_UNSUPPORTED_TYPE("The encoder type {0} configured for field {1} in class {2} does not support objects of type {3}."), 
    ERR_FIELD_INFO_FIELD_FINAL("Field {0} in class {1} is declared final, which is not supported in conjunction with the @LDAPField annotation."), 
    ERR_FIELD_INFO_FIELD_NOT_ANNOTATED("Field {0} in class {1} does not contain the @LDAPField annotation, but an attempt was made to treat it like it did have that annotation."), 
    ERR_FIELD_INFO_FIELD_NOT_MULTIVALUED("Unable to use attribute {0} to assign field {1} in an instance of class {2} because the provided attribute has multiple values but the field can only hold a single value."), 
    ERR_FIELD_INFO_FIELD_STATIC("Field {0} in class {1} is declared static, which is not supported in conjunction with the @LDAPField annotation."), 
    ERR_FIELD_INFO_INVALID_ATTR_NAME("The @LDAPField annotation for field {0} in class {1} cannot be used to construct a valid LDAP attribute name:  {2}"), 
    ERR_FIELD_INFO_INVALID_OC("The @LDAPField annotation for field {0} in class {1} references object class {2} which is not listed as a structural or auxiliary class for the corresponding @LDAPObject annotation on the class."), 
    ERR_FIELD_INFO_LAZY_IN_RDN("Field {0} in class {1} is configured to be lazily-loaded, but is also marked for inclusion in entry RDNs.  Lazily-loaded fields cannot be marked for inclusion in entry RDNs."), 
    ERR_FIELD_INFO_LAZY_WITH_DEFAULT_DECODE("Field {0} in class {1} is configured to be lazily-loaded, but also contains one or more default decode values.  Lazily-loaded fields cannot be assigned default decode values."), 
    ERR_FIELD_INFO_LAZY_WITH_DEFAULT_ENCODE("Field {0} in class {1} is configured to be lazily-loaded, but also contains one or more default encode values.  Lazily-loaded fields cannot be assigned default encode values."), 
    ERR_FIELD_INFO_MISSING_REQUIRED_ATTRIBUTE("Unable to initialize an object of type {0} from entry {1} because the provided entry did not have a value for attribute {2} associated with required field {3}."), 
    ERR_FIELD_INFO_MISSING_REQUIRED_VALUE("Unable to encode the value of field {0} in an object of type {1} to an attribute because it is a required field but it does not have a value."), 
    ERR_FIELD_INFO_UNSUPPORTED_MULTIPLE_DEFAULT_DECODE_VALUES("Field {0} in class {1} is configured with multiple default decode values, but that field can only hold a single value."), 
    ERR_FIELD_INFO_UNSUPPORTED_MULTIPLE_DEFAULT_ENCODE_VALUES("Field {0} in class {1} is configured with multiple default encode values, but that field can only hold a single value."), 
    ERR_GEN_SCHEMA_CANNOT_LOAD_CLASS("Unable to load class ''{0}''.  Please make sure you have provided the correct fully-qualified class name and that it is contained in the Java classpath."), 
    ERR_GEN_SCHEMA_CANNOT_WRITE_SCHEMA("An error occurred while attempting to write the generated schema to file {0}:  {1}"), 
    ERR_GEN_SCHEMA_ERROR_CONSTRUCTING_ATTRS("An error occurred while attempting to construct a set of LDAP attribute type definitions from the information in class {0}:  {1}"), 
    ERR_GEN_SCHEMA_ERROR_CONSTRUCTING_OCS("An error occurred while attempting to construct a set of LDAP object class definitions from the information in class {0}:  {1}"), 
    ERR_GEN_SCHEMA_INVALID_CLASS("Class ''{0}'' cannot be used with the LDAP persistence framework:  {1}"), 
    ERR_GEN_SOURCE_AUXILIARY_CLASS_NOT_AUXILIARY("The specified auxiliary object class ''{0}'' is not declared auxiliary in the directory server schema."), 
    ERR_GEN_SOURCE_AUXILIARY_CLASS_NOT_FOUND("The specified auxiliary object class ''{0}'' was not found in the directory server schema."), 
    ERR_GEN_SOURCE_CANNOT_CONNECT("Unable to establish a connection to the directory server in order to read the schema:  {0}"), 
    ERR_GEN_SOURCE_CANNOT_CREATE_WRITER("Unable to open file ''{0}'' for writing:  {1}"), 
    ERR_GEN_SOURCE_CANNOT_READ_SCHEMA("An error occurred while trying to read the directory server schema:  {0}"), 
    ERR_GEN_SOURCE_INVALID_CLASS_NAME("The provided class name ''{0}'' cannot be used as a valid Java identifier:  {1}"), 
    ERR_GEN_SOURCE_LAZY_ATTRIBUTE_NOT_ALLOWED("The specified lazily-loaded attribute ''{0}'' is not associated with any of the provided structural or auxiliary object classes."), 
    ERR_GEN_SOURCE_LAZY_ATTRIBUTE_NOT_DEFINED("The specified lazily-loaded attribute ''{0}'' is not defined in the server schema."), 
    ERR_GEN_SOURCE_OPERATIONAL_ATTRIBUTE_NOT_DEFINED("The specified operational attribute ''{0}'' is not defined in the directory server schema."), 
    ERR_GEN_SOURCE_OPERATIONAL_ATTRIBUTE_NOT_OPERATIONAL("The specified operational attribute ''{0}'' is not defined as operational in the directory server schema."), 
    ERR_GEN_SOURCE_RDN_ATTRIBUTE_NOT_DEFINED("The specified RDN attribute ''{0}'' is not associated with any of the provided structural or auxiliary object classes."), 
    ERR_GEN_SOURCE_SCHEMA_NOT_RETURNED("No schema entry was returned by the server."), 
    ERR_GEN_SOURCE_STRUCTURAL_CLASS_NOT_FOUND("The specified structural object class ''{0}'' was not found in the directory server schema."), 
    ERR_GEN_SOURCE_STRUCTURAL_CLASS_NOT_STRUCTURAL("The specified structural object class ''{0}'' is not declared structural in the directory server schema."), 
    ERR_GETTER_INFO_CANNOT_ENCODE("An error occurred while attempting to encode the value returned by method {0} in an object of type {1} to an attribute:  {2}"), 
    ERR_GETTER_INFO_CANNOT_GET_ENCODER("An error occurred while attempting to create an instance of class {0} for encoding values of method {1} in class {2}:  {3}"), 
    ERR_GETTER_INFO_CANNOT_INFER_ATTR("Unable to determine the name of the LDAP attribute associated with getter method {0} in class {1} because the @LDAPGetter annotation does not include an attribute element and the method name does no start with 'get'"), 
    ERR_GETTER_INFO_CLASS_NOT_ANNOTATED("Class {0} does not contain the @LDAPObject annotation, but an attempt was made to treat it like it did have that annotation."), 
    ERR_GETTER_INFO_ENCODER_UNSUPPORTED_TYPE("The encoder type {0} configured for method {1} in class {2} does not support methods which return values of type {3}."), 
    ERR_GETTER_INFO_INVALID_OC("The @LDAPGetter annotation for method {0} in class {1} references object class {2} which is not listed as a structural or auxiliary class for the corresponding @LDAPObject annotation on the class."), 
    ERR_GETTER_INFO_METHOD_NOT_ANNOTATED("Method {0} in class {1} does not contain the @LDAPGetter annotation, but an attempt was made to treat it like it did have that annotation."), 
    ERR_GETTER_INFO_METHOD_STATIC("Method {0} in class {1} is declared static, which is not supported in conjunction with the @LDAPGetter annotation."), 
    ERR_GETTER_INFO_METHOD_TAKES_ARGUMENTS("Method {0} in class {1} cannot be marked with the @LDAPGetter annotation because that method takes one or more arguments.  The @LDAPGetter annotation may only be used with methods which do not take any arguments."), 
    ERR_JAVA_NAME_VALIDATOR_INVALID_CHAR("The provided string ''{0}'' cannot be used as a valid Java identifier because it contains an invalid character ''{1}'' at position {2,number,0}."), 
    ERR_JAVA_NAME_VALIDATOR_INVALID_FIRST_CHAR_DIGIT("The provided string ''{0}'' cannot be used as a valid Java identifier because it starts with a numeric digit."), 
    ERR_LDAP_NAME_VALIDATOR_EMPTY("The provided string cannot be used as a valid LDAP attribute or object class name because it is null or empty."), 
    ERR_LDAP_NAME_VALIDATOR_ENDS_WITH_SEMICOLON("The provided string ''{0}'' cannot be used as a valid LDAP attribute description because it ends with a semicolon."), 
    ERR_LDAP_NAME_VALIDATOR_INVALID_CHAR("The provided string ''{0}'' cannot be used as a valid LDAP attribute or object class name because it has an invalid character ''{1}'' at position {2,number,0}."), 
    ERR_LDAP_NAME_VALIDATOR_INVALID_FIRST_CHAR("The provided string ''{0}'' cannot be used as a valid LDAP attribute or object class name because it does not start with an ASCII letter."), 
    ERR_LDAP_NAME_VALIDATOR_INVALID_OPTION_CHAR("The provided string ''{0}'' cannot be used as a valid LDAP attribute description because it contains character ''{1}'' at position {2,number,0}, but that character cannot be used in attribute options."), 
    ERR_LDAP_NAME_VALIDATOR_OPTION_WITH_CONSECUTIVE_SEMICOLONS("The provided string ''{0}'' cannot be used as a valid LDAP attribute description because it contains multiple consecutive semicolons."), 
    ERR_OBJECT_HANDLER_ATTR_CONFLICT("Class {0} includes conflicting annotations for fields and/or methods that will interact with the {1} attribute."), 
    ERR_OBJECT_HANDLER_CONFLICTING_FIELD_ANNOTATIONS("Class {0} has conflicting annotations {1} and {2} for field {3}."), 
    ERR_OBJECT_HANDLER_CONFLICTING_METHOD_ANNOTATIONS("Class {0} has conflicting annotations {1} and {2} for method {3}."), 
    ERR_OBJECT_HANDLER_DN_FIELD_FINAL("Field {0} in class {1} is marked with the @LDAPDNField annotation but is also declared final.  Fields marked with the @LDAPDNField annotation must not be final."), 
    ERR_OBJECT_HANDLER_DN_FIELD_STATIC("Field {0} in class {1} is marked with the @LDAPDNField annotation but is also declared static.  Fields marked with the @LDAPDNField annotation must not be static."), 
    ERR_OBJECT_HANDLER_ENTRY_FIELD_FINAL("Field {0} in class {1} is marked with the @LDAPEntryField annotation but is also declared final.  Fields marked with the @LDAPEntryField annotation must not be final."), 
    ERR_OBJECT_HANDLER_ENTRY_FIELD_STATIC("Field {0} in class {1} is marked with the @LDAPEntryField annotation but is also declared static.  Fields marked with the @LDAPEntryField annotation must not be static."), 
    ERR_OBJECT_HANDLER_ERROR_ACCESSING_DN_FIELD("An error occurred while attempting to access the value of the @LDAPDNField {0} in class {1} to get the DN of the associated entry:  {2}"), 
    ERR_OBJECT_HANDLER_ERROR_ACCESSING_ENTRY_FIELD("An error occurred while attempting to access the value of the @LDAPEntryField {0} in class {1} to get the associated entry:  {2}"), 
    ERR_OBJECT_HANDLER_ERROR_INVOKING_CONSTRUCTOR("An error occurred while attempting to create a new object of type {0} using the default constructor:  {1}"), 
    ERR_OBJECT_HANDLER_ERROR_INVOKING_POST_DECODE_METHOD("An error occurred while attempting to invoke post-decode method {0} on an instance of object {1}:  {2}"), 
    ERR_OBJECT_HANDLER_ERROR_INVOKING_POST_ENCODE_METHOD("An error occurred while attempting to invoke post-encode method {0} on an instance of object {1}:  {2}"), 
    ERR_OBJECT_HANDLER_ERROR_SETTING_DN("An error occurred while attempting to update an object of type {0} to assign value ''{1}'' to DN field {2}:  {3}"), 
    ERR_OBJECT_HANDLER_ERROR_SETTING_ENTRY("An error occurred while attempting to update an object of type {0} to assign a value to entry field {1}:  {2}"), 
    ERR_OBJECT_HANDLER_FILTER_MISSING_REQUIRED_FIELD("Unable to construct a search filter that can be used to search for entries matching the provided object because the object did not have a value for field {0} which is required when generating a filter."), 
    ERR_OBJECT_HANDLER_FILTER_MISSING_REQUIRED_GETTER("Unable to construct a search filter that can be used to search for entries matching the provided object because the object did not have a value for getter method {0} which is required when generating a filter."), 
    ERR_OBJECT_HANDLER_FILTER_MISSING_REQUIRED_OR_ALLOWED("Unable to construct a search filter that can be used to search for entries matching the provided object because the object did not have any field or getter method values which could be included in the filter."), 
    ERR_OBJECT_HANDLER_INVALID_AUXILIARY_CLASS("Class {0} is configured to use an auxiliary object class of ''{1}'', which is not a valid LDAP object class name:  {2}"), 
    ERR_OBJECT_HANDLER_INVALID_DEFAULT_PARENT("The @LDAPObject annotation in class {0} has an invalid default parent DN of ''{1}'':  {2}"), 
    ERR_OBJECT_HANDLER_INVALID_DN_FIELD_TYPE("Class {0} includes the @LDAPDNField annotation for field {1} of type {2} which is not an allowed type for DN fields.  DN fields must have a type of java.lang.String."), 
    ERR_OBJECT_HANDLER_INVALID_ENTRY_FIELD_TYPE("Class {0} includes the @LDAPEntryField annotation for field {1} of type {2} which is not an allowed type for entry fields.  Entry fields must have a type of com.unboundid.ldap.sdk.ReadOnlyEntry."), 
    ERR_OBJECT_HANDLER_INVALID_PARENT_DN("Unable to construct the DN of the entry for an object of type {0} because the provided parent DN ''{1}'' is not a valid DN:  {2}"), 
    ERR_OBJECT_HANDLER_INVALID_POST_DECODE_METHOD("The @LDAPObject annotation in class {0} has an invalid post-decode method of ''{1}'':  {2}"), 
    ERR_OBJECT_HANDLER_INVALID_POST_ENCODE_METHOD("The @LDAPObject annotation in class {0} has an invalid post-encode method of ''{1}'':  {2}"), 
    ERR_OBJECT_HANDLER_INVALID_STRUCTURAL_CLASS("Class {0} is configured to use a structural object class of ''{1}'', which is not a valid LDAP object class name:  {2}"), 
    ERR_OBJECT_HANDLER_INVALID_SUPERIOR_CLASS("Class {0} is configured to use a superior object class of ''{1}'', which is not a valid LDAP object class name:  {2}"), 
    ERR_OBJECT_HANDLER_MULTIPLE_DN_FIELDS("Class {0} has multiple fields marked with the @LDAPDNField annotation.  This annotation may not be used for more than one field in a class."), 
    ERR_OBJECT_HANDLER_MULTIPLE_ENTRY_FIELDS("Class {0} has multiple fields marked with the @LDAPEntryField annotation.  This annotation may not be used for more than one field in a class."), 
    ERR_OBJECT_HANDLER_NO_DEFAULT_CONSTRUCTOR("Class {0} cannot be used with the LDAP SDK persistence framework because it does not provide a zero-argument constructor."), 
    ERR_OBJECT_HANDLER_NO_RDN_DEFINED("Class {0} does not include any fields or getter methods marked for inclusion in the entry RDN."), 
    ERR_OBJECT_HANDLER_OBJECT_NOT_ANNOTATED("Class {0} cannot be used with the LDAP SDK persistence framework because it does not include the @LDAPObject annotation."), 
    ERR_OBJECT_HANDLER_RDN_FIELD_MISSING_VALUE("Unable to construct the DN to use for an object of type {0} because that object does not have a value for RDN field {1}."), 
    ERR_OBJECT_HANDLER_RDN_GETTER_MISSING_VALUE("Unable to construct the DN to use for an object of type {0} because that object does not return a value from RDN getter method {1}."), 
    ERR_OBJECT_SEARCH_RESULTS_ENTRY_SOURCE_EXCEPTION("An error occurred while attempting to read the next search result entry from the directory:  {0}"), 
    ERR_PERSISTER_BIND_NO_ENTRY_FOUND("Unable to find any entry corresponding to the contents of the provided object."), 
    ERR_PERSISTER_DELETE_NO_DN("Unable to remove the provided object from the directory because it was either not retrieved from the directory or does not have a field marked with the @LDAPDNField or @LDAPEntryField annotation."), 
    ERR_PERSISTER_LAZILY_LOAD_NO_DN("Unable to lazily-load any fields for the provided object because the DN of the entry associated with that object cannot be determined."), 
    ERR_PERSISTER_LAZILY_LOAD_NO_ENTRY("Unable to retrieve entry ''{0}'' in order to initialize lazily-loaded fields for the provided object."), 
    ERR_PERSISTER_MODIFY_NO_DN("Unable to determine the target DN to use to update the stored representation for the object because no DN was given for the modify operation and the associated entry DN cannot be obtained from the provided object."), 
    ERR_SETTER_INFO_CANNOT_GET_ENCODER("An error occurred while attempting to create an instance of class {0} for encoding values of method {1} in class {2}:  {3}"), 
    ERR_SETTER_INFO_CANNOT_INFER_ATTR("Unable to determine the name of the LDAP attribute associated with setter method {0} in class {1} because the @LDAPSetter annotation does not include an attribute element and the method name does no start with 'set'"), 
    ERR_SETTER_INFO_CLASS_NOT_ANNOTATED("Class {0} does not contain the @LDAPObject annotation, but an attempt was made to treat it like it did have that annotation."), 
    ERR_SETTER_INFO_ENCODER_UNSUPPORTED_TYPE("The encoder type {0} configured for method {1} in class {2} does not support the method argument type {3}."), 
    ERR_SETTER_INFO_METHOD_DOES_NOT_TAKE_ONE_ARGUMENT("Method {0} in class {1} cannot be marked with the @LDAPSetter annotation because that method does not take exactly one argument.  The @LDAPSetter annotation may only be used with methods which take a single argument."), 
    ERR_SETTER_INFO_METHOD_NOT_ANNOTATED("Method {0} in class {1} does not contain the @LDAPSetter annotation, but an attempt was made to treat it like it did have that annotation."), 
    ERR_SETTER_INFO_METHOD_NOT_MULTIVALUED("Unable to invoke method {0} to assign the values of attribute {1} to an instance of class {2} because that attribute has multiple values but the method argument can only accept a single value."), 
    ERR_SETTER_INFO_METHOD_STATIC("Method {0} in class {1} is declared static, which is not supported in conjunction with the @LDAPSetter annotation."), 
    INFO_GEN_SCHEMA_ARG_DESCRIPTION_JAVA_CLASS("The fully-qualified name of the java class to use to generate the LDAP schema."), 
    INFO_GEN_SCHEMA_ARG_DESCRIPTION_MODIFY_FORMAT("Indicates that the resulting schema file should be in a format that can be used to update a server schema with a tool like ldapmodify."), 
    INFO_GEN_SCHEMA_ARG_DESCRIPTION_OUTPUT_FILE("The path and name of the LDIF file to create with the schema information."), 
    INFO_GEN_SCHEMA_EXAMPLE_1("Generate LDAP schema that can be used to store objects that are instances of class 'com.example.MyClass' and write those definitions in LDIF form to file 'my-schema.ldif'."), 
    INFO_GEN_SCHEMA_TOOL_DESCRIPTION("Generate LDAP schema that may be used to store objects from a properly-annotated class contained in the Java classpath.  The schema elements will be written to the file in LDIF form."), 
    INFO_GEN_SCHEMA_VALUE_PLACEHOLDER_CLASS("{class}"), 
    INFO_GEN_SCHEMA_VALUE_PLACEHOLDER_PATH("{path}"), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_AUXILIARY_CLASS("The name of the auxiliary object class to use for the object.  This is optional, and it may be provided multiple times to specify multiple auxiliary object classes.  Any values that are provided must be the name or OID of an auxiliary object class defined in the directory server schema."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_CLASS_NAME("The unqualified name of the class to use for the generated source file.  If this is not provided, then it will be generated from the name of the structural object class."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_DEFAULT_PARENT_DN("The default parent DN to use for the generated class.  Entries created from objects of that class will be placed below this parent DN unless an alternate parent DN is specified when requesting the operation."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_LAZY_ATTRIBUTE("The name of an LDAP attribute that should be lazily-loaded from the directory.  This may be provided more than once to request multiple lazily-loaded attributes."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_OPERATIONAL_ATTRIBUTE("The name of an LDAP operational attribute that should be made available as a field in the generated source file.  This may be provided more than once to request multiple operational attributes."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_OUTPUT_DIRECTORY("The path of the directory into which the generated source file should be written.  If this is not provided, then the source file will be created in the current working directory.  If a value is provided, then the specified path must exist and must be a directory."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_PACKAGE_NAME("The name of the package to use for the generated source file.  If this is not provided, then the class will be placed in the default (top-level) package."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_RDN_ATTRIBUTE("The name of an LDAP attribute to include in the RDN for entries created from the generated object.  This must be provided, and the value must be the name or OID of an attribute type that is referenced by one of the structural or auxiliary object classes."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_STRUCTURAL_CLASS("The name of the structural object class to use for the object.  This must be provided, and the value must be the name or OID of a structural object class defined in the directory server schema."), 
    INFO_GEN_SOURCE_ARG_DESCRIPTION_TERSE("Generate terse output with a minimal set of elements present in the generated classes."), 
    INFO_GEN_SOURCE_EXAMPLE_1("Generate a 'src/com/example/MyObject.java' source file from the information contained in the 'myStructuralClass' structural object class, as well as the 'auxClass1' and 'auxClass2' auxiliary classes.  Entries created from this object will use an RDN attribute of 'cn' and will be created below 'dc=example,dc=com' by default."), 
    INFO_GEN_SOURCE_TOOL_DESCRIPTION("Generate source code for a Java class that may be used to represent data stored in an LDAP directory server.  The source code will be generated using information read from the directory server schema, and will contain an appropriate set of annotations required to use that class with the LDAP SDK persistence framework."), 
    INFO_GEN_SOURCE_VALUE_PLACEHOLDER_DN("{dn}"), 
    INFO_GEN_SOURCE_VALUE_PLACEHOLDER_NAME("{name}"), 
    INFO_GEN_SOURCE_VALUE_PLACEHOLDER_PATH("{path}");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<PersistMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<PersistMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private PersistMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = PersistMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (PersistMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = PersistMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                PersistMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (PersistMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = PersistMessages.MESSAGES.get(this);
        if (f == null) {
            if (PersistMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(PersistMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            PersistMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (PersistMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = PersistMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (PersistMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = PersistMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                PersistMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-persist");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<PersistMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<PersistMessages, MessageFormat>(100);
    }
}
