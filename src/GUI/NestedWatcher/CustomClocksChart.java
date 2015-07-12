/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI.NestedWatcher;

import GUI.Watcher.WatchModelEntry;
import GUI.Watcher.WatchModelEntryDataLog;
import Simulation.Elements.Inputs.ClockInput;
import VerilogCompiler.Interpretation.ExpressionValue;
import VerilogCompiler.SemanticCheck.VariableInfo;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JScrollBar;
import javax.swing.border.CompoundBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

/**
 *
 * @author Franklin
 */
public class CustomClocksChart{
    
    XYSeriesCollection seriesCollection;
    XYItemRenderer chartRenderer;
    XYPlot chartPlot;
    NumberAxis domainAxis;
    SymbolAxis rangeAxis;
    Debugger debugger;
    
    List<String> rangeSymbols;
    JFreeChart chartElement;
    ChartPanel chartPanel;
    boolean scrolling = false;
    double domainMaxValue = 0;
    final double domainDisplayRange = 10;
    final double domainMaxValueDifference = 1;
    int increaseHeightValue = 0;
    double lastValidSelectedValue = 0;
    
    List<Integer> rangeAxisClocksPosition;
    
    Map<Integer, ClockChartInformation> clocksInformationByClockId;
    
    Map<Double, List<WatchModelEntryDataLog>> dataEntriesByRangeValue;
    private boolean needSaveVariables;
    
    public CustomClocksChart(Debugger debugger){
        this.initializeAndConfigureComponents();
        this.debugger = debugger;
    }
    
    public ChartPanel getCustomClocksChart(){
        return this.chartPanel;
    }
    
