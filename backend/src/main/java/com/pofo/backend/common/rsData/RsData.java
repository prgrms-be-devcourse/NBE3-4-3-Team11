package com.pofo.backend.common.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pofo.backend.common.base.Empty;
import lombok.Getter;
import org.springframework.lang.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class RsData<T> {
	public static final RsData<Empty> OK = new RsData<>("200", "OK", new Empty());

	@NonNull
	private final String resultCode;

	@NonNull
	private final String message;

	private final T data;

	// ✅ Kotlin에서 제네릭 추론 문제 해결
	public RsData(String resultCode, String message, T data) {
		this.resultCode = resultCode;
		this.message = message;
		this.data = data;
	}

	// ✅ 기본 생성자 추가 (data 없이도 생성 가능)
	public RsData(String resultCode, String message) {
		this.resultCode = resultCode;
		this.message = message;
		this.data = null;  // 🚀 `null` 사용하여 안전한 기본값 설정
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
