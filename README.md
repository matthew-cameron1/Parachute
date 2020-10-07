<h1>Parachute</h1>

Parachute is a Minecraft auto deployment system for servers looking for an easy way to scale their gamemode.

<h2>Setup</h2>

Define your application directory in the config: <code>"appDir": "/home/parachute"</code>

When running Parachute for the first time, it will create two sub directories of the application dir **Images** and **Instances**

Inside images, create sub folders of each game type you plan on deploying. Inside the folders please insert the following:

- eula.txt
- plugins folder
- run.sh script
- maps (optional)
- server.properties
- spigot.yml (optional)