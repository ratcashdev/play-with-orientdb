package models;

public class DescribedAddress extends DescribedData<PostalAddress> {
	public DescribedAddress() {
		// TODO Auto-generated constructor stub
	}
	
	public DescribedAddress(PostalAddress data, String name) {
		super(data, name);
	}
}
