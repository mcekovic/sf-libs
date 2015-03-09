package org.strangeforest.annotation;

public enum Propagation {
	REQUIRED,       // Support a current transaction, create a new one if none exists.
	SUPPORTS,       // Support a current transaction, execute non-transactionally if none exists.
	MANDATORY,      // Support a current transaction, throw an exception if none exists.
	REQUIRES_NEW,   // Create a new transaction, suspending the current transaction if one exists.
	NOT_SUPPORTED,  // Execute non-transactionally, suspending the current transaction if one exists.
	NEVER,	       // Execute non-transactionally, throw an exception if a transaction exists.
	COMMIT_NOT_SUPPORTED_NEW, // Execute non-transactionally, committing the current transaction if one exists, creating new transaction after the call if one existed before the call
	COMMIT_NEW      // Commits the current transaction if one exists and creates new one, otherwise behave as REQUIRED
}
