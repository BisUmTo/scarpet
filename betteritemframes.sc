//////
// betteritemframes by BisUmTo
// Right click on an item_frame/glow_item_frame with a shears to make it invisible
// Richt click on an item_frame/glow_item_frame with a glass_pane to make it fixed
// Right click on an item_frame/glow_item_frame with an axe to unfix it
//////

__config() -> {'stay_loaded' -> true, 'scope' -> 'global'};

global_damage_nbt = if(system_info('game_data_version') >= 3837, 'components.minecraft:damage', 'Damage');
global_durability = {'wooden' -> 59, 'stone' -> 131, 'iron' -> 250, 'diamond' -> 1561, 'netherite' -> 2031, 'shears' -> 238};

__on_player_interacts_with_entity(player, entity, hand) -> (
    if(player ~ 'gamemode' == 'spectator' || entity~'type'~'item_frame$'==null || !(entity~'nbt':'Item'), return());
    item_tuple = query(player, 'holds', hand);
    if(item_tuple == null, return());
    [item, count, nbt] = item_tuple;
    rotation = (7 + entity~'nbt':'ItemRotation') % 8;
    if(
        item == 'shears' && !entity~'nbt':'Invisible',
            modify(entity, 'nbt_merge', '{Invisible:true' + if(!entity~'nbt':'Fixed', ',ItemRotation:'+rotation, '') + '}'),
        item == 'glass_pane' && !entity~'nbt':'Fixed',
            modify(entity, 'nbt_merge', '{Fixed:true}');
            inventory_set(player, if(hand == 'mainhand', player~'selected_slot', -1), count - 1, item, nbt);
            return(),
        item ~ '_axe$' && entity~'nbt':'Fixed',
            modify(entity, 'nbt_merge', str('{ItemRotation:%s,Fixed:false}', rotation));
            spawn('item', pos(entity), str('{Item:{id:"minecraft:glass_pane",Count:1b},PickupDelay:10,Motion:[%f,.2,%f]}', rand(0.2) - 0.1, rand(0.2) - 0.1)),
    // else
        return()
    );
    nbt:global_damage_nbt = nbt:global_damage_nbt + 1;
    if (player ~ 'gamemode' != 'creative',
        if(nbt:global_damage_nbt < global_durability:(split('_', item):0),
            inventory_set(player, player ~ 'selected_slot', count, item, nbt), 
        // else
            inventory_set(player, player ~ 'selected_slot', 0);
            sound('entity.item.break', player ~ 'pos', 1.0, 1.0, 'player');
        )
    )
);
