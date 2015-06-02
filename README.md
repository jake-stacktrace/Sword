# Sword
Sword is a statically typed compile-time dependency injection framework for Android and Java 

Installing Sword

Add this to your build.gradle:

<pre>
repositories {
	jcenter()
}
dependencies {
    apt 'com.jake:sword:0.3.0'
}
</pre>

In order to show you how Sword works, let's start with a simple example.

<pre>
class Blah {
   @Inject
   protected Foo foo;
}
</pre>
SwordInjector supports just 2 methods. inject() and get(Class)
<pre>
Blah blah = new Blah();
SwordInjector.inject(blah);

// blah.foo now has a new instance of Foo. Or you can just do:
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

Injected members must not be private or final. If they are Sword raises an error.
Instances can also be provided:

<pre>
class FooModule {
  @Provides
  public Foo createFoo() {
    return new Foo();
  }
}
</pre>

If an instance is bound from more than one place, Sword raises an error. 

If a class or interface is bound from both @Provides and an @Inject constructor, that's an error. 

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

Sword also knows about mockito annotations to help with testing:

<pre>
public class Caller {
   @Inject
   protected Receiver receiver;
	
   public void callReceiver() {
	receiver.receive();
   }
}
public class Receiver {
	@Inject
	Receiver() {}

	public void receive() {
	}
}
public class BaseTest {
  protected void setupTest() {
	MockitoAnnotations.initMocks(this);
	SwordInjector.inject(this);
   }
}
public class CallerTest extends BaseTest {
   @Inject
   protected Caller caller;
   @Mock
   protected Receiver receiver;

   @Before
   public void setUp() {
     super.setupTest();
   }
   @Test
   public void testCallerCallsReceiver() {
	caller.callReceiver();
	verify(receiver).receive();
   }
}
</pre>

Sword can see that @Mock Receiver receiver matches @Inject Receiver receiver and injects Caller's receiver with the mocked out version. This only works to one level.

Notice how Sword was able to inject from the subclass. It was given a BaseTest, and figured out it was an instance of CallerTest, even though BaseTest is not abstract. Both methods SwordInjector.inject(BaseTest) and SwordInjector.inject(CallerTest) methods are generated.

In Android, you can do your injection in an Activity base class.

<p><strong>Overriding</strong></p>
What if you wanted to have UAT tests that had part of the system mocked out, just the external dependencies.

<pre>
class ProductionModule {
	@Provides
	public IRestService getRestService() {
		return new RestService("www.blah.com/rest");
	}

	@Provides
	public ITrackingService getTrackingService() {
		return new TrackingService("www.blah.com/tracking");
	}
}
</pre>

For QA Testing, we want to point to an internal tracker so it doesn't affect our user statistics we are tracking. We also want a Rest service that has a local set of data. In these builds we also have this module:

<pre>
class TestModule {
	@Provides(overrides=true)
	public IRestService getRestService() {
		return new RestService("localhost:8080/rest");
	}
	@Provides(overrides=true)
	public ITrackingService getTrackingService() {
		return new TestTrackingService("localtrackingserver/tracking");
	}
}
</pre>

This TestModule is only included in our test builds and when we run our automated UAT tests. In Android, you can use build types.

Eclipse Support:<br />
Since Eclipse has incremental compilation, it fools Sword. You will have to clean your project files a lot to use it. This will be addressed in a later release.

Supported Annotations:<br />
  @Inject, @Provides, @Named, @Qualifier, @Singleton, @Mock
