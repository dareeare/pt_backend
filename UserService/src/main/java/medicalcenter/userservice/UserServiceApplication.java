package medicalcenter.userservice;

import lombok.extern.log4j.Log4j2;
import medicalcenter.userservice.model.entity.Patient;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class UserServiceApplication {

    public static void main(String[] args) {
        try {
            // Явно загружаем драйвер PostgreSQL
            log.debug("Loading PostgreSQL driver");
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return;
        }
        Configuration configuration = new Configuration()
                .addAnnotatedClass(Patient.class)
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

            //User user = session.get(User.class, 1);
            //System.out.println(user.getFirstName());

            session.getTransaction().commit();

        } finally {
            session.close();
            sessionFactory.close();
        }
    }

}
