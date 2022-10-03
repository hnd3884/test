package org.postgresql.jdbc;

enum StatementCancelState
{
    IDLE, 
    IN_QUERY, 
    CANCELING, 
    CANCELLED;
}
