package ca.innov8solutions.parachute.local;

import ca.innov8solutions.parachute.ParachutePlugin;
import ca.innov8solutions.parachute.framework.ParachuteController;
import ca.innov8solutions.parachute.framework.ParachuteServer;
import ca.innov8solutions.parachute.framework.ServerType;
import ca.innov8solutions.parachute.task.OnlinePingTask;
import ca.innov8solutions.parachute.util.Utils;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LParachuteController extends ParachuteController {

    private @Inject
    ParachutePlugin plugin;

    private List<LParachuteServer> parachuteServers = Lists.newArrayList();

    private Process process;
    private @Inject
    @Named("appDir")
    File appDir;

    int taskId;


    public LParachuteController(String name, ServerType type) {
        super(name, type);
    }

    public Process getProcess() {
        return process;
    }

    public int getRandomPort(int min, int max) {
        return 0;
    }

    public List<LParachuteServer> getServers() {
        return parachuteServers;
    }

    public String getName(String type) {
        int amount = 1;

        for (ParachuteServer server : parachuteServers) {
            if (server.getName().contains(type)) {
                amount++;
            }
        }

        return type + "-" + amount;
    }

    @Override
    public void create() {
        if (process != null)
            return;

        String name = getName(getType().getName());
        System.out.println(name);

        File serverFolder = new File(appDir, "/instances/" + name);
        File imageFolder = new File(appDir, "/images/" + getType().getName());

        if (!imageFolder.exists()) {
            throw new RuntimeException("Cannot find image for server type " + getType().getName());
        }

        if (serverFolder.exists()) {
            serverFolder.delete();
        }

        serverFolder.mkdir();
        int port = getRandomPort(getType().getMinPort(), getType().getMaxPort());

        try {
            System.out.println("Attempting to copy server directory");
            Utils.copyFilesInDirectory(imageFolder, serverFolder);
            Utils.replaceFile(new File(serverFolder, "server.properties"), "server-port=", "server-port=" + port);

            System.out.println("Starting server on new screen");
            process = new ProcessBuilder().directory(serverFolder)
                    .command("screen", "-dmS", name).start();
            process.waitFor();
            process = new ProcessBuilder().directory(serverFolder).command("screen","-S", name, "-p", "0", "-X","stuff","chmod a+x run.sh ; ./run.sh\n").start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        ServerInfo info = plugin.getProxy().constructServerInfo(name, new InetSocketAddress(port), "", false);
        OnlinePingTask task = new OnlinePingTask(info);

        taskId = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            if (task.isOnline().isDone()) {
                try {
                    boolean online = task.isOnline().get();

                    if (task.getAttempts() > 5) {
                        plugin.getProxy().getScheduler().cancel(taskId);
                        throw new Exception("Unable to ping server " + name + " after 5 attempts");
                    }

                    if (online) {
                        ProxyServer.getInstance().getServers().put(name, info);
                        this.parachuteServers.add(new LParachuteServer(name, port));
                        plugin.getProxy().getScheduler().cancel(taskId);
                    }

                    task.setAttempts(task.getAttempts()+1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 25, TimeUnit.SECONDS).getId();

        process = null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean destroy() {
        return false;
    }

    @Override
    public List<String> getIps() {
        return null;
    }
}
