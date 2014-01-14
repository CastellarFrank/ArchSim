/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class RowInfo {
    public boolean rightSideChanges, leftSideChanges;
    public boolean notUsedInMatrix;
    public double voltage;
    public RowType type;
    
    public int rowEqualsToReference;
    public int mappedColumn, mappedRow;

    public RowInfo() {
        type = RowType.NORMAL;
    }
}
