package org.apache.lucene.facet;

import java.util.Arrays;
import org.apache.lucene.util.IntsRef;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.document.SortedSetDocValuesField;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field;
import org.apache.lucene.facet.taxonomy.FacetLabel;
import org.apache.lucene.util.IntsRefBuilder;
import org.apache.lucene.index.IndexableFieldType;
import java.util.Iterator;
import org.apache.lucene.facet.taxonomy.FloatAssociationFacetField;
import org.apache.lucene.facet.taxonomy.IntAssociationFacetField;
import java.util.ArrayList;
import org.apache.lucene.index.IndexableField;
import java.util.HashSet;
import org.apache.lucene.facet.taxonomy.AssociationFacetField;
import org.apache.lucene.facet.sortedset.SortedSetDocValuesFacetField;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import org.apache.lucene.facet.taxonomy.TaxonomyWriter;
import org.apache.lucene.document.Document;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class FacetsConfig
{
    public static final String DEFAULT_INDEX_FIELD_NAME = "$facets";
    private final Map<String, DimConfig> fieldTypes;
    private final Map<String, String> assocDimTypes;
    public static final DimConfig DEFAULT_DIM_CONFIG;
    private static final char DELIM_CHAR = '\u001f';
    private static final char ESCAPE_CHAR = '\u001e';
    
    public FacetsConfig() {
        this.fieldTypes = new ConcurrentHashMap<String, DimConfig>();
        this.assocDimTypes = new ConcurrentHashMap<String, String>();
    }
    
    protected DimConfig getDefaultDimConfig() {
        return FacetsConfig.DEFAULT_DIM_CONFIG;
    }
    
    public DimConfig getDimConfig(final String dimName) {
        DimConfig ft = this.fieldTypes.get(dimName);
        if (ft == null) {
            ft = this.getDefaultDimConfig();
        }
        return ft;
    }
    
    public synchronized void setHierarchical(final String dimName, final boolean v) {
        DimConfig ft = this.fieldTypes.get(dimName);
        if (ft == null) {
            ft = new DimConfig();
            this.fieldTypes.put(dimName, ft);
        }
        ft.hierarchical = v;
    }
    
    public synchronized void setMultiValued(final String dimName, final boolean v) {
        DimConfig ft = this.fieldTypes.get(dimName);
        if (ft == null) {
            ft = new DimConfig();
            this.fieldTypes.put(dimName, ft);
        }
        ft.multiValued = v;
    }
    
    public synchronized void setRequireDimCount(final String dimName, final boolean v) {
        DimConfig ft = this.fieldTypes.get(dimName);
        if (ft == null) {
            ft = new DimConfig();
            this.fieldTypes.put(dimName, ft);
        }
        ft.requireDimCount = v;
    }
    
    public synchronized void setIndexFieldName(final String dimName, final String indexFieldName) {
        DimConfig ft = this.fieldTypes.get(dimName);
        if (ft == null) {
            ft = new DimConfig();
            this.fieldTypes.put(dimName, ft);
        }
        ft.indexFieldName = indexFieldName;
    }
    
    public Map<String, DimConfig> getDimConfigs() {
        return this.fieldTypes;
    }
    
    private static void checkSeen(final Set<String> seenDims, final String dim) {
        if (seenDims.contains(dim)) {
            throw new IllegalArgumentException("dimension \"" + dim + "\" is not multiValued, but it appears more than once in this document");
        }
        seenDims.add(dim);
    }
    
    public Document build(final Document doc) throws IOException {
        return this.build(null, doc);
    }
    
    public Document build(final TaxonomyWriter taxoWriter, final Document doc) throws IOException {
        final Map<String, List<FacetField>> byField = new HashMap<String, List<FacetField>>();
        final Map<String, List<SortedSetDocValuesFacetField>> dvByField = new HashMap<String, List<SortedSetDocValuesFacetField>>();
        final Map<String, List<AssociationFacetField>> assocByField = new HashMap<String, List<AssociationFacetField>>();
        final Set<String> seenDims = new HashSet<String>();
        for (final IndexableField field : doc.getFields()) {
            if (field.fieldType() == FacetField.TYPE) {
                final FacetField facetField = (FacetField)field;
                final DimConfig dimConfig = this.getDimConfig(facetField.dim);
                if (!dimConfig.multiValued) {
                    checkSeen(seenDims, facetField.dim);
                }
                final String indexFieldName = dimConfig.indexFieldName;
                List<FacetField> fields = byField.get(indexFieldName);
                if (fields == null) {
                    fields = new ArrayList<FacetField>();
                    byField.put(indexFieldName, fields);
                }
                fields.add(facetField);
            }
            if (field.fieldType() == SortedSetDocValuesFacetField.TYPE) {
                final SortedSetDocValuesFacetField facetField2 = (SortedSetDocValuesFacetField)field;
                final DimConfig dimConfig = this.getDimConfig(facetField2.dim);
                if (!dimConfig.multiValued) {
                    checkSeen(seenDims, facetField2.dim);
                }
                final String indexFieldName = dimConfig.indexFieldName;
                List<SortedSetDocValuesFacetField> fields2 = dvByField.get(indexFieldName);
                if (fields2 == null) {
                    fields2 = new ArrayList<SortedSetDocValuesFacetField>();
                    dvByField.put(indexFieldName, fields2);
                }
                fields2.add(facetField2);
            }
            if (field.fieldType() == AssociationFacetField.TYPE) {
                final AssociationFacetField facetField3 = (AssociationFacetField)field;
                final DimConfig dimConfig = this.getDimConfig(facetField3.dim);
                if (!dimConfig.multiValued) {
                    checkSeen(seenDims, facetField3.dim);
                }
                if (dimConfig.hierarchical) {
                    throw new IllegalArgumentException("AssociationFacetField cannot be hierarchical (dim=\"" + facetField3.dim + "\")");
                }
                if (dimConfig.requireDimCount) {
                    throw new IllegalArgumentException("AssociationFacetField cannot requireDimCount (dim=\"" + facetField3.dim + "\")");
                }
                final String indexFieldName = dimConfig.indexFieldName;
                List<AssociationFacetField> fields3 = assocByField.get(indexFieldName);
                if (fields3 == null) {
                    fields3 = new ArrayList<AssociationFacetField>();
                    assocByField.put(indexFieldName, fields3);
                }
                fields3.add(facetField3);
                String type;
                if (facetField3 instanceof IntAssociationFacetField) {
                    type = "int";
                }
                else if (facetField3 instanceof FloatAssociationFacetField) {
                    type = "float";
                }
                else {
                    type = "bytes";
                }
                final String curType = this.assocDimTypes.get(indexFieldName);
                if (curType == null) {
                    this.assocDimTypes.put(indexFieldName, type);
                }
                else {
                    if (!curType.equals(type)) {
                        throw new IllegalArgumentException("mixing incompatible types of AssocationFacetField (" + curType + " and " + type + ") in indexed field \"" + indexFieldName + "\"; use FacetsConfig to change the indexFieldName for each dimension");
                    }
                    continue;
                }
            }
        }
        final Document result = new Document();
        this.processFacetFields(taxoWriter, byField, result);
        this.processSSDVFacetFields(dvByField, result);
        this.processAssocFacetFields(taxoWriter, assocByField, result);
        for (final IndexableField field2 : doc.getFields()) {
            final IndexableFieldType ft = field2.fieldType();
            if (ft != FacetField.TYPE && ft != SortedSetDocValuesFacetField.TYPE && ft != AssociationFacetField.TYPE) {
                result.add(field2);
            }
        }
        return result;
    }
    
    private void processFacetFields(final TaxonomyWriter taxoWriter, final Map<String, List<FacetField>> byField, final Document doc) throws IOException {
        for (final Map.Entry<String, List<FacetField>> ent : byField.entrySet()) {
            final String indexFieldName = ent.getKey();
            final IntsRefBuilder ordinals = new IntsRefBuilder();
            for (final FacetField facetField : ent.getValue()) {
                final DimConfig ft = this.getDimConfig(facetField.dim);
                if (facetField.path.length > 1 && !ft.hierarchical) {
                    throw new IllegalArgumentException("dimension \"" + facetField.dim + "\" is not hierarchical yet has " + facetField.path.length + " components");
                }
                final FacetLabel cp = new FacetLabel(facetField.dim, facetField.path);
                this.checkTaxoWriter(taxoWriter);
                final int ordinal = taxoWriter.addCategory(cp);
                ordinals.append(ordinal);
                if (ft.multiValued && (ft.hierarchical || ft.requireDimCount)) {
                    for (int parent = taxoWriter.getParent(ordinal); parent > 0; parent = taxoWriter.getParent(parent)) {
                        ordinals.append(parent);
                    }
                    if (!ft.requireDimCount) {
                        ordinals.setLength(ordinals.length() - 1);
                    }
                }
                for (int i = 1; i <= cp.length; ++i) {
                    doc.add((IndexableField)new StringField(indexFieldName, pathToString(cp.components, i), Field.Store.NO));
                }
            }
            doc.add((IndexableField)new BinaryDocValuesField(indexFieldName, this.dedupAndEncode(ordinals.get())));
        }
    }
    
    private void processSSDVFacetFields(final Map<String, List<SortedSetDocValuesFacetField>> byField, final Document doc) throws IOException {
        for (final Map.Entry<String, List<SortedSetDocValuesFacetField>> ent : byField.entrySet()) {
            final String indexFieldName = ent.getKey();
            for (final SortedSetDocValuesFacetField facetField : ent.getValue()) {
                final FacetLabel cp = new FacetLabel(new String[] { facetField.dim, facetField.label });
                final String fullPath = pathToString(cp.components, cp.length);
                doc.add((IndexableField)new SortedSetDocValuesField(indexFieldName, new BytesRef((CharSequence)fullPath)));
                doc.add((IndexableField)new StringField(indexFieldName, fullPath, Field.Store.NO));
                doc.add((IndexableField)new StringField(indexFieldName, facetField.dim, Field.Store.NO));
            }
        }
    }
    
    private void processAssocFacetFields(final TaxonomyWriter taxoWriter, final Map<String, List<AssociationFacetField>> byField, final Document doc) throws IOException {
        for (final Map.Entry<String, List<AssociationFacetField>> ent : byField.entrySet()) {
            byte[] bytes = new byte[16];
            int upto = 0;
            final String indexFieldName = ent.getKey();
            for (final AssociationFacetField field : ent.getValue()) {
                this.checkTaxoWriter(taxoWriter);
                final FacetLabel label = new FacetLabel(field.dim, field.path);
                final int ordinal = taxoWriter.addCategory(label);
                if (upto + 4 > bytes.length) {
                    bytes = ArrayUtil.grow(bytes, upto + 4);
                }
                bytes[upto++] = (byte)(ordinal >> 24);
                bytes[upto++] = (byte)(ordinal >> 16);
                bytes[upto++] = (byte)(ordinal >> 8);
                bytes[upto++] = (byte)ordinal;
                if (upto + field.assoc.length > bytes.length) {
                    bytes = ArrayUtil.grow(bytes, upto + field.assoc.length);
                }
                System.arraycopy(field.assoc.bytes, field.assoc.offset, bytes, upto, field.assoc.length);
                upto += field.assoc.length;
                for (int i = 1; i <= label.length; ++i) {
                    doc.add((IndexableField)new StringField(indexFieldName, pathToString(label.components, i), Field.Store.NO));
                }
            }
            doc.add((IndexableField)new BinaryDocValuesField(indexFieldName, new BytesRef(bytes, 0, upto)));
        }
    }
    
    protected BytesRef dedupAndEncode(final IntsRef ordinals) {
        Arrays.sort(ordinals.ints, ordinals.offset, ordinals.length);
        final byte[] bytes = new byte[5 * ordinals.length];
        int lastOrd = -1;
        int upto = 0;
        for (int i = 0; i < ordinals.length; ++i) {
            final int ord = ordinals.ints[ordinals.offset + i];
            if (ord > lastOrd) {
                int delta;
                if (lastOrd == -1) {
                    delta = ord;
                }
                else {
                    delta = ord - lastOrd;
                }
                if ((delta & 0xFFFFFF80) == 0x0) {
                    bytes[upto] = (byte)delta;
                    ++upto;
                }
                else if ((delta & 0xFFFFC000) == 0x0) {
                    bytes[upto] = (byte)(0x80 | (delta & 0x3F80) >> 7);
                    bytes[upto + 1] = (byte)(delta & 0x7F);
                    upto += 2;
                }
                else if ((delta & 0xFFE00000) == 0x0) {
                    bytes[upto] = (byte)(0x80 | (delta & 0x1FC000) >> 14);
                    bytes[upto + 1] = (byte)(0x80 | (delta & 0x3F80) >> 7);
                    bytes[upto + 2] = (byte)(delta & 0x7F);
                    upto += 3;
                }
                else if ((delta & 0xF0000000) == 0x0) {
                    bytes[upto] = (byte)(0x80 | (delta & 0xFE00000) >> 21);
                    bytes[upto + 1] = (byte)(0x80 | (delta & 0x1FC000) >> 14);
                    bytes[upto + 2] = (byte)(0x80 | (delta & 0x3F80) >> 7);
                    bytes[upto + 3] = (byte)(delta & 0x7F);
                    upto += 4;
                }
                else {
                    bytes[upto] = (byte)(0x80 | (delta & 0xF0000000) >> 28);
                    bytes[upto + 1] = (byte)(0x80 | (delta & 0xFE00000) >> 21);
                    bytes[upto + 2] = (byte)(0x80 | (delta & 0x1FC000) >> 14);
                    bytes[upto + 3] = (byte)(0x80 | (delta & 0x3F80) >> 7);
                    bytes[upto + 4] = (byte)(delta & 0x7F);
                    upto += 5;
                }
                lastOrd = ord;
            }
        }
        return new BytesRef(bytes, 0, upto);
    }
    
    private void checkTaxoWriter(final TaxonomyWriter taxoWriter) {
        if (taxoWriter == null) {
            throw new IllegalStateException("a non-null TaxonomyWriter must be provided when indexing FacetField or AssociationFacetField");
        }
    }
    
    public static String pathToString(final String dim, final String[] path) {
        final String[] fullPath = new String[1 + path.length];
        fullPath[0] = dim;
        System.arraycopy(path, 0, fullPath, 1, path.length);
        return pathToString(fullPath, fullPath.length);
    }
    
    public static String pathToString(final String[] path) {
        return pathToString(path, path.length);
    }
    
    public static String pathToString(final String[] path, final int length) {
        if (length == 0) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        for (final String s : path) {
            if (s.length() == 0) {
                throw new IllegalArgumentException("each path component must have length > 0 (got: \"\")");
            }
            for (int numChars = s.length(), j = 0; j < numChars; ++j) {
                final char ch = s.charAt(j);
                if (ch == '\u001f' || ch == '\u001e') {
                    sb.append('\u001e');
                }
                sb.append(ch);
            }
            sb.append('\u001f');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    public static String[] stringToPath(final String s) {
        final List<String> parts = new ArrayList<String>();
        final int length = s.length();
        if (length == 0) {
            return new String[0];
        }
        final char[] buffer = new char[length];
        int upto = 0;
        boolean lastEscape = false;
        for (int i = 0; i < length; ++i) {
            final char ch = s.charAt(i);
            if (lastEscape) {
                buffer[upto++] = ch;
                lastEscape = false;
            }
            else if (ch == '\u001e') {
                lastEscape = true;
            }
            else if (ch == '\u001f') {
                parts.add(new String(buffer, 0, upto));
                upto = 0;
            }
            else {
                buffer[upto++] = ch;
            }
        }
        parts.add(new String(buffer, 0, upto));
        assert !lastEscape;
        return parts.toArray(new String[parts.size()]);
    }
    
    static {
        DEFAULT_DIM_CONFIG = new DimConfig();
    }
    
    public static final class DimConfig
    {
        public boolean hierarchical;
        public boolean multiValued;
        public boolean requireDimCount;
        public String indexFieldName;
        
        public DimConfig() {
            this.indexFieldName = "$facets";
        }
    }
}
