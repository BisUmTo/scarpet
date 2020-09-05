__config() -> {'scope'->'global','stay_loaded'->true};
__command() -> '';

c() -> (run('gamemode creative '+player()~'command_name');return('Gamemode cambiata con successo'));
s() -> (run('gamemode survival '+player()~'command_name');return('Gamemode cambiata con successo'));
sp() -> (run('gamemode spectator '+player()~'command_name');return('Gamemode cambiata con successo'));
a() -> (run('gamemode adventure '+player()~'command_name');return('Gamemode cambiata con successo'))
