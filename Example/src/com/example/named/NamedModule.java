package com.example.named;

import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import com.jake.sword.Provides;

public class NamedModule {
    @Provides
    @Named("A words")
    public List<String> getAWords() {
        return Arrays.asList("Apple", "Alligator");
    }

    @Provides
    @Named("B words")
    List<String> getBWords() {
        return Arrays.asList("Bread", "Blue");
    }
}
