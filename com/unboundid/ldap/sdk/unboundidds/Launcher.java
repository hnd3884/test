package com.unboundid.ldap.sdk.unboundidds;

import java.lang.reflect.Constructor;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Arrays;
import com.unboundid.util.CommandLineTool;
import java.util.List;
import java.util.Iterator;
import com.unboundid.ldap.sdk.examples.ValidateLDIF;
import com.unboundid.ldap.sdk.transformations.TransformLDIF;
import com.unboundid.util.ssl.TLSCipherSuiteSelector;
import com.unboundid.ldap.sdk.unboundidds.examples.SummarizeAccessLog;
import com.unboundid.ldap.sdk.unboundidds.examples.SubtreeAccessibility;
import com.unboundid.ldap.sdk.unboundidds.tools.SplitLDIF;
import com.unboundid.ldap.sdk.examples.SearchAndModRate;
import com.unboundid.ldap.sdk.examples.SearchRate;
import com.unboundid.ldap.sdk.examples.ModRate;
import com.unboundid.util.ssl.cert.ManageCertificates;
import com.unboundid.ldap.sdk.unboundidds.tools.ManageAccount;
import com.unboundid.ldap.sdk.examples.LDAPDebugger;
import com.unboundid.ldap.sdk.unboundidds.tools.LDAPSearch;
import com.unboundid.ldap.sdk.unboundidds.tools.LDAPModify;
import com.unboundid.ldap.sdk.unboundidds.tools.LDAPDelete;
import com.unboundid.ldap.sdk.examples.LDAPCompare;
import com.unboundid.ldap.sdk.unboundidds.tools.GenerateTOTPSharedSecret;
import com.unboundid.ldap.sdk.persist.GenerateSourceFromSchema;
import com.unboundid.ldap.sdk.persist.GenerateSchemaFromSource;
import com.unboundid.ldap.sdk.examples.IndentLDAPFilter;
import com.unboundid.ldap.listener.InMemoryDirectoryServerTool;
import com.unboundid.ldap.sdk.examples.IdentifyUniqueAttributeConflicts;
import com.unboundid.ldap.sdk.examples.IdentifyReferencesToMissingEntries;
import com.unboundid.ldap.sdk.unboundidds.examples.DumpDNs;
import com.unboundid.ldap.sdk.examples.Base64Tool;
import com.unboundid.ldap.sdk.examples.AuthRate;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Version;
import java.io.PrintStream;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Launcher
{
    private Launcher() {
    }
    
    public static void main(final String... args) {
        main(System.out, System.err, args);
    }
    
    public static ResultCode main(final OutputStream outStream, final OutputStream errStream, final String... args) {
        if (args == null || args.length == 0 || args[0].equalsIgnoreCase("version")) {
            if (outStream != null) {
                final PrintStream out = new PrintStream(outStream);
                for (final String line : Version.getVersionLines()) {
                    out.println(line);
                }
            }
            return ResultCode.SUCCESS;
        }
        final String firstArg = StaticUtils.toLowerCase(args[0]);
        final String[] remainingArgs = new String[args.length - 1];
        System.arraycopy(args, 1, remainingArgs, 0, remainingArgs.length);
        if (firstArg.equals("authrate")) {
            return AuthRate.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("base64")) {
            return Base64Tool.main(System.in, outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("deliver-one-time-password")) {
            return DeliverOneTimePassword.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("deliver-password-reset-token")) {
            return DeliverPasswordResetToken.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("dump-dns")) {
            return DumpDNs.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("identify-references-to-missing-entries")) {
            return IdentifyReferencesToMissingEntries.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("identify-unique-attribute-conflicts")) {
            return IdentifyUniqueAttributeConflicts.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("in-memory-directory-server")) {
            return InMemoryDirectoryServerTool.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("indent-ldap-filter")) {
            return IndentLDAPFilter.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("generate-schema-from-source")) {
            return GenerateSchemaFromSource.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("generate-source-from-schema")) {
            return GenerateSourceFromSchema.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("generate-totp-shared-secret")) {
            return GenerateTOTPSharedSecret.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("ldapcompare")) {
            return LDAPCompare.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("ldapdelete")) {
            return LDAPDelete.main(System.in, outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("ldapmodify")) {
            return LDAPModify.main(System.in, outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("ldapsearch")) {
            return LDAPSearch.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("ldap-debugger")) {
            return LDAPDebugger.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("manage-account")) {
            return ManageAccount.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("manage-certificates")) {
            return ManageCertificates.main(System.in, outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("modrate")) {
            return ModRate.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("move-subtree")) {
            return MoveSubtree.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("register-yubikey-otp-device")) {
            return RegisterYubiKeyOTPDevice.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("searchrate")) {
            return SearchRate.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("search-and-mod-rate")) {
            return SearchAndModRate.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("split-ldif")) {
            return SplitLDIF.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("subtree-accessibility")) {
            return SubtreeAccessibility.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("summarize-access-log")) {
            return SummarizeAccessLog.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("tls-cipher-suite-selector")) {
            return TLSCipherSuiteSelector.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("transform-ldif")) {
            return TransformLDIF.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("validate-ldif")) {
            return ValidateLDIF.main(remainingArgs, outStream, errStream);
        }
        if (errStream != null) {
            final PrintStream err = new PrintStream(errStream);
            err.println("Unrecognized tool name '" + args[0] + '\'');
            err.println("Supported tool names include:");
            err.println("     authrate");
            err.println("     base64");
            err.println("     deliver-one-time-password");
            err.println("     deliver-password-reset-token");
            err.println("     dump-dns");
            err.println("     generate-schema-from-source");
            err.println("     generate-source-from-schema");
            err.println("     generate-totp-shared-secret");
            err.println("     identify-references-to-missing-entries");
            err.println("     identify-unique-attribute-conflicts");
            err.println("     indent-ldap-filter");
            err.println("     in-memory-directory-server");
            err.println("     ldapcompare");
            err.println("     ldapdelete");
            err.println("     ldapmodify");
            err.println("     ldapsearch");
            err.println("     ldap-debugger");
            err.println("     manage-account");
            err.println("     manage-certificates");
            err.println("     modrate");
            err.println("     move-subtree");
            err.println("     register-yubikey-otp-device");
            err.println("     searchrate");
            err.println("     search-and-mod-rate");
            err.println("     split-ldif");
            err.println("     subtree-accessibility");
            err.println("     summarize-access-log");
            err.println("     tls-cipher-suite-selector");
            err.println("     transform-ldif");
            err.println("     validate-ldif");
            err.println("     version");
        }
        return ResultCode.PARAM_ERROR;
    }
    
    public static List<Class<? extends CommandLineTool>> getToolClasses() {
        return (List<Class<? extends CommandLineTool>>)Arrays.asList(AuthRate.class, Base64Tool.class, DeliverOneTimePassword.class, DeliverPasswordResetToken.class, DumpDNs.class, GenerateSchemaFromSource.class, GenerateSourceFromSchema.class, GenerateTOTPSharedSecret.class, IdentifyReferencesToMissingEntries.class, IdentifyUniqueAttributeConflicts.class, IndentLDAPFilter.class, InMemoryDirectoryServerTool.class, LDAPCompare.class, LDAPDebugger.class, LDAPDelete.class, LDAPModify.class, LDAPSearch.class, ManageAccount.class, ManageCertificates.class, ModRate.class, MoveSubtree.class, RegisterYubiKeyOTPDevice.class, SearchAndModRate.class, SearchRate.class, SplitLDIF.class, SubtreeAccessibility.class, SummarizeAccessLog.class, TLSCipherSuiteSelector.class, TransformLDIF.class, ValidateLDIF.class);
    }
    
    public static CommandLineTool getToolInstance(final Class<?> toolClass, final OutputStream outStream, final OutputStream errStream) throws LDAPException {
        if (!CommandLineTool.class.isAssignableFrom(toolClass)) {
            throw new LDAPException(ResultCode.PARAM_ERROR, UnboundIDDSMessages.ERR_LAUNCHER_CLASS_NOT_COMMAND_LINE_TOOL.get(toolClass.getName(), CommandLineTool.class.getName()));
        }
        Constructor<?> constructor;
        try {
            constructor = toolClass.getConstructor(OutputStream.class, OutputStream.class);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.PARAM_ERROR, UnboundIDDSMessages.ERR_LAUNCHER_TOOL_CLASS_MISSING_EXPECTED_CONSTRUCTOR.get(toolClass.getName()), e);
        }
        try {
            return (CommandLineTool)constructor.newInstance(outStream, errStream);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new LDAPException(ResultCode.LOCAL_ERROR, UnboundIDDSMessages.ERR_LAUNCHER_ERROR_INVOKING_CONSTRUCTOR.get(toolClass.getName(), StaticUtils.getExceptionMessage(e)), e);
        }
    }
}
