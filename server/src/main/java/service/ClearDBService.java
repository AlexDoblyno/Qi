package service;

import dataaccess.AuthSQL;
import dataaccess.GameSQL;
import requestclasses.ClearDBRequest;
import resultclasses.ClearDBResult;

public class ClearDBService extends Service {
    public ClearDBService() {
    }

    public ClearDBService(AuthSQL authSQL, GameSQL gameSQL, UserSQL userSQL) {
        super(authSQL, gameSQL, userSQL);
    }

    public ClearDBResult clearDB(ClearDBRequest request) {
        try {
            this.authSQL.clear();
            this.gameSQL.clear();
            this.userSQL.clear();
        } catch (Exception e) {
            return new ClearDBResult(500, "Error: " + e.getMessage());
        }

        return new ClearDBResult(200);
    }
}
