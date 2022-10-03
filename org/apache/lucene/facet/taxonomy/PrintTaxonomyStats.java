package org.apache.lucene.facet.taxonomy;

import java.io.PrintStream;
import org.apache.lucene.util.SuppressForbidden;
import java.io.IOException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyReader;
import org.apache.lucene.store.FSDirectory;
import java.nio.file.Paths;

public class PrintTaxonomyStats
{
    @SuppressForbidden(reason = "System.out required: command line tool")
    public static void main(final String[] args) throws IOException {
        boolean printTree = false;
        String path = null;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-printTree")) {
                printTree = true;
            }
            else {
                path = args[i];
            }
        }
        if (args.length != (printTree ? 2 : 1)) {
            System.out.println("\nUsage: java -classpath ... org.apache.lucene.facet.util.PrintTaxonomyStats [-printTree] /path/to/taxononmy/index\n");
            System.exit(1);
        }
        final Directory dir = (Directory)FSDirectory.open(Paths.get(path, new String[0]));
        final TaxonomyReader r = new DirectoryTaxonomyReader(dir);
        printStats(r, System.out, printTree);
        r.close();
        dir.close();
    }
    
    public static void printStats(final TaxonomyReader r, final PrintStream out, final boolean printTree) throws IOException {
        out.println(r.getSize() + " total categories.");
        final TaxonomyReader.ChildrenIterator it = r.getChildren(0);
        int child;
        while ((child = it.next()) != -1) {
            final TaxonomyReader.ChildrenIterator chilrenIt = r.getChildren(child);
            int numImmediateChildren = 0;
            while (chilrenIt.next() != -1) {
                ++numImmediateChildren;
            }
            final FacetLabel cp = r.getPath(child);
            out.println("/" + cp.components[0] + ": " + numImmediateChildren + " immediate children; " + (1 + countAllChildren(r, child)) + " total categories");
            if (printTree) {
                printAllChildren(out, r, child, "  ", 1);
            }
        }
    }
    
    private static int countAllChildren(final TaxonomyReader r, final int ord) throws IOException {
        int count = 0;
        final TaxonomyReader.ChildrenIterator it = r.getChildren(ord);
        int child;
        while ((child = it.next()) != -1) {
            count += 1 + countAllChildren(r, child);
        }
        return count;
    }
    
    private static void printAllChildren(final PrintStream out, final TaxonomyReader r, final int ord, final String indent, final int depth) throws IOException {
        final TaxonomyReader.ChildrenIterator it = r.getChildren(ord);
        int child;
        while ((child = it.next()) != -1) {
            out.println(indent + "/" + r.getPath(child).components[depth]);
            printAllChildren(out, r, child, indent + "  ", depth + 1);
        }
    }
}
