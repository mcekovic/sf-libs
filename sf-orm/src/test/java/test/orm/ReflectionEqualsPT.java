package test.orm;

import org.apache.commons.lang3.builder.*;

import org.strangeforest.orm.*;
import org.strangeforest.util.*;

import static org.testng.Assert.*;
import static test.orm.TestMakers.*;

public class ReflectionEqualsPT {

	private static final int WARM_COUNT =   1000;
	private static final int COUNT      = 200000;

	public static void main(String[] args) throws InterruptedException {
		reflectionEqualsCheckerTest();
		equalsBuilderTest();
	}

	private static void equalsBuilderTest() throws InterruptedException {
		repeatEqualsBuilder(WARM_COUNT);
		Thread.sleep(1000L);
		StopWatch watch = new StopWatch();
		repeatEqualsBuilder(COUNT);
		System.out.println(watch.time());
	}

	private static void reflectionEqualsCheckerTest() throws InterruptedException {
		repeatReflectionEqualsChecker(WARM_COUNT);
		Thread.sleep(1000L);
		StopWatch watch = new StopWatch();
		repeatReflectionEqualsChecker(COUNT);
		System.out.println(watch.time());
	}

	private static void repeatEqualsBuilder(int count) {
		TestAggregate a1 = makeAggregate();
		TestAggregate a2 = makeAggregate();
		for (int i = 0; i < count; i++)
			assertTrue(EqualsBuilder.reflectionEquals(a1, a2));
	}

	private static void repeatReflectionEqualsChecker(int count) {
		TestAggregate a1 = makeAggregate();
		TestAggregate a2 = makeAggregate();
		for (int i = 0; i < count; i++)
			assertTrue(EqualsByValueUtil.reflectionEqualsByValue(a1, a2));
	}
}
