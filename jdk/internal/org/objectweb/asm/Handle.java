package jdk.internal.org.objectweb.asm;

public final class Handle
{
    final int tag;
    final String owner;
    final String name;
    final String desc;
    
    public Handle(final int tag, final String owner, final String name, final String desc) {
        this.tag = tag;
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }
    
    public int getTag() {
        return this.tag;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDesc() {
        return this.desc;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Handle)) {
            return false;
        }
        final Handle handle = (Handle)o;
        return this.tag == handle.tag && this.owner.equals(handle.owner) && this.name.equals(handle.name) && this.desc.equals(handle.desc);
    }
    
    @Override
    public int hashCode() {
        return this.tag + this.owner.hashCode() * this.name.hashCode() * this.desc.hashCode();
    }
    
    @Override
    public String toString() {
        return this.owner + '.' + this.name + this.desc + " (" + this.tag + ')';
    }
}
