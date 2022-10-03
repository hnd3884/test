package org.apache.poi.ooxml.util;

import org.apache.poi.openxml4j.opc.PackageProperties;
import org.apache.poi.openxml4j.opc.PackagePartName;
import java.net.URI;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.util.IOUtils;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import java.io.File;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.io.InputStream;

public final class PackageHelper
{
    public static OPCPackage open(final InputStream is) throws IOException {
        try {
            return OPCPackage.open(is);
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException(e);
        }
    }
    
    public static OPCPackage clone(final OPCPackage pkg, final File file) throws OpenXML4JException, IOException {
        final String path = file.getAbsolutePath();
        try (final OPCPackage dest = OPCPackage.create(path)) {
            final PackageRelationshipCollection rels = pkg.getRelationships();
            for (final PackageRelationship rel : rels) {
                final PackagePart part = pkg.getPart(rel);
                if (rel.getRelationshipType().equals("http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties")) {
                    copyProperties(pkg.getPackageProperties(), dest.getPackageProperties());
                }
                else {
                    dest.addRelationship(part.getPartName(), rel.getTargetMode(), rel.getRelationshipType());
                    final PackagePart part_tgt = dest.createPart(part.getPartName(), part.getContentType());
                    final OutputStream out = part_tgt.getOutputStream();
                    IOUtils.copy(part.getInputStream(), out);
                    out.close();
                    if (!part.hasRelationships()) {
                        continue;
                    }
                    copy(pkg, part, dest, part_tgt);
                }
            }
        }
        new File(path).deleteOnExit();
        return OPCPackage.open(path);
    }
    
    private static void copy(final OPCPackage pkg, final PackagePart part, final OPCPackage tgt, final PackagePart part_tgt) throws OpenXML4JException, IOException {
        final PackageRelationshipCollection rels = part.getRelationships();
        if (rels != null) {
            for (final PackageRelationship rel : rels) {
                if (rel.getTargetMode() == TargetMode.EXTERNAL) {
                    part_tgt.addExternalRelationship(rel.getTargetURI().toString(), rel.getRelationshipType(), rel.getId());
                }
                else {
                    final URI uri = rel.getTargetURI();
                    if (uri.getRawFragment() != null) {
                        part_tgt.addRelationship(uri, rel.getTargetMode(), rel.getRelationshipType(), rel.getId());
                    }
                    else {
                        final PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
                        final PackagePart p = pkg.getPart(relName);
                        part_tgt.addRelationship(p.getPartName(), rel.getTargetMode(), rel.getRelationshipType(), rel.getId());
                        if (tgt.containPart(p.getPartName())) {
                            continue;
                        }
                        final PackagePart dest = tgt.createPart(p.getPartName(), p.getContentType());
                        final OutputStream out = dest.getOutputStream();
                        IOUtils.copy(p.getInputStream(), out);
                        out.close();
                        copy(pkg, p, tgt, dest);
                    }
                }
            }
        }
    }
    
    private static void copyProperties(final PackageProperties src, final PackageProperties tgt) {
        tgt.setCategoryProperty(src.getCategoryProperty());
        tgt.setContentStatusProperty(src.getContentStatusProperty());
        tgt.setContentTypeProperty(src.getContentTypeProperty());
        tgt.setCreatorProperty(src.getCreatorProperty());
        tgt.setDescriptionProperty(src.getDescriptionProperty());
        tgt.setIdentifierProperty(src.getIdentifierProperty());
        tgt.setKeywordsProperty(src.getKeywordsProperty());
        tgt.setLanguageProperty(src.getLanguageProperty());
        tgt.setRevisionProperty(src.getRevisionProperty());
        tgt.setSubjectProperty(src.getSubjectProperty());
        tgt.setTitleProperty(src.getTitleProperty());
        tgt.setVersionProperty(src.getVersionProperty());
    }
}
