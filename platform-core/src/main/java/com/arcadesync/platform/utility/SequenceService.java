package com.arcadesync.platform.utility;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.arcadesync.platform.exception.LoggerType;
import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.exception.ValidationException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SequenceService {

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private PlatformCommonService platformCommonService;
	private static final String BASE_SALT = "23456789abcdefghijkmnpqrstuvwxyz";
	private static final Map<Long, Character> MAP_OF_INTEGER_TO_CHARACTER = new HashMap<>();

	static {
		populate();
	}

	private static void populate() {
		long i = 0;
		for (Character c : BASE_SALT.toCharArray()) {
			MAP_OF_INTEGER_TO_CHARACTER.put(i, c);
			++i;
		}
	}

	public String getNextId(String basePath) {
		Long val = getNextNumber(basePath);
		return getString(val);
	}

	private Long getNextNumber(String id) {
		Query query = new Query();
		Criteria criteria = Criteria.where("_id").is(id);
		query.addCriteria(criteria);
		Update update = new Update();
		update.inc("value", 1L);
		mongoTemplate.updateFirst(query, update, SequenceEntity.class);
		SequenceEntity value = mongoTemplate.findById(id, SequenceEntity.class);
		if (ObjectUtils.isEmpty(value)) {
			value = new SequenceEntity();
			value.setId(id);
			value.setValue(1L);
			platformCommonService.takeLock("CREATE_NEW_KEY" + id, 1, "key generation failed try again after 2 seconds",
					LoggerType.ERROR);
			mongoTemplate.save(value);
		}
		log.info("auto increment: {}", value);
		return value.getValue();
	}

	private String getString(Long val) {
		Long copyVal = val;
		if (ObjectUtils.isEmpty(val)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Unable to generate the unique random string");
		}
		StringBuilder sb = new StringBuilder();
		int base = BASE_SALT.length();
		while (val > 0) {
			long modulo = val % base;
			sb.append(MAP_OF_INTEGER_TO_CHARACTER.get(modulo));
			val = val / base;
		}
		log.debug("unique string generated : {} for number {}", sb.toString(), copyVal);
		return sb.reverse().toString();
	}

	@Data
	@Document(collection = "sequence_info")
	public static class SequenceEntity {
		@Id
		private String id;
		private Long value;
		private Instant updatedAt;
	}

}