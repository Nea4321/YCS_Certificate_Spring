package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.auth.entity.User;
import kr.yuhancert.spring.domain.auth.service.JwtService;
import kr.yuhancert.spring.domain.department.entity.DeptCert;
import kr.yuhancert.spring.domain.department.repository.DeptCertRepository;
import kr.yuhancert.spring.domain.user.dto.UserFavoriteDTO;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import kr.yuhancert.spring.domain.user.mapper.UserFavoriteMapper;
import kr.yuhancert.spring.domain.user.repository.UserFavoriteRepository;
import kr.yuhancert.spring.domain.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFavoriteService {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRepository userRepository;
    private final DeptCertRepository deptCertRepository;
    private final UserFavoriteMapper userFavoriteMapper;
    private List<UserFavorite> userFavoriteEntities;

    private final String DEPT = "department";
    private final String CERT = "certificate";
    private final String CANCEL = "cancel";

    public UserFavoriteService(JwtService __jwtService,
                               UserService __userService,
                               UserFavoriteRepository __userFavoriteRepository,
                               UserRepository __userRepository,
                               DeptCertRepository __deptCertRepository,
                               UserFavoriteMapper __userFavoriteMapper) {
        this.jwtService = __jwtService;
        this.userFavoriteRepository = __userFavoriteRepository;
        this.userRepository = __userRepository;
        this.deptCertRepository = __deptCertRepository;
        this.userFavoriteMapper = __userFavoriteMapper;
        this.userService = __userService;
    }

    public List<UserFavoriteDTO> getUserFavorite(HttpServletRequest request) {

        Claims claims = jwtService.parseClaims(request);

        userFavoriteEntities = userFavoriteRepository.findAllByUser_IdAndTypeNot(claims.get("id", Long.class), CANCEL);

        return userFavoriteMapper.toUserFavoriteDTOList(userFavoriteEntities);
    }

    public Boolean isFavorite(HttpServletRequest request, String __type, Long __typeId) {

        Claims claims = jwtService.parseClaims(request);

        List<UserFavorite> userFavoriteList = userFavoriteRepository.findAllByUser_Id(claims.get("id", Long.class));

        Map<String, Map<Long, UserFavorite>> userFavoriteMapMap = userService.toUserFavoriteMapMap(userFavoriteList);

        if (__type == DEPT) {

            Map<Long, UserFavorite> deptFavoriteMap = userFavoriteMapMap.getOrDefault(DEPT, Map.of());

            return deptFavoriteMap.containsKey(__typeId);

        }

        List<Long> idList = userService.getUserCertIdList(userFavoriteMapMap);

        return idList.contains(__typeId);

    }

    public void addUserFavorite(HttpServletRequest request, String __type, Long __typeId) {

        Claims claims = jwtService.parseClaims(request);

        Long userId = claims.get("id", Long.class);

        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        //자격증일 경우 cancel 타입이 있는지 확인
        if (__type == CERT) {
            //있으면 제거
            if (userFavoriteRepository.existsByUser_IdAndTypeAndTypeId(userId, CANCEL, __typeId)) {

                userFavoriteRepository.delete(
                        userFavoriteRepository.findByUser_IdAndTypeAndTypeId(userId, CANCEL, __typeId).orElseThrow()
                );

                return;
            }
        }

        UserFavorite addFavorite = new UserFavorite();
        addFavorite.setUser(userRepository.findById(userId).orElseThrow());
        addFavorite.setType(__type);
        addFavorite.setTypeId(__typeId);

        userFavoriteRepository.save(addFavorite);
    }

    public void deleteUserFavorite(HttpServletRequest request, String __type,Long __typeId) {

        Claims claims = jwtService.parseClaims(request);

        Long userId = claims.get("id", Long.class);

        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Map<String, Map<Long, UserFavorite>> userFavoriteMapMap = userService.toUserFavoriteMapMap(userFavoriteRepository.findAllByUser_Id(userId));

        //자격증일 경우
        if (__type == CERT) {

            Map<Long, UserFavorite> deptFavorite = userFavoriteMapMap.get(DEPT);

            List<DeptCert> deptCertList = deptCertRepository.findAllByDeptMapIdIn(deptFavorite.values().stream()
                    .map(UserFavorite::getTypeId)
                    .collect(Collectors.toSet())
                    .stream().toList()
            );

            Set<Long> idSet = deptCertList.stream()
                    .map(dc -> dc.getCertificate().getId())
                    .collect(Collectors.toSet());

            //학과 거라면 cancel값 추가
            if (idSet.contains(__typeId)) {

                UserFavorite addFavorite = new UserFavorite();

                addFavorite.setUser(userEntity);
                addFavorite.setType(CANCEL);
                addFavorite.setTypeId(__typeId);

                userFavoriteRepository.save(addFavorite);
            }

            //자격증 있으면 삭제
            Map<Long, UserFavorite> certFavoriteMap = userFavoriteMapMap.get(CERT);

            if (certFavoriteMap.containsKey(__typeId))
                userFavoriteRepository.delete(certFavoriteMap.get(__typeId));

            return;
        }

        Map<Long, UserFavorite> deptFavorite = userFavoriteMapMap.get(DEPT);

        if (!deptFavorite.containsKey(__typeId)) return;

        userFavoriteRepository.delete(deptFavorite.get(__typeId));

        //만약 학과 즐겨찾기가 없으면 cancel 다 삭제
        if (!userFavoriteRepository.existsByUser_IdAndType(userId, DEPT))
            userFavoriteRepository.deleteAllByUser_IdAndType(userId,CANCEL);

    }

}
