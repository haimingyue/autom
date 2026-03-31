package com.atoms.backend.user.entity;

import com.atoms.backend.common.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wechat_open_id", unique = true)
    private String wechatOpenId;

    @Column(name = "wechat_union_id", unique = true)
    private String wechatUnionId;

    @Column(name = "display_name", nullable = false, length = 64)
    private String displayName;

    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;

    @Column(name = "timezone", nullable = false, length = 64)
    private String timezone;
}
