/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import Simulation.Elements.Inputs.ClockInput;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author Franklin
 */
public class ClockChartInformation {
    ClockInput clock;
    String userReferenceName;
    XYSeries domainSerie;
    double maxValue;
    private int rangeIndex;
    
    public ClockChartInformation(ClockInput clock){
        this.clock = clock;
    }

    void setReferenceName(String userReferenceName) {
        this.userReferenceName = userReferenceName;
        if(this.domainSerie != null)
            this.domainSerie.setKey(userReferenceName);
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
    
    public double getMaxValue(){
        return this.maxValue;
    }
    
    public void setMaxValue(double value){
        this.maxValue = value;
    }

    public int getRangeIndex() {
        return this.rangeIndex;
    }
    
    public void setRangeIndex(int rangeIndex){
        double oldBottomIndex = this.rangeIndex * 3;
        this.rangeIndex = rangeIndex;
        double newBottomIndex = this.rangeIndex * 3;
        
        if(this.domainSerie == null)
            return;
        
        for (Object item : this.domainSerie.getItems()) {
            XYDataItem dataItem = (XYDataItem)item;
            double current = dataItem.getYValue();
            dataItem.setY(current == oldBottomIndex ? newBottomIndex : newBottomIndex + 2);
        }
    }
}
