package kr.yuhancert.spring.domain.login.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.certificate.dto.ScheduleDTO;
import kr.yuhancert.spring.domain.certificate.service.CertificateService;
import kr.yuhancert.spring.domain.department.entity.DeptCert;
import kr.yuhancert.spring.domain.department.repository.DeptCertRepository;
import kr.yuhancert.spring.domain.login.dto.UserDataDTO;
import kr.yuhancert.spring.domain.login.entity.User;
import kr.yuhancert.spring.domain.login.entity.UserData;
import kr.yuhancert.spring.domain.login.entity.UserFavorite;
import kr.yuhancert.spring.domain.login.repository.UserDataRepository;
import kr.yuhancert.spring.domain.login.repository.UserFavoriteRepository;
import kr.yuhancert.spring.domain.login.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final JwtService jwtService;
    private final JwtKeyService jwtKeyService;
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
                       DeptCertRepository __deptCertRepository

    ) {
        this.jwtService = __jwtService;
        this.jwtKeyService = __jwtKeyService;
        this.certificateService = __certificateService;
        this.userRepository = __userRepository;
        this.userDataRepository = __userDataRepository;
        this.userFavoriteRepository = __userFavoriteRepository;
        this.deptCertRepository = __deptCertRepository;
    }

    public UserDataDTO getUserData(HttpServletRequest request) {

        // 아직 쓸 데이터가 일정밖에 없어서
        // 추후 보여줄 데이터 매퍼만들어서 수정

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("토큰이 없습니다.");
        }

        String token = authHeader.substring(7);

        Claims claims = jwtService.parseClaims(token, jwtKeyService.getAccessSecretKey());

        Long id = claims.get("id", Long.class);

        userDataEntity = userDataRepository.findByUserUserId(id);
        userFavoriteEntities = userFavoriteRepository.findAllByUserUserId(id);

        return new UserDataDTO(getUserSchedule(userFavoriteEntities));
    }

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

    public List<ScheduleDTO> getUserSchedule(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalStateException("토큰이 없습니다.");
        }

        String token = authHeader.substring(7);

        Claims claims = jwtService.parseClaims(token, jwtKeyService.getAccessSecretKey());

        Long id = claims.get("id", Long.class);

        userFavoriteEntities = userFavoriteRepository.findAllByUserUserId(id);

        return getUserSchedule(userFavoriteEntities);
    }
}
