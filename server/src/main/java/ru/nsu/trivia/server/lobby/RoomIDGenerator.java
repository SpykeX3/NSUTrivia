package ru.nsu.trivia.server.lobby;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RoomIDGenerator {
    Random rng = new Random();
    Set<String> inUsage = Collections.synchronizedSet(new HashSet<>());

    public String generate() {
        String result;
        do {
            result = randomID();
        } while (inUsage.contains(result));
        return result;
    }

    public void recall(String id) {
        inUsage.remove(id);
    }

    private String randomID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append((char) (rng.nextInt(26) + 'A'));
        }
        return sb.toString();
    }

    public int idsUsed() {
        return inUsage.size();
    }
}
