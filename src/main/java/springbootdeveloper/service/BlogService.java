package springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springbootdeveloper.domain.Article;
import springbootdeveloper.dto.AddArticleRequest;
import springbootdeveloper.dto.UpdateArticleRequest;
import springbootdeveloper.repository.BlogRepository;

import java.util.List;

@RequiredArgsConstructor    // final이 붙거나 @NotNull이 붙은 필드의 생성자 추가
@Service // 빈으로 등록
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    // 블로그 모든 글을 가져오는
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 블로그 글 하나 조회
    public Article findById(long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    // 블로그 삭제
    public void delete(long id) {
        blogRepository.deleteById(id);
    }

    // 블로그 수정
    @Transactional // 트랜젝션 메서드
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
