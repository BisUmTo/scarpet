//////
// gm by BisUmTo
// Added "/gm c" command to change gamemode to creative mode
// Added "/gm s" command to change gamemode to survival mode
// Added "/gm sp" command to change gamemode to spectator mode
// Added "/gm a" command to change gamemode to adventure mode
//////

__config() -> {'scope'->'global','stay_loaded'->true};
__command() -> '';

c() -> (run('gamemode creative '+player()~'command_name');return('Gamemode cambiata con successo'));
s() -> (run('gamemode survival '+player()~'command_name');return('Gamemode cambiata con successo'));
sp() -> (run('gamemode spectator '+player()~'command_name');return('Gamemode cambiata con successo'));
a() -> (run('gamemode adventure '+player()~'command_name');return('Gamemode cambiata con successo'))
