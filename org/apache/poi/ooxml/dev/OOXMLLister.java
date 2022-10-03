package org.apache.poi.ooxml.dev;

import org.apache.poi.openxml4j.opc.PackageAccess;
import java.io.File;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.io.PrintStream;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.io.Closeable;

public class OOXMLLister implements Closeable
{
    private final OPCPackage container;
    private final PrintStream disp;
    
    public OOXMLLister(final OPCPackage container) {
        this(container, System.out);
    }
    
    public OOXMLLister(final OPCPackage container, final PrintStream disp) {
        this.container = container;
        this.disp = disp;
    }
    
    public static long getSize(final PackagePart part) throws IOException {
        try (final InputStream in = part.getInputStream()) {
            final byte[] b = new byte[8192];
            long size = 0L;
            int read = 0;
            while (read > -1) {
                read = in.read(b);
                if (read > 0) {
                    size += read;
                }
            }
            return size;
        }
    }
    
    public void displayParts() throws InvalidFormatException, IOException {
        final ArrayList<PackagePart> parts = this.container.getParts();
        for (final PackagePart part : parts) {
            this.disp.println(part.getPartName());
            this.disp.println("\t" + part.getContentType());
            if (!part.getPartName().toString().equals("/docProps/core.xml")) {
                this.disp.println("\t" + getSize(part) + " bytes");
            }
            if (!part.isRelationshipPart()) {
                this.disp.println("\t" + part.getRelationships().size() + " relations");
                for (final PackageRelationship rel : part.getRelationships()) {
                    this.displayRelation(rel, "\t  ");
                }
            }
        }
    }
    
    public void displayRelations() {
        final PackageRelationshipCollection rels = this.container.getRelationships();
        for (final PackageRelationship rel : rels) {
            this.displayRelation(rel, "");
        }
    }
    
    private void displayRelation(final PackageRelationship rel, final String indent) {
        this.disp.println(indent + "Relationship:");
        this.disp.println(indent + "\tFrom: " + rel.getSourceURI());
        this.disp.println(indent + "\tTo:   " + rel.getTargetURI());
        this.disp.println(indent + "\tID:   " + rel.getId());
        this.disp.println(indent + "\tMode: " + rel.getTargetMode());
        this.disp.println(indent + "\tType: " + rel.getRelationshipType());
    }
    
    @Override
    public void close() throws IOException {
        this.container.close();
    }
    
    public static void main(final String[] args) throws IOException, InvalidFormatException {
        if (args.length == 0) {
            System.err.println("Use:");
            System.err.println("\tjava OOXMLLister <filename>");
            System.exit(1);
        }
        final File f = new File(args[0]);
        if (!f.exists()) {
            System.err.println("Error, file not found!");
            System.err.println("\t" + f);
            System.exit(2);
        }
        try (final OOXMLLister lister = new OOXMLLister(OPCPackage.open(f.toString(), PackageAccess.READ))) {
            lister.disp.println(f + "\n");
            lister.displayParts();
            lister.disp.println();
            lister.displayRelations();
        }
    }
}
