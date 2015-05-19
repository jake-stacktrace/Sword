# Sword
Sword is a compile-time dependency injection framework. 

class Blah {
   @Inject
   protected Foo foo;
}

Blah blah = new Blah();
SwordInjector.inject(blah);
// blah.foo now has a new instance of Foo

Also supported:
Blah blah = SwordInjector.get(Blah.class);

In order to create Foo, Foo must have members or a constructor marked with @Inject.

class Foo {
  @Inject
  public Foo() {
  }
}

Injected members must not be private or final. If that is desired, use injected constructors.

To use it, download sword.jar and add this to your build.gradle:

Something like:
apt files('sword.jar')

Supported Tags:
  @Inject, @Provides, @Named, @Qualifier, @Singleton, @Mock
