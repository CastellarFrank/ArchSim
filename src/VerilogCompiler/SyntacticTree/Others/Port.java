/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SyntacticTree.Others;

import VerilogCompiler.SemanticCheck.DataType;
import VerilogCompiler.SemanticCheck.ErrorHandler;
import VerilogCompiler.SemanticCheck.ExpressionType;
import VerilogCompiler.SemanticCheck.SemanticCheck;
import VerilogCompiler.SemanticCheck.VariableInfo;
import VerilogCompiler.SyntacticTree.Expressions.Expression;
import VerilogCompiler.SyntacticTree.Expressions.SimpleNumberExpression;
import VerilogCompiler.SyntacticTree.NetType;
import VerilogCompiler.SyntacticTree.PortDirection;
import VerilogCompiler.SyntacticTree.VNode;
import VerilogCompiler.Utils.StringUtils;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Port extends VNode {
    PortDirection direction;
    NetType       dataType;
    String        identifier;
    Expression    minExpression;
    Expression    maxExpression;
    boolean       isVector;
    int           sideIndex;
    int           signalSize;

    public Port(PortDirection direction, NetType dataType, String identifier, Expression minExpression, Expression maxExpression, int line, int column) {
        super(line, column);
        this.direction = direction;
        this.dataType = dataType;
        this.identifier = identifier;
        this.minExpression = minExpression;
        this.maxExpression = maxExpression;
        this.isVector = false;
    }
    
    public Port(PortDirection direction, NetType dataType, 
            String identifier, Expression minExpression, 
            Expression maxExpression, boolean isArray, int line, int column) {
        super(line, column);
        this.direction = direction;
        this.dataType = dataType;
        this.identifier = identifier;
        this.minExpression = minExpression;
        this.maxExpression = maxExpression;
        this.isVector = isArray;
    }

    public PortDirection getDirection() {
        return direction;
    }

    public void setDirection(PortDirection direction) {
        this.direction = direction;
    }

    public NetType getDataType() {
        return dataType;
    }

    public void setDataType(NetType dataType) {
        this.dataType = dataType;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getSideIndex() {
        return sideIndex;
    }

    public void setSideIndex(int sideIndex) {
        this.sideIndex = sideIndex;
    }

    public Expression getMinExpression() {
        return minExpression;
    }

    public void setMinExpression(Expression minExpression) {
        this.minExpression = minExpression;
    }

    public Expression getMaxExpression() {
        return maxExpression;
    }

    public void setMaxExpression(Expression maxExpression) {
        this.maxExpression = maxExpression;
    }

    @Override
    public String toString() {
        String dir = StringUtils.getInstance().PortDirectionToString(direction);
        String type = dataType==null?"": StringUtils.getInstance().NetTypeToString(dataType);
        if (minExpression != null && maxExpression != null){
            return String.format("%s %s %s [%s:%s]", dir, type, identifier, minExpression, maxExpression);
        } else if (minExpression != null){
            return String.format("%s %s %s [%s]", dir, type, identifier, minExpression);
        } else {
            return String.format("%s %s %s", dir, type, identifier);
        }
    }

    @Override
    public ExpressionType validateSemantics() {
        VariableInfo info = new VariableInfo();
        info.isPort = true;
        if (SemanticCheck.getInstance().variableIsRegistered(identifier)) {
            ErrorHandler.getInstance().handleError(line, column, 
                    identifier + " is already defined");
            return ExpressionType.ERROR;
        } else {
            info.addAcceptedType(DataType.NET);
            if (direction == PortDirection.OUTPUT)
                info.addAcceptedType(DataType.VARIABLE);
            
            if (minExpression != null && !isVector)
                info.isArray = true;
            if (isVector) info.isVector = true;
            
        }
        long lsb = 0, msb = 0;
        if (minExpression != null) {
            lsb = ((SimpleNumberExpression) minExpression).getUnsignedNumber();
            minExpression.validateSemantics();
        }
        
        if (maxExpression != null) {
            msb = ((SimpleNumberExpression) maxExpression).getUnsignedNumber();
            maxExpression.validateSemantics();
        }
        
        if (info.isVector) {
            info.LSB = (int)lsb;
            info.MSB = (int)msb;
            signalSize = Math.abs(info.LSB - info.MSB) + 1;
        }
        SemanticCheck.getInstance().registerVariable(identifier, info);
        return null;
    }

    public int getSignalSize() {
        return signalSize;
    }

    @Override
    public VNode getCopy() {
        Expression newMin = minExpression != null ? (Expression)minExpression.getCopy() : null;
        Expression newMax = minExpression != null ? (Expression)maxExpression.getCopy() : null;
        Port copy = new Port(direction, dataType, identifier,
                newMin, newMax, 
                isVector, line, column);
        
        copy.signalSize = this.signalSize;
        
        return copy;
    }
    
}
