__config()->{
	'scope' -> 'global',
    'commands' -> {
        'stop' -> _() -> global_config:'stop' = true,
        'start' -> _() -> global_config:'stop' = false,
        'spawn <name>' -> _(name) -> _spawn_frog(name, pos(player()), null),
        'command' -> _() -> print('Abilitare il comando nella chat di twitch?'),
        'command false' -> _() -> global_config:'command' = false,
        'command true' -> _() -> global_config:'command' = true,
        'gifted' -> _() -> print('Abilitare lo spawn ai donatori di sub?'),
        'gifted false' -> _() -> global_config:'gifted' = false,
        'gifted true' -> _() -> global_config:'gifted' = true,
        'center <pos>' -> _(pos) -> global_config:'center' = pos,
        'radius <radius>' -> _(r) -> global_config:'radius' = [r, 0, r],
        'reload config' -> 'load_config',
        'reset scoreboard' -> _() -> (scoreboard_remove('killer'); add_scoreboard()),
		'top' -> _() -> (
			top = _top();
			print(format('b Top 10:'));
			loop(min(keys(global_frog),10),
				print(str('%d. %s (%d rane)',_+1,top:_,global_frog:(top:_)))
			)
		),
		'rank <name>' -> _(name) -> (
			top = _top();
			pos = top~name;
			if(pos == null,
				print(str('%s non è in classifica',name,)),
				print(str('%s si trova in %d^ posizione con %d rane',name,pos+1,global_frog:name))
			)
		),
		'fix' -> _() -> (
			pos = pos(player());
			nomi = {};
			for(entity_list('frog'),
				nomi:(_~'display_name')+=1
			);
			keys = {};
			for(nomi, keys+=_);
			for(global_frog, keys+=_);
			for(keys,
				n = _;
				if((d = nomi:n - global_frog:n) > 0,
					l = filter(entity_list('frog'),_~'display_name' == n);
					loop(d,
						if(_ < length(l), modify(l:_, 'pos', pos))
					);
					print(format(str('y Ci sono %d rane in più di %s',d,n)))
				);
				if(d < 0,
					loop(-d, _spawn_frog(n,null,null));
					global_frog:n += d;
					write_file('frog', 'json',global_frog);
					print(format(str('y Mancano %d rane di %s',-d,n)))
				);
			)
		),
        'reset file' -> _() -> write_file('frog', 'json', {})
    },
    'arguments' -> {
        'radius' -> {
            'type' -> 'int',
            'min' -> 0,
            'max' -> 255
        }
    },
    'requires' -> {
        'sctwitch' -> '*'
    }
};

_top() -> sort_key(keys(global_frog), -global_frog:_);

global_config = {
    'command' -> true, // Comando in chat?
    'stop' -> false, // Spawnano rane?
    'gifted' -> true, // Spawnano rane quando si donano sub?
    'center' -> [0,100,0], // Coordinate centrali spawn
    'radius' -> [15,0,15] // Range di coordinate dello spawn
};
load_config() -> (
    try(
        for(current_config = read_file('config', 'json'),
            global_config:_ = current_config:_
        )
    )
);
load_config();
write_file('config', 'json', global_config);

// FUNCTIONS
_rand_pos() -> (
    pos = global_config:'center' + map(global_config:'radius', rand(2*_)-_);
    pos:1 = top('terrain', pos);
    pos
);

_spawn_frog(actor, pos, color) ->
if( !global_config:'stop',
    if(!pos, pos = _rand_pos());
    if(!color, color = str('%06X', rand(16777216)));
    if(global_colors~color==null, color = '#'+color);

    global_frog:actor += 1;
    write_file('frog', 'json',global_frog);

    run(str('title @a title {"text":"%s","color":"%s"}', actor, color));
	run('title @a times 4 20 4');
	run('playsound minecraft:entity.item.pickup ambient @a');

    e = spawn('frog', pos, str('{CustomName:\'{"text":"%s","color":"%s"}\',variant:"%s"}',
        replace(replace(replace(actor,'\\\\',''),'"',''),'\\\'',''),
        color,
		rand(['minecraft:temperate','minecraft:warm','minecraft:cold'])
    ));
    _add_death_event(e)
);

// EVENTS
__on_twitch_follow(player, actor) -> 
if(!global_frog:actor,
    _spawn_frog(actor, null, null);
    run('title @a subtitle {"text":"Follow su Twitch!","color":"#9147ff"}');
);
__on_youtube_follow(player, actor) -> 
if(!global_frog:actor,
    _spawn_frog(actor, null, null);
    run('title @a subtitle {"text":"Iscritto su YouTube!","color":"#ff0000"}');
);


__on_twitch_subscription(player, actor, message, tier, months, resubbed, streak, gifted, gifter) ->
if( !gifted,
        loop(max(tier,1), _spawn_frog(actor, null, null));
        run('title @a subtitle {"text":"Sub su Twitch!","color":"#9147ff"}'),
    global_config:'gifted',
        loop(tier, _spawn_frog(gifter, null, null));
        run('title @a subtitle {"text":"Donatore sub su Twitch!","color":"#9147ff"}');
);

__on_twitch_chat_message(player, actor, message, badges, subscriptionMonths) ->
if(global_config:'command' && message == '!rane' && !global_frog:actor,
    _spawn_frog(actor, null, null);
	run('title @a subtitle {"text":"Comando in chat!","color":"#9147ff"}');
);

// GLOBALI UTILI
global_colors = ['black', 'dark_blue', 'dark_green', 'dark_aqua', 'dark_red', 'dark_purple', 'gold', 'gray', 'dark_gray', 'blue', 'green', 'aqua', 'red', 'light_purple', 'yellow', 'white'];
global_frog = read_file('frog', 'json') ||
                 (write_file('frog', 'json', {}); {});

__on_twitch_custom_reward(player, actor, message, badges, subscriptionMonths, customRewardId) ->
if(customRewardId=='bb54df02-0661-4ef5-aefb-91f320f91b15' && !global_frog:actor,
    _spawn_frog(actor, null, null);
    run('title @a subtitle {"text":"Pagamento con dobloni!","color":"#9147ff"}'),
// else if	
customRewardId=='21624778-908f-4b7f-941e-346276bff8ce',
    _spawn_frog(actor, null, null);
    run('title @a subtitle {"text":"EXTRA RANA!","color":"#9147ff"}');
	print(actor+': '+message);
);

// MESSAGGI KILLS
_add_death_event(entity) -> (
	entity_event(entity, 'on_damaged', _(entity, amount, source, attacking_entity) -> (
        global_last_hit:(entity~'uuid') = attacking_entity~'display_name';
	));
	entity_event(entity, 'on_death', _(entity, reason) -> (
        killer = global_last_hit:(entity~'uuid');
		print(player('all'), killer + format('y  ha killato ') + entity~'display_name');
        scoreboard('killer', killer, scoreboard('killer', killer) + 1);
	));
    modify(entity, 'nbt_merge', '{Health:15f,Attributes:[{Name:generic.max_health,Base:15}]}');
);

add_scoreboard() -> (
    if(scoreboard('killer') == null,
        scoreboard_add('killer');
    );
    scoreboard_display('sidebar', 'killer');
    scoreboard_property('killer', 'display_name', format('b#9147ff TOP RANE'))
);
add_scoreboard();

global_last_hit = {};
entity_load_handler('frog', _(e, new) -> schedule(0,'_add_death_event', e));
for(entity_list('frog'), _add_death_event(_));

// SAVE CONFIG
__on_close() -> (
    write_file('config', 'json', global_config);
)
