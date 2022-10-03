package javax.ejb;

public interface MessageDrivenBean extends EnterpriseBean
{
    void ejbRemove() throws EJBException;
    
    void setMessageDrivenContext(final MessageDrivenContext p0) throws EJBException;
}
