package com.notice.board.account;

import java.net.URI;
import java.time.LocalDateTime;

import javax.validation.Valid;

import com.notice.board.account.dto.AccountDto;
import com.notice.board.account.dto.KakaoOauthDto;
import com.notice.board.account.dto.OauthToken;
import com.notice.board.account.dto.ResponseDto;
import com.notice.board.mail.EmailMessage;
import com.notice.board.mail.EmailService;
import com.notice.board.security.PrincipalDetail;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RestController
public class AccountController {

	private final AccountService accountService;
	private final AccountRepository accountRepository;
	private final EmailService emailService;
	private final PasswordEncoder passwordEncoder;

//	private final AuthenticationManager authenticationManager;

//	@Qualifier("AccountController")
//	private  AuthenticationManager authenticationManager;

//	@GetMapping("/")
//	public ResponseEntity<?> home(){
		//?????? ????????? ???????????? jwt?????? ?????? ???????????? ??? ???????????? (???????????? jwt???????????? ???????????????)
	//?????? ???????????? ???????????? ??????????????? ????????? ??? ??????????
//		return ResponseEntity.ok().body("sd");
//	}
	
	
	//??? ??????????????? ??????
	@GetMapping("/account/mypage")
	public ResponseEntity<?> home(@RequestHeader HttpHeaders headers){
		System.out.println("????????? : " + headers);
		System.out.println("????????? ??? ");
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		if(principal.equals("anonymousUser")) {
			System.out.println("???????????????");
		}else {
			System.out.println("????????? ?????? ?????? 1");
			PrincipalDetail principalDetail = (PrincipalDetail)principal;

			String username = principalDetail.getUsername();
			//????????? ?????? ????????? ????????????
			Account account =accountRepository.findByUsername(username);
			System.out.println("????????? ?????? ?????? 2");
			System.out.println(account);
			return ResponseEntity.ok().body(account);
		}
		
		return ResponseEntity.ok().body(null);
	}
	
	// TODO ????????? ??? ??????????
	// ????????????
	@PostMapping("/account/join")
	public ResponseEntity<?> join(@Valid @RequestBody AccountDto accountDto, Errors errors) {
		try {

//			if(errors.hasErrors()) {
//				System.out.println("1????????? : " + errors.getFieldError("nickname").getDefaultMessage());
//				System.out.println("2????????? : " + errors.getFieldValue("nickname"));
//			}
			
			// ?????????(Dto -> Entity)
			Account account = Account.builder().email(accountDto.getEmail()).username(accountDto.getUsername())
					.password(accountDto.getPassword()).role("USER").build();

			// ????????????
			account.generateEmailCheckToken();
			
			///////////////////////////////////////////////////////////////////////
			
			// ????????? Account??????
//			Account authResult = accountService.join(account);
			
			//????????? ??????
			if(account == null || account.getUsername() == null) {
				System.out.println(account);
				return ResponseEntity.ok().body(account);
			}
			
			//???????????? ????????? ?????? ?????? ??????????????? ??????
			if(accountRepository.existsByUsername(account.getUsername())) {
				return ResponseEntity.ok().body(account);
			}
			
			//?????????
			account.encodePassword(passwordEncoder);
			
			//??????
			Account authResult = accountRepository.save(account);
			
			//?????? ?????????
			EmailMessage emailMessage = EmailMessage.builder()
					.to(authResult.getEmail())
					.subject("TM ???????????? ?????? ???????????????.")
					.message("????????????: "+authResult.getEmailCheckToken())
					.build();
			
			emailService.sendEmail(emailMessage);
			
			
			
			
			///////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////

			// ?????????(Entity -> Dto)
			AccountDto dto = AccountDto.builder().email(authResult.getEmail()).username(authResult.getUsername())
					.build();

			return ResponseEntity.ok().body(null);

		} catch (Exception e) {
			ResponseDto responseDto = ResponseDto.builder().error(e.getMessage()).build();
			return ResponseEntity.badRequest().body(null);
		}
	}
	
