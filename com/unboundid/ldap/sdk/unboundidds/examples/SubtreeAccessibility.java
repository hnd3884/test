package com.unboundid.ldap.sdk.unboundidds.examples;

import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.unboundidds.extensions.SetSubtreeAccessibilityExtendedRequest;
import java.util.Iterator;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.extensions.SubtreeAccessibilityRestriction;
import com.unboundid.ldap.sdk.ExtendedRequest;
import com.unboundid.ldap.sdk.unboundidds.extensions.GetSubtreeAccessibilityExtendedRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.unboundidds.extensions.GetSubtreeAccessibilityExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Debug;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.ldap.sdk.unboundidds.extensions.SubtreeAccessibilityState;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.args.StringArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.BooleanArgument;
import java.util.Set;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SubtreeAccessibility extends LDAPCommandLineTool implements Serializable
{
    private static final Set<String> ALLOWED_ACCESSIBILITY_STATES;
    private static final long serialVersionUID = 3703682568143472108L;
    private BooleanArgument set;
    private DNArgument baseDN;
    private DNArgument bypassUserDN;
    private StringArgument accessibilityState;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final SubtreeAccessibility tool = new SubtreeAccessibility(outStream, errStream);
        return tool.runTool(args);
    }
    
    public SubtreeAccessibility(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.set = null;
        this.baseDN = null;
        this.bypassUserDN = null;
        this.accessibilityState = null;
    }
    
    @Override
    public String getToolName() {
        return "subtree-accessibility";
    }
    
    @Override
    public String getToolDescription() {
        return "List or update the set of subtree accessibility restrictions defined in the Directory Server.";
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
        parser.addArgument(this.set = new BooleanArgument('s', "set", 1, "Indicates that the set of accessibility restrictions should be updated rather than retrieved."));
        (this.baseDN = new DNArgument('b', "baseDN", false, 1, "{dn}", "The base DN of the subtree for which an accessibility restriction is to be updated.")).addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDN);
        parser.addArgument(this.accessibilityState = new StringArgument('S', "state", false, 1, "{state}", "The accessibility state to use for the accessibility restriction on the target subtree.  Allowed values:  " + SubtreeAccessibilityState.ACCESSIBLE.getStateName() + ", " + SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED.getStateName() + ", " + SubtreeAccessibilityState.READ_ONLY_BIND_DENIED.getStateName() + ", " + SubtreeAccessibilityState.HIDDEN.getStateName() + '.', SubtreeAccessibility.ALLOWED_ACCESSIBILITY_STATES));
        (this.bypassUserDN = new DNArgument('B', "bypassUserDN", false, 1, "{dn}", "The DN of a user who is allowed to bypass restrictions on the target subtree.")).addLongIdentifier("bypass-user-dn", true);
        parser.addArgument(this.bypassUserDN);
        parser.addDependentArgumentSet(this.baseDN, this.set, new Argument[0]);
        parser.addDependentArgumentSet(this.accessibilityState, this.set, new Argument[0]);
        parser.addDependentArgumentSet(this.bypassUserDN, this.set, new Argument[0]);
        parser.addDependentArgumentSet(this.set, this.baseDN, new Argument[0]);
        parser.addDependentArgumentSet(this.set, this.accessibilityState, new Argument[0]);
    }
    
    @Override
    public ResultCode doToolProcessing() {
        LDAPConnection connection;
        try {
            connection = this.getConnection();
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err("Unable to establish a connection to the target directory server:  ", StaticUtils.getExceptionMessage(le));
            return le.getResultCode();
        }
        try {
            if (this.set.isPresent()) {
                return this.doSet(connection);
            }
            return this.doGet(connection);
        }
        finally {
            connection.close();
        }
    }
    
    private ResultCode doGet(final LDAPConnection connection) {
        GetSubtreeAccessibilityExtendedResult result;
        try {
            result = (GetSubtreeAccessibilityExtendedResult)connection.processExtendedOperation(new GetSubtreeAccessibilityExtendedRequest(new Control[0]));
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err("An error occurred while attempting to invoke the get subtree accessibility request:  ", StaticUtils.getExceptionMessage(le));
            return le.getResultCode();
        }
        if (result.getResultCode() != ResultCode.SUCCESS) {
            this.err("The server returned an error for the get subtree accessibility request:  ", result.getDiagnosticMessage());
            return result.getResultCode();
        }
        final List<SubtreeAccessibilityRestriction> restrictions = result.getAccessibilityRestrictions();
        if (restrictions == null || restrictions.isEmpty()) {
            this.out("There are no subtree accessibility restrictions defined in the server.");
            return ResultCode.SUCCESS;
        }
        if (restrictions.size() == 1) {
            this.out("1 subtree accessibility restriction was found in the server:");
        }
        else {
            this.out(restrictions.size(), " subtree accessibility restrictions were found in the server:");
        }
        for (final SubtreeAccessibilityRestriction r : restrictions) {
            this.out("Subtree Base DN:      ", r.getSubtreeBaseDN());
            this.out("Accessibility State:  ", r.getAccessibilityState().getStateName());
            final String bypassDN = r.getBypassUserDN();
            if (bypassDN != null) {
                this.out("Bypass User DN:       ", bypassDN);
            }
            this.out("Effective Time:       ", r.getEffectiveTime());
            this.out(new Object[0]);
        }
        return ResultCode.SUCCESS;
    }
    
    private ResultCode doSet(final LDAPConnection connection) {
        final SubtreeAccessibilityState state = SubtreeAccessibilityState.forName(this.accessibilityState.getValue());
        if (state == null) {
            this.err("Unsupported subtree accessibility state ", this.accessibilityState.getValue());
            return ResultCode.PARAM_ERROR;
        }
        SetSubtreeAccessibilityExtendedRequest request = null;
        switch (state) {
            case ACCESSIBLE: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetAccessibleRequest(this.baseDN.getStringValue(), new Control[0]);
                break;
            }
            case READ_ONLY_BIND_ALLOWED: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetReadOnlyRequest(this.baseDN.getStringValue(), true, this.bypassUserDN.getStringValue(), new Control[0]);
                break;
            }
            case READ_ONLY_BIND_DENIED: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetReadOnlyRequest(this.baseDN.getStringValue(), false, this.bypassUserDN.getStringValue(), new Control[0]);
                break;
            }
            case HIDDEN: {
                request = SetSubtreeAccessibilityExtendedRequest.createSetHiddenRequest(this.baseDN.getStringValue(), this.bypassUserDN.getStringValue(), new Control[0]);
                break;
            }
            default: {
                this.err("Unsupported subtree accessibility state ", state.getStateName());
                return ResultCode.PARAM_ERROR;
            }
        }
        ExtendedResult result;
        try {
            result = connection.processExtendedOperation(request);
        }
        catch (final LDAPException le) {
            Debug.debugException(le);
            this.err("An error occurred while attempting to invoke the set subtree accessibility request:  ", StaticUtils.getExceptionMessage(le));
            return le.getResultCode();
        }
        if (result.getResultCode() == ResultCode.SUCCESS) {
            this.out("Successfully set an accessibility state of ", state.getStateName(), " for subtree ", this.baseDN.getStringValue());
        }
        else {
            this.out("Unable to set an accessibility state of ", state.getStateName(), " for subtree ", this.baseDN.getStringValue(), ":  ", result.getDiagnosticMessage());
        }
        return result.getResultCode();
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(2));
        final String[] getArgs = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password" };
        exampleMap.put(getArgs, "Retrieve information about all subtree accessibility restrictions defined in the server.");
        final String[] setArgs = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--set", "--baseDN", "ou=subtree,dc=example,dc=com", "--state", "read-only-bind-allowed", "--bypassUserDN", "uid=bypass,dc=example,dc=com" };
        exampleMap.put(setArgs, "Create or update the subtree accessibility state definition for subtree 'ou=subtree,dc=example,dc=com' so that it is read-only for all users except 'uid=bypass,dc=example,dc=com'.");
        return exampleMap;
    }
    
    static {
        ALLOWED_ACCESSIBILITY_STATES = StaticUtils.setOf(SubtreeAccessibilityState.ACCESSIBLE.getStateName(), SubtreeAccessibilityState.READ_ONLY_BIND_ALLOWED.getStateName(), SubtreeAccessibilityState.READ_ONLY_BIND_DENIED.getStateName(), SubtreeAccessibilityState.HIDDEN.getStateName());
    }
}
