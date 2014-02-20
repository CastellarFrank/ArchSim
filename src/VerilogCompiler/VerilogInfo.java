/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package VerilogCompiler;

import java.util.ArrayList;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class VerilogInfo {

    private ArrayList<String> keywords;

    private VerilogInfo() {
        keywords = new ArrayList<String>();
        keywords.add("always");
        keywords.add("and");
        keywords.add("assign");
        keywords.add("begin");
        keywords.add("case");
        keywords.add("deassign");
        keywords.add("default");
        keywords.add("edge");
        keywords.add("else");
        keywords.add("end");
        keywords.add("endcase");
        keywords.add("endmodule");
        keywords.add("for");
        keywords.add("forever");
        keywords.add("if");
        keywords.add("initial");
        keywords.add("inout");
        keywords.add("integer");
        keywords.add("input");
        keywords.add("module");
        keywords.add("nand");
        keywords.add("negedge");
        keywords.add("nor");
        keywords.add("output");
        keywords.add("or");
        keywords.add("parameter");
        keywords.add("posedge");
        keywords.add("reg");
        keywords.add("repeat");
        keywords.add("signed");
        keywords.add("supply0");
        keywords.add("supply1");
        keywords.add("wand");
        keywords.add("wor");
        keywords.add("unsigned");
        keywords.add("wait");
        keywords.add("while");
        keywords.add("wire");
        keywords.add("xnor");
        keywords.add("xor");
    }

    public ArrayList<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    public static VerilogInfo getInstance() {
        return VerilogInfoHolder.INSTANCE;
    }

    private static class VerilogInfoHolder {

        private static final VerilogInfo INSTANCE = new VerilogInfo();
    }
}
