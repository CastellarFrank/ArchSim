/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler;

import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import java.io.StringReader;
import java_cup.runtime.Symbol;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class CompilationHelper {
    public static ModuleDecl parseWithoutSemantics(String source) {
        try {
            ErrorHandler.getInstance().reset();
            SemanticCheck.getInstance().resetAll();
            parser parser = new parser(new VerilogLexer(new StringReader(source)));
            Symbol result = parser.parse();
            if (result == null) {
                return null;
            }

            ModuleDecl module = (ModuleDecl) result.value;
            return module;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());            
            return null;
        }
    }
    
    public static ModuleDecl parseWithSemantics(String source) {
        ModuleDecl module = parseWithoutSemantics(source);
        if (module == null)
            return null;
        module.validateSemantics();
        if (ErrorHandler.getInstance().hasErrors())
            return null;
        return module;
    }
}
