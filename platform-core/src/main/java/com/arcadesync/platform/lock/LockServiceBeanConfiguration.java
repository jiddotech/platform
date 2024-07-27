package com.arcadesync.platform.lock;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class LockServiceBeanConfiguration {
	
	@Autowired
	private RedissonClient client; 

   @Bean
   @Profile(value = {"qa", "prod"})
   public LockService getRedissonLockService() {
	   return new RedissonLockService(client);
   }


   @Bean
   @ConditionalOnMissingBean(LockService.class)
   public LockService getMapBasedLockService() {
	   return new JavaMapBasedLockService();
	}

}