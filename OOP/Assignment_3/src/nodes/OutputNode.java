package nodes;

import java.util.List;

import visitor.Visitable;
import visitor.Visitor;

/**
 * <p>
 * A class that represents an output node. Used for printing the value
 * of a specific character(variable).
 * </p>
 * @author Theodor Stoican
 *
 */
public class OutputNode implements ASTNode, Visitable {
	private String character;
	private char type; //A - ASCII, I - integer
	public OutputNode(String character, char type){
		this.character = character;
		this.type = type;
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
	 * A method used for getting the character from the node.
	 * </p>
	 * @return String a string representing the character's name.
	 * @param none
	 */
	public String getCharacter(){
		return character;
	}
	/**
	 * <p>
	 * A method used for getting the type of the value to be printed
	 * (ASCII or integer).
	 * </p>
	 * @return List<ASTNode> returns the sons of the node as a lists of
	 * ASTNode objects
	 * @param 
	 */
	public char getType (){
		return type;
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
