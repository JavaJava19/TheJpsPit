package com.github.elic0de.thejpspit.spigot.util;

import com.github.elic0de.thejpspit.spigot.TheJpsPit;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DownloadUtil {

    private static final String GITHUB_REPO_RELEASES = "https://api.github.com/repos/JavaJava19/TheJpsPit/releases";
    private static final String GITHUB_RELEASES_LATEST_URL = GITHUB_REPO_RELEASES + "/latest";

    // https://www.spigotmc.org/threads/auto-updater-using-github.324545/
    public static void download(final Player player){
        try {
            final String TOKEN = TheJpsPit.getInstance().getSettings().getGithubToken();

            if (TOKEN.equalsIgnoreCase("")) {
                player.sendMessage("トークンを設定してください");
                return;
            }
            final URL api = new URL(GITHUB_RELEASES_LATEST_URL); //You want to set //this to your own project URL
            final URLConnection con = api.openConnection();
            con.setRequestProperty("Accept", "application/vnd.github+json");
            con.setRequestProperty("Authorization", "Bearer %token%".replaceAll("%token%", TOKEN));
            con.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);

            final JsonObject json = JsonParser.parseReader(new InputStreamReader(con.getInputStream())).getAsJsonObject();

            if (json.has("assets")) {
                final JsonArray assets = json.get("assets").getAsJsonArray();
                for (JsonElement asset : assets) {
                    final JsonObject assetJson = asset.getAsJsonObject();
                    final String ASSETS_URL = assetJson.get("url").getAsString();
                    final String ASSETS_NAME = assetJson.get("name").getAsString();
                    final URL download = new URL(ASSETS_URL); //This is where you put your download URL for your latest release, be sure to change this to //your own.
                    final URLConnection urlConnection = download.openConnection();
                    urlConnection.setRequestProperty("Authorization", "Bearer %token%".replaceAll("%token%", TOKEN));
                    urlConnection.setRequestProperty("Accept", "application/octet-stream");

                    player.sendMessage(
                        ChatColor.GREEN + "[TheJpsPit] " + ASSETS_NAME + " をダウンロードします");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            try {

                                InputStream in = urlConnection.getInputStream();
                                File temp = new File(
                                    "plugins/update"); //We want to make the folder if it doesn't exist //already
                                if (!temp.exists()) {
                                    temp.mkdir();
                                }
                                Path path = new File("plugins/update" + File.separator
                                    + ASSETS_NAME).toPath(); //Here you will //put your file name, it must be named the same everytime for it to properly replace the existing, it will send //the file to the updates folder, from there the server will handle the rest.
                                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.runTaskLaterAsynchronously(TheJpsPit.getInstance(),
                        0); //We want this to be an async task so we don't clog up the main thread, also ThisPlugin.getPlugin() is just a short cut for me not to have to use this, it saves me time incase you were wondering
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
