package de.oskar.forceitem.database;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;

import de.oskar.forceitem.game.collectables.Collectable;
import de.oskar.forceitem.game.managers.states.end.EndManager;
import de.oskar.forceitem.game.managers.teams.Team;
import de.oskar.forceitem.game.managers.teams.TeamManager;

public class GameStorer {

    private Team[] teams;

    public GameStorer() {
        collectData();
        storeData();
    }

    public void collectData() {
        this.teams = TeamManager.getInstance().getTeams().values().toArray(new Team[0]);

    }

    private Document generateDocument() {
        Document document = new Document();
        document.append("_id", new ObjectId());
        document.append("timestamp", System.currentTimeMillis());
        document.put("teams", generateTeamDocuments(teams));
        return document;
    }

    private List<Document> generateTeamDocuments(Team[] teams) {
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; i < teams.length; i++) {
            documents.add(generateTeamDocument(teams[i]));
        }
        return documents;
    }

    private Document generateTeamDocument(Team team) {
        Document document = new Document();
        // Team Color
        document.append("color", team.getColor().getDisplayNameWithoutColor());

        // Players
        Player[] players = team.getPlayers().toArray(new Player[0]);
        document.put("players", generatePlayerDocuments(players));

        // Collectables
        LinkedList<Collectable> collectables = team.getCollectables();
        document.put("collectables", generateCollectablesDocument(collectables));

        // Points
        document.append("points", team.getPoints());

        // Available Skips
        document.append("availableSkips", team.getAvailableSkips());
        return document;
    }

    private List<Document> generateCollectablesDocument(LinkedList<Collectable> collectables) {
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; i < collectables.size(); i++) {
            documents.add(generateCollectableDocument(collectables.get(i)));
        }
        return documents;
    }

    private Document generateCollectableDocument(Collectable collectable) {
        Document document = new Document();
        document.append("name", collectable.getName());
        document.append("displayName", collectable.getDisplayName());
        document.append("material", collectable.getMaterial().toString());
        document.append("difficulty", collectable.getDifficulty().getDisplayName());
        document.append("points", collectable.getPoints());
        document.append("finished", collectable.isFinished());
        document.append("skipped", collectable.isSkipped());
        document.append("active", collectable.isActive());
        return document;
    }

    private List<Document>generatePlayerDocuments(Player[] players) {
        List<Document> documents = new ArrayList<Document>();
        for (int i = 0; i < players.length; i++) {
            documents.add(generatePlayerDocument(players[i]));
        }
        return documents;
    }

    private Document generatePlayerDocument(Player player) {
        Document document = new Document();
        document.append("name", player.getName());
        document.append("uuid", player.getUniqueId().toString());
        return document;
    }

    public void storeData() {
        MongoDatabase database = MongoDB.getInstance().getDatabase();
        try {
            InsertOneResult result = database.getCollection("results").insertOne(generateDocument());
            if (result.getInsertedId() == null) {
                System.out.println("Error while storing data");
                return;
            }
            EndManager.getInstance().sendResultId(result.getInsertedId().asObjectId().getValue().toString());
            System.out.println("Stored data");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
