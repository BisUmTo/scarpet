//////
// decoratedpotsherdswapper by BisUmTo
// Allows players to swap decorated potsherds while placed, by shift right clicking with a sherd or brick
//
// Known issues:
// - If sherd is present in both hands and mainhand has only one item, offhand will be used overwriting the mainhand sherd
//////

__config() -> {'stay_loaded' -> true};

global_sherds = item_list('decorated_pot_sherds');
global_sherd_facing = {
    'south' ->  ['south', 'east', 'west', 'north'],
    'north' ->  ['north', 'west', 'east', 'south'],
    'east' ->   ['east', 'north', 'south', 'west'],
    'west' ->   ['west', 'south', 'north', 'east'],
};

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec) -> (
    if(!item_tuple || !player ~ 'sneaking' || player ~ 'gamemode' == 'spectator' || block != 'decorated_pot' || face == 'up' || face == 'down', return());
    [item, count, nbt] = item_tuple;
    rotation = block_state(block):'facing';
    index_face = (global_sherd_facing:rotation)~face;
    if(hand == 'offhand' && _main_hand_would_fail(player, block, index_face), return());
    if(item == 'brick'|| global_sherds ~ item != null, 
        if((old_item = _swap(player, block, index_face, item)) && player ~ 'gamemode' != 'creative',
            spawn('item', pos(block)+[0.5,1,0.5], str('{Item:{id:"%s",Count:1b},PickupDelay:10,Motion:[%f,.2,%f]}', old_item, rand(0.2) - 0.1, rand(0.2) - 0.1));
            inventory_set(player, if(hand == 'mainhand', player~'selected_slot', -1), count - 1, item, nbt);
        );
        modify(player, 'swing', hand);
    );
);

_swap(player, block, index_face, item) -> (
    nbt = block_data(block);
    if(!nbt:'sherds',
        nbt:'sherds' = ['brick', 'brick', 'brick', 'brick'];
    );
    old_item = nbt:('sherds['+index_face+']');
    if (old_item ~ item, return(null));
    nbt:('sherds['+index_face+']') = item;
    without_updates(set(block, 'air'));
    set(block, block, block_state(block), nbt);
    sound('block.decorated_pot.hit', pos(block), 1.0, 1.5, 'block');
    old_item
);

// Workaround for offhand usage when main hand succeded
_main_hand_would_fail(player, block, index_face) -> (
    item_tuple = player ~ 'holds';
    if(!item_tuple, return(false));
    [item, count, nbt] = item_tuple;
    if(item != 'brick' && global_sherds ~ item == null, return(false));
    block_nbt = block_data(block);
    if(!block_nbt:'sherds',
        block_nbt:'sherds' = ['brick', 'brick', 'brick', 'brick'];
    );
    old_item = block_nbt:('sherds['+index_face+']');
    if(old_item ~ item, return(true));
    false
);
