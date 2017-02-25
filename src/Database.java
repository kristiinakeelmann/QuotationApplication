import java.sql.*;
import java.util.ArrayList;

//Kristeri koodi põhjal
class Database {

    Connection conn = null;

    public void initDatabase() {
        createConnection();
        createTable();

    }

    // Et andmebaasi kasutada peame sellega esiteks ühenduse looma
    public void createConnection() {
        try {
            Class.forName("org.sqlite.JDBC");                          // Lae draiver sqlite.jar failist
            this.conn = DriverManager.getConnection("jdbc:sqlite:test.db"); // loo ühendus andmebaasi failiga
        } catch (Exception e) {                                      // püüa kinni võimalikud errorid
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        System.out.println("Opened initDatabase successfully");            // lihtsalt meie enda jaoks teade
    }

    // Et andmebaasist kasu oleks, loome uue tabeli. See on nagu uus 'sheet' excelis.
    public void createTable() {
        // Käsk ise on CREATE TABLE ja sulgude vahel on kõik tulbad, mida tahan, et tabel hoiaks.
        String sql = "CREATE TABLE TSITAAT (ID INT AUTO_INCREMENT, TSITAAT TEXT, AASTA INT, MARKSONA TEXT)";
        updateTable(sql);
    }

    public void enterQuotation(String tsitaat, String aasta, String marksona) {
        // Andmete sisestamiseks on käsk INSERT. Esimestes sulgudes tulbad KUHU salvestada,teistes sulgudes VALUES() MIDA salvestada.
        String sql = "INSERT INTO TSITAAT (TSITAAT, AASTA, MARKSONA) VALUES ('" + tsitaat + "','" + aasta + "','" + marksona + "')";
        updateTable(sql);
    }

    public void selectTable() {
        try {
            Statement stat = this.conn.createStatement();
            stat.execute("SELECT * FROM TSITAAT");

            ResultSet rs = stat.executeQuery("SELECT * FROM TSITAAT");
            // Kui stat.executeQuery() toob tagasi tühja tulemuse, siis rs'i kasutada ei saa.

            // tsükkel
            Integer mitmes = 0;
            while (rs.next()) {
                mitmes = mitmes + 1;// mitmes++;
                System.out.println("Andmed: " + mitmes.toString() + rs.getString("ID") + ", " + rs.getString("TSITAAT") + ", " + rs.getString("AASTA") + ", " + rs.getString("MARKSONA"));
            }

            rs.close();
            stat.close(); // Statement tuleb samuti kinni panna nagu ka Connection.
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<String> selectQuoatitonByYearAndKeyword(String year, String keyWord) {
        try {
            Statement stat = this.conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT tsitaat FROM TSITAAT WHERE AASTA = '" + year + "' AND marksona = '" + keyWord + "'");
            ArrayList<String> quotesFromDatabase = new ArrayList<String>();
            while (rs.next()) {
                quotesFromDatabase.add(rs.getString("tsitaat"));
                break;
                // ei oota ära kõiki vastuseid, vaid võta mulle esimene leitud väärtus
            }
            rs.close();
            stat.close(); // Statement tuleb samuti kinni panna nagu ka Connection.
            return quotesFromDatabase;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }

    public ArrayList<String> selectColumn(String value) {
        try {
            Statement stat = this.conn.createStatement();
            ResultSet rs = stat.executeQuery("SELECT DISTINCT " + value + " FROM TSITAAT");
            // küsin unikaalsed andmebaasi väärtused
            ArrayList<String> valueFromDatabase = new ArrayList<String>();
            while (rs.next()) {
                valueFromDatabase.add(rs.getString(value));
            }
            rs.close();
            stat.close(); // Statement tuleb samuti kinni panna nagu ka Connection.
            return valueFromDatabase;

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<String>();
        }
    }


    // Andmebaasi muudatused ei tagasta väärtusi (erinevalt päringutest) ja on lihtne eraldi meetodi tuua.
    private void updateTable(String sql) {
        try {
            // Statement objekt on vajalik, et SQL_Login käsku käivitada
            if (this.conn == null) {
                return;
            }
            Statement stat = this.conn.createStatement();
            stat.executeUpdate(sql);
            stat.close(); // Statement tuleb samuti kinni panna nagu ka Connection.
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kui programmis avad ainult ühendusi ja ühtegi ei sulge siis see võib masina kokku jooksutada.
    public void sulgeYhendus() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Ühendus suletud");
    }

}
