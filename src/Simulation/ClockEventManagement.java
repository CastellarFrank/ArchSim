/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation;

import GUI.ContainerPanel;
import Simulation.Elements.Inputs.ClockInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Franklin
 */
public class ClockEventManagement {
    Map<Long, List<ClockInput>> clocksByExecutionTime;
    Thread mainlyThread;
    boolean isSimulationRunning = false;
    long startExecutionTime = 0;
    long executionTimeInterval = 100;
    public final Object lockElement = new Object();
    ContainerPanel containerPanel;
    
    public ClockEventManagement(ContainerPanel containerPanel){
        this.clocksByExecutionTime = new HashMap<Long, List<ClockInput>>();
        this.containerPanel = containerPanel;
    }
    
    public void simulationReseted(){
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
                    Logger.getLogger(ClockEventManagement.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Clock Failed, This shouldn't happened.");
                }
            }
        });
        this.mainlyThread.start();
    }
    
    public void simulationStopped(){    
        this.isSimulationRunning = false;
        this.mainlyThread.interrupt();
    }
    
    private void executeClocksLogic() throws InterruptedException {
        while(this.isSimulationRunning){
            Thread.sleep(this.executionTimeInterval);
            synchronized(this.lockElement){
                this.startExecutionTime += this.executionTimeInterval;
                Iterator<Entry<Long, List<ClockInput>>> it = this.clocksByExecutionTime.entrySet().iterator();
                boolean anyWasToggled = false;
                while(it.hasNext()){
                    Entry<Long, List<ClockInput>> entry = it.next();
                    if(this.startExecutionTime % entry.getKey() == 0){
                        List<ClockInput> clocks = entry.getValue();
                        for (int i = 0; i < clocks.size(); i++) {
                            ClockInput clock = clocks.get(i);
                            if(clock.isEnabled()){
                                anyWasToggled = true;
                                clock.manualToggle();
                            }else{
                                clock.setElementBeenManagementStatus(false);
                                clocks.remove(clock);
                                i--;
                            }
                        }
                    }
                }
                if(anyWasToggled){
                    this.saveVariableValues(startExecutionTime);
                    this.containerPanel.prepareForAnalysis();
                }
            }
        }
        this.startExecutionTime = 0;
    }
    
    public void updateClockInterval(ClockInput clock, long oldTime){
        synchronized(this.lockElement){
            List<ClockInput> clocksList = this.clocksByExecutionTime.get(oldTime);
            clocksList.remove(clock);
            addClockLogic(clock);
        }
    }
    
    public void addClock(ClockInput clockInput){
        synchronized(this.lockElement){
            addClockLogic(clockInput);
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
}
