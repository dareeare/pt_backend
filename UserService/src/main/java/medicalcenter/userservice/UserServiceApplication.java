package medicalcenter.userservice;

import medicalcenter.userservice.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        try {
            // Явно загружаем драйвер PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        Configuration configuration = new Configuration()
                .addAnnotatedClass(Patient.class)
                .addAnnotatedClass(Visit.class)
                .addAnnotatedClass(Doctor.class)
                .setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/medicalcenter")
                .setProperty("hibernate.connection.username", "postgres")
                .setProperty("hibernate.connection.password", "sjsjsqo18ha5")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                .setProperty("hibernate.show_sql", "true");
        //Configuration configuration = new Configuration().addAnnotatedClass(User.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();

        try {
            session.beginTransaction();

            UUID patientId = UUID.fromString("dc99b312-5ef2-4e76-94b3-41a2ea8434f1");
            Patient patient = session.get(Patient.class, patientId);
            System.out.println(patient.getFirstName() +  " " + patient.getLastName());

            session.getTransaction().commit();

        } finally {
            session.close();
            sessionFactory.close();
        }
    }

}
