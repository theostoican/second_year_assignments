package nodes;

import java.util.ArrayList;
import java.util.List;
import visitor.*;
/**
 * <p>
 * A class that represents a node used for the beginning of the abstract syntax tree.
 * It is used for outlining the beginning of the program.
 * </p>
 * @author Theodor Stoican
 *
 */
public class ProgramNode implements ASTNode, Visitable{
	private List<ASTNode> sons;
	
	/**
	 * <p>
	 * Constructor that initializes the inner fields of the class.
	 * </p>
	 * @return none
	 * @param none
	 */
	public ProgramNode(){
		sons = new ArrayList<ASTNode>();
	}
	/**
	 * <p>
	 * A method that adds a vertex as a son of this "program node". Can 
	 * have an unlimited number of sons.
	 * </p>
	 * @return none
	 * @param node - a vertex inside the tree (that implements the ASTNode interface)
	 */
	public void addNode(ASTNode node) {
		sons.add(node);
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
	 * An overridden method used for getting the sons of the node. 
	 * </p>
	 * @return List<ASTNode> returns the sons of the node as a lists of
	 * ASTNode objects
	 * @param none 
	 */
	@Override
	public List<ASTNode> getSons(){
		return sons;
	}
}
