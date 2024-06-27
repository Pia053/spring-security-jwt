package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.util.Date;

// Serializable:
// MapperSupperclass: Một lớp có thông tin ánh xạ được áp dụng từ nó
// Serializable: Được sử dụng để chỉ định một lớp có thông tin ánh xạ được áp dụng kế thừa từ nó
// CreationTimestamp: Tự động tạo ra khi lần đầu tiên
// UpdateTimestamp: Tự động cập nhật sau khi update đối tượng.

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity<T extends Serializable> implements Serializable {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "id")
    private T id;

    @CreatedBy
    @Column(name="created_by")
    private T createdBy;

    @LastModifiedBy
    @Column(name="updated_by")
    private T updatedBy;

    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name="updated_at")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
