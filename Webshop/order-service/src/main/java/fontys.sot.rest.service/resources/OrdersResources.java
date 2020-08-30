package fontys.sot.rest.service.resources;

import fontys.sot.rest.service.model.Item;
import fontys.sot.rest.service.model.ProductOrder;
import fontys.sot.rest.service.model.Customer;
import fontys.sot.rest.service.model.Product;
import org.glassfish.jersey.client.ClientConfig;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Webshop 2020
 * fontys.sot.rest.service.resources.OrdersResources
 *
 * @author Nikolay Nikolaev
 */
@Singleton
@Path("/")
public class OrdersResources {
    private List<ProductOrder> orders;

    private ClientConfig config;
    private Client client;
    private URI baseURI;
    private WebTarget serviceTarget;

    public OrdersResources() {
        orders = new ArrayList<>();
        orders.add(new ProductOrder(new Customer(1, "John", 10),
                new ArrayList<Item>(){{
                    add(new Item(new Product(1, "Fanta", 1.22), 2));
                }}));

        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        baseURI = UriBuilder.fromUri("http://localhost:8080/").build();
        serviceTarget = client.target(baseURI);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ProductOrder> getOrders() {
        return orders;
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public int getNumberOfOrders() {
        return orders.size();
    }

    @GET
    @Path("last")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastOrder() {
        if (orders.size() > 0) return Response.ok().entity(orders.get(orders.size() - 1)).build();
        return Response.serverError().entity("There are no orders in the list.").build();
    }

    @GET
    @Path("order")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderQuery(@QueryParam("id") int id) {
        ProductOrder order = this.getOrder(id);
        if (order == null)
            return Response.serverError().entity("Cannot find order with id " + id + "!").build();
        return Response.ok(order).build();
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public ProductOrder getOrderPath(@PathParam("id") int id) {
        ProductOrder order = this.getOrder(id);
        if (order == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Cannot find order with id " + id + "!")
                    .type(MediaType.TEXT_PLAIN).build());
        return order;
    }

    public ProductOrder getOrder(int id) {
        for (ProductOrder order : orders) {
            if (order.getId() == id) return order;
        }
        return null;
    }

    public double calculateOrderAmount(ProductOrder order) {
        return order.getItems().
                parallelStream().
                mapToDouble(item -> item.getProduct().getPrice() * item.getAmount()).
                sum();
    }

    @DELETE
    @Path("{id}")
    public Response deleteOrder(@PathParam("id") int id) {
        ProductOrder order = this.getOrder(id);
        if (order != null) {

            // Update inventory
            Entity<List<Item>> entity = Entity.entity(order.getItems(),
                    MediaType.APPLICATION_JSON);
            Response response = serviceTarget.path("products/return")
                    .request().accept(MediaType.TEXT_PLAIN).put(entity);

            if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {

                // Update customer balance
                Invocation.Builder spendRequestBuilder = serviceTarget.path("customers/deposit")
                        .queryParam("customerId", order.getCustomer().getId())
                        .queryParam("amount", calculateOrderAmount(order))
                        .request().accept(MediaType.APPLICATION_JSON);
                Response spendResponse = spendRequestBuilder.get();

                if (spendResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                    // Remove the order
                    orders.remove(order);
                    return Response.noContent().build();
                }
            }
        }
        return Response.serverError().entity("Error deleting an order: Cannot find order with id " + id + ".").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createOrder(ProductOrder order) {

        // Update inventory
        Entity<List<Item>> entity = Entity.entity(order.getItems(),
                MediaType.APPLICATION_JSON);
        Response response = serviceTarget.path("products/sell")
                .request().accept(MediaType.TEXT_PLAIN).put(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {

            // Update customer balance
            Invocation.Builder spendRequestBuilder = serviceTarget.path("customers/spend")
                    .queryParam("customerId", order.getCustomer().getId())
                    .queryParam("amount", calculateOrderAmount(order))
                    .request().accept(MediaType.APPLICATION_JSON);
            Response spendResponse = spendRequestBuilder.put(Entity.json(null));

            if (spendResponse.getStatus() == Response.Status.OK.getStatusCode()) {
                // Add the order
                orders.add(order);
                return Response.noContent().build();
            }
        }
        return Response.serverError().entity("Error creating an order.").build();
    }
}
