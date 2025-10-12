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

## Phase 2 Sequence Diagram

Default:  
https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACUmB0KOAAGsYAAhYAcGAowlqLCIlR3BHVZ60+kEolgACiAA8VNgCAVrlz4fdiuZ6oEnE5huM5upgPZ5vUxgKoN46jA9AymfyoWZOJKROz7rJ5EoVOp6hqwABVAYYt4fFCkm2KZRqVR3SrGWoAMSQnBgrsoPoNlhgHtmYnJKCpMm0egMMFA9oDlD1mB9dv9HMqN25hb96mFovFhTL0sqsow9QArIrlWNVap1Zrljq9fUtPIM4Zs8W89BTegOGSKdSo7BNkgwPF4wNPVmEBSOHGUEK0RoKzmS1VLSh6gufRaqKIT-X6gotymd+0qehqygxfk61KT02wPKTjBB2XY9gsfa6tA9TAE+9JxvIlLoFO5pHsWpScme9RoD4CAINet7WtoRYOiAFLogu7oDD63pEZWAb3MGCgcAyl7aPhVqVKhJFkSgCg+CuGLAPx8TUQWtHHgxOi1ExDJ8SuV5soY6GVDCTz6tiaJ4moOFYKpcJ3lKLwTOuiZLFqwLLEJK7tBAiFoGZyyXNelB-sgcowOE7ajMZUymTA5nfFZ8Q2XZDn7Fc06mF4vgBNA7AajEIZwAK0hwAoMAADIQFkhT-oGp41PUzRtF0vQGOo37KgmcxrL8-wcFc9b5Xp3KvCZnyQiCdU7F80KPPpGE3io9QINl4YYllOV8iypLJqmdJGigzI5OxSmDdyC3TTkH5fhKTXKfAbnNjACpKt5oEauB2qQfqhqMktJoReain5VxqiOigLputVXpibadH5cGYYRqx8ixmuvlzLOKbUj6I5ZgDE5QH9voSet+pvTttbOQ2h0lABMBtmdKr+mBWr9lBRjpvoo6IzdyEznN84DDAaAQMwABmvgRkjKPEfRg1IqzOF4S9B33jAAByEALiG3McFj34465+Otl5JNqpd5M3VhEAQ7AXM+JwDN8wD4vVELoPANDqbOtMwnQEgABeKAzmLpaGZG9sro7LsKyKn7Y-tjZHQTCoAIwgaTWsQQO8be-Evuuyb7tpv9Ob1KRKbonJ8SCcJolvYDUkyTAucKRbVr3C1+qTeGGSqDpmA1wZ1CtT57x+QFIJBSFSH+Y5pr7TKof1J5xMd56YUWWMve2f3AVPTOnjeH4-heCg6AxHEiQb1vk2+FgeXo0V0gChlArtAK3Q9OVqiVQnfc-m38Iqf13Jz3Zzfv6-BVC6N9hD4TWyofLaYBSSpzeh9MAud87WXnmgGi6c0JBikjAYGskC7aHBp-dANtYbU0zGOdQMBeZF3Ni-eomMA67WfrcA6-41YTwur2a6cchy6BpgjY8SMTZMy9kFJOM5yGC2GsLXCq18oSztoIqAztXaKz2r+EeqsTpOEjudaOrCKb6iNrI+Ruwl6mzRh7TCZcsHyEkYRZBDojAoG4DnYScCRLaCQajFBRg0EZBmBAGg5j5JsTFtXH+9QD78Qbk3FuFDCowBGI1ZRKj3Lj2VEYleMV-AogZP4bA4ZqQZTRDAAA4omDQx9TExIaIUy+N97CJmGLguhLlgmwg-sJJ+38Wm-0UiNNExTVQTV6SUsBEDK5KU4uJf00DYENLcfzYu9QMH+JcWDHQcYGn4LTpwohdN8wiPKeWCZVYaFBwSSHVRRMo6a20TrKmw4uHENzPTIx-CZEOzkX7YxaFRHnnEaLUZUjPavJ9u8hRxylbBzxu5COlzuwxzYZTPRbyDEmz2X-MR5dAn-Ose42xyAch9LUM4wuhz6KoIWeGBk+ScgYgJdRHBFjrZBLfp00JgzVQRIQLpH+rcYnjFqaqcCDQ+WJgAJLSHAuHcIwRAggk2PEZcKAFxT27ssZIoBKRKtMiqsY-KUCSy1YPC43R4kvxVkk9WOqSmCuFXMMVEqpUyuWHKhVmqOqDxBGqkAGr2qsJnrq-VbrwrGrNMvaKa8OAAHY3BOBQE4GIApghwGSgANngDxIpiYYBFFDgCyhjRWgdBqXUx+CDlT+sTCa+hzK1LQTaaW0Y5a5h9U6bmoaPys5yBQASjEcAeIEuGRsha91lqsixSfHkHAwGKMabjRhajmFaKujo+od1jQshTli8ZNj3owCdNMutdlZlmzJegilSyYyrKzAevB-C4b3J2dAT5JDokHO3dO5WDDR6EwtSwpdNyOHwweaQp5IaNlAsTiC4RJKeVC2whI1O0iE5CPfRCud0LNFXL-XHRFwLkVGMgSSzOfbEwYl1cS7d8yy7MQzXMCuZ4Tw13qL27OXbEwcq5S2l9+obV6oNfsGAlammJOOsk7yjbfVLFSWGgIlh7GjU2NvJACQwCydwhABTAApCA4YaOGH8J6yk2b8atu5M0Z0pUei6vqdetAypsAIGALJqAcAICjSgGsXVYrBO-0Y1e+Bdk7MOacy5tzHnRXSGbWpVtQsABW2m0Ddq0+GftD0ZqDvpMO-kVjx2bVS9tMFSjTWftUadGFZNY6U1XXlsAG76PYv5lMpxMyn2ks8eSiMGKVlrJsxsu92yeE3Razy19OLVAodOZC46FyMOwuuewwhtMBt6j4XOARSKPmou6b8qx+z9TgeQwVmd+U0PqLK3C5d8d9F+1q2216hH4zYCHKxuYNLEzkdG5R50j3O26avLehb3DxyDc257ahNZwUTbndNjWs2sOUwA-epbk58NjurXCeoSWEtse0pyjpUWuMvG82akT6spOrwCF4RzinlOU7jIgFMsBgDYHs4QPIBQs1lIKqZs+F8r432MB+tH3I8cDXKULEA3A8A9sl1AAdBHt2ZxlxiI9EkT3SHsZ9QwVs1i0u0GsTr1t-t3P60D3Z0GCebP5uNorwmCbQ87Iu7W83jeLdN8j0DTKHgssOgz9jIuhO7cJx+23Y9SegaAA  

