package com.ground.auth.dto.common;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 공통으로 사용할 수 있는 response dto 클래스입니다.
 *
 * @param <T> 응답 데이터 타입
 * */

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResponseDto<T> {

    private boolean success;
    // 직렬, 역질렬화에 사용되는 논리적 프로퍼티 값을 무시할 때 사용한다.
    @JsonIgnore
    private HttpStatus status;
    private T data;
    private List<String> errorMessages;


    /**
     * 공통 응답에 사용하는 객체 빌더메소드
     * */
    public static <T> ResponseDtoBuilder<T> builder() {
        return new ResponseDtoBuilder<T>();
    }

    @JsonGetter
    public int getStatus() {
        return this.status.value();
    }

    @NoArgsConstructor
    public static class ResponseDtoBuilder<T> {
        private boolean success;
        private HttpStatus status;
        private T data;
        private List<String> errorMessages;

        public ResponseDtoBuilder<T> success(boolean success){
            this.success = success;
            return this;
        }

        public ResponseDtoBuilder<T> status(HttpStatus status){
            this.status = status;

            return this;
        }

        public ResponseDtoBuilder<T> data(T data){
            this.data = data;

            return this;
        }

        public ResponseDtoBuilder<T> errorMessages(List<String> errorMessages){
            this.errorMessages = errorMessages;

            return this;
        }

        public ResponseDto<T> build(){
            return new ResponseDto<T>(success,status,data,errorMessages);
        }


        @Override
        public String toString() {
            return "ResponseDtoBuilder{" +
                    "success=" + this.success +
                    ", status=" + this.status +
                    ", data=" + this.data +
                    ", errorMessages=" + this.errorMessages +
                    '}';
        }
    }
}
