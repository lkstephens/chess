package dataaccess;

import datamodel.RegisterRequest;
import model.UserData;

public class SQLUserDAO implements UserDAO{
    @Override
    public void createUser(UserData userData) {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return new UserData("1","2","3");       // DELETE ME
    }
}
