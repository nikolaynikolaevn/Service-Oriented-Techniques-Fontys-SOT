package fontys.sot.rest.service.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Webshop 2020
 * ProductOrder
 *
 * @author Nikolay Nikolaev
 */
public class ProductOrder {
    private static int counter = 1;
    private int id;
    private Customer customer;
    private List<Item> items;
    private Date time;

    public ProductOrder() {}

    public ProductOrder(Customer customer, List<Item> items) {
        this.id = counter;
        this.customer = customer;
        this.items = items;
        time = new Date();
        counter++;
    }

    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductOrder product = (ProductOrder) o;
        return id == product.id &&
                time.equals(product.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order ID: " + id +
                "\nCustomer: " + customer +
                "\nItems: \n" + items +
                "\nDate: " + time;
    }
}
