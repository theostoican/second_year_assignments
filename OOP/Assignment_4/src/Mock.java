import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <p>
 * This class is used for mocking the effective objects and provides the
 * necessary operations that correspond to the Mockable interface (execute
 * without and with parameters).
 * </p>
 * 
 * @author Theodor Stoican
 *
 * @param <A>
 *            the type of the input argument
 * @param <B>
 *            the type of the returned result
 */
public class Mock<A, B> implements Mockable<A, B> {
	private List<A> argumentsOfExecute;
	private List<Queue<String>> whatIsNext;
	private List<Queue<RuntimeException>> exceptions;
	private List<Queue<B>> values;
	private Queue<RuntimeException> noArgExeExceptions;
	private boolean withArgs;
	private List<Observer> argObservers;
	private Observer noArgObserver;

	/**
	 * <p>
	 * A constructor that initializes every field inside the class.
	 * </p>
	 */
	public Mock() {
		exceptions = new ArrayList<Queue<RuntimeException>>();
		values = new ArrayList<Queue<B>>();
		whatIsNext = new ArrayList<Queue<String>>();
		argumentsOfExecute = new ArrayList<A>();
		withArgs = false;
		noArgExeExceptions = new LinkedList<RuntimeException>();
		argObservers = new ArrayList<Observer>();
	}

	/**
	 * <p>
	 * The execute method without parameters that overrides the one provided
	 * inside the interface.
	 * </p>
	 * 
	 * @return Unit an object used symbolically as void
	 * @throws RuntimeException
	 */
	@Override
	public Unit execute() throws RuntimeException {
		if (State.isVerifying() == true) {
			boolean result = Mockito.getStrategy()
					.checkIfEqual(noArgObserver.getCounter());
			if (result == false) {
				State.stopVerifying();
				throw new MockitoException(Mockito.getStrategy().getMessage());
			}
			State.stopVerifying();
		} else if (State.isWatching() == true) {
			State.setCurrentMock(this);
			if (noArgObserver == null)
				noArgObserver = new CounterObserver();
			noArgObserver.reset();
			// resetez tot ce a fost inainte, din punct de vedere comportamental
			while (!noArgExeExceptions.isEmpty())
				noArgExeExceptions.remove();
			withArgs = false;
		} else {
			noArgObserver.update();
			// cazul cand dau thenReturn pe mock.execute()
			if (noArgExeExceptions.size() > 0)
				throw noArgExeExceptions.remove();
		}
		return new Unit();
	}

	/**
	 * <p>
	 * The overridden execute method with parameters.
	 * </p>
	 * 
	 * @return B the type of the output argument, previously settled
	 * @param A
	 *            the type of the input argument, previously settled
	 * @throws RuntimeException
	 */
	@Override
	public B execute(A arg) throws RuntimeException {
		if (State.isVerifying() == true) {
			int index = -1;
			for (int i = 0; i <= argumentsOfExecute.size() - 1; ++i) {
				if (argumentsOfExecute.get(i).equals(arg)) {
					index = i;
					break;
				}
			}
			boolean result = true;
			if (index >= 0)
				result = Mockito.getStrategy()
						.checkIfEqual(argObservers.get(index).getCounter());
			if (result == false) {
				State.stopVerifying();
				throw new MockitoException(Mockito.getStrategy().getMessage());
			}
			State.stopVerifying();
		}
		// daca acesta este un watcher
		else if (State.isWatching() == true) {
			State.setCurrentMock(this);
			// resetam mai intai comportamentul anterior, daca exista
			int index = -1;
			for (int i = 0; i <= argumentsOfExecute.size() - 1; ++i) {
				if (argumentsOfExecute.get(i).equals(arg)) {
					index = i;
					break;
				}
			}
			if (index >= 0) {
				argObservers.get(index).reset();
				Queue<String> next = whatIsNext.get(index);
				while (!next.isEmpty())
					next.remove();
				Queue<RuntimeException> previousExceptions = exceptions
						.get(index);
				while (!previousExceptions.isEmpty())
					previousExceptions.remove();
				Queue<B> prevValues = values.get(index);
				while (!prevValues.isEmpty())
					prevValues.remove();
				argumentsOfExecute.remove(index);
				whatIsNext.remove(index);
				exceptions.remove(index);
				values.remove(index);
				argumentsOfExecute.add(arg);
			} else {
				argumentsOfExecute.add(arg);
				argObservers.add(new CounterObserver());
			}
		} else {
			int index = -1;
			for (int i = 0; i <= argumentsOfExecute.size() - 1; ++i) {
				if (argumentsOfExecute.get(i).equals(arg)) {
					index = i;
					break;
				}
			}
			if (index >= 0) {
				argObservers.get(index).update();
				String whatFollows = whatIsNext.get(index).remove();
				if (whatFollows.equals("value"))
					return values.get(index).remove();
				else if (whatFollows.equals("exception"))
					throw exceptions.get(index).remove();
			} else {
				return null;
			}
		}
		withArgs = true;
		return null;
	}

	/**
	 * <p>
	 * A method that supplies the previously created mock with elements to
	 * return
	 * </p>
	 * 
	 * @param arg
	 * @return Mock<A,B> the same mock of this type, in order to fix further
	 *         behaviour
	 */
	public Mock<A, B> thenReturn(B arg) {
		if (State.isWatching()) {
			int index = argumentsOfExecute.size() - 1;
			if (index >= 0) {
				B whatToAdd = (B) arg;
				// daca mai avem comenzi anterioare
				if (whatIsNext.size() == argumentsOfExecute.size()) {
					Queue<String> qNext = whatIsNext.get(index);
					qNext.add("value");
					Queue<B> qValues = values.get(index);
					qValues.add(whatToAdd);
				} else {
					Queue<String> q1 = new LinkedList<String>();
					q1.add("value");
					whatIsNext.add(q1);
					Queue<B> q2 = new LinkedList<B>();
					q2.add(whatToAdd);
					values.add(q2);
					Queue<RuntimeException> q3 = new LinkedList<RuntimeException>();
					exceptions.add(q3);
				}
			}
		}
		return this;
	}

	/**
	 * <p>
	 * A method that deals with the setting of the throwing behaviour. When
	 * executing, it will throw the respective exception
	 * </p>
	 * 
	 * @param e
	 *            an exception which inherits RuntimeException
	 * @return Mock<A,B> the same mock object, in order to be further used
	 */
	public Mock<A, B> thenThrow(RuntimeException e) {
		if (State.isWatching()) {
			if (withArgs == true) {
				int index = argumentsOfExecute.size() - 1;
				// daca mai avem comenzi anterioare
				if (whatIsNext.size() == argumentsOfExecute.size()) {
					Queue<String> qNext = whatIsNext.get(index);
					qNext.add("exception");
					Queue<RuntimeException> qExc = exceptions.get(index);
					qExc.add(e);
				} else {
					Queue<String> q1 = new LinkedList<String>();
					q1.add("exception");
					whatIsNext.add(q1);
					Queue<B> q2 = new LinkedList<B>();
					values.add(q2);
					Queue<RuntimeException> q3 = new LinkedList<RuntimeException>();
					q3.add(e);
					exceptions.add(q3);
				}
			} else {
				noArgExeExceptions.add(e);
			}
		}
		return this;
	}

	/**
	 * <p>
	 * A method that sets the watcher on off state, meaning that Mockito will
	 * not listen to any further behavioral settings. The execution may begin
	 * afterwards.
	 * </p>
	 */
	public void andBeDoneWithIt() {
		State.stopWatching();
	}
}
