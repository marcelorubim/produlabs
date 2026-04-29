package br.com.produlab.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "laboratories")
public class Laboratory extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(nullable = false, unique = true)
    public String initials;
    @Column(nullable = false, unique = true)
    public String name;
    @Column(nullable = false)
    public String region;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
    @Column(name="created_at")
    public LocalDateTime createdAt = LocalDateTime.now();
    @JsonbTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="created_by")
    public User createdBy;

    @Override
    public String toString() {
        return "Laboratory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region='" + region + '\'' +
                '}';
    }
}
