package org.apache.poi.openxml4j.opc;

import java.util.regex.Matcher;
import java.util.function.ToIntFunction;
import com.zaxxer.sparsebits.SparseBitSet;
import java.util.regex.Pattern;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Collections;
import java.util.Collection;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Set;
import java.io.Serializable;

public final class PackagePartCollection implements Serializable
{
    private static final long serialVersionUID = 2515031135957635517L;
    private final Set<String> registerPartNameStr;
    private final TreeMap<String, PackagePart> packagePartLookup;
    
    public PackagePartCollection() {
        this.registerPartNameStr = new HashSet<String>();
        this.packagePartLookup = new TreeMap<String, PackagePart>(PackagePartName::compare);
    }
    
    public PackagePart put(final PackagePartName partName, final PackagePart part) {
        final String ppName = partName.getName();
        final StringBuilder concatSeg = new StringBuilder();
        final String delim = "(?=[/.])";
        for (final String seg : ppName.split("(?=[/.])")) {
            concatSeg.append(seg);
            if (this.registerPartNameStr.contains(concatSeg.toString())) {
                throw new InvalidOperationException("You can't add a part with a part name derived from another part ! [M1.11]");
            }
        }
        this.registerPartNameStr.add(ppName);
        return this.packagePartLookup.put(ppName, part);
    }
    
    public PackagePart remove(final PackagePartName key) {
        if (key == null) {
            return null;
        }
        final String ppName = key.getName();
        final PackagePart pp = this.packagePartLookup.remove(ppName);
        if (pp != null) {
            this.registerPartNameStr.remove(ppName);
        }
        return pp;
    }
    
    public Collection<PackagePart> sortedValues() {
        return Collections.unmodifiableCollection((Collection<? extends PackagePart>)this.packagePartLookup.values());
    }
    
    public boolean containsKey(final PackagePartName partName) {
        return partName != null && this.packagePartLookup.containsKey(partName.getName());
    }
    
    public PackagePart get(final PackagePartName partName) {
        return (partName == null) ? null : this.packagePartLookup.get(partName.getName());
    }
    
    public int size() {
        return this.packagePartLookup.size();
    }
    
    public int getUnusedPartIndex(final String nameTemplate) throws InvalidFormatException {
        if (nameTemplate == null || !nameTemplate.contains("#")) {
            throw new InvalidFormatException("name template must not be null and contain an index char (#)");
        }
        final Pattern pattern = Pattern.compile(nameTemplate.replace("#", "([0-9]+)"));
        final ToIntFunction<String> indexFromName = name -> {
            final Matcher m = pattern.matcher(name);
            return m.matches() ? Integer.parseInt(m.group(1)) : 0;
        };
        return this.packagePartLookup.keySet().stream().mapToInt((ToIntFunction<? super Object>)indexFromName).collect(SparseBitSet::new, SparseBitSet::set, (s1, s2) -> s1.or(s2)).nextClearBit(1);
    }
}
