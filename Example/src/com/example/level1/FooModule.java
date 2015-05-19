package com.example.level1;

import com.jake.sword.Provides;

public class FooModule {
    @Provides
    public IFoo getFoo() {
        return new Foo(new Bar());
    }
}
