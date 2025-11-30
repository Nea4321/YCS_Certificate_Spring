package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.auth.dto.UserTokenDTO;
import kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO;
import kr.yuhancert.spring.domain.certificate.service.CertificateService;
import kr.yuhancert.spring.domain.department.entity.DeptCert;
import kr.yuhancert.spring.domain.department.repository.DeptCertRepository;
import kr.yuhancert.spring.domain.auth.service.JwtKeyService;
import kr.yuhancert.spring.domain.auth.service.JwtService;
import kr.yuhancert.spring.domain.user.dto.UserDataDTO;
import kr.yuhancert.spring.domain.auth.entity.User;
import kr.yuhancert.spring.domain.user.entity.FavoriteType;
import kr.yuhancert.spring.domain.user.entity.UserData;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import kr.yuhancert.spring.domain.user.repository.UserDataRepository;
import kr.yuhancert.spring.domain.user.repository.UserFavoriteRepository;
import kr.yuhancert.spring.domain.auth.repository.UserRepository;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final JwtService jwtService;
    private final JwtKeyService jwtKeyService;
    private final CacheService cacheService;
    private final CertificateService certificateService;
    private final UserRepository userRepository;
    private final UserDataRepository userDataRepository;
    private final UserFavoriteRepository userFavoriteRepository;
    private final DeptCertRepository deptCertRepository;
    private User userEntity;
    private UserData userDataEntity;
    private List<UserFavorite> userFavoriteEntities;

    public UserService(JwtService __jwtService,
                       JwtKeyService __jwtKeyService,
                       CertificateService __certificateService,
                       UserRepository __userRepository,
                       UserDataRepository __userDataRepository,
                       UserFavoriteRepository __userFavoriteRepository,
                       DeptCertRepository __deptCertRepository,
                       CacheService __cacheService

    ) {
        this.jwtService = __jwtService;
        this.jwtKeyService = __jwtKeyService;
        this.certificateService = __certificateService;
        this.userRepository = __userRepository;
        this.userDataRepository = __userDataRepository;
        this.userFavoriteRepository = __userFavoriteRepository;
        this.deptCertRepository = __deptCertRepository;
        this.cacheService = __cacheService;
    }

    public UserDataDTO getUserData(HttpServletRequest request) {

        // 아직 쓸 데이터가 일정밖에 없어서
        // 추후 보여줄 데이터 매퍼만들어서 수정

        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

        userDataEntity = userDataRepository.findByUserId(userId);
        userFavoriteEntities = userFavoriteRepository.findAllByUser_Id(userId);

        return new UserDataDTO(getUserSchedule(request));
    }

    public List<Long> getUserCertIdList(Map<String, Map<Long, UserFavorite>> __userFavorites) {

        Map<Long, UserFavorite> deptFavoriteMap =
                __userFavorites.getOrDefault(FavoriteType.department.toString(), Map.of());

        List<DeptCert> deptCertList = deptCertRepository.findAllByDeptMapIdIn(deptFavoriteMap.keySet().stream().toList());

        Set<Long> idSet = deptCertList.stream()
                .map(dc -> dc.getCertificate().getId())
                .collect(Collectors.toSet());


        Map<Long, UserFavorite> certFavoriteMap =
                __userFavorites.getOrDefault(FavoriteType.certificate.toString(), Map.of());

        certFavoriteMap.values()
                .forEach(fav -> idSet.add(fav.getTypeId()));


        Map<Long, UserFavorite> cancelFavoriteMap =
                __userFavorites.getOrDefault(FavoriteType.cancel.toString(), Map.of());

        cancelFavoriteMap.values()
                .forEach(fav -> idSet.remove(fav.getTypeId()));

        return new ArrayList<>(idSet);
    }

    public List<ScheduleDTO> getUserSchedule(Map<String,Map<Long, UserFavorite>> __userFavorites) {

        if (__userFavorites == null) {
            return List.of(); // 빈 리스트 반환
        }

        List<Long> idList = getUserCertIdList(__userFavorites);


       return certificateService.getSchedule(idList);
    }

    public List<ScheduleDTO> getUserSchedule(HttpServletRequest request) {

        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

        userFavoriteEntities = userFavoriteRepository.findAllByUser_Id(userId);

        return getUserSchedule(toUserFavoriteMapMap(userFavoriteEntities));
    }

    // ex) department - user_id(1) - type_id(2,3,4)...
    // user_favorite db에 들어가 있는 데이터를  [(학과,자격증,캔슬) - [(유저 아이디),(아이디)]]  이런식으로 저장하는 맵핑 함수.
    public Map<String, Map<Long, UserFavorite>> toUserFavoriteMapMap(List<UserFavorite> __userFavorites) {
        return __userFavorites.stream()
                .collect(Collectors.groupingBy(
                        UserFavorite::getType, // type 기준으로 묶기 ("certificate", "cancel", "department")
                        Collectors.toMap(
                                UserFavorite::getTypeId, // 내부 key: typeId (Long)
                                fav -> fav
                        )
                ));
    }

    // 로그인 되어있는 액세스 토큰이랑 redis 에 저장된 토큰이랑 비교하는 로직
    // 이걸로 동일 로그인 인지 체크.
    public ResponseEntity<?> checkToken(HttpServletRequest request) {
        ///  요청 들어온 액세스토큰, redis에 저장된 액세스토큰 두개를 비교 해서 불일치 하면 오류 생성
        log.info("중복 로그인 체크 로직 실행 됨..!");
        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;

        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("access_token")) {
                    accessToken = c.getValue();
                    break;
                }
            }
        }


        UserTokenDTO redisToken = cacheService.get(CacheList.USER_TOKEN_CACHE.getName(),userId ,UserTokenDTO.class);
        log.info("redis에 저장된 토큰: {}",redisToken.getToken());
        log.info("클라이언트에 저장된 토큰: {}",accessToken);

        if (redisToken == null) {
            // 캐시가 사라졌거나 Redis 장애 상황
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("세션 정보가 존재하지 않습니다. 다시 로그인해주세요.");
        }

        if (!redisToken.getToken().equals(accessToken)) {
            // 실제 중복 로그인 감지
            log.info("중복 로그인 감지: userId={}, token={}", userId, accessToken);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("다른 기기에서 로그인하여 해당 세션은 만료되었습니다.");
        }
        return ResponseEntity.ok("");
    }

    /**
     * 학과인지 자격증인지 따라 스케줄 분리
     * 학과면 학과 연동 자격증 가져와 스케줗 요청
     * 자격증이면 바로 스케줄 요청
     * 중복 되는 스케줄 있으면 넘김
     * 로직변경 ㅋㅋㅋㅋ
     * 아까우니 주석처리
     */
    /*
    public List<ScheduleDTO> getUserSchedule(List<UserFavorite> __userFavorites) {

        Map<Long, ScheduleDTO> scheduleMap = new HashMap<>();

        for (UserFavorite uf : __userFavorites) {

            if (Objects.equals(uf.getType(), "dept")){

                List<DeptCert> deptCertList = deptCertRepository.findAllByDeptMapId(uf.getTypeId());

                if (deptCertList == null || deptCertList.isEmpty()) continue;

                List<Long> ids = deptCertList.stream()
                        .map(DeptCert::getId)
                        .toList();

                List<ScheduleDTO> scheduleDTOList = certificateService.getSchedule(ids);

                Map<Long, ScheduleDTO> sm = scheduleDTOList.stream().collect(Collectors.toMap(ScheduleDTO::getCertificate_id, s -> s));

                scheduleMap.forEach(sm::putIfAbsent);

                continue;
            }

            List<ScheduleDTO> scheduleDTOList = certificateService.getSchedule(Collections.singletonList(uf.getTypeId()));

            Map<Long, ScheduleDTO> sm = scheduleDTOList.stream().collect(Collectors.toMap(ScheduleDTO::getCertificate_id, s -> s));

            scheduleMap.forEach(sm::putIfAbsent);
        }

        return new ArrayList<>(scheduleMap.values());
    }
    */

}
