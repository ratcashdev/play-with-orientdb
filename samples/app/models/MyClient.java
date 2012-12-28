package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import modules.orientdb.Model;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;


@Entity
public class MyClient extends Model {
	@Id
	public Object id;
	
	@Version
	public Object version;
	
	/* name */
	public String title;
	
	@Required
	public String firstName;
	
	@Required
	public String lastName;

	public String middleName;
	public boolean nameVerified = false;

	@Email
	//@OneToMany
	public List<DescribedString> emails;

	//@OneToMany
	public List<DescribedAddress> postalAddresses;

	/**
	 * All phone numbers (home, work, foreign, temporary) etc.
	 */
	public List<DescribedString> phoneNumbers;

}