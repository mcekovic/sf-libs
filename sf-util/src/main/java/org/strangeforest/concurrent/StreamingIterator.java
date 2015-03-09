package org.strangeforest.concurrent;

import java.util.*;
import java.util.concurrent.*;

import org.strangeforest.util.*;

public class StreamingIterator<T> implements Iterator<T> {

	private final BlockingQueue buffer;
	private volatile Object current;
	private volatile Throwable error;

	private static final Object END_MARKER = new Object();

	public StreamingIterator(int bufferSize) {
		this(new ArrayBlockingQueue<T>(bufferSize+1));
	}

	public StreamingIterator(BlockingQueue<T> buffer) {
		super();
		this.buffer = buffer;
	}

	public void start(Runnable task) {
		ThreadUtil.runInThread(new RunnableProxy(task));
	}

	public void start(String threadName, Runnable task) {
		ThreadUtil.runInThread(threadName, new RunnableProxy(task));
	}

	public void start(Executor executor, Runnable task) {
		executor.execute(new RunnableProxy(task));
	}

	public void awaitStart() {
		if (hasNext())
			checkError();
	}

	public void awaitStartInterruptibly() throws InterruptedException {
		if (hasNextInterruptibly())
			checkError();
	}

	public void awaitStart(long timeout, TimeUnit unit) throws InterruptedException {
		if (hasNext(timeout, unit))
			checkError();
	}

	public void put(T item) throws InterruptedException {
		buffer.put(item);
	}

	private final class RunnableProxy implements Runnable {

		private final Runnable task;

		public RunnableProxy(Runnable task) {
			super();
			this.task = task;
		}

		@Override public void run() {
			try {
				task.run();
			}
			catch (Throwable th) {
				error = th;
			}
			finally {
				try {
					buffer.put(END_MARKER);
				}
				catch (InterruptedException ignored) {}
			}
		}
	}

	private void checkError() {
		if (error != null) {
			current = END_MARKER;
			Throwable error = this.error;
			this.error = null;
			error.fillInStackTrace();
			ExceptionUtil.throwIt(error);
		}
	}


	// Iterator

	@Override public boolean hasNext() {
		if (current == null) {
			try {
				current = buffer.take();
			}
			catch (InterruptedException ex) {
				current = END_MARKER;
			}
		}
		return hasNextOrError();
	}

	public boolean hasNextInterruptibly() throws InterruptedException {
		if (current == null)
			current = buffer.take();
		return hasNextOrError();
	}

	public boolean hasNext(long timeout, TimeUnit unit) throws InterruptedException {
		if (current == null)
			current = buffer.poll(timeout, unit);
		return hasNextOrError();
	}

	private boolean hasNextOrError() {
		return current != END_MARKER || error != null;
	}

	@Override public T next() {
		if (hasNext()) {
			checkError();
			T current = (T)this.current;
			this.current = null;
			return current;
		}
		else
			throw new NoSuchElementException();
	}

	@Override public void remove() {
		throw new UnsupportedOperationException();
	}
}
