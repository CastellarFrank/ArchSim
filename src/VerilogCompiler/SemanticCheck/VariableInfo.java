/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SemanticCheck;

import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class VariableInfo {
    public boolean isArray = false;
    public boolean isVector = false;
    public boolean isNumeric = false;
    public boolean isModuleInstance = false;
    public boolean isBigEndian = false; /*BIG ENDIAN = MSB...LSB; */
    public DataType type;
    public ArrayList<DataType> acceptedTypes = new ArrayList<DataType>();
    
    public void addAcceptedType(DataType type) {
        if (!acceptedTypes.contains(type))
            acceptedTypes.add(type);
    }
}
