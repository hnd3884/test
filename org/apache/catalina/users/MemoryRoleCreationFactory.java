package org.apache.catalina.users;

import org.apache.catalina.Role;
import org.xml.sax.Attributes;
import org.apache.tomcat.util.digester.AbstractObjectCreationFactory;

class MemoryRoleCreationFactory extends AbstractObjectCreationFactory
{
    private final MemoryUserDatabase database;
    
    public MemoryRoleCreationFactory(final MemoryUserDatabase database) {
        this.database = database;
    }
    
    public Object createObject(final Attributes attributes) {
        String rolename = attributes.getValue("rolename");
        if (rolename == null) {
            rolename = attributes.getValue("name");
        }
        final String description = attributes.getValue("description");
        final Role existingRole = this.database.findRole(rolename);
        if (existingRole == null) {
            return this.database.createRole(rolename, description);
        }
        if (existingRole.getDescription() == null) {
            existingRole.setDescription(description);
        }
        return existingRole;
    }
}
