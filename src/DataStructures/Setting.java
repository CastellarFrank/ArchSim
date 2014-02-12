/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DataStructures;

/**
 * Class that represents a general setting.
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class Setting {

    /** This <code>Setting</code> type **/
    public String type;
    
    /** This <code>Setting</code> name **/
    public String name;
    
    /** This <code>Setting</code> value **/
    public Object value;

    /**
     * Constructor
     * @param type type of this <code>Setting</code>
     * @param name name of this <code>Setting</code>
     * @param value value of this <code>Setting</code>
     */
    public Setting(String type, String name, Object value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
}