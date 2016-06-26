package nodes;
/**
 * <p>
 * An interface used by expression-specific nodes inside the 
 * abstract syntax tree. It is implemented by difference-like, sum-like nodes.
 * </p>
 * @author Theodor Stoican
 *
 */
public interface MathNode extends ASTNode{
	/**
	 * <p>
	 * A method that will be implemented inside every class that represents a
	 * MathNode. It provides the possibility of adding a son to an 
	 * expression-like node.
	 * </p>
	 * @return none
	 * @param ASTNode a node that implements the ASTNode interface and that will
	 * be added as a son
	 */
	public void addSons(ASTNode node);
	/**
	 * <p>
	 * A method that will be implemented inside every class that represents a 
	 * math-type node. It return a boolean, telling the programmer if they can
	 * or not add other sons.
	 * </p>
	 * @return boolean true-if there can be added more users and false-otherwise
	 * @param none
	 */
	public boolean canAddSons();
}
