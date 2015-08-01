/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import DataStructures.CircuitGenerator;
import DataStructures.ModuleRepository;
import GUI.NestedWatcher.CustomTreeModel;
import GUI.NestedWatcher.Debugger;
import GUI.Watcher.WatchesTableModel;
import Simulation.ClockEventManagement;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Elements.Inputs.ClockInput;
import Simulation.Elements.ModuleChip;
import Simulation.Elements.Wire;
import Simulation.Joint;
import Simulation.JointReference;
import Simulation.RowInfo;
import Simulation.RowType;
import Simulation.SimulationFactory;
import Utils.MatrixUtils;
import VerilogCompiler.Interpretation.SimulationScope;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Main class related to rendering this simulator canvas.
 *
 * @author Néstor A. Bermúdez < nestor.bermudezs@gmail.com >
 */
public class ContainerPanel extends JCanvas {
    //<editor-fold defaultstate="collapsed" desc="Attributes">

    /**
     * <
     * code>BaseElement</code> that is being added *
     */
    public BaseElement newElementBeenDrawn;
    /**
     * <
     * code>BaseElement</code> that is under the mouse cursor *
     */
    public BaseElement mouseComponent;
    /**
     * Tells if the simulation is paused*
     */
    public boolean isPaused = true;
    /**
     * <code>SimulationScope</code> used to run this only if its a
     * <code>SimulationCanvas</code>*
     */
    public SimulationScope simulationScope;
    
    public ClockEventManagement clockEventManagement;
    
    public int highestPropagationLevel;
    /**
     * <
     * code>WatchesTableModel</code> used to feed a
     * <code>Watches</code> window *
     */
    public WatchesTableModel watchesTableModel = null;
    public CustomTreeModel debuggerModel = null;
    public Debugger debugger;
    
    protected Vector<BaseElement> elements;
    protected BaseElement[] voltageSourceElements;
    protected Vector<Joint> joints;
    protected Vector<RowInfo> rowsInfo;
    protected boolean dragging = false, mapForStamp = false;
    protected int dragX, dragY, initDragX, initDragY, matrixSize, matrixFullSize;
    protected int gridSize, gridMask, gridRound;
    protected double temporalVoltageMatrix[][], speed = 172.0;
    protected String temporalMultibitsMatrix[][];
    protected int circuitPermute[];
    protected double temporalRightSideVoltages[], rightSideVoltages[];
    protected String temporalRightSideMultibits[], rightSideMultibitsValues[];
    protected int draggingPost = -1;
    protected Rectangle selectedArea = null;
    protected MouseMode defaultMouseMode = MouseMode.SELECT, currentMouseMode = MouseMode.SELECT;
    protected boolean needsAnalysis = false, needsJointsAnalyze = false;
    protected boolean runnable = true, refreshModules;
    protected boolean runWithoutAnalysis;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Methods">
    /**
     * Returns a
     * <code>BaseElement</code> given an index
     *
     * @param index position to be returned.
     * @return <code>BaseElement</code> if in range, <code>null</code> otherwise
     */
    public BaseElement getElement(int index) {
        return this.elements.get(index);
    }

    /**
     * Returns a
     * <code>Joint</code> given an index
     *
     * @param index position to be returned.
     * @return <code>Joint</code> if in range, <code>null</code> otherwise
     */
    public Joint getJoint(int index) {
        if (index >= joints.size()) {
            return null;
        }
        return joints.elementAt(index);
    }

    /**
     * Calculates a distance (before taking square root) between two pair of
     * (x,y) coordinates
     *
     * @param x1 point A's x coordinate
     * @param y1 point A's y coordinate
     * @param x2 point B's x coordinate
     * @param y2 point B's y coordinate
     * @return distance before square root
     */
    public int distance(int x1, int y1, int x2, int y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    /**
     * Initializes this
     * <code>ContainerPanel</code>
     */
    public void init() {
        setGrid();
    }

    /**
     * When dragging mouse, a selection rectangule is displayed, this method
     * tries to set
     * <code>BaseElement</code>s selected if inside this area.
     *
     * @param x second point's x coordinate
     * @param y second point's y coordinate
     */
    public void selectElementsOnArea(int x, int y) {
        int x1 = Math.min(x, initDragX);
        int x2 = Math.max(x, initDragX);
        int y1 = Math.min(y, initDragY);
        int y2 = Math.max(y, initDragY);
        selectedArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);
        int i;
        for (i = 0; i != elements.size(); i++) {
            BaseElement element = elements.elementAt(i);
            element.selectRect(selectedArea);
        }
    }

    /**
     * It sets every
     * <code>BaseElement</code> in this panel as not selected.
     */
    public void clearSelection() {
        for (BaseElement baseElement : elements) {
            baseElement.setSelected(false);
        }
        selectedArea = null;
    }

    /**
     * Main method used to generate a DOM
     * <code>Element</code> usually used to generate a XML file with it.
     *
     * @param document a initialized DOM document
     * @return a root <code>Element</code> containing      * every <code>BaseElement</code>'s XML representation.
     */
    public Element toXmlRootElement(Document document) {
        Element root = document.createElement("elements");
        for (BaseElement element : elements) {
            root.appendChild(element.getXmlElement(document));
        }
        return root;
    }

    /**
     * It constructs a
     * <code>BaseElement</code> then it adds it to this
     * <code>ContainerPanel</code> param type the full class name of the element
     * to be constructed.
     *
     * @param x starting x coordinate.
     * @param y starting y coordinate.
     * @param x2 ending x coordinate.
     * @param y2 ending y coordinate.
     * @param extraParams optional extra parameters like number of inputs on an
     * AndGate.
     */
    public void constructAndAddElement(String type,
            int x, int y, int x2, int y2, String[] extraParams) {
        addElement(constructElement(type, x, y, x2, y2, extraParams));
    }
    //</editor-fold>

