package nodes;

import java.util.List;
import visitor.*;
/**
 * <p>
 * A class that represents a constant node inside the AST and that is used
 * for arithmetical or assignment operations.
 * </p>
 * @author Theodor Stoican
 *
 */
public class ConstantNode implements ASTNode, Visitable{
	private int value;
	
	/**
	 * <p>
	 * Constructor that initializes the inner fields of the class.
	 * </p>
	 * @return none
	 * @param int the value of the node that will be assigned to a varaible
	 */
	public ConstantNode(int value){
		this.value = value;
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
	 * A method used by a specific visitor for getting the value of the node.
	 * It will be further used in particular operations.
	 * </p>
	 * @return int a value corresponding to the node
	 * @param none
	 */
	public int getValue(){
		return value;
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
	public List<ASTNode> getSons() {
		return null;
	}
}
