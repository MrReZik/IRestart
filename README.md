## IRESTART

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![SpigotMC](https://img.shields.io/badge/SpigotMC-1.17+-green.svg)](https://www.spigotmc.org/resources/itnt)
[![Java](https://img.shields.io/badge/Java-17-red.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

**iRestart** is a lightweight and reliable automatic restart scheduler designed specifically for **Velocity Proxy**. The plugin allows you to configure the exact time for the proxy shutdown (or command execution) with flexible notifications for all connected players.

### ✨ Key Features
* **Automatic Scheduling:** Set the exact time (HH:MM) for the restart action to be executed.
* **Time Zone Support:** Ensures correct operation according to the server's time, regardless of your location.
* **Flexible Alerts:** Send warnings via **Chat**, **Titles**, and **ActionBars** with a configurable countdown.
* **Operation Modes:** Supports **`RESTART`** (shuts down the proxy, kicking players) and **`RELOAD`** (executes a predefined console command).
* **Pre-Warning Commands:** Execute console commands before the restart (e.g., `/save-all` or sending messages to backend servers).

### ⚙️ Installation
1.  Make sure you are using **Velocity Proxy (3.4.0+)** and **Java 21**.
2.  Download the latest plugin JAR file.
3.  Place the `iRestart-[version].jar` file into the `plugins` folder on your Velocity server.
4.  Restart the proxy.
5.  Edit the generated configuration file at `plugins/iRestart/config.yml`.

### 🛠️ Commands and Permissions
| Command | Description | Permission |
| :--- | :--- | :--- |
| `/irestart help` | Shows the help menu. | `irestart.admin` |
| `/irestart reload` | Reloads the plugin configuration. | `irestart.admin` |
| `/irestart now` | Immediately executes the restart/action, according to the config settings. | `irestart.admin` |
| `/irestart time set <HH:MM>` | Sets the restart time (e.g., `02:00`). | `irestart.admin` |
| `/irestart timezone <ID>` | Sets the time zone (e.g., `Europe/Moscow`). | `irestart.admin` |
| `/irestart type <RESTART/RELOAD>` | Sets the action mode. | `irestart.admin` |
