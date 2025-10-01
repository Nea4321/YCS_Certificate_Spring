package kr.yuhancert.spring.domain.login.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class UserId implements Serializable {
    // getters & setters
    private String userEmail;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    public UserId() {}

    public UserId(String userEmail, SocialType socialType) {
        this.userEmail = userEmail;
        this.socialType = socialType;
    }

    // equals와 hashCode 필수
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId)) return false;
        UserId userId = (UserId) o;
        return Objects.equals(userEmail, userId.userEmail) &&
                Objects.equals(socialType, userId.socialType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail, socialType);
    }

}