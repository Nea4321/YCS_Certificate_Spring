package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
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

        Claims claims = jwtService.parseClaims(request);

        Long id = claims.get("id", Long.class);

        userDataEntity = userDataRepository.findByUserId(id);
        userFavoriteEntities = userFavoriteRepository.findAllByUser_Id(id);

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

        Long id = claims.get("id", Long.class);

        userFavoriteEntities = userFavoriteRepository.findAllByUser_Id(id);

        return getUserSchedule(toUserFavoriteMapMap(userFavoriteEntities));
    }

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
