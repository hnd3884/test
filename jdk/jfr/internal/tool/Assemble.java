package jdk.jfr.internal.tool;

import java.nio.channels.ReadableByteChannel;
import java.nio.file.OpenOption;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.nio.file.Path;
import java.io.IOException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Deque;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

final class Assemble extends Command
{
    @Override
    public String getName() {
        return "assemble";
    }
    
    @Override
    public List<String> getOptionSyntax() {
        return Collections.singletonList("<repository> <file>");
    }
    
    @Override
    public String getDescription() {
        return "Assemble leftover chunks from a disk repository into a recording file";
    }
    
    @Override
    public void displayOptionUsage(final PrintStream printStream) {
        printStream.println("  <repository>   Directory where the repository is located");
        printStream.println();
        printStream.println("  <file>         Name of the recording file (.jfr) to create");
    }
    
    @Override
    public void execute(final Deque<String> deque) throws UserSyntaxException, UserDataException {
        this.ensureMinArgumentCount(deque, 2);
        this.ensureMaxArgumentCount(deque, 2);
        final Path directory = this.getDirectory(deque.pop());
        final Path value = Paths.get(deque.pop(), new String[0]);
        this.ensureFileDoesNotExist(value);
        this.ensureJFRFile(value);
        try (final FileOutputStream fileOutputStream = new FileOutputStream(value.toFile())) {
            final List<Path> listJFRFiles = this.listJFRFiles(directory);
            if (listJFRFiles.isEmpty()) {
                throw new UserDataException("no *.jfr files found at " + directory);
            }
            this.println();
            this.println("Assembling files... ");
            this.println();
            this.transferTo(listJFRFiles, value, fileOutputStream.getChannel());
            this.println();
            this.println("Finished.");
        }
        catch (final IOException ex) {
            throw new UserDataException("could not open destination file " + value + ". " + ex.getMessage());
        }
    }
    
    private List<Path> listJFRFiles(final Path path) throws UserDataException {
        try {
            final ArrayList list = new ArrayList();
            if (Files.isDirectory(path, new LinkOption[0])) {
                try (final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, "*.jfr")) {
                    for (final Path path2 : directoryStream) {
                        if (!Files.isDirectory(path2, new LinkOption[0]) && Files.isReadable(path2)) {
                            list.add(path2);
                        }
                    }
                }
            }
            list.sort((path3, path4) -> path3.getFileName().compareTo(path4.getFileName()));
            return list;
        }
        catch (final IOException ex) {
            throw new UserDataException("could not list *.jfr for directory " + path + ". " + ex.getMessage());
        }
    }
    
    private void transferTo(final List<Path> list, final Path path, final FileChannel fileChannel) throws UserDataException {
        long n = 0L;
        for (final Path path2 : list) {
            this.println(" " + path2.toString());
            try (final FileChannel open = FileChannel.open(path2, new OpenOption[0])) {
                long transfer;
                for (long size = Files.size(path2); size > 0L; size -= transfer) {
                    transfer = fileChannel.transferFrom(open, n, Math.min(size, 1048576L));
                    n += transfer;
                }
            }
            catch (final IOException ex) {
                throw new UserDataException("could not copy recording chunk " + path2 + " to new file. " + ex.getMessage());
            }
        }
    }
}
