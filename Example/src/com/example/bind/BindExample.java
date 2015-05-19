package com.example.bind;

import javax.inject.Inject;

import com.jake.sword.Bind;

interface IWidget {
}

@Bind(from=IWidget.class,to=Widget.class)
public class BindExample {
	@Inject
	IWidget widget;
}
