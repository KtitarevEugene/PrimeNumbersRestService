package web_app.messaging;

import javax.jms.*;

public class JMSSession implements AutoCloseable {

    private Session session;

    public JMSSession(JMSConnection connection) throws JMSException {
        session = connection.createSession();
    }

    MessageProducer createProducer(String queueName) throws JMSException {
        Queue queue = session.createQueue(queueName);

        return session.createProducer(queue);
    }

    public JMSMessage createMessage (String string) throws JMSException {
        return new JMSMessage(session.createTextMessage(string));
    }

    @Override
    public void close() throws JMSException {
        session.close();
    }
}
