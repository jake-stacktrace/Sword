package com.jake.example.errors.fields;

import java.util.List;

import com.jake.sword.Bind;

@Bind(from=List.class,to=Foo.class)
public class BindIncompatibleType {

}
