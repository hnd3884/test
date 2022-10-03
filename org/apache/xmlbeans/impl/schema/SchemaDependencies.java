package org.apache.xmlbeans.impl.schema;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class SchemaDependencies
{
    private Map _dependencies;
    private Map _contributions;
    
    void registerDependency(final String source, final String target) {
        Set depSet = this._dependencies.get(target);
        if (depSet == null) {
            depSet = new HashSet();
            this._dependencies.put(target, depSet);
        }
        depSet.add(source);
    }
    
    Set computeTransitiveClosure(final List modifiedNamespaces) {
        final List nsList = new ArrayList(modifiedNamespaces);
        final Set result = new HashSet(modifiedNamespaces);
        for (int i = 0; i < nsList.size(); ++i) {
            final Set deps = this._dependencies.get(nsList.get(i));
            if (deps != null) {
                for (final String ns : deps) {
                    if (!result.contains(ns)) {
                        nsList.add(ns);
                        result.add(ns);
                    }
                }
            }
        }
        return result;
    }
    
    SchemaDependencies() {
        this._dependencies = new HashMap();
        this._contributions = new HashMap();
    }
    
    SchemaDependencies(final SchemaDependencies base, final Set updatedNs) {
        this._dependencies = new HashMap();
        this._contributions = new HashMap();
        for (final String target : base._dependencies.keySet()) {
            if (updatedNs.contains(target)) {
                continue;
            }
            final Set depSet = new HashSet();
            this._dependencies.put(target, depSet);
            final Set baseDepSet = base._dependencies.get(target);
            for (final String source : baseDepSet) {
                if (updatedNs.contains(source)) {
                    continue;
                }
                depSet.add(source);
            }
        }
        for (final String ns : base._contributions.keySet()) {
            if (updatedNs.contains(ns)) {
                continue;
            }
            final List fileList = new ArrayList();
            this._contributions.put(ns, fileList);
            final List baseFileList = base._contributions.get(ns);
            final Iterator it2 = baseFileList.iterator();
            while (it2.hasNext()) {
                fileList.add(it2.next());
            }
        }
    }
    
    void registerContribution(final String ns, final String fileURL) {
        List fileList = this._contributions.get(ns);
        if (fileList == null) {
            fileList = new ArrayList();
            this._contributions.put(ns, fileList);
        }
        fileList.add(fileURL);
    }
    
    boolean isFileRepresented(final String fileURL) {
        for (final List fileList : this._contributions.values()) {
            if (fileList.contains(fileURL)) {
                return true;
            }
        }
        return false;
    }
    
    List getFilesTouched(final Set updatedNs) {
        final List result = new ArrayList();
        final Iterator it = updatedNs.iterator();
        while (it.hasNext()) {
            result.addAll(this._contributions.get(it.next()));
        }
        return result;
    }
    
    List getNamespacesTouched(final Set modifiedFiles) {
        final List result = new ArrayList();
        for (final String ns : this._contributions.keySet()) {
            final List files = this._contributions.get(ns);
            for (int i = 0; i < files.size(); ++i) {
                if (modifiedFiles.contains(files.get(i))) {
                    result.add(ns);
                }
            }
        }
        return result;
    }
}