Presentation Mode:  
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACUmB0KOAAGsYAAhYAcGAowlqLCIlR3BHVZ60+kEolgACiAA8VNgCAVrlz4fdiuZ6oEnE5huM5upgPZ5vUxgKoN46jA9AymfyoWZOJKROz7rJ5EoVOp6hqwABVAYYt4fFCkm2KZRqVR3SrGWoAMSQnBgrsoPoNlhgHtmYnJKCpMm0egMMFA9oDlD1mB9dv9HMqN25hb96mFovFhTL0sqsow9QArIrlWNVap1Zrljq9fUtPIM4Zs8W89BTegOGSKdSo7BNkgwPF4wNPVmEBSOHGUEK0RoKzmS1VLSh6gufRaqKIT-X6gotymd+0qehqygxfk61KT02wPKTjBB2XY9gsfa6tA9TAE+9JxvIlLoFO5pHsWpScme9RoD4CAINet7WtoRYOiAFLogu7oDD63pEZWAb3MGCgcAyl7aPhVqVKhJFkSgCg+CuGLAPx8TUQWtHHgxOi1ExDJ8SuV5soY6GVDCTz6tiaJ4moOFYKpcJ3lKLwTOuiZLFqwLLEJK7tBAiFoGZyyXNelB-sgcowOE7ajMZUymTA5nfFZ8Q2XZDn7Fc06mF4vgBNA7AajEIZwAK0hwAoMAADIQFkhT-oGp41PUzRtF0vQGOo37KgmcxrL8-wcFc9b5Xp3KvCZnyQiCdU7F80KPPpGE3io9QINl4YYllOV8iypLJqmdJGigzI5OxSmDdyC3TTkH5fhKTXKfAbnNjACpKt5oEauB2qQfqhqMktJoReain5VxqiOigLputVXpibadH5cGYYRqx8ixmuvlzLOKbUj6I5ZgDE5QH9voSet+pvTttbOQ2h0lABMBtmdKr+mBWr9lBRjpvoo6IzdyEznN84DDAaAQMwABmvgRkjKPEfRg1IqzOF4S9B33jAAByEALiG3McFj34465+Otl5JNqpd5M3VhEAQ7AXM+JwDN8wD4vVELoPANDqbOtMwnQEgABeKAzmLpaGZG9sro7LsKyKn7Y-tjZHQTCoAIwgaTWsQQO8be-Evuuyb7tpv9Ob1KRKbonJ8SCcJolvYDUkyTAucKRbVr3C1+qTeGGSqDpmA1wZ1CtT57x+QFIJBSFSH+Y5pr7TKof1J5xMd56YUWWMve2f3AVPTOnjeH4-heCg6AxHEiQb1vk2+FgeXo0V0gChlArtAK3Q9OVqiVQnfc-m38Iqf13Jz3Zzfv6-BVC6N9hD4TWyofLaYBSSpzeh9MAud87WXnmgGi6c0JBikjAYGskC7aHBp-dANtYbU0zGOdQMBeZF3Ni-eomMA67WfrcA6-41YTwur2a6cchy6BpgjY8SMTZMy9kFJOM5yGC2GsLXCq18oSztoIqAztXaKz2r+EeqsTpOEjudaOrCKb6iNrI+Ruwl6mzRh7TCZcsHyEkYRZBDojAoG4DnYScCRLaCQajFBRg0EZBmBAGg5j5JsTFtXH+9QD78Qbk3FuFDCowBGI1ZRKj3Lj2VEYleMV-AogZP4bA4ZqQZTRDAAA4omDQx9TExIaIUy+N97CJmGLguhLlgmwg-sJJ+38Wm-0UiNNExTVQTV6SUsBEDK5KU4uJf00DYENLcfzYu9QMH+JcWDHQcYGn4LTpwohdN8wiPKeWCZVYaFBwSSHVRRMo6a20TrKmw4uHENzPTIx-CZEOzkX7YxaFRHnnEaLUZUjPavJ9u8hRxylbBzxu5COlzuwxzYZTPRbyDEmz2X-MR5dAn-Ose42xyAch9LUM4wuhz6KoIWeGBk+ScgYgJdRHBFjrZBLfp00JgzVQRIQLpH+rcYnjFqaqcCDQ+WJgAJLSHAuHcIwRAggk2PEZcKAFxT27ssZIoBKRKtMiqsY-KUCSy1YPC43R4kvxVkk9WOqSmCuFXMMVEqpUyuWHKhVmqOqDxBGqkAGr2qsJnrq-VbrwrGrNMvaKa8OAAHY3BOBQE4GIApghwGSgANngDxIpiYYBFFDgCyhjRWgdBqXUx+CDlT+sTCa+hzK1LQTaaW0Y5a5h9U6bmoaPys5yBQASjEcAeIEuGRsha91lqsixSfHkHAwGKMabjRhajmFaKujo+od1jQshTli8ZNj3owCdNMutdlZlmzJegilSyYyrKzAevB-C4b3J2dAT5JDokHO3dO5WDDR6EwtSwpdNyOHwweaQp5IaNlAsTiC4RJKeVC2whI1O0iE5CPfRCud0LNFXL-XHRFwLkVGMgSSzOfbEwYl1cS7d8yy7MQzXMCuZ4Tw13qL27OXbEwcq5S2l9+obV6oNfsGAlammJOOsk7yjbfVLFSWGgIlh7GjU2NvJACQwCydwhABTAApCA4YaOGH8J6yk2b8atu5M0Z0pUei6vqdetAypsAIGALJqAcAICjSgGsXVYrBO-0Y1e+Bdk7MOacy5tzHnRXSGbWpVtQsABW2m0Ddq0+GftD0ZqDvpMO-kVjx2bVS9tMFSjTWftUadGFZNY6U1XXlsAG76PYv5lMpxMyn2ks8eSiMGKVlrJsxsu92yeE3Razy19OLVAodOZC46FyMOwuuewwhtMBt6j4XOARSKPmou6b8qx+z9TgeQwVmd+U0PqLK3C5d8d9F+1q2216hH4zYCHKxuYNLEzkdG5R50j3O26avLehb3DxyDc257ahNZwUTbndNjWs2sOUwA-epbk58NjurXCeoSWEtse0pyjpUWuMvG82akT6spOrwCF4RzinlOU7jIgFMsBgDYHs4QPIBQs1lIKqZs+F8r432MB+tH3I8cDXKULEA3A8A9sl1AAdBHt2ZxlxiI9EkT3SHsZ9QwVs1i0u0GsTr1t-t3P60D3Z0GCebP5uNorwmCbQ87Iu7W83jeLdN8j0DTKHgssOgz9jIuhO7cJx+23Y9SegaAA  
