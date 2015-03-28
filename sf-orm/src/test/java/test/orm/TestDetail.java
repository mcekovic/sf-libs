package test.orm;

import org.strangeforest.orm.*;

public class TestDetail extends DomainValue<TestDetail> {

	private int detailId;
	private String name;

	public TestDetail(int detailId) {
		super();
		this.detailId = detailId;
	}

	public int getDetailId() {
		return detailId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
