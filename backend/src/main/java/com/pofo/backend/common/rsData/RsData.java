package com.pofo.backend.common.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pofo.backend.common.base.Empty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@AllArgsConstructor
@Getter
public class RsData<T> {
	public static final RsData<Empty> OK = new RsData<>("200", "OK", new Empty());

	private final String resultCode;
	private final String message;
	private final T data;

	// ✅ 기존 생성자 유지 (data 없이 생성 가능)
	public RsData(String resultCode, String message) {
		this.resultCode = resultCode;
		this.message = message;
		this.data = null;
	}

	// ✅ data 포함한 생성자 추가
	public RsData(String resultCode, String message, T data) {
		this.resultCode = resultCode;
		this.message = message;
		this.data = data;
	}

	@JsonIgnore
	public int getStatusCode() {
		return Integer.parseInt(resultCode);
	}

	@JsonIgnore
	public boolean isSuccess() {
		return getStatusCode() < 400;
	}

	@JsonIgnore
	public boolean isFail() {
		return !isSuccess();
	}
}