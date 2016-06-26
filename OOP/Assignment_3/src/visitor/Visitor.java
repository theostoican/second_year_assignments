package visitor;

import nodes.*;

/**
 * <p>
 * An interface used for using the visitor design pattern. It provides
 * the visit methods, needed while applying the visitor to every node.
 * </p>
 * @author Theodor Stoican
 *
 */
public interface Visitor
{
	/**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param AssignmentNode a node used for assignment operations
	 */
    public void visit (AssignmentNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param ActNode an act-type node
	 */
    public void visit (ActNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param ConstantNode a node containing a value
	 */
    public void visit (ConstantNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param LvalNode a node used for assignment operations
	 */
    public void visit (LvalNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param SceneNode a scene-like node
	 */
    public void visit (SceneNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param ProgramNode
	 */
    public void visit (ProgramNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param DifferenceNode
	 */
    public void visit (DifferenceNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param SumNode
	 */
    public void visit (SumNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param RValNode
	 */
    public void visit (RValNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param OutputNode
	 */
    public void visit (OutputNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param ProductNode
	 */
    public void visit (ProductNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param SquareNode
	 */
    public void visit (SquareNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param CubeNode
	 */
    public void visit (CubeNode node);
    /**
	 * <p>
	 * A method that will be implemented inside every class that implements
	 * the Visitor interface. Used for applying a specific algorithm to a specific
	 * node.
	 * </p>
	 * @return none
	 * @param QuotientNode
	 */
    public void visit (QuotientNode node);
}
