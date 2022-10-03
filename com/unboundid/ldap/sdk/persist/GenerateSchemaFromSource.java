package com.unboundid.ldap.sdk.persist;

import java.util.LinkedHashMap;
import java.io.File;
import com.unboundid.ldif.LDIFRecord;
import java.util.Iterator;
import java.util.List;
import com.unboundid.ldif.LDIFWriter;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldif.LDIFModifyChangeRecord;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.schema.ObjectClassDefinition;
import com.unboundid.ldap.sdk.schema.AttributeTypeDefinition;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;
import java.io.Serializable;
import com.unboundid.util.CommandLineTool;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class GenerateSchemaFromSource extends CommandLineTool implements Serializable
{
    private static final long serialVersionUID = 1029934829295836935L;
    private BooleanArgument modifyFormatArg;
    private FileArgument outputFileArg;
    private StringArgument classNameArg;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final GenerateSchemaFromSource tool = new GenerateSchemaFromSource(outStream, errStream);
        return tool.runTool(args);
    }
    
    public GenerateSchemaFromSource(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
    }
    
    @Override
    public String getToolName() {
        return "generate-schema-from-source";
    }
    
    @Override
    public String getToolDescription() {
        return PersistMessages.INFO_GEN_SCHEMA_TOOL_DESCRIPTION.get();
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
        (this.classNameArg = new StringArgument('c', "javaClass", true, 1, PersistMessages.INFO_GEN_SCHEMA_VALUE_PLACEHOLDER_CLASS.get(), PersistMessages.INFO_GEN_SCHEMA_ARG_DESCRIPTION_JAVA_CLASS.get())).addLongIdentifier("java-class", true);
        parser.addArgument(this.classNameArg);
        (this.outputFileArg = new FileArgument('f', "outputFile", true, 1, PersistMessages.INFO_GEN_SCHEMA_VALUE_PLACEHOLDER_PATH.get(), PersistMessages.INFO_GEN_SCHEMA_ARG_DESCRIPTION_OUTPUT_FILE.get(), false, true, true, false)).addLongIdentifier("output-file", true);
        parser.addArgument(this.outputFileArg);
        (this.modifyFormatArg = new BooleanArgument('m', "modifyFormat", PersistMessages.INFO_GEN_SCHEMA_ARG_DESCRIPTION_MODIFY_FORMAT.get())).addLongIdentifier("modify-format", true);
        parser.addArgument(this.modifyFormatArg);
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final String className = this.classNameArg.getValue();
        Class<?> targetClass;
        try {
            targetClass = Class.forName(className);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            this.err(PersistMessages.ERR_GEN_SCHEMA_CANNOT_LOAD_CLASS.get(className));
            return ResultCode.PARAM_ERROR;
        }
        LDAPPersister<?> persister;
        try {
            persister = LDAPPersister.getInstance(targetClass);
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
            this.err(PersistMessages.ERR_GEN_SCHEMA_INVALID_CLASS.get(className, StaticUtils.getExceptionMessage(e2)));
            return ResultCode.LOCAL_ERROR;
        }
        List<AttributeTypeDefinition> attrTypes;
        try {
            attrTypes = persister.constructAttributeTypes();
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            this.err(PersistMessages.ERR_GEN_SCHEMA_ERROR_CONSTRUCTING_ATTRS.get(className, StaticUtils.getExceptionMessage(e3)));
            return ResultCode.LOCAL_ERROR;
        }
        List<ObjectClassDefinition> objectClasses;
        try {
            objectClasses = persister.constructObjectClasses();
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            this.err(PersistMessages.ERR_GEN_SCHEMA_ERROR_CONSTRUCTING_OCS.get(className, StaticUtils.getExceptionMessage(e4)));
            return ResultCode.LOCAL_ERROR;
        }
        int i = 0;
        final ASN1OctetString[] attrTypeValues = new ASN1OctetString[attrTypes.size()];
        for (final AttributeTypeDefinition d : attrTypes) {
            attrTypeValues[i++] = new ASN1OctetString(d.toString());
        }
        i = 0;
        final ASN1OctetString[] ocValues = new ASN1OctetString[objectClasses.size()];
        for (final ObjectClassDefinition d2 : objectClasses) {
            ocValues[i++] = new ASN1OctetString(d2.toString());
        }
        LDIFRecord schemaRecord;
        if (this.modifyFormatArg.isPresent()) {
            schemaRecord = new LDIFModifyChangeRecord("cn=schema", new Modification[] { new Modification(ModificationType.ADD, "attributeTypes", attrTypeValues), new Modification(ModificationType.ADD, "objectClasses", ocValues) });
        }
        else {
            schemaRecord = new Entry("cn=schema", new Attribute[] { new Attribute("objectClass", new String[] { "top", "ldapSubentry", "subschema" }), new Attribute("cn", "schema"), new Attribute("attributeTypes", attrTypeValues), new Attribute("objectClasses", ocValues) });
        }
        final File outputFile = this.outputFileArg.getValue();
        try {
            final LDIFWriter ldifWriter = new LDIFWriter(outputFile);
            ldifWriter.writeLDIFRecord(schemaRecord);
            ldifWriter.close();
        }
        catch (final Exception e5) {
            Debug.debugException(e5);
            this.err(PersistMessages.ERR_GEN_SCHEMA_CANNOT_WRITE_SCHEMA.get(outputFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e5)));
            return ResultCode.LOCAL_ERROR;
        }
        return ResultCode.SUCCESS;
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--javaClass", "com.example.MyClass", "--outputFile", "MyClass-schema.ldif" };
        examples.put(args, PersistMessages.INFO_GEN_SCHEMA_EXAMPLE_1.get());
        return examples;
    }
}
