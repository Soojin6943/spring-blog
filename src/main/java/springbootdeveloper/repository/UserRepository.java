package springbootdeveloper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootdeveloper.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // email로 사용자 정보 가져옴
}
