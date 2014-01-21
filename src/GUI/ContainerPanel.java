/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import DataStructures.CircuitGenerator;
import Simulation.Configuration;
import Simulation.Elements.BaseElement;
import Simulation.Elements.Gates.GateElement;
import Simulation.Elements.Gates.NotGate;
import Simulation.Elements.Wire;
import Simulation.Joint;
import Simulation.JointReference;
import Simulation.RowInfo;
import Simulation.RowType;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Néstor A. Bermúdez <nestor.bermudez@unitec.edu>
 */
public class ContainerPanel extends JCanvas {
    //<editor-fold defaultstate="collapsed" desc="Attributes">

    protected Vector<BaseElement> elements;
    protected BaseElement[] voltageSourceElements;
    protected Vector<Joint> joints;
    protected Vector<RowInfo> rowsInfo;
    public BaseElement newElementBeenDrawn, menuElm, mouseComponent, stopElm;
    public boolean needsAnalysis = false, isPaused = false;
    protected boolean dragging = false, mapForStamp = false;
    protected int dragX, dragY, initDragX, initDragY, matrixSize, matrixFullSize;
    protected int gridSize, gridMask, gridRound;
    protected double voltageMatrix[][], temporalVoltageMatrix[][];
    protected double temporalRightSideVoltages[], rightSideVoltages[], leftSideVoltages[];
    protected int draggingPost = -1;
    protected Rectangle selectedArea = null;
    public MouseMode defaultMouseMode = MouseMode.SELECT, currentMouseMode = MouseMode.SELECT;
    protected long lastTimeStamp = 0, lastFrameTimeStamp = 0;
    public boolean runnable = true;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Methods">
    public BaseElement getElement(int i) {
        return this.elements.get(i);
    }

    public Joint getJoint(int index) {
        if (index >= joints.size()) {
            return null;
        }
        return joints.elementAt(index);
    }

