package nodes;

import java.util.ArrayList;
import java.util.List;
import visitor.*;

/**
 * <p>
 * A class that represents a difference node and it is used for a specific operation,
 * namely the difference between the values of its respective sons.
 * </p>
 * @author Theodor Stoican
 *
 */
public class DifferenceNode implements ASTNode, Visitable, MathNode{
	private List<ASTNode> sons;
	private int maxNrSons;
	private int nrSons;
	/**
	 * <p>
	 * Constructor that initializes the inner fields of the class.
	 * </p>
	 * @return none
	 * @param none
	 */
	public DifferenceNode(){
		maxNrSons=2;
		nrSons=0;
		sons = new ArrayList<ASTNode>();
	}
	/**
	 * <p>
	 * A method that increments the number of sons of this node.
	 * </p>
	 * @return none
	 * @param none
	 */
	public void incrementSons(){
		nrSons++;
	}
	/**
	 * <p>
	 * An overridden method used for interrogation. It provides a boolean, signifying if
	 * another node can be added as a son of this one.
	 * </p>
	 * @return boolean true - can accept other sons, false - cannot accept other sons
	 * @param 
	 */
	@Override
	public boolean canAddSons(){
		return (nrSons<maxNrSons);
	}
	/**
	 * <p>
	 * A method used for implementing the Visitor design pattern
	 * and for visiting this node.
	 * </p>
	 * @return none
	 * @param v a visitor that visits this node
	 */
	@Override
	public void accept(Visitor v) {
		v.visit(this);
	}
	/**
	 * <p>
	 * An overriden method that adds an ASTNode object as a son of this node inside the tree.
	 * </p>
	 * @return none
	 * @param node - a vertex inside the tree (that implements the ASTNode interface)
	 */
	@Override
	public void addSons(ASTNode node) {
		sons.add(node);
		incrementSons();
	}
	/**
	 * <p>
	 * An overridden method used for getting the sons of the respective node. 
	 * </p>
	 * @return List<ASTNode> returns the sons of the node as a lists of
	 * ASTNode objects
	 * @param 
	 */
	@Override
	public List<ASTNode> getSons(){
		return sons;
	}

}