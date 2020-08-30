package jms.replier.fx;

public interface MessageHolder<REQUEST, REPLY> {
    REQUEST getRequest();
    void setReply(REPLY reply);
}
