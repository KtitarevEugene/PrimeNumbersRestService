package web_app.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import web_app.common.Constants;
import web_app.messaging.exceptions.JMSConfigurationException;

import javax.jms.*;
import java.util.Properties;

public class JMSConnection implements AutoCloseable {

    private String clientName;
    private String brokerUrl;
    private Connection connection;

    public JMSConnection(Properties properties) throws JMSConfigurationException, JMSException {
        this.brokerUrl = getProperty(properties, Constants.ACTIVE_MQ_BROKER_URL);
        this.clientName = getProperty(properties, Constants.ACTIVE_MQ_PRODUCER_ID);

        init();
    }

    private String getProperty(Properties properties, String name) throws JMSConfigurationException {
        String value = properties.getProperty(name);
        if (value == null) {
            throw new JMSConfigurationException(String.format(
                    "Missing required config property '%s'",
                    name));
        }

        return value;
    }

    public JMSConnection(String brokerUrl, String clientName) throws JMSException {
        this.brokerUrl = brokerUrl;
        this.clientName = clientName;

        init();
    }

    private void init() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);

        connection = connectionFactory.createConnection();
        connection.setClientID(clientName);
    }

    @Override
    public void close() throws JMSException {
        connection.close();
    }

    Session createSession() throws JMSException {
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public String getClientName() {
        return clientName;
    }
}
