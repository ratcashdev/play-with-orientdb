package models;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.Version;

/**
 * Basic abstract data class holding a (dataValue, dataName) pair type of information.
 * <ul>
 * <li> dataValue - is the payload</li>
 * <li> dataName - is the short description of the data. Ideally, one word (like: home, work, newsletter), et.</li>
 * </ul>
 * @param <T>
 */
public abstract class DescribedData<T> {
	@Id
	public Object id;
	
	@Version
	public Object version;

	/**
	 * The short (preferably one word) description of the data in @dataValue
	 */
	public String dataName;
	
	/**
	 * The payload of this class. This can be anything. IBAN account number, URL, email address, postal address
	 * marketing opt-in information and others.
	 */
	@Embedded
	public T dataValue;
	
	public DescribedData() {
		// empty constructor
	}
	
	public DescribedData (T newValue, String name) {
		setDataName(name);
		setDataValue(newValue);
	}
	
	public String getDataName() {
		return dataName;
	}

	public DescribedData<T> setDataName(String dataName) {
		this.dataName = dataName;
		return this;
	}

	public DescribedData<T> setDataValue(T newValue) {
		dataValue = newValue;
		return this;
	}

	public T getDataValue() {
		return dataValue;
	}
}
