package gov.noaa.pmel.sdimetadata.person;

/**
 * Standard information about a person.
 * Super class for Submitter.
 */
public class Investigator implements Cloneable {
    protected String lastName;
    protected String firstName;
    protected String middleInitials;
    protected String organization;
    protected String address;
    protected String phone;
    protected String email;

    /**
     * Create with empty strings for all fields
     */
    public Investigator() {
        lastName = "";
        firstName = "";
        middleInitials = "";
        organization = "";
        address = "";
        phone = "";
        email = "";
    }

    /**
     * @return the last name; never null but may be empty
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *         assign as the last name; if null, an empty string is assigned
     */
    public void setLastName(String lastName) {
        this.lastName = (lastName != null) ? lastName.trim() : "";
    }

    /**
     * @return the first name; never null but may be empty
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *         assign as the first name; if null, an empty string is assigned
     */
    public void setFirstName(String firstName) {
        this.firstName = (firstName != null) ? firstName.trim() : "";
    }

    /**
     * @return the middle initial(s); never null but may be empty
     */
    public String getMiddleInitials() {
        return middleInitials;
    }

    /**
     * @param middleInitials
     *         assign as the middle initial(s); if null, an empty string is assigned
     */
    public void setMiddleInitials(String middleInitials) {
        this.middleInitials = (middleInitials != null) ? middleInitials.trim() : "";
    }

    /**
     * @return the organization name; never null but may be empty
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization
     *         assign as the organization; if null, an empty string is assigned
     */
    public void setOrganization(String organization) {
        this.organization = (organization != null) ? organization.trim() : "";
    }

    /**
     * @return the address; never null but may be empty
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *         assign as the address; if null, an empty string is assigned
     */
    public void setAddress(String address) {
        this.address = (address != null) ? address.trim() : "";
    }

    /**
     * @return the phone number; never null but may be empty
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *         assign as the phone number; if null, an empty string is assigned
     */
    public void setPhone(String phone) {
        this.phone = (phone != null) ? phone.trim() : "";
    }

    /**
     * @return the e-mail address; never null but may be empty
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *         assign as the e-mail address; if null, an empty string is assigned
     */
    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    /**
     * @return whether all the required fields are assigned with valid values.
     */
    public boolean isValid() {
        if ( lastName.isEmpty() )
            return false;
        if ( firstName.isEmpty() )
            return false;
        return true;
    }

    @Override
    public Investigator clone() {
        Investigator dup;
        try {
            dup = (Investigator) super.clone();
        } catch ( CloneNotSupportedException ex ) {
            throw new RuntimeException(ex);
        }
        dup.lastName = lastName;
        dup.firstName = firstName;
        dup.middleInitials = middleInitials;
        dup.organization = organization;
        dup.address = address;
        dup.phone = phone;
        dup.email = email;
        return dup;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( !(obj instanceof Investigator) )
            return false;

        Investigator other = (Investigator) obj;

        if ( !lastName.equals(other.lastName) )
            return false;
        if ( !firstName.equals(other.firstName) )
            return false;
        if ( !middleInitials.equals(other.middleInitials) )
            return false;
        if ( !organization.equals(other.organization) )
            return false;
        if ( !address.equals(other.address) )
            return false;
        if ( !phone.equals(other.phone) )
            return false;
        if ( !email.equals(other.email) )
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = lastName.hashCode();
        result = result * prime + firstName.hashCode();
        result = result * prime + middleInitials.hashCode();
        result = result * prime + organization.hashCode();
        result = result * prime + address.hashCode();
        result = result * prime + phone.hashCode();
        result = result * prime + email.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Investigator{" +
                "lastName='" + lastName + "'" +
                ", firstName='" + firstName + "'" +
                ", middleInitials='" + middleInitials + "'" +
                ", organization='" + organization + "'" +
                ", address='" + address + "'" +
                ", phone='" + phone + "'" +
                ", email='" + email + "'" +
                '}';
    }

}

