package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.auth.dto.UserTokenDTO;
import kr.yuhancert.spring.domain.auth.entity.User;
import kr.yuhancert.spring.domain.auth.service.JwtService;
import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.certificate.repository.CertificateRepository;
import kr.yuhancert.spring.domain.department.entity.DeptCert;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import kr.yuhancert.spring.domain.department.repository.DepartmentRepository;
import kr.yuhancert.spring.domain.department.repository.DeptCertRepository;
import kr.yuhancert.spring.domain.department.repository.DeptMapRepository;
import kr.yuhancert.spring.domain.department.service.DepartmentService;
import kr.yuhancert.spring.domain.user.dto.UserFavoriteDTO;
import kr.yuhancert.spring.domain.user.entity.FavoriteType;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import kr.yuhancert.spring.domain.user.mapper.UserFavoriteMapper;
import kr.yuhancert.spring.domain.user.repository.UserFavoriteRepository;
import kr.yuhancert.spring.domain.auth.repository.UserRepository;
import kr.yuhancert.spring.global.cache.service.CacheService;
import kr.yuhancert.spring.global.cache.util.CacheList;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserFavoriteService {

    private final JwtService jwtService;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRepository userRepository;
    private final DeptCertRepository deptCertRepository;
    private final UserFavoriteMapper userFavoriteMapper;
    private final DepartmentRepository departmentRepository;
    private final CertificateRepository certificateRepository;
    private final DeptMapRepository deptMapRepository;
    private final CacheService cacheService;
    private List<UserFavorite> userFavoriteEntities;
    private List<DeptMap> deptMapEntities;
    private List<Certificate> certificateEntities;

    public UserFavoriteService(JwtService __jwtService,
                               UserService __userService,
                               DepartmentService __departmentService,
                               UserFavoriteRepository __userFavoriteRepository,
                               UserRepository __userRepository,
                               DeptCertRepository __deptCertRepository,
                               UserFavoriteMapper __userFavoriteMapper,
                               DepartmentRepository departmentRepository,
                               CertificateRepository certificateRepository,
                               DeptMapRepository deptMapRepository, CacheService cacheService) {
        this.jwtService = __jwtService;
        this.departmentService = __departmentService;
        this.userFavoriteRepository = __userFavoriteRepository;
        this.userRepository = __userRepository;
        this.deptCertRepository = __deptCertRepository;
        this.userFavoriteMapper = __userFavoriteMapper;
        this.userService = __userService;
        this.departmentRepository = departmentRepository;
        this.certificateRepository = certificateRepository;
        this.deptMapRepository = deptMapRepository;
        this.cacheService = cacheService;
    }


    public List<UserFavoriteDTO> getUserFavorite(HttpServletRequest request) {
        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0L;


        List<UserFavorite> favorites = userFavoriteRepository.findAllByUser_Id(userId);
        Map<String, Map<Long, UserFavorite>> userFavoriteMapMap = userService.toUserFavoriteMapMap(favorites);

        Map<Long, UserFavorite> cancelMap = userFavoriteMapMap.getOrDefault(FavoriteType.cancel.toString(), Map.of());
        Set<Long> cancelCertIds = cancelMap.keySet();

        // 학과 즐겨찾기 Map
        Map<Long, UserFavorite> deptFavoriteMap = userFavoriteMapMap.getOrDefault(FavoriteType.department.toString(), Map.of());
        List<DeptMap> deptMapEntities = deptMapRepository.findAllByIdIn(new ArrayList<>(deptFavoriteMap.keySet()));
        Map<Long, DeptMap> deptMapMap = deptMapEntities.stream()
                .collect(Collectors.toMap(DeptMap::getId, dm -> dm));

        // 직접 즐겨찾기 자격증 Map
        Map<Long, UserFavorite> certFavoriteMap = userFavoriteMapMap.getOrDefault(FavoriteType.certificate.toString(), Map.of());
        List<Certificate> certificateEntities = certificateRepository.findAllByIdIn(new ArrayList<>(certFavoriteMap.keySet()));
        Map<Long, Certificate> certificateMap = certificateEntities.stream()
                .collect(Collectors.toMap(Certificate::getId, c -> c));

        // 학과 연관 자격증 추가
        if (!deptFavoriteMap.isEmpty()) {
            List<DeptCert> deptCertList = deptCertRepository.findAllByDeptMapIdIn(new ArrayList<>(deptFavoriteMap.keySet()));
            Set<Long> linkedCertIds = deptCertList.stream()
                    .map(dc -> dc.getCertificate().getId())
                    .collect(Collectors.toSet());

            // cancel 제외 + 기존 즐겨찾기 제외
            Set<Long> newCertIds = linkedCertIds.stream()
                    .filter(id -> !certificateMap.containsKey(id))
                    .filter(id -> !cancelCertIds.contains(id))
                    .collect(Collectors.toSet());

            if (!newCertIds.isEmpty()) {
                List<Certificate> linkedCertificates = certificateRepository.findAllByIdIn(new ArrayList<>(newCertIds));
                Map<Long, Certificate> newCertMap = linkedCertificates.stream()
                        .collect(Collectors.toMap(Certificate::getId, c -> c));
                certificateMap.putAll(newCertMap);
            }
        }

        return userFavoriteMapper.toUserFavoriteDTOList(userFavoriteMapMap, deptMapMap, certificateMap, deptCertRepository);
    }



    /** 즐찾 되어 있는지 확인함.
     * [특이한 점]
     * 1. 즐찾에 학과가 있는 경우
     * (1). 학과랑 연관된 자격증이 있을때 해당 자격증이 db에 값이 없어도 이 함수에선 포함 되어있다고 판단함.
     *  */
    public Boolean isFavorite(HttpServletRequest request, FavoriteType __type, Long __typeId) {

        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : null;



        List<UserFavorite> userFavoriteList = userFavoriteRepository.findAllByUser_Id(userId);

        Map<String, Map<Long, UserFavorite>> userFavoriteMapMap = userService.toUserFavoriteMapMap(userFavoriteList);

        if (__type == FavoriteType.department) {

            Map<Long, UserFavorite> deptFavoriteMap = userFavoriteMapMap.getOrDefault(FavoriteType.department.toString(), Map.of());

            return deptFavoriteMap.containsKey(__typeId);

        }

        List<Long> idList = userService.getUserCertIdList(userFavoriteMapMap);

        return idList.contains(__typeId);

    }

    /** 즐찾 db에 추가
     * [로직]
     * 1. 로그인 되어있는지 비교하고 db에 있는지 비교해서 저장
     * 2. 여기서 type이 cancel 인 경우 다르게 작동함. -> 아래 delete 에서 설명함.
     * */
    public void addUserFavorite(HttpServletRequest request, FavoriteType __type, Long __typeId) {

        Claims claims = jwtService.parseClaims(request);

        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;



        //(광클 했을때 중복 값 생성 제거)
        userFavoriteRepository.deleteAll(userFavoriteRepository.findAllByUser_IdAndTypeAndTypeId(userId, __type.toString(), __typeId));


        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        //중복값 존재시 return
        if (userFavoriteRepository.existsByUser_IdAndTypeAndTypeId(userId, __type.toString(), __typeId))
            return;

        //자격증일 경우 cancel 타입이 있는지 확인
        if (__type == FavoriteType.certificate) {
            //있으면 제거
            if (userFavoriteRepository.existsByUser_IdAndTypeAndTypeId(userId, FavoriteType.cancel.toString(), __typeId)) {

                userFavoriteRepository.delete(
                        userFavoriteRepository.findByUser_IdAndTypeAndTypeId(userId, FavoriteType.cancel.toString(), __typeId).orElseThrow()
                );

                return;
            }
        }

        UserFavorite addFavorite = new UserFavorite();
        addFavorite.setUser(userEntity);
        addFavorite.setType(__type.toString());
        addFavorite.setTypeId(__typeId);

        userFavoriteRepository.save(addFavorite);
    }
    /** 즐찾 삭제하는 로직
     * [로직이 다르게 작동하는 조건]
     * 1. 학과가 즐찾 db에 저장되어 있고 해당 학과가 연관된 자격증이 있을 때
     * 2.
     * */
    @Transactional
    public void deleteUserFavorite(HttpServletRequest request, FavoriteType __type,Long __typeId) {

        Claims claims = jwtService.parseClaims(request);

        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0;



        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Map<String, Map<Long, UserFavorite>> userFavoriteMapMap = userService.toUserFavoriteMapMap(userFavoriteRepository.findAllByUser_Id(userId));

        Map<Long, UserFavorite> deptFavorite = userFavoriteMapMap.get(FavoriteType.department.toString());

        Map<Long, UserFavorite> certFavoriteMap = userFavoriteMapMap.get(FavoriteType.certificate.toString());
        //자격증일 경우
        if (__type == FavoriteType.certificate) {
            //타입이 자격증인데 학과 저장한 값이 없는 경우 null 체크 오류 방지
            if (deptFavorite != null) {
                List<DeptCert> deptCertList = deptCertRepository.findAllByDeptMapIdIn(deptFavorite.keySet().stream().toList());

                Set<Long> idSet = deptCertList.stream()
                        .map(dc -> dc.getCertificate().getId())
                        .collect(Collectors.toSet());

                if (certFavoriteMap != null && certFavoriteMap.containsKey(__typeId)) {
                    userFavoriteRepository.delete(certFavoriteMap.get(__typeId));
                }

                //학과 거라면 cancel값 추가 + (광클 했을때 중복 값 생성 제거)
                if (idSet.contains(__typeId)) {

                    // 이미 있는 cancel 삭제
                    userFavoriteRepository.deleteAllByUser_IdAndTypeAndTypeId(
                            userId, FavoriteType.cancel.toString(), __typeId
                    );
                    // 새로 추가
                    UserFavorite cancel = new UserFavorite();
                    cancel.setUser(userRepository.getReferenceById(userId));
                    cancel.setType(FavoriteType.cancel.toString());
                    cancel.setTypeId(__typeId);

                    /// db에 unique 제약조건 걸리면 return으로 패스.
                    try {
                        userFavoriteRepository.save(cancel);
                    } catch (DataIntegrityViolationException e) {
                        return;
                    }
                    return;
                }
            }

            //자격증 있으면 삭제
            if (certFavoriteMap != null && certFavoriteMap.containsKey(__typeId)) {
                userFavoriteRepository.delete(certFavoriteMap.get(__typeId));
            }

            return;
        }

        if (deptFavorite != null && deptFavorite.containsKey(__typeId)) {
            userFavoriteRepository.delete(deptFavorite.get(__typeId));
        }

        //만약 학과 즐겨찾기가 없으면 cancel 다 삭제
        if (!userFavoriteRepository.existsByUser_IdAndType(userId, FavoriteType.department.toString())) {
            userFavoriteRepository.deleteAllByUser_IdAndTypeIdAndType(userId, FavoriteType.cancel.toString());}
    }

}
