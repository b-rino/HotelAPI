package app.daos;

import app.entities.User;
import app.entities.Role;
import app.exceptions.EntityAlreadyExistsException;
import app.exceptions.EntityNotFoundException;
import app.exceptions.ValidationException;

public interface ISecurityDAO {
    User getVerifiedUser(String username, String password) throws ValidationException; // used for login
    User createUser(String username, String password) throws EntityAlreadyExistsException; // used for register
    //Role createRole(String role);
    User addUserRole(String username, String role) throws EntityNotFoundException;
    Role createRole(String roleName) throws EntityAlreadyExistsException;
}
