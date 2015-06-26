/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulation.Elements.Inputs;

import Exceptions.ArchException;
import GUI.ContainerPanel;
import GUI.Edit.ClockInputNameTypeHandler;
import GUI.Edit.EditInfo;
import GUI.Edit.InputTypeHandler;
import GUI.Edit.NumberInputTypeHandler;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import static Simulation.Elements.BaseElement.drawThickLine;
import static Simulation.Elements.BaseElement.fontSimulationNumberType;
import static Simulation.Elements.BaseElement.inputElementDescriptionColor;
import static Simulation.Elements.BaseElement.pointRadious;
import static Simulation.Elements.BaseElement.selectionSeparationMargin;
import static Simulation.Elements.BaseElement.sign;
import Simulation.Elements.BasicSwitch;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JCheckBox;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ClockInput extends BasicSwitch {
    int separationBetweenText = 10;
    long timerInMiliSeconds = 1000;
    boolean enabled = true;
    boolean isElementBeenManagement = false;
    String userReference;
    int uniqueId;
    public ClockInput(int x, int y) throws ArchException {
        super(x, y);
        this.isOpen = true;
    }

    public ClockInput(int x, int y, int x2, int y2, String[] extraParams) throws ArchException {
        super(x, y, x2, y2, extraParams);
        this.isOpen = true;
        this.enabled = Boolean.parseBoolean(extraParams[1]);
        this.timerInMiliSeconds = Long.parseLong(extraParams[2]);
        this.userReference = extraParams[2];
    }

    public ClockInput(int x, int y, int x2, int y2, int flags) throws ArchException {
        super(x, y, x2, y2, flags);
        this.isOpen = true;
    }
    
    @Override
    public void toggle() {
    }
    
    public void manualToggle(){
        this.isOpen = !isOpen;
    }
    
    @Override
    public void setPoints() {
        super.setPoints();        
        lead1 = point2;
        lead1.x -= (separationBetweenText * sign(dx));
    }
    
    @Override
    public int getPostCount() { return 1; }

    @Override
    public void draw(Graphics g) {
        Font f = fontSimulationNumberType;
        g.setFont(f);
        g.setColor(needsHighlight() ? BaseElement.selectedColor : BaseElement.defaultColor);
        String text = isOpen ? "L" : "H";
        if (Configuration.LOGIC_VALUES_AS_NUMBER) {
            text = isOpen ? "0" : "1";
        }
        setBbox(point1, lead1, 0);
        drawText(g, text);
        setVoltageColor(g, voltages[0]);
        Point extra1 = new Point(point1.x, lead1.y);
        drawThickLine(g, point1, extra1);
        drawThickLine(g, extra1, lead1);
        drawPosts(g);
        drawDescriptionElementText(g, "(" + this.userReference + ")", separationBetweenText, inputElementDescriptionColor);
    }
    
    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    @Override
    public void doStep() {
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 0){
            return new EditInfo("value", 0, 0, 0, false)
                       .addComponent(new JCheckBox("Enabled", this.enabled));
        }else if(n == 1){
            return new EditInfo("value", new NumberInputTypeHandler(String.valueOf(this.timerInMiliSeconds)));
        }else if(n == 2){
            return new EditInfo("value", new ClockInputNameTypeHandler(userReference, 
                                                                       this.containerPanel.clockEventManagement,
                                                                       this.uniqueId));
        }
        return null;
    }

    @Override
    public void setEditValue(int n, EditInfo editInfo) {
        if(n == 0){
            this.enabled = !editInfo.value.equals("0");
        }else if(n == 1){
            InputTypeHandler input = editInfo.getInputTypeHandler();
            long oldTime = this.timerInMiliSeconds;
            this.timerInMiliSeconds = input.getCurrentAsDecimal();
            this.checkManagement(oldTime);
        }else if(n == 2){
            InputTypeHandler input = editInfo.getInputTypeHandler();
            String newName = input.getCurrent();
            if(newName == null || newName.isEmpty())
                newName = "Clock " + this.uniqueId;
            if(!this.userReference.equals(newName))
                this.containerPanel.clockEventManagement.updateSeriesName(this.uniqueId, newName);
            this.userReference = newName;
        }
    }
    
    
    
    @Override
    public Element getXmlElement(Document document) {
        Element element = super.getXmlElement(document);
        element.setAttribute("type", ClockInput.class.getName());
        
        //Is Enabled
        Element extraParam0 = document.createElement("param");
        extraParam0.setTextContent(String.valueOf(this.enabled));
        element.appendChild(extraParam0);
        
        //Milliseconds
        extraParam0 = document.createElement("param");
        extraParam0.setTextContent(Long.toString(this.timerInMiliSeconds));
        element.appendChild(extraParam0);
        
        //Name
        extraParam0 = document.createElement("param");
        extraParam0.setTextContent(this.userReference);
        element.appendChild(extraParam0);
        
        return element;
    }

    @Override
    public boolean isPostOutput(int index) {
      return index == 0;
    }
    
    @Override
    public void stampVoltages() {
        double newVoltage;
        String multibit;
        if (isOpen) { 
            newVoltage = Configuration.LOGIC_0_VOLTAGE;
            multibit = "0";
        } else {
            newVoltage = Configuration.LOGIC_1_VOLTAGE * 2;
            multibit = "1";
        }
        containerPanel.stampVoltageSource(0, joints[0], voltageSourceReference, newVoltage, multibit);
    }
    
    @Override
    public boolean collidesWith(int x, int y) {
        Point minorX = new Point(point1.x > lead1.x ? lead1 : point1);
        Point minorY = new Point(lead1.y > point1.y ? point1 : lead1);
        
        if(sign(dx) == 1)
            minorX.x -= selectionSeparationMargin;
        
        int heightPointReduction;
        if(sign(dy) == 1){
            heightPointReduction = (pointRadious / 2);
            minorY.y += heightPointReduction;
        }else{
            heightPointReduction = (selectionSeparationMargin + (pointRadious / 2));
        }
        
        Rectangle horizontalRect = new Rectangle(minorX.x, 
                                                 lead1.y - selectionSeparationMargin, 
                                                 boundingBox.width + selectionSeparationMargin,
                                                 selectionSeparationMargin * 2 + 1);
        
        Rectangle verticalRect = new Rectangle(point1.x - selectionSeparationMargin,
                                               minorY.y, 
                                               selectionSeparationMargin * 2 + 1,
                                               boundingBox.height + selectionSeparationMargin - heightPointReduction);
        
        return verticalRect.contains(x, y) || horizontalRect.contains(x, y);
    }
    
    @Override
    public void selectRect(Rectangle r) {
        Point minorX = new Point(point1.x > lead1.x ? lead1 : point1);
        Point minorY = new Point(lead1.y > point1.y ? point1 : lead1);
        
        if(sign(dx) == 1)
            minorX.x -= selectionSeparationMargin;
        
        int heightPointReduction;
        if(sign(dy) == 1){
            heightPointReduction = (pointRadious / 2);
            minorY.y += heightPointReduction;
        }else{
            heightPointReduction = (selectionSeparationMargin + (pointRadious / 2));
        }
        
        Rectangle horizontalRect = new Rectangle(minorX.x, 
                                                 lead1.y - selectionSeparationMargin, 
                                                 boundingBox.width + selectionSeparationMargin,
                                                 selectionSeparationMargin * 2 + 1);
        
        Rectangle verticalRect = new Rectangle(point1.x - selectionSeparationMargin,
                                               minorY.y, 
                                               selectionSeparationMargin * 2 + 1,
                                               boundingBox.height + selectionSeparationMargin - heightPointReduction);
        
        boolean tempSelected = verticalRect.intersects(r) || horizontalRect.intersects(r);
        this.setSelected(tempSelected);
    }
    
    @Override
    public void setContainerPanel(ContainerPanel containerPanel) {
        this.containerPanel = containerPanel;
        this.uniqueId = this.containerPanel.clockEventManagement.getNewUniqueId();
        if(this.userReference == null || this.userReference.isEmpty())
            this.userReference = "Clock " + this.uniqueId;
        setupInitialManagement();
    }

    public long getTimerInMiliSeconds() {
        return timerInMiliSeconds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isElementBeenManagement() {
        return isElementBeenManagement;
    }
    
    public void setElementBeenManagementStatus(boolean isElementBeenManagement){
        this.isElementBeenManagement = isElementBeenManagement;
    }

    private void setupInitialManagement() {
        this.isElementBeenManagement = true;
        this.containerPanel.clockEventManagement.addClock(this);
    }

    private void checkManagement(long oldTime) {
        if(this.isElementBeenManagement ){
            if(oldTime == this.timerInMiliSeconds)
                return;
            this.containerPanel.clockEventManagement.updateClockInterval(this, oldTime);
        }else{
            this.isElementBeenManagement = true;
            this.containerPanel.clockEventManagement.addClock(this);
        }
    }

    public int getUniqueId() {
        return this.uniqueId;
    }

    public String getUserReference() {
        return this.userReference;
    }
}
