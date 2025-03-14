# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

## Sequence Diagram

https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdOUyABRAAyXLg9RgdOAoxgADNvMMhR1MIziSyTqDcSpymgfAgEDiRCo2XLmaSYCBIXIUNTKLSOndZi83hxZj9tgztPL1GzjOUAJIAOW54UFwtG1v0ryW9s22xg3vqNWltDBOtOepJqnKRpQJoUPjAqQtQxFKCdRP1rNO7sjPq5ftjt3zs2AWdS9QgAGt0OXozB69nZc7i4rCvGUOUu42W+gtVQ8blToCLmUYJDETDIqp1VhZ8D+0VqJcYNdLfnJuVVk8R03W2gj1N9hPKFvsuZygAmJxObo9KYB+59L5nseXiZJgOdAOFMLxfH8AJoHYckYB5CBoiSAI0gyLJkHMNkjl3ao6iaVoDHUBI0HfL8gwWJYDiwkFCg3Xd9zzUZdi+G0liYgFzk3JVtSHGAEAQpA0FzGtAxgFj3mxQddV7ZMyQpM04S-QsmWTN0OXIXl+UFAAqGAADFwhqABZasYAAdRYSsuWrO4AF4vx7IsVOnAdlR4m5GQnKcCWkllU2NTJM2zIT6Qc5SFVLNTvV9f0DxEv8L3bGMR1Cl0Sy4ycVU7Btz3HSTnLOIFdz4xCVzXTBaJBdK6IGWKxhgY9HimeL0CvVYDhgDrOq6jqqPvdCMGfV930-WqVl-bL-zGmADjMTgwO8PxAi8FA23g+xfGYZD0kyTAHzyFyd3nCppA0+ouWaFoCNUIjumatBb2ogq52HCaL3Kji73S8FeIQjbYTuiTXKkxzfJgckwECnMAZSvsIs5DSBSy7McqSfSjKR1IYacr7MruzyE28kGDQ4FBuACht-te9AlNS1TyhOvkzoxlG9IM4zkqTcKcZ4tUNXxwx8oq1V1U1Cqtyoq5KOKT7Tj2sBBrfXoZtm0DPAWyDIQ4ODoRgABxfNWS21Ddv65gqqO3WuTwlp7HzW6qfu3qZw++c8bF7nymQWJ9dGVRKeR-9Ae44GwoNcHIf90cLxp2HCjLbk+URu7WfRjmfNdfLBxegO3v5kPUtksAfbUSOUZjpy47UhPNJgHS0eM23faxrmDoyniDDAAAeOBUhZYv8jzwWXc96Fi9ZUqEHXF3xel+cPz6Ru1HGSp+kXj1pGXgBGJ8AGYABYnhQzJ5LuManh0BBQGbE-D0Ap5F69fNAL2GAHr6nJ5cqF8nEaYaF4N5eFRV75nXlvXeB8phH1NKNO+UwL5XxvoxWB-9RiP1GM-GAjQX4q3mhBQI2AfBQGwNweA-lDDFxSNtNCH9MKz3KDhBoF1F72xzugd8D98xS0OmyIWzN-yzGAagw87FCqPSzoaMhY9S78LBvmNBBZB6E1DimMGFII7Q05hnSu8NE6CmTvXDGzcM4ez4bnPKSiC4SPTJkYu69YQcNGOXcK2jyzRVkUI0U5x3EoHXjAASkBvEaE0WlVu3017SEHs7URflrEoGLhPKeoiZ6HSuFMcJYD97TTfvlOWCs-7pPKNvTJwE5pqzwQESwpM+LJBgAAKQgAJPW+ZAjwJAM2E2NDzb0KqJSa2zDggO3fMQ4AlSoBwAgHxKAsxwlcOOFE56pj0ACOeJfUZ4zJnTJARvERc5aHcXKAAKwaWgYu0iLzWlWZQdZ0BNmjHXkHNu+diyF3UQ7JxWj2Q6JrvotmhjgnJLbtnKO45FEyHTio8GpzwnvJLC46uiNF4pwbgbIxITtyAu8R5UFwTDnHNOaRUSlyxkTJud4+5qK6auMrDFBiKALkjKuSS2A-iIBkoif8zOrlhZ8zyvM4E3LRbT05SkvcsyZaFFyTAb+74Smq3AotAIXgRnwG4HgTs2BiGEHiIkShxs5Z7JKPQhmVtzqtGMNkmiw9DSqrhNid2oTMogBtbCB5XkwVExUU69MUB5J+xhXTY1TMbhBPBQC762LwWphtVI-1cNA1WUXiGj1YbMoRo9VG71kM-UUrjadKyI4k3KJTTxSJlrokwF5oKpJwrDUVpFtk2Wps8lK1MDgoAA
