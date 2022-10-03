package com.unboundid.ldap.sdk.examples;

import com.unboundid.util.StaticUtils;
import java.util.LinkedHashMap;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldap.sdk.LDAPException;
import java.io.IOException;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.ldap.sdk.Control;
import java.util.List;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPModify extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = -2602159836108416722L;
    private BooleanArgument continueOnError;
    private BooleanArgument defaultAdd;
    private ControlArgument bindControls;
    private FileArgument ldifFile;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final LDAPModify ldapModify = new LDAPModify(outStream, errStream);
        return ldapModify.runTool(args);
    }
    
    public LDAPModify(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
    }
    
    @Override
    public String getToolName() {
        return "ldapmodify";
    }
    
    @Override
    public String getToolDescription() {
        return "Perform add, delete, modify, and modify DN operations in an LDAP directory server.";
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
    protected boolean logToolInvocationByDefault() {
        return true;
    }
    
    @Override
    public void addNonLDAPArguments(final ArgumentParser parser) throws ArgumentException {
        String description = "Treat LDIF records that do not contain a changetype as add records.";
        (this.defaultAdd = new BooleanArgument('a', "defaultAdd", description)).addLongIdentifier("default-add", true);
        parser.addArgument(this.defaultAdd);
        description = "Attempt to continue processing additional changes if an error occurs.";
        (this.continueOnError = new BooleanArgument('c', "continueOnError", description)).addLongIdentifier("continue-on-error", true);
        parser.addArgument(this.continueOnError);
        description = "The path to the LDIF file containing the changes.  If this is not provided, then the changes will be read from standard input.";
        (this.ldifFile = new FileArgument('f', "ldifFile", false, 1, "{path}", description, true, false, true, false)).addLongIdentifier("ldif-file", true);
        parser.addArgument(this.ldifFile);
        description = "Information about a control to include in the bind request.";
        (this.bindControls = new ControlArgument(null, "bindControl", false, 0, null, description)).addLongIdentifier("bind-control", true);
        parser.addArgument(this.bindControls);
    }
    
    @Override
    protected List<Control> getBindControls() {
        return this.bindControls.getValues();
    }
    
    @Override
    public ResultCode doToolProcessing() {
        LDIFReader ldifReader;
        try {
            if (this.ldifFile.isPresent()) {
                ldifReader = new LDIFReader(this.ldifFile.getValue());
            }
            else {
                ldifReader = new LDIFReader(System.in);
            }
        }
        catch (final IOException ioe) {
            this.err("I/O error creating the LDIF reader:  ", ioe.getMessage());
            return ResultCode.LOCAL_ERROR;
        }
        LDAPConnection connection;
        try {
            connection = this.getConnection();
            this.out("Connected to ", connection.getConnectedAddress(), ':', connection.getConnectedPort());
        }
        catch (final LDAPException le) {
            this.err("Error connecting to the directory server:  ", le.getMessage());
            return le.getResultCode();
        }
        ResultCode resultCode = ResultCode.SUCCESS;
        while (true) {
            LDIFChangeRecord changeRecord;
            try {
                changeRecord = ldifReader.readChangeRecord(this.defaultAdd.isPresent());
            }
            catch (final LDIFException le2) {
                this.err("Malformed change record:  ", le2.getMessage());
                if (!le2.mayContinueReading()) {
                    this.err("Unable to continue processing the LDIF content.");
                    resultCode = ResultCode.DECODING_ERROR;
                    break;
                }
                if (!this.continueOnError.isPresent()) {
                    resultCode = ResultCode.DECODING_ERROR;
                    break;
                }
                continue;
            }
            catch (final IOException ioe2) {
                this.err("I/O error encountered while reading a change record:  ", ioe2.getMessage());
                resultCode = ResultCode.LOCAL_ERROR;
                break;
            }
            if (changeRecord == null) {
                break;
            }
            try {
                this.out("Processing ", changeRecord.getChangeType().toString(), " operation for ", changeRecord.getDN());
                changeRecord.processChange(connection);
                this.out("Success");
                this.out(new Object[0]);
            }
            catch (final LDAPException le3) {
                this.err("Error:  ", le3.getMessage());
                this.err("Result Code:  ", le3.getResultCode().intValue(), " (", le3.getResultCode().getName(), ')');
                if (le3.getMatchedDN() != null) {
                    this.err("Matched DN:  ", le3.getMatchedDN());
                }
                if (le3.getReferralURLs() != null) {
                    for (final String url : le3.getReferralURLs()) {
                        this.err("Referral URL:  ", url);
                    }
                }
                this.err(new Object[0]);
                if (!this.continueOnError.isPresent()) {
                    resultCode = le3.getResultCode();
                    break;
                }
                continue;
            }
        }
        connection.close();
        this.out("Disconnected from the server");
        return resultCode;
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--ldifFile", "changes.ldif" };
        String description = "Attempt to apply the add, delete, modify, and/or modify DN operations contained in the 'changes.ldif' file against the specified directory server.";
        examples.put(args, description);
        args = new String[] { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--continueOnError", "--defaultAdd" };
        description = "Establish a connection to the specified directory server and then wait for information about the add, delete, modify, and/or modify DN operations to perform to be provided via standard input.  If any invalid operations are requested, then the tool will display an error message but will continue running.  Any LDIF record provided which does not include a 'changeType' line will be treated as an add request.";
        examples.put(args, description);
        return examples;
    }
}
