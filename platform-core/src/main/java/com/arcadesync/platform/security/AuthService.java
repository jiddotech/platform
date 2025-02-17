package com.arcadesync.platform.security;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.arcadesync.platform.PlatformConstants;
import com.arcadesync.platform.enums.LogInFrom;
import com.arcadesync.platform.enums.ServiceContainerEnum;
import com.arcadesync.platform.exception.ApplicationException;
import com.arcadesync.platform.exception.AuthenticationException;
import com.arcadesync.platform.exception.PlatformExceptionCodes;
import com.arcadesync.platform.utility.CommonUtility;
import com.arcadesync.platform.utility.PlatformCommonService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthService {

	private static final String AUTH = "auth";

	@Autowired
	private SecurityConfigProps props;

	private String secretKey;

	@Autowired
	@Qualifier(PlatformConstants.EXTERNAL_SLOW_CLIENT)
	private RestTemplate template;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PlatformCommonService platformCommonService;

	private static final ServiceContainerEnum SERVICE = ServiceContainerEnum.AUTH_SERVICE;

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(props.getJwtSecretKey().getBytes());
	}

	public String createToken(String clientId, List<String> roles) {

		Claims claims = Jwts.claims().setSubject(clientId);
		claims.put(AUTH, roles);

		Date now = new Date();

		return Jwts.builder()//
				.setClaims(claims)//
				.setIssuedAt(now)//
				.signWith(SignatureAlgorithm.HS256, secretKey)//
				.compact();
	}

	public Authentication getClientAuthentication(String token) throws ApplicationException {
		LoggedInUser user = validateClientToken(token);
		Set<Permission> permissions = CommonUtility.getPermissions(user.getRoles());
		return new UsernamePasswordAuthenticationToken(user, "", permissions);
	}

	private LoggedInUser validateClientToken(String token) throws ApplicationException {
		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		headers.set(PlatformConstants.SSO_TOKEN_HEADER, props.getCreds().get(SERVICE));
		TokenRequest requets = new TokenRequest(token);
		HttpEntity<TokenRequest> entity = new HttpEntity<>(requets, headers);
		try {
			String url = platformCommonService.getInternalCallUrl(SERVICE, "/validate-token");
			log.trace("url: {} token request : {} headers : {}", url, requets, headers);
			HttpEntity<JsonNode> response = template.exchange(url, HttpMethod.POST, entity, JsonNode.class);
			log.trace("response from the server : {}", response.getBody());
			return mapper.convertValue(response.getBody(), LoggedInUser.class);
		} catch (HttpStatusCodeException exeption) {
			log.error("error response from  the server : {}", exeption.getResponseBodyAsString());
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		} catch (Exception exeption) {
			log.error("error occured while validating token", exeption);
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR);
		}

	}

	@Data
	@AllArgsConstructor
	private class TokenRequest {
		private String token;
	}

	public Authentication getServerAuthentication(String token) throws ApplicationException {
		UserDTO user = validateServerToken(token);
		Set<Permission> permissions = CommonUtility.getPermissions(user.getRoles());
		return new UsernamePasswordAuthenticationToken(user, "", permissions);
	}

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public UserDTO validateServerToken(String token) throws ApplicationException {
		try {
			String userName = getUsername(token);
			log.debug("server called : {}, in token : {}", userName, token);
			Jws<Claims> claimsWrapped = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			Claims claims = claimsWrapped.getBody();

			@SuppressWarnings("unchecked")
			List<String> roles = (List<String>) claims.get(AUTH);

			LoggedInUser user = new LoggedInUser();
			user.setRoles(new ArrayList<>());
			user.setUserId(userName);
			user.setLogInFrom(LogInFrom.INTERNAL_SERVICE);

			RoleDTO roleDTO = new RoleDTO();
			roleDTO.setRoleId(PlatformConstants.DEFAULT_ROLE_ID);
			roleDTO.setPermissions(roles);
			user.getRoles().add(roleDTO);
			return user;
		} catch (JwtException | IllegalArgumentException e) {
			throw new AuthenticationException(PlatformExceptionCodes.AUTHENTICATION_CODE);
		} catch (Exception exeption) {
			log.error("error occured while validating token", exeption);
			throw new ApplicationException(PlatformExceptionCodes.INTERNAL_SERVER_ERROR);
		}
	}

}