package jdk.jfr.internal.tool;

import java.util.Collection;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.IOError;
import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.io.File;
import java.util.Iterator;
import java.io.PrintStream;
import java.util.Deque;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

abstract class Command
{
    public static final String title = "Tool for working with Flight Recorder files (.jfr)";
    private static final Command HELP;
    private static final List<Command> COMMANDS;
    
    private static List<Command> createCommands() {
        final ArrayList list = new ArrayList();
        list.add(new Print());
        list.add(new Metadata());
        list.add(new Summary());
        list.add(new Assemble());
        list.add(new Disassemble());
        list.add(new Version());
        list.add(Command.HELP);
        return (List<Command>)Collections.unmodifiableList((List<?>)list);
    }
    
    static void displayHelp() {
        System.out.println("Tool for working with Flight Recorder files (.jfr)");
        System.out.println();
        displayAvailableCommands(System.out);
    }
    
    public abstract String getName();
    
    public abstract String getDescription();
    
    public abstract void execute(final Deque<String> p0) throws UserSyntaxException, UserDataException;
    
    protected String getTitle() {
        return this.getDescription();
    }
    
    static void displayAvailableCommands(final PrintStream printStream) {
        int n = 1;
        for (final Command command : Command.COMMANDS) {
            if (n == 0) {
                System.out.println();
            }
            displayCommand(printStream, command);
            printStream.println("     " + command.getDescription());
            n = 0;
        }
    }
    
    protected static void displayCommand(final PrintStream printStream, final Command command) {
        int n = 1;
        final String buildAlias = buildAlias(command);
        final String string = " jfr " + command.getName();
        for (final String s : command.getOptionSyntax()) {
            if (n != 0) {
                if (s.length() != 0) {
                    printStream.println(string + " " + s + buildAlias);
                }
                else {
                    printStream.println(string + buildAlias);
                }
            }
            else {
                for (int i = 0; i < string.length(); ++i) {
                    printStream.print(" ");
                }
                printStream.println(" " + s);
            }
            n = 0;
        }
    }
    
