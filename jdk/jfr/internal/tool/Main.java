package jdk.jfr.internal.tool;

import java.util.Iterator;
import java.util.Deque;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Arrays;

public final class Main
{
    private static final int EXIT_OK = 0;
    private static final int EXIT_FAILED = 1;
    private static final int EXIT_WRONG_ARGUMENTS = 2;
    
    public static void main(final String... array) {
        final LinkedList list = new LinkedList((Collection<? extends E>)Arrays.asList(array));
        if (list.isEmpty()) {
            System.out.println("Tool for working with Flight Recorder files (.jfr)");
            System.out.println();
            System.out.println("Before using this tool, you must have a recording file.");
            System.out.println("A file can be created by starting a recording from command line:");
            System.out.println();
            System.out.println(" java -XX:StartFlightRecording:filename=recording.jfr,duration=30s ... ");
            System.out.println();
            System.out.println("A recording can also be started on already running Java Virtual Machine:");
            System.out.println();
            System.out.println(" jcmd (to list available pids)");
            System.out.println(" jcmd <pid> JFR.start");
            System.out.println();
            System.out.println("Recording data can be dumped to file using the JFR.dump command:");
            System.out.println();
            System.out.println(" jcmd <pid> JFR.dump filename=recording.jfr");
            System.out.println();
            System.out.println("The contents of the recording can then be printed, for example:");
            System.out.println();
            System.out.println(" jfr print recording.jfr");
            System.out.println();
            System.out.println(" jfr print --events CPULoad,GarbageCollection recording.jfr");
            System.out.println();
            System.out.println(" jfr print --json --events CPULoad recording.jfr");
            System.out.println();
            System.out.println(" jfr print --categories \"GC,JVM,Java*\" recording.jfr");
            System.out.println();
            System.out.println(" jfr print --events \"jdk.*\" --stack-depth 64 recording.jfr");
            System.out.println();
            System.out.println(" jfr summary recording.jfr");
            System.out.println();
            System.out.println(" jfr metadata recording.jfr");
            System.out.println();
            System.out.println("For more information about available commands, use 'jfr help'");
            System.exit(0);
        }
        final String s = (String)list.remove();
        for (final Command command : Command.getCommands()) {
            if (command.matches(s)) {
                try {
                    command.execute(list);
                    System.exit(0);
                }
                catch (final UserDataException ex) {
                    System.err.println("jfr " + command.getName() + ": " + ex.getMessage());
                    System.exit(1);
                }
                catch (final UserSyntaxException ex2) {
                    System.err.println("jfr " + command.getName() + ": " + ex2.getMessage());
                    System.err.println();
                    System.err.println("Usage:");
                    System.err.println();
                    command.displayUsage(System.err);
                    System.exit(2);
                }
                catch (final Throwable t) {
                    System.err.println("jfr " + command.getName() + ": unexpected internal error, " + t.getMessage());
                    t.printStackTrace();
                    System.exit(1);
                }
            }
        }
        System.err.println("jfr: unknown command '" + s + "'");
        System.err.println();
        System.err.println("List of available commands:");
        System.err.println();
        Command.displayAvailableCommands(System.err);
        System.exit(2);
    }
}
