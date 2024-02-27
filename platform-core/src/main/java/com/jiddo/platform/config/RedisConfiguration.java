package com.jiddo.platform.config;

import java.text.MessageFormat;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class RedisConfiguration {

	private RedisConfigurationProps props;

	@Bean
	public RedissonClient getRedisson() {
		Config config = new Config();
		String url = MessageFormat.format("redis://{0}:{1}", props.getHost(), String.valueOf(props.getPort()));
		config.useSingleServer().setAddress(url).setDatabase(props.getDb());
		RedissonClient client = Redisson.create(config);
		return client;
	}

}
