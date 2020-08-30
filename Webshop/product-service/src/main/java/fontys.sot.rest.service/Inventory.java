package fontys.sot.rest.service;

import fontys.sot.rest.service.model.Product;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashMap;

public class Inventory {
    private HashMap<Product, Integer> products;

    public Inventory() {
        this.products = new HashMap<>();
    }

    public Product getProduct(final int id) {
        for (Product product : this.products.keySet()) {
            if (product.getId() == id) return product;
        }
        return null;
    }

    public void addProduct(final Product product, final int amount) {
        if (amount < 0) throw new WebApplicationException("Amount must be non-negative.");
        final int currentAmount = this.products.getOrDefault(product, 0);
        this.products.put(product, currentAmount + amount);
    }

    public void removeProduct(final int id) {
        this.products.remove(this.getProduct(id));
    }

    public void reduceProductAmount(final int id, final int amount) {
        Product product = this.getProduct(id);
        if (amount < 1) throw new WebApplicationException("Amount must be positive.");
        if (this.products.get(product) >= amount) {
            this.products.replace(product, this.products.get(product) - amount);
        } else {
            throw new WebApplicationException(String.format("Cannot remove %d instances" +
                    " of product as there are only %d instances!", amount, this.products.get(product)));
        }
    }

    public void increaseProductAmount(final int id, final int amount) {
        Product product = this.getProduct(id);
        if (amount < 1) throw new WebApplicationException("Amount must be positive.");
        this.products.replace(product, this.products.get(product) + amount);
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
    }

    public int getCount() {
        return products.size();
    }

    public int getCount(final Product product) {
        return this.products.getOrDefault(product, 0);
    }

    public double calculateTotal() {
        return this.products.entrySet().
                parallelStream().
                mapToDouble(product -> product.getKey().getPrice() * product.getValue()).
                sum();
    }
}
