package com.unboundid.util.args;

import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ResourceBundle;

enum ArgsMessages
{
    ERR_ARG_ALREADY_REGISTERED("Argument ''{0}'' is already registered with an argument parser and cannot be registered a second time or with a different parser."), 
    ERR_ARG_DEFAULT_VALUE_NOT_ALLOWED("Value ''{0}'' configured as a default value is not allowed for argument ''{1}''."), 
    ERR_ARG_DESCRIPTION_NULL("The provided description was null."), 
    ERR_ARG_ID_CHANGE_AFTER_REGISTERED("The set of identifiers for argument ''{0}'' cannot be altered because the argument has already been registered with an argument parser."), 
    ERR_ARG_LIST_INVALID_VALUE("The value ''{0}'' provided for argument ''{1}'' is not acceptable because it was rejected by the associated argument parser:  {2}"), 
    ERR_ARG_LIST_MALFORMED_VALUE("The value ''{0}'' provided for argument ''{1}'' is not a properly-formed argument list:  {2}"), 
    ERR_ARG_MAX_OCCURRENCES_EXCEEDED("The ''{0}'' argument was provided more than the maximum allowed number of times for that argument."), 
    ERR_ARG_MUST_TAKE_VALUE("A value placeholder must be provided for the ''{0}'' argument."), 
    ERR_ARG_NO_IDENTIFIERS("At least one of the short and long identifiers must be non-null."), 
    ERR_ARG_NO_NON_HIDDEN_IDENTIFIER("Argument {0} does not have any non-hidden long or short identifiers."), 
    ERR_ARG_VALUE_DOES_NOT_MATCH_PATTERN_WITHOUT_EXPLANATION("The provided value ''{0}'' is not allowed for argument ''{1}'' because it does not match regular expression ''{2}''."), 
    ERR_ARG_VALUE_DOES_NOT_MATCH_PATTERN_WITH_EXPLANATION("The provided value ''{0}'' is not allowed for argument ''{1}'' because it does not match regular expression ''{2}''.  {3}"), 
    ERR_ARG_VALUE_NOT_ALLOWED("The provided value ''{0}'' is not allowed for argument ''{1}''.  Allowed values include:  {2}."), 
    ERR_ATTR_NAME_VALIDATOR_INVALID_VALUE("Value ''{0}'' provided for argument ''{1}'' is not a valid attribute name:  {2}"), 
    ERR_ATTR_NAME_VALIDATOR_TYPE_NOT_DEFINED("Value ''{0}'' provided for argument ''{1}'' is not valid because attribute type ''{2}'' is not defined in the schema."), 
    ERR_BOOLEAN_VALUES_NOT_ALLOWED("The ''{0}'' argument does not take a value."), 
    ERR_CONTROL_ARG_INVALID_BASE64_VALUE("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a control definition because ''{2}'' is not a valid base64-encoded value."), 
    ERR_CONTROL_ARG_INVALID_CRITICALITY("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a control definition because ''{2}'' is not a valid criticality."), 
    ERR_CONTROL_ARG_INVALID_OID("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a control definition because ''{2}'' is not a valid numeric OID."), 
    ERR_DN_VALUE_NOT_DN("The provided value ''{0}'' for argument ''{1}'' could not be parsed as a distinguished name:  {2}"), 
    ERR_DURATION_ABOVE_UPPER_BOUND("The value for argument ''{0}'' is not acceptable because it represents a duration above the upper bound of {1}."), 
    ERR_DURATION_BELOW_LOWER_BOUND("The value for argument ''{0}'' is not acceptable because it represents a duration below the lower bound of {1}."), 
    ERR_DURATION_DEFAULT_REQUIRES_UNIT("If a default value is defined for duration argument ''{0}'', then a default value unit must also be specified."), 
    ERR_DURATION_EMPTY_VALUE("The value is an empty string."), 
    ERR_DURATION_LOWER_GT_UPPER("Argument ''{0}'' is invalid because the defined lower bound of {1} is greater than the defined upper bound of {2}."), 
    ERR_DURATION_LOWER_REQUIRES_UNIT("If a lower bound value is defined for duration argument ''{0}'', then a lower bound unit must also be specified."), 
    ERR_DURATION_MALFORMED_VALUE("Value ''{0}'' is not valid for argument ''{1}'':  {2}"), 
    ERR_DURATION_NO_DIGIT("The provided string did not include a numeric portion."), 
    ERR_DURATION_NO_UNIT("The provided string did not include a time unit."), 
    ERR_DURATION_UNRECOGNIZED_UNIT("''{0}'' is not a recognized time unit."), 
    ERR_DURATION_UNSUPPORTED_LOWER_BOUND_UNIT("Lower bound time unit ''{0}'' is not supported."), 
    ERR_DURATION_UNSUPPORTED_UPPER_BOUND_UNIT("Upper bound time unit ''{0}'' is not supported."), 
    ERR_DURATION_UPPER_REQUIRES_UNIT("If an upper bound value is defined for duration argument ''{0}'', then an upper bound unit must also be specified."), 
    ERR_FILE_CANNOT_BE_FILE_AND_DIRECTORY("File argument ''{0}'' is configured to require values to be both files and directories.  This is not allowed."), 
    ERR_FILE_CANNOT_READ_FULLY("Unable to fully read the contents of file ''{0}'' specified as the value for argument ''{1}''."), 
    ERR_FILE_DOESNT_EXIST("The file ''{0}'' specified as the value for argument ''{1}'' does not exist."), 
    ERR_FILE_PARENT_DOESNT_EXIST("The file ''{0}'' specified as the value for argument ''{1}'' does not exist, and its parent also does not exist or is not a directory."), 
    ERR_FILE_VALUE_NOT_DIRECTORY("The value for file argument ''{0}'' resolves to path ''{1}'' which exists but is not a directory."), 
    ERR_FILE_VALUE_NOT_FILE("The value for file argument ''{0}'' resolves to path ''{1}'' which exists but is not a file."), 
    ERR_FILTER_VALUE_NOT_FILTER("The provided value ''{0}'' for argument ''{1}'' could not be parsed as a search filter:  {2}"), 
    ERR_INTEGER_VALUE_ABOVE_UPPER_BOUND("The provided value {0,number,0} for argument ''{1}'' was larger than the upper bound of {2,number,0}."), 
    ERR_INTEGER_VALUE_BELOW_LOWER_BOUND("The provided value {0,number,0} for argument ''{1}'' was smaller than the lower bound of {2,number,0}."), 
    ERR_INTEGER_VALUE_NOT_INT("The provided value ''{0}'' for argument ''{1}'' could not be parsed as an integer."), 
    ERR_IP_VALIDATOR_ILLEGAL_IPV4_CHAR("Value ''{0}'' provided for argument ''{1}'' is not acceptable because it is suspected to be an IPv4 address but contains ''{0}'' that is not allowed to appear in IPv4 addresses."), 
    ERR_IP_VALIDATOR_ILLEGAL_IPV6_CHAR("Value ''{0}'' provided for argument ''{1}'' is not acceptable because it is suspected to be an IPv6 address but contains ''{0}'' that is not allowed to appear in IPv6 addresses."), 
    ERR_IP_VALIDATOR_IPV4_NOT_ACCEPTED("Value ''{0}'' provided for argument ''{1}'' appears to be an IPv4 address but only IPv6 addresses are accepted."), 
    ERR_IP_VALIDATOR_IPV6_NOT_ACCEPTED("Value ''{0}'' provided for argument ''{1}'' appears to be an IPv6 address but only IPv4 addresses are accepted."), 
    ERR_IP_VALIDATOR_MALFORMED("Value ''{0}'' provided for argument ''{1}'' is not a valid IP address."), 
    ERR_LDAP_URL_VALIDATOR_MISSING_ATTRIBUTES("LDAP URL ''{0}''provided for argument ''{1}'' does not include the required attributes element."), 
    ERR_LDAP_URL_VALIDATOR_MISSING_BASE_DN("LDAP URL ''{0}''provided for argument ''{1}'' does not include the required base DN element."), 
    ERR_LDAP_URL_VALIDATOR_MISSING_FILTER("LDAP URL ''{0}''provided for argument ''{1}'' does not include the required filter element."), 
    ERR_LDAP_URL_VALIDATOR_MISSING_HOST("LDAP URL ''{0}''provided for argument ''{1}'' does not include the required host element."), 
    ERR_LDAP_URL_VALIDATOR_MISSING_PORT("LDAP URL ''{0}''provided for argument ''{1}'' does not include the required port element."), 
    ERR_LDAP_URL_VALIDATOR_MISSING_SCOPE("LDAP URL ''{0}''provided for argument ''{1}'' does not include the required scope element."), 
    ERR_LDAP_URL_VALIDATOR_VALUE_NOT_LDAP_URL("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a valid LDAP URL:  {2}"), 
    ERR_OID_VALIDATOR_CONSECUTIVE_PERIODS("Value ''{0}'' provided for argument ''{1}'' is not valid because OIDs must not contain consecutive periods."), 
    ERR_OID_VALIDATOR_EMPTY("Value ''{0}'' provided for argument ''{1}'' is not a valid numeric OID because is empty."), 
    ERR_OID_VALIDATOR_ILLEGAL_CHARACTER("Value ''{0}'' provided for argument ''{1}'' is not valid because OIDs must not contain any characters other than digits and periods."), 
    ERR_OID_VALIDATOR_ILLEGAL_FIRST_COMPONENT("Value ''{0}'' provided for argument ''{1}'' is not valid because the first component of an OID must have a value of zero, one, or two."), 
    ERR_OID_VALIDATOR_ILLEGAL_SECOND_COMPONENT("Value ''{0}'' provided for argument ''{1}'' is not valid because if the value of the first component is zero or one, then the value of the second component must be less than forty."), 
    ERR_OID_VALIDATOR_NOT_ENOUGH_COMPONENTS("Value ''{0}'' provided for argument ''{1}'' is not valid because OIDs must contain at least two components."), 
    ERR_OID_VALIDATOR_STARTS_OR_ENDS_WITH_PERIOD("Value ''{0}'' provided for argument ''{1}'' is not valid because OIDs must not start or end with a period."), 
    ERR_PARSER_CANNOT_CREATE_NESTED_SUBCOMMAND("The subcommand argument parser (for subcommand ''{0}'') cannot have its own subcommands."), 
    ERR_PARSER_CANNOT_OPEN_PROP_FILE("Unable to open argument properties file {0}:  {1}"), 
    ERR_PARSER_COMMAND_DESCRIPTION_NULL("The provided command description was null."), 
    ERR_PARSER_COMMAND_NAME_NULL("The provided command name was null."), 
    ERR_PARSER_CONFLICTING_SUBCOMMANDS("Conflicting subcommands:  ''{0}'' and ''{1}''."), 
    ERR_PARSER_DEPENDENT_CONFLICT_MULTIPLE("If argument ''{0}'' is provided, then at least one of the following arguments must also be given:  {1}."), 
    ERR_PARSER_DEPENDENT_CONFLICT_SINGLE("If argument ''{0}'' is provided, then argument ''{1}'' must also be given."), 
    ERR_PARSER_ERROR_READING_PROP_FILE("An error occurred while reading from properties file {0}:  {1}"), 
    ERR_PARSER_EXCLUSIVE_CONFLICT("Arguments ''{0}'' and ''{1}'' are not allowed to be used together."), 
    ERR_PARSER_GEN_PROPS_CANNOT_OPEN_FILE("An error occurred while attempting to open file ''{0}'' for writing:  {1}"), 
    ERR_PARSER_LONG_ARG_DOESNT_TAKE_VALUE("Argument ''--{0}'' does not take a value."), 
    ERR_PARSER_LONG_ARG_MISSING_VALUE("Argument ''--{0}'' requires a value."), 
    ERR_PARSER_LONG_ID_CONFLICT("Another argument is already registered with a long identifier of ''{0}''."), 
    ERR_PARSER_LONG_ID_CONFLICT_WITH_SUBCOMMAND("An argument with a long identifier of ''{0}'' is already registered with subcommand ''{1}}'."), 
    ERR_PARSER_MALFORMED_PROP_LINE("Properties file {0} contains a malformed property definition at or near line {1} that does not include a space or an an equal sign to separate the property name from the value.  The malformed line is:  {2}"), 
    ERR_PARSER_MALFORMED_UNICODE_ESCAPE("Properties file {0} contains a property definition at or near line {1} with a malformed Unicode escape sequence that starts with a backslash followed by a letter u but that is not then followed by four hexadecimal digits."), 
    ERR_PARSER_MISSING_REQUIRED_ARG("Argument ''{0}'' is required to be present but was not provided and does not have a default value."), 
    ERR_PARSER_MISSING_SUBCOMMAND("Command ''{0}'' requires a subcommand, but none was provided."), 
    ERR_PARSER_NOT_ENOUGH_TRAILING_ARGS("Not enough unnamed trailing arguments were provided.  The ''{0}'' tool requires at least {1,number,0} trailing argument(s), with a usage of ''{2}''."), 
    ERR_PARSER_NO_SUBSEQUENT_SHORT_ARG("Unknown argument ''-{0}'' referenced in string ''{1}''."), 
    ERR_PARSER_NO_SUCH_LONG_ID("Unknown argument ''--{0}''"), 
    ERR_PARSER_NO_SUCH_PROPERTIES_FILE("The {0} argument was provided to indicate that argument values should be obtained from properties file {1}, but either that file does not exist or the path exists but does not refer to a file."), 
    ERR_PARSER_NO_SUCH_SHORT_ID("Unknown argument ''-{0}''"), 
    ERR_PARSER_NO_SUCH_SUBCOMMAND("Subcommand ''{0}'' is not supported for command ''{1}''."), 
    ERR_PARSER_PROP_FILE_MISSING_CONTINUATION("Missing the expected continuation of a property definition at or near line {0} in properties file {1}."), 
    ERR_PARSER_PROP_FILE_UNEXPECTED_LEADING_SPACE("Properties file {0} has an unexpected leading space at line {1,number,0}."), 
    ERR_PARSER_REQUIRED_CONFLICT("At least one of the following arguments is required to be present:  {0}."), 
    ERR_PARSER_SHORT_ARG_MISSING_VALUE("Argument ''-{0}'' requires a value."), 
    ERR_PARSER_SHORT_ID_CONFLICT("Another argument is already registered with a short identifier of ''{0}''."), 
    ERR_PARSER_SHORT_ID_CONFLICT_WITH_SUBCOMMAND("An argument with a short identifier of ''{0}'' is already registered with subcommand ''{1}}'."), 
    ERR_PARSER_SUBCOMMAND_ALREADY_REGISTERED_WITH_PARSER("The provided subcommand has already been registered with an argument parser."), 
    ERR_PARSER_SUBSEQUENT_SHORT_ARG_TAKES_VALUE("Argument ''-{0}'' referenced in string ''{1}'' requires a value, but arguments which take values cannot be referenced by their short identifier in a single string containing other arguments referenced by their short identifiers."), 
    ERR_PARSER_TOO_MANY_TRAILING_ARGS("Argument ''{0}'' is not acceptable because command ''{1}'' does not allow more than {2} unnamed trailing argument(s)."), 
    ERR_PARSER_TRAILING_ARGS_COUNT_MISMATCH("The argument parser was configured to require at least {0,number,0} unnamed trailing arguments, which conflicts with the configured maximum of {1,number,0} trailing arguments."), 
    ERR_PARSER_TRAILING_ARGS_NOT_ALLOWED("Argument ''{0}'' is not acceptable because command ''{1}'' does not allow unnamed trailing arguments."), 
    ERR_PARSER_TRAILING_ARGS_PLACEHOLDER_NULL("The argument parser was configured to allow unnamed trailing arguments, but the trailing args placeholder was null."), 
    ERR_PARSER_UNEXPECTED_DASH("Unexpected lone ''-'' character in argument list."), 
    ERR_PARSER_WITH_TRAILING_ARGS_CANNOT_HAVE_SUBCOMMANDS("An argument parser that allows unnamed trailing arguments cannot have subcommands."), 
    ERR_PARSER_WRONG_PROP_FILE_ENC_PW("The provided encryption passphrase is incorrect.  Please enter the correct passphrase used to encrypt properties file ''{0}'':"), 
    ERR_PROHIBIT_DN_IN_SUBTREE_VALIDATOR_VALUE_IN_SUBTREE("Value ''{0}'' provided for argument ''{1}'' is below prohibited subtree ''{2}''."), 
    ERR_PROHIBIT_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_DN("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a valid DN."), 
    ERR_REGEX_VALIDATOR_VALUE_NOT_REGEX("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a valid regular expression."), 
    ERR_REQUIRE_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_DN("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a valid DN."), 
    ERR_REQUIRE_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_IN_SUBTREE("Value ''{0}'' provided for argument ''{1}'' is not below the required subtree ''{2}''."), 
    ERR_REQUIRE_DN_IN_SUBTREE_VALIDATOR_VALUE_NOT_IN_SUBTREES("Value ''{0}'' provided for argument ''{1}'' is not below any of the following permitted subtrees:  {2}."), 
    ERR_SCOPE_VALUE_NOT_VALID("The value ''{0}'' provided for argument ''{1}'' is not acceptable because it does not represent a recognized search scope.  Search scope values should be one of 'base', 'one', 'sub', or 'subordinate'."), 
    ERR_SUBCOMMAND_DESCRIPTION_NULL_OR_EMPTY("The subcommand description must not be null or empty."), 
    ERR_SUBCOMMAND_NAME_ALREADY_IN_USE("Subcommand name ''{0}'' is already in use."), 
    ERR_SUBCOMMAND_NAME_NULL_OR_EMPTY("Subcommand names must not be null or empty."), 
    ERR_SUBCOMMAND_PARSER_ALLOWS_TRAILING_ARGS("The subcommand argument parser must not allow unnamed trailing arguments."), 
    ERR_SUBCOMMAND_PARSER_HAS_SUBCOMMANDS("The subcommand argument parser must not have subcommands."), 
    ERR_SUBCOMMAND_PARSER_NULL("The subcommand argument parser must not be null."), 
    ERR_TIMESTAMP_PARSE_ERROR("Unable to parse the provided value ''{0}'' as a timestamp using any of the supported formats."), 
    ERR_TIMESTAMP_RANGE_VALIDATOR_TOO_NEW("The provided value ''{0}'' for argument ''{1}'' is not acceptable because it is newer than the most recent allowed timestamp value of ''{2}''."), 
    ERR_TIMESTAMP_RANGE_VALIDATOR_TOO_OLD("The provided value ''{0}'' for argument ''{1}'' is not acceptable because it is earlier than the oldest allowed timestamp value of ''{2}''."), 
    ERR_TIMESTAMP_VALUE_NOT_TIMESTAMP("The provided value ''{0}'' for argument ''{1}'' could not be parsed as a valid timestamp in any of the supported formats."), 
    ERR_URL_VALIDATOR_MISSING_SCHEME("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a valid URL because it does not contain a scheme."), 
    ERR_URL_VALIDATOR_UNACCEPTABLE_SCHEME("Value ''{0}'' provided for argument ''{1}'' is not acceptable because ''{2}'' is not a supported scheme."), 
    ERR_URL_VALIDATOR_VALUE_NOT_URL("Value ''{0}'' provided for argument ''{1}'' cannot be parsed as a valid URL:  {2}"), 
    INFO_ARG_DESCRIPTION_GEN_PROP_FILE("Write an empty properties file that may be used to specify default values for arguments."), 
    INFO_ARG_DESCRIPTION_NO_PROP_FILE("Do not obtain any argument values from a properties file."), 
    INFO_ARG_DESCRIPTION_PROP_FILE_PATH("The path to a properties file used to specify default values for arguments not supplied on the command line."), 
    INFO_ARG_DESCRIPTION_SUPPRESS_PROP_FILE_COMMENT("Suppress output listing the arguments obtained from a properties file."), 
    INFO_ARG_LIST_CONSTRAINTS("A provided value must be a string representation of a valid argument list that meets the constraints of the associated argument parser."), 
    INFO_ARG_LIST_TYPE_NAME("Argument List"), 
    INFO_BOOLEAN_CONSTRAINTS("This argument is not allowed to have a value.  If this argument is included in a set of arguments, then it will be assumed to have a value of 'true'.  If it is absent from a set of arguments, then it will be assumed to have a value of 'false'."), 
    INFO_BOOLEAN_TYPE_NAME("Boolean"), 
    INFO_BOOLEAN_VALUE_CONSTRAINTS("A provided value should be either 'true' or 'false'."), 
    INFO_BOOLEAN_VALUE_TYPE_NAME("Boolean"), 
    INFO_CONTROL_CONSTRAINTS("A provided value must be a string representation of a valid LDAP control in the form {oid}[:{criticality}[:{stringValue}|::{base64Value}]]."), 
    INFO_CONTROL_TYPE_NAME("Control"), 
    INFO_DN_CONSTRAINTS("A provided value must be able to be parsed as an LDAP distinguished name as described in RFC 4514."), 
    INFO_DN_TYPE_NAME("LDAP Distinguished Name"), 
    INFO_DURATION_CONSTRAINTS_FORMAT("The provided value must contain an integer followed by a unit of 'ns' (for nanoseconds), 'us' (for microseconds), 'ms' (for milliseconds), 's' (for seconds), 'm' (for minutes), 'h' (for hours), 'd' (for days), or 'w' (for weeks)."), 
    INFO_DURATION_CONSTRAINTS_LOWER_AND_UPPER_BOUND("The specified duration must not be less than {0} or greater than {1}."), 
    INFO_DURATION_CONSTRAINTS_LOWER_BOUND("The specified duration must not be less than {0}."), 
    INFO_DURATION_CONSTRAINTS_UPPER_BOUND("The specified duration must not be greater than {0}."), 
    INFO_DURATION_TYPE_NAME("Duration"), 
    INFO_FILE_CONSTRAINTS_DIR_MAY_EXIST("The specified path must refer to a directory that may or may not exist."), 
    INFO_FILE_CONSTRAINTS_DIR_MUST_EXIST("The specified path must refer to a directory that exists."), 
    INFO_FILE_CONSTRAINTS_DIR_PARENT_MUST_EXIST("The specified path must refer to a directory which may or may not exist, but whose parent directory must exist."), 
    INFO_FILE_CONSTRAINTS_FILE_MAY_EXIST("The specified path must refer to a file that may or may not exist."), 
    INFO_FILE_CONSTRAINTS_FILE_MUST_EXIST("The specified path must refer to a file that exists."), 
    INFO_FILE_CONSTRAINTS_FILE_PARENT_MUST_EXIST("The specified path must refer to a file which may or may not exist, but whose parent directory must exist."), 
    INFO_FILE_CONSTRAINTS_RELATIVE_PATH_SPECIFIED_ROOT("Non-absolute paths will be relative to directory ''{0}''."), 
    INFO_FILE_TYPE_PATH_DIRECTORY("Directory Path"), 
    INFO_FILE_TYPE_PATH_FILE("File Path"), 
    INFO_FILTER_CONSTRAINTS("A provided value must be able to be parsed as an LDAP search filter as described in RFC 4515."), 
    INFO_FILTER_TYPE_NAME("LDAP Search Filter"), 
    INFO_INTEGER_CONSTRAINTS_LOWER_AND_UPPER_BOUND("The specified value must not be less than {0} or greater than {1}."), 
    INFO_INTEGER_TYPE_NAME("Integer"), 
    INFO_PARSER_GEN_PROPS_HEADER_1("This file may be used to specify default values to use for {0} arguments that were not provided by the command line.  Note that although it was generated for the {0} tool, it may contain default values for any number of other tools that use the same arguments.  This allows a single properties file to be used in conjunction with multiple tools.  The should be encoded using the ISO 8859-1 character set.  Any characters that cannot be directly represented in that character set may be encoded as a backslash followed by a lowercase letter u and the four hexadecimal digits that make up the Unicode representation of that character (for example, \u0000 for the Unicode null character)."), 
    INFO_PARSER_GEN_PROPS_HEADER_2("When invoking an applicable tool like {0}, you may indicate that this properties file should be used by launching the tool with the {1} argument and specifying the path to this file.  Alternately, the {2} Java property or the {3} environment variable may be set to the path to the properties file that should be used.  The {4} argument may be used to indicate that the tool should not use any properties file and that it should only use the argument values provided on the command line."), 
    INFO_PARSER_GEN_PROPS_HEADER_3("Each property listed in this file should be a name-value pair in which the name and the value are separated by an equal sign.  The name of the property should be the name of the argument for which you wish to specify a default value.  It may either be the argument name on its own (which will be used for any tool that offers that argument), or it may consist of the tool name, a period, and the argument name (which will only be used for the specified tool).  For example, a property name of 'toolName.argName' will only be used to specify the value for the argName argument for the toolName tool, while a property name of 'argName' will be used to specify the value for any tool that supports the argName argument.  Similarly, if the tool supports subcommands, then the property name may consist of the tool name, a period, the subcommand name, a period, and the argument name (e.g., 'toolName.subCommandName.argName'), and the property will only be used in conjunction with the specified subcommand.  A property that includes a subcommand name will take precedence over a property that does not include a subcommand name, and a property that includes a tool name will take precedence over a property that does not include a tool name."), 
    INFO_PARSER_GEN_PROPS_HEADER_4("If an argument supports multiple values, then the same property may appear multiple times with each of the desired default values.  The values will be used in the order that they appear in the properties file."), 
    INFO_PARSER_GEN_PROPS_HEADER_5("The following properties are supported for use with the ''{0}'' tool:"), 
    INFO_PARSER_PROMPT_FOR_PROP_FILE_ENC_PW("Properties file ''{0}'' is encrypted.  Please enter the passphrase used to encrypt it:"), 
    INFO_PLACEHOLDER_ARGS("{args}"), 
    INFO_PLACEHOLDER_CONTROL("{oid}[:{criticality}[:{stringValue}|::{base64Value}]]"), 
    INFO_PLACEHOLDER_DN("{dn}"), 
    INFO_PLACEHOLDER_DURATION("{duration}"), 
    INFO_PLACEHOLDER_FILTER("{filter}"), 
    INFO_PLACEHOLDER_PATH("{path}"), 
    INFO_PLACEHOLDER_SCOPE("{base|one|sub|subordinates}"), 
    INFO_PLACEHOLDER_TIMESTAMP("{timestamp}"), 
    INFO_PLACEHOLDER_TRUE_FALSE("{true|false}"), 
    INFO_PLACEHOLDER_VALUE("{value}"), 
    INFO_SCOPE_CONSTRAINTS("The provided value should be one of 'base', 'one', 'sub', or 'subordinate'."), 
    INFO_SCOPE_TYPE_NAME("LDAP Search Scope"), 
    INFO_STRING_CONSTRAINTS_ALLOWED_VALUE("A provided value should be one of the following:"), 
    INFO_STRING_CONSTRAINTS_REGEX_WITHOUT_EXPLANATION("A provided value must match regular expression ''{0}''."), 
    INFO_STRING_CONSTRAINTS_REGEX_WITH_EXPLANATION("A provided value must match regular expression ''{0}'' ({1})."), 
    INFO_STRING_TYPE_NAME("String"), 
    INFO_SUBCOMMAND_USAGE_NOOPTIONS("Usage:  {0} {1}"), 
    INFO_SUBCOMMAND_USAGE_OPTIONS("Usage:  {0} {1} '{'options'}'"), 
    INFO_TIMESTAMP_CONSTRAINTS("A provided value must be able to be parsed as a valid generalized time (as described in RFC 4517 section 3.1.13) or as a timestamp in the local time zone using any of the formats 'YYYYMMDDhhmmss.uuu', 'YYYYMMDDhhmmss', or 'YYYYMMDDhhmm'."), 
    INFO_TIMESTAMP_TYPE_NAME("Timestamp"), 
    INFO_USAGE_ARG_IS_REQUIRED("Indicates the argument is required"), 
    INFO_USAGE_NOOPTIONS_NOTRAILING("Usage:  {0}"), 
    INFO_USAGE_NOOPTIONS_TRAILING("Usage:  {0} {1}"), 
    INFO_USAGE_OPTIONS_INCLUDE("Available options include:"), 
    INFO_USAGE_OPTIONS_NOTRAILING("Usage:  {0} '{'options'}'"), 
    INFO_USAGE_OPTIONS_TRAILING("Usage:  {0} '{'options'}' {1}"), 
    INFO_USAGE_SUBCOMMANDS_HEADER("Subcommands:"), 
    INFO_USAGE_SUBCOMMAND_USAGE("Usage:  {0} '{'subcommand'}' '{'options'}'"), 
    INFO_USAGE_UNGROUPED_ARGS("Other Arguments"), 
    INFO_USAGE_USAGE_ARGS("Usage Arguments");
    
