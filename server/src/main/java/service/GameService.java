package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static dataaccess.AuthDAO.getAuth;
import static dataaccess.GameDAO.*;

public class GameService implements Service {

    public CreateResult create(CreateRequest createRequest)
            throws UnauthorizedException, BadRequestException, DataAccessException {
        if (createRequest.authToken() == null ||
                createRequest.gameName() == null) {
            throw new BadRequestException("Bad Request");
        }

        if (authExists(createRequest.authToken())) {
            int gameID;
            while (true) {
                gameID = ThreadLocalRandom.current().nextInt(1000, 10000);
                GameData game = getGame(gameID);
                if (game == null) {
                    game = new GameData(gameID,
                            null, null,
                            createRequest.gameName(),
                            new ChessGame());
                    createGame(game);

                    break;
                }
            }
            return new CreateResult(gameID);
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public JoinResult join(JoinRequest joinRequest)
            throws UnauthorizedException, UnavailableException, BadRequestException, DataAccessException {
        if (joinRequest.authToken() == null ||
                joinRequest.gameID() == 0 ||
                joinRequest.playerColor() == null) {
            throw new BadRequestException("Bad Request");
        }

        AuthData auth = getAuth(joinRequest.authToken());
        if (auth != null) {
            GameData game = getGame(joinRequest.gameID());
            if (game != null) {
                if (joinRequest.playerColor().equals("WHITE")) {
                    if (game.whiteUsername() == null) {
                        updateGame(new GameData(game.gameID(),
                                auth.username(),
                                game.blackUsername(),
                                game.gameName(),
                                game.game()));

                        return new JoinResult();
                    } else {
                        throw new UnavailableException("Already Taken");
                    }
                } else if (joinRequest.playerColor().equals("BLACK")) {
                    if (game.blackUsername() == null) {
                        updateGame(new GameData(game.gameID(),
                                game.whiteUsername(),
                                auth.username(),
                                game.gameName(),
                                game.game()));

                        return new JoinResult();
                    } else {
                        throw new UnavailableException("Already Taken");
                    }
                } else {
                    throw new BadRequestException("Bad Request");
                }
            } else {
                throw new BadRequestException("Bad Request");
            }
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }

    public ListResult list(ListRequest listRequest)
            throws UnauthorizedException, DataAccessException {
        if (authExists(listRequest.authToken())) {
            return new ListResult((List<GameData>) listGames());
        } else {
            throw new UnauthorizedException("Unauthorized");
        }
    }
}
