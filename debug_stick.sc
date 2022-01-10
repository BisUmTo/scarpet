__config() -> {
    'stay_loaded' -> true,
    'resources' -> {
         'source' -> 'https://raw.githubusercontent.com/Arcensoth/mcdata/master/processed/reports/blocks/simplified/data.json',
         'target' -> system_info('app_name')+'.data/data.json',
     }
};

global_properties = read_file('data','json');
global_blacklist_states = ['waterlogged'];

_cycle(list, current, inverse) -> (
    for(global_blacklist_states,
        state = _;
        if((list ~ state) != null,
            delete(list, list ~ state)
        )
    );
    index = list~current;
    i = -1^(inverse+1);
    if(current, list:(index+i), list:0)
);

__on_player_clicks_block(player, block, face) -> (

    item_tuple = player ~ ['holds',hand='mainhand'] || player ~ ['holds',hand='offhand'] || [null];
    if(item_tuple:0 == 'debug_stick' && player ~ 'gamemode' == 'survival' && tick_time() > global_last_click,

        block_name = str(block);
        if(block_name~':'==null,block_name='minecraft:'+block_name);

        nbt = parse_nbt(item_tuple:2 || nbt('{}'));
        compound = nbt:'DebugProperty' || {};
        properties = keys(global_properties:block_name:'properties');
        if(properties,
            current = compound:block_name;
            property = _cycle(properties, current, player~'sneaking');
            compound:block_name = property;
            nbt:'DebugProperty' = compound;
            display_title(player, 'actionbar', str('selected "%s" (%s)', current, block_state(block, current)));
            inventory_set(player, if(hand=='mainhand',player~'selected_slot',-1), item_tuple:1, item_tuple:0, encode_nbt(nbt));
        , // else
            display_title(player, 'actionbar', str('"%s" has no properties', block_name));
        );

        global_last_click = tick_time() + 1;
    );
);

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec) -> (
    item_tuple = player ~ ['holds',hand='mainhand'] || player ~ ['holds',hand='offhand'] || [null];
    if(player ~ 'holds':0 == 'debug_stick' && player ~ 'gamemode' == 'survival',
        block_name = str(block);
        if(block_name~':'==null,block_name='minecraft:'+block_name);

        nbt = parse_nbt(item_tuple:2 || nbt('{}'));
        compound = nbt:'DebugProperty' || {};
        properties = global_properties:block_name:'properties';
        if(properties,
            current_property = compound:block_name || keys(properties):0;

            block_state = block_state(block);
            current_state = block_state:current_property;
            state = _cycle(properties:current_property, current_state, player~'sneaking');
            block_state:current_property = state;
            without_updates(
                set(pos(block), block, block_state, block_data(block))
            );
            display_title(player, 'actionbar', str('"%s" to %s', current_property, state));
        , // else
            display_title(player, 'actionbar', str('"%s" has no properties', block_name));
        )
    )
)