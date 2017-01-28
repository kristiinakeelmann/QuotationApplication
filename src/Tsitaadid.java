import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;


public class Tsitaadid {

    public Tsitaadid() {
        final Database dbcon = new Database();
        dbcon.createConnection();


        Stage stage = new Stage();
        StackPane stackPane = new StackPane();
        BorderPane borderPane = new BorderPane();
        stackPane.getChildren().addAll(borderPane);
        Scene scene = new Scene(stackPane, 600, 600);
        stage.setScene(scene);
        stage.show();


        ComboBox<String> yearCombobox = new ComboBox<String>();
        ArrayList<String> yearComboboxValue = new ArrayList<String>();
        try {
            yearComboboxValue = dbcon.selectColumn("AASTA");
        } catch (Exception e) {
            e.printStackTrace();
            yearComboboxValue = new ArrayList<String>();
        }
        yearCombobox.getItems().addAll(yearComboboxValue);

        ComboBox<String> keywordCombobox = new ComboBox<String>();
        ArrayList<String> keywordComboboxValue = new ArrayList<String>();
        try {
            keywordComboboxValue = dbcon.selectColumn("MARKSONA");
        } catch (Exception e) {
            e.printStackTrace();
            keywordComboboxValue = new ArrayList<String>();
        }

        keywordCombobox.getItems().addAll(keywordComboboxValue);


        Button findQuotationButton = new Button("Leia tsitaat valiku põhjal!");

        Button sendEmailButton = new Button("Saada!");


        Text quotationText = new Text();

        TextField emailTextField = new TextField();
        // JLabel myLabel1 = new JLabel("Saada tsitaat oma e-mailile!");
        emailTextField.setPromptText("Sisesta oma e-mail");

        Text emailSendingMessageTextField = new Text();


        TextField quotationTextField = new TextField();
        // JLabel myLabel2 = new JLabel("Sisesta uus tsitaat");
        quotationTextField.setPromptText("Sisesta tsitaat");

        TextField yearTextField = new TextField();
        yearTextField.setPromptText("Sisesta ilmumisaasta");

        TextField keywordTextField = new TextField();
        keywordTextField.setPromptText("Sisesta märksõna");

        Button saveQuotationButton = new Button("Salvesta!");

        VBox vBox = new VBox(yearCombobox, keywordCombobox, findQuotationButton, quotationText, emailTextField, sendEmailButton, emailSendingMessageTextField, quotationTextField, yearTextField, keywordTextField, saveQuotationButton);
        vBox.setSpacing(20);
        borderPane.setCenter(vBox);


        findQuotationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                final Database dbcon = new Database();
                dbcon.createConnection();
                dbcon.selectTable();

                //muutuja, nimi, selle hilisem kasutamine
                ArrayList<String> result = dbcon.selectQuoatitonByYearAndKeyword(yearCombobox.getValue(), keywordCombobox.getValue());
                if (result.size() > 0) {
                    quotationText.setText(result.get(0));
                } else {
                    quotationText.setText("Ei leidu tsitaati");
                }

            }

        });

        sendEmailButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (emailTextField.getText().toString().equalsIgnoreCase("")) {
                    emailSendingMessageTextField.setText("Sisesta palun e-mail!");
                    return;
                }

                //kasutatud kasutaja anirudh koodi (loodud 28/10/14) põhjal

                final String username = "tsitaadirakendus@gmail.com";
                final String password = "12345tsitaadid";

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.socketFactory.port", "465");
                props.put("mail.smtp.socketFactory.class",
                        "javax.net.ssl.SSLSocketFactory");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.port", "465");
                try {

                    Session session = Session.getDefaultInstance(props,
                            new javax.mail.Authenticator() {
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    return new PasswordAuthentication(username, password);
                                }
                            });
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress("tsitaadirakendus@gmail.com"));
                    message.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(emailTextField.getText().toString()));
                    message.setSubject("Kiri tsitaadirakenduselt");
                    message.setText("Sinu valitud tsitaat:" +
                            quotationTextField.getText().toString());
                    Transport.send(message);

                    System.out.println("Mail sent succesfully!");
                    emailSendingMessageTextField.setText("E-mail edastatud!");

                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }

            }
        });


        saveQuotationButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                if (quotationTextField.getText().toString().equalsIgnoreCase("")) {
                    return;
                }

                dbcon.enterQuotation(quotationTextField.getText().toString(), yearTextField.getText().toString(), keywordTextField.getText().toString());

            }
        });
    }
}
