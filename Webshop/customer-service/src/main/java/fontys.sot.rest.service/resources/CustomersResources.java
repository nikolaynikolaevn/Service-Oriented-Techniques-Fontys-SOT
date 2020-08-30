package fontys.sot.rest.service.resources;

import fontys.sot.rest.service.model.Customer;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Webshop 2020
 * fontys.sot.rest.service.resources.CustomersResources
 *
 * @author Nikolay Nikolaev
 */
@Singleton
@Path("/")
public class CustomersResources {
    private List<Customer> customers;

    public CustomersResources() {
        customers = new ArrayList<>();
        customers.add(new Customer(1, "Joe Smith", 9.22));
        customers.add(new Customer(2, "Ann Johnsson", 12.27));
        customers.add(new Customer(3, "Miranda Winslet", 15.83));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getCustomers() {
        return customers;
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public int getNumberOfCustomers() {
        return customers.size();
    }

    @GET
    @Path("last")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLastCustomer() {
        if (customers.size() > 0) return Response.ok().entity(customers.get(customers.size() - 1)).build();
        return Response.serverError().entity("There are no customers in the list.").build();
    }

    @GET
    @Path("customer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomerQuery(@QueryParam("id") int id) {
        Customer customer = this.getCustomer(id);
        if (customer == null)
            return Response.serverError().entity("Cannot find customer with id " + id + "!").build();
        return Response.ok(customer).build();
    }

    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Customer getCustomerPath(@PathParam("id") int id) {
        Customer customer = this.getCustomer(id);
        if (customer == null)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity("Cannot find customer with id " + id + "!")
                    .type(MediaType.TEXT_PLAIN).build());
        return customer;
    }

    public Customer getCustomer(int id) {
        for (Customer customer : customers) {
            if (customer.getId() == id) return customer;
        }
        return null;
    }

    @DELETE
    @Path("{id}")
    public Response deleteCustomer(@PathParam("id") int id) {
        Customer customer = this.getCustomer(id);
        if (customer != null) {
            customers.remove(customer);
            return Response.noContent().build();
        }
        return Response.serverError().entity("Cannot find customer with id " + id + ".").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer customer) {
        if (this.getCustomer(customer.getId()) != null)
            return Response.serverError().entity("Error post/create: Customer with id "
                    + customer.getId() + " already exists!").build();
        customers.add(customer);
        return Response.noContent().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createCustomerForm(@FormParam("id") int id, @FormParam("name") String name) {
        if (this.getCustomer(id) != null)
            return Response.serverError().entity("Error post/create: Customer with id "
                    + id + " already exists!").build();
        customers.add(new Customer(id, name, 0));
        return Response.noContent().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(Customer customer) {
        Customer existingCustomer = this.getCustomer(customer.getId());
        if (existingCustomer != null) {
            existingCustomer.setName(customer.getName());
            existingCustomer.setBalance(customer.getBalance());
            return Response.noContent().build();
        }
        return Response.serverError().entity("Error put/update: Customer with id "
                + customer.getId() + " does not exist!").build();
    }

    @PUT
    @Path("deposit")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deposit(@QueryParam("customerId") int id, @QueryParam("amount") double amount) {
        Customer customer = this.getCustomer(id);
        if (customer != null) {
            if (amount > 0) {
                customer.setBalance(customer.getBalance() + amount);
                return Response.ok(customer).build();
            }
            return Response.serverError().entity("Error put/update: Amount must be positive.").build();
        }
        return Response.serverError().entity("Error put/update: Customer with id "
                + id + " does not exist!").build();
    }

    @PUT
    @Path("spend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response spend(@QueryParam("customerId") int id, @QueryParam("amount") double amount) {
        Customer customer = this.getCustomer(id);
        if (customer != null) {
            double newBalance = customer.getBalance() - amount;

            if (newBalance < 0) return Response.serverError().entity("Error put/update: Customer with id "
                    + id + " has insufficient balance.").build();

            customer.setBalance(newBalance);
            return Response.ok(customer).build();
        }
        return Response.serverError().entity("Error put/update: Customer with id "
                + id + " does not exist!").build();
    }
}
