package org.eclipse.jdt.internal.compiler.lookup;

public interface TypeConstants
{
    public static final char[] JAVA = "java".toCharArray();
    public static final char[] JAVAX = "javax".toCharArray();
    public static final char[] LANG = "lang".toCharArray();
    public static final char[] IO = "io".toCharArray();
    public static final char[] UTIL = "util".toCharArray();
    public static final char[] ZIP = "zip".toCharArray();
    public static final char[] ANNOTATION = "annotation".toCharArray();
    public static final char[] REFLECT = "reflect".toCharArray();
    public static final char[] LENGTH = "length".toCharArray();
    public static final char[] CLONE = "clone".toCharArray();
    public static final char[] EQUALS = "equals".toCharArray();
    public static final char[] GETCLASS = "getClass".toCharArray();
    public static final char[] HASHCODE = "hashCode".toCharArray();
    public static final char[] OBJECT = "Object".toCharArray();
    public static final char[] MAIN = "main".toCharArray();
    public static final char[] SERIALVERSIONUID = "serialVersionUID".toCharArray();
    public static final char[] SERIALPERSISTENTFIELDS = "serialPersistentFields".toCharArray();
    public static final char[] READRESOLVE = "readResolve".toCharArray();
    public static final char[] WRITEREPLACE = "writeReplace".toCharArray();
    public static final char[] READOBJECT = "readObject".toCharArray();
    public static final char[] WRITEOBJECT = "writeObject".toCharArray();
    public static final char[] CharArray_JAVA_LANG_OBJECT = "java.lang.Object".toCharArray();
    public static final char[] CharArray_JAVA_LANG_ENUM = "java.lang.Enum".toCharArray();
    public static final char[] CharArray_JAVA_LANG_ANNOTATION_ANNOTATION = "java.lang.annotation.Annotation".toCharArray();
    public static final char[] CharArray_JAVA_IO_OBJECTINPUTSTREAM = "java.io.ObjectInputStream".toCharArray();
    public static final char[] CharArray_JAVA_IO_OBJECTOUTPUTSTREAM = "java.io.ObjectOutputStream".toCharArray();
    public static final char[] CharArray_JAVA_IO_OBJECTSTREAMFIELD = "java.io.ObjectStreamField".toCharArray();
    public static final char[] ANONYM_PREFIX = "new ".toCharArray();
    public static final char[] ANONYM_SUFFIX = "(){}".toCharArray();
    public static final char[] WILDCARD_NAME = { '?' };
    public static final char[] WILDCARD_SUPER = " super ".toCharArray();
    public static final char[] WILDCARD_EXTENDS = " extends ".toCharArray();
    public static final char[] WILDCARD_MINUS = { '-' };
    public static final char[] WILDCARD_STAR = { '*' };
    public static final char[] WILDCARD_PLUS = { '+' };
    public static final char[] WILDCARD_CAPTURE_NAME_PREFIX = "capture#".toCharArray();
    public static final char[] WILDCARD_CAPTURE_NAME_SUFFIX = "-of ".toCharArray();
    public static final char[] WILDCARD_CAPTURE_SIGNABLE_NAME_SUFFIX = "capture-of ".toCharArray();
    public static final char[] WILDCARD_CAPTURE = { '!' };
    public static final char[] CAPTURE18 = { '^' };
    public static final char[] BYTE = "byte".toCharArray();
    public static final char[] SHORT = "short".toCharArray();
    public static final char[] INT = "int".toCharArray();
    public static final char[] LONG = "long".toCharArray();
    public static final char[] FLOAT = "float".toCharArray();
    public static final char[] DOUBLE = "double".toCharArray();
    public static final char[] CHAR = "char".toCharArray();
    public static final char[] BOOLEAN = "boolean".toCharArray();
    public static final char[] NULL = "null".toCharArray();
    public static final char[] VOID = "void".toCharArray();
    public static final char[] VALUE = "value".toCharArray();
    public static final char[] VALUES = "values".toCharArray();
    public static final char[] VALUEOF = "valueOf".toCharArray();
    public static final char[] UPPER_SOURCE = "SOURCE".toCharArray();
    public static final char[] UPPER_CLASS = "CLASS".toCharArray();
    public static final char[] UPPER_RUNTIME = "RUNTIME".toCharArray();
    public static final char[] ANNOTATION_PREFIX = "@".toCharArray();
    public static final char[] ANNOTATION_SUFFIX = "()".toCharArray();
    public static final char[] TYPE = "TYPE".toCharArray();
    public static final char[] UPPER_FIELD = "FIELD".toCharArray();
    public static final char[] UPPER_METHOD = "METHOD".toCharArray();
    public static final char[] UPPER_PARAMETER = "PARAMETER".toCharArray();
    public static final char[] UPPER_CONSTRUCTOR = "CONSTRUCTOR".toCharArray();
    public static final char[] UPPER_LOCAL_VARIABLE = "LOCAL_VARIABLE".toCharArray();
    public static final char[] UPPER_ANNOTATION_TYPE = "ANNOTATION_TYPE".toCharArray();
    public static final char[] UPPER_PACKAGE = "PACKAGE".toCharArray();
    public static final char[] ANONYMOUS_METHOD = "lambda$".toCharArray();
    public static final char[] DESERIALIZE_LAMBDA = "$deserializeLambda$".toCharArray();
    public static final char[] LAMBDA_TYPE = "<lambda>".toCharArray();
    public static final char[] TYPE_USE_TARGET = "TYPE_USE".toCharArray();
    public static final char[] TYPE_PARAMETER_TARGET = "TYPE_PARAMETER".toCharArray();
    public static final char[] ORG = "org".toCharArray();
    public static final char[] ECLIPSE = "eclipse".toCharArray();
    public static final char[] CORE = "core".toCharArray();
    public static final char[] RUNTIME = "runtime".toCharArray();
    public static final char[] APACHE = "apache".toCharArray();
    public static final char[] COMMONS = "commons".toCharArray();
    public static final char[] LANG3 = "lang3".toCharArray();
    public static final char[] COM = "com".toCharArray();
    public static final char[] GOOGLE = "google".toCharArray();
    public static final char[] JDT = "jdt".toCharArray();
    public static final char[] INTERNAL = "internal".toCharArray();
    public static final char[] COMPILER = "compiler".toCharArray();
    public static final char[] LOOKUP = "lookup".toCharArray();
    public static final char[] TYPEBINDING = "TypeBinding".toCharArray();
    public static final char[] DOM = "dom".toCharArray();
    public static final char[] ITYPEBINDING = "ITypeBinding".toCharArray();
    public static final char[] SPRING = "springframework".toCharArray();
    public static final char[][] JAVA_LANG = { TypeConstants.JAVA, TypeConstants.LANG };
    public static final char[][] JAVA_IO = { TypeConstants.JAVA, TypeConstants.IO };
    public static final char[][] JAVA_LANG_ANNOTATION = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION };
    public static final char[][] JAVA_LANG_ANNOTATION_ANNOTATION = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "Annotation".toCharArray() };
    public static final char[][] JAVA_LANG_ASSERTIONERROR = { TypeConstants.JAVA, TypeConstants.LANG, "AssertionError".toCharArray() };
    public static final char[][] JAVA_LANG_CLASS = { TypeConstants.JAVA, TypeConstants.LANG, "Class".toCharArray() };
    public static final char[][] JAVA_LANG_CLASSNOTFOUNDEXCEPTION = { TypeConstants.JAVA, TypeConstants.LANG, "ClassNotFoundException".toCharArray() };
    public static final char[][] JAVA_LANG_CLONEABLE = { TypeConstants.JAVA, TypeConstants.LANG, "Cloneable".toCharArray() };
    public static final char[][] JAVA_LANG_ENUM = { TypeConstants.JAVA, TypeConstants.LANG, "Enum".toCharArray() };
    public static final char[][] JAVA_LANG_EXCEPTION = { TypeConstants.JAVA, TypeConstants.LANG, "Exception".toCharArray() };
    public static final char[][] JAVA_LANG_ERROR = { TypeConstants.JAVA, TypeConstants.LANG, "Error".toCharArray() };
    public static final char[][] JAVA_LANG_ILLEGALARGUMENTEXCEPTION = { TypeConstants.JAVA, TypeConstants.LANG, "IllegalArgumentException".toCharArray() };
    public static final char[][] JAVA_LANG_ITERABLE = { TypeConstants.JAVA, TypeConstants.LANG, "Iterable".toCharArray() };
    public static final char[][] JAVA_LANG_NOCLASSDEFERROR = { TypeConstants.JAVA, TypeConstants.LANG, "NoClassDefError".toCharArray() };
    public static final char[][] JAVA_LANG_OBJECT = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.OBJECT };
    public static final char[][] JAVA_LANG_STRING = { TypeConstants.JAVA, TypeConstants.LANG, "String".toCharArray() };
    public static final char[][] JAVA_LANG_STRINGBUFFER = { TypeConstants.JAVA, TypeConstants.LANG, "StringBuffer".toCharArray() };
    public static final char[][] JAVA_LANG_STRINGBUILDER = { TypeConstants.JAVA, TypeConstants.LANG, "StringBuilder".toCharArray() };
    public static final char[][] JAVA_LANG_SYSTEM = { TypeConstants.JAVA, TypeConstants.LANG, "System".toCharArray() };
    public static final char[][] JAVA_LANG_RUNTIMEEXCEPTION = { TypeConstants.JAVA, TypeConstants.LANG, "RuntimeException".toCharArray() };
    public static final char[][] JAVA_LANG_THROWABLE = { TypeConstants.JAVA, TypeConstants.LANG, "Throwable".toCharArray() };
    public static final char[][] JAVA_LANG_REFLECT_CONSTRUCTOR = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "Constructor".toCharArray() };
    public static final char[][] JAVA_IO_PRINTSTREAM = { TypeConstants.JAVA, TypeConstants.IO, "PrintStream".toCharArray() };
    public static final char[][] JAVA_IO_SERIALIZABLE = { TypeConstants.JAVA, TypeConstants.IO, "Serializable".toCharArray() };
    public static final char[][] JAVA_LANG_BYTE = { TypeConstants.JAVA, TypeConstants.LANG, "Byte".toCharArray() };
    public static final char[][] JAVA_LANG_SHORT = { TypeConstants.JAVA, TypeConstants.LANG, "Short".toCharArray() };
    public static final char[][] JAVA_LANG_CHARACTER = { TypeConstants.JAVA, TypeConstants.LANG, "Character".toCharArray() };
    public static final char[][] JAVA_LANG_INTEGER = { TypeConstants.JAVA, TypeConstants.LANG, "Integer".toCharArray() };
    public static final char[][] JAVA_LANG_LONG = { TypeConstants.JAVA, TypeConstants.LANG, "Long".toCharArray() };
    public static final char[][] JAVA_LANG_FLOAT = { TypeConstants.JAVA, TypeConstants.LANG, "Float".toCharArray() };
    public static final char[][] JAVA_LANG_DOUBLE = { TypeConstants.JAVA, TypeConstants.LANG, "Double".toCharArray() };
    public static final char[][] JAVA_LANG_BOOLEAN = { TypeConstants.JAVA, TypeConstants.LANG, "Boolean".toCharArray() };
    public static final char[][] JAVA_LANG_VOID = { TypeConstants.JAVA, TypeConstants.LANG, "Void".toCharArray() };
    public static final char[][] JAVA_UTIL_COLLECTION = { TypeConstants.JAVA, TypeConstants.UTIL, "Collection".toCharArray() };
    public static final char[][] JAVA_UTIL_ITERATOR = { TypeConstants.JAVA, TypeConstants.UTIL, "Iterator".toCharArray() };
    public static final char[][] JAVA_UTIL_OBJECTS = { TypeConstants.JAVA, TypeConstants.UTIL, "Objects".toCharArray() };
    public static final char[][] JAVA_LANG_DEPRECATED = { TypeConstants.JAVA, TypeConstants.LANG, "Deprecated".toCharArray() };
    public static final char[][] JAVA_LANG_ANNOTATION_DOCUMENTED = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "Documented".toCharArray() };
    public static final char[][] JAVA_LANG_ANNOTATION_INHERITED = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "Inherited".toCharArray() };
    public static final char[][] JAVA_LANG_ANNOTATION_REPEATABLE = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "Repeatable".toCharArray() };
    public static final char[][] JAVA_LANG_OVERRIDE = { TypeConstants.JAVA, TypeConstants.LANG, "Override".toCharArray() };
    public static final char[][] JAVA_LANG_FUNCTIONAL_INTERFACE = { TypeConstants.JAVA, TypeConstants.LANG, "FunctionalInterface".toCharArray() };
    public static final char[][] JAVA_LANG_ANNOTATION_RETENTION = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "Retention".toCharArray() };
    public static final char[][] JAVA_LANG_SUPPRESSWARNINGS = { TypeConstants.JAVA, TypeConstants.LANG, "SuppressWarnings".toCharArray() };
    public static final char[][] JAVA_LANG_ANNOTATION_TARGET = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "Target".toCharArray() };
    public static final char[][] JAVA_LANG_ANNOTATION_RETENTIONPOLICY = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "RetentionPolicy".toCharArray() };
    public static final char[][] JAVA_LANG_ANNOTATION_ELEMENTTYPE = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.ANNOTATION, "ElementType".toCharArray() };
    public static final char[][] JAVA_LANG_REFLECT_FIELD = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "Field".toCharArray() };
    public static final char[][] JAVA_LANG_REFLECT_METHOD = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.REFLECT, "Method".toCharArray() };
    public static final char[][] JAVA_IO_CLOSEABLE = { TypeConstants.JAVA, TypeConstants.IO, "Closeable".toCharArray() };
    public static final char[][] JAVA_IO_OBJECTSTREAMEXCEPTION = { TypeConstants.JAVA, TypeConstants.IO, "ObjectStreamException".toCharArray() };
    public static final char[][] JAVA_IO_EXTERNALIZABLE = { TypeConstants.JAVA, TypeConstants.IO, "Externalizable".toCharArray() };
    public static final char[][] JAVA_IO_IOEXCEPTION = { TypeConstants.JAVA, TypeConstants.IO, "IOException".toCharArray() };
    public static final char[][] JAVA_IO_OBJECTOUTPUTSTREAM = { TypeConstants.JAVA, TypeConstants.IO, "ObjectOutputStream".toCharArray() };
    public static final char[][] JAVA_IO_OBJECTINPUTSTREAM = { TypeConstants.JAVA, TypeConstants.IO, "ObjectInputStream".toCharArray() };
    public static final char[][] JAVA_NIO_FILE_FILES = { TypeConstants.JAVA, "nio".toCharArray(), "file".toCharArray(), "Files".toCharArray() };
    public static final char[][] JAVAX_RMI_CORBA_STUB = { TypeConstants.JAVAX, "rmi".toCharArray(), "CORBA".toCharArray(), "Stub".toCharArray() };
    public static final char[][] JAVA_LANG_SAFEVARARGS = { TypeConstants.JAVA, TypeConstants.LANG, "SafeVarargs".toCharArray() };
    public static final char[] INVOKE = "invoke".toCharArray();
    public static final char[][] JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.INVOKE, "MethodHandle".toCharArray(), "PolymorphicSignature".toCharArray() };
    public static final char[][] JAVA_LANG_INVOKE_METHODHANDLE_$_POLYMORPHICSIGNATURE = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.INVOKE, "MethodHandle$PolymorphicSignature".toCharArray() };
    public static final char[][] JAVA_LANG_INVOKE_LAMBDAMETAFACTORY = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.INVOKE, "LambdaMetafactory".toCharArray() };
    public static final char[][] JAVA_LANG_INVOKE_SERIALIZEDLAMBDA = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.INVOKE, "SerializedLambda".toCharArray() };
    public static final char[][] JAVA_LANG_INVOKE_METHODHANDLES = { TypeConstants.JAVA, TypeConstants.LANG, TypeConstants.INVOKE, "MethodHandles".toCharArray() };
    public static final char[][] JAVA_LANG_AUTOCLOSEABLE = { TypeConstants.JAVA, TypeConstants.LANG, "AutoCloseable".toCharArray() };
    public static final char[] CLOSE = "close".toCharArray();
    public static final char[][] GUAVA_CLOSEABLES = { TypeConstants.COM, TypeConstants.GOOGLE, "common".toCharArray(), TypeConstants.IO, "Closeables".toCharArray() };
    public static final char[][] APACHE_IOUTILS = { TypeConstants.ORG, TypeConstants.APACHE, TypeConstants.COMMONS, TypeConstants.IO, "IOUtils".toCharArray() };
    public static final char[][] APACHE_DBUTILS = { TypeConstants.ORG, TypeConstants.APACHE, TypeConstants.COMMONS, "dbutils".toCharArray(), "DbUtils".toCharArray() };
    public static final char[] CLOSE_QUIETLY = "closeQuietly".toCharArray();
    public static final CloseMethodRecord[] closeMethods = { new CloseMethodRecord(TypeConstants.GUAVA_CLOSEABLES, TypeConstants.CLOSE_QUIETLY, 1), new CloseMethodRecord(TypeConstants.GUAVA_CLOSEABLES, TypeConstants.CLOSE, 1), new CloseMethodRecord(TypeConstants.APACHE_IOUTILS, TypeConstants.CLOSE_QUIETLY, 1), new CloseMethodRecord(TypeConstants.APACHE_DBUTILS, TypeConstants.CLOSE, 1), new CloseMethodRecord(TypeConstants.APACHE_DBUTILS, TypeConstants.CLOSE_QUIETLY, 3), new CloseMethodRecord(TypeConstants.APACHE_DBUTILS, "commitAndClose".toCharArray(), 1), new CloseMethodRecord(TypeConstants.APACHE_DBUTILS, "commitAndCloseQuietly".toCharArray(), 1), new CloseMethodRecord(TypeConstants.APACHE_DBUTILS, "rollbackAndClose".toCharArray(), 1), new CloseMethodRecord(TypeConstants.APACHE_DBUTILS, "rollbackAndCloseQuietly".toCharArray(), 1) };
    public static final char[][] JAVA_IO_WRAPPER_CLOSEABLES = { "BufferedInputStream".toCharArray(), "BufferedOutputStream".toCharArray(), "BufferedReader".toCharArray(), "BufferedWriter".toCharArray(), "InputStreamReader".toCharArray(), "PrintWriter".toCharArray(), "LineNumberReader".toCharArray(), "DataInputStream".toCharArray(), "DataOutputStream".toCharArray(), "ObjectInputStream".toCharArray(), "ObjectOutputStream".toCharArray(), "FilterInputStream".toCharArray(), "FilterOutputStream".toCharArray(), "DataInputStream".toCharArray(), "DataOutputStream".toCharArray(), "PushbackInputStream".toCharArray(), "SequenceInputStream".toCharArray(), "PrintStream".toCharArray(), "PushbackReader".toCharArray(), "OutputStreamWriter".toCharArray() };
    public static final char[][] JAVA_UTIL_ZIP_WRAPPER_CLOSEABLES = { "GZIPInputStream".toCharArray(), "InflaterInputStream".toCharArray(), "DeflaterInputStream".toCharArray(), "CheckedInputStream".toCharArray(), "ZipInputStream".toCharArray(), "JarInputStream".toCharArray(), "GZIPOutputStream".toCharArray(), "InflaterOutputStream".toCharArray(), "DeflaterOutputStream".toCharArray(), "CheckedOutputStream".toCharArray(), "ZipOutputStream".toCharArray(), "JarOutputStream".toCharArray() };
    public static final char[][][] OTHER_WRAPPER_CLOSEABLES = { { TypeConstants.JAVA, "security".toCharArray(), "DigestInputStream".toCharArray() }, { TypeConstants.JAVA, "security".toCharArray(), "DigestOutputStream".toCharArray() }, { TypeConstants.JAVA, "beans".toCharArray(), "XMLEncoder".toCharArray() }, { TypeConstants.JAVA, "beans".toCharArray(), "XMLDecoder".toCharArray() }, { TypeConstants.JAVAX, "sound".toCharArray(), "sampled".toCharArray(), "AudioInputStream".toCharArray() } };
    public static final char[][] JAVA_IO_RESOURCE_FREE_CLOSEABLES = { "StringReader".toCharArray(), "StringWriter".toCharArray(), "ByteArrayInputStream".toCharArray(), "ByteArrayOutputStream".toCharArray(), "CharArrayReader".toCharArray(), "CharArrayWriter".toCharArray(), "StringBufferInputStream".toCharArray() };
    public static final char[][] RESOURCE_FREE_CLOSEABLE_STREAM = { TypeConstants.JAVA, TypeConstants.UTIL, "stream".toCharArray(), "Stream".toCharArray() };
    public static final char[] ASSERT_CLASS = "Assert".toCharArray();
    public static final char[][] ORG_ECLIPSE_CORE_RUNTIME_ASSERT = { TypeConstants.ORG, TypeConstants.ECLIPSE, TypeConstants.CORE, TypeConstants.RUNTIME, TypeConstants.ASSERT_CLASS };
    public static final char[] IS_NOTNULL = "isNotNull".toCharArray();
    public static final char[] JUNIT = "junit".toCharArray();
    public static final char[] FRAMEWORK = "framework".toCharArray();
    public static final char[][] JUNIT_FRAMEWORK_ASSERT = { TypeConstants.JUNIT, TypeConstants.FRAMEWORK, TypeConstants.ASSERT_CLASS };
    public static final char[][] ORG_JUNIT_ASSERT = { TypeConstants.ORG, TypeConstants.JUNIT, TypeConstants.ASSERT_CLASS };
    public static final char[] ASSERT_NULL = "assertNull".toCharArray();
    public static final char[] ASSERT_NOTNULL = "assertNotNull".toCharArray();
    public static final char[] ASSERT_TRUE = "assertTrue".toCharArray();
    public static final char[] ASSERT_FALSE = "assertFalse".toCharArray();
    public static final char[] VALIDATE_CLASS = "Validate".toCharArray();
    public static final char[][] ORG_APACHE_COMMONS_LANG_VALIDATE = { TypeConstants.ORG, TypeConstants.APACHE, TypeConstants.COMMONS, TypeConstants.LANG, TypeConstants.VALIDATE_CLASS };
    public static final char[][] ORG_APACHE_COMMONS_LANG3_VALIDATE = { TypeConstants.ORG, TypeConstants.APACHE, TypeConstants.COMMONS, TypeConstants.LANG3, TypeConstants.VALIDATE_CLASS };
    public static final char[][] ORG_ECLIPSE_JDT_INTERNAL_COMPILER_LOOKUP_TYPEBINDING = { TypeConstants.ORG, TypeConstants.ECLIPSE, TypeConstants.JDT, TypeConstants.INTERNAL, TypeConstants.COMPILER, TypeConstants.LOOKUP, TypeConstants.TYPEBINDING };
    public static final char[][] ORG_ECLIPSE_JDT_CORE_DOM_ITYPEBINDING = { TypeConstants.ORG, TypeConstants.ECLIPSE, TypeConstants.JDT, TypeConstants.CORE, TypeConstants.DOM, TypeConstants.ITYPEBINDING };
    public static final char[] IS_TRUE = "isTrue".toCharArray();
    public static final char[] NOT_NULL = "notNull".toCharArray();
    public static final char[][] COM_GOOGLE_COMMON_BASE_PRECONDITIONS = { TypeConstants.COM, TypeConstants.GOOGLE, "common".toCharArray(), "base".toCharArray(), "Preconditions".toCharArray() };
    public static final char[] CHECK_NOT_NULL = "checkNotNull".toCharArray();
    public static final char[] CHECK_ARGUMENT = "checkArgument".toCharArray();
    public static final char[] CHECK_STATE = "checkState".toCharArray();
    public static final char[] REQUIRE_NON_NULL = "requireNonNull".toCharArray();
    public static final char[] INJECT_PACKAGE = "inject".toCharArray();
    public static final char[] INJECT_TYPE = "Inject".toCharArray();
    public static final char[][] JAVAX_ANNOTATION_INJECT_INJECT = { TypeConstants.JAVAX, TypeConstants.INJECT_PACKAGE, TypeConstants.INJECT_TYPE };
    public static final char[][] COM_GOOGLE_INJECT_INJECT = { TypeConstants.COM, TypeConstants.GOOGLE, TypeConstants.INJECT_PACKAGE, TypeConstants.INJECT_TYPE };
    public static final char[] OPTIONAL = "optional".toCharArray();
    public static final char[] AUTOWIRED = "Autowired".toCharArray();
    public static final char[] BEANS = "beans".toCharArray();
    public static final char[] FACTORY = "factory".toCharArray();
    public static final char[][] ORG_SPRING_AUTOWIRED = { TypeConstants.ORG, TypeConstants.SPRING, TypeConstants.BEANS, TypeConstants.FACTORY, TypeConstants.ANNOTATION, TypeConstants.AUTOWIRED };
    public static final char[] REQUIRED = "required".toCharArray();
    public static final int CONSTRAINT_EQUAL = 0;
    public static final int CONSTRAINT_EXTENDS = 1;
    public static final int CONSTRAINT_SUPER = 2;
    public static final char[] INIT = "<init>".toCharArray();
    public static final char[] CLINIT = "<clinit>".toCharArray();
    public static final char[] SYNTHETIC_SWITCH_ENUM_TABLE = "$SWITCH_TABLE$".toCharArray();
    public static final char[] SYNTHETIC_ENUM_VALUES = "ENUM$VALUES".toCharArray();
    public static final char[] SYNTHETIC_ASSERT_DISABLED = "$assertionsDisabled".toCharArray();
    public static final char[] SYNTHETIC_CLASS = "class$".toCharArray();
    public static final char[] SYNTHETIC_OUTER_LOCAL_PREFIX = "val$".toCharArray();
    public static final char[] SYNTHETIC_ENCLOSING_INSTANCE_PREFIX = "this$".toCharArray();
    public static final char[] SYNTHETIC_ACCESS_METHOD_PREFIX = "access$".toCharArray();
    public static final char[] SYNTHETIC_ENUM_CONSTANT_INITIALIZATION_METHOD_PREFIX = " enum constant initialization$".toCharArray();
    public static final char[] SYNTHETIC_STATIC_FACTORY = "<factory>".toCharArray();
    public static final char[] DEFAULT_LOCATION__PARAMETER = "PARAMETER".toCharArray();
    public static final char[] DEFAULT_LOCATION__RETURN_TYPE = "RETURN_TYPE".toCharArray();
    public static final char[] DEFAULT_LOCATION__FIELD = "FIELD".toCharArray();
    public static final char[] DEFAULT_LOCATION__TYPE_ARGUMENT = "TYPE_ARGUMENT".toCharArray();
    public static final char[] DEFAULT_LOCATION__TYPE_PARAMETER = "TYPE_PARAMETER".toCharArray();
    public static final char[] DEFAULT_LOCATION__TYPE_BOUND = "TYPE_BOUND".toCharArray();
    public static final char[] DEFAULT_LOCATION__ARRAY_CONTENTS = "ARRAY_CONTENTS".toCharArray();
    public static final char[] PACKAGE_INFO_NAME = "package-info".toCharArray();
    
    public enum BoundCheckStatus
    {
        OK("OK", 0), 
        NULL_PROBLEM("NULL_PROBLEM", 1), 
        UNCHECKED("UNCHECKED", 2), 
        MISMATCH("MISMATCH", 3);
        
        private BoundCheckStatus(final String s, final int n) {
        }
        
        boolean isOKbyJLS() {
            switch (this) {
                case OK:
                case NULL_PROBLEM: {
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        
        public BoundCheckStatus betterOf(final BoundCheckStatus other) {
            if (this.ordinal() < other.ordinal()) {
                return this;
            }
            return other;
        }
    }
    
    public static class CloseMethodRecord
    {
        public char[][] typeName;
        public char[] selector;
        public int numCloseableArgs;
        
        public CloseMethodRecord(final char[][] typeName, final char[] selector, final int num) {
            this.typeName = typeName;
            this.selector = selector;
            this.numCloseableArgs = num;
        }
    }
}
