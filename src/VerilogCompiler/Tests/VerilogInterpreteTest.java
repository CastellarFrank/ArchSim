/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Tests;

import DataStructures.Loader;
import VerilogCompiler.Interpretation.InstanceModuleScope;
import VerilogCompiler.Interpretation.ModuleInstanceIdGenerator;
import VerilogCompiler.Interpretation.SimulationScope;
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
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class VerilogInterpreteTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Loader.getInstance().loadModules();
        
        try {
            File text = new File("tmp/Register_File.v");
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
                
                InstanceModuleScope scope = SemanticCheck.getInstance().variablesToScope();                
                SimulationScope simScope = new SimulationScope();
                String moduleInstanceId = ModuleInstanceIdGenerator.generate();
                simScope.register(moduleInstanceId, scope);
                
                if (!ErrorHandler.getInstance().hasErrors()) {     
                    module.initModule(simScope, moduleInstanceId);
                    module.executeModule(simScope, moduleInstanceId);
                    simScope.executeScheduledNonBlockingAssigns();
                    System.out.println(simScope.dumpToString(moduleInstanceId));
                }
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
