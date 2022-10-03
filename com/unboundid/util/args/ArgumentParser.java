package com.unboundid.util.args;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.HashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import com.unboundid.ldap.sdk.unboundidds.tools.ToolUtils;
import java.io.FileInputStream;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Arrays;
import com.unboundid.util.Validator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import com.unboundid.util.ObjectPair;
import java.util.List;
import java.util.LinkedHashMap;
import java.io.File;
import com.unboundid.util.CommandLineTool;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ArgumentParser implements Serializable
{
    public static final String PROPERTY_DEFAULT_PROPERTIES_FILE_PATH;
    public static final String ENV_DEFAULT_PROPERTIES_FILE_PATH = "UNBOUNDID_TOOL_PROPERTIES_FILE_PATH";
    private static final String ARG_NAME_OUTPUT_FILE = "outputFile";
    private static final String ARG_NAME_TEE_OUTPUT = "teeOutput";
    private static final String ARG_NAME_PROPERTIES_FILE_PATH = "propertiesFilePath";
    private static final String ARG_NAME_GENERATE_PROPERTIES_FILE = "generatePropertiesFile";
    private static final String ARG_NAME_NO_PROPERTIES_FILE = "noPropertiesFile";
    private static final String ARG_NAME_SUPPRESS_PROPERTIES_FILE_COMMENT = "suppressPropertiesFileComment";
    private static final long serialVersionUID = 3053102992180360269L;
    private volatile CommandLineTool commandLineTool;
    private volatile File propertiesFileUsed;
    private final int maxTrailingArgs;
    private final int minTrailingArgs;
    private final LinkedHashMap<Character, Argument> namedArgsByShortID;
    private final LinkedHashMap<String, Argument> namedArgsByLongID;
    private final LinkedHashMap<String, SubCommand> subCommandsByName;
    private final List<Argument> namedArgs;
    private final List<ObjectPair<Argument, Set<Argument>>> dependentArgumentSets;
    private final List<Set<Argument>> exclusiveArgumentSets;
    private final List<Set<Argument>> requiredArgumentSets;
    private final List<String> argumentsSetFromPropertiesFile;
    private final List<String> trailingArgs;
    private final List<SubCommand> subCommands;
    private final List<String> additionalCommandDescriptionParagraphs;
    private final String commandDescription;
    private final String commandName;
    private final String trailingArgsPlaceholder;
    private volatile SubCommand parentSubCommand;
    private volatile SubCommand selectedSubCommand;
    
    public ArgumentParser(final String commandName, final String commandDescription) throws ArgumentException {
        this(commandName, commandDescription, 0, null);
    }
    
    public ArgumentParser(final String commandName, final String commandDescription, final int maxTrailingArgs, final String trailingArgsPlaceholder) throws ArgumentException {
        this(commandName, commandDescription, 0, maxTrailingArgs, trailingArgsPlaceholder);
    }
    
    public ArgumentParser(final String commandName, final String commandDescription, final int minTrailingArgs, final int maxTrailingArgs, final String trailingArgsPlaceholder) throws ArgumentException {
        this(commandName, commandDescription, null, minTrailingArgs, maxTrailingArgs, trailingArgsPlaceholder);
    }
    
    public ArgumentParser(final String commandName, final String commandDescription, final List<String> additionalCommandDescriptionParagraphs, final int minTrailingArgs, final int maxTrailingArgs, final String trailingArgsPlaceholder) throws ArgumentException {
        if (commandName == null) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_COMMAND_NAME_NULL.get());
        }
        if (commandDescription == null) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_COMMAND_DESCRIPTION_NULL.get());
        }
        if (maxTrailingArgs != 0 && trailingArgsPlaceholder == null) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_TRAILING_ARGS_PLACEHOLDER_NULL.get());
        }
        this.commandName = commandName;
        this.commandDescription = commandDescription;
        this.trailingArgsPlaceholder = trailingArgsPlaceholder;
        if (additionalCommandDescriptionParagraphs == null) {
            this.additionalCommandDescriptionParagraphs = Collections.emptyList();
        }
        else {
            this.additionalCommandDescriptionParagraphs = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(additionalCommandDescriptionParagraphs));
        }
        if (minTrailingArgs >= 0) {
            this.minTrailingArgs = minTrailingArgs;
        }
        else {
            this.minTrailingArgs = 0;
        }
        if (maxTrailingArgs >= 0) {
            this.maxTrailingArgs = maxTrailingArgs;
        }
        else {
            this.maxTrailingArgs = Integer.MAX_VALUE;
        }
        if (this.minTrailingArgs > this.maxTrailingArgs) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_TRAILING_ARGS_COUNT_MISMATCH.get(this.minTrailingArgs, this.maxTrailingArgs));
        }
        this.namedArgsByShortID = new LinkedHashMap<Character, Argument>(StaticUtils.computeMapCapacity(20));
        this.namedArgsByLongID = new LinkedHashMap<String, Argument>(StaticUtils.computeMapCapacity(20));
        this.namedArgs = new ArrayList<Argument>(20);
        this.trailingArgs = new ArrayList<String>(20);
        this.dependentArgumentSets = new ArrayList<ObjectPair<Argument, Set<Argument>>>(20);
        this.exclusiveArgumentSets = new ArrayList<Set<Argument>>(20);
        this.requiredArgumentSets = new ArrayList<Set<Argument>>(20);
        this.parentSubCommand = null;
        this.selectedSubCommand = null;
        this.subCommands = new ArrayList<SubCommand>(20);
        this.subCommandsByName = new LinkedHashMap<String, SubCommand>(StaticUtils.computeMapCapacity(20));
        this.propertiesFileUsed = null;
        this.argumentsSetFromPropertiesFile = new ArrayList<String>(20);
        this.commandLineTool = null;
    }
    
    ArgumentParser(final ArgumentParser source, final SubCommand subCommand) {
        this.commandName = source.commandName;
        this.commandDescription = source.commandDescription;
        this.minTrailingArgs = source.minTrailingArgs;
        this.maxTrailingArgs = source.maxTrailingArgs;
        this.trailingArgsPlaceholder = source.trailingArgsPlaceholder;
        this.additionalCommandDescriptionParagraphs = source.additionalCommandDescriptionParagraphs;
        this.propertiesFileUsed = null;
        this.argumentsSetFromPropertiesFile = new ArrayList<String>(20);
        this.trailingArgs = new ArrayList<String>(20);
        this.namedArgs = new ArrayList<Argument>(source.namedArgs.size());
        this.namedArgsByLongID = new LinkedHashMap<String, Argument>(StaticUtils.computeMapCapacity(source.namedArgsByLongID.size()));
        this.namedArgsByShortID = new LinkedHashMap<Character, Argument>(StaticUtils.computeMapCapacity(source.namedArgsByShortID.size()));
        final LinkedHashMap<String, Argument> argsByID = new LinkedHashMap<String, Argument>(StaticUtils.computeMapCapacity(source.namedArgs.size()));
        for (final Argument sourceArg : source.namedArgs) {
            final Argument a = sourceArg.getCleanCopy();
            try {
                a.setRegistered();
            }
            catch (final ArgumentException ae) {
                Debug.debugException(ae);
            }
            this.namedArgs.add(a);
            argsByID.put(a.getIdentifierString(), a);
            for (final Character c : a.getShortIdentifiers(true)) {
                this.namedArgsByShortID.put(c, a);
            }
            for (final String s : a.getLongIdentifiers(true)) {
                this.namedArgsByLongID.put(StaticUtils.toLowerCase(s), a);
            }
        }
        this.dependentArgumentSets = new ArrayList<ObjectPair<Argument, Set<Argument>>>(source.dependentArgumentSets.size());
        for (final ObjectPair<Argument, Set<Argument>> p : source.dependentArgumentSets) {
            final Set<Argument> sourceSet = p.getSecond();
            final LinkedHashSet<Argument> newSet = new LinkedHashSet<Argument>(StaticUtils.computeMapCapacity(sourceSet.size()));
            for (final Argument a2 : sourceSet) {
                newSet.add(argsByID.get(a2.getIdentifierString()));
            }
            final Argument sourceFirst = p.getFirst();
            final Argument newFirst = argsByID.get(sourceFirst.getIdentifierString());
            this.dependentArgumentSets.add(new ObjectPair<Argument, Set<Argument>>(newFirst, newSet));
        }
        this.exclusiveArgumentSets = new ArrayList<Set<Argument>>(source.exclusiveArgumentSets.size());
        for (final Set<Argument> sourceSet2 : source.exclusiveArgumentSets) {
            final LinkedHashSet<Argument> newSet2 = new LinkedHashSet<Argument>(StaticUtils.computeMapCapacity(sourceSet2.size()));
            for (final Argument a3 : sourceSet2) {
                newSet2.add(argsByID.get(a3.getIdentifierString()));
            }
            this.exclusiveArgumentSets.add(newSet2);
        }
        this.requiredArgumentSets = new ArrayList<Set<Argument>>(source.requiredArgumentSets.size());
        for (final Set<Argument> sourceSet2 : source.requiredArgumentSets) {
            final LinkedHashSet<Argument> newSet2 = new LinkedHashSet<Argument>(StaticUtils.computeMapCapacity(sourceSet2.size()));
            for (final Argument a3 : sourceSet2) {
                newSet2.add(argsByID.get(a3.getIdentifierString()));
            }
            this.requiredArgumentSets.add(newSet2);
        }
        this.parentSubCommand = subCommand;
        this.selectedSubCommand = null;
        this.subCommands = new ArrayList<SubCommand>(source.subCommands.size());
        this.subCommandsByName = new LinkedHashMap<String, SubCommand>(StaticUtils.computeMapCapacity(source.subCommandsByName.size()));
        for (final SubCommand sc : source.subCommands) {
            this.subCommands.add(sc.getCleanCopy());
            for (final String name : sc.getNames(true)) {
                this.subCommandsByName.put(StaticUtils.toLowerCase(name), sc);
            }
        }
    }
    
    public String getCommandName() {
        return this.commandName;
    }
    
    public String getCommandDescription() {
        return this.commandDescription;
    }
    
    public List<String> getAdditionalCommandDescriptionParagraphs() {
        return this.additionalCommandDescriptionParagraphs;
    }
    
    public boolean allowsTrailingArguments() {
        return this.maxTrailingArgs != 0;
    }
    
    public boolean requiresTrailingArguments() {
        return this.minTrailingArgs != 0;
    }
    
    public String getTrailingArgumentsPlaceholder() {
        return this.trailingArgsPlaceholder;
    }
    
    public int getMinTrailingArguments() {
        return this.minTrailingArgs;
    }
    
    public int getMaxTrailingArguments() {
        return this.maxTrailingArgs;
    }
    
    public void enablePropertiesFileSupport() throws ArgumentException {
        final FileArgument propertiesFilePath = new FileArgument(null, "propertiesFilePath", false, 1, null, ArgsMessages.INFO_ARG_DESCRIPTION_PROP_FILE_PATH.get(), true, true, true, false);
        propertiesFilePath.setUsageArgument(true);
        propertiesFilePath.addLongIdentifier("properties-file-path", true);
        this.addArgument(propertiesFilePath);
        final FileArgument generatePropertiesFile = new FileArgument(null, "generatePropertiesFile", false, 1, null, ArgsMessages.INFO_ARG_DESCRIPTION_GEN_PROP_FILE.get(), false, true, true, false);
        generatePropertiesFile.setUsageArgument(true);
        generatePropertiesFile.addLongIdentifier("generate-properties-file", true);
        this.addArgument(generatePropertiesFile);
        final BooleanArgument noPropertiesFile = new BooleanArgument(null, "noPropertiesFile", ArgsMessages.INFO_ARG_DESCRIPTION_NO_PROP_FILE.get());
        noPropertiesFile.setUsageArgument(true);
        noPropertiesFile.addLongIdentifier("no-properties-file", true);
        this.addArgument(noPropertiesFile);
        final BooleanArgument suppressPropertiesFileComment = new BooleanArgument(null, "suppressPropertiesFileComment", 1, ArgsMessages.INFO_ARG_DESCRIPTION_SUPPRESS_PROP_FILE_COMMENT.get());
        suppressPropertiesFileComment.setUsageArgument(true);
        suppressPropertiesFileComment.addLongIdentifier("suppress-properties-file-comment", true);
        this.addArgument(suppressPropertiesFileComment);
        this.addExclusiveArgumentSet(propertiesFilePath, noPropertiesFile, new Argument[0]);
    }
    
    public File getGeneratedPropertiesFile() {
        final Argument a = this.getNamedArgument("generatePropertiesFile");
        if (a == null || !a.isPresent() || !(a instanceof FileArgument)) {
            return null;
        }
        return ((FileArgument)a).getValue();
    }
    
    public Argument getNamedArgument(final Character shortIdentifier) {
        Validator.ensureNotNull(shortIdentifier);
        return this.namedArgsByShortID.get(shortIdentifier);
    }
    
    public Argument getNamedArgument(final String identifier) {
        Validator.ensureNotNull(identifier);
        if (identifier.startsWith("--") && identifier.length() > 2) {
            return this.namedArgsByLongID.get(StaticUtils.toLowerCase(identifier.substring(2)));
        }
        if (identifier.startsWith("-") && identifier.length() == 2) {
            return this.namedArgsByShortID.get(identifier.charAt(1));
        }
        return this.namedArgsByLongID.get(StaticUtils.toLowerCase(identifier));
    }
    
    public ArgumentListArgument getArgumentListArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (ArgumentListArgument)a;
    }
    
    public BooleanArgument getBooleanArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (BooleanArgument)a;
    }
    
    public BooleanValueArgument getBooleanValueArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (BooleanValueArgument)a;
    }
    
    public ControlArgument getControlArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (ControlArgument)a;
    }
    
    public DNArgument getDNArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (DNArgument)a;
    }
    
    public DurationArgument getDurationArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (DurationArgument)a;
    }
    
    public FileArgument getFileArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (FileArgument)a;
    }
    
    public FilterArgument getFilterArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (FilterArgument)a;
    }
    
    public IntegerArgument getIntegerArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (IntegerArgument)a;
    }
    
    public ScopeArgument getScopeArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (ScopeArgument)a;
    }
    
    public StringArgument getStringArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (StringArgument)a;
    }
    
    public TimestampArgument getTimestampArgument(final String identifier) {
        final Argument a = this.getNamedArgument(identifier);
        if (a == null) {
            return null;
        }
        return (TimestampArgument)a;
    }
    
    public List<Argument> getNamedArguments() {
        return Collections.unmodifiableList((List<? extends Argument>)this.namedArgs);
    }
    
    public void addArgument(final Argument argument) throws ArgumentException {
        argument.setRegistered();
        for (final Character c : argument.getShortIdentifiers(true)) {
            if (this.namedArgsByShortID.containsKey(c)) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_SHORT_ID_CONFLICT.get(c));
            }
            if (this.parentSubCommand != null && this.parentSubCommand.getArgumentParser().namedArgsByShortID.containsKey(c)) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_SHORT_ID_CONFLICT.get(c));
            }
        }
        for (final String s : argument.getLongIdentifiers(true)) {
            if (this.namedArgsByLongID.containsKey(StaticUtils.toLowerCase(s))) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_LONG_ID_CONFLICT.get(s));
            }
            if (this.parentSubCommand != null && this.parentSubCommand.getArgumentParser().namedArgsByLongID.containsKey(StaticUtils.toLowerCase(s))) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_LONG_ID_CONFLICT.get(s));
            }
        }
        for (final SubCommand sc : this.subCommands) {
            final ArgumentParser parser = sc.getArgumentParser();
            for (final Character c2 : argument.getShortIdentifiers(true)) {
                if (parser.namedArgsByShortID.containsKey(c2)) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_SHORT_ID_CONFLICT_WITH_SUBCOMMAND.get(c2, sc.getPrimaryName()));
                }
            }
            for (final String s2 : argument.getLongIdentifiers(true)) {
                if (parser.namedArgsByLongID.containsKey(StaticUtils.toLowerCase(s2))) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_LONG_ID_CONFLICT_WITH_SUBCOMMAND.get(s2, sc.getPrimaryName()));
                }
            }
        }
        for (final Character c : argument.getShortIdentifiers(true)) {
            this.namedArgsByShortID.put(c, argument);
        }
        for (final String s : argument.getLongIdentifiers(true)) {
            this.namedArgsByLongID.put(StaticUtils.toLowerCase(s), argument);
        }
        this.namedArgs.add(argument);
    }
    
    public List<ObjectPair<Argument, Set<Argument>>> getDependentArgumentSets() {
        return Collections.unmodifiableList((List<? extends ObjectPair<Argument, Set<Argument>>>)this.dependentArgumentSets);
    }
    
    public void addDependentArgumentSet(final Argument targetArgument, final Collection<Argument> dependentArguments) {
        Validator.ensureNotNull(targetArgument, dependentArguments);
        Validator.ensureFalse(dependentArguments.isEmpty(), "The ArgumentParser.addDependentArgumentSet method must not be called with an empty collection of dependentArguments");
        Validator.ensureTrue(this.namedArgs.contains(targetArgument), "The ArgumentParser.addDependentArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + targetArgument.getIdentifierString() + " argument has not been registered with the argument parser.");
        for (final Argument a : dependentArguments) {
            Validator.ensureTrue(this.namedArgs.contains(a), "The ArgumentParser.addDependentArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + a.getIdentifierString() + " argument has not been registered " + "with the argument parser.");
        }
        final LinkedHashSet<Argument> argSet = new LinkedHashSet<Argument>(dependentArguments);
        this.dependentArgumentSets.add(new ObjectPair<Argument, Set<Argument>>(targetArgument, argSet));
    }
    
    public void addDependentArgumentSet(final Argument targetArgument, final Argument dependentArg1, final Argument... remaining) {
        Validator.ensureNotNull(targetArgument, dependentArg1);
        Validator.ensureTrue(this.namedArgs.contains(targetArgument), "The ArgumentParser.addDependentArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + targetArgument.getIdentifierString() + " argument has not been registered with the argument parser.");
        Validator.ensureTrue(this.namedArgs.contains(dependentArg1), "The ArgumentParser.addDependentArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + dependentArg1.getIdentifierString() + " argument has not been registered with the argument parser.");
        if (remaining != null) {
            for (final Argument a : remaining) {
                Validator.ensureTrue(this.namedArgs.contains(a), "The ArgumentParser.addDependentArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + a.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
            }
        }
        final LinkedHashSet<Argument> argSet = new LinkedHashSet<Argument>(StaticUtils.computeMapCapacity(10));
        argSet.add(dependentArg1);
        if (remaining != null) {
            argSet.addAll((Collection<?>)Arrays.asList(remaining));
        }
        this.dependentArgumentSets.add(new ObjectPair<Argument, Set<Argument>>(targetArgument, argSet));
    }
    
    public void addMutuallyDependentArgumentSet(final Collection<Argument> arguments) {
        Validator.ensureNotNullWithMessage(arguments, "ArgumentParser.addMutuallyDependentArgumentSet.arguments must not be null.");
        Validator.ensureTrue(arguments.size() >= 2, "ArgumentParser.addMutuallyDependentArgumentSet.arguments must contain at least two elements.");
        for (final Argument a : arguments) {
            Validator.ensureTrue(this.namedArgs.contains(a), "ArgumentParser.addMutuallyDependentArgumentSet invoked with argument " + a.getIdentifierString() + " that is not registered with the argument parser.");
        }
        final Set<Argument> allArgsSet = new HashSet<Argument>(arguments);
        for (final Argument a2 : allArgsSet) {
            final Set<Argument> dependentArgs = new HashSet<Argument>(allArgsSet);
            dependentArgs.remove(a2);
            this.addDependentArgumentSet(a2, dependentArgs);
        }
    }
    
    public void addMutuallyDependentArgumentSet(final Argument arg1, final Argument arg2, final Argument... remaining) {
        Validator.ensureNotNullWithMessage(arg1, "ArgumentParser.addMutuallyDependentArgumentSet.arg1 must not be null.");
        Validator.ensureNotNullWithMessage(arg2, "ArgumentParser.addMutuallyDependentArgumentSet.arg2 must not be null.");
        final List<Argument> args = new ArrayList<Argument>(10);
        args.add(arg1);
        args.add(arg2);
        if (remaining != null) {
            args.addAll(Arrays.asList(remaining));
        }
        this.addMutuallyDependentArgumentSet(args);
    }
    
    public List<Set<Argument>> getExclusiveArgumentSets() {
        return Collections.unmodifiableList((List<? extends Set<Argument>>)this.exclusiveArgumentSets);
    }
    
    public void addExclusiveArgumentSet(final Collection<Argument> exclusiveArguments) {
        Validator.ensureNotNull(exclusiveArguments);
        for (final Argument a : exclusiveArguments) {
            Validator.ensureTrue(this.namedArgs.contains(a), "The ArgumentParser.addExclusiveArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + a.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
        }
        final LinkedHashSet<Argument> argSet = new LinkedHashSet<Argument>(exclusiveArguments);
        this.exclusiveArgumentSets.add(Collections.unmodifiableSet((Set<? extends Argument>)argSet));
    }
    
    public void addExclusiveArgumentSet(final Argument arg1, final Argument arg2, final Argument... remaining) {
        Validator.ensureNotNull(arg1, arg2);
        Validator.ensureTrue(this.namedArgs.contains(arg1), "The ArgumentParser.addExclusiveArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + arg1.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
        Validator.ensureTrue(this.namedArgs.contains(arg2), "The ArgumentParser.addExclusiveArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + arg2.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
        if (remaining != null) {
            for (final Argument a : remaining) {
                Validator.ensureTrue(this.namedArgs.contains(a), "The ArgumentParser.addExclusiveArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + a.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
            }
        }
        final LinkedHashSet<Argument> argSet = new LinkedHashSet<Argument>(StaticUtils.computeMapCapacity(10));
        argSet.add(arg1);
        argSet.add(arg2);
        if (remaining != null) {
            argSet.addAll((Collection<?>)Arrays.asList(remaining));
        }
        this.exclusiveArgumentSets.add(Collections.unmodifiableSet((Set<? extends Argument>)argSet));
    }
    
    public List<Set<Argument>> getRequiredArgumentSets() {
        return Collections.unmodifiableList((List<? extends Set<Argument>>)this.requiredArgumentSets);
    }
    
    public void addRequiredArgumentSet(final Collection<Argument> requiredArguments) {
        Validator.ensureNotNull(requiredArguments);
        for (final Argument a : requiredArguments) {
            Validator.ensureTrue(this.namedArgs.contains(a), "The ArgumentParser.addRequiredArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + a.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
        }
        final LinkedHashSet<Argument> argSet = new LinkedHashSet<Argument>(requiredArguments);
        this.requiredArgumentSets.add(Collections.unmodifiableSet((Set<? extends Argument>)argSet));
    }
    
    public void addRequiredArgumentSet(final Argument arg1, final Argument arg2, final Argument... remaining) {
        Validator.ensureNotNull(arg1, arg2);
        Validator.ensureTrue(this.namedArgs.contains(arg1), "The ArgumentParser.addRequiredArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + arg1.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
        Validator.ensureTrue(this.namedArgs.contains(arg2), "The ArgumentParser.addRequiredArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + arg2.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
        if (remaining != null) {
            for (final Argument a : remaining) {
                Validator.ensureTrue(this.namedArgs.contains(a), "The ArgumentParser.addRequiredArgumentSet method may only be used if all of the provided arguments have already been registered with the argument parser via the ArgumentParser.addArgument method.  The " + a.getIdentifierString() + " argument has not been " + "registered with the argument parser.");
            }
        }
        final LinkedHashSet<Argument> argSet = new LinkedHashSet<Argument>(StaticUtils.computeMapCapacity(10));
        argSet.add(arg1);
        argSet.add(arg2);
        if (remaining != null) {
            argSet.addAll((Collection<?>)Arrays.asList(remaining));
        }
        this.requiredArgumentSets.add(Collections.unmodifiableSet((Set<? extends Argument>)argSet));
    }
    
    public boolean hasSubCommands() {
        return !this.subCommands.isEmpty();
    }
    
    public SubCommand getSelectedSubCommand() {
        return this.selectedSubCommand;
    }
    
    void setSelectedSubCommand(final SubCommand subcommand) {
        this.selectedSubCommand = subcommand;
        if (subcommand != null) {
            subcommand.setPresent();
        }
    }
    
    public List<SubCommand> getSubCommands() {
        return Collections.unmodifiableList((List<? extends SubCommand>)this.subCommands);
    }
    
    public SubCommand getSubCommand(final String name) {
        if (name == null) {
            return null;
        }
        return this.subCommandsByName.get(StaticUtils.toLowerCase(name));
    }
    
    public void addSubCommand(final SubCommand subCommand) throws ArgumentException {
        if (subCommand.getGlobalArgumentParser() != null) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_SUBCOMMAND_ALREADY_REGISTERED_WITH_PARSER.get());
        }
        if (this.parentSubCommand != null) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_CANNOT_CREATE_NESTED_SUBCOMMAND.get(this.parentSubCommand.getPrimaryName()));
        }
        if (this.allowsTrailingArguments()) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_WITH_TRAILING_ARGS_CANNOT_HAVE_SUBCOMMANDS.get());
        }
        for (final String name : subCommand.getNames(true)) {
            if (this.subCommandsByName.containsKey(StaticUtils.toLowerCase(name))) {
                throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_NAME_ALREADY_IN_USE.get(name));
            }
        }
        for (final String name : subCommand.getNames(true)) {
            this.subCommandsByName.put(StaticUtils.toLowerCase(name), subCommand);
        }
        this.subCommands.add(subCommand);
        subCommand.setGlobalArgumentParser(this);
    }
    
    void addSubCommand(final String name, final SubCommand subCommand) throws ArgumentException {
        final String lowerName = StaticUtils.toLowerCase(name);
        if (this.subCommandsByName.containsKey(lowerName)) {
            throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_NAME_ALREADY_IN_USE.get(name));
        }
        this.subCommandsByName.put(lowerName, subCommand);
    }
    
    public List<String> getTrailingArguments() {
        return Collections.unmodifiableList((List<? extends String>)this.trailingArgs);
    }
    
    void reset() {
        this.selectedSubCommand = null;
        for (final Argument a : this.namedArgs) {
            a.reset();
        }
        this.propertiesFileUsed = null;
        this.argumentsSetFromPropertiesFile.clear();
        this.trailingArgs.clear();
    }
    
    void resetTrailingArguments() {
        this.trailingArgs.clear();
    }
    
    void addTrailingArgument(final String value) throws ArgumentException {
        if (this.maxTrailingArgs > 0 && this.trailingArgs.size() >= this.maxTrailingArgs) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_TOO_MANY_TRAILING_ARGS.get(value, this.commandName, this.maxTrailingArgs));
        }
        this.trailingArgs.add(value);
    }
    
    public File getPropertiesFileUsed() {
        return this.propertiesFileUsed;
    }
    
    public List<String> getArgumentsSetFromPropertiesFile() {
        return Collections.unmodifiableList((List<? extends String>)this.argumentsSetFromPropertiesFile);
    }
    
    public boolean suppressPropertiesFileComment() {
        final BooleanArgument arg = this.getBooleanArgument("suppressPropertiesFileComment");
        return arg != null && arg.isPresent();
    }
    
    public ArgumentParser getCleanCopy() {
        return new ArgumentParser(this, null);
    }
    
    public void parse(final String[] args) throws ArgumentException {
        ArgumentParser subCommandParser = null;
        boolean inTrailingArgs = false;
        boolean skipFinalValidation = false;
        String subCommandName = null;
        for (int i = 0; i < args.length; ++i) {
            final String s = args[i];
            if (inTrailingArgs) {
                if (this.maxTrailingArgs == 0) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_TRAILING_ARGS_NOT_ALLOWED.get(s, this.commandName));
                }
                if (this.trailingArgs.size() >= this.maxTrailingArgs) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_TOO_MANY_TRAILING_ARGS.get(s, this.commandName, this.maxTrailingArgs));
                }
                this.trailingArgs.add(s);
            }
            else if (s.equals("--")) {
                inTrailingArgs = true;
            }
            else if (s.startsWith("--")) {
                final int equalPos = s.indexOf(61);
                String argName;
                if (equalPos > 0) {
                    argName = s.substring(2, equalPos);
                }
                else {
                    argName = s.substring(2);
                }
                final String lowerName = StaticUtils.toLowerCase(argName);
                Argument a = this.namedArgsByLongID.get(lowerName);
                if (a == null && subCommandParser != null) {
                    a = subCommandParser.namedArgsByLongID.get(lowerName);
                }
                if (a == null) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_NO_SUCH_LONG_ID.get(argName));
                }
                if (a.isUsageArgument()) {
                    skipFinalValidation |= skipFinalValidationBecauseOfArgument(a);
                }
                a.incrementOccurrences();
                if (a.takesValue()) {
                    if (equalPos > 0) {
                        a.addValue(s.substring(equalPos + 1));
                    }
                    else {
                        if (++i >= args.length) {
                            throw new ArgumentException(ArgsMessages.ERR_PARSER_LONG_ARG_MISSING_VALUE.get(argName));
                        }
                        a.addValue(args[i]);
                    }
                }
                else if (equalPos > 0) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_LONG_ARG_DOESNT_TAKE_VALUE.get(argName));
                }
            }
            else if (s.startsWith("-")) {
                if (s.length() == 1) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_UNEXPECTED_DASH.get());
                }
                if (s.length() == 2) {
                    final char c = s.charAt(1);
                    Argument a2 = this.namedArgsByShortID.get(c);
                    if (a2 == null && subCommandParser != null) {
                        a2 = subCommandParser.namedArgsByShortID.get(c);
                    }
                    if (a2 == null) {
                        throw new ArgumentException(ArgsMessages.ERR_PARSER_NO_SUCH_SHORT_ID.get(c));
                    }
                    if (a2.isUsageArgument()) {
                        skipFinalValidation |= skipFinalValidationBecauseOfArgument(a2);
                    }
                    a2.incrementOccurrences();
                    if (a2.takesValue()) {
                        if (++i >= args.length) {
                            throw new ArgumentException(ArgsMessages.ERR_PARSER_SHORT_ARG_MISSING_VALUE.get(c));
                        }
                        a2.addValue(args[i]);
                    }
                }
                else {
                    char c = s.charAt(1);
                    Argument a2 = this.namedArgsByShortID.get(c);
                    if (a2 == null && subCommandParser != null) {
                        a2 = subCommandParser.namedArgsByShortID.get(c);
                    }
                    if (a2 == null) {
                        throw new ArgumentException(ArgsMessages.ERR_PARSER_NO_SUCH_SHORT_ID.get(c));
                    }
                    if (a2.isUsageArgument()) {
                        skipFinalValidation |= skipFinalValidationBecauseOfArgument(a2);
                    }
                    a2.incrementOccurrences();
                    if (a2.takesValue()) {
                        a2.addValue(s.substring(2));
                    }
                    else {
                        for (int j = 2; j < s.length(); ++j) {
                            c = s.charAt(j);
                            a2 = this.namedArgsByShortID.get(c);
                            if (a2 == null && subCommandParser != null) {
                                a2 = subCommandParser.namedArgsByShortID.get(c);
                            }
                            if (a2 == null) {
                                throw new ArgumentException(ArgsMessages.ERR_PARSER_NO_SUBSEQUENT_SHORT_ARG.get(c, s));
                            }
                            if (a2.isUsageArgument()) {
                                skipFinalValidation |= skipFinalValidationBecauseOfArgument(a2);
                            }
                            a2.incrementOccurrences();
                            if (a2.takesValue()) {
                                throw new ArgumentException(ArgsMessages.ERR_PARSER_SUBSEQUENT_SHORT_ARG_TAKES_VALUE.get(c, s));
                            }
                        }
                    }
                }
            }
            else if (this.subCommands.isEmpty()) {
                inTrailingArgs = true;
                if (this.maxTrailingArgs == 0) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_TRAILING_ARGS_NOT_ALLOWED.get(s, this.commandName));
                }
                this.trailingArgs.add(s);
            }
            else {
                if (this.selectedSubCommand != null) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_CONFLICTING_SUBCOMMANDS.get(subCommandName, s));
                }
                subCommandName = s;
                this.selectedSubCommand = this.subCommandsByName.get(StaticUtils.toLowerCase(s));
                if (this.selectedSubCommand == null) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_NO_SUCH_SUBCOMMAND.get(s, this.commandName));
                }
                this.selectedSubCommand.setPresent();
                subCommandParser = this.selectedSubCommand.getArgumentParser();
            }
        }
        if (!this.handlePropertiesFile()) {
            return;
        }
        if (skipFinalValidation) {
            return;
        }
        if (!this.subCommands.isEmpty() && this.selectedSubCommand == null) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_MISSING_SUBCOMMAND.get(this.commandName));
        }
        doFinalValidation(this);
        if (this.selectedSubCommand != null) {
            doFinalValidation(this.selectedSubCommand.getArgumentParser());
        }
    }
    
    public void setCommandLineTool(final CommandLineTool commandLineTool) {
        this.commandLineTool = commandLineTool;
    }
    
    private static void doFinalValidation(final ArgumentParser parser) throws ArgumentException {
        for (final Argument a : parser.namedArgs) {
            if (a.isRequired() && !a.isPresent()) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_MISSING_REQUIRED_ARG.get(a.getIdentifierString()));
            }
        }
        if (parser.trailingArgs.size() < parser.minTrailingArgs) {
            throw new ArgumentException(ArgsMessages.ERR_PARSER_NOT_ENOUGH_TRAILING_ARGS.get(parser.commandName, parser.minTrailingArgs, parser.trailingArgsPlaceholder));
        }
        for (final ObjectPair<Argument, Set<Argument>> p : parser.dependentArgumentSets) {
            final Argument targetArg = p.getFirst();
            if (targetArg.getNumOccurrences() > 0) {
                final Set<Argument> argSet = p.getSecond();
                boolean found = false;
                for (final Argument a2 : argSet) {
                    if (a2.getNumOccurrences() > 0) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    continue;
                }
                if (argSet.size() == 1) {
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_DEPENDENT_CONFLICT_SINGLE.get(targetArg.getIdentifierString(), argSet.iterator().next().getIdentifierString()));
                }
                boolean first = true;
                final StringBuilder buffer = new StringBuilder();
                for (final Argument a3 : argSet) {
                    if (first) {
                        first = false;
                    }
                    else {
                        buffer.append(", ");
                    }
                    buffer.append(a3.getIdentifierString());
                }
                throw new ArgumentException(ArgsMessages.ERR_PARSER_DEPENDENT_CONFLICT_MULTIPLE.get(targetArg.getIdentifierString(), buffer.toString()));
            }
        }
        for (final Set<Argument> argSet2 : parser.exclusiveArgumentSets) {
            Argument setArg = null;
            for (final Argument a4 : argSet2) {
                if (a4.getNumOccurrences() > 0) {
                    if (setArg != null) {
                        throw new ArgumentException(ArgsMessages.ERR_PARSER_EXCLUSIVE_CONFLICT.get(setArg.getIdentifierString(), a4.getIdentifierString()));
                    }
                    setArg = a4;
                }
            }
        }
        for (final Set<Argument> argSet2 : parser.requiredArgumentSets) {
            boolean found2 = false;
            for (final Argument a4 : argSet2) {
                if (a4.getNumOccurrences() > 0) {
                    found2 = true;
                    break;
                }
            }
            if (!found2) {
                boolean first2 = true;
                final StringBuilder buffer2 = new StringBuilder();
                for (final Argument a2 : argSet2) {
                    if (first2) {
                        first2 = false;
                    }
                    else {
                        buffer2.append(", ");
                    }
                    buffer2.append(a2.getIdentifierString());
                }
                throw new ArgumentException(ArgsMessages.ERR_PARSER_REQUIRED_CONFLICT.get(buffer2.toString()));
            }
        }
    }
    
    private static boolean skipFinalValidationBecauseOfArgument(final Argument a) {
        return !"propertiesFilePath".equals(a.getLongIdentifier()) && !"noPropertiesFile".equals(a.getLongIdentifier()) && !"suppressPropertiesFileComment".equals(a.getLongIdentifier()) && !"outputFile".equals(a.getLongIdentifier()) && !"teeOutput".equals(a.getLongIdentifier()) && a.isUsageArgument();
    }
    
    private boolean handlePropertiesFile() throws ArgumentException {
        FileArgument propertiesFilePath;
        FileArgument generatePropertiesFile;
        BooleanArgument noPropertiesFile;
        try {
            propertiesFilePath = this.getFileArgument("propertiesFilePath");
            generatePropertiesFile = this.getFileArgument("generatePropertiesFile");
            noPropertiesFile = this.getBooleanArgument("noPropertiesFile");
        }
        catch (final Exception e) {
            Debug.debugException(e);
            return true;
        }
        if (propertiesFilePath == null || generatePropertiesFile == null || noPropertiesFile == null) {
            return true;
        }
        if (noPropertiesFile.isPresent()) {
            if (propertiesFilePath.isPresent()) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_EXCLUSIVE_CONFLICT.get(noPropertiesFile.getIdentifierString(), propertiesFilePath.getIdentifierString()));
            }
            if (generatePropertiesFile.isPresent()) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_EXCLUSIVE_CONFLICT.get(noPropertiesFile.getIdentifierString(), generatePropertiesFile.getIdentifierString()));
            }
            return true;
        }
        else if (generatePropertiesFile.isPresent()) {
            if (propertiesFilePath.isPresent()) {
                throw new ArgumentException(ArgsMessages.ERR_PARSER_EXCLUSIVE_CONFLICT.get(generatePropertiesFile.getIdentifierString(), propertiesFilePath.getIdentifierString()));
            }
            this.generatePropertiesFile(generatePropertiesFile.getValue().getAbsolutePath());
            return false;
        }
        else {
            if (!propertiesFilePath.isPresent()) {
                String path = StaticUtils.getSystemProperty(ArgumentParser.PROPERTY_DEFAULT_PROPERTIES_FILE_PATH);
                if (path == null) {
                    path = StaticUtils.getEnvironmentVariable("UNBOUNDID_TOOL_PROPERTIES_FILE_PATH");
                }
                if (path != null) {
                    final File propertiesFile = new File(path);
                    if (propertiesFile.exists() && propertiesFile.isFile()) {
                        this.handlePropertiesFile(propertiesFile);
                    }
                }
                return true;
            }
            final File propertiesFile2 = propertiesFilePath.getValue();
            if (propertiesFile2.exists() && propertiesFile2.isFile()) {
                this.handlePropertiesFile(propertiesFilePath.getValue());
                return true;
            }
            throw new ArgumentException(ArgsMessages.ERR_PARSER_NO_SUCH_PROPERTIES_FILE.get(propertiesFilePath.getIdentifierString(), propertiesFile2.getAbsolutePath()));
        }
    }
    
    private void generatePropertiesFile(final String path) throws ArgumentException {
        PrintWriter w;
        try {
            w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.ISO_8859_1));
        }
        catch (final Exception e) {
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_PARSER_GEN_PROPS_CANNOT_OPEN_FILE.get(path, StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            wrapComment(w, ArgsMessages.INFO_PARSER_GEN_PROPS_HEADER_1.get(this.commandName));
            w.println('#');
            wrapComment(w, ArgsMessages.INFO_PARSER_GEN_PROPS_HEADER_2.get(this.commandName, "propertiesFilePath", ArgumentParser.PROPERTY_DEFAULT_PROPERTIES_FILE_PATH, "UNBOUNDID_TOOL_PROPERTIES_FILE_PATH", "noPropertiesFile"));
            w.println('#');
            wrapComment(w, ArgsMessages.INFO_PARSER_GEN_PROPS_HEADER_3.get());
            w.println('#');
            wrapComment(w, ArgsMessages.INFO_PARSER_GEN_PROPS_HEADER_4.get());
            w.println('#');
            wrapComment(w, ArgsMessages.INFO_PARSER_GEN_PROPS_HEADER_5.get(this.commandName));
            for (final Argument a : this.getNamedArguments()) {
                this.writeArgumentProperties(w, null, a);
            }
            for (final SubCommand sc : this.getSubCommands()) {
                for (final Argument a2 : sc.getArgumentParser().getNamedArguments()) {
                    this.writeArgumentProperties(w, sc, a2);
                }
            }
        }
        finally {
            w.close();
        }
    }
    
    private void writeArgumentProperties(final PrintWriter w, final SubCommand sc, final Argument a) {
        if (a.isUsageArgument() || a.isHidden()) {
            return;
        }
        w.println();
        w.println();
        wrapComment(w, a.getDescription());
        w.println('#');
        final String constraints = a.getValueConstraints();
        if (constraints != null && !constraints.isEmpty() && !(a instanceof BooleanArgument)) {
            wrapComment(w, constraints);
            w.println('#');
        }
        String identifier;
        if (a.getLongIdentifier() != null) {
            identifier = a.getLongIdentifier();
        }
        else {
            identifier = a.getIdentifierString();
        }
        String placeholder = a.getValuePlaceholder();
        if (placeholder == null) {
            if (a instanceof BooleanArgument) {
                placeholder = "{true|false}";
            }
            else {
                placeholder = "";
            }
        }
        String propertyName;
        if (sc == null) {
            propertyName = this.commandName + '.' + identifier;
        }
        else {
            propertyName = this.commandName + '.' + sc.getPrimaryName() + '.' + identifier;
        }
        w.println("# " + propertyName + '=' + placeholder);
        if (a.isPresent()) {
            for (final String s : a.getValueStringRepresentations(false)) {
                w.println(propertyName + '=' + s);
            }
        }
    }
    
    private static void wrapComment(final PrintWriter w, final String s) {
        for (final String line : StaticUtils.wrapLine(s, 77)) {
            w.println("# " + line);
        }
    }
    
    private void handlePropertiesFile(final File propertiesFile) throws ArgumentException {
        final String propertiesFilePath = propertiesFile.getAbsolutePath();
        InputStream inputStream = null;
        BufferedReader reader;
        try {
            inputStream = new FileInputStream(propertiesFile);
            final CommandLineTool tool = this.commandLineTool;
            List<char[]> cachedPasswords;
            PrintStream out;
            PrintStream err;
            if (tool == null) {
                cachedPasswords = Collections.emptyList();
                out = System.out;
                err = System.err;
            }
            else {
                cachedPasswords = tool.getPasswordFileReader().getCachedEncryptionPasswords();
                out = tool.getOut();
                err = tool.getErr();
            }
            final ObjectPair<InputStream, char[]> encryptionData = ToolUtils.getPossiblyPassphraseEncryptedInputStream(inputStream, cachedPasswords, true, ArgsMessages.INFO_PARSER_PROMPT_FOR_PROP_FILE_ENC_PW.get(propertiesFile.getAbsolutePath()), ArgsMessages.ERR_PARSER_WRONG_PROP_FILE_ENC_PW.get(propertiesFile.getAbsolutePath()), out, err);
            inputStream = encryptionData.getFirst();
            if (tool != null && encryptionData.getSecond() != null) {
                tool.getPasswordFileReader().addToEncryptionPasswordCache(encryptionData.getSecond());
            }
            inputStream = ToolUtils.getPossiblyGZIPCompressedInputStream(inputStream);
            reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
        }
        catch (final Exception e) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
            }
            Debug.debugException(e);
            throw new ArgumentException(ArgsMessages.ERR_PARSER_CANNOT_OPEN_PROP_FILE.get(propertiesFilePath, StaticUtils.getExceptionMessage(e)), e);
        }
        try {
            boolean lineIsContinued = false;
            int lineNumber = 0;
            final ArrayList<ObjectPair<Integer, StringBuilder>> propertyLines = new ArrayList<ObjectPair<Integer, StringBuilder>>(10);
            while (true) {
                String line;
                try {
                    line = reader.readLine();
                    ++lineNumber;
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                    throw new ArgumentException(ArgsMessages.ERR_PARSER_ERROR_READING_PROP_FILE.get(propertiesFilePath, StaticUtils.getExceptionMessage(e3)), e3);
                }
                if (line == null) {
                    if (lineIsContinued) {
                        throw new ArgumentException(ArgsMessages.ERR_PARSER_PROP_FILE_MISSING_CONTINUATION.get(lineNumber - 1, propertiesFilePath));
                    }
                    this.propertiesFileUsed = propertiesFile;
                    if (propertyLines.isEmpty()) {
                        return;
                    }
                    final HashMap<String, ArrayList<String>> propertyMap = new HashMap<String, ArrayList<String>>(StaticUtils.computeMapCapacity(propertyLines.size()));
                    for (final ObjectPair<Integer, StringBuilder> p : propertyLines) {
                        lineNumber = p.getFirst();
                        final String line2 = handleUnicodeEscapes(propertiesFilePath, lineNumber, p.getSecond());
                        final int equalPos = line2.indexOf(61);
                        if (equalPos <= 0) {
                            throw new ArgumentException(ArgsMessages.ERR_PARSER_MALFORMED_PROP_LINE.get(propertiesFilePath, lineNumber, line2));
                        }
                        final String propertyName = line2.substring(0, equalPos).trim();
                        final String propertyValue = line2.substring(equalPos + 1).trim();
                        if (propertyValue.isEmpty()) {
                            continue;
                        }
                        boolean prefixedWithToolName = false;
                        boolean prefixedWithSubCommandName = false;
                        Argument a = this.getNamedArgument(propertyName);
                        if (a == null) {
                            if (propertyName.startsWith(this.commandName + '.')) {
                                prefixedWithToolName = true;
                                String basePropertyName = propertyName.substring(this.commandName.length() + 1);
                                a = this.getNamedArgument(basePropertyName);
                                if (a == null) {
                                    final int periodPos = basePropertyName.indexOf(46);
                                    if (periodPos > 0) {
                                        final String subCommandName = basePropertyName.substring(0, periodPos);
                                        if (this.selectedSubCommand != null && this.selectedSubCommand.hasName(subCommandName)) {
                                            prefixedWithSubCommandName = true;
                                            basePropertyName = basePropertyName.substring(periodPos + 1);
                                            a = this.selectedSubCommand.getArgumentParser().getNamedArgument(basePropertyName);
                                        }
                                    }
                                    else if (this.selectedSubCommand != null) {
                                        a = this.selectedSubCommand.getArgumentParser().getNamedArgument(basePropertyName);
                                    }
                                }
                            }
                            else if (this.selectedSubCommand != null) {
                                a = this.selectedSubCommand.getArgumentParser().getNamedArgument(propertyName);
                            }
                        }
                        if (a == null) {
                            continue;
                        }
                        String canonicalPropertyName;
                        if (prefixedWithToolName) {
                            if (prefixedWithSubCommandName) {
                                canonicalPropertyName = this.commandName + '.' + this.selectedSubCommand.getPrimaryName() + '.' + a.getIdentifierString();
                            }
                            else {
                                canonicalPropertyName = this.commandName + '.' + a.getIdentifierString();
                            }
                        }
                        else {
                            canonicalPropertyName = a.getIdentifierString();
                        }
                        ArrayList<String> valueList = propertyMap.get(canonicalPropertyName);
                        if (valueList == null) {
                            valueList = new ArrayList<String>(5);
                            propertyMap.put(canonicalPropertyName, valueList);
                        }
                        valueList.add(propertyValue);
                    }
                    this.setArgsFromPropertiesFile(propertyMap, false);
                    if (this.selectedSubCommand != null) {
                        this.setArgsFromPropertiesFile(propertyMap, true);
                    }
                }
                else {
                    final int initialLength = line.length();
                    line = StaticUtils.trimLeading(line);
                    final boolean hasLeadingWhitespace = line.length() < initialLength;
                    if (hasLeadingWhitespace && !lineIsContinued) {
                        throw new ArgumentException(ArgsMessages.ERR_PARSER_PROP_FILE_UNEXPECTED_LEADING_SPACE.get(propertiesFilePath, lineNumber));
                    }
                    if (line.isEmpty() || line.startsWith("#")) {
                        if (lineIsContinued) {
                            throw new ArgumentException(ArgsMessages.ERR_PARSER_PROP_FILE_MISSING_CONTINUATION.get(lineNumber - 1, propertiesFilePath));
                        }
                        continue;
                    }
                    else {
                        final boolean hasTrailingBackslash = line.endsWith("\\");
                        if (line.endsWith("\\")) {
                            line = line.substring(0, line.length() - 1);
                        }
                        if (lineIsContinued) {
                            ((StringBuilder)propertyLines.get(propertyLines.size() - 1).getSecond()).append(line);
                        }
                        else {
                            propertyLines.add(new ObjectPair<Integer, StringBuilder>(lineNumber, new StringBuilder(line)));
                        }
                        lineIsContinued = hasTrailingBackslash;
                    }
                }
            }
        }
        finally {
            try {
                reader.close();
            }
            catch (final Exception e4) {
                Debug.debugException(e4);
            }
        }
    }
    
    static String handleUnicodeEscapes(final String propertiesFilePath, final int lineNumber, final StringBuilder buffer) throws ArgumentException {
        for (int pos = 0; pos < buffer.length(); ++pos) {
            final char c = buffer.charAt(pos);
            if (c == '\\' && pos <= buffer.length() - 5) {
                final char nextChar = buffer.charAt(pos + 1);
                Label_0171: {
                    if (nextChar != 'u') {
                        if (nextChar != 'U') {
                            break Label_0171;
                        }
                    }
                    try {
                        final String hexDigits = buffer.substring(pos + 2, pos + 6);
                        final byte[] bytes = StaticUtils.fromHex(hexDigits);
                        final int i = (bytes[0] & 0xFF) << 8 | (bytes[1] & 0xFF);
                        buffer.setCharAt(pos, (char)i);
                        for (int j = 0; j < 5; ++j) {
                            buffer.deleteCharAt(pos + 1);
                        }
                        continue;
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                        throw new ArgumentException(ArgsMessages.ERR_PARSER_MALFORMED_UNICODE_ESCAPE.get(propertiesFilePath, lineNumber), e);
                    }
                }
                buffer.deleteCharAt(pos);
            }
        }
        return buffer.toString();
    }
    
    private void setArgsFromPropertiesFile(final Map<String, ArrayList<String>> propertyMap, final boolean useSubCommand) throws ArgumentException {
        ArgumentParser p;
        if (useSubCommand) {
            p = this.selectedSubCommand.getArgumentParser();
        }
        else {
            p = this;
        }
        for (final Argument a : p.namedArgs) {
            if (a.getNumOccurrences() > 0) {
                continue;
            }
            boolean exclusiveArgumentHasValue = false;
        Label_0160:
            for (final Set<Argument> exclusiveArgumentSet : this.exclusiveArgumentSets) {
                if (exclusiveArgumentSet.contains(a)) {
                    for (final Argument exclusiveArg : exclusiveArgumentSet) {
                        if (exclusiveArg.getNumOccurrences() > 0) {
                            exclusiveArgumentHasValue = true;
                            break Label_0160;
                        }
                    }
                }
            }
            if (exclusiveArgumentHasValue) {
                continue;
            }
            List<String> values = null;
            if (useSubCommand) {
                values = propertyMap.get(this.commandName + '.' + this.selectedSubCommand.getPrimaryName() + '.' + a.getIdentifierString());
            }
            if (values == null) {
                values = propertyMap.get(this.commandName + '.' + a.getIdentifierString());
            }
            if (values == null) {
                values = propertyMap.get(a.getIdentifierString());
            }
            if (values == null) {
                continue;
            }
            for (final String value : values) {
                if (a instanceof BooleanArgument) {
                    final BooleanValueArgument bva = new BooleanValueArgument(a.getShortIdentifier(), a.getLongIdentifier(), false, null, a.getDescription());
                    bva.addValue(value);
                    if (!bva.getValue()) {
                        continue;
                    }
                    a.incrementOccurrences();
                    this.argumentsSetFromPropertiesFile.add(a.getIdentifierString());
                }
                else {
                    a.addValue(value);
                    a.incrementOccurrences();
                    this.argumentsSetFromPropertiesFile.add(a.getIdentifierString());
                    if (a.isSensitive()) {
                        this.argumentsSetFromPropertiesFile.add("***REDACTED***");
                    }
                    else {
                        this.argumentsSetFromPropertiesFile.add(value);
                    }
                }
            }
        }
    }
    
    public List<String> getUsage(final int maxWidth) {
        if (this.selectedSubCommand != null) {
            return this.getSubCommandUsage(maxWidth);
        }
        final ArrayList<String> lines = new ArrayList<String>(100);
        lines.addAll(StaticUtils.wrapLine(this.commandDescription, maxWidth));
        lines.add("");
        for (final String additionalDescriptionParagraph : this.additionalCommandDescriptionParagraphs) {
            lines.addAll(StaticUtils.wrapLine(additionalDescriptionParagraph, maxWidth));
            lines.add("");
        }
        if (!this.subCommands.isEmpty() && this.subCommands.size() < 10) {
            lines.add(ArgsMessages.INFO_USAGE_SUBCOMMANDS_HEADER.get());
            lines.add("");
            for (final SubCommand sc : this.subCommands) {
                final StringBuilder nameBuffer = new StringBuilder();
                nameBuffer.append("  ");
                final Iterator<String> nameIterator = sc.getNames(false).iterator();
                while (nameIterator.hasNext()) {
                    nameBuffer.append(nameIterator.next());
                    if (nameIterator.hasNext()) {
                        nameBuffer.append(", ");
                    }
                }
                lines.add(nameBuffer.toString());
                for (final String descriptionLine : StaticUtils.wrapLine(sc.getDescription(), maxWidth - 4)) {
                    lines.add("    " + descriptionLine);
                }
                lines.add("");
            }
        }
        if (!this.subCommands.isEmpty()) {
            lines.addAll(StaticUtils.wrapLine(ArgsMessages.INFO_USAGE_SUBCOMMAND_USAGE.get(this.commandName), maxWidth));
        }
        else if (this.namedArgs.isEmpty()) {
            if (this.maxTrailingArgs == 0) {
                lines.addAll(StaticUtils.wrapLine(ArgsMessages.INFO_USAGE_NOOPTIONS_NOTRAILING.get(this.commandName), maxWidth));
            }
            else {
                lines.addAll(StaticUtils.wrapLine(ArgsMessages.INFO_USAGE_NOOPTIONS_TRAILING.get(this.commandName, this.trailingArgsPlaceholder), maxWidth));
            }
        }
        else if (this.maxTrailingArgs == 0) {
            lines.addAll(StaticUtils.wrapLine(ArgsMessages.INFO_USAGE_OPTIONS_NOTRAILING.get(this.commandName), maxWidth));
        }
        else {
            lines.addAll(StaticUtils.wrapLine(ArgsMessages.INFO_USAGE_OPTIONS_TRAILING.get(this.commandName, this.trailingArgsPlaceholder), maxWidth));
        }
        if (!this.namedArgs.isEmpty()) {
            lines.add("");
            lines.add(ArgsMessages.INFO_USAGE_OPTIONS_INCLUDE.get());
            boolean hasRequired = false;
            final LinkedHashMap<String, List<Argument>> argumentsByGroup = new LinkedHashMap<String, List<Argument>>(StaticUtils.computeMapCapacity(10));
            final ArrayList<Argument> argumentsWithoutGroup = new ArrayList<Argument>(this.namedArgs.size());
            final ArrayList<Argument> usageArguments = new ArrayList<Argument>(this.namedArgs.size());
            for (final Argument a : this.namedArgs) {
                if (a.isHidden()) {
                    continue;
                }
                if (a.isRequired() && !a.hasDefaultValue()) {
                    hasRequired = true;
                }
                final String argumentGroup = a.getArgumentGroupName();
                if (argumentGroup == null) {
                    if (a.isUsageArgument()) {
                        usageArguments.add(a);
                    }
                    else {
                        argumentsWithoutGroup.add(a);
                    }
                }
                else {
                    List<Argument> groupArgs = argumentsByGroup.get(argumentGroup);
                    if (groupArgs == null) {
                        groupArgs = new ArrayList<Argument>(10);
                        argumentsByGroup.put(argumentGroup, groupArgs);
                    }
                    groupArgs.add(a);
                }
            }
            for (final Map.Entry<String, List<Argument>> e : argumentsByGroup.entrySet()) {
                lines.add("");
                lines.add("  " + e.getKey());
                lines.add("");
                for (final Argument a2 : e.getValue()) {
                    getArgUsage(a2, lines, true, maxWidth);
                }
            }
            if (!argumentsWithoutGroup.isEmpty()) {
                if (argumentsByGroup.isEmpty()) {
                    for (final Argument a : argumentsWithoutGroup) {
                        getArgUsage(a, lines, false, maxWidth);
                    }
                }
                else {
                    lines.add("");
                    lines.add("  " + ArgsMessages.INFO_USAGE_UNGROUPED_ARGS.get());
                    lines.add("");
                    for (final Argument a : argumentsWithoutGroup) {
                        getArgUsage(a, lines, true, maxWidth);
                    }
                }
            }
            if (!usageArguments.isEmpty()) {
                if (argumentsByGroup.isEmpty()) {
                    for (final Argument a : usageArguments) {
                        getArgUsage(a, lines, false, maxWidth);
                    }
                }
                else {
                    lines.add("");
                    lines.add("  " + ArgsMessages.INFO_USAGE_USAGE_ARGS.get());
                    lines.add("");
                    for (final Argument a : usageArguments) {
                        getArgUsage(a, lines, true, maxWidth);
                    }
                }
            }
            if (hasRequired) {
                lines.add("");
                if (argumentsByGroup.isEmpty()) {
                    lines.add("* " + ArgsMessages.INFO_USAGE_ARG_IS_REQUIRED.get());
                }
                else {
                    lines.add("  * " + ArgsMessages.INFO_USAGE_ARG_IS_REQUIRED.get());
                }
            }
        }
        return lines;
    }
    
    private List<String> getSubCommandUsage(final int maxWidth) {
        final ArrayList<String> lines = new ArrayList<String>(100);
        lines.addAll(StaticUtils.wrapLine(this.selectedSubCommand.getDescription(), maxWidth));
        lines.add("");
        lines.addAll(StaticUtils.wrapLine(ArgsMessages.INFO_SUBCOMMAND_USAGE_OPTIONS.get(this.commandName, this.selectedSubCommand.getPrimaryName()), maxWidth));
        final ArgumentParser parser = this.selectedSubCommand.getArgumentParser();
        if (!parser.namedArgs.isEmpty()) {
            lines.add("");
            lines.add(ArgsMessages.INFO_USAGE_OPTIONS_INCLUDE.get());
            boolean hasRequired = false;
            final LinkedHashMap<String, List<Argument>> argumentsByGroup = new LinkedHashMap<String, List<Argument>>(StaticUtils.computeMapCapacity(10));
            final ArrayList<Argument> argumentsWithoutGroup = new ArrayList<Argument>(parser.namedArgs.size());
            final ArrayList<Argument> usageArguments = new ArrayList<Argument>(parser.namedArgs.size());
            for (final Argument a : parser.namedArgs) {
                if (a.isHidden()) {
                    continue;
                }
                if (a.isRequired() && !a.hasDefaultValue()) {
                    hasRequired = true;
                }
                final String argumentGroup = a.getArgumentGroupName();
                if (argumentGroup == null) {
                    if (a.isUsageArgument()) {
                        usageArguments.add(a);
                    }
                    else {
                        argumentsWithoutGroup.add(a);
                    }
                }
                else {
                    List<Argument> groupArgs = argumentsByGroup.get(argumentGroup);
                    if (groupArgs == null) {
                        groupArgs = new ArrayList<Argument>(10);
                        argumentsByGroup.put(argumentGroup, groupArgs);
                    }
                    groupArgs.add(a);
                }
            }
            for (final Map.Entry<String, List<Argument>> e : argumentsByGroup.entrySet()) {
                lines.add("");
                lines.add("  " + e.getKey());
                lines.add("");
                for (final Argument a2 : e.getValue()) {
                    getArgUsage(a2, lines, true, maxWidth);
                }
            }
            if (!argumentsWithoutGroup.isEmpty()) {
                if (argumentsByGroup.isEmpty()) {
                    for (final Argument a : argumentsWithoutGroup) {
                        getArgUsage(a, lines, false, maxWidth);
                    }
                }
                else {
                    lines.add("");
                    lines.add("  " + ArgsMessages.INFO_USAGE_UNGROUPED_ARGS.get());
                    lines.add("");
                    for (final Argument a : argumentsWithoutGroup) {
                        getArgUsage(a, lines, true, maxWidth);
                    }
                }
            }
            if (!usageArguments.isEmpty()) {
                if (argumentsByGroup.isEmpty()) {
                    for (final Argument a : usageArguments) {
                        getArgUsage(a, lines, false, maxWidth);
                    }
                }
                else {
                    lines.add("");
                    lines.add("  " + ArgsMessages.INFO_USAGE_USAGE_ARGS.get());
                    lines.add("");
                    for (final Argument a : usageArguments) {
                        getArgUsage(a, lines, true, maxWidth);
                    }
                }
            }
            if (hasRequired) {
                lines.add("");
                if (argumentsByGroup.isEmpty()) {
                    lines.add("* " + ArgsMessages.INFO_USAGE_ARG_IS_REQUIRED.get());
                }
                else {
                    lines.add("  * " + ArgsMessages.INFO_USAGE_ARG_IS_REQUIRED.get());
                }
            }
        }
        return lines;
    }
    
    private static void getArgUsage(final Argument a, final List<String> lines, final boolean indent, final int maxWidth) {
        final StringBuilder argLine = new StringBuilder();
        if (indent && maxWidth > 10) {
            if (a.isRequired() && !a.hasDefaultValue()) {
                argLine.append("  * ");
            }
            else {
                argLine.append("    ");
            }
        }
        else if (a.isRequired() && !a.hasDefaultValue()) {
            argLine.append("* ");
        }
        boolean first = true;
        for (final Character c : a.getShortIdentifiers(false)) {
            if (first) {
                argLine.append('-');
                first = false;
            }
            else {
                argLine.append(", -");
            }
            argLine.append(c);
        }
        for (final String s : a.getLongIdentifiers(false)) {
            if (first) {
                argLine.append("--");
                first = false;
            }
            else {
                argLine.append(", --");
            }
            argLine.append(s);
        }
        final String valuePlaceholder = a.getValuePlaceholder();
        if (valuePlaceholder != null) {
            argLine.append(' ');
            argLine.append(valuePlaceholder);
        }
        int subsequentLineWidth = maxWidth - 4;
        if (subsequentLineWidth < 4) {
            subsequentLineWidth = maxWidth;
        }
        final List<String> identifierLines = StaticUtils.wrapLine(argLine.toString(), maxWidth, subsequentLineWidth);
        for (int i = 0; i < identifierLines.size(); ++i) {
            if (i == 0) {
                lines.add(identifierLines.get(0));
            }
            else {
                lines.add("    " + identifierLines.get(i));
            }
        }
        final String description = a.getDescription();
        if (maxWidth > 10) {
            String indentString;
            if (indent) {
                indentString = "        ";
            }
            else {
                indentString = "    ";
            }
            final List<String> descLines = StaticUtils.wrapLine(description, maxWidth - indentString.length());
            for (final String s2 : descLines) {
                lines.add(indentString + s2);
            }
        }
        else {
            lines.addAll(StaticUtils.wrapLine(description, maxWidth));
        }
    }
    
    public void getUsage(final OutputStream outputStream, final int maxWidth) throws IOException {
        final List<String> usageLines = this.getUsage(maxWidth);
        for (final String s : usageLines) {
            outputStream.write(StaticUtils.getBytes(s));
            outputStream.write(StaticUtils.EOL_BYTES);
        }
    }
    
    public String getUsageString(final int maxWidth) {
        final StringBuilder buffer = new StringBuilder();
        this.getUsageString(buffer, maxWidth);
        return buffer.toString();
    }
    
    public void getUsageString(final StringBuilder buffer, final int maxWidth) {
        for (final String line : this.getUsage(maxWidth)) {
            buffer.append(line);
            buffer.append(StaticUtils.EOL);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("ArgumentParser(commandName='");
        buffer.append(this.commandName);
        buffer.append("', commandDescription={");
        buffer.append('\'');
        buffer.append(this.commandDescription);
        buffer.append('\'');
        if (this.additionalCommandDescriptionParagraphs != null) {
            for (final String additionalParagraph : this.additionalCommandDescriptionParagraphs) {
                buffer.append(", '");
                buffer.append(additionalParagraph);
                buffer.append('\'');
            }
        }
        buffer.append("}, minTrailingArgs=");
        buffer.append(this.minTrailingArgs);
        buffer.append(", maxTrailingArgs=");
        buffer.append(this.maxTrailingArgs);
        if (this.trailingArgsPlaceholder != null) {
            buffer.append(", trailingArgsPlaceholder='");
            buffer.append(this.trailingArgsPlaceholder);
            buffer.append('\'');
        }
        buffer.append(", namedArgs={");
        final Iterator<Argument> iterator = this.namedArgs.iterator();
        while (iterator.hasNext()) {
            iterator.next().toString(buffer);
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append('}');
        if (!this.subCommands.isEmpty()) {
            buffer.append(", subCommands={");
            final Iterator<SubCommand> subCommandIterator = this.subCommands.iterator();
            while (subCommandIterator.hasNext()) {
                subCommandIterator.next().toString(buffer);
                if (subCommandIterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(')');
    }
    
    static {
        PROPERTY_DEFAULT_PROPERTIES_FILE_PATH = ArgumentParser.class.getName() + ".propertiesFilePath";
    }
}
