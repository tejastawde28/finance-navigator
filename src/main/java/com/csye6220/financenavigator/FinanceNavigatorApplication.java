package com.csye6220.financenavigator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class FinanceNavigatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceNavigatorApplication.class, args);
    }
}
