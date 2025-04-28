//////
// graves by BisUmTo
// On player death, if items are on the ground, it will generate a tombstone with item inside of it
// Sneaking on one's tombstone, will release all items
// Added "/graves" command to manually remove a grave (no ownership is verified, ops only)
// Added "/graves list" command to print a list with all tobstone cordinates of the executor
// Added "/graves list <player>" command to print a list with all tobstone cordinates of the given player (ops only)
//////

__config() -> {
    'stay_loaded' -> true,
    'scope' -> 'global',
    'commands' -> {
        '' -> '_command',
        'list' -> _() -> _graves_list(player()~'name'),
        'list <player>' -> '_graves_list',
    },
    'arguments' -> {
        'player' -> {'type'->'players','single'->true}
    }
};

_command() -> if((player=player())~'permission_level'>=1, _remove_grave(player, pos(player), null, true));
_graves_list(player_name) -> (
    for(keys(uuid_storage = parse_nbt(nbt_storage('redcraft:players'))),
        if(lower(uuid_storage:_)==lower(player_name), player_uuid = _; break())
    );
    if(!player_uuid,
        p = player(player_name);
        player_uuid = p~'uuid';
    );
    if(player_uuid,
        graves = parse_nbt(nbt_storage('redcraft:graves')):player_uuid;
        if(graves,
            print('-'*20);
            print(player(), format(str('fb %s\'s graves:', player_name)));
            for(graves,
                print(player(), format(str('g %s @ %.00f %.00f %.00f',_:'Dimension',_:'Pos':0,_:'Pos':1,_:'Pos':2)))
            );
            print('-'*20),
        // else
            print(player(),format(str('ri No grave found for %s',player_name)))
        ),
    // else
        print(player(),format(str('ri No player found with name %s',player_name)))
    )
);
if(scoreboard('gb.grave.time') == null,
    scoreboard_add('gb.grave.time')
);

__on_player_dies(player) -> (
    nbt = parse_nbt(nbt_storage('redcraft:players'));
    if(put(nbt, player~'uuid', player~'name') != null,
        nbt_storage('redcraft:players', encode_nbt(nbt))
    );
    if(inventory_has_items(player),
        schedule(0, _(outer(player)) -> if(
            (items = filter(entity_area('item', (pos=pos(player)) + [0,player~'eye_height',0], [3, 3, 3]), _~'age'==0)) != [],
            __make_grave(player, pos, items)
        ))
    )
);

__make_grave(player, pos, items) -> (
    time = __save_grave_position(player);
    name = player~'name';
    display_entities = [
        a=spawn('block_display', pos, str('{Passengers: [{block_state: {Name: "minecraft:spruce_wall_sign", Properties: {facing: "north", waterlogged: "false"}}, id: "minecraft:block_display", transformation: {left_rotation: [0.0f, 1.0f, 0.0f, 0.0f], right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], scale: [0.375f, 0.5f, 0.5f], translation: [0.1875f, 0.0625f, -0.25f]}}, {block_state: {Name: "minecraft:coarse_dirt"}, id: "minecraft:block_display", transformation: {left_rotation: [0.0f, 1.0f, 0.0f, 0.0f], right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], scale: [0.5f, 0.5f, 0.5f], translation: [0.25f, -0.25f, -0.25f]}}, {block_state: {Name: "minecraft:cobbled_deepslate_wall", Properties: {east: "low", north: "none", south: "none", up: "true", waterlogged: "false", west: "low"}}, id: "minecraft:block_display", transformation: {left_rotation: [0.0f, 1.0f, 0.0f, 0.0f], right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], scale: [0.5f, 0.5f, 0.5f], translation: [0.25f, 0.25f, -0.375f]}}, {alignment: "center", background: 0, default_background: 0b, id: "minecraft:text_display", line_width: 200, see_through: 0b, shadow: 0b, text: \'"%s"\', text_opacity: -1b, transformation: {left_rotation: [0.0f, 0.0f, 0.0f, 1.0f], right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], scale: [0.25f, 0.25f, 1.0f], translation: [0.0f, 0.455416f, -0.48f]}}], block_state: {Name: "minecraft:coarse_dirt"}, transformation: {left_rotation: [0.0f, 1.0f, 0.0f, 0.0f], right_rotation: [0.0f, 0.0f, 0.0f, 1.0f], scale: [0.5f, 0.5f, 0.5f], translation: [0.25f, -0.25f, 0.25f]}}', name)),
        ...(a~'passengers'),
    ];
    for(display_entities,
        modify(_, 'tag', ['gb.grave_armor_stand', 'gb.grave']);
        scoreboard('gb.grave.time', _~'uuid', time)
    );
    for(items,
        modify(_, 'invulnerable', true);
        modify(_, 'pos', pos);
        modify(_, 'pickup_delay', 32767);
        modify(_, 'gravity', false);
        modify(_, 'motion', [0,0,0]);
        modify(_, 'nbt_merge', '{Age:-32768}');
        modify(_, 'mount', armor_stands:1);
        modify(_, 'tag', ['gb.grave_item', 'gb.grave']);
        scoreboard('gb.grave.time', _~'uuid', time)
    )
);

__save_grave_position(player) -> (
    nbt = parse_nbt(nbt_storage('redcraft:graves'));
    if(!has(nbt,player~'uuid'), nbt:(player~'uuid') = []);
    nbt:(player~'uuid') += {
        'Pos' -> player~'pos',
        'Dimension' -> player~'dimension',
        'Tick' -> system_info('world_time')
    };
    nbt_storage('redcraft:graves', encode_nbt(nbt));
    system_info('world_time')
);
__remove_grave_position(player,tick) -> (
    nbt = parse_nbt(nbt_storage('redcraft:graves'));
    if(!has(nbt,player~'uuid'), return(false));
    for(nbt:(player~'uuid'),if(_:'Tick' == tick, i=_i; break()));
    if(i==null, return(false));
    delete(nbt:(player~'uuid'):i);
    nbt_storage('redcraft:graves', encode_nbt(nbt));
    true
);

_get_graves_positions(player) -> (
    nbt = parse_nbt(nbt_storage('redcraft:graves'));
    if(!has(nbt,player~'uuid'), nbt:(player~'uuid') = []);
    return(nbt:(player~'uuid'))
);

_distance(p1, p2) -> reduce(p1-p2, _a + _*_, 0);

__on_player_starts_sneaking(player) -> (
    if(player ~ 'gamemode' == 'spectator', return());
	for(_get_graves_positions(player),
		grave = _;
		if(player ~ 'dimension' == grave:'Dimension' && _distance(pos(player), grave:'Pos')<1,
			__remove_grave(player, pos(player), grave:'Tick', false);
            __remove_grave_position(player,grave:'Tick')
		)
	)
);

__remove_grave(player, pos, tick, ignore_tick) -> (
    for(filter([
            ...entity_area('block_display', pos, [1.5,1.5,1.5]),
            ...entity_area('text_display', pos, [1.5,1.5,1.5])
        ], scoreboard('gb.grave.time', _~'uuid') == tick || ignore_tick),
        modify(_, 'remove')
    );
    for(filter(entity_area('item', pos, [1,1,1]), scoreboard('gb.grave.time', _~'uuid') == tick || ignore_tick),
        modify(_, 'pickup_delay', 0);
        modify(_, 'gravity', true);
        if(!ignore_tick, modify(_, 'nbt_merge', str('{Owner:%s}',player~'nbt':'UUID')))
    );
    sound('block.wart_block.fall', pos, 1.0, 0, 'block');
)
