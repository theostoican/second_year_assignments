import org.junit.Assert;
import org.junit.Before;

public class MockitoTests {

	private Mockable<Integer, Character> mock;

	@Before
	public void setup() {
		mock = Mockito.mock(new Mockable<Integer, Character>() {
			public Unit execute() throws RuntimeException {
				System.out.println("without param");
				return new Unit();
			};

			public Character execute(Integer arg) throws RuntimeException {
				System.out.println("with param");
				return 'c';
			};
		});
	}

	@org.junit.Test
	public void testSimpleArgExecution() {
		Mockito.watch().when(mock.execute(3)).thenReturn('c').andBeDoneWithIt();
		char c = mock.execute(3);
		Assert.assertEquals(c, 'c');
	}

	@org.junit.Test
	public void complicatedArgExecution() {
		Mockito.watch().when(mock.execute(2)).thenReturn('x').thenReturn('m')
				.thenReturn('a').thenReturn('s').andBeDoneWithIt();
		String test = new String(Character.toString(mock.execute(2))
				+ Character.toString(mock.execute(2))
				+ Character.toString(mock.execute(2))
				+ Character.toString(mock.execute(2)));
		Assert.assertEquals(test, "xmas");
	}

	@org.junit.Test
	public void mixedArgExecution() {
		Mockito.watch().when(mock.execute(0)).thenReturn('x')
				.thenThrow(new RuntimeException("excellent")).thenReturn('a')
				.thenThrow(new IndexOutOfBoundsException("incredible"))
				.andBeDoneWithIt();
		char c = mock.execute(0);
		Assert.assertEquals(c, 'x');
		try {
			mock.execute(0);
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertEquals(e.getMessage(), "excellent");
		}
		c = mock.execute(0);
		Assert.assertEquals(c, 'a');
		try {
			mock.execute(0);
			Assert.fail();
		} catch (IndexOutOfBoundsException e) {
			Assert.assertEquals(e.getMessage(), "incredible");
		}
	}

	@org.junit.Test
	public void executeWithoutWatcher() {
		Assert.assertEquals(mock.execute(2), null);
	}

	@org.junit.Test
	public void moreArguments() {
		Mockito.watch().when(mock.execute(2)).thenReturn('z').andBeDoneWithIt();
		Mockito.watch().when(mock.execute(3))
				.thenThrow(new RuntimeException("moreArg")).andBeDoneWithIt();
		char c = mock.execute(2);
		Assert.assertEquals(c, 'z');
		try {
			mock.execute(3);
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertEquals(e.getMessage(), "moreArg");
		}
	}

	@org.junit.Test
	public void moreMixedArguments() {
		Mockito.watch().when(mock.execute(10)).thenReturn('z')
				.thenThrow(new NullPointerException("null")).andBeDoneWithIt();
		Mockito.watch().when(mock.execute(4))
				.thenThrow(new RuntimeException("moreArg")).thenReturn('a')
				.andBeDoneWithIt();
		char c = mock.execute(10);
		Assert.assertEquals(c, 'z');
		try {
			mock.execute(10);
			Assert.fail();
		} catch (NullPointerException e) {
			Assert.assertEquals(e.getMessage(), "null");
		}
		try {
			mock.execute(4);
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertEquals(e.getMessage(), "moreArg");
		}
		c = mock.execute(4);
		Assert.assertEquals(c, 'a');
	}

	@org.junit.Test
	public void testSimpleNoArgExecution() {
		Mockito.watch().when(mock.execute())
				.thenThrow(new NullPointerException("gmail")).andBeDoneWithIt();
		;
		try {
			mock.execute();
			Assert.fail();
		} catch (NullPointerException e) {
			Assert.assertEquals(e.getMessage(), "gmail");
		}
	}

	@org.junit.Test
	public void multipleNoArgExecution() {
		Mockito.watch().when(mock.execute())
				.thenThrow(new NullPointerException("mihai"))
				.thenThrow(new IndexOutOfBoundsException("eminescu"))
				.andBeDoneWithIt();
		try {
			mock.execute();
			Assert.fail();
		} catch (NullPointerException e) {
			Assert.assertEquals(e.getMessage(), "mihai");
		}
		try {
			mock.execute();
			Assert.fail();
		} catch (IndexOutOfBoundsException e) {
			Assert.assertEquals(e.getMessage(), "eminescu");
		}
	}

