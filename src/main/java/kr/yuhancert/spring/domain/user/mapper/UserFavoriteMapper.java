package kr.yuhancert.spring.domain.user.mapper;

import kr.yuhancert.spring.domain.certificate.entity.Certificate;
import kr.yuhancert.spring.domain.department.entity.DeptMap;
import kr.yuhancert.spring.domain.department.repository.DeptCertRepository;
import kr.yuhancert.spring.domain.user.dto.UserFavoriteDTO;
import kr.yuhancert.spring.domain.user.entity.FavoriteType;
import kr.yuhancert.spring.domain.user.entity.UserFavorite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@Qualifier("userFavoriteMapper")
public interface UserFavoriteMapper {
    @Mapping(source = "type", target = "type")
    @Mapping(source = "typeId", target = "type_id")
    UserFavoriteDTO toUserFavoriteDTO(UserFavorite __userFavorite);


    default List<UserFavoriteDTO> toUserFavoriteDTOList(
            Map<String, Map<Long, UserFavorite>> __user,
            Map<Long, DeptMap> __dept,
            Map<Long, Certificate> __cert,
            DeptCertRepository deptCertRepository
    ) {
        List<UserFavoriteDTO> userFavoriteDTOList = new ArrayList<>();

        if (__user == null || __dept == null || __cert == null) return userFavoriteDTOList;

        List<UserFavorite> deptFavorite = __user.getOrDefault(FavoriteType.department.toString(), Map.of()).values().stream().toList();
        List<UserFavorite> certFavorite = __user.getOrDefault(FavoriteType.certificate.toString(), Map.of()).values().stream().toList();
        List<UserFavorite> cancelFavorite = __user.getOrDefault(FavoriteType.cancel.toString(), Map.of()).values().stream().toList();

        // cancel 타입 ID set
        Set<Long> cancelCertIds = cancelFavorite.stream()
                .map(UserFavorite::getTypeId)
                .collect(Collectors.toSet());

        for (UserFavorite df : deptFavorite) {

            DeptMap dm = __dept.get(df.getTypeId());
            String name;

            if (dm != null && dm.getMajor() != null) {
                name = dm.getMajor().getMajorName();
            } else if (dm != null && dm.getDepartment() != null) {
                name = dm.getDepartment().getDepartmentName();
            } else if (dm != null && dm.getFaculty() != null){
                name = dm.getFaculty().getFacultyName();
            } else
                continue;

            userFavoriteDTOList.add(new UserFavoriteDTO(
                    FavoriteType.department.toString(),
                    df.getTypeId(),
                    name
            ));

            //학과랑 연관된 자격증 추가
            List<Long> linkedCertIds = deptCertRepository.findAllByDeptMapId(df.getTypeId())
                    .stream()
                    .map(dc -> dc.getCertificate().getId())
                    .toList();

            for (Long certId : linkedCertIds) {
                if (__cert.containsKey(certId) && !cancelCertIds.contains(certId)) {
                    userFavoriteDTOList.add(new UserFavoriteDTO(
                            FavoriteType.certificate.toString(),
                            certId,
                            __cert.get(certId).getCertificateName()
                    ));
                }
            }
        }

        for (UserFavorite cf : certFavorite) {
            if (!cancelCertIds.contains(cf.getTypeId()) && __cert.containsKey(cf.getTypeId())) {
                userFavoriteDTOList.add(new UserFavoriteDTO(
                        FavoriteType.certificate.toString(),
                        cf.getTypeId(),
                        __cert.get(cf.getTypeId()).getCertificateName()
                ));
            }
        }
        return userFavoriteDTOList;
    }
}
