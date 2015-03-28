package test.orm;

import org.strangeforest.orm.*;

public class SimpleTestEntity extends DomainEntity<Long, SimpleTestEntity> {

	private String name;

	public SimpleTestEntity() {
		this(null);
	}

	public SimpleTestEntity(Long id) {
		super(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
