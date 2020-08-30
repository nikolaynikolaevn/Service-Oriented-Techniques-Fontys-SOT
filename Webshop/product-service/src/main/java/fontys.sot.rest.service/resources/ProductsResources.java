package fontys.sot.rest.service.resources;

import fontys.sot.rest.service.Inventory;
import fontys.sot.rest.service.model.Item;
import fontys.sot.rest.service.model.Product;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

/**
 * Webshop 2020
 * ProductsResources
 *
 * @author Nikolay Nikolaev
 */
@Singleton
@Path("/")
public class ProductsResources {
    private Inventory inventory;

    public ProductsResources() {
        inventory = new Inventory();
        inventory.addProduct(new Product(1, "Coca Cola", 1.20), 3);
        inventory.addProduct(new Product(2, "Fanta", 1.50), 5);
        inventory.addProduct(new Product(3, "Sprite", 1.25), 10);
        inventory.addProduct(new Product(3, "Mirinda", 1.15), 15);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Product> getProducts() {
        return inventory.getProducts().keySet();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public int getNumberOfProducts() {
        return inventory.getCount();
    }

    @GET
    @Path("product")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductQuery(@QueryParam("id") int id) {
        Product product = this.getProduct(id);
        if (product == null)
            return Response.serverError().entity("Cannot find product with id " + id + "!").build();
        return Response.ok(product).build();
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Product getProductPath(@PathParam("id") int id) {
        Product product = this.getProduct(id);
        if (product == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Cannot find product with id " + id + "!")
                    .type(MediaType.TEXT_PLAIN).build());
        return product;
    }

    public Product getProduct(int id) {
        return inventory.getProduct(id);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response reduceProductAmount(@QueryParam("productId") int id,
                                @QueryParam("amount") int amount) {
        this.inventory.reduceProductAmount(id, amount);
        return Response.ok(this.inventory.getProduct(id)).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteProduct(@PathParam("id") int id) {
        this.inventory.removeProduct(id);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createProduct(@QueryParam("amount") int amount, Product product) {
        if (this.getProduct(product.getId()) != null)
            return Response.serverError().entity("Error post/create: Product with id "
                    + product.getId() + " already exists!").build();
        this.inventory.addProduct(product, amount);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createProductForm(@FormParam("id") int id, @FormParam("name") String name,
                                      @FormParam("price") double price,
                                      @FormParam("amount") int amount) {
        if (this.getProduct(id) != null)
            return Response.serverError().entity("Error post/create: Product with id "
                    + id + " already exists!").build();
        this.inventory.addProduct(new Product(id, name, price), amount);
        return Response.noContent().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateProduct(Product product) {
        Product existingProduct = this.getProduct(product.getId());
        if (existingProduct != null) {
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            return Response.noContent().build();
        }
        return Response.serverError().entity("Error put/update: Product with id "
                + product.getId() + " does not exist!").build();
    }

    @PUT
    @Path("sell")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sellProducts(List<Item> items) {
        for (Item item : items) {
            this.inventory.reduceProductAmount(item.getProduct().getId(), item.getAmount());
        }
        return Response.noContent().build();
    }

    @PUT
    @Path("return")
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnProducts(List<Item> items) {
        for (Item item : items) {
            this.inventory.increaseProductAmount(item.getProduct().getId(), item.getAmount());
        }
        return Response.noContent().build();
    }
}
