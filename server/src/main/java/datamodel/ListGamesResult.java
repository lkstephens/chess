package datamodel;

import java.util.List;

public record ListGamesResult(List<GameDataTruncated> games) {
}
