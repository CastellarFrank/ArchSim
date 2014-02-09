/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import VerilogCompiler.Interpretation.SimulationScope;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class SimulationFactory {
    public static SimulationScope createSimulationScope() {
        return new SimulationScope();
    }
}
