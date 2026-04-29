package br.com.produlab.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "full_name",nullable = false)
    public String fullName;
    @Column(nullable = false)
    public String email;
    @JsonbTransient
    @Column(nullable = false)
    public String password;
    @Column(nullable = true)
    public String telephone;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_profile")
    public UserProfile userProfile;
    @Column(name="password_expired")
    public Boolean passwordExpired = true;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="created_by")
    public User createdBy;
    @Column(name="deleted_at")
    public LocalDateTime deletedAt;
    @JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="deleted_by")
    public User deletedBy;
    @Column(name="number_logins")
    public Integer numberLogins;
    @Column(name="last_login")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    public LocalDateTime lastLogin;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {
            CascadeType.MERGE,
            CascadeType.PERSIST
    })
    @JoinTable(
            name = "users_laboratories",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "laboratory_id")
    )
    public List<Laboratory> userLaboratories;

    public static Optional<User> findByEmailSenha(String email, String password){
        // Removed debug logs that exposed sensitive information
        User user = find("email = ?1 and password = ?2 AND deletedAt IS NULL",email,password).firstResult();
        return Optional.ofNullable(user);
    }

    public static Optional<User> findByEmail(String email){
        User user = find("email",email).firstResult();
        return Optional.ofNullable(user);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
