package com.example.named;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class NamedExample {
    @Inject
    @Named("A words")
    protected List<String> aWords;
    @Inject
    @Named("B words")
    protected List<String> bWords;
}
