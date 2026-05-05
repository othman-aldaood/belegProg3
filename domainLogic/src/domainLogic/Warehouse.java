package domainLogic;

import java.util.ArrayList;

import java.util.List;

/*
* Lager
* */
public class Warehouse {

    // die kapazität für under Lager
    private int capacity;
    private List<Customer>  customers;



    public Warehouse(int capacity) {
        this.capacity = capacity;
        this.customers = new ArrayList<>();
    }



}
