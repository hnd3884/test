package sun.security.provider.certpath;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class AdjacencyList
{
    private ArrayList<BuildStep> mStepList;
    private List<List<Vertex>> mOrigList;
    
    public AdjacencyList(final List<List<Vertex>> mOrigList) {
        this.mStepList = new ArrayList<BuildStep>();
        this.buildList(this.mOrigList = mOrigList, 0, null);
    }
    
    public Iterator<BuildStep> iterator() {
        return Collections.unmodifiableList((List<? extends BuildStep>)this.mStepList).iterator();
    }
    
    private boolean buildList(final List<List<Vertex>> list, final int n, final BuildStep buildStep) {
        final List list2 = list.get(n);
        boolean b = true;
        boolean b2 = true;
        for (final Vertex vertex : list2) {
            if (vertex.getIndex() != -1) {
                if (list.get(vertex.getIndex()).size() != 0) {
                    b = false;
                }
            }
            else if (vertex.getThrowable() == null) {
                b2 = false;
            }
            this.mStepList.add(new BuildStep(vertex, 1));
        }
        if (b) {
            if (b2) {
                if (buildStep == null) {
                    this.mStepList.add(new BuildStep(null, 4));
                }
                else {
                    this.mStepList.add(new BuildStep(buildStep.getVertex(), 2));
                }
                return false;
            }
            final ArrayList list3 = new ArrayList();
            for (final Vertex vertex2 : list2) {
                if (vertex2.getThrowable() == null) {
                    list3.add(vertex2);
                }
            }
            if (list3.size() == 1) {
                this.mStepList.add(new BuildStep((Vertex)list3.get(0), 5));
            }
            else {
                this.mStepList.add(new BuildStep((Vertex)list3.get(0), 5));
            }
            return true;
        }
        else {
            boolean buildList = false;
            for (final Vertex vertex3 : list2) {
                if (vertex3.getIndex() != -1 && list.get(vertex3.getIndex()).size() != 0) {
                    final BuildStep buildStep2 = new BuildStep(vertex3, 3);
                    this.mStepList.add(buildStep2);
                    buildList = this.buildList(list, vertex3.getIndex(), buildStep2);
                }
            }
            if (buildList) {
                return true;
            }
            if (buildStep == null) {
                this.mStepList.add(new BuildStep(null, 4));
            }
            else {
                this.mStepList.add(new BuildStep(buildStep.getVertex(), 2));
            }
            return false;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[\n");
        int n = 0;
        for (final List list : this.mOrigList) {
            sb.append("LinkedList[").append(n++).append("]:\n");
            final Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                sb.append(((Vertex)iterator2.next()).toString()).append("\n");
            }
        }
        sb.append("]\n");
        return sb.toString();
    }
}
