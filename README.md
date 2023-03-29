# DodgeballTestPlugin
### By Magnetite

Welcome to Dodgeball!

Dodgeball is a primitive game of dodging a ball being thrown at you. There are two teams each trying to get the other team out. 
The objective of dodgeball is to eliminate all players of the opposing team by throwing balls and hitting the opposing players.
Players are not allowed to cross the line in the middle of the court. 

Notes: 
    • Snowballs are the balls in this game. When the snowballs land, spawn another snowball where it landed. When a player is hit with a snowball, they are out. 
    • Has placeholders (placeholderAPI) that can be used in other configurations to track how many players are alive on both sides. 
    • A pregame lobby function is included. 
    • Has customizable commands on victory in the config. 

## Arena System
Support for multiple games at once as the arenas spawn in different areas of the dodgeball world. 
A lobby and arena are provided by default in the plugin folder. You may replace them with any schematics of the same name and configure their spawn locations. 

## Placeholders
%dodgeball_team1_alive% - Returns the number of players alive on team one 
%dodgeball_team2_alive% - Returns the number of players alive on team two 
