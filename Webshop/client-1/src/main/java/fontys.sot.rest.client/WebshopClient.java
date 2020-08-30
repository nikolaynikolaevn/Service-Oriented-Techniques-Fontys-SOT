package fontys.sot.rest.client;

import fontys.sot.rest.service.model.Customer;
import fontys.sot.rest.service.model.Item;
import fontys.sot.rest.service.model.Product;
import fontys.sot.rest.service.model.ProductOrder;

import org.glassfish.jersey.client.ClientConfig;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.*;

/**
 * Webshop 2020
 * Client
 *
 * @author Nikolay Nikolaev
 */
public class WebshopClient {
    private ClientConfig config;
    private Client client;
    private URI baseURI;
    private WebTarget serviceTarget;

    public WebshopClient() {
        config = new ClientConfig();
        client = ClientBuilder.newClient(config);
        baseURI = UriBuilder.fromUri("http://localhost:8080/").build();
        serviceTarget = client.target(baseURI);
    }

    public static void main(String[] args) {
        WebshopClient webshopClient = new WebshopClient();
        Scanner scanner = new Scanner(System.in);
        int id, amount;
        String name;
        double balance, price;

        int choice = -1;
        while (true) {
            System.out.println("\n\n=== Webshop CLI Client ===");
            System.out.println("\nMENU:\n");
            System.out.println("Customers:");
            System.out.println("Enter 1 for showing all customers.");
            System.out.println("Enter 2 for searching for a customer by ID.");
            System.out.println("Enter 3 for customer count.");
            System.out.println("Enter 4 for showing the last registered costumer.");
            System.out.println("Enter 5 for creating a costumer.");
            System.out.println("Enter 6 for deleting a costumer.");
            System.out.println("Enter 7 for updating a costumer.");
            System.out.println("\nProducts:");
            System.out.println("Enter 8 for showing all products.");
            System.out.println("Enter 9 for searching for a product by ID.");
            System.out.println("Enter 10 for product count.");
            System.out.println("Enter 11 for creating a product.");
            System.out.println("Enter 12 for deleting a product.");
            System.out.println("Enter 13 for updating a product.");
            System.out.println("\nOrders:");
            System.out.println("Enter 14 for showing all orders.");
            System.out.println("Enter 15 for searching for an order by ID.");
            System.out.println("Enter 16 for order count.");
            System.out.println("Enter 17 for showing the last submitted order.");
            System.out.println("Enter 18 for creating an order.");
            System.out.println("Enter 19 for deleting an order.");

            System.out.println("Enter 0 to exit.");
            System.out.println();
            System.out.print("Select an option: ");

            choice = Integer.parseInt(scanner.nextLine());
            System.out.println();

            switch (choice) {
                // CUSTOMERS
                case 1:
                    webshopClient.getCustomers();
                    break;
                case 2:
                    System.out.print("Enter an ID to find: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.println();
                    webshopClient.getCustomerPath(id);
                    break;
                case 3:
                    webshopClient.getCustomerCount();
                    break;
                case 4:
                    webshopClient.getLastCustomer();
                    break;
                case 5:
                    System.out.print("Enter ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter Balance: ");
                    balance = Double.parseDouble(scanner.nextLine());
                    System.out.println();
                    webshopClient.createCustomer(id, name, balance);
                    break;
                case 6:
                    System.out.print("Enter ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.println();
                    webshopClient.deleteCustomer(id);
                    break;
                case 7:
                    System.out.print("Enter ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter Balance: ");
                    balance = Double.parseDouble(scanner.nextLine());
                    System.out.println();
                    webshopClient.updateCustomer(id, name, balance);
                    break;

                // PRODUCTS
                case 8:
                    webshopClient.getProducts();
                    break;
                case 9:
                    System.out.print("Enter an ID to find: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.println();
                    webshopClient.getProductPath(id);
                    break;
                case 10:
                    webshopClient.getProductCount();
                    break;
                case 11:
                    System.out.print("Enter ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter Price: ");
                    price = Double.parseDouble(scanner.nextLine());
                    System.out.print("Enter Amount: ");
                    amount = Integer.parseInt(scanner.nextLine());
                    System.out.println();
                    webshopClient.createProduct(id, name, price, amount);
                    break;
                case 12:
                    System.out.print("Enter ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.println();
                    webshopClient.deleteProduct(id);
                    break;
                case 13:
                    System.out.print("Enter ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Name: ");
                    name = scanner.nextLine();
                    System.out.print("Enter Price: ");
                    price = Double.parseDouble(scanner.nextLine());
                    System.out.println();
                    webshopClient.updateProduct(id, name, price);
                    break;

                // ORDERS
                case 14:
                    webshopClient.getOrders();
                    break;
                case 15:
                    System.out.print("Enter an ID to find: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.println();
                    webshopClient.getOrderPath(id);
                    break;
                case 16:
                    webshopClient.getOrderCount();
                    break;
                case 17:
                    webshopClient.getLastOrder();
                    break;
                case 18:
                    System.out.print("Enter Customer ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    Customer customer = webshopClient.getCustomer(id);

                    List<Item> items = new ArrayList<>();

                    String isAdd = "";
                    while (!isAdd.equals("q")) {
                        System.out.print("Enter Product ID: ");
                        id = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Amount: ");
                        amount = Integer.parseInt(scanner.nextLine());
                        items.add(new Item(webshopClient.getProduct(id), amount));

                        System.out.print("\nDo you want to continue adding?" +
                                " (enter 'q' to finish, 'y' to continue): ");
                        isAdd = scanner.nextLine();
                    }
                    System.out.println();
                    webshopClient.createOrder(new ProductOrder(customer, items));
                    break;
                case 19:
                    System.out.print("Enter ID: ");
                    id = Integer.parseInt(scanner.nextLine());
                    System.out.println();
                    webshopClient.deleteOrder(id);
                    break;

                // EXIT
                case 0:
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    /*
     ****************************************
     * CUSTOMERS
     ****************************************
     */
    public void getCustomerCount() {
        Builder requestBuilder = serviceTarget.path("customers/count")
                .request().accept(MediaType.TEXT_PLAIN);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Integer entity = response.readEntity(Integer.class);
            System.out.println("There are " + entity + " customers in total.");
        } else {
            printError(response);
        }
    }

    public void getLastCustomer() {
        Builder requestBuilder = serviceTarget.path("customers/last")
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Customer entity = response.readEntity(Customer.class);
            System.out.println("The last customer is:\n");
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public void getCustomers() {
        Builder requestBuilder = serviceTarget.path("customers")
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            GenericType<ArrayList<Customer>> genericType = new GenericType<>() {};
            ArrayList<Customer> entity = response.readEntity(genericType);
            System.out.println("Customers:\n");
            for (Customer customer : entity) {
                System.out.println(customer);
                System.out.println();
            }
        } else {
            printError(response);
        }
    }

    public void getCustomerQuery(int id) {
        Builder requestBuilder = serviceTarget.path("customer")
                .queryParam("id", id).request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Customer entity = response.readEntity(Customer.class);
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public Customer getCustomer(int id) {
        Builder requestBuilder = serviceTarget.path("customers/" + id)
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Customer entity = response.readEntity(Customer.class);
            return entity;
        }
        return null;
    }

    public void getCustomerPath(int id) {
        Builder requestBuilder = serviceTarget.path("customers/" + id)
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Customer entity = response.readEntity(Customer.class);
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public void deleteCustomer(int id) {
        WebTarget resourceTarget = serviceTarget.path("customers/" + id);
        Builder requestBuilder = resourceTarget.request().accept(MediaType.TEXT_PLAIN);
        Response response = requestBuilder.delete();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Deleted customer " + id + ".");
        else printError(response);
    }

    public void createCustomer(int id, String name, double balance) {
        Customer customer = new Customer(id, name, balance);
        Entity<Customer> entity = Entity.entity(customer, MediaType.APPLICATION_JSON);

        Response response = serviceTarget.path("customers")
                .request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Created customer " + id + ".");
        else printError(response);
    }

    public void createCustomerForm(int id, String name, double balance) {
        Form form = new Form();
        form.param("id", String.valueOf(id));
        form.param("name", name);
        form.param("balance", String.valueOf(balance));
        Entity<Form> entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED);

        Response response = serviceTarget.path("customers")
                .request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Created customer " + id + ".");
        else printError(response);
    }

    public void updateCustomer(int id, String name, double balance) {
        Customer customer = new Customer(id, name, balance);
        Entity<Customer> entity = Entity.entity(customer, MediaType.APPLICATION_JSON);

        Response response = serviceTarget.path("customers")
                .request().accept(MediaType.TEXT_PLAIN).put(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Updated customer " + id + ".");
        else printError(response);
    }

    /*
     ****************************************
     * PRODUCTS
     ****************************************
     */

    public void getProductCount() {
        Builder requestBuilder = serviceTarget.path("products/count")
                .request().accept(MediaType.TEXT_PLAIN);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Integer entity = response.readEntity(Integer.class);
            System.out.println("There are " + entity + " products in total.");
        } else {
            printError(response);
        }
    }

    public void getProducts() {
        Builder requestBuilder = serviceTarget.path("products")
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            GenericType<Set<Product>> genericType = new GenericType<>() {};
            Set<Product> entity = response.readEntity(genericType);
            System.out.println("Products:\n");
            for (Product product : entity) {
                System.out.println(product);
                System.out.println();
            }
        } else {
            printError(response);
        }
    }

    public void getProductQuery(int id) {
        Builder requestBuilder = serviceTarget.path("product")
                .queryParam("id", id).request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Product entity = response.readEntity(Product.class);
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public Product getProduct(int id) {
        Builder requestBuilder = serviceTarget.path("products/" + id)
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Product entity = response.readEntity(Product.class);
            return entity;
        }
        return null;
    }

    public void getProductPath(int id) {
        Builder requestBuilder = serviceTarget.path("products/" + id)
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Product entity = response.readEntity(Product.class);
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public void deleteProduct(int id) {
        WebTarget resourceTarget = serviceTarget.path("products/" + id);
        Builder requestBuilder = resourceTarget.request().accept(MediaType.TEXT_PLAIN);
        Response response = requestBuilder.delete();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Deleted product " + id + ".");
        else printError(response);
    }

    public void createProduct(int id, String name, double price, int amount) {
        Product product = new Product(id, name, price);
        Entity<Product> entity = Entity.entity(product, MediaType.APPLICATION_JSON);

        Response response = serviceTarget.path("products")
                .queryParam("amount", amount).request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Created product " + id + ".");
        else printError(response);
    }

    public void createProductForm(int id, String name, double price, int amount) {
        Form form = new Form();
        form.param("id", String.valueOf(id));
        form.param("name", name);
        form.param("price", String.valueOf(price));
        form.param("amount", String.valueOf(amount));
        Entity<Form> entity = Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED);

        Response response = serviceTarget.path("products")
                .request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Created product " + id + ".");
        else printError(response);
    }

    public void updateProduct(int id, String name, double price) {
        Product product = new Product(id, name, price);
        Entity<Product> entity = Entity.entity(product, MediaType.APPLICATION_JSON);

        Response response = serviceTarget.path("products")
                .request().accept(MediaType.TEXT_PLAIN).put(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Updated product " + id + ".");
        else printError(response);
    }

    /*
     ****************************************
     * ORDERS
     ****************************************
     */

    public void getOrderCount() {
        Builder requestBuilder = serviceTarget.path("orders/count")
                .request().accept(MediaType.TEXT_PLAIN);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            Integer entity = response.readEntity(Integer.class);
            System.out.println("There are " + entity + " orders in total.");
        } else {
            printError(response);
        }
    }

    public void getLastOrder() {
        Builder requestBuilder = serviceTarget.path("orders/last")
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            ProductOrder entity = response.readEntity(ProductOrder.class);
            System.out.println("The last order is:\n");
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public void getOrders() {
        Builder requestBuilder = serviceTarget.path("orders")
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            GenericType<List<ProductOrder>> genericType = new GenericType<>() {};
            List<ProductOrder> entity = response.readEntity(genericType);
            System.out.println("Orders:\n");
            for (ProductOrder order : entity) {
                System.out.println(order);
                System.out.println();
            }
        } else {
            printError(response);
        }
    }

    public void getOrderQuery(int id) {
        Builder requestBuilder = serviceTarget.path("order")
                .queryParam("id", id).request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            ProductOrder entity = response.readEntity(ProductOrder.class);
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public void getOrderPath(int id) {
        Builder requestBuilder = serviceTarget.path("orders/" + id)
                .request().accept(MediaType.APPLICATION_JSON);
        Response response = requestBuilder.get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            ProductOrder entity = response.readEntity(ProductOrder.class);
            System.out.println(entity);
        } else {
            printError(response);
        }
    }

    public void deleteOrder(int id) {
        WebTarget resourceTarget = serviceTarget.path("orders/" + id);
        Builder requestBuilder = resourceTarget.request().accept(MediaType.TEXT_PLAIN);
        Response response = requestBuilder.delete();

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Deleted order " + id + ".");
        else printError(response);
    }

    public void createOrder(ProductOrder order) {
        Entity<ProductOrder> entity = Entity.entity(order, MediaType.APPLICATION_JSON);

        Response response = serviceTarget.path("orders")
                .request().accept(MediaType.TEXT_PLAIN).post(entity);

        if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
            System.out.println("Created order.");
        else printError(response);
    }

    /*
     ****************************************
     * ERROR HANDLING
     ****************************************
     */

    private void printError(Response response) {
        System.out.println("ERROR: " + response);
        String entity = response.readEntity(String.class);
        System.out.println(entity);
    }
}
