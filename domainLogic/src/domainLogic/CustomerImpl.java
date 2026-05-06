package domainLogic;

import administration.Customer;

import java.util.Objects;

public class CustomerImpl implements Customer {

    private String name;

    // constructor
    public CustomerImpl(String name) {
        this.name = name;
    }

    // getter for the name
    public String getName() {
        return name;
    }

    // setter for the name
    public void setName(String name) {
        this.name = name;
    }

    //Quelle: https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CustomerImpl customer = (CustomerImpl) o;
        return Objects.equals(name, customer.name);
    }

    //Quelle: https://stackoverflow.com/questions/11742593/what-is-the-hashcode-for-a-custom-class-having-just-two-int-properties
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public boolean removeCustomer(Customer customer) {

        if (this.name.contains(customer)) {
            this.name.remove(customer);
            return true;
        }
        return false;
    }
}