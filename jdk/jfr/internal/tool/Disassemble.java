package jdk.jfr.internal.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import jdk.jfr.internal.consumer.ChunkHeader;
import jdk.jfr.internal.consumer.RecordingInput;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

final class Disassemble extends Command
{
    @Override
    public String getName() {
        return "disassemble";
    }
    
    @Override
    public List<String> getOptionSyntax() {
        final ArrayList list = new ArrayList();
        list.add("[--output <directory>]");
        list.add("[--max-chunks <chunks>]");
        list.add("[--max-size <size>]");
        list.add("<file>");
        return list;
    }
    
    @Override
    public void displayOptionUsage(final PrintStream printStream) {
        printStream.println(" --output <directory>    The location to write the disassembled file,");
        printStream.println("                         by default the current directory");
        printStream.println("");
        printStream.println(" --max-chunks <chunks>   Maximum number of chunks per disassembled file,");
        printStream.println("                         by default 5. The chunk size varies, but is ");
        printStream.println("                         typically around 15 MB.");
        printStream.println("");
        printStream.println(" --max-size <size>       Maximum number of bytes per file.");
        printStream.println("");
        printStream.println("  <file>                 Location of the recording file (.jfr)");
    }
    
    @Override
    public String getDescription() {
        return "Disassamble a recording file into smaller files/chunks";
    }
    
    @Override
    public void execute(final Deque<String> deque) throws UserSyntaxException, UserDataException {
        if (deque.isEmpty()) {
            throw new UserSyntaxException("missing file");
        }
        final Path jfrInputFile = this.getJFRInputFile(deque);
        int int1 = Integer.MAX_VALUE;
        int int2 = Integer.MAX_VALUE;
        String property = System.getProperty("user.dir");
        for (int i = deque.size(); i > 0; i = deque.size()) {
            if (this.acceptOption(deque, "--output")) {
                property = deque.pop();
            }
            if (this.acceptOption(deque, "--max-size")) {
                final String s = deque.pop();
                try {
                    int2 = Integer.parseInt(s);
                    if (int2 < 1) {
                        throw new UserDataException("max size must be at least 1");
                    }
                }
                catch (final NumberFormatException ex) {
                    throw new UserDataException("not a valid value for --max-size.");
                }
            }
            if (this.acceptOption(deque, "--max-chunks")) {
                final String s2 = deque.pop();
                try {
                    int1 = Integer.parseInt(s2);
                    if (int1 < 1) {
                        throw new UserDataException("max chunks must be at least 1.");
                    }
                }
                catch (final NumberFormatException ex2) {
                    throw new UserDataException("not a valid value for --max-size.");
                }
            }
            if (i == deque.size()) {
                throw new UserSyntaxException("unknown option " + (String)deque.peek());
            }
        }
        final Path directory = this.getDirectory(property);
        this.println();
        this.println("Examining recording " + jfrInputFile + " ...");
        if (int2 != Integer.MAX_VALUE && int1 == Integer.MAX_VALUE) {
            try {
                final long size = Files.size(jfrInputFile);
                if (int2 >= size) {
                    this.println();
                    this.println("File size (" + size + ") does not exceed max size (" + int2 + ")");
                    return;
                }
            }
            catch (final IOException ex3) {
                throw new UserDataException("unexpected i/o error when determining file size" + ex3.getMessage());
            }
        }
        if (int2 == Integer.MAX_VALUE && int1 == Integer.MAX_VALUE) {
            int1 = 5;
        }
        List<Long> chunkSizes;
        try {
            chunkSizes = this.findChunkSizes(jfrInputFile);
        }
        catch (final IOException ex4) {
            throw new UserDataException("unexpected i/o error. " + ex4.getMessage());
        }
        if (int2 == Integer.MAX_VALUE == chunkSizes.size() <= int1) {
            throw new UserDataException("number of chunks in recording (" + chunkSizes.size() + ") doesn't exceed max chunks (" + int1 + ")");
        }
        this.println();
        if (chunkSizes.size() > 0) {
            final List<Long> combineChunkSizes = this.combineChunkSizes(chunkSizes, int1, int2);
            this.print("File consists of " + chunkSizes.size() + " chunks. The recording will be split into ");
            this.println(combineChunkSizes.size() + " files");
            this.println();
            this.splitFile(directory, jfrInputFile, combineChunkSizes);
            return;
        }
        throw new UserDataException("no JFR chunks found in file.");
    }
    
    private List<Long> findChunkSizes(final Path path) throws IOException {
        try (final RecordingInput recordingInput = new RecordingInput(path.toFile())) {
            final ArrayList list = new ArrayList();
            ChunkHeader nextHeader = new ChunkHeader(recordingInput);
            list.add(nextHeader.getSize());
            while (!nextHeader.isLastChunk()) {
                nextHeader = nextHeader.nextHeader();
                list.add(nextHeader.getSize());
            }
            return list;
        }
    }
    
    private List<Long> combineChunkSizes(final List<Long> list, final int n, final long n2) {
        final ArrayList list2 = new ArrayList();
        int n3 = 1;
        long longValue = list.get(0);
        for (int i = 1; i < list.size(); ++i) {
            final long longValue2 = list.get(i);
            if (longValue + longValue2 > n2) {
                list2.add(longValue);
                n3 = 1;
                longValue = longValue2;
            }
            else {
                longValue += longValue2;
                if (n3 == n) {
                    list2.add(longValue);
                    longValue = 0L;
                    n3 = 1;
                }
                else {
                    ++n3;
                }
            }
        }
        if (longValue != 0L) {
            list2.add(longValue);
        }
        return list2;
    }
    
    private void splitFile(final Path path, final Path path2, final List<Long> list) throws UserDataException {
        final int length = String.valueOf(list.size() - 1).length();
        final String string = path2.getFileName().toString();
        final String string2 = (Object)string.subSequence(0, string.length() - 4) + "_%0" + length + "d.jfr";
        for (int i = 0; i < list.size(); ++i) {
            final String format = String.format(string2, i);
            try {
                final Path resolve = path.resolve(format);
                if (Files.exists(resolve, new LinkOption[0])) {
                    throw new UserDataException("can't create disassembled file " + resolve + ", a file with that name already exist");
                }
            }
            catch (final InvalidPathException ex) {
                throw new UserDataException("can't construct path with filename" + format);
            }
        }
        try (final DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(path2.toFile())))) {
            for (int j = 0; j < list.size(); ++j) {
                final byte[] bytes = this.readBytes(dataInputStream, list.get(j).intValue());
                final File file = path.resolve(String.format(string2, j)).toFile();
                this.println("Writing " + file + " ... " + bytes.length);
                final FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            }
        }
        catch (final IOException ex2) {
            throw new UserDataException("i/o error writing file " + path2);
        }
    }
    
    private byte[] readBytes(final InputStream inputStream, final int n) throws UserDataException, IOException {
        final byte[] array = new byte[n];
        int read;
        for (int i = 0; i < array.length; i += read) {
            read = inputStream.read(array, i, array.length - i);
            if (read == -1) {
                throw new UserDataException("unexpected end of data");
            }
        }
        return array;
    }
}
