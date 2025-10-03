package app.dtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private String username;
    private String password;
    private Set<String> roles = new HashSet();


    public UserDTO(String username, String password){
        this.username = username;
        this.password = password;
    }

    public UserDTO(String username, Set<String> roles){
        this.username = username;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(username, userDTO.username) && Objects.equals(roles, userDTO.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, roles);
    }


    @Override
    public String toString() {
        return "User with username: " + username + " and roles: " + roles;
    }
}