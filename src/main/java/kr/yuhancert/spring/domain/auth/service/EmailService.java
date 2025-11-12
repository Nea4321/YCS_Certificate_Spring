package kr.yuhancert.spring.domain.auth.service;

import kr.yuhancert.spring.domain.auth.dto.EmailCheckDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    // 이메일 전송을 위한 스프링 내장 객체
    private final JavaMailSender mailSender;

    // 이메일별 인증 코드 및 만료시간 저장용 (임시 Map, 서버 재시작 시 사라짐)
    private final Map<String, EmailCheckDTO> verificationStore = new ConcurrentHashMap<>();


    /**
     * 이메일로 인증 코드 발송
     */
    public void sendVerificationCode(String email) {
        // 6자리 랜덤 숫자 생성 (100000~999999)
        String code = String.valueOf((int) (Math.random() * 900000 + 100000));

        // 만료시간: 현재시간 + 3분
        long expiryTime = System.currentTimeMillis() + (3 * 60 * 1000);

        // 이메일-코드 쌍을 Map에 저장
        verificationStore.put(email, new EmailCheckDTO(code, expiryTime));

        try {
            // 메일 메시지 객체 생성
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email); // 수신자
            message.setSubject("자격지신 이메일 인증 코드"); // 제목
            message.setText("인증 코드: " + code + "\n3분 안에 입력하세요."); // 본문 내용
            mailSender.send(message); // 실제 메일 전송
            log.info(" email 전송 로직 실행 ");
        } catch (Exception e) {
            // 전송 실패 시 예외 던지기
            throw new RuntimeException("메일 전송 실패: " + e.getMessage());
        }
    }

    /**
     * 이메일과 코드가 일치하는지 검증
     */
    public boolean verifyCode(String email, String code) {
        // 해당 이메일로 저장된 인증정보 가져오기
        EmailCheckDTO info = verificationStore.get(email);

        // 저장된 코드가 없으면 false 반환
        if (info == null) return false;

        // 만료시간이 지났으면 삭제 후 false 반환
        if (System.currentTimeMillis() > info.getExpiryTime()) {
            verificationStore.remove(email);
            return false;
        }

        // 코드가 일치하는지 검사
        boolean valid = info.getCode().equals(code);

        // 일치한다면 사용 후 Map에서 제거
        if (valid) {
            verificationStore.remove(email);
            log.info(" email 인증 확인 로직 실행 ");
        }

        // 결과 반환
        return valid;
    }


}
