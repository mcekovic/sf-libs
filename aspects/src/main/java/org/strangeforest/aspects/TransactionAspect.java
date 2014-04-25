package org.strangeforest.aspects;

import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.*;
import org.strangeforest.annotation.*;
import org.strangeforest.transaction.*;

@Aspect
public class TransactionAspect extends AnnotationDrivenAspectSupport<Transactional, TransactionAspect.TransactionalInfo> {

	@Pointcut("execution(public * ((@org.strangeforest.annotation.Transactional *)+).*(..)) && @this(org.strangeforest.annotation.Transactional)")
	private void executionOfAnyPublicMethodInTransactionalType() {}

	@Pointcut("execution(* *(..)) && @annotation(org.strangeforest.annotation.Transactional)")
	private void executionOfTransactionalMethod() {}

	@Pointcut("executionOfAnyPublicMethodInTransactionalType() || executionOfTransactionalMethod()")
	private void transactionalMethodExecution() {}

	@Around("transactionalMethodExecution()")
	public Object doInTransaction(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		TransactionalInfo tranInfo = getAnnotationInfo(signature, Transactional.class);
		switch (tranInfo.propagation) {

			case REQUIRED:
				return TransactionManager.execute(new JoinPointProceedCallback(joinPoint, tranInfo.rollbackOnly));

			case SUPPORTS:
				if (tranInfo.rollbackOnly && TransactionManager.isInTransaction())
					TransactionManager.setRollbackOnly();
				return joinPoint.proceed();

			case MANDATORY: {
				if (!TransactionManager.isInTransaction())
					throw new TransactionException("Transaction is mandatory for: " + signature.getName());
				if (tranInfo.rollbackOnly)
					TransactionManager.setRollbackOnly();
				return joinPoint.proceed();
			}

			case REQUIRES_NEW: {
				Transaction tran = TransactionManager.getTransaction();
				if (tran != null)
					tran.suspend();
				try {
					return TransactionManager.execute(new JoinPointProceedCallback(joinPoint, tranInfo.rollbackOnly));
				}
				finally {
					if (tran != null)
						tran.resume();
				}
			}

			case NOT_SUPPORTED: {
				Transaction tran = TransactionManager.getTransaction();
				if (tran != null) {
					tran.suspend();
					try {
						return joinPoint.proceed();
					}
					finally {
						tran.resume();
					}
				}
				else
					return joinPoint.proceed();
			}

			case NEVER: {
				if (TransactionManager.isInTransaction())
					throw new TransactionException("Cannot invoke under transaction context: " + signature.getName());
				return joinPoint.proceed();
			}

			case COMMIT_NOT_SUPPORTED_NEW: {
				Transaction tran = TransactionManager.getTransaction();
				if (tran != null) {
					tran.commit();
					try {
						return joinPoint.proceed();
					}
					finally {
						TransactionManager.begin();
					}
				}
				else
					return joinPoint.proceed();
			}

			case COMMIT_NEW: {
				Transaction tran = TransactionManager.getTransaction();
				if (tran != null) {
					tran.commit();
					TransactionManager.begin();
					return joinPoint.proceed();
				}
				else
					return TransactionManager.execute(new JoinPointProceedCallback(joinPoint, tranInfo.rollbackOnly));
			}

			default:
				throw new TransactionException("Transaction propagation type not supported: " + tranInfo.propagation);
		}
	}

	@Override protected TransactionalInfo getAnnotationInfo(Transactional tranAnn, TransactionalInfo tranInfo) {
		if (tranInfo == null)
			tranInfo = new TransactionalInfo();
		if (tranAnn != null) {
			Propagation propagation = tranAnn.propagation();
			if (propagation != Propagation.REQUIRED)
				tranInfo.propagation = propagation;
			boolean rollbackOnly = tranAnn.rollbackOnly();
			if (rollbackOnly)
				tranInfo.rollbackOnly = true;
		}
		return tranInfo;
	}

	private static final class JoinPointProceedCallback implements TransactionCallback<Object> {

		private final ProceedingJoinPoint joinPoint;
		private final boolean rollbackOnly;

		public JoinPointProceedCallback(ProceedingJoinPoint joinPoint, boolean rollbackOnly) {
			this.joinPoint = joinPoint;
			this.rollbackOnly = rollbackOnly;
		}

		@Override public Object doInTransaction(Transaction tran) throws Throwable {
			if (rollbackOnly)
				TransactionManager.setRollbackOnly();
			return joinPoint.proceed();
		}
	}

	public static final class TransactionalInfo {
		private Propagation propagation = Propagation.REQUIRED;
		private boolean rollbackOnly = false;
	}
}
