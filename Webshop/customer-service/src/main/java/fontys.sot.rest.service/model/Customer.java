package fontys.sot.rest.service.model;

import java.util.Objects;

/**
 * Webshop 2020
 * Customer
 *
 * @author Nikolay Nikolaev
 */
public class Customer {
    private int id;
    private String name;
    private double balance;

    public Customer() {}

    public Customer(int id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id &&
                name.equals(customer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer ID: " + id +
                "\nName: " + name +
                "\nBalance: " + balance;
    }
}
