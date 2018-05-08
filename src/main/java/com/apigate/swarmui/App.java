package com.apigate.swarmui;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.apigate.swarmui.model.SwarmUISetting;


@SpringBootApplication
@MapperScan("com.apigate.swarmui.mapper")
@EnableConfigurationProperties({SwarmUISetting.class})
public class App {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(App.class);
		// 关闭Banner打印
		app.setBannerMode(Banner.Mode.OFF);
		// 添加监听器
		// app.addListeners(new MyListener());
		app.run(args);
	}
}
