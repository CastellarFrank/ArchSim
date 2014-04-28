/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.Interpretation;

import Simulation.Configuration;
import VerilogCompiler.SemanticCheck.VariableInfo;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimulationScope {
    //<editor-fold defaultstate="collapsed" desc="Attributes">

    HashMap<String, InstanceModuleScope> scopes;
    public static String currentModuleName = null;
    //</editor-fold>

    public SimulationScope() {
        scopes = new HashMap<String, InstanceModuleScope>();
    }

    public void register(String moduleInstanceId, InstanceModuleScope scope) {
        scopes.put(moduleInstanceId, scope);
    }

    public void unregister(String moduleInstaceId) {
        if (Configuration.DEBUG_MODE) {
            System.out.println("unregistering " + moduleInstaceId + " instance module scope");
        }
        scopes.remove(moduleInstaceId);
    }

    public void executeScheduledNonBlockingAssigns() {
        for (Map.Entry<String, InstanceModuleScope> scope : scopes.entrySet()) {
            scope.getValue().executeNonBlockingAssigns();
        }
    }

    public String dumpToString(String moduleInstanceId) {
        return getScope(moduleInstanceId).dumpToString();
    }

    public InstanceModuleScope getScope(String moduleInstanceId) {
        return scopes.get(moduleInstanceId);
    }

    public ExpressionValue getVariableValue(String moduleInstanceId, String variable) {
        if (scopes.containsKey(moduleInstanceId)) {
            return scopes.get(moduleInstanceId).getVariableValue(variable);
        }
        return null;
    }

    public String getFormattedValue(String moduleInstanceId, String variable) {
        if (scopes.containsKey(moduleInstanceId)) {
            VariableInfo info = scopes.get(moduleInstanceId).getVariableInfo(variable);
            if (info == null || info.value == null) {
                return null;
            }
            Object val = info.value.value;
            if (val instanceof Integer || val instanceof BigInteger) {
                return padWithZeros(val, Math.abs(info.MSB - info.LSB) + 1);
            } else if (val instanceof Object[])
                return "z";
            return val != null ? val.toString() : null;
        }
        return null;
    }

    public String padWithZeros(Object val, int size) {
        if (val.toString().length() > size) {
            return val.toString().substring(val.toString().length() - size);
        }
        if (val instanceof Integer || val instanceof BigInteger) {
            if (size == 0) {
                return val.toString();
            }
            String f = "%0" + Math.abs(size) + "d";
            return String.format(f, val);
        } else if (val instanceof String) {
            if (val.toString().length() == 16) return val.toString();
            String f = "%0" + (16 - val.toString().length()) + "d";
            return String.format(f, 0) + val;
        }
        
        return null;
    }

    public VariableInfo getVariableInfo(String moduleInstanceId, String variable) {
        if (scopes.containsKey(moduleInstanceId)) {
            return scopes.get(moduleInstanceId).getVariableInfo(variable);
        }
        return null;
    }

    public void init() {
        Set<String> keys = scopes.keySet();
        for (String key : keys) {
            scopes.get(key).init(this, key);
        }
    }

    public void runStep() {
        Set<String> keys = scopes.keySet();
        for (String key : keys) {
            scopes.get(key).runStep(this, key);
        }
    }
}
