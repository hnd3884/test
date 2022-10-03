package com.unboundid.util.args;

import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ObjectPair;
import java.util.Map;
import java.util.LinkedHashMap;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.INTERFACE_NOT_THREADSAFE)
public final class SubCommand
{
    private volatile ArgumentParser globalArgumentParser;
    private final ArgumentParser subcommandArgumentParser;
    private volatile boolean isPresent;
    private final LinkedHashMap<String[], String> exampleUsages;
    private final Map<String, ObjectPair<String, Boolean>> names;
    private final String description;
    
    public SubCommand(final String name, final String description, final ArgumentParser parser, final LinkedHashMap<String[], String> exampleUsages) throws ArgumentException {
        this.names = new LinkedHashMap<String, ObjectPair<String, Boolean>>(StaticUtils.computeMapCapacity(5));
        this.addName(name);
        this.description = description;
        if (description == null || description.isEmpty()) {
            throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_DESCRIPTION_NULL_OR_EMPTY.get());
        }
        if ((this.subcommandArgumentParser = parser) == null) {
            throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_PARSER_NULL.get());
        }
        if (parser.allowsTrailingArguments()) {
            throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_PARSER_ALLOWS_TRAILING_ARGS.get());
        }
        if (parser.hasSubCommands()) {
            throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_PARSER_HAS_SUBCOMMANDS.get());
        }
        if (exampleUsages == null) {
            this.exampleUsages = new LinkedHashMap<String[], String>(StaticUtils.computeMapCapacity(10));
        }
        else {
            this.exampleUsages = new LinkedHashMap<String[], String>(exampleUsages);
        }
        this.isPresent = false;
        this.globalArgumentParser = null;
    }
    
    private SubCommand(final SubCommand source) {
        this.names = new LinkedHashMap<String, ObjectPair<String, Boolean>>(source.names);
        this.description = source.description;
        this.subcommandArgumentParser = new ArgumentParser(source.subcommandArgumentParser, this);
        this.exampleUsages = new LinkedHashMap<String[], String>(source.exampleUsages);
        this.isPresent = false;
        this.globalArgumentParser = null;
    }
    
    public String getPrimaryName() {
        return (String)this.names.values().iterator().next().getFirst();
    }
    
    public List<String> getNames() {
        return this.getNames(true);
    }
    
    public List<String> getNames(final boolean includeHidden) {
        final ArrayList<String> nameList = new ArrayList<String>(this.names.size());
        for (final ObjectPair<String, Boolean> p : this.names.values()) {
            if (includeHidden || !p.getSecond()) {
                nameList.add(p.getFirst());
            }
        }
        return Collections.unmodifiableList((List<? extends String>)nameList);
    }
    
    public boolean hasName(final String name) {
        return this.names.containsKey(StaticUtils.toLowerCase(name));
    }
    
    public void addName(final String name) throws ArgumentException {
        this.addName(name, false);
    }
    
    public void addName(final String name, final boolean isHidden) throws ArgumentException {
        if (name == null || name.isEmpty()) {
            throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_NAME_NULL_OR_EMPTY.get());
        }
        final String lowerName = StaticUtils.toLowerCase(name);
        if (this.names.containsKey(lowerName)) {
            throw new ArgumentException(ArgsMessages.ERR_SUBCOMMAND_NAME_ALREADY_IN_USE.get(name));
        }
        if (this.globalArgumentParser != null) {
            this.globalArgumentParser.addSubCommand(name, this);
        }
        this.names.put(lowerName, new ObjectPair<String, Boolean>(name, isHidden));
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public ArgumentParser getArgumentParser() {
        return this.subcommandArgumentParser;
    }
    
    public boolean isPresent() {
        return this.isPresent;
    }
    
    void setPresent() {
        this.isPresent = true;
    }
    
    ArgumentParser getGlobalArgumentParser() {
        return this.globalArgumentParser;
    }
    
    void setGlobalArgumentParser(final ArgumentParser globalArgumentParser) {
        this.globalArgumentParser = globalArgumentParser;
    }
    
    public LinkedHashMap<String[], String> getExampleUsages() {
        return this.exampleUsages;
    }
    
    public SubCommand getCleanCopy() {
        return new SubCommand(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("SubCommand(");
        if (this.names.size() == 1) {
            buffer.append("name='");
            buffer.append(this.names.values().iterator().next());
            buffer.append('\'');
        }
        else {
            buffer.append("names={");
            final Iterator<ObjectPair<String, Boolean>> iterator = this.names.values().iterator();
            while (iterator.hasNext()) {
                buffer.append('\'');
                buffer.append((String)iterator.next().getFirst());
                buffer.append('\'');
                if (iterator.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append('}');
        }
        buffer.append(", description='");
        buffer.append(this.description);
        buffer.append("', parser=");
        this.subcommandArgumentParser.toString(buffer);
        buffer.append(')');
    }
}
