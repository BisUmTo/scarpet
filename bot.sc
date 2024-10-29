//////
// bot script by BisUmTo
// adds /bot command:
// /bot -> Shows help
// /bot list -> Shows all bots in the chat
// /bot spawn -> Spawns a bot in current position for 24 hours
// /bot spawn <time> -> Spawns a bot in current position for specified time
// /bot kill -> Kills nearest bot (in 5 blocks radius)
// /bot kill <name> -> Kills bot with specified name
//////

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global',
    'commands' -> {
        '' -> _() -> _help(),
        'list' -> _() -> _list(),
        'spawn' -> _() -> _spawn(1728000),
        'spawn <time>' -> _(time) -> _spawn(time),
        'kill' -> _() -> _kill_nearest(),
        'kill <name>' -> _(name) -> _kill(name)
    },
    'arguments' -> {
        'name' -> {
            'type' -> 'term',
            'suggester' -> _(args) -> _get_online_bots()
        }
    }
};

_get_online_bots() -> filter(player('*'), _~'command_name'~'_bot\\d+$');

_help() -> (
    print(player(), format('b Usage of /bot:'));
    print(player(), format('ig  /bot list', '?/bot list', '  - Shows all bots in the chat'));
    print(player(), format('ig  /bot spawn', '?/bot spawn', '  - Spawns a bot in current position for 24 hours'));
    print(player(), format('ig  /bot spawn <time>', '?/bot spawn','  - Spawns a bot in current position for specified time'));
    print(player(), format('ig  /bot kill', '?/bot kill', '  - Kills nearest bot (in 5 blocks radius)'));
    print(player(), format('ig  /bot kill <name>', '?/bot kill', '  - Kills bot with specified name'));
);

_list() -> (
    bots = _get_online_bots();
    if(!bots, return(print(player(), 'No bots online')));
    print(player(), format('b \nOnline bots:'));
    for(bots, 
        print(player(), format(str('ig  %s', _~'command_name'), str('?/bot kill %s', _~'command_name')))
    );
);

_spawn(time) -> (
    time = min(1728000, time);
    player = player();
    name = _get_bot_name(player);
    if(name == null, return(print(player(), format('r Too many bots online'))));
    run(str('execute as %s at @s run player %s spawn', 
        player()~'command_name',
        name
    ));
    schedule(time, _(outer(name), outer(player)) -> (
        run(str('player %s kill', name));
        print(player, format(str('g Bot %s despawned', name)));
    ));
    print(player(), format(str('g Bot %s spawned for %s ticks', name, time)));

);

_get_bot_name(player) -> (
    name = player~'command_name'+'_bot';
    id = 1;
    while(player(name+id), 1000, id = id + 1);
    if(id >= 1000, return(null));
    name + id;
);

_kill(name) -> (
    bot = filter(_get_online_bots(), _~'command_name' == name);
    if(!bot, return(print(player(), format(str('r Bot %s not found', name)))));
    run(str('player %s kill', name));
    print(player(), format(str('g Bot %s killed', name)));
);

_kill_nearest() -> (
    bots = _get_online_bots();
    player = player();
    if(!bots, return(print(player, format('r No bots online'))));
    bot = null;
    distance = 5;
    for(bots, 
        if(distance(pos(_), pos(player)) < distance, 
            bot = _;
            distance = distance(pos(bot), pos(player));
        )
    );
    if(!bot, return(print(player, format('r No bots found in 5 blocks radius'))));
    run(str('player %s kill', bot));
    print(player, format(str('g Bot %s killed', bot)));
);

distance(pos1, pos2) -> sqrt((pos1:0 - pos2:0)^2 + (pos1:1 - pos2:1)^2 + (pos1:2 - pos2:2)^2);
