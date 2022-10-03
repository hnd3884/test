package com.unboundid.ldap.sdk.examples;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.CompareResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.CompareRequest;
import com.unboundid.ldap.sdk.LDAPException;
import java.text.ParseException;
import com.unboundid.util.Base64;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Control;
import java.util.Iterator;
import java.util.List;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.ControlArgument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPCompare extends LDAPCommandLineTool implements Serializable
{
    private static final long serialVersionUID = 719069383330181184L;
    private ArgumentParser parser;
    private ControlArgument bindControls;
    private ControlArgument compareControls;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final LDAPCompare ldapCompare = new LDAPCompare(outStream, errStream);
        return ldapCompare.runTool(args);
    }
    
    public LDAPCompare(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
    }
    
    @Override
    public String getToolName() {
        return "ldapcompare";
    }
    
    @Override
    public String getToolDescription() {
        return "Process compare operations in LDAP directory server.";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public int getMinTrailingArguments() {
        return 2;
    }
    
    @Override
    public int getMaxTrailingArguments() {
        return -1;
    }
    
    @Override
    public String getTrailingArgumentsPlaceholder() {
        return "attr:value dn1 [dn2 [dn3 [...]]]";
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
        this.parser = parser;
        String description = "Information about a control to include in the bind request.";
        (this.bindControls = new ControlArgument(null, "bindControl", false, 0, null, description)).addLongIdentifier("bind-control", true);
        parser.addArgument(this.bindControls);
        description = "Information about a control to include in compare requests.";
        parser.addArgument(this.compareControls = new ControlArgument('J', "control", false, 0, null, description));
    }
    
    @Override
    public void doExtendedNonLDAPArgumentValidation() throws ArgumentException {
        final List<String> trailingArgs = this.parser.getTrailingArguments();
        if (trailingArgs.size() < 2) {
            throw new ArgumentException("At least two trailing argument must be provided to specify the assertion criteria in the form 'attr:value'.  All additional trailing arguments must be the DNs of the entries against which to perform the compare.");
        }
        final Iterator<String> argIterator = trailingArgs.iterator();
        final String ava = argIterator.next();
        if (ava.indexOf(58) < 1) {
            throw new ArgumentException("The first trailing argument value must specify the assertion criteria in the form 'attr:value'.");
        }
        while (argIterator.hasNext()) {
            final String arg = argIterator.next();
            try {
                new DN(arg);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                throw new ArgumentException("Unable to parse trailing argument '" + arg + "' as a valid DN.", e);
            }
        }
    }
    
    @Override
    protected List<Control> getBindControls() {
        return this.bindControls.getValues();
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final List<String> trailingArguments = this.parser.getTrailingArguments();
        if (trailingArguments.isEmpty()) {
            this.err("No attribute value assertion was provided.");
            this.err(new Object[0]);
            this.err(this.parser.getUsageString(StaticUtils.TERMINAL_WIDTH_COLUMNS - 1));
            return ResultCode.PARAM_ERROR;
        }
        if (trailingArguments.size() == 1) {
            this.err("No target entry DNs were provided.");
            this.err(new Object[0]);
            this.err(this.parser.getUsageString(StaticUtils.TERMINAL_WIDTH_COLUMNS - 1));
            return ResultCode.PARAM_ERROR;
        }
        final String avaString = trailingArguments.get(0);
        final int colonPos = avaString.indexOf(58);
        if (colonPos <= 0) {
            this.err("Malformed attribute value assertion.");
            this.err(new Object[0]);
            this.err(this.parser.getUsageString(StaticUtils.TERMINAL_WIDTH_COLUMNS - 1));
            return ResultCode.PARAM_ERROR;
        }
        final String attributeName = avaString.substring(0, colonPos);
        final int doubleColonPos = avaString.indexOf("::");
        byte[] assertionValueBytes = null;
        LDAPConnection connection = null;
        Label_0300: {
            if (doubleColonPos == colonPos) {
                try {
                    assertionValueBytes = Base64.decode(avaString.substring(colonPos + 2));
                    break Label_0300;
                }
                catch (final ParseException pe) {
                    this.err("Unable to base64-decode the assertion value:  ", pe.getMessage());
                    this.err(new Object[0]);
                    this.err(this.parser.getUsageString(StaticUtils.TERMINAL_WIDTH_COLUMNS - 1));
                    return ResultCode.PARAM_ERROR;
                }
            }
            assertionValueBytes = StaticUtils.getBytes(avaString.substring(colonPos + 1));
            try {
                connection = this.getConnection();
                this.out("Connected to ", connection.getConnectedAddress(), ':', connection.getConnectedPort());
            }
            catch (final LDAPException le) {
                this.err("Error connecting to the directory server:  ", le.getMessage());
                return le.getResultCode();
            }
        }
        ResultCode resultCode = ResultCode.SUCCESS;
        CompareRequest compareRequest = null;
        for (int i = 1; i < trailingArguments.size(); ++i) {
            final String targetDN = trailingArguments.get(i);
            if (compareRequest == null) {
                compareRequest = new CompareRequest(targetDN, attributeName, assertionValueBytes);
                compareRequest.setControls(this.compareControls.getValues());
            }
            else {
                compareRequest.setDN(targetDN);
            }
            try {
                this.out("Processing compare request for entry ", targetDN);
                final CompareResult result = connection.compare(compareRequest);
                if (result.compareMatched()) {
                    this.out("The compare operation matched.");
                }
                else {
                    this.out("The compare operation did not match.");
                }
            }
            catch (final LDAPException le2) {
                resultCode = le2.getResultCode();
                this.err("An error occurred while processing the request:  ", le2.getMessage());
                this.err("Result Code:  ", le2.getResultCode().intValue(), " (", le2.getResultCode().getName(), ')');
                if (le2.getMatchedDN() != null) {
                    this.err("Matched DN:  ", le2.getMatchedDN());
                }
                if (le2.getReferralURLs() != null) {
                    for (final String url : le2.getReferralURLs()) {
                        this.err("Referral URL:  ", url);
                    }
                }
            }
            this.out(new Object[0]);
        }
        connection.close();
        this.out(new Object[0]);
        this.out("Disconnected from the server");
        return resultCode;
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "givenName:John", "uid=jdoe,ou=People,dc=example,dc=com" };
        final String description = "Attempt to determine whether the entry for user 'uid=jdoe,ou=People,dc=example,dc=com' has a value of 'John' for the givenName attribute.";
        examples.put(args, "Attempt to determine whether the entry for user 'uid=jdoe,ou=People,dc=example,dc=com' has a value of 'John' for the givenName attribute.");
        return examples;
    }
}
