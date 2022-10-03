package com.unboundid.ldap.sdk.unboundidds.examples;

import java.util.Iterator;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.unboundidds.extensions.StreamDirectoryValuesIntermediateResponse;
import com.unboundid.ldap.sdk.IntermediateResponse;
import java.util.LinkedHashMap;
import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.io.File;
import com.unboundid.ldap.sdk.ExtendedRequest;
import java.util.List;
import com.unboundid.ldap.sdk.unboundidds.extensions.StreamDirectoryValuesExtendedRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.LDAPException;
import java.io.IOException;
import com.unboundid.util.StaticUtils;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.ResultCode;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicLong;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.ldap.sdk.IntermediateResponseListener;
import com.unboundid.util.LDAPCommandLineTool;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class DumpDNs extends LDAPCommandLineTool implements IntermediateResponseListener
{
    private static final long serialVersionUID = 774432759537092866L;
    private DNArgument baseDN;
    private FileArgument outputFile;
    private final AtomicLong dnsWritten;
    private PrintStream outputStream;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final DumpDNs tool = new DumpDNs(outStream, errStream);
        return tool.runTool(args);
    }
    
    public DumpDNs(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.baseDN = null;
        this.outputFile = null;
        this.outputStream = null;
        this.dnsWritten = new AtomicLong(0L);
    }
    
    @Override
    public String getToolName() {
        return "dump-dns";
    }
    
    @Override
    public String getToolDescription() {
        return "Obtain a listing of all of the DNs for all entries below a specified base DN in the Directory Server.";
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
        (this.baseDN = new DNArgument('b', "baseDN", true, 1, "{dn}", "The base DN below which to dump the DNs of all entries in the Directory Server.")).addLongIdentifier("base-dn", true);
        parser.addArgument(this.baseDN);
        (this.outputFile = new FileArgument('f', "outputFile", false, 1, "{path}", "The path of the output file to which the entry DNs will be written.  If this is not provided, then entry DNs will be written to standard output.", false, true, true, false)).addLongIdentifier("output-file", true);
        parser.addArgument(this.outputFile);
    }
    
    @Override
    public LDAPConnectionOptions getConnectionOptions() {
        final LDAPConnectionOptions options = new LDAPConnectionOptions();
        options.setUseSynchronousMode(true);
        options.setResponseTimeoutMillis(0L);
        return options;
    }
    
    @Override
    public ResultCode doToolProcessing() {
        final File f = this.outputFile.getValue();
        if (f == null) {
            this.outputStream = this.getOut();
        }
        else {
            try {
                this.outputStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(f)));
            }
            catch (final IOException ioe) {
                this.err("Unable to open output file '", f.getAbsolutePath(), " for writing:  ", StaticUtils.getExceptionMessage(ioe));
                return ResultCode.LOCAL_ERROR;
            }
        }
        LDAPConnection conn;
        try {
            conn = this.getConnection();
        }
        catch (final LDAPException le) {
            this.err("Unable to obtain a connection to the Directory Server:  ", le.getExceptionMessage());
            return le.getResultCode();
        }
        final StreamDirectoryValuesExtendedRequest streamValuesRequest = new StreamDirectoryValuesExtendedRequest(this.baseDN.getStringValue(), SearchScope.SUB, false, null, 1000, new Control[0]);
        streamValuesRequest.setIntermediateResponseListener(this);
        streamValuesRequest.setResponseTimeoutMillis(0L);
        try {
            final ExtendedResult streamValuesResult = conn.processExtendedOperation(streamValuesRequest);
            this.err("Processing completed.  ", this.dnsWritten.get(), " DNs written.");
            return streamValuesResult.getResultCode();
        }
        catch (final LDAPException le2) {
            this.err("Unable  to send the stream directory values extended request to the Directory Server:  ", le2.getExceptionMessage());
            return le2.getResultCode();
        }
        finally {
            if (f != null) {
                this.outputStream.close();
            }
            conn.close();
        }
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> exampleMap = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "--hostname", "server.example.com", "--port", "389", "--bindDN", "uid=admin,dc=example,dc=com", "--bindPassword", "password", "--baseDN", "dc=example,dc=com", "--outputFile", "example-dns.txt" };
        exampleMap.put(args, "Dump all entry DNs at or below 'dc=example,dc=com' to the file 'example-dns.txt'");
        return exampleMap;
    }
    
    @Override
    public void intermediateResponseReturned(final IntermediateResponse intermediateResponse) {
        StreamDirectoryValuesIntermediateResponse streamValuesIR;
        try {
            streamValuesIR = new StreamDirectoryValuesIntermediateResponse(intermediateResponse);
        }
        catch (final LDAPException le) {
            this.err("Unable to parse an intermediate response message as a stream directory values intermediate response:  ", le.getExceptionMessage());
            return;
        }
        final String diagnosticMessage = streamValuesIR.getDiagnosticMessage();
        if (diagnosticMessage != null && !diagnosticMessage.isEmpty()) {
            this.err(diagnosticMessage);
        }
        final List<ASN1OctetString> values = streamValuesIR.getValues();
        if (values != null && !values.isEmpty()) {
            for (final ASN1OctetString s : values) {
                this.outputStream.println(s.toString());
            }
            final long updatedCount = this.dnsWritten.addAndGet(values.size());
            if (this.outputFile.isPresent()) {
                this.err(updatedCount, " DNs written.");
            }
        }
    }
}