    /**
     * Method used to print on standard output voltages array and right side
     * voltages
     */
    public void printThings() {
        if (Configuration.DEBUG_MODE && false) {
            if (temporalRightSideVoltages != null) {
                System.out.println("RIGHT SIDE VOLTAGES!!! COUNT: " + temporalRightSideVoltages.length);
                for (int i = 0; i < temporalRightSideVoltages.length; i++) {
                    double b = temporalRightSideVoltages[i];
                    System.out.print(b + ",");
                }
                System.out.println("\nEND OF RIGHT SIDE VOLTAGES");
            }
            if (temporalRightSideMultibits != null) {
                for (int i = 0; i < temporalRightSideMultibits.length; i++) {
                    String value = temporalRightSideMultibits[i];
                    System.out.println("value " + value);
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color old = g.getColor();
        
        if (isPaused)
            g.setColor(Configuration.BACKGROUND_COLOR);
        else
            g.setColor(Color.WHITE);
        //super.paintComponent(g);        
        
        g.fillRect(-this.getX(), -this.getY(), getWidth() * 2, getHeight() * 2);
        
        

        int spacing = 16;
        int height = getHeight() / spacing;
        int width = getWidth() / spacing;
        
        if (Configuration.DRAW_DOTTED_BG) {
            background = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = background.createGraphics();
            g2d.setColor(Color.WHITE.darker());
            for (int count = 0; count <= width; count++) {
                int x = (int) Math.round(count * spacing);
                int y = 0;
                for (int c2 = 0; c2 <= height; c2++) {
                    y = (int) Math.round(c2 * spacing);
                    g2d.drawRect(x, y, 1, 1);
                }
            }
            g2d.dispose();


            g2d = (Graphics2D) g.create();
            int x = (getWidth() - background.getWidth()) / 2;
            int y = (getHeight() - background.getHeight()) / 2;
            g2d.drawImage(background, x, y, this);
            g2d.dispose();
        }
        
        g.setColor(Color.RED.darker());
        
        g.setColor(old);
        updatePreview(g);
    }

    @Override
    public void update(Graphics g) {
        updatePreview(g);
    }

    private void initializeArray(String[] array, String defValue) {
        for (int i = 0; i < array.length; i++) {
            array[i] = defValue;
        }
    }

    /**
     * Method that analyzes the circuit when it changes. It does this things:
     * <ul> <li>updates joints of elements</li> <li>stamp voltages on a matrix
     * to analyze</li> <li>delete unused elements</li> <li>reduces matrix
     * size</li> </ul>
     */
    
    protected void analyze() {
        this.setJointsAndVoltageSourcesReferences();
        this.prepareAndCalculateMatrixValues();
    }

    //<editor-fold defaultstate="collapsed" desc="LU">
    boolean lu_factor(double a[][], int size, int pivot[]) {
        double scaleFactors[];
        int i, j, k;

        scaleFactors = new double[size];

        // divide each row by its largest element, keeping track of the
        // scaling factors
        for (i = 0; i != size; i++) {
            double largest = 0;
            for (j = 0; j != size; j++) {
                double x = Math.abs(a[i][j]);
                if (x > largest) {
                    largest = x;
                }
            }
            // if all zeros, it's a singular matrix
            if (largest == 0) {
                return false;
            }
            scaleFactors[i] = 1.0 / largest;
        }

        // use Crout's method; loop through the columns
        for (j = 0; j != size; j++) {

            // calculate upper triangular elements for this column
            for (i = 0; i != j; i++) {
                double q = a[i][j];
                for (k = 0; k != i; k++) {
                    q -= a[i][k] * a[k][j];
                }
                a[i][j] = q;
            }

            // calculate lower triangular elements for this column
            double largest = 0;
            int largestRow = -1;
            for (i = j; i != size; i++) {
                double q = a[i][j];
                for (k = 0; k != j; k++) {
                    q -= a[i][k] * a[k][j];
                }
                a[i][j] = q;
                double x = Math.abs(q);
                if (x >= largest) {
                    largest = x;
                    largestRow = i;
                }
            }

            // pivoting
            if (j != largestRow) {
                double x;
                for (k = 0; k != size; k++) {
                    x = a[largestRow][k];
                    a[largestRow][k] = a[j][k];
                    a[j][k] = x;
                }
                scaleFactors[largestRow] = scaleFactors[j];
            }

            // keep track of row interchanges
            pivot[j] = largestRow;

            // avoid zeros
            if (a[j][j] == 0.0) {
                System.out.println("avoided zero");
                a[j][j] = 1e-18;
            }

            if (j != size - 1) {
                double mult = 1.0 / a[j][j];
                for (i = j + 1; i != size; i++) {
                    a[i][j] *= mult;
                }
            }
        }
        return true;
    }

    void lu_solve(double a[][], int n, int ipvt[], double b[]) {
        int i;

        // find first nonzero b element
        for (i = 0; i != n; i++) {
            int row = ipvt[i];

            double swap = b[row];
            b[row] = b[i];
            b[i] = swap;
            if (swap != 0) {
                break;
            }
        }

        int bi = i++;
        for (; i < n; i++) {
            int row = ipvt[i];
            int j;
            double tot = b[row];

            b[row] = b[i];
            // forward substitution using the lower triangular matrix
            for (j = bi; j < i; j++) {
                tot -= a[i][j] * b[j];
            }
            b[i] = tot;
        }
        for (i = n - 1; i >= 0; i--) {
            double tot = b[i];

            // back-substitution using the upper triangular matrix
            int j;
            for (j = i + 1; j != n; j++) {
                tot -= a[i][j] * b[j];
            }
            b[i] = tot / a[i][i];
        }
    }
    //</editor-fold>

    /**
     * It initializes the list with a given number of elements
     *
     * @param rowCount number of rows to be in the list
     */
    protected void initializeRows(int rowCount) {
        rowsInfo = new Vector<RowInfo>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            rowsInfo.add(new RowInfo());
        }
    }

    /**
     * Method that executes the behaviour of every
     * <code>BaseElement</code> in this container.<p> For example: if the
     * baseElement is a
     * <code>ModuleChip</code> instance it executes its module associated logic.
     */
    public void runStep() {
        if (elements == null || elements.isEmpty() || !runnable) {
            return;
        }
        
        System.arraycopy(temporalRightSideVoltages, 0, rightSideVoltages, 0,
                temporalRightSideVoltages.length);

        System.arraycopy(temporalRightSideMultibits, 0, rightSideMultibitsValues, 0,
                temporalRightSideMultibits.length);

        //<editor-fold defaultstate="collapsed" desc="Sub Iteration logic">

        if (!isPaused) {
            for (int i = 0; i < this.highestPropagationLevel; i++) {
                if(i == 0)
                    this.setJoinValues();
                //joining the values of the components in case there is new inputs values to be joined
                this.prepareAndCalculateMatrixValues();
                setJoinValues();
            }                    
        }

        for (BaseElement baseElement : elements) {
            baseElement.doStep();
            if(baseElement.needsPropagation()){
                for (int i = 0; i < this.highestPropagationLevel + 1; i++) {
                    this.prepareAndCalculateMatrixValues();
                    setJoinValues();
                }
            }
        }
        //</editor-fold>   
    }

    private void setJoinValues() {
        lu_solve(temporalVoltageMatrix, matrixSize, circuitPermute,
                temporalRightSideVoltages);
        for (int rowIndex = 0; rowIndex < matrixFullSize; rowIndex++) {
            RowInfo rowInfo = rowsInfo.get(rowIndex);
            double newVoltage;
            String multibits;
            if (rowInfo.type == RowType.CONSTANT) {
                newVoltage = rowInfo.voltage;
                multibits = rowInfo.multiBitsValue;
            } else {
                newVoltage = rightSideVoltages[rowInfo.mappedColumn];
                multibits = rightSideMultibitsValues[rowInfo.mappedColumn];
            }
            
            if (rowIndex < joints.size() - 1) {
                Joint joint = joints.elementAt(rowIndex + 1);
                for (JointReference jointRef : joint.references) {
                    jointRef.element.setVoltage(jointRef.postNumber, newVoltage);
                    jointRef.element.setMultiBitsValue(jointRef.postNumber, multibits);
                }
            }
            // else {
            //int newIndex = rowIndex - (joints.size() - 1);
            //voltageSourceElements[newIndex].setCurrent(newVoltage);
            //}
        } 
    }

    /**
     * Resumes execution.
     */
    public void resume() {
        isPaused = false;
        this.clockEventManagement.simulationStarted();
    }

    /**
     * Pauses execution.
     */
    public void pause() {
        isPaused = true;
        this.clockEventManagement.simulationStopped();
    }

    /**
     * Resets the execution.
     */
    public void reset() {
        isPaused = true;
        this.clockEventManagement.simulationReseted();
        for (BaseElement element : elements) {
            if (element instanceof ModuleChip) {
                ((ModuleChip)element).reset();
            }
        }
        this.prepareForAnalysis();
    }

    /**
     * Static method that prints to standard output just if its running in debug
     * mode.
     *
     * @param message message to print
     */
    public static void DEBUG(String message) {
        if (Configuration.DEBUG_MODE) {
            System.out.println(message);
        }
    }
    long secTime = 0;

    /**
     * It updates every element contained in this
     * <code>ContainerPanel</code>
     *
     * @param g graphics used to paint.
     */
    public void updatePreview(Graphics g) {
        boolean needsRun = needsAnalysis || this.runWithoutAnalysis;
        this.runWithoutAnalysis = false;
        if (needsAnalysis || needsJointsAnalyze) {
            this.analyze();
            needsAnalysis = false;
            needsJointsAnalyze = false;
        }
        
        printThings();
        
        if (!isPaused && needsRun) {
            ModuleRepository.registeringModuleLock.readLock().lock();
            try{
                runStep();
            }finally{
                ModuleRepository.registeringModuleLock.readLock().unlock();
            }
            //analyze();
            if (watchesTableModel != null) {
                watchesTableModel.updateValues();
                if(this.debugger != null){
                    this.debugger.saveVariablesIfNeeded(watchesTableModel.getModelData());
                }
            }
        }

        //System.out.println("After step");
        //printThings();

        for (BaseElement baseElement : elements) {
            baseElement.draw(g);
        }

        //<editor-fold defaultstate="collapsed" desc="Color bad or good connections">
        for (int jointIndex = 0; jointIndex < joints.size(); jointIndex++) {
            Joint joint = joints.elementAt(jointIndex);
            if(this.currentMouseMode != MouseMode.SELECT_DRAG && joint.isSelected())
                continue;
            
            if(joint.isError()){
                this.drawPostWithMargin(g, joint.coordX, joint.coordY, BaseElement.invalidPostColor);
            }else if (joint.references.size() == 1) {
                JointReference reference = joint.references.elementAt(0);
                for (BaseElement baseElement : elements) {
                    if (baseElement != reference.element
                            && baseElement.collidesWith(joint.coordX, joint.coordY)) {
                        this.drawPostWithMargin(g, joint.coordX, joint.coordY, BaseElement.collidePostColor);
                        break;
                    }
                }
            }else if(joint.references.size() >= 2){
                this.drawPostWithMargin(g, joint.coordX, joint.coordY, BaseElement.connectedPostColor);
            }
        }
        //</editor-fold>   

        if (newElementBeenDrawn != null
                && (newElementBeenDrawn.x != newElementBeenDrawn.x2 || newElementBeenDrawn.y != newElementBeenDrawn.y2)) {
            newElementBeenDrawn.draw(g);
        } else if (selectedArea != null) {
            Color old = g.getColor();
            g.setColor(Configuration.SELECT_RECT_COLOR);
            g.drawRect(selectedArea.x, selectedArea.y,
                    selectedArea.width, selectedArea.height);

            g.setColor(old);
        }
    }
    
    public void drawPostWithMargin(Graphics g, int x0, int y0, Color pointColor) {
        int tempPointRadious = BaseElement.pointRadious - 1;
        int pointMargin = tempPointRadious / 2;
        Color old = g.getColor();
        g.setColor(BaseElement.postColor);
        
        g.fillOval(x0 - pointMargin - 1, y0 - pointMargin - 1, tempPointRadious + 2, tempPointRadious + 2);
        g.setColor(pointColor);
        
        g.fillOval(x0 - pointMargin, y0 - pointMargin, tempPointRadious, tempPointRadious);
        g.setColor(old);
    }

    /**
     * It adds a
     * <code>BaseElement</code> to this container, before that, it sets
     * <code>element</code>'s containerPanel to this and snaps its coordinates
     * to the grid size.
     *
     * @param element element to be added.
     */
    public void addElement(BaseElement element) {
        if (element == null) {
            return;
        }
        if (element.containerPanel == null) {
            element.setContainerPanel(this);
        }
        element.x = snapGrid(element.x);
        element.y = snapGrid(element.y);
        element.x2 = snapGrid(element.x2);
        element.y2 = snapGrid(element.y2);
        element.setPoints();
        
        //remove
        if (!(element instanceof ModuleChip) && element.x == element.x2 && element.y == element.y2) {
            if(element instanceof ClockInput)
                clockEventManagement.removeClock((ClockInput)element);
            return;
        }
        if(element instanceof ModuleChip)
            ((ModuleChip)element).setNeedsRefresh(this.refreshModules);
        
        elements.add(element);
    }

    @Override
    public void removeAll() {
        elements.clear();
    }
    
    BufferedImage background;
    int dotCount = 100;

    /**
     * Constructor.
     */
    public ContainerPanel() {       
        elements = new Vector<BaseElement>(50);
        joints = new Vector<Joint>(100);
        simulationScope = SimulationFactory.createSimulationScope();
        this.clockEventManagement = new ClockEventManagement(this);

        BaseElement.defaultColor = Color.BLACK;
        BaseElement.whiteColor = Color.WHITE;
        BaseElement.selectedColor = new Color(255,215,0);
        BaseElement.lowSignalColor = new Color(255, 128, 0).darker();
        BaseElement.highSignalColor = Color.GREEN.brighter();
        BaseElement.highImpedanceSignalColor = Color.BLUE;
        BaseElement.unknownSignalColor = Color.RED;
        BaseElement.textColor = Color.DARK_GRAY;

        needsAnalysis = true;
    }

    /**
     * It sets the grid size.
     */
    protected void setGrid() {
        gridSize = Configuration.SMALL_GRID ? 8 : 16;
        gridMask = ~(gridSize - 1);
        gridRound = gridSize / 2 - 1;
    }

    /**
     * It rounds a value to the closest snapped grid number.
     *
     * @param x value to be snapped
     * @return snapped value
     */
    public int snapGrid(int x) {
        return (x + gridRound) & gridMask;
    }

    /**
     * Constructs a
     * <code>BaseElement</code> from given parameters.
     *
     * @param type the full class name of the element to be constructed.
     * @param x starting x coordinate.
     * @param y starting y coordinate.
     * @return a <code>BaseElement</code> instantiated through reflection.
     */
    public BaseElement constructElement(String type, int x, int y) {
        BaseElement element = CircuitGenerator.getInstance().constructElement(type,
                snapGrid(x), snapGrid(y));

        if (element != null) {
            element.setContainerPanel(this);
        }
        return element;
    }

    /**
     * Constructs a
     * <code>BaseElement</code> from given parameters.
     *
     * @param type the full class name of the element to be constructed.
     * @param x starting x coordinate.
     * @param y starting y coordinate.
     * @param x2 ending x coordinate.
     * @param y2 ending y coordinate.
     * @param extraParams optional extra parameters like number of inputs on an
     * AndGate.
     * @return a <code>BaseElement</code> instantiated through reflection.
     */
    public BaseElement constructElement(String type,
            int x, int y, int x2, int y2, String[] extraParams) {

        BaseElement element = CircuitGenerator.getInstance().constructElement(type,
                x, y, x2, y2, extraParams);
        if (element != null) {
            element.setContainerPanel(this);

        }
        return element;
    }

    /**
     * Moves the closest component's post so the element being dragged changes
     * it size.
     *
     * @param x x coordinate to be moved
     * @param y y coordinate to be moved
     */
    public void dragPost(int x, int y) {
        if (draggingPost == -1) {
            int distance1 = distance(mouseComponent.x, mouseComponent.y, x, y);
            int distance2 = distance(mouseComponent.x2, mouseComponent.y2, x, y);
            draggingPost =
                    (distance1 > distance2) ? 1 : 0;
        }
        int dx = x - dragX;
        int dy = y - dragY;
        
        if(dx == 0 && dy == 0)
            return;
        
        int symbol = draggingPost == 0 ? 1 : -1;
        
        
        if(mouseComponent.x + dx * symbol == mouseComponent.x2 
                && mouseComponent.y + dy * symbol == mouseComponent.y2){
            if(dx != 0)
                dx += BaseElement.sign(dx) * (gridMask * BaseElement.sign(gridMask));
            if(dy != 0)
                dy += BaseElement.sign(dy) * (gridMask * BaseElement.sign(gridMask));
        }
        mouseComponent.movePoint(draggingPost, dx, dy);
        prepareForAnalysis();
    }

    /**
     * Returns the index of the
     * <code>element</code> inside
     * <code>elements</code> list.
     *
     * @param element element to be indexed
     * @return index of the given element
     */
    public int getElementPosition(BaseElement element) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.elementAt(i) == element) {
                return i;
            }
        }
        return -1;
    }

    /**
     * It moves every selected element to the given x and y coordinates.
     *
     * @param x target x coordinate
     * @param y target y coordinate
     * @return <code>true</code> if at least one element was      * moved, <code>false</code> otherwise
     */
    public boolean dragSelected(int x, int y) {
        boolean mouseElementSelected = false;
        if (mouseComponent != null && !mouseComponent.isSelected()) {
            mouseComponent.setSelected(mouseElementSelected = true);
        }

        // snap grid, unless we're only dragging text elements
        int i;
        for (i = 0; i != elements.size(); i++) {
            BaseElement ce = getElement(i);
            if (ce.isSelected()) {
                break;
            }
        }
        if (i != elements.size()) {
            x = snapGrid(x);
            y = snapGrid(y);
        }

        int dx = x - dragX;
        int dy = y - dragY;
        if (dx == 0 && dy == 0) {
            // don't leave mouseComponent selected if we selected it above
            if (mouseElementSelected) {
                mouseComponent.setSelected(false);
            }
            return false;
        }
        boolean allowed = true;

        // check if moves are allowed
        for (i = 0; allowed && i != elements.size(); i++) {
            BaseElement ce = getElement(i);
            if (ce.isSelected() && !ce.allowMove(dx, dy)) {
                allowed = false;
            }
        }

        if (allowed) {
            for (i = 0; i != elements.size(); i++) {
                BaseElement baseElement = getElement(i);
                if (baseElement.isSelected()) {
                    if (Configuration.KEEP_CONNECTED_ON_DRAG
                            && !(baseElement instanceof Wire)) {
                        int[] elementJoints = baseElement.joints;
                        for (int j = 0; j < elementJoints.length; j++) {
                            int k = elementJoints[j];
                            if (k >= joints.size()) {
                                continue;
                            }
                            Joint joint = joints.get(k);
                            if (joint.references.size() > 1) {
                                for (int ref = 0; ref < joint.references.size(); ref++) {
                                    JointReference jointRef = joint.references.elementAt(ref);
                                    if (jointRef.element != baseElement) {
                                        jointRef.element.getPost(jointRef.postNumber).x += dx;
                                        jointRef.element.getPost(jointRef.postNumber).y += dy;

                                        jointRef.element.movePoint(jointRef.postNumber, dx, dy);
                                    }
                                }
                            }
                        }
                    }
                    if (!Configuration.KEEP_CONNECTED_ON_DRAG
                            || !(baseElement instanceof Wire)) {
                        baseElement.move(dx, dy);
                    }
                }
            }
            prepareForAnalysis();
        }

        // don't leave mouseComponent selected if we selected it above
        if (mouseElementSelected) {
            mouseComponent.setSelected(false);
        }

        return allowed;
    }

    /**
     * It moves every element to the given x and y coordinates
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void moveAll(int x, int y) {
        int dx = x - dragX;
        int dy = y - dragY;

        if (dx == 0 && dy == 0) {
            return;
        }

        for (BaseElement baseElement : elements) {
            baseElement.move(dx, dy);
        }
    }

    /**
     * It tells other methods that analysis is needed.
     */
    public void prepareForAnalysis() {
        needsAnalysis = true;
        repaint();
    }

    /**
     * Tells if this
     * <code>ContainerPanel</code> has any element.
     *
     * @return <code>true</code> if there is elements on this container,
     * <code>false</code> otherwise
     */
    public boolean containsElements() {
        return !elements.isEmpty();
    }

    //<editor-fold defaultstate="collapsed" desc="Stamping">
    //<editor-fold defaultstate="collapsed" desc="Instant stamping">
    /**
     * Stamps a voltage source between two nodes.
     *
     * @param fromNode source node number
     * @param toNode target node number
     * @param voltageSourceIndex index of the voltage source
     * @param newVoltage voltage to be stamped
     */
    public void stampVoltageSource(int fromNode, int toNode,
            int voltageSourceIndex, double newVoltage, String multibits) {
        int rowIndex = joints.size() + voltageSourceIndex;
        stampVoltageMatrix(rowIndex, fromNode, -1, multibits);
        stampVoltageMatrix(rowIndex, toNode, 1, multibits);
        setRowInfoRightSideChanges(rowIndex, newVoltage);
        setRowInfoMultibitsChange(rowIndex, multibits);
        stampVoltageMatrix(fromNode, rowIndex, 1, multibits);
        stampVoltageMatrix(toNode, rowIndex, -1, multibits);
    }

    /**
     * Sets a right side voltage on a
     * <code>RowInfo</code>
     *
     * @param rowInfoIndex row info index inside the list
     * @param newVoltage voltage to be set
     */
    public void setRowInfoRightSideChanges(int rowInfoIndex, double newVoltage) {
        if (rowInfoIndex <= 0) {
            return;
        }
        if (mapForStamp) {
            rowInfoIndex = rowsInfo.elementAt(rowInfoIndex - 1).mappedRow;
        } else {
            rowInfoIndex -= 1;
        }
        temporalRightSideVoltages[rowInfoIndex] = newVoltage;
    }

    /**
     * Sets a right side multibits value of a
     * <code>RowInfo</code>
     *
     * @param rowInfoIndex row info index inside the list
     * @param multibits multibits value
     */
    public void setRowInfoMultibitsChange(int rowInfoIndex, String multibits) {
        if (rowInfoIndex <= 0) {
            return;
        }
        if (mapForStamp) {
            rowInfoIndex = rowsInfo.elementAt(rowInfoIndex - 1).mappedRow;
        } else {
            rowInfoIndex -= 1;
        }
        temporalRightSideMultibits[rowInfoIndex] = multibits;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Wait for next runStep">
    /**
     * It sets the voltage source index to the connection between two nodes.
     *
     * @param fromNode source node number
     * @param toNode target node number
     * @param voltageSourceIndex index of the voltage source
     */
    public void stampVoltageSource(int fromNode, int toNode, int voltageSourceIndex) {
        int rowIndex = joints.size() + voltageSourceIndex;
        stampVoltageMatrix(rowIndex, fromNode, -1, "z");
        stampVoltageMatrix(rowIndex, toNode, 1, "z");
        setRowInfoRightSideChanges(rowIndex);
        stampVoltageMatrix(fromNode, rowIndex, 1, "z");
        stampVoltageMatrix(toNode, rowIndex, -1, "z");
    }

    /**
     * It tells a
     * <code>RowInfo</code> that it voltage changed.
     *
     * @param rowInfoIndex index of the <code>RowInfo</code> to update
     */
    public void setRowInfoRightSideChanges(int rowInfoIndex) {
        if (rowInfoIndex <= 0) {
            return;
        }
        rowsInfo.get(rowInfoIndex - 1).rightSideChanges = true;
    }

    /**
     * Sets a right side multibits value of a
     * <code>RowInfo</code>
     *
     * @param rowInfoIndex row info index inside the list
     */
    public void setRowInfoMultibitsChange(int rowInfoIndex) {
        if (rowInfoIndex <= 0) {
            return;
        }
        rowsInfo.get(rowInfoIndex - 1).rightSideChanges = true;
    }
    //</editor-fold>

    /**
     * It stamps the specified voltage to the global matrix used to analyze the
     * circuit.
     *
     * @param row row number in which voltage will be stamped
     * @param column column number in which voltage will be stamped
     * @param newVoltage voltage to be stamped
     */
    public void stampVoltageMatrix(int row, int column, double newVoltage, String multibits) {
        if (row <= 0 || column <= 0) {
            return;
        }
        if (mapForStamp) {
            row = rowsInfo.elementAt(row - 1).mappedRow;
            column = rowsInfo.elementAt(column - 1).mappedColumn;
        } else {
            row -= 1;
            column -= 1;
        }
        temporalVoltageMatrix[row][column] += newVoltage;
        if (temporalMultibitsMatrix[row][column] == null) {
            temporalMultibitsMatrix[row][column] = multibits;
        }
    }

    /**
     * Stamps a voltage depending on the resistor value. <p> Usually used to
     * avoid zeros on voltages matrix.
     *
     * @param nodeA first node number
     * @param nodeB second node number
     * @param resistance resistance value
     */
    public void stampResistor(int nodeA, int nodeB, double resistance) {
        double r0 = 1 / resistance;
        if (Double.isNaN(r0) || Double.isInfinite(r0)) {
            int a = 0;
            a /= a;
        }
        stampVoltageMatrix(nodeA, nodeA, r0, "z");
        stampVoltageMatrix(nodeB, nodeB, r0, "z");
        stampVoltageMatrix(nodeA, nodeB, -r0, "z");
        stampVoltageMatrix(nodeB, nodeA, -r0, "z");
    }

    /**
     * Updates voltage source to the specified voltage.
     *
     * @param voltageSourceIndex voltage source index to be updated
     * @param newVoltage voltage value to be set
     */
    public void updateVoltageSource(int voltageSourceIndex, double newVoltage, String multibits) {
        int voltageIndex = joints.size() + voltageSourceIndex;
        setRowInfoRightSideChanges(voltageIndex, newVoltage);
        setRowInfoMultibitsChange(voltageIndex, multibits);
    }
    //</editor-fold>

    private void errorOcurredOnRow(int index) {
        int voltageSourceIndex = index - joints.size() + 1;
        int tempJoints[];
        if(voltageSourceIndex >= 0 && voltageSourceIndex < voltageSourceElements.length){
            BaseElement voltageElement = this.voltageSourceElements[voltageSourceIndex];
            tempJoints = voltageElement.joints;
        }else{
            tempJoints = new int[1];
            tempJoints[0] = index + 1;
        }
        
        List<Integer> iteratedJointIndex = new ArrayList<Integer>();
        this.setJointsAsError(tempJoints, iteratedJointIndex);
    }

    private void setJointsAsError(int[] joints, List<Integer> iteratedJointIndex) {
        for(int jointIndex : joints){
            if(iteratedJointIndex.contains(jointIndex))
                continue;
            iteratedJointIndex.add(jointIndex);
            Joint joint = this.getJoint(jointIndex);
            joint.setAsErrorState();
            for(JointReference reference : joint.references){
                this.setJointsAsError(reference.element.joints, iteratedJointIndex);
            }
        }
    }

    private void setJointsAndVoltageSourcesReferences() {
        joints.clear();
        if (elements.isEmpty() || !runnable) {
            return;
        }
        
        this.prepareElementsForAnalysis();
        
        Joint globalGround = new Joint(-1, -1, 0);
        joints.add(globalGround);
        int elementIndex, postIndex, jointIndex;
        int totalVoltageSourceCount = 0;
        for (elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
            BaseElement baseElement = getElement(elementIndex);            
            int postCount = baseElement.getPostCount();
            int elementVoltageSourceCount = baseElement.getVoltageSourceCount();
            
            //<editor-fold defaultstate="collapsed" desc="Update joints">
            for (postIndex = 0; postIndex < postCount; postIndex++) {
                Point post = baseElement.getPost(postIndex);
                for (jointIndex = 0; jointIndex < joints.size(); jointIndex++) {
                    Joint joint = getJoint(jointIndex);
                    if (post.x == joint.coordX && post.y == joint.coordY) {
                        break;
                    }
                }
                
                int outOfRange = joints.size();
                JointReference jointRef = new JointReference(baseElement, postIndex);
                if (jointIndex == outOfRange) {
                    /*Add new joint*/
                    Joint newJoint = new Joint(post.x, post.y, jointIndex);
                    newJoint.addReference(jointRef);
                    baseElement.setJointIndex(postIndex, jointIndex);
                    joints.add(newJoint);
                } else {
                    /*Add joint reference to existing joint*/
                    Joint joint = getJoint(jointIndex);
                    joint.addReference(jointRef);
                    baseElement.setJointIndex(postIndex, jointIndex);                    
                }
            }
            //</editor-fold>

            totalVoltageSourceCount += elementVoltageSourceCount;
        }
        
        this.analyzePropagationLevels();
        
        voltageSourceElements = new BaseElement[totalVoltageSourceCount];

        totalVoltageSourceCount = 0;
        int ivs;
        for (BaseElement baseElement : elements) {
            ivs = baseElement.getVoltageSourceCount();
            for (int i = 0; i < ivs; i++) {
                voltageSourceElements[totalVoltageSourceCount] = baseElement;
                baseElement.setVoltageSourceReference(i, totalVoltageSourceCount++);
            }
        }
    }

    private void prepareAndCalculateMatrixValues() {
        if (elements.isEmpty() || !runnable) {
            return;
        }
        int rowCount = joints.size() + voltageSourceElements.length - 1;
        initializeRows(rowCount);

        temporalRightSideVoltages = new double[rowCount];
        temporalRightSideMultibits = new String[rowCount];
        initializeArray(temporalRightSideMultibits, "z");

        rightSideVoltages = new double[rowCount];
        rightSideMultibitsValues = new String[rowCount];
        initializeArray(rightSideMultibitsValues, "z");

        matrixSize = matrixFullSize = rowCount;
        temporalVoltageMatrix = new double[rowCount][rowCount];
        temporalMultibitsMatrix = new String[rowCount][rowCount];
        for (int i = 0; i < rowCount; i++) {
            initializeArray(temporalMultibitsMatrix[i], "z");
        }
        circuitPermute = new int[matrixSize];
        mapForStamp = false;

        for (BaseElement baseElement : elements) {
            baseElement.stampVoltages();
//            System.out.println("-------------------\nElement: " + baseElement.getClass().toString());
//            MatrixUtils.printNumberMatrix(temporalVoltageMatrix);
        }
        
        //<editor-fold defaultstate="collapsed" desc="Closures">
        boolean closure[] = new boolean[joints.size()];
        boolean changed = true;
        closure[0] = true;
        while (changed) {
            //DEBUG("changed");
            changed = false;
            for (int i = 0; i < elements.size(); i++) {
                //DEBUG("for " + i);
                BaseElement baseElement = getElement(i);

                for (int j = 0; j < baseElement.getPostCount(); j++) {
                    //DEBUG("for post " + j);
                    if (!closure[baseElement.joints[j]]) {
                        //DEBUG("!closure[ce.getNode(j)]" + j);
                        if (baseElement.hasGroundConnection(j)) {
                            //DEBUG("has gc " + j);
                            closure[baseElement.joints[j]] = changed = true;
                        }
                        continue;
                    }
                    int k;
                    for (k = 0; k != baseElement.getPostCount(); k++) {
                        //DEBUG("for k " + k);
                        if (j == k) {
                            continue;
                        }
                        int nodeK = baseElement.joints[k];
                        if (baseElement.thereIsConnectionBetween(j, k)
                                && !closure[nodeK]) {
                            //DEBUG("getConnection kn" + nodeK);
                            closure[nodeK] = true;
                            changed = true;
                        }
                    }
                }
            }
            if (changed) {
                continue;
            }

            for (int i = 0; i != joints.size(); i++) {
                if (!closure[i]) {
                    //DEBUG("node " + i + " unconnected");
                    stampResistor(0, i, 1e8);
                    closure[i] = true;
                    changed = true;
                    break;
                }
            }
        }
        
//        System.out.println("After Clousures");
//        MatrixUtils.printNumberMatrix(temporalVoltageMatrix);
        
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Matrix reduction">
        int index, innerIndex;
        for (index = 0; index < rowCount; index++) {
            int quantityM = -1, quantityP = -1;
            double valueP = 0, rightSideSum = 0;
            boolean isJoint = index < (joints.size() - 1);
            int voltageEntries = 0;

            RowInfo rowInfo = rowsInfo.elementAt(index);
            if (rowInfo.leftSideChanges || rowInfo.notUsedInMatrix || rowInfo.rightSideChanges) {
                continue;
            }

            for (innerIndex = 0; innerIndex < rowCount; innerIndex++) {
                double q = temporalVoltageMatrix[index][innerIndex];
                if (rowsInfo.elementAt(innerIndex).type == RowType.CONSTANT) {
                    rightSideSum -= rowsInfo.elementAt(innerIndex).voltage * q;
                    if(q != 1 && q!= -1 )
                        continue;
                }
                if (q == 0) {
                    continue;
                }
                if(q == -1){
                    voltageEntries ++;
                }
                if(voltageEntries > 1){
                    System.err.println("Error!");
                    this.errorOcurredOnRow(index);
                    return;
                }
                if (quantityP == -1) {
                    quantityP = innerIndex;
                    valueP = q;
                    continue;
                }
                if(q == -valueP || q == valueP){
                    if(quantityM == -1)
                        quantityM = innerIndex;
                    continue;
                }
                break;
            }
            if (innerIndex == rowCount) {
                if (quantityP == -1) {
                    System.err.println("Error!");
                    this.errorOcurredOnRow(index);
                    return;
                }
                RowInfo secondRowInfo;
                if (quantityM == -1) {
                    if(valueP == -1)
                        continue; //if the value is -1 it means that the current row value is going to be set.
                    secondRowInfo = rowsInfo.elementAt(quantityP);
                    if (secondRowInfo.type == RowType.EQUALS_TO_ANOTHER) {
                        if(secondRowInfo.rowEqualsToReference != index){
                            //Is an error because I'm trying of setting a value into a row which already has or is going to have an assignment from other row.
                            System.err.println("Error at voltage sources!");
                            this.errorOcurredOnRow(index);
                            return;
                        }
                    }
                    
                    secondRowInfo.type = RowType.CONSTANT;
                    rowInfo.notUsedInMatrix = true;
                    secondRowInfo.voltage = Math.abs((temporalRightSideVoltages[index] + rightSideSum) / valueP);
                    secondRowInfo.multiBitsValue = temporalRightSideMultibits[index];
                    index = -1;
                } else {
                    boolean isAnyConstant = false;
                    int newQuantityM = quantityM;
                    for(; newQuantityM < rowCount ; newQuantityM++){
                        double valueQ = temporalVoltageMatrix[index][newQuantityM];
                        if(valueQ == 0)
                            continue;
                        if(valueQ == 1){
                            //I need to switch the reference
                            secondRowInfo = rowsInfo.elementAt(newQuantityM);
                        }else{
                            secondRowInfo = rowsInfo.elementAt(quantityP);
                        }
                        
                        if(rowInfo.type == RowType.CONSTANT){
                            rowInfo.notUsedInMatrix = true;
                            isAnyConstant = true;
                            secondRowInfo.type = RowType.CONSTANT;
                            secondRowInfo.voltage = Math.abs(isJoint 
                                                    ? rowInfo.voltage
                                                    : (temporalRightSideVoltages[index] + rightSideSum) / valueP);
                            secondRowInfo.multiBitsValue = isJoint 
                                                           ? rowInfo.multiBitsValue
                                                           :temporalRightSideMultibits[index];
                        }else{
                            /*this reference is going to be solved in other iteration.*/
                        }
                    }
                    if(isAnyConstant)
                        index = -1;
                }
            }
        }


        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Delete useless rows">
        int newCol = 0;
        for (index = 0; index < matrixSize; index++) {
            RowInfo rowInfo = rowsInfo.elementAt(index);
            switch (rowInfo.type) {
                case NORMAL:
                    rowInfo.mappedColumn = newCol++;
                    continue;
                case CONSTANT:
                    rowInfo.mappedColumn = -1;
                    break;
                case EQUALS_TO_ANOTHER:
                    RowInfo pivot;
                    for (innerIndex = 0; innerIndex != 100; innerIndex++) {
                        pivot = rowsInfo.elementAt(rowInfo.rowEqualsToReference);
                        if (pivot.type != RowType.EQUALS_TO_ANOTHER) {
                            break;
                        }
                        if (index == pivot.rowEqualsToReference) {
                            break;
                        }
                        rowInfo.rowEqualsToReference = pivot.rowEqualsToReference;
                    }
                    break;
            }
        }

        for (index = 0; index != rowCount; index++) {
            RowInfo rowInfo = rowsInfo.elementAt(index);
            if (rowInfo.type == RowType.EQUALS_TO_ANOTHER) {
                RowInfo secondRowInfo = rowsInfo.elementAt(rowInfo.rowEqualsToReference);
                if (secondRowInfo.type == RowType.CONSTANT) {
                    rowInfo.type = secondRowInfo.type;
                    rowInfo.voltage = secondRowInfo.voltage;
                    rowInfo.multiBitsValue = secondRowInfo.multiBitsValue;
                    rowInfo.mappedColumn = -1;
                } else {
                    rowInfo.mappedColumn = secondRowInfo.mappedColumn;
                }
            }
        }
        //</editor-fold>  

        mapForStamp = true;
    }

    private void prepareElementsForAnalysis() {
        /* Ordering BaseElement list to improve analysis of Joints*/
        Vector<BaseElement> newList = new Vector<BaseElement>();
        Vector<BaseElement> wiresList = new Vector<BaseElement>();
        boolean refreshNeeded = this.refreshModules;
        this.refreshModules = false;
        for(BaseElement element : this.elements){
            if(element.isWire()){
                wiresList.add(element);
                ((Wire)element).clearJoinInput();
            }else{
                if(refreshNeeded && element instanceof ModuleChip){
                    ModuleChip moduleChip = (ModuleChip)element;
                    if(moduleChip.needsRefresh()){
                        newList.add(element);
                    }else{
                        simulationScope.unregister(moduleChip.getModuleInstanceId());
                        ModuleRepository.getInstance().unregisterModuleChip(moduleChip.getModuleName(), moduleChip);
                    }
                }else{
                    newList.add(element);
                }
            }
            element.clearForAnalysis();
        }
        
        for(BaseElement wire : wiresList){
            newList.add(wire);
        }
        
        this.elements = newList;
    }

    private void analyzePropagationLevels() {
        this.highestPropagationLevel = 1;
        for(Joint joint : joints){
            /*Maybe there is a better way to analyze this without re-creating the iterated joint list for each node*/
            List<Integer> iteratedJoints = new ArrayList<Integer>();
            int value = this.getPropagationLevels(joint.getIndex(), iteratedJoints, 1);
            if(value > highestPropagationLevel)
                this.highestPropagationLevel = value;
        }
        
    }

    private int getPropagationLevels(int jointIndex, List<Integer> iteratedJoints, int level) {
        Joint joint = this.getJoint(jointIndex);
        if(joint == null || !joint.hasWiresConnected() || iteratedJoints.contains(jointIndex))
            return level;
        iteratedJoints.add(jointIndex);
        int highestLevel = level;
        for(JointReference jointReference : joint.references){
            if(!jointReference.element.isWire())
                continue;
            int firstJointIndex = jointReference.element.joints[0];
            int oppositeJointIndex = firstJointIndex == joint.getIndex()
                                     ? jointReference.element.joints[1]
                                     : firstJointIndex;
            
            int value = getPropagationLevels(oppositeJointIndex, iteratedJoints, level + 1);
            if(value > highestLevel)
                highestLevel = value;
        }        
        return highestLevel;
    }

    public void prepareRunWithoutAnalysis() {
        this.runWithoutAnalysis = true;
        this.repaint();
    }
}
