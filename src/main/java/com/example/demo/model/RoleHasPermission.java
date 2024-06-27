package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_role_has_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleHasPermission extends AbstractEntity<Integer> {

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name="permiession_id")
    private Permission permission;
}
