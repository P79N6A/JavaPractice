package com.hanxiaocu.timingtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @desc:
 * @author: hanchenghai
 * @date: 2018/11/05 3:19 PM
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Application {
	public static void main(String[] args) {
		new SpringApplication(Application.class).run(args);

	}
}
