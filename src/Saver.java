/**
 * Created by piotrek on 23.05.17.
 */

//package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import java.io.*;

import org.postgresql.*;
import org.postgresql.util.PSQLException;

////import org.apache.poi.xssf.usermodel.XSSFSheet;
////import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.sql.*;

public class Saver {

    public static final String INSERT_RECORDS = "INSERT INTO java(date, usd, aud, cad, euro, huf, chf, gbp, jpy, czk, dkk, nok, sek, xdr, no, full_no) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static String GET_COUNT = "SELECT COUNT(*) FROM java";

    public void insertRecords(String filePath) {

            /* Create Connection objects */
        PreparedStatement prepStmt = null;
        java.sql.Statement stmt = null;
        int count = 0;
        ArrayList<String> mylist = new ArrayList<String>();

        try {
            Class.forName("org.postgresql.Driver");
            Connection con = null;
            con = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/java", "java", "qwer1234");

            System.out.println("Connection :: [" + con + "]");
            prepStmt = con.prepareStatement(INSERT_RECORDS);
            stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(GET_COUNT);
            while (result.next()) {

                int val = result.getInt(1);
                System.out.println(val);
                count = val + 1;

            }


            //prepStmt.setInt(1,count);

            /* We should now load excel objects and loop through the worksheet data */
            FileInputStream fis = new FileInputStream(new File(filePath));
            System.out.println("FileInputStream Object created..! ");
             /* Load workbook */
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            System.out.println("XSSFWorkbook Object created..! ");
            /* Load worksheet */
            HSSFSheet sheet = workbook.getSheetAt(0);
            System.out.println("XSSFSheet Object created..! ");
            // we loop through and insert data
            Iterator ite = sheet.rowIterator();
            System.out.println("Row Iterator invoked..! ");

            while (ite.hasNext()) {
                Row row = (Row) ite.next();
                System.out.println("Row value fetched..! ");
                Iterator<Cell> cellIterator = row.cellIterator();
                System.out.println("Cell Iterator invoked..! ");
                int index = 1;
                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();

                    DataFormatter fmt = new DataFormatter();
                    String i = fmt.formatCellValue(cell);

                    System.out.println("getting cell value..! ");

                    if (index == 1) {
                        try {
                            int foo = Integer.parseInt(i);
                        } catch (NumberFormatException e) {
                            break;
                        }
                    }



                    if (index == 1) {
                        String x = i.substring(0, 4) + "-" + i.substring(4, 6) + "-" + i.substring(6, i.length());
                        prepStmt.setDate(index, java.sql.Date.valueOf(x));
                        index++;
                    } else if (cell.getCellType() == 1) {
                        prepStmt.setString(index, i);
                        index++;
                    } else if (i.indexOf(',') == -1) {
                        prepStmt.setInt(index, (int) Integer.parseInt(i));
                        index++;
                    } else if (cell.getCellType() == 0) {
                        int y = i.indexOf(',');
                        String n = i.substring(0, (y)) + '.' + i.substring(y + 1);
                        Float f = Float.parseFloat(n);
                        prepStmt.setFloat(index, f);
                        index++;
                    }

                }
//                System.out.println(prepStmt);
                //we can execute the statement before reading the next row
                try {
                    prepStmt.executeUpdate();
                    System.out.println("New row added");
                    prepStmt = con.prepareStatement(INSERT_RECORDS);
                } catch (PSQLException e) {
                    prepStmt = con.prepareStatement(INSERT_RECORDS);
                }
            }

               /* Close input stream */
            fis.close();
               /* Close prepared statement */
            prepStmt.close();

               /* Close connection */
            con.close();
            System.out.println("Connection closed!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



