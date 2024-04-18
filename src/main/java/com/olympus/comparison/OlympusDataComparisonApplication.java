package com.olympus.comparison;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@MapperScan("com.olympus.comparison.repository")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class OlympusDataComparisonApplication {

    public static void main(String[] args) {
        SpringApplication.run(OlympusDataComparisonApplication.class, args);
    }

}