    public int distance(int x1, int y1, int x2, int y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    public void init() {
        setGrid();
    }

    public void selectionOnArea(int x, int y) {
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

    public void clearSelection() {
        for (BaseElement baseElement : elements) {
            baseElement.setSelected(false);
        }
        selectedArea = null;
    }

    public Element toXmlRootElement(Document document) {
        Element root = document.createElement("elements");
        for (BaseElement element : elements) {
            root.appendChild(element.getXmlElement(document));
        }
        return root;
    }

    public BaseElement constructElement(String type, int x, int y) {

        BaseElement element = CircuitGenerator.getInstance().constructElement(type,
                snapGrid(x), snapGrid(y));

        if (element != null) {
            element.containerPanel = this;
        }

        return element;
    }

    public BaseElement constructElement(String type,
            int x, int y, int x2, int y2, String[] extraParams) {

        BaseElement element = CircuitGenerator.getInstance().constructElement(type,
                x, y, x2, y2, extraParams);

        if (element != null) {
            element.containerPanel = this;
        }

        return element;
    }

    public void constructAndAddElement(String type,
            int x, int y, int x2, int y2, String[] extraParams) {
        addElement(constructElement(type, x, y, x2, y2, extraParams));
    }
    //</editor-fold>

    @Override
    protected void paintComponent(Graphics g) {
        Color old = g.getColor();
        g.setColor(Configuration.BACKGROUND_COLOR);
        super.paintComponent(g);        
        g.fillRect(-this.getX(), -this.getY(), getWidth() * 2, getHeight() * 2);
        g.setColor(old);
        updatePreview(g);
    }

    protected void analyze() {
        if (elements.isEmpty() || !runnable) {
            return;
        }

        joints.clear();

        Joint globalGround = new Joint(-1, -1);
        joints.add(globalGround);

        int elementIndex, postIndex, jointIndex;
        int totalVoltageSourceCount = 0;
        for (elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
            BaseElement baseElement = getElement(elementIndex);
            int postCount = baseElement.getPostCount();
            int voltageSourceCount = baseElement.getVoltageSourceCount();

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
                    Joint newJoint = new Joint(post.x, post.y);
                    newJoint.references.add(jointRef);

                    baseElement.setJointIndex(postIndex, jointIndex);
                    joints.add(newJoint);
                } else {
                    /*Add joint reference to existing joint*/
                    Joint joint = getJoint(jointIndex);
                    joint.references.add(jointRef);

                    baseElement.setJointIndex(postIndex, jointIndex);
                    if (postIndex == 0) {
                        baseElement.setVoltage(postIndex, 0.0);
                    }
                }
            }
            //</editor-fold>

            totalVoltageSourceCount += voltageSourceCount;
        }

        voltageSourceElements = new BaseElement[totalVoltageSourceCount];

        int rowCount = joints.size() + totalVoltageSourceCount - 1;
        //<editor-fold defaultstate="collapsed" desc="Set voltage source references">
        int voltageSourceCount, control = 0;
        for (BaseElement baseElement : elements) {
            voltageSourceCount = baseElement.getVoltageSourceCount();
            for (int i = 0; i < voltageSourceCount; i++) {
                baseElement.setVoltageSourceReference(i, control);
                voltageSourceElements[control] = baseElement;
                control += 1;
            }
        }

        initializeRows(rowCount);

        temporalRightSideVoltages = new double[rowCount];
        rightSideVoltages = new double[rowCount];
        matrixSize = matrixFullSize = rowCount;
        temporalVoltageMatrix = new double[rowCount][rowCount];
        voltageMatrix = new double[rowCount][rowCount];
        mapForStamp = false;

        for (BaseElement baseElement : elements) {
            baseElement.stampVoltages();
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
        //</editor-fold>

        System.arraycopy(temporalRightSideVoltages, 0, rightSideVoltages, 0,
                temporalRightSideVoltages.length);

        //<editor-fold defaultstate="collapsed" desc="Matrix reduction">
        int index, innerIndex = 0;
        for (index = 0; index < matrixSize; index++) {
            int quantityM = -1, quantityP = -1;
            double value = 0, constantsSum = 0;

            RowInfo rowInfo = rowsInfo.elementAt(index);
            if (rowInfo.leftSideChanges || rowInfo.notUsedInMatrix || rowInfo.rightSideChanges) {
                continue;
            }

            for (innerIndex = 0; innerIndex < matrixSize; innerIndex++) {
                double q = temporalVoltageMatrix[index][innerIndex];
                if (rowsInfo.elementAt(innerIndex).type == RowType.CONSTANT) {
                    constantsSum -= rowsInfo.elementAt(innerIndex).voltage * q;
                    continue;
                }
                if (q == 0) {
                    continue;
                }
                if (quantityP == -1) {
                    quantityP = innerIndex;
                    value = q;
                    continue;
                }
                if (quantityM == -1 && q == -value) {
                    quantityM = innerIndex;
                    continue;
                }
                break;
            }
            if (innerIndex == matrixSize) {
                if (quantityP == -1) {
                    System.err.println("Error!");
                    return;
                }
                RowInfo secondRowInfo = rowsInfo.elementAt(quantityP);
                if (quantityM == -1) {
                    secondRowInfo.type = RowType.CONSTANT;
                    rowInfo.notUsedInMatrix = true;
                    secondRowInfo.voltage = (temporalRightSideVoltages[index] + constantsSum) / value;
                    index = -1;
                } else if (constantsSum + temporalRightSideVoltages[index] == 0) {
                    secondRowInfo.type = RowType.EQUALS_TO_ANOTHER;
                    rowInfo.notUsedInMatrix = true;
                }
            }
        }

        if (Configuration.DEBUG_MODE) {
            for (int i = 0; i < temporalRightSideVoltages.length; i++) {
                double b = temporalRightSideVoltages[i];
                System.out.print(b + ",");
            }
            System.out.println();
        }

        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Delete useless rows">
        int newCol = 0;
        for (int i = 0; i < matrixSize; i++) {
            RowInfo rowInfo = rowsInfo.elementAt(i);
            switch (rowInfo.type) {
                case NORMAL:
                    rowInfo.mappedColumn = newCol;
                    newCol++;
                    continue;
                case CONSTANT:
                    rowInfo.mappedColumn = -1;
                    break;
                case EQUALS_TO_ANOTHER:
                    RowInfo piv = null;
                    break;
            }
        }

        double newMatrix[][] = new double[newCol][newCol];
        double newRightSides[] = new double[newCol];

        int newIndex = 0;
        for (int i = 0; i < matrixSize; i++) {
            RowInfo rowInfo = rowsInfo.elementAt(i);
            if (rowInfo.notUsedInMatrix) {
                rowInfo.mappedRow = -1;
                continue;
            }
            newRightSides[newIndex] = temporalRightSideVoltages[i];
            rowInfo.mappedRow = newIndex;

            for (int j = 0; j < matrixSize; j++) {
                RowInfo rowInfoJ = rowsInfo.elementAt(j);
                if (rowInfoJ.type == RowType.CONSTANT) {
                    newRightSides[newIndex] -= rowInfoJ.voltage * temporalVoltageMatrix[i][j];
                } else {
                    newMatrix[newIndex][rowInfoJ.mappedColumn] += temporalVoltageMatrix[i][j];
                }
            }
            newIndex++;
        }

        temporalRightSideVoltages = newRightSides;
        temporalVoltageMatrix = newMatrix;
        matrixSize = newCol;

        System.arraycopy(temporalRightSideVoltages, 0, rightSideVoltages, 0,
                temporalRightSideVoltages.length);

        for (int i = 0; i < matrixSize; i++) {
            System.arraycopy(temporalVoltageMatrix[i], 0, voltageMatrix[i], 0, matrixSize);
        }

        //</editor-fold>
        //</editor-fold>       
        mapForStamp = true;
    }

    public void initializeRows(int rowCount) {
        rowsInfo = new Vector<RowInfo>(rowCount);
        for (int i = 0; i < rowCount; i++) {
            rowsInfo.add(new RowInfo());
        }
    }

    public void runStep() {
        if (elements.isEmpty() || !runnable) {
            return;
        }

        /*long steprate = (long) (160 * 100.23124542356);
        long tm = System.currentTimeMillis();
        long temporalLastTimeStamp = lastTimeStamp;
        int iter;
        if (1000 >= steprate * (tm - lastTimeStamp)) {
            return;
        }*/

        //for (iter = 1;; iter++) {
        //    for (int iteration = 0; iteration < 5000; iteration++) {
                for (int i = 0; i < matrixSize; i++) {
                    System.arraycopy(temporalVoltageMatrix[i], 0, voltageMatrix[i], 0, matrixSize);
                }

                for (BaseElement baseElement : elements) {
                    if (baseElement instanceof NotGate || baseElement instanceof GateElement) {
                        baseElement.doStep();
                    }
                }
                System.arraycopy(temporalRightSideVoltages, 0, rightSideVoltages, 0,
                        temporalRightSideVoltages.length);
                if (isPaused) {
                    return;
                }
                for (int rowIndex = 0; rowIndex < matrixFullSize; rowIndex++) {
                    RowInfo rowInfo = rowsInfo.get(rowIndex);
                    double newVoltage = 0;
                    if (rowInfo.type == RowType.CONSTANT) {
                        newVoltage = rowInfo.voltage;
                    } else {
                        newVoltage = rightSideVoltages[rowInfo.mappedColumn];
                    }

                    if (Double.isNaN(newVoltage)) {
                    }
                    if (rowIndex < joints.size() - 1) {
                        Joint joint = joints.elementAt(rowIndex + 1);
                        for (JointReference jointRef : joint.references) {
                            jointRef.element.setVoltage(jointRef.postNumber, newVoltage);
                        }
                    } else {
                        int newIndex = rowIndex - (joints.size() - 1);
                        //voltageSourceElements[newIndex].setCurrent(newVoltage);
                    }
                }
                //break;
            //}
            /*temporalLastTimeStamp = System.currentTimeMillis();
            if (iter * 1000 >= steprate * (temporalLastTimeStamp - lastTimeStamp)
                    || (temporalLastTimeStamp - lastFrameTimeStamp > 500)) {
                break;
            }
        }
        lastTimeStamp = temporalLastTimeStamp;*/
    }

    public void resume() {
        isPaused = false;
    }

    public void pause() {
        isPaused = true;
    }

    public static void DEBUG(String message) {
        if (Configuration.DEBUG_MODE) {
            System.out.println(message);
        }
    }

    public void updatePreview(Graphics g) {
        if (needsAnalysis || true /*TODO: remove true*/) {
            analyze();
            needsAnalysis = false;
        }

        if (!isPaused) {
            runStep();
        }

        lastFrameTimeStamp = System.currentTimeMillis();

        for (BaseElement baseElement : elements) {
            baseElement.draw(g);
        }

        //<editor-fold defaultstate="collapsed" desc="Color bad connections">
        for (int jointIndex = 0; jointIndex < joints.size(); jointIndex++) {
            Joint joint = joints.elementAt(jointIndex);
            if (joint.references.size() == 1) {
                boolean collidesWith = false;
                JointReference reference = joint.references.elementAt(0);
                for (BaseElement baseElement : elements) {
                    if (baseElement != reference.element
                            && baseElement.collidesWith(joint.coordX, joint.coordY)) {
                        collidesWith = true;
                    }
                }

                if (collidesWith) {
                    Color old = g.getColor();
                    g.setColor(Color.RED);
                    g.fillOval(joint.coordX - 3, joint.coordY - 3, 7, 7);
                    g.setColor(old);
                }
            }
        }
        //</editor-fold>   

        if (newElementBeenDrawn != null
                && (newElementBeenDrawn.x != newElementBeenDrawn.x2 || newElementBeenDrawn.y != newElementBeenDrawn.y2)) {
            newElementBeenDrawn.draw(g);
        } else if (selectedArea != null) {
            Color old = g.getColor();
            g.setColor(Color.YELLOW.darker());
            g.drawRect(selectedArea.x, selectedArea.y,
                    selectedArea.width, selectedArea.height);

            g.setColor(old);
        }
    }

    public void addElement(BaseElement element) {
        if (element == null) {
            return;
        }
        if (element.containerPanel == null) {
            element.containerPanel = this;
        }
        element.x = snapGrid(element.x);
        element.y = snapGrid(element.y);
        element.x2 = snapGrid(element.x2);
        element.y2 = snapGrid(element.y2);
        element.setPoints();

        elements.add(element);
    }

    @Override
    public void removeAll() {
        elements.clear();
    }

    public ContainerPanel() {
        elements = new Vector<BaseElement>(50);
        joints = new Vector<Joint>(100);

        BaseElement.defaultColor = Color.BLACK;
        BaseElement.whiteColor = Color.WHITE;
        BaseElement.selectedColor = new Color(255, 128, 0);
        BaseElement.lowSignalColor = new Color(102, 51, 0);
        BaseElement.highSignalColor = Color.GREEN.darker();
        BaseElement.highImpedanceSignalColor = Color.RED;
        BaseElement.unknownSignalColor = Color.BLUE;
        BaseElement.textColor = Color.DARK_GRAY;

        needsAnalysis = true;
    }

    protected void setGrid() {
        gridSize = Configuration.SMALL_GRID ? 8 : 16;
        gridMask = ~(gridSize - 1);
        gridRound = gridSize / 2 - 1;
    }

    public int snapGrid(int x) {
        return (x + gridRound) & gridMask;
    }

    public void dragPost(int x, int y) {
        if (draggingPost == -1) {
            int distance1 = distance(mouseComponent.x, mouseComponent.y, x, y);
            int distance2 = distance(mouseComponent.x2, mouseComponent.y2, x, y);
            draggingPost =
                    (distance1 > distance2) ? 1 : 0;
        }
        int dx = x - dragX;
        int dy = y - dragY;
        if (dx == 0 && dy == 0) {
            return;
        }
        mouseComponent.movePoint(draggingPost, dx, dy);
        prepareForAnalysis();
        repaint();
    }

    public boolean dragSelected(int x, int y) {
        boolean me = false;
        if (mouseComponent != null && !mouseComponent.isSelected()) {
            mouseComponent.setSelected(me = true);
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
            if (me) {
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
                            if (k >= joints.size()) continue;
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
                            || !(baseElement instanceof Wire))
                        baseElement.move(dx, dy);
                }
            }
            prepareForAnalysis();
        }

