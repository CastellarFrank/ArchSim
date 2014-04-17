/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler.SemanticCheck;

import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ErrorHandler {
    ArrayList<String> errors, warnings;
    
    private ErrorHandler() {
        errors = new ArrayList<String>();
        warnings = new ArrayList<String>();
    }
    
    public void handleError(int line, int column, String error) {
        String errorMessage = "%d:%d: %s";
        errors.add(String.format(errorMessage, line+1, column+1, error));
    }
    
    public String getErrors() {
        String result = "";
        for (String string : errors) {
            result += string + "\n";
        }
        
        return result;
    }
    
    public ArrayList<String> getErrorList() {
        return errors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public void reset() {
        this.errors.clear();
        this.warnings.clear();
    }
    
    public static ErrorHandler getInstance() {
        return ErrorHandlerHolder.INSTANCE;
    }
    
    private static class ErrorHandlerHolder {

        private static final ErrorHandler INSTANCE = new ErrorHandler();
    }
}
