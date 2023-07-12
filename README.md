Bedwars
=======

[![CI](https://github.com/Filiprogrammer/Bedwars/actions/workflows/main.yml/badge.svg)](https://github.com/Filiprogrammer/Bedwars/actions/workflows/main.yml)

Highly customizable Bedwars plugin

Features
--------

* User friendly arena setup process
* Sophisticated game world system (Every game runs in a temporary copy of the original world)
* Customizable game states with various actions
  * Destroy/replace all beds
  * Spawn/kill dragons
  * Change spawner rates
  * Change max player health
  * Play sounds
  * Display custom messages
* Customizable item shop with several special items
* Customizable team shop
  * Mining boost
  * Protection boost
  * Attack boost
  * Heal pool
  * Restore the bed
  * Trap
  * Additional team dragon
* Customizable resource spawners
* Customizable sounds
* Customizable multilingual messages
* Customizable default items when a player respawns
* Various other customizations
* Bedwars specific events to be used by other plugins

Using the Bedwars Plugin
------------------------

*TODO*

Building the Bedwars Plugin
---------------------------

```console
sudo apt update
sudo apt install --no-install-recommends git default-jdk-headless
git clone https://github.com/Filiprogrammer/Bedwars.git
./gradlew build
```
