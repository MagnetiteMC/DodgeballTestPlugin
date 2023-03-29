# DodgeballTestPlugin
### By Magnetite

Welcome to Dodgeball!

Dodgeball is a primitive game of dodging a ball being thrown at you. There are two teams each trying to get the other team out. 
The objective of dodgeball is to eliminate all players of the opposing team by throwing balls and hitting the opposing players.
Players are not allowed to cross the line in the middle of the court. 

Notes:  
&nbsp;&nbsp;&nbsp;&nbsp;• Snowballs are the balls in this game. When they land another snowball spawns where it landed.  
&nbsp;&nbsp;&nbsp;&nbsp;• When a player is hit with a snowball or touches the tape (obsidian), they are out.   
&nbsp;&nbsp;&nbsp;&nbsp;• Customizable commands on victory/loss are available in the config.  

## Arena System
Support for multiple games at once as the arenas spawn in different areas of the dodgeball world. 
A pre-game lobby and arena are provided by default in the plugin folder. You may replace them with any schematics of the same name and configure their spawn locations. 

## Placeholders
**%dodgeball_team1_alive%** - Returns the number of players alive on team one  
**%dodgeball_team2_alive%** - Returns the number of players alive on team two 

## Commands and Permissions

| Name                 | Permission       | Usage                                          |
|----------------------|------------------|------------------------------------------------|
| /dodgeball create    | dodgeball.create | Creates a new game with any available game ID. |
| /dodgeball join (id) | dodgeball.join   | Joins a game with the specified game ID.       |
| /dodgeball leave     | dodgeball.leave  | Leaves the game that you're currently in.      |
| /dodgeball reload    | dodgeball.reload | Reloads the config.yml file.                   |
| /dodgeball help      | dodgeball.help   | Shows the help menu.                           |