	//
	// ?????? ?????? ????????? ??????
	// rnbtbybcuffxraba(??????)
	@Transactional
	@PostMapping("/check-email-token")
	public ResponseEntity<?> checkEmailToken(@RequestBody AccountDto accountDto) {
		
		
		
		// ???????????? ????????? ?????? ??? ??? ???????????? ????????? ????????????
		System.out.println(accountDto.getToken()); //?????????
		System.out.println(accountDto.getEmail()); //?????????
		String token = accountDto.getToken();

		Account account = accountRepository.findByEmail(accountDto.getEmail());
		System.out.println("???????????? : " + account);
		System.out.println("???????????? : " + accountDto);
		
		
		if(account == null) {
			account.setUsername("sulbin");
			return ResponseEntity.ok().body(account);
		}

		if (!account.getEmailCheckToken().equals(accountDto.getToken())) {
			String emailtokenError = "wrong.token";
//			return ResponseEntity.badRequest().body(accountDto);//
//			return ResponseEntity.ok(accountDto);//
			return new ResponseEntity<>(accountDto,HttpStatus.OK);
		}

		// ????????? ?????? ??????
		account.setEmailVerified(true);
		account.setJoinedAt(LocalDateTime.now());
		
		return ResponseEntity.ok().body(null);
	}

	// ???????????? ??????
	@PostMapping("/account/findpassword")
	public ResponseEntity<?> findPassword(@RequestBody AccountDto accountDto) {
		// ????????? ??????
		// ????????? ????????? ????????? ?????? ???????????? ????????????
		if(!accountRepository.existsByEmail(accountDto.getEmail())) {
			return ResponseEntity.ok().body(accountDto);
		}
		Account account = accountRepository.findByEmail(accountDto.getEmail());
//		if(account == null) {
//			account.setUsername("sulbin");
//			return ResponseEntity.ok().body(account);
//		}

		System.out.println("??????");
		// ?????? ?????? ??????
		account.temporaryPassword();

		EmailMessage emailMessage = EmailMessage.builder().to(account.getEmail()).subject("TM ?????? ???????????? ?????? ???????????????.")
				.message("?????? ????????????: " + account.getPassword()).build();

		emailService.sendEmail(emailMessage);

		account.encodePassword(passwordEncoder);
		accountRepository.save(account);

		// ????????? ?????? ?????? ?????? ???????????????
		return ResponseEntity.ok().body(null);
	}
	
	//?????? ??????
	@Transactional
	@DeleteMapping("/account/delete")
	public ResponseEntity<?> deleteUser(){
		
		//???????????? ????????????
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		System.out.println("????????????");
		System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		PrincipalDetail principalDetail = (PrincipalDetail)principal;
		
		
		//?????? ????????? ????????????
		Account account = accountRepository.findByUsername(principalDetail.getUsername());
		if(account == null) {
			return ResponseEntity.badRequest().body(account);
		}
		
		//???????????? ????????????
		if(account != null) {
			System.out.println("?????? : " + account);
			accountRepository.deleteByUsername(account.getUsername());
		}
		
		return ResponseEntity.ok().body(null);
	}

