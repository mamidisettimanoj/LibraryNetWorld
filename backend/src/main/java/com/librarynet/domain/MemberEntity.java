package com.librarynet.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_code", nullable = false, unique = true, length = 30)
    private String memberCode;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "max_borrow_limit", nullable = false)
    private int maxBorrowLimit = 3;

    public MemberEntity() { }

    public MemberEntity(String memberCode, String name, String email, String department, int maxBorrowLimit) {
        this.memberCode = memberCode;
        this.name = name;
        this.email = email;
        this.department = department;
        this.maxBorrowLimit = maxBorrowLimit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMemberCode() { return memberCode; }
    public void setMemberCode(String memberCode) { this.memberCode = memberCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public int getMaxBorrowLimit() { return maxBorrowLimit; }
    public void setMaxBorrowLimit(int maxBorrowLimit) { this.maxBorrowLimit = maxBorrowLimit; }
}