    private static String buildAlias(final Command command) {
        final List<String> aliases = command.getAliases();
        if (aliases.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        if (aliases.size() == 1) {
            sb.append(" (alias ");
            sb.append(aliases.get(0));
            sb.append(")");
            return sb.toString();
        }
        sb.append(" (aliases ");
        for (int i = 0; i < aliases.size(); ++i) {
            sb.append((String)aliases.get(i));
            if (i < aliases.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
    public static List<Command> getCommands() {
        return Command.COMMANDS;
    }
    
    public static Command valueOf(final String s) {
        for (final Command command : Command.COMMANDS) {
            if (command.getName().equals(s)) {
                return command;
            }
        }
        return null;
    }
    
    public List<String> getOptionSyntax() {
        return Collections.singletonList("");
    }
    
    public void displayOptionUsage(final PrintStream printStream) {
    }
    
    protected boolean acceptOption(final Deque<String> deque, final String s) throws UserSyntaxException {
        if (!s.equals(deque.peek())) {
            return false;
        }
        if (deque.size() < 2) {
            throw new UserSyntaxException("missing value for " + deque.peek());
        }
        deque.remove();
        return true;
    }
    
    protected void warnForWildcardExpansion(final String s, final String s2) throws UserDataException {
        try {
            if (!s2.contains(File.pathSeparator) && !Files.exists(Paths.get(".", s2), new LinkOption[0])) {
                return;
            }
            throw new UserDataException("wildcards should be quoted, for example " + s + " \"Foo*\"");
        }
        catch (final InvalidPathException ex) {}
    }
    
    protected boolean acceptFilterOption(final Deque<String> deque, final String s) throws UserSyntaxException {
        if (!this.acceptOption(deque, s)) {
            return false;
        }
        if (deque.isEmpty()) {
            throw new UserSyntaxException("missing filter after " + s);
        }
        if (deque.peek().startsWith("--")) {
            throw new UserSyntaxException("missing filter after " + s);
        }
        return true;
    }
    
    protected final void ensureMaxArgumentCount(final Deque<String> deque, final int n) throws UserSyntaxException {
        if (deque.size() > n) {
            throw new UserSyntaxException("too many arguments");
        }
    }
    
    protected final void ensureMinArgumentCount(final Deque<String> deque, final int n) throws UserSyntaxException {
        if (deque.size() < n) {
            throw new UserSyntaxException("too few arguments");
        }
    }
    
    protected final Path getDirectory(final String s) throws UserDataException {
        try {
            final Path absolutePath = Paths.get(s, new String[0]).toAbsolutePath();
            if (!Files.exists(absolutePath, new LinkOption[0])) {
                throw new UserDataException("directory does not exist, " + s);
            }
            if (!Files.isDirectory(absolutePath, new LinkOption[0])) {
                throw new UserDataException("path must be directory, " + s);
            }
            return absolutePath;
        }
        catch (final InvalidPathException ex) {
            throw new UserDataException("invalid path '" + s + "'");
        }
    }
    
    protected final Path getJFRInputFile(final Deque<String> deque) throws UserSyntaxException, UserDataException {
        if (deque.isEmpty()) {
            throw new UserSyntaxException("missing file");
        }
        final String s = deque.removeLast();
        if (s.startsWith("--")) {
            throw new UserSyntaxException("missing file");
        }
        try {
            final Path absolutePath = Paths.get(s, new String[0]).toAbsolutePath();
            this.ensureAccess(absolutePath);
            this.ensureJFRFile(absolutePath);
            return absolutePath;
        }
        catch (final IOError ioError) {
            throw new UserDataException("i/o error reading file '" + s + "', " + ioError.getMessage());
        }
        catch (final InvalidPathException ex) {
            throw new UserDataException("invalid path '" + s + "'");
        }
    }
    
    private void ensureAccess(final Path path) throws UserDataException {
        try (final RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r")) {
            if (randomAccessFile.length() == 0L) {
                throw new UserDataException("file is empty '" + path + "'");
            }
            randomAccessFile.read();
        }
        catch (final FileNotFoundException ex) {
            throw new UserDataException("could not open file " + ex.getMessage());
        }
        catch (final IOException ex2) {
            throw new UserDataException("i/o error reading file '" + path + "', " + ex2.getMessage());
        }
    }
    
    protected final void couldNotReadError(final Path path, final IOException ex) throws UserDataException {
        throw new UserDataException("could not read recording at " + path.toAbsolutePath() + ". " + ex.getMessage());
    }
    
    protected final Path ensureFileDoesNotExist(final Path path) throws UserDataException {
        if (Files.exists(path, new LinkOption[0])) {
            throw new UserDataException("file '" + path + "' already exists");
        }
        return path;
    }
    
    protected final void ensureJFRFile(final Path path) throws UserDataException {
        if (!path.toString().endsWith(".jfr")) {
            throw new UserDataException("filename must end with '.jfr'");
        }
    }
    
    protected void displayUsage(final PrintStream printStream) {
        displayCommand(printStream, this);
        printStream.println();
        this.displayOptionUsage(printStream);
    }
    
    protected final void println() {
        System.out.println();
    }
    
    protected final void print(final String s) {
        System.out.print(s);
    }
    
    protected final void println(final String s) {
        System.out.println(s);
    }
    
    protected final boolean matches(final String s) {
        final Iterator<String> iterator = this.getNames().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    protected List<String> getAliases() {
        return Collections.emptyList();
    }
    
    public List<String> getNames() {
        final ArrayList list = new ArrayList();
        list.add(this.getName());
        list.addAll(this.getAliases());
        return list;
    }
    
    static {
        HELP = new Help();
        COMMANDS = createCommands();
    }
}
