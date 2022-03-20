//////
// accessibleenderchest by BisUmTo
// Right click holding an Ender Chest to open it from your inventory directly
//
// From config it is possible to:
// - Enable/disable from hand Ender Chest access 
// - Enable/disable from command Ender Chest access
// - Enable/disable moving prevention (move currently opened Ender Chest)
//
// Note: unload app while changing configs
//////

__config()->{
    'commands'->{
        '' -> 'command',
        'enable' -> ['enable', true],
        'disable' -> ['enable', false],
    }
};

global_config = {
    'from_hand' -> true,
    'from_command' -> false,
    'prevent_moving' -> true
};
try(
    for(current_config = read_file('config', 'json'),
        global_config:_ = current_config:_
    )
);
write_file('config', 'json', global_config);

command() -> (
    player = player();
    if(!player,
        print('Only players can run this command');
        return('Only players can run this command')
    );
    if(!global_config:'from_command',
        display_title(player, 'actionbar', format('r You cannot run this command'));
        return('You cannot run this command')
    );
    __open(null)
);

enable(value) -> (
    player = player();
    if(player && player ~ 'permission_level' < 2,
        display_title(player, 'actionbar', format('r You cannot run this command'));
        return('You cannot run this command')
    );
    value = bool(value);
    if(value != global_config:'from_command',
        global_config:'from_command' = value;
        if(player,
            if(value,
                display_title(player, 'actionbar', format('l Command enabled')),
                display_title(player, 'actionbar', format('y Command disabled')),
            ),
            print('Command ' + if(value, 'enabled', 'disabled'))
        ),
        if(player,
            display_title(player, 'actionbar', format('g Nothing changed')),
            print('Nothing changed') 
        ) 
    )
);

__open(from_slot) -> (
    player = player();
    if(!player,
        print(format('r Player not found'));
        return('Player not found');
    );
    global_screen = create_screen(player,'generic_9x3','Ender Chest',_(screen, player, action, data, outer(from_slot))->( 
        if(action=='close',
            drop_item(screen,-1);
            sound('block.ender_chest.close', pos(player), 1, 1, 'block')
        );
        if(action=='slot_update' && data:'slot' >= 0 && data:'slot' < 27,
            item_tuple = inventory_get(screen, data:'slot');
            [item, count, nbt] = item_tuple || [null, 0, null];
            inventory_set('enderchest',player,data:'slot', count, item, nbt)
        );
        if(global_config:'prevent_moving' && from_slot != null && (
            data:'slot' == 54 + from_slot || 
            action == 'swap' && data:'button' == from_slot),
            return('cancel')
        )
    ));
    loop(inventory_size('enderchest',player),
        item_tuple = inventory_get('enderchest',player, _);
        [item, count, nbt] = item_tuple || [null, 0, null];
        inventory_set(global_screen,_, count, item, nbt)
    );
    sound('block.ender_chest.open', pos(player), 1, 1, 'block')
);

__on_player_uses_item(player, item, hand) ->(
	if(hand != 'mainhand' || !global_config:'from_hand', return());
    if(item:0 == 'ender_chest' && player ~ ['trace',5,'blocks'] == null,
        __open(player ~ 'selected_slot')
    )
);

__on_close() -> (
    close_screen(global_screen);
    write_file('config', 'json', global_config);
)
