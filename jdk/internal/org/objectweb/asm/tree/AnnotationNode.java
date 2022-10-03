package jdk.internal.org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.AnnotationVisitor;

public class AnnotationNode extends AnnotationVisitor
{
    public String desc;
    public List<Object> values;
    
    public AnnotationNode(final String s) {
        this(327680, s);
        if (this.getClass() != AnnotationNode.class) {
            throw new IllegalStateException();
        }
    }
    
    public AnnotationNode(final int n, final String desc) {
        super(n);
        this.desc = desc;
    }
    
    AnnotationNode(final List<Object> values) {
        super(327680);
        this.values = values;
    }
    
    @Override
    public void visit(final String s, final Object o) {
        if (this.values == null) {
            this.values = new ArrayList<Object>((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        this.values.add(o);
    }
    
    @Override
    public void visitEnum(final String s, final String s2, final String s3) {
        if (this.values == null) {
            this.values = new ArrayList<Object>((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        this.values.add(new String[] { s2, s3 });
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String s, final String s2) {
        if (this.values == null) {
            this.values = new ArrayList<Object>((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        final AnnotationNode annotationNode = new AnnotationNode(s2);
        this.values.add(annotationNode);
        return annotationNode;
    }
    
    @Override
    public AnnotationVisitor visitArray(final String s) {
        if (this.values == null) {
            this.values = new ArrayList<Object>((this.desc != null) ? 2 : 1);
        }
        if (this.desc != null) {
            this.values.add(s);
        }
        final ArrayList list = new ArrayList();
        this.values.add(list);
        return new AnnotationNode(list);
    }
    
    @Override
    public void visitEnd() {
    }
    
    public void check(final int n) {
    }
    
    public void accept(final AnnotationVisitor annotationVisitor) {
        if (annotationVisitor != null) {
            if (this.values != null) {
                for (int i = 0; i < this.values.size(); i += 2) {
                    accept(annotationVisitor, (String)this.values.get(i), this.values.get(i + 1));
                }
            }
            annotationVisitor.visitEnd();
        }
    }
    
    static void accept(final AnnotationVisitor annotationVisitor, final String s, final Object o) {
        if (annotationVisitor != null) {
            if (o instanceof String[]) {
                final String[] array = (String[])o;
                annotationVisitor.visitEnum(s, array[0], array[1]);
            }
            else if (o instanceof AnnotationNode) {
                final AnnotationNode annotationNode = (AnnotationNode)o;
                annotationNode.accept(annotationVisitor.visitAnnotation(s, annotationNode.desc));
            }
            else if (o instanceof List) {
                final AnnotationVisitor visitArray = annotationVisitor.visitArray(s);
                if (visitArray != null) {
                    final List list = (List)o;
                    for (int i = 0; i < list.size(); ++i) {
                        accept(visitArray, null, list.get(i));
                    }
                    visitArray.visitEnd();
                }
            }
            else {
                annotationVisitor.visit(s, o);
            }
        }
    }
}
