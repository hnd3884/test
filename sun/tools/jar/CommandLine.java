package sun.tools.jar;

import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class CommandLine
{
    public static String[] parse(final String[] array) throws IOException {
        final ArrayList list = new ArrayList(array.length);
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            if (s.length() > 1 && s.charAt(0) == '@') {
                final String substring = s.substring(1);
                if (substring.charAt(0) == '@') {
                    list.add(substring);
                }
                else {
                    loadCmdFile(substring, list);
                }
            }
            else {
                list.add(s);
            }
        }
        return (String[])list.toArray(new String[list.size()]);
    }
    
    private static void loadCmdFile(final String s, final List<String> list) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(s));
        final StreamTokenizer streamTokenizer = new StreamTokenizer(bufferedReader);
        streamTokenizer.resetSyntax();
        streamTokenizer.wordChars(32, 255);
        streamTokenizer.whitespaceChars(0, 32);
        streamTokenizer.commentChar(35);
        streamTokenizer.quoteChar(34);
        streamTokenizer.quoteChar(39);
        while (streamTokenizer.nextToken() != -1) {
            list.add(streamTokenizer.sval);
        }
        bufferedReader.close();
    }
}
