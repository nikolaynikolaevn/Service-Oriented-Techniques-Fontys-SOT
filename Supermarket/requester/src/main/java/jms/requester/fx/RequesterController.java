package jms.requester.fx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequesterController implements Initializable {

    @FXML TextField tfMessage;

    @FXML TextField tfProduct;
    @FXML TextField tfDescription;
    @FXML TextField tfPrice;

    @FXML ListView<MessageHolder<Message, Message>> lvRequestReply;
    @FXML ListView<MessageHolder<Message, Message>> lvRequestReplyProducts;

    private final Logger logger =  Logger.getLogger(RequesterController.class.getName());

    Connection connection;
    Session session;

    Destination sendTextDestination, sendObjectDestination, receiveTextDestination, receiveObjectDestination;
    MessageProducer textProducer, objectProducer;
    MessageConsumer textConsumer, objectConsumer;

    Gson gson;

    private MessageHolder<Message, Message> findRequest(String correlationID, ListView<MessageHolder<Message, Message>> listView) {
        for (MessageHolder<Message, Message> listLine : listView.getItems()) {
            try {
                if (listLine.getRequest().getJMSMessageID().equals(correlationID)) return listLine;
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @FXML
    public void btnSendClicked() {
        String requestText = tfMessage.getText();
        Message msg = sendTextRequest(requestText, textProducer, receiveTextDestination);
        ListLine<Message, Message> listLine = new ListLine<>(msg);
        lvRequestReply.getItems().add(listLine);
        logger.log(Level.INFO, "Sent request: " + requestText);
    }

    @FXML
    public void btnSendProductClicked() {
        try {
            Product p = new Product(tfProduct.getText(), tfDescription.getText(), Double.parseDouble(tfPrice.getText()));
            Message msg = sendTextRequest(gson.toJson(p), objectProducer, receiveObjectDestination);
            ProductListLine<Message, Message> listLine = new ProductListLine<>(msg);
            lvRequestReplyProducts.getItems().add(listLine);
            logger.log(Level.INFO, "Sent product request: " + p);
        } catch (NumberFormatException e) {
            System.out.println("Error: Price format is invalid.");
        }
    }

    private Message sendTextRequest(String body, MessageProducer mp, Destination d) {
        try {
            TextMessage msg = session.createTextMessage(body);
            msg.setJMSReplyTo(d);
            mp.send(msg);
            System.out.println("JMSMessageID=" + msg.getJMSMessageID()
                    + " JMSDestination=" + msg.getJMSDestination()
                    + " Text=" + msg.getText());
            return msg;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is executed after
     *   - the RequesterController object is created and
     *   - the fxml form and its components is created
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            connection = connectionFactory.createConnection();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            sendTextDestination = session.createQueue("requestTextQueue");
            textProducer = session.createProducer(sendTextDestination);

            sendObjectDestination = session.createQueue("requestObjectQueue");
            objectProducer = session.createProducer(sendObjectDestination);

            receiveTextDestination = session.createTemporaryQueue();
            textConsumer = session.createConsumer(receiveTextDestination);

            receiveObjectDestination = session.createTemporaryQueue();
            objectConsumer = session.createConsumer(receiveObjectDestination);

            gson = new Gson();

            textConsumer.setMessageListener(reply -> {
                System.out.println("received text reply: " + reply);
                processReply(reply, lvRequestReply);
            });

            objectConsumer.setMessageListener(reply -> {
                System.out.println("received object reply: " + reply);
                processReply(reply, lvRequestReplyProducts);
            });
            connection.start();

            logger.log(Level.INFO, "Started listening for replies.");

            lvRequestReplyProducts.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

                Product newestProduct = ((ProductListLine<Message, Message>)newValue).getNewProduct();
                if (newestProduct == null) newestProduct = ((ProductListLine<Message, Message>)newValue).getOldProduct();

                tfProduct.setText(newestProduct.getName());
                tfDescription.setText(newestProduct.getDescription());
                tfPrice.setText(String.valueOf(newestProduct.getPrice()));

            });
        } catch (JMSException e) {
            e.printStackTrace();
            stop();
        }
        logger.log(Level.INFO, "Requester started.");
    }

    private void processReply(Message reply, ListView<MessageHolder<Message, Message>> listView) {
        try {
            MessageHolder<Message, Message> requestReply = findRequest(reply.getJMSCorrelationID(), listView);
            if (requestReply != null) {
                requestReply.setReply(reply);
                Platform.runLater(listView::refresh);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is executed when the form is closed. See RequesterMain
     */
    public void stop(){
        try {
            if (textProducer != null) textProducer.close();
            if (objectProducer != null) objectProducer.close();
            if (textConsumer != null) textConsumer.close();
            if (objectConsumer != null) objectConsumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
