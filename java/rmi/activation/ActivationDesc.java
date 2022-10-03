package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.io.Serializable;

public final class ActivationDesc implements Serializable
{
    private ActivationGroupID groupID;
    private String className;
    private String location;
    private MarshalledObject<?> data;
    private boolean restart;
    private static final long serialVersionUID = 7455834104417690957L;
    
    public ActivationDesc(final String s, final String s2, final MarshalledObject<?> marshalledObject) throws ActivationException {
        this(ActivationGroup.internalCurrentGroupID(), s, s2, marshalledObject, false);
    }
    
    public ActivationDesc(final String s, final String s2, final MarshalledObject<?> marshalledObject, final boolean b) throws ActivationException {
        this(ActivationGroup.internalCurrentGroupID(), s, s2, marshalledObject, b);
    }
    
    public ActivationDesc(final ActivationGroupID activationGroupID, final String s, final String s2, final MarshalledObject<?> marshalledObject) {
        this(activationGroupID, s, s2, marshalledObject, false);
    }
    
    public ActivationDesc(final ActivationGroupID groupID, final String className, final String location, final MarshalledObject<?> data, final boolean restart) {
        if (groupID == null) {
            throw new IllegalArgumentException("groupID can't be null");
        }
        this.groupID = groupID;
        this.className = className;
        this.location = location;
        this.data = data;
        this.restart = restart;
    }
    
    public ActivationGroupID getGroupID() {
        return this.groupID;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public MarshalledObject<?> getData() {
        return this.data;
    }
    
    public boolean getRestartMode() {
        return this.restart;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ActivationDesc) {
            final ActivationDesc activationDesc = (ActivationDesc)o;
            if (this.groupID == null) {
                if (activationDesc.groupID != null) {
                    return false;
                }
            }
            else if (!this.groupID.equals(activationDesc.groupID)) {
                return false;
            }
            if (this.className == null) {
                if (activationDesc.className != null) {
                    return false;
                }
            }
            else if (!this.className.equals(activationDesc.className)) {
                return false;
            }
            if (this.location == null) {
                if (activationDesc.location != null) {
                    return false;
                }
            }
            else if (!this.location.equals(activationDesc.location)) {
                return false;
            }
            if (this.data == null) {
                if (activationDesc.data != null) {
                    return false;
                }
            }
            else if (!this.data.equals(activationDesc.data)) {
                return false;
            }
            if (this.restart == activationDesc.restart) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((this.location == null) ? 0 : (this.location.hashCode() << 24)) ^ ((this.groupID == null) ? 0 : (this.groupID.hashCode() << 16)) ^ ((this.className == null) ? 0 : (this.className.hashCode() << 9)) ^ ((this.data == null) ? 0 : (this.data.hashCode() << 1)) ^ (this.restart ? 1 : 0);
    }
}
