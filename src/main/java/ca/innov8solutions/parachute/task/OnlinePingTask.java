package ca.innov8solutions.parachute.task;

import io.netty.util.concurrent.CompleteFuture;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerConnectRequest;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class OnlinePingTask implements Listener {

    private ServerInfo info;
    private int attempts = 0;

    public OnlinePingTask(ServerInfo info) {
        this.info = info;
    }

    public Future<Boolean> isOnline() {
        Future<Boolean> future = new CompletableFuture<>();

        info.ping((serverPing, throwable) -> {
            if (throwable == null)
                ((CompletableFuture<Boolean>) future).complete(true);
        });
        return future;
    }

    public ServerInfo getInfo() {
        return info;
    }

    public void setInfo(ServerInfo info) {
        this.info = info;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }
}
