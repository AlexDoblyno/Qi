package dataaccess;

import dataaccess.DAO.*;

public record DataAccess(MemoryUserDAO user, MemoryGameDAO game, MemoryAuthDAO auth) {}