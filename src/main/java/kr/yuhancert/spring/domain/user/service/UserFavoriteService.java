package kr.yuhancert.spring.domain.user.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import kr.yuhancert.spring.domain.login.service.JwtService;
import kr.yuhancert.spring.domain.user.dto.UserFavoriteDTO;
import kr.yuhancert.spring.domain.user.entity.User;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import kr.yuhancert.spring.domain.user.mapper.UserFavoriteMapper;
import kr.yuhancert.spring.domain.user.repository.UserFavoriteRepository;
import kr.yuhancert.spring.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFavoriteService {

    private final JwtService jwtService;
    private final UserFavoriteRepository userFavoriteRepository;
    private final UserRepository userRepository;
    private final UserFavoriteMapper userFavoriteMapper;
    private List<UserFavorite> userFavoriteEntities;

    public UserFavoriteService(JwtService __jwtService,
                               UserFavoriteRepository __userFavoriteRepository,
                               UserRepository __userRepository,
                               UserFavoriteMapper userFavoriteMapper) {
        this.jwtService = __jwtService;
        this.userFavoriteRepository = __userFavoriteRepository;
        this.userRepository = __userRepository;
        this.userFavoriteMapper = userFavoriteMapper;
    }

    public List<UserFavoriteDTO> getUserFavorite(HttpServletRequest request) {

        Claims claims = jwtService.parseClaims(request);

        userFavoriteEntities = userFavoriteRepository.findAllById(claims.get("id", Long.class));

        return userFavoriteMapper.toUserFavoriteDTOList(userFavoriteEntities);
    }

    public void addUserFavorite(HttpServletRequest request, String __type, Long __typeId) {

        Claims claims = jwtService.parseClaims(request);

        UserFavorite addFavorite = new UserFavorite(claims.get("id", Long.class),
                userRepository.findById(claims.get("id", Long.class))
                        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다.")),
                __type,
                __typeId
                );

        userFavoriteRepository.save(addFavorite);
    }

    public void deleteUserFavorite(HttpServletRequest request, String __type,Long __typeId) {

        Claims claims = jwtService.parseClaims(request);

        Long userId = claims.get("id", Long.class);


        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        UserFavorite favorite = userFavoriteRepository
                .findByUserIdAndTypeAndTypeId(userId, __type, __typeId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기 항목이 존재하지 않습니다."));

        userFavoriteRepository.delete(favorite);

    }

}
