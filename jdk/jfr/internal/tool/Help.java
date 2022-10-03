package jdk.jfr.internal.tool;

import java.util.Deque;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class Help extends Command
{
    @Override
    public String getName() {
        return "help";
    }
    
    @Override
    public List<String> getOptionSyntax() {
        return Collections.singletonList("[<command>]");
    }
    
    @Override
    protected List<String> getAliases() {
        return Arrays.asList("--help", "-h", "-?");
    }
    
    @Override
    public void displayOptionUsage(final PrintStream printStream) {
        this.println("  <command>   The name of the command to get help for");
    }
    
    @Override
    public String getDescription() {
        return "Display all available commands, or help about a specific command";
    }
    
    @Override
    public void execute(final Deque<String> deque) throws UserSyntaxException, UserDataException {
        if (deque.isEmpty()) {
            Command.displayHelp();
            return;
        }
        this.ensureMaxArgumentCount(deque, 1);
        final String s = deque.remove();
        final Command value = Command.valueOf(s);
        if (value == null) {
            throw new UserDataException("unknown command '" + s + "'");
        }
        this.println(value.getTitle());
        this.println();
        value.displayUsage(System.out);
    }
}
