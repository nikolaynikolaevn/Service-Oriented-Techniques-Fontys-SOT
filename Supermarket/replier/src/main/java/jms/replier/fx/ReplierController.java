package jms.replier.fx;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class ReplierController implements Initializable {

    @FXML TextField tfAnswer;

    @FXML TextField tfProduct;
    @FXML TextField tfDescription;
    @FXML TextField tfPrice;

    @FXML ListView<MessageHolder<Message, Message>> lvRequestReply;
    @FXML ListView<MessageHolder<Message, Message>> lvRequestReplyProducts;

    private final Logger logger =  Logger.getLogger(ReplierController.class.getName());

    Connection connection;
    Session session;

    Destination receiveTextDestination, receiveObjectDestination;
    MessageProducer producer;
    MessageConsumer textConsumer, objectConsumer;

    Gson gson;

    @FXML
    public void btnSendClicked() {
        String reply = tfAnswer.getText();
        MessageHolder<Message, Message> listLine = lvRequestReply.getSelectionModel().getSelectedItem();
        if (listLine != null) sendTextReply(listLine, reply, lvRequestReply);
    }

    @FXML
    public void btnSendProductClicked() {
        MessageHolder<Message, Message> listLine = lvRequestReplyProducts.getSelectionModel().getSelectedItem();
        if (listLine != null) {
            try {
                Product p = new Product(tfProduct.getText(), tfDescription.getText(), Double.parseDouble(tfPrice.getText()));
                sendTextReply(listLine, gson.toJson(p), lvRequestReplyProducts);
                logger.log(Level.INFO, "Sent product request: " + p);
            } catch (NumberFormatException e) {
                System.out.println("Error: Price format is invalid.");
            }
        }
    }

    private void sendTextReply(MessageHolder<Message, Message> listLine, String reply, ListView<MessageHolder<Message, Message>> listView) {
        try {
            TextMessage replyMsg = session.createTextMessage(reply);
            Message request = listLine.getRequest();
            replyMsg.setJMSCorrelationID(request.getJMSMessageID());

            producer.send(request.getJMSReplyTo(), replyMsg);
            listLine.setReply(replyMsg);
            Platform.runLater(listView::refresh);

            logger.info("Sent reply " + reply + " for request " + request);
            System.out.println("JMSMessageID=" + replyMsg.getJMSMessageID()
                    + " JMSDestination=" + replyMsg.getJMSDestination()
                    + " Text=" + replyMsg.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is executed after
     *   - the ReplierController object is created and
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

            producer = session.createProducer(null);

            receiveTextDestination = session.createQueue("requestTextQueue");
            textConsumer = session.createConsumer(receiveTextDestination);

            receiveObjectDestination = session.createQueue("requestObjectQueue");
            objectConsumer = session.createConsumer(receiveObjectDestination);

            gson = new Gson();

            textConsumer.setMessageListener(request -> {
                System.out.println("received text request: " + request);
                ListLine<Message, Message> listLine = new ListLine<>(request);
                lvRequestReply.getItems().add(listLine);
                Platform.runLater(lvRequestReply::refresh);
            });

            objectConsumer.setMessageListener(request -> {
                System.out.println("received object request: " + request);
                ProductListLine<Message, Message> listLine = new ProductListLine<>(request);
                lvRequestReplyProducts.getItems().add(listLine);
                Platform.runLater(lvRequestReplyProducts::refresh);
            });
            connection.start();

            logger.log(Level.INFO, "Started listening for requests.");

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
        logger.info("Replier started.");
    }

    /**
     * This is executed when the form is closed. See ReplierMain
     */
    public void stop(){
        try {
            if (producer != null) producer.close();
            if (textConsumer != null) textConsumer.close();
            if (objectConsumer != null) objectConsumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
