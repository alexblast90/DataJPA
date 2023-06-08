package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//SPring DATA JPA관련은 이곳, 나머지는 상속해서 다른곳에서 작성한다.
public interface MemberRepository extends JpaRepository<Member,Long>, MemberRepositoryCustom {

    //쿼리 메소드 기능 3가지

    //메소드 이름으로 쿼리 생성
    //메소드 이름으로 JPA NamedQuery 호출 -> 실무에서 거의 사용 X
    //@Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의 -> @Query


    //메소드 이름으로 쿼리 생성
    List<Member> findByUsernameAndAgeGreaterThan(String username,int age);
    List<Member> findTop3HelloBy();

    //@Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의 -> @Query
    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    //@Query,값
    @Query("select m.username from Member m")
    List<String> findUsernameList();
    
    //@Query,Dto 조
    @Query("select new study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    //반환 타입
    List<Member> findListByUsername(String username); //컬렉션
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //옵셔널

    //Page & Slice 페이지와 슬라이스 (리스트도~)
    //countQuert => 페이지의 쿼리와 토탈카운트를 세는 쿼리를 분리한다. (성능적인 측면에서 좋음)
    @Query(value = "select m from Member m left join m.team t" ,
            countQuery = "select count(m.username) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
//    Slice<Member> findByAge(int age, Pageable pageable);

    //벌크성 수정 쿼리
    //modifying -> JPA의 executeUpdate(); 역할, 빼면 Invalid 오류!
    @Modifying(clearAutomatically = true)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
    
    //페치조인 (연관관계가 있는것을 데이터베이스 조인을 활용하여 한번에 다 끌고 오는 것) 셀렉트 단계에서 필요한 정보를 모두 넣어 가져온다.
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //엔티티그래프 -> 페치조인을 간편하게 할수 있도록 도와준다.
    //기존의 findAll메서드를 이용해서 오버라이드 시키고 @EntityGraph를 붙여 team을 넣어준다.
    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();
    //JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();
    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
            List<Member> findByUsername(String username);


    //JPA Hint&Lock
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value =
            "true"))
    Member findReadOnlyByUsername(String username);




}
