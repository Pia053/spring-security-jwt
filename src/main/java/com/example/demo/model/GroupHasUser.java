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
@Table(name = "tbl_group_has_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupHasUser extends AbstractEntity<Integer> {

    @ManyToOne
    @JoinColumn(name ="group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

}
