__config()->{
	'scope' -> 'global',
    'commands' -> {
        'stop' -> _() -> global_stop = true,
        'start' -> _() -> global_stop = false,
        'spawn <name>' -> _(name) -> _spawn_snowgolem(name, pos(player()), null),
        'command' -> _() -> print('Abilitare il comando nella chat di twitch?'),
        'command false' -> _() -> global_command = false,
        'command true' -> _() -> global_command = true,
        'gifted' -> _() -> print('Abilitare lo spawn ai donatori di sub?'),
        'gifted false' -> _() -> global_gifted = false,
        'gifted true' -> _() -> global_gifted = true,
        'resetFile' -> _() -> write_file('snowman', 'json', {})
    }
};

global_command = true; // Comando in chat?
global_stop = false; // Spawnano golem?
global_gifted = true; // Spawnano golem quando si donano sub?
global_center = [8,4,8]; // Coordinate centrali spawn
global_range = [15,0,15]; // Range di coordinate dello spawn

// FUNCTIONS
_rand_pos() -> (
    global_center + map(global_range, rand(2*_)-_)
);

_spawn_snowgolem(actor, pos, color) ->
if( !global_stop,
    if(!pos, pos = _rand_pos());
    if(!color, color = str('%06X', rand(16777216)));
    if(global_colors~color==null, color = '#'+color);

    global_snowman:actor += 1;
    write_file('snowman', 'json',global_snowman);

    run(str('title @a title {"text":"%s","color":"%s"}', actor, color));
	run('title @a times 4 20 4');
	run('playsound minecraft:entity.item.pickup ambient @a');

    e = spawn('snow_golem', pos, str('{CustomName:\'{"text":"%s","color":"%s"}\'}',
        replace(replace(replace(actor,'\\\\',''),'"',''),'\\\'',''),
        color
    ));
    _add_death_event(e)
);

// EVENTS
__on_twitch_follow(player, actor) -> 
if(!global_snowman:actor,
    _spawn_snowgolem(actor, null, null);
    run('title @a subtitle {"text":"Follow su Twitch!","color":"#9147ff"}');
);
__on_youtube_follow(player, actor) -> 
if(!global_snowman:actor,
    _spawn_snowgolem(actor, null, null);
    run('title @a subtitle {"text":"Iscritto su YouTube!","color":"#ff0000"}');
);


__on_twitch_subscription(player, actor, message, tier, months, resubbed, streak, gifted, gifter) ->
if( !gifted,
        loop(max(tier,1), _spawn_snowgolem(actor, null, null));
        run('title @a subtitle {"text":"Sub su Twitch!","color":"#9147ff"}'),
    global_gifted,
        loop(tier, _spawn_snowgolem(gifter, null, null));
        run('title @a subtitle {"text":"Donatore sub su Twitch!","color":"#9147ff"}');
);

__on_twitch_chat_message(player, actor, message, badges, subscriptionMonths) ->
if(global_command && message == '!snowman' && !global_snowman:actor,
    _spawn_snowgolem(actor, null, null);
	run('title @a subtitle {"text":"Comando in chat!","color":"#9147ff"}');
);

// GLOBALI UTILI
global_colors = ['black', 'dark_blue', 'dark_green', 'dark_aqua', 'dark_red', 'dark_purple', 'gold', 'gray', 'dark_gray', 'blue', 'green', 'aqua', 'red', 'light_purple', 'yellow', 'white'];
global_snowman = read_file('snowman', 'json') ||
                 (write_file('snowman', 'json', {}); {});

__on_twitch_custom_reward(player, actor, message, badges, subscriptionMonths, customRewardId) ->
if(customRewardId=='bb54df02-0661-4ef5-aefb-91f320f91b15' && !global_snowman:actor,
    _spawn_snowgolem(actor, null, null);
    run('title @a subtitle {"text":"Pagamento con dobloni!","color":"#9147ff"}'),
// else if	
customRewardId=='21624778-908f-4b7f-941e-346276bff8ce',
    _spawn_snowgolem(actor, null, null);
    run('title @a subtitle {"text":"EXTRA SNOWMAN!","color":"#9147ff"}');
	print(actor+': '+message);
);

// MESSAGGI KILLS
_add_death_event(entity) -> (
	entity_event(entity, 'on_damaged', _(entity, amount, source, attacking_entity) -> (
        global_last_hit:(entity~'uuid') = attacking_entity~'display_name';
	));
	entity_event(entity, 'on_death', _(entity, reason) -> (
        killer = global_last_hit:(entity~'uuid');
		print(player('all'), entity~'display_name' + format('y  è stato killato da ') + killer);
        scoreboard('killer', killer, scoreboard('killer', killer) + 1);
	));
    modify(entity, 'nbt_merge', '{Health:15f,Attributes:[{Name:generic.max_health,Base:15}]}');
);

scoreboard_add('killer');
scoreboard_display('sidebar', 'killer');
scoreboard_property('killer', 'display_name', format('b#9147ff TOP SNOWMAN'));

global_last_hit = {};
entity_load_handler('snow_golem', _(e, new) -> schedule(0,'_add_death_event', e));
for(entity_list('snow_golem'), _add_death_event(_));
