package sun.security.x509;

import sun.security.util.ObjectIdentifier;
import sun.security.util.DerOutputStream;
import java.util.Iterator;
import java.io.IOException;
import sun.security.util.DerValue;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class GeneralSubtrees implements Cloneable
{
    private final List<GeneralSubtree> trees;
    private static final int NAME_DIFF_TYPE = -1;
    private static final int NAME_MATCH = 0;
    private static final int NAME_NARROWS = 1;
    private static final int NAME_WIDENS = 2;
    private static final int NAME_SAME_TYPE = 3;
    
    public GeneralSubtrees() {
        this.trees = new ArrayList<GeneralSubtree>();
    }
    
    private GeneralSubtrees(final GeneralSubtrees generalSubtrees) {
        this.trees = new ArrayList<GeneralSubtree>(generalSubtrees.trees);
    }
    
    public GeneralSubtrees(final DerValue derValue) throws IOException {
        this();
        if (derValue.tag != 48) {
            throw new IOException("Invalid encoding of GeneralSubtrees.");
        }
        while (derValue.data.available() != 0) {
            this.add(new GeneralSubtree(derValue.data.getDerValue()));
        }
    }
    
    public GeneralSubtree get(final int n) {
        return this.trees.get(n);
    }
    
    public void remove(final int n) {
        this.trees.remove(n);
    }
    
    public void add(final GeneralSubtree generalSubtree) {
        if (generalSubtree == null) {
            throw new NullPointerException();
        }
        this.trees.add(generalSubtree);
    }
    
    public boolean contains(final GeneralSubtree generalSubtree) {
        if (generalSubtree == null) {
            throw new NullPointerException();
        }
        return this.trees.contains(generalSubtree);
    }
    
    public int size() {
        return this.trees.size();
    }
    
    public Iterator<GeneralSubtree> iterator() {
        return this.trees.iterator();
    }
    
    public List<GeneralSubtree> trees() {
        return this.trees;
    }
    
    public Object clone() {
        return new GeneralSubtrees(this);
    }
    
    @Override
    public String toString() {
        return "   GeneralSubtrees:\n" + this.trees.toString() + "\n";
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        final DerOutputStream derOutputStream2 = new DerOutputStream();
        for (int i = 0; i < this.size(); ++i) {
            this.get(i).encode(derOutputStream2);
        }
        derOutputStream.write((byte)48, derOutputStream2);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof GeneralSubtrees && this.trees.equals(((GeneralSubtrees)o).trees));
    }
    
    @Override
    public int hashCode() {
        return this.trees.hashCode();
    }
    
    private GeneralNameInterface getGeneralNameInterface(final int n) {
        return getGeneralNameInterface(this.get(n));
    }
    
    private static GeneralNameInterface getGeneralNameInterface(final GeneralSubtree generalSubtree) {
        return generalSubtree.getName().getName();
    }
    
    private void minimize() {
        for (int i = 0; i < this.size() - 1; ++i) {
            final GeneralNameInterface generalNameInterface = this.getGeneralNameInterface(i);
            boolean b = false;
            int j = i + 1;
            while (j < this.size()) {
                Label_0112: {
                    switch (generalNameInterface.constrains(this.getGeneralNameInterface(j))) {
                        case 0: {
                            b = true;
                            break Label_0112;
                        }
                        case 1: {
                            this.remove(j);
                            --j;
                            break;
                        }
                        case 2: {
                            b = true;
                            break Label_0112;
                        }
                    }
                    ++j;
                    continue;
                }
                break;
            }
            if (b) {
                this.remove(i);
                --i;
            }
        }
    }
    
    private GeneralSubtree createWidestSubtree(final GeneralNameInterface generalNameInterface) {
        try {
            GeneralName generalName = null;
            switch (generalNameInterface.getType()) {
                case 0: {
                    generalName = new GeneralName(new OtherName(((OtherName)generalNameInterface).getOID(), null));
                    break;
                }
                case 1: {
                    generalName = new GeneralName(new RFC822Name(""));
                    break;
                }
                case 2: {
                    generalName = new GeneralName(new DNSName(""));
                    break;
                }
                case 3: {
                    generalName = new GeneralName(new X400Address((byte[])null));
                    break;
                }
                case 4: {
                    generalName = new GeneralName(new X500Name(""));
                    break;
                }
                case 5: {
                    generalName = new GeneralName(new EDIPartyName(""));
                    break;
                }
                case 6: {
                    generalName = new GeneralName(new URIName(""));
                    break;
                }
                case 7: {
                    generalName = new GeneralName(new IPAddressName((byte[])null));
                    break;
                }
                case 8: {
                    generalName = new GeneralName(new OIDName(new ObjectIdentifier((int[])null)));
                    break;
                }
                default: {
                    throw new IOException("Unsupported GeneralNameInterface type: " + generalNameInterface.getType());
                }
            }
            return new GeneralSubtree(generalName, 0, -1);
        }
        catch (final IOException ex) {
            throw new RuntimeException("Unexpected error: " + ex, ex);
        }
    }
    
    public GeneralSubtrees intersect(final GeneralSubtrees generalSubtrees) {
        if (generalSubtrees == null) {
            throw new NullPointerException("other GeneralSubtrees must not be null");
        }
        final GeneralSubtrees generalSubtrees2 = new GeneralSubtrees();
        GeneralSubtrees generalSubtrees3 = null;
        if (this.size() == 0) {
            this.union(generalSubtrees);
            return null;
        }
        this.minimize();
        generalSubtrees.minimize();
        for (int i = 0; i < this.size(); ++i) {
            final GeneralNameInterface generalNameInterface = this.getGeneralNameInterface(i);
            boolean b = false;
        Label_0186:
            for (int j = 0; j < generalSubtrees.size(); ++j) {
                final GeneralSubtree value = generalSubtrees.get(j);
                switch (generalNameInterface.constrains(getGeneralNameInterface(value))) {
                    case 1: {
                        this.remove(i);
                        --i;
                        generalSubtrees2.add(value);
                        b = false;
                        break Label_0186;
                    }
                    case 3: {
                        b = true;
                        break;
                    }
                    case 0:
                    case 2: {
                        b = false;
                        break Label_0186;
                    }
                }
            }
            if (b) {
                boolean b2 = false;
                for (int k = 0; k < this.size(); ++k) {
                    final GeneralNameInterface generalNameInterface2 = this.getGeneralNameInterface(k);
                    if (generalNameInterface2.getType() == generalNameInterface.getType()) {
                        for (int l = 0; l < generalSubtrees.size(); ++l) {
                            final int constrains = generalNameInterface2.constrains(generalSubtrees.getGeneralNameInterface(l));
                            if (constrains == 0 || constrains == 2 || constrains == 1) {
                                b2 = true;
                                break;
                            }
                        }
                    }
                }
                if (!b2) {
                    if (generalSubtrees3 == null) {
                        generalSubtrees3 = new GeneralSubtrees();
                    }
                    final GeneralSubtree widestSubtree = this.createWidestSubtree(generalNameInterface);
                    if (!generalSubtrees3.contains(widestSubtree)) {
                        generalSubtrees3.add(widestSubtree);
                    }
                }
                this.remove(i);
                --i;
            }
        }
        if (generalSubtrees2.size() > 0) {
            this.union(generalSubtrees2);
        }
        for (int n = 0; n < generalSubtrees.size(); ++n) {
            final GeneralSubtree value2 = generalSubtrees.get(n);
            final GeneralNameInterface generalNameInterface3 = getGeneralNameInterface(value2);
            boolean b3 = false;
        Label_0477:
            for (int n2 = 0; n2 < this.size(); ++n2) {
                switch (this.getGeneralNameInterface(n2).constrains(generalNameInterface3)) {
                    case -1: {
                        b3 = true;
                        break;
                    }
                    case 0:
                    case 1:
                    case 2:
                    case 3: {
                        b3 = false;
                        break Label_0477;
                    }
                }
            }
            if (b3) {
                this.add(value2);
            }
        }
        return generalSubtrees3;
    }
    
    public void union(final GeneralSubtrees generalSubtrees) {
        if (generalSubtrees != null) {
            for (int i = 0; i < generalSubtrees.size(); ++i) {
                this.add(generalSubtrees.get(i));
            }
            this.minimize();
        }
    }
    
    public void reduce(final GeneralSubtrees generalSubtrees) {
        if (generalSubtrees == null) {
            return;
        }
        for (int i = 0; i < generalSubtrees.size(); ++i) {
            final GeneralNameInterface generalNameInterface = generalSubtrees.getGeneralNameInterface(i);
            for (int j = 0; j < this.size(); ++j) {
                switch (generalNameInterface.constrains(this.getGeneralNameInterface(j))) {
                    case 0: {
                        this.remove(j);
                        --j;
                        break;
                    }
                    case 1: {
                        this.remove(j);
                        --j;
                    }
                }
            }
        }
    }
}
