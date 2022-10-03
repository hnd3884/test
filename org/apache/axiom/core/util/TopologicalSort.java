package org.apache.axiom.core.util;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Collection;

public final class TopologicalSort
{
    private TopologicalSort() {
    }
    
    private static <T> void visit(final Collection<T> vertices, final EdgeRelation<? super T> edgeRelation, final List<T> result, final Set<T> visited, final T vertex) {
        if (visited.add(vertex)) {
            for (final T vertex2 : vertices) {
                if (vertex2 != vertex && edgeRelation.isEdge((Object)vertex, (Object)vertex2)) {
                    visit((Collection<Object>)vertices, (EdgeRelation<? super Object>)edgeRelation, (List<Object>)result, (Set<Object>)visited, vertex2);
                }
            }
            result.add(vertex);
        }
    }
    
    public static <T> List<T> sort(final Collection<T> vertices, final EdgeRelation<? super T> edgeRelation) {
        final List<T> result = new ArrayList<T>(vertices.size());
        final Set<T> visited = new HashSet<T>();
        for (final T vertex : vertices) {
            visit(vertices, edgeRelation, result, visited, vertex);
        }
        return result;
    }
}