    private void initializeAndConfigureComponents() {
        this.clocksInformationByClockId = new HashMap<Integer, ClockChartInformation>();
        this.dataEntriesByRangeValue = new HashMap<Double, List<WatchModelEntryDataLog>>();
        this.rangeAxisClocksPosition = new ArrayList<Integer>();
        
        this.seriesCollection = new XYSeriesCollection();
        
        this.chartRenderer = new XYLineAndShapeRenderer(true, false); 
        
        this.domainAxis = new NumberAxis("Seconds");
        this.domainAxis.setRange(0, this.domainDisplayRange);
        this.domainAxis.setAutoTickUnitSelection(true);
        this.rangeAxis = new SymbolAxis(null, new String[]{});
        this.rangeSymbols = new ArrayList<String>();
        
        //Configuring Plot
        this.chartPlot = new XYPlot(seriesCollection,  this.domainAxis, this.rangeAxis, this.chartRenderer);
        this.chartPlot.setBackgroundPaint(Color.WHITE);
        this.chartPlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        this.chartPlot.setRangeGridlinePaint(Color.WHITE);
        this.chartPlot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
        this.chartPlot.setDomainCrosshairVisible(true);
        this.chartPlot.setDomainCrosshairPaint(Color.BLUE);
        this.chartPlot.setDomainCrosshairStroke(new BasicStroke(1.2f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND,
              1.0f, new float[] {6.0f, 6.0f}, 0.0f));
        this.chartPlot.setRangeCrosshairVisible(false);
        
        //Configuring Chart
        this.chartElement = this.createChart();
        
        //Configuring ChartPanel
        this.chartPanel = new ChartPanel(chartElement){
            @Override
            public void restoreAutoBounds() {
                super.restoreAutoBounds();
                scrolling = false;
                updateDomainRange();
            }

            @Override
            public void zoom(Rectangle2D selection) {
                scrolling = true;
                super.zoom(selection);
            }
        };
        chartPanel.setDomainZoomable(true);
        chartPanel.setRangeZoomable(false);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getScrollType() != MouseWheelEvent.WHEEL_UNIT_SCROLL) return;
                scrolling = true;
            }
        });
        CompoundBorder compoundborder = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4), BorderFactory.createEtchedBorder());
        chartPanel.setBorder(compoundborder);
        Dimension prefSize = this.chartPanel.getPreferredSize();
        this.chartPanel.setPreferredSize(new Dimension(prefSize.width  / 2, prefSize.height /2 - 2));
        this.increaseHeightValue = prefSize.height / 4;
    }
    
    private JFreeChart createChart(){
        JFreeChart chart = new JFreeChart(null, new Font("Tahoma", 0, 18), this.chartPlot, true);
        chart.setBackgroundPaint(Color.WHITE);
        chart.addProgressListener(new ChartProgressListener() {

            @Override
            public void chartProgress(ChartProgressEvent chartEvent) {
                if (chartEvent.getType() != 2)
                    return;
                double xValue = chartPlot.getDomainCrosshairValue();
                if(lastValidSelectedValue == xValue)
                    return;
                if(!dataEntriesByRangeValue.containsKey(xValue)){
                    chartPlot.setDomainCrosshairValue(lastValidSelectedValue);
                    return;
                }
                lastValidSelectedValue = xValue;
                List<WatchModelEntryDataLog> entries = dataEntriesByRangeValue.get(xValue);
                debugger.changeLogWatchEntries(entries);
            }
        });
        return chart;
    }
    
    private void updateDomainRange() {
        double value;
        if(this.domainMaxValue > this.domainDisplayRange && !scrolling){
            value = this.domainMaxValue - this.domainDisplayRange;
            this.domainAxis.setRange(value, this.domainMaxValue + this.domainMaxValueDifference);
        }
    }

    public void addClockSeries(ClockInput clockInput) {
        int uniqueId = clockInput.getUniqueId();
        String userReferenceName = clockInput.getUserReference();
        
        //Adding new values at Range Axis.
        int rangeIndex = 0;
        this.rangeSymbols.add(0, "");
        this.rangeSymbols.add(0, userReferenceName);
        this.rangeSymbols.add(0, "");
        updateRangeAxis();
        this.rangeAxisClocksPosition.add(0, uniqueId);
        
        //Creating new Series
        XYSeries newDomainSeries = new XYSeries(userReferenceName);
        int firstRangeElement = rangeIndex * 3; 
        int lastRangeElement = firstRangeElement + 2;
        double xValue = this.domainMaxValue;
        double yValue = clockInput.isOpen ? firstRangeElement : lastRangeElement;
        newDomainSeries.add(xValue, yValue);
        this.seriesCollection.addSeries(newDomainSeries);
        
        ClockChartInformation clockInfo = new ClockChartInformation(clockInput);
        clockInfo.setReferenceName(userReferenceName);
        clockInfo.setDomainSerie(newDomainSeries);
        clockInfo.setMaxValue(this.domainMaxValue);
        this.clocksInformationByClockId.put(uniqueId, clockInfo);
        this.recalculateRangeIndex();
        this.adjustChartHeight(true);
    }

    private void updateRangeAxis() {
        this.rangeAxis = new SymbolAxis(null, this.rangeSymbols.toArray(new String[this.rangeSymbols.size()]));
        this.chartPlot.setRangeAxis(this.rangeAxis);
    }

    public void removeClockSeries(ClockInput clockInput) {
        int uniqueId = clockInput.getUniqueId();
        this.removeClock(uniqueId);
    }

    public void processClocks(Collection<List<ClockInput>> values) {
        for(List<ClockInput> clocks : values){
            for(ClockInput clock: clocks){
                this.addClockSeries(clock);
            }
        }
    }

    private void removeClock(int uniqueId) {
        if(!this.clocksInformationByClockId.containsKey(uniqueId))
            return;
        
        //Removing Series
        ClockChartInformation clockInfo = this.clocksInformationByClockId.get(uniqueId);
        this.seriesCollection.removeSeries(clockInfo.getDomainSerie());
        
        //Removing Range Axis Info
        int rangeIndex = clockInfo.getRangeIndex();
        int firstElement = rangeIndex * 3;
        this.rangeSymbols.remove(firstElement);
        this.rangeSymbols.remove(firstElement);
        this.rangeSymbols.remove(firstElement);
        this.updateRangeAxis();
        this.rangeAxisClocksPosition.remove(rangeIndex);
        
        //Removing map
        this.clocksInformationByClockId.remove(uniqueId);
        this.recalculateRangeIndex();
        this.adjustChartHeight(false);
    }

    public void updateClocksSeries(List<Integer> clocksToggled) {
        for(int clockUniqueId : clocksToggled){
            this.updateClockSerie(clockUniqueId);
        }
    }

    private void updateClockSerie(int clockUniqueId) {
        if(!this.clocksInformationByClockId.containsKey(clockUniqueId))
            return;
        
        ClockChartInformation clockInfo = this.clocksInformationByClockId.get(clockUniqueId);
        int rangeIndex = clockInfo.getRangeIndex();
        int bottomRangeIndex = rangeIndex*3;
        int topRangeIndex = bottomRangeIndex + 2;
        ClockInput clock = clockInfo.getClock();
        
        double xValue;
        xValue = clockInfo.getMaxValue() + ((double)clock.getTimerInMiliSeconds()) / 1000;
        clockInfo.setMaxValue(xValue);
        if(xValue > this.domainMaxValue)
            this.domainMaxValue = xValue;
        
        XYSeries serie = clockInfo.getDomainSerie();
        
        if(clock.isOpen){
            if(clock.isEnabled()){
                this.needSaveVariables = true;
                serie.add(xValue, topRangeIndex);
            }
            serie.add(xValue, bottomRangeIndex);
        }else{
            if(clock.isEnabled()){
                this.needSaveVariables = true;
                serie.add(xValue, bottomRangeIndex);
            }
            serie.add(xValue, topRangeIndex);
        } 
        this.updateDomainRange();
    }

    public void changeSeriesName(int uniqueId, String newName) {
        ClockChartInformation clockInfo = this.clocksInformationByClockId.get(uniqueId);
        int clockRangeindex = clockInfo.getRangeIndex();
        int bottomRangeIndex = clockRangeindex * 3;
        this.rangeSymbols.set(bottomRangeIndex + 1, newName);
        this.updateRangeAxis();
        clockInfo.setReferenceName(newName);
    }

    public void resetSeries() {
        this.domainMaxValue = 0;
        this.domainAxis.setRange(0,this.domainDisplayRange);
        for(ClockChartInformation clock :this.clocksInformationByClockId.values()){
            XYSeries series = clock.getDomainSerie();
            series.clear();
            int rangeIndex = clock.getRangeIndex();
            int bottomIndex = rangeIndex * 3;
            int yValue = clock.getClock().isOpen ? bottomIndex : bottomIndex + 2;
            series.add(domainMaxValue, yValue);
            clock.setMaxValue(domainMaxValue);
            this.dataEntriesByRangeValue.clear();
        }
    }

    private void recalculateRangeIndex() {
        for(int i = 0; i < this.rangeAxisClocksPosition.size(); i++){
            int uniqueId = this.rangeAxisClocksPosition.get(i);
            this.clocksInformationByClockId.get(uniqueId).setRangeIndex(i);
        }
    }

    public boolean seriesNameExists(String seriesName, int ignoreClockId) {
        for(ClockChartInformation clockInfo : this.clocksInformationByClockId.values()){
            if(clockInfo.getClock().getUniqueId() != ignoreClockId && clockInfo.getUserReferenceName().equals(seriesName))
                return true;
        }
        return false;
    }

    private void adjustChartHeight(boolean add) {
        int result, symbol;
        if(add){
            result = 0;
            symbol = 1;
        }else{
            result = 2;
            symbol = -1;
        }
        
        if(this.rangeAxisClocksPosition.size() % 3 != result)
            return;
        Dimension prefSize = this.chartPanel.getPreferredSize();
        this.chartPanel.setPreferredSize(new Dimension(prefSize.width, prefSize.height + (increaseHeightValue * symbol)));
        this.chartPanel.revalidate();
        this.debugger.pack();
        JScrollBar vertical = debugger.getCustomChartScrollPane().getVerticalScrollBar();
        vertical.setValue( vertical.getMaximum());
    }

    public void saveVariablesIfNeeded(ArrayList<WatchModelEntry> modelData) {
        if(!this.needSaveVariables)
            return;
        
        this.needSaveVariables = false;
        double currentMax = this.domainMaxValue;
        if(dataEntriesByRangeValue.containsKey(currentMax))
            return;
        
        List<WatchModelEntryDataLog> logEntries = new ArrayList<WatchModelEntryDataLog>();
        for(WatchModelEntry entry : modelData){
            if(entry.chip == null)
                continue;
            String userReference = entry.chip.getUserReference();
            String moduleId = entry.moduleInstanceId;
            String variableName = entry.variableName;
            VariableInfo varInfo = this.debugger.simulationScope.getVariableInfo(moduleId, variableName);
            if(varInfo == null)
                continue;
            
            if(varInfo.isArray){
                Object[] values = (Object[]) this.debugger.simulationScope.getVariableValue(moduleId, variableName).value;
                List<String> stringValues = new ArrayList<String>();
                for (Object val : values) {
                    if (val != null && !val.toString().matches("[xXzZ]"))
                        val = this.debugger.simulationScope.padWithZeros(val, varInfo.signalSize);
                    String stringValue = val == null ? "z" : val.toString();
                    stringValues.add(stringValue);
                }
                logEntries.add(new WatchModelEntryDataLog(variableName, stringValues, userReference));
            }else{
                ExpressionValue infoValue = this.debugger.simulationScope.getVariableValue(moduleId, variableName);
                Object val = infoValue == null ? null : infoValue.getValueAsString();
                String stringValue = val == null ? "z" : this.debugger.simulationScope.getFormattedValue(moduleId, variableName);
                logEntries.add(new WatchModelEntryDataLog(variableName, stringValue, userReference));
            }
        }
        this.dataEntriesByRangeValue.put(currentMax, logEntries);
    }
}