	// ????????? ????????? ????????? ????????????
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) {

		// POST???????????? key=value ???????????? ??????(??????????????????)
		// Retrofit2
		// Okhttp
		// RestTemplate

		RestTemplate restTemplate = new RestTemplate();

		// HttpHeader ???????????? ??????
		HttpHeaders header = new HttpHeaders();
		header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody ???????????? ??????
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "a10968d091035e94e254188bf4ca528a");
		params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
		params.add("code", code);

		// HttpHeader??? HttpBody??? ????????? ??????????????? ??????
		HttpEntity<MultiValueMap<String, String>> kakaoRequest = new HttpEntity<>(params, header);

		System.out.println(kakaoRequest);

		// http ????????????
		ResponseEntity<String> response = restTemplate.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoRequest, String.class);

		// Gson, Json Simple, ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();

		OauthToken oauthToken = null;

		try {
			oauthToken = objectMapper.readValue(response.getBody(), OauthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println("????????? ?????? ?????? : " + oauthToken.getAccess_token());

		// -------------------------------------------------------------------------------------------------------
		// ????????? ???????????? ????????????...
		RestTemplate restTemplate2 = new RestTemplate();

		// HttpHeader ???????????? ??????
		HttpHeaders header2 = new HttpHeaders();
		header2.add("Authorization", "Bearer " + oauthToken.getAccess_token());// ???????????? jwt?????? ????????? ??????????
		header2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader??? HttpBody??? ????????? ??????????????? ??????
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(header2);

		System.out.println(kakaoRequest);

		// http ????????????
		ResponseEntity<String> response2 = restTemplate2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoProfileRequest, String.class);

		// Gson, Json Simple, ObjectMapper
		ObjectMapper objectMapper2 = new ObjectMapper();

		KakaoOauthDto kakaoOauthDto = null;

		try {
			kakaoOauthDto = objectMapper2.readValue(response2.getBody(), KakaoOauthDto.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		System.out.println("????????? ?????? ????????? : " + kakaoOauthDto.getId());
		System.out.println("????????? ?????? ????????? : " + kakaoOauthDto.getKakao_account().getEmail());
		System.out.println("????????? ?????? ????????? : " + kakaoOauthDto.getProperties().getNickname());

		// ????????? ????????? ??????
		Account hasaccount = accountRepository.findByEmail(kakaoOauthDto.getKakao_account().getEmail());
		System.out.println("1????????? ?????? : " + hasaccount);

		Account authResult = null;

		// ????????? ?????????
		if (hasaccount == null) {
			System.out.println("2????????????");
			// ???????????? ??????
//					.username(kakaoOauthDto.getKakao_account().getEmail()+"_"+kakaoOauthDto.getId())
//					.email(kakaoOauthDto.getProperties().getNickname()+"_"+kakaoOauthDto.getId())
			Account account = Account.builder().username(kakaoOauthDto.getKakao_account().getEmail()).password("teemo") // ???????????????
																														// ????????????
																														// ??????
																														// ????????????
					.role("USER").build();

			account.encodePassword(passwordEncoder);

			System.out.println("3???????????? : " + account);
			authResult = accountRepository.save(account);
			System.out.println("4???????????? ?????? : " + authResult);
		}

		System.out.println("5????????? ??????");
		// ?????? ???????????? ?????? ???????????? ??????????
		// ????????? ????????? ?????????

		//---------------------------------------------------------------------------
		// ????????? ???????????? ????????????...
		RestTemplate restTemplate3 = new RestTemplate();
//
//		// HttpHeader ???????????? ??????
//		HttpHeaders header3 = new HttpHeaders();
//		header3.add("Content-type", "application/json;charset=utf-8");
//
//		MultiValueMap<String, String> paramLogin = new LinkedMultiValueMap<String, String>();
//		paramLogin.add("username", authResult.getUsername());
//		paramLogin.add("password", authResult.getPassword());
//		paramLogin.add("email", authResult.getEmail());
//		
//		System.out.println("????????? ?????????1 : " + paramLogin);
//		
//		// HttpHeader??? HttpBody??? ????????? ??????????????? ??????
//		HttpEntity<MultiValueMap<String, String>> KakaoLogin = new HttpEntity<>(paramLogin, header3);
//		
////		ObjectMapper ob = new ObjectMapper();
////		String str=null;
////		try {
////			str = ob.writerWithDefaultPrettyPrinter().writeValueAsString(KakaoLogin);
////		} catch (JsonProcessingException e) {
////			e.printStackTrace();
////		}
////		
////		System.out.println("????????? ?????????2 : " + str);
//
//		// http ????????????
//		ResponseEntity<?> response3 = restTemplate3.exchange(
//				"http://localhost:8080/login", 
//				HttpMethod.POST,
//				KakaoLogin, 
//				String.class);
//		
//		System.out.println("????????????3 : " + response3);

		//---------------------------------------------------------------------------------
		// create request body
		JSONObject request = new JSONObject();
		request.put("username", authResult.getUsername());
		request.put("password", "teemo");

		// set headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

		// send request and parse result
		ResponseEntity<String> loginResponse = restTemplate3
		  .exchange("http://localhost:8080/login", HttpMethod.POST, entity, String.class);
		
		
		

//		Authentication authenticateAction = authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(authResult.getUsername(), authResult.getPassword()));
//		System.out.println("6????????? ??????");
//		SecurityContextHolder.getContext().setAuthentication(authenticateAction);

		System.out.println("????????? ????????? ??????");
//		System.out.println("????????? ????????? ??????");

		return loginResponse.getHeaders().toString();
	}

	// ------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------
	// ???
	@GetMapping("/testuri")
	public ResponseEntity<?> testuri() {
		AccountDto ac = new AccountDto();
		ac.setPassword("manager");
		System.out.println(ac);
		return ResponseEntity.ok().body(ac);
	}
	@GetMapping("/user")
	public ResponseEntity<?> user() {
//		Account ac = new Account();
//		ac.setUsername("wook");
//		ac.setPassword("user");
//		ac.setRole("USER");

		return ResponseEntity.ok().body("sd");
	}

	// ????????? ???????????? ????????? ??? ???????
	@GetMapping("/manager")
	public ResponseEntity<?> manager() {
		AccountDto ac = new AccountDto();
		ac.setPassword("manager");
		System.out.println(ac);
		return ResponseEntity.ok().body(null);
	}
}