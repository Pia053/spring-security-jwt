package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group extends AbstractEntity<Integer> {

    private String name;

    private String description;

    @OneToOne
    private Role role;

    @OneToMany(mappedBy = "group")
    private Set<GroupHasUser> groups = new HashSet<>();
}
