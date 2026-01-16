package services;

import units.Employee;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Properties;

public class EmailService {

    // CONFIGURATION
    private final String FROM_EMAIL = "goldenhourfop@gmail.com";
    private final String APP_PASSWORD = "";
    private final String TO_EMAIL = "nurinhumairafauzi@gmail.com"; // send to yourself

    private final String RECEIPT_FOLDER = "data/SalesReceipt";

    public void sendDailySalesReport(Employee manager, double totalSales) {

        String today = LocalDate.now().toString();

        try {
            //SMTP properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            //authentication
            Session session = Session.getInstance(props,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                        }
                    });

            //create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(TO_EMAIL));
            message.setSubject("Daily Sales Report - " + today);

            //email body
            String body =
                    "Hello Headquarters,\n\n" +
                    "Daily Sales Summary\n" +
                    "Date: " + today + "\n" +
                    "Total Sales Amount: RM" + totalSales + "\n\n" +
                    "Attached are the daily sales receipts for all outlets.\n\n" +
                    "Regards,\n" +
                    manager.getName() + " (" + manager.getOutlet() + ")";

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            //Attach receipts for today
            File folder = new File(RECEIPT_FOLDER);
            File[] files = folder.listFiles();

            if (files != null) {
                for (File f : files) {
                    if (f.getName().endsWith("_sales_" + today + ".txt")) {
                        MimeBodyPart attachment = new MimeBodyPart();
                        attachment.attachFile(f);
                        multipart.addBodyPart(attachment);
                    }
                }
            }

            message.setContent(multipart);

            //send email
            Transport.send(message);

            System.out.println("Daily sales report email sent successfully.");

        } catch (Exception e) {
            System.out.println("Failed to send daily sales email.");
            e.printStackTrace();
        }
    }
}