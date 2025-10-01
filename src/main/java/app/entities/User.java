package app.entities;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "username", nullable = false)
    private String username;
    private String password;

    @ManyToMany
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @EqualsAndHashCode.Exclude
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
