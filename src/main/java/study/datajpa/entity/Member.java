package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","username","age"})
public class Member extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="team_id")
    private Team team;

    //디폴트생성자(without파라미터) / Protected 프록싱 기술같은걸 쓸때 구현체들이 객체를 만들어야하기 때문에 꼭 만들어야한다
    //NoargsConstructor를 사용하면 편하게 사용할 수 있다.
//    protected Member() {
//    }

    public Member(String username) {
        this.username = username;
    }

    public Member(String username, int age, Team team) {
        this.username = username;
        this.age = age;
        if(team != null) {
            changeTeam(team);
        }
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }

    //Setter를 쓰지않고 메서드를 만들어서 사용할 수 있다. 이런식으로
//    public void changeUsername(String username){
//        this.username = username;
//    }

    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
