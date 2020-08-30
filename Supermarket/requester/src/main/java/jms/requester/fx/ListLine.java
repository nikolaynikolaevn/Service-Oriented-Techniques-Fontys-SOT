package jms.requester.fx;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Objects;

public class ListLine<REQUEST, REPLY> implements MessageHolder<REQUEST, REPLY> {

    private final REQUEST request;
    private REPLY reply;

    public ListLine(REQUEST request, REPLY reply) {
        this.request = request;
        this.reply = reply;
    }

    public ListLine(REQUEST request) {
        this.request = request;
        this.reply = null;
    }

    public REQUEST getRequest() {
        return request;
    }

    public void setReply(REPLY reply) {
        this.reply = reply;
    }

    @Override
    public String toString() {
        String requestString = "", replyString = "";
        if (request == null){
            requestString = "can't find request...";
        } else {
            try {
                requestString = ((TextMessage)request).getText();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

        if (reply == null){
            replyString = "can't find reply...";
        } else {
            try {
                replyString = ((TextMessage)reply).getText();
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
        ListLine<?, ?> listLine = (ListLine<?, ?>) o;
        return request.equals(listLine.request) &&
                Objects.equals(reply, listLine.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, reply);
    }
}
