package web_app.messaging;

import javax.jms.Message;
import javax.jms.TextMessage;

public class JMSMessage {

    private Message message;

    JMSMessage (TextMessage message) {
        this.message = message;
    }

    Message getMessage() {
        return message;
    }
}
