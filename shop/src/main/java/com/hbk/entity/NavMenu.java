package com.hbk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "nav_menu",
        indexes = {
@Index(name = "idx_nav_menu_parent", columnList = "parent_id"),
@Index(name = "idx_nav_menu_depth", columnList = "depth"),
@Index(name = "idx_nav_menu_sort", columnList = "sort_order")
        }
)
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NavMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String path;

    //1,2,3
    @Column(nullable = false)
    private Integer depth;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "visible_yn", nullable = false, length = 1)
    private  String visibleYn;

    @ManyToOne(fetch = FetchType.LAZY) //부모 (자기참조 관계)
    //LAZY 로딩 → 필요할 때만 조회
    //여러 자식은 하나의 부모를 가진다 (ManyToOne)
    @JoinColumn(name = "parent_id")//parent_id 컬럼이 FK
    @JsonIgnore //무한참조 방지
    private  NavMenu parent;

    //👉 하나의 부모는 여러 자식을 가짐, parent 필드 기준으로 매핑, 부모 저장/삭제 시 자식도 함께 처리
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC, id ASC")//👉 자식 메뉴 정렬 조건
    private List<NavMenu> children = new ArrayList<>();//기본값으로 빈 리스트 생성

    public void addChild(NavMenu child){//👉 부모-자식 관계를 양방향으로 자동 설정
        child.setParent(this);
        this.children.add(child);
        //child.parent = parent parent.children 에 child 추가
    }




}
