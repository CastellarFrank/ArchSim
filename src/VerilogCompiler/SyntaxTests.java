/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler;

import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import java.io.FileReader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class SyntaxTests {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            parser parser = new parser(new VerilogLexer(new FileReader("C:\\Users\\Alejandro\\Documents\\NetBeansProjects\\GradProjectV2\\src\\VerilogCompiler\\Tests\\FullGrammarExample.v")));
            ModuleDecl module = (ModuleDecl) parser.parse().value;
            System.out.println(module.toString());
        } catch (Exception ex) {
            Logger.getLogger(SyntaxTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