	@org.junit.Test
	public void verifyLeastOnce() {
		Mockito.watch().when(mock.execute(2)).thenReturn('a').thenReturn('b')
				.thenReturn('c').andBeDoneWithIt();
		try {
			Mockito.verify(mock, Mockito.atLeastOnce()).execute(2);
			Assert.fail();
		} catch (MockitoException e) {

		}
		mock.execute(2);
		mock.execute(2);
		mock.execute(2);
		try {
			Mockito.verify(mock, Mockito.atLeastOnce()).execute(2);
		} catch (MockitoException e) {
			Assert.fail();
		}
	}

	@org.junit.Test
	public void verifyExactlyOnce() {
		Mockito.watch().when(mock.execute(2)).thenReturn('a').andBeDoneWithIt();
		try {
			Mockito.verify(mock, Mockito.exactlyOnce()).execute(2);
			Assert.fail();
		} catch (MockitoException e) {

		}
		mock.execute(2);
		try {
			Mockito.verify(mock, Mockito.exactlyOnce()).execute(2);
		} catch (MockitoException e) {
			Assert.fail();
		}
	}

	@org.junit.Test
	public void verifyTimes() {
		Mockito.watch().when(mock.execute(2)).thenReturn('a').thenReturn('x')
				.thenReturn('b').andBeDoneWithIt();
		try {
			Mockito.verify(mock, Mockito.times(3)).execute(2);
			Assert.fail();
		} catch (MockitoException e) {

		}
		mock.execute(2);
		mock.execute(2);
		mock.execute(2);
		try {
			Mockito.verify(mock, Mockito.times(3)).execute(2);
		} catch (MockitoException e) {
			Assert.fail();
		}
	}

	@org.junit.Test
	public void multipleMocks() {
		Mockable<Integer, Character> mock1 = Mockito
				.mock(new Mockable<Integer, Character>() {
					public Unit execute() throws RuntimeException {
						System.out.println("without param");
						return new Unit();
					};

					public Character execute(Integer arg)
							throws RuntimeException {
						System.out.println("with param");
						return 'c';
					};
				});
		Mockable<Integer, String> mock2 = Mockito
				.mock(new Mockable<Integer, String>() {
					public Unit execute() throws RuntimeException {
						System.out.println("without param2");
						return new Unit();
					};

					public String execute(Integer arg) throws RuntimeException {
						System.out.println("with param2");
						return "str";
					};
				});
		Mockito.watch().when(mock1.execute(1)).thenReturn('c')
				.andBeDoneWithIt();
		Mockito.watch().when(mock2.execute(1)).thenReturn("str")
				.andBeDoneWithIt();
		char c = mock1.execute(1);
		Assert.assertEquals(c, 'c');
		String s = mock2.execute(1);
		Assert.assertEquals("str", s);
	}

	@org.junit.Test
	public void multipleMocksVerify() {
		Mockable<Integer, Character> mock1 = Mockito
				.mock(new Mockable<Integer, Character>() {
					public Unit execute() throws RuntimeException {
						System.out.println("without param");
						return new Unit();
					};

					public Character execute(Integer arg)
							throws RuntimeException {
						System.out.println("with param");
						return 'c';
					};
				});
		Mockable<Integer, String> mock2 = Mockito
				.mock(new Mockable<Integer, String>() {
					public Unit execute() throws RuntimeException {
						System.out.println("without param2");
						return new Unit();
					};

					public String execute(Integer arg) throws RuntimeException {
						System.out.println("with param2");
						return "str";
					};
				});
		Mockito.watch().when(mock1.execute(1)).thenReturn('c')
				.thenThrow(new NullPointerException("mock1")).andBeDoneWithIt();
		Mockito.watch().when(mock2.execute(1)).thenReturn("str")
				.thenThrow(new RuntimeException("mock2")).thenReturn("str2")
				.andBeDoneWithIt();
		mock1.execute(1);
		try {
			mock1.execute(1);
			Assert.fail();
		} catch (NullPointerException e) {
			Assert.assertEquals(e.getMessage(), "mock1");
		}
		try {
			Mockito.verify(mock1, Mockito.times(2)).execute(1);
		} catch (MockitoException e) {
			Assert.fail();
		}
		mock2.execute(1);
		try {
			mock2.execute(1);
			Assert.fail();
		} catch (RuntimeException e) {
			Assert.assertEquals(e.getMessage(), "mock2");
		}
		mock2.execute(1);
		try {
			Mockito.verify(mock2, Mockito.times(3)).execute(1);
		} catch (MockitoException e) {
			Assert.fail();
		}
	}
}
