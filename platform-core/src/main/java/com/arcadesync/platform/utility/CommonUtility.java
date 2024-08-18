package com.arcadesync.platform.utility;

import java.io.Closeable;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PropertyPlaceholderHelper;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.dto.CodeValueDTO;
import com.arcadesync.platform.dto.GeoCoordinatesDTO;
import com.arcadesync.platform.dto.Range;
import com.arcadesync.platform.dto.SaveTagRequest;
import com.arcadesync.platform.exception.ApplicationException;
import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.exception.ValidationException;
import com.arcadesync.platform.filters.DateRangeFilter;
import com.arcadesync.platform.security.Permission;
import com.arcadesync.platform.security.RoleDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CommonUtility {

	private static final Map<Integer, String> DAYS_MAPPING = new HashMap<>();
	private static final Map<Integer, String> WEEKS_MAPPING = new HashMap<>();
	private static final ZoneOffset INDIA_ZONE_OFFSET = ZoneOffset.ofHoursMinutes(5, 30);
	private static final DateTimeFormatter IST_DATE_FOMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter IST_MONTH_FOMATTER = DateTimeFormatter.ofPattern("MMMM yy");

	private static final String PLACEHOLDER_SUFFIX_CURLY = "}";
	private static final String PLACEHOLDER_PREFIX_CURLY = "{";
	private static final Pattern ALPHANUMERIC_AND_UNDERSCORE_PATTERN = Pattern.compile("[A-Za-z0-9_]+",
			Pattern.CASE_INSENSITIVE);

	static {
		DAYS_MAPPING.put(0, "Today");
		DAYS_MAPPING.put(1, "Yesterday");

		WEEKS_MAPPING.put(0, "This Week");
		WEEKS_MAPPING.put(1, "Last Week");
		WEEKS_MAPPING.put(2, "2 weeks ago");
		WEEKS_MAPPING.put(3, "3 weeks ago");
		WEEKS_MAPPING.put(4, "4 weeks ago");
	}

	private CommonUtility() {
	}

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yy", Locale.UK)
			.withZone(PlatformConstants.CURRENT_ZONE_ID);
	private static final String COLON = ":";

	private final static String REG_EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,10}$";
	private final static Pattern PATTERN_EMAIL = Pattern.compile(REG_EMAIL_PATTERN);

	public static void close(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Exception e) {
			log.error("Error occured while closing the closeable.", e);
		}
	}

	public static boolean validateEmail(final String email) {
		if (ObjectUtils.isEmpty(email)) {
			return false;
		}
		return PATTERN_EMAIL.matcher(email).find();
	}

	public static String getUrlSlug(String str) {
		return ObjectUtils.isEmpty(str) ? str : replaceSpaceWithHyphen(str.toLowerCase());
	}

	public static String replaceSpaceWithHyphen(String str) {
		return ObjectUtils.isEmpty(str) ? str : str.replace(" ", "-");
	}

	public static Range<Integer> parseRange(String range, String regexSplitter) {
		String arr[] = range.split(regexSplitter);
		if (arr.length != 2) {
			return null;
		}
		return new Range<Integer>(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
	}

	public static Range<Long> parseRangeLong(String range, String regexSplitter) {
		String arr[] = range.split(regexSplitter);
		if (arr.length != 2) {
			return null;
		}
		return new Range<Long>(Long.parseLong(arr[0]), Long.parseLong(arr[1]));
	}

	public static List<CodeValueDTO<String, String>> getRecentTimeFilterValues() {
		return getRecentTimeFilterValues(3, 3, 4);
	}

	public static List<CodeValueDTO<String, String>> getRecentTimeFilterValues(int day, int week, int month) {
		List<CodeValueDTO<String, String>> list = new ArrayList<>();
		if (!WEEKS_MAPPING.containsKey(week)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Invalid filter week configuration");
		}
		getDayFilterValues(day, list);
		getWeekFilterValues(week, list);
		getMonthlyFilterValues(month, list);
		return list;
	}

	public static void getMonthlyFilterValues(int noOfFilters, List<CodeValueDTO<String, String>> list) {
		if (noOfFilters == 0) {
			return;
		}
		LocalDate nowDate = LocalDate.now();
		Instant end = nowDate.plusMonths(1).withDayOfMonth(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusMonths(i).withDayOfMonth(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
			String code = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			String value = MessageFormat.format("By Month - {0}", FORMATTER.format(start));
			list.add(new CodeValueDTO<String, String>(code, value));
			end = start;
		}
	}

	public static void getDayFilterValues(int noOfFilters, List<CodeValueDTO<String, String>> list) {
		if (noOfFilters == 0) {
			return;
		}
		LocalDate nowDate = LocalDate.now();
		Instant end = nowDate.plusDays(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusDays(i).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
			String code = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			String text = DAYS_MAPPING.get(i);
			if (ObjectUtils.isEmpty(text)) {
				text = getDate(start);
			}
			String value = MessageFormat.format("By Day - {0}", text);
			list.add(new CodeValueDTO<String, String>(code, value));
			end = start;
		}
	}

	public static void getWeekFilterValues(int noOfFilters, List<CodeValueDTO<String, String>> list) {
		if (noOfFilters == 0) {
			return;
		}
		LocalDate nowDate = LocalDate.now();
		nowDate = nowDate.minusDays(nowDate.getDayOfWeek().getValue() - 1);
		Instant end = nowDate.plusWeeks(1).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
		for (int i = 0; i <= noOfFilters; ++i) {
			log.trace("start time : {}", end);
			Instant start = nowDate.minusWeeks(i).atStartOfDay().toInstant(INDIA_ZONE_OFFSET);
			String code = MessageFormat.format("{0}-{1}", String.valueOf(start.toEpochMilli()),
					String.valueOf(end.toEpochMilli()));
			String value = MessageFormat.format("By Week - {0}", WEEKS_MAPPING.get(i));
			list.add(new CodeValueDTO<String, String>(code, value));
			end = start;
		}
	}

	public static String getKey(String first, String second) {
		return MessageFormat.format("{0}###{1}", first, second);
	}

	public static String getDate(Instant instant) {
		if (ObjectUtils.isEmpty(instant)) {
			return null;
		}
		return instant.atZone(PlatformConstants.CURRENT_ZONE_ID).format(IST_DATE_FOMATTER);
	}

	public static String getMonth(Instant instant) {
		if (ObjectUtils.isEmpty(instant)) {
			return null;
		}
		return instant.atZone(PlatformConstants.CURRENT_ZONE_ID).format(IST_MONTH_FOMATTER);
	}

	public static String replaceKeysWithValueProps(String message, Map<String, String> mapOfKeyValues) {
		log.debug("props : {}", mapOfKeyValues);
		Properties props = new Properties();
		if (!ObjectUtils.isEmpty(mapOfKeyValues)) {
			for (Entry<String, String> entry : mapOfKeyValues.entrySet()) {
				props.put(entry.getKey(),
						Objects.isNull(entry.getValue()) ? PlatformConstants.EMPTY_STRING : entry.getValue());
			}
		}
		PropertyPlaceholderHelper propertyPlaceholderHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX_CURLY,
				PLACEHOLDER_SUFFIX_CURLY, COLON, false);
		return propertyPlaceholderHelper.replacePlaceholders(message, props);
	}

	public static String getLocation(GeoCoordinatesDTO coordinates) {
		return MessageFormat.format("https://maps.google.com/maps?q={0},{1}", coordinates.getLat(),
				coordinates.getLon());
	}

	public static String maskStringToLast4Characters(String string) {
		if (ObjectUtils.isEmpty(string)) {
			return "";
		} else {
			String last4Digits = string.substring(string.length() - 4);
			return MessageFormat.format("xxxxxx{0}", last4Digits);
		}
	}

	public static void validateRequest(SaveTagRequest request) {
		if (ObjectUtils.isEmpty(request)) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid request");
		}
		if (ObjectUtils.isEmpty(request.getEntityId())) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(), "Invalid entityId");
		}
		if (!ObjectUtils.isEmpty(request.getTags())) {
			for (String tag : request.getTags()) {
				validateTag(tag);
			}
		}
	}

	public static void validateTag(String tag) {
		Matcher m = ALPHANUMERIC_AND_UNDERSCORE_PATTERN.matcher(tag);

		if (!m.matches()) {
			throw new ValidationException(PlatformExceptionCodes.INVALID_DATA.getCode(),
					"Only Alphanumeric and underscore are allowed. Invalid tag : " + tag);
		}
	}

	public static DateRangeFilter getLastMonthFilter(String key) {
		ZonedDateTime zd = Instant.now().atZone(PlatformConstants.CURRENT_ZONE_ID);
		ZonedDateTime end = zd.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime start = zd.minusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
		log.debug("start: {} end: {}, al start: {}, al end: {}", start.toEpochSecond(), end.toEpochSecond(),
				start.toInstant().toEpochMilli(), end.toInstant().toEpochMilli());
		return new DateRangeFilter(key, start.toInstant(), end.toInstant());
	}

	public static DateRangeFilter getMonthFilter(String key, int month, int year) {
		ZonedDateTime zd = Instant.now().atZone(PlatformConstants.CURRENT_ZONE_ID);
		ZonedDateTime start = zd.withMonth(month).withYear(year).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
		ZonedDateTime end = start.plusMonths(1);
		return new DateRangeFilter(key, start.toInstant(), end.toInstant());
	}

	public static Set<Permission> getPermissions(List<RoleDTO> roles) {
		Set<Permission> permissions = new HashSet<>();
		if (!ObjectUtils.isEmpty(roles)) {
			for (RoleDTO role : roles) {
				if (!ObjectUtils.isEmpty(role.getPermissions())) {
					for (String permission : role.getPermissions()) {
						permissions.add(new Permission(permission));
					}
				}
			}
		}
		return permissions;
	}

	public static void sleep(long milliSeconds) {
		if (milliSeconds > 0) {
			try {
				Thread.sleep(milliSeconds);
			} catch (InterruptedException e) {
				throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR.getCode(),
						"sleep method interrupted");
			}
		}
	}

	public static boolean isChargerClosed(Range<Instant> bookingDuration, Range<Integer> openCloseTimeInSeconds) {
		Optional<Range<Instant>> openCloseTimeToday = getTodayOpenTimings(openCloseTimeInSeconds);
		if (openCloseTimeToday.isPresent()) {
			log.debug("charger open timings : {}, bookingDuration: {}", openCloseTimeToday.get(), bookingDuration);
			Range<Instant> openTimings = openCloseTimeToday.get();
			return bookingDuration.getFrom().isBefore(openTimings.getFrom())
					|| bookingDuration.getTo().isAfter(openTimings.getTo());
		} else {
			return false;
		}
	}

	public static Optional<Range<Instant>> getTodayOpenTimings(Range<Integer> openCloseTimeInSeconds) {
		ZonedDateTime now = ZonedDateTime.now(PlatformConstants.CURRENT_ZONE_ID);
		Instant todayStart = now.truncatedTo(ChronoUnit.DAYS).toInstant();
		Instant start = todayStart.plus(openCloseTimeInSeconds.getFrom(), ChronoUnit.SECONDS);
		Instant end = todayStart.plus(openCloseTimeInSeconds.getTo(), ChronoUnit.SECONDS);
		Duration duration = Duration.between(start, end);
		if (duration.toMinutes() > 1430) {
			return Optional.empty();
		} else {
			return Optional.of(new Range<Instant>(start, end));
		}
	}

	public static <T> List<T> getPage(List<T> sourceList, Pageable page) {
		if (page.getPageNumber() < 0 || page.getPageSize() <= 0) {
			throw new IllegalArgumentException("invalid page size: " + page.getPageSize());
		}

		int fromIndex = page.getPageNumber() * page.getPageSize();
		if (sourceList == null || sourceList.size() <= fromIndex) {
			return Collections.emptyList();
		}
		return sourceList.subList(fromIndex, Math.min(fromIndex + page.getPageSize(), sourceList.size()));
	}

	public static Range<Instant> getLastMonthRange() {
		// Create a ZonedDateTime object
		ZonedDateTime zonedDateTime = ZonedDateTime.now();

		// Get the month of the ZonedDateTime object
		zonedDateTime = zonedDateTime.minusMonths(1);
		int month = zonedDateTime.getMonth().getValue();
		int year = zonedDateTime.getYear();

		// Create a LocalDate object with the month and year of the ZonedDateTime object
		LocalDate localDate = LocalDate.of(year, month, 1);

		// Create a LocalTime object with the time set to 00:00:00
		LocalTime localTime = LocalTime.of(0, 0, 0);

		// Create a LocalDateTime object with the LocalDate and LocalTime objects
		LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

		// Create a ZonedDateTime object with the LocalDateTime object and the timezone
		// of the original ZonedDateTime object
		ZonedDateTime startOfMonth = ZonedDateTime.of(localDateTime, zonedDateTime.getZone());
		return new Range<Instant>(startOfMonth.toInstant(), startOfMonth.plusMonths(1).toInstant().minusMillis(1L));
	}

	public static Range<Instant> getThisMonthRange() {
		// Create a ZonedDateTime object
		ZonedDateTime zonedDateTime = ZonedDateTime.now();

		// Get the month of the ZonedDateTime object
		int month = zonedDateTime.getMonth().getValue();
		int year = zonedDateTime.getYear();

		// Create a LocalDate object with the month and year of the ZonedDateTime object
		LocalDate localDate = LocalDate.of(year, month, 1);

		// Create a LocalTime object with the time set to 00:00:00
		LocalTime localTime = LocalTime.of(0, 0, 0);

		// Create a LocalDateTime object with the LocalDate and LocalTime objects
		LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

		// Create a ZonedDateTime object with the LocalDateTime object and the timezone
		// of the original ZonedDateTime object
		ZonedDateTime startOfMonth = ZonedDateTime.of(localDateTime, zonedDateTime.getZone());
		return new Range<Instant>(startOfMonth.toInstant(), startOfMonth.plusMonths(1).toInstant().minusMillis(1L));
	}

	public static Range<Instant> getTomorrow() {
		ZonedDateTime now = ZonedDateTime.now();
		LocalDateTime tomorrow = now.plusDays(1L).toLocalDateTime();
		LocalDateTime min = tomorrow.with(LocalTime.MIN);
		LocalDateTime max = tomorrow.with(LocalTime.MAX);
		return new Range<Instant>(ZonedDateTime.of(min, now.getZone()).toInstant(),
				ZonedDateTime.of(max, now.getZone()).toInstant());
	}

	public static Range<Instant> getToday() {
		ZonedDateTime now = ZonedDateTime.now();
		LocalDateTime tomorrow = now.toLocalDateTime();
		LocalDateTime min = tomorrow.with(LocalTime.MIN);
		LocalDateTime max = tomorrow.with(LocalTime.MAX);
		return new Range<Instant>(ZonedDateTime.of(min, now.getZone()).toInstant(),
				ZonedDateTime.of(max, now.getZone()).toInstant());
	}

	public static Range<Instant> getYesterday() {
		ZonedDateTime now = ZonedDateTime.now();
		LocalDateTime tomorrow = now.minusDays(1L).toLocalDateTime();
		LocalDateTime min = tomorrow.with(LocalTime.MIN);
		LocalDateTime max = tomorrow.with(LocalTime.MAX);
		return new Range<Instant>(ZonedDateTime.of(min, now.getZone()).toInstant(),
				ZonedDateTime.of(max, now.getZone()).toInstant());
	}

	public static Pageable getPageableWithDefaultSort(Pageable page) {
		return PageRequest.of(page.getPageNumber(), page.getPageSize(), PlatformConstants.DEFAULT_SORT);
	}
}
