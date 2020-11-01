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
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import org.apache.commons.lang3.RandomUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LParachuteController extends ParachuteController {

    private
    ParachutePlugin plugin;

    private @Inject
    JedisPool jedisPool;

    private List<ParachuteServer> parachuteServers = Lists.newArrayList();

    private Process process;

    private @Inject
    @Named("appDir")
    File appDir;

    int taskId;


    public LParachuteController(ParachutePlugin plugin, String name, ServerType type) {
        super(name, type);
        this.plugin = plugin;
        ProxyServer.getInstance().getScheduler().schedule(plugin, this::destroy, 0L, 60, TimeUnit.SECONDS);
        int i = 0;
        while (parachuteServers.size() < getType().getMinInstances() && i < 1000000) {
            create();
            i++;
        }
    }

    public Process getProcess() {
        return process;
    }

    public int getRandomPort(int min, int max) {
        return RandomUtils.nextInt(min, max);
    }

    @Override
    public List<ParachuteServer> getServers() {
        System.out.println("Servers:" + parachuteServers.size());
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
            process = new ProcessBuilder().directory(serverFolder).command("screen", "-S", name, "-p", "0", "-X", "stuff", "chmod a+x run.sh ; ./run.sh\n").start();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        ServerInfo info = plugin.getProxy().constructServerInfo(name, new InetSocketAddress("127.0.0.1", port), "", false);

        int[] attempts = new int[]{0};
        taskId = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            info.ping((serverPing, throwable) -> {
                if (throwable == null) {
                    //We online
                    ProxyServer.getInstance().getServers().put(name, info);
                    LParachuteServer server = new LParachuteServer(name, port);
                    server.setInfo(info);
                    this.parachuteServers.add(server);
                    plugin.getProxy().getScheduler().cancel(taskId);
                    System.out.println("Server detected! Adding to list of servers");
                    return;
                } else {
                    System.out.println("Failed to ping server");
                    attempts[0] += 1;
                }
                if (attempts[0] > 5) {
                    ProxyServer.getInstance().getScheduler().cancel(taskId);
                    System.out.println("Cancelling task!");
                }
            });
        }, 0L, 20L, TimeUnit.SECONDS).getId();

        process = null;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean destroy() {
        //This is called in a repeating task

        if (getServers().size() <= getType().getMinInstances()) {
            return false;
        }

        //Ok we need to remove a server, so lets check which to remove

        int gameServersNeeded = (int) Math.ceil(getServers().size() / 24.0);

        if (gameServersNeeded < getServers().size()) {
            //We need to remove a server

            ServerInfo empty = plugin.getProxy().getServers().values().stream().filter(s -> s.getPlayers().size() == 0).findAny().orElse(null);

            if (empty != null) {
                return kill(empty.getName());
            }
            ParachuteServer random = getServers().get(RandomUtils.nextInt(0, getServers().size() - 1));
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);

            try {
                out.writeUTF("Forward");
                out.writeUTF(random.getName());
                out.writeUTF("Parachute");
                out.writeUTF("disableFlag");
            } catch (Exception e) {
                e.printStackTrace();
            }
            random.getInfo().sendData("parachute:main", b.toByteArray());
            System.out.println("Sent shutdown flag to random game server");
        }

        return false;
    }

    private boolean kill(String name) {
        try {
            process = new ProcessBuilder().command("screen", "-S", name, "-p", "0", "-X", "stuff", "'stop\n'").start();
            process.waitFor();
            process = new ProcessBuilder().command("screen", "-X", "-S", name, "kill").start();
            this.getServers().remove(getByName(name));
            plugin.getProxy().getServers().remove(name);
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String> getIps() {
        return null;
    }

    public ParachuteServer getByName(String name) {
        for (ParachuteServer p : parachuteServers) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
}
