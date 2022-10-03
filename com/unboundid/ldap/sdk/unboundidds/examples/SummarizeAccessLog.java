package com.unboundid.ldap.sdk.unboundidds.examples;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import com.unboundid.util.ReverseComparator;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.unboundidds.logs.AccessLogMessage;
import java.util.Iterator;
import com.unboundid.util.ObjectPair;
import java.util.Map;
import com.unboundid.ldap.sdk.unboundidds.logs.SearchResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.ModifyDNResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.ModifyResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.ExtendedResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.DeleteResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.CompareResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.BindResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.AddResultAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.UnbindRequestAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.SearchRequestAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.ExtendedRequestAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.AbandonRequestAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.OperationAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.DisconnectAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.ConnectAccessLogMessage;
import com.unboundid.ldap.sdk.unboundidds.logs.LogException;
import java.io.IOException;
import javax.crypto.BadPaddingException;
import java.io.Reader;
import com.unboundid.ldap.sdk.unboundidds.logs.AccessLogReader;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Debug;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolUtils;
import java.util.List;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.Argument;
import com.unboundid.util.StaticUtils;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.HashSet;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.concurrent.atomic.AtomicLong;
import java.util.HashMap;
import java.text.DecimalFormat;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import com.unboundid.util.CommandLineTool;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class SummarizeAccessLog extends CommandLineTool implements Serializable
{
    private static final long serialVersionUID = 7189168366509887130L;
    private ArgumentParser argumentParser;
    private BooleanArgument isCompressed;
    private FileArgument encryptionPassphraseFile;
    private final DecimalFormat decimalFormat;
    private long logDurationMillis;
    private double addProcessingDuration;
    private double bindProcessingDuration;
    private double compareProcessingDuration;
    private double deleteProcessingDuration;
    private double extendedProcessingDuration;
    private double modifyProcessingDuration;
    private double modifyDNProcessingDuration;
    private double searchProcessingDuration;
    private long numAbandons;
    private long numAdds;
    private long numBinds;
    private long numCompares;
    private long numConnects;
    private long numDeletes;
    private long numDisconnects;
    private long numExtended;
    private long numModifies;
    private long numModifyDNs;
    private long numNonBaseSearches;
    private long numSearches;
    private long numUnbinds;
    private long numUncachedAdds;
    private long numUncachedBinds;
    private long numUncachedCompares;
    private long numUncachedDeletes;
    private long numUncachedExtended;
    private long numUncachedModifies;
    private long numUncachedModifyDNs;
    private long numUncachedSearches;
    private long numUnindexedAttempts;
    private long numUnindexedFailed;
    private long numUnindexedSuccessful;
    private final HashMap<Long, AtomicLong> searchEntryCounts;
    private final HashMap<ResultCode, AtomicLong> addResultCodes;
    private final HashMap<ResultCode, AtomicLong> bindResultCodes;
    private final HashMap<ResultCode, AtomicLong> compareResultCodes;
    private final HashMap<ResultCode, AtomicLong> deleteResultCodes;
    private final HashMap<ResultCode, AtomicLong> extendedResultCodes;
    private final HashMap<ResultCode, AtomicLong> modifyResultCodes;
    private final HashMap<ResultCode, AtomicLong> modifyDNResultCodes;
    private final HashMap<ResultCode, AtomicLong> searchResultCodes;
    private final HashMap<SearchScope, AtomicLong> searchScopes;
    private final HashMap<String, AtomicLong> clientAddresses;
    private final HashMap<String, AtomicLong> clientConnectionPolicies;
    private final HashMap<String, AtomicLong> disconnectReasons;
    private final HashMap<String, AtomicLong> extendedOperations;
    private final HashMap<String, AtomicLong> filterTypes;
    private final HashSet<String> processedRequests;
    private final LinkedHashMap<Long, AtomicLong> addProcessingTimes;
    private final LinkedHashMap<Long, AtomicLong> bindProcessingTimes;
    private final LinkedHashMap<Long, AtomicLong> compareProcessingTimes;
    private final LinkedHashMap<Long, AtomicLong> deleteProcessingTimes;
    private final LinkedHashMap<Long, AtomicLong> extendedProcessingTimes;
    private final LinkedHashMap<Long, AtomicLong> modifyProcessingTimes;
    private final LinkedHashMap<Long, AtomicLong> modifyDNProcessingTimes;
    private final LinkedHashMap<Long, AtomicLong> searchProcessingTimes;
    
    public static void main(final String[] args) {
        final ResultCode resultCode = main(args, System.out, System.err);
        if (resultCode != ResultCode.SUCCESS) {
            System.exit(resultCode.intValue());
        }
    }
    
    public static ResultCode main(final String[] args, final OutputStream outStream, final OutputStream errStream) {
        final SummarizeAccessLog summarizer = new SummarizeAccessLog(outStream, errStream);
        return summarizer.runTool(args);
    }
    
    public SummarizeAccessLog(final OutputStream outStream, final OutputStream errStream) {
        super(outStream, errStream);
        this.decimalFormat = new DecimalFormat("0.000");
        this.logDurationMillis = 0L;
        this.addProcessingDuration = 0.0;
        this.bindProcessingDuration = 0.0;
        this.compareProcessingDuration = 0.0;
        this.deleteProcessingDuration = 0.0;
        this.extendedProcessingDuration = 0.0;
        this.modifyProcessingDuration = 0.0;
        this.modifyDNProcessingDuration = 0.0;
        this.searchProcessingDuration = 0.0;
        this.numAbandons = 0L;
        this.numAdds = 0L;
        this.numBinds = 0L;
        this.numCompares = 0L;
        this.numConnects = 0L;
        this.numDeletes = 0L;
        this.numDisconnects = 0L;
        this.numExtended = 0L;
        this.numModifies = 0L;
        this.numModifyDNs = 0L;
        this.numNonBaseSearches = 0L;
        this.numSearches = 0L;
        this.numUnbinds = 0L;
        this.numUncachedAdds = 0L;
        this.numUncachedBinds = 0L;
        this.numUncachedCompares = 0L;
        this.numUncachedDeletes = 0L;
        this.numUncachedExtended = 0L;
        this.numUncachedModifies = 0L;
        this.numUncachedModifyDNs = 0L;
        this.numUncachedSearches = 0L;
        this.searchEntryCounts = new HashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.addResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.bindResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.compareResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.deleteResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.extendedResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.modifyResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.modifyDNResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.searchResultCodes = new HashMap<ResultCode, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.searchScopes = new HashMap<SearchScope, AtomicLong>(StaticUtils.computeMapCapacity(4));
        this.clientAddresses = new HashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(100));
        this.clientConnectionPolicies = new HashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(100));
        this.disconnectReasons = new HashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(100));
        this.extendedOperations = new HashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(10));
        this.filterTypes = new HashMap<String, AtomicLong>(StaticUtils.computeMapCapacity(100));
        this.processedRequests = new HashSet<String>(StaticUtils.computeMapCapacity(100));
        this.addProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        this.bindProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        this.compareProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        this.deleteProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        this.extendedProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        this.modifyProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        this.modifyDNProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        this.searchProcessingTimes = new LinkedHashMap<Long, AtomicLong>(StaticUtils.computeMapCapacity(11));
        populateProcessingTimeMap(this.addProcessingTimes);
        populateProcessingTimeMap(this.bindProcessingTimes);
        populateProcessingTimeMap(this.compareProcessingTimes);
        populateProcessingTimeMap(this.deleteProcessingTimes);
        populateProcessingTimeMap(this.extendedProcessingTimes);
        populateProcessingTimeMap(this.modifyProcessingTimes);
        populateProcessingTimeMap(this.modifyDNProcessingTimes);
        populateProcessingTimeMap(this.searchProcessingTimes);
    }
    
    @Override
    public String getToolName() {
        return "summarize-access-log";
    }
    
    @Override
    public String getToolDescription() {
        return "Examine one or more access log files from Ping Identity, UnboundID, or Nokia/Alcatel-Lucent 8661 server products to display a number of metrics about operations processed within the server.";
    }
    
    @Override
    public String getToolVersion() {
        return "4.0.14";
    }
    
    @Override
    public int getMinTrailingArguments() {
        return 1;
    }
    
    @Override
    public int getMaxTrailingArguments() {
        return -1;
    }
    
    @Override
    public String getTrailingArgumentsPlaceholder() {
        return "{path}";
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
    public boolean supportsPropertiesFile() {
        return true;
    }
    
    @Override
    public void addToolArguments(final ArgumentParser parser) throws ArgumentException {
        this.argumentParser = parser;
        String description = "Indicates that the log file is compressed.";
        (this.isCompressed = new BooleanArgument('c', "isCompressed", description)).addLongIdentifier("is-compressed", true);
        this.isCompressed.addLongIdentifier("compressed", true);
        this.isCompressed.setHidden(true);
        parser.addArgument(this.isCompressed);
        description = "Indicates that the log file is encrypted and that the encryption passphrase is contained in the specified file.  If the log data is encrypted and this argument is not provided, then the tool will interactively prompt for the encryption passphrase.";
        (this.encryptionPassphraseFile = new FileArgument(null, "encryptionPassphraseFile", false, 1, null, description, true, true, true, false)).addLongIdentifier("encryption-passphrase-file", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryptionPasswordFile", true);
        this.encryptionPassphraseFile.addLongIdentifier("encryption-password-file", true);
        parser.addArgument(this.encryptionPassphraseFile);
    }
    
    @Override
    public void doExtendedArgumentValidation() throws ArgumentException {
        final List<String> trailingArguments = this.argumentParser.getTrailingArguments();
        if (trailingArguments == null || trailingArguments.isEmpty()) {
            throw new ArgumentException("No access log file paths were provided.");
        }
    }
    
    @Override
    public ResultCode doToolProcessing() {
        String encryptionPassphrase = null;
        if (this.encryptionPassphraseFile.isPresent()) {
            try {
                encryptionPassphrase = ToolUtils.readEncryptionPassphraseFromFile(this.encryptionPassphraseFile.getValue());
            }
            catch (final LDAPException e) {
                Debug.debugException(e);
                this.err(e.getMessage());
                return e.getResultCode();
            }
        }
        long logLines = 0L;
        for (final String path : this.argumentParser.getTrailingArguments()) {
            final File f = new File(path);
            this.out("Examining access log ", f.getAbsolutePath());
            AccessLogReader reader = null;
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(f);
                final ObjectPair<InputStream, String> p = ToolUtils.getPossiblyPassphraseEncryptedInputStream(inputStream, encryptionPassphrase, !this.encryptionPassphraseFile.isPresent(), "Log file '" + path + "' is encrypted.  Please enter the " + "encryption passphrase:", "ERROR:  The provided passphrase was incorrect.", this.getOut(), this.getErr());
                inputStream = p.getFirst();
                if (p.getSecond() != null && encryptionPassphrase == null) {
                    encryptionPassphrase = p.getSecond();
                }
                if (this.isCompressed.isPresent()) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                else {
                    inputStream = ToolUtils.getPossiblyGZIPCompressedInputStream(inputStream);
                }
                reader = new AccessLogReader(new InputStreamReader(inputStream));
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                this.err("Unable to open access log file ", f.getAbsolutePath(), ":  ", StaticUtils.getExceptionMessage(e2));
                return ResultCode.LOCAL_ERROR;
            }
            finally {
                if (reader == null && inputStream != null) {
                    try {
                        inputStream.close();
                    }
                    catch (final Exception e3) {
                        Debug.debugException(e3);
                    }
                }
            }
            long startTime = 0L;
            long stopTime = 0L;
            while (true) {
                AccessLogMessage msg;
                try {
                    msg = reader.read();
                }
                catch (final IOException ioe) {
                    Debug.debugException(ioe);
                    this.err("Error reading from access log file ", f.getAbsolutePath(), ":  ", StaticUtils.getExceptionMessage(ioe));
                    if (ioe.getCause() != null && ioe.getCause() instanceof BadPaddingException) {
                        this.err("This error is likely because the log is encrypted and the server still has the log file open.  It is recommended that you only try to examine encrypted logs after they have been rotated.  You can use the rotate-log tool to force a rotation at any time.  Attempting to proceed with just the data that was successfully read.");
                        break;
                    }
                    return ResultCode.LOCAL_ERROR;
                }
                catch (final LogException le) {
                    Debug.debugException(le);
                    this.err("Encountered an error while attempting to parse a line inaccess log file ", f.getAbsolutePath(), ":  ", StaticUtils.getExceptionMessage(le));
                    continue;
                }
                if (msg == null) {
                    break;
                }
                ++logLines;
                stopTime = msg.getTimestamp().getTime();
                if (startTime == 0L) {
                    startTime = stopTime;
                }
                switch (msg.getMessageType()) {
                    case CONNECT: {
                        this.processConnect((ConnectAccessLogMessage)msg);
                        continue;
                    }
                    case DISCONNECT: {
                        this.processDisconnect((DisconnectAccessLogMessage)msg);
                        continue;
                    }
                    case REQUEST: {
                        switch (((OperationAccessLogMessage)msg).getOperationType()) {
                            case ABANDON: {
                                this.processAbandonRequest((AbandonRequestAccessLogMessage)msg);
                                continue;
                            }
                            case EXTENDED: {
                                this.processExtendedRequest((ExtendedRequestAccessLogMessage)msg);
                                continue;
                            }
                            case SEARCH: {
                                this.processSearchRequest((SearchRequestAccessLogMessage)msg);
                                continue;
                            }
                            case UNBIND: {
                                this.processUnbindRequest((UnbindRequestAccessLogMessage)msg);
                                continue;
                            }
                        }
                        continue;
                    }
                    case RESULT: {
                        switch (((OperationAccessLogMessage)msg).getOperationType()) {
                            case ADD: {
                                this.processAddResult((AddResultAccessLogMessage)msg);
                                continue;
                            }
                            case BIND: {
                                this.processBindResult((BindResultAccessLogMessage)msg);
                                continue;
                            }
                            case COMPARE: {
                                this.processCompareResult((CompareResultAccessLogMessage)msg);
                                continue;
                            }
                            case DELETE: {
                                this.processDeleteResult((DeleteResultAccessLogMessage)msg);
                                continue;
                            }
                            case EXTENDED: {
                                this.processExtendedResult((ExtendedResultAccessLogMessage)msg);
                                continue;
                            }
                            case MODIFY: {
                                this.processModifyResult((ModifyResultAccessLogMessage)msg);
                                continue;
                            }
                            case MODDN: {
                                this.processModifyDNResult((ModifyDNResultAccessLogMessage)msg);
                                continue;
                            }
                            case SEARCH: {
                                this.processSearchResult((SearchResultAccessLogMessage)msg);
                                continue;
                            }
                        }
                        continue;
                    }
                }
            }
            try {
                reader.close();
            }
            catch (final Exception e3) {
                Debug.debugException(e3);
            }
            this.logDurationMillis += stopTime - startTime;
        }
        final int numFiles = this.argumentParser.getTrailingArguments().size();
        this.out(new Object[0]);
        this.out("Examined ", logLines, " lines in ", numFiles, (numFiles == 1) ? " file" : " files", " covering a total duration of ", StaticUtils.millisToHumanReadableDuration(this.logDurationMillis));
        if (logLines == 0L) {
            return ResultCode.SUCCESS;
        }
        this.out(new Object[0]);
        final double logDurationSeconds = this.logDurationMillis / 1000.0;
        final double connectsPerSecond = this.numConnects / logDurationSeconds;
        final double disconnectsPerSecond = this.numDisconnects / logDurationSeconds;
        this.out("Total connections established:  ", this.numConnects, " (", this.decimalFormat.format(connectsPerSecond), "/second)");
        this.out("Total disconnects:  ", this.numDisconnects, " (", this.decimalFormat.format(disconnectsPerSecond), "/second)");
        if (!this.clientAddresses.isEmpty()) {
            this.out(new Object[0]);
            final List<ObjectPair<String, Long>> connectCounts = getMostCommonElements(this.clientAddresses, 20);
            this.out("Most common client addresses:");
            for (final ObjectPair<String, Long> p2 : connectCounts) {
                final long count = p2.getSecond();
                final double percent = 100.0 * count / this.numConnects;
                this.out(p2.getFirst(), ":  ", count, " (", this.decimalFormat.format(percent), ")");
            }
        }
        if (!this.clientConnectionPolicies.isEmpty()) {
            long totalCCPs = 0L;
            for (final AtomicLong l : this.clientConnectionPolicies.values()) {
                totalCCPs += l.get();
            }
            final List<ObjectPair<String, Long>> reasonCounts = getMostCommonElements(this.clientConnectionPolicies, 20);
            this.out(new Object[0]);
            this.out("Most common client connection policies:");
            for (final ObjectPair<String, Long> p3 : reasonCounts) {
                final long count2 = p3.getSecond();
                final double percent2 = 100.0 * count2 / totalCCPs;
                this.out(p3.getFirst(), ":  ", p3.getSecond(), " (", this.decimalFormat.format(percent2), "%)");
            }
        }
        if (!this.disconnectReasons.isEmpty()) {
            final List<ObjectPair<String, Long>> reasonCounts2 = getMostCommonElements(this.disconnectReasons, 20);
            this.out(new Object[0]);
            this.out("Most common disconnect reasons:");
            for (final ObjectPair<String, Long> p2 : reasonCounts2) {
                final long count = p2.getSecond();
                final double percent = 100.0 * count / this.numDisconnects;
                this.out(p2.getFirst(), ":  ", p2.getSecond(), " (", this.decimalFormat.format(percent), "%)");
            }
        }
        final long totalOps = this.numAbandons + this.numAdds + this.numBinds + this.numCompares + this.numDeletes + this.numExtended + this.numModifies + this.numModifyDNs + this.numSearches + this.numUnbinds;
        if (totalOps > 0L) {
            final double percentAbandon = 100.0 * this.numAbandons / totalOps;
            final double percentAdd = 100.0 * this.numAdds / totalOps;
            final double percentBind = 100.0 * this.numBinds / totalOps;
            final double percentCompare = 100.0 * this.numCompares / totalOps;
            final double percentDelete = 100.0 * this.numDeletes / totalOps;
            final double percentExtended = 100.0 * this.numExtended / totalOps;
            final double percentModify = 100.0 * this.numModifies / totalOps;
            final double percentModifyDN = 100.0 * this.numModifyDNs / totalOps;
            final double percentSearch = 100.0 * this.numSearches / totalOps;
            final double percentUnbind = 100.0 * this.numUnbinds / totalOps;
            final double abandonsPerSecond = this.numAbandons / logDurationSeconds;
            final double addsPerSecond = this.numAdds / logDurationSeconds;
            final double bindsPerSecond = this.numBinds / logDurationSeconds;
            final double comparesPerSecond = this.numCompares / logDurationSeconds;
            final double deletesPerSecond = this.numDeletes / logDurationSeconds;
            final double extendedPerSecond = this.numExtended / logDurationSeconds;
            final double modifiesPerSecond = this.numModifies / logDurationSeconds;
            final double modifyDNsPerSecond = this.numModifyDNs / logDurationSeconds;
            final double searchesPerSecond = this.numSearches / logDurationSeconds;
            final double unbindsPerSecond = this.numUnbinds / logDurationSeconds;
            this.out(new Object[0]);
            this.out("Total operations examined:  ", totalOps);
            this.out("Abandon operations examined:  ", this.numAbandons, " (", this.decimalFormat.format(percentAbandon), "%, ", this.decimalFormat.format(abandonsPerSecond), "/second)");
            this.out("Add operations examined:  ", this.numAdds, " (", this.decimalFormat.format(percentAdd), "%, ", this.decimalFormat.format(addsPerSecond), "/second)");
            this.out("Bind operations examined:  ", this.numBinds, " (", this.decimalFormat.format(percentBind), "%, ", this.decimalFormat.format(bindsPerSecond), "/second)");
            this.out("Compare operations examined:  ", this.numCompares, " (", this.decimalFormat.format(percentCompare), "%, ", this.decimalFormat.format(comparesPerSecond), "/second)");
            this.out("Delete operations examined:  ", this.numDeletes, " (", this.decimalFormat.format(percentDelete), "%, ", this.decimalFormat.format(deletesPerSecond), "/second)");
            this.out("Extended operations examined:  ", this.numExtended, " (", this.decimalFormat.format(percentExtended), "%, ", this.decimalFormat.format(extendedPerSecond), "/second)");
            this.out("Modify operations examined:  ", this.numModifies, " (", this.decimalFormat.format(percentModify), "%, ", this.decimalFormat.format(modifiesPerSecond), "/second)");
            this.out("Modify DN operations examined:  ", this.numModifyDNs, " (", this.decimalFormat.format(percentModifyDN), "%, ", this.decimalFormat.format(modifyDNsPerSecond), "/second)");
            this.out("Search operations examined:  ", this.numSearches, " (", this.decimalFormat.format(percentSearch), "%, ", this.decimalFormat.format(searchesPerSecond), "/second)");
            this.out("Unbind operations examined:  ", this.numUnbinds, " (", this.decimalFormat.format(percentUnbind), "%, ", this.decimalFormat.format(unbindsPerSecond), "/second)");
            final double totalProcessingDuration = this.addProcessingDuration + this.bindProcessingDuration + this.compareProcessingDuration + this.deleteProcessingDuration + this.extendedProcessingDuration + this.modifyProcessingDuration + this.modifyDNProcessingDuration + this.searchProcessingDuration;
            this.out(new Object[0]);
            this.out("Average operation processing duration:  ", this.decimalFormat.format(totalProcessingDuration / totalOps), "ms");
            if (this.numAdds > 0L) {
                this.out("Average add operation processing duration:  ", this.decimalFormat.format(this.addProcessingDuration / this.numAdds), "ms");
            }
            if (this.numBinds > 0L) {
                this.out("Average bind operation processing duration:  ", this.decimalFormat.format(this.bindProcessingDuration / this.numBinds), "ms");
            }
            if (this.numCompares > 0L) {
                this.out("Average compare operation processing duration:  ", this.decimalFormat.format(this.compareProcessingDuration / this.numCompares), "ms");
            }
            if (this.numDeletes > 0L) {
                this.out("Average delete operation processing duration:  ", this.decimalFormat.format(this.deleteProcessingDuration / this.numDeletes), "ms");
            }
            if (this.numExtended > 0L) {
                this.out("Average extended operation processing duration:  ", this.decimalFormat.format(this.extendedProcessingDuration / this.numExtended), "ms");
            }
            if (this.numModifies > 0L) {
                this.out("Average modify operation processing duration:  ", this.decimalFormat.format(this.modifyProcessingDuration / this.numModifies), "ms");
            }
            if (this.numModifyDNs > 0L) {
                this.out("Average modify DN operation processing duration:  ", this.decimalFormat.format(this.modifyDNProcessingDuration / this.numModifyDNs), "ms");
            }
            if (this.numSearches > 0L) {
                this.out("Average search operation processing duration:  ", this.decimalFormat.format(this.searchProcessingDuration / this.numSearches), "ms");
            }
            this.printProcessingTimeHistogram("add", this.numAdds, this.addProcessingTimes);
            this.printProcessingTimeHistogram("bind", this.numBinds, this.bindProcessingTimes);
            this.printProcessingTimeHistogram("compare", this.numCompares, this.compareProcessingTimes);
            this.printProcessingTimeHistogram("delete", this.numDeletes, this.deleteProcessingTimes);
            this.printProcessingTimeHistogram("extended", this.numExtended, this.extendedProcessingTimes);
            this.printProcessingTimeHistogram("modify", this.numModifies, this.modifyProcessingTimes);
            this.printProcessingTimeHistogram("modify DN", this.numModifyDNs, this.modifyDNProcessingTimes);
            this.printProcessingTimeHistogram("search", this.numSearches, this.searchProcessingTimes);
            if (!this.addResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.addResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common add operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numAdds;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.bindResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.bindResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common bind operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numBinds;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.compareResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.compareResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common compare operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numCompares;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.deleteResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.deleteResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common delete operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numDeletes;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.extendedResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.extendedResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common extended operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numExtended;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.modifyResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.modifyResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common modify operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numModifies;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.modifyDNResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.modifyDNResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common modify DN operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numModifyDNs;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.searchResultCodes.isEmpty()) {
                final List<ObjectPair<ResultCode, Long>> rcCounts = getMostCommonElements(this.searchResultCodes, 20);
                this.out(new Object[0]);
                this.out("Most common search operation result codes:");
                for (final ObjectPair<ResultCode, Long> p4 : rcCounts) {
                    final long count3 = p4.getSecond();
                    final double percent3 = 100.0 * count3 / this.numSearches;
                    this.out(p4.getFirst().getName(), ":  ", p4.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.extendedOperations.isEmpty()) {
                final List<ObjectPair<String, Long>> extOpCounts = getMostCommonElements(this.extendedOperations, 20);
                this.out(new Object[0]);
                this.out("Most common extended operation types:");
                for (final ObjectPair<String, Long> p5 : extOpCounts) {
                    final long count3 = p5.getSecond();
                    final double percent3 = 100.0 * count3 / this.numExtended;
                    this.out(p5.getFirst(), ":  ", p5.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            this.out(new Object[0]);
            this.out("Number of unindexed search attempts:  ", this.numUnindexedAttempts);
            this.out("Number of successfully-completed unindexed searches:  ", this.numUnindexedSuccessful);
            this.out("Number of failed unindexed searches:  ", this.numUnindexedFailed);
            if (!this.searchScopes.isEmpty()) {
                final List<ObjectPair<SearchScope, Long>> scopeCounts = getMostCommonElements(this.searchScopes, 20);
                this.out(new Object[0]);
                this.out("Most common search scopes:");
                for (final ObjectPair<SearchScope, Long> p6 : scopeCounts) {
                    final long count3 = p6.getSecond();
                    final double percent3 = 100.0 * count3 / this.numSearches;
                    this.out(p6.getFirst().getName(), ":  ", p6.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.searchEntryCounts.isEmpty()) {
                final List<ObjectPair<Long, Long>> entryCounts = getMostCommonElements(this.searchEntryCounts, 20);
                this.out(new Object[0]);
                this.out("Most common search entry counts:");
                for (final ObjectPair<Long, Long> p7 : entryCounts) {
                    final long count3 = p7.getSecond();
                    final double percent3 = 100.0 * count3 / this.numSearches;
                    this.out(p7.getFirst(), ":  ", p7.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
            if (!this.filterTypes.isEmpty()) {
                final List<ObjectPair<String, Long>> filterCounts = getMostCommonElements(this.filterTypes, 20);
                this.out(new Object[0]);
                this.out("Most common generic filters for searches with a non-base scope:");
                for (final ObjectPair<String, Long> p5 : filterCounts) {
                    final long count3 = p5.getSecond();
                    final double percent3 = 100.0 * count3 / this.numNonBaseSearches;
                    this.out(p5.getFirst(), ":  ", p5.getSecond(), " (", this.decimalFormat.format(percent3), "%)");
                }
            }
        }
        final long totalUncached = this.numUncachedAdds + this.numUncachedBinds + this.numUncachedCompares + this.numUncachedDeletes + this.numUncachedExtended + this.numUncachedModifies + this.numUncachedModifyDNs + this.numUncachedSearches;
        if (totalUncached > 0L) {
            this.out(new Object[0]);
            this.out("Operations accessing uncached data:");
            this.printUncached("Add", this.numUncachedAdds, this.numAdds);
            this.printUncached("Bind", this.numUncachedBinds, this.numBinds);
            this.printUncached("Compare", this.numUncachedCompares, this.numCompares);
            this.printUncached("Delete", this.numUncachedDeletes, this.numDeletes);
            this.printUncached("Extended", this.numUncachedExtended, this.numExtended);
            this.printUncached("Modify", this.numUncachedModifies, this.numModifies);
            this.printUncached("Modify DN", this.numUncachedModifyDNs, this.numModifyDNs);
            this.printUncached("Search", this.numUncachedSearches, this.numSearches);
        }
        return ResultCode.SUCCESS;
    }
    
    @Override
    public LinkedHashMap<String[], String> getExampleUsages() {
        final LinkedHashMap<String[], String> examples = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(1));
        final String[] args = { "/ds/logs/access" };
        final String description = "Analyze the contents of the /ds/logs/access access log file.";
        examples.put(args, "Analyze the contents of the /ds/logs/access access log file.");
        return examples;
    }
    
    private static void populateProcessingTimeMap(final HashMap<Long, AtomicLong> m) {
        m.put(1L, new AtomicLong(0L));
        m.put(2L, new AtomicLong(0L));
        m.put(3L, new AtomicLong(0L));
        m.put(5L, new AtomicLong(0L));
        m.put(10L, new AtomicLong(0L));
        m.put(20L, new AtomicLong(0L));
        m.put(30L, new AtomicLong(0L));
        m.put(50L, new AtomicLong(0L));
        m.put(100L, new AtomicLong(0L));
        m.put(1000L, new AtomicLong(0L));
        m.put(Long.MAX_VALUE, new AtomicLong(0L));
    }
    
    private void processConnect(final ConnectAccessLogMessage m) {
        ++this.numConnects;
        final String clientAddr = m.getSourceAddress();
        if (clientAddr != null) {
            AtomicLong count = this.clientAddresses.get(clientAddr);
            if (count == null) {
                count = new AtomicLong(0L);
                this.clientAddresses.put(clientAddr, count);
            }
            count.incrementAndGet();
        }
        final String ccp = m.getClientConnectionPolicy();
        if (ccp != null) {
            AtomicLong l = this.clientConnectionPolicies.get(ccp);
            if (l == null) {
                l = new AtomicLong(0L);
                this.clientConnectionPolicies.put(ccp, l);
            }
            l.incrementAndGet();
        }
    }
    
    private void processDisconnect(final DisconnectAccessLogMessage m) {
        ++this.numDisconnects;
        final String reason = m.getDisconnectReason();
        if (reason != null) {
            AtomicLong l = this.disconnectReasons.get(reason);
            if (l == null) {
                l = new AtomicLong(0L);
                this.disconnectReasons.put(reason, l);
            }
            l.incrementAndGet();
        }
    }
    
    private void processAbandonRequest(final AbandonRequestAccessLogMessage m) {
        ++this.numAbandons;
    }
    
    private void processExtendedRequest(final ExtendedRequestAccessLogMessage m) {
        this.processedRequests.add(m.getConnectionID() + "-" + m.getOperationID());
        this.processExtendedRequestInternal(m);
    }
    
    private void processExtendedRequestInternal(final ExtendedRequestAccessLogMessage m) {
        final String oid = m.getRequestOID();
        if (oid != null) {
            AtomicLong l = this.extendedOperations.get(oid);
            if (l == null) {
                l = new AtomicLong(0L);
                this.extendedOperations.put(oid, l);
            }
            l.incrementAndGet();
        }
    }
    
    private void processSearchRequest(final SearchRequestAccessLogMessage m) {
        this.processedRequests.add(m.getConnectionID() + "-" + m.getOperationID());
        this.processSearchRequestInternal(m);
    }
    
    private void processSearchRequestInternal(final SearchRequestAccessLogMessage m) {
        final SearchScope scope = m.getScope();
        if (scope != null) {
            if (scope != SearchScope.BASE) {
                ++this.numNonBaseSearches;
            }
            AtomicLong scopeCount = this.searchScopes.get(scope);
            if (scopeCount == null) {
                scopeCount = new AtomicLong(0L);
                this.searchScopes.put(scope, scopeCount);
            }
            scopeCount.incrementAndGet();
            if (!scope.equals(SearchScope.BASE)) {
                final Filter filter = m.getParsedFilter();
                if (filter != null) {
                    final String genericString = new GenericFilter(filter).toString();
                    AtomicLong filterCount = this.filterTypes.get(genericString);
                    if (filterCount == null) {
                        filterCount = new AtomicLong(0L);
                        this.filterTypes.put(genericString, filterCount);
                    }
                    filterCount.incrementAndGet();
                }
            }
        }
    }
    
    private void processUnbindRequest(final UnbindRequestAccessLogMessage m) {
        ++this.numUnbinds;
    }
    
    private void processAddResult(final AddResultAccessLogMessage m) {
        ++this.numAdds;
        updateResultCodeCount(m.getResultCode(), this.addResultCodes);
        this.addProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.addProcessingTimes);
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedAdds;
        }
    }
    
    private void processBindResult(final BindResultAccessLogMessage m) {
        ++this.numBinds;
        updateResultCodeCount(m.getResultCode(), this.bindResultCodes);
        this.bindProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.bindProcessingTimes);
        final String ccp = m.getClientConnectionPolicy();
        if (ccp != null) {
            AtomicLong l = this.clientConnectionPolicies.get(ccp);
            if (l == null) {
                l = new AtomicLong(0L);
                this.clientConnectionPolicies.put(ccp, l);
            }
            l.incrementAndGet();
        }
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedBinds;
        }
    }
    
    private void processCompareResult(final CompareResultAccessLogMessage m) {
        ++this.numCompares;
        updateResultCodeCount(m.getResultCode(), this.compareResultCodes);
        this.compareProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.compareProcessingTimes);
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedCompares;
        }
    }
    
    private void processDeleteResult(final DeleteResultAccessLogMessage m) {
        ++this.numDeletes;
        updateResultCodeCount(m.getResultCode(), this.deleteResultCodes);
        this.deleteProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.deleteProcessingTimes);
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedDeletes;
        }
    }
    
    private void processExtendedResult(final ExtendedResultAccessLogMessage m) {
        ++this.numExtended;
        final String id = m.getConnectionID() + "-" + m.getOperationID();
        if (!this.processedRequests.remove(id)) {
            this.processExtendedRequestInternal(m);
        }
        updateResultCodeCount(m.getResultCode(), this.extendedResultCodes);
        this.extendedProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.extendedProcessingTimes);
        final String ccp = m.getClientConnectionPolicy();
        if (ccp != null) {
            AtomicLong l = this.clientConnectionPolicies.get(ccp);
            if (l == null) {
                l = new AtomicLong(0L);
                this.clientConnectionPolicies.put(ccp, l);
            }
            l.incrementAndGet();
        }
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedExtended;
        }
    }
    
    private void processModifyResult(final ModifyResultAccessLogMessage m) {
        ++this.numModifies;
        updateResultCodeCount(m.getResultCode(), this.modifyResultCodes);
        this.modifyProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.modifyProcessingTimes);
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedModifies;
        }
    }
    
    private void processModifyDNResult(final ModifyDNResultAccessLogMessage m) {
        ++this.numModifyDNs;
        updateResultCodeCount(m.getResultCode(), this.modifyDNResultCodes);
        this.modifyDNProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.modifyDNProcessingTimes);
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedModifyDNs;
        }
    }
    
    private void processSearchResult(final SearchResultAccessLogMessage m) {
        ++this.numSearches;
        final String id = m.getConnectionID() + "-" + m.getOperationID();
        if (!this.processedRequests.remove(id)) {
            this.processSearchRequestInternal(m);
        }
        final ResultCode resultCode = m.getResultCode();
        updateResultCodeCount(resultCode, this.searchResultCodes);
        this.searchProcessingDuration += doubleValue(m.getProcessingTimeMillis(), this.searchProcessingTimes);
        final Long entryCount = m.getEntriesReturned();
        if (entryCount != null) {
            AtomicLong l = this.searchEntryCounts.get(entryCount);
            if (l == null) {
                l = new AtomicLong(0L);
                this.searchEntryCounts.put(entryCount, l);
            }
            l.incrementAndGet();
        }
        final Boolean isUnindexed = m.isUnindexed();
        if (isUnindexed != null && isUnindexed) {
            ++this.numUnindexedAttempts;
            if (resultCode == ResultCode.SUCCESS) {
                ++this.numUnindexedSuccessful;
            }
            else {
                ++this.numUnindexedFailed;
            }
        }
        final Boolean uncachedDataAccessed = m.getUncachedDataAccessed();
        if (uncachedDataAccessed != null && uncachedDataAccessed) {
            ++this.numUncachedSearches;
        }
    }
    
    private static void updateResultCodeCount(final ResultCode rc, final HashMap<ResultCode, AtomicLong> m) {
        if (rc == null) {
            return;
        }
        AtomicLong l = m.get(rc);
        if (l == null) {
            l = new AtomicLong(0L);
            m.put(rc, l);
        }
        l.incrementAndGet();
    }
    
    private static double doubleValue(final Double d, final HashMap<Long, AtomicLong> m) {
        if (d == null) {
            return 0.0;
        }
        for (final Map.Entry<Long, AtomicLong> e : m.entrySet()) {
            if (d <= e.getKey()) {
                e.getValue().incrementAndGet();
                break;
            }
        }
        return d;
    }
    
    private static <K> List<ObjectPair<K, Long>> getMostCommonElements(final Map<K, AtomicLong> m, final int n) {
        final TreeMap<Long, List<K>> reverseMap = new TreeMap<Long, List<K>>(new ReverseComparator<Object>());
        for (final Map.Entry<K, AtomicLong> e : m.entrySet()) {
            final Long count = e.getValue().get();
            List<K> list = reverseMap.get(count);
            if (list == null) {
                list = new ArrayList<K>(n);
                reverseMap.put(count, list);
            }
            list.add(e.getKey());
        }
        final ArrayList<ObjectPair<K, Long>> returnList = new ArrayList<ObjectPair<K, Long>>(n);
        for (final Map.Entry<Long, List<K>> e2 : reverseMap.entrySet()) {
            final Long l = e2.getKey();
            for (final K k : e2.getValue()) {
                returnList.add(new ObjectPair<K, Long>(k, l));
            }
            if (returnList.size() >= n) {
                break;
            }
        }
        return returnList;
    }
    
    private void printProcessingTimeHistogram(final String t, final long n, final LinkedHashMap<Long, AtomicLong> m) {
        if (n <= 0L) {
            return;
        }
        this.out(new Object[0]);
        this.out("Count of ", t, " operations by processing time:");
        long lowerBound = 0L;
        long accumulatedCount = 0L;
        final Iterator<Map.Entry<Long, AtomicLong>> i = m.entrySet().iterator();
        while (i.hasNext()) {
            final Map.Entry<Long, AtomicLong> e = i.next();
            final long upperBound = e.getKey();
            final long count = e.getValue().get();
            final double categoryPercent = 100.0 * count / n;
            accumulatedCount += count;
            final double accumulatedPercent = 100.0 * accumulatedCount / n;
            if (i.hasNext()) {
                this.out("Between ", lowerBound, "ms and ", upperBound, "ms:  ", count, " (", this.decimalFormat.format(categoryPercent), "%, ", this.decimalFormat.format(accumulatedPercent), "% accumulated)");
                lowerBound = upperBound;
            }
            else {
                this.out("Greater than ", lowerBound, "ms:  ", count, " (", this.decimalFormat.format(categoryPercent), "%, ", this.decimalFormat.format(accumulatedPercent), "% accumulated)");
            }
        }
    }
    
    private void printUncached(final String operationType, final long numUncached, final long numTotal) {
        if (numUncached == 0L) {
            return;
        }
        this.out(operationType, ":  ", numUncached, " (", this.decimalFormat.format(100.0 * numUncached / numTotal), "%)");
    }
}
