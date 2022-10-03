package org.apache.xmlbeans.impl.util;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.List;
import java.io.Reader;

public class Diff
{
    public static void readersAsText(final Reader r1, final String name1, final Reader r2, final String name2, final List diffs) throws IOException {
        LineNumberReader reader1;
        LineNumberReader reader2;
        String line1;
        String line2;
        for (reader1 = new LineNumberReader(r1), reader2 = new LineNumberReader(r2), line1 = reader1.readLine(), line2 = reader2.readLine(); line1 != null && line2 != null; line1 = reader1.readLine(), line2 = reader2.readLine()) {
            if (!line1.equals(line2)) {
                diffs.add("File \"" + name1 + "\" and file \"" + name2 + "\" differ at line " + reader1.getLineNumber() + ":" + "\n" + line1 + "\n========\n" + line2);
                break;
            }
        }
        if (line1 == null && line2 != null) {
            diffs.add("File \"" + name2 + "\" has extra lines at line " + reader2.getLineNumber() + ":\n" + line2);
        }
        if (line1 != null && line2 == null) {
            diffs.add("File \"" + name1 + "\" has extra lines at line " + reader1.getLineNumber() + ":\n" + line1);
        }
    }
}
