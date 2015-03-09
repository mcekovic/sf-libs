package org.strangeforest.transaction;

import org.strangeforest.util.*;

/**
 * <p><tt>TransactionManager</tt> manages transactions by holding transactional resources
 * in thread local variables.
 * Typical usage with <tt>ConnectionPool</tt> or <tt>ConnectionPoolDataSource</tt>:</p>
 * <blockquote><pre>
 * XXX xxx = ...;
 * Transaction tran = TransactionManager.begin();
 * try {
 *
 *    Connection conn = xxx.getConnection();
 *    // Do JDBC stuff here
 *    if (something is gone wrong)
 *       tran.setRollbackOnly();
 *    // Do JDBC stuff here
 *    conn.close(); // close() is ignored and can be omitted
 *
 *    tran.commit();
 * }
 * catch (Throwable th) {
 *    tran.rollback();
 *    throw th;
 * }
 * </pre></blockquote>
 * <p>Typical usage with <tt>DriverManager</tt>:</p>
 * <blockquote><pre>
 * Transaction tran = TransactionManager.begin();
 * try {
 *    tran.enlistResource(new ConnectionResource(DriverManager.getConnection(...)));
 *
 *    Connection conn = ((ConnectionResource)tran.getResource()).getConnection();
 *    // Do JDBC stuff here
 *    if (something is gone wrong)
 *       tran.setRollbackOnly();
 *    // Do JDBC stuff here
 *    // conn.close() MUST NOT be called
 *
 *    tran.commit();
 * }
 * catch (Throwable th) {
 *    tran.rollback();
 *    throw th;
 * }
 * </pre></blockquote>
 * <p>P.S. If using Propagation.COMMIT_NOT_SUPPORTED_NEW or COMMIT_NEW, <tt>TransactionManager</tt> commit/rollback methods should be used instead of methods on <tt>Transaction</tt> instance.</p>
 */
public abstract class TransactionManager {

	private static ThreadLocal<Transaction> transaction = new ThreadLocal<>();

	public static <R> R execute(TransactionCallback<R> callback) {
		Transaction tran = begin();
		try {
			R result = callback.doInTransaction(tran);
			commit();
			return result;
		}
		catch (Throwable th) {
			rollback();
			throw ExceptionUtil.throwIt(th);
		}
	}

	public static Transaction begin() {
		Transaction tran = getTransaction();
		if (tran == null) {
			tran = new Transaction();
			transaction.set(tran);
		}
		tran.begin();
		return tran;
	}

	public static boolean isInTransaction() {
		return transaction.get() != null;
	}

	public static Transaction getTransaction() {
		return transaction.get();
	}

	public static void commit() {
		getInTransaction().commit();
	}

	public static void rollback() {
		getInTransaction().rollback();
	}

	public static Transaction suspend() {
		Transaction tran = getInTransaction();
		tran.suspend();
		return tran;
	}

	public static void resume(Transaction tran) {
		if (tran != transaction.get())
			throw new IllegalStateException("Cannot resume while in another transaction.");
		tran.resume();
	}

	public static boolean isRollbackOnly() {
		return getInTransaction().isRollbackOnly();
	}

	public static void setRollbackOnly() {
		getInTransaction().setRollbackOnly();
	}


	// Helper methods

	static void setTransaction(Transaction tran) {
		transaction.set(tran);
	}

	static void removeTransaction() {
		transaction.set(null);
	}

	private static Transaction getInTransaction() {
		Transaction tran = transaction.get();
		if (tran != null)
			return tran;
		else
			throw new IllegalStateException("Transaction has not been started.");
	}
}
