package com.unboundid.util;

import java.util.Iterator;
import com.unboundid.ldap.sdk.examples.ValidateLDIF;
import com.unboundid.ldap.sdk.transformations.TransformLDIF;
import com.unboundid.util.ssl.TLSCipherSuiteSelector;
import com.unboundid.ldap.sdk.examples.SearchAndModRate;
import com.unboundid.ldap.sdk.examples.SearchRate;
import com.unboundid.ldap.sdk.examples.ModRate;
import com.unboundid.util.ssl.cert.ManageCertificates;
import com.unboundid.ldap.sdk.examples.LDAPDebugger;
import com.unboundid.ldap.sdk.examples.LDAPSearch;
import com.unboundid.ldap.sdk.examples.LDAPModify;
import com.unboundid.ldap.sdk.examples.LDAPCompare;
import com.unboundid.ldap.sdk.persist.GenerateSourceFromSchema;
import com.unboundid.ldap.sdk.persist.GenerateSchemaFromSource;
import com.unboundid.ldap.listener.InMemoryDirectoryServerTool;
import com.unboundid.ldap.sdk.examples.IndentLDAPFilter;
import com.unboundid.ldap.sdk.examples.IdentifyUniqueAttributeConflicts;
import com.unboundid.ldap.sdk.examples.IdentifyReferencesToMissingEntries;
import com.unboundid.ldap.sdk.examples.Base64Tool;
import com.unboundid.ldap.sdk.examples.AuthRate;
import com.unboundid.ldap.sdk.Version;
import java.io.PrintStream;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;

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
        if (firstArg.equals("identify-references-to-missing-entries")) {
            return IdentifyReferencesToMissingEntries.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("identify-unique-attribute-conflicts")) {
            return IdentifyUniqueAttributeConflicts.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("indent-ldap-filter")) {
            return IndentLDAPFilter.main(outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("in-memory-directory-server")) {
            return InMemoryDirectoryServerTool.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("generate-schema-from-source")) {
            return GenerateSchemaFromSource.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("generate-source-from-schema")) {
            return GenerateSourceFromSchema.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("ldapcompare")) {
            return LDAPCompare.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("ldapmodify")) {
            return LDAPModify.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("ldapsearch")) {
            return LDAPSearch.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("ldap-debugger")) {
            return LDAPDebugger.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("manage-certificates")) {
            return ManageCertificates.main(System.in, outStream, errStream, remainingArgs);
        }
        if (firstArg.equals("modrate")) {
            return ModRate.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("searchrate")) {
            return SearchRate.main(remainingArgs, outStream, errStream);
        }
        if (firstArg.equals("search-and-mod-rate")) {
            return SearchAndModRate.main(remainingArgs, outStream, errStream);
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
            err.println("     generate-schema-from-source");
            err.println("     generate-source-from-schema");
            err.println("     identify-references-to-missing-entries");
            err.println("     identify-unique-attribute-conflicts");
            err.println("     indent-ldap-filter");
            err.println("     in-memory-directory-server");
            err.println("     ldapcompare");
            err.println("     ldapmodify");
            err.println("     ldapsearch");
            err.println("     ldap-debugger");
            err.println("     manage-certificates");
            err.println("     modrate");
            err.println("     searchrate");
            err.println("     search-and-mod-rate");
            err.println("     tls-cipher-suite-selector");
            err.println("     transform-ldif");
            err.println("     validate-ldif");
            err.println("     version");
        }
        return ResultCode.PARAM_ERROR;
    }
}
