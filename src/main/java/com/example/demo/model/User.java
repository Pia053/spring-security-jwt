package com.example.demo.model;

import com.example.demo.infrastructure.util.Gender;
import com.example.demo.infrastructure.util.UserStatus;
import com.example.demo.infrastructure.util.UserType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractEntity<Long> implements UserDetails, Serializable {

    @Column(name="date_of_birth")
    @Temporal(TemporalType.DATE) // lấy ngày không lấy h
    private Date dateOfBirth;

    @Column(name="username")
    private String username;

    @Column(name="password")
    private String password;

    @Column(name="phone")
    private String phone;

    @Column(name="email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "ENUM('OWNER', 'ADMIN', 'USER')")
    private UserType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "ENUM('MALE', 'FEMALE', 'OTHER')")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('ACTIVE', 'INACTIVE', 'NONE')")
    private UserStatus status;

    @OneToMany(mappedBy = "user")
    private Set<GroupHasUser> groupHasUsers = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserHasRole> userHasRoles = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Address> addresses = new HashSet<>();

    public void saveAdd(Address address){
        if(address != null){
            if(addresses == null){
                addresses = new HashSet<>();
            }
//          nó bắt đầu set này
            addresses.add(address);
            address.setUser(this); // (USER) suser_id
        }
    }

//    Lấy về quyền hạn
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

//    Còn hạn tk hay không
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

// khóa hay không
    @Override
    public boolean isAccountNonLocked() {

//        Cho điều kiện nếu đúng thì chấp nhận
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

//    Hiển thị hay ko
    @Override
    public boolean isEnabled() {
        return true;
    }
}