    private static final boolean IS_WITHIN_UNIT_TESTS;
    private static final ResourceBundle RESOURCE_BUNDLE;
    private static final ConcurrentHashMap<ArgsMessages, String> MESSAGE_STRINGS;
    private static final ConcurrentHashMap<ArgsMessages, MessageFormat> MESSAGES;
    private final String defaultText;
    
    private ArgsMessages(final String defaultText) {
        this.defaultText = defaultText;
    }
    
    public String get() {
        String s = ArgsMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ArgsMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ArgsMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ArgsMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        if (ArgsMessages.IS_WITHIN_UNIT_TESTS && (s.contains("{0}") || s.contains("{0,number,0}") || s.contains("{1}") || s.contains("{1,number,0}") || s.contains("{2}") || s.contains("{2,number,0}") || s.contains("{3}") || s.contains("{3,number,0}") || s.contains("{4}") || s.contains("{4,number,0}") || s.contains("{5}") || s.contains("{5,number,0}") || s.contains("{6}") || s.contains("{6,number,0}") || s.contains("{7}") || s.contains("{7,number,0}") || s.contains("{8}") || s.contains("{8,number,0}") || s.contains("{9}") || s.contains("{9,number,0}") || s.contains("{10}") || s.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + s);
        }
        return s;
    }
    