        // don't leave mouseComponent selected if we selected it above
        if (me) {
            mouseComponent.setSelected(false);
        }

        return allowed;
    }

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

    public void prepareForAnalysis() {
        needsAnalysis = true;
        repaint();
    }

    public boolean containsElements() {
        return !elements.isEmpty();
    }

    //<editor-fold defaultstate="collapsed" desc="Stamping">
    //<editor-fold defaultstate="collapsed" desc="Instant stamping">
    public void stampVoltageSource(int fromNode, int toNode,
            int voltageSourceIndex, double newVoltage) {
        int rowIndex = joints.size() + voltageSourceIndex;
        stampVoltageMatrix(rowIndex, fromNode, -1);
        stampVoltageMatrix(rowIndex, toNode, 1);
        setRowInfoRightSideChanges(rowIndex, newVoltage);
        stampVoltageMatrix(fromNode, rowIndex, 1);
        stampVoltageMatrix(toNode, rowIndex, -1);
    }

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

    public void setRowInfoLeftSideChanges(int rowInfoIndex, double newVoltage) {
        if (rowInfoIndex <= 0) {
            return;
        }
        if (mapForStamp) {
            rowInfoIndex = rowsInfo.elementAt(rowInfoIndex - 1).mappedRow;
        } else {
            rowInfoIndex -= 1;
        }
        leftSideVoltages[rowInfoIndex] = newVoltage;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Wait for next runStep">
    public void stampVoltageSource(int fromNode, int toNode, int voltageSourceIndex) {
        int rowIndex = joints.size() + voltageSourceIndex;
        stampVoltageMatrix(rowIndex, fromNode, -1);
        stampVoltageMatrix(rowIndex, toNode, 1);
        setRowInfoRightSideChanges(rowIndex);
        stampVoltageMatrix(fromNode, rowIndex, 1);
        stampVoltageMatrix(toNode, rowIndex, -1);
    }

    public void setRowInfoRightSideChanges(int rowInfoIndex) {
        if (rowInfoIndex <= 0) {
            return;
        }
        rowsInfo.get(rowInfoIndex - 1).rightSideChanges = true;
    }

    public void setRowInfoLeftSideChanges(int rowInfoIndex) {
        if (rowInfoIndex <= 0) {
            return;
        }
        rowsInfo.get(rowInfoIndex - 1).leftSideChanges = true;
    }
    //</editor-fold>

    public void stampVoltageMatrix(int row, int column, double newVoltage) {
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
    }

    public void stampResistor(int n1, int n2, double r) {
        double r0 = 1 / r;
        if (Double.isNaN(r0) || Double.isInfinite(r0)) {
            int a = 0;
            a /= a;
        }
        stampVoltageMatrix(n1, n1, r0);
        stampVoltageMatrix(n2, n2, r0);
        stampVoltageMatrix(n1, n2, -r0);
        stampVoltageMatrix(n2, n1, -r0);
    }

    public void updateVoltageSource(int voltageSourceIndex, double newVoltage) {
        int voltageIndex = joints.size() + voltageSourceIndex;
        setRowInfoRightSideChanges(voltageIndex, newVoltage);
    }
    //</editor-fold>
}
