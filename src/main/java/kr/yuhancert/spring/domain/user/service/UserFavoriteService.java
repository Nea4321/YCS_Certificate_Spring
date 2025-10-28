package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                               DeptMapRepository deptMapRepository) {
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
    }


    public List<UserFavoriteDTO> getUserFavorite(HttpServletRequest request) {
        Claims claims = jwtService.parseClaims(request);
        Object idObj = claims.get("id");
        Long userId = (idObj instanceof Number) ? ((Number) idObj).longValue() : 0L;

        List<UserFavorite> favorites = userFavoriteRepository.findAllByUser_Id(userId);
        Map<String, Map<Long, UserFavorite>> userFavoriteMapMap = userService.toUserFavoriteMapMap(favorites);

        userFavoriteMapMap.remove(FavoriteType.cancel.toString());

        Map<Long, UserFavorite> deptFavoriteMap = userFavoriteMapMap.get(FavoriteType.department.toString());
        Map<Long, DeptMap> deptMapMap;
        if (deptFavoriteMap != null) {
            deptMapEntities = deptMapRepository.findAllByIdIn(deptFavoriteMap.keySet().stream().toList());
        }
        if (deptMapEntities != null) {
            deptMapMap = deptMapEntities.stream().collect(Collectors.toMap(DeptMap::getId, dm -> dm));
        } else {
            deptMapMap = Map.of();
        }


        Map<Long, UserFavorite> certFavoriteMap = userFavoriteMapMap.get(FavoriteType.certificate.toString());
        Map<Long, Certificate> certificateMap;
        if (certFavoriteMap != null)
            certificateEntities = certificateRepository.findAllByIdIn(certFavoriteMap.keySet().stream().toList());
        if (certificateEntities != null) {
            certificateMap = certificateEntities.stream().collect(Collectors.toMap(Certificate::getId, c -> c));
        } else {
            certificateMap = Map.of();
        }

        return userFavoriteMapper.toUserFavoriteDTOList(userFavoriteMapMap, deptMapMap, certificateMap);
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

                //학과 거라면 cancel값 추가
                if (idSet.contains(__typeId)) {

                    UserFavorite addFavorite = new UserFavorite();

                    addFavorite.setUser(userEntity);
                    addFavorite.setType(FavoriteType.cancel.toString());
                    addFavorite.setTypeId(__typeId);

                    userFavoriteRepository.save(addFavorite);
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
