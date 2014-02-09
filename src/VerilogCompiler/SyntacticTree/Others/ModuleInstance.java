/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import DataStructures.ModuleRepository;
import VerilogCompiler.Interpretation.ModuleInstanceIdGenerator;
import VerilogCompiler.Interpretation.SimulationScope;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SyntacticTree.Declarations.ModuleDecl;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;
import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ModuleInstance extends VNode {
    String identifier, moduleName, moduleInstanceId;
    ArrayList<Expression> moduleConnectionList;
    
    ModuleDecl moduleInstance;

    public ModuleInstance(String identifier, ArrayList<Expression> moduleConnectionList, int line, int column) {
        super(line, column);
        this.identifier = identifier;
        this.moduleConnectionList = moduleConnectionList;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<Expression> getModuleConnectionList() {
        return moduleConnectionList;
    }

    public void setModuleConnectionList(ArrayList<Expression> moduleConnectionList) {
        this.moduleConnectionList = moduleConnectionList;
    }
    
    public void initModuleInstance(SimulationScope simulationScope) {
        simulationScope.register(moduleInstanceId, moduleInstance.getScope().getCopy());
        moduleInstance.initModule(simulationScope, moduleInstanceId);
    }
    
    public void executeModuleInstance(SimulationScope simulationScope, String parentModuleInstanceId) {
        System.out.println("Executing module instance inside " + parentModuleInstanceId);
        System.out.println(moduleInstance);
        System.out.println("End of instance");
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", identifier, 
                StringUtils.getInstance().ListToString(moduleConnectionList, ","));
    }
    
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public ExpressionType validateSemantics() {
        if (SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is already defined");
            return ExpressionType.ERROR;
        }
        moduleInstance = ModuleRepository.getInstance().getModuleLogic(moduleName);
        moduleInstanceId = ModuleInstanceIdGenerator.generate();
        
        if (moduleInstance.getPortList().size() != moduleConnectionList.size()) {
            ErrorHandler.getInstance().handleError(line, column, 
                    moduleName + " instance with name " + identifier +
                    " different number of connections between formal and actual");
            return ExpressionType.ERROR;
        }
        
        for (Expression expression : moduleConnectionList) {
            expression.validateSemantics();
        }
        return null;
    }

    @Override
    public VNode getCopy() {
        ArrayList<Expression> exps = new ArrayList<Expression>();
        for (Expression expression : moduleConnectionList) {
            exps.add((Expression)expression.getCopy());
        }
        ModuleInstance newOne = new ModuleInstance(identifier, exps, line, column);
        newOne.setModuleName(moduleName);
        newOne.moduleInstance = (ModuleDecl) moduleInstance.getCopy();
        return newOne;
    }
    
}
