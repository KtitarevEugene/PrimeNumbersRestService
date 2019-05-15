package web_app.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

public class JMSProducer implements AutoCloseable {

    private MessageProducer producer;

    public JMSProducer(JMSSession session, String queueName) throws JMSException {
        producer = session.createProducer(queueName);
    }

    public String send(JMSMessage jmsMessage) throws JMSException {
        Message message = jmsMessage.getMessage();

        producer.send(message);

        return message.getJMSMessageID();
    }

    @Override
    public void close() throws JMSException {
        producer.close();
    }
}
