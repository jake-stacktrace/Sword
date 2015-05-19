# Sword
Sword is a compile-time dependency injection framework. 
<pre>
class Blah {
   @Inject
   protected Foo foo;
}

Blah blah = new Blah();
SwordInjector.inject(blah);
// blah.foo now has a new instance of Foo
</pre>
Also supported:
<pre>
Blah blah = SwordInjector.get(Blah.class);
</pre>
In order to create Foo, Foo must have members or a constructor marked with @Inject.

<pre>
class Foo {
  @Inject
  public Foo() {
  }
}
</pre>

Injected members must not be private or final. If that is desired, use injected constructors.
Instances can also be provided:

<pre>
class FooModule {
  @Provides
  public Foo createFoo() {
    return new Foo();
  }
}
</pre>

@Named is supported too:
<pre>
class Blah {
  @Inject
  @Named("foo1")
  protected Foo foo;
}

class FooModule {
  @Provides
  @Named("foo1")
  public Foo createFooOne() {
    return new Foo();
  }
  @Provides
  @Named("foo2")
  public Foo createFooTwo() {
    return new Foo();
  }
}
</pre>

If @Named isn't enough, you can create your own qualifiers:
<pre>
@Qualifier
public @interface Slow {
}
@Qualifier
public @interface Fast {
}
public class QualifierExample {
	@Inject
	@Fast
	protected String fast;
	
	@Inject
	@Slow
	protected String slow;
}
class SpeedModule {
	@Provides
	@Fast
	public String getFast() { return "fast"; }
	
	@Provides
	@Slow
	public String getSlow() { return "slow"; }
}
</pre>
Sword also knows about mockito annotations:
<pre>
public class Blah {
	@Inject
	protected Receiver receiver;
}
public class Receiver {
	@Inject
	Receiver() {}

	public void receive() {
	}
}
public class BlahTest {
	@Inject
	protected Blah blah;
	@Mock
	protected Receiver receiver;

   @Before
   public void setUp() {
		MockitoAnnotations.initMocks(this);
		SwordInjector.inject(this);
   }
	@Test
	public void testBlah() {
		blah.callReceiver();
		verify(receiver).receive();
   }
}
</pre>

Sword can see that @Mock Receiver receiver matches @Inject Receiver receiver and injects Blah's receiver with the mocked out version. This only works to one level.

Download <a href="https://github.com/jake-stacktrace/Sword/tree/master/com.jake.sword">sword.jar</a> and add this to your build.gradle:

Something like:
<pre>
apt files('sword.jar')
</pre>

Eclipse Support:
Since Eclipse has incremental compilation, it fools Sword. You will have to clean your project files a lot to use it. This will be addressed in a later release.

Supported Tags:
  @Inject, @Provides, @Named, @Qualifier, @Singleton, @Mock
