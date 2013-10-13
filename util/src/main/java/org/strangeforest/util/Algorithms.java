package org.strangeforest.util;

import java.util.*;

public abstract class Algorithms {

	public static <T> void forEach(Iterable<? extends T> col, Consumer<T> consumer) {
		for (T item : col)
			consumer.accept(item);
	}

	public static <T> int count(Iterable<? extends T> col, Predicate<T> predicate) {
		int count = 0;
		for (T item : col)
			if (predicate.test(item))
				count++;
		return count;
	}

	public static <T> T find(Iterable<? extends T> col, Predicate<T> predicate) {
		for (T item : col)
			if (predicate.test(item))
				return item;
		return null;
	}

	public static <T> boolean exists(Iterable<? extends T> col, Predicate<T> predicate) {
		for (T item : col)
			if (predicate.test(item))
				return true;
		return false;
	}

	public static <T> void filter(Iterable<? extends T> col, Predicate<T> predicate) {
		for (Iterator<? extends T> iter = col.iterator(); iter.hasNext(); )
			if (!predicate.test(iter.next()))
				iter.remove();
	}

	public static <T> List<T> select(Iterable<? extends T> col, Predicate<T> predicate) {
		List<T> result = new ArrayList<>();
		for (T item : col)
			if (predicate.test(item))
				result.add(item);
		return result;
	}

	public static <T1, T2> Iterator<T2> transform(final Iterator<? extends T1> iterator, final Function<T1, T2> function) {
		return new Iterator<T2>() {
			@Override public boolean hasNext() {
				return iterator.hasNext();
			}
			@Override public T2 next() {
				return function.apply(iterator.next());
			}
			@Override public void remove() {
				iterator.remove();
			}
		};
	}

	public static <T1, T2> List<T2> transform(Collection<? extends T1> col, Function<T1, T2> function) {
		List<T2> transformed = new ArrayList<>(col.size());
		for (T1 item : col)
			transformed.add(function.apply(item));
		return transformed;
	}

	public static <T1, T2> List<T2> transformSkipNulls(Collection<? extends T1> col, Function<T1, T2> function) {
		List<T2> transformed = new ArrayList<>(col.size());
		for (T1 item : col) {
			if (item != null)
				transformed.add(function.apply(item));
		}
		return transformed;
	}

	public static <T1, T2> Map<T1, T2> transformToMap(Collection<? extends T1> col, Function<T1, T2> function) {
		Map<T1, T2> map = new HashMap<>(col.size()*3/2);
		for (T1 item : col)
			map.put(item, function.apply(item));
		return map;
	}

	public static <T1, T2> Map<T2, T1> projectToMap(Collection<? extends T1> col, Function<T1, T2> function) {
		Map<T2, T1> map = new HashMap<>(col.size()*3/2);
		for (T1 item : col)
			map.put(function.apply(item), item);
		return map;
	}

	public static <T> Predicate<T> not(Predicate<T> predicate) {
		return new NotPredicate<>(predicate);
	}

	public static <T> Predicate<T> and(Predicate<T> predicate1, Predicate<T> predicate2) {
		return new AndPredicate<>(predicate1, predicate2);
	}

	public static <T> Predicate<T> or(Predicate<T> predicate1, Predicate<T> predicate2) {
		return new OrPredicate<>(predicate1, predicate2);
	}

	public static <T> Predicate<T> xor(Predicate<T> predicate1, Predicate<T> predicate2) {
		return new XorPredicate<>(predicate1, predicate2);
	}

	public static <T1, T2, T3> Function<T1, T3> chain(Function<T1, T2> function1, Function<T2, T3> function2) {
		return new ChainedFunction<>(function1, function2);
	}

	private static class NotPredicate<T> implements Predicate<T> {

		private Predicate<T> predicate;

		public NotPredicate(Predicate<T> predicate) {
			this.predicate = predicate;
		}

		@Override public boolean test(T t) {
			return !predicate.test(t);
		}
	}

	private static class AndPredicate<T> implements Predicate<T> {

		private Predicate<T> predicate1;
		private Predicate<T> predicate2;

		public AndPredicate(Predicate<T> predicate1, Predicate<T> predicate2) {
			this.predicate1 = predicate1;
			this.predicate2 = predicate2;
		}

		@Override public boolean test(T t) {
			return predicate1.test(t) && predicate2.test(t);
		}
	}

	private static class OrPredicate<T> implements Predicate<T> {

		private Predicate<T> predicate1;
		private Predicate<T> predicate2;

		public OrPredicate(Predicate<T> predicate1, Predicate<T> predicate2) {
			this.predicate1 = predicate1;
			this.predicate2 = predicate2;
		}

		@Override public boolean test(T t) {
			return predicate1.test(t) || predicate2.test(t);
		}
	}

	private static class XorPredicate<T> implements Predicate<T> {

		private Predicate<T> predicate1;
		private Predicate<T> predicate2;

		public XorPredicate(Predicate<T> predicate1, Predicate<T> predicate2) {
			this.predicate1 = predicate1;
			this.predicate2 = predicate2;
		}

		@Override public boolean test(T t) {
			return predicate1.test(t) != predicate2.test(t);
		}
	}

	private static class ChainedFunction<T1, T2, T3> implements Function<T1, T3> {

		private Function<T1, T2> function1;
		private Function<T2, T3> function2;

		public ChainedFunction(Function<T1, T2> function1, Function<T2, T3> function2) {
			this.function1 = function1;
			this.function2 = function2;
		}

		@Override public T3 apply(T1 obj) {
			return function2.apply(function1.apply(obj));
		}
	}
}
