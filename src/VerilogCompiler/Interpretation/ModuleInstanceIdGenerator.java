/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ModuleInstanceIdGenerator {
    private static int nextId = 0;
    
    public static String generate() {
        return "moduleInstance" + nextId++;
    }
}
