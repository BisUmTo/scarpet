//////
// accessibleshukerbox by BisUmTo
// Right click holding a Shulkerbox to open it from your inventory directly
// While a "portable"
//
// From config it is possible to:
// - Enable/disable from hand Shulker Box access
// - Enable/disable Shulkerbox nesting
//
// Note: unload app while changing configs
//////

__config()->{
    'requires' -> {
        'carpet' -> '>=1.4.47'
    }
};

// CONFIG
global_config = {
    'from_hand' -> true,
    'prevent_nesting' -> true
};
try(
    for(current_config = read_file('config', 'json'),
        global_config:_ = current_config:_
    )
);
write_file('config', 'json', global_config);

__open(shulker_slot) -> (
    player = player();
    if(!player,
        print(format('r Player not found'));
        return('Player not found');
    );
    item_tuple = inventory_get(player, (shulker_slot-18)%36);
    if(!item_tuple,
        print(format('r Item not found'));
        return('Item not found');
    );

    [item, count, nbt] = item_tuple;
    if(!nbt, nbt = nbt('{}'));
    put(nbt, 'BlockEntityTag.Items[]', nbt('{}'), 'merge');

    display_name = nbt:'display.Name' || '';
    name = decode_json(display_name):'text' || title(replace(item, '_', ' '));

    content = parse_nbt(nbt:'BlockEntityTag.Items');

    global_screen=create_screen(player,'generic_9x3', name ,_(screen, player, action, data, outer(shulker_slot))->(
        // OPEN ANOTHER SHULKER GUI
        if(data:'slot' >= 27 && action == 'clone', 
            sub_item_tuple = inventory_get(screen, data:'slot');
            if(sub_item_tuple && 
               sub_item_tuple:0 ~ 'shulker_box$' &&
               sub_item_tuple:1 == 1,
                global_prvnt = true;
                __open(data:'slot');
                inventory_set(global_screen, -1, global_prvnt:1, global_prvnt:0, global_prvnt:2);
                return('cancel')
            )
        );

        // CLOSE SHULKER GUI
        if(action=='close', 
            if(global_prvnt == true,
                global_prvnt = inventory_get(screen,-1);
                inventory_set(screen, -1, 0)
            , // else
                drop_item(screen,-1);
                sound('block.shulker_box.close', pos(player), 1, 1, 'block')
            )
        );

        // DISABLE INTERACTION WITH CURRENT
        if(action != 'update_slot' && data:'slot' == shulker_slot,
            return('cancel')
        );
        if(action == 'swap' && 54 + data:'button' == shulker_slot,
            return('cancel')
        );
        
        // DISABLE NESTING 
        if(global_config:'prevent_nesting',
            if(action == 'swap' && data:'slot'<27,
                sub_item_tuple = inventory_get(screen, 54 + data:'button');
                if(sub_item_tuple && sub_item_tuple:0 ~ 'shulker_box$',
                    return('cancel')
                );
            );
            if(action == 'quick_move' && inventory_find(screen, null) < 27,
                sub_item_tuple = inventory_get(screen, data:'slot');
                if(sub_item_tuple && sub_item_tuple:0 ~ 'shulker_box$',
                    return('cancel')
                )
            );
            if(action == 'pickup' && data:'slot' < 27,
                sub_item_tuple = inventory_get(screen, -1);
                if(sub_item_tuple && sub_item_tuple:0 ~ 'shulker_box$',
                    return('cancel')
                )
            )
        );  

        // UPDATE SHULKER
        if(action == 'slot_update' && data:'slot' >= 0 && data:'slot' < 27,
            item_tuple = inventory_get(screen, shulker_slot);
            [item, count, nbt] = item_tuple;
            if(!nbt, nbt = nbt('{BlockEntityTag:{Items:[]}}'));
            content = parse_nbt(nbt:'BlockEntityTag':'Items');

            sub_item_tuple = inventory_get(screen, data:'slot') || [null, 0, null];

            mode = 'insert';
            index = 0;
            for(content,
                if(_:'Slot' < data:'slot', index = _i,
                   _:'Slot' > data:'slot', break(),
                   mode = 'replace'; index = _i
                );
            );

            element = {
                'id' -> sub_item_tuple:0 || 'air',
                'Count' -> sub_item_tuple:1,
                'Slot' -> data:'slot'
            };
            if(sub_item_tuple:2,
                element:'tag' = parse_nbt(sub_item_tuple:2)
            ); 

            put(content, index, element, mode);   
            nbt:'BlockEntityTag.Items' = encode_nbt(content, true);
            
            inventory_set(screen, shulker_slot, count, item, nbt)
        )
    ));

    for(content,
        inventory_set(global_screen, 
            _:'Slot', 
            _:'Count' || 1, 
            _:'id' || 'air', 
            encode_nbt(_:'tag' || {})
        )
    );    

    sound('block.shulker_box.open', pos(player), 1, 1, 'block');
);

__on_player_uses_item(player, item, hand) ->(
if(hand != 'mainhand', return());
    if(item:0 ~ 'shulker_box$' && 
       player ~ ['trace',5,'blocks'] == null &&
       item:1 == 1,
        __open(27+(player ~ 'selected_slot'+27)%36)
    )
);

__on_close() -> (
    if(global_screen,close_screen(global_screen));
    write_file('config', 'json', global_config)
)
