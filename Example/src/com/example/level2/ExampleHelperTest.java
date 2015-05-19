package com.example.level2;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.jake.sword.SwordInjector;

public class ExampleHelperTest {
    private ExampleHelper exampleHelper;

    @Before
    public void setUp() {
        exampleHelper = new ExampleHelper();
        SwordInjector.inject(exampleHelper);
    }

    @Test
    public void testExampleHelperPopulatesInjectionsToTwoLevels() {
        assertNotNull(exampleHelper.myExample.getFooImpl());
    }

    @Test
    public void testMultipleLevels() throws Exception {
		assertNotNull(exampleHelper.myExample.getFoo().getBar());
	}
}
