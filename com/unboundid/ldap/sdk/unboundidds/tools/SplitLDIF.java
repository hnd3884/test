package com.unboundid.ldap.sdk.unboundidds.tools;

import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.schema.Schema;
import java.util.Map;
import java.util.Collections;
import com.unboundid.ldap.sdk.Entry;
import java.io.IOException;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.ByteStringBuffer;
import java.util.zip.GZIPOutputStream;
import com.unboundid.util.PassphraseEncryptedOutputStream;
import java.io.FileOutputStream;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldif.LDIFReaderEntryTranslator;
import com.unboundid.ldif.LDIFReader;
import java.io.InputStream;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Iterator;
import java.util.Set;
import java.io.File;
import java.util.List;
import com.unboundid.ldap.sdk.Filter;
import java.util.LinkedHashSet;
import com.unboundid.util.args.ArgumentException;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.SubCommand;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.CommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SplitLDIF extends CommandLineTool
{
    private static final int MAX_OUTPUT_LINE_LENGTH;
    private BooleanArgument addEntriesOutsideSplitBaseDNToAllSets;
    private BooleanArgument addEntriesOutsideSplitBaseDNToDedicatedSet;
    private BooleanArgument compressTarget;
    private BooleanArgument encryptTarget;
    private BooleanArgument sourceCompressed;
    private DNArgument splitBaseDN;
    private FileArgument encryptionPassphraseFile;
    private FileArgument schemaPath;
    private FileArgument sourceLDIF;
    private FileArgument targetLDIFBasePath;
    private IntegerArgument numThreads;
    private IntegerArgument splitUsingHashOnRDNNumSets;
    private SubCommand splitUsingHashOnRDN;
    private BooleanArgument splitUsingHashOnAttributeAssumeFlatDIT;
    private BooleanArgument splitUsingHashOnAttributeUseAllValues;
    private IntegerArgument splitUsingHashOnAttributeNumSets;
    private StringArgument splitUsingHashOnAttributeAttributeName;
    private SubCommand splitUsingHashOnAttribute;
    private BooleanArgument splitUsingFewestEntriesAssumeFlatDIT;
    private IntegerArgument splitUsingFewestEntriesNumSets;
    private SubCommand splitUsingFewestEntries;
    private BooleanArgument splitUsingFilterAssumeFlatDIT;
    private FilterArgument splitUsingFilterFilter;
    private SubCommand splitUsingFilter;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final OutputStream out, final OutputStream err, final String... args) {
        final SplitLDIF tool = new SplitLDIF(out, err);
        return tool.runTool(args);
    }
    
    public SplitLDIF(final OutputStream out, final OutputStream err) {
        super(out, err);
        this.addEntriesOutsideSplitBaseDNToAllSets = null;
        this.addEntriesOutsideSplitBaseDNToDedicatedSet = null;
        this.compressTarget = null;
        this.encryptTarget = null;
        this.sourceCompressed = null;
        this.splitBaseDN = null;
        this.encryptionPassphraseFile = null;
        this.schemaPath = null;
        this.sourceLDIF = null;
        this.targetLDIFBasePath = null;
        this.numThreads = null;
        this.splitUsingHashOnRDNNumSets = null;
        this.splitUsingHashOnRDN = null;
        this.splitUsingHashOnAttributeAssumeFlatDIT = null;
        this.splitUsingHashOnAttributeUseAllValues = null;
        this.splitUsingHashOnAttributeNumSets = null;
        this.splitUsingHashOnAttributeAttributeName = null;
        this.splitUsingHashOnAttribute = null;
        this.splitUsingFewestEntriesAssumeFlatDIT = null;
        this.splitUsingFewestEntriesNumSets = null;
        this.splitUsingFewestEntries = null;
        this.splitUsingFilterAssumeFlatDIT = null;
        this.splitUsingFilterFilter = null;
        this.splitUsingFilter = null;
    }
    
    @Override
    public String getToolName() {
        return "split-ldif";
    }
    
    @Override
    public String getToolDescription() {
        return ToolMessages.INFO_SPLIT_LDIF_TOOL_DESCRIPTION.get();
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
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    public void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        (this.sourceLDIF = new FileArgument('l', "sourceLDIF", true, 0, null, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_SOURCE_LDIF.get(), true, false, true, false)).addLongIdentifier("inputLDIF", true);
        this.sourceLDIF.addLongIdentifier("source-ldif", true);
        this.sourceLDIF.addLongIdentifier("input-ldif", true);
        parser.addArgument(this.sourceLDIF);
        (this.sourceCompressed = new BooleanArgument('C', "sourceCompressed", ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_SOURCE_COMPRESSED.get())).addLongIdentifier("inputCompressed", true);
        this.sourceCompressed.addLongIdentifier("source-compressed", true);
        this.sourceCompressed.addLongIdentifier("input-compressed", true);
        parser.addArgument(this.sourceCompressed);
        (this.targetLDIFBasePath = new FileArgument('o', "targetLDIFBasePath", false, 1, null, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_TARGET_LDIF_BASE.get(), false, true, true, false)).addLongIdentifier("outputLDIFBasePath", true);
        this.targetLDIFBasePath.addLongIdentifier("target-ldif-base-path", true);
        this.targetLDIFBasePath.addLongIdentifier("output-ldif-base-path", true);
        parser.addArgument(this.targetLDIFBasePath);
        (this.compressTarget = new BooleanArgument('c', "compressTarget", ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_COMPRESS_TARGET.get())).addLongIdentifier("compressOutput", true);
        this.compressTarget.addLongIdentifier("compress", true);
        this.compressTarget.addLongIdentifier("compress-target", true);
        this.compressTarget.addLongIdentifier("compress-output", true);
        parser.addArgument(this.compressTarget);
        (this.encryptTarget = new BooleanArgument(null, "encryptTarget", ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_ENCRYPT_TARGET.get())).addLongIdentifier("encryptOutput", true);
        this.encryptTarget.addLongIdentifier("encrypt", true);
        this.encryptTarget.addLongIdentifier("encrypt-target", true);
        this.encryptTarget.addLongIdentifier("encrypt-output", true);
        parser.addArgument(this.encryptTarget);
        (this.encryptionPassphraseFile = new FileArgument(null, "encryptionPassphraseFile", false, 1, null, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_ENCRYPT_PW_FILE.get(), true, true, true, false)).addLongIdentifier("encryptionPasswordFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-passphrase-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-password-file", true);
        parser.addArgument(this.encryptionPassphraseFile);
        (this.splitBaseDN = new DNArgument('b', "splitBaseDN", true, 1, null, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_SPLIT_BASE_DN.get())).addLongIdentifier("baseDN", true);
        this.splitBaseDN.addLongIdentifier("split-base-dn", true);
        this.splitBaseDN.addLongIdentifier("base-dn", true);
        parser.addArgument(this.splitBaseDN);
        (this.addEntriesOutsideSplitBaseDNToAllSets = new BooleanArgument(null, "addEntriesOutsideSplitBaseDNToAllSets", 1, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_OUTSIDE_TO_ALL_SETS.get())).addLongIdentifier("add-entries-outside-split-base-dn-to-all-sets", true);
        parser.addArgument(this.addEntriesOutsideSplitBaseDNToAllSets);
        (this.addEntriesOutsideSplitBaseDNToDedicatedSet = new BooleanArgument(null, "addEntriesOutsideSplitBaseDNToDedicatedSet", 1, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_OUTSIDE_TO_DEDICATED_SET.get())).addLongIdentifier("add-entries-outside-split-base-dn-to-dedicated-set", true);
        parser.addArgument(this.addEntriesOutsideSplitBaseDNToDedicatedSet);
        (this.schemaPath = new FileArgument(null, "schemaPath", false, 0, null, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_SCHEMA_PATH.get(), true, false, false, false)).addLongIdentifier("schemaFile", true);
        this.schemaPath.addLongIdentifier("schemaDirectory", true);
        this.schemaPath.addLongIdentifier("schema-path", true);
        this.schemaPath.addLongIdentifier("schema-file", true);
        this.schemaPath.addLongIdentifier("schema-directory", true);
        parser.addArgument(this.schemaPath);
        (this.numThreads = new IntegerArgument('t', "numThreads", false, 1, null, ToolMessages.INFO_SPLIT_LDIF_GLOBAL_ARG_DESC_NUM_THREADS.get(), 1, Integer.MAX_VALUE, 1)).addLongIdentifier("num-threads", true);
        parser.addArgument(this.numThreads);
        final ArgumentParser splitUsingHashOnRDNParser = new ArgumentParser("split-using-hash-on-rdn", ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_RDN_DESC.get());
        (this.splitUsingHashOnRDNNumSets = new IntegerArgument(null, "numSets", true, 1, null, ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_RDN_ARG_DESC_NUM_SETS.get(), 2, Integer.MAX_VALUE)).addLongIdentifier("num-sets", true);
        splitUsingHashOnRDNParser.addArgument(this.splitUsingHashOnRDNNumSets);
        final LinkedHashMap<String[], String> splitUsingHashOnRDNExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        splitUsingHashOnRDNExamples.put(new String[] { "split-using-hash-on-rdn", "--sourceLDIF", "whole.ldif", "--targetLDIFBasePath", "split.ldif", "--splitBaseDN", "ou=People,dc=example,dc=com", "--numSets", "4", "--schemaPath", "config/schema", "--addEntriesOutsideSplitBaseDNToAllSets" }, ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_RDN_EXAMPLE.get());
        (this.splitUsingHashOnRDN = new SubCommand("split-using-hash-on-rdn", ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_RDN_DESC.get(), splitUsingHashOnRDNParser, splitUsingHashOnRDNExamples)).addName("hash-on-rdn", true);
        parser.addSubCommand(this.splitUsingHashOnRDN);
        final ArgumentParser splitUsingHashOnAttributeParser = new ArgumentParser("split-using-hash-on-attribute", ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_ATTR_DESC.get());
        (this.splitUsingHashOnAttributeAttributeName = new StringArgument(null, "attributeName", true, 1, "{attr}", ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_ATTR_ARG_DESC_ATTR_NAME.get())).addLongIdentifier("attribute-name", true);
        splitUsingHashOnAttributeParser.addArgument(this.splitUsingHashOnAttributeAttributeName);
        (this.splitUsingHashOnAttributeNumSets = new IntegerArgument(null, "numSets", true, 1, null, ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_ATTR_ARG_DESC_NUM_SETS.get(), 2, Integer.MAX_VALUE)).addLongIdentifier("num-sets", true);
        splitUsingHashOnAttributeParser.addArgument(this.splitUsingHashOnAttributeNumSets);
        (this.splitUsingHashOnAttributeUseAllValues = new BooleanArgument(null, "useAllValues", 1, ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_ATTR_ARG_DESC_ALL_VALUES.get())).addLongIdentifier("use-all-values", true);
        splitUsingHashOnAttributeParser.addArgument(this.splitUsingHashOnAttributeUseAllValues);
        (this.splitUsingHashOnAttributeAssumeFlatDIT = new BooleanArgument(null, "assumeFlatDIT", 1, ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_ATTR_ARG_DESC_ASSUME_FLAT_DIT.get())).addLongIdentifier("assume-flat-dit", true);
        splitUsingHashOnAttributeParser.addArgument(this.splitUsingHashOnAttributeAssumeFlatDIT);
        final LinkedHashMap<String[], String> splitUsingHashOnAttributeExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        splitUsingHashOnAttributeExamples.put(new String[] { "split-using-hash-on-attribute", "--sourceLDIF", "whole.ldif", "--targetLDIFBasePath", "split.ldif", "--splitBaseDN", "ou=People,dc=example,dc=com", "--attributeName", "uid", "--numSets", "4", "--schemaPath", "config/schema", "--addEntriesOutsideSplitBaseDNToAllSets" }, ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_ATTR_EXAMPLE.get());
        (this.splitUsingHashOnAttribute = new SubCommand("split-using-hash-on-attribute", ToolMessages.INFO_SPLIT_LDIF_SC_HASH_ON_ATTR_DESC.get(), splitUsingHashOnAttributeParser, splitUsingHashOnAttributeExamples)).addName("hash-on-attribute", true);
        parser.addSubCommand(this.splitUsingHashOnAttribute);
        final ArgumentParser splitUsingFewestEntriesParser = new ArgumentParser("split-using-fewest-entries", ToolMessages.INFO_SPLIT_LDIF_SC_FEWEST_ENTRIES_DESC.get());
        (this.splitUsingFewestEntriesNumSets = new IntegerArgument(null, "numSets", true, 1, null, ToolMessages.INFO_SPLIT_LDIF_SC_FEWEST_ENTRIES_ARG_DESC_NUM_SETS.get(), 2, Integer.MAX_VALUE)).addLongIdentifier("num-sets", true);
        splitUsingFewestEntriesParser.addArgument(this.splitUsingFewestEntriesNumSets);
        (this.splitUsingFewestEntriesAssumeFlatDIT = new BooleanArgument(null, "assumeFlatDIT", 1, ToolMessages.INFO_SPLIT_LDIF_SC_FEWEST_ENTRIES_ARG_DESC_ASSUME_FLAT_DIT.get())).addLongIdentifier("assume-flat-dit", true);
        splitUsingFewestEntriesParser.addArgument(this.splitUsingFewestEntriesAssumeFlatDIT);
        final LinkedHashMap<String[], String> splitUsingFewestEntriesExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        splitUsingFewestEntriesExamples.put(new String[] { "split-using-fewest-entries", "--sourceLDIF", "whole.ldif", "--targetLDIFBasePath", "split.ldif", "--splitBaseDN", "ou=People,dc=example,dc=com", "--numSets", "4", "--schemaPath", "config/schema", "--addEntriesOutsideSplitBaseDNToAllSets" }, ToolMessages.INFO_SPLIT_LDIF_SC_FEWEST_ENTRIES_EXAMPLE.get());
        (this.splitUsingFewestEntries = new SubCommand("split-using-fewest-entries", ToolMessages.INFO_SPLIT_LDIF_SC_FEWEST_ENTRIES_DESC.get(), splitUsingFewestEntriesParser, splitUsingFewestEntriesExamples)).addName("fewest-entries", true);
        parser.addSubCommand(this.splitUsingFewestEntries);
        final ArgumentParser splitUsingFilterParser = new ArgumentParser("split-using-filter", ToolMessages.INFO_SPLIT_LDIF_SC_FILTER_DESC.get());
        splitUsingFilterParser.addArgument(this.splitUsingFilterFilter = new FilterArgument(null, "filter", true, 0, null, ToolMessages.INFO_SPLIT_LDIF_SC_FILTER_ARG_DESC_FILTER.get()));
        (this.splitUsingFilterAssumeFlatDIT = new BooleanArgument(null, "assumeFlatDIT", 1, ToolMessages.INFO_SPLIT_LDIF_SC_FILTER_ARG_DESC_ASSUME_FLAT_DIT.get())).addLongIdentifier("assume-flat-dit", true);
        splitUsingFilterParser.addArgument(this.splitUsingFilterAssumeFlatDIT);
        final LinkedHashMap<String[], String> splitUsingFilterExamples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        splitUsingFilterExamples.put(new String[] { "split-using-filter", "--sourceLDIF", "whole.ldif", "--targetLDIFBasePath", "split.ldif", "--splitBaseDN", "ou=People,dc=example,dc=com", "--filter", "(timeZone=Eastern)", "--filter", "(timeZone=Central)", "--filter", "(timeZone=Mountain)", "--filter", "(timeZone=Pacific)", "--schemaPath", "config/schema", "--addEntriesOutsideSplitBaseDNToAllSets" }, ToolMessages.INFO_SPLIT_LDIF_SC_FILTER_EXAMPLE.get());
        (this.splitUsingFilter = new SubCommand("split-using-filter", ToolMessages.INFO_SPLIT_LDIF_SC_FILTER_DESC.get(), splitUsingFilterParser, splitUsingFilterExamples)).addName("filter", true);
        parser.addSubCommand(this.splitUsingFilter);
    }
    
    @Override
    public void doExtendedArgumentValidation() throws ArgumentException {
        final List<File> sourceLDIFValues = this.sourceLDIF.getValues();
        if (sourceLDIFValues.size() > 1 && !this.targetLDIFBasePath.isPresent()) {
            throw new ArgumentException(ToolMessages.ERR_SPLIT_LDIF_NO_TARGET_BASE_PATH.get(this.sourceLDIF.getIdentifierString(), this.targetLDIFBasePath.getIdentifierString()));
        }
        if (this.splitUsingFilter.isPresent()) {
            final List<Filter> filterList = this.splitUsingFilterFilter.getValues();
            final Set<Filter> filterSet = new LinkedHashSet<Filter>(StaticUtils.computeMapCapacity(filterList.size()));
            for (final Filter f : filterList) {
                if (filterSet.contains(f)) {
                    throw new ArgumentException(ToolMessages.ERR_SPLIT_LDIF_NON_UNIQUE_FILTER.get(this.splitUsingFilterFilter.getIdentifierString(), f.toString()));
                }
                filterSet.add(f);
            }
            if (filterSet.size() < 2) {
                throw new ArgumentException(ToolMessages.ERR_SPLIT_LDIF_NOT_ENOUGH_FILTERS.get(this.splitUsingFilter.getPrimaryName(), this.splitUsingFilterFilter.getIdentifierString()));
            }
        }
    }
    
    @Override
    public ResultCode doToolProcessing() {
        Schema schema;
        try {
            schema = this.getSchema();
        }
        catch (final LDAPException le) {
            this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, le.getMessage());
            return le.getResultCode();
        }
        String encryptionPassphrase = null;
        if (this.encryptionPassphraseFile.isPresent()) {
            try {
                encryptionPassphrase = ToolUtils.readEncryptionPassphraseFromFile(this.encryptionPassphraseFile.getValue());
            }
            catch (final LDAPException e) {
                Debug.debugException(e);
                this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, e.getMessage());
                return e.getResultCode();
            }
        }
        SplitLDIFTranslator translator;
        if (this.splitUsingHashOnRDN.isPresent()) {
            translator = new SplitLDIFRDNHashTranslator(this.splitBaseDN.getValue(), this.splitUsingHashOnRDNNumSets.getValue(), this.addEntriesOutsideSplitBaseDNToAllSets.isPresent(), this.addEntriesOutsideSplitBaseDNToDedicatedSet.isPresent());
        }
        else if (this.splitUsingHashOnAttribute.isPresent()) {
            translator = new SplitLDIFAttributeHashTranslator(this.splitBaseDN.getValue(), this.splitUsingHashOnAttributeNumSets.getValue(), this.splitUsingHashOnAttributeAttributeName.getValue(), this.splitUsingHashOnAttributeUseAllValues.isPresent(), this.splitUsingHashOnAttributeAssumeFlatDIT.isPresent(), this.addEntriesOutsideSplitBaseDNToAllSets.isPresent(), this.addEntriesOutsideSplitBaseDNToDedicatedSet.isPresent());
        }
        else if (this.splitUsingFewestEntries.isPresent()) {
            translator = new SplitLDIFFewestEntriesTranslator(this.splitBaseDN.getValue(), this.splitUsingFewestEntriesNumSets.getValue(), this.splitUsingFewestEntriesAssumeFlatDIT.isPresent(), this.addEntriesOutsideSplitBaseDNToAllSets.isPresent(), this.addEntriesOutsideSplitBaseDNToDedicatedSet.isPresent());
        }
        else {
            if (!this.splitUsingFilter.isPresent()) {
                this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_CANNOT_DETERMINE_SPLIT_ALGORITHM.get(this.splitUsingHashOnRDN.getPrimaryName() + ", " + this.splitUsingHashOnAttribute.getPrimaryName() + ", " + this.splitUsingFewestEntries.getPrimaryName() + ", " + this.splitUsingFilter.getPrimaryName()));
                return ResultCode.PARAM_ERROR;
            }
            final List<Filter> filterList = this.splitUsingFilterFilter.getValues();
            final LinkedHashSet<Filter> filterSet = new LinkedHashSet<Filter>(StaticUtils.computeMapCapacity(filterList.size()));
            for (final Filter f : filterList) {
                filterSet.add(f);
            }
            translator = new SplitLDIFFilterTranslator(this.splitBaseDN.getValue(), schema, filterSet, this.splitUsingFilterAssumeFlatDIT.isPresent(), this.addEntriesOutsideSplitBaseDNToAllSets.isPresent(), this.addEntriesOutsideSplitBaseDNToDedicatedSet.isPresent());
        }
        LDIFReader ldifReader;
        try {
            InputStream inputStream;
            if (this.sourceLDIF.isPresent()) {
                final ObjectPair<InputStream, String> p = ToolUtils.getInputStreamForLDIFFiles(this.sourceLDIF.getValues(), encryptionPassphrase, this.getOut(), this.getErr());
                inputStream = p.getFirst();
                if (encryptionPassphrase == null && p.getSecond() != null) {
                    encryptionPassphrase = p.getSecond();
                }
            }
            else {
                inputStream = System.in;
            }
            ldifReader = new LDIFReader(inputStream, this.numThreads.getValue(), translator);
            if (schema != null) {
                ldifReader.setSchema(schema);
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_ERROR_CREATING_LDIF_READER.get(StaticUtils.getExceptionMessage(e2)));
            return ResultCode.LOCAL_ERROR;
        }
        ResultCode resultCode = ResultCode.SUCCESS;
        final LinkedHashMap<String, OutputStream> outputStreams = new LinkedHashMap<String, OutputStream>(StaticUtils.computeMapCapacity(10));
        try {
            final AtomicLong entriesRead = new AtomicLong(0L);
            final AtomicLong entriesExcluded = new AtomicLong(0L);
            final TreeMap<String, AtomicLong> fileCounts = new TreeMap<String, AtomicLong>();
        Label_2099:
            while (true) {
                SplitLDIFEntry entry;
                try {
                    entry = (SplitLDIFEntry)ldifReader.readEntry();
                }
                catch (final LDIFException le2) {
                    Debug.debugException(le2);
                    resultCode = ResultCode.LOCAL_ERROR;
                    final File f2 = this.getOutputFile(".errors");
                    OutputStream s = outputStreams.get(".errors");
                    if (s == null) {
                        try {
                            s = new FileOutputStream(f2);
                            if (this.encryptTarget.isPresent()) {
                                if (encryptionPassphrase == null) {
                                    try {
                                        encryptionPassphrase = ToolUtils.promptForEncryptionPassphrase(false, true, this.getOut(), this.getErr());
                                    }
                                    catch (final LDAPException ex) {
                                        Debug.debugException(ex);
                                        this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ex.getMessage());
                                        return ex.getResultCode();
                                    }
                                }
                                s = new PassphraseEncryptedOutputStream(encryptionPassphrase, s);
                            }
                            if (this.compressTarget.isPresent()) {
                                s = new GZIPOutputStream(s);
                            }
                            outputStreams.put(".errors", s);
                            fileCounts.put(".errors", new AtomicLong(0L));
                        }
                        catch (final Exception e3) {
                            Debug.debugException(e3);
                            resultCode = ResultCode.LOCAL_ERROR;
                            this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_CANNOT_OPEN_OUTPUT_FILE.get(f2.getAbsolutePath(), StaticUtils.getExceptionMessage(e3)));
                            break;
                        }
                    }
                    final ByteStringBuffer buffer = new ByteStringBuffer();
                    buffer.append((CharSequence)"# ");
                    buffer.append((CharSequence)le2.getMessage());
                    buffer.append(StaticUtils.EOL_BYTES);
                    final List<String> dataLines = le2.getDataLines();
                    if (dataLines != null) {
                        for (final String dataLine : dataLines) {
                            buffer.append((CharSequence)dataLine);
                            buffer.append(StaticUtils.EOL_BYTES);
                        }
                    }
                    buffer.append(StaticUtils.EOL_BYTES);
                    try {
                        s.write(buffer.toByteArray());
                    }
                    catch (final Exception e4) {
                        Debug.debugException(e4);
                        resultCode = ResultCode.LOCAL_ERROR;
                        this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_ERROR_WRITING_ERROR_TO_FILE.get(le2.getMessage(), f2.getAbsolutePath(), StaticUtils.getExceptionMessage(e4)));
                        break;
                    }
                    if (le2.mayContinueReading()) {
                        this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_INVALID_LDIF_RECORD_RECOVERABLE.get(StaticUtils.getExceptionMessage(le2)));
                        continue;
                    }
                    this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_INVALID_LDIF_RECORD_UNRECOVERABLE.get(StaticUtils.getExceptionMessage(le2)));
                    break;
                }
                catch (final IOException ioe) {
                    Debug.debugException(ioe);
                    resultCode = ResultCode.LOCAL_ERROR;
                    this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_IO_READ_ERROR.get(StaticUtils.getExceptionMessage(ioe)));
                    break;
                }
                catch (final Exception e5) {
                    Debug.debugException(e5);
                    resultCode = ResultCode.LOCAL_ERROR;
                    this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_UNEXPECTED_READ_ERROR.get(StaticUtils.getExceptionMessage(e5)));
                    break;
                }
                if (entry == null) {
                    break;
                }
                final long readCount = entriesRead.incrementAndGet();
                if (readCount % 1000L == 0L) {
                    this.wrapOut(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.INFO_SPLIT_LDIF_PROGRESS.get(readCount));
                }
                Set<String> sets = entry.getSets();
                byte[] ldifBytes = entry.getLDIFBytes();
                if (sets == null) {
                    try {
                        sets = translator.translate((Entry)entry, 0L).getSets();
                    }
                    catch (final Exception e6) {
                        Debug.debugException(e6);
                    }
                    if (sets == null) {
                        final SplitLDIFEntry errorEntry = translator.createEntry(entry, ToolMessages.ERR_SPLIT_LDIF_ENTRY_WITHOUT_PARENT.get(entry.getDN(), this.splitBaseDN.getStringValue()), Collections.singleton(".errors"));
                        ldifBytes = errorEntry.getLDIFBytes();
                        sets = errorEntry.getSets();
                    }
                }
                if (sets.isEmpty()) {
                    entriesExcluded.incrementAndGet();
                }
                else {
                    for (final String set : sets) {
                        if (set.equals(".errors")) {
                            resultCode = ResultCode.LOCAL_ERROR;
                        }
                        final File f3 = this.getOutputFile(set);
                        OutputStream s2 = outputStreams.get(set);
                        if (s2 == null) {
                            try {
                                s2 = new FileOutputStream(f3);
                                if (this.encryptTarget.isPresent()) {
                                    if (encryptionPassphrase == null) {
                                        try {
                                            encryptionPassphrase = ToolUtils.promptForEncryptionPassphrase(false, true, this.getOut(), this.getErr());
                                        }
                                        catch (final LDAPException ex2) {
                                            Debug.debugException(ex2);
                                            this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ex2.getMessage());
                                            return ex2.getResultCode();
                                        }
                                    }
                                    s2 = new PassphraseEncryptedOutputStream(encryptionPassphrase, s2);
                                }
                                if (this.compressTarget.isPresent()) {
                                    s2 = new GZIPOutputStream(s2);
                                }
                                outputStreams.put(set, s2);
                                fileCounts.put(set, new AtomicLong(0L));
                            }
                            catch (final Exception e7) {
                                Debug.debugException(e7);
                                resultCode = ResultCode.LOCAL_ERROR;
                                this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_CANNOT_OPEN_OUTPUT_FILE.get(f3.getAbsolutePath(), StaticUtils.getExceptionMessage(e7)));
                                break Label_2099;
                            }
                        }
                        try {
                            s2.write(ldifBytes);
                        }
                        catch (final Exception e7) {
                            Debug.debugException(e7);
                            resultCode = ResultCode.LOCAL_ERROR;
                            this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_ERROR_WRITING_TO_FILE.get(entry.getDN(), f3.getAbsolutePath(), StaticUtils.getExceptionMessage(e7)));
                            break Label_2099;
                        }
                        fileCounts.get(set).incrementAndGet();
                    }
                }
            }
            final long finalReadCount = entriesRead.get();
            if (finalReadCount > 1000L) {
                this.out(new Object[0]);
            }
            this.wrapOut(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.INFO_SPLIT_LDIF_PROCESSING_COMPLETE.get(finalReadCount));
            final long excludedCount = entriesExcluded.get();
            if (excludedCount > 0L) {
                this.wrapOut(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.INFO_SPLIT_LDIF_EXCLUDED_COUNT.get(excludedCount));
            }
            for (final Map.Entry<String, AtomicLong> e8 : fileCounts.entrySet()) {
                final File f4 = this.getOutputFile(e8.getKey());
                this.wrapOut(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.INFO_SPLIT_LDIF_COUNT_TO_FILE.get(e8.getValue().get(), f4.getName()));
            }
        }
        finally {
            try {
                ldifReader.close();
            }
            catch (final Exception e9) {
                Debug.debugException(e9);
            }
            for (final Map.Entry<String, OutputStream> e10 : outputStreams.entrySet()) {
                try {
                    e10.getValue().close();
                }
                catch (final Exception ex3) {
                    Debug.debugException(ex3);
                    resultCode = ResultCode.LOCAL_ERROR;
                    this.wrapErr(0, SplitLDIF.MAX_OUTPUT_LINE_LENGTH, ToolMessages.ERR_SPLIT_LDIF_ERROR_CLOSING_FILE.get(this.getOutputFile(e10.getKey()), StaticUtils.getExceptionMessage(ex3)));
                }
            }
        }
        return resultCode;
    }
    
    private Schema getSchema() throws LDAPException {
        if (this.schemaPath.isPresent()) {
            final ArrayList<File> schemaFiles = new ArrayList<File>(10);
            for (final File path : this.schemaPath.getValues()) {
                if (path.isFile()) {
                    schemaFiles.add(path);
                }
                else {
                    final TreeMap<String, File> fileMap = new TreeMap<String, File>();
                    for (final File schemaDirFile : path.listFiles()) {
                        final String name = schemaDirFile.getName();
                        if (schemaDirFile.isFile() && name.toLowerCase().endsWith(".ldif")) {
                            fileMap.put(name, schemaDirFile);
                        }
                    }
                    schemaFiles.addAll(fileMap.values());
                }
            }
            if (schemaFiles.isEmpty()) {
                throw new LDAPException(ResultCode.PARAM_ERROR, ToolMessages.ERR_SPLIT_LDIF_NO_SCHEMA_FILES.get(this.schemaPath.getIdentifierString()));
            }
            try {
                return Schema.getSchema(schemaFiles);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, ToolMessages.ERR_SPLIT_LDIF_ERROR_LOADING_SCHEMA.get(StaticUtils.getExceptionMessage(e)));
            }
        }
        try {
            final String instanceRootStr = StaticUtils.getEnvironmentVariable("INSTANCE_ROOT");
            if (instanceRootStr != null) {
                final File instanceRoot = new File(instanceRootStr);
                final File configDir = new File(instanceRoot, "config");
                final File schemaDir = new File(configDir, "schema");
                if (schemaDir.exists()) {
                    final TreeMap<String, File> fileMap2 = new TreeMap<String, File>();
                    for (final File schemaDirFile2 : schemaDir.listFiles()) {
                        final String name2 = schemaDirFile2.getName();
                        if (schemaDirFile2.isFile() && name2.toLowerCase().endsWith(".ldif")) {
                            fileMap2.put(name2, schemaDirFile2);
                        }
                    }
                    if (!fileMap2.isEmpty()) {
                        return Schema.getSchema(new ArrayList<File>(fileMap2.values()));
                    }
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
        }
        return null;
    }
    
    private File getOutputFile(final String extension) {
        File baseFile;
        if (this.targetLDIFBasePath.isPresent()) {
            baseFile = this.targetLDIFBasePath.getValue();
        }
        else {
            baseFile = this.sourceLDIF.getValue();
        }
        return new File(baseFile.getAbsolutePath() + extension);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(4));
        for (final Map.Entry<String[], String> e : this.splitUsingHashOnRDN.getExampleUsages().entrySet()) {
            exampleMap.put(e.getKey(), e.getValue());
        }
        for (final Map.Entry<String[], String> e : this.splitUsingHashOnAttribute.getExampleUsages().entrySet()) {
            exampleMap.put(e.getKey(), e.getValue());
        }
        for (final Map.Entry<String[], String> e : this.splitUsingFewestEntries.getExampleUsages().entrySet()) {
            exampleMap.put(e.getKey(), e.getValue());
        }
        for (final Map.Entry<String[], String> e : this.splitUsingFilter.getExampleUsages().entrySet()) {
            exampleMap.put(e.getKey(), e.getValue());
        }
        return exampleMap;
    }
    
    static {
        MAX_OUTPUT_LINE_LENGTH = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
