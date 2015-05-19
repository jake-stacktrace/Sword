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

To use it, download sword.jar and add this to your build.gradle:

Something like:
<pre>
apt files('sword.jar')
</pre>
Supported Tags:
  @Inject, @Provides, @Named, @Qualifier, @Singleton, @Mock
