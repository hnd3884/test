package com.unboundid.util;

import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolInvocationLogDetails;
import java.util.Iterator;
import java.util.TreeMap;
import java.io.File;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolInvocationLogShutdownHook;
import java.util.Set;
import com.unboundid.util.args.Argument;
import java.util.HashSet;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolInvocationLogger;
import java.util.concurrent.atomic.AtomicReference;
import java.io.FileOutputStream;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Collection;
import com.unboundid.util.args.ArgumentHelper;
import com.unboundid.util.args.SubCommand;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.BooleanArgument;

@Extensible
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public abstract class CommandLineTool
{
    private BooleanArgument appendToOutputFileArgument;
    private BooleanArgument helpArgument;
    private BooleanArgument helpSASLArgument;
    private BooleanArgument helpSubcommandsArgument;
    private BooleanArgument interactiveArgument;
    private BooleanArgument teeOutputArgument;
    private BooleanArgument versionArgument;
    private FileArgument outputFileArgument;
    private final List<BooleanArgument> enableSSLDebuggingArguments;
    private final PasswordFileReader passwordFileReader;
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private volatile PrintStream out;
    private volatile PrintStream err;
    
    public CommandLineTool(final OutputStream outStream, final OutputStream errStream) {
        this.appendToOutputFileArgument = null;
        this.helpArgument = null;
        this.helpSASLArgument = null;
        this.helpSubcommandsArgument = null;
        this.interactiveArgument = null;
        this.teeOutputArgument = null;
        this.versionArgument = null;
        this.outputFileArgument = null;
        if (outStream == null) {
            this.out = NullOutputStream.getPrintStream();
        }
        else {
            this.out = new PrintStream(outStream);
        }
        if (errStream == null) {
            this.err = NullOutputStream.getPrintStream();
        }
        else {
            this.err = new PrintStream(errStream);
        }
        this.originalOut = this.out;
        this.originalErr = this.err;
        this.passwordFileReader = new PasswordFileReader(this.out, this.err);
        this.enableSSLDebuggingArguments = new ArrayList<BooleanArgument>(1);
    }
    
    public final ResultCode runTool(final String... args) {
        ArgumentParser parser;
        try {
            parser = this.createArgumentParser();
            boolean exceptionFromParsingWithNoArgumentsExplicitlyProvided = false;
            Label_0055: {
                Label_0050: {
                    if (this.supportsInteractiveMode() && this.defaultsToInteractiveMode()) {
                        if (args != null) {
                            if (args.length != 0) {
                                break Label_0050;
                            }
                        }
                        try {
                            parser.parse(args);
                        }
                        catch (final Exception e) {
                            Debug.debugException(e);
                            exceptionFromParsingWithNoArgumentsExplicitlyProvided = true;
                        }
                        break Label_0055;
                    }
                }
                parser.parse(args);
            }
            final File generatedPropertiesFile = parser.getGeneratedPropertiesFile();
            if (this.supportsPropertiesFile() && generatedPropertiesFile != null) {
                this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS - 1, UtilityMessages.INFO_CL_TOOL_WROTE_PROPERTIES_FILE.get(generatedPropertiesFile.getAbsolutePath()));
                return ResultCode.SUCCESS;
            }
            if (this.helpArgument.isPresent()) {
                this.out(parser.getUsageString(StaticUtils.TERMINAL_WIDTH_COLUMNS - 1));
                this.displayExampleUsages(parser);
                return ResultCode.SUCCESS;
            }
            if (this.helpSASLArgument != null && this.helpSASLArgument.isPresent()) {
                this.out(SASLUtils.getUsageString(StaticUtils.TERMINAL_WIDTH_COLUMNS - 1));
                return ResultCode.SUCCESS;
            }
            if (this.helpSubcommandsArgument != null && this.helpSubcommandsArgument.isPresent()) {
                final TreeMap<String, SubCommand> subCommands = getSortedSubCommands(parser);
                for (final SubCommand sc : subCommands.values()) {
                    final StringBuilder nameBuffer = new StringBuilder();
                    final Iterator<String> nameIterator = sc.getNames(false).iterator();
                    while (nameIterator.hasNext()) {
                        nameBuffer.append(nameIterator.next());
                        if (nameIterator.hasNext()) {
                            nameBuffer.append(", ");
                        }
                    }
                    this.out(nameBuffer.toString());
                    for (final String descriptionLine : StaticUtils.wrapLine(sc.getDescription(), StaticUtils.TERMINAL_WIDTH_COLUMNS - 3)) {
                        this.out("  " + descriptionLine);
                    }
                    this.out(new Object[0]);
                }
                this.wrapOut(0, StaticUtils.TERMINAL_WIDTH_COLUMNS - 1, UtilityMessages.INFO_CL_TOOL_USE_SUBCOMMAND_HELP.get(this.getToolName()));
                return ResultCode.SUCCESS;
            }
            if (this.versionArgument != null && this.versionArgument.isPresent()) {
                this.out(this.getToolVersion());
                return ResultCode.SUCCESS;
            }
            for (final BooleanArgument a : this.enableSSLDebuggingArguments) {
                if (a.isPresent()) {
                    StaticUtils.setSystemProperty("javax.net.debug", "all");
                }
            }
            boolean extendedValidationDone = false;
            Label_0700: {
                if (this.interactiveArgument != null) {
                    if (!this.interactiveArgument.isPresent()) {
                        if (!this.defaultsToInteractiveMode() || (args != null && args.length != 0)) {
                            break Label_0700;
                        }
                        if (!parser.getArgumentsSetFromPropertiesFile().isEmpty()) {
                            if (!exceptionFromParsingWithNoArgumentsExplicitlyProvided) {
                                break Label_0700;
                            }
                        }
                    }
                    try {
                        final List<String> interactiveArgs = this.requestToolArgumentsInteractively(parser);
                        if (interactiveArgs == null) {
                            final CommandLineToolInteractiveModeProcessor processor = new CommandLineToolInteractiveModeProcessor(this, parser);
                            processor.doInteractiveModeProcessing();
                            extendedValidationDone = true;
                        }
                        else {
                            ArgumentHelper.reset(parser);
                            parser.parse(StaticUtils.toArray(interactiveArgs, String.class));
                        }
                    }
                    catch (final LDAPException le) {
                        Debug.debugException(le);
                        final String message = le.getMessage();
                        if (message != null && !message.isEmpty()) {
                            this.err(message);
                        }
                        return le.getResultCode();
                    }
                }
            }
            if (!extendedValidationDone) {
                this.doExtendedArgumentValidation();
            }
        }
        catch (final ArgumentException ae) {
            Debug.debugException(ae);
            this.err(ae.getMessage());
            return ResultCode.PARAM_ERROR;
        }
        if (this.outputFileArgument != null && this.outputFileArgument.isPresent()) {
            final File outputFile = this.outputFileArgument.getValue();
            final boolean append = this.appendToOutputFileArgument != null && this.appendToOutputFileArgument.isPresent();
            PrintStream outputFileStream;
            try {
                final FileOutputStream fos = new FileOutputStream(outputFile, append);
                outputFileStream = new PrintStream(fos, true, "UTF-8");
            }
            catch (final Exception e2) {
                Debug.debugException(e2);
                this.err(UtilityMessages.ERR_CL_TOOL_ERROR_CREATING_OUTPUT_FILE.get(outputFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e2)));
                return ResultCode.LOCAL_ERROR;
            }
            if (this.teeOutputArgument != null && this.teeOutputArgument.isPresent()) {
                this.out = new PrintStream(new TeeOutputStream(new OutputStream[] { this.out, outputFileStream }));
                this.err = new PrintStream(new TeeOutputStream(new OutputStream[] { this.err, outputFileStream }));
            }
            else {
                this.out = outputFileStream;
                this.err = outputFileStream;
            }
        }
        final List<String> argsSetFromPropertiesFiles = parser.getArgumentsSetFromPropertiesFile();
        if (!argsSetFromPropertiesFiles.isEmpty() && !parser.suppressPropertiesFileComment()) {
            for (final String line : StaticUtils.wrapLine(UtilityMessages.INFO_CL_TOOL_ARGS_FROM_PROPERTIES_FILE.get(parser.getPropertiesFileUsed().getPath()), StaticUtils.TERMINAL_WIDTH_COLUMNS - 3)) {
                this.out("# ", line);
            }
            final StringBuilder buffer = new StringBuilder();
            for (final String s : argsSetFromPropertiesFiles) {
                if (s.startsWith("-")) {
                    if (buffer.length() > 0) {
                        this.out(buffer);
                        buffer.setLength(0);
                    }
                    buffer.append("#      ");
                    buffer.append(s);
                }
                else {
                    if (buffer.length() == 0) {
                        buffer.append("#      ");
                    }
                    else {
                        buffer.append(' ');
                    }
                    buffer.append(StaticUtils.cleanExampleCommandLineArgument(s));
                }
            }
            if (buffer.length() > 0) {
                this.out(buffer);
            }
            this.out(new Object[0]);
        }
        CommandLineToolShutdownHook shutdownHook = null;
        final AtomicReference<ResultCode> exitCode = new AtomicReference<ResultCode>();
        if (this.registerShutdownHook()) {
            shutdownHook = new CommandLineToolShutdownHook(this, exitCode);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
        final ToolInvocationLogDetails logDetails = ToolInvocationLogger.getLogMessageDetails(this.getToolName(), this.logToolInvocationByDefault(), this.getErr());
        ToolInvocationLogShutdownHook logShutdownHook = null;
        if (logDetails.logInvocation()) {
            final HashSet<Argument> argumentsSetFromPropertiesFile = new HashSet<Argument>(StaticUtils.computeMapCapacity(10));
            final ArrayList<ObjectPair<String, String>> propertiesFileArgList = new ArrayList<ObjectPair<String, String>>(10);
            getToolInvocationPropertiesFileArguments(parser, argumentsSetFromPropertiesFile, propertiesFileArgList);
            final ArrayList<ObjectPair<String, String>> providedArgList = new ArrayList<ObjectPair<String, String>>(10);
            getToolInvocationProvidedArguments(parser, argumentsSetFromPropertiesFile, providedArgList);
            logShutdownHook = new ToolInvocationLogShutdownHook(logDetails);
            Runtime.getRuntime().addShutdownHook(logShutdownHook);
            String propertiesFilePath;
            if (propertiesFileArgList.isEmpty()) {
                propertiesFilePath = "";
            }
            else {
                final File propertiesFile = parser.getPropertiesFileUsed();
                if (propertiesFile == null) {
                    propertiesFilePath = "";
                }
                else {
                    propertiesFilePath = propertiesFile.getAbsolutePath();
                }
            }
            ToolInvocationLogger.logLaunchMessage(logDetails, providedArgList, propertiesFileArgList, propertiesFilePath);
        }
        try {
            exitCode.set(this.doToolProcessing());
        }
        catch (final Exception e3) {
            Debug.debugException(e3);
            this.err(StaticUtils.getExceptionMessage(e3));
            exitCode.set(ResultCode.LOCAL_ERROR);
        }
        finally {
            if (logShutdownHook != null) {
                Runtime.getRuntime().removeShutdownHook(logShutdownHook);
                String completionMessage = this.getToolCompletionMessage();
                if (completionMessage == null) {
                    completionMessage = exitCode.get().getName();
                }
                ToolInvocationLogger.logCompletionMessage(logDetails, exitCode.get().intValue(), completionMessage);
            }
            if (shutdownHook != null) {
                Runtime.getRuntime().removeShutdownHook(shutdownHook);
            }
        }
        return exitCode.get();
    }
    
    private static void getToolInvocationProvidedArguments(final ArgumentParser parser, final Set<Argument> argumentsSetFromPropertiesFile, final List<ObjectPair<String, String>> argList) {
        final String noValue = null;
        final SubCommand subCommand = parser.getSelectedSubCommand();
        if (subCommand != null) {
            argList.add(new ObjectPair<String, String>(subCommand.getPrimaryName(), noValue));
        }
        for (final Argument arg : parser.getNamedArguments()) {
            if (!arg.isPresent()) {
                continue;
            }
            if (argumentsSetFromPropertiesFile.contains(arg)) {
                continue;
            }
            if (arg.takesValue()) {
                for (final String value : arg.getValueStringRepresentations(false)) {
                    if (arg.isSensitive()) {
                        argList.add(new ObjectPair<String, String>(arg.getIdentifierString(), "*****REDACTED*****"));
                    }
                    else {
                        argList.add(new ObjectPair<String, String>(arg.getIdentifierString(), value));
                    }
                }
            }
            else {
                argList.add(new ObjectPair<String, String>(arg.getIdentifierString(), noValue));
            }
        }
        if (subCommand != null) {
            getToolInvocationProvidedArguments(subCommand.getArgumentParser(), argumentsSetFromPropertiesFile, argList);
        }
        for (final String trailingArgument : parser.getTrailingArguments()) {
            argList.add(new ObjectPair<String, String>(trailingArgument, noValue));
        }
    }
    
    private static void getToolInvocationPropertiesFileArguments(final ArgumentParser parser, final Set<Argument> argumentsSetFromPropertiesFile, final List<ObjectPair<String, String>> argList) {
        final SubCommand subCommand = parser.getSelectedSubCommand();
        ArgumentParser subCommandParser;
        if (subCommand == null) {
            subCommandParser = null;
        }
        else {
            subCommandParser = subCommand.getArgumentParser();
        }
        final String noValue = null;
        final Iterator<String> iterator = parser.getArgumentsSetFromPropertiesFile().iterator();
        while (iterator.hasNext()) {
            final String arg = iterator.next();
            if (arg.startsWith("-")) {
                Argument a;
                if (arg.startsWith("--")) {
                    final String longIdentifier = arg.substring(2);
                    a = parser.getNamedArgument(longIdentifier);
                    if (a == null && subCommandParser != null) {
                        a = subCommandParser.getNamedArgument(longIdentifier);
                    }
                }
                else {
                    final char shortIdentifier = arg.charAt(1);
                    a = parser.getNamedArgument(shortIdentifier);
                    if (a == null && subCommandParser != null) {
                        a = subCommandParser.getNamedArgument(shortIdentifier);
                    }
                }
                if (a == null) {
                    continue;
                }
                argumentsSetFromPropertiesFile.add(a);
                if (a.takesValue()) {
                    final String value = iterator.next();
                    if (a.isSensitive()) {
                        argList.add(new ObjectPair<String, String>(a.getIdentifierString(), noValue));
                    }
                    else {
                        argList.add(new ObjectPair<String, String>(a.getIdentifierString(), value));
                    }
                }
                else {
                    argList.add(new ObjectPair<String, String>(a.getIdentifierString(), noValue));
                }
            }
            else {
                argList.add(new ObjectPair<String, String>(arg, noValue));
            }
        }
    }
    
    private static TreeMap<String, SubCommand> getSortedSubCommands(final ArgumentParser parser) {
        final TreeMap<String, SubCommand> m = new TreeMap<String, SubCommand>();
        for (final SubCommand sc : parser.getSubCommands()) {
            m.put(sc.getPrimaryName(), sc);
        }
        return m;
    }
    
    private void displayExampleUsages(final ArgumentParser parser) {
        LinkedHashMap<String[], String> examples;
        if (parser != null && parser.getSelectedSubCommand() != null) {
            examples = parser.getSelectedSubCommand().getExampleUsages();
        }
        else {
            examples = this.getExampleUsages();
        }
        if (examples == null || examples.isEmpty()) {
            return;
        }
        this.out(UtilityMessages.INFO_CL_TOOL_LABEL_EXAMPLES);
        final int wrapWidth = StaticUtils.TERMINAL_WIDTH_COLUMNS - 1;
        for (final Map.Entry<String[], String> e : examples.entrySet()) {
            this.out(new Object[0]);
            this.wrapOut(2, wrapWidth, e.getValue());
            this.out(new Object[0]);
            final StringBuilder buffer = new StringBuilder();
            buffer.append("    ");
            buffer.append(this.getToolName());
            final String[] args = e.getKey();
            for (int i = 0; i < args.length; ++i) {
                buffer.append(' ');
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (i < args.length - 1 && !args[i + 1].startsWith("-")) {
                        final ExampleCommandLineArgument cleanArg = ExampleCommandLineArgument.getCleanArgument(args[i + 1]);
                        arg = arg + ' ' + cleanArg.getLocalForm();
                        ++i;
                    }
                }
                else {
                    final ExampleCommandLineArgument cleanArg = ExampleCommandLineArgument.getCleanArgument(arg);
                    arg = cleanArg.getLocalForm();
                }
                if (buffer.length() + arg.length() + 2 < wrapWidth) {
                    buffer.append(arg);
                }
                else {
                    buffer.append('\\');
                    this.out(buffer.toString());
                    buffer.setLength(0);
                    buffer.append("         ");
                    buffer.append(arg);
                }
            }
            this.out(buffer.toString());
        }
    }
    
    public abstract String getToolName();
    
    public abstract String getToolDescription();
    
    public List<String> getAdditionalDescriptionParagraphs() {
        return Collections.emptyList();
    }
    
    public String getToolVersion() {
        return null;
    }
    
    public int getMinTrailingArguments() {
        return 0;
    }
    
    public int getMaxTrailingArguments() {
        return 0;
    }
    
    public String getTrailingArgumentsPlaceholder() {
        return null;
    }
    
    public boolean supportsInteractiveMode() {
        return false;
    }
    
    public boolean defaultsToInteractiveMode() {
        return false;
    }
    
    protected List<String> requestToolArgumentsInteractively(final ArgumentParser parser) throws LDAPException {
        return null;
    }
    
    public boolean supportsPropertiesFile() {
        return false;
    }
    
    protected boolean supportsOutputFile() {
        return false;
    }
    
    protected boolean logToolInvocationByDefault() {
        return false;
    }
    
    protected String getToolCompletionMessage() {
        return null;
    }
    
    public final ArgumentParser createArgumentParser() throws ArgumentException {
        final ArgumentParser parser = new ArgumentParser(this.getToolName(), this.getToolDescription(), this.getAdditionalDescriptionParagraphs(), this.getMinTrailingArguments(), this.getMaxTrailingArguments(), this.getTrailingArgumentsPlaceholder());
        parser.setCommandLineTool(this);
        this.addToolArguments(parser);
        if (this.supportsInteractiveMode()) {
            (this.interactiveArgument = new BooleanArgument(null, "interactive", UtilityMessages.INFO_CL_TOOL_DESCRIPTION_INTERACTIVE.get())).setUsageArgument(true);
            parser.addArgument(this.interactiveArgument);
        }
        if (this.supportsOutputFile()) {
            (this.outputFileArgument = new FileArgument(null, "outputFile", false, 1, null, UtilityMessages.INFO_CL_TOOL_DESCRIPTION_OUTPUT_FILE.get(), false, true, true, false)).addLongIdentifier("output-file", true);
            this.outputFileArgument.setUsageArgument(true);
            parser.addArgument(this.outputFileArgument);
            (this.appendToOutputFileArgument = new BooleanArgument(null, "appendToOutputFile", 1, UtilityMessages.INFO_CL_TOOL_DESCRIPTION_APPEND_TO_OUTPUT_FILE.get(this.outputFileArgument.getIdentifierString()))).addLongIdentifier("append-to-output-file", true);
            this.appendToOutputFileArgument.setUsageArgument(true);
            parser.addArgument(this.appendToOutputFileArgument);
            (this.teeOutputArgument = new BooleanArgument(null, "teeOutput", 1, UtilityMessages.INFO_CL_TOOL_DESCRIPTION_TEE_OUTPUT.get(this.outputFileArgument.getIdentifierString()))).addLongIdentifier("tee-output", true);
            this.teeOutputArgument.setUsageArgument(true);
            parser.addArgument(this.teeOutputArgument);
            parser.addDependentArgumentSet(this.appendToOutputFileArgument, this.outputFileArgument, new Argument[0]);
            parser.addDependentArgumentSet(this.teeOutputArgument, this.outputFileArgument, new Argument[0]);
        }
        (this.helpArgument = new BooleanArgument('H', "help", UtilityMessages.INFO_CL_TOOL_DESCRIPTION_HELP.get())).addShortIdentifier('?', true);
        this.helpArgument.setUsageArgument(true);
        parser.addArgument(this.helpArgument);
        if (!parser.getSubCommands().isEmpty()) {
            (this.helpSubcommandsArgument = new BooleanArgument(null, "helpSubcommands", 1, UtilityMessages.INFO_CL_TOOL_DESCRIPTION_HELP_SUBCOMMANDS.get())).addLongIdentifier("helpSubcommand", true);
            this.helpSubcommandsArgument.addLongIdentifier("help-subcommands", true);
            this.helpSubcommandsArgument.addLongIdentifier("help-subcommand", true);
            this.helpSubcommandsArgument.setUsageArgument(true);
            parser.addArgument(this.helpSubcommandsArgument);
        }
        final String version = this.getToolVersion();
        if (version != null && !version.isEmpty() && parser.getNamedArgument("version") == null) {
            Character shortIdentifier;
            if (parser.getNamedArgument('V') == null) {
                shortIdentifier = 'V';
            }
            else {
                shortIdentifier = null;
            }
            (this.versionArgument = new BooleanArgument(shortIdentifier, "version", UtilityMessages.INFO_CL_TOOL_DESCRIPTION_VERSION.get())).setUsageArgument(true);
            parser.addArgument(this.versionArgument);
        }
        if (this.supportsPropertiesFile()) {
            parser.enablePropertiesFileSupport();
        }
        return parser;
    }
    
    void setHelpSASLArgument(final BooleanArgument helpSASLArgument) {
        this.helpSASLArgument = helpSASLArgument;
    }
    
    protected void addEnableSSLDebuggingArgument(final BooleanArgument enableSSLDebuggingArgument) {
        this.enableSSLDebuggingArguments.add(enableSSLDebuggingArgument);
    }
    
    static Set<String> getUsageArgumentIdentifiers(final CommandLineTool tool) {
        final LinkedHashSet<String> ids = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(9));
        ids.add("help");
        ids.add("version");
        ids.add("helpSubcommands");
        if (tool.supportsInteractiveMode()) {
            ids.add("interactive");
        }
        if (tool.supportsPropertiesFile()) {
            ids.add("propertiesFilePath");
            ids.add("generatePropertiesFile");
            ids.add("noPropertiesFile");
            ids.add("suppressPropertiesFileComment");
        }
        if (tool.supportsOutputFile()) {
            ids.add("outputFile");
            ids.add("appendToOutputFile");
            ids.add("teeOutput");
        }
        return Collections.unmodifiableSet((Set<? extends String>)ids);
    }
    
    public abstract void addToolArguments(final ArgumentParser p0) throws ArgumentException;
    
    public void doExtendedArgumentValidation() throws ArgumentException {
    }
    
    public abstract ResultCode doToolProcessing();
    
    protected boolean registerShutdownHook() {
        return false;
    }
    
    protected void doShutdownHookProcessing(final ResultCode resultCode) {
        throw new LDAPSDKUsageException(UtilityMessages.ERR_COMMAND_LINE_TOOL_SHUTDOWN_HOOK_NOT_IMPLEMENTED.get(this.getToolName()));
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public LinkedHashMap<String[], String> getExampleUsages() {
        return null;
    }
    
    public final PasswordFileReader getPasswordFileReader() {
        return this.passwordFileReader;
    }
    
    public final PrintStream getOut() {
        return this.out;
    }
    
    public final PrintStream getOriginalOut() {
        return this.originalOut;
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final synchronized void out(final Object... msg) {
        write(this.out, 0, 0, msg);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final synchronized void wrapOut(final int indent, final int wrapColumn, final Object... msg) {
        write(this.out, indent, wrapColumn, msg);
    }
    
    final synchronized void wrapStandardOut(final int firstLineIndent, final int subsequentLineIndent, final int wrapColumn, final boolean endWithNewline, final Object... msg) {
        write(this.out, firstLineIndent, subsequentLineIndent, wrapColumn, endWithNewline, msg);
    }
    
    public final PrintStream getErr() {
        return this.err;
    }
    
    public final PrintStream getOriginalErr() {
        return this.originalErr;
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final synchronized void err(final Object... msg) {
        write(this.err, 0, 0, msg);
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.METHOD_THREADSAFE)
    public final synchronized void wrapErr(final int indent, final int wrapColumn, final Object... msg) {
        write(this.err, indent, wrapColumn, msg);
    }
    
    private static void write(final PrintStream stream, final int indent, final int wrapColumn, final Object... msg) {
        write(stream, indent, indent, wrapColumn, true, msg);
    }
    
    private static void write(final PrintStream stream, final int firstLineIndent, final int subsequentLineIndent, final int wrapColumn, final boolean endWithNewline, final Object... msg) {
        final StringBuilder buffer = new StringBuilder();
        for (final Object o : msg) {
            buffer.append(o);
        }
        if (wrapColumn > 2) {
            boolean firstLine = true;
            for (final String line : StaticUtils.wrapLine(buffer.toString(), wrapColumn - firstLineIndent, wrapColumn - subsequentLineIndent)) {
                int indent;
                if (firstLine) {
                    indent = firstLineIndent;
                    firstLine = false;
                }
                else {
                    stream.println();
                    indent = subsequentLineIndent;
                }
                if (indent > 0) {
                    for (int i = 0; i < indent; ++i) {
                        stream.print(' ');
                    }
                }
                stream.print(line);
            }
        }
        else {
            if (firstLineIndent > 0) {
                for (int j = 0; j < firstLineIndent; ++j) {
                    stream.print(' ');
                }
            }
            stream.print(buffer.toString());
        }
        if (endWithNewline) {
            stream.println();
        }
        stream.flush();
    }
}
