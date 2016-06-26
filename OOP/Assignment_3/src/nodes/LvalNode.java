package nodes;

import java.util.List;

import visitor.*;
/**
 * <p>
 * A class that represents a left node(with respect to the assignment node).
 * It stores the value of a character.
 * </p>
 * @author Theodor Stoican
 *
 */
public class LvalNode implements ASTNode, Visitable{
	private String character;
	
	/**
	 * <p>
	 * Constructor that initializes the inner fields of the class.
	 * </p>
	 * @return none
	 * @param String a string representing the name of the character(variable).
	 */
	public LvalNode(String character)
	{
		this.character = character;
	}
	/**
	 * <p>
	 * A method used by a visitor to get the name of the character(variable).
	 * </p>
	 * @return none
	 * @param none
	 */
	public String getCharacter(){
		return character;
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