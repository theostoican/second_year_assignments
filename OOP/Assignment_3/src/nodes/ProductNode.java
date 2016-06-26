package nodes;

import visitor.Visitable;
import visitor.Visitor;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>
 * A class that represents the product between the values of two variables.
 * </p>
 * @author Theodor Stoican
 *
 */
public class ProductNode implements MathNode, ASTNode, Visitable{
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
	public ProductNode(){
		maxNrSons=2;
		nrSons=0;
		sons = new ArrayList<ASTNode>();
	}
	/**
	 * <p>
	 * A method that adds a vertex as a son of this "act node".
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
	 * An overridden method that adds an ASTNode object as a son of this node inside the tree.
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
	public List<ASTNode> getSons()
	{
		return sons;
	}
}