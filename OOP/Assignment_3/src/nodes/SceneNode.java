package nodes;

import java.util.ArrayList;
import java.util.List;
import visitor.*;

/**
 * <p>
 * A class that represents a node used for scene nodes inside the abstract syntax tree.
 * It provides some inner fields used for storing the sons(ArrayList) of the respective
 * vertex and the number of the scene.
 * </p>
 * @author Theodor Stoican
 *
 */
public class SceneNode implements ASTNode, Visitable {
	private ArrayList<ASTNode> sons;
	private String num;
	
	/**
	 * <p>
	 * Constructor that initializes the scene number.
	 * </p>
	 * @return none
	 * @param String a string that represents the scene number
	 */
	public SceneNode(String num){
		this.num = num;
		sons = new ArrayList<ASTNode>();
	}
	/**
	 * <p>
	 * A method that adds a vertex as a son of this "scene node".
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
		// TODO Auto-generated method stub
		v.visit(this);
	}
	/**
	 * <p>
	 * A method used by a specific visitor to get the scene number of a specific
	 * scene node inside the SPL program.
	 * </p>
	 * @return String return the number of the scene as a String object
	 * @param
	 */
	public String getSceneNumber(){
		return num;
	}
	/**
	 * <p>
	 * An overridden method used for getting the sons of the node. 
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
