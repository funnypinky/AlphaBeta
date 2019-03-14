/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package alphabeta.data;

import alphabeta.AlphaBeta;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen Häsler
 */
public class DatabaseAccess {

    private Connection connection;
    private String databasename;

    /**
     *
     * @param databasename
     */
    public DatabaseAccess(String databasename) {
        this.databasename = databasename;
    }

    /**
     *
     * @throws SQLException
     */
    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + AlphaBeta.DATABASEPATH + this.databasename);
        System.out.println("Connection to SQLite has been established.");
    }

    /**
     *
     * @throws SQLException
     */
    public void shutdown() throws SQLException {
        connection.close();
    }

    /**
     *
     * @return
     */
    public Connection getConnect() {
        return this.connection;
    }

    /**
     *
     * @param query
     * @return
     * @throws SQLException
     */
    public ResultSet executeQuery(final String query) throws SQLException {
        final Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        stmt.close();
        return rs;
    }

    /**
     *
     * @param update
     * @throws SQLException
     */
    public void executeUpdate(final String update) throws SQLException {
        final Statement stmt = connection.createStatement();
        final int executionStatus = stmt.executeUpdate(update);
        if (executionStatus == -1) {
            System.out.println("error on execution");
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public boolean isExistTable(String tableName) {
        boolean existTable = false;
        String temp[] = getTables();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].equalsIgnoreCase(tableName)) {
                existTable = true;
            }
        }
        return existTable;
    }

    /**
     *
     * @return
     */
    public String[] getTables() {
        List<String> temp = new ArrayList<>();
        String getReturn[];
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            try (ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"})) {
                while (resultSet.next()) {
                    temp.add(resultSet.getString("TABLE_NAME"));
                }
            }
            getReturn = new String[temp.size()];
            for (int i = 0; i < temp.size(); i++) {
                getReturn[i] = temp.get(i);
            }
            return getReturn;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void firstRun() {
        String createOrgan = "CREATE TABLE organTable ("
                + "    idNTCP    INTEGER       NOT NULL"
                + "                            PRIMARY KEY AUTOINCREMENT "
                + "                            UNIQUE,"
                + "    organ VARCHAR,"
                + "    n DOUBLE,"
                + "    m DOUBLE,"
                + "    td50 DOUBLE,"
                + "    alphabetaMax DOUBLE,"
                + "    alphabetaMin DOUBLE);";
        String createTCP = "CREATE  TABLE tcp ("
                + "idTCP INTEGER NOT NULL"
                + " PRIMARY KEY AUTOINCREMENT"
                + "UNIQUE, "
                + "tumorentitaet VARCHAR, "
                + "TD50 DOUBLE , y DOUBLE);";

        String createSynonym = "CREATE TABLE synonym ("
                + "    id      INTEGER PRIMARY KEY AUTOINCREMENT"
                + "                    UNIQUE,"
                + "    organID         REFERENCES organTable (idNTCP) ON DELETE CASCADE,"
                + "    synonym VARCHAR);";
        try {
            final Statement stmt = this.connection.createStatement();
            stmt.addBatch(createTCP);
            stmt.addBatch(createOrgan);
            stmt.addBatch(createSynonym);
            stmt.executeBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
