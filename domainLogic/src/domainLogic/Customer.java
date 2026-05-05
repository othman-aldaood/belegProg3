package domainLogic;

import java.util.List;

/**
 *
 */
public class Customer {

    private String name;

    /**
     *
     * @param name
     */
    public Customer(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param customerName
     * @retur
     */
    public boolean addCustomer(String customerName) {
        try {
            if (this.name.equals(customerName))
                return false;
            else
                this.name = customerName;
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}