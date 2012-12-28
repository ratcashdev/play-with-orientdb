package models;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import play.data.validation.Constraints.Required;

@Entity
@Embeddable
public class PostalAddress {
	@Id
	public Object id;
	
	@Version
	public Object version;
	
	public String zip;
	public String addr1;
	public String addr2;
	
	@Required
	public String country;
	@Required
	public String city;
	
	public PostalAddress() {
		// TODO Auto-generated constructor stub
	}
	
	public PostalAddress(String addr1, String addr2, String city, String zip, String country) {
		setAddress(addr1, addr2, city, zip, country);
	}
	
	public void setAddress(String addr1, String addr2, String city, String zip, String country) {
		setZip(zip).setAddress(addr1, addr2).setCity(city).setCountry(country);
	}
	
	public PostalAddress setCity(String city) {
		this.city = city;
		return this;
	}
	
	public PostalAddress setAddress(String addr1, String addr2) {
		this.addr1 = addr1;
		this.addr2 = addr2;
		return this;
	}
	
	public PostalAddress setZip(String zip) {
		this.zip = zip;
		return this;
	}
	
	public PostalAddress setCountry(String country) {
		this.country = country;
		return this;
	}
	
	public String getAddr1() {
		return addr1;
	}

	public void setAddr1(String addr1) {
		this.addr1 = addr1;
	}

	public String getAddr2() {
		return addr2;
	}

	public void setAddr2(String addr2) {
		this.addr2 = addr2;
	}

	public String getZip() {
		return zip;
	}

	public String getCountry() {
		return country;
	}

	public String getCity() {
		return city;
	}

}
