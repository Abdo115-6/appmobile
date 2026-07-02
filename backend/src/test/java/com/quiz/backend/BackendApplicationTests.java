package com.quiz.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Test
    void contextLoads() {
    }

    @Test
    void inspectItmMvtColumns() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("--- COLUMNS IN ITMMVT ---");
            try (ResultSet columns = metaData.getColumns(null, "MALLZELLIJ", "ITMMVT", "%")) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String typeName = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    System.out.println("Column: " + columnName + " (" + typeName + ", size: " + columnSize + ")");
                }
            }
            System.out.println("-------------------------");
        }
    }
}

