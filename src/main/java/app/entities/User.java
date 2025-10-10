package app.entities;


import jakarta.persistence.*;
import lombok.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "username", nullable = false)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String username;
    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User(String username, String password){
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.username = username;
    }

    public boolean checkPassword(String realPassword) {
        if(BCrypt.checkpw(realPassword, password)) {
            return true;
        } else {
            return false;
        }
    }

    public void addRole(Role role){
        roles.add(role);
        role.getUsers().add(this);
    }
}
