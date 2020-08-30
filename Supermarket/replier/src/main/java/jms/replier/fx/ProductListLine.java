package jms.replier.fx;

import com.google.gson.Gson;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Objects;

public class ProductListLine<REQUEST, REPLY> implements MessageHolder<REQUEST, REPLY> {

    private Gson gson = new Gson();

    private final REQUEST request;
    private REPLY reply;

    public ProductListLine(REQUEST request, REPLY reply) {
        this.request = request;
        this.reply = reply;

    }

    public ProductListLine(REQUEST request) {
        this.request = request;
        this.reply = null;
    }

    public REQUEST getRequest() {
        return request;
    }

    public void setReply(REPLY reply) {
        this.reply = reply;
    }

    public Product getOldProduct() {
        if (request != null) {
            try {
                return gson.fromJson(((TextMessage) request).getText(), Product.class);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Product getNewProduct() {
        if (reply != null) {
            try {
                return gson.fromJson(((TextMessage) reply).getText(), Product.class);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String requestString = "", replyString = "";
        if (request == null){
            requestString = "can't find request...";
        } else {
            try {
                requestString = (gson.fromJson(((TextMessage)request).getText(), Product.class)).toString();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        if (reply == null){
            replyString = "can't find reply...";
        } else {
            try {
                replyString = (gson.fromJson(((TextMessage)reply).getText(), Product.class)).toString();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
        return requestString + "  --->  " + replyString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductListLine<?, ?> listLine = (ProductListLine<?, ?>) o;
        return request.equals(listLine.request) &&
                Objects.equals(reply, listLine.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, reply);
    }
}
