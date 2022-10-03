package jdk.jfr.internal.tool;

import java.util.Arrays;
import java.util.List;
import java.util.Deque;

final class Version extends Command
{
    @Override
    public String getName() {
        return "version";
    }
    
    @Override
    public String getDescription() {
        return "Display version of the jfr tool";
    }
    
    @Override
    public void execute(final Deque<String> deque) {
        System.out.println("1.0");
    }
    
    @Override
    protected List<String> getAliases() {
        return Arrays.asList("--version");
    }
}
