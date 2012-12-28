package models;

import javax.persistence.Entity;

@Entity
public class DescribedString extends DescribedData<String> {
	public DescribedString() {
		// TODO Auto-generated constructor stub
	}
	
	public DescribedString(String data, String name) {
		super(data, name);
	}
	
}
