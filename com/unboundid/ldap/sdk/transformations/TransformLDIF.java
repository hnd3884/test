package com.unboundid.ldap.sdk.transformations;

import com.unboundid.ldap.sdk.Entry;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.EnumSet;
import com.unboundid.ldap.sdk.ChangeType;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Attribute;
import java.util.Iterator;
import java.util.TreeMap;
import com.unboundid.ldif.LDIFRecord;
import com.unboundid.util.ObjectPair;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFException;
import java.util.zip.GZIPOutputStream;
import com.unboundid.util.PassphraseEncryptedOutputStream;
import java.io.FileOutputStream;
import com.unboundid.util.Debug;
import com.unboundid.ldif.LDIFReader;
import java.io.InputStream;
import java.io.File;
import com.unboundid.ldif.AggregateLDIFReaderChangeRecordTranslator;
import java.util.Collection;
import com.unboundid.ldif.AggregateLDIFReaderEntryTranslator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.ldif.LDIFReaderChangeRecordTranslator;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.ByteStringBuffer;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.ScopeArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldif.LDIFReaderEntryTranslator;
import com.unboundid.util.CommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class TransformLDIF extends CommandLineTool implements LDIFReaderEntryTranslator
{
    private static final int MAX_OUTPUT_LINE_LENGTH;
    private BooleanArgument addToExistingValues;
    private BooleanArgument appendToTargetLDIF;
    private BooleanArgument compressTarget;
    private BooleanArgument encryptTarget;
    private BooleanArgument excludeRecordsWithoutChangeType;
    private BooleanArgument excludeNonMatchingEntries;
    private BooleanArgument flattenAddOmittedRDNAttributesToEntry;
    private BooleanArgument flattenAddOmittedRDNAttributesToRDN;
    private BooleanArgument hideRedactedValueCount;
    private BooleanArgument processDNs;
    private BooleanArgument sourceCompressed;
    private BooleanArgument sourceContainsChangeRecords;
    private BooleanArgument sourceFromStandardInput;
    private BooleanArgument targetToStandardOutput;
    private DNArgument addAttributeBaseDN;
    private DNArgument excludeEntryBaseDN;
    private DNArgument flattenBaseDN;
    private DNArgument moveSubtreeFrom;
    private DNArgument moveSubtreeTo;
    private FileArgument encryptionPassphraseFile;
    private FileArgument schemaPath;
    private FileArgument sourceLDIF;
    private FileArgument targetLDIF;
    private FilterArgument addAttributeFilter;
    private FilterArgument excludeEntryFilter;
    private FilterArgument flattenExcludeFilter;
    private IntegerArgument initialSequentialValue;
    private IntegerArgument numThreads;
    private IntegerArgument randomSeed;
    private IntegerArgument sequentialValueIncrement;
    private IntegerArgument wrapColumn;
    private ScopeArgument addAttributeScope;
    private ScopeArgument excludeEntryScope;
    private StringArgument addAttributeName;
    private StringArgument addAttributeValue;
    private StringArgument excludeAttribute;
    private StringArgument excludeChangeType;
    private StringArgument redactAttribute;
    private StringArgument renameAttributeFrom;
    private StringArgument renameAttributeTo;
    private StringArgument replaceValuesAttribute;
    private StringArgument replacementValue;
    private StringArgument scrambleAttribute;
    private StringArgument scrambleJSONField;
    private StringArgument sequentialAttribute;
    private StringArgument textAfterSequentialValue;
    private StringArgument textBeforeSequentialValue;
    private final ThreadLocal<ByteStringBuffer> byteStringBuffers;
    
    public static void main(final String... args) {
        final ResultCode resultCode = main(System.out, System.err, args);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final OutputStream out, final OutputStream err, final String... args) {
        final TransformLDIF tool = new TransformLDIF(out, err);
        return tool.runTool(args);
    }
    
    public TransformLDIF(final OutputStream out, final OutputStream err) {
        super(out, err);
        this.addToExistingValues = null;
        this.appendToTargetLDIF = null;
        this.compressTarget = null;
        this.encryptTarget = null;
        this.excludeRecordsWithoutChangeType = null;
        this.excludeNonMatchingEntries = null;
        this.flattenAddOmittedRDNAttributesToEntry = null;
        this.flattenAddOmittedRDNAttributesToRDN = null;
        this.hideRedactedValueCount = null;
        this.processDNs = null;
        this.sourceCompressed = null;
        this.sourceContainsChangeRecords = null;
        this.sourceFromStandardInput = null;
        this.targetToStandardOutput = null;
        this.addAttributeBaseDN = null;
        this.excludeEntryBaseDN = null;
        this.flattenBaseDN = null;
        this.moveSubtreeFrom = null;
        this.moveSubtreeTo = null;
        this.encryptionPassphraseFile = null;
        this.schemaPath = null;
        this.sourceLDIF = null;
        this.targetLDIF = null;
        this.addAttributeFilter = null;
        this.excludeEntryFilter = null;
        this.flattenExcludeFilter = null;
        this.initialSequentialValue = null;
        this.numThreads = null;
        this.randomSeed = null;
        this.sequentialValueIncrement = null;
        this.wrapColumn = null;
        this.addAttributeScope = null;
        this.excludeEntryScope = null;
        this.addAttributeName = null;
        this.addAttributeValue = null;
        this.excludeAttribute = null;
        this.excludeChangeType = null;
        this.redactAttribute = null;
        this.renameAttributeFrom = null;
        this.renameAttributeTo = null;
        this.replaceValuesAttribute = null;
        this.replacementValue = null;
        this.scrambleAttribute = null;
        this.scrambleJSONField = null;
        this.sequentialAttribute = null;
        this.textAfterSequentialValue = null;
        this.textBeforeSequentialValue = null;
        this.byteStringBuffers = new ThreadLocal<ByteStringBuffer>();
    }
    
    @Override
    public String getToolName() {
        return "transform-ldif";
    }
    
    @Override
    public String getToolDescription() {
        return TransformationMessages.INFO_TRANSFORM_LDIF_TOOL_DESCRIPTION.get();
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
        (this.sourceLDIF = new FileArgument('l', "sourceLDIF", false, 0, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SOURCE_LDIF.get(), true, true, true, false)).addLongIdentifier("inputLDIF", true);
        this.sourceLDIF.addLongIdentifier("source-ldif", true);
        this.sourceLDIF.addLongIdentifier("input-ldif", true);
        this.sourceLDIF.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.sourceLDIF);
        (this.sourceFromStandardInput = new BooleanArgument(null, "sourceFromStandardInput", 1, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SOURCE_STD_IN.get())).addLongIdentifier("source-from-standard-input", true);
        this.sourceFromStandardInput.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.sourceFromStandardInput);
        parser.addRequiredArgumentSet(this.sourceLDIF, this.sourceFromStandardInput, new Argument[0]);
        parser.addExclusiveArgumentSet(this.sourceLDIF, this.sourceFromStandardInput, new Argument[0]);
        (this.targetLDIF = new FileArgument('o', "targetLDIF", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_TARGET_LDIF.get(), false, true, true, false)).addLongIdentifier("outputLDIF", true);
        this.targetLDIF.addLongIdentifier("target-ldif", true);
        this.targetLDIF.addLongIdentifier("output-ldif", true);
        this.targetLDIF.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.targetLDIF);
        (this.targetToStandardOutput = new BooleanArgument(null, "targetToStandardOutput", 1, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_TARGET_STD_OUT.get())).addLongIdentifier("target-to-standard-output", true);
        this.targetToStandardOutput.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.targetToStandardOutput);
        parser.addExclusiveArgumentSet(this.targetLDIF, this.targetToStandardOutput, new Argument[0]);
        (this.sourceContainsChangeRecords = new BooleanArgument(null, "sourceContainsChangeRecords", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SOURCE_CONTAINS_CHANGE_RECORDS.get())).addLongIdentifier("source-contains-change-records", true);
        this.sourceContainsChangeRecords.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.sourceContainsChangeRecords);
        (this.appendToTargetLDIF = new BooleanArgument(null, "appendToTargetLDIF", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_APPEND_TO_TARGET.get())).addLongIdentifier("append-to-target-ldif", true);
        this.appendToTargetLDIF.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.appendToTargetLDIF);
        parser.addExclusiveArgumentSet(this.targetToStandardOutput, this.appendToTargetLDIF, new Argument[0]);
        (this.wrapColumn = new IntegerArgument(null, "wrapColumn", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_WRAP_COLUMN.get(), 5, Integer.MAX_VALUE)).addLongIdentifier("wrap-column", true);
        this.wrapColumn.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.wrapColumn);
        (this.sourceCompressed = new BooleanArgument('C', "sourceCompressed", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SOURCE_COMPRESSED.get())).addLongIdentifier("inputCompressed", true);
        this.sourceCompressed.addLongIdentifier("source-compressed", true);
        this.sourceCompressed.addLongIdentifier("input-compressed", true);
        this.sourceCompressed.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.sourceCompressed);
        (this.compressTarget = new BooleanArgument('c', "compressTarget", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_COMPRESS_TARGET.get())).addLongIdentifier("compressOutput", true);
        this.compressTarget.addLongIdentifier("compress", true);
        this.compressTarget.addLongIdentifier("compress-target", true);
        this.compressTarget.addLongIdentifier("compress-output", true);
        this.compressTarget.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.compressTarget);
        (this.encryptTarget = new BooleanArgument(null, "encryptTarget", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ENCRYPT_TARGET.get())).addLongIdentifier("encryptOutput", true);
        this.encryptTarget.addLongIdentifier("encrypt", true);
        this.encryptTarget.addLongIdentifier("encrypt-target", true);
        this.encryptTarget.addLongIdentifier("encrypt-output", true);
        this.encryptTarget.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.encryptTarget);
        (this.encryptionPassphraseFile = new FileArgument(null, "encryptionPassphraseFile", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ENCRYPTION_PW_FILE.get(), true, true, true, false)).addLongIdentifier("encryptionPasswordFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-passphrase-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-password-file", true);
        this.encryptionPassphraseFile.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_LDIF.get());
        parser.addArgument(this.encryptionPassphraseFile);
        (this.scrambleAttribute = new StringArgument('a', "scrambleAttribute", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SCRAMBLE_ATTR.get())).addLongIdentifier("attributeName", true);
        this.scrambleAttribute.addLongIdentifier("scramble-attribute", true);
        this.scrambleAttribute.addLongIdentifier("attribute-name", true);
        this.scrambleAttribute.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SCRAMBLE.get());
        parser.addArgument(this.scrambleAttribute);
        (this.scrambleJSONField = new StringArgument(null, "scrambleJSONField", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_FIELD_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SCRAMBLE_JSON_FIELD.get(this.scrambleAttribute.getIdentifierString()))).addLongIdentifier("scramble-json-field", true);
        this.scrambleJSONField.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SCRAMBLE.get());
        parser.addArgument(this.scrambleJSONField);
        parser.addDependentArgumentSet(this.scrambleJSONField, this.scrambleAttribute, new Argument[0]);
        (this.randomSeed = new IntegerArgument('s', "randomSeed", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_RANDOM_SEED.get())).addLongIdentifier("random-seed", true);
        this.randomSeed.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SCRAMBLE.get());
        parser.addArgument(this.randomSeed);
        (this.sequentialAttribute = new StringArgument('S', "sequentialAttribute", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SEQUENTIAL_ATTR.get(this.sourceContainsChangeRecords.getIdentifierString()))).addLongIdentifier("sequentialAttributeName", true);
        this.sequentialAttribute.addLongIdentifier("sequential-attribute", true);
        this.sequentialAttribute.addLongIdentifier("sequential-attribute-name", true);
        this.sequentialAttribute.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SEQUENTIAL.get());
        parser.addArgument(this.sequentialAttribute);
        parser.addExclusiveArgumentSet(this.sourceContainsChangeRecords, this.sequentialAttribute, new Argument[0]);
        (this.initialSequentialValue = new IntegerArgument('i', "initialSequentialValue", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_INITIAL_SEQUENTIAL_VALUE.get(this.sequentialAttribute.getIdentifierString()))).addLongIdentifier("initial-sequential-value", true);
        this.initialSequentialValue.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SEQUENTIAL.get());
        parser.addArgument(this.initialSequentialValue);
        parser.addDependentArgumentSet(this.initialSequentialValue, this.sequentialAttribute, new Argument[0]);
        (this.sequentialValueIncrement = new IntegerArgument(null, "sequentialValueIncrement", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SEQUENTIAL_INCREMENT.get(this.sequentialAttribute.getIdentifierString()))).addLongIdentifier("sequential-value-increment", true);
        this.sequentialValueIncrement.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SEQUENTIAL.get());
        parser.addArgument(this.sequentialValueIncrement);
        parser.addDependentArgumentSet(this.sequentialValueIncrement, this.sequentialAttribute, new Argument[0]);
        (this.textBeforeSequentialValue = new StringArgument(null, "textBeforeSequentialValue", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SEQUENTIAL_TEXT_BEFORE.get(this.sequentialAttribute.getIdentifierString()))).addLongIdentifier("text-before-sequential-value", true);
        this.textBeforeSequentialValue.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SEQUENTIAL.get());
        parser.addArgument(this.textBeforeSequentialValue);
        parser.addDependentArgumentSet(this.textBeforeSequentialValue, this.sequentialAttribute, new Argument[0]);
        (this.textAfterSequentialValue = new StringArgument(null, "textAfterSequentialValue", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SEQUENTIAL_TEXT_AFTER.get(this.sequentialAttribute.getIdentifierString()))).addLongIdentifier("text-after-sequential-value", true);
        this.textAfterSequentialValue.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_SEQUENTIAL.get());
        parser.addArgument(this.textAfterSequentialValue);
        parser.addDependentArgumentSet(this.textAfterSequentialValue, this.sequentialAttribute, new Argument[0]);
        (this.replaceValuesAttribute = new StringArgument(null, "replaceValuesAttribute", false, 1, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_REPLACE_VALUES_ATTR.get(this.sourceContainsChangeRecords.getIdentifierString()))).addLongIdentifier("replace-values-attribute", true);
        this.replaceValuesAttribute.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_REPLACE_VALUES.get());
        parser.addArgument(this.replaceValuesAttribute);
        parser.addExclusiveArgumentSet(this.sourceContainsChangeRecords, this.replaceValuesAttribute, new Argument[0]);
        (this.replacementValue = new StringArgument(null, "replacementValue", false, 0, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_REPLACEMENT_VALUE.get(this.replaceValuesAttribute.getIdentifierString()))).addLongIdentifier("replacement-value", true);
        this.replacementValue.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_REPLACE_VALUES.get());
        parser.addArgument(this.replacementValue);
        parser.addDependentArgumentSet(this.replaceValuesAttribute, this.replacementValue, new Argument[0]);
        parser.addDependentArgumentSet(this.replacementValue, this.replaceValuesAttribute, new Argument[0]);
        (this.addAttributeName = new StringArgument(null, "addAttributeName", false, 1, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ADD_ATTR.get("--addAttributeValue", this.sourceContainsChangeRecords.getIdentifierString()))).addLongIdentifier("add-attribute-name", true);
        this.addAttributeName.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_ADD_ATTR.get());
        parser.addArgument(this.addAttributeName);
        parser.addExclusiveArgumentSet(this.sourceContainsChangeRecords, this.addAttributeName, new Argument[0]);
        (this.addAttributeValue = new StringArgument(null, "addAttributeValue", false, 0, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ADD_VALUE.get(this.addAttributeName.getIdentifierString()))).addLongIdentifier("add-attribute-value", true);
        this.addAttributeValue.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_ADD_ATTR.get());
        parser.addArgument(this.addAttributeValue);
        parser.addDependentArgumentSet(this.addAttributeName, this.addAttributeValue, new Argument[0]);
        parser.addDependentArgumentSet(this.addAttributeValue, this.addAttributeName, new Argument[0]);
        (this.addToExistingValues = new BooleanArgument(null, "addToExistingValues", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ADD_MERGE_VALUES.get(this.addAttributeName.getIdentifierString(), this.addAttributeValue.getIdentifierString()))).addLongIdentifier("add-to-existing-values", true);
        this.addToExistingValues.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_ADD_ATTR.get());
        parser.addArgument(this.addToExistingValues);
        parser.addDependentArgumentSet(this.addToExistingValues, this.addAttributeName, new Argument[0]);
        (this.addAttributeBaseDN = new DNArgument(null, "addAttributeBaseDN", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ADD_BASE_DN.get(this.addAttributeName.getIdentifierString()))).addLongIdentifier("add-attribute-base-dn", true);
        this.addAttributeBaseDN.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_ADD_ATTR.get());
        parser.addArgument(this.addAttributeBaseDN);
        parser.addDependentArgumentSet(this.addAttributeBaseDN, this.addAttributeName, new Argument[0]);
        (this.addAttributeScope = new ScopeArgument(null, "addAttributeScope", false, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ADD_SCOPE.get(this.addAttributeBaseDN.getIdentifierString(), this.addAttributeName.getIdentifierString()))).addLongIdentifier("add-attribute-scope", true);
        this.addAttributeScope.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_ADD_ATTR.get());
        parser.addArgument(this.addAttributeScope);
        parser.addDependentArgumentSet(this.addAttributeScope, this.addAttributeName, new Argument[0]);
        (this.addAttributeFilter = new FilterArgument(null, "addAttributeFilter", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_ADD_FILTER.get(this.addAttributeName.getIdentifierString()))).addLongIdentifier("add-attribute-filter", true);
        this.addAttributeFilter.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_ADD_ATTR.get());
        parser.addArgument(this.addAttributeFilter);
        parser.addDependentArgumentSet(this.addAttributeFilter, this.addAttributeName, new Argument[0]);
        (this.renameAttributeFrom = new StringArgument(null, "renameAttributeFrom", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_RENAME_FROM.get())).addLongIdentifier("rename-attribute-from", true);
        this.renameAttributeFrom.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_RENAME.get());
        parser.addArgument(this.renameAttributeFrom);
        (this.renameAttributeTo = new StringArgument(null, "renameAttributeTo", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_RENAME_TO.get(this.renameAttributeFrom.getIdentifierString()))).addLongIdentifier("rename-attribute-to", true);
        this.renameAttributeTo.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_RENAME.get());
        parser.addArgument(this.renameAttributeTo);
        parser.addDependentArgumentSet(this.renameAttributeFrom, this.renameAttributeTo, new Argument[0]);
        parser.addDependentArgumentSet(this.renameAttributeTo, this.renameAttributeFrom, new Argument[0]);
        (this.flattenBaseDN = new DNArgument(null, "flattenBaseDN", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_FLATTEN_BASE_DN.get())).addLongIdentifier("flatten-base-dn", true);
        this.flattenBaseDN.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_FLATTEN.get());
        parser.addArgument(this.flattenBaseDN);
        parser.addExclusiveArgumentSet(this.sourceContainsChangeRecords, this.flattenBaseDN, new Argument[0]);
        (this.flattenAddOmittedRDNAttributesToEntry = new BooleanArgument(null, "flattenAddOmittedRDNAttributesToEntry", 1, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_FLATTEN_ADD_OMITTED_TO_ENTRY.get())).addLongIdentifier("flatten-add-omitted-rdn-attributes-to-entry", true);
        this.flattenAddOmittedRDNAttributesToEntry.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_FLATTEN.get());
        parser.addArgument(this.flattenAddOmittedRDNAttributesToEntry);
        parser.addDependentArgumentSet(this.flattenAddOmittedRDNAttributesToEntry, this.flattenBaseDN, new Argument[0]);
        (this.flattenAddOmittedRDNAttributesToRDN = new BooleanArgument(null, "flattenAddOmittedRDNAttributesToRDN", 1, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_FLATTEN_ADD_OMITTED_TO_RDN.get())).addLongIdentifier("flatten-add-omitted-rdn-attributes-to-rdn", true);
        this.flattenAddOmittedRDNAttributesToRDN.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_FLATTEN.get());
        parser.addArgument(this.flattenAddOmittedRDNAttributesToRDN);
        parser.addDependentArgumentSet(this.flattenAddOmittedRDNAttributesToRDN, this.flattenBaseDN, new Argument[0]);
        (this.flattenExcludeFilter = new FilterArgument(null, "flattenExcludeFilter", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_FLATTEN_EXCLUDE_FILTER.get())).addLongIdentifier("flatten-exclude-filter", true);
        this.flattenExcludeFilter.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_FLATTEN.get());
        parser.addArgument(this.flattenExcludeFilter);
        parser.addDependentArgumentSet(this.flattenExcludeFilter, this.flattenBaseDN, new Argument[0]);
        (this.moveSubtreeFrom = new DNArgument(null, "moveSubtreeFrom", false, 0, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_MOVE_SUBTREE_FROM.get())).addLongIdentifier("move-subtree-from", true);
        this.moveSubtreeFrom.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_MOVE.get());
        parser.addArgument(this.moveSubtreeFrom);
        (this.moveSubtreeTo = new DNArgument(null, "moveSubtreeTo", false, 0, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_MOVE_SUBTREE_TO.get(this.moveSubtreeFrom.getIdentifierString()))).addLongIdentifier("move-subtree-to", true);
        this.moveSubtreeTo.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_MOVE.get());
        parser.addArgument(this.moveSubtreeTo);
        parser.addDependentArgumentSet(this.moveSubtreeFrom, this.moveSubtreeTo, new Argument[0]);
        parser.addDependentArgumentSet(this.moveSubtreeTo, this.moveSubtreeFrom, new Argument[0]);
        (this.redactAttribute = new StringArgument(null, "redactAttribute", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_REDACT_ATTR.get())).addLongIdentifier("redact-attribute", true);
        this.redactAttribute.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_REDACT.get());
        parser.addArgument(this.redactAttribute);
        (this.hideRedactedValueCount = new BooleanArgument(null, "hideRedactedValueCount", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_HIDE_REDACTED_COUNT.get())).addLongIdentifier("hide-redacted-value-count", true);
        this.hideRedactedValueCount.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_REDACT.get());
        parser.addArgument(this.hideRedactedValueCount);
        parser.addDependentArgumentSet(this.hideRedactedValueCount, this.redactAttribute, new Argument[0]);
        (this.excludeAttribute = new StringArgument(null, "excludeAttribute", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_ATTR_NAME.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_EXCLUDE_ATTR.get())).addLongIdentifier("suppressAttribute", true);
        this.excludeAttribute.addLongIdentifier("exclude-attribute", true);
        this.excludeAttribute.addLongIdentifier("suppress-attribute", true);
        this.excludeAttribute.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_EXCLUDE.get());
        parser.addArgument(this.excludeAttribute);
        (this.excludeEntryBaseDN = new DNArgument(null, "excludeEntryBaseDN", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_EXCLUDE_ENTRY_BASE_DN.get(this.sourceContainsChangeRecords.getIdentifierString()))).addLongIdentifier("suppressEntryBaseDN", true);
        this.excludeEntryBaseDN.addLongIdentifier("exclude-entry-base-dn", true);
        this.excludeEntryBaseDN.addLongIdentifier("suppress-entry-base-dn", true);
        this.excludeEntryBaseDN.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_EXCLUDE.get());
        parser.addArgument(this.excludeEntryBaseDN);
        parser.addExclusiveArgumentSet(this.sourceContainsChangeRecords, this.excludeEntryBaseDN, new Argument[0]);
        (this.excludeEntryScope = new ScopeArgument(null, "excludeEntryScope", false, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_EXCLUDE_ENTRY_SCOPE.get(this.sourceContainsChangeRecords.getIdentifierString()))).addLongIdentifier("suppressEntryScope", true);
        this.excludeEntryScope.addLongIdentifier("exclude-entry-scope", true);
        this.excludeEntryScope.addLongIdentifier("suppress-entry-scope", true);
        this.excludeEntryScope.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_EXCLUDE.get());
        parser.addArgument(this.excludeEntryScope);
        parser.addExclusiveArgumentSet(this.sourceContainsChangeRecords, this.excludeEntryScope, new Argument[0]);
        (this.excludeEntryFilter = new FilterArgument(null, "excludeEntryFilter", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_EXCLUDE_ENTRY_FILTER.get(this.sourceContainsChangeRecords.getIdentifierString()))).addLongIdentifier("suppressEntryFilter", true);
        this.excludeEntryFilter.addLongIdentifier("exclude-entry-filter", true);
        this.excludeEntryFilter.addLongIdentifier("suppress-entry-filter", true);
        this.excludeEntryFilter.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_EXCLUDE.get());
        parser.addArgument(this.excludeEntryFilter);
        parser.addExclusiveArgumentSet(this.sourceContainsChangeRecords, this.excludeEntryFilter, new Argument[0]);
        (this.excludeNonMatchingEntries = new BooleanArgument(null, "excludeNonMatchingEntries", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_EXCLUDE_NON_MATCHING.get())).addLongIdentifier("exclude-non-matching-entries", true);
        this.excludeNonMatchingEntries.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_EXCLUDE.get());
        parser.addArgument(this.excludeNonMatchingEntries);
        parser.addDependentArgumentSet(this.excludeNonMatchingEntries, this.excludeEntryBaseDN, this.excludeEntryScope, this.excludeEntryFilter);
        (this.excludeChangeType = new StringArgument(null, "excludeChangeType", false, 0, TransformationMessages.INFO_TRANSFORM_LDIF_PLACEHOLDER_CHANGE_TYPES.get(), TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_EXCLUDE_CHANGE_TYPE.get(), StaticUtils.setOf("add", "delete", "modify", "moddn"))).addLongIdentifier("exclude-change-type", true);
        this.excludeChangeType.addLongIdentifier("exclude-changetype", true);
        this.excludeChangeType.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_EXCLUDE.get());
        parser.addArgument(this.excludeChangeType);
        (this.excludeRecordsWithoutChangeType = new BooleanArgument(null, "excludeRecordsWithoutChangeType", 1, TransformationMessages.INFO_TRANSFORM_LDIF_EXCLUDE_WITHOUT_CHANGETYPE.get())).addLongIdentifier("exclude-records-without-change-type", true);
        this.excludeRecordsWithoutChangeType.addLongIdentifier("exclude-records-without-changetype", true);
        this.excludeRecordsWithoutChangeType.setArgumentGroupName(TransformationMessages.INFO_TRANSFORM_LDIF_ARG_GROUP_EXCLUDE.get());
        parser.addArgument(this.excludeRecordsWithoutChangeType);
        (this.schemaPath = new FileArgument(null, "schemaPath", false, 0, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_SCHEMA_PATH.get(), true, true, false, false)).addLongIdentifier("schemaFile", true);
        this.schemaPath.addLongIdentifier("schemaDirectory", true);
        this.schemaPath.addLongIdentifier("schema-path", true);
        this.schemaPath.addLongIdentifier("schema-file", true);
        this.schemaPath.addLongIdentifier("schema-directory", true);
        parser.addArgument(this.schemaPath);
        (this.numThreads = new IntegerArgument('t', "numThreads", false, 1, null, TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_NUM_THREADS.get(), 1, Integer.MAX_VALUE, 1)).addLongIdentifier("num-threads", true);
        parser.addArgument(this.numThreads);
        (this.processDNs = new BooleanArgument('d', "processDNs", TransformationMessages.INFO_TRANSFORM_LDIF_ARG_DESC_PROCESS_DNS.get())).addLongIdentifier("process-dns", true);
        parser.addArgument(this.processDNs);
        parser.addRequiredArgumentSet(this.scrambleAttribute, this.sequentialAttribute, this.replaceValuesAttribute, this.addAttributeName, this.renameAttributeFrom, this.flattenBaseDN, this.moveSubtreeFrom, this.redactAttribute, this.excludeAttribute, this.excludeEntryBaseDN, this.excludeEntryScope, this.excludeEntryFilter, this.excludeChangeType, this.excludeRecordsWithoutChangeType);
    }
    
    @Override
    public void doExtendedArgumentValidation() throws ArgumentException {
        if (!this.targetLDIF.isPresent() && !this.targetToStandardOutput.isPresent() && !this.scrambleAttribute.isPresent() && !this.sequentialAttribute.isPresent()) {
            throw new ArgumentException(TransformationMessages.ERR_TRANSFORM_LDIF_MISSING_TARGET_ARG.get(this.targetLDIF.getIdentifierString(), this.targetToStandardOutput.getIdentifierString()));
        }
        final int renameFromOccurrences = this.renameAttributeFrom.getNumOccurrences();
        final int renameToOccurrences = this.renameAttributeTo.getNumOccurrences();
        if (renameFromOccurrences != renameToOccurrences) {
            throw new ArgumentException(TransformationMessages.ERR_TRANSFORM_LDIF_ARG_COUNT_MISMATCH.get(this.renameAttributeFrom.getIdentifierString(), this.renameAttributeTo.getIdentifierString()));
        }
        final int moveFromOccurrences = this.moveSubtreeFrom.getNumOccurrences();
        final int moveToOccurrences = this.moveSubtreeTo.getNumOccurrences();
        if (moveFromOccurrences != moveToOccurrences) {
            throw new ArgumentException(TransformationMessages.ERR_TRANSFORM_LDIF_ARG_COUNT_MISMATCH.get(this.moveSubtreeFrom.getIdentifierString(), this.moveSubtreeTo.getIdentifierString()));
        }
    }
    
    @Override
    public ResultCode doToolProcessing() {
        Schema schema;
        try {
            schema = this.getSchema();
        }
        catch (final LDAPException le) {
            this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, le.getMessage());
            return le.getResultCode();
        }
        String encryptionPassphrase = null;
        if (this.encryptionPassphraseFile.isPresent()) {
            try {
                encryptionPassphrase = ToolUtils.readEncryptionPassphraseFromFile(this.encryptionPassphraseFile.getValue());
            }
            catch (final LDAPException e) {
                this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, e.getMessage());
                return e.getResultCode();
            }
        }
        final ArrayList<LDIFReaderEntryTranslator> entryTranslators = new ArrayList<LDIFReaderEntryTranslator>(10);
        final ArrayList<LDIFReaderChangeRecordTranslator> changeRecordTranslators = new ArrayList<LDIFReaderChangeRecordTranslator>(10);
        final AtomicLong excludedEntryCount = new AtomicLong(0L);
        this.createTranslators(entryTranslators, changeRecordTranslators, schema, excludedEntryCount);
        final AggregateLDIFReaderEntryTranslator entryTranslator = new AggregateLDIFReaderEntryTranslator(entryTranslators);
        final AggregateLDIFReaderChangeRecordTranslator changeRecordTranslator = new AggregateLDIFReaderChangeRecordTranslator(changeRecordTranslators);
        File targetFile;
        if (this.targetLDIF.isPresent()) {
            targetFile = this.targetLDIF.getValue();
        }
        else if (this.targetToStandardOutput.isPresent()) {
            targetFile = null;
        }
        else {
            targetFile = new File(this.sourceLDIF.getValue().getAbsolutePath() + ".scrambled");
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
            ldifReader = new LDIFReader(inputStream, this.numThreads.getValue(), entryTranslator, changeRecordTranslator);
            if (schema != null) {
                ldifReader.setSchema(schema);
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.ERR_TRANSFORM_LDIF_ERROR_CREATING_LDIF_READER.get(StaticUtils.getExceptionMessage(e2)));
            return ResultCode.LOCAL_ERROR;
        }
        ResultCode resultCode = ResultCode.SUCCESS;
        OutputStream outputStream = null;
        try {
            try {
                if (targetFile == null) {
                    outputStream = this.getOut();
                }
                else {
                    outputStream = new FileOutputStream(targetFile, this.appendToTargetLDIF.isPresent());
                }
                if (this.encryptTarget.isPresent()) {
                    if (encryptionPassphrase == null) {
                        encryptionPassphrase = ToolUtils.promptForEncryptionPassphrase(false, true, this.getOut(), this.getErr());
                    }
                    outputStream = new PassphraseEncryptedOutputStream(encryptionPassphrase, outputStream);
                }
                if (this.compressTarget.isPresent()) {
                    outputStream = new GZIPOutputStream(outputStream);
                }
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
                this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.ERR_TRANSFORM_LDIF_ERROR_CREATING_OUTPUT_STREAM.get(targetFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e3)));
                resultCode = ResultCode.LOCAL_ERROR;
            }
            long entriesWritten = 0L;
            while (true) {
                LDIFRecord ldifRecord = null;
                try {
                    ldifRecord = ldifReader.readLDIFRecord();
                }
                catch (final LDIFException le2) {
                    Debug.debugException(le2);
                    if (le2.mayContinueReading()) {
                        this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.ERR_TRANSFORM_LDIF_RECOVERABLE_MALFORMED_RECORD.get(StaticUtils.getExceptionMessage(le2)));
                        if (resultCode != ResultCode.SUCCESS) {
                            continue;
                        }
                        resultCode = ResultCode.PARAM_ERROR;
                        continue;
                    }
                    this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.ERR_TRANSFORM_LDIF_UNRECOVERABLE_MALFORMED_RECORD.get(StaticUtils.getExceptionMessage(le2)));
                    if (resultCode == ResultCode.SUCCESS) {
                        resultCode = ResultCode.PARAM_ERROR;
                    }
                }
                catch (final Exception e4) {
                    Debug.debugException(e4);
                    this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.ERR_TRANSFORM_LDIF_UNEXPECTED_READ_ERROR.get(StaticUtils.getExceptionMessage(e4)));
                    resultCode = ResultCode.LOCAL_ERROR;
                }
                if (ldifRecord == null) {
                    break;
                }
                try {
                    if (ldifRecord instanceof PreEncodedLDIFEntry) {
                        outputStream.write(((PreEncodedLDIFEntry)ldifRecord).getLDIFBytes());
                    }
                    else {
                        final ByteStringBuffer buffer = this.getBuffer();
                        if (this.wrapColumn.isPresent()) {
                            ldifRecord.toLDIF(buffer, this.wrapColumn.getValue());
                        }
                        else {
                            ldifRecord.toLDIF(buffer, 0);
                        }
                        buffer.append(StaticUtils.EOL_BYTES);
                        buffer.write(outputStream);
                    }
                }
                catch (final Exception e4) {
                    Debug.debugException(e4);
                    this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.ERR_TRANSFORM_LDIF_WRITE_ERROR.get(targetFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e4)));
                    resultCode = ResultCode.LOCAL_ERROR;
                }
                ++entriesWritten;
                if (this.targetToStandardOutput.isPresent() || entriesWritten % 1000L != 0L) {
                    continue;
                }
                final long numExcluded = excludedEntryCount.get();
                if (numExcluded > 0L) {
                    this.wrapOut(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.INFO_TRANSFORM_LDIF_WROTE_ENTRIES_WITH_EXCLUDED.get(entriesWritten, numExcluded));
                }
                else {
                    this.wrapOut(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.INFO_TRANSFORM_LDIF_WROTE_ENTRIES_NONE_EXCLUDED.get(entriesWritten));
                }
            }
            if (!this.targetToStandardOutput.isPresent()) {
                final long numExcluded2 = excludedEntryCount.get();
                if (numExcluded2 > 0L) {
                    this.wrapOut(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.INFO_TRANSFORM_LDIF_COMPLETE_WITH_EXCLUDED.get(entriesWritten, numExcluded2));
                }
                else {
                    this.wrapOut(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.INFO_TRANSFORM_LDIF_COMPLETE_NONE_EXCLUDED.get(entriesWritten));
                }
            }
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (final Exception e5) {
                    Debug.debugException(e5);
                    this.wrapErr(0, TransformLDIF.MAX_OUTPUT_LINE_LENGTH, TransformationMessages.ERR_TRANSFORM_LDIF_ERROR_CLOSING_OUTPUT_STREAM.get(targetFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e5)));
                    if (resultCode == ResultCode.SUCCESS) {
                        resultCode = ResultCode.LOCAL_ERROR;
                    }
                }
            }
            try {
                ldifReader.close();
            }
            catch (final Exception e5) {
                Debug.debugException(e5);
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
                throw new LDAPException(ResultCode.PARAM_ERROR, TransformationMessages.ERR_TRANSFORM_LDIF_NO_SCHEMA_FILES.get(this.schemaPath.getIdentifierString()));
            }
            try {
                return Schema.getSchema(schemaFiles);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new LDAPException(ResultCode.LOCAL_ERROR, TransformationMessages.ERR_TRANSFORM_LDIF_ERROR_LOADING_SCHEMA.get(StaticUtils.getExceptionMessage(e)));
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
    
    private void createTranslators(final List<LDIFReaderEntryTranslator> entryTranslators, final List<LDIFReaderChangeRecordTranslator> changeRecordTranslators, final Schema schema, final AtomicLong excludedEntryCount) {
        if (this.scrambleAttribute.isPresent()) {
            Long seed;
            if (this.randomSeed.isPresent()) {
                seed = (long)this.randomSeed.getValue();
            }
            else {
                seed = null;
            }
            final ScrambleAttributeTransformation t = new ScrambleAttributeTransformation(schema, seed, this.processDNs.isPresent(), this.scrambleAttribute.getValues(), this.scrambleJSONField.getValues());
            entryTranslators.add(t);
            changeRecordTranslators.add(t);
        }
        if (this.sequentialAttribute.isPresent()) {
            long initialValue;
            if (this.initialSequentialValue.isPresent()) {
                initialValue = this.initialSequentialValue.getValue();
            }
            else {
                initialValue = 0L;
            }
            long incrementAmount;
            if (this.sequentialValueIncrement.isPresent()) {
                incrementAmount = this.sequentialValueIncrement.getValue();
            }
            else {
                incrementAmount = 1L;
            }
            for (final String attrName : this.sequentialAttribute.getValues()) {
                final ReplaceWithCounterTransformation t2 = new ReplaceWithCounterTransformation(schema, attrName, initialValue, incrementAmount, this.textBeforeSequentialValue.getValue(), this.textAfterSequentialValue.getValue(), this.processDNs.isPresent());
                entryTranslators.add(t2);
            }
        }
        if (this.replaceValuesAttribute.isPresent()) {
            final ReplaceAttributeTransformation t3 = new ReplaceAttributeTransformation(schema, this.replaceValuesAttribute.getValue(), this.replacementValue.getValues());
            entryTranslators.add(t3);
        }
        if (this.addAttributeName.isPresent()) {
            final AddAttributeTransformation t4 = new AddAttributeTransformation(schema, this.addAttributeBaseDN.getValue(), this.addAttributeScope.getValue(), this.addAttributeFilter.getValue(), new Attribute(this.addAttributeName.getValue(), schema, this.addAttributeValue.getValues()), !this.addToExistingValues.isPresent());
            entryTranslators.add(t4);
        }
        if (this.renameAttributeFrom.isPresent()) {
            final Iterator<String> renameFromIterator = this.renameAttributeFrom.getValues().iterator();
            final Iterator<String> renameToIterator = this.renameAttributeTo.getValues().iterator();
            while (renameFromIterator.hasNext()) {
                final RenameAttributeTransformation t5 = new RenameAttributeTransformation(schema, renameFromIterator.next(), renameToIterator.next(), this.processDNs.isPresent());
                entryTranslators.add(t5);
                changeRecordTranslators.add(t5);
            }
        }
        if (this.flattenBaseDN.isPresent()) {
            final FlattenSubtreeTransformation t6 = new FlattenSubtreeTransformation(schema, this.flattenBaseDN.getValue(), this.flattenAddOmittedRDNAttributesToEntry.isPresent(), this.flattenAddOmittedRDNAttributesToRDN.isPresent(), this.flattenExcludeFilter.getValue());
            entryTranslators.add(t6);
        }
        if (this.moveSubtreeFrom.isPresent()) {
            final Iterator<DN> moveFromIterator = this.moveSubtreeFrom.getValues().iterator();
            final Iterator<DN> moveToIterator = this.moveSubtreeTo.getValues().iterator();
            while (moveFromIterator.hasNext()) {
                final MoveSubtreeTransformation t7 = new MoveSubtreeTransformation(moveFromIterator.next(), moveToIterator.next());
                entryTranslators.add(t7);
                changeRecordTranslators.add(t7);
            }
        }
        if (this.redactAttribute.isPresent()) {
            final RedactAttributeTransformation t8 = new RedactAttributeTransformation(schema, this.processDNs.isPresent(), !this.hideRedactedValueCount.isPresent(), this.redactAttribute.getValues());
            entryTranslators.add(t8);
            changeRecordTranslators.add(t8);
        }
        if (this.excludeAttribute.isPresent()) {
            final ExcludeAttributeTransformation t9 = new ExcludeAttributeTransformation(schema, this.excludeAttribute.getValues());
            entryTranslators.add(t9);
            changeRecordTranslators.add(t9);
        }
        if (this.excludeEntryBaseDN.isPresent() || this.excludeEntryScope.isPresent() || this.excludeEntryFilter.isPresent()) {
            final ExcludeEntryTransformation t10 = new ExcludeEntryTransformation(schema, this.excludeEntryBaseDN.getValue(), this.excludeEntryScope.getValue(), this.excludeEntryFilter.getValue(), !this.excludeNonMatchingEntries.isPresent(), excludedEntryCount);
            entryTranslators.add(t10);
        }
        if (this.excludeChangeType.isPresent()) {
            final Set<ChangeType> changeTypes = EnumSet.noneOf(ChangeType.class);
            for (final String changeTypeName : this.excludeChangeType.getValues()) {
                changeTypes.add(ChangeType.forName(changeTypeName));
            }
            changeRecordTranslators.add(new ExcludeChangeTypeTransformation(changeTypes));
        }
        if (this.excludeRecordsWithoutChangeType.isPresent()) {
            entryTranslators.add(new ExcludeAllEntriesTransformation());
        }
        entryTranslators.add(this);
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(4));
        examples.put(new String[] { "--sourceLDIF", "input.ldif", "--targetLDIF", "scrambled.ldif", "--scrambleAttribute", "givenName", "--scrambleAttribute", "sn", "--scrambleAttribute", "cn", "--numThreads", "10", "--schemaPath", "/ds/config/schema", "--processDNs" }, TransformationMessages.INFO_TRANSFORM_LDIF_EXAMPLE_SCRAMBLE.get());
        examples.put(new String[] { "--sourceLDIF", "input.ldif", "--targetLDIF", "sequential.ldif", "--sequentialAttribute", "uid", "--initialSequentialValue", "1", "--sequentialValueIncrement", "1", "--textBeforeSequentialValue", "user.", "--numThreads", "10", "--schemaPath", "/ds/config/schema", "--processDNs" }, TransformationMessages.INFO_TRANSFORM_LDIF_EXAMPLE_SEQUENTIAL.get());
        examples.put(new String[] { "--sourceLDIF", "input.ldif", "--targetLDIF", "added-organization.ldif", "--addAttributeName", "o", "--addAttributeValue", "Example Corp.", "--addAttributeFilter", "(objectClass=person)", "--numThreads", "10", "--schemaPath", "/ds/config/schema" }, TransformationMessages.INFO_TRANSFORM_LDIF_EXAMPLE_ADD.get());
        examples.put(new String[] { "--sourceLDIF", "input.ldif", "--targetLDIF", "rebased.ldif", "--moveSubtreeFrom", "o=example.com", "--moveSubtreeTo", "dc=example,dc=com", "--numThreads", "10", "--schemaPath", "/ds/config/schema" }, TransformationMessages.INFO_TRANSFORM_LDIF_EXAMPLE_REBASE.get());
        return examples;
    }
    
    @Override
    public Entry translate(final Entry original, final long firstLineNumber) throws LDIFException {
        final ByteStringBuffer buffer = this.getBuffer();
        if (this.wrapColumn.isPresent()) {
            original.toLDIF(buffer, this.wrapColumn.getValue());
        }
        else {
            original.toLDIF(buffer, 0);
        }
        buffer.append(StaticUtils.EOL_BYTES);
        return new PreEncodedLDIFEntry(original, buffer.toByteArray());
    }
    
    private ByteStringBuffer getBuffer() {
        ByteStringBuffer buffer = this.byteStringBuffers.get();
        if (buffer == null) {
            buffer = new ByteStringBuffer();
            this.byteStringBuffers.set(buffer);
        }
        else {
            buffer.clear();
        }
        return buffer;
    }
    
    static {
        MAX_OUTPUT_LINE_LENGTH = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
    }
}
