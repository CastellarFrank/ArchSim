/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import GUI.ContainerPanel;
import GUI.NestedWatcher.CustomClocksChart;
import Simulation.Elements.Inputs.ClockInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
/**
 *
 * @author Franklin
 */
public class ClockEventManagement {
    Map<Long, List<ClockInput>> clocksByExecutionTime;
    Thread mainlyThread;
    boolean isSimulationRunning = false;
    boolean needsRestart = false;
    long startExecutionTime = 0;
    long executionTimeInterval = 100;
    public final Object lockElement = new Object();
    ContainerPanel containerPanel;
    int uniqueIdCounter = 0;
    CustomClocksChart clocksChartLogic;
    
    public ClockEventManagement(ContainerPanel containerPanel){
        this.clocksByExecutionTime = new HashMap<Long, List<ClockInput>>();
        this.containerPanel = containerPanel;
    }
    
    public void simulationReseted(){
        this.needsRestart = true;
        this.simulationStopped();
    }
    
    public void simulationStarted(){
        this.isSimulationRunning = true;
        
        this.mainlyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    executeClocksLogic();
                } catch (InterruptedException ex) {
                    System.out.println("Clock Failed, This shouldn't happened.");
                }
            }
        });
        this.mainlyThread.start();
    }
    
    public void simulationStopped(){    
        this.isSimulationRunning = false;
        
        try {
            /*The waiting time could be removed, 
            but the threat could throw an Interrupt Exception, 
            which exception it really doesn't matter.*/
            Thread.sleep(this.executionTimeInterval);
        } catch (InterruptedException ex) {}
        /*Just to make sure that the threat is going to be stopped.*/
        this.mainlyThread.interrupt();
        if(this.needsRestart){
            this.resetSeries();
            this.needsRestart = false;
        }
    }
    
    private void executeClocksLogic() throws InterruptedException {
        while(this.isSimulationRunning){
            Thread.sleep(this.executionTimeInterval);
            synchronized(this.lockElement){
                if(this.clocksByExecutionTime.isEmpty()){
                    this.resetSeries();
                    continue;
                }
                this.startExecutionTime += this.executionTimeInterval;
                Iterator<Entry<Long, List<ClockInput>>> it = this.clocksByExecutionTime.entrySet().iterator();
                List<Integer> clocksIds = new ArrayList<Integer>();
                boolean anyToggled = false;
                while(it.hasNext()){
                    Entry<Long, List<ClockInput>> entry = it.next();
                    if(this.startExecutionTime > 0 && this.startExecutionTime % entry.getKey() == 0){
                        List<ClockInput> clocks = entry.getValue();
                        for (ClockInput clock : clocks) {
                            if(clock.isEnabled()){
                                clock.toggle();
                                anyToggled = true;
                            }
                            clocksIds.add(clock.getUniqueId());
                        }
                    }
                }
                this.makeSeriesChanges(clocksIds);
                if(anyToggled){
                    this.containerPanel.prepareRunWithoutAnalysis();
                    this.saveVariableValues(startExecutionTime);
                }
            }
        }
    }
    
    public void updateClockInterval(ClockInput clock, long oldTime){
        synchronized(this.lockElement){
            List<ClockInput> clocksList = this.clocksByExecutionTime.get(oldTime);
            clocksList.remove(clock);
            if(clocksList.isEmpty())
                this.clocksByExecutionTime.remove(oldTime);
            addClockLogic(clock);
        }
    }
    
    public void addClock(ClockInput clockInput){
        synchronized(this.lockElement){
            if(this.clocksChartLogic != null)
                this.clocksChartLogic.addClockSeries(clockInput);
            this.addClockLogic(clockInput);
        }
    }

    private void addClockLogic(ClockInput clockInput) {
        Long timer = clockInput.getTimerInMiliSeconds();
        if(this.clocksByExecutionTime.containsKey(timer)){
            List<ClockInput> clocksList = this.clocksByExecutionTime.get(timer);
            clocksList.add(clockInput);
        }else{
            List<ClockInput> clocksList = new ArrayList<ClockInput>();
            clocksList.add(clockInput);
            this.clocksByExecutionTime.put(timer, clocksList);
        }
    }

    private void saveVariableValues(long startExecutionTime) {
        // TODO
    }

    public int getNewUniqueId() {
        return uniqueIdCounter++;
    }

    public void setCustomClocksChart(CustomClocksChart customChart) {
        synchronized(this.lockElement){
            this.clocksChartLogic = customChart;
            this.clocksChartLogic.processClocks(clocksByExecutionTime.values());
        }
    }

    public void removeClock(ClockInput clockInput) {
        synchronized(this.lockElement){
            long timer = clockInput.getTimerInMiliSeconds();
            List<ClockInput> clocksList = this.clocksByExecutionTime.get(timer);
            clocksList.remove(clockInput);
            if(clocksList.isEmpty())
                this.clocksByExecutionTime.remove(timer);
            
            if(this.clocksChartLogic != null)
                this.clocksChartLogic.removeClockSeries(clockInput);
        }
    }
    
    public void makeSeriesChanges(List<Integer> clocksToggled){
        this.clocksChartLogic.updateClocksSeries(clocksToggled);
    }

    public void updateSeriesName(int uniqueId, String newName) {
        this.clocksChartLogic.changeSeriesName(uniqueId, newName);
    }

    private void resetSeries() {
        this.startExecutionTime = 0;
        this.clocksChartLogic.resetSeries();
    }

    public boolean seriesNameExist(String currentValue, int ignoreClockId) {
        return this.clocksChartLogic.seriesNameExists(currentValue, ignoreClockId);
    }
}