    public String get(final Object... args) {
        MessageFormat f = ArgsMessages.MESSAGES.get(this);
        if (f == null) {
            if (ArgsMessages.RESOURCE_BUNDLE == null) {
                f = new MessageFormat(this.defaultText);
            }
            else {
                try {
                    f = new MessageFormat(ArgsMessages.RESOURCE_BUNDLE.getString(this.name()));
                }
                catch (final Exception e) {
                    f = new MessageFormat(this.defaultText);
                }
            }
            ArgsMessages.MESSAGES.putIfAbsent(this, f);
        }
        final String formattedMessage;
        synchronized (f) {
            formattedMessage = f.format(args);
        }
        if (ArgsMessages.IS_WITHIN_UNIT_TESTS && (formattedMessage.contains("{0}") || formattedMessage.contains("{0,number,0}") || formattedMessage.contains("{1}") || formattedMessage.contains("{1,number,0}") || formattedMessage.contains("{2}") || formattedMessage.contains("{2,number,0}") || formattedMessage.contains("{3}") || formattedMessage.contains("{3,number,0}") || formattedMessage.contains("{4}") || formattedMessage.contains("{4,number,0}") || formattedMessage.contains("{5}") || formattedMessage.contains("{5,number,0}") || formattedMessage.contains("{6}") || formattedMessage.contains("{6,number,0}") || formattedMessage.contains("{7}") || formattedMessage.contains("{7,number,0}") || formattedMessage.contains("{8}") || formattedMessage.contains("{8,number,0}") || formattedMessage.contains("{9}") || formattedMessage.contains("{9,number,0}") || formattedMessage.contains("{10}") || formattedMessage.contains("{10,number,0}"))) {
            throw new IllegalArgumentException("Message " + this.getClass().getName() + '.' + this.name() + " contains an un-replaced token:  " + formattedMessage);
        }
        return f.format(args);
    }
    
    @Override
    public String toString() {
        String s = ArgsMessages.MESSAGE_STRINGS.get(this);
        if (s == null) {
            if (ArgsMessages.RESOURCE_BUNDLE == null) {
                s = this.defaultText;
            }
            else {
                try {
                    s = ArgsMessages.RESOURCE_BUNDLE.getString(this.name());
                }
                catch (final Exception e) {
                    s = this.defaultText;
                }
                ArgsMessages.MESSAGE_STRINGS.putIfAbsent(this, s);
            }
        }
        return s;
    }
    
    static {
        IS_WITHIN_UNIT_TESTS = (Boolean.getBoolean("com.unboundid.ldap.sdk.RunningUnitTests") || Boolean.getBoolean("com.unboundid.directory.server.RunningUnitTests"));
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle("unboundid-ldapsdk-args");
        }
        catch (final Exception ex) {}
        RESOURCE_BUNDLE = rb;
        MESSAGE_STRINGS = new ConcurrentHashMap<ArgsMessages, String>(100);
        MESSAGES = new ConcurrentHashMap<ArgsMessages, MessageFormat>(100);
    }
}
