package org.json.simple;

class Yytoken
{
    private final Types type;
    private final Object value;
    
    Yytoken(final Types type, final Object value) {
        switch (type) {
            case COLON: {
                this.value = ":";
                break;
            }
            case COMMA: {
                this.value = ",";
                break;
            }
            case END: {
                this.value = "";
                break;
            }
            case LEFT_BRACE: {
                this.value = "{";
                break;
            }
            case LEFT_SQUARE: {
                this.value = "[";
                break;
            }
            case RIGHT_BRACE: {
                this.value = "}";
                break;
            }
            case RIGHT_SQUARE: {
                this.value = "]";
                break;
            }
            default: {
                this.value = value;
                break;
            }
        }
        this.type = type;
    }
    
    Types getType() {
        return this.type;
    }
    
    Object getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.type.toString()).append("(").append(this.value).append(")");
        return sb.toString();
    }
    
    enum Types
    {
        COLON, 
        COMMA, 
        DATUM, 
        END, 
        LEFT_BRACE, 
        LEFT_SQUARE, 
        RIGHT_BRACE, 
        RIGHT_SQUARE;
    }
}
