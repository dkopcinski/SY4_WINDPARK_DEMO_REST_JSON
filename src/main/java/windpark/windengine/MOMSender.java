package windpark.windengine;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.TimeUnit;

public class MOMSender {

    private static String user = ActiveMQConnection.DEFAULT_USER;
    private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static String subject = "windengine_001";
    private WindengineService service;

    public MOMSender(String ID) {
        this.service = new WindengineService();
        System.out.println( "Sender started." );

        // Create the connection.
        Session session = null;
        Connection connection = null;
        MessageProducer producer = null;
        Destination destination = null;

        try {

            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory( user, password, url );
            connection = connectionFactory.createConnection();
            connection.start();

            // Create the session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic( subject );

            // Create the producer.
            producer = session.createProducer(destination);
            producer.setDeliveryMode( DeliveryMode.NON_PERSISTENT );
           // Create the message
            TextMessage message = session.createTextMessage(new ObjectMapper().writeValueAsString(service.getWindengineData(ID)));
            producer.send(message);
            System.out.println( message.getText() );

            connection.stop();

        } catch (Exception e) {

            System.out.println("[MessageProducer] Caught: " + e);
            e.printStackTrace();

        } finally {

            try { producer.close(); } catch ( Exception e ) {}
            try { session.close(); } catch ( Exception e ) {}
            try { connection.close(); } catch ( Exception e ) {}

        }
        System.out.println( "Sender finished." );

    } // end main

    public static void main(String[] args){
        int i = 1;
        while(true) {
            new MOMSender("00"+i);
            i++;
            try {
                TimeUnit.SECONDS.sleep(5);
            }catch(InterruptedException exc){
                System.err.println(exc.getMessage());
            }
        }
    }
}
