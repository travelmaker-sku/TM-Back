package com.notice.board.account.dto;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

//@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class KakaoOauthDto {

	public Long id;
	public String connected_at;
	public Properties properties;
	public Kakao_account kakao_account;
	
	@Data
	public class Properties {

		public String nickname;

	}
	
	@Data
	public class Kakao_account {

		public Boolean profile_nickname_needs_agreement;
		public Profile profile;
		public Boolean has_email;
		public Boolean email_needs_agreement;
		public Boolean is_email_valid;
		public Boolean is_email_verified;
		public String email;

		@Data
		public class Profile {

			public String nickname;

		}

	}

}





