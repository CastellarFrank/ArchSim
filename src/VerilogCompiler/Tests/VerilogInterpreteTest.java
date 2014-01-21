/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Tests;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import VerilogCompiler.VerilogLexer;
import VerilogCompiler.parser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java_cup.runtime.Symbol;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class VerilogInterpreteTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            File text = new File("tmp/ALU.xml");
            FileInputStream stream  = new FileInputStream(text);
            byte[] data = new byte[(int)text.length()];
            
            stream.read(data);
            stream.close();
            
            String source = new String(data);
            
            try {
                ErrorHandler.getInstance().reset();
                SemanticCheck.getInstance().resetAll();
                parser parser = new parser(new VerilogLexer(new StringReader(source)));
                Symbol result = parser.parse();
                if (result == null) return;
                
                ModuleDecl module = (ModuleDecl) result.value;
                System.out.println(module.toString());
                module.validateSemantics();
                
                if (!ErrorHandler.getInstance().hasErrors())
                    module.executeModule();
                else
                    System.err.println(ErrorHandler.getInstance().getErrors());
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        
    }
}
