/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Utils;

import VerilogCompiler.SyntacticTree.Base;
import VerilogCompiler.SyntacticTree.GateType;
import VerilogCompiler.SyntacticTree.NetType;
import VerilogCompiler.SyntacticTree.Operator;
import VerilogCompiler.SyntacticTree.PortDirection;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class StringUtils {
    
    private StringUtils() {
    }
    
    public static StringUtils getInstance() {
        return StringUtilsHolder.INSTANCE;
    }
    
    private static class StringUtilsHolder {

        private static final StringUtils INSTANCE = new StringUtils();
    }
    
    public String BaseToString(Base base) {
        switch(base) {
            case BINARY:
                return "'b";
            case DECIMAL:
                return "'d";
            case HEXADECIMAL:
                return "'h";
            case OCTAL:
                return "'o";
            default:
                return null;
        }
    }
    
    public String OperatorToString(Operator operator) {
        switch (operator) {
            case _OP_ADD:
                return "+";
            case _OP_BIT_AND:
                return "&";
            case _OP_BIT_NAND:
                return "~&";
            case _OP_BIT_NEG:
                return "~";
            case _OP_BIT_NOR:
                return "~|";
            case _OP_BIT_OR:
                return "|";
            case _OP_BIT_XNOR:
                return "~^";
            case _OP_BIT_XOR:
                return "^";
            case _OP_DIV:
                return "/";
            case _OP_EQ:
                return "=";
            case _OP_GRT:
                return ">";
            case _OP_GRTEQ:
                return ">=";
            case _OP_LOG_AND:
                return "&&";
            case _OP_LOG_NEG:
                return "!";
            case _OP_LOG_OR:
                return "||";
            case _OP_LST:
                return "<";
            case _OP_LSTEQ:
                return "<=";
            case _OP_L_ARIT_SHIFT:
                return "<<<";
            case _OP_L_SHIFT:
                return "<<";
            case _OP_MINUS:
                return "-";
            case _OP_MOD:
                return "%";
            case _OP_NOTEQ:
                return "!=";
            case _OP_R_ARIT_SHIFT:
                return ">>>";
            case _OP_R_SHIFT:
                return ">>";
            case _OP_TIMES:
                return "*";
            default:
                    return null;
        }
    }
    
    public String NetTypeToString(NetType type) {
        switch (type) {
            case SUPPLY0: return "supply0";
            case SUPPLY1: return "supply1";
            case WAND: return "wand";
            case WIRE: return "wire";
            case WOR: return "wor";
            case DATA_TYPE_INTEGER: return "integer";
            case DATA_TYPE_REG: return "reg";
            default:return null;
                
        }
    }
    
    public String PortDirectionToString(PortDirection direction) {
        switch(direction) {
            case INOUT: return "inout";
            case OUTPUT: return "output";
            case INPUT: return "input";
            default: return null;
        }
    }

    
    public String GateTypeToString(GateType type) {
        switch (type) {
            case AND: return "and";
            case NAND: return "nand";
            case NOR: return "nor";
            case OR: return "or";
            case XNOR: return "xnor";
            case XOR: return "xor";
            default: return null;
        }
    }
    
    public String ListToString(ArrayList<?> list, String separator) {
        String result = "";
        for (int i = 0; i < list.size() - 1; i++) {
            result += list.get(i).toString() + separator;
        }
        if (list.size() >= 1) {
            result += list.get(list.size() - 1);
        }
        
        return result;
    }
}
