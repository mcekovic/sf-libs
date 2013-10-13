package org.strangeforest.concurrent;

import java.util.concurrent.*;

/**
 * <p>This is a lock manager that has equivalent methods as <tt>java.util.concurrent.locks.Lock</tt> interface.</p>
 */
public interface LockManager<K> {

	/**
	 * Locks the object associated with specified key.
	 * This method will block if some other thread has locked the object and will wait until that thread unlocks the object.
	 * @param key key of object to be locked.
	 */
	void lock(K key);

	/**
	 * Locks the object associated with specified key.
	 * This method will block if some other thread has locked the object and will wait
	 * until that thread unlocks the object or current thread is interrupted.
	 * @param key key of object to be locked.
	 * @throws InterruptedException if current thread is interrupted.
	 */
	void lockInterruptibly(K key) throws InterruptedException;

	/**
	 * Tries to lock the object associated with specified key.
	 * This method will return immediately.
	 * @param key key of object to be locked.
	 * @return <tt>true</tt> if object is successfully locked, otherwise <tt>false</tt>.
	 */
	boolean tryLock(K key);

	/**
	 * Locks the object associated with specified key. This method will block if
	 * some other thread has locked the object and will wait until that thread
	 * unlocks the object or <tt>timeout</tt> specified time units have passed.
	 * @param key key of object to be locked.
	 * @param timeout number of milliseconds to wait if object is already locked.
	 * @param unit time unit.
	 * @return <tt>true</tt> if object is successfully locked, otherwise <tt>false</tt>.
	 * @throws InterruptedException if current thread is interrupted.
	 */
	boolean tryLock(K key, long timeout, TimeUnit unit) throws InterruptedException;

	/**
	 * Unlocks the object with specified key.
	 * @param key key of object to be unlocked.
	 */
	void unlock(K key);

	/**
	 * Checks if the object associated with specified key is locked.
	 * @param key key of the object to be checked.
	 * @return <tt>true</tt> if object is locked, otherwise <tt>false</tt>.
	 */
	boolean isLocked(K key);
}
