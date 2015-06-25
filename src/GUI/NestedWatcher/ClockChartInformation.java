/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import Simulation.Elements.Inputs.ClockInput;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Franklin
 */
public class ClockChartInformation {
    ClockInput clock;
    String userReferenceName;
    XYSeries domainSerie;
    
    public ClockChartInformation(ClockInput clock){
        this.clock = clock;
    }

    void setReferenceName(String userReferenceName) {
        this.userReferenceName = userReferenceName;
    }

    void setDomainSerie(XYSeries newDomainSeries) {
        this.domainSerie = newDomainSeries;
    }

    public ClockInput getClock() {
        return clock;
    }

    public String getUserReferenceName() {
        return userReferenceName;
    }

    public XYSeries getDomainSerie() {
        return domainSerie;
    }
}
