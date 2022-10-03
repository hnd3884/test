package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import java.util.Iterator;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldif.LDIFException;
import java.io.IOException;
import com.unboundid.ldif.DuplicateValueBehavior;
import com.unboundid.ldif.LDIFReader;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import java.util.List;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.Collection;
import java.util.ArrayList;
import java.io.File;
import java.util.TreeMap;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldap.sdk.schema.EntryValidator;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldif.LDIFReaderEntryTranslator;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ValidateLDIF extends LDAPCommandLineTool implements LDIFReaderEntryTranslator
{
    private static final String EOL;
    private BooleanArgument ignoreDuplicateValues;
    private BooleanArgument ignoreUndefinedObjectClasses;
    private BooleanArgument ignoreUndefinedAttributes;
    private BooleanArgument ignoreMalformedDNs;
    private BooleanArgument ignoreMissingRDNValues;
    private BooleanArgument ignoreMissingSuperiorObjectClasses;
    private BooleanArgument ignoreStructuralObjectClasses;
    private BooleanArgument ignoreProhibitedObjectClasses;
    private BooleanArgument ignoreProhibitedAttributes;
    private BooleanArgument ignoreMissingAttributes;
    private BooleanArgument ignoreSingleValuedAttributes;
    private BooleanArgument ignoreAttributeSyntax;
    private BooleanArgument ignoreNameForms;
    private BooleanArgument isCompressed;
    private FileArgument schemaDirectory;
    private FileArgument ldifFile;
    private FileArgument rejectFile;
    private FileArgument encryptionPassphraseFile;
    private IntegerArgument numThreads;
    private StringArgument ignoreSyntaxViolationsForAttribute;
    private final AtomicLong entriesProcessed;
    private final AtomicLong malformedEntries;
    private EntryValidator entryValidator;
    private LDIFWriter rejectWriter;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final ValidateLDIF validateLDIF = new ValidateLDIF(outStream, errStream);
        return validateLDIF.runTool(args);
    }
    
    public ValidateLDIF(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.entriesProcessed = new AtomicLong(0L);
        this.malformedEntries = new AtomicLong(0L);
    }
    
    @Override
    public String getToolName() {
        return "validate-ldif";
    }
    
    @Override
    public String getToolDescription() {
        return "Validate the contents of an LDIF file against the server schema.";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public boolean supportsInteractiveMode() {
        return true;
    }
    
    @Override
    public boolean defaultsToInteractiveMode() {
        return true;
    }
    
    @Override
    protected boolean supportsOutputFile() {
        return true;
    }
    
    @Override
    protected boolean defaultToPromptForBindPassword() {
        return true;
    }
    
    @Override
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    protected boolean includeAlternateLongIdentifiers() {
        return true;
    }
    
    @Override
    protected boolean supportsSSLDebugging() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        String description = "The path to the LDIF file to process.  The tool will automatically attempt to detect whether the file is encrypted or compressed.";
        (this.ldifFile = new FileArgument('f', "ldifFile", true, 1, "{path}", description, true, true, true, false)).addLongIdentifier("ldif-file", true);
        parser.addArgument(this.ldifFile);
        description = "Indicates that the specified LDIF file is compressed using gzip compression.";
        (this.isCompressed = new BooleanArgument('c', "isCompressed", description)).addLongIdentifier("is-compressed", true);
        this.isCompressed.setHidden(true);
        parser.addArgument(this.isCompressed);
        description = "Indicates that the specified LDIF file is encrypted and that the encryption passphrase is contained in the specified file.  If the LDIF data is encrypted and this argument is not provided, then the tool will interactively prompt for the encryption passphrase.";
        (this.encryptionPassphraseFile = new FileArgument(null, "encryptionPassphraseFile", false, 1, null, description, true, true, true, false)).addLongIdentifier("encryption-passphrase-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryptionPasswordFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-password-file", true);
        parser.addArgument(this.encryptionPassphraseFile);
        description = "The path to the file to which rejected entries should be written.";
        (this.rejectFile = new FileArgument('R', "rejectFile", false, 1, "{path}", description, false, true, true, false)).addLongIdentifier("reject-file", true);
        parser.addArgument(this.rejectFile);
        description = "The path to a directory containing one or more LDIF files with the schema information to use.  If this is provided, then no LDAP communication will be performed.";
        (this.schemaDirectory = new FileArgument(null, "schemaDirectory", false, 1, "{path}", description, true, true, false, true)).addLongIdentifier("schema-directory", true);
        parser.addArgument(this.schemaDirectory);
        description = "The number of threads to use when processing the LDIF file.";
        (this.numThreads = new IntegerArgument('t', "numThreads", true, 1, "{num}", description, 1, Integer.MAX_VALUE, 1)).addLongIdentifier("num-threads", true);
        parser.addArgument(this.numThreads);
        description = "Ignore validation failures due to entries containing duplicate values for the same attribute.";
        (this.ignoreDuplicateValues = new BooleanArgument(null, "ignoreDuplicateValues", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreDuplicateValues.addLongIdentifier("ignore-duplicate-values", true);
        parser.addArgument(this.ignoreDuplicateValues);
        description = "Ignore validation failures due to object classes not defined in the schema.";
        (this.ignoreUndefinedObjectClasses = new BooleanArgument(null, "ignoreUndefinedObjectClasses", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreUndefinedObjectClasses.addLongIdentifier("ignore-undefined-object-classes", true);
        parser.addArgument(this.ignoreUndefinedObjectClasses);
        description = "Ignore validation failures due to attributes not defined in the schema.";
        (this.ignoreUndefinedAttributes = new BooleanArgument(null, "ignoreUndefinedAttributes", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreUndefinedAttributes.addLongIdentifier("ignore-undefined-attributes", true);
        parser.addArgument(this.ignoreUndefinedAttributes);
        description = "Ignore validation failures due to entries with malformed DNs.";
        (this.ignoreMalformedDNs = new BooleanArgument(null, "ignoreMalformedDNs", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreMalformedDNs.addLongIdentifier("ignore-malformed-dns", true);
        parser.addArgument(this.ignoreMalformedDNs);
        description = "Ignore validation failures due to entries with RDN attribute values that are missing from the set of entry attributes.";
        (this.ignoreMissingRDNValues = new BooleanArgument(null, "ignoreMissingRDNValues", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreMissingRDNValues.addLongIdentifier("ignore-missing-rdn-values", true);
        parser.addArgument(this.ignoreMissingRDNValues);
        description = "Ignore validation failures due to entries without exactly structural object class.";
        (this.ignoreStructuralObjectClasses = new BooleanArgument(null, "ignoreStructuralObjectClasses", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreStructuralObjectClasses.addLongIdentifier("ignore-structural-object-classes", true);
        parser.addArgument(this.ignoreStructuralObjectClasses);
        description = "Ignore validation failures due to entries with object classes that are not allowed.";
        (this.ignoreProhibitedObjectClasses = new BooleanArgument(null, "ignoreProhibitedObjectClasses", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreProhibitedObjectClasses.addLongIdentifier("ignore-prohibited-object-classes", true);
        parser.addArgument(this.ignoreProhibitedObjectClasses);
        description = "Ignore validation failures due to entries that are one or more superior object classes.";
        (this.ignoreMissingSuperiorObjectClasses = new BooleanArgument(null, "ignoreMissingSuperiorObjectClasses", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreMissingSuperiorObjectClasses.addLongIdentifier("ignore-missing-superior-object-classes", true);
        parser.addArgument(this.ignoreMissingSuperiorObjectClasses);
        description = "Ignore validation failures due to entries with attributes that are not allowed.";
        (this.ignoreProhibitedAttributes = new BooleanArgument(null, "ignoreProhibitedAttributes", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreProhibitedAttributes.addLongIdentifier("ignore-prohibited-attributes", true);
        parser.addArgument(this.ignoreProhibitedAttributes);
        description = "Ignore validation failures due to entries missing required attributes.";
        (this.ignoreMissingAttributes = new BooleanArgument(null, "ignoreMissingAttributes", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreMissingAttributes.addLongIdentifier("ignore-missing-attributes", true);
        parser.addArgument(this.ignoreMissingAttributes);
        description = "Ignore validation failures due to entries with multiple values for single-valued attributes.";
        (this.ignoreSingleValuedAttributes = new BooleanArgument(null, "ignoreSingleValuedAttributes", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreSingleValuedAttributes.addLongIdentifier("ignore-single-valued-attributes", true);
        parser.addArgument(this.ignoreSingleValuedAttributes);
        description = "Ignore validation failures due to entries with attribute values that violate their associated syntax.  If this is provided, then no attribute syntax violations will be flagged.  If this is not provided, then all attribute syntax violations will be flagged except for violations in those attributes excluded by the --ignoreSyntaxViolationsForAttribute argument.";
        (this.ignoreAttributeSyntax = new BooleanArgument(null, "ignoreAttributeSyntax", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreAttributeSyntax.addLongIdentifier("ignore-attribute-syntax", true);
        parser.addArgument(this.ignoreAttributeSyntax);
        description = "The name or OID of an attribute for which to ignore validation failures due to violations of the associated attribute syntax.  This argument can only be used if the --ignoreAttributeSyntax argument is not provided.";
        (this.ignoreSyntaxViolationsForAttribute = new StringArgument(null, "ignoreSyntaxViolationsForAttribute", false, 0, "{attr}", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreSyntaxViolationsForAttribute.addLongIdentifier("ignore-syntax-violations-for-attribute", true);
        parser.addArgument(this.ignoreSyntaxViolationsForAttribute);
        description = "Ignore validation failures due to entries with RDNs that violate the associated name form definition.";
        (this.ignoreNameForms = new BooleanArgument(null, "ignoreNameForms", description)).setArgumentGroupName("Validation Strictness Arguments");
        this.ignoreNameForms.addLongIdentifier("ignore-name-forms", true);
        parser.addArgument(this.ignoreNameForms);
        parser.addExclusiveArgumentSet(this.ignoreAttributeSyntax, this.ignoreSyntaxViolationsForAttribute, new Argument[0]);
    }
    
    @Override
    public ResultCode doToolProcessing() {
        Schema schema;
        if (this.schemaDirectory.isPresent()) {
            final File schemaDir = this.schemaDirectory.getValue();
            try {
                final TreeMap<String, File> fileMap = new TreeMap<String, File>();
                for (final File f : schemaDir.listFiles()) {
                    final String name = f.getName();
                    if (f.isFile() && name.endsWith(".ldif")) {
                        fileMap.put(name, f);
                    }
                }
                if (fileMap.isEmpty()) {
                    this.err("No LDIF files found in directory " + schemaDir.getAbsolutePath());
                    return ResultCode.PARAM_ERROR;
                }
                final ArrayList<File> fileList = new ArrayList<File>(fileMap.values());
                schema = Schema.getSchema(fileList);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                this.err("Unable to read schema from files in directory " + schemaDir.getAbsolutePath() + ":  " + StaticUtils.getExceptionMessage(e));
                return ResultCode.LOCAL_ERROR;
            }
        }
        else {
            try {
                final LDAPConnection connection = this.getConnection();
                schema = connection.getSchema();
                connection.close();
            }
            catch (final LDAPException le) {
                Debug.debugException(le);
                this.err("Unable to connect to the directory server and read the schema:  ", le.getMessage());
                return le.getResultCode();
            }
        }
        String encryptionPassphrase = null;
        if (this.encryptionPassphraseFile.isPresent()) {
            try {
                encryptionPassphrase = ToolUtils.readEncryptionPassphraseFromFile(this.encryptionPassphraseFile.getValue());
            }
            catch (final LDAPException e2) {
                Debug.debugException(e2);
                this.err(e2.getMessage());
                return e2.getResultCode();
            }
        }
        (this.entryValidator = new EntryValidator(schema)).setCheckAttributeSyntax(!this.ignoreAttributeSyntax.isPresent());
        this.entryValidator.setCheckMalformedDNs(!this.ignoreMalformedDNs.isPresent());
        this.entryValidator.setCheckEntryMissingRDNValues(!this.ignoreMissingRDNValues.isPresent());
        this.entryValidator.setCheckMissingAttributes(!this.ignoreMissingAttributes.isPresent());
        this.entryValidator.setCheckNameForms(!this.ignoreNameForms.isPresent());
        this.entryValidator.setCheckProhibitedAttributes(!this.ignoreProhibitedAttributes.isPresent());
        this.entryValidator.setCheckProhibitedObjectClasses(!this.ignoreProhibitedObjectClasses.isPresent());
        this.entryValidator.setCheckMissingSuperiorObjectClasses(!this.ignoreMissingSuperiorObjectClasses.isPresent());
        this.entryValidator.setCheckSingleValuedAttributes(!this.ignoreSingleValuedAttributes.isPresent());
        this.entryValidator.setCheckStructuralObjectClasses(!this.ignoreStructuralObjectClasses.isPresent());
        this.entryValidator.setCheckUndefinedAttributes(!this.ignoreUndefinedAttributes.isPresent());
        this.entryValidator.setCheckUndefinedObjectClasses(!this.ignoreUndefinedObjectClasses.isPresent());
        if (this.ignoreSyntaxViolationsForAttribute.isPresent()) {
            this.entryValidator.setIgnoreSyntaxViolationAttributeTypes(this.ignoreSyntaxViolationsForAttribute.getValues());
        }
        this.rejectWriter = null;
        LDIFReader ldifReader;
        try {
            InputStream inputStream = new FileInputStream(this.ldifFile.getValue());
            inputStream = ToolUtils.getPossiblyPassphraseEncryptedInputStream(inputStream, encryptionPassphrase, false, "LDIF file '" + this.ldifFile.getValue().getPath() + "' is encrypted.  Please enter the encryption passphrase:", "ERROR:  The provided passphrase was incorrect.", this.getOut(), this.getErr()).getFirst();
            if (this.isCompressed.isPresent()) {
                inputStream = new GZIPInputStream(inputStream);
            }
            else {
                inputStream = ToolUtils.getPossiblyGZIPCompressedInputStream(inputStream);
            }
            ldifReader = new LDIFReader(inputStream, this.numThreads.getValue(), this);
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            this.err("Unable to open the LDIF reader:  ", StaticUtils.getExceptionMessage(e3));
            return ResultCode.LOCAL_ERROR;
        }
        ldifReader.setSchema(schema);
        Label_0813: {
            if (this.ignoreDuplicateValues.isPresent()) {
                ldifReader.setDuplicateValueBehavior(DuplicateValueBehavior.STRIP);
                break Label_0813;
            }
            ldifReader.setDuplicateValueBehavior(DuplicateValueBehavior.REJECT);
            try {
                try {
                    if (this.rejectFile.isPresent()) {
                        this.rejectWriter = new LDIFWriter(this.rejectFile.getValue());
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    this.err("Unable to create the reject writer:  ", StaticUtils.getExceptionMessage(e3));
                    return ResultCode.LOCAL_ERROR;
                }
                ResultCode resultCode = ResultCode.SUCCESS;
            Label_0923_Outer:
                while (true) {
                    while (true) {
                        try {
                            Entry e4;
                            do {
                                e4 = ldifReader.readEntry();
                            } while (e4 != null);
                            break;
                        }
                        catch (final LDIFException le2) {
                            Debug.debugException(le2);
                            this.malformedEntries.incrementAndGet();
                            if (resultCode == ResultCode.SUCCESS) {
                                resultCode = ResultCode.DECODING_ERROR;
                            }
                            if (this.rejectWriter != null) {
                                try {
                                    this.rejectWriter.writeComment("Unable to parse an entry read from LDIF:", false, false);
                                    if (!le2.mayContinueReading()) {
                                        this.rejectWriter.writeComment(StaticUtils.getExceptionMessage(le2), false, false);
                                        this.rejectWriter.writeComment("Unable to continue LDIF processing.", false, true);
                                        this.err("Aborting LDIF processing:  ", StaticUtils.getExceptionMessage(le2));
                                        return ResultCode.LOCAL_ERROR;
                                    }
                                    this.rejectWriter.writeComment(StaticUtils.getExceptionMessage(le2), false, true);
                                }
                                catch (final IOException ioe) {
                                    Debug.debugException(ioe);
                                    this.err("Unable to write to the reject file:", StaticUtils.getExceptionMessage(ioe));
                                    this.err("LDIF parse failure that triggered the rejection:  ", StaticUtils.getExceptionMessage(le2));
                                    return ResultCode.LOCAL_ERROR;
                                }
                            }
                            continue Label_0923_Outer;
                        }
                        catch (final IOException ioe2) {
                            Debug.debugException(ioe2);
                            if (this.rejectWriter != null) {
                                try {
                                    this.rejectWriter.writeComment("I/O error reading from LDIF:", false, false);
                                    this.rejectWriter.writeComment(StaticUtils.getExceptionMessage(ioe2), false, true);
                                    return ResultCode.LOCAL_ERROR;
                                }
                                catch (final Exception ex) {
                                    Debug.debugException(ex);
                                    this.err("I/O error reading from LDIF:", StaticUtils.getExceptionMessage(ioe2));
                                    return ResultCode.LOCAL_ERROR;
                                }
                            }
                            continue Label_0923_Outer;
                        }
                        continue;
                    }
                }
                if (this.malformedEntries.get() > 0L) {
                    this.out(this.malformedEntries.get() + " entries were malformed and could not " + "be read from the LDIF file.");
                }
                if (this.entryValidator.getInvalidEntries() > 0L) {
                    if (resultCode == ResultCode.SUCCESS) {
                        resultCode = ResultCode.OBJECT_CLASS_VIOLATION;
                    }
                    for (final String s : this.entryValidator.getInvalidEntrySummary(true)) {
                        this.out(s);
                    }
                }
                else if (this.malformedEntries.get() == 0L) {
                    this.out("No errors were encountered.");
                }
                return resultCode;
            }
            finally {
                try {
                    ldifReader.close();
                }
                catch (final Exception e5) {
                    Debug.debugException(e5);
                }
                try {
                    if (this.rejectWriter != null) {
                        this.rejectWriter.close();
                    }
                }
                catch (final Exception e5) {
                    Debug.debugException(e5);
                }
            }
        }
    }
    
    @Override
    public Entry translate(final Entry entry, final long firstLineNumber) {
        final ArrayList<String> invalidReasons = new ArrayList<String>(5);
        if (!this.entryValidator.entryIsValid(entry, invalidReasons) && this.rejectWriter != null) {
            synchronized (this) {
                try {
                    this.rejectWriter.writeEntry(entry, listToString(invalidReasons));
                }
                catch (final IOException ioe) {
                    Debug.debugException(ioe);
                }
            }
        }
        final long numEntries = this.entriesProcessed.incrementAndGet();
        if (numEntries % 1000L == 0L) {
            this.out("Processed ", numEntries, " entries.");
        }
        return null;
    }
    
    private static String listToString(final List<String> l) {
        if (l == null || l.isEmpty()) {
            return null;
        }
        final StringBuilder buffer = new StringBuilder();
        final Iterator<String> iterator = l.iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(ValidateLDIF.EOL);
            }
        }
        return buffer.toString();
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        String[] args = { "--hostname", "server.example.com", "--port", "389", "--ldifFile", "data.ldif", "--rejectFile", "rejects.ldif", "--numThreads", "4" };
        String description = "Validate the contents of the 'data.ldif' file using the schema defined in the specified directory server using four concurrent threads.  All types of validation will be performed, and information about any errors will be written to the 'rejects.ldif' file.";
        examples.put(args, description);
        args = new String[] { "--schemaDirectory", "/ds/config/schema", "--ldifFile", "data.ldif", "--rejectFile", "rejects.ldif", "--ignoreStructuralObjectClasses", "--ignoreAttributeSyntax" };
        description = "Validate the contents of the 'data.ldif' file using the schema defined in LDIF files contained in the /ds/config/schema directory using a single thread.  Any errors resulting from entries that do not have exactly one structural object class or from values which violate the syntax for their associated attribute types will be ignored.  Information about any other failures will be written to the 'rejects.ldif' file.";
        examples.put(args, description);
        return examples;
    }
    
    public EntryValidator getEntryValidator() {
        return this.entryValidator;
    }
    
    static {
        EOL = StaticUtils.getSystemProperty("line.separator", "\n");
    }
}
