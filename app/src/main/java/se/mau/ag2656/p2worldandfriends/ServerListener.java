package se.mau.ag2656.p2worldandfriends;

import org.json.JSONObject;

public interface ServerListener {

    void serverCallback(JSONObject serverResponse);
}
