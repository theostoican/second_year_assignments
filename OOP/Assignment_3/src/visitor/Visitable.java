package visitor;

/**
 * <p>
 * An interface used for using the visitor design pattern. It provides
 * the accept method, needed while applying the visitor to every node.
 * </p>
 * @author Theodor Stoican
 *
 */
public interface Visitable
{
	/**
	 * <p>
	 * A method that will be implemented inside every class that represents
	 * a Visitable object. Used for calling the respective visit method.
	 * </p>
	 * @return none
	 * @param Visitor the visitor that will visit the node
	 */
    public void accept(Visitor v);

}