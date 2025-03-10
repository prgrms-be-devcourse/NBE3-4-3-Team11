package com.pofo.backend.domain.user.join.service;

import com.pofo.backend.domain.user.join.entity.User;
import com.pofo.backend.domain.user.join.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DormantAccountService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 */1 * * * ?")
    public void markDormantAccounts() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(1);


        // 커스텀 쿼리 메서드를 사용하여 dormantFlg가 "N", null, "N/A" 인 사용자들을 조회

        List<User> inactiveUsers = userRepository.findInactiveUsers(threshold);
        System.out.println("휴먼 처리 대상 사용자 수: " + inactiveUsers.size());

        for (User user : inactiveUsers) {
            user.setDormantFlg("Y");
            user.setDormantStartAt(LocalDateTime.now());
            user.setDormantEndAt(null);
            userRepository.save(user);
        }
    }
}


