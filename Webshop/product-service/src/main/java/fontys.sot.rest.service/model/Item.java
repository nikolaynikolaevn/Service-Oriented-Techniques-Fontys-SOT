package fontys.sot.rest.service.model;

import java.util.Objects;

public class Item {
    private Product product;
    private int amount;

    public Item() {}

    public Item(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return product == item.product &&
                amount == item.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, amount);
    }

    @Override
    public String toString() {
        return "Product: " + product +
                "\nAmount: " + amount;
    }
}
